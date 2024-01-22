import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'settingemail service tenant dimatikan/diaktifkan'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')).length() > 0) {
            'setting email service tenant'
            CustomKeywords.'connection.APIFullService.settingEmailServiceTenant'(conneSign, findTestData(excelPath).getValue(
                    GlobalVariable.NumofColm, rowExcel('Setting Email Service')))
        }
        
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
        } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'Yes') {
            'get tenant per case dari colm excel'
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        
        'HIT API Login untuk ambil bearer token'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')

            'HIT API'
            respon = WS.sendRequest(findTestObject('Postman/resendNotifSign', [('docId') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('Document ID')), ('callerId') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('username'))]))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
                'mengambil status code berdasarkan response HIT API'
                statusCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

                'ambil lama waktu yang diperlukan hingga request menerima balikan'
                elapsedTime = ((respon.elapsedTime / 1000) + ' second')

                'ambil body dari hasil respons'
                responseBody = respon.responseBodyContent

                'panggil keyword untuk proses beautify dari respon json yang didapat'
                CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

                'write to excel response elapsed time'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                    1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

                'jika status codenya 0'
                if (statusCode == 0) {
                    if (GlobalVariable.checkStoreDB == 'Yes') {
                        'cek balance mutation dan juga pemotongan saldo'
                        checkBalanceMutation(conneSign)
                    }
                    
                    'tulis sukses jika store DB berhasil'
                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                } else {
                    'call function get API error message'
                    getErrorMessageAPI(respon)
                }
            } else {
                'write to excel status failed dan reason : '
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedHitAPI)
            }
        } else {
            getErrorMessageAPI(responLogin)
        }
    }
}

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + message)) + 
        '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def checkBalanceMutation(Connection conneSign) {
    'deklarasi email signer'
    String emailSigner = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('username'))

    'deklarasi global variabel tenant'
    GlobalVariable.Tenant = CustomKeywords.'connection.DataVerif.getTenantCode'(conneSign, emailSigner)

    'conversi email menjadi format sha256 kalau perlu'
    if (!(emailSigner.contains('@'))) {
        emailSigner = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(emailSigner)
    }
    
    emailServiceOnTenant = CustomKeywords.'connection.DataVerif.getEmailService'(conneSign, GlobalVariable.Tenant)

    fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, emailSigner)

    mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

    if (mustUseWAFirst == '1') {
        'menggunakan saldo wa'
        ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'WhatsApp Message', fullNameUser)

        if (balmut.size() == 0) {
            GlobalVariable.FlagFailed = 1

            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')
        }
        
        if ((balmut[8]) != -1) {
            GlobalVariable.FlagFailed = 1

            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 'Saldo WA tidak terpotong')
        }
    } else {
        if (emailServiceOnTenant == 1) {
            useWAMessage = CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, GlobalVariable.Tenant)

            if (useWAMessage == '1') {
                'menggunakan saldo wa'
                ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'WhatsApp Message', 
                    fullNameUser)

                if (balmut.size() == 0) {
                    GlobalVariable.FlagFailed = 1

                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                'Reason Failed')) + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')
                }
                
                if ((balmut[8]) != -1) {
                    GlobalVariable.FlagFailed = 1

                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                'Reason Failed')) + ';') + 'Saldo WA tidak terpotong')
                }
            } else if (useWAMessage == '0') {
                'menggunakan saldo wa'
                ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'SMS Notif', fullNameUser)

                if (balmut.size() == 0) {
                    GlobalVariable.FlagFailed = 1

                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                'Reason Failed')) + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via SMS')
                }
                
                if ((balmut[8]) != -1) {
                    GlobalVariable.FlagFailed = 1

                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                'Reason Failed')) + ';') + 'Saldo SMS tidak terpotong')
                }
            }
        }
    }
    
    'cek apakah perlu untuk pengecekan DB'
    if ((GlobalVariable.checkStoreDB == 'Yes') && (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == 
    '1')) {
        WebUI.delay(1)

        'declare arraylist arraymatch'
        ArrayList arrayMatch = []

        'ambil data last transaction dari DB'
        ArrayList resultDB = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(conneSign, findTestData(excelPath).getValue(
                GlobalVariable.NumofColm, rowExcel('username')), 'Document')

        'declare arrayindex'
        arrayindex = 0

        'lakukan loop untuk pengecekan data'
        for (int i = 0; i < (resultDB.size() / 2); i++) {
            'verify business line dan office code'
            arrayMatch.add(WebUI.verifyMatch((resultDB[i]).toString(), (resultDB[(i + 3)]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
        }
        
        'jika data db tidak sesuai dengan excel'
        if (arrayMatch.contains(false)) {
            GlobalVariable.FlagFailed = 1

            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 'Transaksi OTP tidak masuk balance mutation')
        }
    }
}

