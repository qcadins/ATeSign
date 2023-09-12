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
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declafe split'
int splitnum = -1

documentId = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', 
    splitnum)

for (o = 0; o < documentId.size(); o++) {
    documentIdInput = (('["' + (documentId[o])) + '"]')

    String refNumber = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, documentId[o])

    'ambil nama vendor dari DB'
    String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, refNumber)

    'ambil tenant dan vendor code yang akan digunakan document'
    ArrayList tenantVendor = CustomKeywords.'connection.DataVerif.getTenantandVendorCode'(conneSign, documentIdInput.replace(
            '"', '').replace('[', '').replace(']', ''))

    'setting vendor otp dimatikan/diaktifkan'
    if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Enable User Vendor OTP? (Sign External)')).length() > 
    0) {
        'update setting vendor otp ke table di DB'
        CustomKeywords.'connection.UpdateData.updateVendorOTP'(conneSign, tenantVendor[1], findTestData(excelPathAPISignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('Enable User Vendor OTP? (Sign External)')))
    }
    
    'setting tenant otp dimatikan/diaktifkan'
    if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Enable Need OTP for signing? (Sign External)')).length() > 
    0) {
        'update setting otp ke table di DB'
        CustomKeywords.'connection.UpdateData.updateTenantOTPReq'(conneSign, tenantVendor[0], findTestData(excelPathAPISignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('Enable Need OTP for signing? (Sign External)')))
    }
    
    'setting tenant password dimatikan/diaktifkan'
    if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Enable Need Password for signing? (Sign External)')).length() > 
    0) {
        'update setting pass tenant ke table di DB'
        CustomKeywords.'connection.UpdateData.updateTenantPassReq'(conneSign, tenantVendor[0], findTestData(excelPathAPISignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('Enable Need Password for signing? (Sign External)')))
    }
    
    HashMap<String, String> saldoBefore = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), [('excel') : excelPathAPISignDocument
            , ('sheet') : sheet, ('vendor') : vendor], FailureHandling.CONTINUE_ON_FAILURE)

    ArrayList paymentTypeUsed = getPaymentTypeUsed(conneSign, vendor)

    GlobalVariable.FlagFailed = 0

    'Inisialisasi otp, photo, ipaddress, dan total signed sebelumnya yang dikosongkan'
    String otp, photo, ipaddress

    ArrayList totalSignedBefore = [],totalSignedAfter = [], flaggingOTP = []

    'Split dokumen id agar mendapat dokumenid 1 per 1 dengan case bulk'
    documentId = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(
        ',', splitnum)

    'Jika flag tenant no'
    if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code (Sign External)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'No') {
        'set tenant kosong'
        GlobalVariable.Tenant = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code (Sign External)')).split(
            ';', -1)[GlobalVariable.indexUsed])
    } else if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code (Sign External)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
        'Input tenant'
        GlobalVariable.Tenant = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))
    }
    
    'check if mau menggunakan api_key yang salah atau benar'
    if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key (Sign External)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
        'get api key dari db'
        GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
    } else if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key (Sign External)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'No') {
        'get api key salah dari excel'
        GlobalVariable.api_key = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key (Sign External)')).split(
            ';', -1)[GlobalVariable.indexUsed])
    }
    
    flaggingOTP = CustomKeywords.'connection.DataVerif.getParameterFlagPassOTP'(conneSign, (documentId[0]).toString())

	println vendor
	println findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use correct OTP From Database (Sign External)')).split(
        ';', -1)[GlobalVariable.indexUsed]
    if (vendor.equalsIgnoreCase('Privy')) {
        'request OTP dengan HIT API'

        'Constraint : Dokumen yang dipasang selalu dengan referal number di dokumen pertama.'
        respon_OTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : findTestData(
                        excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('callerId (Sign External)')).split(
                        ';', -1)[GlobalVariable.indexUsed], ('phoneNo') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('phoneNo (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]
                    , ('email') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(
                        ';', -1)[GlobalVariable.indexUsed], ('refnumber') : '"' + CustomKeywords.'connection.APIFullService.getRefNumber'(
                        conneSign, documentId[0]) + '"']))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon_OTP, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code_otp = WS.getElementPropertyValue(respon_OTP, 'status.code', FailureHandling.OPTIONAL)

            'jika codenya 0'
            if (code_otp == 0) {
                'Dikasih delay 50 detik dikarenakan loading untuk mendapatkan OTP Privy via SMS.'
                WebUI.delay(50)

                otp = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong OTP (Sign External)')).split(
                    ';', -1)[GlobalVariable.indexUsed])
            }
        }
    } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use correct OTP From Database (Sign External)')).split(
        ';', -1)[GlobalVariable.indexUsed] == 'Yes') {
        'check if mau menggunakan OTP yang salah atau benar'

        'request OTP dengan HIT API'

        'Constraint : Dokumen yang dipasang selalu dengan referal number di dokumen pertama.'
        respon_OTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : findTestData(
                        excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('callerId (Sign External)')).split(
                        ';', -1)[GlobalVariable.indexUsed], ('phoneNo') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('phoneNo (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]
                    , ('email') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(
                        ';', -1)[GlobalVariable.indexUsed], ('refnumber') : ('"' + CustomKeywords.'connection.APIFullService.getRefNumber'(
                        conneSign, documentId[0])) + '"']))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon_OTP, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code_otp = WS.getElementPropertyValue(respon_OTP, 'status.code', FailureHandling.OPTIONAL)

            'jika codenya 0'
            if (code_otp == 0) {
                'Dikasih delay 1 detik dikarenakan loading untuk mendapatkan OTP.'
                WebUI.delay(3)

                'Mengambil otp dari database'
                otp = '"' + CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, (findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]).replace('"', '')) + '"'
            } else {
                getErrorMessageAPI(respon_OTP)
            }
        } else {
            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.HITAPI Gagal'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                ';') + GlobalVariable.ReasonFailedOTPError)
        }
    } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use correct OTP From Database (Sign External)')).split(
        ';', -1)[GlobalVariable.indexUsed] == 'No') {
        'get otp dari excel'
        otp = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong OTP (Sign External)')).split(
            ';', -1)[GlobalVariable.indexUsed])
    }
    
    if (otp.replace('"', '').length() > 0) {
        'check if mau menggunakan base64 untuk photo yang salah atau benar'
        if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Base64 SelfPhoto (Sign External)')).split(
            ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
            'get base64 photo dari fungsi'
            photo = (('"' + phototoBase64(findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel(
                        'SelfPhoto (Sign External)')).split(';', -1)[GlobalVariable.indexUsed])) + '"')
        } else if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Base64 SelfPhoto (Sign External)')).split(
            ';', -1)[GlobalVariable.indexUsed]) == 'No') {
            'get base64 photo salah dari excel'
            photo = (('"' + (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('SelfPhoto (Sign External)')).split(
                ';', -1)[GlobalVariable.indexUsed])) + '"')
        }
        
        'check if mau menggunakan ip address yang salah atau benar'
        if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct ipAddress (Sign External)')).split(
            ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
            'get ip address dari fungsi'
            ipaddress = (('"' + correctipAddress()) + '"')
        } else if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct ipAddress (Sign External)')).split(
            ';', -1)[GlobalVariable.indexUsed]) == 'No') {
            'get ip address salah dari excel'
            ipaddress = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('ipAddress (Sign External)')).split(
                ';', -1)[GlobalVariable.indexUsed])
        }
        
        'looping berdasarkan ukuran dari dokumen id'
        for (int z = 0; z < documentId.size(); z++) {
            'Memasukkan input dari total signed'	
            (totalSignedBefore[z]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, (documentId[z]).toString())
        }
        
        'HIT API Sign'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Sign Document', [('callerId') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]
                    , ('documentId') : documentIdInput, ('email') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        rowExcel('email (Sign External)')).split(';', -1)[GlobalVariable.indexUsed], ('password') : ('"' + 
                    findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Password Signer'))) + 
                    '"', ('ipAddress') : ipaddress, ('browserInfo') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        rowExcel('browserInfo (Sign External)')).split(';', -1)[GlobalVariable.indexUsed], ('otp') : otp
                    , ('selfPhoto') : photo]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'get status code'
            trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

            'Jika trxNonya tidak kosong dari response'
            if (trxNo != null) {
                'Input excel'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('trxNos') - 
                    1, GlobalVariable.NumofColm - 1, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        rowExcel('trxNos')) + ';') + trxNo.toString().replace('[', '').replace(']', ''))
            }
            
            'jika codenya 0'
            if (code == 0) {
                'Loop berdasarkan jumlah documen id'
                for (int x = 0; x < documentId.size(); x++) {
                    signCount = CustomKeywords.'connection.APIFullService.getTotalSigner'(conneSign, (documentId[x]).toString(), 
                        (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(
                            ';', -1)[GlobalVariable.indexUsed]).replace('"', ''))

                    'Loop untuk check db update sign. Maksimal 200 detik.'
                    for (int v = 1; v <= 20; v++) {
                        'Mengambil total Signed setelah sign'
                        (totalSignedAfter[x]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, documentId[
                            x])

                        'Verify total signed sebelum dan sesudah. Jika sesuai maka break'
                        if ((totalSignedAfter[x]) == ((totalSignedBefore[x]) + Integer.parseInt(signCount))) {
                            WebUI.verifyEqual(totalSignedAfter[x], (totalSignedBefore[x]) + Integer.parseInt(signCount), 
                                FailureHandling.CONTINUE_ON_FAILURE)

                            if (GlobalVariable.FlagFailed == 0) {
                                'write to excel success'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                            }
                            
                            'Write to excel mengenai Document ID'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                                rowExcel('Result Count Success') - 1, GlobalVariable.NumofColm - 1, (findTestData(excelPathAPISignDocument).getValue(
                                    GlobalVariable.NumofColm, rowExcel('Result Count Success')) + ';') + 'Success : 1')

                            'Write to excel mengenai Document ID'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                                rowExcel('Result Count Failed') - 1, GlobalVariable.NumofColm - 1, (findTestData(excelPathAPISignDocument).getValue(
                                    GlobalVariable.NumofColm, rowExcel('Result Count Failed')) + ';') + 'Failed : 0')

                            'check Db'
                            if (GlobalVariable.checkStoreDB == 'Yes') {
                                'Panggil function responseAPIStoreDB dengan parameter totalSigned, ipaddress, dan array dari documentId'
                                responseAPIStoreDB(conneSign, ipaddress, documentId, trxNo)
                            }
                            
                            if (trxNo != null) {
                                'ambil trx no untuk displit'
                                trxNo = trxNo.split(', ', -1)

                                'Diberikan delay dengan pembuatan trx no di db sebesar 5 detik'
                                WebUI.delay(5)

                                'looping per trx no'
                                for (int i = 0; i < trxNo.size(); i++) {
                                    'Mengambil tipe saldo yang telah digunakan'
                                    checkTypeofUsedSaldo = CustomKeywords.'connection.APIFullService.getTypeUsedSaldo'(conneSign, 
                                        trxNo[i])

                                    if (GlobalVariable.FlagFailed == 1) {
                                        'Write To Excel GlobalVariable.StatusFailed dengan alasan bahwa saldo transaksi '
                                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                            GlobalVariable.StatusFailed, ((((findTestData(excelPathAPISignDocument).getValue(
                                                GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ' Transaksi dengan nomor ') + 
                                            ('<' + (trxNo[i]))) + '> digunakan untuk ') + checkTypeofUsedSaldo)
                                    }
                                }
                            }
                            
                            'Memanggil DocumentMonitoring untuk dicheck apakah documentnya sudah masuk'
                            WebUI.callTestCase(findTestCase('Main Flow/VerifyDocumentMonitoring'), [('excelPathFESignDocument') : excelPathAPISignDocument
                                    , ('sheet') : sheet, ('linkDocumentMonitoring') : 'Not Used', ('nomorKontrak') : refNumber], 
                                FailureHandling.CONTINUE_ON_FAILURE)

                            HashMap<String, String> saldoAfter = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), 
                                [('excel') : excelPathAPISignDocument, ('sheet') : sheet, ('vendor') : vendor], FailureHandling.CONTINUE_ON_FAILURE)

                            'looping payment type used'
                            for (l = 0; l < paymentTypeUsed.size(); l++) {
                                'looping untuk mendapatkan key dari hashmap'
                                for (String key : saldoBefore.keySet()) {
                                    String values = saldoBefore.get(key)

                                    if (key == (paymentTypeUsed[l])) {
                                        checkVerifyEqualOrMatch(WebUI.verifyEqual(Integer.parseInt(saldoBefore.get(key)) - 
                                                1, Integer.parseInt(saldoAfter.getAt(key))), ' terhadap pemotongan saldo ' + key)

                                        if (trxNo != null) {
                                            verifySaldoUsedForLiveness(conneSign, trxNo)
                                        }
                                        
                                        verifySaldoSigned(conneSign, documentId[0])
                                    }
                                }
                            }
                            
                            break
                        } else if (v == 20) {
                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' dalam jeda waktu 200 detik ')

                            GlobalVariable.FlagFailed = 1
                        } else {
                            'Delay 10 detik.'
                            WebUI.delay(10)
                        }
                    }
                }
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            getErrorMessageAPI(respon)
        }
    }
}

