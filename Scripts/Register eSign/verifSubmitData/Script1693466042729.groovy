import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys

'get current date'
currentDate = new Date().format('dd-MMM-yyyy')

'click menu Inquiry Invitation'
WebUI.click(findTestObject('InquiryInvitation/menu_InquiryInvitation'))

'input email'
WebUI.setText(findTestObject('InquiryInvitation/input_SearchBox'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        rowExcel('Email')))

'Looping delay untuk handling search data selama +- 2 menit'
for (int i = 1; i <= 8; i++) {
    'click button cari'
    WebUI.click(findTestObject('InquiryInvitation/button_Cari'))

    'Pengecekan ada/tidak adanya data yang muncul'
    if (WebUI.verifyElementPresent(findTestObject('InquiryInvitation/tr_Name'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
        break
    } else {
        'delay 10 detik'
        WebUI.delay(10)
    }
}

'verify nama'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('InquiryInvitation/tr_Name')).toUpperCase(), findTestData(
            excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('$Nama')).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama')

'verify email'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('InquiryInvitation/tr_Receiver')).toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email')).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Email')

'verify phone'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('InquiryInvitation/tr_Phone')).toUpperCase(), findTestData(
            excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('$No Handphone')).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Phone')

'split date and time'
invitationDate = WebUI.getText(findTestObject('InquiryInvitation/tr_InvitationDate')).split(' ', -1)

'verify invitation date'
checkVerifyEqualOrMatch(WebUI.verifyMatch(invitationDate[0] , currentDate, false, FailureHandling.CONTINUE_ON_FAILURE), ' Invitation Date')

verifyListUndangan()

def verifyListUndangan() {
	currentDate = new Date().format('yyyy-MM-dd')
	
	'navigate to url esign'
	WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 5))

	'maximize window'
	WebUI.maximizeWindow()

	'store user login'	
	GlobalVariable.userLogin = findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email Login')).toUpperCase()
	
	'input email'
	WebUI.setText(findTestObject('Login/input_Email'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email Login')))
	
	'store GV user login'
	GlobalVariable.userLogin = findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))
	
	'input password'
	WebUI.setText(findTestObject('Login/input_Password'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Password Login')))
	
	'click button login'
	WebUI.click(findTestObject('Login/button_Login'))
	
	if(WebUI.verifyElementPresent(findTestObject('Login/input_Perusahaan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {	
		'input perusahaan'
		WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Perusahaan Login')))
		
		'enter untuk input perusahaan'
		WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))
		
		'input peran'
		WebUI.setText(findTestObject('Login/input_Peran'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Peran Login')))
		
		'enter untuk input peran'
		WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))
		
		'click button pilih peran'
		WebUI.click(findTestObject('Login/button_pilihPeran'))
	}
	
	'click menu list undangan'
	WebUI.click(findTestObject('ListUndangan/menu_ListUndangan'))
	
	'set text nama'
	WebUI.setText(findTestObject('ListUndangan/input_Nama'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
			rowExcel('$Nama')))
	
	'set text penerima undangan'
	WebUI.setText(findTestObject('ListUndangan/input_PenerimaUndangan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
			rowExcel('Email')))
	
	'set text tanggal pengiriman dari'
	WebUI.setText(findTestObject('ListUndangan/input_TanggalPengirimanDari'), currentDate)
	
	'set text tanggal pengiriman ke'
	WebUI.setText(findTestObject('ListUndangan/input_TanggalPengirimanKe'), currentDate)
	
	'click button cari'
	WebUI.click(findTestObject('ListUndangan/button_Cari'))
	
	'verify nama'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_Nama')), findTestData(excelPathBuatUndangan).getValue(
				GlobalVariable.NumofColm, rowExcel('$Nama')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama')
	
	'verify pengiriman melalui'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_PengirimanMelalui')), 'Email',
			false, FailureHandling.CONTINUE_ON_FAILURE), ' Pengiriman Melalui')
	
	'verify penerima undangan'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_PenerimaUndangan')), findTestData(
				excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Email')), false, FailureHandling.CONTINUE_ON_FAILURE), ' Penerima Undangan')
	
	tanggalPengiriman = WebUI.getText(findTestObject('ListUndangan/table_TanggalPengiriman')).split(' ', -1)
	
	parsedDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(tanggalPengiriman[0], 'dd-MMM-yyyy', 'yyyy-MM-dd')
	
	'verify tanggal pengiriman'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(parsedDate, currentDate, false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Pengiriman')
	
	'verify tanggal registrasi'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_TanggalRegistrasi')), '-', false,
			FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Registrasi')
	
	'verify status registrasi'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_StatusRegistrasi')), 'NOT DONE',
			false, FailureHandling.CONTINUE_ON_FAILURE), ' Status Registrasi')
	
	'verify Status undangan'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_StatusUndangan')), 'AKTIF', false,
			FailureHandling.CONTINUE_ON_FAILURE), ' Status Undangan')
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, SheetName, cellValue)
}