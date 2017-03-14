import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class NQueens {

	static int N;
	static ArrayList<Queen> Q;
	static boolean[][] board;
	static int TRIALS;
	static double timeToFlush; 
	public static void main(String[] args) throws InterruptedException {
		TRIALS = 10;
		N = 10;
		
		if (N > 750)
			timeToFlush =  N;
		else if (N > 500)
			timeToFlush =  2.5;
		else if (N > 100)
			timeToFlush =  0.5;
		else
			timeToFlush = 0.01;
		
		timeToFlush *= 1000; //sec to msec 
		Q = new ArrayList<>();
		board = new boolean[N][N];
		solve();		
		
	}

	private static double solve() {
		int timeouts = 0;
		int tries = 0;
		double sum = 0.0;

		while (tries < TRIALS) {

			long y = System.currentTimeMillis();

			// initiate the game
			initiate();

			long x = System.currentTimeMillis();
			while (thereAreConflicts()) {

				// flush if it took to much time
				if (System.currentTimeMillis()  > timeToFlush + x) {
					initiate();
					x = System.currentTimeMillis();
					timeouts++;
					System.out.println("Timeout !");
				}

				// pick any conflicting queen
				ArrayList<Queen> list = new ArrayList<>(); // contains list of conflicting queens
				for (Queen q : Q)
					if (q.conflicts != 0)
						list.add(q);


				Queen q = getMaxRandomQueen(list);// pick a random conflicting queen

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

				
				// insert a random conflicting queen
				int r = getRandomMin(equalMin);
				board[q.x][r] = true;
				q.y = r;

				// update others, the location may attack others
				for (Queen queen : Q)
					queen.conflicts = getAttacks(queen.x, queen.y);
				
				printBoard();
			}
			double time = ((System.currentTimeMillis() - y) / 1000.0);
			System.out.print(time + " secs!\n");
			sum = sum + time;
			tries++;
		}
		
		double average = sum / tries;
		System.out.println("The board = " + N + "*" + N);
		System.out.println("Average of "+TRIALS+" runs: " + average);
		System.out.println("Timeouts = "+timeouts);
		return average;
	}

	private static Integer getRandomMin(ArrayList<Integer> equalMin) {
		return equalMin.get(ThreadLocalRandom.current().nextInt(0, equalMin.size()));
	}

	private static Queen getMaxRandomQueen(ArrayList<Queen> list) {
		ArrayList<Queen> randomQueens = new ArrayList<>();
		int numberOfRandomQueens = 100; 
		for(int i = 0; i < numberOfRandomQueens; i++)
			randomQueens.add(list.get(ThreadLocalRandom.current().nextInt(0, list.size())));
		
		// pick the max in terms of # of conflicts
		Queen maxRandom = Collections.max(randomQueens, new Comparator<Queen>() {
			@Override
			public int compare(Queen o1, Queen o2) {
				return o1.conflicts - o2.conflicts;
			}
		});
		
		return maxRandom;
	}

	private static boolean thereAreConflicts() {
		// return false if there are no conflicts
		for (Queen q : Q)
			if (q.conflicts != 0)
				return true;
		return false;
	}

	private static void initiate() {
		board = new boolean[N][N];
		Q = new ArrayList<>();

		// A queen is put on a random row
		int k = N - 1;
		while (k >= 0)
			putQueen(k--);
	}

	private static int getAttacks(int i, int j) {
		// returns how many attacks are on a given cell (i, j)

		int attacks = 0;

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
					System.out.printf(" Q[" + getAttacks(i, j) + "]\t");
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
	}

	public static int findMinIdx(int[] numbers) {
		if (numbers == null || numbers.length == 0)
			return -1; 
		
		int minVal = numbers[0]; 
		int minIdx = 0; 
		for (int idx = 1; idx < numbers.length; idx++) {
			if (numbers[idx] < minVal) {
				minVal = numbers[idx];
				minIdx = idx;
			}
		}
		return minIdx;
	}

}
