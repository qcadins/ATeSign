import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
        } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'Yes') {
            'get tenant per case dari colm excel'
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Code'))
        }
        
        'get Psre'
        GlobalVariable.Psre = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Vendor Code'))

        'HIT API Login untuk ambil bearer token'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [
			('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('username')), 
			('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')

            String signerBody = '', documentFile = ''

            'Inisialisasi documentFile'
            if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Base64')) == 'Yes') {
                documentFile = CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('documentExample')))
            } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Base64')) == 'No') {
                documentFile = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentExample'))
            }
            
            'Inisialisasi signerTypeCode'
            signerTypeCode = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('signerTypeCode')).split(
                ';', -1)

            'Inisialisasi signTypeCode'
            signTypeCode = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('signTypeCode')).split(';', 
                -1)

            'Inisialisasi signPage'
            signPage = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('signPage')).split(';', -1)

            'Inisialisasi signLocation'
            signLocation = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('signLocation')).split('\\n', 
                -1)

            'Inisialisasi transform'
            transform = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('transform')).split('\\n', -1)

            'Inisialisasi position'
            position = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('position')).split('\\n', -1)

            'Inisialisasi positionVida'
            positionVida = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('positionVida')).split('\\n', 
                -1)

            'Inisialisasi positionPrivy'
            positionPrivy = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('positionPrivy')).split(
                '\\n', -1)

            'Inisialisasi seqNo'
            seqNo = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('seqNo')).split(';', -1)

            for (index = 1; index <= signerTypeCode.size(); index++) {
                if (index < signerTypeCode.size()) {
                    signerBody = (((((((((((((((((((signerBody + '{ "signerTypeCode": "') + (signerTypeCode[(index - 1)])) + 
                    '", "signTypeCode": "') + (signTypeCode[(index - 1)])) + '", "signPage": "') + (signPage[(index - 1)])) + 
                    '", "signLocation": ') + (signLocation[(index - 1)])) + ', "transform": "') + (transform[(index - 1)])) + 
                    '", "position": "') + (position[(index - 1)])) + '", "positionVida": "') + (positionVida[(index - 1)])) + 
                    '", "positionPrivy": "') + (positionPrivy[(index - 1)])) + '", "seqNo": "') + (seqNo[(index - 1)])) + 
                    '"},')
                } else if (index == signerTypeCode.size()) {
                    signerBody = (((((((((((((((((((signerBody + '{ "signerTypeCode": "') + (signerTypeCode[(index - 1)])) + 
                    '", "signTypeCode": "') + (signTypeCode[(index - 1)])) + '", "signPage": "') + (signPage[(index - 1)])) + 
                    '", "signLocation": ') + (signLocation[(index - 1)])) + ', "transform": "') + (transform[(index - 1)])) + 
                    '", "position": "') + (position[(index - 1)])) + '", "positionVida": "') + (positionVida[(index - 1)])) + 
                    '", "positionPrivy": "') + (positionPrivy[(index - 1)])) + '", "seqNo": "') + (seqNo[(index - 1)])) + 
                    '"}')
                }
            }
            
            'HIT API'
            respon = WS.sendRequest(findTestObject('Postman/Update Document Template', [('callerId') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('username')), ('documentTemplateCode') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('documentTemplateCode')), ('documentTemplateName') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateName')), ('documentTemplateDescription') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateDescription')), ('isActive') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('isActive')), ('isSequence') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('isSequence')), ('numberOfPage') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('numberOfPage')), ('paymentSignTypeCode') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('paymentSignTypeCode')), ('isSignLocOnly') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('isSignLocOnly')), ('documentExample') : documentFile
                        , ('signer') : signerBody]))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
                'mengambil status code berdasarkan response HIT API'
                statucCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

				'ambil lama waktu yang diperlukan hingga request menerima balikan'
				elapsedTime = (respon.elapsedTime / 1000) + ' second'
	
				'ambil body dari hasil respons'
				responseBody = respon.responseBodyContent
	
				'panggil keyword untuk proses beautify dari respon json yang didapat'
				CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
						excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
				'write to excel response elapsed time'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
					1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
				
                'jika status codenya 0'
                if (statucCode == 0) {
                    if (GlobalVariable.checkStoreDB == 'Yes') {
                        ArrayList resultDataSigner = CustomKeywords.'connection.PengaturanDokumen.getDocumentTemplateAPI'(
                            conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')))

                        ArrayList resultDataDocTemplate = CustomKeywords.'connection.PengaturanDokumen.getDocumentTemplateAPI2'(
                            conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')))

                        ArrayList arrayMatch = []

                        arrayIndex = 0

                        'verify documentTemplateCode'
                        arrayMatch.add(WebUI.verifyMatch(resultDataDocTemplate[arrayIndex++], findTestData(excelPath).getValue(
                                    GlobalVariable.NumofColm, rowExcel('documentTemplateCode')), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify documentTemplateName'
                        arrayMatch.add(WebUI.verifyMatch(resultDataDocTemplate[arrayIndex++], findTestData(excelPath).getValue(
                                    GlobalVariable.NumofColm, rowExcel('documentTemplateName')), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify documentTemplateDescription'
                        arrayMatch.add(WebUI.verifyMatch(resultDataDocTemplate[arrayIndex++], findTestData(excelPath).getValue(
                                    GlobalVariable.NumofColm, rowExcel('documentTemplateDescription')), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify isActive'
                        arrayMatch.add(WebUI.verifyMatch(resultDataDocTemplate[arrayIndex++], findTestData(excelPath).getValue(
                                    GlobalVariable.NumofColm, rowExcel('isActive')), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify paymentSignTypeCode'
                        arrayMatch.add(WebUI.verifyMatch(resultDataDocTemplate[arrayIndex++], findTestData(excelPath).getValue(
                                    GlobalVariable.NumofColm, rowExcel('paymentSignTypeCode')), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify isSequence'
                        arrayMatch.add(WebUI.verifyMatch(resultDataDocTemplate[arrayIndex++], findTestData(excelPath).getValue(
                                    GlobalVariable.NumofColm, rowExcel('isSequence')), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify Vendor Code'
                        arrayMatch.add(WebUI.verifyMatch(resultDataDocTemplate[arrayIndex++], findTestData(excelPath).getValue(
                                    GlobalVariable.NumofColm, rowExcel('Vendor Code')), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'reset arrayIndex'
                        arrayIndex = 0

                        for (index = 0; index < (resultDataSigner.size() / 9); index++) {
                            'verify signLocation'
                            arrayMatch.add(WebUI.verifyMatch(resultDataSigner[arrayIndex++], signLocation[index], false, 
                                    FailureHandling.CONTINUE_ON_FAILURE))

                            'verify signerTypeCode'
                            arrayMatch.add(WebUI.verifyMatch(resultDataSigner[arrayIndex++], signerTypeCode[index], false, 
                                    FailureHandling.CONTINUE_ON_FAILURE))

                            'verify signTypeCode'
                            arrayMatch.add(WebUI.verifyMatch(resultDataSigner[arrayIndex++], signTypeCode[index], false, 
                                    FailureHandling.CONTINUE_ON_FAILURE))

                            'verify signPage'
                            arrayMatch.add(WebUI.verifyMatch(resultDataSigner[arrayIndex++], signPage[index], false, FailureHandling.CONTINUE_ON_FAILURE))

                            'verify transform'
                            arrayMatch.add(WebUI.verifyMatch(resultDataSigner[arrayIndex++], transform[index], false, FailureHandling.CONTINUE_ON_FAILURE))

                            'verify position'
                            arrayMatch.add(WebUI.verifyMatch(resultDataSigner[arrayIndex++], position[index], false, FailureHandling.CONTINUE_ON_FAILURE))

                            'verify positionVida'
                            arrayMatch.add(WebUI.verifyMatch(resultDataSigner[arrayIndex++], positionVida[index], false, 
                                    FailureHandling.CONTINUE_ON_FAILURE))

                            'verify positionPrivy'
                            arrayMatch.add(WebUI.verifyMatch(resultDataSigner[arrayIndex++], positionPrivy[index], false, 
                                    FailureHandling.CONTINUE_ON_FAILURE))

                            'verify seqNo'
                            arrayMatch.add(WebUI.verifyMatch(resultDataSigner[arrayIndex++], seqNo[index], false, FailureHandling.CONTINUE_ON_FAILURE))
                        }
                        
                        if (arrayMatch.contains(false)) {
                            GlobalVariable.FlagFailed = 1

                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                        }
                    }
                    
                    'tulis sukses jika store DB berhasil'
                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                } else {
                    'call function get API error message'
                    getErrorMessageAPI(respon)
                }
            } else {
                'write to excel status failed dan reason : '
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
                        '') + ';') + GlobalVariable.ReasonFailedHitAPI)
            }
        } else {
            getErrorMessageAPI(responLogin)
        }
    }
}

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + message)) + 
        '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

