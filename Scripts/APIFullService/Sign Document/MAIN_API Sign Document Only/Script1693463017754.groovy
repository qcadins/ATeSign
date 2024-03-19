import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPISignDocument).columnNumbers

'declafe split'
int splitnum = -1

'looping API Sign Document Only'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'ambil tenant dan vendor code yang akan digunakan document'
        ArrayList tenantVendor = CustomKeywords.'connection.DataVerif.getTenantandVendorCode'(conneSign, findTestData(excelPathAPISignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('$documentid')).replace('"', '').replace('[', '').replace(']', ''))

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPISignDocument, GlobalVariable.NumofColm, rowExcel(
                'Use Correct Base Url'))

        'setting vendor otp dimatikan/diaktifkan'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Enable User Vendor OTP?(0/1/\'Empty\')')).length() > 
        0) {
            'update setting vendor otp ke table di DB'
            CustomKeywords.'connection.UpdateData.updateVendorOTP'(conneSign, tenantVendor[1], findTestData(excelPathAPISignDocument).getValue(
                    GlobalVariable.NumofColm, rowExcel('Enable User Vendor OTP?(0/1/\'Empty\')')))
        }
        
        'setting tenant otp dimatikan/diaktifkan'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Enable Need OTP for signing?(0/1/\'Empty\')')).length() > 
        0) {
            'update setting otp ke table di DB'
            CustomKeywords.'connection.UpdateData.updateTenantOTPReq'(conneSign, tenantVendor[0], findTestData(excelPathAPISignDocument).getValue(
                    GlobalVariable.NumofColm, rowExcel('Enable Need OTP for signing?(0/1/\'Empty\')')))
        }
        
        'setting tenant password dimatikan/diaktifkan'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Enable Need Password for signing?(0/1/\'Empty\')')).length() > 
        0) {
            'update setting pass tenant ke table di DB'
            CustomKeywords.'connection.UpdateData.updateTenantPassReq'(conneSign, tenantVendor[0], findTestData(excelPathAPISignDocument).getValue(
                    GlobalVariable.NumofColm, rowExcel('Enable Need Password for signing?(0/1/\'Empty\')')))
        }
        
        GlobalVariable.FlagFailed = 0

        'Inisialisasi otp, photo, ipaddress, dan total signed sebelumnya yang dikosongkan'
        String otp, photo, ipaddress

        ArrayList totalSignedBefore = [], totalSignedAfter = []

        'Split dokumen id agar mendapat dokumenid 1 per 1 dengan case bulk'
        documentId = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('$documentid')).replace(
            '[', '').replace(']', '').replace('"', '').split(',', splitnum)

        'Mengambil PSre dari setting excel percase'
        GlobalVariable.Psre = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))

        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 
        'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Tenant Code'))
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code')) == 
        'Yes') {
            'Mengambil tenant dari setting excel percase'
            GlobalVariable.Tenant = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 
        'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct API Key')) == 
        'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Wrong API Key'))
        }
        
        String refNumber = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, documentId[0])

        String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, refNumber)

        flaggingOTP = CustomKeywords.'connection.DataVerif.getParameterFlagPassOTP'(conneSign, findTestData(excelPathAPISignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('$documentid')).replace('"', '').replace('[', '').replace(']', ''))

        if ((refNumber.toString() == 'null') || (refNumber.toString() == '')) {
            refNumber = ''

            vendor = ''
        }
        
        if (vendor.equalsIgnoreCase('Privy')) {
            'request OTP dengan HIT API'
            responOTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : findTestData(
                            excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('phoneNo') : findTestData(
                            excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('phoneNo')), ('email') : findTestData(
                            excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email')), ('refnumber') : refNumber
                        , ('listDocumentId') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                            rowExcel('$documentid')).replace('[', '').replace(']', ''), ('vendor') : vendor, 
						('sendingPointOption') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('sendingPointOption'))]))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(responOTP, 200, FailureHandling.OPTIONAL) == true) {
                'get status code'
                codeOTP = WS.getElementPropertyValue(responOTP, 'status.code', FailureHandling.OPTIONAL)

                'jika codenya 0'
                if (codeOTP == 0) {
                    'Dikasih delay 50 detik dikarenakan loading untuk mendapatkan OTP.'
                    WebUI.delay(50)

                    otp = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong OTP'))
                }
            }
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use correct OTP From Database')) == 
        'Yes') {
            'check if mau menggunakan OTP yang salah atau benar'

            'request OTP dengan HIT API'

            'Constraint : Dokumen yang dipasang selalu dengan referal number di dokumen pertama.'
            responOTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : findTestData(
                            excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('phoneNo') : findTestData(
                            excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('phoneNo')), ('email') : findTestData(
                            excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email')), ('refnumber') : refNumber
                        , ('listDocumentId') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                            rowExcel('$documentid')).replace('[', '').replace(']', ''), ('vendor') : vendor]))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(responOTP, 200, FailureHandling.OPTIONAL) == true) {
                'get status code'
                codeOTP = WS.getElementPropertyValue(responOTP, 'status.code', FailureHandling.OPTIONAL)

                'jika codenya 0'
                if (codeOTP == 0) {
                    'Dikasih delay 1 detik dikarenakan loading untuk mendapatkan OTP.'
                    WebUI.delay(1)

                    'Mengambil otp dari database'
                    otp = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPISignDocument).getValue(
                            GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''))
                } else {
                    getErrorMessageAPI(responOTP)

                    otp = ''
                }
            } else {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.HITAPI Gagal'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + GlobalVariable.ReasonFailedOTPError)
            }
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use correct OTP From Database')) == 
        'No') {
            'get otp dari excel'
            otp = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong OTP'))
        }
        
        'check if mau menggunakan base64 untuk photo yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Base64 SelfPhoto')) == 
        'Yes') {
            'get base64 photo dari fungsi'
            photo = phototoBase64(findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('SelfPhoto')))
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Base64 SelfPhoto')) == 
        'No') {
            'get base64 photo salah dari excel'
            photo = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('SelfPhoto'))
        }
        
        'check if mau menggunakan ip address yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct ipAddress')) == 
        'Yes') {
            'get ip address dari fungsi'
            ipaddress = correctipAddress()
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct ipAddress')) == 
        'No') {
            'get ip address salah dari excel'
            ipaddress = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('ipAddress'))
        }
        
        'looping berdasarkan ukuran dari dokumen id'
        for (int z = 0; z < documentId.size(); z++) {
            'Memasukkan input dari total signed'
            (totalSignedBefore[z]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, documentId[z])
        }
        
        'HIT API Sign'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Sign Document', [('callerId') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')), ('documentId') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('$documentid')), ('email') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('email')), ('password') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('password')), ('ipAddress') : ipaddress, ('browserInfo') : findTestData(
                        excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('browserInfo')), ('otp') : otp
                    , ('selfPhoto') : photo, ('phoneNo') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        rowExcel('phoneNo signer'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'ambil lama waktu yang diperlukan hingga request menerima balikan'
            elapsedTime = (respon.elapsedTime / 1000) + ' second'

            'ambil body dari hasil respons'
            responseBody = respon.responseBodyContent

            'panggil keyword untuk proses beautify dari respon json yang didapat'
            CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                    excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

            'write to excel response elapsed time'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

            'get status code'
            trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

            'Jika trxNonya tidak kosong dari response'
            if (trxNo != null) {
                'Input excel'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('trxno') - 
                    1, GlobalVariable.NumofColm - 1, trxNo.toString().replace('[', '').replace(']', ''))
            }
            
            'jika codenya 0'
            if (code == 0) {
                'Loop berdasarkan jumlah documen id'
                for (int x = 0; x < documentId.size(); x++) {
                    signCount = CustomKeywords.'connection.APIFullService.getTotalSigner'(conneSign, documentId[x], findTestData(
                            excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', 
                            ''))

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
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                            'check Db'
                            if (GlobalVariable.checkStoreDB == 'Yes') {
                                'Panggil function responseAPIStoreDB dengan parameter totalSigned, ipaddress, dan array dari documentId'
                                responseAPIStoreDB(conneSign, ipaddress, documentId)
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
            
            if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('trxno')) != '') {
                'ambil trx no untuk displit'
                trxNo = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('trxno')).split(
                    ', ', -1)

                'Diberikan delay dengan pembuatan trx no di db sebesar 5 detik'
                WebUI.delay(5)

                'looping per trx no'
                for (int i = 0; i < trxNo.size(); i++) {
                    'Mengambil tipe saldo yang telah digunakan'
                    checkTypeofUsedSaldo = CustomKeywords.'connection.APIFullService.getTypeUsedSaldo'(conneSign, trxNo[
                        i])

                    if (GlobalVariable.FlagFailed == 1) {
                        'Write To Excel GlobalVariable.StatusFailed dengan alasan bahwa saldo transaksi '
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, ((((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + ' Transaksi dengan nomor ') + ('<' + (trxNo[i]))) + 
                            '> digunakan untuk ') + checkTypeofUsedSaldo)
                    }
                }
            }
        } else {
            getErrorMessageAPI(respon)
        }
    }
}

