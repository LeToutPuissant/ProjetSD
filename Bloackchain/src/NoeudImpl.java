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
	
	/** Fonction d'intémentation de l'id des Op*/
	private int incIdOp;
	
	/** Buffer des opération en attente de confirmation */
	private Vector<Operation> bufferOp;
	
	private Blockchain chaine;
	
	public NoeudImpl (int id, String ip, String port)
		throws RemoteException{
			super();
			this.id = id;
			this.adresse = new Adresse(ip,port);
			this.carnetAdresse = new Vector<Adresse>();
			this.bufferOp = new Vector<Operation>();
			this.chaine = new Blockchain();
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
			
			// Envoie toutes les opération du buffer.
			if(!bufferOp.isEmpty()){
				System.out.println("Envoie des opérations du buffer au nouveau contacte");
				try{
					Noeud b = (Noeud) Naming.lookup("rmi://" + ad.getIp() + ":" + ad.getPort() + "/Message") ;
					for(Operation op : bufferOp){
						b.receptionOperation(op);
					}
				}
				catch (NotBoundException re) { System.out.println(re) ; }
				catch (RemoteException re) { System.out.println(re) ; }
				catch (MalformedURLException e) { System.out.println(e) ; }
			}
		}
		else{
			System.out.println("Adresse : " + ad.getIp() + ":" + ad.getPort() + " est deja connue");
		}
	}
	
	/**
	 * Ajout l'adresse du serveur à l'objet courant et ajout l'adresse de l'objet courant au serveur en paramètre
	 * @param ad adresse du serveur
	 */
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
	
	/**
	 * Ajout l'opération au buffer a condition qu'elle n'existe pas déjà
	 * @param op Opération à ajotuer
	 */
	public void ajouterOperation(Operation op){
		if(estOperationInconnue(op)){
			if(op.getIdO() > incIdOp){
				incIdOp = op.getIdO()+1;
			}
			bufferOp.add(op);
			System.out.println("Operation ajoute : " + op);
			propagerOperation(op);
			System.out.println("Propagation");
		}
		else{
			System.out.println("Operation : " + op + " est deja connue");
		}
	}
	
	/**
	 * Vérifie si l'opération existe déjà dans le buffer
	 * @param op Opération à vérifier
	 * @return true si elle n'existe pas dans le buffer false sinon
	 */
	public boolean estOperationInconnue(Operation op){
		boolean existeDeja = false;
		
		// Verifie si l'adresse est deja connue
		for(int i=0; !existeDeja && i<bufferOp.size(); i++){
			existeDeja = bufferOp.get(i).equals(op);
		}
		
		return !existeDeja;
	}
	
	/**
	 * Propage l'opération à tous ces voisins
	 * @param op Opération a propager
	 */
	public void propagerOperation(Operation op){
		for(int i=0; i<carnetAdresse.size(); i++){
			try{
				Noeud b = (Noeud) Naming.lookup(
						"rmi://" + carnetAdresse.get(i).getIp() 
						+ ":" + carnetAdresse.get(i).getPort() + "/Message");
				b.receptionOperation(op);
			}
			catch (NotBoundException re) { System.out.println(re) ; }
			catch (RemoteException re) { System.out.println(re) ; }
			catch (MalformedURLException e) { System.out.println(e) ; }
		}
	}
	
	public synchronized boolean ajouterBloc(Bloc bloc){
		//Vérification si le nouveau bloc est valide (si il est déjà connue alors il est invalide)
		if(chaine.isValidNewBlock(bloc, chaine.dernierBloc())){
			chaine.ajoutBloc(bloc);
			System.out.println("Ajout du bloc : " + bloc.getIdB());
			return true;
		}
		else{
			System.out.println("Le noeud n'est pas valide ou est deja connue");
		}
		return false;
	}
	
	public void propagerBloc(Bloc bloc){
		for(int i=0; i<carnetAdresse.size(); i++){
			try{
				Noeud b = (Noeud) Naming.lookup(
						"rmi://" + carnetAdresse.get(i).getIp() 
						+ ":" + carnetAdresse.get(i).getPort() + "/Message");
				b.receptionBloc(bloc);
			}
			catch (NotBoundException re) { System.out.println(re) ; }
			catch (RemoteException re) { System.out.println(re) ; }
			catch (MalformedURLException e) { System.out.println(e) ; }
		}
	}
	
	/*************/
	/* Accesseur */
	/*************/
	
	public int getId() {
		return id;
	}

	public Adresse getAdresse() {
		return adresse;
	}

	public Vector<Adresse> getCarnetAdresse() {
		return carnetAdresse;
	}

	public int getIncIdOp() {
		return incIdOp;
	}

	public Vector<Operation> getBufferOp() {
		return bufferOp;
	}

	public Blockchain getChaine() {
		return chaine;
	}

	/***********************/
	/* Fonction du Serveur */
	/***********************/

	public synchronized String messageDistant()
		throws RemoteException{ 
		return("Ici le serveur : ack !");
    }
	
	public synchronized void envoieAdresse(Adresse ad)
			throws RemoteException{
		ajouterAdresse(ad);
	}
	
	public synchronized void envoieMessage(String m)
			throws RemoteException{
		System.out.println(m);
	}
	
	public synchronized void propMessage(String m)
			throws RemoteException{
		System.out.println(m);
		propagerMessage(m);
	}
	
	public synchronized void ajouterServeur(Adresse ad)
			throws RemoteException{
		ajouterAdresse(ad);
	}
	
	public synchronized void receptionOperation(Operation op)
			throws RemoteException{
		ajouterOperation(op);
	}
	
	public void receptionBloc(Bloc bloc)
			throws RemoteException{
		// Si le bloc a été rajouté alors on propage
		if(ajouterBloc(bloc)){
			//Propage le bloc au voisin
			propagerBloc(bloc);
		}
	}
}
