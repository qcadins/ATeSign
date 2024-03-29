import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

ArrayList regionOffice = []

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'get tenant per case dari colm excel'
        GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode'))

        'ubah region name dan office name yang diinput menjadi code'
        regionOffice = CustomKeywords.'connection.APIFullService.convertRegionOfficetoCode'(conneSign, findTestData(excelPath).getValue(
                GlobalVariable.NumofColm, rowExcel('Office Name*')), findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                rowExcel('Region Name*')), GlobalVariable.Tenant)

        'HIT API Login untuk get token'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')

            'HIT API'
            respon = WS.sendRequest(findTestObject('Postman/Inquiry Normal', [('page') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('Page')), ('customerName') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('Customer Name*')), ('refNumber') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('Ref Number*')), ('requestedDateStart') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('Request Date Start*')), ('requestedDateEnd') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Request Date End*')), ('completedDateStart') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Complete Date Start*')), ('completedDateEnd') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Complete Date End*')), ('documentType') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Document Type*')), ('transactionStatus') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Transaction Status*')), ('stampingStatus') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Stamping Status*')), ('callerId') : findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('username')), ('regionCode') : regionOffice[
                        0], ('officeCode') : regionOffice[1]]))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
                'mengambil status code berdasarkan response HIT API'
                statusCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

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

                'jika status codenya 0'
                if (statusCode == 0) {
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

