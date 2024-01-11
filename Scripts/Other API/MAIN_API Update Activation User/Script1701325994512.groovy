import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import java.util.regex.Matcher as Matcher
import java.util.regex.Pattern as Pattern

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'ubah invitation menjadi code only'
        String code = parseCodeOnly(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Invitation Link')))

        try {
            'lakukan decrypt untuk code dari link diatas dan cek ke DB'
            String decryptedKey = decryptLink(conneSign, code)

            'jika invitation code tidak terdapat di DB'
            if (CustomKeywords.'connection.APIFullService.getCountInvCodeonDB'(conneSign, decryptedKey) != 1) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 'Key yang diencrypt pada URL tidak terdapat di DB')
            }
        } catch (Exception e) {
            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 'Link gagal di-decrypt')
        } 
        
        'HIT API Update status aktivasi user'
        respon = WS.sendRequest(findTestObject('Postman/Update Activation User', [
					('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), 
					('code') :  code, 
					('pass') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Password')), 
					('phoneNo') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('phoneNumber'))]))

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

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            statusCode = WS.getElementPropertyValue(respon, 'status.code')

            'Jika status codenya 0'
            if (statusCode == 0) {
                if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                    'declare arraylist arraymatch'
                    ArrayList arrayMatch = []

                    'get data store db'
                    ArrayList result = CustomKeywords.'connection.APIFullService.getUpdateActivationUserAPIOnly'(conneSign, 
                        findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('phoneNumber')))

                    'declare arrayindex'
                    arrayindex = 0

                    'verify fullName'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'fullName', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify nik'
                    arrayMatch.add(WebUI.verifyMatch(CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(WS.getElementPropertyValue(
                                    respon, 'idNo', FailureHandling.OPTIONAL)), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify phoneNo'
                    arrayMatch.add(WebUI.verifyMatch(CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(WS.getElementPropertyValue(
                                    respon, 'phoneNo', FailureHandling.OPTIONAL)), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify email'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'email', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify registeredDate'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'registeredDate', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

					'jika data db tidak sesuai dengan excel'
					if (arrayMatch.contains(false)) {
						GlobalVariable.FlagFailed = 1

						'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
									'Reason Failed')) + ';') + 'Transaksi OTP tidak masuk balance mutation')
					}
                }
                
                'tulis sukses jika store DB berhasil'
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

def getErrorMessageAPI(def respon) {
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

def parseCodeOnly(String url) {
    'ambil data sesudah "code="'
    Pattern pattern = Pattern.compile('code=([^&]+)')

    'ambil matcher dengan URL'
    Matcher matcher = pattern.matcher(url)

    'cek apakah apttern nya sesuai'
    if (matcher.find()) {
        'ubah jadi string'
        String code = matcher.group(1)

        'decode semua ascii pada url'
        code = URLDecoder.decode(code, 'UTF-8')

        return code
    } else {
        ''
    }
}

def decryptLink(Connection conneSign, String invCode) {
    aesKey = CustomKeywords.'connection.DataVerif.getAESKey'(conneSign)

    'enkripsi msg'
    encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseDecrypt'(invCode, aesKey)

    encryptMsg
}

