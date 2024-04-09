import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.testobject.ResponseObject

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

'looping API Download Document'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel(
                'Use Correct Base Url'))

        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 
        'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
        } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 
        'Yes') {
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 
        'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        'HIT API get template sign loc'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Get Balance Mutation', [
			('callerId') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')),
			('balType') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$balanceType')),
			('startDate') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Start Date')),
			('endDate') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$End Date')),
			('offCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Office Code')),
			('businessLine') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Business Line Code')),
			('regionCode') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Region Code'))]))

        'ambil lama waktu yang diperlukan hingga request menerima balikan'
        elapsedTime = ((respon.elapsedTime / 1000) + ' second')

        'ambil body dari hasil respons'
        responseBody = respon.responseBodyContent

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
			
			'panggil keyword untuk proses beautify dari respon json yang didapat'
			CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
					excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
			'write to excel response elapsed time'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
				1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

            if (code == 0) {
                if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                    'declare arraylist arraymatch'
                    ArrayList arrayMatch = []

                    'get data store db'
                    ArrayList result = CustomKeywords.'connection.APIFullService.getBalanceMutation'(conneSign,
						findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$balanceType')),
						findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$Start Date')),
						findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$End Date')),
						findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Office Code')),
						findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Business Line Code')),
						findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Region Code')))
					
					String resultchange = result.toString().replace('[', '').replace(']', '').replace(' ' , '')
					
					result = resultchange.split(',', -1)
                    
					'declare arrayindex'
                    arrayindex = 0
					
					int size = 0, module = 0

					'cek apakah merupakan stamp duty query'
					if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$balanceType')) == 'SDT') {
						size = result.size() / 19
					} else {
						size = result.size() / 11
					}
								
					'looping semua array yang terdapat di respon API dan DB'
					for (i = 0; i < size; i++) {
						String a = WS.getElementPropertyValue(respon, 'balanceMutations[' + i + ']', FailureHandling.OPTIONAL)
						
						a = a.replace('[', '').replace(']', '').replace(' ' , '')
						
						ArrayList apiResult = a.split(',', -1)
						
						for (int j = 0; j < apiResult.size(); j++) {
							'verify no urutan'
							arrayMatch.add(WebUI.verifyMatch(apiResult[j], result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
							
							if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('$balanceType')) == 'SDT') {
								module = arrayindex % 19
							} else {
								module = arrayindex % 11
							}
							
							'jika next array index kosong, skip pengecekan ke DB'
							if ((module) == 1 && (result[arrayindex] == '' || result[arrayindex] == 'null')) {
								arrayindex += 2
							} else if (result[arrayindex] == 'null' || result[arrayindex] == '') {
								arrayindex += 1
							}
						}
					}
                          
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        GlobalVariable.FlagFailed = 1

                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
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

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ('<' + message) + '>')
}
