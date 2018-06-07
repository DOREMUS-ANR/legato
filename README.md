# <b> <i> Legato </i> : Disambiguating and Linking Heterogeneous Resources Across RDF Graphs </b>

An automatic data linking tool developed by [DOREMUS][3].

<b> About <i> Legato </i> </b>
========
<b> <i> Legato </i> </b> is based on the following steps:
1. **Data cleaning**: A preprocessing step allowing for an efficient instances comparison.
2. **Instance profiling**: Instance represenation (subgraph) based on the Concise Bounded Description (CBD) of the classe allowing to extract information considered relevant for the entity comparison task.
3. **Indexing and Instance matching**: We apply standard NLP techniques to index the instance profiles by using a term frequency vector model. The threshold value of Legato applies to the similarity computed at this stage. Low thresholds are recommended to ensure high recall (default 0.2). 
4. **Link repairing**: A post-processing step to repair erroneous links generated in the matching step by clustering highly similar instances together and applying a key-identification adn ranking algorthims.

<b> How to run <i> Legato </i> </b>
========
For running <b> <i> Legato </i> </b> through the GUI, please run the "main.java" class in the "legato" package. Then, select the source, the target and a reference alignement (if availble). Then, you can choose between two treatment's modes:
- Automatic allows to filter resources by fixing only the classes to compare.
- Manual allows to filter resources by classe and comparate by a set of selected properties.
The field "threshold value" allows to define Legato's threshold in the Instance matching step. Legato will consider only resources with a similarity higher than the threshold value.
When you have chosen the mode and features for filter, click on "run" for link generation and (optionally) evaluating the produced links. If no reference alignement file exists, <b> <i> Legato </i> </b> matches the instances without evaluating the produced links.


Benchmark datasets: [DOREMUS data][1] (DOREMUS data about classical music).

The figure, below, illustrates <b> <i> Legato </i> </b>  running on [FPT data][2] :

![GUI](img/legato1.png)

<b> Requirements </b>
========
JDK 8 or later

[1]: https://github.com/DOREMUS-ANR/doremus-playground

[2]: https://github.com/DOREMUS-ANR/doremus-playground/tree/master/DS_FP

[3]: http://www.doremus.org/
