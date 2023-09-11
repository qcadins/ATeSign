import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPIRegistrasi).columnNumbers

String selfPhoto, idPhoto

'looping API Registrasi'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        ArrayList<String> saldoBefore = [], saldoAfter = []
		
		GlobalVariable.FlagFailed = 0

		'get Psre per case'
		GlobalVariable.Psre = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
		
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIRegistrasi, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'check ada value maka setting need password for signing'
        if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Setting Flag Need Password')).length() > 0) {
            'setting need password for signing'
            CustomKeywords.'connection.APIFullService.settingFlagNeedPassword'(conneSign, findTestData(excelPathAPIRegistrasi).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Flag Need Password')))
        }
        
        'check ada value maka setting email service tenant'
        if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')).length() > 0) {
            'setting email service tenant'
            CustomKeywords.'connection.APIFullService.settingEmailServiceTenant'(conneSign, findTestData(excelPathAPIRegistrasi).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Email Service')))
        }
        
        'check ada value maka setting register as dukcapil check'
        if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Setting as Dukcapil Check')).length() > 0) {
            'setting register as dukcapil check'
            CustomKeywords.'connection.APIFullService.settingRegisterasDukcapilCheck'(conneSign, findTestData(excelPathAPIRegistrasi).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting as Dukcapil Check')))
        }
        
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
        } else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 'Yes') {
            selfPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIRegistrasi).getValue(
                    GlobalVariable.NumofColm, rowExcel('selfPhoto')))) + '"')
        } else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 'No') {
            selfPhoto = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('selfPhoto'))
        }
        
        if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 'Yes') {
            idPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIRegistrasi).getValue(
                    GlobalVariable.NumofColm, rowExcel('idPhoto')))) + '"')
        } else if (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 'No') {
            idPhoto = findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('idPhoto'))
        }
        
        'HIT API'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Register', [('callerId') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')), ('nama') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                        rowExcel('nama')), ('email') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('email')), ('tmpLahir') : findTestData(
                        excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('tmpLahir')), ('tglLahir') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, rowExcel('tglLahir')), ('jenisKelamin') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, rowExcel('jenisKelamin')), ('tlp') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                        rowExcel('tlp')), ('idKtp') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('idKtp')), ('alamat') : findTestData(
                        excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('alamat')), ('kecamatan') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, rowExcel('kecamatan')), ('kelurahan') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                        rowExcel('kelurahan')), ('kota') : findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('kota')), ('provinsi') : findTestData(
                        excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('provinsi')), ('kodePos') : findTestData(excelPathAPIRegistrasi).getValue(
                        GlobalVariable.NumofColm, rowExcel('kodePos')), ('selfPhoto') : selfPhoto, ('idPhoto') : idPhoto, ('password') : findTestData(
                        excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('password'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'declare arraylist arraymatch'
            arrayMatch = []

            if (code == 0) {
                'mengambil response'
                trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

                email = WS.getElementPropertyValue(respon, 'email', FailureHandling.OPTIONAL)

                println(trxNo)

                if (GlobalVariable.checkStoreDB == 'Yes') {
                    if (GlobalVariable.Psre != 'PRIVY') {
                        arrayIndex = 0

                        'get data from db'
                        ArrayList<String> result = CustomKeywords.'connection.APIFullService.checkAPIRegisterActive'(conneSign, 
                            findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''), 
                            findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('tlp')).replace('"', ''))

                        ArrayList<String> resultTrx = CustomKeywords.'connection.APIFullService.getAPIRegisterTrx'(conneSign, 
                            trxNo[0], trxNo[1])

                        ArrayList<String> resultDataUser = CustomKeywords.'connection.Registrasi.buatUndanganStoreDB'(conneSign, 
                            findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''))

                        println(resultDataUser)

                        'verify is_active'
                        arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]).toUpperCase(), '1', false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify is_registered'
                        arrayMatch.add(WebUI.verifyMatch((result[arrayIndex++]).toUpperCase(), '1', false, FailureHandling.CONTINUE_ON_FAILURE))

                        'reset index kembali 0 untuk array selanjutnya'
                        arrayIndex = 0

                        'verify trx Verification qty = -1'
                        arrayMatch.add(WebUI.verifyMatch(resultTrx[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify trx PNBP qty = -1'
                        arrayMatch.add(WebUI.verifyMatch(resultTrx[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

                        'reset index kembali 0 untuk array selanjutnya'
                        arrayIndex = 0

                        'verify full name'
                        arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                    GlobalVariable.NumofColm, rowExcel('nama')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify tempat lahir'
                        arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                    GlobalVariable.NumofColm, rowExcel('tmpLahir')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'parse Date from MM/dd/yyyy > yyyy-MM-dd'
                        sDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(resultDataUser[arrayIndex++], 
                            'MM/dd/yyyy', 'yyyy-MM-dd')

                        'verify tanggal lahir'
                        arrayMatch.add(WebUI.verifyMatch(sDate.toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                    GlobalVariable.NumofColm, rowExcel('tglLahir')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify jenis kelamin'
                        arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                    GlobalVariable.NumofColm, rowExcel('jenisKelamin')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify email'
                        arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                    GlobalVariable.NumofColm, rowExcel('email')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify provinsi'
                        arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                    GlobalVariable.NumofColm, rowExcel('provinsi')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify kota'
                        arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                    GlobalVariable.NumofColm, rowExcel('kota')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify kecamatan'
                        arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                    GlobalVariable.NumofColm, rowExcel('kecamatan')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify kelurahan'
                        arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                    GlobalVariable.NumofColm, rowExcel('kelurahan')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify kode pos'
                        arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathAPIRegistrasi).getValue(
                                    GlobalVariable.NumofColm, rowExcel('kodePos')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
                    } else {
                        'looping untuk delay 100detik menunggu proses request status'
                        for (delay = 1; delay <= 5; delay++) {
                            resultDataUser = CustomKeywords.'connection.APIFullService.getAPIRegisterPrivyStoreDB'(conneSign, 
                                trxNo[0])

                            'reset arraymatch'
                            arrayMatch = []

                            arrayIndex = 0

                            'verify request status = 1'
                            arrayMatch.add(WebUI.verifyMatch(resultDataUser[arrayIndex++], '1', false, FailureHandling.OPTIONAL))

                            'verify is external = 1'
                            arrayMatch.add(WebUI.verifyMatch(resultDataUser[arrayIndex++], '1', false, FailureHandling.OPTIONAL))

                            if (arrayMatch.contains(false)) {
                                'jika sudah delay ke 5 maka dianggap failed'
                                if (delay == 5) {
                                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, 
                                        GlobalVariable.NumofColm, GlobalVariable.StatusFailed, ((findTestData(excelPathAPIRegistrasi).getValue(
                                            GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB) + 
                                        ' Karena Job Tidak Jalan')
                                }
                                
                                'delay 20detik'
                                WebUI.delay(20)
                            } else {
                                break
                            }
                        }
                    }
                    
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
						
						GlobalVariable.FlagFailed = 1
                    } else {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                            0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, ('<' + message) + '>')

                GlobalVariable.FlagFailed = 1

                if ((GlobalVariable.checkStoreDB == 'Yes') && (trxNo != null)) {
                    'get trx dari db'
                    ArrayList<String> resultTrx = CustomKeywords.'connection.APIFullService.getAPIRegisterTrx'(conneSign, 
                        trxNo[0], trxNo[1])

                    arrayIndex = 0

                    'verify saldo privy Verification'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(resultTrx[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' Gagal Verifikasi Saldo Terpotong - Privy')

                    if (GlobalVariable.Psre == 'VIDA') {
                        'verify saldo privy PNBP'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch((resultTrx[arrayIndex++]).toString(), '-1', false, FailureHandling.CONTINUE_ON_FAILURE), 
                            ' Gagal Verifikasi Saldo Terpotong - Privy')
                    } else if (GlobalVariable.Psre == 'PRIVY') {
                        'verify saldo privy PNBP'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch((resultTrx[arrayIndex++]).toString(), 'null', false, FailureHandling.CONTINUE_ON_FAILURE), 
                            ' Gagal Verifikasi Saldo Terpotong - Privy')
                    }
                    
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)

                        GlobalVariable.FlagFailed = 1
                    }
                }
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, ('<' + message) + '>')

            GlobalVariable.FlagFailed = 1
        }
    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

