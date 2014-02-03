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
#include "common.h"

using namespace std;
using namespace google;

namespace cm {

static inline bool
validate_rbfkern_delta_sq(const char *flagname, double value)
{
	(void)flagname;
	return value > 0;
}
DEFINE_double(rbfkern_delta_sq, 3.0, "RBF kernel squared delta.");

static inline bool
validate_polykern_tradeoff(const char *flagname, double value)
{
	(void)flagname;
	return value >= 0;
}
DEFINE_double(polykern_tradeoff, 1.0, "Polynomial kernel tradeoff.");

static inline bool
validate_polykern_order(const char *flagname, int32 value)
{
	(void)flagname;
	return value > 0;
}
DEFINE_int32(polykern_order, 1, "Polynomial kernel order.");

static inline bool
validate_noise_var(const char *flagname, double value)
{
	(void)flagname;
	return value >= 0;
}
DEFINE_double(noise_var, 0.001, "Noise variance.");

static inline bool
validate_inst_max_len(const char *flagname, int32 value)
{
	(void)flagname;
	return value >= 64;
}
DEFINE_int32(inst_max_len, 8192, "Line max length. (>= 64)");

static inline bool
validate_kern(const char *flagname, const string &value)
{
	(void)flagname;
	return value == "poly" || value == "rbf";
}
DEFINE_string(kern, "rbf", "Kernel to use.");

static inline bool
validate_train_file(const char *flagname, const string &value)
{
	(void)flagname;
	return value.size();
}
DEFINE_string(train_file, "", "Training data file.");

DEFINE_string(test_file, "", "Test file, left empty for reading from stdin.");
DEFINE_string(output, "", "Output file, use stderr if not specified.");
DEFINE_bool(verbose, false, "Be verbose.");

int setup_validators()
{
	RETURN_IF_TRUE(!RegisterFlagValidator(&FLAGS_rbfkern_delta_sq, validate_rbfkern_delta_sq),
		       -1, "rbfkern_delta_sq is not valid");
	RETURN_IF_TRUE(!RegisterFlagValidator(&FLAGS_polykern_tradeoff, validate_polykern_tradeoff),
		       -1, "polykern_tradeoff is not valid");
	RETURN_IF_TRUE(!RegisterFlagValidator(&FLAGS_polykern_order, validate_polykern_order),
		       -1, "polykern_order is not valid");
	RETURN_IF_TRUE(!RegisterFlagValidator(&FLAGS_kern, validate_kern),
		       -1, "kern is not valid");
	RETURN_IF_TRUE(!RegisterFlagValidator(&FLAGS_noise_var, validate_noise_var),
		       -1, "noise_var is not valid");
	RETURN_IF_TRUE(!RegisterFlagValidator(&FLAGS_inst_max_len, validate_inst_max_len),
		       -1, "inst_max_len is not valid");
	RETURN_IF_TRUE(!RegisterFlagValidator(&FLAGS_train_file, validate_train_file),
		       -1, "train_file is not valid");
	return 0;
}

}
