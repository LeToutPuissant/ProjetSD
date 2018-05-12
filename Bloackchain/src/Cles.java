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
 * Class cr�ant les cl�s cryptage. Elle permet aussi de encod� et de d�coder
 * 
 * @author Florian GUILLEMEAU
 * @date 10 mai 2018
 */
public class Cles {
	/** Cl� priv�e */
	private PrivateKey clePrive;
	/** Cl� public */
	private PublicKey clePublic;
	
	/**
	 * Constructeur de la paire de cl�
	 */
	public Cles(){
		// Cr�ation d'un g�n�rateur RSA
        KeyPairGenerator generateurCles = null;
        try {
            generateurCles = KeyPairGenerator.getInstance("RSA");
            generateurCles.initialize(2048);
        } catch(NoSuchAlgorithmException e) {
            System.err.println("Erreur lors de l'initialisation du g�n�rateur de cl�s : " + e);
            System.exit(-1);
        }
 
        // G�n�ration de la paire de cl�s
        KeyPair paireCles = generateurCles.generateKeyPair();
        
        clePrive = paireCles.getPrivate();
        clePublic = paireCles.getPublic();
	}
	
	/**
	 * Renvoie la cl� priv�e
	 * @return cl� priv�e
	 */
	public PrivateKey getClePrive(){
		//return paireCles.getPrivate();
		return clePrive;
	}
	
	/**
	 * Renvoie la cl� public
	 * @return cl� public
	 */
	public PublicKey getClePublic(){
		//return paireCles.getPublic();
		return clePublic;
	}
	
	/**
	 * Dechiffrement d'un message
	 * @param message Message � d�chiffrer
	 * @param cle Cl� de d�chiffrement
	 * @return Message d�chiffr�
	 */
	public static byte[] dechiffrement(byte[] message, Key cle){
		 // D�chiffrement du message
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
	 * @param message Message � chiffrer
	 * @param cle Cl� de cryptage
	 * @return message chiffr�
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
