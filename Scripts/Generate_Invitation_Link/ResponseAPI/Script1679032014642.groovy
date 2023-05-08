import internal.GlobalVariable as GlobalVariable
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.testobject.ObjectRepository as ObjectRepository
import com.ibm.media.bean.multiplayer.LinksArrayEditor as LinksArrayEditor
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webservice.verification.WSResponseManager as WSResponseManager
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import org.openqa.selenium.Keys as Keys
import java.sql.Connection as Connection
import groovy.json.JsonOutput as JsonOutput

'declare invitation link inquiry'
String invitation_link_inquiry = ''

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'get colm excel'
int countColmExcel = findTestData(API_Excel_Path).getColumnNumbers()

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		
		'Open test case untuk login sebagai Invenditor'
		WebUI.callTestCase(findTestCase('Login/Login_Inveditor'), [:], FailureHandling.STOP_ON_FAILURE)
		
        'Pembuatan pengisian variable di sendRequest per column berdasarkan data excel.'
        ArrayList<String> ListInvitation = new ArrayList<String>()

        'Declare variable untuk sendRequest'
        (ListInvitation[0]) = (((((((((((((((((((((((((('{"email" :' + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
            13)) + ',"nama" :') + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 14)) + ',"tlp": ') + findTestData(
            API_Excel_Path).getValue(GlobalVariable.NumofColm, 15)) + ',"jenisKelamin" : ') + findTestData(API_Excel_Path).getValue(
            GlobalVariable.NumofColm, 16)) + ',"tmpLahir" : ') + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
            17)) + ',"tglLahir" : ') + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 18)) + ',"idKtp" : ') + 
        findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 19)) + ', "provinsi" : ') + findTestData(API_Excel_Path).getValue(
            GlobalVariable.NumofColm, 20)) + ', "kota" : ') + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
            21)) + ', "kecamatan" : ') + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 22)) + ',"kelurahan": ') + 
        findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 23)) + ',"kodePos" : ') + findTestData(API_Excel_Path).getValue(
            GlobalVariable.NumofColm, 24)) + ',"alamat" : ') + findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
            25)) + '}  ')

        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/Gen Invitation Link', [('callerId') : findTestData(API_Excel_Path).getValue(
                        GlobalVariable.NumofColm, 9), ('tenantCode') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                        11), ('users') : ListInvitation[0]]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'mengambil status code berdasarkan response HIT API'
            status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika status codenya 0'
            if (status_Code == 0) {
                'Mengambil links berdasarkan response HIT API'
                links = WS.getElementPropertyValue(respon, 'links', FailureHandling.OPTIONAL)

                println((GlobalVariable.NumofColm + ' ') + links)

                'Klik menu Inquiry Invitation'
                WebUI.click(findTestObject('Object Repository/InquiryInvitation/menu_InquiryInvitation'))

                'Jika pencarian menggunakan email'
                if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('email')) {
                    'Set search box di Inquiry Invitation'
                    WebUI.setText(findTestObject('Object Repository/InquiryInvitation/input_SearchBox'), findTestData(API_Excel_Path).getValue(
                            GlobalVariable.NumofColm, 13).replace('"', ''))

                    'Jika pencarian menggunakan nomor telp'
                } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('phone')) {
                    'Set search box di Inquiry Invitation'
                    WebUI.setText(findTestObject('Object Repository/InquiryInvitation/input_SearchBox'), findTestData(API_Excel_Path).getValue(
                            GlobalVariable.NumofColm, 15).replace('"', ''))
                } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 7).equalsIgnoreCase('id no')) {
                    'Set search box di Inquiry Invitation'
                    WebUI.setText(findTestObject('Object Repository/InquiryInvitation/input_SearchBox'), findTestData(API_Excel_Path).getValue(
                            GlobalVariable.NumofColm, 19).replace('"', ''))
                }
                
                'Klik button Cari'
                WebUI.click(findTestObject('Object Repository/InquiryInvitation/button_Cari'), FailureHandling.CONTINUE_ON_FAILURE)

                if (WebUI.verifyElementPresent(findTestObject('Object Repository/InquiryInvitation/button_ViewLink'), 2, 
                    FailureHandling.CONTINUE_ON_FAILURE)) {
                    'Klik button View Link'
                    WebUI.click(findTestObject('Object Repository/InquiryInvitation/button_ViewLink'))

                    if (WebUI.verifyElementPresent(findTestObject('Object Repository/InquiryInvitation/errorLog'), GlobalVariable.TimeOut, 
                        FailureHandling.OPTIONAL)) {
                        errorLog = WebUI.getAttribute(findTestObject('Object Repository/InquiryInvitation/errorLog'), 'aria-label')

                        'write to excel status failed dan reason : errorLog UI'
                        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 
                                2).replace('-', '') + ';') + errorLog)
                    }
                    
                    'Klik pop up link'
                    WebUI.click(findTestObject('Object Repository/InquiryInvitation/input_Link'), FailureHandling.CONTINUE_ON_FAILURE)

                    'Mengambil value dari pop up'
                    invitation_link_inquiry = WebUI.getAttribute(findTestObject('Object Repository/InquiryInvitation/input_Link'), 
                        'value', FailureHandling.CONTINUE_ON_FAILURE)

                    'Button tutup'
                    WebUI.click(findTestObject('Object Repository/InquiryInvitation/button_TutupDapatLink'), FailureHandling.CONTINUE_ON_FAILURE)
                }
                
                'Klik menu Inquiry Invitation'
                WebUI.click(findTestObject('Object Repository/InquiryInvitation/menu_InquiryInvitation'))

                'Verify value pop up dan response HIT API'
                if (WebUI.verifyMatch(links.toString().replace('[', '').replace(']', ''), invitation_link_inquiry, false, 
                    FailureHandling.CONTINUE_ON_FAILURE)) {
                    'write to excel success'
                    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Generate Inv Link', 
                        0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
					
					if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
						
						'call test case ResponseAPIStoreDB'
						WebUI.callTestCase(findTestCase('Generate_Invitation_Link/ResponseAPIStoreDB'), [('API_Excel_Path') : 'Registrasi/Generate_Inv_Link'],
								FailureHandling.CONTINUE_ON_FAILURE)
					}
					
                    'store link di GV'
                    GlobalVariable.Link = links.toString().replace('[', '').replace(']', '')

                    'call test case daftar akun'
                    WebUI.callTestCase(findTestCase('Generate_Invitation_Link/DaftarAkunDataVerif'), [('excelPathGenerateLink') : 'Registrasi/Generate_Inv_Link'], 
                        FailureHandling.STOP_ON_FAILURE)
                } else {
                    'write to excel status failed dan reason : '
                    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                            '-', '') + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch)
                }
                
                'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
            } else {
                messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'write to excel status failed dan reason : '
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                        '-', '') + ';') + messageFailed)
            }
        } else {
            'write to excel status failed dan reason : '
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Generate Inv Link', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedHitAPI)
        }
		
		'close browser'
		WebUI.closeBrowser()
    }
}