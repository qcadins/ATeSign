import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare untuk split array excel'
semicolon = ';'

splitIndex = -1

int checked = 0

int firstRun = 0

'looping berdasarkan jumlah kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathPengaturanDokumen).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    }
    else if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'get Tenant per case dari excel'
        GlobalVariable.Tenant = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))

        'get Psre per case dari excel'
        GlobalVariable.Psre = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))

        'check if email login case selanjutnya masih sama dengan sebelumnya'
        if ((findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) != 
        findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Email Login'))) || (firstRun == 
        0)) {
            'call test case login per case'
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('sheet') : sheet, ('Path') : excelPathPengaturanDokumen
                    , ('Email') : 'Email Login', ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], 
                FailureHandling.STOP_ON_FAILURE)

            'click menu pengaturan dokumen'
            WebUI.click(findTestObject('TandaTanganDokumen/btn_PengaturanDokumen'))

            'apakah cek paging diperlukan di awal run'
            if (GlobalVariable.checkPaging == 'Yes') {
               'call function check paging'
               // checkPaging()

                'call function input cancel'
              //  inputCancel(conneSign, checked)
            }
            
            firstRun = 1
        }
        
        'click menu pengaturan dokumen'
        WebUI.click(findTestObject('TandaTanganDokumen/btn_PengaturanDokumen'))

        'declare isMmandatory Complete'
        int isMandatoryComplete = Integer.parseInt(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                rowExcel('Is Mandatory Complete')))

        if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
            'Unexecuted')) {
            GlobalVariable.FlagFailed = 0
        }
        
        'Pembuatan variable mengenai jumlah delete, jumlah lock, dan indexlock untuk loop kedepannya'
        RoleTandaTangan = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('RoleTandaTangan')).split(
            semicolon, splitIndex)

        ArrayList<String> TipeTandaTangan = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('TipeTandaTangan')).split(
            semicolon, splitIndex)

        SignBoxAction = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Pindahkan SignBox')).split(
            semicolon, splitIndex)

        SignBoxLocation = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Lokasi Pemindahan signbox')).split(
            '\\n', splitIndex)

        LockSignBox = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Lock Sign Box')).split(
            semicolon, splitIndex)

        'Klik tombol pengaturan dokumen'
        if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'New')) {
            'Klik tombol tambah pengaturan dokumen'
            WebUI.click(findTestObject('TandaTanganDokumen/btn_Add'))

            'Pengecekan apakah masuk page tambah pengaturan dokumen'
            if (WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_TambahTemplatDokumen'), GlobalVariable.TimeOut)) {
                'call function input form'
                inputForm(conneSign, checked)

                'Klik button lanjut'
                WebUI.click(findTestObject('TandaTanganDokumen/btn_Lanjut'))

                if (isMandatoryComplete > 0) {
                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + semicolon) + GlobalVariable.ReasonFailedMandatory)

                    GlobalVariable.FlagFailed = 1
                } else if (WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_konfirmasi'), GlobalVariable.TimeOut, 
                    FailureHandling.CONTINUE_ON_FAILURE)) {
                    'Klik Konfirmasi no'
                    WebUI.click(findTestObject('TandaTanganDokumen/btn_KonfirmasiNo'))

                    'Data document template code, template name, template description yang telah diinput diverifikasi dengan excel'
                    checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Kode Templat Dokumen')), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateCode'), 
                                'value'), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Document Template Code ')

                    checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Nama Templat Dokumen')), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateName'), 
                                'value'), false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Document Template Name')

                    checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                                rowExcel('$Deskripsi')), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateDescription'), 
                                'value'), false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Deskripsi Document Template')

                    'Klik dropdown mengenai tipe pembayaran'
                    WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_openddlTipePembayaran'))

                    'Verifikasi input tipe pembayaran dengan excel'
                    checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                                rowExcel('$Tipe Pembayaran TTD')), WebUI.getText(findTestObject('TandaTanganDokumen/check_tipePembayaran')), 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Tipe Pembayaran ')

                    'Klik dropdown mengenai status'
                    WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ddlaktif'))

                    'Verifikasi input status dengan excel'
                    checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Status Active')), WebUI.getText(findTestObject('TandaTanganDokumen/check_tipePembayaran')), 
                            false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Status ')

                    'Input enter'
                    WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_Status'), Keys.chord(Keys.ENTER))

                    'Klik lanjut'
                    WebUI.click(findTestObject('TandaTanganDokumen/btn_Lanjut'))

                    'Klik Konfirmasi Yes'
                    WebUI.click(findTestObject('TandaTanganDokumen/btn_KonfirmasiYes'))

                    if (WebUI.verifyElementPresent(findTestObject('Object Repository/TandaTanganDokumen/errorlog'), GlobalVariable.TimeOut, 
                        FailureHandling.OPTIONAL)) {
                        'get reason'
                        ReasonFailed = WebUI.getAttribute(findTestObject('RegisterEsign/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

                        'write to excel status failed dan reason'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + semicolon) + '<') + ReasonFailed) + '>')

                        GlobalVariable.FlagFailed = 1
                    } else if (WebUI.verifyElementPresent(findTestObject('Object Repository/TandaTanganDokumen/btn_ttd'), 
                        GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                        'call function input signbox'
                        inputSignBox()

                        'click button simpan'
                        WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_SimpanPengaturanDokumen'))

                        'Jika error lognya muncul'
                        if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, 
                            FailureHandling.OPTIONAL)) {
                            GlobalVariable.FlagFailed = 1

                            'ambil teks errormessage'
                            errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', 
                                FailureHandling.CONTINUE_ON_FAILURE)

                            'Tulis di excel itu adalah error'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (((findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + errormessage) + '>')

                            GlobalVariable.FlagFailed = 1

                            continue
                        }
                        
                        'call function untuk sorting sequence sign'
                        sortingSequenceSign()

                        'call function checkpopupberhasil'
                        checkPopUpBerhasil(isMandatoryComplete, semicolon)
                    }
                } else {
                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + semicolon) + GlobalVariable.ReasonFailedSaveGagal) + 
                        ' pada Pembuatan Document Template ')

                    GlobalVariable.FlagFailed = 1
                }
            }
        } else if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'View')) {
            'call function search pengaturan dokumen'
            searchPengaturanDokumen()

            'Klik button view'
            WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/button_View'))

            'get data doc template from db'
            result = CustomKeywords.'connection.PengaturanDokumen.getDataDocTemplate'(conneSign, findTestData(excelPathPengaturanDokumen).getValue(
                    GlobalVariable.NumofColm, rowExcel('Kode Templat Dokumen - Search')))

            arrayIndex = 0

            'verify field view doc template code'
            checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/view_DocTempCode'), 
                        'value', FailureHandling.CONTINUE_ON_FAILURE), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify field view doc template name'
            checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/view_DocTempName'), 
                        'value', FailureHandling.CONTINUE_ON_FAILURE), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify field view doc template description'
            checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/view_DocTempDescription'), 
                        'value', FailureHandling.CONTINUE_ON_FAILURE), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify field view doc template status'
            checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/view_DocTempStatus'), 
                        'value', FailureHandling.CONTINUE_ON_FAILURE), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'Klik button Kembali'
            WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/button_Kembali'))

            if (WebUI.verifyElementPresent(findTestObject('Object Repository/TandaTanganDokumen/input_KodeTemplatDokumen'), 
                GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && (GlobalVariable.FlagFailed == 0)) {
                if (GlobalVariable.FlagFailed == 0) {
                    'write to excel success'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                        1, GlobalVariable.StatusSuccess)
                }
            }
        } else if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'Edit')) {
            'call function search pengaturan dokumen'
            searchPengaturanDokumen()

            'Klik button Edit'
            WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/button_Edit'))

            'get data doc template from db'
            result = CustomKeywords.'connection.PengaturanDokumen.dataDocTemplateStoreDB'(conneSign, findTestData(excelPathPengaturanDokumen).getValue(
                    GlobalVariable.NumofColm, rowExcel('Kode Templat Dokumen - Search')))

            arrayIndex = 0

            'verify field view doc template code'
            checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/view_DocTempCode'), 
                        'value', FailureHandling.CONTINUE_ON_FAILURE), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify field view doc template name'
            checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/view_DocTempName'), 
                        'value', FailureHandling.CONTINUE_ON_FAILURE), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify field view doc template description'
            checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/view_DocTempDescription'), 
                        'value', FailureHandling.CONTINUE_ON_FAILURE), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'Klik dropdown mengenai tipe pembayaran'
            WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_openddlTipePembayaran'))

            'verify field view doc template tipe pembayaran'
            checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('TandaTanganDokumen/check_tipePembayaran')), 
                    result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'Klik dropdown mengenai status'
            WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ddlaktif'))

            'verify field view doc template status'
            checkVerifyPaging(WebUI.verifyMatch(WebUI.getText(findTestObject('TandaTanganDokumen/check_tipePembayaran')), 
                    result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'call function input form'
            inputForm(conneSign, checked)

            'Klik button lanjut'
            WebUI.click(findTestObject('TandaTanganDokumen/btn_Lanjut'))

            'Jika label Konfirmasi muncul'
            if (WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_konfirmasi'), GlobalVariable.TimeOut)) {
                'Klik Konfirmasi no'
                WebUI.click(findTestObject('TandaTanganDokumen/btn_KonfirmasiNo'))

                'Data document template code, template name, template description yang telah diinput diverifikasi dengan excel'
                checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Kode Templat Dokumen')), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateCode'), 
                            'value'), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Document Template Code ')

                checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Nama Templat Dokumen')), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateName'), 
                            'value'), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Document Template Name ')

                checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                            rowExcel('$Deskripsi')), WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateDescription'), 
                            'value'), false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Deskripsi Document Template ')

                'Klik dropdown mengenai tipe pembayaran'
                WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_openddlTipePembayaran'))

                'Verifikasi input tipe pembayaran dengan excel'
                checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                            rowExcel('$Tipe Pembayaran TTD')), WebUI.getText(findTestObject('TandaTanganDokumen/check_tipePembayaran')), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' pada tipe pembayaran ')

                'Klik dropdown mengenai status'
                WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ddlaktif'))

                'Verifikasi input status dengan excel'
                checkVerifyEqualorMatch(WebUI.verifyMatch(findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Status Active')), WebUI.getText(findTestObject('TandaTanganDokumen/check_tipePembayaran')), 
                        false, FailureHandling.CONTINUE_ON_FAILURE), ' pada status ')

                'Input enter'
                WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_Status'), Keys.chord(Keys.ENTER))

                'Klik lanjut'
                WebUI.click(findTestObject('TandaTanganDokumen/btn_Lanjut'))

                'Klik Konfirmasi Yes'
                WebUI.click(findTestObject('TandaTanganDokumen/btn_KonfirmasiYes'))

                'Jika error lognya muncul'
                if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    'ambil teks errormessage'
                    errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

                    'jika error message tidak menandakan tidak berhasil'
                    if (!(errormessage.contains('berhasil di update.'))) {
                        'Tulis di excel itu adalah error'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + errormessage) + '>')

                        GlobalVariable.FlagFailed = 1
                    }
                }
                
                if (isMandatoryComplete > 0) {
                    'write to excel status failed dan reason'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Reason Failed')).replace('-', '') + semicolon) + GlobalVariable.ReasonFailedMandatory)

                    GlobalVariable.FlagFailed = 1
                } else if (WebUI.verifyElementPresent(findTestObject('Object Repository/TandaTanganDokumen/input_KodeTemplatDokumen'), 
                    GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                } else if (WebUI.verifyElementPresent(findTestObject('Object Repository/TandaTanganDokumen/btn_ttd'), GlobalVariable.TimeOut, 
                    FailureHandling.OPTIONAL)) {
                    'call function input signbox'
                    inputSignBox()

                    'click button simpan'
                    WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_SimpanPengaturanDokumen'))

                    'Jika error lognya muncul'
                    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                        GlobalVariable.FlagFailed = 1

                        'ambil teks errormessage'
                        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

                        'Tulis di excel itu adalah error'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (((findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + errormessage) + '>')

                        GlobalVariable.FlagFailed = 1

                        continue
                    }
                    
                    'call function sorting sequence sign'
                    sortingSequenceSign()

                    'call function checkpopupberhasil'
                    checkPopUpBerhasil(isMandatoryComplete, semicolon)
                }
            }
        } else if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'Setting')) {
            'call function search pengaturan dokumen'
            searchPengaturanDokumen()

            'Klik button setting'
            WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/button_Setting'))

            'get data doc template from db'
            result = CustomKeywords.'connection.PengaturanDokumen.getDataDocTemplate'(conneSign, findTestData(excelPathPengaturanDokumen).getValue(
                    GlobalVariable.NumofColm, rowExcel('Kode Templat Dokumen - Search')))

            arrayIndex = 0

            'verify field view doc template code'
            checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/view_DocTempCode'), 
                        'value', FailureHandling.CONTINUE_ON_FAILURE), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify field view doc template name'
            checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/view_DocTempName'), 
                        'value', FailureHandling.CONTINUE_ON_FAILURE), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify field view doc template description'
            checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/view_DocTempDescription'), 
                        'value', FailureHandling.CONTINUE_ON_FAILURE), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'verify field view doc template status'
            checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/view_DocTempStatus'), 
                        'value', FailureHandling.CONTINUE_ON_FAILURE), result[arrayIndex++], false, FailureHandling.CONTINUE_ON_FAILURE))

            'count signbox'
            variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-setting-signer > div:nth-child(3) > div > app-document-anotate > section > section.box > div app-bbox'))

            String roleTTD

            String tipeTTD

            'looping signbox sesuai jumlah yang ada di ui'
            for (index = 1; index <= variable.size(); index++) {
                'modify object Role sign box'
                modifyObjectRoleSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                    index) + ']/div/div/small', true)

                'modify object Tipe sign box'
                modifyObjectTipeSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 
                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                    index) + ']/div/div/span', true)

                'modify object sign box'
                modifyObjectSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 
                    'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                    index) + ']/div', true)

                roleTTD = WebUI.getText(modifyObjectRoleSignBox)

                if (roleTTD.equalsIgnoreCase('Meterai')) {
                    tipeTTD = roleTTD
                } else if (!(roleTTD.equalsIgnoreCase('Meterai'))) {
                    tipeTTD = (WebUI.getText(modifyObjectTipeSignBox).split(' ')[0])
                }
                
                if (tipeTTD == 'Prf disini') {
                    tipeTTD = 'Paraf'
                }
                
                locationSignBox = WebUI.getAttribute(modifyObjectSignBox, 'style', FailureHandling.CONTINUE_ON_FAILURE)

                'looping signbox inputan excel'
                for (indexExcel = 0; indexExcel < RoleTandaTangan.size(); indexExcel++) {
                    if (!(roleTTD.equalsIgnoreCase(RoleTandaTangan[indexExcel]) && tipeTTD.equalsIgnoreCase(TipeTandaTangan[
                        indexExcel]))) {
                        if (indexExcel == (RoleTandaTangan.size() - 1)) {
                            'modify object button delete sign box'
                            modifyObjectButtonDeleteSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 
                                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                index) + ']/div/button[2]/span', true)

                            'scroll element'
                            WebUI.scrollToElement(modifyObjectButtonDeleteSignBox, GlobalVariable.TimeOut)

                            'click button delete'
                            WebUI.click(modifyObjectButtonDeleteSignBox)

                            index--

                            variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-setting-signer > div:nth-child(3) > div > app-document-anotate > section > section.box > div app-bbox'))
                        }
                    } else if (roleTTD.equalsIgnoreCase(RoleTandaTangan[indexExcel]) && tipeTTD.equalsIgnoreCase(TipeTandaTangan[
                        indexExcel])) {
                        if ((SignBoxAction[indexExcel]).equalsIgnoreCase('Yes')) {
                            if (!(locationSignBox.contains(SignBoxLocation[indexExcel]))) {
                                'memindahkan sign box'
                                CustomKeywords.'customizekeyword.JSExecutor.jsExecutionFunction'('arguments[0].style.transform = arguments[1]', 
                                    ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                    index) + ']/div', SignBoxLocation[indexExcel])
                            }
                        }
                        
                        'modify object button lock sign box'
                        modifyObjectButtonLockSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 
                            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                            index) + ']/div/button[1]', true)

                        statusLock = WebUI.getAttribute(modifyObjectButtonLockSignBox, 'class', FailureHandling.CONTINUE_ON_FAILURE).toString()

                        if ((LockSignBox[indexExcel]).equalsIgnoreCase('Yes')) {
                            if (!(statusLock.equalsIgnoreCase('fa fa-2x fa-lock'))) {
                                'click lock signbox'
                                WebUI.click(modifyObjectButtonLockSignBox)
                            }
                        } else if ((LockSignBox[indexExcel]).equalsIgnoreCase('Yes')) {
                            if (statusLock.equalsIgnoreCase('fa fa-2x fa-lock')) {
                                'click lock signbox'
                                WebUI.click(modifyObjectButtonLockSignBox)
                            }
                        }
                        
                        break
                    }
                }
            }
            
            'looping signbox inputan excel'
            for (indexExcel = 0; indexExcel < RoleTandaTangan.size(); indexExcel++) {
                'looping signbox sesuai jumlah yang ada di ui'
                for (index = 1; index <= variable.size(); index++) {
                    'modify object Role sign box'
                    modifyObjectRoleSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 
                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                        index) + ']/div/div/small', true)

                    'modify object Tipe sign box'
                    modifyObjectTipeSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 
                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                        index) + ']/div/div/span', true)

                    'modify object sign box'
                    modifyObjectSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 
                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                        index) + ']/div', true)

                    roleTTD = WebUI.getText(modifyObjectRoleSignBox)

                    if (roleTTD.equalsIgnoreCase('Meterai') == false) {
                        tipeTTD = (WebUI.getText(modifyObjectTipeSignBox).split(' ')[0])
                    } else {
                        tipeTTD = roleTTD
                    }
                    
                    if (tipeTTD == 'Prf') {
                        tipeTTD = 'Paraf'
                    }
                    
                    locationSignBox = WebUI.getAttribute(modifyObjectSignBox, 'style', FailureHandling.CONTINUE_ON_FAILURE)

                    if (!(roleTTD.equalsIgnoreCase(RoleTandaTangan[indexExcel]) && tipeTTD.equalsIgnoreCase(TipeTandaTangan[
                        indexExcel]))) {
                        if (index == variable.size()) {
                            if ((TipeTandaTangan[indexExcel]).equalsIgnoreCase('TTD')) {
                                'Klik button tanda tangan'
                                WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ttd'))
                            } else if ((TipeTandaTangan[indexExcel]).equalsIgnoreCase('Paraf')) {
                                'Klik button tanda tangan'
                                WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_paraf'))
                            } else if ((TipeTandaTangan[indexExcel]).equalsIgnoreCase('Meterai')) {
                                'Klik button tanda tangan'
                                WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_materai'))
                            }
                            
                            if ((TipeTandaTangan[indexExcel]).equalsIgnoreCase('TTD') || (TipeTandaTangan[indexExcel]).equalsIgnoreCase(
                                'Paraf')) {
                                'Verify label tanda tangannya muncul atau tidak'
                                WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_TipeTandaTangan'), GlobalVariable.TimeOut, 
                                    FailureHandling.CONTINUE_ON_FAILURE)

                                'Memilih tipe signer apa berdasarkan excel'
                                WebUI.selectOptionByLabel(findTestObject('TandaTanganDokumen/ddl_TipeTandaTangan'), RoleTandaTangan[
                                    indexExcel], false)

                                'Klik set tanda tangan'
                                WebUI.click(findTestObject('TandaTanganDokumen/btn_setTandaTangan'))

                                'count signbox'
                                variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-setting-signer > div:nth-child(3) > div > app-document-anotate > section > section.box > div app-bbox'))

                                'modify label tipe tanda tangan di kotak'
                                modifyObjectRoleSignBox = WebUI.modifyObjectProperty(findTestObject('Object Repository/TandaTanganDokumen/lbl_TTDTipeTandaTangan'), 
                                    'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                    variable.size()) + ']/div/div/small', true)

                                'Verifikasi antara excel dan UI, apakah tipenya sama'
                                WebUI.verifyMatch(RoleTandaTangan[indexExcel], WebUI.getText(modifyObjectRoleSignBox), false)
                            }
                            
                            'Verify apakah tanda tangannya ada'
                            if (WebUI.verifyElementPresent(modifyObjectRoleSignBox, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
                                'check if signbox mau dipindahkan'
                                if ((SignBoxAction[indexExcel]).equalsIgnoreCase('Yes')) {
                                    'memindahkan sign box'
                                    CustomKeywords.'customizekeyword.JSExecutor.jsExecutionFunction'('arguments[0].style.transform = arguments[1]', 
                                        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                        variable.size()) + ']/div', SignBoxLocation[indexExcel])
                                }
                                
                                'check if signbox mau dilock posisinya'
                                if ((LockSignBox[indexExcel]).equalsIgnoreCase('Yes')) {
                                    'modify obejct lock signbox'
                                    modifyobjectLockSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/btn_LockSignBox'), 
                                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                        variable.size()) + ']/div/button[1]/span', true)

                                    'click lock signbox'
                                    WebUI.click(modifyobjectLockSignBox)
                                }
                            }
                        }
                    } else if (roleTTD.equalsIgnoreCase(RoleTandaTangan[indexExcel]) && tipeTTD.equalsIgnoreCase(TipeTandaTangan[
                        indexExcel])) {
                        if ((SignBoxAction[indexExcel]).equalsIgnoreCase('Yes')) {
                            if (!(locationSignBox.contains(SignBoxLocation[indexExcel]))) {
                                'memindahkan sign box'
                                CustomKeywords.'customizekeyword.JSExecutor.jsExecutionFunction'('arguments[0].style.transform = arguments[1]', 
                                    ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                                    index) + ']/div', SignBoxLocation[indexExcel])
                            }
                        }
                        
                        'modify object button lock sign box'
                        modifyObjectButtonLockSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 
                            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                            index) + ']/div/button[1]', true)

                        statusLock = WebUI.getAttribute(modifyObjectButtonLockSignBox, 'class', FailureHandling.CONTINUE_ON_FAILURE).toString()

                        if ((LockSignBox[indexExcel]).equalsIgnoreCase('Yes')) {
                            if (!(statusLock.equalsIgnoreCase('fa fa-2x fa-lock'))) {
                                'click lock signbox'
                                WebUI.click(modifyObjectButtonLockSignBox)
                            }
                        } else if ((LockSignBox[indexExcel]).equalsIgnoreCase('Yes')) {
                            if (statusLock.equalsIgnoreCase('fa fa-2x fa-lock')) {
                                'click lock signbox'
                                WebUI.click(modifyObjectButtonLockSignBox)
                            }
                        }
                        
                        break
                    }
                }
            }
            
            'click button simpan'
            WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_SimpanPengaturanDokumen'))

            'call function sorting sequence sign'
            sortingSequenceSign()

            'call function checkpopupberhasil'
            checkPopUpBerhasil(isMandatoryComplete, semicolon)
        }
        
        'check if new or edit'
        if ((findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'New') || findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
            'Edit')) && (GlobalVariable.FlagFailed == 0)) {
            'call fucntion after edit'
            verifyAfterAddorEdit(TipeTandaTangan)

            'check if storedb = yes dan flagfailed = 0'
            if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                'call test case pengaturan dokumen store db'
                WebUI.callTestCase(findTestCase('PengaturanDokumen/PengaturanDokumenStoreDB'), [('excelPathPengaturanDokumen') : 'PengaturanDokumen/PengaturanDokumen'], 
                    FailureHandling.CONTINUE_ON_FAILURE)
            }
        }
    }
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        GlobalVariable.FlagFailed = 1

        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

