import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = []

'check if action new/services'
if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('New') || findTestData(excelPathTenant).getValue(
    GlobalVariable.NumofColm, 7).equalsIgnoreCase('Edit')) {
    'get data balance mutation dari DB'
    ArrayList<String> result = CustomKeywords.'connection.Tenant.getTenantStoreDB'(conneSign, findTestData(excelPathTenant).getValue(
            GlobalVariable.NumofColm, 14))

    'get data services dari DB'
    ArrayList<String> resultServices = CustomKeywords.'connection.Tenant.getTenantServicesDescription'(conneSign, findTestData(
            excelPathTenant).getValue(GlobalVariable.NumofColm, 12))

    'declare arrayindex'
    arrayindex = 0

    'verify tenant name'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 12).toUpperCase(), 
            (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('New')) {
        'verify tenant code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 13).toUpperCase(), 
                (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
    } else {
        'skip'
        arrayindex++
    }
    
    'verify label ref number'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 14).toUpperCase(), 
            (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify API Key'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 16).toUpperCase(), 
            (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Email reminder'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 21).toUpperCase().replace(';',','), 
            (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    ArrayList<String> arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 18).split(';', -1)

    ArrayList<String> arrayServicesBatasSaldo = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 19).split(
        ';', -1)

    'looping untuk verif services dan bata saldo'
    indexServices = 0

    for (indexExcel = 0; indexExcel < arrayServices.size(); indexExcel++) {
        String services = resultServices[indexServices++]

        if (services.equalsIgnoreCase(arrayServices[indexExcel])) {
            'verify services'
            arrayMatch.add(WebUI.verifyMatch(services.toUpperCase(), (arrayServices[indexExcel]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify service batas saldo'
            arrayMatch.add(WebUI.verifyMatch(resultServices[indexServices++], arrayServicesBatasSaldo[indexExcel], false, 
                    FailureHandling.CONTINUE_ON_FAILURE))
        }
    }
} else if (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Service')) {
    'get data balacne mutation dari DB'
    String result = CustomKeywords.'connection.Tenant.getTenantServices'(conneSign, findTestData(excelPathTenant).getValue(
            GlobalVariable.NumofColm, 9)).replace('{', '').replace('}', '').replace('"', '').replace(',', '')

    'split result to array'
    ArrayList<String> resultarray = result.split(':0')

    'get array Services dari excel'
    ArrayList<String> arrayServices = findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 25).split(';', -1)

    'verify services'
    arrayMatch.add(arrayServices.containsAll(resultarray))
}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('Tenant', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathTenant).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

