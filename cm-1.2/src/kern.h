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

#ifndef _CM_KERN_H
#define _CM_KERN_H

#include <gsl/gsl_matrix.h>

namespace cm {

class kernel {
public:
	typedef double (*dot_func_t)(const gsl_vector *, const gsl_vector *);

	virtual ~kernel() { }

	virtual double
	operator()(const gsl_vector *,
		   const gsl_vector *,
		   dot_func_t df = def_dot) const = 0;

protected:
	// default dot product function
	static double def_dot(const gsl_vector *, const gsl_vector *);
};

class rbf_kern : public kernel {
public:
	rbf_kern(double delta_sq)
	: _delta_sq(delta_sq) { }

	virtual ~rbf_kern() { }

	virtual double
	operator()(const gsl_vector *,
		   const gsl_vector *,
		   dot_func_t df = def_dot) const;

private:
	double _delta_sq;
};

class poly_kern : public kernel {
public:
	poly_kern(double tradeoff, double order)
	: _tradeoff(tradeoff), _order(order) { }

	virtual ~poly_kern() { }

	virtual double
	operator()(const gsl_vector *,
		   const gsl_vector *,
		   dot_func_t df = def_dot) const;

private:
	double _tradeoff;
	double _order;
};

// compute a kernel matrix using the given kernel
// mat1: left feature matrix
// mat2: right feature matrix
// kern: kernel instance
gsl_matrix *comp_kern_mat(
	const gsl_matrix *mat1,
	const gsl_matrix *mat2,
	const kernel *kern);

// compute square kernel matrix
gsl_matrix *comp_kern_mat(const gsl_matrix *mat1, const kernel *kern);

}

#endif  // _CM_KERN_H
