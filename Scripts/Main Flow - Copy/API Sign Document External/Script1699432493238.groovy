import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

String refNumber = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, GlobalVariable.storeVar.keySet()[[
        0]])

documentIdInput = (('["' + (GlobalVariable.storeVar.keySet()[[0]])) + '"]')

int delayExpiredOTP = 0

int countOTP = 0

int countBiometric = 0

GlobalVariable.FlagFailed = 0

GlobalVariable.totalDelayPerSecond = 0

GlobalVariable.batasWaktu = 0

'Inisialisasi otp, photo, ipaddress, dan total signed sebelumnya yang dikosongkan'
String otp = ''

String photo

String ipaddress

ArrayList totalSignedBefore = []

ArrayList totalSignedAfter = []

'setting menggunakan base url yang benar atau salah'
CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPISignDocument, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url (Sign External)'))

'check ada value maka setting email service tenant'
if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')).length() > 
0) {
    if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == '') {
        SHA256IdNo = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(findTestData(excelPathAPISignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('phoneNo (Sign External)')).split(';', -1)[GlobalVariable.indexUsed])
    } else {
        SHA256IdNo = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(
            ';', -1)[GlobalVariable.indexUsed])
    }
    
    'setting email service tenant'
    CustomKeywords.'connection.SendSign.settingEmailServiceVendorRegisteredUser'(conneSign, findTestData(excelPathAPISignDocument).getValue(
            GlobalVariable.NumofColm, rowExcel('Setting Email Service')), SHA256IdNo)
}

'ambil nama vendor dari DB'
String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, refNumber)

if (vendor == 'null') {
    vendor = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Vendor'))
}

if (vendor.equalsIgnoreCase('Digisign')) {
    signTypeUsed = 'Dokumen'
} else {
    signTypeUsed = 'TTD'
}

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

flaggingOTP = CustomKeywords.'connection.DataVerif.getParameterFlagPassOTP'(conneSign, (GlobalVariable.storeVar.keySet()[
    [0]]).toString())

String sendingPoint = ''

if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('sendingPointOption (Sent Otp)')).split(
    ';', -1)[GlobalVariable.indexUsed]).length() == 0) {
    sendingPoint = ''
} else {
    sendingPoint = ((', "sendingPointOption" : "' + (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
        rowExcel('sendingPointOption (Sent Otp)')).split(';', -1)[GlobalVariable.indexUsed])) + '"')
}

