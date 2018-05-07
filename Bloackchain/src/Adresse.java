import java.io.Serializable;

/**
 * Adresse d'un objet rmi
 * 
 * @author Florian GUILLEMEAU
 * @date 7 mai 2018
 */
public class Adresse implements Serializable{
	/** Adresse ip */
	private String ip;
	/** Port */
	private String port;
	
	/**
	 * Constructeur du port
	 * @param ip adresse ip
	 * @param port numéro de port
	 */
	public Adresse(String ip, String port){
		this.ip = ip;
		this.port = port;
	}

	/**
	 * Getter ip
	 * @return adresse ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Getter port
	 * @return numero de port
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * Compare deux adresse
	 * @param ad adresse à comparer
	 * @return true si identique false sinon
	 */
	public boolean equals(Adresse ad){
		return ip.compareTo(ad.getIp())==0 && port.compareTo(ad.getPort())==0;
	}
}
