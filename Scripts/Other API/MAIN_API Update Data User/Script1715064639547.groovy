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

semicolon = ';'

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.DataVerif.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'HIT API Login untuk get token'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')
        } else {
            getErrorMessageAPI(responLogin)
        }
        
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Token')) == 'No') {
            GlobalVariable.token = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Token'))
        }		
		String idMsUser = ''
		
		if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Input Id Ms User based on idno ?')) == 'Yes') {
			idMsUser = CustomKeywords.'connection.APIOnly.getIdMsUser'(conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('idNo')))
		} else {
			'hardcoded 1'
			idMsUser = '1'
		}
		
        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/Update Data User', [('callerId') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')), ('email') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('email')), ('provinsi') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('provinsi')), ('kota') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('kota')), ('kecamatan') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('kecamatan')), ('gender') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('gender')), ('kelurahan') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('kelurahan')), ('zipCode') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('zipCode')), ('dob') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('dob')), ('pob') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('pob')), ('idNo') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('idNo')), ('address') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('address')), ('name') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('name')), ('phone') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('phone')), ('idMsUser') : idMsUser]))

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
                if (GlobalVariable.checkStoreDB == 'Yes') {}
                
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

