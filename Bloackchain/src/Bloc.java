import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Bloc implements Serializable{

	private int idB;
	private static final String SEP = "\n";
	private Operation[] op;
	private byte[] hash; //La chaine résultat sera la concatenation de hash et des opérations
	private byte[] previousHash;
	private int idCreateur;
	
	public Bloc(int idBloc, Operation[] op, byte[] preHash, int idCreateur) {
		idB = idBloc;
		this.op = op;
		previousHash = preHash;
		hash = calculerHash(this).getBytes();
		this.idCreateur = idCreateur;
	}

	public int getIdB() {
		return idB;
	}

	public void setIdB(int idB) {
		this.idB = idB;
	}

	public byte[] getHash() {
		return hash;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public byte[] getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(byte[] previousHash) {
		this.previousHash = previousHash;
	}

	public Operation[] getOp() {
		return op;
	}

	public void setOp(Operation[] op) {
		this.op = op;
	}
	
	public int getIdCreateur() {
		return idCreateur;
	}

	public String str() {
		String ops = op[0].toString();
		//On concatène les opérations en une seule String
		for(int i = 1; i < op.length; i++) {
			ops += SEP + op[i];
		}
		return idB + ops + previousHash;
	}
	
	public String toString(){
		return str();
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
