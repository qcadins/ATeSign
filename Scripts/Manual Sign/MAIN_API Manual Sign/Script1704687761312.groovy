import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'variable untuk keperluan split excel'
semicolon = ';'

delimiter = '\\|'

enter = '\\n'

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(API_Excel_Path, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode'))

        CustomKeywords.'connection.UpdateData.updateDBManualSignLevelNotification'(conneSign, API_Excel_Path, sheet)

        'Inisialisasi callerId'
        callerId = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))

        'Inisialisasi ref No'
        refNo = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo'))

        'Inisialisasi psreCode'
        psreCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$psreCode'))

        'Inisialisasi documentName'
        documentName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$documentName'))

        'Inisialisasi documentDate'
        documentDate = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$documentDate'))

        'Inisialisasi peruriDocType'
        peruriDocType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('peruriDocType'))

        'Inisialisasi isAutomaticStamp'
        isAutomaticStamp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('isAutomaticStamp'))

        'Inisialisasi paymentType'
        paymentType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$paymentType'))

        'Inisialisasi isSequence'
        isSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('isSequence'))

        String documentFile

        'Inisialisasi documentFile'
        if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 Document')) == 
        'Yes') {
            documentFile = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, rowExcel('documentFile')))) + '"')
        } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 Document')) == 
        'No') {
            documentFile = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('documentFile'))
        }
        
        'Inisialisasi tenantCode'
        tenantCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode'))

        'Inisialisasi officeCode'
        officeCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('officeCode'))

        'Inisialisasi regionCode'
        regionCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('regionCode'))

        'Inisialisasi businessLineCode'
        businessLineCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('businessLineCode'))

        'join all main parameter'
        String bodyAPI = (((((((((((((((((((((((((('{"audit": {"callerId": "' + callerId) + '"},"psreCode": "') + psreCode) + 
        '","tenantCode": "') + tenantCode) + '","referenceNo": "') + refNo) + '","documentName": "') + documentName) + '","documentDate": "') + 
        documentDate) + '","peruriDocType": "') + peruriDocType) + '","isAutomaticStamp": "') + isAutomaticStamp) + '","paymentType": "') + 
        paymentType) + '","isSequence": "') + isSequence) + '","officeCode": "') + officeCode) + '","regionCode": "') + 
        regionCode) + '","businessLineCode": "') + businessLineCode) + '","documentFile": ') + documentFile

        'pindahkan hasil bodiAPI ke bodyAPIFinal'
        String bodyAPIFinal = bodyAPI

        println(bodyAPIFinal)

        'clear variable body API untuk menampung stamp location'
        bodyAPI = ''

        'Inisialisasi stampPage'
        stampPage = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('stampPage')).split(semicolon, 
            -1)

        'Inisialisasi transformStamping'
        transformStamping = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('transform - Stamping')).split(
            enter, -1)

        'Inisialisasi notes'
        notes = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('notes')).split(semicolon, -1)

        'Inisialisasi stampLocation'
        stampLocation = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('stampLocation')).split(
            enter, -1)

        'looping untuk menggabungkan stamping location'
        for (index = 0; index < stampPage.size(); index++) {
            if ((stampPage.size() - 1) == index) {
                bodyAPI = (((((((((bodyAPI + '{"stampPage": ') + (stampPage[index])) + ',"transform": "') + (transformStamping[
                index])) + '","notes": "') + (notes[index])) + '","stampLocation": ') + (stampLocation[index])) + '}')

                bodyAPI = ((',"stampingLocations": [' + bodyAPI) + ']')
            } else if ((stampPage.size() - 1) != index) {
                bodyAPI = (((((((((bodyAPI + '{"stampPage": "') + (stampPage[index])) + '","transform": "') + (transformStamping[
                index])) + '","notes": "') + (notes[index])) + '","stampLocation": ') + (stampLocation[index])) + '},')
            }
        }
        
        bodyAPIFinal = (bodyAPIFinal + bodyAPI)

        bodyAPI = ''

        'Inisialisasi nama'
        nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$nama')).split(semicolon, -1)

        'Inisialisasi tlp'
        tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$tlp')).split(semicolon, -1)

        'Inisialisasi email'
        email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$email')).split(semicolon, -1)

        'Inisialisasi signerType'
        signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$signerType')).split(semicolon, 
            -1)

        'Inisialisasi signSequence'
        signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('signSequence')).split(semicolon, 
            -1)

        'Inisialisasi id'
        id = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('id')).split(enter, -1)

        'Inisialisasi signPage'
        signPage = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('signPage')).split(enter, -1)

        'Inisialisasi transformSign'
        transformSign = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('transform - Signing')).split(
            enter, -1)

        'Inisialisasi position'
        position = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('position')).split(enter, -1)

        'Inisialisasi positionVida'
        positionVida = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('positionVida')).split(enter, 
            -1)

        'Inisialisasi positionPrivy'
        positionPrivy = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('positionPrivy')).split(
            enter, -1)

        'Inisialisasi signLocation'
        signLocation = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('signLocation')).split(enter, 
            -1)

        'looping untuk menggabungkan Signer'
        for (signerIndex = 0; signerIndex < nama.size(); signerIndex++) {
            String signLoc = ''

            'Inisialisasi id'
            ids = (id[signerIndex]).split(semicolon, -1)

            'Inisialisasi signPage'
            signPages = (signPage[signerIndex]).split(semicolon, -1)

            'Inisialisasi transformSign'
            transformSigns = (transformSign[signerIndex]).split(semicolon, -1)

            'Inisialisasi position'
            positions = (position[signerIndex]).split(semicolon, -1)

            'Inisialisasi positionVida'
            positionVidas = (positionVida[signerIndex]).split(semicolon, -1)

            'Inisialisasi positionPrivy'
            positionPrivys = (positionPrivy[signerIndex]).split(semicolon, -1)

            'Inisialisasi signLocation'
            signLocations = (signLocation[signerIndex]).split(semicolon, -1)

            'looping untuk menggabungkan Sign location'
            for (index = 0; index < signPages.size(); index++) {
                if ((signPages.size() - 1) == index) {
                    signLoc = (((((((((((((((signLoc + '{"id": ') + (ids[index])) + ',"signPage": ') + (signPages[index])) + 
                    ',"transform": "') + (transformSigns[index])) + '","position": ') + (positions[index])) + ',"positionVida": ') + 
                    (positionVidas[index])) + ',"positionPrivy": ') + (positionPrivys[index])) + ',"signLocation": ') + 
                    (signLocations[index])) + '}')

                    signLoc = ((',"signLocations": [' + signLoc) + ']')
                } else if ((signPages.size() - 1) != index) {
                    signLoc = (((((((((((((((signLoc + '{"id": ') + (ids[index])) + ',"signPage": ') + (signPages[index])) + 
                    ',"transform": "') + (transformSigns[index])) + '","position": ') + (positions[index])) + ',"positionVida": ') + 
                    (positionVidas[index])) + ',"positionPrivy": ') + (positionPrivys[index])) + ',"signLocation": ') + 
                    (signLocations[index])) + '},')
                }
            }
            
            if ((nama.size() - 1) == signerIndex) {
                bodyAPI = (((((((((((((bodyAPI + '{"name":"') + (nama[signerIndex])) + '","phone": "') + (tlp[signerIndex])) + 
                '","email": "') + (email[signerIndex])) + '","signerTypeCode": "') + (signerType[signerIndex])) + '","seqNo": "') + 
                (signSequence[signerIndex])) + '"') + signLoc) + '}')

                bodyAPI = ((',"signers": [' + bodyAPI) + ']')
            } else if ((nama.size() - 1) != signerIndex) {
                bodyAPI = (((((((((((((bodyAPI + '{"name":"') + (nama[signerIndex])) + '","phone": "') + (tlp[signerIndex])) + 
                ',""email": "') + (email[signerIndex])) + '","signerTypeCode": "') + (signerType[signerIndex])) + '","seqNo": "') + 
                (signSequence[signerIndex])) + '"') + signLoc) + '},')
            }
        }
        
        'menggabungkan body request kedalam 1 variable'
        bodyAPIFinal = ((bodyAPIFinal + bodyAPI) + '}')

        'HIT API'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [('email') : findTestData(API_Excel_Path).getValue(
                        GlobalVariable.NumofColm, rowExcel('Email Login')), ('password') : findTestData(API_Excel_Path).getValue(
                        GlobalVariable.NumofColm, rowExcel('Password Login'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Use True Token')) != 'No') {
                'Parsing token menjadi GlobalVariable'
                GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')
            } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Use True Token')).equalsIgnoreCase(
                'No')) {
                GlobalVariable.token = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Token'))
            }
            
            println(GlobalVariable.token)

            'HIT API Manual Sign'
            responManualSign = WS.sendRequest(findTestObject('Postman/Manual Sign', [('request') : bodyAPIFinal]))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(responManualSign, 200, FailureHandling.OPTIONAL) == true) {
                'get Status Code'
                statusCode = WS.getElementPropertyValue(responManualSign, 'status.code')

                'ambil lama waktu yang diperlukan hingga request menerima balikan'
                elapsedTime = ((responManualSign.elapsedTime / 1000) + ' second')

                'ambil body dari hasil respons'
                responseBody = responManualSign.responseBodyContent

                'panggil keyword untuk proses beautify dari respon json yang didapat'
                CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                        API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

                'write to excel response elapsed time'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                    1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

                'Jika status codenya 0'
                if (statusCode == 0) {
                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                        if (GlobalVariable.checkStoreDB == 'Yes') {
                            arrayIndex = 0

                            result = CustomKeywords.'connection.ManualSign.getAPIManualSign'(conneSign, refNo)

                            ArrayList<String> arrayMatch = []

                            arrayMatch.add(WebUI.verifyMatch(psreCode, result[0], false, FailureHandling.CONTINUE_ON_FAILURE))

                            arrayMatch.add(WebUI.verifyMatch(refNo, result[1], false, FailureHandling.CONTINUE_ON_FAILURE))

                            arrayMatch.add(WebUI.verifyMatch(documentName, result[2], false, FailureHandling.CONTINUE_ON_FAILURE))

                            arrayMatch.add(WebUI.verifyMatch(documentDate, result[3], false, FailureHandling.CONTINUE_ON_FAILURE))

                            arrayMatch.add(WebUI.verifyMatch(peruriDocType, result[4], false, FailureHandling.CONTINUE_ON_FAILURE))

                            arrayMatch.add(WebUI.verifyMatch(isAutomaticStamp, result[5], false, FailureHandling.CONTINUE_ON_FAILURE))

                            arrayMatch.add(WebUI.verifyMatch(paymentType, result[6], false, FailureHandling.CONTINUE_ON_FAILURE))

                            arrayMatch.add(WebUI.verifyMatch(isSequence, result[7], false, FailureHandling.CONTINUE_ON_FAILURE))

                            if (officeCode != '') {
                                arrayMatch.add(WebUI.verifyMatch(officeCode, result[8], false, FailureHandling.CONTINUE_ON_FAILURE))
                            }
                            
                            if (regionCode != '') {
                                arrayMatch.add(WebUI.verifyMatch(regionCode, result[9], false, FailureHandling.CONTINUE_ON_FAILURE))
                            }
                            
                            if (businessLineCode != '') {
                                arrayMatch.add(WebUI.verifyMatch(businessLineCode, result[10], false, FailureHandling.CONTINUE_ON_FAILURE))
                            }
                            
                            arrayMatch.add(WebUI.verifyMatch('1', result[11], false, FailureHandling.CONTINUE_ON_FAILURE))

                            'get check saldo wa or sms di send document'
                            checkSaldoWAOrSMS(conneSign, findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('$email')))

                            'jika data db tidak sesuai dengan excel'
                            if (arrayMatch.contains(false)) {
                                GlobalVariable.FlagFailed = 1

                                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                            }
                        }
                    }
                } else {
                    getErrorMessageAPI(responManualSign)
                }
            } else {
                getErrorMessageAPI(responManualSign)
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(responLogin, 'error_description', FailureHandling.OPTIONAL).toString()

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
                    '') + ';') + '<') + message) + '>')

            GlobalVariable.FlagFailed = 1
        }
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
        ';') + '<') + message) + '>')

    GlobalVariable.FlagFailed = 1
}

