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
		
		'get psre per case dari colm excel'
		GlobalVariable.Psre = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
		
        'encrypt invitation code'
        encryptMsg = encryptLink(conneSign, findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('email')))

        'HIT API Sign Document'
        responGetActLinkDIGI = WS.sendRequest(findTestObject('Postman/Get Activation Link DIGI', [('callerID') : findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('callerId')), ('msg') : ('"' + encryptMsg) + 
                    '"']))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responGetActLinkDIGI, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            status_Code = WS.getElementPropertyValue(responGetActLinkDIGI, 'status.code')

            'Jika status codenya 0'
            if (status_Code == 0) {
                'get url'
                url = WS.getElementPropertyValue(responGetActLinkDIGI, 'url')

                'get idNo'
                idNo = WS.getElementPropertyValue(responGetActLinkDIGI, 'idNo')

                'get fullName'
                fullName = WS.getElementPropertyValue(responGetActLinkDIGI, 'fullName')

                'write to excel url'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 4, GlobalVariable.NumofColm - 
                    1, ('<' + url) + '>')

                'write to excel idno dan fullname'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 5, GlobalVariable.NumofColm - 
                    1, ((('<' + idNo) + ';') + fullName) + '>')

                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            } else {
                getErrorMessageAPI(responGetActLinkDIGI)
            }
        } else {
            getErrorMessageAPI(responGetActLinkDIGI)
        }
    }
}

def getErrorMessageAPI(def respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + ('<' + message)) + 
        '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def encryptLink(Connection conneSign, String msg) {
    'get invitation code dari db'
    String invCode = CustomKeywords.'connection.APIFullService.getInvitationCode'(conneSign, msg)

    'Mengambil aes key based on tenant tersebut'
    String aesKey = CustomKeywords.'connection.APIFullService.getAesKeyEncryptUrl'(conneSign)

	println(invCode)
	println(aesKey)
    'enkripsi msg'
    encryptMsg = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(invCode, aesKey)

    println(encryptMsg)

    return encryptMsg
}

