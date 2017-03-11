
public class Queen {
	public int x;
	public int y;
	public int conflicts;
	
	public Queen(int x, int y, int conflicts) {
		this.x = x;
		this.y = y;
		this.conflicts = conflicts;
	}

	@Override
	public String toString() {
		return "Queen [x=" + x + ", y=" + y + ", conflicts=" + conflicts + "]";
	}
	
	
}
