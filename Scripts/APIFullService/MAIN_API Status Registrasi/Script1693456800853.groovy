import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPICheckRegistrasi).columnNumbers

'looping API Registrasi'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPICheckRegistrasi, GlobalVariable.NumofColm, 
            rowExcel('Use Correct Base Url'))

        'check if mau menggunakan tenant yang salah atau benar'
        if (findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 
        'Yes') {
            'get tenant dari excel per case'
            GlobalVariable.Tenant = findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Tenant Login'))
        } else if (findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 
        'No') {
            'get Tenant salah dari excel'
            GlobalVariable.Tenant = findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Wrong Tenant Code'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 
        'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 
        'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Wrong API Key'))
        }
        
        'HIT API check registrasi'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Check Registration', [('callerId') : findTestData(
                        excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('dataType') : findTestData(
                        excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('dataType')), ('dataValue') : findTestData(
                        excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('userData'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'ambil lama waktu yang diperlukan hingga request menerima balikan'
            elapsedTime = (respon.elapsedTime / 1000) + ' second'

            'ambil body dari hasil respons'
            responseBody = respon.responseBodyContent

            'panggil keyword untuk proses beautify dari respon json yang didapat'
            CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                    excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

            'write to excel response elapsed time'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

            if (code == 0) {
                'mengambil response'
                vendor = WS.getElementPropertyValue(respon, 'registrationData.vendor', FailureHandling.OPTIONAL)

                vendoractive = WS.getElementPropertyValue(respon, 'registrationData.registrationStatus', FailureHandling.OPTIONAL)

                if (GlobalVariable.checkStoreDB == 'Yes') {
                    arrayIndex = 0

                    'get data from db'
                    ArrayList result = CustomKeywords.'connection.APIFullService.getAPICheckRegisterStoreDB'(conneSign, 
                        findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('userData')))

                    println(result)

                    'declare arraylist arraymatch'
                    ArrayList arrayMatch = []

                    for (index = 0; index < vendor.size(); index++) {
                        arrayIndex = result.indexOf(vendor[index])

                        vendorDB = (result[arrayIndex++])

                        vendorStatusDB = (result[arrayIndex++])

                        if ((vendor[index]) == vendorDB) {
                            'verify vendor'
                            arrayMatch.add(WebUI.verifyMatch(vendorDB.toUpperCase(), (vendor[index]).toUpperCase(), false, 
                                    FailureHandling.CONTINUE_ON_FAILURE))

                            'verify vendor status'
                            arrayMatch.add(WebUI.verifyMatch(vendorStatusDB, vendoractive[index], false, FailureHandling.CONTINUE_ON_FAILURE))
                        }
                    }
                    
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)

                        GlobalVariable.FlagFailed = 1
                    } else {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
				getErrorMessageAPI(respon)
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
			getErrorMessageAPI(respon)
        }
    }
}

def getErrorMessageAPI(ResponseObject respon) {
	'mengambil status code berdasarkan response HIT API'
	message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

	'Write To Excel GlobalVariable.StatusFailed and errormessage'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
		((findTestData(excelPathAPICheckRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + message)) +
		'>')

	GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

