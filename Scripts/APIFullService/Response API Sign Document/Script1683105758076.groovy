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
import java.net.InetAddress as InetAddress

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.writeExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.connectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPISignDocument).getColumnNumbers()

'looping API Sign Document'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break

    } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        String otp

        String Photo

        String ipaddress
		
		totalSigned_before = ''
		
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 18) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 19)
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 18) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 16) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.dataVerif.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 16) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 17)
        }
        
        'check if mau menggunakan OTP yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 20) == 'Yes') {
            'request OTP'

            'HIT API'

            'Constraint = untuk user USERCIIE@AD-INS.COM dengan nomor telefon 081233444403. Hash no telp jika dari db'
            respon_OTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : '""', ('phoneNo') : '"081233444403"'
                        , ('email') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 10), ('refnumber') : '""']))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(respon_OTP, 200, FailureHandling.OPTIONAL) == true) {
                'get status code'
                code_otp = WS.getElementPropertyValue(respon_OTP, 'status.code', FailureHandling.OPTIONAL)

                'jika codenya 0'
                if (code_otp == 0) {
                    'Dikasih delay 1 detik dikarenakan loading untuk mendapatkan OTP.'
                    WebUI.delay(1)

                    'Mengambil otp dari database'
                    otp = (('"' + CustomKeywords.'connection.dataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPISignDocument).getValue(
                            GlobalVariable.NumofColm, 10).replace('"', ''))) + '"')
                } else {
                    'mengambil status code berdasarkan response HIT API'
                    message = WS.getElementPropertyValue(respon_OTP, 'status.message', FailureHandling.OPTIONAL)

                    'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                    CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, message)
                }
            } else {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.HITAPI Gagal'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        2) + ';') + GlobalVariable.ReasonFailedHitAPI)
            }
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 20) == 'No') {
            'get otp dari excel'
            otp = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 21)
        }
        
        'check if mau menggunakan base64 untuk photo yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 22) == 'Yes') {
            'get base64 photo dari fungsi'
            Photo = (('"' + PhototoBase64(findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 14))) + 
            '"')
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 22) == 'No') {
            'get base64 photo salah dari excel'
            Photo = (('"' + findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 14)) + '"')
        }
        
        'check if mau menggunakan ip address yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 23) == 'Yes') {
            'get ip address dari fungsi'
            ipaddress = (('"' + CorrectipAddress()) + '"')
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 23) == 'No') {
            'get ip address salah dari excel'
            ipaddress = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 12)
        }
        
        ArrayList<String> documentId = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 9).replace(
            '[', '').replace(']', '').replace('"','').split(',', -1)

        for (int z = 0; z < documentId.size(); z++) {
           
			if(z == 0) {
				totalSigned_before = totalSigned_before + (CustomKeywords.'connection.dataVerif.getTotalSigned'(conneSign, documentId[z]) + ',')
			}
			if(z == documentId.size() - 1) 
				{
				totalSigned_before = totalSigned_before + (CustomKeywords.'connection.dataVerif.getTotalSigned'(conneSign, documentId[z]))
				}
			}

		println totalSigned_before
		
        'HIT API Sign'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Sign Document', [('callerId') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, 25), ('documentId') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, 9), ('email') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        10), ('password') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 11)
                    , ('ipAddress') : ipaddress, ('browserInfo') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        13), ('otp') : otp, ('selfPhoto') : Photo]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
			
			'get status code'
			trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)
			if(trxNo != null) {
				CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sign Document',
				4, GlobalVariable.NumofColm - 1, trxNo.toString().replace('[','').replace(']',''))
			}
			
            
            'jika codenya 0'
            if (code == 0) {
				
				println documentId.size()
				println documentId
				for (int x = 0; x < documentId.size(); x++) {
					
					for (int v = 1; v <= 20; v++) {
						'Mengambil total Signed setelah sign'
						totalSigned_after = CustomKeywords.'connection.dataVerif.getTotalSigned'(conneSign, documentId[x])
		
						'Verify total signed sebelum dan sesudah'
						if (WebUI.verifyEqual(totalSigned_after, Integer.parseInt(totalSigned_before.split(
									',', -1)[x]) + 1, FailureHandling.CONTINUE_ON_FAILURE)) {
							break
						} else if (v == 20) {
							'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
							CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm,
								GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 2) +
									';') + GlobalVariable.ReasonFailedSignGagal)
							}
						else {
							WebUI.delay(10)
						}
					}
				}
                'write to excel success'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sign Document', 
                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                'check Db'
                if (GlobalVariable.checkStoreDB == 'Yes') {
                    ResponseAPIStoreDB(conneSign, totalSigned_before,ipaddress)
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, message)
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
            CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, message)
        }
    }
}

def CorrectipAddress() {
    String ipAddress = InetAddress.getLocalHost().getHostAddress()

    return ipAddress
}

def PhototoBase64(String filePath) {
    return CustomKeywords.'customizeKeyword.convertFile.BASE64File'(filePath)
}

def ResponseAPIStoreDB(Connection conneSign, String totalSigned_before, String ipaddress) {
	'get current date'
	def currentDate = new Date().format('yyyy-MM-dd')

    ArrayList<String> documentId = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 9).replace(
        '[', '').replace(']', '').split(',', -1)

    'declare arraylist arraymatch'
    ArrayList<String> arrayMatch = new ArrayList<String>()

    for (int i = 0; i < documentId.size(); i++) {
        'get data from db'
		arrayIndex = 0
		
        ArrayList<String> result = CustomKeywords.'connection.dataVerif.getSign'(conneSign, documentId[i].replace('"',''), findTestData(
                excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 10).replace('"', ''))

        'verify qty dalam transaksi. Jika done = 1'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

		if(findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 5) != '') {
			'verify trx no'
			arrayMatch.add(WebUI.verifyEqual((result[arrayIndex++]), findTestData(excelPathAPISignDocument).getValue(
			GlobalVariable.NumofColm, 6), FailureHandling.CONTINUE_ON_FAILURE))
		} else {
			arrayIndex++
		}
		
        'verify request status. 3 = done'
        arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]), '3', false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify ref number yang tertandatangan'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], CustomKeywords.'connection.dataVerif.getRefNumber'(conneSign, documentId[i].replace('"','')), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify ip address'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], ipaddress.replace('"',''), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify user browser'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        13).replace('"',''), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify callerId'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++],  findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        25).replace('"',''), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify signing proces. 0 berarti tidak ada proses tanda tangan lagi.'
        arrayMatch.add(WebUI.verifyEqual(result[arrayIndex++], 0, FailureHandling.CONTINUE_ON_FAILURE))

        'verify tanggal tanda tangan'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], currentDate, false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'verify api key'
		arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.api_key, false, FailureHandling.CONTINUE_ON_FAILURE))
 
        'verify tenant'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.Tenant, false, FailureHandling.CONTINUE_ON_FAILURE))
    }
    
    'jika data db tidak sesuai dengan excel'
    if (arrayMatch.contains(false)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizeKeyword.writeExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedStoredDB)
    }
}

