import java.rmi.server.UnicastRemoteObject ;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.PublicKey;

public class NoeudImpl 
	extends UnicastRemoteObject
	implements Noeud, Runnable{
	
	/** Id du Noeud */
	private int id;
	
	/** Adresse du noeud */
	private Adresse adresse;
	
	/** Carnet d'adresse de serveur */
	private Vector<Adresse> carnetAdresse;
	
	/** Fonction d'int�mentation de l'id des Op*/
	private int incIdOp;
	
	/** Buffer des op�ration en attente de confirmation */
	private Vector<Operation> bufferOp;
	
	/** Chaine de bloc */
	private Blockchain chaine;
	
	/** Tableau associatif d'id et de cl� public */
	private HashMap<Integer, PublicKey> tabClePublic;
	
	/** Paire de cl� */
	private Cles paireCles;
	
	/* Attribut pour la preuve de travaille */
	/** Temps minimum d'attente */
	private static final int MIN_TEMPS = 10;
	/** Temps  maximal d'attente */
	private static final int MAX_TEMPS = 60;
	
	public NoeudImpl (int id, String ip, String port)
		throws RemoteException{
			super();
			this.id = id;
			this.adresse = new Adresse(ip,port);
			this.carnetAdresse = new Vector<Adresse>();
			this.bufferOp = new Vector<Operation>();
			this.chaine = new Blockchain();
			this.tabClePublic = new HashMap<Integer, PublicKey>();
			this.paireCles = new Cles();
	}
	
	/**
	 * V�rifie si l'adresse est inconnue
	 * @param ad adresse a v�rifie
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
	
	/**
	 * Ajout l'adresse du serveur � l'objet courant et ajout l'adresse de l'objet courant au serveur en param�tre
	 * @param ad adresse du serveur
	 */
	public void lierServeur(Adresse ad){
		ajouterAdresse(ad);
		// Ajout de l'adresse de l'objet local � l'autre serveur
		try{
			Noeud b = (Noeud) Naming.lookup("rmi://" + ad.getIp() + ":" + ad.getPort() + "/Message") ;
			
			//Envoie la cl� au serveur
			b.receptionCle(id, paireCles.getClePublic());
			
			
			// Demander la cl� du noeud
			ajouterCle(b.demanderId(), b.demanderCle());
			
			// Demander Operation
			Operation[] tabOp = b.demanderOperations();
			for(Operation op : tabOp){
				if(this.ajouterOperation(op)){
					propagerOperation(op);
				}
			}
			
			// Demande les blocs du noeud																																																				
			Bloc[] blocs = b.demanderBlocs();
			for(Bloc bloc : blocs){
				if(this.ajouterBloc(bloc)){
					propagerBloc(bloc);
				}
			}
			
			// Envoie toutes les donn�es du noeud
			b.ajouterServeur(this.adresse);
			try{
				TimeUnit.SECONDS.sleep(1);
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		catch (NotBoundException re) { System.out.println(re) ; }
		catch (RemoteException re) { System.out.println(re) ; }
		catch (MalformedURLException e) { System.out.println(e) ; }
	}
	
	/**
	 * Ajout l'op�ration au buffer a condition qu'elle n'existe pas d�j�
	 * @param op Op�ration � ajotuer
	 */
	public synchronized boolean ajouterOperation(Operation op){
		if(estOperationInconnue(op)){
			if(op.getIdO() > incIdOp){
				incIdOp = op.getIdO()+1;
			}
			bufferOp.add(op);
			System.out.println("Operation ajoute : " + op);
			return true;
		}
		else{
			System.out.println("Operation : " + op + " est deja connue");
		}
		return false;
	}
	
	/**
	 * V�rifie si l'op�ration existe d�j� dans le buffer
	 * @param op Op�ration � v�rifier
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
	 * Propage l'op�ration � tous ces voisins
	 * @param op Op�ration a propager
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
		PublicKey cle;
		byte[] tmp = null;
		// Si c'est ce noeud qui a cr�� ce bloc
		if(id == bloc.getIdCreateur()){
			cle = this.paireCles.getClePublic();
		}
		else{
			// Regarde si le noeud connait une cl� associ� � l'id
			cle = tabClePublic.get(bloc.getIdCreateur());
		}
		
		
		// Si on connait la cl� alors on d�crypte pour la v�rification
		if(cle != null){
			tmp = bloc.getHash(); // On sauvegarde le hash pr�c�dent
			bloc.setHash(Cles.dechiffrement(bloc.getHash(), cle));
		}
		
		//V�rification si le nouveau bloc est valide (si il est d�j� connue alors il est invalide)
		if(chaine.isValidNewBlock(bloc, chaine.dernierBloc())){
			// Si le blo �tait chiffr� alors on remet le hash chiffr�
			if(cle != null){
				bloc.setHash(tmp);
			}
			chaine.ajoutBloc(bloc);
			System.out.println("Ajout du bloc : " + bloc.getIdB());
			System.out.println("Elimination des op�rations presente dans le bloc");
			eliminerOperation(bloc);
			return true;
		}
		else{
			System.out.println("Le bloc n'est pas valide ou est deja connue");
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
	
	/**
	 * Elimine les op�rations du buffer pr�sent dans le bloc
	 * @param b Bloc
	 */
	public void eliminerOperation(Bloc b){
		//V�rifie pour chaque op�ration pr�sent dans le bloc
		for(int i=0; i<b.getOp().length; i++){
			// Et pour chaque op�ration du Buffer
			for(int j=0; j<bufferOp.size(); j++){
				//Si deux op�rations correspondent
				if(b.getOp()[i].equals(bufferOp.get(j))){
					System.out.println("Suppremession : " + bufferOp.get(j).getIdO());
					// Supprime 
					bufferOp.remove(j);
					// Decremente de 1 car la taille vient de diminuer de 1
					j--;
				}
			}
		}
	}
	
	/**
	 * Ajoute la cl�
	 * @param id Id du serveur
	 * @param c Cl� du serveur
	 * @return si la cl� a �t� ajout�
	 */
	public synchronized boolean ajouterCle(int id, PublicKey c){
		if(estCleInconnue(id)){
			tabClePublic.put(id, c);
			System.out.println("La cl� du serveur " + id + " est ajoute");
			return true;
		}
		else{
			System.out.println("L'id est d�ja connue");
		}
		return false;
	}
	
	/**
	 * Si l'identifiant est d�ja associ� � une valeur
	 * @param id Id associ� au serveur
	 * @return si l'id est deja inconnue alors true sinon false
	 */
	public boolean estCleInconnue(int id){
		return !tabClePublic.containsKey(id);
	}
	
	/**
	 * Propage la cl� aux voisins
	 * @param id Id du serveur
	 * @param c Cl� public du serveur
	 */
	public void propagationCle(int id, PublicKey c){
		for(int i=0; i<carnetAdresse.size(); i++){
			try{
				Noeud b = (Noeud) Naming.lookup(
						"rmi://" + carnetAdresse.get(i).getIp() 
						+ ":" + carnetAdresse.get(i).getPort() + "/Message");
				b.receptionCle(id, c);
			}
			catch (NotBoundException re) { System.out.println(re) ; }
			catch (RemoteException re) { System.out.println(re) ; }
			catch (MalformedURLException e) { System.out.println(e) ; }
		}
	}
	
	/**
	 * Fait une preuve de Travail et cr�e un bloc
	 * @return novueau bloc
	 */
	public Bloc preuveDeTravaille(){
		Bloc b;
		int idB=0;
		Operation[] tabOp = new Operation[bufferOp.size()];
		//bufferOp.toArray(tabOp);
		//bufferOp.copyInto(tabOp);
		for(int i=0; i<bufferOp.size(); i++){
			tabOp[i] = bufferOp.get(i);
		}
		
		try{
			TimeUnit.SECONDS.sleep((int)(Math.random()*(MAX_TEMPS - MIN_TEMPS) + MIN_TEMPS));
		}
		catch(Exception e){
			System.out.println(e);
		}
		if(chaine.dernierBloc() != null){
			idB = chaine.dernierBloc().getIdB()+1;
		}
		b = new Bloc(
			idB,
			tabOp,
			//null,
			chaine.dernierBloc()==null? null : chaine.dernierBloc().getHash(),
			id);
		//Crypte le hash
		b.setHash(Cles.chiffrement(b.getHash(), this.paireCles.getClePrive()));
		return b;
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

	public synchronized void ajouterServeur(Adresse ad)
			throws RemoteException{
		ajouterAdresse(ad);
	}
	
	public void receptionOperation(Operation op)
			throws RemoteException{
		if(ajouterOperation(op)){
			// Propagation de l'operation
			System.out.println("Propagation de l'operation : " + op.getIdO());
			this.propagerOperation(op);
		}
		
	}
	
	public void receptionBloc(Bloc bloc)
			throws RemoteException{
		// Si le bloc a �t� rajout� alors on propage
		if(ajouterBloc(bloc)){
			//Propage le bloc au voisin
			propagerBloc(bloc);
		}
	}
	
	/**
	 * Transfere tous les blocs de la chaine vers le client
	 * @return tableau de bloc
	 * @throws RemoteException
	 */
	public Bloc[] demanderBlocs()
			throws RemoteException{
		Bloc[] tab = new Bloc[chaine.getBlocs().size()];
		for(int i=0; i<chaine.getBlocs().size(); i++){
			tab[i] = chaine.getBlocs().get(i);
		}
		return tab;
	}
	
	/**
	 * Envoie la cl� public et l'id associ� � cette cl�
	 * @param id Id du serveur
	 * @param c Cl� public
	 * @throws RemoteException
	 */
	public void receptionCle(int id, PublicKey c)
			throws RemoteException{
		// Propage si la cl� a �t� ajout�
		if(ajouterCle(id,c)){
			propagationCle(id, c);;
		}
	}
	
	/**
	 * Demande au serveur
	 * @return la cl� du serveur
	 * @throws RemoteException
	 */
	public PublicKey demanderCle()
			throws RemoteException{
		return paireCles.getClePublic();
	}
	
	/**
	 * Demande les op�rations du buffer
	 * @return Les op�rations du buffer
	 * @throws RemoteException
	 */
	public Operation[] demanderOperations()
			throws RemoteException{
		Operation[] tab = new Operation[bufferOp.size()];
		for(int i=0; i<bufferOp.size(); i++){
			tab[i] = bufferOp.get(i);
		}
		return tab;
	}
	
	/**
	 * Demande l'id du serveur
	 * @return
	 * @throws RemoteException
	 */
	public int demanderId()
			throws RemoteException{
		return this.id;
	}

	/*********************/
	/* Fonction Runnable */
	/*********************/
	/**
	 * Cr�e un thread. Ce thread lance le serveur.
	 */
	public void run(){
		try{
			/** Lancement du Serveur */
			Naming.rebind("rmi://" + this.adresse.getIp() + ":" + this.adresse.getPort() + "/Message" ,this);
			System.out.println("Serveur pret");
		}
		catch (RemoteException re) {
			System.out.println(re);
			//System.exit(1);
		}
		catch (MalformedURLException e) {
			System.out.println(e);
			//System.exit(1);
		}
	}
}