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
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        GlobalVariable.FlagFailed = 0

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
        
        'HIT API Vendor List registrasi'
        respon = WS.sendRequest(findTestObject('Postman/Invitation Register Data', [
						('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), 
						('code') : code]))

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
                'lakukan cek db jika diperlukan'
                if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                    'declare arraylist arraymatch'
                    ArrayList arrayMatch = []

                    'get data store db'
                    ArrayList result = CustomKeywords.'connection.APIFullService.getInvRegisterData'(conneSign, WS.getElementPropertyValue(
                            respon, 'userData.tlp').toString())

                    'declare arrayindex'
                    arrayindex = 0

                    'verify provinsi'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'userData.provinsi', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kota'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'userData.kota', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kecamatan'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'userData.kecamatan', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

					if (WS.getElementPropertyValue(respon, 'userData.email', FailureHandling.OPTIONAL).toString() != 'null') {
						'verify email'
						arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'userData.email', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
					} else {
						arrayindex++
					}
                    'verify kelurahan'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'userData.kelurahan', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kodePos'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'userData.kodePos', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify nama'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'userData.nama', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify alamat'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'userData.alamat', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify jenis kelamin'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'userData.jenisKelamin', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify tlp'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'userData.tlp', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify tmpLahir'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'userData.tmpLahir', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify tglLahir'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'userData.tglLahir', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify idKtp'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'userData.idKtp', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify tenantCode'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'tenantCode', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify vendorCode'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'vendorCode', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify vendorName'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'vendorName', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

					if (WS.getElementPropertyValue(respon, 'vendorName', FailureHandling.OPTIONAL) == 'Privy') {
						'verify livenessurl'
						arrayMatch.add(WebUI.verifyNotMatch(WS.getElementPropertyValue(respon, 'livenessUrl', FailureHandling.OPTIONAL),
								'null', false, FailureHandling.CONTINUE_ON_FAILURE))
						
						'tulis di excel hasil link yang bisa digunakan untuk sign'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Liveness Url Link') -
							1, GlobalVariable.NumofColm - 1, WS.getElementPropertyValue(respon, 'livenessUrl', FailureHandling.OPTIONAL))
					}
                    'verify verifPhone'
                    arrayMatch.add(WebUI.verifyMatch(WS.getElementPropertyValue(respon, 'verifPhone', FailureHandling.OPTIONAL), 
                            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        GlobalVariable.FlagFailed = 1

                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
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

