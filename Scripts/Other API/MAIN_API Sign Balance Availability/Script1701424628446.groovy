import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'Pembuatan pengisian variable di sendRequest per jumlah documentId.'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPath).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

       'get vendor per case dari colm excel'
       GlobalVariable.Psre = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Vendor Code'))
        
        'get tenant per case dari colm excel'
        GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode'))
        
        'inisialisasi arrayList'
        ArrayList documentId = [], list = [], listDocId = []

        'Mengambil document id dari excel dan displit'
        documentId = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$documentId')).split(';', -1)

        for (int q = 0; q < documentId.size(); q++) {
            list.add(('"' + documentId.get(q)) + '"')

            if (q == 0) {
                listDocId.add(list.get(q))
            } else {
                listDocId.set(0, (listDocId.get(0) + ',') + list.get(q))
            }
        }
        
        'ubah menjadi string'
        String listDoc = listDocId.toString().replace('[', '').replace(']', '')

        'HIT API Login untuk get token'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [
			('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('username')), 
			('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')

            'HIT API Sign Balance availability'
            responSignBalVal = WS.sendRequest(findTestObject('Postman/Sign Balance Availability', [
        	('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), 
        	('docId') : listDoc]))

            'ambil lama waktu yang diperlukan hingga request menerima balikan'
            elapsedTime = ((responSignBalVal.elapsedTime / 1000) + ' second')

            'ambil body dari hasil respons'
            responseBody = responSignBalVal.responseBodyContent

            'panggil keyword untuk proses beautify dari respon json yang didapat'
            CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                    excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

            'write to excel response elapsed time'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(responSignBalVal, 200, FailureHandling.OPTIONAL) == true) {
                'get Status Code'
                statusCode = WS.getElementPropertyValue(responSignBalVal, 'status.code')

                'Jika status codenya 0'
                if (statusCode == 0) {
                    'get vendor Code'
                    GlobalVariable.Response = WS.getElementPropertyValue(responSignBalVal, 'vendorCode')

                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                } else {
                    getErrorMessageAPI(responSignBalVal)
                }
            } else {
                getErrorMessageAPI(responSignBalVal)
            }
        } else {
            getErrorMessageAPI(responLogin)
        }
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
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

