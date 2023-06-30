import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import java.net.InetAddress as InetAddress
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPISignDocument).columnNumbers

int splitnum = -1

'looping API Sign Document'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'declare variable array'
        ArrayList saldoBefore = [], saldoAfter = []

        saldoBefore = loginAdminGetSaldo(conneSign)
		
		println saldoBefore
		
        GlobalVariable.FlagFailed = 0

        'Inisialisasi otp, photo, ipaddress, dan total signed sebelumnya yang dikosongkan'
        String otp, photo, ipaddress

        ArrayList totalSignedBefore = [],  totalSignedAfter = []

        'Split dokumen id agar mendapat dokumenid 1 per 1 dengan case bulk'
        documentId = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 10).replace('[', '').replace(
            ']', '').replace('"', '').split(',', splitnum)

        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 19) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 20)
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 19) == 'Yes') {
            'Mengambil tenant dari setting'
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 17) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 17) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 18)
        }
        
        'check if mau menggunakan OTP yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 21) == 'Yes') {
            'request OTP dengan HIT API'

            'Constaint : Dokumen yang dipasang selalu dengan referal number di dokumen pertama.'
            respon_OTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : findTestData(
                            excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 26), ('phoneNo') : findTestData(
                            excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 8), ('email') : findTestData(excelPathAPISignDocument).getValue(
                            GlobalVariable.NumofColm, 11), ('refnumber') : ('"' + CustomKeywords.'connection.APIFullService.getRefNumber'(
                            conneSign, documentId[0])) + '"']))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(respon_OTP, 200, FailureHandling.OPTIONAL) == true) {
                'get status code'
                code_otp = WS.getElementPropertyValue(respon_OTP, 'status.code', FailureHandling.OPTIONAL)

                'jika codenya 0'
                if (code_otp == 0) {
                    'Dikasih delay 1 detik dikarenakan loading untuk mendapatkan OTP.'
                    WebUI.delay(1)

                    'Mengambil otp dari database'
                    otp = (('"' + CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPISignDocument).getValue(
                            GlobalVariable.NumofColm, 11).replace('"', ''))) + '"')
                } else {
                    'mengambil status code berdasarkan response HIT API'
                    message = WS.getElementPropertyValue(respon_OTP, 'status.message', FailureHandling.OPTIONAL)

                    'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, message)
                }
            } else {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.HITAPI Gagal'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        2) + ';') + GlobalVariable.ReasonFailedOTPError)
            }
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 21) == 'No') {
            'get otp dari excel'
            otp = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 22)
        }
        
        'check if mau menggunakan base64 untuk photo yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 23) == 'Yes') {
            'get base64 photo dari fungsi'
            photo = (('"' + phototoBase64(findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 15))) + 
            '"')
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 23) == 'No') {
            'get base64 photo salah dari excel'
            photo = (('"' + findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 15)) + '"')
        }
        
        'check if mau menggunakan ip address yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 24) == 'Yes') {
            'get ip address dari fungsi'
            ipaddress = (('"' + correctipAddress()) + '"')
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 24) == 'No') {
            'get ip address salah dari excel'
            ipaddress = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 13)
        }
        
        'looping berdasarkan ukuran dari dokumen id'
        for (int z = 0; z < documentId.size(); z++) {
            'Memasukkan input dari total signed'
            (totalSignedBefore[z]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, documentId[z])
        }
        
        'HIT API Sign'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Sign Document', [('callerId') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, 26), ('documentId') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, 10), ('email') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        11), ('password') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 12)
                    , ('ipAddress') : ipaddress, ('browserInfo') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        14), ('otp') : otp, ('selfPhoto') : photo]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'get status code'
            trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

            'Jika trxNonya tidak kosong dari response'
            if (trxNo != null) {
                'Input excel'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sign Document', 
                    5, GlobalVariable.NumofColm - 1, trxNo.toString().replace('[', '').replace(']', ''))
            }
            
            'jika codenya 0'
            if (code == 0) {
                'Loop berdasarkan jumlah documen id'
                for (int x = 0; x < documentId.size(); x++) {
                    signCount = CustomKeywords.'connection.APIFullService.getTotalSigner'(conneSign, documentId[x], findTestData(
                            excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

                    'Loop untuk check db update sign. Maksimal 200 detik.'
                    for (int v = 1; v <= 20; v++) {
                        'Mengambil total Signed setelah sign'
                        (totalSignedAfter[x]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, documentId[
                            x])

                        'Verify total signed sebelum dan sesudah. Jika sesuai maka break'
                        if ((totalSignedAfter[x]) == ((totalSignedBefore[x]) + Integer.parseInt(signCount))) {
                            WebUI.verifyEqual(totalSignedAfter[x], (totalSignedBefore[x]) + Integer.parseInt(signCount), 
                                FailureHandling.CONTINUE_ON_FAILURE)

                            'write to excel success'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sign Document', 
                                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                            'check Db'
                            if (GlobalVariable.checkStoreDB == 'Yes') {
                                'Panggil function responseAPIStoreDB dengan parameter totalSigned, ipaddress, dan array dari documentId'
                                responseAPIStoreDB(conneSign, ipaddress, documentId)
                            }
                            
                            break
                        } else if (v == 20) {
                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                                    2) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' dalam jeda waktu 200 detik ')

                            GlobalVariable.FlagFailed = 1
                        } else {
                            'Delay 10 detik.'
                            WebUI.delay(10)
                        }
                    }
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, message)

                GlobalVariable.FlagFailed = 1
            }
            
            if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 6) != '') && (GlobalVariable.FlagFailed == 
            1)) {
                'ambil trx no untuk displit'
                trxNo = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 6).split(',', -1)

                'looping per trx no'
                for (int i = 0; i < trxNo.size(); i++) {
                    'Mengambil tipe saldo yang tela hdigunakan'
                    checkTypeofUsedSaldo = CustomKeywords.'connection.APIFullService.getTypeUsedSaldo'(conneSign, trxNo[
                        i])

                    'Write To Excel GlobalVariable.StatusFailed dengan alasan bahwa saldo transaksi '
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                            2) + ';') + ' Transaksi dengan nomor ') + (trxNo[i])) + ' digunakan untuk ') + checkTypeofUsedSaldo)
                }
            }
			
			saldoAfter = loginAdminGetSaldo(conneSign)
			
			if (saldoBefore == saldoAfter) {
				'Write To Excel GlobalVariable.StatusFailed dengan alasan bahwa saldo transaksi '
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, ((((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
						2) + ';') + GlobalVariable.ReasonFailedNoneUI + ' terhadap pemotongan saldo ')

			}
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, message)

            GlobalVariable.FlagFailed = 1
        }
    }
}