def correctipAddress() {
    return InetAddress.localHost.hostAddress
}

def phototoBase64(String filePath) {
    return CustomKeywords.'customizekeyword.ConvertFile.base64File'(filePath)
}

def responseAPIStoreDB(Connection conneSign, String ipaddress, String[] documentId, String trxNo) {
    'get current date'
    currentDate = new Date().format('yyyy-MM-dd')

    'declare arraylist arraymatch'
    ArrayList arrayMatch = []

    'loop berdasarkan dokumen id'
    for (int i = 0; i < documentId.size(); i++) {
        'get data from db'
        arrayIndex = 0

        'Array result. Value dari db'
        result = CustomKeywords.'connection.APIFullService.getSign'(conneSign, (documentId[i]).toString(), (findTestData(
                excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(';', 
                -1)[GlobalVariable.indexUsed]).replace('"', ''))

        'verify qty dalam transaksi. Jika done = 1'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

        'Check liveness compare adalah 0 dikarenakan trxNo yang didapat adalah transaksi untuk liveness compare.'

        'Ini perlu dideklarasi dikarenakan jika 2 dokumen, trxNo tetap 1, sehingga perlu diflag apakah dia sudah check trxnya atau belum'
        checkLivenessCompare = 0

        'Jika trxNonya tidak kosong dan checkLivenessComparenya 0'
        if (((trxNo != '') && (trxNo != null)) && (checkLivenessCompare == 0)) {
            'verify trx no. Jika sesuai, maka'
            if (WebUI.verifyEqual(result[arrayIndex++], trxNo, FailureHandling.CONTINUE_ON_FAILURE)) {
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
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    rowExcel('browserInfo (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]).replace('"', ''), 
                false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify callerId'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    rowExcel('callerId (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]).replace('"', ''), false, 
                FailureHandling.CONTINUE_ON_FAILURE))

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
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedStoredDB)
    }
}

