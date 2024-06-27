import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testdata.reader.SheetPOI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By


'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathPencarianPengguna).columnNumbers

int checkLogin = 0

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'jika pengguna belum login'
        if ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) != 
        findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Email login'))) || (checkLogin == 
        0)) {
            'panggil fungsi login'
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathPencarianPengguna
                    , ('Email') : 'Email Login', ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], 
                FailureHandling.CONTINUE_ON_FAILURE)

            'click menu pencarian pengguna'
            WebUI.click(findTestObject('PencarianPenggunaAdmin/menu_PencarianPengguna'))

            'click menu pelanggan'
            WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/menu_Karyawan'))

            'call function check paging'
            checkPaging()

            checkLogin = 1
        }
        
        if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
            'Unexecuted')) {
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
		inputDDLExact('PencarianPenggunaAdmin/Karyawan/Select_Status', findTestData(excelPathPencarianPengguna).getValue(
                GlobalVariable.NumofColm, rowExcel('Status Karyawan')))

        'click button cari'
        WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_Cari'))

        'jika hasil pencarian tidak muncul'
        if (WebUI.verifyElementPresent(findTestObject('PencarianDokumen/noDataWarning'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + 'Hasil Pencarian tidak ada')
        } else {
			String userType 
            'resultdb'
            resultDB = CustomKeywords.'connection.PencarianPengguna.getListPencarianPenggunaKaryawan'(conneSign, userType)

            'inisialisasi index'
            arrayIndex = 0

            'get column'
            variableColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry-user > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller > datatable-row-wrapper:nth-child(1) > datatable-body-row > div.datatable-row-center.datatable-row-group.ng-star-inserted datatable-body-cell'))

            'get row'
            variableRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-inquiry-user > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

            'looping row'
            for (loopingRow = 1; loopingRow <= variableRow.size(); loopingRow++) {
                'looping column'
                for (loopingCol = 1; loopingCol <= variableColumn.size(); loopingCol++) {
                    'modify per row dan column.'
                    modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'), 
                        'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-inquiry-user/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
                        loopingRow) + ']/datatable-body-row/div[2]/datatable-body-cell[') + loopingCol) + ']/div', true)

					'jika di column aksi'
                    if (loopingCol == variableColumn.size()) {
                        continue
                    } else {
                        'verify'
                        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), resultDB[arrayIndex++], 
                                false, FailureHandling.CONTINUE_ON_FAILURE), ' pada kolom ke- ' + loopingCol)
                    }
                }
            }
            
            'check if View / reset OTP'
            if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Pencarian Pengguna Action')).equalsIgnoreCase(
                'View')) {
                'click button view'
                WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_View'))

				index = 0
                'PERUBAHAN 4.6'
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
                    ' Status Auto Sign')

                'verify Status'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_Status'), 
                            'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE), 
                    ' Input Status')

                'click button kembali'
                WebUI.click(findTestObject('PencarianPenggunaAdmin/View/button_Kembali'))
            } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Pencarian Pengguna Action')).equalsIgnoreCase(
                'Reset OTP')) {
                'click button reset OTP'
                WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_ResetOTP'))

                'click button Ya Kirim OTP'
                WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_YaKirimOTP'))

                if (WebUI.verifyElementPresent(findTestObject('PencarianPenggunaAdmin/Pengguna/MessagePopUp'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {
                    'click button OK'
                    WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_OK'))

                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                    
                    'get data reset request OTP dari DB'
                    int resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, value)

                    'verify OTP reset menjadi 0'
                    checkVerifyEqualOrMatch(WebUI.verifyMatch(resultResetOTP.toString(), '0', false, FailureHandling.CONTINUE_ON_FAILURE), 
                        ' OTP tidak Kereset')
                } else {
                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' OTP')
                }
            } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Pencarian Pengguna Action')).equalsIgnoreCase(
                'Resend Link')) {
                'click button resend link'
                WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_ResendLink'))

                if (WebUI.verifyElementPresent(findTestObject('PencarianPenggunaAdmin/Pengguna/errorLog'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {
                    'get alert'
                    AlertMsg = WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Pengguna/errorLog'), 'aria-label', 
                        FailureHandling.OPTIONAL)

                    if (AlertMsg.contains('berhasil')) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    } else {
                        'write to excel status failed dan reason'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend) + 
                            ' Link')
                    }
                } else {
                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' Link')
                }
            }
        }
    }
}

def checkPaging() {
	'input search box'
	WebUI.setText(findTestObject('PencarianPenggunaAdmin/Karyawan/input_Email'), findTestData(excelPathPencarianPengguna).getValue(
			GlobalVariable.NumofColm, rowExcel('$Email')))

	'click button cari'
	WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_Cari'))

	'click button reset'
	WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_Reset'))

	'verify search box reset'
	checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Karyawan/input_Email'),
				'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def inputDDLExact(String locationObject, String input) {
	'Input value status'
	WebUI.setText(findTestObject(locationObject), input)

	if (input != '') {
		WebUI.click(findTestObject(locationObject))
	
	'get token unik'
	tokenUnique = WebUI.getAttribute(findTestObject(locationObject), 'aria-owns')
	
	'modify object label Value'
	modifyObjectGetDDLFromToken = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
		'equals', '//*[@id="' + tokenUnique + '"]/div/div[2]', true)
	
	DDLFromToken = WebUI.getText(modifyObjectGetDDLFromToken)
	
	for (i = 0; i < DDLFromToken.split('\n', -1).size(); i++) {
		if (DDLFromToken.split('\n', -1)[i].toString().toLowerCase() == input.toString().toLowerCase()) {
			modifyObjectClicked = WebUI.modifyObjectProperty(findTestObject('DocumentMonitoring/lbl_Value'), 'xpath',
		'equals', '//*[@id="' + tokenUnique + '"]/div/div[2]/div[' + (i + 1) + ']', true)

			WebUI.click(modifyObjectClicked)
			break
		}
	}
	} else {
		WebUI.click(findTestObject(locationObject))
		
		WebUI.sendKeys(findTestObject(locationObject), Keys.chord(Keys.ENTER))
	}
}