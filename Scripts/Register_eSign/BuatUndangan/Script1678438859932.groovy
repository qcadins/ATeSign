import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare variable array'
ArrayList<String> saldoBefore, saldoAfter

int countCheckSaldo = 0

WebUI.openBrowser('')

saldoBefore = loginAdminGetSaldo(countCheckSaldo, conneSign)

countCheckSaldo = 1

GlobalVariable.FlagFailed = 0

'call test case login inveditor'
WebUI.callTestCase(findTestCase('Login/Login_Inveditor'), [:], FailureHandling.CONTINUE_ON_FAILURE)

'click menu buat undangan'
WebUI.click(findTestObject('BuatUndangan/menu_BuatUndangan'))

'input NIK'
WebUI.setText(findTestObject('BuatUndangan/input_NIK'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        9))

'input nama lengkap'
WebUI.setText(findTestObject('BuatUndangan/input_NamaLengkap'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        10))

'input tempat lahir'
WebUI.setText(findTestObject('BuatUndangan/input_TempatLahir'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        11))

'input tanggal lahir'
WebUI.setText(findTestObject('BuatUndangan/input_TanggalLahir'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        12))

'cek if pria(M) / wanita(F)'
if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 13).equalsIgnoreCase('M')) {
    'click radio pria'
    WebUI.click(findTestObject('BuatUndangan/radio_Pria'))
} else if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 13).equalsIgnoreCase('F')) {
    'click radio wanita'
    WebUI.click(findTestObject('BuatUndangan/radio_Wanita'))
}

'input no handphone'
WebUI.setText(findTestObject('BuatUndangan/input_noHandphone'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        14))

'input email'
WebUI.setText(findTestObject('BuatUndangan/input_Email'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        15))

'input alamat lengkap'
WebUI.setText(findTestObject('BuatUndangan/input_AlamatLengkap'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        17))

'input provinsi'
WebUI.setText(findTestObject('BuatUndangan/input_Provinsi'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        18))

'input kota'
WebUI.setText(findTestObject('BuatUndangan/input_Kota'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        19))

'input kecamatan'
WebUI.setText(findTestObject('BuatUndangan/input_Kecamatan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        20))

'input kelurahan'
WebUI.setText(findTestObject('BuatUndangan/input_Kelurahan'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        21))

'input kode pos'
WebUI.setText(findTestObject('BuatUndangan/input_KodePos'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        22))

'input wilayah'
WebUI.setText(findTestObject('BuatUndangan/input_Wilayah'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        23))

'input office'
WebUI.setText(findTestObject('BuatUndangan/input_Office'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        25))

'input lini bisnis'
WebUI.setText(findTestObject('BuatUndangan/input_LiniBisnis'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        26))

'input task no'
WebUI.setText(findTestObject('BuatUndangan/input_TaskNo'), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
        27))

'click button save'
WebUI.click(findTestObject('BuatUndangan/button_Save'))

if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/button_YaProses'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'click button ya proses'
    WebUI.click(findTestObject('BuatUndangan/button_YaProses'))
}

'declare isMmandatory Complete'
int isMandatoryComplete = Integer.parseInt(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 5))

