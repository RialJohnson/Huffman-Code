/** Lab #1 Huffman Encoding
 * Section 01 (6-8 p.m.)
 * Rial Johnson & Connor Overcast
 */

package code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Scanner;
import java.io.File;

public class HuffApp {
	private int[] freqTable = new int[ASCII_TABLE_SIZE];
	private final static int ASCII_TABLE_SIZE = 128;
	private String originalMessage = "";
	private PriorityQ theQueue;
	private HuffTree huffTree;
	private String encodedMessage = "";
	private String[] codeTable;
	private String decodedMessage = "";
	private int charUsedCount = 0; // counts number of chars actually used

	public static void main(String[] args) {
		new HuffApp();
	}

	public HuffApp() {
		codeTable = new String[ASCII_TABLE_SIZE];
		readInput();
		displayOriginalMessage();
		makeFrequencyTable(originalMessage);
		displayFrequencyTable();
		addToQueue();
		buildTree(theQueue);
		makeCodeTable(huffTree.root, "");
		encode();
		displayEncodedMessage();
		displayCodeTable();
		decode();
		displayDecodedMessage();
	}

	// reads input from .txt file save to originalMessage field
	private void readInput() {

		try {
			originalMessage = new Scanner(new File("input.txt")).useDelimiter("//A").next();
		} catch (FileNotFoundException exception) { // catches if a file isn't
													// found
			System.out.print("File not found!");
		} catch (IOException exception) { // catches everything else
			System.out.println("Something went terribly wrong!");
		}
	}

	// prints out the original string
	private void displayOriginalMessage() {
		System.out.println("Original message: " + originalMessage);
	}

	// populate the frequency table using inputString. results are saved to the
	private void makeFrequencyTable(String inputString) {

		// loops for string and counts the frequency of each character
		for (int i = 0; i < inputString.length(); i++) {
			char tempChar = inputString.charAt(i); // stores character
			int asciiValue = (int) tempChar; // cast the char to its ascii value

			freqTable[asciiValue] = freqTable[asciiValue] + 1; // adds 1 to //
																// array value
		}
	}

	// print the frequency table. skipping any elements that are not represented
	private void displayFrequencyTable() {
		
		System.out.println("\nFrequency Table");
		System.out.println("char | val"); // formatting

		// prints out the frequency by looping through every ascii value
		for (int i = 0; i < ASCII_TABLE_SIZE; i++) {
			if (freqTable[i] != 0) {
				System.out.println((char) i + "    | " + freqTable[i]);
				charUsedCount++;
			}
		}
		
		System.out.println(""); // formatting
	}

	// add the values in the frequency table to the PriorityQueue.
	private void addToQueue() {

		int[] inOrderFreq = new int[charUsedCount + 1];
		int count = 0;
		int indexOfI = 0;

		// iterate through new array to store frequencies in descending order
		for (int j = 0; j <= charUsedCount; j++) {

			int max = 0;

			// checks every value in ascii table to find maximum frequency
			for (int i = 0; i < ASCII_TABLE_SIZE; i++) {
				if (freqTable[i] > max) {

					boolean okayToAdd = true;
					// checks if value is already in ordered frequency array
					for (int k = 0; k < charUsedCount; k++) {

						if (i == inOrderFreq[k]) {
							okayToAdd = false;
						}
					}

					// if value is not already in array, adds it to array
					if (okayToAdd) {
						max = freqTable[i];
						indexOfI = i;
					}
				}

			}
			inOrderFreq[count] = indexOfI;
			count++;
		}

		theQueue = new PriorityQ(charUsedCount);

		// loops through in order frequency array
		for (int i = 0; i < inOrderFreq.length - 1; i++) {
			huffTree = new HuffTree((char) inOrderFreq[i], freqTable[inOrderFreq[i]]); // creates
																						// nodes
			theQueue.insert(huffTree); // inserts the node
		}

	}

	// Pull items from the priority queue and combine them to form
	// a HuffTree. Saves the results to the huffTree field
	private void buildTree(PriorityQ hufflist) {


		// loops until there is only one item in the tree
		while (theQueue.getSize() > 1) {

			int freq1 = theQueue.peekMin().getWeight(); // freq of left child
			HuffTree leftChild = theQueue.peekMin(); // saves tree
			theQueue.remove(); // removes minimum from the tree

			int freq2 = theQueue.peekMin().getWeight(); // freq of right child
			HuffTree rightChild = theQueue.peekMin(); //
			theQueue.remove();

			// creates new tree with combined trees
			huffTree = new HuffTree((freq1 + freq2), leftChild, rightChild);
			theQueue.insert(huffTree); // inserts new tree into priority queue
		}
	}

	// traverses through tree to make huffman code
	private void makeCodeTable(HuffNode huffNode, String bc) {

		// checks if current node is null
		if (huffNode != null) {

			// if the left child isn't null
			if (huffNode.leftChild != null) {
				makeCodeTable(huffNode.leftChild, bc + "0"); // traverse left
																// and add 0 to
																// string
			}

			// if the right child isn't null
			if (huffNode.rightChild != null) {
				makeCodeTable(huffNode.rightChild, bc + "1"); // traverse right
																// and add 1 to
																// string
			}

			// if the node is a leaf
			if (huffNode.leftChild == null && huffNode.rightChild == null) {

				codeTable[(int) huffNode.character] = bc; // add the string to
															// the code table
															// array
			}
		}

	}

	// print code table, skipping any empty elements
	private void displayCodeTable() {
		
		// formatting
		System.out.println("");
		System.out.println("Code Table"); 
		System.out.println("char | val");
		
		// iterates through ascii table
		for (int i = 0; i < ASCII_TABLE_SIZE; i++) {
			// prints anything with a weight > 0
			if (freqTable[i] != 0) {
				System.out.println((char) i + "    | " + codeTable[i]); // prints avalues
			}
		}
		
		// formatting
		System.out.println("");
	}

	// encodes the original message 
	private void encode() {

		// iterates through original message
		for (int i = 0; i < originalMessage.length(); i++) {
			// iterates through ascii table
			for (int j = 0; j < ASCII_TABLE_SIZE; j++) {
				if (j == (int) originalMessage.charAt(i)) {
					encodedMessage = encodedMessage + codeTable[j]; // changes value to binary
				}
			}
		}
	}

	// displays encoded message
	private void displayEncodedMessage() {
		System.out.println("Encoded message:");
		System.out.println(encodedMessage);
	}

	// decode the message and store the result in the decodedMessage field
	private void decode() {
		
		HuffNode huffNode = huffTree.root; // assigns root of tree to a variable

		// iterates through encoded message
		for (int i = 0; i < encodedMessage.length(); i++) {
			
			// loops until the node is a leaf
			while (!huffNode.isLeaf()) {
				
				// goes left for 0
				if (encodedMessage.charAt(i) == '0') {
					huffNode = huffNode.leftChild;
					i++;
				}

				// goes right for 1
				else if (encodedMessage.charAt(i) == '1') {
					huffNode = huffNode.rightChild;
					i++;
				}
			}

			// adds value to decoded message
			decodedMessage = decodedMessage + huffNode.character; 
			
			huffNode = huffTree.root; // resets node to root
			i--; // decrements to prevent letter skipping

		}

	}

	// displays decoded message
	public void displayDecodedMessage() {
		System.out.println("Decoded message: " + decodedMessage);
	}

}
