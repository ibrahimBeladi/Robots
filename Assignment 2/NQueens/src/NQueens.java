import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class NQueens {

	final static int TRIALS = 10;
	final static int N = 1000;
	static double timeToFlush = N/43.0; // this value should be tuned manually (time to re-initiate), should increase as N increases
	static ArrayList<Queen> Q = new ArrayList<>();
	static boolean[][] board = new boolean[N][N];

	public static void main(String[] args) throws InterruptedException {

		System.out.println("The board is " + N + "*" + N);
		int tries = 0;
		double sum = 0.0;

		while (tries < TRIALS) {

			long y = System.currentTimeMillis();

			// initiate the game
			initiate();

			long x = System.currentTimeMillis();

			while (thereAreConflicts()) {

				// flush after a certain amount of time
				if ((System.currentTimeMillis() - x) / 1000.0 > timeToFlush) {
					initiate();
					x = System.currentTimeMillis();
					continue;
				}

				// pick any conflicting queen
				ArrayList<Queen> list = new ArrayList<>(); // contains list of conflicting queens
				for (Queen q : Q)
					if (q.conflicts != 0)
						list.add(q);

				int randomNum = ThreadLocalRandom.current().nextInt(0, list.size()); // pick a random conflicting queen
				Queen q = list.get(randomNum);

				// move it
				board[q.x][q.y] = false; // free current position
				int attacks[] = new int[N]; // array of attacks in each cell in the queen's row
				for (int k = 0; k < N; k++) {
					attacks[k] = getAttacks(q.x, k);
				}
				
				// pick one of the least conflicting cells
				int minIndex = findMinIdx(attacks); 
				int minValue = attacks[minIndex];
				ArrayList<Integer> equalMin = new ArrayList<>();
				for(int k = 0; k < attacks.length; k++)
					if(attacks[k] == minValue)
						equalMin.add(k);

				int randomIdx = ThreadLocalRandom.current().nextInt(0, equalMin.size()); // pick a random conflicting queen
				int finalIdx = equalMin.get(randomIdx);
				
				// update the board
				board[q.x][finalIdx] = true;
				q.y = minIndex;

				// update others, the location may attack others
				for (Queen queen : Q)
					queen.conflicts = getAttacks(queen.x, queen.y);

			}
			double time = ((System.currentTimeMillis() - y) / 1000.0);
			System.out.print(time + " secs!\n");
			sum = sum + time;
			tries++;
			//printBoard();
		}

		System.out.println("\nAverage of "+TRIALS+" runs: " + sum / tries);
		//printBoard();

	}

	private static boolean thereAreConflicts() {
		// return false if there are no conflicts
		for (Queen q : Q)
			if (q.conflicts != 0)
				return true;

		return false;
	}

	private static void initiate() {

		//		for(Queen q : Q)
		//			board[q.x][q.y] = false;

		board = new boolean[N][N];

		// clear the list, if it is not cleared (used in flush)
		Q = new ArrayList<>();

		//int[] ints = new Random().ints(0, N).distinct().limit(N).toArray(); // distinct random numbers between 0 and N-1. The rows

		// A queen is put on a random row
		int k = N - 1;
		while (k >= 0) {
			putQueen(k);
			k--;
		}
	}

	private static int getAttacks(int i, int j) {
		// returns how many attacks are on a given cell (i, j)

		int attacks = 0;

//		 attacks within row
		for (int k = 0; k < N; k++)
			if (board[i][k] && k != j)
				attacks++;

		// attacks within column
		for (int k = 0; k < N; k++)
			if (board[k][j] && k != i)
				attacks++;

		attacks = attacks + getAttacksDia(i, j);

		return attacks;
	}

	private static int getAttacksDia(int i, int j) {
		return attacksRightUpperDia(i - 1, j + 1) + attacksLeftUpperDia(i - 1, j - 1)
		+ attacksRightLowerDia(i + 1, j + 1) + attacksLeftLowerDia(i + 1, j - 1);

	}

	private static int attacksLeftLowerDia(int i, int j) {
		int s = 0;
		for (; j >= 0 && i != N; i++, j--)
			if (board[i][j])
				s++;
		return s;
	}

	private static int attacksRightLowerDia(int i, int j) {
		int s = 0;
		for (; i != N && j != N; i++, j++)
			if (board[i][j])
				s++;
		return s;
	}

	private static int attacksLeftUpperDia(int i, int j) {
		int s = 0;
		for (; i >= 0 && j >= 0; i--, j--)
			if (board[i][j])
				s++;
		return s;
	}

	private static int attacksRightUpperDia(int i, int j) {
		int s = 0;
		for (; i >= 0 && j != N; i--, j++)
			if (board[i][j])
				s++;
		return s;
	}

	@SuppressWarnings("unused")
	private static void printBoard() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (board[i][j])
					System.out.printf(board[i][j] + "[" + getAttacks(i, j) + "]\t");
				else
					System.out.printf("[" + getAttacks(i, j) + "]\t");
			}

			System.out.println();
		}
		System.out.println("----------------------");
	}

	private static void putQueen(int i) {
		// places a queen on the least conflicting column, in the row i

		int randomNum = ThreadLocalRandom.current().nextInt(0, N); // pick a random number as column index

		Queen q = new Queen(i, randomNum, getAttacks(i, randomNum)); // object carries useful info about a queen
		Q.add(q); // put it on a list

		board[i][randomNum] = true; // update the board

		// update others, the location may attack others
		for (Queen queen : Q)
			queen.conflicts = getAttacks(queen.x, queen.y);

	}

	public static int findMinIdx(int[] numbers) {
		if (numbers == null || numbers.length == 0)
			return -1; // Saves time for empty array
		// As pointed out by ZouZou, you can save an iteration by assuming the first index is the smallest
		int minVal = numbers[0]; // Keeps a running count of the smallest value so far
		int minIdx = 0; // Will store the index of minVal
		for (int idx = 1; idx < numbers.length; idx++) {
			if (numbers[idx] < minVal) {
				minVal = numbers[idx];
				minIdx = idx;
			}
		}
		return minIdx;
	}

}
