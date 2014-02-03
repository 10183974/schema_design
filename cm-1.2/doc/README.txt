HBase Query Performace Models
------------------------------

cm is the training and predicting program running on x86_64 architectures.

Basic usage:
   Example 1:
    $./cm --train_file=data/train_data.txt --test_file=data/test_data.txt
    This would train the cost model using examples from
    train_data.txt, and then output predicted avg_times for each query
    given in test_data.txt. Stdout is used for output.

Training data format
   The input training examples are given as a text file, in which each
line is an example and has the following format:
avg_time	MPL	nsvr	fea1	fea2	...
   MPL is the number of concurrent queries and nsvr is the number of
region servers. avg_time is computed as the completion time of all
queries divided by the number of queries. Fields are delimited by
TABs('\t').

Test data format
MPL	nsvr	fea1	fea2	...

fea1, fea2, ... are input features, and they could be query
parameters, HBase configurations, e.g., cache size, and dataset
attributes, e.g., number of rows, average record lengths and etc.

Commandline argument list
    -inst_max_len (Line max length. (>= 64)) type: int32 default: 8192
    -kern (Kernel to use.) type: string default: "rbf"
    -noise_var (Noise variance.) type: double default: 0.001
    -output (Output file, use stderr if not specified.) type: string
      default: ""
    -polykern_order (Polynomial kernel order.) type: int32 default: 1
    -polykern_tradeoff (Polynomial kernel tradeoff.) type: double
    -default: 1
    -rbfkern_delta_sq (RBF kernel squared delta.) type: double
    -default: 3
    -test_file (Test file, left empty for reading from stdin.) type:
    -string
      default: ""
    -train_file (Training data file.) type: string default: ""
    -verbose (Be verbose.) type: bool default: false
