import java.rmi.server.UnicastRemoteObject ;
import java.util.Vector;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException ;

public class NoeudImpl 
	extends UnicastRemoteObject
	implements Noeud{
	
	/** Id du Noeud */
	private int id;
	
	/** Adresse du noeud */
	private Adresse adresse;
	
	/** Carnet d'adresse de serveur */
	private Vector<Adresse> carnetAdresse;
	
	public NoeudImpl (int id, String ip, String port)
		throws RemoteException{
			super();
			this.id = id;
			this.adresse = new Adresse(ip,port);
			this.carnetAdresse = new Vector<Adresse>();
	}
	
	/**
	 * Vérifie si l'adresse est inconnue
	 * @param ad adresse a vérifie
	 * @return true si inconnue false sinon
	 */
	public boolean estAdresseInconnue(Adresse ad){
		boolean existeDeja = false;
		
		// Verifie si l'adresse est deja connue
		for(int i=0; !existeDeja && i<carnetAdresse.size(); i++){
			existeDeja = carnetAdresse.get(i).equals(ad);
		}
		
		return !existeDeja;
	}
	
	/**
	 * Ajoute une adresse au carnet du serveur
	 * @param ip adresse ip
	 * @param port numero de port
	 */
	public void ajouterAdresse(String ip, String port){
		ajouterAdresse(new Adresse(ip, port));
	}
	
	/**
	 * Ajout d'une adresse au canet du serveur
	 * @param ad adresse a ajouter
	 */
	public void ajouterAdresse(Adresse ad){
		if(estAdresseInconnue(ad)){
			carnetAdresse.add(ad);
			System.out.println("Adresse ajoute : " + ad.getIp() + ":" + ad.getPort());
		}
		else{
			System.out.println("Adresse : " + ad.getIp() + ":" + ad.getPort() + " est deja connue");
		}
		
	}
	
	public void lierServeur(Adresse ad){
		ajouterAdresse(ad);
		// Ajout de l'adresse de l'objet local à l'autre serveur
		try{
			Noeud b = (Noeud) Naming.lookup("rmi://" + ad.getIp() + ":" + ad.getPort() + "/Message") ;
			b.ajouterServeur(this.adresse);
		}
		catch (NotBoundException re) { System.out.println(re) ; }
		catch (RemoteException re) { System.out.println(re) ; }
		catch (MalformedURLException e) { System.out.println(e) ; }
	}
	
	public void propagerMessage(String m){
		for(Adresse ad : carnetAdresse){
			try{
				Noeud b = (Noeud) Naming.lookup("rmi://" + ad.getIp() + ":" + ad.getPort() + "/Message") ;
				b.envoieMessage(m);
			}
			catch (NotBoundException re) { System.out.println(re) ; }
			catch (RemoteException re) { System.out.println(re) ; }
			catch (MalformedURLException e) { System.out.println(e) ; }
		}
	}
	
	/***********************/
	/* Fonction du Serveur */
	/***********************/

	public String messageDistant()
		throws RemoteException{ 
		return("Ici le serveur : ack !");
    }
	
	public void envoieAdresse(Adresse ad)
			throws RemoteException{
		ajouterAdresse(ad);
	}
	
	public void envoieMessage(String m)
			throws RemoteException{
		System.out.println(m);
	}
	
	public void propMessage(String m)
			throws RemoteException{
		System.out.println(m);
		propagerMessage(m);
	}
	
	public void ajouterServeur(Adresse ad)
			throws RemoteException{
		ajouterAdresse(ad);
	}
}
