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

String dateStart, dateEnd, deliveryStatus

for (GlobalVariable.NumofColm; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    status = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Status'))

    if (status == '') {
        break
    } else if (status.equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0

        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPath, GlobalVariable.NumofColm, rowExcel('Use Correct Base Url'))

        'get vendor per case dari colm excel'
        GlobalVariable.Psre = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Vendor Code*'))
        
       // userCorrectTenantCode = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Use Correct Tenant Code'))

        GlobalVariable.Tenant = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('tenantCode'))

        'ambil data date start'
        dateStart = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Report Date Start*'))

        'ambil data date start'
        dateEnd = findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Report Date End*'))

        'HIT API Login untuk token'
        responLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('username')), ('password') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                        rowExcel('password'))]))

        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL)) {
            GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')

            'check delivery status dan convert menjadi integer yang benar'
            if ((findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == 'All') || (findTestData(
                excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == '')) {
                'set deliverystatus kosong'
                deliveryStatus = ''
            } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == 'Not Started') {
                'set deliverystatus 0'
                deliveryStatus = '0'
            } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == 'Waiting') {
                'set deliverystatus 1'
                deliveryStatus = '1'
            } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == 'Failed') {
                'set deliverystatus 2'
                deliveryStatus = '2'
            } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == 'Delivered') {
                'set deliverystatus 3'
                deliveryStatus = '3'
            } else if (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Delivery Status*')) == 'Read') {
                'set deliverystatus 4'
                deliveryStatus = '4'
            }
            
            'HIT API utamanya'
            respon = WS.sendRequest(findTestObject('Postman/listMessageDelivery', [('callerId') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('username')), ('page') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('Page*')), ('messageMedia') : findTestData(excelPath).getValue(
                            GlobalVariable.NumofColm, rowExcel('Message Media*')), ('dateStart') : dateStart, ('dateEnd') : dateEnd
                        , ('deliveryStatus') : deliveryStatus, ('recipient') : findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Recipient*'))]))

            if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL)) {
                statusCode = WS.getElementPropertyValue(respon, 'status.code')

                elapsedTime = ((respon.elapsedTime / 1000) + ' seconds')

                responseBody = respon.responseBodyContent

                CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
                        excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))

                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') - 
                    1, GlobalVariable.NumofColm - 1, elapsedTime.toString())

                if (statusCode == 0) {
                    if (GlobalVariable.checkStoreDB == 'Yes') {
                        if (((WS.getElementPropertyValue(respon, 'page').toString() != '') && (WS.getElementPropertyValue(
                            respon, 'page').toString() != '0')) && (WS.getElementPropertyValue(respon, 'page').toString() != 
                        'null')) {
                            vendorname = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.vendorName')

                            reportTime = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.reportTime')

                            recipient = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.recipient')

                            trxNo = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.trxNo')

                            messageMedia = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.messageMedia')

                            deliveryState = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.deliveryStatus')

                            deliveryStatusInformation = WS.getElementPropertyValue(respon, 'listMessageDeliveryReport.deliveryStatusInformation')

                            int index = (WS.getElementPropertyValue(respon, 'page') - 1) * 10

                            arrayMatch = []

                            ArrayList result = CustomKeywords.'connection.APIFullService.getListMessageDeliveryAPIOnly'(
                                conneSign, GlobalVariable.Tenant, GlobalVariable.Psre, findTestData(excelPath).getValue(
                                    GlobalVariable.NumofColm, rowExcel('Message Media*')), dateStart, dateEnd, deliveryStatus, 
                                findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Recipient*')))

                            int arrayIndex = 0

                            'loop untuk pengecekan hasil dari DB'
                            for (index; index < vendorname.size(); index++) {
                                'verify vendorname'
                                arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], vendorname[index], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify reportTime'
                                arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], reportTime[index], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify recipient'
                                arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], recipient[index], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify trxNo'
                                arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], trxNo[index], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify messageMedia'
                                arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], messageMedia[index], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify deliveryStatus'
                                arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], deliveryState[index], false, FailureHandling.CONTINUE_ON_FAILURE))

                                'verify deliveryStatusInformation'
                                arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], deliveryStatusInformation[index], 
                                        false, FailureHandling.CONTINUE_ON_FAILURE))
                            }
                            
                            if (arrayMatch.contains(false)) {
                                GlobalVariable.FlagFailed = 1

                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, (findTestData(excelPath).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
                            }
                        }
                    }
                    
                    if (GlobalVariable.FlagFailed == 0) {
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                } else {
                    getErrorMessageAPI(respon)
                }
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            getErrorMessageAPI(responLogin)
        }
    }
}

def getErrorMessageAPI(ResponseObject respon) {
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPath).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + '<' + message + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

