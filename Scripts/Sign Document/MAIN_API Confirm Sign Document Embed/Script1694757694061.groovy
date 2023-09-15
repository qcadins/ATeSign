import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable

import java.nio.charset.StandardCharsets
import java.sql.Connection as Connection
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(API_Excel_Path).columnNumbers
	
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {

		'get tenant yang ditesting dari excel per colm'
		GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
		
		'get aesKet Tenant'
		aesKey = CustomKeywords.'connection.APIFullService.getAesKeyBasedOnTenant'(conneSign, GlobalVariable.Tenant)
		
		println(aesKey)
		'get office code dari db'
		officeCode = CustomKeywords.'connection.DataVerif.getOfficeCode'(conneSign, findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('documentId')))
		
		'pembuatan message yang akan dienkrip'
		msg = ((((('{"officeCode":"') + officeCode) + '","email":"') + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('email'))) + '"}')
		
		'encrypt and decode officecode + email'
		endcodedMsg = encryptEncodeValue(msg, aesKey)
		
		'encrypt and decode documentID'
		endcodedDocumentId = encryptEncodeValue(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('documentId')), aesKey)
		
        'HIT API Sign Document'
        responConfirmSignDoc = WS.sendRequest(findTestObject('Postman/Confirm Sign Document Embed', [('callerId') : findTestData(API_Excel_Path).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')), ('tenantCode') : '"' + GlobalVariable.Tenant + '"', ('msg') : '"' + endcodedMsg + '"', ('documentId') : '"' + endcodedDocumentId + '"', 
						('ipAddress') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('ipAddress')),
						('browser') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('browser'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responConfirmSignDoc, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            status_Code = WS.getElementPropertyValue(responConfirmSignDoc, 'status.code')

            'Jika status codenya 0'
            if (status_Code == 0) {
                'get current date'
                currentDate = new Date().format('yyyy-MM-dd')
			
				'get message'
				message = WS.getElementPropertyValue(responConfirmSignDoc, 'status')
				
				'write to excel response'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 5,
					GlobalVariable.NumofColm - 1, '<' + message + '>')
                
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                        GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                }
            } else {
                getErrorMessageAPI(responConfirmSignDoc)
            }
        } else {
            getErrorMessageAPI(responConfirmSignDoc)
        }
    }
    
}

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
        ('<' + message)) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def encryptEncodeValue(String value, String aesKey) {
    'enkripsi msg'
    encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(value, aesKey)

    println(encryptMsg)

    try {
		return URLEncoder.encode(encryptMsg, StandardCharsets.UTF_8.toString());
	} catch (UnsupportedEncodingException ex) {
		throw new RuntimeException(ex.getCause());
	}
}
