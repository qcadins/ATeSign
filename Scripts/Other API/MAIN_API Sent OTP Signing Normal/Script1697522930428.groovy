import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

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
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPISentOTPSigning, GlobalVariable.NumofColm, 
            rowExcel('Use Correct base Url'))

        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 
        'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Wrong tenant Code'))
        } else if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 
        'Yes') {
            GlobalVariable.Tenant = findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel(
                    '$tenantCode'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Vendor Code')) == 
        'Yes') {
            'get psre per case dari excel'
            GlobalVariable.Psre = findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('$vendorCode'))
        } else if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Vendor Code')) == 
        'No') {
            'get api key salah dari excel'
            GlobalVariable.Psre = findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Vendor Code'))
        }
        
        'mengambil reset otp request numbernya awal berapa'
        reset_otp_request_num = Integer.parseInt(CustomKeywords.'connection.APIFullService.getResetCodeRequestNum'(conneSign, 
                findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))))

        'mengambil nilai otp awal baik berisi ataupun kosong'
        otp_code = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPISentOTPSigning).getValue(
                GlobalVariable.NumofColm, rowExcel('Email Login')))

        println(reset_otp_request_num)

        println(otp_code)

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

        if (listDocId.isEmpty()) {
            'ubah menjadi string'
            listDoc = ''
        } else {
            'ubah menjadi string'
            listDoc = listDocId.toString().replace('[', '').replace(']', '')
        }
        
        'HIT API'
        responLogin = WS.sendRequest(findTestObject('APIFullService - Privy/Postman/Login', [('email') : findTestData(excelPathAPISentOTPSigning).getValue(
                        GlobalVariable.NumofColm, rowExcel('Email Login')).toString(), ('password') : findTestData(excelPathAPISentOTPSigning).getValue(
                        GlobalVariable.NumofColm, rowExcel('Password Login')).toString()]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Use True Token')).equalsIgnoreCase(
                'Yes')) {
                'Parsing token menjadi GlobalVariable'
                GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')
            } else if (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Use True Token')).equalsIgnoreCase(
                'No')) {
                GlobalVariable.token = findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel(
                        'Wrong Token'))
            }
            
            'HIT API'
            respon = WS.sendRequest(findTestObject('Postman/Sent Otp Signing Normal', [('callerId') : ('"' + findTestData(
                            excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('$callerId'))) + '"'
                        , ('phoneNo') : ('"' + findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 
                            rowExcel('$phoneNo'))) + '"', ('vendorCode') : ('"' + findTestData(excelPathAPISentOTPSigning).getValue(
                            GlobalVariable.NumofColm, rowExcel('$vendorCode'))) + '"', ('tenantCode') : ('"' + findTestData(
                            excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('$tenantcode'))) + '"'
                        , ('listDocumentId') : listDoc]))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
                'get status code'
                code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

                'declare arraylist arraymatch'
                ArrayList arrayMatch = []

                'jika codenya 0'
                if (code == 0) {
                    if (GlobalVariable.Psre == 'PRIVY') {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                        'input di excel mengenai trxno yang telah didapat'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                                'OTP') - 1, GlobalVariable.NumofColm - 1, 'OTP tidak masuk ke DB(PRIVY)')
                    } else {
                        'ambil otp baru dari DB'
                        newOTP = CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPISentOTPSigning).getValue(
                                GlobalVariable.NumofColm, rowExcel('Email Login')))

                        'ambil resetnum dari DB'
                        newResetNum = Integer.parseInt(CustomKeywords.'connection.APIFullService.getResetCodeRequestNum'(
                                conneSign, findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel(
                                        'Email Login'))))

                        'input di excel mengenai trxno yang telah didapat'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel(
                                'OTP') - 1, GlobalVariable.NumofColm - 1, newOTP)

                        if (GlobalVariable.checkStoreDB == 'Yes') {
                            ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(
                                conneSign, CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('$documentId'))), 'Signing')

                            'lakukan loop untuk pengecekan data'
                            for (int i = 0; i < (officeRegionBline.size() / 3); i++) {
                                'verify business line dan office code'
                                arrayMatch.add(WebUI.verifyMatch((officeRegionBline[i]).toString(), (officeRegionBline[(i + 
                                        3)]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
                            }
                            
                            if (newOTP.toString() != 'null') {
                                'verify otp code tidak sama'
                                arrayMatch.add(WebUI.verifyNotEqual(newOTP, otp_code, FailureHandling.CONTINUE_ON_FAILURE))
                            }
                            
                            'verify otp code tidak sama'
                            arrayMatch.add(WebUI.verifyEqual(newResetNum, reset_otp_request_num + 1, FailureHandling.CONTINUE_ON_FAILURE))

                            if (arrayMatch.contains(false)) {
                                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, (findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                            } else {
                                'write to excel success'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 
                                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
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
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(excelPathAPISentOTPSigning).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
            '-', '') + ';') + '<') + message) + '>')

    GlobalVariable.FlagFailed = 1
}

