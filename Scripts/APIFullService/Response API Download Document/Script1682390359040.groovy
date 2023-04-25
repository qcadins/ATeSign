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
int countColmExcel = findTestData(excelPathAPIDownload).getColumnNumbers()

'looping API Status Signing'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break //12
    } else if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, 15) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, 16)
        } else if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, 15) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, 13) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.dataVerif.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, 13) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, 14)
        }
        
        'HIT API check Status Signing'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Download Document', [('callerId') : findTestData(
                        excelPathAPIDownload).getValue(GlobalVariable.NumofColm, 9), ('documentId') : findTestData(excelPathAPIDownload).getValue(
                        GlobalVariable.NumofColm, 11)]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                'mengambil response'
                base64PDF = WS.getElementPropertyValue(respon, 'pdfBase64', FailureHandling.OPTIONAL)

				'decode Bas64 to File PDF'
                CustomKeywords.'customizeKeyword.convertFile.DecodeBase64'(base64PDF, findTestData(excelPathAPIDownload).getValue(
                        GlobalVariable.NumofColm, 18))
				
				'check is file downloaded dan apakah mau di delete'
                if (CustomKeywords.'customizeKeyword.Download.isFileDownloaded'(findTestData(excelPathAPIDownload).getValue(
                        GlobalVariable.NumofColm, 17)) == true) {
                    'write to excel success'
                    CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Download Document', 
                        0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Download Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, message)
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Download Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, message)
        }
    }
}



