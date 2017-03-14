import java.security.NoSuchAlgorithmException;
import java.io.File;
import java.io.IOException;

/**
 * Currently awful implementation of MerkelsPuzzle
 * @autor Martin Damov
 * @autor Anja Strobel
 */
public class MerkelsPuzzle {
		
		
	public static void main(String [ ] args) throws NoSuchAlgorithmException, IOException
	{
		PuzzleGenerator puzzles = new PuzzleGenerator("puzzles.txt");
		puzzles.generatePuzzles();
	}
	
}