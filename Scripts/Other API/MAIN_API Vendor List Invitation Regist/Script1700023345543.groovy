import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import java.util.regex.Matcher
import java.util.regex.Pattern

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

       'get tenant per case dari colm excel'
        GlobalVariable.Tenant = (('"' + findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode'))) + 
            '"')

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
        
        'HIT API Vendor List registrasi'
        responVendorList = WS.sendRequest(findTestObject('Postman/vendorListInvReg', [('callerId') : ('"' + findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))) + '"', ('code') : ('"' + code) + 
                    '"']))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responVendorList, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            status_Code = WS.getElementPropertyValue(responVendorList, 'status.code')

			'ambil lama waktu yang diperlukan hingga request menerima balikan'
			elapsedTime = (responVendorList.elapsedTime / 1000) + ' second'
	
			'ambil body dari hasil respons'
			responseBody = responVendorList.responseBodyContent
	
			'panggil keyword untuk proses beautify dari respon json yang didapat'
			CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
					excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
			'write to excel response elapsed time'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
				1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
			
            'Jika status codenya 0'
            if (status_Code == 0) {
                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)
            } else {
                getErrorMessageAPI(responVendorList)
            }
        } else {
            getErrorMessageAPI(responVendorList)
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
        return ''
    }
}

def decryptLink(Connection conneSign, String invCode) {
    aesKey = CustomKeywords.'connection.DataVerif.getAESKey'(conneSign)

    'enkripsi msg'
    encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseDecrypt'(invCode, aesKey)

    encryptMsg
}

