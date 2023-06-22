import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'Pembuatan pengisian variable di sendRequest per jumlah documentId.'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).columnNumbers; (GlobalVariable.NumofColm)++) {

    'declare arraylist untuk list, listdocid, listemail'
    ArrayList<String> list, listDocId, listEmail

    'list dengan array 0 harus kosong'
    (listDocId[0]) = ''

    'list dengan array 0 harus kosong'
    (listEmail[0]) = ''

    'Mengambil document id dari excel dan displit'
    documentId = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12).split(';', -1)

    'mengambil isi email signer berdasarkan excel dan di split.'
    emailSigner = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11).split(';', -1)

    'Looping berdasarkan jumlah dokumen id di excel'
    for (int q = 1; q <= documentId.size(); q++) {
        'Memasukkan ke dalam list mengenai isi send Request dengan document Id excel'
        (list[(q - 1)]) = (('"documentId": "' + (documentId[(q - 1)])) + '",')

        'Memasukkan list kedalam listDocId agar menyatu'
        (listDocId[0]) = ((listDocId[0]) + (list[(q - 1)]))

        'Mengkosongkan List agar dapat digunakan di loop email'
        (list[(q - 1)]) = ''
    }
    
    'loop berdasarkan jumlah email signer di database'
    for (int i = 1; i <= emailSigner.size(); i++) {
        'Memasukkan ke dalam list mengenai isi send Request dengan email'
        (list[(i - 1)]) = (('"email": "' + (emailSigner[(i - 1)])) + '",')

        'Memasukkan list kedalam listEmail agar menyatu'
        (listEmail[0]) = ((listEmail[0]) + (list[(i - 1)]))

        'Mengkosongkan List agar dapat digunakan'
        (list[(i - 1)]) = ''
    }
    
    'HIT API Login untuk token : andy@ad-ins.com'
    respon_login = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData('Login/Login').getValue(2, 
                    3), ('password') : findTestData('Login/Login').getValue(3, 3)]))

    'Jika status HIT API Login 200 OK'
    if (WS.verifyResponseStatusCode(respon_login, 200, FailureHandling.OPTIONAL) == true) {
        'Parsing token menjadi GlobalVariable'
        GlobalVariable.token = WS.getElementPropertyValue(respon_login, 'access_token')

        'HIT API Sign Document'
        respon_signdoc = WS.sendRequest(findTestObject('Postman/Sign Doc', [('callerId') : ('"' + findTestData(API_Excel_Path).getValue(
                        GlobalVariable.NumofColm, 9)) + '"', ('email') : listEmail[0], ('documentId') : listDocId[0], ('msg') : ('"' + 
                    findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13)) + '"']))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon_signdoc, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            status_Code = WS.getElementPropertyValue(respon_signdoc, 'status.code')

            'Jika status codenya 0'
            if (status_Code == 0) {
                'get vendor Code'
                GlobalVariable.Response = WS.getElementPropertyValue(respon_signdoc, 'vendorCode')

                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sign Document', 
                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                'Memanggil testCase mengenai SignDocumentStoreDb'
                WebUI.callTestCase(findTestCase('Sign_Document/SignDocumentStoreDb'), [('API_Excel_Path') : 'Registrasi/SignDocument'], 
                    FailureHandling.CONTINUE_ON_FAILURE)

                'Jika tidak ada, maka '
            } else {
                'write to excel status failed dan reason : message Failed dari response'
                messageFailed = WS.getElementPropertyValue(respon_signdoc, 'status.message', FailureHandling.OPTIONAL).toString()

                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                        '-', '') + ';') + messageFailed)
            }
        } else {
            'write to excel status failed dan reason : message Failed dari response'
            messageFailed = WS.getElementPropertyValue(respon_signdoc, 'status.message', FailureHandling.OPTIONAL).toString()

            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + messageFailed)
        }
    } else {
        'write to excel status failed dan reason : failed hit api'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace('-', 
                '') + ';') + GlobalVariable.ReasonFailedHitAPI)

        'call test case login inveditor'
        WebUI.callTestCase(findTestCase('Login/Login_Inveditor'), [:], FailureHandling.STOP_ON_FAILURE)

        'call test case error report'
        WebUI.callTestCase(findTestCase('Sign_Document/ErrorReport'), [('excelPathSignDoc') : 'Registrasi/BulkSignDocument'], 
            FailureHandling.STOP_ON_FAILURE)

        'close browser'
        WebUI.closeBrowser()
    }
}

