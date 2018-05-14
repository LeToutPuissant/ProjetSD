import java.rmi.Remote ; 
import java.rmi.RemoteException ;
import java.security.PublicKey; 

public interface Noeud extends Remote{
	
	/**
	 * Envoie une adrese � un serveur pour qu'il l'ajoute � son canet
	 * @param ad adresse � ajouter
	 * @throws RemoteException
	 */
	public void envoieAdresse(Adresse ad)
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
	
	/**
	 * Envoie un bloc au noeud
	 * @param bloc Bloc � envoyer
	 * @throws RemoteException
	 */
	public void receptionBloc(Bloc bloc)
			throws RemoteException;
	
	/**
	 * Transfere une tous les blocs de la chaine vers le client
	 * @return tableau de bloc
	 * @throws RemoteException
	 */
	public Bloc[] demanderBlocs()
			throws RemoteException;
	
	/**
	 * Envoie la cl� public et l'id associ� � cette cl�
	 * @param id Id du serveur
	 * @param c Cl� public
	 * @throws RemoteException
	 */
	public void receptionCle(int id, PublicKey c)
			throws RemoteException;
	
	/**
	 * Demande au serveur
	 * @return la cl� du serveur
	 * @throws RemoteException
	 */
	public PublicKey demanderCle()
			throws RemoteException;
	
	/**
	 * Demande les op�rations du buffer
	 * @return Les op�rations du buffer
	 * @throws RemoteException
	 */
	public Operation[] demanderOperations()
			throws RemoteException;
	
	/**
	 * Demande l'id du serveur
	 * @return
	 * @throws RemoteException
	 */
	public int demanderId()
			throws RemoteException;
}
