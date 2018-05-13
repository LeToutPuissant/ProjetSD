import java.rmi.* ;
import java.util.concurrent.TimeUnit;
import java.net.MalformedURLException ; 

public class Client{
	public static void main(String [] args){
		if (args.length != 2){
			System.out.println("Usage : java Client <machine du Serveur> <port du rmiregistry>") ;
			System.exit(0) ;
		}
		try{
			int id = 5;
			//Cles paire = new Cles();
			Noeud b = (Noeud) Naming.lookup("rmi://" + args[0] + ":" + args[1] + "/Message") ;
			
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
			b1 = new Bloc(1,listeOp1, null, id);
			b1.setHash(Cles.chiffrement(b1.getHash(), paire.getClePrive()));
			b2 = new Bloc(2,listeOp2, b1.getHash(), id);
			b2.setHash(Cles.chiffrement(b2.getHash(), paire.getClePrive()));
			
			// Envoie de la clé
			b.receptionCle(id, paire.getClePublic());
			
			// Fait dormir le proc 60 sec
			try{
				TimeUnit.SECONDS.sleep(1);
			}
			catch(Exception e){
				System.out.println(e);
			}
			
			//Envoie du bloc1
			b.receptionBloc(b1);
			
			// Fait dormir le proc 60 sec
			try{
				TimeUnit.SECONDS.sleep(1);
			}
			catch(Exception e){
				System.out.println(e);
			}
			
			//Envoie du bloc2
			b.receptionBloc(b2);
			
			
			
			System.out.println("fin") ; 
		}
		catch (NotBoundException re) { System.out.println(re) ; }
		catch (RemoteException re) { System.out.println(re) ; }
		catch (MalformedURLException e) { System.out.println(e) ; }
	}
}
