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

#ifndef _CM_COMMON_H
#define _CM_COMMON_H

#include <ulib/util_log.h>

#define RETURN_IF_TRUE(cond, ret, msg, ...) do {	\
		if (cond) {				\
			ULIB_FATAL(msg, ##__VA_ARGS__);	\
			return ret;			\
		}					\
	} while (0)

#define fclose_safe(fp) do {						\
		if (fp && fp != stdin && fp != stdout && fp != stderr)	\
			fclose(fp);					\
	} while (0)

#endif  // _CM_COMMON_H
