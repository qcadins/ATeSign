import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.model.FailureHandling as FailureHandling
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

String refNumber = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, GlobalVariable.storeVar.keySet()[[
        0]])

documentIdInput = (('["' + (GlobalVariable.storeVar.keySet()[[0]])) + '"]')

int saldoUsed = 0, delayExpiredOTP = 0, countOTP = 0, countBiometric = 0

GlobalVariable.FlagFailed = 0

'Inisialisasi otp, photo, ipaddress, dan total signed sebelumnya yang dikosongkan'
String otp, photo, ipaddress

ArrayList totalSignedBefore = [],  totalSignedAfter = [], flaggingOTP = [], loopingEmailSigner = []

'setting menggunakan base url yang benar atau salah'
CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPISignDocument, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url (Sign External)'))

'ambil nama vendor dari DB'
String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, refNumber)

if (vendor == null) {
    vendor = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Vendor'))
}

'ambil tenant dan vendor code yang akan digunakan document'
ArrayList tenantVendor = CustomKeywords.'connection.DataVerif.getTenantandVendorCode'(conneSign, GlobalVariable.storeVar.keySet()[
    0])

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

'setting sent otp by email'
if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email (Sign External)')).length() > 
0) {
    'update setting sent otp by email'
    CustomKeywords.'connection.SendSign.settingSentOTPbyEmail'(conneSign, findTestData(excelPathAPISignDocument).getValue(
            GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email (Sign External)')))
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

if (vendor.equalsIgnoreCase('Privy') || vendor.equalsIgnoreCase('Digisign')) {
    'request OTP dengan HIT API'

    'Constraint : Dokumen yang dipasang selalu dengan referal number di dokumen pertama.'
    respon_OTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : findTestData(excelPathAPISignDocument).getValue(
                    GlobalVariable.NumofColm, rowExcel('callerId (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]
                , ('phoneNo') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('phoneNo (Sign External)')).split(
                    ';', -1)[GlobalVariable.indexUsed], ('email') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    rowExcel('email (Sign External)')).split(';', -1)[GlobalVariable.indexUsed], ('refnumber') : ('"' + 
                CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, GlobalVariable.storeVar.keySet()[[0]])) + 
                '"', ('listDocumentId') : documentIdInput.replace('[', '').replace(']', '')]))

    'Jika status HIT API 200 OK'
    if (WS.verifyResponseStatusCode(respon_OTP, 200, FailureHandling.OPTIONAL) == true) {
        'get status code'
        code_otp = WS.getElementPropertyValue(respon_OTP, 'status.code', FailureHandling.OPTIONAL)

        'get psreCode'
        psreCode = WS.getElementPropertyValue(respon_OTP, 'psreCode', FailureHandling.OPTIONAL)

        'jika codenya 0'
        if (code_otp == 0) {
            'verify psre respon == psre db'
            checkVerifyEqualOrMatch(WebUI.verifyMatch(psreCode.toString().toUpperCase(), vendor.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
                ' pada psre response dengan vendor DB ')

            email = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(
                ';', -1)[GlobalVariable.indexUsed]).replace('"', '')

            if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email (Sign External)')) == 
            '1') && email.contains('OUTLOOK.COM')) {
                'call keyword get otp dari email'
                otp = (('"' + CustomKeywords.'customizekeyword.GetEmail.getEmailContent'(email, findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, rowExcel('Password Signer')), 'OTP')) + '"')
            } else if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email (Sign External)')) == 
            '0') || !(email.contains('OUTLOOK.COM'))) {
                'Dikasih delay 50 detik dikarenakan loading untuk mendapatkan OTP Privy via SMS.'
                WebUI.delay(50)

                otp = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong OTP (Sign External)')).split(
                    ';', -1)[GlobalVariable.indexUsed])
            }
            
            println(otp)
        }
		countOTP++
    } else {
		getErrorMessageAPI(respon_OTP)
	}
} else if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use correct OTP From Database (Sign External)')).split(
    ';', -1)[GlobalVariable.indexUsed]) == 'Yes') {
    'check ada value maka Setting OTP Active Duration'
    if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration (Sign External)')).length() > 
    0) {
        'Setting OTP Active Duration'
        CustomKeywords.'connection.APIFullService.settingOTPActiveDuration'(conneSign, findTestData(excelPathAPISignDocument).getValue(
                GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration (Sign External)')))

        delayExpiredOTP = (60 * Integer.parseInt(findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                rowExcel('Setting OTP Active Duration'))))
    }
    
    'check if mau menggunakan OTP yang salah atau benar'

    'request OTP dengan HIT API'

    'Constraint : Dokumen yang dipasang selalu dengan referal number di dokumen pertama.'
    respon_OTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : findTestData(excelPathAPISignDocument).getValue(
                    GlobalVariable.NumofColm, rowExcel('callerId (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]
                , ('phoneNo') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('phoneNo (Sign External)')).split(
                    ';', -1)[GlobalVariable.indexUsed], ('email') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    rowExcel('email (Sign External)')).split(';', -1)[GlobalVariable.indexUsed], ('refnumber') : ('"' + 
                CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, GlobalVariable.storeVar.keySet()[[0]])) + 
                '"', ('listDocumentId') : documentIdInput.replace('[', '').replace(']', '')]))

    'Jika status HIT API 200 OK'
    if (WS.verifyResponseStatusCode(respon_OTP, 200, FailureHandling.OPTIONAL) == true) {
        'get status code'
        code_otp = WS.getElementPropertyValue(respon_OTP, 'status.code', FailureHandling.OPTIONAL)

        'get psreCode'
        psreCode = WS.getElementPropertyValue(respon_OTP, 'psreCode', FailureHandling.OPTIONAL)

        'jika codenya 0'
        if (code_otp == 0) {
            checkVerifyEqualOrMatch(WebUI.verifyMatch(psreCode.toString().toUpperCase(), vendor.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), 
                ' pada psre response dengan vendor DB ')

            'Dikasih delay 1 detik dikarenakan loading untuk mendapatkan OTP.'
            WebUI.delay(3)

            'Mengambil otp dari database'
            otp = (('"' + CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, (findTestData(excelPathAPISignDocument).getValue(
                    GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]).replace(
                    '"', ''))) + '"')
			
			countOTP++
        } else {
            getErrorMessageAPI(respon_OTP)
        }
    } else {
		getErrorMessageAPI(respon_OTP)
    }
} else if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Use correct OTP From Database (Sign External)')).split(
    ';', -1)[GlobalVariable.indexUsed]) == 'No') {
    'get otp dari excel'
    otp = (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Wrong OTP (Sign External)')).split(
        ';', -1)[GlobalVariable.indexUsed])
}

