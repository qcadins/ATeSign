package customizekeyword

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonSlurper

public class GetSMS {

	@Keyword
	String getOTP(String nameSMS) {
		ResponseObject response = WS.sendRequest(findTestObject('Postman/SMS/API Pushbullet'))

		if (WS.verifyResponseStatusCode(response, 200)) {
			Map jsonResponse = new JsonSlurper().parseText(response.responseBodyContent)

			String body = jsonResponse.threads.find { thread ->
				thread.recipients[0]?.name == nameSMS && thread.latest?.body
			}?.latest?.body ?: "No message found for '$nameSMS'."

			// Check if the message contains "OTP"
			if (body.toString().containsIgnoreCase('http')) {
				return "OTP Tidak Diterima oleh Pushbullet"
				// Extract the number using regular expression
			} 
			// Extract the number using regular expression
			List<String> digits = body.findAll(/\d+/)
			String otp = digits ? digits.join() : 'No OTP found in the message.'
			return otp
		}

		"Failed to retrieve messages. Status code: ${response.statusCode}"
	}
}


