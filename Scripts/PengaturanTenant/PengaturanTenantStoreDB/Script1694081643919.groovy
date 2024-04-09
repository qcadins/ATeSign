import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

ArrayList arrayMatch = []

ArrayList balance = []

ArrayList resultDbNew = []

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'Mengambil value excel setelah diedit pengaturan tenant'
resultDbNew = CustomKeywords.'connection.PengaturanTenant.getPengaturanTenant'(conneSign, findTestData(excelPathFEPengaturanTenant).getValue(
        GlobalVariable.NumofColm, rowExcel('Email Login')).toUpperCase())

'declare arrayIndex menjadi 0'
arrayIndex = 0

'verify login'
arrayMatch.add(WebUI.verifyMatch(CustomKeywords.'connection.MaskingEsign.maskData'(findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Email Login')).toUpperCase()), 
        resultDbNew[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify waktu edit'
arrayMatch.add(WebUI.verifyMatch(currentDate, resultDbNew[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'membuat emailDb menjadi sorted berdasarkan asc'
emailDb = resultDbNew[arrayIndex++].split(',')*.trim()

'membuat email Excel menjadi sorted berdasarkan asc'
emailData = findTestData(excelPathFEPengaturanTenant)
    .getValue(GlobalVariable.NumofColm, rowExcel('Email Reminder Saldo'))
    .split(',')

sortedEmails = emailData*.trim().sort()

emailExcel = sortedEmails.join(',')

'verifikasi email'
arrayMatch.add(WebUI.verifyMatch(emailExcel, emailDb, false, FailureHandling.CONTINUE_ON_FAILURE))

'verify Label Ref Number'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('$Label Ref Number')), 
        resultDbNew[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify URL upload'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('URL Upload')), 
        resultDbNew[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'Mengambil value treshold balance di db dan dipecah'
balance = (resultDbNew[arrayIndex]).replace('{', '').replace('}', '').replace('"', '').split(',', -1)

'looping berdasarkan jumlah tipe saldo'
for (j = 0; j < tipeSaldo.size(); j++) {
    'looping berdasarkan jumlah array balance'
    for (i = 0; i < balance.size(); i++) {
        'split menggunakan : ke variable balances'
        balances = (balance[i]).split(':', -1)

        'Jika balances yang ke 0, yaitu Tipe Saldo sama dengan tipe Saldo excel'
        if ((balances[0]) == (tipeSaldo[j]).toUpperCase()) {
            'verify tipe saldo'
            arrayMatch.add(WebUI.verifyMatch(balances[0], tipeSaldo[j], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify saldo tipe saldo'
            arrayMatch.add(WebUI.verifyEqual(balances[1], saldoTipeSaldo[j], FailureHandling.CONTINUE_ON_FAILURE))
        }
    }
}

'menambah arrayIndex'
arrayIndex++

'verify stamping otomatis'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('isStampingOtomatis')), 
        resultDbNew[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

arrayIndex++

'verify radio_button'
arrayMatch.add(WebUI.verifyMatch(use_wa, resultDbNew[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify callback url'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('URL Callback')), 
        resultDbNew[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify url redirect aktivasi'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Url Redirect Aktivasi')), 
        resultDbNew[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'verify url redirect tanda tangan'
arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Url Redirect Tanda tangan')), 
        resultDbNew[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

'Jika storedbnya ada false'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
            '-', '') + ';') + GlobalVariable.ReasonFailedStoredDB)

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