def checkPaging() {
    'Klik tombol pengaturan dokumen'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_PengaturanDokumen'))

    'Input teks di nama template dokumen'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_NamaTemplatDokumen'), 'e')

    'Input teks kode template dokumen'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_KodeTemplatDokumen'), 'f')

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), 'Active')

    'Klik enter'
    WebUI.sendKeys(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), Keys.chord(Keys.ENTER))

    'Klik button set Ulang'
    WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_Set Ulang'))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/input_NamaTemplatDokumen'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/input_KodeTemplatDokumen'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify field ke reset'
    checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), 
                'value', FailureHandling.CONTINUE_ON_FAILURE), '', false, FailureHandling.CONTINUE_ON_FAILURE))

    'check if ada paging'
    if (WebUI.verifyElementVisible(findTestObject('TandaTanganDokumen/button_NextPage'), FailureHandling.OPTIONAL)) {
        'click page 2'
        WebUI.click(findTestObject('TandaTanganDokumen/button_Page2'))

        'verify paging di page 2'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/button_Page2'), 'class', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click page 1'
        WebUI.click(findTestObject('TandaTanganDokumen/button_Page1'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/button_Page1'), 'class', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click next page'
        WebUI.click(findTestObject('TandaTanganDokumen/button_NextPage'))

        'verify paging di page 2'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/button_Page2'), 'class', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

        'click prev page'
        WebUI.click(findTestObject('TandaTanganDokumen/button_PrevPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/button_Page1'), 'class', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))

        'get total page'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-documents > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

        'click max page'
        WebUI.click(findTestObject('TandaTanganDokumen/button_MaxPage'))

        'get total data'
        lastPage = (Double.parseDouble(WebUI.getText(findTestObject('TandaTanganDokumen/label_TotalData')).split(' ', -1)[
            0]) / 25)

        'jika hasil perhitungan last page memiliki desimal'
        if (lastPage.toString().endsWith('.0')) {
            'tidak ada round up'
            additionalRoundUp = 0
        } else {
            'round up dengan tambahan 0.5'
            additionalRoundUp = 0.5
        }
        
        'verify paging di page terakhir'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/paging_Page'), 'aria-label', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'page ' + Math.round(lastPage + additionalRoundUp), 
                false, FailureHandling.CONTINUE_ON_FAILURE))

        'click min page'
        WebUI.click(findTestObject('TandaTanganDokumen/button_MinPage'))

        'verify paging di page 1'
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/button_Page1'), 'class', 
                    FailureHandling.CONTINUE_ON_FAILURE), 'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
    }
}

