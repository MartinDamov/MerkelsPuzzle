import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


/**
 * Class to read a file and crack a random puzzle.
 */
public class PuzzleCrack {
	
	private String _fileName;
	
	private int puzzleNumber;
	private String key;
	
	/**
	 * Default constructor. Sets the file Name
	 * @param fileName name of file containing puzzles 
	 */
	public PuzzleCrack(String fileName) {
		this._fileName = fileName;
	}
	
	/**
	 * Cracks a randomly chosen puzzle and displays gathered information.
	 * <p>
	 * A puzzle is randomly chosen and then cracked using a brute force approach.
	 * All possible keys are tried in order to decrypt the puzzle, if a valid puzzle pattern is found the 
	 * information is displayed and the search for keys stopped. 
	 * @return int number of cracked puzzle
	 */
	public int crackPuzzle() {
		
		// get a puzzle
		String puzzleStr = choosePuzzle();
		
		// read through the puzzle to get needed data
		byte[] puzzle = CryptoLib.stringToByteArray(puzzleStr);
		
		//crack chosen puzzle
		crackChosenPuzzle(puzzle);
		
		return puzzleNumber;
	}
	
	/**
	 * Gets the key stored in a previously cracked puzzle.
	 * <p>
	 * Gets the saved information of the puzzle and returns a secret key.
	 * @return retrieved SecretKey 
	 */
	public SecretKey getKey(){
		SecretKey secretKey=null;
		
		try {
			//key is created from a string
			secretKey = CryptoLib.createKey(CryptoLib.stringToByteArray(key));
		} 
		catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException e) {
			//Display exception
			System.out.println("An error occurred when retrieving key from cracked puzzle. Execution was aborted.\r\n" + e.getMessage());
			e.printStackTrace();
			//Abort program
			System.exit(1);
		}
		return secretKey;
	}
	
	/**
	 * Opens the specified puzzle file and chooses a random puzzle.
	 * <p>
	 * A random number < 1024 is chosen. Then the file is opened and the line following the random number is read.
	 * The result of the read is returned.
	 * Occurring exceptions are handled and will abort the program. 
	 * @return String random Puzzle 
	 */
	private String choosePuzzle() {
		String randomPuzzle = null;
		
		// try to read from the file and get a random puzzle
		try {
			
			// choose a random number < 1024 (1024 puzzles existing) 
			int puzzleNumber = randomPuzzleNumber(0, 1023);
			
			//open a file stream
			try (Stream<String> lines = Files.lines(Paths.get(_fileName))) {
			    //random puzzle is retrieved by skipping irrelevant lines
				randomPuzzle = lines.skip(puzzleNumber).findFirst().get();
			}
			
		} catch (IOException e) {
			//Display exception
			System.out.println("An error occurred while choosing a random puzzle. Execution was aborted.\r\n" + e.getMessage());
			e.printStackTrace();
			//Abort program
			System.exit(1);
		}
		return randomPuzzle;
	}
	
	/**
	 * Cracks a given puzzle using a brute force approach.
	 * <p>
	 * Generates all possible keys in a loop according to the key pattern.
	 * The loop is aborted if a fitting key is found.
	 * Key verification is achieved by trying to decrypt the given puzzle.
	 * Occurring exceptions are handled and will abort the program.
	 * @param puzzle puzzle to crack
	 */
	private void crackChosenPuzzle(byte[] puzzle){
		
		//empty array for key
		byte[] keyPart = new byte[8];
		
		for(int i=0; i<= 65535; i++){
			
			//fill the first two bytes of key part
			System.arraycopy(CryptoLib.smallIntToByteArray(i),0, keyPart, 0, 2);
			//fill key part with 6 bytes of 0
			Arrays.fill(keyPart, 2, 7, (byte)0);
		
			//generate DES key with given pattern
			try {
				
				//generate secret key
				SecretKey key = CryptoLib.createKey(keyPart);
				
				//try decryption
				if(tryDecryption(puzzle, key)){
					
					//key was found, information was extracted
					break;
				}
			} 
			catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException e) {
				//Display exception
				System.out.println("An error occurred when cracking puzzle. Execution was aborted.\r\n" + e.getMessage());
				e.printStackTrace();
				//Abort program
				System.exit(1);
			}	
		}	
	}
	
	/**
	 * Tries to decrypt the puzzle with a given key.
	 * <p>
	 * The puzzle - key combination is used to decrypt using DES.
	 * If the decryption process does not return errors or exceptions the first byte of the puzzle is checked.
	 * This puzzle is then checked further in getPuzzleInformation. 
	 * @param puzzle puzzle to decrypt
	 * @param key key to try
	 * @return true if key is valid, else false
	 */
	private boolean tryDecryption(byte[] puzzle, SecretKey key){
		
		byte[] decryptedPuzzle = null;
		
		try {
			//get a DES cipher Instance
			Cipher cipher = Cipher.getInstance("DES");
			//put cipher in decryption mode
			cipher.init(Cipher.DECRYPT_MODE, key);
			//decrypt puzzle
			decryptedPuzzle = cipher.doFinal(puzzle);
		} 
		//decryption failed
		catch(BadPaddingException e){
			return false;
		}
		//something went wrong...
		catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
			//Display exception
			System.out.println("An error occurred when trying to decrypt puzzle. Execution was aborted.\r\n" + e.getMessage());
			e.printStackTrace();
			//Abort program
			System.exit(1);
		}
		
		//decryption successful
		//check if first byte contains 0	
		if(decryptedPuzzle[0] == 0){
			//get all information in puzzle	
			return getPuzzleInformation(decryptedPuzzle);	
		}
		else{
			//not a appropriat key
			return false;
		}
	}
	
	/**
	 * Checks the puzzles format and retrieves information stored in the puzzle.
	 * <p>
	 * First the format is checked (16 bytes of 0).
	 * The the puzzle number and the secret key are retrieved, stored in a global variable and displayed.
	 * @param decryptedPuzzle a decrypted puzzle
	 * @return true if the format is right, else false
	 */
	private boolean getPuzzleInformation(byte [] decryptedPuzzle){
		
		//check if first 16 bytes are 0
		for(int i=0; i<16; i++){
			if(decryptedPuzzle[i] != 0){
				//not the right format
				return false;
			}
		}
		
		//get puzzle number
		puzzleNumber = CryptoLib.byteArrayToSmallInt(Arrays.copyOfRange(decryptedPuzzle, 16, 18));
		System.out.println("--PuzzleCrack: The puzzle number is " + Integer.toString(puzzleNumber) + ".");
		
		//get secretKey
		key = CryptoLib.byteArrayToString(Arrays.copyOfRange(decryptedPuzzle, 18, decryptedPuzzle.length));
		System.out.println("--PuzzleCrack: The secret key is " + key + ". Keep this key secret. Do not show it to anyone!");
		
		return true;
	}
	
	/**
	 * Chooses a random number between two bounds.
	 * 
	 * @param int min bound
	 * @param int max bound
	 */
	private int randomPuzzleNumber(int min, int max) {
		
		Random random = new Random();
		
		// generate random number
		int puzzleNumber = random.nextInt((max - min) + 1) + min;
		
		return puzzleNumber;
	} // end randomPuzzleNumber
	
}