def verifySaldoUsedForLiveness(Connection conneSign, String trxNo) {
    'klik button saldo'
    WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))

    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), 'ESIGN/ADINS', false)

    docid = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(',' - 
        1)

    noKontrak = ''

    for (int i = 1; i <= docid.size(); i++) {
        if (noKontrak != '') {
            noKontrak = (noKontrak + ';')
        }
        
        noKontrak = (noKontrak + CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, docid[(i - 1)]))
    }
    
    'ambil trx no untuk displit'
    trxNo = trxNo.split(',', -1)

    'looping per trx no'
    for (int i = 0; i < trxNo.size(); i++) {
        'Mengambil tipe saldo yang telah digunakan'
        checkTypeofUsedSaldo = CustomKeywords.'connection.APIFullService.getTypeUsedSaldo'(conneSign, trxNo[i])

        'input tipe saldo'
        WebUI.setText(findTestObject('BuatUndangan/checkSaldo/input_TipeSaldo'), checkTypeofUsedSaldo.replace('Use ', ''))

        'enter untuk input tipe saldo'
        WebUI.sendKeys(findTestObject('BuatUndangan/checkSaldo/input_TipeSaldo'), Keys.chord(Keys.ENTER))

        'click button cari'
        WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_Cari'))

        'get row'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

        'modify object button last page'
        modifyObjectButtonLastPage = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
            variable.size()) + ']', true)

        if (WebUI.getAttribute(modifyObjectButtonLastPage, 'class', FailureHandling.OPTIONAL) != 'disabled') {
            'click button last page'
            WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_LastPage'))
        }
        
        'get row'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

        'modify object no transaksi'
        modifyObjectNoTransaksi = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div', true)

        'modify object tanggal transaksi'
        modifyObjectTanggalTransaksi = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div', true)

        'modify object tipe transaksi'
        modifyObjectTipeTransaksi = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div', true)

        'modify object user'
        modifyObjectUser = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)

        'modify object no kontrak'
        modifyObjectNoKontrak = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div', true)

        'modify object Catatan'
        modifyObjectCatatan = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[8]/div', true)

        'modify object qty'
        modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div', true)

        'modify object qty'
        modifyObjectBalance = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[10]/div', true)

        'get trx dari db'
        ArrayList result = CustomKeywords.'connection.DataVerif.getSaldoTrx'(conneSign, (findTestData(excelPathAPISignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]).replace(
                '"', ''), (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('callerId (Sign External)')).split(
                ';', -1)[GlobalVariable.indexUsed]).replace('"', ''), checkTypeofUsedSaldo)

        arrayIndex = 0

        'verify no trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectNoTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' No Trx')

        'verify tgl trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTanggalTransaksi), (result[arrayIndex++]).replace(
                    '.0', ''), false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Trx')

        'verify tipe trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTipeTransaksi), result[arrayIndex++], false, 
                FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Trx')

        'verify user trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUser), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' User')

        'verify note trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectCatatan), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' Notes')

        'verify qty trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectQty), (result[arrayIndex++]).replace('-', ''), 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' Qty Trx')

        'verify qty trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectBalance), '1'.replace('-', ''), false, FailureHandling.CONTINUE_ON_FAILURE), 
            ' Balance ')
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
        ('<' + message)) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, 'Main', cellValue)
}

