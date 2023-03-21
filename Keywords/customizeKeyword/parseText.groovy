package customizeKeyword

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import org.openqa.selenium.Keys as Keys
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import internal.GlobalVariable


public class parseText {
	
	@Keyword
	public static String parseEncrypt(String value, String AESKey) {
		try {
			String key = "JavasEncryptDemo"; // 128 bit key
			String randomVector = "RandomJavaVector"; // 16 bytes IV 
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
	public static String parseDecrypt(String encrypted, String AESKey)
	{
		try {
			String key = "JavasEncryptDemo"; // 128 bit key
			String randomVector = "RandomJavaVector"; // 16 bytes IV 
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
