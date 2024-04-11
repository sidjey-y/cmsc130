/*
 * File: QuineMcCluskey.java
 * --------------------------
 * This file contains the QuineMcCluskey class, which implements the Quine-McCluskey method
 * for simplifying Boolean functions.
 *
 * Author: Shana Galman
 * Version: 1.0
 * Date: April 11, 2024
 *
 * Description:
 * This class provides methods to solve Boolean functions using the Quine-McCluskey method.
 * It includes functions to group terms, identify prime implicants, perform simplification,
 * and print the results.
 * References @ the end
 */

import java.util.*;
import javax.swing.JOptionPane;


public class QuineMcCluskeyMethod {

    /* comparator for comparing terms based on the number of ones in their binary form */

    private static class OnesComparator implements Comparator<Term>{

        /*compares two terms based on their number of ones*/

        @Override
        public int compare (Term x, Term y) {
            return x.getNumOnes() - y.getNumOnes();
        }
    }

    /* array of terms to store terms necessary for solution */
    private Term[] inputTerms;

    /* array list storing minterms entered by user */
    private ArrayList<Integer> inputMinterms;

    /* int value for the maximum length possible for solution */
    private int maximumLength;

    /* array list array containing solutions accumulated throughout the program */
    private ArrayList<String>[] solution;

    /* array list containing prime implicants accumulated throughout the program */
    private ArrayList<String> primeImplicants;

    /* array list storing every term necessary for the second stage of solving */
    private ArrayList<Term> finTerm;

    /* array list of array lists storing terms gathered from the first step of solving */
    public ArrayList<ArrayList<Term>[]> firstStep;

    /* array list of Hash sets storing checked terms gathered from the first step of solving */
    public ArrayList<HashSet<String>> checkFirst;

    /* array list storing simplified terms after using Petrick's method */
    public ArrayList<String> simplified;

    /* constructor for the initialization of an object that implements the Quine-McCluskey method */
    public QuineMcCluskeyMethod (String mintermsStr) {

        // converts minterms string input to int array

        int[] minterms = convertString(mintermsStr);

        // sorts minterms array
        Arrays.sort(minterms);

        // calculate max. length of prime implicants
        maximumLength = Integer.toBinaryString(minterms[minterms.length - 1]).length();

        this.inputMinterms = new ArrayList<>();

        primeImplicants = new ArrayList<String>();
        firstStep = new ArrayList<ArrayList<Term>[]>();
        checkFirst = new ArrayList<HashSet<String>>();
        simplified = new ArrayList<String>();

        // combine minterms in one array
        Term[] temp = new Term[minterms.length];
        int k = 0; // index in temp array
        for (int i = 0; i < minterms.length; i++) {
            temp[k++] = new Term(minterms[i], maximumLength);
            this.inputMinterms.add(minterms[i]);
        }

        // fill the terms array with terns
        inputTerms = new Term[k];
        for (int i = 0; i < k; i++) {
            inputTerms[i] = temp[i];
        }

        // sort terms according to number of ones
        Arrays.sort(inputTerms, new OnesComparator());
    }

