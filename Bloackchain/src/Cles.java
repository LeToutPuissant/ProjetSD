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
	/** Paire de cl�s de cryptage */
	private KeyPair paireCles;
	
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
	}
	
	/**
	 * Renvoie la cl� priv�e
	 * @return cl� priv�e
	 */
	public PrivateKey getClePrive(){
		return paireCles.getPrivate();
	}
	
	/**
	 * Renvoie la cl� public
	 * @return cl� public
	 */
	public PrivateKey getClePublic(){
		return paireCles.getPrivate();
	}
	
	public static String dechiffrement(String message, Key cle){
		byte[] messageCode = message.getBytes();
		 // D�chiffrement du message
        byte[] bytes = null;
        try {
            Cipher dechiffreur = Cipher.getInstance("RSA");
            dechiffreur.init(Cipher.DECRYPT_MODE, cle);
            bytes = dechiffreur.doFinal(messageCode);
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
 
        return new String(bytes);
	}
	
	public static String chiffrement(String message, Key cle){
		byte[] messageCode = message.getBytes();
		 // Chiffrement du message
        byte[] bytes = null;
        try {
            Cipher dechiffreur = Cipher.getInstance("RSA");
            dechiffreur.init(Cipher.ENCRYPT_MODE, cle);
            bytes = dechiffreur.doFinal(messageCode);
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
 
        return new String(bytes);
	}
}
