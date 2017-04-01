import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;



/**
 * Class to read a file and crack a random puzzle.
 */
public class PuzzleCrack {
	
	private String _fileName;
	
	/**
	 * Default constructor. Sets the fileName
	 * @param fileName
	 */
	public PuzzleCrack(String fileName) {
		this._fileName = fileName;
	}
	
	/**
	 * Crack a chosen puzzle
	 * @return puzzle number
	 */
	public int crackPuzzle() {
		
		// get a puzzle
		String puzzleString= choosePuzzle();
		
		// check if we have valid puzzle
		if (puzzleString == null) {
			System.out.println("An error occurred when cracking puzzle. Execution was aborted.\r\n");
			//Abort program
			System.exit(1);
		}
		
		// Try to crack the puzzle
		try {

			// Actual cracking
			String cracked = crack(puzzleString);
			
			// Get the puzzle number is byte array
			byte[] puzzleNumberByte = Arrays.copyOfRange(CryptoLib.stringToByteArray(cracked), 16, 18);
			
			// Convert the puzzle number to an int
			int puzzleNumber = CryptoLib.byteArrayToSmallInt(puzzleNumberByte);
			
			// Allow Bob to say the puzzle number
			System.out.println("BOB: Puzzle numbers is " + puzzleNumber);
			
			// Get the key
			byte[] keyByte = Arrays.copyOfRange(CryptoLib.stringToByteArray(cracked), 18, 26);
			
			// Convert it to string
			String key = CryptoLib.byteArrayToString(keyByte);
			
			System.out.println(key);
			
			return puzzleNumber;
			
		} catch (Exception e) {
			System.out.println("An error occurred when cracking puzzle. Execution was aborted.\r\n" + e.getMessage());
			//Abort program
			System.exit(1);
		}
		
		// If there was an error, return -1
		return -1;
	}
	
	/**
	 * Crack a puzzle by brute force. This is done by trying all possible cases (2 ^ 16).
	 * A valid key will start with 128 zero bits.
	 * @param puzzle to be decrypted
	 * @return cracked puzzle
	 */
	public String crack(String puzzle) throws Exception {
		DES des = new DES();
		String crackedPuzzle = null;
		ArrayList<String> decryptedPuzzles = new ArrayList<String>();
		
		// Try to decrypt the puzzle for every possible case
		for (int i = 0; i < Math.pow(2, 16); i++) {
			
			// Create bytes of zeros for the key's end
			byte[] zeros = new byte[6];
			
			// Convert current index to byte array
			byte[] indexBits = CryptoLib.smallIntToByteArray(i);
			
			// Generate the key by concatenating the index bits and zero bits
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(indexBits);
			stream.write(zeros);
			byte[] key = stream.toByteArray();
			
			// Try to decrypt the puzzle
			try {
				
				// Add the puzzle to the list if the decryption is successful
				byte[] decryptedPuzzle = des.decrypt(puzzle, CryptoLib.createKey(key));
				
				decryptedPuzzles.add(CryptoLib.byteArrayToString(decryptedPuzzle));
			} catch (Exception e) {
				// Not a valid key
			}
		}
		
		// Get a puzzle which starts with 128 zero bits
		for (int i = 0; i < decryptedPuzzles.size(); i++) {
			
			// Convert puzzle to byte array
			byte[] bytePuzzle = CryptoLib.stringToByteArray(decryptedPuzzles.get(i));
			
			// Generate zeros
			byte[] zeros = new byte[16];
			
			// Get the zeros part of the puzzle
			byte[] zerosPart = Arrays.copyOfRange(bytePuzzle, 0, 16);
			
			if (Arrays.equals(zerosPart, zeros)) {
				
				// The cracked puzzle is found
				crackedPuzzle = decryptedPuzzles.get(i);
			}
		}
		
		return crackedPuzzle;
	}
	
	/**
	 * Open and read a file
	 * @return String randomPuzzle
	 */
	private String choosePuzzle() {
		String randomPuzzle = null;
		
		// try to read from the file and get a random puzzle
		try {
			
			// We know there are 1024 puzzles. 
			// This could be changed to accept arguments if needed.
			int puzzleNumber = randomPuzzleNumber(0, 1023);
			
			try (Stream<String> lines = Files.lines(Paths.get(_fileName))) {
			    //random puzzle is retrieved
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
	} // end choosePuzzle
	
	/**
	 * Choose a random number between two bounds
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