def checkSaldoWAOrSMS(Connection conneSign, String emailSigner) {
    'inisialisasi arraylist balmut'
    ArrayList<String> balmut = []

    'inisialisasi tipesaldo'
    String tipeSaldo

    'get email per document menggunakan email signer yang diplit enter'
    ArrayList<String> emailPerDoc = emailSigner.split(';', -1)

    'looping per email per document'
    for (loopingEmailPerDoc = 0; loopingEmailPerDoc < emailPerDoc.size(); loopingEmailPerDoc++) {
        'get full name user dari email tersebut'
        fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, emailPerDoc[loopingEmailPerDoc])

        notifTypeDB = CustomKeywords.'connection.APIFullService.getWASMSFromNotificationType'(conneSign, emailPerDoc[loopingEmailPerDoc], 
            'MANUAL_SIGN_REQ', GlobalVariable.Tenant)

        if (notifTypeDB == 'Email') {
            continue
        }
        
        if ((notifTypeDB == '0') || (notifTypeDB == 'Level Tenant')) {
            'get email service dari email tersebut'
            emailServiceOnVendor = CustomKeywords.'connection.DataVerif.getEmailServiceAsVendorUser'(conneSign, emailPerDoc[
                loopingEmailPerDoc])

            'get settinog must use wa first'
            mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

            'jika must use wa first'
            if (mustUseWAFirst == '1') {
                'tipe saldonya menjadi wa'
                tipeSaldo = 'WhatsApp Message'

                'menggunakan saldo wa'
                balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

                checkingSaldo(balmut, tipeSaldo)
            } else {
                'jika email servicenya 1'
                if (emailServiceOnVendor == '1') {
                    'check use wa message'
                    useWAMessage = CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, GlobalVariable.Tenant)

                    'jika menggunakan wa'
                    if (useWAMessage == '1') {
                        tipeSaldo = 'WhatsApp Message'

                        'menggunakan saldo wa'
                        balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

                        checkingSaldo(balmut, tipeSaldo)
                    } else if (useWAMessage == '0') {
                        'jika tidak menggunakan use wa message, maka mengarah ke sms'

                        'ke sms / wa'
                        SMSSetting = CustomKeywords.'connection.DataVerif.getSMSSetting'(conneSign, 'Send Document')

                        'jika setting sms = 1'
                        if (SMSSetting == '1') {
                            'ke sms'
                            tipeSaldo = 'SMS Notif'

                            'get balmut dari sms '
                            balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

                            checkingSaldo(balmut, tipeSaldo)
                        }
                    }
                }
            }
        } else {
            tipeSaldo = notifTypeDB

            'menggunakan saldo wa'
            balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

            checkingSaldo(balmut, tipeSaldo)
        }
    }
}

def checkingSaldo(ArrayList<String> balmut, String tipeSaldo) {
    'jika balmutnya tidak ada value'
    if (balmut.size() == 0) {
        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
            ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via ') + tipeSaldo)
    } else if ((balmut[9]) != '-1') {
        GlobalVariable.FlagFailed = 1

        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 'Saldo ') + 
            tipeSaldo) + ' tidak terpotong')
    }
}

