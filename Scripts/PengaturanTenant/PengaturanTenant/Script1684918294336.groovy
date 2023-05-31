import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.sql.Connection as Connection

'get current date'
def currentDate = new Date().format('yyyy-MM-dd')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'sheet untuk dicetak'
sheet = 'PengaturanTenant'

'looping berdasarkan Number of column'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathFEPengaturanTenant).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'declare arraylist arraymatch'
        ArrayList arrayMatch = []

        'declare result Db setelah edit, result Db untuk sebelum edit, arraylist untuk balance'
        ArrayList resultDbNew = [], resultDbPrevious = [], balance = []

        'declare variable inisialisasi for'
        int i, j

        'inisalisasi arrayIndex yaitu 0'
        arrayIndex = 0

        'declare variable string'
        String activationCallBackUrl, descriptionBalanceType

        'Call test Case untuk login sebagai admin wom admin client'
        WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathFEPengaturanTenant, ('sheet') : sheet], 
            FailureHandling.STOP_ON_FAILURE)

        'Klik menu pengaturan tenant'
        WebUI.click(findTestObject('Object Repository/PengaturanTenant/menu_PengaturanTenant'))

        'Delay 5 karena batas Saldonya loading lumayan lama.'
        WebUI.delay(5)
		
        'Verifikasi sudah pindah page'
        WebUI.verifyElementPresent(findTestObject('Object Repository/PengaturanTenant/label_PengaturanTenant'), GlobalVariable.TimeOut, 
            FailureHandling.CONTINUE_ON_FAILURE)
		
		'Mengambil hasil db untuk sebelum diedit untuk mendapatkan total emailnya ada berapa'
		resultDbPrevious = CustomKeywords.'connection.DataVerif.getPengaturanTenant'(conneSign, findTestData('Login/Login').getValue(
		2, 2).toUpperCase())
		
		resultDbPrevious[5] = resultDbPrevious[5].replace('{','').replace('}','').replace('"','').split(',',-1)

		ArrayList<String> arrtipeSaldoBefore = []
		String tipeSaldoBefore = ''
		String saldoTipeSaldoBefore = ''
		
		for(i = 0 ; i < resultDbPrevious[5].size();i++) {
			arrtipeSaldoBefore[i] = resultDbPrevious[5][i].split(':',-1)
			println arrtipeSaldoBefore[i]
			if(i == resultDbPrevious[5].size() - 1) {
				tipeSaldoBefore = tipeSaldoBefore + arrtipeSaldoBefore[i][0]
				saldoTipeSaldoBefore = saldoTipeSaldoBefore + arrtipeSaldoBefore[i][1]
			}else {
			tipeSaldoBefore = tipeSaldoBefore + arrtipeSaldoBefore[i][0] + ','
			saldoTipeSaldoBefore = saldoTipeSaldoBefore + arrtipeSaldoBefore[i][1] + ','
			}
			println tipeSaldoBefore
			println saldoTipeSaldoBefore
		}
		
		'loop berdasarkan tipe saldo'
		for (i = 0; i < tipeSaldoBefore.size(); i++) {
			'modify object mencari object berdasarkan id nya tipe saldo tersebut'
			modifyObjectTipeBatasSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/PengaturanTenant/label_TipeBatasSaldo'),
				'for', 'equals', ('' + (tipeSaldoBefore[i])) + '', true)

			'Mengambil deskripsi dari tipe saldo tersebut'
			descriptionBalanceType = CustomKeywords.'connection.DataVerif.getDescriptionBalanceType'(conneSign, tipeSaldo[
				i])

			'Jika pada loopingan terakhir'
			if (i == (tipeSaldoBefore.size() - 1)) {
				'Jika masih tidak ketemu textnya'
				if (WebUI.getText(modifyObjectTipeBatasSaldo) == null) {
					'Write excel tidak ketemu dengan tipe saldo tersebut'
					CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (((findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm,
							2) + ';') + GlobalVariable.ReasonFailedNoneUI) + 'mengenai tipe saldo ') + (tipeSaldoBefore[i]))
				}
			}
			
			'Jika teks yang didapat itu sama dengan deskripsi batas saldo'
			if (WebUI.getText(modifyObjectTipeBatasSaldo) == descriptionBalanceType) {
				
				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTipeBatasSaldo),descriptionBalanceType, false, FailureHandling.OPTIONAL), ' dengan alasan tidak cocok antara ' + WebUI.getText(modifyObjectTipeBatasSaldo) + ' dan ' + descriptionBalanceType)
				
				'modify object untuk input batas saldo'
				modifyObjectInputBatasSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/PengaturanTenant/input_BatasSaldo'),
					'id', 'equals', ('' + (tipeSaldoBefore[i])) + '', true)

				checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectInputBatasSaldo),descriptionBalanceType, false, FailureHandling.OPTIONAL), ' dengan alasan tidak cocok antara ' + WebUI.getText(modifyObjectTipeBatasSaldo) + ' dan ' + descriptionBalanceType)
				
			}
		}
		
		arrayIndex = resultDbPrevious.indexOf(countEmailBefore)
		
		checkVerifyEqualorMatch(WebUI.verifyMatch(descriptionBalanceType, activationCallBackUrl, false), reason)
		
        'Set text untuk Label Ref Number'
        WebUI.setText(findTestObject('Object Repository/PengaturanTenant/input_LabelRefNumber'), findTestData(excelPathFEPengaturanTenant).getValue(
                GlobalVariable.NumofColm, 8))

        'Set text untuk input URL Upload'
        WebUI.setText(findTestObject('Object Repository/PengaturanTenant/input_URLUpload'), findTestData(excelPathFEPengaturanTenant).getValue(
                GlobalVariable.NumofColm, 9))

        'Klik Copy'
        WebUI.click(findTestObject('Object Repository/PengaturanTenant/button_Copy'))

        'Check error log'
        checkerrorLog()

        'tipe saldo displit berdasarkan ;'
        tipeSaldo = findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 10).split(';', -1)

        'saldo dari Tipe Saldo di split berdasarkan ;'
        saldoTipeSaldo = findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 11).split(';', -1)

        'loop berdasarkan tipe saldo'
        for (i = 0; i < tipeSaldo.size(); i++) {
            'modify object mencari object berdasarkan id nya tipe saldo tersebut'
            modifyObjectTipeBatasSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/PengaturanTenant/label_TipeBatasSaldo'), 
                'for', 'equals', ('' + (tipeSaldo[i])) + '', true)

            'Mengambil deskripsi dari tipe saldo tersebut'
            descriptionBalanceType = CustomKeywords.'connection.DataVerif.getDescriptionBalanceType'(conneSign, tipeSaldo[
                i])

            'Jika pada loopingan terakhir'
            if (i == (tipeSaldo.size() - 1)) {
                'Jika masih tidak ketemu textnya'
                if (WebUI.getText(modifyObjectTipeBatasSaldo) == null) {
                    'Write excel tidak ketemu dengan tipe saldo tersebut'
                    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (((findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 
                            2) + ';') + GlobalVariable.ReasonFailedNoneUI) + 'mengenai tipe saldo ') + (tipeSaldo[i]))
                }
            }
            
            'Jika teks yang didapat itu sama dengan deskripsi batas saldo'
            if (WebUI.getText(modifyObjectTipeBatasSaldo) == descriptionBalanceType) {
                'modify object untuk input batas saldo'
                modifyObjectInputBatasSaldo = WebUI.modifyObjectProperty(findTestObject('Object Repository/PengaturanTenant/input_BatasSaldo'), 
                    'id', 'equals', ('' + (tipeSaldo[i])) + '', true)

                'Set text di input batas saldo'
                WebUI.setText(modifyObjectInputBatasSaldo, saldoTipeSaldo[i])
            }
        }
        
		'Mengambil email dari db sebelum diedit'
		countEmailBefore = (resultDbPrevious[arrayIndex++]).split(',', -1)
		
		'Mengambil Email dari excel untuk diinput dan displit'
        arrayEmailInput = findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 12).split(',', -1)

        'looping untuk hapus email reminder yang tidak ada di excel'
        for (index = 20; index <= (20 + countEmailBefore.size()); index++) {
            'modify object untuk input email'
            modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Object Repository/PengaturanTenant/input_PenerimaEmailReminderSaldo'), 
                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-tenant-settings/div[2]/div/div/div/div/form/div[' + 
                index) + ']/div/input', true)

            'modify object button Hapus dari yang email pertama'
            modifyObjectButtonHapus = WebUI.modifyObjectProperty(findTestObject('Object Repository/PengaturanTenant/button_HapusPenerimaEmailReminderSaldo'), 
                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-tenant-settings/div[2]/div/div/div/div/form/div[' + 
                index) + ']/div/button', true)

            if (WebUI.verifyElementPresent(modifyObjectInputEmail, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                'looping untuk input email reminder'
                for (indexexcel = 1; indexexcel <= arrayEmailInput.size(); indexexcel++) {
                    'check if email ui = excel'
                    if (WebUI.getAttribute(modifyObjectInputEmail, 'value', FailureHandling.OPTIONAL).equalsIgnoreCase(arrayEmailInput[
                        (indexexcel - 1)])) {
                        break
                    } else {
                        if (indexexcel == arrayEmailInput.size()) {
                            'click tambah email'
                            WebUI.click(modifyObjectButtonHapus)

                            index--
                        }
                    }
                }
            } else {
                break
            }
        }
        
        'looping untuk input email reminder yang tidak ada di ui'
        for (indexexcel = 1; indexexcel <= arrayEmailInput.size(); indexexcel++) {
            'looping untuk delete email reminder'
            for (index = 20; index <= (20 + countEmailBefore.size()); index++) {
                'modify object untuk delete email'
                modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('Object Repository/PengaturanTenant/input_PenerimaEmailReminderSaldo'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-tenant-settings/div[2]/div/div/div/div/form/div[' + 
                    index) + ']/div/input', true)

                if (WebUI.verifyElementNotPresent(modifyObjectInputEmail, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'click tambah email'
                    WebUI.click(findTestObject('PengaturanTenant/button_Tambah'))

                    'input email reminder'
                    WebUI.setText(modifyObjectInputEmail, arrayEmailInput[(indexexcel - 1)])

                    break
                } else if (WebUI.getAttribute(modifyObjectInputEmail, 'value', FailureHandling.OPTIONAL).equalsIgnoreCase(
                    arrayEmailInput[(indexexcel - 1)])) {
                    break
                }
            }
        }

        'Jika stamping otomatisnya yes'
        if (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 13) == 'Yes') {
            'Jika kondisi sekarang masih No'
            if ((resultDbPrevious[6]) == 'No') {
                'Klik button stamping Otomatis'
                WebUI.click(findTestObject('Object Repository/PengaturanTenant/slide_StampingOtomatis'))
            }
        } else if (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 13) == 'No') {
            'Jika stamping otomatisnya no, namun kondisi sekarang Yes'
            if ((resultDbPrevious[6]) == 'Yes') {
                'Klik button stamping Otomatis'
                WebUI.click(findTestObject('Object Repository/PengaturanTenant/slide_StampingOtomatis'))
            }
        }
        
        'Jika Unchange url Activation CallBacknya No'
        if (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 14) == 'No') {
            'activation Callback Url dari excel'
            activationCallBackUrl = findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 15)

            'Input activation Call Back Url'
            WebUI.setText(findTestObject('Object Repository/PengaturanTenant/input_Tambah_activationCallbackUrl'), activationCallBackUrl)
        } else {

            'Jika Unchange url Activation CallBacknya Yes, maka mengambil value dari UI'
            activationCallBackUrl = WebUI.getAttribute(findTestObject('Object Repository/PengaturanTenant/input_Tambah_activationCallbackUrl'), 
                'value')

        }
        
        'Klik button Coba'
        WebUI.click(findTestObject('Object Repository/PengaturanTenant/button_Coba'))

        'check Popup'
        checkPopup()

        'Check error log'
        checkerrorLog()

        'Jika button Simpan tidak bisa diklik'
        if (WebUI.verifyElementHasAttribute(findTestObject('Object Repository/PengaturanTenant/button_Simpan'), 'disabled', 
            GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
            'Jika button simpan tidak ada, maka write excel save gagal'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedSaveGagal)
        } else {
            'Klik button Simpan'
            WebUI.click(findTestObject('Object Repository/PengaturanTenant/button_Simpan'))

            'check pop up'
            checkPopup()
        }
        
		//klik simpan dlu, baru check popup ada atau tidak.
        'Mengambil value excel setelah diedit pengaturan tenant'
        resultDbNew = CustomKeywords.'connection.DataVerif.getPengaturanTenant'(conneSign, findTestData('Login/Login').getValue(
                2, 2).toUpperCase())

		'declare arrayIndex menjadi 0'
        arrayIndex = 0

		'membuat emailDb menjadi sorted berdasarkan asc'
        emailDb = (resultDbNew[arrayIndex++]).split(',').collect({it.trim()}).sort().join(',')

		'membuat email Excel menjadi sorted berdasarkan asc'
        emailExcel = findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 12).split(',').collect(
            {it.trim()}).sort().join(',')

		'verifikasi email'
        arrayMatch.add(WebUI.verifyMatch(emailExcel, emailDb, false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify login'
        arrayMatch.add(WebUI.verifyMatch(findTestData('Login/Login').getValue(2, 2).toUpperCase(), resultDbNew[arrayIndex++], 
                false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify waktu edit'
        arrayMatch.add(WebUI.verifyMatch(currentDate, resultDbNew[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify Label Ref Number'
        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 8), 
                resultDbNew[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify URL upload'
        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 9), 
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
                if ((balances[0]).equals((tipeSaldo[j]).toUpperCase())) {
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
        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 13), 
                resultDbNew[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify aktivasi callback url'
        arrayMatch.add(WebUI.verifyMatch(activationCallBackUrl, resultDbNew[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'Jika storedbnya ada false'
        if (arrayMatch.contains(false)) {
            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                GlobalVariable.ReasonFailedStoredDB)
        }
    }
}

def checkPopup() {
    'Jika popup muncul'
    if (WebUI.verifyElementNotPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    } else {
        'label popup diambil'
        lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

        if (!(lblpopup.contains('Success')) && !(lblpopup.contains('successfully'))) {
            'Tulis di excel sebagai failed dan error.'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + 
                lblpopup)

            return true
        }
        
        'Klik OK untuk popupnya'
        WebUI.click(findTestObject('PengaturanTenant/errorLog_OK'))
    }
    
    return false
}

def checkerrorLog() {
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
		'ambil teks errormessage'
		errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

		if (!(errormessage.contains('URL Upload tersalin')) && !(errormessage.contains('feedback'))) {
			'Tulis di excel itu adalah error'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				(findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') +
				errormessage)

		}
	}

}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
	if (isMatch == false) {
		'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
		GlobalVariable.FlagFailed = 1

		'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			((findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) +
			reason)

		return false
	}
	
	return true
}