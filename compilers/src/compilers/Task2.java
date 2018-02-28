package compilers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Task2 {
	static String line = null;
	static String l1 = null;
	static String l2 = null;
	static String l3 = null;
	static String l4 = null;
	static String l5 = null;
	static String l6 = null;
	static String [] states;
	static String [] goals;
	static String [] alphabets;
	static String start;
	static String [] transitions;
	static String [] inputs;
	static ArrayList<Transition> transitionList = new ArrayList<>();
	public static void main(String[] args) throws IOException {
		FileReader fileReader = new FileReader("in1.in");
		BufferedReader br = new BufferedReader(fileReader);		
		while((line = br.readLine())!= null){
			//line = states
			transitionList.clear();
			l1 = br.readLine(); //Goal
			l2 = br.readLine(); //Alphabet
			l3 = br.readLine(); //Start State
			l4 = br.readLine(); //Transitions
			l5 = br.readLine(); //Input
			l6 = br.readLine(); //Should be empty
			if(!checkLines()){
				System.err.print("empty line.");
				continue;
			}
			states = line.split(",");
			goals = l1.split(",");
			if(!checkGoal()){
				continue;
			}
			alphabets = l2.split(",");
			start = l3;
			if(!checkStart()){
				System.err.println("Invalid start state "+start);
				continue;
			}
			transitions = l4.split("#");
			boolean error = false;
			boolean error2 = false;
			for(String transition : transitions){
				error2 = false;
				String [] transitionArray = transition.split(",");
				if(transitionArray.length != 3){
					error = true;
					break;
				}
				for ( int i = 0 ; i <2 ;i++){
					if(!inArray(transitionArray[i], states)){
						error2 = true;
						System.err.println("Invalid transition. "+transitionArray[i]+" is not included in the states.");
						break;
					}	
				}
				if(!inArray(transitionArray[2], alphabets) &&!transitionArray[2].equals("$")){
					error2 = true;
					System.err.println("Invalid transition. "+transitionArray[2]+" is not included in the alphabet.");
				}
				if(error2){
					break;
				}
				transitionList.add(new Transition(transitionArray[0],transitionArray[1],transitionArray[2]));
			}
			if(error){
				System.err.println("Invalid transition. Transitions should be of size 3");
				continue;
			}
			if(error2){
				continue;
			}
			inputs = l5.split("#");
			boolean error3 = false;
			String badInput = "";
			for(String input : inputs){
				String [] inputArray = input.split(",");
				for(String inputAlphabet : inputArray){
					if(!inArray(inputAlphabet, alphabets)){
						error3 = true;
						badInput = inputAlphabet;
						break;
					}
				}
			}
			if(error3){
				System.err.println("Invalid input string at "+badInput);
				continue;
			}
			System.out.println("NFA Constructed");
			System.out.println("Equivalent DFA: ");
			ArrayList<String> initialStateDFA = getAllEpsilonClosure(start, transitionList.toArray(new Transition[transitionList.size()]));
			ArrayList<Transition> NFATransitions= new ArrayList<>();
			ArrayList<ArrayList<String>> allStates = new ArrayList<>();
			NFATransitions = makeTransitions(initialStateDFA, transitionList.toArray(new Transition[transitionList.size()]), alphabets);
			for(int i = 0; i < NFATransitions.size() ; i ++) {
				addToStates(allStates,NFATransitions.get(i).fromAL);
				addToStates(allStates,NFATransitions.get(i).toAL);
				addTransitionsIfNotExists(NFATransitions,makeTransitions(NFATransitions.get(i).toAL, transitionList.toArray(new Transition[transitionList.size()]), alphabets));
			}
			//PRINTING ALL STATES
			String DFAStates = "";
			for(int i = 0 ; i<allStates.size();i++) {
				ArrayList<String> stateInAllStates = allStates.get(i);
				DFAStates += printStates(stateInAllStates);
				if(i<allStates.size()-1) {
					DFAStates += ",";
				}
			}
			System.out.println(DFAStates);
			
			//PRINTING GOAL STATES
			String DFAGoals = "";
			for(int i = 0 ; i<allStates.size();i++) {
				ArrayList<String> stateInAllStates = allStates.get(i);
				if(hasAcceptState(goals, stateInAllStates)) {
					DFAGoals += printStates(stateInAllStates);
					if(i<allStates.size()-1) {
						DFAGoals += ",";
					}
				}
			}
			System.out.println(DFAGoals);
			
			//PRINTING ALPHABET
			System.out.println(l2);
			
			//PRINTING INITIAL STATE
			String DFAInitState = printStates(initialStateDFA);
			System.out.println(DFAInitState);
			
			//PRINTING ALL TRANSITIONS
			String DFATransitions = "";
			for(int i = 0 ; i<NFATransitions.size();i++) {
				DFATransitions+=printStates(NFATransitions.get(i).fromAL);
				DFATransitions+=",";
				DFATransitions+=printStates(NFATransitions.get(i).toAL);
				DFATransitions+=",";
				DFATransitions+=NFATransitions.get(i).alphabet;
				if(i < NFATransitions.size() - 1) {
					DFATransitions+="#";
				}
			}
			System.out.println(DFATransitions);
			
			//PRINTING INPUT
			System.out.println(l5);
			
			constructAndSolveDFA(DFAStates, DFAGoals, l2, DFAInitState, DFATransitions, l5);
		}
		br.close();
	}
	
	public static void constructAndSolveDFA(String DFAstates, String DFAacceptStates, String DFAAlphabet, String DFAinitState, String DFAtransitions, String DFAinput ) {
		String line = DFAstates;
		String l1 = DFAacceptStates;
		String l2 = DFAAlphabet;
		String l3 = DFAinitState;
		String l4 = DFAtransitions;
		String l5 = DFAinput;
		states = line.split(",");
		goals = l1.split(",");
		if(!checkGoal()){
			return;
		}
		alphabets = l2.split(",");
		start = l3;
		if(!checkStart()){
			System.err.println("Invalid start state "+start);
			return;
		}
		transitions = l4.split("#");
		boolean error = false;
		boolean error2 = false;
		for(String transition : transitions){
			error2 = false;
			String [] transitionArray = transition.split(",");
			if(transitionArray.length != 3){
				error = true;
				break;
			}
			for ( int i = 0 ; i <2 ;i++){
				if(!inArray(transitionArray[i], states)){
					error2 = true;
					System.err.println("Invalid transition. "+transitionArray[i]+" is not included in the states.");
					break;
				}	
			}
			if(!inArray(transitionArray[2], alphabets) &&!transitionArray[2].equals("$")){
				error2 = true;
				System.err.println("Invalid transition. "+transitionArray[2]+" is not included in the alphabet.");
			}
			if(error2){
				break;
			}
			transitionList.add(new Transition(transitionArray[0],transitionArray[1],transitionArray[2]));
		}
		if(error){
			System.err.println("Invalid transition. Transitions should be of size 3");
			return;
		}
		if(error2){
			return;
		}
		inputs = l5.split("#");
		boolean error3 = false;
		String badInput = "";
		for(String input : inputs){
			String [] inputArray = input.split(",");
			for(String inputAlphabet : inputArray){
				if(!inArray(inputAlphabet, alphabets)){
					error3 = true;
					badInput = inputAlphabet;
					break;
				}
			}
		}
		if(error3){
			System.err.println("Invalid input string at "+badInput);
			return;
		}
		boolean error4 = false;
		for(String state : states){
			for(String alphabet : alphabets){
				if(!existsTransition(state,alphabet)){
					error4 = true;
					System.err.println("Missing transition for state " +state+" on input "+ alphabet );
					break;
				}
			}
		}
		if(error4){
			return;
		}
		System.out.println("DFA Constructed");
		for(String input : inputs){
			String result = processInput(input);
			if(inArray(result, goals)){
				System.out.println("Accepted");
			} else {
				System.out.println("Rejected");
			}
		}
		System.out.println("");
	}

	private static void addToStates(ArrayList<ArrayList<String>> allStates, ArrayList<String> someStates) {
			if(!allStates.contains(someStates)) {
				allStates.add(someStates);
			}		
	}
	public static void addTransitionsIfNotExists(ArrayList<Transition> nFATransitions,ArrayList<Transition> newTransitions) {
		for(int i = 0 ; i< newTransitions.size() ; i++) {
			Collections.sort(newTransitions.get(i).fromAL);
			Collections.sort(newTransitions.get(i).toAL);
			int j;
			for (j = 0;j < nFATransitions.size(); j++) {
				Collections.sort(nFATransitions.get(j).fromAL);
				Collections.sort(nFATransitions.get(j).toAL);
				if(nFATransitions.get(j).fromAL.equals(newTransitions.get(i).fromAL) && nFATransitions.get(j).toAL.equals(newTransitions.get(i).toAL) && nFATransitions.get(j).alphabet.equals(newTransitions.get(i).alphabet)){
					break;
				}
			}
			if(j == nFATransitions.size()) {
				nFATransitions.add(newTransitions.get(i));
			}
		}
	}
	private static String processInput(String input) {
		String currentState = start;
		String [] inputArray = input.split(",");
		for(int i = 0 ; i< inputArray.length ;i++){
			for(int j = 0 ; j < transitionList.size() ; j++){
				if(transitionList.get(j).from.equals(currentState) && transitionList.get(j).alphabet.equals(inputArray[i])){
					currentState = transitionList.get(j).to;
					break;
				}
			}
		}
		return currentState;
	}
	private static boolean checkStart() {
		return inArray(start, states);
	}
	private static boolean checkGoal() {
		for(String goal : goals){
			if(goal.equals("")){
				continue;
			}
			if(!inArray(goal,states)){
				System.err.println("Invalid accept state "+goal);
				return false;
			}
		}
		return true;
	}
	private static boolean checkLines() {
		if(l1 == ""){
			System.err.println("First line is an");
			return false;
		}
		if(l2 == ""){
			System.err.println("Second line is an");
			return false;
		}
		if(l3 == ""){
			System.err.println("Third line is an");
			return false;
		}
		if(l4 == ""){
			System.err.println("Fourth line is an");
			return false;
		}
		if(l5 == ""){
			System.err.println("Fifth line is an");
			return false;
		}
		if(l6 == ""){
			System.err.println("Last line is not an");
			return false;
		}
		return true;
	}
	
	private static boolean inArray(String s , String [] array){
		for(int i = 0 ; i < array.length;i++){
			if(array[i].equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public static ArrayList<String> getEpsilonClosure(String state,Transition[]transitions){
		ArrayList<String> result = new ArrayList<>();
		result.add(state);
		for(int i = 0 ; i<transitions.length;i++) {
			if(transitions[i].alphabet.equals("$") && transitions[i].from.equals(state)&&!result.contains(transitions[i].to)) {
				result.add(transitions[i].to);
			}
		}
		return result;
	}
	
	public static ArrayList<String> getAllEpsilonClosure(String state,Transition[]transitions){
		ArrayList<String> result = getEpsilonClosure(state, transitions);
		for(int i = 0 ; i < result.size();i++) {
			ArrayList<String> newOutcome = getEpsilonClosure(result.get(i), transitions);
			for(int j = 0 ; j<newOutcome.size();j++) {
				if (!result.contains(newOutcome.get(j))) {
					result.add(newOutcome.get(j));
				}
			}
		}
		return result;
	}
	
	public static boolean hasAcceptState(String[] acceptStates, ArrayList<String> stateOfStates) {
		for(int i = 0 ; i < stateOfStates.size();i++) {
			for( int j = 0 ; j < acceptStates.length ;j++) {
				if(acceptStates[j].equals(stateOfStates.get(i))) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static ArrayList<String> getStatesForGivenInput(ArrayList<String> stateOfStates, Transition[]transitions, String alphabet){
		ArrayList<String> result = new ArrayList<>();
		for(int i = 0 ; i < stateOfStates.size() ; i++) {
			for(int j = 0 ; j < transitions.length ;j++) {
				if(transitions[j].alphabet.equals(alphabet) && transitions[j].from.equals(stateOfStates.get(i))&&!result.contains(transitions[j].to)) {
					result.add(transitions[j].to);
					addIfNotContains(result, getAllEpsilonClosure(transitions[j].to, transitions));
				}
			}
		}
		return result;
	}
	public static void addIfNotContains(ArrayList<String> result, ArrayList<String> arrayToBeAdded) {
		for(int i = 0; i<arrayToBeAdded.size();i++) {
			if(!result.contains(arrayToBeAdded.get(i))) {
				result.add(arrayToBeAdded.get(i));
			}
		}
	}
	
	public static ArrayList<Transition> makeTransitions(ArrayList<String> stateOfStates,Transition[]transitions,String[]alphabets) {
		ArrayList<Transition> result= new ArrayList<>();
		for(int i = 0 ; i< alphabets.length ; i++) {
			ArrayList<String> toStates = getStatesForGivenInput(stateOfStates, transitions, alphabets[i]);
			if(toStates.size() == 0) {
				toStates.add("Dead");
			}
			result.add(new Transition(stateOfStates, toStates, alphabets[i]));
		}
		return result;
	}
	public static String printStates(ArrayList<String>states) {
		String r = "";
		for(int i = 0 ; i<states.size();i++) {
			r+=states.get(i);
			if(i < states.size() - 1) {
				r+="*";
			}
		}
		return r;
	}
	private static boolean existsTransition(String state, String alphabet) {
		for(int i = 0 ; i < transitionList.size() ; i++){
			if(transitionList.get(i).from.equals(state) && transitionList.get(i).alphabet.equals(alphabet)){
				return true;
			}
		}
		return false;
	}
}

class Transition {
	String from;
	String to;
	ArrayList<String> fromAL;
	ArrayList<String> toAL;
	String alphabet;
	public Transition(String from, String to, String alphabet){
		this.from = from;
		this.to = to;
		this.alphabet = alphabet;
	}
	public Transition(ArrayList<String> from, ArrayList<String> to, String alphabet){
		this.fromAL = from;
		this.toAL = to;
		this.alphabet = alphabet;
	}
}







