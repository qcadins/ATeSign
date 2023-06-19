import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPISentOTPSigning).columnNumbers

'looping API Sent OTP Signing'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 16) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 17)
        } else if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 16) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 14) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 14) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 15)
        }
        
        'mengambil reset otp request numbernya awal berapa'
        reset_otp_request_num = CustomKeywords.'connection.APIFullService.getResetCodeRequestNum'(conneSign, findTestData(excelPathAPISentOTPSigning).getValue(
                GlobalVariable.NumofColm, 11).replace('"', ''))

        'mengambil nilai otp awal baik berisi ataupun kosong'
        otp_code = CustomKeywords.'connection.DataVerif.getOTP'(conneSign, findTestData(excelPathAPISentOTPSigning).getValue(
                GlobalVariable.NumofColm, 11).replace('"', ''))

        'HIT API'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : findTestData(excelPathAPISentOTPSigning).getValue(
                        GlobalVariable.NumofColm, 9), ('phoneNo') : findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 
                        10), ('email') : findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 11)
                    , ('refnumber') : findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 12)]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

			'declare arraylist arraymatch'
			ArrayList<String> arrayMatch = []
			
            'jika codenya 0'
            if (code == 0) {
                'mengambil response trx nonya'
                trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

                'input di excel mengenai trxno yang telah didapat'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sent OTP Signing', 
                    5, GlobalVariable.NumofColm - 1, trxNo.toString())

                'check Db'
                if (GlobalVariable.checkStoreDB == 'Yes') {
                    arrayIndex = 0

                    'get data from db'
                    ArrayList<String> result = CustomKeywords.'connection.APIFullService.checkAPISentOTPSigning'(conneSign, findTestData(
                            excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 6))

                    'verify trxno'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISentOTPSigning).getValue(
                                GlobalVariable.NumofColm, 6), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify email'
                    arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]).toUpperCase(), findTestData(excelPathAPISentOTPSigning).getValue(
                                GlobalVariable.NumofColm, 11).replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify trx qty = -1'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify ref number'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISentOTPSigning).getValue(
                                GlobalVariable.NumofColm, 12).replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify no telp'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISentOTPSigning).getValue(
                                GlobalVariable.NumofColm, 10).replace('"', '') + ' : Send OTP SMS', false, FailureHandling.CONTINUE_ON_FAILURE))

                    newOTP = (result[arrayIndex++])

                    'verify otp code tidak sama'
                    arrayMatch.add(WebUI.verifyNotEqual(newOTP, otp_code, FailureHandling.CONTINUE_ON_FAILURE))

                    'input di excel mengenai trxno yang telah didapat'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sent OTP Signing', 
                        6, GlobalVariable.NumofColm - 1, newOTP)

                    'verify reset otp request number '
                    arrayMatch.add(WebUI.verifyEqual(result[arrayIndex++], Integer.parseInt(reset_otp_request_num) + 1, 
                            FailureHandling.CONTINUE_ON_FAILURE))

                    'verify api key'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.api_key, false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify tenant'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.Tenant, false, FailureHandling.CONTINUE_ON_FAILURE))

                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sent OTP Signing', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    } else {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sent OTP Signing', 
                            0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                } else {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sent OTP Signing', 
                        0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sent OTP Signing', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, message)
				
				String result = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(
					excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 6))
				
				'check Db'
				if (GlobalVariable.checkStoreDB == 'Yes') {
					'verify no telp'
					arrayMatch.add(WebUI.verifyEqual(result, null, FailureHandling.CONTINUE_ON_FAILURE))
					
					if (arrayMatch.contains(false)) {
						'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sent OTP Signing', GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm,
								2) + ';') + GlobalVariable.ReasonFailedStoredDB)
					}
				}
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sent OTP Signing', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, message)
        }
    }
}

