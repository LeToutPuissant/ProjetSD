import java.net.* ;
import java.rmi.* ;
import java.util.concurrent.TimeUnit;

public class Serveur{
	public static void main(String [] args){
		if (args.length < 3){
			System.out.println("Usage : java Serveur <id> <ip> <port du rmiregistry> [ipVoisin portVoisin ...]") ;
			System.exit(0);
		}
		
		try{
			Bloc bloc;
			/** Création de l'objet */
			NoeudImpl objLocal = new NoeudImpl(Integer.parseInt(args[0]), args[1], args[2]);
			
			/** Ajoute les voisins au carnet d'adresse */
			for(int i=3; i<args.length; i+=2){
				objLocal.lierServeur(new Adresse(args[i], args[i+1]));
			}
			
			
			/** Lancement du Serveur */
			Thread t = new Thread(objLocal);
			t.run();
			
			/** Fait du travail */
			for(int i=0; i<10; i++){
				objLocal.travaille();
			}
		}
		catch (RemoteException re) { System.out.println(re) ; }
		//Termine le processus
		System.out.println("Fin serveur");
		System.exit(0);
	}
}