if (otp.replace('"', '').length() >= 0) {
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
    
    'check if ingin testing expired otp'
    if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration (Sign External)')).length() > 
    0) {
        'delay untuk input expired otp'
        delayExpiredOTP = (delayExpiredOTP + 10)

        WebUI.delay(delayExpiredOTP)
    }
    
    'looping berdasarkan ukuran dari dokumen id'

    'Memasukkan input dari total signed'
    (totalSignedBefore[0]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, (GlobalVariable.storeVar.keySet()[
        [0]]).toString())

    'HIT API Sign'
    respon = WS.sendRequest(findTestObject('APIFullService/Postman/Sign Document', [('callerId') : findTestData(excelPathAPISignDocument).getValue(
                    GlobalVariable.NumofColm, rowExcel('callerId (Sign External)')).split(';', -1)[GlobalVariable.indexUsed]
                , ('documentId') : documentIdInput, ('email') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    rowExcel('email (Sign External)')).split(';', -1)[GlobalVariable.indexUsed], ('password') : ('"' + findTestData(
                    excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Password Signer'))) + '"', ('ipAddress') : ipaddress
                , ('browserInfo') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('browserInfo (Sign External)')).split(
                    ';', -1)[GlobalVariable.indexUsed], ('otp') : otp, ('selfPhoto') : photo]))

    'Jika status HIT API 200 OK'
    if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
        'get status code'
        code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

        'get trxno'
        trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

        'get psreCode'
        psreCode = WS.getElementPropertyValue(respon, 'psreCode', FailureHandling.OPTIONAL)

        'Jika trxNonya tidak kosong dari response'
        if (trxNo != null) {
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
                [0]]).toString(), (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(
                    ';', -1)[GlobalVariable.indexUsed]).replace('"', ''))

            'Loop untuk check db update sign. Maksimal 200 detik.'
            for (int v = 1; v <= 20; v++) {
                'Mengambil total Signed setelah sign'
                (totalSignedAfter[0]) = CustomKeywords.'connection.APIFullService.getTotalSigned'(conneSign, GlobalVariable.storeVar.keySet()[
                    [0]])

                'Verify total signed sebelum dan sesudah. Jika sesuai maka break'
                if ((totalSignedAfter[0]) == ((totalSignedBefore[0]) + Integer.parseInt(signCount))) {
                    WebUI.verifyEqual(totalSignedAfter[0], (totalSignedBefore[0]) + Integer.parseInt(signCount), FailureHandling.CONTINUE_ON_FAILURE)

                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                    
                    'Write to excel mengenai Document ID'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                            'Result Count Success') - 1, GlobalVariable.NumofColm - 1, (findTestData(excelPathAPISignDocument).getValue(
                            GlobalVariable.NumofColm, rowExcel('Result Count Success')) + ';') + 'Success : ' + signCount)

                    'Write to excel mengenai Document ID'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                            'Result Count Failed') - 1, GlobalVariable.NumofColm - 1, (findTestData(excelPathAPISignDocument).getValue(
                            GlobalVariable.NumofColm, rowExcel('Result Count Failed')) + ';') + 'Failed : 0')

                    'check Db'
                    if (GlobalVariable.checkStoreDB == 'Yes') {
                        'Panggil function responseAPIStoreDB dengan parameter totalSigned, ipaddress, dan array dari documentId'
                        responseAPIStoreDB(conneSign, ipaddress, GlobalVariable.storeVar.keySet()[0], trxNo.toString())
                    }
                    
                    if (trxNo != null) {
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
                    
					
					GlobalVariable.eSignData.putAt('VerifikasiSign', Integer.parseInt(signCount))
					
					GlobalVariable.eSignData.putAt('VerifikasiOTP', countOTP)
					
					GlobalVariable.eSignData.putAt('VerifikasiBiometric', countBiometric)

					checkSaldoWAOrSMS(conneSign)
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
        } else {
            getErrorMessageAPI(respon)
        }
    } else {
        getErrorMessageAPI(respon)
    }
	
	GlobalVariable.eSignData.putAt('NoKontrakProcessed', refNumber)
}