def searchPengaturanDokumen() {
    'click menu pengaturan dokumen'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_PengaturanDokumen'))

    'Input teks kode template dokumen'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_KodeTemplatDokumen'), findTestData(excelPathPengaturanDokumen).getValue(
            GlobalVariable.NumofColm, rowExcel('Kode Templat Dokumen - Search')))

    'Input teks di nama template dokumen'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_NamaTemplatDokumen'), findTestData(excelPathPengaturanDokumen).getValue(
            GlobalVariable.NumofColm, rowExcel('Nama Templat Dokumen - Search')))

    'Input All untuk reset ddl agar tidak salah pilih'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), 'All')

    'Klik enter'
    WebUI.sendKeys(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), Keys.chord(Keys.ENTER))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), findTestData(excelPathPengaturanDokumen).getValue(
            GlobalVariable.NumofColm, rowExcel('Status Active - Search')))

    'Klik enter'
    WebUI.sendKeys(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), Keys.chord(Keys.ENTER))

    'Klik button cari'
    WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_cari'))
}

def verifyAfterAddorEdit(ArrayList<String> tipeTTD) {
    'click menu pengaturan dokumen'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_PengaturanDokumen'))

    'Input teks kode template dokumen'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_KodeTemplatDokumen'), findTestData(excelPathPengaturanDokumen).getValue(
            GlobalVariable.NumofColm, rowExcel('Kode Templat Dokumen')))

    'Input teks di nama template dokumen'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_NamaTemplatDokumen'), findTestData(excelPathPengaturanDokumen).getValue(
            GlobalVariable.NumofColm, rowExcel('Nama Templat Dokumen')))

    'Input AKtif pada input Status'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), findTestData(excelPathPengaturanDokumen).getValue(
            GlobalVariable.NumofColm, rowExcel('Status Active')))

    'Klik enter'
    WebUI.sendKeys(findTestObject('Object Repository/TandaTanganDokumen/input_Status'), Keys.chord(Keys.ENTER))

    'Klik button cari'
    WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_cari'))

    'verify after add / edit kode pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_KodePengaturanDokumen')), 
            findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Kode Templat Dokumen')), 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' kode pengaturan dokumen')

    'verify after add / edit nama pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_NamaPengaturanDokumen')), 
            findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Nama Templat Dokumen')).toUpperCase(), 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' Nama pengaturan dokumen')

    'verify after add / edit deskripsi pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_DeskripsiPengaturanDokumen')), 
            findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('$Deskripsi')).toUpperCase(), 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' Deskripsi pengaturan dokumen')

    'verify after add / edit TTD pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_TTD')), 
            tipeTTD.count('TTD').toString(), false, FailureHandling.CONTINUE_ON_FAILURE), ' TTD pengaturan dokumen')

    'verify after add / edit Paraf pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_Paraf')), 
            tipeTTD.count('Paraf').toString(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Paraf pengaturan dokumen')

    'verify after add / edit Meterai pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_Meterai')), 
            tipeTTD.count('Meterai').toString(), false, FailureHandling.CONTINUE_ON_FAILURE), ' Meterai pengaturan dokumen')

    'verify after add / edit Tipe Pembayaran Dokumen pengaturan dokumen'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getText(findTestObject('Object Repository/TandaTanganDokumen/label_TipePembayaranTTD')), 
            findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('$Tipe Pembayaran TTD')), 
            false, FailureHandling.CONTINUE_ON_FAILURE), ' Tipe Pembayaran Dokumen pengaturan dokumen')
}