if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use correct OTP From Database (Sign External)')).split(
    ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
    if (vendor.equalsIgnoreCase('Privy') || vendor.equalsIgnoreCase('Digisign')) {
        'request OTP dengan HIT API'

        'Constraint : Dokumen yang dipasang selalu dengan referal number di dokumen pertama.'
        responOTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : findTestData(
                        excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('phoneNo') : findTestData(
                        excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('phoneNo (Sent Otp)')).split(
                        ';', -1)[GlobalVariable.indexUsed], ('email') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]
                    , ('refnumber') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel(
                            'refNumber (Sent Otp)')).split(';', -1)[GlobalVariable.indexUsed], ('listDocumentId') : documentIdInput.replace(
                        '[', '').replace(']', ''), ('vendor') : vendor, ('sendingPointOption') : sendingPoint]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responOTP, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            codeOTP = WS.getElementPropertyValue(responOTP, 'status.code', FailureHandling.OPTIONAL)

            'get psreCode'
            psreCode = WS.getElementPropertyValue(responOTP, 'psreCode', FailureHandling.OPTIONAL)

            'jika codenya 0'
            if (codeOTP == 0) {
                'verify psre respon == psre db'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(psreCode.toString().toUpperCase(), vendor.toUpperCase(), false, 
                        FailureHandling.CONTINUE_ON_FAILURE), ' pada psre response dengan vendor DB ')

                ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(conneSign, 
                    CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, CustomKeywords.'connection.APIFullService.getRefNumber'(
                            conneSign, GlobalVariable.storeVar.keySet()[0])), 'Signing')

                'lakukan loop untuk pengecekan data'
                for (i = 0; i < (officeRegionBline.size() / 3); i++) {
                    'verify business line dan office code'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch((officeRegionBline[i]).toString(), (officeRegionBline[(i + 
                            3)]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada pengecekan Office/Region/Bline pada Sent otp Signing')
                }
                
                email = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(
                    ';', -1)[GlobalVariable.indexUsed])

                /*
                if (CustomKeywords.'connection.UpdateData.checkNotifTypeExistforTenant'(conneSign) > 0) {
                    if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (OTP Sign External Flow)')) == 
                    '1') {
                        otp = handlingOTP('WhatsApp', email, psreCode)
                    } else if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP By Email (OTP Sign External Flow)')) == 
                    '1') && (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == 
                    '0')) {
                        if (email.contains('OUTLOOK.COM')) {
                            otp = handlingOTP('Email', email, psreCode)
                        } else {
                            WebUI.delay(45)

                            otp = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Manual OTP')).split(
                                ';', -1)[GlobalVariable.indexUsed])
                        }
                    } else if (((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP By Email (OTP Sign External Flow)')) == 
                    '1') && (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == 
                    '1')) || (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (OTP Sign External Flow)')) == 
                    '1')) {
                        otp = handlingOTP('WhatsApp', email, psreCode)
                    } else {
                        otp = handlingOTP('SMS', email, psreCode)
                    }
                } else {
                    if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Level Tenant)')) == 
                    '1') {
                        otp = handlingOTP('WhatsApp', email, psreCode)
                    } else if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email (Level Tenant)')) == 
                    '1') && (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == 
                    '0')) {
                        if (email.contains('OUTLOOK.COM')) {
                            otp = handlingOTP('Email', email, psreCode)
                        } else {
                            WebUI.delay(45)

                            otp = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Manual OTP')).split(
                                ';', -1)[GlobalVariable.indexUsed])
                        }
                    } else if (((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email (Level Tenant)')) == 
                    '1') && (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == 
                    '1')) || (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Level Tenant)')) == 
                    '1')) {
                        otp = handlingOTP('WhatsApp', email, psreCode)
                    } else {
                        otp = handlingOTP('SMS', email, psreCode)
                    }
                }
                */
                otp = handlingOTP('SMS', email, psreCode)

                println(otp)
            } else {
                getErrorMessageAPI(responOTP)
            }
            
            countOTP++
        } else {
            getErrorMessageAPI(responOTP)
        }
    } else {
        'check ada value maka Setting OTP Active Duration'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 
        0) {
            delayExpiredOTP = (60 * Integer.parseInt(findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    rowExcel('Setting OTP Active Duration'))))
        }
        
        'check if mau menggunakan OTP yang salah atau benar'

        'request OTP dengan HIT API'

        'Constraint : Dokumen yang dipasang selalu dengan referal number di dokumen pertama.'
        responOTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : findTestData(
                        excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('phoneNo') : findTestData(
                        excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('phoneNo (Sent Otp)')).split(
                        ';', -1)[GlobalVariable.indexUsed], ('email') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]
                    , ('refnumber') : CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, GlobalVariable.storeVar.keySet()[
                        [0]]), ('listDocumentId') : documentIdInput.replace('[', '').replace(']', ''), ('vendor') : vendor
                    , ('sendingPointOption') : sendingPoint]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responOTP, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            codeOTP = WS.getElementPropertyValue(responOTP, 'status.code', FailureHandling.OPTIONAL)

            'get psreCode'
            psreCode = WS.getElementPropertyValue(responOTP, 'psreCode', FailureHandling.OPTIONAL)

            'jika codenya 0'
            if (codeOTP == 0) {
                checkVerifyEqualOrMatch(WebUI.verifyMatch(psreCode.toString().toUpperCase(), vendor.toUpperCase(), false, 
                        FailureHandling.CONTINUE_ON_FAILURE), ' pada psre response dengan vendor DB ')

                ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(conneSign, 
                    CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, CustomKeywords.'connection.APIFullService.getRefNumber'(
                            conneSign, GlobalVariable.storeVar.keySet()[0])), 'Signing')

                'lakukan loop untuk pengecekan data'
                for (int i = 0; i < (officeRegionBline.size() / 3); i++) {
                    'verify business line dan office code'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch((officeRegionBline[i]).toString(), (officeRegionBline[(i + 
                            3)]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada pengecekan Office/Region/Bline pada Sent otp Signing')
                }
                
                'Dikasih delay 1 detik dikarenakan loading untuk mendapatkan OTP.'
                WebUI.delay(3)

                if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(
                    ';', -1)[GlobalVariable.indexUsed]) == '') {
                    'Mengambil otp dari database'
                    otp = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPISignDocument).getValue(
                            GlobalVariable.NumofColm, rowExcel('phoneNo (Sign External)')).split(';', -1)[GlobalVariable.indexUsed])
                } else {
                    'Mengambil otp dari database'
                    otp = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPISignDocument).getValue(
                            GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(';', -1)[GlobalVariable.indexUsed])
                }
                
                countOTP++
            } else {
                getErrorMessageAPI(responOTP)
            }
        } else {
            getErrorMessageAPI(responOTP)
        }
    }
} else if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use correct OTP From Database (Sign External)')).split(
    ';', -1)[GlobalVariable.indexUsed]) == 'No') {
    'get otp dari excel'
    otp = GlobalVariable.wrongOtp
}

