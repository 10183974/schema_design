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

#include "flags.h"
#include "debug.h"

namespace cm {

void print_mat(const gsl_matrix *mat, const char *cap)
{
	if (!FLAGS_verbose)
		return;
	if (cap)
		printf("%s\n"
		       "--------------------------\n", cap);
	else
		printf("\nMatrix:\n"
		       "--------------------------\n");
	for (size_t i = 0; i < mat->size1; ++i) {
		printf("[%zu,]\t", i);
		for (size_t j = 0; j < mat->size2; ++j) {
			printf("%10.3lf", gsl_matrix_get(mat, i, j));
			if (j == mat->size2 - 1)
				putchar('\n');
		}
	}
	printf("\n");
}

}
