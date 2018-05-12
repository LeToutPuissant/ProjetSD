import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Class créant les clés cryptage. Elle permet aussi de encodé et de décoder
 * 
 * @author Florian GUILLEMEAU
 * @date 10 mai 2018
 */
public class Cles {
	/** Clé privée */
	private PrivateKey clePrive;
	/** Clé public */
	private PublicKey clePublic;
	
	/**
	 * Constructeur de la paire de clé
	 */
	public Cles(){
		// Création d'un générateur RSA
        KeyPairGenerator generateurCles = null;
        try {
            generateurCles = KeyPairGenerator.getInstance("RSA");
            generateurCles.initialize(2048);
        } catch(NoSuchAlgorithmException e) {
            System.err.println("Erreur lors de l'initialisation du générateur de clés : " + e);
            System.exit(-1);
        }
 
        // Génération de la paire de clés
        KeyPair paireCles = generateurCles.generateKeyPair();
        
        clePrive = paireCles.getPrivate();
        clePublic = paireCles.getPublic();
	}
	
	/**
	 * Renvoie la clé privée
	 * @return clé privée
	 */
	public PrivateKey getClePrive(){
		//return paireCles.getPrivate();
		return clePrive;
	}
	
	/**
	 * Renvoie la clé public
	 * @return clé public
	 */
	public PublicKey getClePublic(){
		//return paireCles.getPublic();
		return clePublic;
	}
	
	/**
	 * Dechiffrement d'un message
	 * @param message Message à déchiffrer
	 * @param cle Clé de déchiffrement
	 * @return Message déchiffré
	 */
	public static byte[] dechiffrement(byte[] message, Key cle){
		 // Déchiffrement du message
        byte[] bytes = null;
        try {
            Cipher dechiffreur = Cipher.getInstance("RSA");
            dechiffreur.init(Cipher.DECRYPT_MODE, cle);
            System.out.println("poil");
            bytes = dechiffreur.doFinal(message);
        } catch(NoSuchAlgorithmException e) {
            System.err.println("Erreur lors du dechiffrement : " + e);
            System.exit(-1);
        } catch(NoSuchPaddingException e) {
            System.err.println("Erreur lors du dechiffrement : " + e);
            System.exit(-1);
        } catch(InvalidKeyException e) {
            System.err.println("Erreur lors du dechiffrement : " + e);
            System.exit(-1);
        } catch(IllegalBlockSizeException e) {
            System.err.println("Erreur lors du dechiffrement : " + e);
            System.exit(-1);
        } catch(BadPaddingException e) {
            System.err.println("Erreur lors du dechiffrement : " + e);
            System.exit(-1);
        }
 
        return bytes;
	}
	
	/**
	 * Chiffrement du message
	 * @param message Message à chiffrer
	 * @param cle Clé de cryptage
	 * @return message chiffré
	 */
	public static byte[] chiffrement(byte[] message, Key cle){
		 // Chiffrement du message
        byte[] bytes = null;
        try {
            Cipher dechiffreur = Cipher.getInstance("RSA");
            dechiffreur.init(Cipher.ENCRYPT_MODE, cle);
            bytes = dechiffreur.doFinal(message);
        } catch(NoSuchAlgorithmException e) {
            System.err.println("Erreur lors du chiffrement : " + e);
            System.exit(-1);
        } catch(NoSuchPaddingException e) {
            System.err.println("Erreur lors du chiffrement : " + e);
            System.exit(-1);
        } catch(InvalidKeyException e) {
            System.err.println("Erreur lors du chiffrement : " + e);
            System.exit(-1);
        } catch(IllegalBlockSizeException e) {
            System.err.println("Erreur lors du chiffrement : " + e);
            System.exit(-1);
        } catch(BadPaddingException e) {
            System.err.println("Erreur lors du chiffrement : " + e);
            System.exit(-1);
        }
        
        return bytes;
	}
}