def correctipAddress() {
    return InetAddress.localHost.hostAddress
}

def phototoBase64(String filePath) {
    return CustomKeywords.'customizekeyword.ConvertFile.base64File'(filePath)
}

def responseAPIStoreDB(Connection conneSign, String ipaddress, String[] documentId) {
    'get current date'
    currentDate = new Date().format('yyyy-MM-dd')

    'declare arraylist arraymatch'
    ArrayList<String> arrayMatch = []

    'loop berdasarkan dokumen id'
    for (int i = 0; i < documentId.size(); i++) {
        'get data from db'
        arrayIndex = 0

        println(documentId[i])

        println(findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 11))

        'Array result. Value dari db'
        result = CustomKeywords.'connection.APIFullService.getSign'(conneSign, (documentId[i]).replace('"', ''), findTestData(
                excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

        println(result)

        WebUI.delay(30)

        'verify qty dalam transaksi. Jika done = 1'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

        'Check liveness compare adalah 0 dikarenakan trxNo yang didapat adalah transaksi untuk liveness compare.'

        'Ini perlu dideklarasi dikarenakan jika 2 dokumen, trxNo tetap 1, sehingga perlu diflag apakah dia sudah check trxnya atau belum'
        checkLivenessCompare = 0

        'Jika trxNonya tidak kosong dan checkLivenessComparenya 0'
        if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 6) != '') && (checkLivenessCompare == 
        0)) {
            'verify trx no. Jika sesuai, maka'
            if (WebUI.verifyEqual(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    6), FailureHandling.CONTINUE_ON_FAILURE)) {
                'Ditambah 1'
                checkLivenessCompare++

                'arrayMatchnya diinput true'
                arrayMatch.add(true)
            }
        } else {
            'Tambah dari arrayIndex'
            arrayIndex++
        }
        
        'verify request status. 3 = done'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '3', false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify ref number yang tertandatangan'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, 
                    (documentId[i]).replace('"', '')), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify ip address'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], ipaddress.replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify user browser'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    14).replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify callerId'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    26).replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify signing proces. 0 berarti tidak ada proses tanda tangan lagi.'
        arrayMatch.add(WebUI.verifyEqual(result[arrayIndex++], 0, FailureHandling.CONTINUE_ON_FAILURE))

        'verify tanggal tanda tangan'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], currentDate, false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify api key'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.api_key, false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify tenant'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.Tenant, false, FailureHandling.CONTINUE_ON_FAILURE))
    }
    
    'jika data db tidak sesuai dengan excel'
    if (arrayMatch.contains(false)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedStoredDB)
    }
}