def inputCancel(Connection conneSign, int checked) {
    'click menu pengaturan dokumen'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_PengaturanDokumen'))

    'Klik tombol tambah pengaturan dokumen'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_Add'))

    'call function input form'
    inputForm(conneSign, checked)

    'Klik button cancel'
    WebUI.click(findTestObject('TandaTanganDokumen/button_Cancel'))

    'check if sudah tercancel dan pindah halaman utama'
    if (WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/input_tipePembayaran'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        checkVerifyEqualorMatch(false, ' FAILED TO CANCEL')
    }
    
    'Klik tombol tambah pengaturan dokumen'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_Add'))

    'verify field kode template dokumen kosong'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateCode'), 
                'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Kode Template Dokumen tidak kosong')

    'verify field Nama template dokumen kosong'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateName'), 
                'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Nama Template Dokumen tidak kosong')

    'verify field Deskripsi template dokumen kosong'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_documentTemplateDescription'), 
                'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Deskripsi Template Dokumen tidak kosong')

    'verify field tipe pembayaran kosong'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_tipePembayaran'), 
                'value', FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field Tipe Pembayaran tidak kosong')

    'verify field status kosong'
    checkVerifyEqualorMatch(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('TandaTanganDokumen/input_Status'), 'value', 
                FailureHandling.OPTIONAL), '', false, FailureHandling.CONTINUE_ON_FAILURE), ' Field status tidak kosong')

    'Klik button cancel'
    WebUI.click(findTestObject('TandaTanganDokumen/button_Cancel'))
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkDDL(TestObject objectDDL, ArrayList listDB, String reason) {
    'declare array untuk menampung ddl'
    ArrayList list = []

    'click untuk memunculkan ddl'
    WebUI.click(objectDDL)

    'get id ddl'
    id = WebUI.getAttribute(findTestObject('TandaTanganDokumen/ddlClass'), 'id', FailureHandling.CONTINUE_ON_FAILURE)

    'get row'
    variable = DriverFactory.webDriver.findElements(By.cssSelector(('#' + id) + '> div > div:nth-child(2) div'))

    'looping untuk get ddl kedalam array'
    for (i = 1; i < variable.size(); i++) {
        'modify object DDL'
        modifyObjectDDL = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals', 
            ((('//*[@id=\'' + id) + '-') + i) + '\']', true)

        'add ddl ke array'
        list.add(WebUI.getText(modifyObjectDDL))
    }
    
    'verify ddl ui = db'
    checkVerifyEqualOrMatch(listDB.containsAll(list), reason)

    println(listDB)

    println(list)

    'verify jumlah ddl ui = db'
    checkVerifyEqualOrMatch(WebUI.verifyEqual(list.size(), listDB.size(), FailureHandling.CONTINUE_ON_FAILURE), ' Jumlah ' + 
        reason)

    checkVerifyEqualOrMatch(WebUI.verifyMatch(list.sort().toString(), listDB.sort().toString(), false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' Jumlah ' + reason)

    'Input enter untuk tutup ddl'
    WebUI.sendKeys(objectDDL, Keys.chord(Keys.ENTER))
}

