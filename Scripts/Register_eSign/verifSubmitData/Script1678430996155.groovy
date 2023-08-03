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
        15))

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
            excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 10).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama')

'verify email'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('InquiryInvitation/tr_Receiver')).toUpperCase(), 
        findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 15).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Email')

'verify phone'
checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('InquiryInvitation/tr_Phone')).toUpperCase(), findTestData(
            excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 14).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Phone')

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

	'set value userLogin'
	GlobalVariable.userLogin = findTestData(excelPathBuatUndangan).getValue(2, 65).toUpperCase()

	'input email'
    WebUI.setText(findTestObject('Login/input_Email'), findTestData(excelPathBuatUndangan).getValue(2, 65))

    'input password'
    WebUI.setText(findTestObject('Login/input_Password'), findTestData(excelPathBuatUndangan).getValue(2, 
            66))

    'click button login'
    WebUI.click(findTestObject('Login/button_Login'), FailureHandling.STOP_ON_FAILURE)

    'input perusahaan'
    WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excelPathBuatUndangan).getValue(2, 
            67))

    WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))

    'input peran'
    WebUI.setText(findTestObject('Login/input_Peran'), findTestData(excelPathBuatUndangan).getValue(2, 
            68))

    WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))

	'click button pilih peran'
	WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
	
	'click menu list undangan'
	WebUI.click(findTestObject('ListUndangan/menu_ListUndangan'))
	
	'set text nama'
	WebUI.setText(findTestObject('ListUndangan/input_Nama'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
			10))
	
	'set text penerima undangan'
	WebUI.setText(findTestObject('ListUndangan/input_PenerimaUndangan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm,
			15))
	
	'set text tanggal pengiriman dari'
	WebUI.setText(findTestObject('ListUndangan/input_TanggalPengirimanDari'), currentDate)
	
	'set text tanggal pengiriman ke'
	WebUI.setText(findTestObject('ListUndangan/input_TanggalPengirimanKe'), currentDate)
	
	'click button cari'
	WebUI.click(findTestObject('ListUndangan/button_Cari'))
	
	'verify nama'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_Nama')), findTestData(excelPathBuatUndangan).getValue(
				GlobalVariable.NumofColm, 10), false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama')
	
	'verify pengiriman melalui'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_PengirimanMelalui')), 'Email',
			false, FailureHandling.CONTINUE_ON_FAILURE), ' Pengiriman Melalui')
	
	'verify penerima undangan'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_PenerimaUndangan')), findTestData(
				excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 15), false, FailureHandling.CONTINUE_ON_FAILURE), ' Penerima Undangan')
	
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
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

        GlobalVariable.FlagFailed = 1
    }
}

