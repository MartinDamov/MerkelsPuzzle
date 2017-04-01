import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;

/**
 * Simulation of a library for solving MerkelsPuzzle.
 * <p>
 * This simulation uses all classes provided to generate puzzles, crack puzzles 
 * and en/decrypt messages using DES. The CryptoLib library is used for helper functions.
 * @autor Martin Damov
 * @autor Anja Strobel
 */
public class MerkelsPuzzle {
		
	/**
	 * Simulates a Merkles Puzzle exchange between two parties, ALICE and BOB.
	 * <p>
	 * This simulation demonstrates the use of the classes in order to solve Merkles Puzzle.
	 * The exchange is printed to the screen with each party explaining the steps taken by them.
	 * @see PuzzleGenerator
	 * @see PuzzleCrack
	 * 
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 * @throws InvalidKeyException 
	 */
	public static void main(String [ ] args) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException 
	{
		//Person 1 (ALICE) generates puzzles
		System.out.println("ALICE: I am generating puzzles using the PuzzleGenerator.");
		PuzzleGenerator puzzles = new PuzzleGenerator("puzzles.txt");
		puzzles.generatePuzzles();
		System.out.println("ALICE: I am sending my puzzles to BOB.");
		
		//Person 2 (BOB) cracks a random puzzle and retrieves information
		System.out.println("BOB: I have recived a file containig puzzles from Alice.");
		System.out.println("BOB: I am using PuzzleCrack to crack a random puzzle.");
		PuzzleCrack cracker = new PuzzleCrack("puzzles.txt");
		int puzzleNumber = cracker.crackPuzzle();
		
		//Puzzle number is exchanged
		System.out.println("BOB: I am sending the puzzle number to Alice.");
		System.out.println("ALICE: I have recieved a message from Bob containing " + Integer.toString(puzzleNumber) + ".");
		
		//ALICE looks up secret key
		System.out.println("ALICE: I am using the lookup function of the PuzzleGenerator to get the secret key.");
		String secretKey = puzzles.keyLookup(puzzleNumber);
		
		//Message is encrypted using DES and the agreed key
		System.out.println("ALICE: I am using the DESEncryptionModule to send a secret message to Bob.");
		String message = "HelloBob";
		System.out.println("ALICE: I will encrypt the following message and send it to Bob:");
		System.out.println(message);
		SecretKey key = CryptoLib.createKey(CryptoLib.stringToByteArray(secretKey));
		DESEncryptionModule des = new DESEncryptionModule();
		message = des.encryptMessage(key, CryptoLib.stringToByteArray(message));
		
		//Message is decrypted using DES and the agreed key
		System.out.println("BOB: I have recieved this encrypted message from Alice.");
		System.out.println(message);
		System.out.println("BOB: I am decrypting the message using the DESEncryptionModule and the key from the puzzle");
		key = cracker.getKey();
		message = des.decryptMessage(key, CryptoLib.stringToByteArray(message));
		System.out.println("BOB: The message reads:");
		System.out.println(message);
	}
	
}