(GlobalVariable.eSignData['VerifikasiOTP']) = countOTP

if (otp != null) {
    'check if mau menggunakan base64 untuk photo yang salah atau benar'
    if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Base64 SelfPhoto (Sign External)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
        'get base64 photo dari fungsi'
        photo = phototoBase64(findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('SelfPhoto (Sign External)')).split(
                ';', -1)[GlobalVariable.indexUsed])
    } else if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Base64 SelfPhoto (Sign External)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'No') {
        'get base64 photo salah dari excel'
        photo = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('SelfPhoto (Sign External)')).split(
            ';', -1)[GlobalVariable.indexUsed])
    }
    
    'check if mau menggunakan ip address yang salah atau benar'
    if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct ipAddress (Sign External)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
        'get ip address dari fungsi'
        ipaddress = correctipAddress()
    } else if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct ipAddress (Sign External)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'No') {
        'get ip address salah dari excel'
        ipaddress = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('ipAddress (Sign External)')).split(
            ';', -1)[GlobalVariable.indexUsed])
    }
    
    'check if ingin testing expired otp'
    if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')).length() > 
    0) {
        'delay untuk input expired otp'
        delayExpiredOTP = (delayExpiredOTP + 10)

        WebUI.delay(delayExpiredOTP)
    }
    
    if (!(otp.find('\\d'))) {
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                '-', '') + ';') + otp.toString())
    }
    
    GlobalVariable.batasWaktu = ((delayExpiredOTP / 60) + 2)

    'Memasukkan input dari total signed'
    (totalSignedBefore[0]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, (GlobalVariable.storeVar.keySet()[
        [0]]).toString())

    'HIT API Sign'
    respon = WS.sendRequest(findTestObject('APIFullService/Postman/Sign Document', [('callerId') : findTestData(excelPathAPISignDocument).getValue(
                    GlobalVariable.NumofColm, rowExcel('callerId')), ('documentId') : documentIdInput, ('email') : findTestData(
                    excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(
                    ';', -1)[GlobalVariable.indexUsed], ('password') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    rowExcel('Password Signer')), ('ipAddress') : ipaddress, ('browserInfo') : findTestData(excelPathAPISignDocument).getValue(
                    GlobalVariable.NumofColm, rowExcel('browserInfo (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]
                , ('otp') : otp, ('selfPhoto') : photo, ('phoneNo') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    rowExcel('phoneNo (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]]))

    'Jika status HIT API 200 OK'
    if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
        'get status code'
        code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

        'get trxno'
        trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

        'get psreCode'
        psreCode = WS.getElementPropertyValue(respon, 'psreCode', FailureHandling.OPTIONAL)

        'Jika trxNonya tidak kosong dari response'
        if (trxNo.toString().contains('[')) {
            'Input excel'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('trxNos') - 
                1, GlobalVariable.NumofColm - 1, findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    rowExcel('trxNos')) + trxNo.toString().replace('[', '').replace(']', ''))
        }
        
        'jika codenya 0'
        if (code == 0) {
            'get psreCode'
            psreCode = WS.getElementPropertyValue(respon, 'psreCode', FailureHandling.OPTIONAL)

            checkVerifyEqualOrMatch(WebUI.verifyMatch(psreCode.toString().toUpperCase(), vendor.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
                ' pada psre response dengan vendor DB ')

            signCount = CustomKeywords.'connection.APIFullService.getTotalSigner'(conneSign, (GlobalVariable.storeVar.keySet()[
                [0]]).toString(), findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(
                    ';', -1)[GlobalVariable.indexUsed])

            'Loop untuk check db update sign. Maksimal 200 detik.'
            for (int v = 1; v <= 20; v++) {
                'Mengambil total Signed setelah sign'
                (totalSignedAfter[0]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, GlobalVariable.storeVar.keySet()[
                    [0]])

                'Verify total signed sebelum dan sesudah. Jika sesuai maka break'
                if ((totalSignedAfter[0]) == ((totalSignedBefore[0]) + Integer.parseInt(signCount))) {
                    WebUI.verifyEqual(totalSignedAfter[0], (totalSignedBefore[0]) + Integer.parseInt(signCount), FailureHandling.CONTINUE_ON_FAILURE)

                    ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(conneSign, 
                        CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, GlobalVariable.storeVar.keySet()[
                            [0]]), 'Signing')

                    'lakukan loop untuk pengecekan data'
                    for (int a = 0; a < (officeRegionBline.size() / 2); a++) {
                        'verify business line dan office code'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch((officeRegionBline[a]).toString(), (officeRegionBline[
                                (a + 3)]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE), 'Pada Pengecekan Office dan Business Line')
                    }
                    
                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                    
                    'Write to excel mengenai Document ID'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                            'Result Count Success') - 1, GlobalVariable.NumofColm - 1, ((findTestData(excelPathAPISignDocument).getValue(
                            GlobalVariable.NumofColm, rowExcel('Result Count Success')) + ';') + 'Success : ') + signCount)

                    'Write to excel mengenai Document ID'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                            'Result Count Failed') - 1, GlobalVariable.NumofColm - 1, (findTestData(excelPathAPISignDocument).getValue(
                            GlobalVariable.NumofColm, rowExcel('Result Count Failed')) + ';') + 'Failed : 0')

                    'check Db'
                    if (GlobalVariable.checkStoreDB == 'Yes') {
                        'Panggil function responseAPIStoreDB dengan parameter totalSigned, ipaddress, dan array dari documentId'
                        responseAPIStoreDB(conneSign, ipaddress, GlobalVariable.storeVar.keySet()[0], trxNo.toString())
                    }
                    
                    if (trxNo.toString().replace('[', '').replace(']', '') != 'null') {
                        'ambil trx no untuk displit'
                        trxNo = trxNo.replace('[', '').replace(']', '').split(', ', -1)

                        'Diberikan delay dengan pembuatan trx no di db sebesar 5 detik'
                        WebUI.delay(5)

                        'looping per trx no'
                        for (int i = 0; i < trxNo.size(); i++) {
                            'Mengambil tipe saldo yang telah digunakan'
                            checkTypeofUsedSaldo = CustomKeywords.'connection.APIFullService.getTypeUsedSaldo'(conneSign, 
                                trxNo[i])

                            countBiometric++

                            if (GlobalVariable.FlagFailed == 1) {
                                'Write To Excel GlobalVariable.StatusFailed dengan alasan bahwa saldo transaksi '
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, ((((((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')) + ';') + '<') + ' Transaksi dengan nomor ') + +(trxNo[
                                    i])) + 'digunakan untuk ') + checkTypeofUsedSaldo) + '>')
                            }
                        }
                    }
                    
                    (GlobalVariable.eSignData['VerifikasiSign']) = Integer.parseInt(signCount)

                    (GlobalVariable.eSignData['VerifikasiBiometric']) = countBiometric

                    checkSaldoWAOrSMS(conneSign, vendor)

                    checkAutoStamp(conneSign, refNumber, GlobalVariable.saldo)

                    break
                } else if (v == 20) {
                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' dalam jeda waktu ') + 
                        (v * 10)) + ' detik ')

                    GlobalVariable.FlagFailed = 1
                } else {
                    'Delay 10 detik.'
                    WebUI.delay(10)

                    'SigningDB mengambil value dari hasil query'
                    signingDB = CustomKeywords.'connection.SendSign.getSigningStatusProcess'(conneSign, documentIdInput.replace(
                            '[', '').replace(']', '').replace('"', ''), findTestData(excelPathAPISignDocument).getValue(
                            GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(';', -1)[GlobalVariable.indexUsed])

                    if ((signingDB[0]) == '2') {
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedProcessFailed)

                        break
                    } else if ((signingDB[0]) == '3') {
                        continue
                    }
                }
            }
            
            (GlobalVariable.eSignData['NoKontrakProcessed']) = ((GlobalVariable.eSignData['NoKontrakProcessed']) + refNumber)
        } else {
            getErrorMessageAPI(respon)
        }
    } else {
        getErrorMessageAPI(respon)
    }
} /*
    if ((notifTypeDB == '0') || (notifTypeDB == 'Level Tenant')) {
        'jika must use wa'
        if (mustUseWAFirst == '1') {
            tipeSaldo = 'WhatsApp Message'

            'menggunakan saldo wa'
            balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

            if (balmut.size() == 0) {
                'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')

                GlobalVariable.FlagFailed = 1
            } else {
                penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
            }
        } else {
            'jika email service on vendor hidup'
            if (emailServiceOnVendor == '1') {
                useWAMessage = CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, GlobalVariable.Tenant)

                if (useWAMessage == '1') {
                    tipeSaldo = 'WhatsApp Message'

                    'menggunakan saldo wa'
                    balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

                    if (balmut.size() == 0) {
                        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')

                        GlobalVariable.FlagFailed = 1
                    } else {
                        penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
                    }
                } else if (useWAMessage == '0') {
                    'ke otp'
                    tipeSaldo = 'OTP'

                    balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

                    if (balmut.size() == 0) {
                        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP')

                        GlobalVariable.FlagFailed = 1

                        (GlobalVariable.eSignData['VerifikasiOTP']) = ((GlobalVariable.eSignData['VerifikasiOTP']) - 1)
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
                        GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP')

                    GlobalVariable.FlagFailed = 1

                    (GlobalVariable.eSignData['VerifikasiOTP']) = ((GlobalVariable.eSignData['VerifikasiOTP']) - 1)
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
                ((findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via ') + 
                tipeSaldo.replace(' Message', '').replace(' Notif', ''))
        } else {
            'penggunaan saldo didapat dari ikuantitaas query balmut'
            penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
        }
    }
    */

