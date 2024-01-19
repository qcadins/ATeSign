import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
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
        }
        catch (IllegalArgumentException e) {
            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 'Link gagal di-decrypt')
        } 
        
        'write to excel otp before'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('OTP before') - 
            1, GlobalVariable.NumofColm - 1, CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(
                    excelPath).getValue(GlobalVariable.NumofColm, rowExcel('phoneNumber'))))

        'HIT API Sent OTP untuk aktivasi user'
        respon = WS.sendRequest(findTestObject('Postman/Sent OTP Activation User', [
					('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), 
					('code') : code, 
					('phoneNo') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('phoneNumber'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            statusCode = WS.getElementPropertyValue(respon, 'status.code')

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
	
            'Jika status codenya 0'
            if (statusCode == 0) {
				if (GlobalVariable.checkStoreDB == 'Yes') {
					'declare arraylist arraymatch'
					ArrayList arrayMatch = []

					'ambil data last transaction dari DB'
					ArrayList resultDB = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(conneSign,
						findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('phoneNumber')), 'Register')

					println(resultDB)

					'declare arrayindex'
					arrayindex = 0

					'lakukan loop untuk pengecekan data'
					for (int i = 0; i < (resultDB.size() / 2); i++) {
						'verify business line dan office code'
						arrayMatch.add(WebUI.verifyMatch((resultDB[i]).toString(), (resultDB[(i + 3)]).toString(), false,
								FailureHandling.CONTINUE_ON_FAILURE))
					}
					
					'jika data db tidak sesuai dengan excel'
					if (arrayMatch.contains(false)) {
						GlobalVariable.FlagFailed = 1

						'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
									'Reason Failed')) + ';') + 'Transaksi OTP tidak masuk balance mutation')
					}
				}
				
                'write to excel otp latest'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('OTP latest') - 
                    1, GlobalVariable.NumofColm - 1, CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('phoneNumber'))))

                'cek apakah send ulang OTP berhasil'
                if (WebUI.verifyNotMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('OTP before')), 
                    findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('OTP latest')), false, FailureHandling.OPTIONAL) && GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                } else {
                    'Write To Excel GlobalVariable.StatusFailed and errormessage'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                'Reason Failed')) + ';') + 'Hit sukses, namun OTP tidak ter-update')
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
    } 

	''
}

def decryptLink(Connection conneSign, String invCode) {
    aesKey = CustomKeywords.'connection.DataVerif.getAESKey'(conneSign)

    'enkripsi msg'
    encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseDecrypt'(invCode, aesKey)

    encryptMsg
}

