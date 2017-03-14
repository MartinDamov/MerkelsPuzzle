import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Currently awful implementation of MerkelsPuzzle
 * @autor Martin Damov
 * @autor Anja Strobel
 */
public class MerkelsPuzzle {

	public static void main(String [ ] args) throws NoSuchAlgorithmException
	{
		PuzzleGeneration();
	}
	
	
	/**
	 * Method generating 1024 random puzzles and writing them in a file.
	 * Each puzzle is a cryptogram whose plaintext starts with 128 zero bits,
	 * followed by unique 16 bit puzzle number and then 64 bit key.
	 * These criptograms are encrypted using DES with a key whose final 48 bits are zeros.
	 *
	 * @return boolean value if operation successful 
	 * @throws NoSuchAlgorithmException 
	 */
	public static List<String> PuzzleGeneration() throws NoSuchAlgorithmException {
		
		List<String> puzzles = new ArrayList<String>();
		
		String key; //key for each puzzle
		byte[] puzzleTmp = new byte[18]; //puzzle plus number
		
		for (int i=1; i<=1024; i++){
			
			//fill first 16 bytes with 0
			Arrays.fill(puzzleTmp, 0, 15, (byte)0);
			
			//add 2 byte puzzle number
			System.arraycopy(CryptoLib.smallIntToByteArray(i),0, puzzleTmp, 16, 2);
			
			//generate 8 byte DES key
			key = generateDESKeyForPuzzle();
			
			//combine byte array and key, then add to puzzle list
			puzzles.add(CryptoLib.byteArrayToString(puzzleTmp) + key);
			
		}
		
		//shuffle puzzles
		Collections.shuffle(puzzles);
		
		return puzzles;
	} // end PuzzleGeneration
	
	
	//generates a DES key and returns its Base64 string representation
	private static String generateDESKeyForPuzzle() throws NoSuchAlgorithmException{
		//Use java's key generator to produce a random key.
		KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
		keyGenerator.init(56);
		SecretKey secretKey = keyGenerator.generateKey();
		
		String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

		return encodedKey;
	}

	
	
}