
public class Main {

	public static void main(String[] args) {
		/*Operation o1 = new Operation(1, "Cocou");
		Operation o2 = new Operation(2, "Vendredi");
		Operation o3 = new Operation(3, "Sylvain");
		Operation o4 = new Operation(4, "Perdu");
		Operation o5 = new Operation(5, "Dead Cells");
		
		Operation[] listeOp1 = new Operation[3];
		listeOp1[0] = o1;
		listeOp1[1] = o2;
		listeOp1[2] = o3;
		
		Operation[] listeOp2 = new Operation[2];
		listeOp2[0] = o4;
		listeOp2[1] = o5;
		
		Blockchain bc = new Blockchain();
		bc.ajoutBloc(bc.nouveauBloc(listeOp1));
		bc.ajoutBloc(bc.nouveauBloc(listeOp2));
		
		bc.afficheBlockchain();
		
		if(bc.isBlockChainValid()) {
			System.out.println("Valide");
		}
		else {
			System.out.println("Non valide");
		}*/
		
		String message = "message";
		Cles paireCles = new Cles();
		byte[] messageDecrypte, messageCrypte, m = message.getBytes();
		
		// prive -> public
		messageCrypte = Cles.chiffrement(m, paireCles.getClePrive());
		
		messageDecrypte = Cles.dechiffrement(messageCrypte, paireCles.getClePublic());
		
		System.out.println("Message identique ? " + (message.compareTo(new String(messageDecrypte))==0) );
		
		//public -> prive
		messageCrypte = Cles.chiffrement(m, paireCles.getClePublic());
		
		messageDecrypte = Cles.dechiffrement(messageCrypte, paireCles.getClePrive());
		
		System.out.println("Message identique ? " + (message.compareTo(new String(messageDecrypte))==0));
			
	}
}
