package customizeKeyword

import com.kms.katalon.core.annotation.Keyword
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import internal.GlobalVariable


public class parseText {

	@Keyword
	public static String parseEncrypt(String value, String AESKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(AESKey.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(value.getBytes());
			return Base64.encodeBase64String(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Keyword
	public static String parseDecrypt(String encrypted, String AESKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(AESKey.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] originalText = cipher.doFinal(Base64.decodeBase64(encrypted));
			return new String(originalText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
