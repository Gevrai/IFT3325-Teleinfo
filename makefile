SRC_DIR := src
BUILD_DIR := bin
TEST_DIR := tests

CLASSPATH := $(TEST_DIR)/lib/junit-4.12.jar:$(TEST_DIR)/lib/hamcrest-core-1.3.jar

MAIN-RECEIVER = receiver.Receiver
MAIN-SENDER = sender.Sender
TESTS = tests.BitStreamTest

CLASSES := \
	./src/utils/*.java \
	./src/receiver/*.java \
	./src/sender/*.java

JAVAC=javac -d $(BUILD_DIR)

## all   -> Base rule : Compiles from scratch, apply whole test suite, and run the program
all: clean build run

## help  -> Shows this help.
help:
	@fgrep -h "##" $(MAKEFILE_LIST) | fgrep -v fgrep | sed -e 's/\\$$//' | sed -e 's/##//'

## clean -> Cleans the build directory
clean :
	-rm -rf $(BUILD_DIR)

## build -> Builds the entire program
build :
	mkdir -p $(BUILD_DIR)
	$(JAVAC) -cp $(CLASSES)

## run-receiver   -> Runs the receiver with some default values
run-receiver : build
	java -cp "$(BUILD_DIR)" $(MAIN-RECEIVER) 3547

## run-sender   -> Runs the sender with some default values
run-sender : build
	java -cp "$(BUILD_DIR)" $(MAIN-SENDER) localhost 3547 test.txt 0

## test  -> Apply test suite to compiled program
test: build
	java -cp "$(CLASSPATH):./$(BUILD_DIR)" org.junit.runner.JUnitCore $(TESTS)