def checkPopUpBerhasil(int isMandatoryComplete, String semicolon) {
    'check if pengaturan document berhasil disimpan'
    if (isMandatoryComplete > 0) {
        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                '-', '') + semicolon) + GlobalVariable.ReasonFailedMandatory)

        GlobalVariable.FlagFailed = 1
    } else if (WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
        'get reason from error log'
        reason = WebUI.getAttribute(findTestObject('TandaTanganDokumen/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

        'write to excel status failed dan reason'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (((findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                '-', '') + semicolon) + '<') + reason) + '>')

        GlobalVariable.FlagFailed = 1
    } else if (WebUI.getText(findTestObject('TandaTanganDokumen/label_PopUp'), FailureHandling.OPTIONAL).equalsIgnoreCase(
        'Document Template berhasil di simpan')) {
        'click button ok'
        WebUI.click(findTestObject('TandaTanganDokumen/button_Ok'))

        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)
        }
    }
}

def sortingSequenceSign() {
    'check if Sequential signing iya'
    if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Sequential Signing')).equalsIgnoreCase(
        'Iya')) {
        'get urutan seq sign dari excel'
        ArrayList seqSignRole = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Urutan Signing')).split(
            ';', -1)

        'count box'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('#cdk-drop-list-0 div'))

        'looping seq sign'
        for (int seq = 1; seq <= variable.size(); seq++) {
            'modify label tipe tanda tangan di kotak'
            modifyObject = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals', 
                ('//*[@id="cdk-drop-list-0"]/div[' + seq) + ']', true)

            index = (seqSignRole.indexOf(WebUI.getText(modifyObject).replace(seq.toString() + '. ', '')) + 1)

            if (seq != index) {
                'modify label tipe tanda tangan di kotak'
                modifyObjectNew = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 
                    'equals', ('//*[@id="cdk-drop-list-0"]/div[' + index) + ']', true)

                'pindahin ke urutan sesuai excel'
                WebUI.dragAndDropToObject(modifyObject, modifyObjectNew)

                'untuk proses pemindahan'
                WebUI.delay(2)

                seq--
            }
        }
        
        'click button simpan'
        WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_SimpanPengaturanDokumen'))

        'delay untuk loading simpan'
        WebUI.delay(3)
    }
}

