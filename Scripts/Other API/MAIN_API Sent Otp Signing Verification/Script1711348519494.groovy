import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPISentOTPSigning).columnNumbers

'looping API Sent OTP Signing'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
		GlobalVariable.FlagFailed = 0
	
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPISentOTPSigning, GlobalVariable.NumofColm, 
            rowExcel('Use Correct base Url'))

        'mengambil reset otp request numbernya awal berapa'
        resetOTPRequestNum = Integer.parseInt(CustomKeywords.'connection.APIFullService.getResetCodeRequestNum'(conneSign, 
                findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('username'))))

        'mengambil nilai otp awal baik berisi ataupun kosong'
        otpCode = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPISentOTPSigning).getValue(
                GlobalVariable.NumofColm, rowExcel('username')))

        println(resetOTPRequestNum)

        println(otpCode)

        WebUI.delay(5)

        'inisialisasi arrayList'
        ArrayList documentId = []

        ArrayList list = []

        ArrayList listDocId = []

        'Mengambil document id dari excel dan displit'
        documentId = findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('$documentId')).split(
            ';', -1)

        for (int q = 0; q < documentId.size(); q++) {
            list.add(('"' + documentId.get(q)) + '"')

            if (q == 0) {
                listDocId.add(list.get(q))
            } else {
                listDocId.set(0, (listDocId.get(0) + ',') + list.get(q))
            }
        }
        
        String listDoc

        if (listDocId.empty) {
            'ubah menjadi string'
            listDoc = ''
        } else {
            'ubah menjadi string'
            listDoc = listDocId.toString().replace('[', '').replace(']', '')
        }
        
        'HIT API'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPathAPISentOTPSigning).getValue(
                        GlobalVariable.NumofColm, rowExcel('username')), ('password') : findTestData(excelPathAPISentOTPSigning).getValue(
                        GlobalVariable.NumofColm, rowExcel('password'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Use True Token')).equalsIgnoreCase(
                'No')) {
                'Parsing token menjadi GlobalVariable'
                GlobalVariable.token = findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel(
                        'Wrong Token'))

                GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')
            } else {
                'Parsing token menjadi GlobalVariable'
                GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')
            }
            
            String sendingPoint = ''

            if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('sendingPointOption')).length() == 
            0) {
                sendingPoint = ''
            } else {
                sendingPoint = ((', "sendingPointOption" : "' + findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 
                    rowExcel('sendingPointOption'))) + '"')
            }
            
            'HIT API'
            respon = WS.sendRequest(findTestObject('Postman/Sent Otp Signing Verification', [('callerId') : findTestData(
                            excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('$callerId')), ('phoneNo') : findTestData(
                            excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('$phoneNo')), ('vendorCode') : findTestData(
                            excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('$vendorCode')), ('tenantCode') : findTestData(
                            excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('$tenantCode')), ('listDocumentId') : listDoc
                        , ('sendingPointOption') : sendingPoint]))
			
            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
                'get status code'
                code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)
				
				'ambil lama waktu yang diperlukan hingga request menerima balikan'
				elapsedTime = ((respon.elapsedTime / 1000) + ' second')
	
				'ambil body dari hasil respons'
				responseBody = respon.responseBodyContent
	
				'panggil keyword untuk proses beautify dari respon json yang didapat'
				CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
						excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
				'write to excel response elapsed time'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
					1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
                'declare arraylist arraymatch'
                ArrayList arrayMatch = []

                'jika codenya 0'
                if (code == 0) {
                    if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('$vendorCode')).toLowerCase() == 
                    'privy') {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                        'input di excel mengenai trxno yang telah didapat'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                                'OTP') - 1, GlobalVariable.NumofColm - 1, 'OTP tidak masuk ke DB(PRIVY)')
                    } else {
                        'ambil otp baru dari DB'
                        newOTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPISentOTPSigning).getValue(
                                GlobalVariable.NumofColm, rowExcel('username')))

                        'ambil resetnum dari DB'
                        newResetNum = Integer.parseInt(CustomKeywords.'connection.APIFullService.getResetCodeRequestNum'(
                                conneSign, findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel(
                                        'username'))))

                        'input di excel mengenai trxno yang telah didapat'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                                'OTP') - 1, GlobalVariable.NumofColm - 1, newOTP)

                        perDoc = findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('$documentId')).split(
                            ';', -1)

						'inisialisasi variable helperquery dan datetime'
                        ArrayList helperQuery = [], dateTime = []

						'increment 1'
                        increment = 1

                        if (GlobalVariable.checkStoreDB == 'Yes') {
							'looping based on per documentid'
                            for (loopingDoc = 0; loopingDoc < perDoc.size(); loopingDoc++) {
								'get ref number dan sorting request date'
                                helperQuery = (helperQuery + CustomKeywords.'connection.APIOnly.getRefNumberAndSortDescending'(
                                    conneSign, perDoc[loopingDoc]))

                                dateTime.add((helperQuery[increment]).toString())

                                increment += 2
                            }
                            
                            Collections.sort(dateTime, Collections.reverseOrder())

                            increment = 1

                            String refNumber = ''

                            for (loopingGetRefNum = 0; loopingGetRefNum < (helperQuery.size() / 2); loopingGetRefNum++) {
                                if ((dateTime[0]) == (helperQuery[increment])) {
                                    refNumber = (helperQuery[(increment - 1)])

                                    break
                                } else {
									increment += 2
								}
                            }

                            ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(
                                conneSign, refNumber, 'Signing')

                            'lakukan loop untuk pengecekan data'
                            for (int i = 0; i < (officeRegionBline.size() / 3); i++) {
                                'verify business line dan office code'
                                arrayMatch.add(WebUI.verifyMatch((officeRegionBline[i]).toString(), (officeRegionBline[(i + 
                                        3)]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
                            }
                            
							checkSaldoWAOrSMS(conneSign)
							
                            'verify otp code tidak sama'
                            arrayMatch.add(WebUI.verifyEqual(newResetNum, resetOTPRequestNum + 1, FailureHandling.CONTINUE_ON_FAILURE))

                            if (arrayMatch.contains(false)) {
                                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                            } else {
								if (GlobalVariable.FlagFailed == 0) {
                                'write to excel success'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
								}
                            }
                        }
                    }
                } else {
                    getErrorMessageAPI(respon)
                }
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(responLogin, 'error_description', FailureHandling.OPTIONAL).toString()

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + '<') + message) + '>')

            GlobalVariable.FlagFailed = 1
        }
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
            '-', '') + ';') + '<') + message) + '>')

    GlobalVariable.FlagFailed = 1
}

