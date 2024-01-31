import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

int countColmExcel = findTestData(excelPath).columnNumbers

semicolon = ';'

int splitnum = -1

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
        //	'verify Psre == default vendor yang active'
        //	arrayMatch.add(WebUI.verifyMatch('null', (result[arrayindex++]).toString(), false, FailureHandling.CONTINUE_ON_FAILURE))
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

		GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode'))
		
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'HIT API Login untuk token : andy@ad-ins.com'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('password'))]))

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'Parsing token menjadi GlobalVariable'
            GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')
        } else {
            getErrorMessageAPI(responLogin)
        }
        
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Token')) == 'No') {
            GlobalVariable.token = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Token'))
        }
        
        String signer = new String()

        signerTypeCode = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('signerTypeCode')).split(semicolon, 
            splitnum)

        signTypeCode = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('signTypeCode')).split(semicolon, 
            splitnum)

        signPage = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('signPage')).split(semicolon, splitnum)

        llx = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('llx')).split(semicolon, splitnum)

        lly = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('lly')).split(semicolon, splitnum)

        urx = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('urx')).split(semicolon, splitnum)

        ury = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('ury')).split(semicolon, splitnum)

        transform = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('transform')).split(semicolon, splitnum)

        position = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('position')).split(semicolon, splitnum)

        positionVida = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('positionVida')).split(semicolon, 
            splitnum)

        positionPrivy = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('positionPrivy')).split(semicolon, 
            splitnum)

        seqNo = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('seqNo')).split(semicolon, splitnum)

        String listSigner = ''

        for (i = 0; i < signerTypeCode.size(); i++) {
            ArrayList<String> list = []

			if (signTypeCode[i] == 'STD') {
				signerTypeCode[i] = 'null'
			} else {
				signerTypeCode[i] = '"' + signerTypeCode[i] + '"'
			}
			
            if ((i == 0) && (i == (signerTypeCode.size() - 1))) {
                list.add(((((((((((((((((((((('{"signerTypeCode": ' + (signerTypeCode[i])) + ',"signTypeCode": "') + (signTypeCode[
                    i])) + '","signPage":') + (signPage[i])) + ',"signLocation": {') + '"llx" :') + (llx[i])) + ',"lly" :') + 
                    (lly[i])) + ',"urx" :') + (urx[i])) + ',"ury" :') + (ury[i])) + '},"transform": "') + (transform[i])) + 
                    '","positionVida": "') + (positionVida[i])) + '","positionPrivy": "') + (positionPrivy[i])) + '","seqNo": "') + seqNo[i] + '"' +
                    '}')
            } else if (i == (signerTypeCode.size() - 1)) {
                list.add(((((((((((((((((((((('{"signerTypeCode": "' + (signerTypeCode[i])) + '","signTypeCode": "') + (signTypeCode[
                    i])) + '","signPage":') + (signPage[i])) + ',"signLocation": {') + '"llx" :') + (llx[i])) + '"lly" :') + 
                    (lly[i])) + '"urx" :') + (urx[i])) + '"ury" :') + (ury[i])) + '},"transform": "') + (transform[i])) + 
                    '","positionVida": ') + (positionVida[i])) + ',"positionPrivy": ') + (positionPrivy[i])) + ',"seqNo": ') + seqNo[i] + '"' +
                    '}')
            } else {
                list.add(((((((((((((((((((((('{"signerTypeCode": "' + (signerTypeCode[i])) + '","signTypeCode": "') + (signTypeCode[
                    i])) + '","signPage":') + (signPage[i])) + ',"signLocation": {') + '"llx" :') + (llx[i])) + '"lly" :') + 
                    (lly[i])) + '"urx" :') + (urx[i])) + '"ury" :') + (ury[i])) + '},"transform": "') + (transform[i])) + 
                    '","positionVida": ') + (positionVida[i])) + ',"positionPrivy": ') + (positionPrivy[i])) + ',"seqNo": ') + seqNo[i] + '"' +
                    '},')
            }
            
            listSigner = (listSigner + (list[i]))
        }
        
        'HIT API Login untuk token : andy@ad-ins.com'
        respon = WS.sendRequest(findTestObject('Postman/Add Document Template', [('callerId') : findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')), ('documentExample') : CustomKeywords.'customizekeyword.ConvertFile.base64File'(
                        findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentExample'))),('documentTemplateCode') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')), ('documentTemplateName') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateName')), ('documentTemplateDescription') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateDescription')), ('isActive') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('isActive')), ('isSequence') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('isSequence')), ('numberOfPage') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('numberOfPage')), ('paymentSignTypeCode') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('paymentSignTypeCode')), ('useSignQr') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('useSignQr')), ('tenantCode') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode')), ('vendorCode') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('vendorCode')), ('isSignLocOnly') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('isSignLocOnly')), ('signer') : listSigner]))

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

        'Jika status HIT API Login 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            statusCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'Jika status codenya 0'
            if (statusCode == 0) {
                if (GlobalVariable.checkStoreDB == 'Yes') {
                    'get data doc template dari DB'
                    ArrayList<String> result = CustomKeywords.'connection.PengaturanDokumen.dataDocTemplateStoreDB'(conneSign, 
                        findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')))

                    'declare arraylist arraymatch'
                    ArrayList<String> arrayMatch = []

                    'declare arrayindex'
                    arrayindex = 0

                    'cek untuk data diisi di excel atau tidak'
                    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')).length() > 
                    0) {
                        'verify doc template code'
                        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                        'documentTemplateCode')).toUpperCase(), (result[arrayindex++]).toString().toUpperCase(), 
                                false, FailureHandling.CONTINUE_ON_FAILURE))
                    } else {
                        arrayindex++
                    }
                    
                    'cek untuk data diisi di excel atau tidak'
                    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateName')).length() > 
                    0) {
                        'verify doc template name'
                        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                        'documentTemplateName')).toUpperCase(), (result[arrayindex++]).toString().toUpperCase(), 
                                false, FailureHandling.CONTINUE_ON_FAILURE))
                    } else {
                        arrayindex++
                    }
                    
                    'verify deskripsi'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'documentTemplateDescription')).toUpperCase(), (result[arrayindex++]).toUpperCase(), 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    'verify tipe pembayaran'
                    tipePembayaran = CustomKeywords.'connection.APIFullService.getPaymentTypeDescription'(conneSign, findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('paymentSignTypeCode')), findTestData(
                            excelPath).getValue(GlobalVariable.NumofColm, rowExcel('vendorCode')), findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('tenantCode')))

                    arrayMatch.add(WebUI.verifyMatch(tipePembayaran.toUpperCase(), (result[arrayindex++]).toUpperCase(), false, 
                            FailureHandling.CONTINUE_ON_FAILURE))

                    'cek untuk data diisi di excel atau tidak'
                    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('isActive')).length() > 
                    0) {
                        'verify status'
                        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                        'isActive')).replace('0', 'Inactive').replace('1', 'Active').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
                    } else {
                        arrayindex++
                    }
                    
                    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('vendorCode')).length() > 0) {
                        'verify Psre == inputan excel'
                        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                        'vendorCode')).toUpperCase(), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
                    } else {
                        arrayindex++
                    }
                    
                    'cek untuk data diisi atau tidak'
                    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('isSequence')).length() > 
                    0) {
                        'verify isSequence'
                        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                        'isSequence')).replace('0', 'Tidak').replace('1', 'Iya').toUpperCase(), (result[arrayindex++]).toUpperCase(), false, 
                                FailureHandling.CONTINUE_ON_FAILURE))
                    } else {
                        arrayindex++
                    }
                    
                    'cek untuk data diisi di excel atau tidak'
                    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('seqNo')).length() > 
                    0) {
                        String getUrutanSigning = CustomKeywords.'connection.PengaturanDokumen.getUrutanSigningNoDistinct'(conneSign, 
                            findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode')))

                        'verify urutan sequence sign'
                        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                        'seqNo')).toUpperCase(), getUrutanSigning.toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
                    }
                    
                    'verify qr sign'
                    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'useSignQr')), CustomKeywords.'connection.APIFullService.getUseSignQRFromDocTemplate'(
                                conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentTemplateCode'))), 
                            false, FailureHandling.CONTINUE_ON_FAILURE))

                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        GlobalVariable.FlagFailed = 1

                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    }
                }
                
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            getErrorMessageAPI(respon)
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