def inputForm(Connection conneSign, int checked) {
    if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Action')).equalsIgnoreCase(
        'New')) {
        'Input text documentTemplateCode'
        WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateCode'), findTestData(excelPathPengaturanDokumen).getValue(
                GlobalVariable.NumofColm, rowExcel('Kode Templat Dokumen')))
    }
    
    'Input text documentTemplateName'
    WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateName'), findTestData(excelPathPengaturanDokumen).getValue(
            GlobalVariable.NumofColm, rowExcel('Nama Templat Dokumen')))

    'Input text documentTemplateDescription'
    WebUI.setText(findTestObject('TandaTanganDokumen/input_documentTemplateDescription'), findTestData(excelPathPengaturanDokumen).getValue(
            GlobalVariable.NumofColm, rowExcel('$Deskripsi')))

    if ((GlobalVariable.NumofColm == 2) && (checked == 0)) {
        'get data tipe-tipe pembayaran secara asc'
        ArrayList tipePembayaranDB = CustomKeywords.'connection.PengaturanDokumen.getLovTipePembayaran'(conneSign)

        'check ddl tipe pembayaran'
        checkDDL(findTestObject('TandaTanganDokumen/input_tipePembayaran'), tipePembayaranDB, ' pada tipe pembayaran Ttd ')
    }
    
    'Input value tipe pembayaran'
    WebUI.setText(findTestObject('TandaTanganDokumen/input_tipePembayaran'), findTestData(excelPathPengaturanDokumen).getValue(
            GlobalVariable.NumofColm, rowExcel('$Tipe Pembayaran TTD')))

    'Input enter'
    WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_tipePembayaran'), Keys.chord(Keys.ENTER))

    'click label judul'
    WebUI.click(findTestObject('TandaTanganDokumen/label_Judul'))

    if ((GlobalVariable.NumofColm == 2) && (checked == 0)) {
        'get data tipe-tipe pembayaran secara asc'
        ArrayList resultVendorDDL = CustomKeywords.'connection.PengaturanDokumen.getDDLVendor'(conneSign)

        'check ddl psre'
        checkDDL(findTestObject('Object Repository/TandaTanganDokumen/select_Psre'), resultVendorDDL, ' pada DDL Psre ')

        checked = 1
    }
    
    'Input value Psre'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/select_Psre'), findTestData(excelPathPengaturanDokumen).getValue(
            GlobalVariable.NumofColm, rowExcel('Input Psre')))

    'Input enter'
    WebUI.sendKeys(findTestObject('Object Repository/TandaTanganDokumen/select_Psre'), Keys.chord(Keys.ENTER))

    'click label judul'
    WebUI.click(findTestObject('TandaTanganDokumen/label_Judul'))

    'Input value sequential sign'
    WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/select_SequentialSigning'), findTestData(excelPathPengaturanDokumen).getValue(
            GlobalVariable.NumofColm, rowExcel('Sequential Signing')))

    'Input enter'
    WebUI.sendKeys(findTestObject('Object Repository/TandaTanganDokumen/select_SequentialSigning'), Keys.chord(Keys.ENTER))

	if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Input Psre')).equalsIgnoreCase('Privy')) {
		WebUI.click(findTestObject('TandaTanganDokumen/select_QRSign'))
		
		WebUI.verifyElementText(findTestObject('TandaTanganDokumen/ddl_QRSign'), 'Ya', FailureHandling.CONTINUE_ON_FAILURE)
	} else {
		'Input value QRSign'
		WebUI.setText(findTestObject('Object Repository/TandaTanganDokumen/select_QRSign'), findTestData(excelPathPengaturanDokumen).getValue(
			GlobalVariable.NumofColm, rowExcel('QR Lacak Ttd')))

		'Input enter'
		WebUI.sendKeys(findTestObject('Object Repository/TandaTanganDokumen/select_QRSign'), Keys.chord(Keys.ENTER))
	}
	
    'click label judul'
    WebUI.click(findTestObject('TandaTanganDokumen/label_Judul'))

    'Input value status'
    WebUI.setText(findTestObject('TandaTanganDokumen/input_Status'), findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 
            rowExcel('Status Active')))

    'Input enter'
    WebUI.sendKeys(findTestObject('TandaTanganDokumen/input_Status'), Keys.chord(Keys.ENTER))

    'click label judul'
    WebUI.click(findTestObject('TandaTanganDokumen/label_Judul'))

    'Jika panjang dokumen lebih besar dari 0'
    if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('$Dokumen')).length() > 0) {
        'Code untuk mengambil file berdasarkan direktori masing-masing sekaligus ambil value dari excel'
        String userDir = System.getProperty('user.dir')

        String filePath = userDir + findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel(
                '$Dokumen'))

        'Upload file berdasarkan filePath yang telah dirancang'
        WebUI.uploadFile(findTestObject('TandaTanganDokumen/input_documentExample'), filePath, FailureHandling.CONTINUE_ON_FAILURE)
    }
}

