import java.util.Arrays;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

/*
 * TODO: Could remove writeToFile() and put its code in the PuzzleGeneration()
 */

/**
 * Currently awful implementation of MerklesPuzzle
 * @autor Martin Damov
 * @autor Anja Strobel
 */
public class MerklesPuzzle {

	// fileName where to store the generated puzzles
	// currently the fileName is hardcoded as we don't expect for the user to enter the name
	private File _fileName = new File("puzzles.txt");

	// FileWriter object
	private FileWriter _writer = new FileWriter(fileName);

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
		byte[] start = new byte[16];
		Arrays.fill(start, (byte)0);

		// create the file where the puzzles will be stored
		fileName.createFile();
		
		// generate puzzles
		for (int i = 1; i <= 1024; i++) {
			// create a puzzle number from an int (using CryptoLib from Phil)
			byte[] puzzleNumber = smallIntToByteArray(i);

			// generate random 

		}

		// close the FileWriter after the operation is complete
		_writer.flush();
		_writer.close();

		return true;		
	} // end PuzzleGeneration

	/**
	 * Method which writes a puzzle to a file
	 * @param String to write 
	 * @return
	 */
	public void writeToFile(String line) throws IOException {
		// using the FileWriter, write the line to the fileName
		_writer.write(line + "\n");
	}

}