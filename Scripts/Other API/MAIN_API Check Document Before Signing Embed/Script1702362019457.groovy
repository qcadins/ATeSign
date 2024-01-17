import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import java.nio.charset.StandardCharsets as StandardCharsets
import java.sql.Connection as Connection
import java.time.LocalDateTime as LocalDateTime
import java.time.format.DateTimeFormatter as DateTimeFormatter
import java.util.regex.Matcher as Matcher
import java.util.regex.Pattern as Pattern

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'Connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

'looping API Confirm OTP'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        'inisialisasi arrayList'
        ArrayList documentId = [], listDocId = []

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'get tenant per case dari colm excel'
        GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        
        'Mengambil document id dari excel dan displit'
        documentId = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Document ID')).split(';', -1)

        'get office code dari db'
        officeCode = CustomKeywords.'connection.DataVerif.getOfficeCode'(conneSign, documentId[0])

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

            url = 'embed/document/checkDocumentBeforeSigningEmbed'
        } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Embed Version')).equalsIgnoreCase(
            'V1')) {
            'get aesKet general'
            aesKey = CustomKeywords.'connection.DataVerif.getAESKey'(conneSign)

            'pembuatan message yang akan dienkrip'
            msg = (((((('{\'officeCode\':\'' + officeCode) + '\',\'email\':\'') + findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                rowExcel('email'))) + '\',\'tenantCode\':\'') + GlobalVariable.Tenant) + '\'}')

            url = 'document/checkDocumentBeforeSigningEmbed'
        }
        
        if (aesKey.toString() != 'null') {
            if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Msg')) == 'No') {
                'officecode + email + time stamp tanpa encrypt'
                endcodedMsg = msg
            } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Msg')) == 'Yes') {
                'encrypt and decode officecode + email + time stamp'
                endcodedMsg = encryptEncodeValue(msg, aesKey)
            }
            
            for (int q = 0; q < documentId.size(); q++) {
                if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct DocumentID')) == 'No') {
                    encryptDocID = documentId
                } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct DocumentID')) == 
                'Yes') {
                    encryptDocID = encryptEncodeValue((documentId[q]).toString(), aesKey)
                }
                
                listDocId.add(('"' + encryptDocID) + '"')
            }
        } else {
            endcodedMsg = ''
        }
        
        'ubah menjadi string'
        String listDoc = listDocId.toString().replace('[', '').replace(']', '')

        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/Check Document Before Signing Embed', [('docId') : 
                        listDoc, ('msg') : parseCodeOnly(endcodedMsg), ('url') : url]))

        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            statusCode = WS.getElementPropertyValue(respon, 'status.code')

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

            'Jika status codenya 0'
            if (statusCode == 0) {
                'get  doc id'
                docId = WS.getElementPropertyValue(respon, 'listCheckDocumentBeforeSigning.documentId', FailureHandling.OPTIONAL)

                'get  signing Process'
                signingProcess = WS.getElementPropertyValue(respon, 'listCheckDocumentBeforeSigning.signingProcess', FailureHandling.OPTIONAL)

                if (GlobalVariable.checkStoreDB == 'Yes') {
                    'declare arraylist arraymatch'
                    ArrayList arrayMatch = []

                    for (index = 0; index < docId.size(); index++) {
                        println(signingProcess)

                        'decrypt docid'
                        decryptedDocId = CustomKeywords.'customizekeyword.ParseText.parseDecrypt'(docId[index], aesKey)

                        'get data from DB'
                        ArrayList resultDB = CustomKeywords.'connection.APIFullService.getDocSignSequence'(conneSign, decryptedDocId, 
                            findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', ''))

                        arrayIndex = 0

                        'verify doc ID'
                        arrayMatch.add(WebUI.verifyMatch(decryptedDocId, resultDB[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                        'verify status'
                        arrayMatch.add(WebUI.verifyMatch(signingProcess[index], resultDB[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))
                    }
                    
                    'jika data db tidak sesuai dengan excel'
                    if (arrayMatch.contains(false)) {
                        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel(
                                    'Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                    } else {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                }
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            'call function get error message API'
            getErrorMessageAPI(respon)
        }
    }
}

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ('<' + message) + '>')
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def encryptEncodeValue(String value, String aesKey) {
    'enkripsi msg'
    CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(value, aesKey)
}

def parseCodeOnly(String url) {
    'ambil data sesudah "code="'
    Pattern pattern = Pattern.compile('([^&]+)')

    'ambil matcher dengan URL'
    Matcher matcher = pattern.matcher(url)

    'cek apakah apttern nya sesuai'
    if (matcher.find()) {
        'ubah jadi string'
        String code = matcher.group(1)

        'decode semua ascii pada url'
        code = URLDecoder.decode(code, 'UTF-8')

        return code
    }
    
    ''
}

