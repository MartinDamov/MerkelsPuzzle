import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class PuzzleGenerator {
	
	private File _fileName;
	
	// variable for holding the puzzle numbers and corresponding keys
	private List<byte[]> _keyLookup = new ArrayList<byte[]>();
	
	/**
	 * Constructor for Puzzle Generator
	 * 
	 * @param puzzleFileName file to store puzzles in
	 */
	public PuzzleGenerator (String puzzleFileName){
		
		// fileName where to store the generated puzzles
		_fileName = new File(puzzleFileName);
	}
			
	/**
	 * Method generating 1024 random puzzles and writing them in a file.
	 * Each puzzle is a cryptogram whose plaintext starts with 128 zero bits,
	 * followed by unique 16 bit puzzle number and then 64 bit key.
	 * These cryptograms are encrypted using DES with a key whose final 48 bits are zeros.
	 * The resulting puzzles are shuffled and written to the specified file.
	 *
	 * Occurring exceptions are handled and will abort the program.
	 */
	public void generatePuzzles() {
			
		List<String> encryptedPuzzles = new ArrayList<String>();
		
		String puzzle;
		byte[] puzzleTmp = new byte[26];
		
		for (int i=1; i<=1024; i++){
			
			//fill first 16 bytes with 0
			Arrays.fill(puzzleTmp, 0, 15, (byte)0);
			
			//add 2 byte puzzle number
			System.arraycopy(CryptoLib.smallIntToByteArray(i),0, puzzleTmp, 16, 2);
			
			//generate 8 byte DES key
			System.arraycopy(generateDESKeyForPuzzle(), 0, puzzleTmp, 18, 8);
			
			//add the the puzzle key and the DES key so they can be later used for key lookup
			_keyLookup.add((Arrays.copyOfRange(puzzleTmp, 16, puzzleTmp.length)));
			
			//encrypt puzzle
			puzzle = encryptPuzzle(puzzleTmp);
			
			//add puzzle to list
			encryptedPuzzles.add(puzzle);
		}
		
		//shuffle puzzles
		Collections.shuffle(encryptedPuzzles);
		
		//write puzzles to file
		writeToFile(encryptedPuzzles);
		
		System.out.println("--PuzzleGenerator: Puzzles generated sucessfully, encrypted and saved to specified file.\r\n");
		return;
	} // end PuzzleGeneration
		
	
	/**
	 * Looks up a key to be used for a given puzzle number.
	 * <p>
	 * The key is retrieved from a List and displayed to the user.
	 * @return key to be used
	 */
	public String keyLookup(int puzzleNumber) { 
		
		// convert puzzle number to byte array
		byte[] puzzleNum = CryptoLib.smallIntToByteArray(puzzleNumber);
					
		for (int i = 0; i < _keyLookup.size(); i++) {
						
			// get the saved puzzle from the private List
			byte[] savedPuzzle = _keyLookup.get(i);  	
			
			// get the puzzle number from the saved puzzle
			byte[] savedPuzzleNum = {savedPuzzle[0], savedPuzzle[1]};
			
			// see if we have a match;
			// if there is one, return the puzzle key
			if (Arrays.equals(savedPuzzleNum, puzzleNum)) {
				
				String key = CryptoLib.byteArrayToString(Arrays.copyOfRange(savedPuzzle, 2, savedPuzzle.length));
				System.out.println("--PuzzleGenerator: Key lookup sucessful. The secret key is "+ key + ".\r\n");
				return key;
			}
		}
		
		return "Error! Key not found!";
}
	
	/**
	 * Generates a DES Key to be used in the cryptogram.
	 * <p>
	 * The javax.crypto KeyGenerator is used to generate the key. 
	 * The key is encoded to a byte array.
	 * Occurring exceptions are and will abort the program.
	 * @return DES key encoded as byte array.
	 */
	private byte[] generateDESKeyForPuzzle() {
		
		KeyGenerator keyGenerator = null;
		try{
			
			//Use java's key generator to produce a random key.
			keyGenerator = KeyGenerator.getInstance("DES");
			keyGenerator.init(56);
		}
		catch(NoSuchAlgorithmException e){
			//Display exception
			System.out.println("An error occurred while generating keys for puzzles. Execution was aborted.\r\n" + e.getMessage());
			e.printStackTrace();
			//Abort program
			System.exit(1);
		}
		
		//Generate secret key
		SecretKey secretKey = keyGenerator.generateKey();
		
		//Encode key to byte array
		byte[] encodedKey = secretKey.getEncoded();
		
		return encodedKey;
	}
		
	/**
	 * Encrypts a given puzzle using a DES key ending in 48 zeros.
	 * <p>
	 * The encryption key is generated using a random byte pattern ending in 48 zeros.
	 * The puzzle is then encrypted using this key. For these operations the javax.crypto library is used.
	 * Occuring exceptions are displayed and the program will abort the program.
	 * 
	 * @param puzzle cryptogram to encrypt
	 * @return encrypted puzzle as byte array
	 */
	private String encryptPuzzle(byte[] puzzle){

		String encryptedPuzzle=null;
		byte[] seed = new byte[8];
	
		try{
			//DES cipher instance
			Cipher cipher = Cipher.getInstance("DES");
		
			//generate Random number
			Random random = new Random();
			int rnd = random.nextInt(65534);
		
			//2 bytes random number for seed
			System.arraycopy(CryptoLib.smallIntToByteArray(rnd),0, seed, 0, 2);
		
			//fill seed with 6 bytes of 0
			Arrays.fill(seed, 2, 7, (byte)0);
		
			//generate DES key with given pattern
			SecretKey key = CryptoLib.createKey(seed);
			
			//Initialise the cipher to be in encrypt mode, using the given key.
			cipher.init(Cipher.ENCRYPT_MODE, key);
			
			//Perform the encryption
			byte[] encryptedByte = cipher.doFinal(puzzle);
			
			//Use CryptoLib to convert the encypted puzzle to a string
			encryptedPuzzle = CryptoLib.byteArrayToString(encryptedByte);
		}
		catch(InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException e){
			//Display exception
			System.out.println("An error occurred when encrypting puzzles. Execution was aborted.\r\n" + e.getMessage());
			e.printStackTrace();
			//Abort program
			System.exit(1);
		}
	
		return encryptedPuzzle;
	}
		

	/**
	 * Writes given puzzles to file.
	 * The given puzzles are written to a file with each puzzle in one line.
	 * <p>
	 * Occurring exceptions are displayed and will abort the program.
	 * @param lines List of puzzles to write to file.
	 */
	private void writeToFile(List<String> lines) {
		
		try{
			// FileWriter object
			FileWriter writer = new FileWriter(_fileName);
			// create the file where the puzzles will be stored
			_fileName.createNewFile();
		
			//write lines
			for(String line : lines){
	
				writer.write(line + "\r\n");
			}
		
			// close the FileWriter after the operation is complete
			writer.flush();
			writer.close();
		}
		catch(IOException e){
			
			//Display exception
			System.out.println("Puzzles could not be written to file! Execution was aborted.\r\n" + e.getMessage());
			e.printStackTrace();
			//Abort program
			System.exit(1);
		}
	}
	
	

}