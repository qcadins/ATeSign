import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get column excel'
int countColmExcel = findTestData(excelPathTryCallbackURL).columnNumbers

'sheet yang digunakan di excel'
sheet = 'API Try Callback URL'

'looping per column'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathTryCallbackURL).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathTryCallbackURL).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathTryCallbackURL).getValue(GlobalVariable.NumofColm, 15) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathTryCallbackURL).getValue(GlobalVariable.NumofColm, 16)
        } else if (findTestData(excelPathTryCallbackURL).getValue(GlobalVariable.NumofColm, 15) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathTryCallbackURL).getValue(GlobalVariable.NumofColm, 13) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathTryCallbackURL).getValue(GlobalVariable.NumofColm, 13) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathTryCallbackURL).getValue(GlobalVariable.NumofColm, 14)
        }
        
		'Set base url dengan value dari excel'
        GlobalVariable.base_url = findTestData(excelPathTryCallbackURL).getValue(GlobalVariable.NumofColm, 11)

        'HIT API'
        respon = WS.sendRequest(findTestObject('PengaturanTenant/API Try Callback URL', [('email') : findTestData(excelPathTryCallbackURL).getValue(
                        GlobalVariable.NumofColm, 8), ('nik') : findTestData(excelPathTryCallbackURL).getValue(GlobalVariable.NumofColm, 
                        9), ('activationStatus') : findTestData(excelPathTryCallbackURL).getValue(GlobalVariable.NumofColm, 
                        10)]), FailureHandling.CONTINUE_ON_FAILURE)

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika codenya 0'
            if (code == 0) {
                'mengambil response message'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

				'Jika messagenya success'
                if ('Success'.equalsIgnoreCase(message)) {
                    'write to excel success'
                    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                } else {
                    'Jika tidak sukses, maka Write To Excel GlobalVariable.StatusFailed and errormessage'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, GlobalVariable.ReasonFailedHitAPI + ' dengan hasil ' + message)
                }
            } else {
                ' Jika code bukan 0, maka mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'error', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    '<' + message.toString() + '>')
            }
        } else {
            'Jika status tidak 200, maka mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'error', FailureHandling.OPTIONAL)
			
			'Jika messagenya tidak null'
			if (message != null) {
            'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
             CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
             '<' + message.toString() + '>')
        } else {
			'Jika messagenya null maka Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, GlobalVariable.ReasonFailedHitAPI + ' dengan hasil ' + message)
		}
    }
}
}