def correctipAddress() {
    InetAddress.localHost.hostAddress
}

def phototoBase64(String filePath) {
    CustomKeywords.'customizekeyword.ConvertFile.base64File'(filePath)
}

def responseAPIStoreDB(Connection conneSign, String ipaddress, String documentId, String trxNo) {
    'get current date'
    currentDate = new Date().format('yyyy-MM-dd')

    'declare arraylist arraymatch'
    ArrayList arrayMatch = []

    'get data from db'
    arrayIndex = 0

    'Array result. Value dari db'
    result = CustomKeywords.'connection.APIFullService.getSign'(conneSign, (GlobalVariable.storeVar.keySet()[0]).toString(), 
        findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(
            ';', -1)[GlobalVariable.indexUsed])

    'verify qty dalam transaksi. Jika done = 1'
    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify request status. 3 = done'
    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '3', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify ref number yang tertandatangan'
    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, 
                (GlobalVariable.storeVar.keySet()[0]).replace('"', '')), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify ip address'
    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], ipaddress.replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify user browser'
    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                rowExcel('browserInfo (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]).replace('"', ''), false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'verify callerId'
    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                rowExcel('callerId')), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify signing proces. 0 berarti tidak ada proses tanda tangan lagi.'
    arrayMatch.add(WebUI.verifyEqual(result[arrayIndex++], 0, FailureHandling.CONTINUE_ON_FAILURE))

    'verify tanggal tanda tangan'
    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], currentDate, false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify api key'
    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.api_key, false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify tenant'
    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.Tenant, false, FailureHandling.CONTINUE_ON_FAILURE))

    trxNo = trxNo.replace('[', '').replace(']', '')

    'Jika trxNonya tidak kosong'
    if ((trxNo != 'null') && (trxNo != '')) {
        trxNo = trxNo.split(', ', -1)

        'Array result. Value dari db'
        trxList = CustomKeywords.'connection.APIFullService.getTrxNoAPISign'(conneSign, documentId)

        for (loopingTrxNo = 0; loopingTrxNo < trxNo.size(); loopingTrxNo++) {
            if (trxList.contains(trxNo[loopingTrxNo])) {
                continue
            }
            
            if (loopingTrxNo == (trxNo.size() - 1)) {
                'Write To Excel GlobalVariable.StatusFailed dengan alasan bahwa saldo transaksi '
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
                    ';') + '<') + ' Transaksi dengan nomor ') + +(trxNo[loopingTrxNo])) + ' tidak termasuk sebagai transaksi pada dokumen ini') + 
                    '>')
            }
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
        ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
        ('<' + message)) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def getPaymentTypeUsed(Connection conneSign, String vendor) {
    ArrayList paymentType = []

    if (vendor.toUpperCase() != 'PRIVY') {
        vendor = 'ESIGN/ADINS'

        if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use correct OTP From Database (Sign External)')).split(
            ';', -1)[GlobalVariable.indexUsed]) != 'Yes') {
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
    
    paymentType
}

