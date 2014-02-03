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
#include "common.h"
#include "flags.h"
#include "kern.h"
#include "model.h"

using namespace std;
using namespace google;
using namespace cm;

static inline void show_copyright()
{
	printf("+----------------------------------------------------------------------------+\n"
	       "|            Training & Predicting program for Query Makespan                |\n"
	       "| Copyright (c) 2014, Zilong Tan (zilong.tan@duke.edu). All rights reserved. |\n"
	       "+----------------------------------------------------------------------------+\n\n");
}

int output_pred(const string &file, const gsl_vector *pred)
{
	FILE *fp;

	if (!file.size())
		fp = stderr;
	else {
		fp = fopen(file.c_str(), "w");
		if (fp == NULL) {
			ULIB_FATAL("couldn't open %s", file.c_str());
			return -1;
		}
	}

	for (size_t i = 0; i < pred->size; ++i)
		fprintf(fp, "%lf\n", gsl_vector_get(pred, i));

	fclose_safe(fp);

	return 0;
}

int main(int argc, char *argv[])
{
	int ret = 0;

	show_copyright();
	RETURN_IF_TRUE(setup_validators(), -1, "couldn't setup flags validators");
	ParseCommandLineFlags(&argc, &argv, true);
	RETURN_IF_TRUE(argc > 1, -1, "unhandled parameter: %s\n", argv[1]);

	kernel *kern;
	if (FLAGS_kern == "poly")
		kern = new poly_kern(FLAGS_polykern_tradeoff, FLAGS_polykern_order);
	else
		kern = new rbf_kern(FLAGS_rbfkern_delta_sq);

	model *cm = new model(FLAGS_inst_max_len, FLAGS_noise_var, kern);

	if (!cm->train(FLAGS_train_file)) {
		do {
			gsl_vector *pred;
			RETURN_IF_TRUE(cm->predict(FLAGS_test_file, &pred), -1, "prediction failed");
			RETURN_IF_TRUE(output_pred(FLAGS_output, pred), -1, "couldn't output predictions");
			gsl_vector_free(pred);
		} while (!FLAGS_test_file.size());
	} else {
		ULIB_FATAL("couldn't train a cost model");
		ret = -1;
	}

	delete cm;
	delete kern;

	return ret;
}
