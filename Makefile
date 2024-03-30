all: Main.class

main.class:
	javac *.java

run:
	java -cp ojdbc7.jar:. Main
