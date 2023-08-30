import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathPencarianPengguna).columnNumbers

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        GlobalVariable.FlagFailed = 0
		
		String value
		
		if (GlobalVariable.NumofColm == 2) {
			'call test case login admin'
			WebUI.callTestCase(findTestCase('Login/Login_Admin'), [('excel') : excelPathPencarianPengguna, ('sheet') : 'PencarianPengguna-Pelanggan'],
				FailureHandling.CONTINUE_ON_FAILURE)
			
			'call function check paging'
			checkPaging()
		}

		if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Email')) {
            'set text search box dengan email'
            WebUI.setText(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
                    GlobalVariable.NumofColm, 11))
        } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Phone')) {
            'set text search box dengan Phone'
            WebUI.setText(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
                    GlobalVariable.NumofColm, 10))
        } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('Id no')) {
            'set text search box dengan NIK'
            WebUI.setText(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
                    GlobalVariable.NumofColm, 9))
        }
        
        'click button cari'
        WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_Cari'))

        'check if View / reset OTP'
        if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('View')) {
            'click button view'
            WebUI.click(findTestObject('PencarianPenggunaAdmin/Karyawan/button_View'))

            'get data view dari DB'
            ArrayList<String> resultData = CustomKeywords.'connection.PencarianPengguna.getPencarianPengguna'(conneSign, 
                findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 11).toUpperCase())

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
                ' Status Auto Sign')

            'verify Status'
            checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/View/input_Status'), 
                        'value', FailureHandling.OPTIONAL), resultData[index++], false, FailureHandling.CONTINUE_ON_FAILURE), 
                ' Input Status')

            'click button kembali'
            WebUI.click(findTestObject('PencarianPenggunaAdmin/View/button_Kembali'))
        } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Reset OTP')) {
            'click button reset OTP'
            WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_ResetOTP'))

            'click button Ya Kirim OTP'
            WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_YaKirimOTP'))

            if (WebUI.verifyElementPresent(findTestObject('PencarianPenggunaAdmin/Pengguna/MessagePopUp'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
                'click button OK'
                WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_OK'))

                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PencarianPengguna-Pelanggan', 
                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

				if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm,7) == 'Email') {
					value = findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 11).toUpperCase()
				} else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm,7) == 'Phone') {
					value = convertSHA256(findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 10))
				} else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm,7) == 'Id no') {
					value = convertSHA256(findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 9))
				}

                'get data reset request OTP dari DB'
                int resultResetOTP = CustomKeywords.'connection.DataVerif.getResetOTP'(conneSign, value)

                'verify OTP reset menjadi 0'
                checkVerifyEqualOrMatch(WebUI.verifyMatch(resultResetOTP.toString(), '0', false, FailureHandling.CONTINUE_ON_FAILURE), 
                    ' OTP tidak Kereset')
            } else {
                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Pelanggan', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 
                        2).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' OTP')
            }
        } else if (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 6).equalsIgnoreCase('Resend Link')) {
            'click button resend link'
            WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_ResendLink'))

            if (WebUI.verifyElementPresent(findTestObject('PencarianPenggunaAdmin/Pengguna/errorLog'), GlobalVariable.TimeOut, 
                FailureHandling.OPTIONAL)) {
                'get alert'
                AlertMsg = WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Pengguna/errorLog'), 'aria-label', 
                    FailureHandling.OPTIONAL)

                if (AlertMsg.contains('berhasil')) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PencarianPengguna-Pelanggan', 
                        0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                } else {
                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Pelanggan', 
                        GlobalVariable.NumofColm, GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(
                            GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend) + 
                        ' Link')
                }
            } else {
                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Pelanggan', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 
                        2).replace('-', '') + ';') + GlobalVariable.ReasonFailedResend) + ' Link')
            }
        }
        
        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'PencarianPengguna-Pelanggan', 
                0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
        }
    }
}

def checkPaging() {
    'click menu pencarian pengguna'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/menu_PencarianPengguna'))

    'click menu pelanggan'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/menu_Pelanggan'))

    'input search box'
    WebUI.setText(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), findTestData(excelPathPencarianPengguna).getValue(
            GlobalVariable.NumofColm, 11))

    'click button cari'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_Cari'))

    'click button reset'
    WebUI.click(findTestObject('PencarianPenggunaAdmin/Pengguna/button_Reset'))

    'verify search box reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('PencarianPenggunaAdmin/Pengguna/input_SearchBox'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if ((isMatch == false) && (GlobalVariable.FlagFailed == 0)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Pelanggan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, ((findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('PencarianPengguna-Pelanggan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathPencarianPengguna).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}
def convertSHA256(String input) {
	return CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(input)
}