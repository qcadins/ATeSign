package customizekeyword

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

import javax.mail.BodyPart as BodyPart
import javax.mail.Folder as Folder
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.NoSuchProviderException
import javax.mail.Session as Session
import javax.mail.Store as Store
import javax.mail.internet.MimeMultipart as MimeMultipart
import org.jsoup.Jsoup as Jsoup
import org.jsoup.nodes.Document as Document
import java.util.regex.Matcher as Matcher
import java.util.regex.Pattern as Pattern

import internal.GlobalVariable

public class GetEmail {

	@Keyword
	getEmailContent(String email, String inputPassword) {
		String host = 'outlook.office365.com'

		String username = email

		String password = inputPassword

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
		Message[] messages = emailFolder.getMessages()

		System.out.println('messages.length---' + messages.length)

		int lastEmailSequence = messages.length

		Message message = messages[(lastEmailSequence - 1)]

		String emailSubject = message.getSubject()
		
		System.out.println('Email Number ' + lastEmailSequence)

		System.out.println('Subject: ' + emailSubject)
		
		System.out.println('Subject: ' + message.getContent().toString())

		System.out.println('From: ' + (message.getFrom()[0]))

		Object content = message.getContent()

		// Pastikan content adalah instance dari MimeMultipart
		if (content instanceof MimeMultipart) {
			MimeMultipart mimeMultipart = ((content) as MimeMultipart)

			StringBuilder bodyText = new StringBuilder()

			String textWithoutHtml

			// Loop melalui semua bagian dalam MimeMultipart
			for (int i = 0; i < mimeMultipart.getCount(); i++) {
				BodyPart bodyPart = mimeMultipart.getBodyPart(i)

				String contentType = bodyPart.getContentType()

				// Hanya ambil bagian teks dari bagian yang berisi teks
				if (contentType.startsWith('text/plain') || contentType.startsWith('text/html')) {
					String partText = ((bodyPart.getContent()) as String)

					// Gunakan jsoup untuk menghilangkan elemen HTML
					Document doc = Jsoup.parse(partText)

					textWithoutHtml = doc.text()

					//bodyText.append(textWithoutHtml);
					System.out.println(textWithoutHtml)
				}
			}

			String otpCode = findOtpCode(textWithoutHtml.toString())

			System.out.println('OTP Code: ' + otpCode)
		}

		emailFolder.close(false)

		store.close() // Gunakan regular expression untuk mencari angka dalam teks

		emailSubject
	}

	def findOtpCode(String text) {
		Pattern pattern = Pattern.compile('\\d+')

		Matcher matcher = pattern.matcher(text)

		if (matcher.find()) {
			return matcher.group()
		}

		return ''
	}
}
