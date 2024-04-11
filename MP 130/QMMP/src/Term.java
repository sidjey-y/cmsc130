/*
 * File: Term.java
 * --------------------------
 *
 * Author: Shana Galman
 * Version: 1.0
 * Date: April 11, 2024
 */


// importing the ArrayList class from java.util package for handling dynamic arrays.
import java.util.*;

// definition of the Term class.
public class Term {

    // declaration of private member variables inputTerms, num, and nums.
    private String inputTerms;
    private int num;
    private ArrayList<Integer> nums;

    // constructor to initialize a Term object with a single value and a specified length.
    public Term (int value, int length){

        // converting the integer value to its binary representation as a string.
        String binary = Integer.toBinaryString(value);

        // ensuring that the binary string has the specified length by adding leading zeros if necessary.
        StringBuffer temp = new StringBuffer(binary);
        while (temp.length() != length){
            temp.insert(0, 0);
        }
        // setting the inputTerms to the binary string.
        this.inputTerms = temp.toString();

        // initializing the nums ArrayList with the value.
        nums = new ArrayList<Integer>();
        nums.add(value);

        // counting the number of '1's in the binary string.
        num = 0;
        for (int i = 0; i < inputTerms.length(); i++){
            if(inputTerms.charAt(i) == '1')
                num++;
        }
    }

    // Constructor to initialize a Term object based on two other Term objects.
    public Term (Term term1, Term term2){
        // Scanning both terms and replacing non-matching characters with '-'.
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < term1.getString().length(); i++){
            if (term1.getString().charAt(i) != term2.getString().charAt(i))
                temp.append("-");
            else
                temp.append(term1.getString().charAt(i));
        }
        // Setting the inputTerms to the constructed string.
        this.inputTerms = temp.toString();

        // Counting the number of '1's in the constructed string.
        num = 0;
        for (int i = 0; i < inputTerms.length(); i++){
            if (this.inputTerms.charAt(i) == '1')
                num++;
        }

        // Initializing the nums ArrayList by combining the lists from term1 and term2.
        nums = new ArrayList<Integer>();
        for (int i = 0; i < term1.getNums().size(); i++){
            nums.add(term1.getNums().get(i));
        }
        for (int i = 0; i < term2.getNums().size(); i++){
            nums.add(term2.getNums().get(i));
        }
    }

    // Getter method to retrieve the inputTerms string.
    String getString() {
        return inputTerms;
    }

    // Getter method to retrieve the nums ArrayList.
    ArrayList<Integer> getNums(){
        return nums;
    }

    // Getter method to retrieve the number of '1's in the inputTerms string.
    int getNumOnes(){
        return num;
    }
}