def inputSignBox() {
    'Pembuatan variable mengenai jumlah delete, jumlah lock, dan indexlock untuk loop kedepannya'
    RoleTandaTangan = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('RoleTandaTangan')).split(
        semicolon, splitIndex)

    TipeTandaTangan = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('TipeTandaTangan')).split(
        semicolon, splitIndex)

    SignBoxAction = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Pindahkan SignBox')).split(
        semicolon, splitIndex)

    SignBoxLocation = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Lokasi Pemindahan signbox')).split(
        '\\n', splitIndex)

    LockSignBox = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('Lock Sign Box')).split(
        semicolon, splitIndex)

    'Klik button tanda tangan'
    WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ttd'))

    'Klik set tanda tangan'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_setTandaTangan'))

    'click button lock signbox'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_LockSignBox'))

    isLocked = WebUI.getAttribute(findTestObject('TandaTanganDokumen/btn_LockSignBox'), 'class', FailureHandling.STOP_ON_FAILURE)

    'verify sign box is locked'
    checkVerifyEqualorMatch(WebUI.verifyMatch(isLocked, 'fa fa-2x fa-lock', false, FailureHandling.CONTINUE_ON_FAILURE), 
        ' pada sign Box ')

    'Klik button Delete'
    WebUI.click(findTestObject('TandaTanganDokumen/btn_DeleteSignBox'))

    'verify sign box is deleted'
    checkVerifyEqualorMatch(WebUI.verifyElementNotPresent(findTestObject('TandaTanganDokumen/signBox'), GlobalVariable.TimeOut, 
            FailureHandling.CONTINUE_ON_FAILURE), ' pada deletenya sign Box ')

    if (findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, rowExcel('RoleTandaTangan')).length() > 
    0) {
        'looping berdasarkan total tanda tangan'
        for (int j = 1; j <= RoleTandaTangan.size(); j++) {
            if ((TipeTandaTangan[(j - 1)]).equalsIgnoreCase('TTD')) {
                'Klik button tanda tangan'
                WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ttd'))
            } else if ((TipeTandaTangan[(j - 1)]).equalsIgnoreCase('Paraf')) {
                'Klik button tanda tangan'
                WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_paraf'))
            } else if ((TipeTandaTangan[(j - 1)]).equalsIgnoreCase('Meterai')) {
                'Klik button tanda tangan'
                WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_materai'))
            }
            
            'modify label tipe tanda tangan di kotak'
            modifyobjectTTDlblRoleTandaTangan = WebUI.modifyObjectProperty(findTestObject('Object Repository/TandaTanganDokumen/lbl_TTDTipeTandaTangan'), 
                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                j) + ']/div/div/small', true)

            if ((TipeTandaTangan[(j - 1)]).equalsIgnoreCase('TTD') || (TipeTandaTangan[(j - 1)]).equalsIgnoreCase('Paraf')) {
                'Verify label tanda tangannya muncul atau tidak'
                WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_TipeTandaTangan'), GlobalVariable.TimeOut, 
                    FailureHandling.CONTINUE_ON_FAILURE)

                'Memilih tipe signer apa berdasarkan excel'
                WebUI.selectOptionByLabel(findTestObject('TandaTanganDokumen/ddl_TipeTandaTangan'), RoleTandaTangan[(j - 
                    1)], false)

                'Klik set tanda tangan'
                WebUI.click(findTestObject('TandaTanganDokumen/btn_setTandaTangan'))

                'Verifikasi antara excel dan UI, apakah tipenya sama'
                WebUI.verifyMatch(RoleTandaTangan[(j - 1)], WebUI.getText(modifyobjectTTDlblRoleTandaTangan), false)
            }
            
            'Verify apakah tanda tangannya ada'
            if (WebUI.verifyElementPresent(modifyobjectTTDlblRoleTandaTangan, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
                'check if signbox mau dipindahkan'
                if ((SignBoxAction[(j - 1)]).equalsIgnoreCase('Yes')) {
                    'memindahkan sign box'
                    CustomKeywords.'customizekeyword.JSExecutor.jsExecutionFunction'('arguments[0].style.transform = arguments[1]', 
                        ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                        j) + ']/div', SignBoxLocation[(j - 1)])
                }
                
                'check if signbox mau dilock posisinya'
                if ((LockSignBox[(j - 1)]).equalsIgnoreCase('Yes')) {
                    'modify obejct lock signbox'
                    modifyobjectLockSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/btn_LockSignBox'), 
                        'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
                        j) + ']/div/button[1]/span', true)

                    'click lock signbox'
                    WebUI.click(modifyobjectLockSignBox)
                }
            }
        }
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

