import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.sql.Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'get current date'
currentDate = new Date().format('yyyy-MM-dd')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'sheet untuk dicetak'
sheet = 'PengaturanTenant'

int firstRun = 0

'looping berdasarkan Number of column'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathFEPengaturanTenant).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		 GlobalVariable.FlagFailed = 0
		 
		 if(findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) !=
			 findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Email Login')) || firstRun == 0) {
			 'panggil fungsi login'
			 WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet,
				  ('Path') : excelPathFEPengaturanTenant, ('Email') : 'Email Login', ('Password') : 'Password Login'
				 , ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], FailureHandling.STOP_ON_FAILURE)
			 
			 firstRun = 1
		 }

		'declare result Db setelah edit, result Db untuk sebelum edit, arraylist untuk balance, declare array split dari result di db, array tipe saldo sebelumnya, array saldo dari tipe saldo sebelumnya, dan arrayMatch'
        ArrayList resultDbPrevious = [], arrSplitResultDb = [], arrTipeSaldoBefore = [], arrSaldoTipeSaldoBefore = []

        'declare variable inisialisasi for'
        int i, j

        'inisalisasi arrayIndex yaitu 0'
        arrayIndex = 0

        'declare variable string'
        String activationCallBackUrl, descriptionBalanceType, use_wa

		WebUI.refresh()
		
        'Klik menu pengaturan tenant'
        WebUI.click(findTestObject('PengaturanTenant/menu_PengaturanTenant'))

        'Delay 5 karena batas Saldonya loading lumayan lama.'
        WebUI.delay(5)

        'Verifikasi sudah pindah page'
        WebUI.verifyElementPresent(findTestObject('PengaturanTenant/label_PengaturanTenant'), GlobalVariable.TimeOut, 
            FailureHandling.CONTINUE_ON_FAILURE)

        'Page Down'
        WebUI.sendKeys(findTestObject('PengaturanTenant/input_LabelRefNumber'), Keys.chord(Keys.PAGE_DOWN), 
            FailureHandling.OPTIONAL)

        'Mengambil hasil db untuk sebelum diedit untuk mendapatkan total emailnya ada berapa'
        resultDbPrevious = CustomKeywords.'connection.PengaturanTenant.getPengaturanTenant'(conneSign, findTestData(excelPathFEPengaturanTenant).getValue(
                GlobalVariable.NumofColm, rowExcel('Email Login')).toUpperCase())

        'diskip 2 karena sekaligus pengecekan after. 2 yang diskip mengenai diupdate oleh siapa dan tanggal updatenya'
        arrayIndex = (arrayIndex + 2)

        'Mengambil email dari db sebelum diedit'
        countEmailBefore = (resultDbPrevious[arrayIndex++]).split(',', -1)

        'looping untuk check email'
        for (index = 25; index < (25 + countEmailBefore.size()); index++) {
            'modify object untuk input email'
            modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('PengaturanTenant/input_PenerimaEmailReminderSaldo'), 
                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-tenant-settings/div[2]/div/div/div/div/form/div[' + 
                index) + ']/div/input', true)

			'Scroll ke email tersebut'
            WebUI.scrollToElement(modifyObjectInputEmail, GlobalVariable.TimeOut)
			
			'Kasih delay 2 detik'
            WebUI.delay(2)
			
			'verifikasi email'
            checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(modifyObjectInputEmail, 'ng-reflect-model'), countEmailBefore[
            (index - 25)], false, FailureHandling.OPTIONAL), ((' dengan alasan tidak cocok antara ' + WebUI.getAttribute(
            modifyObjectInputEmail, 'ng-reflect-model')) + ' dan ') + (countEmailBefore[(index - 25)]))
        }
        
        'verifikasi label ref number'
        checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PengaturanTenant/input_LabelRefNumber'), 
                    'value'), resultDbPrevious[arrayIndex], false, FailureHandling.OPTIONAL), ((' dengan alasan tidak cocok antara ' + 
            WebUI.getAttribute(findTestObject('PengaturanTenant/input_LabelRefNumber'), 'value')) + ' dan ') + 
            (resultDbPrevious[arrayIndex]))
		
		'array index naik 1'
        arrayIndex++

        'verifikasi url upload'
        checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PengaturanTenant/input_URLUpload'), 
        'value'), resultDbPrevious[arrayIndex], false, FailureHandling.OPTIONAL), ((' dengan alasan tidak cocok antara ' + 
        WebUI.getAttribute(findTestObject('PengaturanTenant/input_URLUpload'), 'value')) + ' dan ') + 
        (resultDbPrevious[arrayIndex]))

		'array index naik 1'
        arrayIndex++

        'verifikasi saldo. Untuk split treshold balance dan replace beberapa yang tidak dibutuhkan'
        (resultDbPrevious[arrayIndex]) = (resultDbPrevious[arrayIndex]).replace('{', '').replace('}', '').replace('"', '').split(
            ',', -1)

		'Looping untuk memasukkan hasil db ke array2 yang telah disiapkan'
        for (i = 0; i < (resultDbPrevious[arrayIndex]).size(); i++) {
			'result db tersebut displit lagi untuk dapat tipe dan saldonyat'
            (arrSplitResultDb[i]) = ((resultDbPrevious[arrayIndex])[i]).split(':', -1)
			
			'Memasukkan tipe saldo ke array'
            arrTipeSaldoBefore.add((arrSplitResultDb[i])[0])
			
			'Memasukkan saldo tipe saldo ke array'
            arrSaldoTipeSaldoBefore.add((arrSplitResultDb[i])[1])
        }
        
		WebUI.delay(5)
        'loop berdasarkan tipe saldo'
        for (i = 0; i < arrTipeSaldoBefore.size(); i++) {
            'modify object mencari object berdasarkan id nya tipe saldo tersebut'
            modifyObjectTipeBatasSaldo = WebUI.modifyObjectProperty(findTestObject('PengaturanTenant/label_TipeBatasSaldo'), 
                'for', 'equals', arrTipeSaldoBefore[i], true)

            'Mengambil deskripsi dari tipe saldo tersebut'
            descriptionBalanceType = CustomKeywords.'connection.PengaturanTenant.getDescriptionBalanceType'(conneSign, arrTipeSaldoBefore[
                i])
			
            'Jika masih tidak ketemu textnya'
            if (WebUI.getText(modifyObjectTipeBatasSaldo) == null) {
                'Write excel tidak ketemu dengan tipe saldo tersebut'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedNoneUI) + 
                    'mengenai tipe saldo ') + '<' + (arrTipeSaldoBefore[i]) + '>')
            } else if (WebUI.getText(modifyObjectTipeBatasSaldo) == descriptionBalanceType) {
                'Jika tipe batas saldonya sesuai dengan deskripsi dari balance, maka modify object untuk input batas saldo'
                modifyObjectInputBatasSaldo = WebUI.modifyObjectProperty(findTestObject('PengaturanTenant/input_BatasSaldo'), 
                    'id', 'equals', arrTipeSaldoBefore[i], true)
				
				'verifikasi tipe batas saldo'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTipeBatasSaldo), descriptionBalanceType, 
                        false, FailureHandling.OPTIONAL), ((' dengan alasan tidak cocok antara ' + WebUI.getText(modifyObjectTipeBatasSaldo)) + 
                    ' dan ') + descriptionBalanceType)
				
				'verifikasi saldo tipe batas saldo'
                checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(modifyObjectInputBatasSaldo, 'value'), arrSaldoTipeSaldoBefore[
                        i], false, FailureHandling.OPTIONAL), ((' dengan alasan tidak cocok antara ' + WebUI.getAttribute(
                        modifyObjectInputBatasSaldo, 'value')) + ' dan ') + (arrTipeSaldoBefore[i]))
            }
        }
		
		'Set text untuk Label Ref Number'
		setTextEmptyValidation(findTestObject('PengaturanTenant/input_LabelRefNumber'), '$Label Ref Number')
		
        'Set text untuk input URL Upload'
		setTextEmptyValidation(findTestObject('PengaturanTenant/input_URLUpload'), 'URL Upload')

        'Klik Copy'
        WebUI.click(findTestObject('PengaturanTenant/button_Copy'))

        'Check error log'
        checkerrorLog()

        'tipe saldo displit berdasarkan ;'
        tipeSaldo = findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Tipe Batas Saldo')).split(';', -1)

        'saldo dari Tipe Saldo di split berdasarkan ;'
        saldoTipeSaldo = findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Batas Saldo')).split(';', -1)

        'loop berdasarkan tipe saldo'
        for (i = 0; i < tipeSaldo.size(); i++) {
            'modify object mencari object berdasarkan id nya tipe saldo tersebut'
            modifyObjectTipeBatasSaldo = WebUI.modifyObjectProperty(findTestObject('PengaturanTenant/label_TipeBatasSaldo'), 
                'for', 'equals', tipeSaldo[i], true)

            'Mengambil deskripsi dari tipe saldo tersebut'
            descriptionBalanceType = CustomKeywords.'connection.PengaturanTenant.getDescriptionBalanceType'(conneSign, tipeSaldo[
                i])

            'Jika pada loopingan terakhir'
            if (i == (tipeSaldo.size() - 1)) {
                'Jika masih tidak ketemu textnya'
                if (WebUI.getText(modifyObjectTipeBatasSaldo) == null) {
                    'Write excel tidak ketemu dengan tipe saldo tersebut'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (((findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedNoneUI) + 'mengenai tipe saldo ') + '<' + (tipeSaldo[i]) + '>')
                }
            }
            
            'Jika teks yang didapat itu sama dengan deskripsi batas saldo'
            if (WebUI.getText(modifyObjectTipeBatasSaldo) == descriptionBalanceType) {
                'modify object untuk input batas saldo'
                modifyObjectInputBatasSaldo = WebUI.modifyObjectProperty(findTestObject('PengaturanTenant/input_BatasSaldo'), 
                    'id', 'equals', tipeSaldo[i], true)

                'Set text di input batas saldo'
                WebUI.setText(modifyObjectInputBatasSaldo, saldoTipeSaldo[i])
            }
        }
        
        'Mengambil Email dari excel untuk diinput dan displit'
        arrayEmailInput = findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Email Reminder Saldo')).split(',', -1)

        'looping untuk hapus email reminder yang tidak ada di excel'
        for (index = 25; index <= (25 + countEmailBefore.size()); index++) {
            'modify object untuk input email'
            modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('PengaturanTenant/input_PenerimaEmailReminderSaldo'), 
                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-tenant-settings/div[2]/div/div/div/div/form/div[' + 
                index) + ']/div/input', true)

            'modify object button Hapus dari yang email pertama'
            modifyObjectButtonHapus = WebUI.modifyObjectProperty(findTestObject('PengaturanTenant/button_HapusPenerimaEmailReminderSaldo'), 
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
            for (index = 25; index <= (25 + countEmailBefore.size()); index++) {
                'modify object untuk delete email'
                modifyObjectInputEmail = WebUI.modifyObjectProperty(findTestObject('PengaturanTenant/input_PenerimaEmailReminderSaldo'), 
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
        if (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('isStampingOtomatis')) == 'Yes') {
            'Jika kondisi sekarang masih No'
            if ((resultDbPrevious[6]) == 'No') {
                'Klik button stamping Otomatis'
                WebUI.click(findTestObject('PengaturanTenant/slide_StampingOtomatis'))
            }
        } else if (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('isStampingOtomatis')) == 'No') {
            'Jika stamping otomatisnya no, namun kondisi sekarang Yes'
            if ((resultDbPrevious[6]) == 'Yes') {
                'Klik button stamping Otomatis'
                WebUI.click(findTestObject('PengaturanTenant/slide_StampingOtomatis'))
            }
        }
        
        'Jika Unchange url Activation CallBacknya No'
        if (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Unchange urlActivationCallback')) == 'No') {
            'activation Callback Url dari excel'
            activationCallBackUrl = findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('urlActivationCallback'))

            'Input activation Call Back Url'
			setTextEmptyValidation(findTestObject('PengaturanTenant/input_Tambah_activationCallbackUrl'), 'urlActivationCallback')
        } else {
            'Jika Unchange url Activation CallBacknya Yes, maka mengambil value dari UI'
            activationCallBackUrl = WebUI.getAttribute(findTestObject('PengaturanTenant/input_Tambah_activationCallbackUrl'), 
                'value')
        }
		
		'input pilihan untuk kirim notifikasi tanpa email'
		if (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Metode Pengiriman Notifikasi'))
			== 'SMS') {
			
			'klik pada radio button SMS'
			WebUI.click(findTestObject('PengaturanTenant/Radiobtn_useNotifSMS'))
			
			'value untuk dibanding dengan DB'
			use_wa = '0'
			
		} else if (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Metode Pengiriman Notifikasi'))
			== 'Whatsapp') {
			
			'klik pada radio button WhatsApp'
			WebUI.click(findTestObject('PengaturanTenant/Radiobtn_useNotifWA'))
			
			'value untuk dibanding dengan DB'
			use_wa = '1'
		}
		
		'input url callback'
		setTextEmptyValidation(findTestObject('PengaturanTenant/input_URL callback'), 'URL Callback')
		
		'input url redirect aktivasi'
		setTextEmptyValidation(findTestObject('PengaturanTenant/input_URLAktivasiRedirect'), 'Url Redirect Aktivasi')

		'input url redirect TTD'
		setTextEmptyValidation(findTestObject('PengaturanTenant/input_URLRedirectTTD'), 'Url Redirect Tanda tangan')
		
		'klik pada form nya untuk update value'
		WebUI.click(findTestObject('PengaturanTenant/FormPengaturanTenant'))
		
        'Klik button Simpan'
        WebUI.click(findTestObject('PengaturanTenant/button_Simpan'))
		
		'Check error log'
		if (checkerrorLog() == true) {
			continue
		}

		'check pop up'
		if (checkPopup() == true) {
			'Jika button simpan tidak ada, maka write excel save gagal'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
				(findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedSaveGagal)
		}
		
		'Klik button Coba'
		WebUI.click(findTestObject('PengaturanTenant/button_Coba'))

		'check Popup'
		checkPopup()

		'Check error log'
		if (checkerrorLog() == true) {
			continue
		}
		
		'Klik button Coba'
		WebUI.click(findTestObject('PengaturanTenant/button_CobaURLCallback'))

		'check Popup'
		checkPopup()

		'Check error log'
		if (checkerrorLog() == true) {
			continue
		}
		
		WebUI.delay(GlobalVariable.TimeOut)
		
		if(GlobalVariable.checkStoreDB.equals('Yes')) {
			
			'panggil fungsi check store DB'
			WebUI.callTestCase(findTestCase('PengaturanTenant/PengaturanTenantStoreDB'), [('sheet') : sheet,
				 ('excelPathFEPengaturanTenant') : excelPathFEPengaturanTenant, ('conneSign') : conneSign, ('currentDate') : currentDate
				, ('tipeSaldo') : tipeSaldo, ('saldoTipeSaldo') : saldoTipeSaldo, ('activationCallBackUrl') : activationCallBackUrl,
					('use_wa') : use_wa], FailureHandling.CONTINUE_ON_FAILURE)
		}
        
		if (GlobalVariable.FlagFailed == 0) {
			'write to excel success'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm -
				1, GlobalVariable.StatusSuccess)
		}
    }
}

