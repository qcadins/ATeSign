package customizekeyword

import com.kms.katalon.core.annotation.Keyword
import javax.mail.BodyPart as BodyPart
import javax.mail.Folder as Folder
import javax.mail.Message
import javax.mail.Session as Session
import javax.mail.Store
import javax.mail.internet.MimeMultipart as MimeMultipart
import org.jsoup.Jsoup as Jsoup
import org.jsoup.nodes.Document as Document
import java.util.regex.Matcher as Matcher
import java.util.regex.Pattern as Pattern

public class GetEmail {

	@Keyword
	getEmailContent(String email, String inputPassword, String value) {
		String host = 'outlook.office365.com', otpCode, username = email, password = inputPassword

		Properties properties = new Properties()

		properties.put('mail.pop3.host', host)

		properties.put('mail.pop3.port', '995')

		properties.put('mail.pop3.starttls.enable', 'true')

		Session emailSession = Session.getDefaultInstance(properties)

		//create the POP3 store object and connect with the pop server
		Store store = emailSession.getStore('pop3s')

		store.connect(host, username, password)

		//create the folder object and open it
		Folder emailFolder = store.getFolder('INBOX')

		emailFolder.open(Folder.READ_ONLY)

		// retrieve the messages from the folder in an array and print it
		Message[] messages = emailFolder.messages

		int lastEmailSequence = messages.length

		Message message = messages[(lastEmailSequence - 1)]

		String emailSubject = message.subject

		Object content = message.content

		// Pastikan content adalah instance dari MimeMultipart
		if (content instanceof MimeMultipart) {
			MimeMultipart mimeMultipart = ((content) as MimeMultipart)

			StringBuilder bodyText = new StringBuilder()

			String textWithoutHtml

			// Loop melalui semua bagian dalam MimeMultipart
			for (int i = 0 ; i < mimeMultipart.count ; i++) {
				BodyPart bodyPart = mimeMultipart.getBodyPart(i)

				String contentType = bodyPart.contentType
				Document doc

				// Hanya ambil bagian teks dari bagian yang berisi teks
				if (contentType.startsWith('text/plain') || contentType.startsWith('text/html')) {
					String partText = ((bodyPart.content) as String)

					// Gunakan jsoup untuk menghilangkan elemen HTML
					doc = Jsoup.parse(partText)

					textWithoutHtml = doc.text()

					println(textWithoutHtml)

					break
				} else if (!(contentType.startsWith('text/plain') || contentType.startsWith('text/html'))) {
					InputStream is = bodyPart.inputStream
					BufferedReader br = new BufferedReader(new InputStreamReader(is))
					String line
					while ((line = br.readLine()) != null) {
						bodyText.append(line)
					}

					// Gunakan jsoup untuk menghilangkan elemen HTML
					doc = Jsoup.parse(bodyText.toString())

					textWithoutHtml = findText(doc.text())

					break
				}
			}

			otpCode = findOtpCode(textWithoutHtml.toString())

			println('OTP Code: ' + otpCode)
		}

		emailFolder.close(false)

		store.close() // Gunakan regular expression untuk mencari angka dalam teks

		if (value == 'Certif') {
			emailSubject
		} else if (value == 'OTP') {
			otpCode
		}
	}

	def findOtpCode(String text) {
		Pattern pattern = Pattern.compile('\\d+')

		Matcher matcher = pattern.matcher(text)

		if (matcher.find()) {
			return matcher.group()
		}

		'false'
	}

	def findText(String text) {
		Pattern pattern = Pattern.compile('Yth(.+?)Tim eSignHub')

		Matcher matcher = pattern.matcher(text)

		if (matcher.find()) {
			// Extract the matched text
			String extractedText = matcher.group(1).trim()
			String cleanedText = extractedText.replaceAll('[\\n]+', ' ')
			return extractedText
		} else {
			println('Match not found.')

			return 'false'
		}
	}
	
}