def checkAutoStamp(Connection conneSign, String noKontrak, HashMap<String, String> resultSaldoBefore) {
    'split nomor kontrak'
    noKontrakPerDoc = noKontrak.split(';', -1)

    'inisialisasi flag error dms'
    int flagErrorDMS = 0

    'inisialisasi saldo after dan before'
    String saldoAfter

    String saldoBefore = resultSaldoBefore.get('Meterai')

    'looping'
    for (loopingPerKontrak = 0; loopingPerKontrak < noKontrakPerDoc.size(); loopingPerKontrak++) {
        'check autostamp'
        automaticStamp = CustomKeywords.'connection.Meterai.getAutomaticStamp'(conneSign, noKontrakPerDoc[loopingPerKontrak])

        'jika autostamp'
        if (automaticStamp == '1') {
            'get sign status'
            getSignStatus = CustomKeywords.'connection.SendSign.getSignStatus'(conneSign, noKontrakPerDoc[loopingPerKontrak])

            'jika complete'
            if (getSignStatus == 'Complete') {
                'mengambil value db proses ttd'
                prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, noKontrakPerDoc[loopingPerKontrak])

                'jika prosesnya bukan 0 atau berjalan'
                if (prosesMaterai != 0) {
                    'looping dari 1 hingga 12'
                    for (i = 1; i <= 12; i++) {
                        'mengambil value db proses ttd'
                        prosesMaterai = CustomKeywords.'connection.Meterai.getProsesMaterai'(conneSign, noKontrakPerDoc[
                            loopingPerKontrak])

                        'jika proses materai gagal (51)'
                        if (((prosesMaterai == 51) || (prosesMaterai == 61)) && (flagErrorDMS == 0)) {
                            'Kasih delay untuk mendapatkan update db untuk error stamping'
                            WebUI.delay(3)

                            'get reason gailed error message untuk stamping'
                            errorMessageDB = CustomKeywords.'connection.Meterai.getErrorMessage'(conneSign, noKontrakPerDoc[
                                loopingPerKontrak])

                            'Write To Excel GlobalVariable.StatusFailed and errormessage'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + ' dengan alasan ') + 
                                errorMessageDB.toString())

                            GlobalVariable.FlagFailed = 1

                            if (!(errorMessageDB.toString().contains('upload DMS'))) {
                                break
                            } else {
                                flagErrorDMS = 1

                                continue
                            }
                        } else if (((prosesMaterai == 53) || (prosesMaterai == 63)) || (flagErrorDMS == 1)) {
                            WebUI.delay(3)

                            resultSaldoAfter = WebUI.callTestCase(findTestCase(''), [('excel') : excelPathAPISignDocument
                                    , ('sheet') : sheet, ('usageSaldo') : 'Stamp'], FailureHandling.CONTINUE_ON_FAILURE)

                            saldoAfter = resultSaldoAfter.get('Meterai')

                            'Mengambil value total stamping dan total meterai'
                            totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
                                conneSign, noKontrakPerDoc[loopingPerKontrak])

                            'declare arraylist arraymatch'
                            arrayMatch = []

                            'dibandingkan total meterai dan total stamp'
                            arrayMatch.add(WebUI.verifyMatch(totalMateraiAndTotalStamping[0], totalMateraiAndTotalStamping[
                                    1], false, FailureHandling.CONTINUE_ON_FAILURE))

                            'jika data db tidak bertambah'
                            if (arrayMatch.contains(false)) {
                                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)

                                GlobalVariable.FlagFailed = 1
                            } else {
                                GlobalVariable.FlagFailed = 0

                                WebUI.comment(saldoBefore)

                                WebUI.comment(saldoAfter)

                                'check saldo before dan after'
                                checkVerifyEqualOrMatch(WebUI.verifyEqual(Integer.parseInt(saldoBefore) - Integer.parseInt(
                                            totalMateraiAndTotalStamping[1]), Integer.parseInt(saldoAfter), FailureHandling.CONTINUE_ON_FAILURE), 
                                    ' pada pemotongan saldo Meterai Autostamp')
                            }
                            
                            break
                        } else {
                            'Jika bukan 51/61 dan 53/63, maka diberikan delay 20 detik'
                            WebUI.delay(10)

                            'Jika looping berada di akhir, tulis error failed proses stamping'
                            if (i == 12) {
                                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, ((((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedProsesStamping) + 
                                    ' dengan jeda waktu ') + (i * 12)) + ' detik ')

                                GlobalVariable.FlagFailed = 1
                            }
                        }
                    }
                    
                    'Jika flag failed tidak 0'
                    if (GlobalVariable.FlagFailed == 0) {
                        if (flagErrorDMS == 1) {
                            'write to excel Failed'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                                rowExcel('Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusFailed)
                        } else {
                            'write to excel success'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                                rowExcel('Status') - 1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                        }
                        
                        'Mengambil value total stamping dan total meterai'
                        totalMateraiAndTotalStamping = CustomKeywords.'connection.Meterai.getTotalMateraiAndTotalStamping'(
                            conneSign, noKontrakPerDoc[loopingPerKontrak])

                        if (((totalMateraiAndTotalStamping[0]) != '0') && (prosesMaterai != 63)) {
                            'Call verify meterai'
                            WebUI.callTestCase(findTestCase(''), [('excelPathMeterai') : excelPathAPISignDocument, ('sheet') : sheet
                                    , ('noKontrak') : noKontrakPerDoc[loopingPerKontrak], ('linkDocumentMonitoring') : ''
                                    , ('CancelDocsStamp') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Cancel Docs after Stamp?'))], FailureHandling.CONTINUE_ON_FAILURE)
                        }
                    }
                } else {
                    'Jika masih tidak ada'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + 'Autostamp gagal ')

                    'get saldo after'
                    resultSaldoAfter = WebUI.callTestCase(findTestCase('Main Flow - Copy/getSaldo'), [('excel') : excelPathAPISignDocument
                            , ('sheet') : sheet, ('usageSaldo') : 'Stamp'], FailureHandling.CONTINUE_ON_FAILURE)

                    'get saldo after meterai'
                    saldoAfter = resultSaldoAfter.get('Meterai')

                    'cehck saldo before dan after'
                    checkVerifyEqualOrMatch(WebUI.verifyEqual(Integer.parseInt(saldoBefore), Integer.parseInt(saldoAfter), 
                            FailureHandling.CONTINUE_ON_FAILURE), ' pada pemotongan saldo Meterai Gagal Autostamp')
                }
            }
        }
    }
}

