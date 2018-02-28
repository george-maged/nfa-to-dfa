# NFAtoDFA

This is a java project that converts a given NFA with inputs to its corresponding DFA and whether the input is accepted or rejected.

## Input File

The input file can contain multiple NFAs but they have to be separated by an empty line.

Each NFA is composed of the following lines:

1. Set of states separated by commas.
2. Set of accept states separated by commas.
3. Input alphabet separated by commas.
4. The initial state.
5. The transitions between states separated by hashtags.
  * Each transition contains the source state followed by the target state followed by the given input alphabet.  
   _Example: S1,S2,1 means S1 goes to S2 given 1_.  
6. Set of inputs separated by hashtags.  
  * Each input contains the input alphabet separated by commas
  _Example: 1,2,0,1,2#1,1,1 will result in passing 1,2,0,1,2 to the DFA and returning either **"ACCEPTED"** or **"REJECTED"** and then it will pass 1,1,1 and return **"ACCEPTED"** or **"REJECTED"**.
  
## Output

The output is printed in the console after the NFA is converted to the DFA and the input is passed to the DFA.  
As mentioned before for each input string it prints either **"ACCEPTED"** or **"REJECTED"**.

The algorithm also checks for problems with the provided NFA, such as an invalid transition, accept state, initial state or input string.

## How to use it

I did not make a command line interface or JAR file or anything like that because this was a uni project and I can't be bothered.

1. Import the project to your Java editor.
2. Copy the input file in the project folder.
3. Change the file name in Task2.java.
4. Run.
