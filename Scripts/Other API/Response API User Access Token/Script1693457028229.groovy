import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable

if (sheet == '') {
    'get data file path'
    GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

    sheet = 'API Sent OTP Signing'

    emailSigner = findTestData(excelPath).getValue(GlobalVariable.NumofColm, 11).replace('"', '')
} else {
    emailSigner = findTestData(excelPath).getValue(GlobalVariable.NumofColm, 9).replace('"', '')
}

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get privyId dari DB'
privyId = CustomKeywords.'connection.APIFullService.getPrivyId'(conneSign, emailSigner)

'HIT API user access token'
respon = WS.sendRequest(findTestObject('APIFullService/Postman/API User Access Token', [('privyId') : privyId]))

'Jika status HIT API 201 OK'
if (WS.verifyResponseStatusCode(respon, 201, FailureHandling.OPTIONAL) == true) {
    code = WS.getElementPropertyValue(respon, 'code', FailureHandling.OPTIONAL)

    'mengambil response'
    token = WS.getElementPropertyValue(respon, 'data.token', FailureHandling.OPTIONAL)

    'print token'
    GlobalVariable.AccessToken = token

    'print token yang ada'
    println(token)

    'write to excel success'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
        1, GlobalVariable.StatusSuccess)
} else {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'errors.messages', FailureHandling.OPTIONAL).toString().replace('[', '').replace(
        ']', '')

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ('<' + message) + '>')
}

