package customizekeyword

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import groovy.json.JsonSlurper
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

public class GetSMS {

	String currentDate = new Date().format('yyyy-MM-dd')
	Map threadForRecipient, threadLastest, jsonResponse

	@Keyword
	String getOTP(String nameSMS) {
		ResponseObject response = WS.sendRequest(findTestObject('Postman/SMS/API Pushbullet', [('function') : 'threads']))

		if (WS.verifyResponseStatusCode(response, 200)) {
			jsonResponse = new JsonSlurper().parseText(response.responseBodyContent)

			// Filter threads based on dynamic recipient name
			threadForRecipient = jsonResponse.threads.find { thread ->
				thread.recipients[0]?.name == nameSMS
			}

			// Extract the ID if the thread with the dynamic recipient name is found
			String id = threadForRecipient ? threadForRecipient.id : ""

			if (id != '') {
				ResponseObject responseDetail = WS.sendRequest(findTestObject('Postman/SMS/API Pushbullet', [('function') : 'thread_' + id]))

				if (WS.verifyResponseStatusCode(responseDetail, 200)) {
					Map jsonResponseDetail = new JsonSlurper().parseText(responseDetail.responseBodyContent)

					threadLastest = jsonResponseDetail.thread[0]

					// Extract the timestamp from the thread
					String timeStamp = threadLastest.timestamp ?: "No timestamp found for this thread."

					// Convert the timestamp string to a long
					long timestamp = Long.parseLong(timeStamp)

					// Convert the UTC timestamp to Jakarta time
					LocalDateTime utcDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of("UTC"))
					LocalDateTime jakartaDateTime = utcDateTime.plusHours(7)

					// Format Jakarta date
					String jakartaDate = jakartaDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

					if (currentDate == jakartaDate) {
						// Extract the body of the latest message in the thread
						String body = threadLastest.body ?: "No message found for '$nameSMS'."

						// Check if the message contains "OTP"
						if (body.containsIgnoreCase('http')) {
							return "OTP Tidak Diterima oleh Pushbullet"
						}

						// Extract the number using regular expression
						List<String> digits = body.findAll(/\d+/)
						String otp = digits ? digits.join() : 'No OTP found in the message.'

						otp
					} else {
						'Tidak dapat mengambil OTP dikarenakan tidak ada OTP yang masuk via SMS hari ini.'
					}
				} else {
					"Failed to retrieve thread details. Status code: ${responseDetail.statusCode}"
				}
			}
		}
	}
}