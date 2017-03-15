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
		N = 1000;
		
		if (N > 750)
			timeToFlush =  N;
		else if (N >= 500)
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
				Q.remove(q); // remove it so that it is not affected by itself, also reduce # of comparisons
				updateAffectedBy(q, -1); // update ONLY affected queens after removing
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
				q.conflicts = minValue;
				updateAffectedBy(q, 1); // update ONLY affected queens after inserting
				Q.add(q);
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

	private static void updateAffectedBy(Queen q2, int i) {
		for(Queen q : Q)
			if((q.y == q2.y) || ((q.x - q.y) == (q2.x - q2.y)) || ((q.x + q.y) == (q2.x + q2.y)))
				q.conflicts += i;
		
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
		
		// conflicts must be updated from here
		for (Queen queen : Q)
			queen.conflicts = getAttacks(queen.x, queen.y);
	}

	private static int getAttacks(int i, int j) {
		// returns how many attacks are on a given cell (i, j)

		int attacks = 0;

		for(Queen q : Q)
			if((q.y == j) || ((q.x - q.y) == (i - j)) || ((q.x + q.y) == (i + j)))
				attacks++;
	
		return attacks;
	}

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
		Queen q = new Queen(i, randomNum, 1); // object carries useful info about a queen, no need to update from here
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
