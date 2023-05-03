import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import java.sql.Connection as Connection
import org.apache.commons.io.FileUtils as FileUtils
import java.net.InetAddress

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPISignDocument).getColumnNumbers()

'looping API Sign Document'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel + 2; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else 
		
		ArrayList<String> documentId = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 9).replace('[','').replace(']','').split(',',-1)
		
		'check if mau menggunakan api_key yang salah atau benar'
		if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 16) == 'Yes') {
			'get api key dari db'
			GlobalVariable.api_key = CustomKeywords.'connection.dataVerif.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
		} else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 16) == 'No') {
			'get api key salah dari excel'
			GlobalVariable.api_key = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 17)
		}
		
		if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 18) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 19)
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 18) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
		'check if mau menggunakan OTP yang salah atau benar'
		if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 20) == 'Yes') {
			'request OTP'
			'HIT API'
			'Constraint = untuk user USERCIIE@AD-INS.COM dengan nomor telefon 081233444403. Hash no telp jika dari db'
			respon_OTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : "",
				 ('phoneNo') : '"081233444403"', ('email') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 10)
					, ('refnumber') : CustomKeywords.'connection.dataVerif.getRefNumber'(conneSign, documentId[i])]))
	
			
			
			'get OTP dari db'
			otp = CustomKeywords.'connection.dataVerif.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
		} else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 16) == 'No') {
			'get api key salah dari excel'
			GlobalVariable.api_key = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 17)
		}
		

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
			
			'jika codenya 0'
            if (code == 0) {
                'mengambil response trx nonya'
                trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)
				
				'input di excel mengenai trxno yang telah didapat'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sent OTP Signing', 
                    4, GlobalVariable.NumofColm - 1, trxNo.toString())
				
				'check Db'
                if (GlobalVariable.checkStoreDB == 'Yes') {
                    arrayIndex = 0

                    'declare arraylist arraymatch'
                    ArrayList<String> arrayMatch = new ArrayList<String>()

                    'get data from db'
                    ArrayList<String> result = CustomKeywords.'connection.dataVerif.checkAPISentOTPSigning'(conneSign, findTestData(
                            excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 5))

                    'verify trxno'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(
                                GlobalVariable.NumofColm, 5),false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify email'
                    arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]).toUpperCase(), findTestData(excelPathAPISignDocument).getValue(
                                GlobalVariable.NumofColm, 11).replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify trx qty = -1'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify ref number'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(
                                GlobalVariable.NumofColm, 12).replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify no telp'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(
                                GlobalVariable.NumofColm, 10).replace('"', '') + ' : Send OTP SMS', false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify otp code tidak sama'
                    arrayMatch.add(WebUI.verifyNotEqual(result[arrayIndex++], otp_code, FailureHandling.CONTINUE_ON_FAILURE))

					'verify reset otp request number '
					arrayMatch.add(WebUI.verifyEqual(result[arrayIndex++], Integer.parseInt(reset_otp_request_num) + 1, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify api key'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.api_key, false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify tenant'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.Tenant, false, FailureHandling.CONTINUE_ON_FAILURE))

                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sent OTP Signing', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    }
					else {
						'write to excel success'
						CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sent OTP Signing',
							0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
					}
                } else {
                    'write to excel success'
                    CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sent OTP Signing', 
                        0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                }
            }
			else {
				'mengambil status code berdasarkan response HIT API'
				message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)
	
				'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
				CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sent OTP Signing', GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, message)
			}
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sent OTP Signing', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, message)
        }
    }
}
