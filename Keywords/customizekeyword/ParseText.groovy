package customizekeyword

import com.kms.katalon.core.annotation.Keyword
import org.apache.commons.codec.binary.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

public class ParseText {
	@Keyword
	parseEncrypt(String value, String aesKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(aesKey.getBytes('UTF-8'), 'AES')
			Cipher cipher = Cipher.getInstance('AES/ECB/PKCS5Padding')
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
			byte[] encrypted = cipher.doFinal(value.getBytes())
			return Base64.encodeBase64String(encrypted)
		} catch (Exception e) {
			e.printStackTrace()
		}
	}

	@Keyword
	parseDecrypt(String encrypted, String aesKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(aesKey.getBytes('UTF-8'), 'AES')
			Cipher cipher = Cipher.getInstance('AES/ECB/PKCS5Padding')
			cipher.init(Cipher.DECRYPT_MODE, skeySpec)
			byte[] originalText = cipher.doFinal(Base64.decodeBase64(encrypted))
			return new String(originalText)
		} catch (Exception e) {
			e.printStackTrace()
		}
	}
	
	
}