    /* @return int array with minterms parsed from String input */
    private int[] convertString(String s) {
        // replace commas with spaces, if commas were used
        s = s.replace(",", " ");

        // if string is empty
        if (s.trim().equals("")) {
            return new int[] {};
        }

        // split string delimited by spaces and store in an array
        String[] a = s.trim().split(" +");
        int[] t = new int[a.length]; // array of minterms

        // parse strings in the array to integers, throw error message if not digits, strings, or commas
        for (int i = 0; i < t.length; i++) {
            try {
                // until it reaches outside bounds
                int temp = Integer.parseInt(a[i]);
                t[i] = temp;
            } catch (Exception e) {
                if (s.matches("[\\d,\\s]+"))
                    JOptionPane.showMessageDialog(null, "Invalid input. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // check for duplicates using a hash set. if no duplicates add to hash set, repeat until end of array
        HashSet<Integer> dup = new HashSet<>();
        for (int i = 0; i < t.length; i++) {
            if (dup.contains(t[i])) {
                JOptionPane.showMessageDialog(null, "Duplicates encountered. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            dup.add(t[i]);
        }

        return t;
    }

    /* @return array of array lists of terms where each element represents a group of terms with the same number of ones */
    private ArrayList<Term>[] group(Term[] terms) {
        // create an array of array lists based on their number of ones, with size from the maximum number of ones
        ArrayList<Term>[] groups = new ArrayList[terms[terms.length - 1].getNumOnes() + 1];

        // initialize each array list in the groups array
        for (int i = 0; i < groups.length; i++) {
            groups[i] = new ArrayList<>();
        }

        // group the terms based on their number of ones, respective of their index in the groups array
        for (int i = 0; i < terms.length; i++) {
            int k = terms[i].getNumOnes();
            groups[k].add(terms[i]);
        }

        return groups;
    }

    /* main solver method of the class to be called */
    public void solve(){
        // keep track of the unchecked terms
        ArrayList<Term> unchecked = new ArrayList<>();

        // gather the first list of grouped terms
        ArrayList<Term>[] list = group(this.inputTerms);

        // store resulting terms of each iteration
        ArrayList<Term>[] result;

        // add the current list to the firstStep array list array
        firstStep.add(list);

        // loop as long as result array is not empty and length > 1
        boolean insert = true;

        do {
            // store checked terms next
            HashSet<String> checked= new HashSet<>();

            // set result array to a new empty array
            result = new ArrayList[list.length - 1];

            ArrayList<String> temp;
            insert = false;

            // loop over
            for (int i = 0; i < list.length - 1; i++){
                result[i] = new ArrayList<>();
                // keep track of added terms in results to avoid duplicates
                temp = new ArrayList<>();

                // loop over each element in first group with all elements of second
                for (int j = 0; j < list[i].size(); j++){
                    // loop over each element in the second group
                    for (int k = 0; k < list[i + 1].size(); k++){
                        // check first if is a valid combination
                        if (checkValidity(list[i].get(j), list[i + 1].get(k))) {
                            // append the terms to be checked
                            checked.add(list[i].get(j).getString());
                            checked.add(list[i+1].get(k).getString());

                            Term n = new Term(list[i].get(j), list[i+1].get(k));

                            // check if resulting term is already in the results, don't add them
                            if (!temp.contains(n.getString())) {
                                result[i].add(n);
                                insert = true;
                            }
                            temp.add(n.getString());

                        }
                    }
                }
            }

            // if result is not empty and new terms generated, update unchecked
            if (insert) {
                for (int i = 0; i < list.length; i++) {
                    for (int j = 0; j < list[i].size(); j++) {
                        if (!checked.contains(list[i].get(j).getString())) {
                            // add the unchecked terms to the unchecked array list
                            unchecked.add(list[i].get(j));
                        }
                    }
                }
                list = result;

                // add result and checked to firstStep and checkedFirstStep array lists
                firstStep.add(list);
                checkFirst.add(checked);
            }
        } while (insert && list.length > 1);

        // copy resulting minterms into new array list along with unchecked terms
        finTerm = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            for (int j = 0; j < list[i].size(); j++) {
                finTerm.add(list[i].get(j));
            }
        }
        for (int i = 0; i < unchecked.size(); i++) {
            finTerm.add(unchecked.get(i));
        }

        solveSecond();
    }

    /* second stage of solution using Quine-McCluskey method recursively calls itself if there are still remaining minterms */
    public void solveSecond(){

        // identify prime implicants, first and check for row dominance then column dominance
        if (!identifyPrimeImplicants()) {
            if (!rowDominance()) {

                if (!columnDominance()) {
                    // if none succeeds, go to simplify method
                    simplify();
                    return;
                }
            }
        }

        // if there are still minterms to be taken call this function again
        if (inputMinterms.size() != 0)
            solveSecond();
            // if all minterms taken, add to solution
        else {
            solution = new ArrayList[1];
            solution[0] = primeImplicants;
        }
    }

    /*check if two terms are valid for grouping  */
    boolean checkValidity (Term term1, Term term2) {
        // check if both terms have the same length
        if (term1.getString().length() != term2.getString().length())
            return false;

        // count differing positions, return false immediately if '-' is paired with 0 or 1
        int k = 0;
        for (int i = 0; i < term1.getString().length(); i++) {
            if (term1.getString().charAt(i) == '-' && term2.getString().charAt(i) != '-')
                return false;
            else if (term1.getString().charAt(i) != '-' && term2.getString().charAt(i) == '-')
                return false;
            else if (term1.getString().charAt(i) != term2.getString().charAt(i))
                k++;
        }

        // only return true if there is exactly one differing position from both terms
        if (k != 1)
            return false;
        else
            return true;
    }

    /* check if two terms have all its numbers present in another term  */
    boolean contains(Term term1, Term term2) {
        // return false if the number of minterms grouped in term1 is less than or equal than that of term2's
        if (term1.getNums().size() <= term2.getNums().size()) {
            return false;
        }

        // else, gather all numbers associated with term1 and term2 in array lists
        ArrayList<Integer> a = term1.getNums();
        ArrayList<Integer> b = term2.getNums();

        // return true if all numbers in b are in a, else false
        if (a.containsAll(b))
            return true;
        else
            return false;
    }

    /*
     simplifies the solution done on the object using Petrick's method  
     source <a href="https://www.allaboutcircuits.com/technical-articles/prime-implicant-simplification-using-petricks-method/">Petrick's method</a>
    */

    void simplify(){
        HashSet<String>[] temp = new HashSet[inputMinterms.size()];

        //construct temp array containing sets of associated characters for minterms in finalTerms
        for (int i = 0; i < inputMinterms.size(); i++) {
            temp[i] = new HashSet<>();
            for (int j = 0; j < finTerm.size(); j++) {
                if (finTerm.get(j).getNums().contains(inputMinterms.get(i))) {
                    char t = (char) ('a' + j);
                    simplified.add(t + ": " + finTerm.get(j).getString());
                    temp[i].add(t + "");
                }
            }
        }

        // multiply sets in temp for simplification
        HashSet<String> finalResult = multiply(temp, 0);

        // identify minimum length terms in finalResult and count occurences
        int min = -1;
        int count = 0;
        for (Iterator<String> t = finalResult.iterator(); t.hasNext();) {
            String m = t.next();
            if (min == -1 || m.length() < min) {
                min = m.length();
                count = 1;
            } else if (min == m.length()) {
                count++;
            }
        }

        // add the simplified minimum terms to solutions
        solution = new ArrayList[count];
        int k = 0;
        for (Iterator<String> t = finalResult.iterator(); t.hasNext();) {
            String c = t.next();
            if (c.length() == min) {
                solution[k] = new ArrayList<>();
                for (int i = 0; i < c.length(); i++) {
                    solution[k].add(finTerm.get((int) c.charAt(i) - 'a').getString());
                }
                for (int i = 0; i < primeImplicants.size(); i++) {
                    solution[k].add(primeImplicants.get(i));
                }
                k++;
            }
        }
    }

    /* multiplies elements from sets at indeces adjacent to each other in the Hash set array and recurvisely computes for the product */
    HashSet<String> multiply(HashSet<String>[] p, int k){
        // check if k is greater than or equal to p.length - 1
        if (k >= p.length - 1)
            return p[k];

        // initialize resulting Hash set
        HashSet<String> s = new HashSet<>();

        // iterate through elements of p[k]
        for (Iterator<String> t = p[k].iterator(); t.hasNext();) {
            String temp2 = t.next();
            // iterate through elements of p[k +1]
            for (Iterator<String> g = p[k + 1].iterator(); g.hasNext();) {
                String temp3 = g.next();
                // add mixed elements to resulting Hash set
                s.add(mix(temp2, temp3));
            }
        }
        p[k + 1] = s; // update element from Hash set array at index k+1 with the resulting set
        return multiply(p, k + 1); // recursion to multiply the following sets until the end index
    }

    /* mixes terms and simplifies those that are duplicated with respect to properties of boolean expressions */
    String mix (String str1, String str2){
        // Hash set to immediately remove duplicates
        HashSet<Character> r = new HashSet<>();

        // add characters from str1 to Hash set r
        for (int i = 0; i < str1.length(); i++)
            r.add(str1.charAt(i));

        // add characters from str2 to Hash set r
        for (int i = 0; i < str2.length(); i++)
            r.add(str2.charAt(i));

        // construct resulting string by concatenating characters from Hash set r
        String result = "";
        for (Iterator<Character> i = r.iterator(); i.hasNext();)
            result += i.next();

        return result;
    }

    /* identify prime implicants, add them to primeImplicants array list, and remove from minterms and finalTerms array lists */
    private boolean identifyPrimeImplicants(){
        // initialize columns array to store indeces of final terms matching each minterm
        ArrayList<Integer>[] columns = new ArrayList[inputMinterms.size()];

        // fill columns with indeces of final terms that match each minterm
        for (int i = 0; i < inputMinterms.size(); i++) {
            columns[i] = new ArrayList();
            for (int j = 0; j < finTerm.size(); j++) {
                if (finTerm.get(j).getNums().contains(inputMinterms.get(i))) {
                    columns[i].add(j);
                }
            }
        }
        boolean isPrimeImplicant = false;

        // check each minterm's matched final terms for single matches
        for (int i = 0; i < inputMinterms.size(); i++) {
            if (columns[i].size() == 1) {
                isPrimeImplicant = true;

                // gather numbers of associated minterms with the prime implicant
                ArrayList<Integer> del = finTerm.get(columns[i].get(0)).getNums();

                // remove associated minterms from object's array of minterms
                for (int j = 0; j < inputMinterms.size(); j++) {
                    if (del.contains(inputMinterms.get(j))) {
                        inputMinterms.remove(j);
                        j--;
                    }
                }

                // add the identified prime implicant to the primeImplicant array list
                primeImplicants.add(finTerm.get(columns[i].get(0)).getString());
                // remove identified prime implicant from finalTerms array list
                finTerm.remove(columns[i].get(0).intValue());
                break;
            }
        }
        return isPrimeImplicant;
    }

    /* identify dominating columns and removes them from the minterms and finalTerms array lists */
    private boolean columnDominance(){
        boolean flag = false;

        // create a table
        ArrayList<ArrayList<Integer>> columns = new ArrayList<>();

        // fill columns with indeces of final terms that match each minterm
        for (int i = 0; i < inputMinterms.size(); i++){
            columns.add(new ArrayList<Integer>());
            for (int j = 0; j < finTerm.size(); j++){
                if (finTerm.get(j).getNums().contains(inputMinterms.get(i)))
                    columns.get(i).add(j);
            }
        }

        // identify dominating columns, where a column has its all its checks present in the other dominating columns, and remove them
        for (int i = 0; i < columns.size(); i++) {
            for (int j = i + 1; j < columns.size(); j++) {
                if (columns.get(j).containsAll(columns.get(i)) && columns.get(j).size() > columns.get(i).size()) {
                    columns.remove(j);
                    inputMinterms.remove(j);
                    j--;
                    flag = true;
                } else if (columns.get(i).containsAll(columns.get(j)) && columns.get(i).size() > columns.get(j).size()) {
                    columns.remove(i);
                    inputMinterms.remove(i);
                    i--;
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /* identify dominating rows and removes them from the minterms and finalTerms array lists */
    private boolean rowDominance(){
        boolean flag = false;

        // identify dominating rows, where a row has its all its checks present in the other dominating columns, and remove them
        for (int i = 0; i < finTerm.size() - 1; i++) {
            for (int j = i + 1; j < finTerm.size(); j++) {
                if (contains(finTerm.get(i), finTerm.get(j))) {
                    finTerm.remove(j);
                    j--;
                    flag = true;
                } else if (contains(finTerm.get(j), finTerm.get(i))) {
                    finTerm.remove(i);
                    i--;
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /* converts a term in binary form to standard form */
    String toStandardForm(String s) {
        StringBuilder r = new StringBuilder();

        // i keeps track of variables starting from 'A'
        for (int i = 0; i < s.length(); i++) {

            // ignores '-' and proceeds to next character
            if (s.charAt(i) == '-') {
                continue;
            }

            // unprimed variable if 1
            else if (s.charAt(i) == '1') {
                r.append((char) ('A' + i));
            }

            // primed variable if 0
            else {
                r.append((char) ('A' + i));
                r.append('\'');
            }
        }

        // if the resulting string is empty, append 1 to represent a constant
        if (r.toString().length() == 0) {
            r.append("1");
        }
        return r.toString();
    }

    /* build a String for the final resulting solutions to be presented to the user */
    public String printResults(String[] variables) {
        StringBuilder printedAnswer = new StringBuilder();
        for (int i = 0; i < solution.length; i++) {

            if (solution.length == 1)
                printedAnswer.append("Solution:").append("\n");
            else
                printedAnswer.append("Solution #").append(i+1).append(":").append("\n");

            // convert solution to standard form first, separate sum of products with '+'
            StringBuilder finalAnswer = new StringBuilder();
            for (int j = 0; j < solution[i].size(); j++) {
                finalAnswer.append(toStandardForm(solution[i].get(j)));
                if (j != solution[i].size() - 1) {
                    finalAnswer.append(" + ");
                }
            }

            // replace characters from converted standard form with their respective variable entered by the user
            for (int j = 0; j < finalAnswer.toString().length(); j++){
                if(finalAnswer.charAt(j) == 'A')
                    printedAnswer.append(variables[0]);
                else if (finalAnswer.charAt(j) == 'B')
                    printedAnswer.append(variables[1]);
                else if (finalAnswer.charAt(j) == 'C')
                    printedAnswer.append(variables[2]);
                else if (finalAnswer.charAt(j) == 'D')
                    printedAnswer.append(variables[3]);
                else if (finalAnswer.charAt(j) == 'E')
                    printedAnswer.append(variables[4]);
                else if (finalAnswer.charAt(j) == 'F')
                    printedAnswer.append(variables[5]);
                else if (finalAnswer.charAt(j) == 'G')
                    printedAnswer.append(variables[6]);
                else if (finalAnswer.charAt(j) == 'H')
                    printedAnswer.append(variables[7]);
                else if (finalAnswer.charAt(j) == 'I')
                    printedAnswer.append(variables[8]);
                else if (finalAnswer.charAt(j) == 'J')
                    printedAnswer.append(variables[9]);
                else
                    printedAnswer.append(finalAnswer.toString().charAt(j));
            }
            printedAnswer.append("\n\n");
        }
        return printedAnswer.toString();
    }
}


/*
 * References
https://arxiv.org/ftp/arxiv/papers/1410/1410.1059.pdf#:~:text=Quine%2DMcCluskey%20(QM)%20method,makes%20it%20an%20efficient%20technique. 
https://github.com/grejojoby/Quine-McCluskey-Algorithm-Java
https://courses.cs.washington.edu/courses/cse370/07au/Homeworks/Quine.html
https://github.com/archie94/Quine-McCluskey
https://www.tutorialspoint.com/digital_circuits/digital_circuits_quine_mccluskey_tabular_method.htm
https://softwarerecs.stackexchange.com/questions/47568/java-library-for-boolean-minimization
https://www.codeproject.com/Questions/811093/How-Do-I-Write-A-Code-To-Implement-Quine-Mccluskey
*/