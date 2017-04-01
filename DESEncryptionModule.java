import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class DESEncryptionModule {
	
	/**
 	* Encrypts a message with a given key using DES.  
 	* 
 	* @param key key to encrypt the message
 	* @param message message to encrypt
 	* @return encrypted message
 	*/
	public String encryptMessage(SecretKey key, byte[] message){
		
		try{
			//DES cipher instance
			Cipher cipher = Cipher.getInstance("DES");
			
			//Initialise the cipher to be in encrypt mode, using the given key.
			cipher.init(Cipher.ENCRYPT_MODE, key);
			
			//Perform the encryption
			byte[] encryptedByte = cipher.doFinal(message);
			
			//Use CryptoLib to convert the encypted message to a string
			return CryptoLib.byteArrayToString(encryptedByte);
		}
		catch(NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException e){
			//Display exception
			System.out.println("An error occurred when encrypting message. Execution was aborted.\r\n" + e.getMessage());
			e.printStackTrace();
			//Abort program
			System.exit(1);
			return "";
		}
	}
	
	/**
	 * Decrypts a message with a given key using DES.
	 *  
	 * @param key key to decrypt the message
	 * @param message message to decrypt
	 * @return decrypted message
	 */
	public String decryptMessage(SecretKey key, byte[] message){
		
		try{
			
			//DES cipher instance
			Cipher cipher = Cipher.getInstance("DES");
		
			//Initialise the cipher to be in decrypt mode, using the given key.
			cipher.init(Cipher.DECRYPT_MODE, key);
		
			//Perform the decryption
			byte[] decryptedByte = cipher.doFinal(message);
		
			//Use CryptoLib to convert the decrypted message to a string
			return CryptoLib.byteArrayToString(decryptedByte);
		}
		catch(NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException e){
			//Display exception
			System.out.println("An error occurred when encrypting message. Execution was aborted.\r\n" + e.getMessage());
			e.printStackTrace();
			//Abort program
			System.exit(1);
			return "";
		}
	}
	
}
