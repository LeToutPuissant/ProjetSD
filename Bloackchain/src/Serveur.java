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
			//objLocal.ajouterOperation(new Operation(0,"Merde"));
			/** Fait du travail */
			for(int i=0; i<10; i++){
				bloc = objLocal.preuveDeTravaille();
				// Si le bloc a été ajouté
				if(objLocal.ajouterBloc(bloc)){
					System.out.println("Ce noeud a créé le bloc " + bloc.getIdB());
					objLocal.propagerBloc(bloc);
				}
			}
		}
		catch (RemoteException re) { System.out.println(re) ; }
	}
}
