/* Zilong Tan, Shivnath Babu
 * Copyright (c) 2014 Duke University
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, subject to the conditions listed in the Metis LICENSE
 * file. These conditions include: you must preserve this copyright
 * notice, and you cannot mention the copyright holders in advertising
 * related to the Software without their permission.  The Software is
 * provided WITHOUT ANY WARRANTY, EXPRESS OR IMPLIED. This notice is a
 * summary of the Metis LICENSE file; the license in that file is
 * legally binding.
 */

#include <stdio.h>
#include <string.h>
#include <gsl/gsl_blas.h>
#include <gsl/gsl_linalg.h>
#include <gsl/gsl_permutation.h>
#include <ulib/math_factorial.h>
#include "common.h"
#include "debug.h"
#include "model.h"

using namespace std;

namespace cm {

model::~model()
{
	gsl_vector_free(_tv);
	gsl_matrix_free(_fm);
	gsl_vector_free(_col_mean);
	gsl_vector_free(_col_sd);
	gsl_matrix_free(_ikm);
}

inline double model::scale(unsigned int n, unsigned int r)
{
	return 1.0/n + 1.0/r - 1.0/n/comb(n+r-1,n);
}

int model::get_dims(const string &file, int *nrow, int *ncol)
{
	FILE *fp = fopen(file.c_str(), "r");
	if (fp == NULL) {
		ULIB_FATAL("couldn't open %s", file.c_str());
		return -1;
	}
	char *line = new char[_ins_maxlen];
	if (fgets(line, _ins_maxlen, fp) != line) {
		delete [] line;
		fclose(fp);
		ULIB_FATAL("couldn't read %s", file.c_str());
		return -1;
	}
	int nc = 0;
	const char *p = strtok(line, "\t\n");
	while (p) {
		++nc;
		p = strtok(NULL, "\t\n");
	}
	delete [] line;
	*ncol = nc;
	int ch;
	int nr = 1;
	while ((ch = fgetc(fp)) != EOF) {
		if (ch == '\n')
			++nr;
	}
	fclose(fp);
	*nrow = nr;
	return 0;
}

int model::alloc_training_data(int nrow, int ncol)
{
	gsl_vector_free(_tv);
	gsl_matrix_free(_fm);

	_tv = gsl_vector_alloc(nrow);
	if (_tv == NULL) {
		ULIB_FATAL("couldn't allocate target value vector");
		return -1;
	}
	_fm = gsl_matrix_alloc(nrow, ncol - 3);
	if (_fm == NULL) {
		ULIB_FATAL("couldn't allocate feature matrix");
		_tv = NULL;
		return -1;
	}

	return 0;
}

int model::load_training_data(const string &file)
{
	int ret = -1;
	int nrow, ncol;

	if (get_dims(file, &nrow, &ncol)) {
		ULIB_FATAL("couldn't obtain training data dimensions");
		return -1;
	}
	if (nrow <= 0 || ncol < 4) {
		ULIB_FATAL("invalid training data dimensions");
		return -1;
	}

	FILE *fp = fopen(file.c_str(), "r");
	if (fp == NULL) {
		ULIB_FATAL("couldn't open %s", file.c_str());
		return -1;
	}

	if (alloc_training_data(nrow, ncol)) {
		fclose(fp);
		ULIB_FATAL("couldn't allocate training data");
		return -1;
	}

	char *line = new char[_ins_maxlen];
	for (int i = 0; i < nrow; ++i) {
		if (fgets(line, _ins_maxlen, fp) != line) {
			ULIB_FATAL("couldn't read row=%d in training data", i);
			goto done;
		}
		double t;
		char *token = strtok(line, "\t\n");
		if (token == NULL) {
			ULIB_FATAL("couldn't get target value of row=%d", i);
			goto done;
		}
		t = atof(token);
		token = strtok(NULL, "\t\n");
		if (token == NULL) {
			ULIB_FATAL("couldn't get MPL of row=%d", i);
			goto done;
		}
		int n, r;
		n = atoi(token);
		token = strtok(NULL, "\t\n");
		if (token == NULL) {
			ULIB_FATAL("couldn't get number of servers of row=%d", i);
			goto done;
		}
		r = atoi(token);
		gsl_vector_set(_tv, i, t/scale(n, r));
		int k = 0;
		while ((token = strtok(NULL, "\t\n")) != NULL) {
			if (k == ncol - 3) {
				ULIB_FATAL("extra column detected in row=%d", i);
				goto done;
			}
			gsl_matrix_set(_fm, i, k, atof(token));
			++k;
		}
		if (k != ncol - 3) {
			ULIB_FATAL("missing column detected in row=%d", i);
			goto done;
		}
	}
	ret = 0;
done:
	delete [] line;
	fclose(fp);
	return ret;
}

int model::get_col_mean()
{
	size_t nrow = _fm->size1;
	size_t ncol = _fm->size2;

	gsl_vector_free(_col_mean);

	gsl_vector *mean = gsl_vector_alloc(ncol);
	if (mean == NULL) {
		ULIB_FATAL("couldn't allocate vector");
		return -1;
	}

	for (size_t j = 0; j < ncol; ++j) {
		double sum = 0;
		for (size_t i = 0; i < nrow; ++i)
			sum += gsl_matrix_get(_fm, i, j);
		sum /= nrow;
		gsl_vector_set(mean, j, sum);
	}

	_col_mean = mean;

	return 0;
}

int model::get_col_sd()
{
	size_t nrow = _fm->size1;
	size_t ncol = _fm->size2;

	gsl_vector_free(_col_sd);

	gsl_vector *sd = gsl_vector_alloc(ncol);
	if (sd == NULL) {
		ULIB_FATAL("couldn't allocate vector");
		return -1;
	}

	double k = sqrt(nrow);
	for (size_t j = 0; j < ncol; ++j) {
		gsl_vector_const_view cv = gsl_matrix_const_column(_fm, j);
		double d = gsl_blas_dnrm2(&cv.vector);
		gsl_vector_set(sd, j, d/k);
	}

	_col_sd = sd;

	return 0;
}

inline void model::zero_out_mat(gsl_matrix *mat)
{
	size_t ncol = mat->size2;

	for (size_t j = 0; j < ncol; ++j) {
		gsl_vector_view cv = gsl_matrix_column(mat, j);
		gsl_vector_add_constant(&cv.vector, -gsl_vector_get(_col_mean, j));
	}
}

inline void model::norm_mat(gsl_matrix *mat)
{
	size_t ncol = mat->size2;

	for (size_t j = 0; j < ncol; ++j) {
		gsl_vector_view cv = gsl_matrix_column(mat, j);
		double d = gsl_vector_get(_col_sd, j);
		if (d > 0.000001)
			gsl_vector_scale(&cv.vector, 1/d);
	}
}

int model::train(const std::string &file)
{
	int ret = -1;
	gsl_matrix *km = NULL;
	gsl_matrix *ikm = NULL;
	gsl_permutation *perm = NULL;
	gsl_vector_view dv;

	gsl_matrix_free(_ikm);

	if (load_training_data(file)) {
		ULIB_FATAL("couldn't load training data");
		goto done;
	}
	dbg_print_mat(_fm, "Feature Matrix:");
	if (get_col_mean()) {
		ULIB_FATAL("couldn't get feature column means");
		goto done;
	}
	zero_out_mat(_fm);
	if (get_col_sd()) {
		ULIB_FATAL("couldn't get feature column standard deviations");
		goto done;
	}
	norm_mat(_fm);
	dbg_print_mat(_fm, "Normalized Feature Matrix:");

	km = comp_kern_mat(_fm, _kern);
	dbg_print_mat(km, "Kernel Matrix:");
	if (km == NULL) {
		ULIB_FATAL("couldn't compute kernel matrix");
		goto done;
	}

	dv = gsl_matrix_diagonal(km);
	gsl_vector_add_constant(&dv.vector, _noise_var);

	ikm = gsl_matrix_alloc(km->size1, km->size2);
	if (ikm == NULL) {
		ULIB_FATAL("couldn't allocate cost model");
		goto done;
	}

	int signum;
	perm = gsl_permutation_alloc(ikm->size1);
	if (perm == NULL) {
		ULIB_FATAL("couldn't allocate cost model");
		goto done;
	}
	gsl_linalg_LU_decomp(km, perm, &signum);
	gsl_linalg_LU_invert(km, perm, ikm);

	_ikm = ikm;
	ikm = NULL;
	ret = 0;
done:
	gsl_permutation_free(perm);
	gsl_matrix_free(km);
	gsl_matrix_free(ikm);

	return ret;
}

int model::predict(const string &file, gsl_vector **p)
{
	int ret = -1;
	int nrow, ncol;

	FILE *fp = NULL;
	gsl_matrix *mat = NULL;
	gsl_vector *vec = NULL;
	gsl_vector *ptv = NULL;
	gsl_matrix *km1 = NULL;
	gsl_matrix *res = NULL;
	char *line = NULL;

	if (!file.size()) {
		fp = stdin;
		nrow = 1;
		ncol = _col_mean->size + 2;
	} else {
		if (get_dims(file, &nrow, &ncol)) {
			ULIB_FATAL("couldn't get dimensions of test examples");
			goto done;
		}
		fp = fopen(file.c_str(), "r");
		if (fp == NULL) {
			ULIB_FATAL("couldn't open %s", file.c_str());
			goto done;
		}
	}
	if (nrow <= 0 || ncol != (int)_col_mean->size + 2) {
		ULIB_FATAL("invalid test dimensions, (nrow=%d,ncol=%d)", nrow, ncol);
		goto done;
	}

	mat = gsl_matrix_alloc(nrow, ncol - 2);
	if (mat == NULL) {
		ULIB_FATAL("couldn't allocate test feature matrix");
		goto done;
	}
	vec = gsl_vector_alloc(nrow);
	if (vec == NULL) {
		ULIB_FATAL("couldn't allocate scaling vector");
		goto done;
	}
	ptv = gsl_vector_alloc(nrow);
	if (ptv == NULL) {
		ULIB_FATAL("couldn't allocate predicting vector");
		goto done;
	}

	line = new char[_ins_maxlen];
	for (int i = 0; i < nrow; ++i) {
		if (fgets(line, _ins_maxlen, fp) != line) {
			ULIB_FATAL("couldn't read test row=%d", i);
			goto done;
		}
		char *token = strtok(line, "\t\n");
		if (token == NULL) {
			ULIB_FATAL("couldn't get MPL of row=%d", i);
			goto done;
		}
		int n, r;
		n = atoi(token);
		token = strtok(NULL, "\t\n");
		if (token == NULL) {
			ULIB_FATAL("couldn't get number of servers of row=%d", i);
			goto done;
		}
		r = atoi(token);
		gsl_vector_set(vec, i, scale(n, r));
		int k = 0;
		while ((token = strtok(NULL, "\t\n")) != NULL) {
			if (k == ncol - 2) {
				ULIB_FATAL("extra column detected in row=%d", i);
				goto done;
			}
			gsl_matrix_set(mat, i, k, atof(token));
			++k;
		}
		if (k != ncol - 2) {
			ULIB_FATAL("missing column detected in row=%d", i);
			goto done;
		}
	}
	dbg_print_mat(mat, "Test Matrix:");
	zero_out_mat(mat);
	norm_mat(mat);
	dbg_print_mat(mat, "Normalized Test Matrix:");

	km1 = comp_kern_mat(mat, _fm, _kern);
	if (km1 == NULL) {
		ULIB_FATAL("couldn't compute test kernel matrix");
		goto done;
	}
	dbg_print_mat(km1, "Test Kernel Matrix:");

	res = gsl_matrix_alloc(km1->size1, _ikm->size2);
	if (res == NULL) {
		ULIB_FATAL("couldn't allocate temporary matrix");
		goto done;
	}

	gsl_blas_dgemm(CblasNoTrans, CblasNoTrans, 1.0, km1, _ikm, 0.0, res);
	dbg_print_mat(res, "Predictive Matrix:");
	gsl_blas_dgemv(CblasNoTrans, 1.0, res, _tv, 0.0, ptv);
	gsl_vector_mul(ptv, vec);
	*p  = ptv;
	ptv = NULL;
	ret = 0;
done:
	gsl_matrix_free(mat);
	gsl_vector_free(vec);
	gsl_vector_free(ptv);
	gsl_matrix_free(km1);
	gsl_matrix_free(res);
	delete [] line;
	fclose_safe(fp);
	return ret;
}

}
