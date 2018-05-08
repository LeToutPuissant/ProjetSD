import java.io.Serializable;

public class Operation implements Serializable{
	
	private static int incld;
	private int idO;
	private String op;
	
	public Operation(int idO, String op) {
		this.idO = idO;
		this.op = op;
	}
	
	public boolean equals(Operation o) {
		boolean equal = false;
		if(this.idO == o.getIdO() && this.toString().compareTo(o.toString()) == 0) {
			equal = true;
		}
		
		return equal;
	}

	public static int getIncld() {
		return incld;
	}

	public int getIdO() {
		return idO;
	}

	public String getOp() {
		return op;
	}
	
	public String toString(){
		return op;
	}
}
