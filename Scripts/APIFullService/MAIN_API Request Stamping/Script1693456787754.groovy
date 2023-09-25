import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPIRequestStamping).columnNumbers

'looping API request stamping'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIRequestStamping, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))
		
		'get psre dari excel per case'
		GlobalVariable.Psre = findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
		
		'check if tidak mau menggunakan tenant code yang benar'
		if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'No') {
			'set tenant kosong'
			GlobalVariable.Tenant = findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
		} else if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 'Yes') {
			GlobalVariable.Tenant = findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
		}
		
		'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        'HIT API check Status stamping'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Request Stamping', [('callerId') : findTestData(excelPathAPIRequestStamping).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')), ('refNumber') : findTestData(excelPathAPIRequestStamping).getValue(
                        GlobalVariable.NumofColm, rowExcel('refNumber'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                'mengambil response'
                message = WS.getElementPropertyValue(respon, 'checkStampingStatus.message', FailureHandling.OPTIONAL)

                if (GlobalVariable.checkStoreDB == 'Yes') {
					'get totalMaterai from db'
					String totalMaterai = CustomKeywords.'connection.APIFullService.getTotalMaterai'(conneSign, findTestData(excelPathAPIRequestStamping).getValue(
                        GlobalVariable.NumofColm, rowExcel('refNumber')).replace('"',''))
					
					'looping untuk delay sebanyak total materai yang ada pada document'
					for (i = 0 ; i <= Integer.parseInt(totalMaterai) ; i++) {
						'delay untuk menunggu proses transaksi selesai sebanyak total materai yang ada'
						WebUI.delay(10)
					}
					
					'mengambil value db proses ttd'
					int prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, findTestData(excelPathAPIRequestStamping).getValue(
                        GlobalVariable.NumofColm, rowExcel('refNumber')).replace('"',''))

					'jika proses materai gagal (51)'
					if (prosesMaterai == 51) {
						
						'Diberikan delay 3 detik untuk update error message pada db'
						WebUI.delay(3)
						
						'get reason gailed error message untuk stamping'
						errorMessageDB = CustomKeywords.'connection.Meterai.getErrorMessage'(conneSign, findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('refNumber')).replace('"',''))
					   
						'Write To Excel GlobalVariable.StatusFailed and errormessage'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
						GlobalVariable.ReasonFailedProsesStamping + ' dengan alasan ' + errorMessageDB.toString())

						GlobalVariable.FlagFailed = 1
					}

					'looping untuk cek saldo'
					for (meteraiTrx = 1; meteraiTrx <= 20; meteraiTrx++) {
						'declare arraylist arraymatch'
						arrayMatch = []
						
						'get trx from db'
						String result = CustomKeywords.'connection.APIFullService.getAPIRequestStampingTrx'(conneSign, findTestData(excelPathAPIRequestStamping).getValue(
								GlobalVariable.NumofColm, rowExcel('refNumber')).replace('"',''), totalMaterai)
						
						'verify saldo terpotong'
						arrayMatch.add(WebUI.verifyMatch(result, '-'+totalMaterai, false, FailureHandling.OPTIONAL))
						
						'jika data db tidak sesuai dengan excel'
	                    if (arrayMatch.contains(false)) {
							if(meteraiTrx == 20) {
								'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
								CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
									GlobalVariable.StatusFailed, (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, 
										rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB + ' Pemotongan Saldo tidak sesuai jumlah stamping')								
							}
							
							WebUI.delay(10)
	                    } else {
	                        'write to excel success'
	                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
	                            0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
							
							break
	                    }
					}

                    
                }
            } else {
               'call function get error msg'
			   getErrorMsg(respon)	
            }
        } else {
            'call function get error msg'
			getErrorMsg(respon)
        }
    }
}

def getErrorMsg (def respon) {
	'mengambil status code berdasarkan response HIT API'
	message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

	'Write To Excel GlobalVariable.StatusFailed and errormessage'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
		GlobalVariable.StatusFailed, '<' + message + '>')
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}