def correctipAddress() {
    return InetAddress.localHost.hostAddress
}

def phototoBase64(String filePath) {
    return CustomKeywords.'customizekeyword.ConvertFile.base64File'(filePath)
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
        (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('email (Sign External)')).split(
            ';', -1)[GlobalVariable.indexUsed]).replace('"', ''))

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

    'Jika trxNonya tidak kosong'
    if (trxNo != 'null') {
        trxNo = trxNo.split(', ', -1)

        'Array result. Value dari db'
        trxList = CustomKeywords.'connection.APIFullService.getTrxNoAPISign'(conneSign, (documentId[i]).toString())

        for (loopingTrxNo = 0; loopingTrxNo < trxNo.size(); loopingTrxNo++) {
            if (trxList.contains(trxNo[loopingTrxNo])) {
                continue
            }
            
            if ((loopingTrxNo == (trxNo.size() - 1)) && (i == (documentId.size() - 1))) {
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
        WebUI.setText(findTestObject('RegisterEsign/checkSaldo/input_TipeSaldo'), checkTypeofUsedSaldo.replace('Use ', ''))

        'enter untuk input tipe saldo'
        WebUI.sendKeys(findTestObject('RegisterEsign/checkSaldo/input_TipeSaldo'), Keys.chord(Keys.ENTER))

        'click button cari'
        WebUI.click(findTestObject('RegisterEsign/checkSaldo/button_Cari'))

        'get row'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

        'modify object button last page'
        modifyObjectButtonLastPage = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
            variable.size()) + ']', true)

        if (WebUI.getAttribute(modifyObjectButtonLastPage, 'class', FailureHandling.OPTIONAL) != 'disabled') {
            'click button last page'
            WebUI.click(findTestObject('RegisterEsign/checkSaldo/button_LastPage'))
        }
        
        'get row'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

        'modify object no transaksi'
        modifyObjectNoTransaksi = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div', true)

        'modify object tanggal transaksi'
        modifyObjectTanggalTransaksi = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div', true)

        'modify object tipe transaksi'
        modifyObjectTipeTransaksi = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div', true)

        'modify object user'
        modifyObjectUser = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)

        'modify object no kontrak'
        modifyObjectNoKontrak = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div', true)

        'modify object Catatan'
        modifyObjectCatatan = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[8]/div', true)

        'modify object qty'
        modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div', true)

        'modify object qty'
        modifyObjectBalance = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath', 
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
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def verifySaldoSigned(Connection conneSign, String documentId, String signTypeUsed) {
    'get current date'
    def currentDate = new Date().format('yyyy-MM-dd')

    'klik button saldo'
    WebUI.click(findTestObject('isiSaldo/SaldoAdmin/menu_Saldo'))

    String noKontrak = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, documentId)

    String documentTemplateName = CustomKeywords.'connection.DataVerif.getDocumentName'(conneSign, noKontrak)

    if (signTypeUsed == 'TTD') {
        signTypeUsed = 'Sign'
    } else {
        signTypeUsed = 'Document'
    }
    
    'ambil nama vendor dari DB'
    String vendor = CustomKeywords.'connection.DataVerif.getVendorNameForSaldo'(conneSign, noKontrak)

    'klik ddl untuk tenant memilih mengenai Vida'
    WebUI.selectOptionByLabel(findTestObject('Saldo/ddl_Vendor'), vendor, false)

    documentType = CustomKeywords.'connection.APIFullService.getDocumentType'(conneSign, noKontrak)

    'input filter dari saldo'
    WebUI.setText(findTestObject('Saldo/input_tipesaldo'), signTypeUsed)

    'Input enter'
    WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))

    'Input tipe transaksi'
    WebUI.setText(findTestObject('Saldo/input_tipetransaksi'), 'Use ' + signTypeUsed)

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

            WebUI.delay(2)

            'ambil inquiry di db'
            ArrayList inquiryDB = CustomKeywords.'connection.APIFullService.gettrxSaldo'(conneSign, noKontrak, saldoUsedperDoc.toString(), 
                'Use ' + signTypeUsed)

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
    
    return paymentType
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

                            resultSaldoAfter = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), [('excel') : excelPathAPISignDocument
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
                            WebUI.callTestCase(findTestCase('Main Flow/verifyMeterai'), [('excelPathMeterai') : excelPathAPISignDocument
                                    , ('sheet') : sheet, ('noKontrak') : noKontrakPerDoc[loopingPerKontrak], ('linkDocumentMonitoring') : ''
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
                    resultSaldoAfter = WebUI.callTestCase(findTestCase('Main Flow/getSaldo'), [('excel') : excelPathAPISignDocument
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

def checkSaldoWAOrSMS(Connection conneSign) {
	ArrayList balmut = []

	int penggunaanSaldo = 0

	String tipeSaldo

	emailServiceOnVendor = CustomKeywords.'connection.DataVerif.getEmailServiceAsVendorUser'(conneSign, GlobalVariable.storeVar.getAt(
			GlobalVariable.storeVar.keySet()[0]))

	fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[
			0]))

	mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

	if (mustUseWAFirst == '1') {
		tipeSaldo = 'WhatsApp Message'

		'menggunakan saldo wa'
		balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser, GlobalVariable.eSignData.getAt('VerifikasiOTP'))

		if (balmut.size() == 0) {
			'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				(findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
					'-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')
		} else {
			penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 9))
		}
	} else {
		if (emailServiceOnVendor == '1') {
			useWAMessage = CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, GlobalVariable.Tenant)

			if (useWAMessage == '1') {
				tipeSaldo = 'WhatsApp Message'

				'menggunakan saldo wa'
				balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser, GlobalVariable.eSignData.getAt('VerifikasiOTP'))

				if (balmut.size() == 0) {
					'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')
				} else {
					penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 9))
				}
			} else if (useWAMessage == '0') {
					'ke sms'
					tipeSaldo = 'OTP'

					balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser, GlobalVariable.eSignData.getAt('VerifikasiOTP'))

					if (balmut.size() == 0) {
						'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
								rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP')
					} else {
						penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 9))
					}
				
			}
		} else {
					'ke sms'
					tipeSaldo = 'OTP'

					balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser, GlobalVariable.eSignData.getAt('VerifikasiOTP'))

					if (balmut.size() == 0) {
						'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
						CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
							GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm,
								rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP')
					} else {
						penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 9))
					}
				}
	}
	
	int pemotonganSaldo = 0

	int increment

	for (looping = 0; looping < penggunaanSaldo; looping++) {
		if (looping == 0) {
			increment = 0
		} else {
			increment = (increment + 10)
		}
		
		pemotonganSaldo = (pemotonganSaldo + Integer.parseInt(balmut[(increment + 8)].replace('-','')))
		
		GlobalVariable.eSignData.putAt('allTrxNo', GlobalVariable.eSignData.getAt('allTrxNo') + balmut[increment + 0] + ';')
		
		GlobalVariable.eSignData.putAt('allSignType', GlobalVariable.eSignData.getAt('allSignType') + balmut[increment + 2].replace('Use ',''))
		
		GlobalVariable.eSignData.putAt('emailUsageSign', GlobalVariable.eSignData.getAt('emailUsageSign') + ';' + fullNameUser)
	
	}
	
	if (tipeSaldo == 'WhatsApp Message') {
		GlobalVariable.eSignData.putAt('CountVerifikasiWA', GlobalVariable.eSignData.getAt('CountVerifikasiWA') + pemotonganSaldo)
	} else if (tipeSaldo == 'SMS Notif') {
		GlobalVariable.eSignData.putAt('CountVerifikasiSMS', GlobalVariable.eSignData.getAt('CountVerifikasiSMS') + pemotonganSaldo)
	}
}
