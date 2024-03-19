import groovy.json.JsonSlurper as JsonSlurper
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

//import com.twilio.Twilio
//import com.twilio.rest.api.v2010.account.Message
//import com.twilio.type.PhoneNumber
//import com.twilio.base.ResourceSet
'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign Main.xlsx')

sheet = 'Main'

'connect DB eSign'
GlobalVariable.NumofColm = 4

excelPathFESignDocument = 'Main/Main'

Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

int aa = 75

int bb = aa / 60

println aa / 60
println bb

WebUI.delay(bb - aa)
println bb.getClass()


WebUI.delay(1000000) //*[@id="searchForm"]/div[1]/div[10]/app-question/app-select/div/div[2]/ng-dropdown-panel
//
//aa = WebUI.getAttribute(findTestObject('View User OTP/text_otp'), 'value')
//println aa
//
//WebUI.delay(19000000)
//@Grab(group='com.twilio.sdk', module='twilio', version='7.55.0')
//
//accountSid = 'AC67480a3610517f15d6f3179268d80111'
//authToken = '1a5e0ca394243f7454befb7fc84e5343'
//
//// Initialize Twilio client
//Twilio.init(accountSid, authToken)
//
//
//// Retrieve messages using Twilio API
//ResourceSet<Message> messages = Message.reader('').limit(20).read()
//
//println messages
//// Print the body of each message
//messages.each { message ->
//    println(message.getBody())
//}
//otp = CustomKeywords.'customizekeyword.GetSMS.getOTP'('eSignHub')
//println otp
//otp1 = CustomKeywords.'customizekeyword.GetSMS.getOTP'('PrivyID')
//println otp1
//otp2 = CustomKeywords.'customizekeyword.GetSMS.getOTP'('DIGISIGN')
//println otp2

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

