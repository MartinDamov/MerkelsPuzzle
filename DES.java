import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class DES {

	private static Cipher cipher;
	
	/**
	 * Default construct setting to DES cipher
	 * @throws Exception 
	 * @throws NoSuchAlgorithmException 
	 */
	public DES() throws NoSuchAlgorithmException, Exception {
		cipher = Cipher.getInstance("DES");
	}
	
	public byte[] decrypt(String ciphertext, SecretKey key) throws Exception{
		
		// Convert ciphertext from string to bytes
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] ciphertextBytes = decoder.decode(ciphertext);
		
		// Set the cipher to decyption mode and decrypt the ciphertext
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = cipher.doFinal(ciphertextBytes);
		
		return decrypted;
	}
}
