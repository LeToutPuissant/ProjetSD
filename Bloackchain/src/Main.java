
public class Main {

	public static void main(String[] args) {
		Operation o1 = new Operation(1, "Cocou");
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
		
		Cles paire = new Cles();
		
		Bloc b1, b2;
		b1 = new Bloc(1,listeOp1, null, 0);
		
		//Chiffrement du hash
		b1.setHash(Cles.chiffrement(b1.getHash(), paire.getClePrive()));
		
		// Sauvegarde le hash crypté
		byte[] tmp = b1.getHash();
		
		//Renplace le hash crypté par un hash decrypté
		byte[] oui = Cles.dechiffrement(b1.getHash(), paire.getClePublic());
		b1.setHash(oui);
		
		// Vérifie la validité du Bloc
		Blockchain bc = new Blockchain();
		System.out.println(bc.isValidNewBlock(b1, null));
		
		//Remet le hash crypté
		b1.setHash(tmp);
		
		// Ajout à la chaine
		bc.ajoutBloc(b1);
		
		
		/* B2 */
		b2 = new Bloc(2,listeOp2, b1.getHash(), 0);
		
		//Chiffrement du hash
		b2.setHash(Cles.chiffrement(b2.getHash(), paire.getClePrive()));
		
		// Sauvegarde le hash crypté
		tmp = b2.getHash();
		
		//Renplace le hash crypté par un hash decrypté
		 oui = Cles.dechiffrement(b2.getHash(), paire.getClePublic());
		b2.setHash(oui);
		
		// Vérifie la validité du Bloc
		System.out.println(bc.isValidNewBlock(b2, b1));
		
		//Remet le hash crypté
		b2.setHash(tmp);
		
		// Ajout à la chaine
		bc.ajoutBloc(b2);
		
		
		/*
		//Oui
		String hashComp = b1.calculerHash(b1); //Création d'un hash de comparaison
		String hashDechiffre = new String(Cles.dechiffrement(b1.getHash(), paire.getClePublic())); // Decriptage du hash
		
		System.out.println(hashComp);
		System.out.println(hashDechiffre);
		
		System.out.println(hashComp.compareTo(hashDechiffre)==0);
		
		//Comparaison de byte[]
		System.out.println(hashComp.getBytes());
		System.out.println(hashDechiffre.getBytes());
		
		System.out.println(hashComp.getBytes() == hashDechiffre.getBytes());*/
		
		
		//b2 = new Bloc(1,listeOp2, b1.getHash());
		
		
		/*
		Blockchain bc = new Blockchain();
		bc.ajoutBloc(b1);
		bc.ajoutBloc(bc.nouveauBloc(listeOp2));*/
		
		
	}
}
