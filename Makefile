all: compile run clean

compile: GeneticAlgorithm.java
	javac GeneticAlgorithm.java

run:
	java GeneticAlgorithm $(IMG) $(DIR)

rec: clean
	javac GeneticAlgorithmRec.java
	java GeneticAlgorithmRec $(IMG) $(DIR)

clean:
	rm -f *.class