def checkSaldoWAOrSMS(Connection conneSign) {
	String vendor = findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('$vendorCode'))
	
	ArrayList<String> balmut = []

	String tipeSaldo

	emailServiceOnVendor = CustomKeywords.'connection.DataVerif.getEmailServiceAsVendorUser'(conneSign, findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('username')))

	fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('username')))

	mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('$tenantCode')))

	notifTypeDB = CustomKeywords.'connection.APIFullService.getWASMSFromNotificationTypeOTP'(conneSign, findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('username')), 'OTP_SIGN_NORMAL', GlobalVariable.Tenant)

	if (vendor.equalsIgnoreCase('Privy')) {
		mustUseWaFirst = '0'

		emailServiceOnVendor = '0'
	}
	
	if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('sendingPointOption')).toString().contains('WA')) {
		tipeSaldo = 'WhatsApp Message'
		
		'menggunakan saldo wa'
		balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)
		
		if (balmut.size() == 0) {
		'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
		(findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
		'-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')
		
		GlobalVariable.FlagFailed = 1
		} else {
			if (balmut[9] != '-1') {
				'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				(findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
				'-', '') + ';') + 'Tidak terjadi transaksi.')
				
				GlobalVariable.FlagFailed = 1
			}
		}
	}
	
	if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('sendingPointOption')).toString().contains('SMS')) {
		tipeSaldo = 'OTP'
		
		'menggunakan saldo sms'
		balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)
		
		if (balmut.size() == 0) {
		'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
		(findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
		'-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via SMS')
		
		GlobalVariable.FlagFailed = 1
		} else {
			if (balmut[9] != '-1') {
				'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				(findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
				'-', '') + ';') + 'Tidak terjadi transaksi.')
				
				GlobalVariable.FlagFailed = 1
			}
		}
	}
	
	WebUI.comment(balmut.toString())
	
	/*
	if (notifTypeDB == '0' || notifTypeDB == 'Level Tenant') {
		if (mustUseWAFirst == '1') {
			tipeSaldo = 'WhatsApp Message'

			'menggunakan saldo wa'
			balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

			if (balmut.size() == 0) {
				'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
					(findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
						'-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')

				GlobalVariable.FlagFailed = 1
			} else {
				penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
			}
		} else {
			if (emailServiceOnVendor == '1') {
				useWAMessage = CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, GlobalVariable.Tenant)

				if (useWAMessage == '1') {
					tipeSaldo = 'WhatsApp Message'

					'menggunakan saldo wa'
					balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

					if (balmut.size() == 0) {
						'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
								rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')

						GlobalVariable.FlagFailed = 1
					} else {
						penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
					}
				} else if (useWAMessage == '0') {
					'ke sms'
					tipeSaldo = 'OTP'

					balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

					if (balmut.size() == 0) {
						'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
								rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP')

						(GlobalVariable.eSignData['VerifikasiOTP']) = ((GlobalVariable.eSignData['VerifikasiOTP']) - 1)

						GlobalVariable.FlagFailed = 1
					} else {
						penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
					}
				}
			} else {
				'ke sms'
				tipeSaldo = 'OTP'

				balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

				if (balmut.size() == 0) {
					'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP')

					(GlobalVariable.eSignData['VerifikasiOTP']) = ((GlobalVariable.eSignData['VerifikasiOTP']) - 1)

					GlobalVariable.FlagFailed = 1
				} else {
					penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
				}
			}
		}
	} else {
		if (vendor.equalsIgnoreCase('Privy')) {
			tipeSaldo = 'OTP'
		} else {
			tipeSaldo = notifTypeDB
		}
		
		'menggunakan saldo wa'
		balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

		'jika balmutnya tidak ada value'
		if (balmut.size() == 0) {
			'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				((findTestData(excelPathFESignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
					'-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via ') +
				tipeSaldo.replace(' Message', '').replace(' Notif', ''))
		} else {
			'penggunaan saldo didapat dari ikuantitaas query balmut'
			penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
		}
	}
*/
}