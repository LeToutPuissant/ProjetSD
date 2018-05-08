import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Bloc {

	private int idB;
	private static char sep = '\n';
	private Operation[] op;
	private String hash; //La chaine résultat sera la concatenation de hash et des opérations
	private String previousHash;
	
	public Bloc(int id, Operation[] op, String preHash) {
		idB = id;
		this.op = op;
		previousHash = preHash;
		hash = calculerHash(this);
	}

	public int getIdB() {
		return idB;
	}

	public void setIdB(int idB) {
		this.idB = idB;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public Operation[] getOp() {
		return op;
	}

	public void setOp(Operation[] op) {
		this.op = op;
	}
	
	public String str() {
		String ops = "";
		//On concatène les opérations en une seule String
		for(int i = 0; i < op.length; i++) {
			ops += "/n" + op[i];
		}
		return idB + ops + previousHash;
	}
	
	public static String calculerHash(Bloc bloc) {
		if (bloc != null) {
		      MessageDigest digest = null;

		      try {
		        digest = MessageDigest.getInstance("SHA-256");
		      } catch (NoSuchAlgorithmException e) {
		        return null;
		      }
					
		      String txt = bloc.str();
		      final byte bytes[] = digest.digest(txt.getBytes());
		      final StringBuilder builder = new StringBuilder();
					
		      for (final byte b : bytes) {
		        String hex = Integer.toHexString(0xff & b);

		        if (hex.length() == 1) {
		          builder.append('0');
		        }
						
		        builder.append(hex);
		      }
					
		      return builder.toString();
		    }
			  
		    return null;
	}
	
}