'cek if muncul popup'
if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/label_ValidationError'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get reason'
    ReasonFailed = WebUI.getText(findTestObject('BuatUndangan/label_ReasonError'))

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + ReasonFailed)

    'click button tutup error'
    WebUI.click(findTestObject('BuatUndangan/button_TutupError'))

    'click button cancel'
    WebUI.click(findTestObject('BuatUndangan/button_Cancel'))

    'click button ya batal undangan'
    WebUI.click(findTestObject('BuatUndangan/button_YaBatalUndangan'))

    GlobalVariable.FlagFailed = 1
} else if (WebUI.verifyElementPresent(findTestObject('BuatUndangan/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
    'get reason'
    ReasonFailed = WebUI.getAttribute(findTestObject('BuatUndangan/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + ReasonFailed)

    if (ReasonFailed.contains('sudah digunakan oleh link undangan lain') || ReasonFailed.contains('sudah terdaftar')) {
        'declare error type error'
        GlobalVariable.ErrorType = 'ERROR'
    } else {
        'declare error type reject'
        GlobalVariable.ErrorType = 'REJECT'
    }
    
    GlobalVariable.FlagFailed = 1

    'call test case error report'
    WebUI.callTestCase(findTestCase('Register_eSign/ErrorReport'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
        FailureHandling.CONTINUE_ON_FAILURE)
} else if (WebUI.getAttribute(findTestObject('BuatUndangan/PopUp/input_Link'), 'value', FailureHandling.OPTIONAL) == 'undefined') {
    GlobalVariable.ErrorType = 'ERROR'

    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ErrorType)

    'click tutup popup'
    WebUI.click(findTestObject('BuatUndangan/button_TutupDapatLink'))

    GlobalVariable.FlagFailed = 1

    'call test case error report'
    WebUI.callTestCase(findTestCase('Register_eSign/ErrorReport'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
        FailureHandling.CONTINUE_ON_FAILURE)
} else if (isMandatoryComplete > 0) {
    'write to excel status failed dan reason'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + ';') + GlobalVariable.ReasonFailedMandatory)
} else {
    'write to excel success'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'BuatUndangan', 0, GlobalVariable.NumofColm - 
        1, GlobalVariable.StatusSuccess)

    'get link'
    GlobalVariable.Link = WebUI.getAttribute(findTestObject('BuatUndangan/PopUp/input_Link'), 'value')

    'write to excel Link buat undangan'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'BuatUndangan', 66, GlobalVariable.NumofColm - 
        1, GlobalVariable.Link)

    'HIT API Login untuk token : invenditor@womf'
    responLogin = WS.sendRequest(findTestObject('Postman/Login', [('username') : findTestData('Login/Login').getValue(2, 
                    4), ('password') : findTestData('Login/Login').getValue(3, 4)]))

    'Jika status HIT API 200 OK'
    if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
        'Parsing token menjadi GlobalVariable'
        GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')

        'HIT API get Invitation Link'
        responGetInvLink = WS.sendRequest(findTestObject('Postman/Get Inv Link', [('callerId') : '""', ('receiverDetail') : ('"' + 
                    findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 15)) + '"', ('tenantCode') : ('"' + 
                    GlobalVariable.Tenant) + '"', ('vendorCode') : ('"' + GlobalVariable.Psre) + '"']))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responGetInvLink, 200, FailureHandling.OPTIONAL) == true) {
            'get Status Code'
            status_Code = WS.getElementPropertyValue(responGetInvLink, 'status.code')

            'Jika status codenya 0'
            if (status_Code == 0) {
                'Get invitation Link'
                InvitationLink = WS.getElementPropertyValue(responGetInvLink, 'invitationLink')

                if (WebUI.verifyMatch(GlobalVariable.Link, InvitationLink, false)) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'BuatUndangan', 
                        0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                } else {
                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
                            2).replace('-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)
                }
            } else {
                messageFailed = WS.getElementPropertyValue(responGetInvLink, 'status.message', FailureHandling.OPTIONAL).toString()

                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 
                        2).replace('-', '') + ';') + messageFailed)
            }
        } else {
            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedHitAPI)
        }
    } else {
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2).replace(
                '-', '') + ';') + GlobalVariable.ReasonFailedHitAPI)
    }
    
    'click tutup popup'
    WebUI.click(findTestObject('BuatUndangan/button_TutupDapatLink'))

    if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 6).length() > 0) {
        'call test case inquiry invitation'
        WebUI.callTestCase(findTestCase('InquiryInvitation/InquiryInvitation'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
            FailureHandling.STOP_ON_FAILURE)
    } else {
        'call test case verif Submit Data'
        WebUI.callTestCase(findTestCase('Register_eSign/verifSubmitData'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
            FailureHandling.CONTINUE_ON_FAILURE)
    }
    
    'call test case daftar akun data verif'
    WebUI.callTestCase(findTestCase('Register_eSign/DaftarAkunDataVerif'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
        FailureHandling.CONTINUE_ON_FAILURE)

    if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
        'delay nunggu data db'
        WebUI.delay(5)

        'call test case BuatUndanganStore DB'
        WebUI.callTestCase(findTestCase('Register_eSign/BuatUndanganStoreDB'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
            FailureHandling.CONTINUE_ON_FAILURE)
    }
    
	if (GlobalVariable.FlagFailed == 0) {		
	    'kurang saldo before dengan proses verifikasi'
	    saldoBefore.set(0, (Integer.parseInt(saldoBefore[0]) - 1).toString())
	
	    saldoBefore.set(1, (Integer.parseInt(saldoBefore[1]) - 1).toString())
	
	    'kurang saldo before dengan jumlah counter send OTP'
	    saldoBefore.set(2, (Integer.parseInt(saldoBefore[2]) - GlobalVariable.Counter).toString())
	
	    saldoBefore.set(3, (Integer.parseInt(saldoBefore[3]) - GlobalVariable.Counter).toString())
	
	    saldoAfter = loginAdminGetSaldo(countCheckSaldo, conneSign)
	
	    'verify saldoafter tidak sama dengan saldo before'
	    checkVerifyEqualOrMatch(saldoAfter.equals(saldoBefore), ' Saldo')
	}
}

if (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 61).equalsIgnoreCase('Yes') && (GlobalVariable.FlagFailed == 
0)) {
    'call test case untuk cek inquiry invitation field after register'
    WebUI.callTestCase(findTestCase('InquiryInvitation/InquiryInvitationAfterRegist'), [('excelPathBuatUndangan') : 'Registrasi/BuatUndangan'], 
        FailureHandling.CONTINUE_ON_FAILURE)
}

def loginAdminGetSaldo(int countCheckSaldo, Connection conneSign) {
    ArrayList<String> saldo = []

    'navigate to url esign'
    WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 5))

    'maximize window'
    WebUI.maximizeWindow()

    'set value userLogin'
    GlobalVariable.userLogin = findTestData(excelPathBuatUndangan).getValue(2, 66).toUpperCase()

    'input email'
    WebUI.setText(findTestObject('Login/input_Email'), findTestData(excelPathBuatUndangan).getValue(2, 66))

    'input password'
    WebUI.setText(findTestObject('Login/input_Password'), findTestData(excelPathBuatUndangan).getValue(2, 
            67))

    'click button login'
    WebUI.click(findTestObject('Login/button_Login'), FailureHandling.STOP_ON_FAILURE)

    'input perusahaan'
    WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excelPathBuatUndangan).getValue(2, 
            68))

    WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))

    'input peran'
    WebUI.setText(findTestObject('Login/input_Peran'), findTestData(excelPathBuatUndangan).getValue(2, 
            69))

    WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))

    'click button pilih peran'
    WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)

    'click menu saldo'
    WebUI.click(findTestObject('BuatUndangan/checkSaldo/menu_Saldo'))

    'click ddl bahasa'
    WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_bahasa'))

    'click english'
    WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_English'))

    'select vendor'
    WebUI.selectOptionByLabel(findTestObject('BuatUndangan/checkSaldo/select_Vendor'), '(?i)' + 'VIDA', true)

    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    for (index = 2; index <= variable.size(); index++) {
        'modify object box info'
        modifyObjectBoxInfo = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) + 
            ']/div/div/div/div/div[1]/h3', true)

        'check if box info = tipe saldo di excel'
        if (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase('Verification')) {
            'modify object qty'
            modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
                'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) + 
                ']/div/div/div/div/div[2]/h3', true)

            'get qty saldo before'
            saldo.add(WebUI.getText(modifyObjectQty).replace(',', ''))

            break
        }
    }
    
    'input tipe saldo'
    WebUI.setText(findTestObject('BuatUndangan/checkSaldo/input_TipeSaldo'), 'Verification')

    'enter untuk input tipe saldo'
    WebUI.sendKeys(findTestObject('BuatUndangan/checkSaldo/input_TipeSaldo'), Keys.chord(Keys.ENTER))

