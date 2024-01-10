import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'Pembuatan pengisian variable di sendRequest per jumlah documentId.'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPath).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        'inisialisasi arrayList'
        ArrayList documentId = [], listDocId = []

        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'check if tidak mau menggunakan vendor code yang benar'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Vendor Code')) == 'No') {
            'set vendor kosong'
            GlobalVariable.Psre = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Vendor Code'))
        } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Vendor Code')) == 'Yes') {
            'get vendor per case dari colm excel'
            GlobalVariable.Psre = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
        }
        
        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
        } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'Yes') {
            'get tenant per case dari colm excel'
            GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        
        'get aesKet Tenant'
        aesKey = CustomKeywords.'connection.APIFullService.getAesKeyBasedOnTenant'(conneSign, GlobalVariable.Tenant)

        currentDate = LocalDateTime.now()

        localeIndonesia = new Locale('id', 'ID')

        formatter = DateTimeFormatter.ofPattern('yyyy-MM-dd HH:mm:ss', localeIndonesia)

        formattedDate = currentDate.format(formatter)

        if (aesKey.toString() != 'null') {
            'Mengambil document id dari excel dan displit'
            documentId = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Document ID')).split(';', 
                -1)

            'get office code dari db'
            officeCode = CustomKeywords.'connection.DataVerif.getOfficeCode'(conneSign, documentId[0])

            'pembuatan message yang akan dienkrip'
            msg = (((((('{\'officeCode\':\'' + officeCode) + '\',\'email\':\'') + findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                rowExcel('email'))) + '\',\'timestamp\':\'') + formattedDate) + '\'}')

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

        'HIT API Sign Balance availability'
        respon_signBalVal = WS.sendRequest(findTestObject('Postman/Sign Balance Availability Embed', [('callerId') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('docId') : listDoc, ('msg') : endcodedMsg]))

        'ambil lama waktu yang diperlukan hingga request menerima balikan'
        elapsedTime = (respon_signBalVal.elapsedTime / 1000) + ' second'

        'ambil body dari hasil respons'
        responseBody = respon_signBalVal.responseBodyContent

        'panggil keyword untuk proses beautify dari respon json yang didapat'
        CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

        'write to excel response elapsed time'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
            1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon_signBalVal, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            status_Code = WS.getElementPropertyValue(respon_signBalVal, 'status.code')

            'Jika status codenya 0'
            if (status_Code == 0) {
                'get vendor Code'
                GlobalVariable.Response = WS.getElementPropertyValue(respon_signBalVal, 'vendorCode')

                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)
            } else {
                getErrorMessageAPI(respon_signBalVal)
            }
        } else {
            getErrorMessageAPI(respon_signBalVal)
        }
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
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

def encryptEncodeValue(String value, String aesKey) {
    'enkripsi msg'
    encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(value, aesKey)
}

