import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import java.sql.Connection

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

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPICheckRegistrasi).getColumnNumbers()

'looping API Registrasi'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, 14) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.dataVerif.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, 14) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, 15)
        }
		
	        'HIT API check registrasi'
	       respon = WS.sendRequest(findTestObject('APIFullService/Postman/Check Registration', [('callerId') : findTestData(excelPathAPICheckRegistrasi).getValue(
						GlobalVariable.NumofColm, 9), ('dataType') : findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm,
						11), ('dataValue') : findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm,
						12)])) 
		   
		   'Jika status HIT API 200 OK'
		   if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
					'mengambil status code berdasarkan response HIT API'
					vendor = WS.getElementPropertyValue(respon, 'registrationData.vendor', FailureHandling.OPTIONAL)
					vendoractive = WS.getElementPropertyValue(respon, 'registrationData.registrationStatus', FailureHandling.OPTIONAL)
		  
					println(vendor)
					println(vendoractive)
		  }else {
			  'mengambil status code berdasarkan response HIT API'
			  message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)
			  
			  println(message)
			  'Write To Excel GlobalVariable.StatusFailed and errormessage'
			  CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Check Registrasi', GlobalVariable.NumofColm,
					  GlobalVariable.StatusFailed, message)
		  }
    }
}


