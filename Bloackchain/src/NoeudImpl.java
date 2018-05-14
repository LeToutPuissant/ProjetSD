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
	
	/** Fonction d'intémentation de l'id des Op*/
	private int incIdOp;
	
	/** Buffer des opération en attente de confirmation */
	private Vector<Operation> bufferOp;
	
	/** Chaine de bloc */
	private Blockchain chaine;
	
	/** Tableau associatif d'id et de clé public */
	private HashMap<Integer, PublicKey> tabClePublic;
	
	/** Paire de clé */
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
	
	/**
	 * Ajout l'adresse du serveur à l'objet courant et ajout l'adresse de l'objet courant au serveur en paramètre
	 * @param ad adresse du serveur
	 */
	public void lierServeur(Adresse ad){
		ajouterAdresse(ad);
		// Ajout de l'adresse de l'objet local à l'autre serveur
		try{
			Noeud b = (Noeud) Naming.lookup("rmi://" + ad.getIp() + ":" + ad.getPort() + "/Message") ;
			
			//Envoie la clé au serveur
			b.receptionCle(id, paireCles.getClePublic());
			
			
			// Demander la clé du noeud
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
			
			// Envoie toutes les données du noeud
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
	 * Ajout l'opération au buffer a condition qu'elle n'existe pas déjà
	 * @param op Opération à ajotuer
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
		PublicKey cle;
		byte[] tmp = null;
		// Si c'est ce noeud qui a créé ce bloc
		if(id == bloc.getIdCreateur()){
			cle = this.paireCles.getClePublic();
		}
		else{
			// Regarde si le noeud connait une clé associé à l'id
			cle = tabClePublic.get(bloc.getIdCreateur());
		}
		
		
		// Si on connait la clé alors on décrypte pour la vérification
		if(cle != null){
			tmp = bloc.getHash(); // On sauvegarde le hash précédent
			bloc.setHash(Cles.dechiffrement(bloc.getHash(), cle));
		}
		
		//Vérification si le nouveau bloc est valide (si il est déjà connue alors il est invalide)
		if(chaine.isValidNewBlock(bloc, chaine.dernierBloc())){
			// Si le blo était chiffré alors on remet le hash chiffré
			if(cle != null){
				bloc.setHash(tmp);
			}
			chaine.ajoutBloc(bloc);
			System.out.println("Ajout du bloc : " + bloc.getIdB());
			System.out.println("Elimination des opérations presente dans le bloc");
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
	 * Elimine les opérations du buffer présent dans le bloc
	 * @param b Bloc
	 */
	public void eliminerOperation(Bloc b){
		//Vérifie pour chaque opération présent dans le bloc
		for(int i=0; i<b.getOp().length; i++){
			// Et pour chaque opération du Buffer
			for(int j=0; j<bufferOp.size(); j++){
				//Si deux opérations correspondent
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
	 * Ajoute la clé
	 * @param id Id du serveur
	 * @param c Clé du serveur
	 * @return si la clé a été ajouté
	 */
	public synchronized boolean ajouterCle(int id, PublicKey c){
		if(estCleInconnue(id)){
			tabClePublic.put(id, c);
			System.out.println("La clé du serveur " + id + " est ajoute");
			return true;
		}
		else{
			System.out.println("L'id est déja connue");
		}
		return false;
	}
	
	/**
	 * Si l'identifiant est déja associé à une valeur
	 * @param id Id associé au serveur
	 * @return si l'id est deja inconnue alors true sinon false
	 */
	public boolean estCleInconnue(int id){
		return !tabClePublic.containsKey(id);
	}
	
	/**
	 * Propage la clé aux voisins
	 * @param id Id du serveur
	 * @param c Clé public du serveur
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
	 * Fait une preuve de Travail et crée un bloc
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
		// Si le bloc a été rajouté alors on propage
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
	 * Envoie la clé public et l'id associé à cette clé
	 * @param id Id du serveur
	 * @param c Clé public
	 * @throws RemoteException
	 */
	public void receptionCle(int id, PublicKey c)
			throws RemoteException{
		// Propage si la clé a été ajouté
		if(ajouterCle(id,c)){
			propagationCle(id, c);;
		}
	}
	
	/**
	 * Demande au serveur
	 * @return la clé du serveur
	 * @throws RemoteException
	 */
	public PublicKey demanderCle()
			throws RemoteException{
		return paireCles.getClePublic();
	}
	
	/**
	 * Demande les opérations du buffer
	 * @return Les opérations du buffer
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
	 * Crée un thread. Ce thread lance le serveur.
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