//    'input tipe transaksi'
//    WebUI.setText(findTestObject('BuatUndangan/checkSaldo/input_TipeTransaksi'), 'Use Verification')
//
//    'enter untuk input tipe saldo'
//    WebUI.sendKeys(findTestObject('BuatUndangan/checkSaldo/input_TipeTransaksi'), Keys.chord(Keys.ENTER))

    'click button cari'
    WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_Cari'))

    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

    'modify object button last page'
    modifyObjectButtonLastPage = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
        variable.size()) + ']', true)

    if (WebUI.getAttribute(modifyObjectButtonLastPage, 'class', FailureHandling.OPTIONAL) != 'disabled') {
        'click button last page'
        WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_LastPage'))
    }
    
    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

    'modify object balance'
    modifyObjectBalance = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[10]/div', true)

    'get trx saldo'
    saldo.add(WebUI.getText(modifyObjectBalance).replace(',', ''))

    if ((countCheckSaldo == 1) && (GlobalVariable.FlagFailed == 0)) {
        'modify object no transaksi'
        modifyObjectNoTransaksi = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div', true)

        'modify object tanggal transaksi'
        modifyObjectTanggalTransaksi = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div', true)

        'modify object tipe transaksi'
        modifyObjectTipeTransaksi = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div', true)

        'modify object user'
        modifyObjectUser = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)

        'modify object no kontrak'
        modifyObjectNoKontrak = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[5]/div', true)

        'modify object Catatan'
        modifyObjectCatatan = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[8]/div', true)

        'modify object qty'
        modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div', true)

        'get trx dari db'
        ArrayList<String> result = CustomKeywords.'connection.DataVerif.getSaldoTrx'(conneSign, findTestData(excelPathBuatUndangan).getValue(
                GlobalVariable.NumofColm, 15), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 14), 
            'Use Verification')

        arrayIndex = 0

        'verify no trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectNoTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' No Trx')

        'verify tgl trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTanggalTransaksi), (result[arrayIndex++]).replace(
                    '.0', ''), false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Trx')

        'verify tipe trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTipeTransaksi), result[arrayIndex++], false, 
                FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Trx')

        'verify user trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUser), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' User')

        'verify note trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectCatatan), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Notes')

        'verify qty trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectQty), (result[arrayIndex++]).replace('-', ''), 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' Qty Trx')
    }
    
    'select vendor'
    WebUI.selectOptionByLabel(findTestObject('BuatUndangan/checkSaldo/select_Vendor'), '(?i)' + 'ESIGN/ADINS', true)

    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > div > div > div div'))

    for (index = 2; index <= variable.size(); index++) {
        'modify object box info'
        modifyObjectBoxInfo = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) + 
            ']/div/div/div/div/div[1]/h3', true)

        'check if box info = tipe saldo di excel'
        if (WebUI.getText(modifyObjectBoxInfo).equalsIgnoreCase('OTP')) {
            'modify object qty'
            modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
                'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/div/div/div/div[' + index) + 
                ']/div/div/div/div/div[2]/h3', true)

            'get qty saldo before'
            saldo.add(WebUI.getText(modifyObjectQty).replace(',', ''))

            break
        }
    }
    
    'input tipe saldo'
    WebUI.setText(findTestObject('BuatUndangan/checkSaldo/input_TipeSaldo'), 'OTP')

    'enter untuk input tipe saldo'
    WebUI.sendKeys(findTestObject('BuatUndangan/checkSaldo/input_TipeSaldo'), Keys.chord(Keys.ENTER))

