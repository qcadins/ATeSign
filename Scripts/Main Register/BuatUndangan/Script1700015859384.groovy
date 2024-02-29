import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

'check if login sama || perlu melakukan login lagi'
if (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inveditor Login')) != findTestData(excelPathRegister).getValue(
    GlobalVariable.NumofColm, rowExcel('Inveditor Login'))) || (GlobalVariable.LoginAgain == 0)) || WebUI.verifyElementNotPresent(
    findTestObject('RegisterEsign/menu_BuatUndangan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'call test case login per case'
    WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathRegister, ('Email') : 'Inveditor Login'
            , ('Password') : 'Inveditor Password Login', ('Perusahaan') : 'Inveditor Perusahaan Login', ('Peran') : 'Inveditor Peran Login'], 
        FailureHandling.STOP_ON_FAILURE)
}

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

GlobalVariable.VerificationCount = 1

GlobalVariable.FlagFailed = 0

'call function input > cancel > verify form kosong'
inputCancel()

'input form buat undangan'
inputBuatUndangan()

'click button save'
WebUI.click(findTestObject('RegisterEsign/button_Save'))

if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/button_YaProses'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'click button ya proses'
    WebUI.click(findTestObject('RegisterEsign/button_YaProses'))
}

'declare isMmandatory Complete'
int isMandatoryComplete = Integer.parseInt(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Is Mandatory Complete')))

'cek if muncul popup'
if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/label_ValidationError'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get reason'
    ReasonFailed = WebUI.getText(findTestObject('RegisterEsign/label_ReasonError'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
        ';') + '<') + ReasonFailed) + '>')

    'click button tutup error'
    WebUI.click(findTestObject('RegisterEsign/button_TutupError'))

    'click button cancel'
    WebUI.click(findTestObject('RegisterEsign/button_Cancel'))

    'click button ya batal undangan'
    WebUI.click(findTestObject('RegisterEsign/button_YaBatalUndangan'))

    GlobalVariable.FlagFailed = 1

    GlobalVariable.LoginAgain = 1
} else if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'call function get error log'
    getErrorLog()

    GlobalVariable.LoginAgain = 1
} else if (WebUI.getAttribute(findTestObject('RegisterEsign/PopUp/input_Link'), 'value', FailureHandling.OPTIONAL) == 'undefined') {
    GlobalVariable.ErrorType = 'ERROR'

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
        ';') + GlobalVariable.ErrorType)

    'click tutup popup'
    WebUI.click(findTestObject('RegisterEsign/button_TutupDapatLink'))

    GlobalVariable.FlagFailed = 1

    'call test case error report'
    WebUI.callTestCase(findTestCase('Main Register/ErrorReport'), [('excelPathRegister') : excelPathRegister], FailureHandling.CONTINUE_ON_FAILURE)

    GlobalVariable.LoginAgain = 1
} else if (isMandatoryComplete > 0) {
    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
        ';') + GlobalVariable.ReasonFailedMandatory)

    GlobalVariable.LoginAgain = 1
} else {
    'get link'
    GlobalVariable.Link = WebUI.getAttribute(findTestObject('RegisterEsign/PopUp/input_Link'), 'value')

    'write to excel Link buat undangan'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Link Invitation') - 
        1, GlobalVariable.NumofColm - 1, GlobalVariable.Link)

    'HIT API Login untuk token : invenditor@womf'
    responLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Inveditor Login')), ('password') : findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('Inveditor Password Login'))]))

    'Jika status HIT API 200 OK'
    if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
        'Parsing token menjadi GlobalVariable'
        GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')

        String receiverDetail

        'check if email kosong atau tidak'
        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() > 2) {
            receiverDetail = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email'))
        } else {
            receiverDetail = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No Telepon'))
        }
        
        'HIT API get Invitation Link'
        responGetInvLink = WS.sendRequest(findTestObject('Postman/Get Inv Link', [('callerId') : findTestData(excelPathRegister).getValue(
                        GlobalVariable.NumofColm, rowExcel('callerId')), ('receiverDetail') : receiverDetail]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responGetInvLink, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            status_Code = WS.getElementPropertyValue(responGetInvLink, 'status.code')

            'Jika status codenya 0'
            if (status_Code == 0) {
                'Get invitation Link'
                InvitationLink = WS.getElementPropertyValue(responGetInvLink, 'invitationLink')

                if (!(WebUI.verifyMatch(GlobalVariable.Link, InvitationLink, false, FailureHandling.CONTINUE_ON_FAILURE))) {
                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
                        ' FE Buat Undangan Gen Link dan API Get Inv Link')

                    GlobalVariable.FlagFailed = 1
                }
            } else if ((status_Code == 5512) || (WS.getElementPropertyValue(responGetInvLink, 'status.message', FailureHandling.OPTIONAL) == 
            'Tidak bisa mengakses user milik tenant lain')) {
                'cek ke excel bahwa data user sudah diregist otomatis ke tenant lain'
                result = CustomKeywords.'connection.Registrasi.checkAddUserOtherTenant'(conneSign, findTestData(excelPathRegister).getValue(
                        GlobalVariable.NumofColm, rowExcel('No Telepon')).replace('"', ''))

                if (result == 0) {
                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + 'Error User di Tenant baru tidak berhasil ditambahkan')

                    GlobalVariable.FlagFailed = 1
                } else if (result > 1) {
                    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')) + ';') + 'Terdapat 2 user yang sama di tenant ') + GlobalVariable.Tenant)

                    GlobalVariable.FlagFailed = 1
                } else {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)

                    GlobalVariable.FlagFailed = 1
                }
            } else {
                messageFailed = WS.getElementPropertyValue(responGetInvLink, 'status.message', FailureHandling.OPTIONAL).toString()

                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                        '-', '') + ';') + '<') + messageFailed) + '>')

                GlobalVariable.FlagFailed = 1
            }
        } else {
            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
                    '') + ';') + GlobalVariable.ReasonFailedHitAPI)

            GlobalVariable.FlagFailed = 1
        }
    } else {
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
                '') + ';') + GlobalVariable.ReasonFailedHitAPI)

        GlobalVariable.FlagFailed = 1
    }
    
    'click tutup popup'
    WebUI.click(findTestObject('RegisterEsign/button_TutupDapatLink'))

    if (GlobalVariable.FlagFailed == 0) {
        'write to excel success'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
            1, GlobalVariable.StatusSuccess)
    }
    