def checkSaldoWAOrSMS(Connection conneSign, String vendor) {
    'inisialisasi balmut, penggunaan saldo, dan tipe saldo. Get email serbice as vendor, full name user, dan setting must use wa'
    ArrayList balmut = []

    int penggunaanSaldo = 0

    String tipeSaldo

    emailServiceOnVendor = CustomKeywords.'connection.DataVerif.getEmailServiceAsVendorUser'(conneSign, GlobalVariable.storeVar[
        (GlobalVariable.storeVar.keySet()[0])])

    fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, GlobalVariable.storeVar[(GlobalVariable.storeVar.keySet()[
        0])])

    notifTypeDB = CustomKeywords.'connection.APIFullService.getWASMSFromNotificationTypeOTP'(conneSign, GlobalVariable.storeVar[
        (GlobalVariable.storeVar.keySet()[0])], 'OTP_SIGN_EXTERNAL', GlobalVariable.Tenant)

    mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

    'jika menggunakan privy, maka tidak bisa menggunakan wa dan pemotongan melalui otp privy'
    if (vendor.equalsIgnoreCase('Privy')) {
        mustUseWaFirst = '0'

        emailServiceOnVendor = '0'
    }
    
    if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('sendingPointOption (Sent Otp)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'WA') {
        tipeSaldo = 'WhatsApp Message'

        'menggunakan saldo wa'
        balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

        if (balmut.size() == 0) {
            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')

            GlobalVariable.FlagFailed = 1
        } else {
            penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
        }
    }
    
    if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('sendingPointOption (Sent Otp)')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'SMS') {
        tipeSaldo = 'OTP'

        'menggunakan saldo wa'
        balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

        if (balmut.size() == 0) {
            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via SMS')

            GlobalVariable.FlagFailed = 1
        } else {
            penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
        }
    }
    
    int pemotonganSaldo = 0

    int increment

    if (penggunaanSaldo > 0) {
        for (looping = 0; looping < penggunaanSaldo; looping++) {
            if (looping == 0) {
                increment = 0
            } else {
                increment = (increment + 10)
            }
            
            pemotonganSaldo = (pemotonganSaldo + Integer.parseInt((balmut[(increment + 8)]).replace('-', '')))

            (GlobalVariable.eSignData['allTrxNo']) = (((GlobalVariable.eSignData['allTrxNo']) + (balmut[(increment + 0)])) + 
            ';')

            (GlobalVariable.eSignData['allSignType']) = (((GlobalVariable.eSignData['allSignType']) + (balmut[(increment + 
            2)]).replace('Use ', '')) + ';')

            (GlobalVariable.eSignData['emailUsageSign']) = (((GlobalVariable.eSignData['emailUsageSign']) + fullNameUser) + 
            ';')
        }
        
        if (tipeSaldo == 'WhatsApp Message') {
            (GlobalVariable.eSignData['CountVerifikasiWA']) = ((GlobalVariable.eSignData['CountVerifikasiWA']) + pemotonganSaldo)

            (GlobalVariable.eSignData['VerifikasiOTP']) = ((GlobalVariable.eSignData['VerifikasiOTP']) - (GlobalVariable.eSignData[
            'CountVerifikasiWA']))
        } else if (tipeSaldo == 'SMS Notif') {
            (GlobalVariable.eSignData['CountVerifikasiSMS']) = ((GlobalVariable.eSignData['CountVerifikasiSMS']) + pemotonganSaldo)

            (GlobalVariable.eSignData['VerifikasiOTP']) = ((GlobalVariable.eSignData['VerifikasiOTP']) - (GlobalVariable.eSignData[
            'CountVerifikasiWA']))
        }
    }
}

def handlingOTP(String typeOfHandling, String email, String vendor) {
    if (typeOfHandling == 'WhatsApp') {
        WebUI.delay(45)

        findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Manual OTP').split(';', -1)[
            GlobalVariable.indexUsed])
    } else if (typeOfHandling == 'SMS') {
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Getting OTP Value from SMS Via Pushbullet ?')) == 
        'Yes') {
            if (vendor.equalsIgnoreCase('Privy')) {
                otp = CustomKeywords.'customizekeyword.GetSMS.getOTP'('PrivyID')
            } else if (vendor.equalsIgnoreCase('Digisign')) {
                otp = CustomKeywords.'customizekeyword.GetSMS.getOTP'('DIGISIGN')
            }
            
            if (otp.find('\\d')) {
                otp
            } else {
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + otp.toString())

                WebUI.delay(45)

                ''
            }
        } else {
            WebUI.delay(45)

            findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Manual OTP').split(';', 
                    -1)[GlobalVariable.indexUsed])
        }
    } else if (typeOfHandling == 'Email') {
        CustomKeywords.'customizekeyword.GetEmail.getEmailContent'(email, findTestData(excelPathAPISignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('Password Signer')), 'OTP')
    }
}

