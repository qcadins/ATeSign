import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'looping untuk menjalankan Main'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathAPIGetSignLink).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIGetSignLink, GlobalVariable.NumofColm, rowExcel(
                'Use Correct Base Url'))

        'Jika flag tenant no'
        if (findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 
        'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
        } else if (findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 
        'Yes') {
            'Input tenant'
            GlobalVariable.Tenant = findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 
        'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 
        'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        'inisialisasi arrayList'
        ArrayList list = [], listDocId = []
		
		List documentId = []

        'Mengambil document id dari excel dan displit'
        documentId = findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('$documentId')).split(
            ';', -1)

        for (int q = 0; q < documentId.size(); q++) {
            list.add(('"' + documentId.get(q)) + '"')

            if (q == 0) {
                listDocId.add(list.get(q))
            } else {
                listDocId.set(0, (listDocId.get(0) + ',') + list.get(q))
            }
        }
        
        'ubah menjadi string'
        String listDoc = listDocId.toString().replace('[', '').replace(']', '')

        'HIT API Sign Document'
        responSignDoc = WS.sendRequest(findTestObject('APIFullService/Postman/API Get Sign Link', [('callerId') : findTestData(
                        excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('$CallerId')), ('listDocumentId') : listDoc
                    , ('loginId') : findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('$loginId'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responSignDoc, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            statusCode = WS.getElementPropertyValue(responSignDoc, 'status.code')

            'Jika status codenya 0'
            if (statusCode == 0) {
                'get sign link'
                GlobalVariable.Response = WS.getElementPropertyValue(responSignDoc, 'signLink')

                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 
                    1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                'tulis di excel hasil link yang bisa digunakan untuk sign'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Sign Link') - 
                    1, GlobalVariable.NumofColm - 1, GlobalVariable.Response)

                'cek apakah user ingin melakukan process signing langsung'
                if (findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('Continue with Signing Process?')) == 
                'Yes') {
                    String refNum

                    'open browser'
                    WebUI.openBrowser('')

                    'navigate to url signlink'
                    WebUI.navigateToUrl(findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel(
                                'Sign Link')))

                    'maximized window'
                    WebUI.maximizeWindow()

                    if (documentId.size() > 1) {
                        listDoc.replace('"', '').replace(',', '')
                    } else {
                        listDoc.replace('"', '')
                    }
                    
                    'jika diketahui hanya ada satu dokumen'
                    if (documentId.size == 1) {
                        'ambil nomorkontrak untuk dikirim ke signing digisign'
                        refNum = CustomKeywords.'connection.APIFullService.getRefNumber'(conneSign, documentId[0])
                    } else {
                        'jika lebih dari 1 dokumen, auto bulksign'
                        refNum = ''
                    }
                    
                    'call test case signing digisign'
                    WebUI.callTestCase(findTestCase('Main Flow - Copy/Signing Digisign'), [('excelPathFESignDocument') : excelPathAPIGetSignLink
                            , ('sheet') : sheet, ('flowGetSignLink') : 'Yes', ('emailSigner') : findTestData(excelPathAPIGetSignLink).getValue(
                                GlobalVariable.NumofColm, rowExcel('$loginId')), ('documentId') : documentId, ('nomorKontrak') : refNum], 
                        FailureHandling.CONTINUE_ON_FAILURE)
                }
            } else {
                'write to excel status failed dan reason : message Failed dari response'
                messageFailed = WS.getElementPropertyValue(responSignDoc, 'status.message', FailureHandling.OPTIONAL).toString()

                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + '<') + messageFailed) + '>')
            }
        } else {
            'write to excel status failed dan reason : message Failed dari response'
            messageFailed = WS.getElementPropertyValue(responSignDoc, 'status.message', FailureHandling.OPTIONAL).toString()

            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(excelPathAPIGetSignLink).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                '<') + messageFailed) + '>')
        }
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

