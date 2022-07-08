# tfm-vrp

This aplication uses [jMetal](https://github.com/jMetal/jMetal) to solve the problem covering an area with a set of drones. It can execute an algorithm against a single problem or use the jMetal [Experiment](https://github.com/jMetal/jMetal/blob/master/jmetal-lab/src/main/java/org/uma/jmetal/lab/experiment/Experiment.java) class to run a series of algorithms against a list of problems and generate metrics to compare the results.

The application tries to optimize the number of vehicles used and the distance of the longest route taken by them, thus minimizing the time of coverage of the given area.

## Command line use

Running the program without arguments will print all the avaiable arguments with a brief explanation for each one.

Out of all the arguments, the most important is '-vrpe', which is used to run the already mentioned jMetal experiments. The argument accepts a file containing the configuration for the experiment.

The resources directory contains configurations for different experiments, algorithms and problems.

### Experiment configuration
The configuration is a text file with the a structure following the next example:

```
STUDY_NAME: test
EXPERIMENT_BASE_DIRECTORY: testExperiment
PROBLEMS_DIRECTORY: resources/experiments/experiment1/problems
ALGORITHMS_DIRECTORY: resources/experiments/experiment1/algorithms
NUMBER_OF_INDEPENDENT_RUNS: 25
CORES: 4
EOF
```

The parameters in this file are:

- STUDY_NAME: name for the study
- EXPERIMENT_BASE_DIRECTORY: directory where the results of the experiment will be written
- PROBLEMS_DIRECTORY: directory containing the problems to be used in the experiment
- ALGORITHMS_DIRECTORY: directory containing the algorithms to be tested
- NUMBER_OF_INDEPENDENT_RUNS: number of times each problem should be run with each algorithm
- CORES: number of CPU cores to use
- EOF: indicates the end of the file

### Algorithm configuration

Each algorithm will be configured in a text file, following this example:

```
ALGORITHM_NAME: ngsaii1
ALGORITHM_TYPE: NGSAII
COMMENT: ngsaii1
POPULATION_SIZE: 100
MAX_EVALUATIONS: 1000
CROSSOVER_TYPE: PMX
CROSSOVER_PROBABILITY: 0.9
SELECTION_TYPE: BinaryTournamentSelection
MUTATION_TYPE: PermutationSwap
MUTATION_PROBABILITY: 0.1
EOF
```

- ALGORITHM_NAME: name for the algorithm's configuration
- ALGORITHM_TYPE: algorithm to be run. It must be one of the following values:
	- NGSAII
- COMMENT: comments to explain the algorithm configuration if needed
- POPULATION_SIZE: size of the population used by the genetic algorithm
- MAX_EVALUATIONS: maximum number of generations
- CROSSOVER_TYPE: crossover algorithm to be used. It must be one of the following:
	- PMX
- CROSSOVER_PROBABILITY: probability of the crossover
- SELECTION_TYPE: selection algorithm to be used. It must be one of the following:
	- BinaryTournamentSelection
- MUTATION_TYPE: mutation algorithm to be used. It must be one of the following:
	- PermutationSwap
- MUTATION_PROBABILITY: probability of applying mutation
- EOF: indicates the end of the file

### Problem configuration

Each problem will be configured in a text file. This configuration follows the one used in the [TSPLIB](http://comopt.ifi.uni-heidelberg.de/software/TSPLIB95/) database, but with two modifications to convert the TSP problems into VRP ones. The modifications consist in adding two parameters:

- DEPOT: index of the location used as depot
- NUMBER_OF_VEHICLES: number of vehicles avaiable. Not all of them need to be used

This allows us to use any instance of a TSP problem in the mentioned database with minimal overhead.

The next segment gives an example of a simple VRP problem where the locations form a square:

```
NAME: square
TYPE: VRP
COMMENT: Square
DIMENSION: 4
DEPOT: 0
NUMBER_OF_VEHICLES: 3
EDGE_WEIGHT_TYPE : EUC_2D
NODE_COORD_SECTION
1 0 0
2 0 100
3 100 100
4 100 0
EOF
```