//    'check ada value maka setting Link Is Active'
//    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting is_active Link')).length() > 
//    0) {
//        'check if email kosong atau tidak'
//        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() > 0) {
//            'setting Link Is Active'
//            CustomKeywords.'connection.APIFullService.settingLinkIsActive'(conneSign, findTestData(excelPathRegister).getValue(
//                    GlobalVariable.NumofColm, rowExcel('Setting is_active Link')), findTestData(excelPathRegister).getValue(
//                    GlobalVariable.NumofColm, rowExcel('$Email')))
//        } else {
//            'setting Link Is Active'
//            CustomKeywords.'connection.APIFullService.settingLinkIsActive'(conneSign, findTestData(excelPathRegister).getValue(
//                    GlobalVariable.NumofColm, rowExcel('Setting is_active Link')), findTestData(excelPathRegister).getValue(
//                    GlobalVariable.NumofColm, rowExcel('No Telepon')))
//        }
//        
//        inputBuatUndangan()
//
//        'click button save'
//        WebUI.click(findTestObject('RegisterEsign/button_Save'))
//
//        if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/button_YaProses'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
//            'click button ya proses'
//            WebUI.click(findTestObject('RegisterEsign/button_YaProses'))
//        }
//        
//        if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
//            'call function get error log'
//            getErrorLog()
//        } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting is_active Link')) == 
//        '0') {
//            'write to excel failed'
//            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
//                (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
//                    '') + ';') + ' Link tergenerate walupun sudah tidak active')
//
//            GlobalVariable.FlagFailed = 1
//        } else {
//            'click tutup popup'
//            WebUI.click(findTestObject('RegisterEsign/button_TutupDapatLink'))
//        }
//    }
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def inputBuatUndangan() {
    'click menu buat undangan'
    WebUI.click(findTestObject('RegisterEsign/menu_BuatUndangan'))

    'input NIK'
    WebUI.setText(findTestObject('RegisterEsign/input_NIK'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('$NIK')))

    'input nama lengkap'
    WebUI.setText(findTestObject('RegisterEsign/input_NamaLengkap'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Nama')))

    'input tempat lahir'
    WebUI.setText(findTestObject('RegisterEsign/input_TempatLahir'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Tempat Lahir')))

    'input tanggal lahir'
    WebUI.setText(findTestObject('RegisterEsign/input_TanggalLahir'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Tanggal Lahir')))

    'cek if pria(M) / wanita(F)'
    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Jenis Kelamin')).equalsIgnoreCase('M')) {
        'click radio pria'
        WebUI.click(findTestObject('RegisterEsign/radio_Pria'))
    } else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Jenis Kelamin')).equalsIgnoreCase(
        'F')) {
        'click radio wanita'
        WebUI.click(findTestObject('RegisterEsign/radio_Wanita'))
    }
    
    'input no handphone'
    WebUI.setText(findTestObject('RegisterEsign/input_noHandphone'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('No Telepon')))

    'input email'
    WebUI.setText(findTestObject('RegisterEsign/input_Email'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Email')))

    'input alamat lengkap'
    WebUI.setText(findTestObject('RegisterEsign/input_AlamatLengkap'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Alamat')))

    'input provinsi'
    WebUI.setText(findTestObject('RegisterEsign/input_Provinsi'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Provinsi')))

    'input kota'
    WebUI.setText(findTestObject('RegisterEsign/input_Kota'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Kota')))

    'input kecamatan'
    WebUI.setText(findTestObject('RegisterEsign/input_Kecamatan'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Kecamatan')))

    'input kelurahan'
    WebUI.setText(findTestObject('RegisterEsign/input_Kelurahan'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Kelurahan')))

    'input kode pos'
    WebUI.setText(findTestObject('RegisterEsign/input_KodePos'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Kode Pos')))

    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Wilayah')).length() > 0) {
        'input wilayah'
		inputDDLExact('RegisterEsign/input_Wilayah', findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
                rowExcel('Wilayah')))
    }
    
    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('OfficeName')).length() > 0) {
        'input office'
		inputDDLExact('RegisterEsign/input_Office', findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm,
			rowExcel('OfficeName')))
    }
    
    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('BusinessLineName')).length() > 0) {
        'input lini bisnis'
		inputDDLExact('RegisterEsign/input_LiniBisnis', findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm,
			rowExcel('BusinessLineName')))
     }
    
    'input task no'
    WebUI.setText(findTestObject('RegisterEsign/input_TaskNo'), findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Ref No')))
}

def inputCancel() {
    if (GlobalVariable.NumofColm == 2) {
        'input form buat undangan'
        inputBuatUndangan()

        'click button cancel'
        WebUI.click(findTestObject('RegisterEsign/button_Cancel'))

        'click button ya batal undangan'
        WebUI.click(findTestObject('RegisterEsign/button_YaBatalUndangan'))

        'check if sudah ter cancel dan pindah page ke home'
        if (WebUI.verifyElementPresent(findTestObject('RegisterEsign/input_Kecamatan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            checkVerifyEqualOrMatch(false, 'FAILED TO CANCEL')
        }
        
        'click menu buat undangan'
        WebUI.click(findTestObject('RegisterEsign/menu_BuatUndangan'))

        'verify field NIK kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_NIK'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field NIK tidak kosong')

        'verify field Nama Lengkap kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_NamaLengkap'), 
                    'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Nama Lengkap tidak kosong')

        'verify field tempat lahir kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_TempatLahir'), 
                    'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Tempat Lahir tidak kosong')

        'verify field tanggal lahir kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_TanggalLahir'), 
                    'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field tanggal Lahir tidak kosong')

        'verify field no handphone kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_noHandphone'), 
                    'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field no Handphone tidak kosong')

        'verify field email kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Email'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Email tidak kosong')

        'verify field alamat Lengkap kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_AlamatLengkap'), 
                    'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field alamat lengkap tidak kosong')

        'verify field Provinsi kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Provinsi'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Provinsi tidak kosong')

        'verify field Kota kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Kota'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Kota tidak kosong')

        'verify field kecamatan kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Kecamatan'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Kecamatan tidak kosong')

        'verify field kelurahan kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Kelurahan'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field kelurahan tidak kosong')

        'verify field kode pos kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_KodePos'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field kode pos tidak kosong')

        'verify field wilayah kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Wilayah'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field wilayah tidak kosong')

        'verify field office kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_Office'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field office tidak kosong')

        'verify field lini bisnis kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_LiniBisnis'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field Lini Bisnis tidak kosong')

        'verify field task no kosong'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('RegisterEsign/input_TaskNo'), 'value', 
                    FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' field task no tidak kosong')

        'click button cancel'
        WebUI.click(findTestObject('RegisterEsign/button_Cancel'))

        'click button ya batal undangan'
        WebUI.click(findTestObject('RegisterEsign/button_YaBatalUndangan'))
    }
}

def getErrorLog() {
    'get reason'
    ReasonFailed = WebUI.getAttribute(findTestObject('RegisterEsign/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
        ';') + '<') + ReasonFailed) + '>')

    if ((ReasonFailed.contains('sudah digunakan di link') || ReasonFailed.contains('sudah terdaftar')) || ReasonFailed.contains(
        'Tidak bisa generate ulang invitation link')) {
        'declare error type error'
        GlobalVariable.ErrorType = 'ERROR'
    } else {
        'declare error type reject'
        GlobalVariable.ErrorType = 'REJECT'
    }
    
    GlobalVariable.FlagFailed = 1

    'call test case error report'
    WebUI.callTestCase(findTestCase('Main Register/ErrorReport'), [('excelPathRegister') : excelPathRegister], FailureHandling.CONTINUE_ON_FAILURE)
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
		'equals', '//*[@id="' + tokenUnique + '"]/div/div[2]/div['+ (i + 1) +']', true)

			WebUI.click(modifyObjectClicked)
			break
		}
	}
	} else {
		WebUI.click(findTestObject(locationObject))
		
		WebUI.sendKeys(findTestObject(locationObject), Keys.chord(Keys.ENTER))
	}
}