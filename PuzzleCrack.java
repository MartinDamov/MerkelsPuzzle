import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.stream.Stream;

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
	public void crackPuzzle() {
		
		// get a puzzle
		String puzzleStr = choosePuzzle();
		
		// check if we have valid puzzle
		if (puzzleStr == null) {

		}
		
		// read through the puzzle to get needed data
		byte[] puzzle = CryptoLib.stringToByteArray(puzzleStr);
		
		System.out.println("--PuzzleCrack: Not yet able to crack puzzles. Please implement.");
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