

/**
 * Currently awful implementation of MerkelsPuzzle
 * @autor Martin Damov
 * @autor Anja Strobel
 */
public class MerkelsPuzzle {
		
		
	public static void main(String [ ] args) 
	{
		System.out.println("ALICE: I am generating puzzles using the PuzzleGenerator.\r\n");
		PuzzleGenerator puzzles = new PuzzleGenerator("puzzles.txt");
		puzzles.generatePuzzles();
		System.out.println("ALICE: I am sending my puzzles to BOB.\r\n");
		System.out.println("BOB: I recived a file containig puzzles from Alice.\r\n");
		System.out.println("BOB: I am using PuzzleCrack to crack a random puzzle.\r\n");
		PuzzleCrack cracker = new PuzzleCrack("puzzles.txt");
		cracker.crackPuzzle();
		
		
	}
	
}