def verifySaldoSigned(Connection conneSign, String documentId) {
    'get current date'
    def currentDate = new Date().format('yyyy-MM-dd')

    'klik button saldo'
    WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))

    String noKontrak = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, documentId)

    String documentTemplateName = CustomKeywords.'connection.DataVerif.getDocumentName'(conneSign, noKontrak)

    'ambil nama vendor dari DB'
    String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, noKontrak)

    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), vendor, false)

    documentType = CustomKeywords.'connection.APIFullService.getDocumentType'(conneSign, noKontrak)

    'input filter dari saldo'
    WebUI.setText(findTestObject('Saldo/input_tipesaldo'), findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
            rowExcel('TipeSaldo')))

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))

    'Input tipe transaksi'
    WebUI.setText(findTestObject('Saldo/input_tipetransaksi'), findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
            rowExcel('TipeTransaksi')))

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_fromdate'), currentDate)

    'Input tipe dokumen'
    WebUI.setText(findTestObject('Saldo/input_tipedokumen'), documentType)

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipedokumen'), Keys.chord(Keys.ENTER))

    'Input referal number'
    WebUI.setText(findTestObject('Saldo/input_refnumber'), noKontrak)

    'Input documentTemplateName'
    WebUI.setText(findTestObject('Saldo/input_namadokumen'), documentTemplateName)

    'Input date sekarang'
    WebUI.setText(findTestObject('Saldo/input_todate'), currentDate)

    'Klik cari'
    WebUI.click(findTestObject('Saldo/btn_cari'))

    'Mengambil value dari db mengenai tipe pembayran'
    paymentType = CustomKeywords.'connection.APIFullService.getPaymentType'(conneSign, noKontrak)

    'Jika tipe pembayarannya per sign'
    if (paymentType == 'Per Sign') {
        'Memanggil saldo total yang telah digunakan per dokumen tersebut'
        saldoUsedperDoc = CustomKeywords.'connection.APIFullService.getTotalSignedUsingRefNumber'(conneSign, noKontrak)

        if (saldoUsedperDoc == '0') {
            WebUI.delay(10)

            'Memanggil saldo total yang telah digunakan per dokumen tersebut'
            saldoUsedperDoc = CustomKeywords.'connection.APIFullService.getTotalSignedUsingRefNumber'(conneSign, noKontrak)
        }
    } else {
        saldoUsedperDoc = 1
    }
    
    'delay dari 10 sampe 60 detik'
    for (int d = 1; d <= 6; d++) {
        'Jika dokumennya ada, maka'
        if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'get column di saldo'
            variableSaldoColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-header > div > div.datatable-row-center.ng-star-inserted datatable-header-cell'))

            'get row di saldo'
            variableSaldoRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

            'ambil inquiry di db'
            ArrayList inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldo'(conneSign, noKontrak, saldoUsedperDoc.toString())

            index = 0

            'check total row dengan yang tertandatangan'
            checkVerifyEqualOrMatch(WebUI.verifyMatch(variableSaldoRow.size().toString(), saldoUsedperDoc.toString(), false, 
                    FailureHandling.CONTINUE_ON_FAILURE), ' pada jumlah tertanda tangan dengan row transaksi ')

            'looping mengenai rownya'
            for (int j = 1; j <= variableSaldoRow.size(); j++) {
                'looping mengenai columnnya'
                for (int u = 1; u <= variableSaldoColumn.size(); u++) {
                    'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
                    modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 
                        'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', true)

                    WebUI.scrollToElement(modifyperrowpercolumn, GlobalVariable.TimeOut)

                    'Jika u di lokasi qty atau kolom ke 9'
                    if (u == 9) {
                        'Jika yang qtynya 1 dan databasenya juga, berhasil'
                        if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((inquiryDB[(u - 1)]) == '-1')) {
                            'Jika bukan untuk 2 kolom itu, maka check ke db'
                            checkVerifyEqualOrMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), inquiryDB[
                                    index++], false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kuantitas di Mutasi Saldo dengan nomor kontrak ' + 
                                noKontrak)
                        } else {
                            'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
                            GlobalVariable.FlagFailed = 1

                            'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, ((((';' + GlobalVariable.ReasonFailedSignGagal) + ' terlihat pada Kuantitas di Mutasi Saldo dengan nomor kontrak ') + 
                                '<') + noKontrak) + '>')
                        }
                    } else if (u == variableSaldoColumn.size()) {
                        'Jika di kolom ke 10, atau di FE table saldo, check saldo dari table dengan saldo yang sekarang. Takeout dari dev karena no issue dan sudah sepakat'
                    } else {
                        'Jika bukan untuk 2 kolom itu, maka check ke db'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), inquiryDB[index++], 
                                false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Mutasi Saldo dengan nomor kontrak ' + 
                            noKontrak)
                    }
                }
            }
            
            break
        } else {
            'jika kesempatan yang terakhir'
            if (d == 6) {
                'Jika masih tidak ada'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + GlobalVariable.ReasonFailedNoneUI) + ' dengan nomor kontrak ') + '<') + noKontrak) + 
                    '>')
            }
            
            'delay 10 detik'
            WebUI.delay(10)

            'Klik cari'
            WebUI.click(findTestObject('Saldo/btn_cari'))
        }
    }
}

def getPaymentTypeUsed(Connection conneSign, String vendor) {
    ArrayList paymentType = []

    if (vendor.toUpperCase() != 'PRIVY') {
        vendor = 'ESIGN/ADINS'

        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use correct OTP From Database (Sign External)')).split(';', -1)[GlobalVariable.indexUsed] != 
        'Yes') {
            isSplit = CustomKeywords.'connection.APIFullService.getSplitLivenessFaceCompareBill'(conneSign)

            if (isSplit == '1') {
                paymentType = ['Liveness', 'Face Compare']
            } else {
                paymentType = ['Liveness Face Compare']
            }
        } else {
            paymentType = ['OTP']
        }
    } else {
        paymentType = ['OTP']
    }
    
    return paymentType
}

