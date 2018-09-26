NAME1="Draw"
#NAME2="A2Enhanced"
# you may need to pass OS=win to run on windows
# HACK: vecmath is included regardless if needed
all:
	@echo "Compiling..."
	javac -cp vecmath.jar $(NAME1).java
#	javac -cp vecmath.jar $(NAME2).java
run: all
		@echo "Running ..."
		java -cp "vecmath.jar:." $(NAME1)
#		java -cp "vecmath.jar:." $(NAME2)

clean:
	rm -rf *.class
	rm -rf view/*.class
	rm -rf model/*.class
