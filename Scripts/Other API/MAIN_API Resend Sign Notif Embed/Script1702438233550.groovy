import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import java.time.LocalDateTime as LocalDateTime
import java.time.format.DateTimeFormatter as DateTimeFormatter
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
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

        'get tenant per case dari colm excel'
        GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))

        'get office code dari db'
        officeCode = CustomKeywords.'connection.DataVerif.getOfficeCode'(conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                rowExcel('Document ID')))

        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Embed Version')).equalsIgnoreCase('V2')) {
            'get aesKet Tenant'
            aesKey = CustomKeywords.'connection.APIFullService.getAesKeyBasedOnTenant'(conneSign, GlobalVariable.Tenant)

            currentDate = LocalDateTime.now()

            localeIndonesia = new Locale('id', 'ID')

            formatter = DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss', localeIndonesia)

            formattedDate = currentDate.format(formatter)

            'pembuatan message yang akan dienkrip'
            msg = (((((('{\'officeCode\':\'' + officeCode) + '\',\'email\':\'') + findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                rowExcel('email'))) + '\',\'timestamp\':\'') + formattedDate) + '\'}')

            url = 'embed/document/resendSignNotif'
        } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Embed Version')).equalsIgnoreCase(
            'V1')) {
            'get aesKet general'
            aesKey = CustomKeywords.'connection.DataVerif.getAESKey'(conneSign)

            'pembuatan message yang akan dienkrip'
            msg = (((((('{\'officeCode\':\'' + officeCode) + '\',\'email\':\'') + findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                rowExcel('email'))) + '\',\'tenantCode\':\'') + GlobalVariable.Tenant) + '\'}')

            url = 'document/resendSignNotif'
        }
        
        if (aesKey.toString() != 'null') {
            if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Msg')) == 'No') {
                'officecode + email + time stamp tanpa encrypt'
                endcodedMsg = msg
            } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Msg')) == 'Yes') {
                'encrypt and decode officecode + email + time stamp'
                endcodedMsg = encryptEncodeValue(msg, aesKey)
            }
            
            if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct DocumentID')) == 'No') {
                'get document id polos dari excel'
                endcodedDocumentId = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Document ID'))
            } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct DocumentID')) == 
            'Yes') {
                'encrypt document id'
                endcodedDocumentId = encryptEncodeValue(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                            'Document ID')), aesKey)
            }
        } else {
            endcodedMsg = ''
        }
        
        println(endcodedMsg)

        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/Resend Sign Notif Embed', [('callerId') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')), ('documentId') : endcodedDocumentId, ('msg') : endcodedMsg
                    , ('url') : url]))

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
                if ((GlobalVariable.checkStoreDB == 'Yes') && findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                    rowExcel('Embed Version')).equalsIgnoreCase('V1')) {
                    emailServiceOnTenant = CustomKeywords.'connection.DataVerif.getEmailService'(conneSign, GlobalVariable.Tenant)

                    String emailSHA256

                    if (!(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('email')).contains('@'))) {
                        emailSHA256 = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(findTestData(excelPath).getValue(
                                GlobalVariable.NumofColm, rowExcel('email')))
                    } else {
                        emailSHA256 = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('email'))
                    }
                    
                    fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, emailSHA256)

                    mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

                    if (mustUseWAFirst == '1') {
                        'menggunakan saldo wa'
                        ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'WhatsApp Message', 
                            fullNameUser)

                        if (balmut.size() == 0) {
                            GlobalVariable.FlagFailed = 1

                            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')
                        }
                        
                        if ((balmut[8]) != -1) {
                            GlobalVariable.FlagFailed = 1

                            'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + 'Saldo WA tidak terpotong')
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
                                        GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                            rowExcel('Reason Failed')) + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via WhatsApp')
                                }
                                
                                if ((balmut[8]) != -1) {
                                    GlobalVariable.FlagFailed = 1

                                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                        GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                            rowExcel('Reason Failed')) + ';') + 'Saldo WA tidak terpotong')
                                }
                            } else if (useWAMessage == '0') {
                                'menggunakan saldo wa'
                                ArrayList balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'SMS Notif', 
                                    fullNameUser)

                                if (balmut.size() == 0) {
                                    GlobalVariable.FlagFailed = 1

                                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                        GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                            rowExcel('Reason Failed')) + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman OTP Via SMS')
                                }
                                
                                if ((balmut[8]) != -1) {
                                    GlobalVariable.FlagFailed = 1

                                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                        GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                            rowExcel('Reason Failed')) + ';') + 'Saldo SMS tidak terpotong')
                                }
                            }
                        }
                    }
                    
                    'declare arraylist arraymatch'
                    ArrayList arrayMatch = []

                    'ambil data last transaction dari DB'
                    ArrayList resultDB = CustomKeywords.'connection.DataVerif.getBusinessLineOfficeCode'(conneSign, findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('email')), 'Document')

                    println(resultDB)

                    'declare arrayindex'
                    arrayindex = 0

                    'lakukan loop untuk pengecekan data'
                    for (int i = 0; i < (resultDB.size() / 2); i++) {
                        'verify business line dan office code'
                        arrayMatch.add(WebUI.verifyMatch((resultDB[i]).toString(), (resultDB[(i + 3)]).toString(), false, 
                                FailureHandling.CONTINUE_ON_FAILURE))
                    }
                    
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        GlobalVariable.FlagFailed = 1

                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'Reason Failed')) + ';') + 'Transaksi OTP tidak masuk balance mutation')
                    }
                }
                
                'tulis sukses jika store DB berhasil'
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else {
                'call function get API error message'
                getErrorMessageAPI(respon)
            }
        } else {
            'write to excel status failed dan reason : '
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
                ';') + GlobalVariable.ReasonFailedHitAPI)
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

def encryptEncodeValue(String value, String aesKey) {
    'enkripsi msg'
    CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(value, aesKey)
}

