import CryptoLib;
import java.util.Arrays;

/**
 * Currently awful implementation of MerkelsPuzzle
 * @autor Martin Damov
 * @autor 
 */
public class MerklesPuzzle {

	/**
	 * Method generating 1024 random puzzles and writing them in a file.
	 * Each puzzle is a cryptogram whose plaintext starts with 128 zero bits,
	 * followed by unique 16 bit puzzle number and then 64 bit key.
	 * These criptograms are encrypted using DES with a key whose final 48 bits are zeros.
	 *
	 * @return boolean value if operation successful 
	 */
	public boolean PuzzleGeneration() {
		// populate with zeros
		byte[16] start;
		Arrays.fill(start, 0);
		
		// generate puzzles
		for (int i = 1; i <= 1024; i++) {
			// create a puzzle number from an int (using CryptoLib from Phil)
			byte[] puzzleNumber = smallIntToByteArray(i);

			// generate random 
		}		
	} // end PuzzleGeneration
}