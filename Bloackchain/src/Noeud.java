import java.rmi.Remote ; 
import java.rmi.RemoteException ;
import java.security.PublicKey; 

public interface Noeud extends Remote{
	
	/**
	 * Envoie une adrese à un serveur pour qu'il l'ajoute à son canet
	 * @param ad adresse à ajouter
	 * @throws RemoteException
	 */
	public void envoieAdresse(Adresse ad)
			throws RemoteException;
	
	/**
	 * Ajoute l'adresse du serveur en paramètre au noeud
	 * @param ad Adresse du serveur
	 * @throws RemoteException
	 */
	public void ajouterServeur(Adresse ad)
			throws RemoteException;
	
	/**
	 * Envoie une opération au noeud
	 * @param op Opération à envoyer
	 * @throws RemoteException
	 */
	public void receptionOperation(Operation op)
			throws RemoteException;
	
	/**
	 * Envoie un bloc au noeud
	 * @param bloc Bloc à envoyer
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
	 * Envoie la clé public et l'id associé à cette clé
	 * @param id Id du serveur
	 * @param c Clé public
	 * @throws RemoteException
	 */
	public void receptionCle(int id, PublicKey c)
			throws RemoteException;
	
	/**
	 * Demande au serveur
	 * @return la clé du serveur
	 * @throws RemoteException
	 */
	public PublicKey demanderCle()
			throws RemoteException;
	
	/**
	 * Demande les opérations du buffer
	 * @return Les opérations du buffer
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
