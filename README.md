# FlashFoodProb
Code accompanying "Simulating the Real Origins of Communication"
R A Blythe and T C Scott-Phillips
https://doi.org/10.1371/journal.pone.0113636

It is written in Java. To run the code you will need a Java Runtime Environment
(JRE). To compile the code, you will need the full Java Development Kit (JDK).

It has been tested with Java SE 15; it will likely work with versions dating
back to 2013.

It can be run in interactive or batch mode.

## Interactive mode

The interactive simulation is packaged into dist/ffp_interactive.jar

This may run if you simply double-click on it; if not try launching it from
the command line with

```
$ java -jar dist/ffp_interactive.jar
```

If it doesn't run, this may be because it is not compatible with your runtime
environment. In this case you should build the executable from source. In
the FlashFoodProb directory, enter the following commands:

```
$ javac -d bin -cp src src/Interactive.java
$ java -cp bin Interactive
```

The interactive simulation opens up a window displaying the state of the world.

The Setup panel allows you to change the size of the world, the number of
agents and the number of sites populated with food. It also sets the initial
strategy for all agents. If 'Active' is ticked, initially all agents have
the value of the corresponding parameter (brightness, contrast, motility,
directedness) set to 1. Otherwise it is set to 0. If 'Locked' is tick, the
initial value cannot change when agents regenerate. Cost sets the inherent
cost of the behaviour, as described in supporting document S1.

Important note: any changes in the Setup panel come into effect only if you
click Reset.

A _sweep_ is a single update of the system, comprising N single agent updates
(where N is the number of agents). Note each agent is chosen at random, so in
one sweep an agent may be chosen many times or not at all.

To perform a single sweep, click the 'Sweep' button. To perform multiple
sweeps, click the 'Sweeps' button; the number next to that button indicates
the number of sweeps to perform. Note that if motility is set to zero, you will
not see any movement until a strategy with nonzero motility enters the system
via regeneration (see below).

Click 'Regenerate' to regenerate the population. This causes agents to have
offspring in proportion to how much food they have consumed, subject also to
mutation of the strategy with a probability per offspring of the value given in
the 'Mutate' box. You can run multiple successive generations by clicking
'Generations'. In each generation, the number of sweeps in the Sweeps box is run,
and then agents are regenerated. This happens as many times as specified in
the Generations box.

When the simulation is paused, you can inspect the state of any individual agent
with the Agents panel. The first column (Agt) is the agent index, running from 0
to N-1. Sps is the agent's species index. When an agent reproduces, the offspring
inherits the species index of its parent, unless a new strategy is created by
mutation, in which case a new index is assigned. B, C, M and D indicate the
value of the brightness, contrast, motility and directedness parameters for
the agent in question. Yum indicates if it is on a food site, and Lit whether
its light is activated. Clicking on a row of the table highlights the location
of the agent in the world. It may be difficult to see, but agents are coloured
yellow in the world when their light is active. You can also see the efficiency
of the agent (which enters into its likelihood of reproduction) and the amount
of food consumed when you select an agent in the panel.

The graphs panel shows the average values of B, C, M and D and also those for
the most abundant ('dominant') species. This can be viewed in real time. You
can toggle on and off the different plots which can help you see which is which.
You can export these data into a file for further analysis. Each row of the table
in the output file corresponds to a time series; the columns correspond to:

generation average-B average-C average-M average-D dominant-B dominant-C dominant-M dominant-D

## Batch mode

If you want to run the simulation in batch mode, you can do so by compiling
the Batch class:

```
$ javac -d bin -cp src src/Batch.java
```

Many of the simulation parameters are baked in and match what was used in the
paper. The initial strategy, whether certain behaviours are immutable and
whether the neutrality adjustment was made can be controlled with arguments
to the Batch command.

To activate initial brightness, contrast, motility and directedness strategies
(i.e., to set their initial values to 1) put 'B', 'C', 'M' or 'D' (in combination) on
the command line. To fix the behaviour, add an 'x' after the relevant strategy.
If you want to activate the neutrality adjustment, add an 'f' to the character
sequence.

For example,

```
$ java -cp bin Batch B Cx M
```

would run in batch mode with brightness=1, contrast=1, motility=1 but directedness=0
for all agents. Contrast will remain 1 throughout the simulation; the other
strategies can mutate over time. No neutrality adjustment is made.

The output indicates whenever the dominant strategy changes, and what it is.
The columns are:

generation dominant-B dominant-C dominant-M dominant-D

By generating a large number of such files, and averaging over them, you can
recreate the datasets (up to statistical fluctuations) presented in the paper.
