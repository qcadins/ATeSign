import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import java.sql.Connection as Connection
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
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Check Registration', [('callerId') : findTestData(
                        excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, 9), ('dataType') : findTestData(
                        excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, 11), ('dataValue') : findTestData(
                        excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, 12)]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
			
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                'mengambil response'
                vendor = WS.getElementPropertyValue(respon, 'registrationData.vendor', FailureHandling.OPTIONAL)

                vendoractive = WS.getElementPropertyValue(respon, 'registrationData.registrationStatus', FailureHandling.OPTIONAL)

                if (GlobalVariable.checkStoreDB == 'Yes') {
                    arrayIndex = 0

                    'get data from db'
                    ArrayList<String> result = CustomKeywords.'connection.dataVerif.getAPICheckRegisterStoreDB'(conneSign, 
                        findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, 12).replace('"', ''))

                    'declare arraylist arraymatch'
                    ArrayList<String> arrayMatch = new ArrayList<String>()

                    for (index = 0; index < vendor.size(); index++) {
                        if ((vendoractive[index]) != '0') {
                            'verify vendor'
                            arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]).toUpperCase(), (vendor[index]).toUpperCase(), 
                                    false, FailureHandling.CONTINUE_ON_FAILURE))

                            'verify vendor status'
                            arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]).toUpperCase(), (vendoractive[index]).toUpperCase(), 
                                    false, FailureHandling.CONTINUE_ON_FAILURE))
                        }
                    }
                    
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Check Registrasi', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathIsiSaldo).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    } else {
                        'write to excel success'
                        CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Check Registrasi', 
                            0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Check Registrasi', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, message)
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Check Registrasi', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, message)
        }
    }
}