import groovy.json.JsonSlurper
import internal.GlobalVariable
import java.sql.Connection as Connection
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign Main.xlsx')
sheet = 'Main'
println(rowExcel('reason Failed'))
println(rowExcel('Cancel Docs after Stamp?'))
//'connect DB eSign'
//Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()
//
//GlobalVariable.eSignData['VerifikasiOTP'] = 0
//
//GlobalVariable.eSignData['VerifikasiOTP'] = 1
//
//'menggunakan saldo wa'
//balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'OTP', 'USERCIIE', (GlobalVariable.eSignData.getAt('VerifikasiOTP')))
//
//println balmut
//WebUI.Delay(100000000)
<<<<<<< HEAD
otp = CustomKeywords.'customizekeyword.GetSMS.getOTP'('eSignHub')
println otp
if (otp.find(/\d/)) {
	println 'ada angka'
	println otp
} else {
	println 'tidak ada angka'
	println otp
}

otp1 = CustomKeywords.'customizekeyword.GetSMS.getOTP'('PrivyID')
println otp1
if (otp1.find(/\d/)) {
	println 'ada angka'
	println otp1
} else {
	println 'tidak ada angka'
	println otp1
}
otp2 = CustomKeywords.'customizekeyword.GetSMS.getOTP'('DIGISIGN')
println otp2
if (otp2.find(/\d/)) {
	println 'ada angka'
	println otp2
} else {
	println 'tidak ada angka'
	println otp2
}

HashMap aa = []

aa['a'] = 'bbb'
println aa.keySet()[0]

WebUI.delay(5.25)
sheet = 'Main'
GlobalVariable.NumofColm = 4

=======
//otp = CustomKeywords.'customizekeyword.GetSMS.getOTP'('eSignHub')
//println otp
//if (otp.find(/\d/)) {
//	println 'ada angka'
//	println otp
//} else {
//	println 'tidak ada angka'
//	println otp
//}
//
//otp1 = CustomKeywords.'customizekeyword.GetSMS.getOTP'('PrivyID')
//println otp1
//if (otp1.find(/\d/)) {
//	println 'ada angka'
//	println otp1
//} else {
//	println 'tidak ada angka'
//	println otp1
//}
//otp2 = CustomKeywords.'customizekeyword.GetSMS.getOTP'('DIGISIGN')
//println otp2
//if (otp2.find(/\d/)) {
//	println 'ada angka'
//	println otp2
//} else {
//	println 'tidak ada angka'
//	println otp2
//}
//
//sheet = 'Main'
//GlobalVariable.NumofColm = 4
//CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'Main', rowExcel('documentid') - 1, GlobalVariable.NumofColm - 1, '')
//
>>>>>>> branch 'master' of https://github.com/qcadins/ATeSign
def rowExcel(String cellValue) {
	CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}
//
////'get data file path'
////GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')
////
////
////ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getOfficeRegionBlineCodeUsingRefNum'(conneSign, 'TEST DOCUMENT 3')
////
////println officeRegionBline
////println officeRegionBline.size()
//
//
//CustomKeywords.'customizekeyword.ConvertFile.decodeBase64Extension'('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',
//	 'IL_REPORT 17-12-2023-21-13-13.xlsx')