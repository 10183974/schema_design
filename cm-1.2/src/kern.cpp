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

#include <math.h>
#include <gsl/gsl_blas.h>
#include "common.h"
#include "kern.h"

namespace cm {

inline double kernel::def_dot(const gsl_vector *x1, const gsl_vector *x2)
{
	double res;
	gsl_blas_ddot(x1, x2, &res);
	return res;
}

inline double rbf_kern::operator()(
	const gsl_vector *x1,
	const gsl_vector *x2,
	dot_func_t df) const
{
	return exp(-0.5*(df(x1,x1) + df(x2,x2) - 2*df(x1,x2))/_delta_sq);
}

inline double poly_kern::operator()(
	const gsl_vector *x1,
	const gsl_vector *x2,
	dot_func_t df) const
{
	return pow(df(x1,x2) + _tradeoff, _order);
}

gsl_matrix *comp_kern_mat(
	const gsl_matrix *mat1,
	const gsl_matrix *mat2,
	const kernel *kern)
{
	int nrow = mat1->size1;
	int ncol = mat2->size1;

	gsl_matrix *km = gsl_matrix_alloc(nrow, ncol);
	if (km == NULL) {
		ULIB_FATAL("couldn't allocate kernel matrix, (nrow=%d,ncol=%d)", nrow, ncol);
		return NULL;
	}

	for (int i = 0; i < nrow; ++i) {
		gsl_vector_const_view v1 = gsl_matrix_const_row(mat1, i);
		for (int j = 0; j < ncol; ++j) {
			gsl_vector_const_view v2 = gsl_matrix_const_row(mat2, j);
			double val = (*kern)(&v1.vector, &v2.vector);
			gsl_matrix_set(km, i, j, val);
		}
	}

	return km;
}

gsl_matrix *comp_kern_mat(const gsl_matrix *mat1, const kernel *kern)
{
	int nrow = mat1->size1;

	gsl_matrix *km = gsl_matrix_alloc(nrow, nrow);
	if (km == NULL) {
		ULIB_FATAL("couldn't allocate kernel matrix, (nrow=%d,ncol=%d)", nrow, nrow);
		return NULL;
	}

	for (int i = 0; i < nrow; ++i) {
		gsl_vector_const_view v1 = gsl_matrix_const_row(mat1, i);
		for (int j = 0; j <= i; ++j) {
			gsl_vector_const_view v2 = gsl_matrix_const_row(mat1, j);
			double val = (*kern)(&v1.vector, &v2.vector);
			gsl_matrix_set(km, i, j, val);
			if (i != j)
				gsl_matrix_set(km, j, i, val);
		}
	}

	return km;
}

}
