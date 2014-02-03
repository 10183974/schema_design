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

#ifndef _CM_FLAGS_H
#define _CM_FLAGS_H

#include <gflags/gflags.h>

namespace cm {

// RBF kernel squared delta
DECLARE_double(rbfkern_delta_sq);
// Polynomial kernel tradeoff
DECLARE_double(polykern_tradeoff);
// Polynomial kernel order
DECLARE_int32(polykern_order);
// Noise variance.
DECLARE_double(noise_var);
// Maximum instance length allowed in training file
DECLARE_int32(inst_max_len);
// Kernel to use
DECLARE_string(kern);
// Training file name, cannot be empty
DECLARE_string(train_file);
// Test file name, left empty to specify stdin
DECLARE_string(test_file);
// Output file name, left empty to specify stderr
DECLARE_string(output);
// Show status information
DECLARE_bool(verbose);

int setup_validators();

}

#endif  // _CM_FLAGS_H
