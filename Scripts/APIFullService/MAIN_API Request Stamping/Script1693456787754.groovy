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
    } else if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIRequestStamping, GlobalVariable.NumofColm, 
            rowExcel('Use Correct Base Url'))

        'get psre dari excel per case'
        GlobalVariable.Psre = findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))

        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 
        'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Wrong Tenant Code'))
        } else if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 
        'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Tenant Login'))

            'ubah vendor stamping jika diperlukan '
            if ((findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')).length() > 
            0) && (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')) != 
            'No')) {
                'ambil idLov untuk diupdate secara otomatis ke DB'
                int idLov = CustomKeywords.'connection.ManualStamp.getIdLovVendorStamping'(conneSign, findTestData(excelPathAPIRequestStamping).getValue(
                        GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')))

                'lakukan update vendor stamping yang akan dipakai'
                CustomKeywords.'connection.UpdateData.updateVendorStamping'(conneSign, idLov)
            }
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 
        'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 
        'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Wrong API Key'))
        }
        
        'HIT API check Status stamping'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Request Stamping', [('callerId') : findTestData(excelPathAPIRequestStamping).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')), ('refNumber') : findTestData(excelPathAPIRequestStamping).getValue(
                        GlobalVariable.NumofColm, rowExcel('refNumber'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'ambil lama waktu yang diperlukan hingga request menerima balikan'
            elapsedTime = ((respon.elapsedTime / 1000) + ' second')

            'ambil body dari hasil respons'
            responseBody = respon.responseBodyContent

            'panggil keyword untuk proses beautify dari respon json yang didapat'
            CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                    excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

            'write to excel response elapsed time'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

            if (code == 0) {
                if (GlobalVariable.checkStoreDB == 'Yes') {		
					'declare arraylist arraymatch'
					arrayMatch = []
					
                    'get totalMaterai from db'
                    String totalMaterai = CustomKeywords.'connection.APIFullService.getTotalMaterai'(conneSign, findTestData(
                            excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('refNumber')))

                    'looping dari 1 hingga 12'
                    for (i = 1; i <= 12; i++) {
                        'mengambil value db proses ttd'
                        prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, findTestData(excelPathAPIRequestStamping).getValue(
                                GlobalVariable.NumofColm, rowExcel('refNumber')))

                        'jika proses materai gagal (51)'
                        if ((prosesMaterai == 51) || (prosesMaterai == 61)) {
                            'Kasih delay untuk mendapatkan update db untuk error stamping'
                            WebUI.delay(3)

                            'get reason gailed error message untuk stamping'
                            errorMessageDB = CustomKeywords.'connection.Meterai.getErrorMessage'(conneSign, findTestData(
                                    excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('refNumber')))

                            'Write To Excel GlobalVariable.StatusFailed and errormessage'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (((findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan alasan ') + 
                                errorMessageDB.toString())

                            GlobalVariable.FlagFailed = 1

                            break
                        } else if ((prosesMaterai == 53) || (prosesMaterai == 63)) {
                            'Jika proses meterai sukses (53), berikan delay 3 sec untuk update di db'
                            WebUI.delay(3)

                            'Mengambil value total stamping dan total meterai'
                            totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
                                conneSign, findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('refNumber')))

                            'dibandingkan total meterai dan total stamp'
                            arrayMatch.add(WebUI.verifyMatch(totalMateraiAndTotalStamping[0], totalMateraiAndTotalStamping[
                                    1], false, FailureHandling.CONTINUE_ON_FAILURE))

                            'get trx from db'
                            String result = CustomKeywords.'connection.APIFullService.getAPIRequestStampingTrx'(conneSign, 
                                findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('refNumber')), 
                                totalMaterai)

                            'verify saldo terpotong'
                            arrayMatch.add(WebUI.verifyMatch(result, '-' + totalMaterai, false, FailureHandling.OPTIONAL))

							ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(
								conneSign, findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, rowExcel('refNumber')), 'Stamping')

							'lakukan loop untuk pengecekan data'
							for (int i = 0; i < (officeRegionBline.size() / 2); i++) {
								'verify business line dan office code'
								arrayMatch.add(WebUI.verifyMatch((officeRegionBline[i]).toString(), (officeRegionBline[(i +
										3)]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
							}
							
                            'jika data db tidak bertambah'
                            if (arrayMatch.contains(false)) {
                                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, (findTestData(excelPathAPIRequestStamping).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)

                                GlobalVariable.FlagFailed = 1
                            } else {
                                GlobalVariable.FlagFailed = 0
                            }
                            
                            break
                        } else {
                            'Jika bukan 51/61 dan 53/63, maka diberikan delay 20 detik'
                            WebUI.delay(10)

                            'Jika looping berada di akhir, tulis error failed proses stamping'
                            if (i == 12) {
                                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, ((((findTestData(excelPathAPIRequestStamping).getValue(
                                        GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + 
                                    ' dengan jeda waktu ') + (i * 12)) + ' detik ')

                                GlobalVariable.FlagFailed = 1

                                break
                            }
                        }
                    }
                }
                
                'Jika flag failed tidak 0'
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel Failed'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                            'Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                } else {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                            'Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusFailed)
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

def getErrorMsg(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ('<' + message) + '>')
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

