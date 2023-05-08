package customizeKeyword

import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable
import org.json.JSONObject
import javax.xml.bind.DatatypeConverter
import java.nio.file.Files
import java.nio.file.Paths


public class ConvertFile {

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