def loginAdminGetSaldo(Connection conneSign) {
    ArrayList totalSaldo = []

    sheet = 'API Sign Document'

	docid = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 10).replace('"', '').replace('[', '').replace(']', '').split(',' -1)
	noKontrak = ''
	for (int i = 1 ; i <= docid.size(); i++) {
		if (noKontrak != '') {
			noKontrak = noKontrak + ';'
		}
		noKontrak = noKontrak + CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign,docid[i-1])
	}

    'Call test Case untuk login sebagai admin wom admin client'
    WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathAPISignDocument, ('sheet') : sheet], FailureHandling.STOP_ON_FAILURE)

    'klik button saldo'
    WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))

    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), 'Vida', false)

    'get total div di Saldo'
    variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    'looping berdasarkan total div yang ada di saldo'
    for (int c = 1; c <= variableDivSaldo.size(); c++) {
        'modify object mengenai find tipe saldo'
        modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + 
            (c + 1)) + ']/div/div/div/div/div[1]', true)

        'verifikasi label saldonya '
        if (WebUI.verifyElementText(modifyObjectFindSaldoSign, 'TTD', FailureHandling.OPTIONAL)) {
            'modify object mengenai ambil total jumlah saldo'
            modifyObjecttotalSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_countsaldo'), 'xpath', 'equals', 
                ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + (c + 1)) + ']/div/div/div/div/div[2]', 
                true)

            'mengambil total saldo yang pertama'
            totalSaldo.add(WebUI.getText(modifyObjecttotalSaldoSign))
			break
        }
    }
	
	vendor = 'ESIGN/ADINS'
	
	if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,22) != '') {
    isSplit = CustomKeywords.'connection.APIFullService.getSplitLivenessFaceCompareBill'(conneSign)

    if (isSplit == '1') {
        paymentType = ['Liveness', 'Face Compare']
    } else {
        paymentType = ['Liveness Face Compare']
    }
	}
	else {
		paymentType = ['OTP']
	}
	
    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), vendor, false)

    'get total div di Saldo'
    variableDivSaldo = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    for (int i = 0; i < paymentType.size(); i++) {
        'looping berdasarkan total div yang ada di saldo'
        for (int c = 1; c <= variableDivSaldo.size(); c++) {
            'modify object mengenai find tipe saldo'
            modifyObjectFindSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_saldo'), 'xpath', 'equals', 
                ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + (c + 1)) + ']/div/div/div/div/div[1]', 
                true)

            'verifikasi label saldonya '
            if (WebUI.verifyElementText(modifyObjectFindSaldoSign, paymentType[i], FailureHandling.OPTIONAL)) {
                'modify object mengenai ambil total jumlah saldo'
                modifyObjecttotalSaldoSign = WebUI.modifyObjectProperty(findTestObject('Saldo/lbl_countsaldo'), 'xpath', 
                    'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + 
                    (c + 1)) + ']/div/div/div/div/div[2]', true)

                'mengambil total saldo yang pertama'
                totalSaldo.add(WebUI.getText(modifyObjecttotalSaldoSign))
				break
            }
        }
    }

	'Memanggil DocumentMonitoring untuk dicheck apakah documentnya sudah masuk'
	WebUI.callTestCase(findTestCase('DocumentMonitoring/VerifyDocumentMonitoring'), [('excelPathFESignDocument') : excelPathAPISignDocument
			, ('sheet') : sheet, ('linkDocumentMonitoring') : 'Not Used', ('nomorKontrak') : noKontrak], FailureHandling.CONTINUE_ON_FAILURE)

    'return total saldo awal'
    return totalSaldo
}

