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
			
			// Envoie d'une clé inconnue
			//b.receptionCle(id, paire.getClePublic());
			// Fait dormir le proc 60 sec
			try{
				TimeUnit.SECONDS.sleep(30);
			}
			catch(Exception e){
				System.out.println(e);
			}
			
			//Envoie d'une clé inconnue
			//b.receptionCle(id, paire.getClePublic());
			
			// Fait dormir le proc 60 sec
			try{
				TimeUnit.SECONDS.sleep(30);
			}
			catch(Exception e){
				System.out.println(e);
			}
			
			//Renvoie le bloc b1 qui ne devrait pas être stoqué
			//b.receptionCle(id, paire.getClePublic());
			
			// Fait dormir le proc 60 sec
			try{
				TimeUnit.SECONDS.sleep(30);
			}
			catch(Exception e){
				System.out.println(e);
			}
			
			//b.receptionCle(10,new Cles().getClePublic());
			
			System.out.println("fin") ; 
		}
		catch (NotBoundException re) { System.out.println(re) ; }
		catch (RemoteException re) { System.out.println(re) ; }
		catch (MalformedURLException e) { System.out.println(e) ; }
	}
}
