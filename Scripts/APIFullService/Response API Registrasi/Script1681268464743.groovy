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
int countColmExcel = findTestData(excelPathAPIRegistrasi).columnNumbers

String selfPhoto, idPhoto, trxNo

'looping API Registrasi'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 30) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 30)
        } else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 30) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 28) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 28) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 29)
        }
        
        if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 32) == 'Yes') {
            selfPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIRegistrasi).getValue(
                    GlobalVariable.NumofColm, 24))) + '"')
        } else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 32) == 'No') {
            selfPhoto = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 24)
        }
		
		if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 33) == 'Yes') {
			idPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIRegistrasi).getValue(
					GlobalVariable.NumofColm, 25))) + '"')
		} else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 33) == 'No') {
			idPhoto = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 25)
		}
        
        'HIT API'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Register', [('callerId') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, 9), ('nama') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                        11), ('email') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 12), ('tmpLahir') : findTestData(
                        excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 13), ('tglLahir') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, 14), ('jenisKelamin') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, 15), ('tlp') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                        16), ('idKtp') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 17), ('alamat') : findTestData(
                        excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 18), ('kecamatan') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, 19), ('kelurahan') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                        20), ('kota') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 21), ('provinsi') : findTestData(
                        excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 22), ('kodePos') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, 23), ('selfPhoto') : selfPhoto, ('idPhoto') : idPhoto, ('password') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                        26)]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
			'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                'mengambil response'
                trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

                email = WS.getElementPropertyValue(respon, 'email', FailureHandling.OPTIONAL)

                if (GlobalVariable.checkStoreDB == 'Yes') {
                    arrayIndex = 0

                    'get data from db'
                    ArrayList<String> result = CustomKeywords.'connection.APIFullService.checkAPIRegisterActive'(conneSign, findTestData(
                            excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 12).replace('"', ''), findTestData(
                            excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 16).replace('"', ''))

                    String resultTrx = CustomKeywords.'connection.APIFullService.getAPIRegisterTrx'(conneSign, trxNo)

                    ArrayList<String> resultDataUser = CustomKeywords.'connection.Registrasi.buatUndanganStoreDB'(conneSign, 
                        findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 12).replace('"', ''))

                    'declare arraylist arraymatch'
                    arrayMatch = []

                    'verify is_active'
                    arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]).toUpperCase(), '1', false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify is_registered'
                    arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]).toUpperCase(), '1', false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify trx qty = -1'
                    arrayMatch.add(WebUI.verifyMatch(resultTrx, '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify full name'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 11).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify tempat lahir'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 13).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'parse Date from MM/dd/yyyy > yyyy-MM-dd'
                    sDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(findTestData(excelPathAPIRegistrasi).getValue(
                            GlobalVariable.NumofColm, 14).replace('"', ''), 'MM/dd/yyyy', 'yyyy-MM-dd')

                    'verify tanggal lahir'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), sDate.toUpperCase(), 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify jenis kelamin'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 15).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify email'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 12).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify provinsi'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 22).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kota'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 21).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kecamatan'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 19).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kelurahan'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 20).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kode pos'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                GlobalVariable.NumofColm, 23).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Registrasi', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedStoredDB) 
						} else {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Registrasi', 
                            0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)
				
				println trxNo
				
				trxNo = trxNo.replace('[', '').replace(']', '')
				
                'Write To Excel GlobalVariable.StatusFailed and errormessage'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Registrasi', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, message)

                if ((GlobalVariable.checkStoreDB == 'Yes') && (trxNo != null)) {
                    String resultTrx = CustomKeywords.'connection.APIFullService.getAPIRegisterTrx'(conneSign, trxNo)

println resultTrx
println trxNo
WebUI.delay(30)
                    'declare arraylist arraymatch'
                    ArrayList arrayMatch = []

                    'verify trx qty = -1'
                    arrayMatch.add(WebUI.verifyMatch(resultTrx, '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Registrasi', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                                2) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    }
                }
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Registrasi', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, message)
        }
    }
}

