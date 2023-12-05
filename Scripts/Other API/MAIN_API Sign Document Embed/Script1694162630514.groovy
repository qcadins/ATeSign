import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPath).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))
		
		GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))

        'Mengambil aes key based on tenant tersebut'
        String aesKey = CustomKeywords.'connection.APIFullService.getAesKeyEncryptUrl'(conneSign)

        'encrypt doc id'
        encryptdocId = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                rowExcel('documentid')), aesKey)

        'encrypt tenant, officecode, tenant'
        msg = encryptLink(conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('documentid')), 
            findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('email')), aesKey)

        'HIT API Sign Document'
        responsigndocEmbed = WS.sendRequest(findTestObject('Postman/Sign Doc Embed', [('callerId') : '"' + findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')) + '"', ('email') : ('"' + findTestData(excelPath).getValue(
                        GlobalVariable.NumofColm, rowExcel('email'))) + '"', ('docId') : ('"' + encryptdocId) + '"', ('msg') : ('"' + 
                    msg) + '"']))
		
        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responsigndocEmbed, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            status_Code = WS.getElementPropertyValue(responsigndocEmbed, 'status.code')

            'Jika status codenya 0'
            if (status_Code == 0) {				
				'get current date'
				currentDate = new Date().format('yyyy-MM-dd')
				
                'check Db'
                if (GlobalVariable.checkStoreDB == 'Yes') {
					for(timeout = 1; timeout <= 5; timeout++) {		
						'declare arraylist arraymatch'
						ArrayList<String> arrayMatch = []
						
						'get sign date from db'
						result = CustomKeywords.'connection.SendSign.getSignDocEmbedStoreDB'(conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
								rowExcel('documentid')))
						
						'verify date'
						arrayMatch.add(WebUI.verifyMatch(currentDate, result, false, FailureHandling.OPTIONAL))
						
						if(timeout == 5 && arrayMatch.contains(false)) {
							'Write To Excel GlobalVariable.StatusFailed and melebihi batas waktu delay'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
								(findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB + 'Job Sign tidak jalan selama delay 100 detik')
							
							GlobalVariable.FlagFailed = 1
						} else {
							'delay 20 detik'
							WebUI.delay(20)
							
							continue
						} 
					}
                }
                
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else {
                getErrorMessageAPI(responsigndocEmbed)
            }
        } else {
            getErrorMessageAPI(responsigndocEmbed)
        }
    }
}

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
        ('<' + message)) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def encryptLink(Connection conneSign, String documentId, String emailSigner, String aesKey) {
    officeCode = CustomKeywords.'connection.DataVerif.getOfficeCode'(conneSign, documentId)

    'pembuatan message yang akan dienkrip'
    msg = (((((('{"tenantCode":"' + findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))) + 
    '","officeCode":"') + officeCode) + '","email":"') + emailSigner) + '"}')

    'enkripsi msg'
    encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(msg, aesKey)

    println(msg)

    return encryptMsg
}

