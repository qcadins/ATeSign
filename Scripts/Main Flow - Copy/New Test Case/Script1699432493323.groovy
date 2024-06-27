import groovy.json.JsonSlurper as JsonSlurper
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.util.Collections

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

// Ensure GlobalVariable.eSignData['allTrxNo'] has been defined and contains the value you want to manipulate
GlobalVariable.eSignData['allTrxNo'] = "844111;"

signtypes = (GlobalVariable.eSignData['allTrxNo']).toString().split(';', -1)
println signtypes

signtypes = signtypes.findAll { it.trim() }

println signtypes
println signtypes[0]

WebUI.delay(1000000)

Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()


otp = CustomKeywords.'customizekeyword.GetSMS.getOTP'('eSignHub')
println otp
otp1 = CustomKeywords.'customizekeyword.GetSMS.getOTP'('PrivyID')
println otp1
otp2 = CustomKeywords.'customizekeyword.GetSMS.getOTP'('DIGISIGN')
println otp2

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

