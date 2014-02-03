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

#ifndef _CM_MODEL_H
#define _CM_MODEL_H

#include <string>
#include <gsl/gsl_matrix.h>
#include "kern.h"

namespace cm {

class model {
public:
	model(int maxlen, double noise_var, const kernel *kern)
	: _ins_maxlen(maxlen), _noise_var(noise_var), _kern(kern),
	  _tv(0), _fm(0), _col_mean(0), _col_sd(0), _ikm(0)
	{ }

	~model();

	// train a cost model
	// 'file' is provided as the training example file, of which
	// each line has the following format:
	// avg_time	MPL	nregsvr	f1	f2	...
	// Fields are delimited by TABs('\t').
	int train(const std::string &file);

	// predict the avg_time's for instances provided in 'file',
	// 'p' will hold a pointer to the predicted vector.
	int predict(const std::string &file, gsl_vector **p);

private:
	// get dimensions of training data
	int get_dims(const std::string &file, int *nrow, int *ncol);
	// nrow and ncol are the dimensions of training data
	int alloc_training_data(int nrow, int ncol);
	int load_training_data(const std::string &file);
	// calculate the means of matrix columns
	int get_col_mean();
	// calculate the standard deviations of matrix columns
	int get_col_sd();
	// zero out matrix column means
	void zero_out_mat(gsl_matrix *mat);
	// normalize columns to unit variance
	void norm_mat(gsl_matrix *mat);

	static double scale(unsigned int n, unsigned int r);

	int _ins_maxlen;       // maximum length allowed for each instance
	double _noise_var;     // noise variance
	const kernel *_kern;   // kernel object
	gsl_vector *_tv;       // target value vector
	gsl_matrix *_fm;       // feature matrix
	gsl_vector *_col_mean; // feature column means
	gsl_vector *_col_sd;   // feature column standard deviations
	gsl_matrix *_ikm;      // the model
};

}

#endif  // _CM_MODEL_H
