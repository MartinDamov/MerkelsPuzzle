import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.ByteBuffer;
import java.util.Random;

//TODO: after getting byte array from the puzzle, crack it
//TODO: after a puzzle is cracked, get the secret key and send the puzzle number


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
	 */
	public void crackPuzzle() throws IOException {
		
		// get a puzzle
		String puzzleStr = choosePuzzle();
		
		// check if we have valid puzzle
		if (puzzleStr == null) {
			throw new IOException();
		}
		
		// read through the puzzle to get needed data
		byte[] puzzle = CryptoLib.stringToByteArray(puzzleStr);
		
		
	}
	
	/**
	 * Open and read a file
	 * @return String randomPuzzle
	 */
	public String choosePuzzle() throws IOException {
		String randomPuzzle = null;
		
		// try to read from the file and get a random puzzle
		try {
			FileReader fileReader = new FileReader(_fileName);
			LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
			
			// create a BufferedReader object to read lines
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			// We know there are 1024 puzzles. 
			// This could be changed to accept arguments if needed.
			int puzzleNumber = randomPuzzleNumber(1, 1024);
			
			// get the puzzle by reading the file line by line
			while (bufferedReader.readLine() != null) {
				if (lineNumberReader.getLineNumber() == puzzleNumber) {
					// we found the puzzle
					randomPuzzle = bufferedReader.readLine();
					
					// close to release resources
					fileReader.close();
					bufferedReader.close();
					lineNumberReader.close();
					
					break;
				}
			}
			
			// close if no match was found
			fileReader.close();
			bufferedReader.close();
			lineNumberReader.close();
			
			return randomPuzzle;
			
		} catch (Exception e) {
			
			// catch if any error occurs
			e.printStackTrace();
		}
		return randomPuzzle;
	} // end choosePuzzle
	
	/**
	 * Choose a random number between two bounds
	 * @param int min bound
	 * @param int max bound
	 */
	public int randomPuzzleNumber(int min, int max) {
		
		Random random = new Random();
		
		// generate random number
		int puzzleNumber = random.nextInt((max - min) + 1) + min;
		
		return puzzleNumber;
	} // end randomPuzzleNumber
	
}