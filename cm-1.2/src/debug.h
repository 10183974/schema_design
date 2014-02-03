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

#ifndef _CM_DEBUG_H
#define _CM_DEBUG_H

#include <stdio.h>
#include <gsl/gsl_matrix.h>

#ifdef NDEBUG
#define dbg_print_mat(mat,cap)
#else
#define dbg_print_mat(mat,cap) print_mat(mat,cap)
#endif

namespace cm {

// print the content of a matrix
void print_mat(const gsl_matrix *mat, const char *cap = NULL);

}

#endif  // _CM_DEBUG_H
