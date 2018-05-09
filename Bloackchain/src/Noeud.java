import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

public interface Noeud extends Remote{

	public String messageDistant()
			throws RemoteException ;
	
	/**
	 * Envoie une adrese � un serveur pour qu'il l'ajoute � son canet
	 * @param ad adresse � ajouter
	 * @throws RemoteException
	 */
	public void envoieAdresse(Adresse ad)
			throws RemoteException;
	
	public void envoieMessage(String m)
			throws RemoteException;
	
	public void propMessage(String m)
			throws RemoteException;
	
	/**
	 * Ajoute l'adresse du serveur en param�tre au noeud
	 * @param ad Adresse du serveur
	 * @throws RemoteException
	 */
	public void ajouterServeur(Adresse ad)
			throws RemoteException;
	
	/**
	 * Envoie une op�ration au noeud
	 * @param op Op�ration � envoyer
	 * @throws RemoteException
	 */
	public void receptionOperation(Operation op)
			throws RemoteException;
}