def checkPopup() {
    'Jika popup muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/lbl_popup'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'label popup diambil'
        lblpopup = WebUI.getText(findTestObject('KotakMasuk/Sign/lbl_popup'), FailureHandling.CONTINUE_ON_FAILURE)

        if (!(lblpopup.contains('Success')) && !(lblpopup.contains('successfully')) && ! (lblpopup.contains('Pengaturan Tenant telah berhasil disimpan'))) {
            'Tulis di excel sebagai failed dan error.'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + 
                '<' + lblpopup + '>')

			
			'Klik OK untuk popupnya'
			WebUI.click(findTestObject('PengaturanTenant/errorLog_OK'))
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
                (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + 
                '<' + errormessage + '>')
			
			return true
        }
    }
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        GlobalVariable.FlagFailed = 1

        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        return false
    }
    
    return true
}

def setTextEmptyValidation(TestObject object, String Testdata) {
	
	'jika testdata kosong'
	if (findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel(Testdata)).equalsIgnoreCase('')) {
		
		'select all text di field tersebut'
		WebUI.sendKeys(object, Keys.chord(Keys.CONTROL + 'a'))
		
		'hapus text tersebut'
		WebUI.sendKeys(object, Keys.chord(Keys.BACK_SPACE))
		
		'input text kosong'
		WebUI.setText(object, '')
	} else {
		
		'input text sesuai testdata'
		WebUI.setText(object, findTestData(excelPathFEPengaturanTenant).getValue(GlobalVariable.NumofColm, rowExcel(Testdata)))
	}
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}