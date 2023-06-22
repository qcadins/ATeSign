import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathStamping).columnNumbers

int i
sheet = 'API Stamping'

'looping API request stamping'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		'check if tidak mau menggunakan tenant code yang benar'
		if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 15) == 'No') {
			'set tenant kosong'
			GlobalVariable.Tenant = findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 16)
		} else if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 15) == 'Yes') {
			GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
		}
		
		'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 13) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 13) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 14)
        }

        'HIT API check Status stamping'
        respon = WS.sendRequest(findTestObject('Postman/Stamping', [('callerId') : findTestData(excelPathStamping).getValue(
                        GlobalVariable.NumofColm, 9), ('refNumber') : findTestData(excelPathStamping).getValue(
                        GlobalVariable.NumofColm, 11)]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
            if (code == 0) {
				for (i = 1 ; i <= 6 ; i ++) {
               int prosesMaterai = CustomKeywords.'connection.Stamping.getProsesMaterai'(conneSign, findTestData(excelPathStamping).getValue(
                        GlobalVariable.NumofColm, 11).replace('"',''))
				
				   if (prosesMaterai == 51) {
					   'Write To Excel GlobalVariable.StatusFailed and errormessage'
					   CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Stamping', GlobalVariable.NumofColm,
						   GlobalVariable.StatusFailed, GlobalVariable.ReasonFailedProsesStamping)
					   
					   GlobalVariable.FlagFailed = 1
					   break
				   } else if (prosesMaterai == 53) {
					   'get totalMaterai from db'
					   
					   WebUI.delay(3)
					   
					   ArrayList totalMateraiAndTotalStamping = CustomKeywords.'connection.Stamping.getTotalMateraiAndTotalStamping'(conneSign, findTestData(excelPathStamping).getValue(
						   GlobalVariable.NumofColm, 11).replace('"',''))
					   
					   'declare arraylist arraymatch'
					   arrayMatch = []
					   
					   arrayMatch.add(WebUI.verifyMatch(totalMateraiAndTotalStamping[0], totalMateraiAndTotalStamping[1], false, FailureHandling.CONTINUE_ON_FAILURE))

					   'jika data db tidak sesuai dengan excel'
					   if (arrayMatch.contains(false)) {
						   'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
						   CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Stamping', GlobalVariable.NumofColm,
							   GlobalVariable.StatusFailed, (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm,
								   2) + ';') + GlobalVariable.ReasonFailedStoredDB)
						   GlobalVariable.FlagFailed = 1
						   
					   } 
					   break
				   } else {
						   WebUI.delay(10)
						   
						   if (i == 6) {
							   'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
							   CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Stamping', GlobalVariable.NumofColm,
								   GlobalVariable.StatusFailed, (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm,
									   2) + ';') + GlobalVariable.ReasonFailedProsesStamping + ' dengan jeda waktu 60 detik ')
							   GlobalVariable.FlagFailed = 1
						   }
				   }
				   
				}
				
				if (GlobalVariable.FlagFailed == 0) {
					'write to excel success'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Stamping', 0, GlobalVariable.NumofColm -
						1, GlobalVariable.StatusSuccess)
					
					'Call API Send doc'
					WebUI.callTestCase(findTestCase('Meterai/verifyMeterai'), [('excelPathMeterai') : excelPathStamping, ('sheet') : sheet, ('noKontrak') : findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 11).replace('"','')], FailureHandling.CONTINUE_ON_FAILURE)
	
				}
				
			

            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

				'write to excel status failed dan reason'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
					(findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + message)
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + message)
        }
    }
}

