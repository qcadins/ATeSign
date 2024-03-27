import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.DataVerif.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))
		
        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/getAvailableSendingOptionsInvitation', [('msg') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('msg'))]))

        'ambil lama waktu yang diperlukan hingga request menerima balikan'
        elapsedTime = ((respon.elapsedTime / 1000) + ' second')

        'ambil body dari hasil respons'
        responseBody = respon.responseBodyContent

        'panggil keyword untuk proses beautify dari respon json yang didapat'
        CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

        'write to excel response elapsed time'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
            1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            statusCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'Jika status codenya 0'
            if (statusCode == 0) {
                if (GlobalVariable.checkStoreDB == 'Yes') {
                    'get defaultAvailableOptionSendingPoint'
                    defaultAvailableOptionSendingPoint = WS.getElementPropertyValue(respon, 'defaultAvailableOptionSendingPoint', 
                        FailureHandling.OPTIONAL)
					
					'get defaultAvailableOptionSendingPoint'
					listAvailableOptionSendingPoint = WS.getElementPropertyValue(respon, 'listAvailableOptionSendingPoint',
						FailureHandling.OPTIONAL)
					
					'get aes key dari general setting'
					String getAesKey = CustomKeywords.'connection.APIFullService.getAesKeyEncryptUrl'(conneSign)
					
					'get parse decrypt dari inputan'
					String parseDecryptMsg = CustomKeywords.'customizekeyword.ParseText.parseDecrypt'(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('msg')), getAesKey)
					
					'get tenant code dari invitation link'
					String tenantCode = CustomKeywords.'connection.DataVerif.getTenantCodeBasedOnInvitationLink'(conneSign, parseDecryptMsg)
					
					'get result list available by db'
					ArrayList resultListAvailable = CustomKeywords.'connection.APIOnly.listAvailableOptionSendingPointGetAvailSendingPointInv'(conneSign, tenantCode)
					
					'get data balance type dari DB'
					ArrayList result = CustomKeywords.'connection.GetUserActivationStatusResetPassword.getGetUserActivationStatusResetPasswordStoreDB'(
						conneSign, tenantCode)
					
                    'declare arraylist arraymatch'
                    ArrayList arrayMatch = []

                    'declare arrayindex'
                    arrayIndex = 0
					
					'verify list available option sending point'
					arrayMatch.add(WebUI.verifyMatch(resultListAvailable.toString(), listAvailableOptionSendingPoint.toString(), false, FailureHandling.CONTINUE_ON_FAILURE))

					'jika resultnya bukan SMS dan WA, akan otomatis ke SMS'
					if (result[0] != 'SMS' && result[0] != 'WA') {
						result[0] = 'SMS'
					}
					
                    'verify default otp'
                    arrayMatch.add(WebUI.verifyMatch(result[0], defaultAvailableOptionSendingPoint, false, FailureHandling.CONTINUE_ON_FAILURE))

                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        GlobalVariable.FlagFailed = 1

                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    }
                }
                
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            getErrorMessageAPI(respon)
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

def getNotifType(Connection conneSign, String emailSigner) {
    emailServiceOnVendor = CustomKeywords.'connection.DataVerif.getEmailServiceAsVendorUser'(conneSign, emailSigner)

    fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, emailSigner)

    mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

    String notifTypeDB = CustomKeywords.'connection.APIFullService.getWASMSFromNotificationType'(conneSign, emailSigner, 'RESET_PASSWORD', 
        GlobalVariable.Tenant)
	
    if (notifTypeDB == 'Email') {
        'EMAIL'
    } 
    else if ((notifTypeDB == '0') || (notifTypeDB == 'Level Tenant')) {
        if (mustUseWAFirst == '1') {
            'SMS'
        } else {
            if (emailServiceOnVendor == '1') {
                'SMS'
            } else {
                'EMAIL'
            }
        }
    } else {
        'SMS'
    }
}

