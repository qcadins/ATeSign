import com.kms.katalon.core.model.FailureHandling as FailureHandling
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.main.CustomKeywordDelegatingMetaClass as CustomKeywordDelegatingMetaClass
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.sql.Connection as Connection
import java.sql.Statement as Statement
import java.sql.DriverManager as DriverManager
import java.sql.ResultSet as ResultSet
import java.sql.ResultSetMetaData as ResultSetMetaData
import org.openqa.selenium.By as By
import org.openqa.selenium.support.ui.Select as Select
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory

GlobalVariable.Response = 'API Sign Document'

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).getColumnNumbers(); (GlobalVariable.NumofColm)++) {
    'HIT API Login untuk token : andy@ad-ins.com'
    respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData('Login/Login').getValue(2, 
                    3), ('password') : findTestData('Login/Login').getValue(3, 3)]))

    'Jika status HIT API Login 200 OK'
    if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL) == true) {
        'Parsing token menjadi GlobalVariable'
        GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')

        'HIT API Sign Document'
        respon_signdoc = WS.sendRequest(findTestObject('Postman/Sign Doc', [('callerId') : ('"' + findTestData(API_Excel_Path).getValue(
                        GlobalVariable.NumofColm, 9)) + '"', ('email') : ('"' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                        11)) + '"', ('documentId') : ('"' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                        12)) + '"', ('msg') : ('"' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13)) + 
                    '"']))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon_signdoc, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            status_Code = WS.getElementPropertyValue(respon_signdoc, 'status.code')

            'Jika status codenya 0'
            if (status_Code == 0) {
                'Get link'
                url = WS.getElementPropertyValue(respon_signdoc, 'url')

                CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sign Document', 
                    4, GlobalVariable.NumofColm - 1, url)

                'Open signLink buat check apakah dokumen yang ada disana dengan documentId excel sama'
                WebUI.openBrowser('')

                'navigate to url esign'
                WebUI.navigateToUrl(url)

                'fungsi select untuk yang mengarah ke element document pertama'
                select = WebUI.getAttribute(findTestObject('Object Repository/SignDocument/Nav Doc'), 'text')
				
				'Jika get text terdapat documentId (pengecekan sama atau tidak)'
                if (select.contains(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12)) == true) {
                    
					'Web di Maximize'
					WebUI.maximizeWindow()

					'Scroll menuju clickSign (Kotak Merah)'
                    WebUI.scrollToElement(findTestObject('Object Repository/SignDocument/btn_clickSign'), GlobalVariable.TimeOut)
					
					'Klik kotak Merah'
                    WebUI.click(findTestObject('Object Repository/SignDocument/btn_clickSign'))

					'Verify tanda tangannya ada setelah diclick'
                    WebUI.verifyElementPresent(findTestObject('Object Repository/SignDocument/NavSign'), GlobalVariable.TimeOut)

					'Klik total Page'
                    WebUI.click(findTestObject('Object Repository/SignDocument/btn_totalPage'))

					'Klik proses di pop up total page'
                    WebUI.click(findTestObject('Object Repository/SignDocument/btn_runtotalPage'))

					'Klik pdf next'
                    WebUI.click(findTestObject('Object Repository/SignDocument/btn_pdfnext'))

					'Klik pdf prev'
                    WebUI.click(findTestObject('Object Repository/SignDocument/btn_pdfprev'))

					'Klik zoom in'
                    WebUI.click(findTestObject('Object Repository/SignDocument/btn_zoomin'))

					'Klik zoom out'
                    WebUI.click(findTestObject('Object Repository/SignDocument/btn_zoomout'))

					'Klik btn proses'
                    WebUI.click(findTestObject('Object Repository/SignDocument/btn_Proses'))

                    'Get vendorCode'
                    GlobalVariable.Response = WS.getElementPropertyValue(respon_signdoc, 'vendorCode')

                    'Memanggil testCase mengenai SignDocumentStoreDb'
                    WebUI.callTestCase(findTestCase('Sign_Document/SignDocumentStoreDb'), [('API_Excel_Path') : 'Registrasi/SignDocument'], 
                        FailureHandling.CONTINUE_ON_FAILURE)

                    'write to excel success'
                    CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sign Document', 
                     0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
					
					'Tutup browser'
                    WebUI.closeBrowser()
				'Jika tidak ada, maka '
                } else {
                    'write to excel status failed dan reason : verify tidak equal'
                    CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                      '-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)
                }
            } else {
				'write to excel status failed dan reason : message Failed dari response'
                messageFailed = WS.getElementPropertyValue(respon_signdoc, 'status.message', FailureHandling.OPTIONAL).toString()
                'write to excel status failed dan reason'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                        '-', '') + ';') + messageFailed)
            }
        } else {
			
            'write to excel status failed dan reason : failed hit api'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedHitAPI)
			
			'call test case login inveditor'
			WebUI.callTestCase(findTestCase('Login/Login_Inveditor'), [:], FailureHandling.STOP_ON_FAILURE)
			
			'call test case error report'
			WebUI.callTestCase(findTestCase('Sign_Document/ErrorReport'), [('excelPathSignDoc') : 'Registrasi/BulkSignDocument'], FailureHandling.STOP_ON_FAILURE)
			
			'close browser'
			WebUI.closeBrowser()
        }
    }
}

