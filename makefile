SRC_DIR := src
BUILD_DIR := bin
TEST_DIR := tests

CLASSPATH := lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar

MAIN-RECEIVER = receiver.Receiver
MAIN-SENDER = sender.Sender
TESTS = tests.BitUtilsTest tests.SessionTest tests.NetworkAbstractionTest


JAVA_FILES := $(shell find ./src -type f -name "*.java")

JAVAC=javac -d $(BUILD_DIR)

## all					-> Base rule : Compiles from scratch, apply whole test suite, and run the program
all: clean build test set-executable

## help					-> Shows this help.
help:
	@fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$$//' | sed -e 's/##//'

## clean				-> Cleans the build directory and unnecessary files
clean :
	@echo "Cleaning files"
	@rm -rf $(BUILD_DIR)
	@rm -f Sender SendAndReceiveTest Receiver output.txt
	@find ./src -type f -name "*.class" -exec rm -f {} \;

## build				-> Builds the entire program and testing suite
build :
	@echo "Building whole program"
	@mkdir -p $(BUILD_DIR)
	@$(JAVAC) -cp $(CLASSPATH) $(JAVA_FILES)

## run-receiver	-> Runs the receiver with some default values
run-receiver : build set-executables
	./Receiver 3547 0.5 "testOutput.txt"

## run-sender		-> Runs the sender with some default values
run-sender : build set-executables
	./Sender localhost 3547 testInput.txt 0

## set-aliases  -> Set up aliases for running the programs easily
set-executable :
	@echo "Setting up executables"
	@echo "#!/bin/bash" >Sender
	@echo "java -cp "bin" sender.Sender \$$@" >>Sender
	@echo "#!/bin/bash" >Receiver
	@echo "java -cp "bin" receiver.Receiver \$$@" >>Receiver
	@echo "#!/bin/bash" >SendAndReceiveTest
	@echo "java -cp "bin" SendAndReceiveTest \$$@" >>SendAndReceiveTest
	@chmod +x Receiver
	@chmod +x Sender
	@chmod +x SendAndReceiveTest

## tests  			-> Apply test suite to compiled program
test: build
	@echo "Running JUnit test suite"
	@java -cp "$(CLASSPATH):./$(BUILD_DIR)" org.junit.runner.JUnitCore $(TESTS)

