package customizekeyword

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonSlurper
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

public class GetSMS {
	@Keyword
	String getOTP(String nameSMS) {
		def response = WS.sendRequest(findTestObject('Postman/SMS/API Pushbullet'))

		if (WS.verifyResponseStatusCode(response, 200)) {
			def jsonResponse = new JsonSlurper().parseText(response.responseBodyContent)

			String body = jsonResponse.threads.find {
				it.recipients[0]?.name == nameSMS && it.latest?.body
			}?.latest?.body ?: "No message found for " + nameSMS + "."

			// Extract the number using regular expression
			List<String> digits = body.findAll(/\d+/)
			String otp = digits ? digits.join() : "No OTP found in the message."
			return otp
		} else {
			return "Failed to retrieve messages. Status code: ${response.statusCode}"
		}
	}
}
