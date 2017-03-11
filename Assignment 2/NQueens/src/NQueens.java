import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class NQueens {

	static int N = 60;
	static ArrayList<Queen> Q = new ArrayList<>();
	static char[][] board = new char[N][N];
	
	public static void main(String[] args) throws InterruptedException {
		
		int tries = 0;
		
		while(tries < 10) {
			
			long y = System.currentTimeMillis();
			
			// initiate the game
			initiate(1);		
			
			long x = System.currentTimeMillis();
			
			while(thereAreConflicts()) {
				
				// flush after a certain amount of time
				if((System.currentTimeMillis() - x)/1000.0 > N/5) {					
					initiate(1);				
					x = System.currentTimeMillis();
					continue;
				}
				
				// pick any conflicting queen
				ArrayList<Queen> list = new ArrayList<>(); // contains list of conflicting queens
				for(Queen q : Q)
					if(q.conflicts != 0)
						list.add(q);
				
				int randomNum = ThreadLocalRandom.current().nextInt(0, list.size()); // pick a random conflicting queen
				int coin = ThreadLocalRandom.current().nextInt(0, 2); // increase randomness, decides to pick the current location again
				Queen q = list.get(randomNum);
	
				// move it
				board[q.x][q.y] = '\u0000'; // free current position
				int attacks[] = new int[N]; // array of attacks in each cell in the queen's row
				for(int k = 0; k < N; k++) {
					if (k == q.y && coin == 1)
						attacks[k] = Integer.MAX_VALUE; // do not pick the current location again
					else
						attacks[k] = getAttacks(q.x, k);
					}
				
				int minIndex = findMinIdx(attacks); // least conflicting cell to put a queen
				
				// update the board
				board[q.x][minIndex] = 'Q';
				q.y = minIndex;

				// update others, the location may attack others
				for(Queen queen : Q)
					queen.conflicts = getAttacks(queen.x, queen.y);
				
			}
			
			System.out.println("Finished in: " + (System.currentTimeMillis() - y)/1000.0 + " secs!");
			tries++;
		}
	}
	
	private static boolean thereAreConflicts() {
		// return false if there are no conflicts
		for(Queen q : Q)
			if(q.conflicts != 0)
				return true;
		
		return false;
	}

	private static void initiate(int j) {
		Q.clear(); // clear the list, if it is not cleared (used in flush)
		
		// j is used if we need to flush. First initiating is already '\u0000' so no need to enter the loop
		if (j == 1)
			for(int i = 0; i < N; i++)
				Arrays.fill(board[i], '\u0000');
		
		int[] ints = new Random().ints(0, N).distinct().limit(N).toArray(); // distinct random numbers between 0 and N-1. The rows
		
		// A queen is put on a random row
		int k = N-1;
		while(k > 0) {
			putQueen(ints[k]);
			k--;
		}
	}
	
	private static int getAttacks(int i, int j) {	
		// returns how many attacks are on a given cell (i, j)
		
		int attacks = 0;
		
		// attacks within row
		for(int k = 0; k < N; k++)
			if(board[i][k] == ('Q') && k != j)
				attacks++;
		
		// attacks within column
		for(int k = 0; k < N; k++)
			if(board[k][j] == ('Q') && k != i)
				attacks++;
		
		attacks = attacks + getAttacksDia(i, j);
		
		return attacks;
	}

	private static int getAttacksDia(int i, int j) {
		return attacksRightUpperDia(i-1, j+1)
				+ attacksLeftUpperDia(i-1, j-1)
				+ attacksRightLowerDia(i+1, j+1)
				+ attacksLeftLowerDia(i+1, j-1);
		
	}

	private static int attacksLeftLowerDia(int i, int j) {
		if (i < 0 || j < 0 || i == N || j == N)
			return 0;

		if(board[i][j] == ('Q'))
			return 1 + attacksLeftLowerDia(i+1, j-1);
		
		else
			return attacksLeftLowerDia(i+1, j-1);
	}

	private static int attacksRightLowerDia(int i, int j) {
		if (i < 0 || j < 0 || i == N || j == N)
			return 0;

		if(board[i][j] == ('Q'))
			return 1 + attacksRightLowerDia(i+1, j+1);
		
		else
			return attacksRightLowerDia(i+1, j+1);
	}

	private static int attacksLeftUpperDia(int i, int j) {
		if (i < 0 || j < 0 || i == N || j == N)
			return 0;

		if(board[i][j] == 'Q')
			return 1 + attacksLeftUpperDia(i-1, j-1);
		
		else
			return attacksLeftUpperDia(i-1, j-1);
	}

	private static int attacksRightUpperDia(int i, int j) {
		if (i < 0 || j < 0 || i == N || j == N)
			return 0;

		if(board[i][j] == 'Q')
			return 1 + attacksRightUpperDia(i-1, j+1);
		
		else
			return attacksRightUpperDia(i-1, j+1);
		
	}


	private static void printBoard() {
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				if(board[i][j] == 'Q')
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
		
		int[] attacks = new int[N];
		for(int k = 0; k < N; k++)
			attacks[k] = getAttacks(i, k);
		
		int minIndex = findMinIdx(attacks); // index of least conflicting column
		Queen q = new Queen(i, minIndex, getAttacks(i, minIndex)); // object carries useful info about a queen
		Q.add(q); // put it on a list
		
		board[i][minIndex] = 'Q'; // update the board
		
		// update others, the location may attack others
		for(Queen queen : Q)
			queen.conflicts = getAttacks(queen.x, queen.y);
		
	}
	
	public static int findMinIdx(int[] numbers) {
	    if (numbers == null || numbers.length == 0) return -1; // Saves time for empty array
	    // As pointed out by ZouZou, you can save an iteration by assuming the first index is the smallest
	    int minVal = numbers[0]; // Keeps a running count of the smallest value so far
	    int minIdx = 0; // Will store the index of minVal
	    for(int idx=1; idx<numbers.length; idx++) {
	        if(numbers[idx] < minVal) {
	            minVal = numbers[idx];
	            minIdx = idx;
	        }
	    }
	    return minIdx;
	}

}
