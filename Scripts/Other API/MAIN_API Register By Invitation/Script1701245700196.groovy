import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPathGenerateLink).columnNumbers

String selfPhoto

String idPhoto

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'get psre from excel percase'
        GlobalVariable.Psre = findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))

        'get Tenant from excel percase'
        GlobalVariable.Tenant = findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathGenerateLink, GlobalVariable.NumofColm, rowExcel(
                'Use Correct base Url'))

        if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 
        'Yes') {
            selfPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathGenerateLink).getValue(
                    GlobalVariable.NumofColm, rowExcel('selfPhoto')))) + '"')
        } else if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 
        'No') {
            selfPhoto = findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('selfPhoto'))
        }
        
        if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 
        'Yes') {
            idPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathGenerateLink).getValue(
                    GlobalVariable.NumofColm, rowExcel('idPhoto')))) + '"')
        } else if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 
        'No') {
            idPhoto = findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('idPhoto'))
        }
        
        String value

        if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Input correct Message')).equalsIgnoreCase(
            'Yes')) {
            'get invitationcode dari DB > encrypt invitation code > encode invitation code yang sudah di encrypt'
            value = encodeValue(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace(
                    '"', ''), conneSign)
        } else if (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Input correct Message')).equalsIgnoreCase(
            'No')) {
            value = findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Message'))
        }
        
        println(value)

        'Pembuatan pengisian variable di sendRequest per column berdasarkan data excel.'
        ArrayList listInvitation = []

        'Declare variable untuk sendRequest'
        (listInvitation[0]) = (((((((((((((((((((((((((((((('{"email" :' + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('email'))) + ',"nama" :') + findTestData(excelPathGenerateLink).getValue(
            GlobalVariable.NumofColm, rowExcel('nama'))) + ',"tlp": ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('tlp'))) + ',"jenisKelamin" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('jenisKelamin'))) + ',"tmpLahir" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('tmpLahir'))) + ',"tglLahir" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('tglLahir'))) + ',"idKtp" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('idKtp'))) + ', "provinsi" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('provinsi'))) + ', "kota" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('kota'))) + ', "kecamatan" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('kecamatan'))) + ',"kelurahan": ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('kelurahan'))) + ',"kodePos" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('kodePos'))) + ',"alamat" : ') + findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
            rowExcel('alamat'))) + ',"selfPhoto" : ') + selfPhoto) + ',"idPhoto" : ') + idPhoto) + '}  ')

        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/Register Invitation', [('msg') : value, ('userData') : listInvitation[
                    0], ('callerId') : findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'mengambil status code berdasarkan response HIT API'
            statusCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika status codenya 0'
            if (statusCode == 0) {
                'declare arraylist arraymatch'
                ArrayList arrayMatch = []

                if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                    'declare arrayindex'
                    arrayindex = 0

                    'get data buat undangan dari DB'
                    ArrayList resultDataDiri = CustomKeywords.'connection.Registrasi.buatUndanganStoreDB'(conneSign, findTestData(
                            excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', '').toUpperCase())

                    'verify nama'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                                rowExcel('nama')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify tempat lahir'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                                rowExcel('tmpLahir')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    'parse Date from MM/dd/yyyy > yyyy-MM-dd'
                    sDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(resultDataDiri[arrayindex++], 'MM/dd/yyyy', 
                        'yyyy-MM-dd')

                    'verify tanggal lahir'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                                rowExcel('tglLahir')).replace('"', ''), sDate, false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify jenis kelamin'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                                rowExcel('jenisKelamin')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify email'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                                rowExcel('email')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify provinsi'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                                rowExcel('provinsi')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kota'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                                rowExcel('kota')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kecamatan'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                                rowExcel('kecamatan')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kelurahan'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                                rowExcel('kelurahan')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify kode pos'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                                rowExcel('kodePos')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), 
                            false, FailureHandling.CONTINUE_ON_FAILURE))
                }
                
                'jika data db tidak sesuai dengan excel'
                if (arrayMatch.contains(false)) {
                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                } else {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else {
                'call function get API error message'
                getAPIErrorMessage(respon)
            }
        } else {
            'write to excel status failed dan reason : '
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedHitAPI)
        }
    }
}

def getAPIErrorMessage(ResponseObject respon) {
    'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
    messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'write to excel status failed dan reason : '
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(excelPathGenerateLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
            '') + ';') + '<') + messageFailed) + '>')
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def encodeValue(String value, Connection conneSign) {
    'get invitation code dari db'
    String invCode = CustomKeywords.'connection.APIFullService.getInvitationCode'(conneSign, value)

    'Mengambil aes key based on tenant tersebut'
    String aesKey = CustomKeywords.'connection.APIFullService.getAesKeyEncryptUrl'(conneSign)

    'encrypt invitation code'
    CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(invCode, aesKey)
}

