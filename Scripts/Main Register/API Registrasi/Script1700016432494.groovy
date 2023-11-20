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

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

String selfPhoto, idPhoto

'check ada value maka setting register as dukcapil check'
if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting as Dukcapil Check')).length() > 
0) {
    'setting register as dukcapil check'
    CustomKeywords.'connection.APIFullService.settingRegisterasDukcapilCheck'(conneSign, findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('Setting as Dukcapil Check')))
}

'check ada value maka setting need password for signing'
if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Flag Need Password')).length() > 
0) {
    'setting need password for signing'
    CustomKeywords.'connection.APIFullService.settingFlagNeedPassword'(conneSign, findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('Setting Flag Need Password')))
}

if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 'Yes') {
    selfPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('selfPhoto')))) + '"')
} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 
'No') {
    selfPhoto = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('selfPhoto'))
}

if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 'Yes') {
    idPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathRegister).getValue(
            GlobalVariable.NumofColm, rowExcel('idPhoto')))) + '"')
} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 
'No') {
    idPhoto = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('idPhoto'))
}

'HIT API'
respon = WS.sendRequest(findTestObject('APIFullService/Postman/Register', [('callerId') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('callerId')), ('psreCode') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('psreInput')), ('nama') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('$Nama')), ('email') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    '$Email')), ('tmpLahir') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Tempat Lahir')), ('tglLahir') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Tanggal Lahir')), ('jenisKelamin') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Jenis Kelamin')), ('tlp') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('No Telepon')), ('idKtp') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    '$NIK')), ('alamat') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Alamat')), ('kecamatan') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Kecamatan')), ('kelurahan') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Kelurahan')), ('kota') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Kota')), ('provinsi') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Provinsi')), ('kodePos') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Kode Pos')), ('selfPhoto') : selfPhoto, ('idPhoto') : idPhoto, ('password') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Password User'))]))

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

        psreCode = WS.getElementPropertyValue(respon, 'psreCode', FailureHandling.OPTIONAL)

        println(trxNo)

        if (GlobalVariable.checkStoreDB == 'Yes') {
            'get psre Registered'
            String resultVendorRegistered = CustomKeywords.'connection.APIFullService.getRegisteredVendor'(conneSign, findTestData(
                    excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', ''))

            ArrayList<String> resultTrx = CustomKeywords.'connection.APIFullService.getAPIRegisterTrx'(conneSign, trxNo[
                0], trxNo[1])

            'reset index kembali 0 untuk array selanjutnya'
            arrayIndex = 0

            'verify trx Verification qty = -1'
            arrayMatch.add(WebUI.verifyMatch(resultTrx[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

            if ((GlobalVariable.Psre == 'VIDA') || (GlobalVariable.Psre == 'DIGI')) {
                'verify trx PNBP qty / text verification = -1'
                arrayMatch.add(WebUI.verifyMatch(resultTrx[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

                arrayIndex = 0

                'get data from db'
                ArrayList<String> result = CustomKeywords.'connection.APIFullService.checkAPIRegisterActive'(conneSign, 
                    findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', ''), 
                    findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No Telepon')).replace('"', ''))

                ArrayList<String> resultDataUser = CustomKeywords.'connection.Registrasi.buatUndanganStoreDB'(conneSign, 
                    findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', ''), 
                    findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No Telepon')).replace('"', ''))

                println(resultDataUser)

                if (GlobalVariable.Psre == 'VIDA') {
                    'verify is_active'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '1', false, FailureHandling.CONTINUE_ON_FAILURE))
                } else if (GlobalVariable.Psre == 'DIGI') {
                    'verify is_active'
                    arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '0', false, FailureHandling.CONTINUE_ON_FAILURE))
                }
                
                'verify is_registered'
                arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '1', false, FailureHandling.CONTINUE_ON_FAILURE))

                'reset index kembali 0 untuk array selanjutnya'
                arrayIndex = 0

                'verify full name'
                arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('$Nama')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                'verify tempat lahir'
                arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('Tempat Lahir')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                'parse Date from MM/dd/yyyy > yyyy-MM-dd'
                sDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(resultDataUser[arrayIndex++], 'MM/dd/yyyy', 
                    'yyyy-MM-dd')

                'verify tanggal lahir'
                arrayMatch.add(WebUI.verifyMatch(sDate.toUpperCase(), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Tanggal Lahir')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                'verify jenis kelamin'
                arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('Jenis Kelamin')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')) != '""') {
                    'verify email'
                    arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathRegister).getValue(
                                GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
                } else {
                    arrayIndex++
                }
                
                'verify provinsi'
                arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('Provinsi')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                'verify kota'
                arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('Kota')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                'verify kecamatan'
                arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('Kecamatan')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                'verify kelurahan'
                arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('Kelurahan')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

                'verify kode pos'
                arrayMatch.add(WebUI.verifyMatch((resultDataUser[arrayIndex++]).toUpperCase(), findTestData(excelPathRegister).getValue(
                            GlobalVariable.NumofColm, rowExcel('Kode Pos')).replace('"', '').toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
            } else if (GlobalVariable.Psre == 'PRIVY') {
                'looping untuk delay 100detik menunggu proses request status'
                for (delay = 1; delay <= 5; delay++) {
                    resultDataUser = CustomKeywords.'connection.APIFullService.getAPIRegisterPrivyStoreDB'(conneSign, trxNo[
                        0])

                    'reset arraymatch'
                    arrayMatch = []

                    arrayIndex = 0

                    'verify request status = 1'
                    arrayMatch.add(WebUI.verifyMatch(resultDataUser[arrayIndex++], '0', false, FailureHandling.OPTIONAL))

                    'verify is external = 1'
                    arrayMatch.add(WebUI.verifyMatch(resultDataUser[arrayIndex++], '1', false, FailureHandling.OPTIONAL))

                    if (arrayMatch.contains(false)) {
                        'jika sudah delay ke 5 maka dianggap failed'
                        if (delay == 5) {
                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB) + ' Karena Job Tidak Jalan')
                        }
                        
                        'delay 20detik'
                        WebUI.delay(20)
                    } else {
                        break
                    }
                }
            }
            
            if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('psreInput')) == '""') {
                String resultDefaultVvendor = CustomKeywords.'connection.APIFullService.getDefaultVendor'(conneSign, GlobalVariable.Tenant)

                'verify psre default =  respon'
                arrayMatch.add(WebUI.verifyMatch(resultDefaultVvendor.toUpperCase(), psreCode.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
            } else {
                if (((GlobalVariable.Psre == 'VIDA') || (GlobalVariable.Psre == 'DIGI')) || (GlobalVariable.Psre == 'TKNAJ')) {
                    'verify psre registered db = excel'
                    arrayMatch.add(WebUI.verifyMatch(resultVendorRegistered.toUpperCase(), findTestData(excelPathRegister).getValue(
                                GlobalVariable.NumofColm, rowExcel('psreInput')).replace('"', '').toUpperCase(), false, 
                            FailureHandling.CONTINUE_ON_FAILURE))

                    'verify psre registered db =  respon'
                    arrayMatch.add(WebUI.verifyMatch(resultVendorRegistered.toUpperCase(), psreCode.toUpperCase(), false, 
                            FailureHandling.CONTINUE_ON_FAILURE))
                }
                
                'verify psre input =  respon'
                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                                'psreInput')).replace('"', '').toUpperCase(), psreCode.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
            }
            
            'jika data db tidak sesuai dengan excel'
            if (arrayMatch.contains(false)) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                    GlobalVariable.ReasonFailedStoredDB)

                GlobalVariable.FlagFailed = 1
            } else {
                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)
            }
        }
    } else {
        'mengambil status code berdasarkan response HIT API'
        message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

        trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

        'Write To Excel GlobalVariable.StatusFailed and errormessage'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ('<' + message) + '>')

        GlobalVariable.FlagFailed = 1

        if ((GlobalVariable.checkStoreDB == 'Yes') && (trxNo != null)) {
            'get trx dari db'
            ArrayList<String> resultTrx = CustomKeywords.'connection.APIFullService.getAPIRegisterTrx'(conneSign, trxNo[
                0], trxNo[1])

            arrayIndex = 0

            'verify saldo Verification'
            checkVerifyEqualOrMatch(WebUI.verifyMatch(resultTrx[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE), 
                ' Gagal Verifikasi Saldo Terpotong')

            if ((GlobalVariable.Psre == 'VIDA') || (GlobalVariable.Psre == 'DIGI')) {
                if (message.equalsIgnoreCase('Verifikasi Gagal. Nama, Tanggal Lahir, atau Foto Diri tidak sesuai. Harap cek kembali Nama dan Tanggal Lahir Anda serta mengambil ulang Foto Diri.')) {
                    'verify saldo VIDA PNBP / DIGI TEXT VERIFICATION'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch((resultTrx[arrayIndex++]).toString(), '-1', false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' Gagal Verifikasi Saldo Terpotong - VIDA / DIGI')
                } else {
                    'verify saldo VIDA PNBP / DIGI TEXT VERIFICATION'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch((resultTrx[arrayIndex++]).toString(), 'null', false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' Gagal Verifikasi Saldo Terpotong - VIDA / DIGI')
                } 
            }
            
            'jika data db tidak sesuai dengan excel'
            if (arrayMatch.contains(false)) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                    GlobalVariable.ReasonFailedStoredDB)

                GlobalVariable.FlagFailed = 1
            }
        }
    }
} else {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ('<' + message) + '>')

    GlobalVariable.FlagFailed = 1
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

