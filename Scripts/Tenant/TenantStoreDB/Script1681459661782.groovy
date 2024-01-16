import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList arrayMatch = []

'check if action new/services'
if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase('New') || findTestData(
    excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase('Edit')) {
    'get data balance mutation dari DB'
    ArrayList result = CustomKeywords.'connection.Tenant.getTenantStoreDB'(conneSign, findTestData(excelPathTenant).getValue(
            GlobalVariable.NumofColm, rowExcel('$LabelRefNumber')))

    'get data services dari DB'
    ArrayList resultServices = CustomKeywords.'connection.Tenant.getTenantServicesDescription'(conneSign, findTestData(excelPathTenant).getValue(
            GlobalVariable.NumofColm, rowExcel('$NamaTenant')))

    'declare arrayindex'
    arrayindex = 0

    'verify tenant name'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('$NamaTenant')).toUpperCase(), 
            (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase('New')) {
        'verify tenant code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('$KodeTenant')).toUpperCase(), 
                (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
    } else {
        'skip'
        arrayindex++
    }
    
    'verify label ref number'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('$LabelRefNumber')).toUpperCase(), 
            (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify API Key'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('API Key')).toUpperCase(), 
            (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Email reminder'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase().replace(
                ';', ','), (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    ArrayList arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Services')).split(
        ';', -1)

    ArrayList arrayServicesBatasSaldo = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Batas Saldo Services')).split(
        ';', -1)

    'looping untuk verif services dan bata saldo'
    indexServices = 0

    for (indexExcel = 0; indexExcel < arrayServices.size(); indexExcel++) {
        String services = resultServices[indexServices++]

        'jika index service sama dengan array size, lakukan break'
        if (indexServices == arrayServices.size()) {
            break
        }
        
        if (services.equalsIgnoreCase(arrayServices[indexExcel])) {
            'verify services'
            arrayMatch.add(WebUI.verifyMatch(services.toUpperCase(), (arrayServices[indexExcel]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify service batas saldo'
            arrayMatch.add(WebUI.verifyMatch(resultServices[indexServices++], arrayServicesBatasSaldo[indexExcel], false, 
                    FailureHandling.CONTINUE_ON_FAILURE))
        }
    }
} else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase('Service')) {
    'get data balacne mutation dari DB'
    String result = CustomKeywords.'connection.Tenant.getTenantServices'(conneSign, findTestData(excelPathTenant).getValue(
            GlobalVariable.NumofColm, rowExcel('$Nama Tenants'))).replace('{', '').replace('}', '').replace('"', '').replace(
        ',', '')

    'split result to array'
    ArrayList resultarray = result.split(':0')

    'get array Services dari excel'
    ArrayList arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('ServicesCheck')).split(
        ';', -1)

    'verify services'
    arrayMatch.add(arrayServices.containsAll(resultarray))
}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

