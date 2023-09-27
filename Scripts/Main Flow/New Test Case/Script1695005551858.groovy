import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import java.sql.Connection as Connection

//
//'check ada value maka Setting OTP Active Duration'
//if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 0) {
//	'Setting OTP Active Duration'
//	CustomKeywords.'connection.APIFullService.settingOTPActiveDuration'(conneSign, findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')))
//	
//	delayExpiredOTP = 60 * Integer.parseInt(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')))
//}
//
//
//
//
//'bikin flag untuk dilakukan OTP by db'
//if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Correct OTP (Yes/No)')).split(';', -1)[GlobalVariable.indexUsed] == 'Yes') {
//	'value OTP dari db'
//	WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), OTP)
//} else {
//	if (vendor.equalsIgnoreCase('Privy')) {
//		'check if ingin testing expired otp'
//		if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 0) {
//			'delay untuk input expired otp'
//			delayExpiredOTP = delayExpiredOTP + 30
//			
//			WebUI.delay(delayExpiredOTP)
//		} else {
//		'untuk input manual otp'
//		WebUI.delay(60)
//		}
//	}
//	
//	'value OTP dari excel'
//	WebUI.setText(findTestObject('KotakMasuk/Sign/input_OTP'), findTestData(excelPathFESignDocument).getValue(
//	GlobalVariable.NumofColm, rowExcel('Manual OTP')).split(';', -1)[GlobalVariable.indexUsed])
//}
//
//if (!vendor.equalsIgnoreCase('Privy')) {
//	'check if ingin testing expired otp'
//		if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 0) {
//		'delay untuk input expired otp'
//		delayExpiredOTP = delayExpiredOTP + 30
//		
//		WebUI.delay(delayExpiredOTP)
//	}
//}
//
//'klik verifikasi OTP'
//WebUI.click(findTestObject('KotakMasuk/Sign/btn_ProsesOTP'))
//
//
//
//
//
//'Resend OTP'
//if (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Resend OTP (Yes/No)')).split(';', -1)[GlobalVariable.indexUsed] == 'Yes') {
//	'Ambil data dari excel mengenai countResend'
//	countResend = findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('CountResendOTP')).split(';', -1)[GlobalVariable.indexUsed].toInteger()
//
//	'Looping dari 1 hingga total count resend OTP'
//	for ( w = 1; w <= countResend; w++) {
//		if (delayExpiredOTP <= 298) {
//			'taruh waktu delay'
//			WebUI.delay(298 - delayExpiredOTP)
//		}
//		'Klik resend otp'
//		WebUI.click(findTestObject('KotakMasuk/Sign/btn_ResendOTP'))
//
//		'checkErrorLog'
//		checkErrorLog()
//
//		'check pop up'
//		checkPopup()

batas = 3
for (i = 0 ; i <= batas ; i++) {
	println i
	println batas
	if (i == 2) {
		i = 0
		batas--
	}	
	println i
	println batas

}

println batas