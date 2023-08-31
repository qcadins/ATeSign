import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPIDownload).columnNumbers

'looping API Download Document'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIDownload, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))
		
		'get psre dari excel per case'
		GlobalVariable.Psre = findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
		
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
        } else if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        'HIT API check Download Document'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Download Document', [('callerId') : findTestData(
                        excelPathAPIDownload).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('documentId') : findTestData(excelPathAPIDownload).getValue(
                        GlobalVariable.NumofColm, rowExcel('documentId'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                'mengambil response'
                base64PDF = WS.getElementPropertyValue(respon, 'pdfBase64', FailureHandling.OPTIONAL)

                'decode Bas64 to File PDF'
                CustomKeywords.'customizekeyword.ConvertFile.decodeBase64'(base64PDF, findTestData(excelPathAPIDownload).getValue(
                        GlobalVariable.NumofColm, rowExcel('File Name')))

                'check is file downloaded dan apakah mau di delete'
                if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathAPIDownload).getValue(
                        GlobalVariable.NumofColm, rowExcel('Delete File ?'))) == true) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                        0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, '<' + message + '>')
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, '<' + message + '>')
        }
    }
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}