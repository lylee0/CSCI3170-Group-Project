all: Main.class

Main.class:
	javac -encoding UTF-8 -cp .ojdbc7.jar *.java 

run:
	java -cp ojdbc7.jar:. Main
