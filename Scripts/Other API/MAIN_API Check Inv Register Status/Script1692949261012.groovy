import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.nio.charset.StandardCharsets as StandardCharsets
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'Connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathCheckInvRegisterStatus).columnNumbers

'looping API Check Inv Register Status'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 
    0) {
        break
    } else if (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
		'setting untuk is-external-activation'
		if (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Setting isExternalActivation(VendorUser)')).length() >
		0) {
			CustomKeywords.'connection.UpdateData.updateExternalActivationVendorUser'(conneSign,
				Integer.parseInt(findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Setting isExternalActivation(VendorUser)'))),
				findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Email/PhoneNo')))
		}
	
        'get tenant dari excel per case'
        GlobalVariable.Tenant = findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel(
                'Tenant Login'))

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathCheckInvRegisterStatus, GlobalVariable.NumofColm, 
            rowExcel('Use Correct Base Url'))

        'get psre dari excel per case'
        GlobalVariable.Psre = findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel(
                'Psre Login'))

        String value

        if (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Input correct Message')).equalsIgnoreCase(
            'Yes')) {
            'get invitationcode dari DB > encrypt invitation code > encode invitation code yang sudah di encrypt'
            value = encodeValue(findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel(
                        'Email/PhoneNo')), conneSign)
        } else if (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Input correct Message')).equalsIgnoreCase(
            'No')) {
            value = findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Message'))
        }
        
        'HIT API'
        respon = WS.sendRequest(findTestObject('APIFullService - Privy/Postman/API Check Inv Register Status', [('callerId') : findTestData(
                        excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('CallerId')), ('msg') : value]))

        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get  code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'ambil lama waktu yang diperlukan hingga request menerima balikan'
            elapsedTime = ((respon.elapsedTime / 1000) + ' second')

            'ambil body dari hasil respons'
            responseBody = respon.responseBodyContent

            'panggil keyword untuk proses beautify dari respon json yang didapat'
            CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                    excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

            'write to excel response elapsed time'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

            if (code == 0) {
                'get activeStatus'
                activeStatus = WS.getElementPropertyValue(respon, 'activeStatus', FailureHandling.OPTIONAL)

                'get registrationStatus'
                registrationStatus = WS.getElementPropertyValue(respon, 'registrationStatus', FailureHandling.OPTIONAL)

                'get verificationInProgress'
                verificationInProgress = WS.getElementPropertyValue(respon, 'verificationInProgress', FailureHandling.OPTIONAL)

                'get verificationResult'
                verificationResult = WS.getElementPropertyValue(respon, 'verificationResult', FailureHandling.OPTIONAL)

                'get activationByExternalFlow'
                activationByExternalFlowResult = WS.getElementPropertyValue(respon, 'activationByExternalFlow', FailureHandling.OPTIONAL)

                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)

                'write to excel verif status'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Result') - 
                    1, GlobalVariable.NumofColm - 1, (((((((activeStatus + ';') + registrationStatus) + ';') + verificationInProgress) + 
                    ';') + verificationResult) + ';') + activationByExternalFlowResult)

                if (GlobalVariable.checkStoreDB == 'Yes') {
                    'declare arraylist arraymatch'
                    ArrayList arrayMatch = []

                    'get result dari db'
                    result = CustomKeywords.'connection.APIFullService.getCheckInvRegisStoreDB'(conneSign, findTestData(
                            excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, rowExcel('Email/PhoneNo')))

                    arrayIndex = 0

                    'verify activeStatus'
                    arrayMatch.add(WebUI.verifyMatch(activeStatus, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify registrationStatus'
                    arrayMatch.add(WebUI.verifyMatch(registrationStatus, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify verificationInProgress'
                    arrayMatch.add(WebUI.verifyMatch(verificationInProgress, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify verificationResult'
                    arrayMatch.add(WebUI.verifyMatch(verificationResult, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    if (GlobalVariable.Psre != 'PRIVY') {
                        'verify activationByExternalFlowResult'
                        arrayMatch.add(WebUI.verifyMatch(activationByExternalFlowResult, result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
                    } else {
                        'verify activationByExternalFlowResult'
                        arrayMatch.add(WebUI.verifyMatch(activationByExternalFlowResult.toString(), 'null', false, FailureHandling.CONTINUE_ON_FAILURE))
                    }
                    
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathCheckInvRegisterStatus).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)

                        GlobalVariable.FlagFailed = 1
                    }
                }
            } else {
                'call function get error message API'
                getErrorMessageAPI(respon)
            }
        } else {
            'call function get error message API'
            getErrorMessageAPI(respon)
        }
    }
}

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ('<' + message) + '>')
}

def encodeValue(String value, Connection conneSign) {
    'get invitation code dari db'
    String invCode = CustomKeywords.'connection.APIFullService.getInvitationCode'(conneSign, value)

    'Mengambil aes key based on tenant tersebut'
    String aesKey = CustomKeywords.'connection.APIFullService.getAesKeyEncryptUrl'(conneSign)

    'encrypt invitation code'
    String encryptCode = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(invCode, aesKey)

    try {
        return URLEncoder.encode(encryptCode, StandardCharsets.UTF_8.toString())
    }
    catch (UnsupportedEncodingException ex) {
        throw new RuntimeException(ex.cause)
    } 
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

