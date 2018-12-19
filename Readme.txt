Assignment 1: Keyur Kirti Mehta

Main classes are as follows:
Queue.java
masterObj.java
processObj.java
LamportSimu.java

makefile will compile all the java files and create respective class files

Below are the steps to execute the application:

1. Login to any machine of CSCI using valid credentials.

2. Once logged in go to desired path and execute below commands to complie the code
	a. cd %FilePath%
	b. make
this will compile all the java file and create corresponding class files

3. execute below commands to run
	java LamportSimu 

Simulation will start and save the logical clock value in the 'Thread_LogiClk.txt' file at same location.
User can change the t (number of events per iteration),probability and number of iteration to run during execution from 
LamportSimu.java file.