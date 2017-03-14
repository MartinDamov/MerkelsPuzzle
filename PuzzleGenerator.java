import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


public class PuzzleGenerator {
	
	private File _fileName;
	
	public PuzzleGenerator (String puzzleFileName){
		
		// fileName where to store the generated puzzles
		_fileName = new File(puzzleFileName);
	}
			
		/**
		 * Method generating 1024 random puzzles and writing them in a file.
		 * Each puzzle is a cryptogram whose plaintext starts with 128 zero bits,
		 * followed by unique 16 bit puzzle number and then 64 bit key.
		 * These criptograms are encrypted using DES with a key whose final 48 bits are zeros.
		 *
		 * @return boolean value if operation successful 
		 * @throws NoSuchAlgorithmException 
		 * @throws IOException 
		 */
		public void generatePuzzles() throws NoSuchAlgorithmException, IOException {
			
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
			
			//write puzzles to file
			writeToFile(puzzles);
			
			return;
		} // end PuzzleGeneration
		
		
		//generates a DES key and returns its Base64 string representation
		private String generateDESKeyForPuzzle() throws NoSuchAlgorithmException{
			//Use java's key generator to produce a random key.
			KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
			keyGenerator.init(56);
			SecretKey secretKey = keyGenerator.generateKey();
			
			String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

			return encodedKey;
		}

		/**
		 * Method which writes a puzzle to a file
		 * @param String to write 
		 * @return
		 */
		private void writeToFile(List<String> lines) throws IOException {
			
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
		
		
	}