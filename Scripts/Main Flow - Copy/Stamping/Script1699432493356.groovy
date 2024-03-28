import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.testobject.ResponseObject

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

documentId = findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', -1)

String refNumber = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, documentId[0])

int prosesMaterai

'mengambil value db proses ttd'
prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, refNumber)

    'ubah vendor stamping jika diperlukan '
    if ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')).length() > 
    0) && (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')) != 
    'No')) {
        'ambil idLov untuk diupdate secara otomatis ke DB'
        int idLov = CustomKeywords.'connection.ManualStamp.getIdLovVendorStamping'(conneSign, findTestData(excelPathStamping).getValue(
         GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')))
		
        'lakukan update vendor stamping yang akan dipakai'
        CustomKeywords.'connection.UpdateData.updateVendorStamping'(conneSign, idLov)
    }
    
    String valueRefNum

    String nomorKontrakDocument

    'pasang flag error DMS'
    int flagErrorDMS = 0

    GlobalVariable.base_url = findTestData('Login/Setting').getValue(7, 2)

    if ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Option for Stamp Document :')) == 
    'API Stamping External') || (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Option for Stamp Document :')) == 
    'API Stamping Normal')) {
        'penggunaan versi 3.1.0 atau 3.0.0 dengan 1 api yang sama'
        if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Option for Stamp Document :')) == 
        'API Stamping External') {
            'set base url menjadi v.3.1.0'
            GlobalVariable.base_url = (GlobalVariable.base_url + '/services/external/document/requestStamping')

            'setting value body di api berdasarkan versi stamp nya'
            valueRefNum = '"refNumber"'
        } else if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Option for Stamp Document :')) == 
        'API Stamping Normal') {
            'set base url menjadi v.3.0.0'
            GlobalVariable.base_url = (GlobalVariable.base_url + '/services/confins/stampduty/attachMeteraiPajakku')

            'setting value body di api berdasarkan versi stamp nya'
            valueRefNum = '"agreementNo"'
        }
        
        nomorKontrakDocument = (('"' + refNumber) + '"')

        callerId = findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))

		'Jika flag tenant no'
		if ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code (Stamp)'))) == 'No') {
			'set tenant kosong'
			GlobalVariable.Tenant = (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code (Stamp)')))
		} else if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code (Stamp)')) == 'Yes') {
			'Input tenant'
			GlobalVariable.Tenant = findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))
		}
		
		'check if mau menggunakan api_key yang salah atau benar'
		if ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key (Stamp)'))) == 'Yes') {
			'get api key dari db'
			GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
		} else if ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key (Stamp)'))) == 'No') {
			'get api key salah dari excel'
			GlobalVariable.api_key = (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key (Stamp)')))
		}
		
        'HIT API stamping'
        respon = WS.sendRequest(findTestObject('Flow Stamping', [('callerId') : callerId, ('valueRefNum') : valueRefNum, ('refNumber') : nomorKontrakDocument]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika code 0'
            if (code == 0) {
                'looping dari 1 hingga 12'
                for (i = 1; i <= 12; i++) {
                    'mengambil value db proses ttd'
                    prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, nomorKontrakDocument.replace(
                            '"', ''))

                    'jika proses materai gagal (51)'
                    if (((prosesMaterai == 51) || (prosesMaterai == 61)) && (flagErrorDMS == 0)) {
                        'Kasih delay untuk mendapatkan update db untuk error stamping'
                        WebUI.delay(3)

                        'get reason gailed error message untuk stamping'
                        errorMessageDB = CustomKeywords.'connection.Meterai.getErrorMessage'(conneSign, nomorKontrakDocument.replace(
                                '"', ''))

                        'Write To Excel GlobalVariable.StatusFailed and errormessage'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan alasan ') + 
                            errorMessageDB.toString())

                        GlobalVariable.FlagFailed = 1

                        if (errorMessageDB.toString().contains('upload DMS')) {
							flagErrorDMS = 1
							
							continue
                        } else {
							break
                        }
                    } else if (((prosesMaterai == 53) || (prosesMaterai == 63)) || (flagErrorDMS == 1)) {
                        'Jika proses meterai sukses (53), berikan delay 3 sec untuk update di db'
                        WebUI.delay(3)

                        'Mengambil value total stamping dan total meterai'
                        totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
                            conneSign, nomorKontrakDocument.replace('"', ''))

                        'declare arraylist arraymatch'
                        arrayMatch = []

                        'dibandingkan total meterai dan total stamp'
                        arrayMatch.add(WebUI.verifyMatch(totalMateraiAndTotalStamping[0], totalMateraiAndTotalStamping[1], 
                                false, FailureHandling.CONTINUE_ON_FAILURE))

						GlobalVariable.eSignData['VerifikasiMeterai'] = GlobalVariable.eSignData['VerifikasiMeterai'] + Integer.parseInt(totalMateraiAndTotalStamping[0])

						ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(
							conneSign, nomorKontrakDocument, 'Stamping')

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
                                GlobalVariable.StatusFailed, (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
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
                                GlobalVariable.StatusFailed, ((((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan jeda waktu ') + 
                                (i * 12)) + ' detik ')

                            GlobalVariable.FlagFailed = 1
                        }
                    }
                }
                
                'Jika flag failed tidak 0'
                if (GlobalVariable.FlagFailed == 0) {
                    if (flagErrorDMS == 1) {
                        'write to excel Failed'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                                'Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusFailed)
                    } else {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                                'Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                    
                    'Mengambil value total stamping dan total meterai'
                    totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(conneSign, 
                        nomorKontrakDocument.replace('"', ''))

                    if (((totalMateraiAndTotalStamping[0]) != '0') && (prosesMaterai != 63 || prosesMaterai != 53)) {
                        'Call verify meterai'
                        WebUI.callTestCase(findTestCase('Main Flow - Copy/verifyMeterai'), [('excelPathMeterai') : excelPathStamping
                         , ('sheet') : sheet, ('noKontrak') : nomorKontrakDocument.replace('"', ''), ('linkDocumentMonitoring') : linkDocumentMonitoring
                        , ('CancelDocsStamp') : CancelDocsStamp], FailureHandling.OPTIONAL)
                    }
                }
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            getErrorMessageAPI(respon)
        }
    } else if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Option for Stamp Document :')) == 
    'Start Stamping') {
		if (GlobalVariable.ErrorType.size() > 0) {
			GlobalVariable.FlagFailed = 1
		} else {
		'Memanggil DocumentMonitoring untuk dicheck apakah documentnya sudah masuk'
		WebUI.callTestCase(findTestCase('Main Flow - Copy/VerifyDocumentMonitoring'), [('excelPathFESignDocument') : excelPathStamping
            , ('sheet') : sheet, ('linkDocumentMonitoring') : '', ('nomorKontrak') : refNumber, ('isStamping') : 'Yes'
            , ('CancelDocsStamp') : CancelDocsStamp], FailureHandling.CONTINUE_ON_FAILURE)
		
		'Mengambil value total stamping dan total meterai'
		totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(conneSign,
			refNumber)

		'mengambil value db proses ttd'
		prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, refNumber)
		
		if (((totalMateraiAndTotalStamping[0]) != '0') && (prosesMaterai != 63 || prosesMaterai != 53)) {
			'Call verify meterai'
			WebUI.callTestCase(findTestCase('Main Flow - Copy/verifyMeterai'), [('excelPathMeterai') : excelPathStamping
			 , ('sheet') : sheet, ('noKontrak') : refNumber, ('linkDocumentMonitoring') : linkDocumentMonitoring
			, ('CancelDocsStamp') : CancelDocsStamp], FailureHandling.CONTINUE_ON_FAILURE)
		}
		}
    }
	
	if (GlobalVariable.FlagFailed == 0) {
		'write to excel success'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
				'Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
	} else {
		'write to excel success'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
				'Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusFailed)
	}
	
	'inisialisasi variable untuk looping. Looping diperlukan untuk break/continue'
	forLoopingWithBreakAndContinue = 1
	
	'looping'
	for (o = 0; o < forLoopingWithBreakAndContinue; o++) {
		'mengambil value db proses ttd'
		prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, refNumber)
		
		if (prosesMaterai == 1 || prosesMaterai == 51 || prosesMaterai == 321 || prosesMaterai == 521 || prosesMaterai == 61) {
		if (findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Using Retry Stamping Feature ?')) == 'Yes') {
			'Memanggil DocumentMonitoring untuk dicheck apakah documentnya sudah masuk'
			WebUI.callTestCase(findTestCase('Main Flow - Copy/VerifyDocumentMonitoring'), [('excelPathFESignDocument') : excelPathStamping
					, ('sheet') : sheet, ('linkDocumentMonitoring') : '', ('nomorKontrak') : refNumber, ('isStamping') : ''
					, ('CancelDocsStamp') : CancelDocsStamp, ('retryStamping') : findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Using Retry Stamping Feature ?'))], FailureHandling.CONTINUE_ON_FAILURE)
			}
		}
	}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(excelPathStamping).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + '<') + 
        message) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

