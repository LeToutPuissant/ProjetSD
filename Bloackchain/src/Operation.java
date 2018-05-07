
public class Operation {
	
	static int incld;
	int idO;
	String op;
	
	public Operation(String op) {
		this.op = op;
	}
	
	//public String toString();
	
	public boolean equals(Operation o) {
		boolean equal = false;
		if(this.idO == o.idO && this.toString() == o.toString()) {
			equal = true;
		}
		
		return equal;
	}
	
}
