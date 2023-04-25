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

import internal.GlobalVariable
import org.json.JSONObject
import javax.xml.bind.DatatypeConverter
import java.nio.file.Files
import java.nio.file.Paths


public class convertFile {

	@Keyword
	public static String BASE64File(String filename) {

		File f = new File(System.getProperty('user.dir') + "\\File" + filename)
		FileInputStream fis = new FileInputStream(f)
		byte[] byteArray = new byte[(int)f.length()]
		fis.read(byteArray)
		String imageString = new sun.misc.BASE64Encoder().encode(byteArray)

		return imageString.replaceAll("[\r\n\t ]", "")
	}
	
	@Keyword
	public static String DecodeBase64(String base64String, String filename) {
		
		byte[] bytes = DatatypeConverter.parseBase64Binary(base64String)
		
		// Specify the file path and name
		String filePath = System.getProperty('user.dir') + "\\Download\\" + filename + ".PDF"
		
		// Write the decoded bytes to the file
		Files.write(Paths.get(filePath), bytes)
	}
}