//    'input tipe transaksi'
//    WebUI.setText(findTestObject('BuatUndangan/checkSaldo/input_TipeTransaksi'), 'Use OTP')
//
//    'enter untuk input tipe saldo'
//    WebUI.sendKeys(findTestObject('BuatUndangan/checkSaldo/input_TipeTransaksi'), Keys.chord(Keys.ENTER))

    'click button cari'
    WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_Cari'))

    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

    'modify object button last page'
    modifyObjectButtonLastPage = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
        'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' + 
        variable.size()) + ']', true)

    if (WebUI.getAttribute(modifyObjectButtonLastPage, 'class', FailureHandling.OPTIONAL) != 'disabled') {
        'click button last page'
        WebUI.click(findTestObject('BuatUndangan/checkSaldo/button_LastPage'))
    }
    
    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

    'modify object balance'
    modifyObjectBalance = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 'equals', 
        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
        variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[10]/div', true)

    'get trx saldo'
    saldo.add(WebUI.getText(modifyObjectBalance).replace(',', ''))

    if ((countCheckSaldo == 1) && (GlobalVariable.FlagFailed == 0)) {
        'modify object no transaksi'
        modifyObjectNoTransaksi = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div', true)

        'modify object tanggal transaksi'
        modifyObjectTanggalTransaksi = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 
            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[2]/div', true)

        'modify object tipe transaksi'
        modifyObjectTipeTransaksi = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div', true)

        'modify object user'
        modifyObjectUser = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[4]/div', true)

        'modify object Catatan'
        modifyObjectCatatan = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 
            'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[8]/div', true)

        'modify object qty'
        modifyObjectQty = WebUI.modifyObjectProperty(findTestObject('BuatUndangan/checkSaldo/modifyObject'), 'xpath', 'equals', 
            ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' + 
            variable.size()) + ']/datatable-body-row/div[2]/datatable-body-cell[9]/div', true)

        'get trx dari db'
        ArrayList<String> result = CustomKeywords.'connection.DataVerif.getSaldoTrx'(conneSign, findTestData(excelPathBuatUndangan).getValue(
                GlobalVariable.NumofColm, 15), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 14), 
            'Use OTP')

        'get count trx'
        String resultCount = CustomKeywords.'connection.DataVerif.getCountTrx'(conneSign, findTestData(excelPathBuatUndangan).getValue(
                GlobalVariable.NumofColm, 15), findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 14), 
            'Use OTP')

        arrayIndex = 0

        'verify no trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectNoTransaksi), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' No Trx')

        'verify tgl trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTanggalTransaksi), (result[arrayIndex++]).replace(
                    '.0', ''), false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Trx')

        'verify tipe trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectTipeTransaksi), result[arrayIndex++], false, 
                FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Trx')

        'verify user trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectUser), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' User')

        'verify note trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectCatatan), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE), ' Notes')

        'verify qty trx ui = db'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyObjectQty), (result[arrayIndex++]).replace('-', ''), 
                false, FailureHandling.CONTINUE_ON_FAILURE), ' Qty trx')

        'verify count transaction'
        checkVerifyEqualOrMatch(WebUI.verifyMatch(resultCount, GlobalVariable.Counter.toString(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Count Trx')
		
		'call function verify list undangan'
		verifyListUndangan()
    }
    
    'close browser'
    WebUI.closeBrowser()

    return saldo
}

def verifyListUndangan(){
	currentDate = new Date().format('yyyy-MM-dd')
	
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
	
	tanggalRegistrasi = WebUI.getText(findTestObject('ListUndangan/table_TanggalRegistrasi')).split(' ', -1)
	
	parsedDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(tanggalRegistrasi[0], 'dd-MMM-yyyy', 'yyyy-MM-dd')
	
	'verify tanggal registrasi'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(parsedDate, currentDate, false, FailureHandling.CONTINUE_ON_FAILURE), ' Tanggal Registrasi')
	
	'verify status registrasi'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_StatusRegistrasi')), 'DONE',
			false, FailureHandling.CONTINUE_ON_FAILURE), ' Status Registrasi')
	
	'verify Status undangan'
	checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('ListUndangan/table_StatusUndangan')), 'NON AKTIF', false,
			FailureHandling.CONTINUE_ON_FAILURE), ' Status Undangan')
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch + reason)

        GlobalVariable.FlagFailed = 1
    }
}

