import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathPencarianPengguna).columnNumbers, checkLogin = 0

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		'jika pengguna belum login'
		if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) !=
			findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Email login')) || checkLogin == 0) {
			'panggil fungsi login'
			WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet,
				('Path') : excelPathPencarianPengguna, ('Email') : 'Email Login', ('Password') : 'Password Login',
					('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], FailureHandling.CONTINUE_ON_FAILURE)
			
			'click menu pencarian pengguna'
			WebUI.click(findTestObject('PencarianPenggunaAdmin/menu_PencarianPengguna'))
			
			'click menu pelanggan'
			WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/menu_Karyawan'))
			
			'call function check paging'
			checkPaging()
			
			checkLogin = 1
		}
		
		if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
			GlobalVariable.FlagFailed = 0
		}
		
        'input email'
        WebUI.setText(findTestObject('PencarianPenggunaAdmin/Karyawan/input_Email'), findTestData(excelPathPencarianPengguna).getValue(
                GlobalVariable.NumofColm, rowExcel('$Email')))

        'input nama lengkap'
        WebUI.setText(findTestObject('PencarianPenggunaAdmin/Karyawan/input_NamaLengkap'), findTestData(excelPathPencarianPengguna).getValue(
                GlobalVariable.NumofColm, rowExcel('Nama Lengkap')))

        'input tanggal aktivasi dari'
        WebUI.setText(findTestObject('PencarianPenggunaAdmin/Karyawan/input_TanggalAktivasiDari'), findTestData(excelPathPencarianPengguna).getValue(
                GlobalVariable.NumofColm, rowExcel('Tanggal Aktivasi Dari')))

        'input tanggal aktivasi sampai'
        WebUI.setText(findTestObject('PencarianPenggunaAdmin/Karyawan/input_TanggalAktivasiSampai'), findTestData(excelPathPencarianPengguna).getValue(
                GlobalVariable.NumofColm, rowExcel('Tanggal Aktivasi Sampai')))

        'input status aktivasi'
        WebUI.setText(findTestObject('PencarianPenggunaAdmin/Karyawan/Select_Status'), findTestData(excelPathPencarianPengguna).getValue(
                GlobalVariable.NumofColm, rowExcel('StatusKaryawan')))

        'send keys enter'
        WebUI.sendKeys(findTestObject('PencarianPenggunaAdmin/Karyawan/Select_Status'), Keys.chord(Keys.ENTER))

        'click button cari'
        WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_Cari'))
		
		'jika hasil pencarian tidak muncul'
		if (WebUI.verifyElementPresent(findTestObject('PencarianDokumen/noDataWarning'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
			
			'write to excel status failed dan reason'
			CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
				GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm,
					rowExcel('Reason Failed')).replace('-', '') + ';') + 'Hasil Pencarian tidak ada'))
		} else {
			
			'check if view / reset OTP'
			if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Pencarian Pengguna Action')).equalsIgnoreCase('View')) {
				'click button view'
				WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_View'))
	
				'get data view dari DB'
				ArrayList<String> resultData = CustomKeywords.'connection.PencarianPengguna.getPencarianPengguna'(conneSign,
					findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).toUpperCase())
	
				index = 0
	
				'verify nama'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_Nama'),
							'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE),
					' Nama')
	
				'verify Email'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_Email'),
							'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE),
					' Email')
	
				'parse Date from yyyy-MM-dd > dd-MMM-yyyy'
				sDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(resultData[index++], 'yyyy-MM-dd', 'dd-MMM-yyyy')
	
				'verify tanggal lahir'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_TanggalLahir'),
							'value', FailureHandling.OPTIONAL), sDate, false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Lahir')
	
				'verify Status AutoSign'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_StatusAutoSign'),
							'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE),
					' Status AutoSign')
	
				'verify Status'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_Status'),
							'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE),
					' Status')
	
				'click button kembali'
				WebUI.click(findTestObject('PencarianPenggunaAdmin/View/button_Kembali'))
				
				if (GlobalVariable.FlagFailed == 0) {
					'write to excel success'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
						0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
				}
			} else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Pencarian Pengguna Action')).equalsIgnoreCase('Reset OTP')) {
				'click button reset OTP'
				WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_ResetOTP'))
	
				'click button Ya Kirim OTP'
				WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_YaKirimOTP'))
	
				if (WebUI.verifyElementPresent(findTestObject('PencarianPenggunaAdmin/Karyawan/MessagePopUp'), GlobalVariable.TimeOut,
					FailureHandling.OPTIONAL)) {
					'click button OK'
					WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_OK'))
	
					'write to excel success'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
						0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
	
					'get data reset request OTP dari DB'
					String resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, findTestData(excelPathPencarianPengguna).getValue(
							GlobalVariable.NumofColm, rowExcel('$Email')).toUpperCase())
	
					'verify OTP reset menjadi 0'
					checkVerifyEqualOrMatch(WebUI.verifyMatch(resultResetOTP, '0', false, FailureHandling.CONTINUE_ON_FAILURE),
						' OTP')
				} else {
					'write to excel status failed dan reason'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' OTP')
				}
			}
		}
    }
}

def checkPaging() {

    'input email'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Karyawan/input_Email'), 'AAA@EMAIL.COM')

    'input nama lengkap'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Karyawan/input_NamaLengkap'), 'AAAAAAAAAA')

    'input tanggal aktivasi dari'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Karyawan/input_TanggalAktivasiDari'), '2023-01-01')

    'input tanggal aktivasi sampai'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Karyawan/input_TanggalAktivasiSampai'), '2023-01-01')

    'input status aktivasi'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Karyawan/Select_Status'), 'Active')

    'send keys enter'
    WebUI.sendKeys(findTestObject('PencarianPenggunaAdmin/Karyawan/Select_Status'), Keys.chord(Keys.ENTER))

    'click button reset'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_Reset'))

    'verify input email'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Karyawan/input_Email'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify input nama lengkap'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Karyawan/input_NamaLengkap'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify tanggal dari'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Karyawan/input_TanggalAktivasiDari'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify tanggal sampai'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Karyawan/input_TanggalAktivasiSampai'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify status'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Karyawan/Select_Status'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click button cari'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_Cari'))

    'click page 2'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/page_2'))

    'verify page 2 active'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Karyawan/label_Page'), 
                'ng-reflect-page'), '2', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click page 1'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/page_1'))

    'verify page 1 active'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Karyawan/label_Page'), 
                'ng-reflect-page'), '1', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click next page'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_NextPage'))

    'verify page 2 active'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Karyawan/label_Page'), 
                'ng-reflect-page'), '2', false, FailureHandling.CONTINUE_ON_FAILURE))

    'click previous page'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_PreviousPage'))

    'verify page 1 active'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Karyawan/label_Page'), 
                'ng-reflect-page'), '1', false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + 
            ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}