def correctipAddress() {
    InetAddress.localHost.hostAddress
}

def phototoBase64(String filePath) {
    CustomKeywords.'customizekeyword.ConvertFile.base64File'(filePath)
}

def responseAPIStoreDB(Connection conneSign, String ipaddress, String[] documentId) {
    'get current date'
    currentDate = new Date().format('yyyy-MM-dd')

    'declare arraylist arraymatch'
    ArrayList arrayMatch = []

    'loop berdasarkan dokumen id'
    for (int i = 0; i < documentId.size(); i++) {
        'get data from db'
        arrayIndex = 0

        'Array result. Value dari db'
        result = CustomKeywords.'connection.APIFullService.getSign'(conneSign, (documentId[i]).replace('"', ''), findTestData(
                excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''))

        'verify qty dalam transaksi. Jika done = 1'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify request status. 3 = done'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '3', false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify ref number yang tertandatangan'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, 
                    (documentId[i]).replace('"', '')), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify ip address'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], ipaddress.replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify user browser'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    rowExcel('browserInfo')).replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify callerId'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    rowExcel('callerId')).replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify signing proces. 0 berarti tidak ada proses tanda tangan lagi.'
        arrayMatch.add(WebUI.verifyEqual(result[arrayIndex++], 0, FailureHandling.CONTINUE_ON_FAILURE))

        'verify tanggal tanda tangan'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], currentDate, false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify api key'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.api_key, false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify tenant'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.Tenant, false, FailureHandling.CONTINUE_ON_FAILURE))

        ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(conneSign, CustomKeywords.'connection.APIFullService.getRefNumber'(
                conneSign, documentId[i]), 'Signing')

        WebUI.comment(officeRegionBline.toString())

        'lakukan loop untuk pengecekan data'
        for (int a = 0; a < (officeRegionBline.size() / 2); a++) {
            'verify business line dan office code'
            arrayMatch.add(WebUI.verifyMatch((officeRegionBline[a]).toString(), (officeRegionBline[(a + 3)]).toString(), 
                    false, FailureHandling.CONTINUE_ON_FAILURE))
        }
    }
    
    'jika data db tidak sesuai dengan excel'
    if (arrayMatch.contains(false)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedStoredDB)
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

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
        '<') + message) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

