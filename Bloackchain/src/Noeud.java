import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

public interface Noeud extends Remote{

	public String messageDistant()
			throws RemoteException ;
	
	/**
	 * Envoie une adrese à un serveur pour qu'il l'ajoute à son canet
	 * @param ad adresse à ajouter
	 * @throws RemoteException
	 */
	public void envoieAdresse(Adresse ad)
			throws RemoteException;
	
	public void envoieMessage(String m)
			throws RemoteException;
	
	public void propMessage(String m)
			throws RemoteException;
	
	public void ajouterServeur(Adresse ad)
			throws RemoteException;
	
	public void envoieOperation(Operation op)
			throws RemoteException;
}
