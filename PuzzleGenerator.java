import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
		
		
	//generates a DES key and returns its byte array representation
	
	/**
	 * Generates a DES Key to be used in the cryptogram.
	 * 
	 * The javax.crypto KeyGenerator is used to generate the key. 
	 * The key is encoded to a byte array.
	 * Exceptions occurring are displayed and then abort the program.
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
	 * The encryption key is generated using a random byte pattern ending in 48 zeros.
	 * The puzzle is then encrypted using this key. For these operations the javax.crypto library is used.
	 * Occuring exceptions are displayed and the program aborted in this case.
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
			
			//Get a new Base64 (ASCII) encoder and use it to convert ciphertext back to a string
			Base64.Encoder encoder = Base64.getEncoder();
			encryptedPuzzle = encoder.encodeToString(encryptedByte);
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
	 * 
	 * Exceptions are displayed and program is aborted on occurrence.
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