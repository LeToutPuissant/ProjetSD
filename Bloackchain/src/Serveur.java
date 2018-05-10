import java.net.* ;
import java.rmi.* ;

public class Serveur{
	public static void main(String [] args){
		if (args.length < 3){
			System.out.println("Usage : java Serveur <id> <ip> <port du rmiregistry> [ipVoisin portVoisin ...]") ;
			System.exit(0);
		}
		
		try{
			/** Création de l'objet */
			NoeudImpl objLocal = new NoeudImpl(Integer.parseInt(args[0]), args[1], args[2]);
			
			/** Ajoute les voisins au carnet d'adresse */
			for(int i=3; i<args.length; i+=2){
				objLocal.lierServeur(new Adresse(args[i], args[i+1]));
			}
			
			
			/** Lancement du Serveur */
			Naming.rebind("rmi://" + args[1] + ":" + args[2] + "/Message" ,objLocal) ;
			System.out.println("Serveur pret") ;
		}
		catch (RemoteException re) { System.out.println(re) ; }
		catch (MalformedURLException e) { System.out.println(e) ; }
	}
}
