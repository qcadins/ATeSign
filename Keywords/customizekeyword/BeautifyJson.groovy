package customizekeyword

import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class BeautifyJson {

	WriteExcel needto = new WriteExcel()

	@Keyword
	def process(String responseBody, String sheet, int rowNo, String fileName) {
		try {
			// Parse the original JSON string
			def slurper = new groovy.json.JsonSlurper()
			def json = slurper.parseText(responseBody)

			// Beautify the JSON
			def builder = new groovy.json.JsonBuilder(json)
			def beautifiedJson = builder.toPrettyString()

			try {
				needto.writeToExcel(GlobalVariable.DataFilePath, sheet, rowNo, GlobalVariable.NumofColm -
						1, beautifiedJson.toString())
			} catch (FileNotFoundException ex) {
				String beautifiedJsonPath = System.getProperty('user.dir') + '\\Response\\' + fileName + '.json'

				new File(beautifiedJsonPath).text = beautifiedJson

				needto.writeToExcel(GlobalVariable.DataFilePath, sheet, rowNo, GlobalVariable.NumofColm -
						1, beautifiedJsonPath)

				ex.printStackTrace()
			}
		} catch (Exception e) {
			this.println("Failed to beautify the JSON: ${e.message}")
			
			e.printStackTrace()
		}
	}
}