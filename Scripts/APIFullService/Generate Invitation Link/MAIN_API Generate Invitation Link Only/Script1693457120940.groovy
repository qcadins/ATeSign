import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPIGenerateInvLink).columnNumbers

String selfPhoto, idPhoto

'looping API Generate Invitation Link'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
		
		'setting psre per case'
		GlobalVariable.Psre = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Psre Login'))
		
		'setting menggunakan base url yang benar atau salah'
		CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPIGenerateInvLink, GlobalVariable.NumofColm, rowExcel('Use Correct base Url'))
		
		GlobalVariable.FlagFailed = 0
		
		'check ada value maka setting email service tenant'
		if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')).length() > 0) {
			'setting email service tenant'
			CustomKeywords.'connection.APIFullService.settingEmailServiceTenant'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')))
		}
		
		'check ada value maka setting allow regenerate link'
		if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Allow Regenarate Link')).length() > 0) {
			'setting allow regenerate link'
			CustomKeywords.'connection.APIFullService.settingAllowRegenerateLink'(conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Setting Allow Regenarate Link')))
		}
		
		'check if tidak mau menggunakan tenant code yang benar'
		if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
			'set tenant kosong'
			GlobalVariable.Tenant = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
		} else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'Yes') {
			GlobalVariable.Tenant = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))
		}
		
		'check if mau menggunakan api_key yang salah atau benar'
		if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'Yes') {
			'get api key dari db'
			GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
		} else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'No') {
			'get api key salah dari excel'
			GlobalVariable.api_key = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
		}
		
		'check if self photo mau menggunakan base64 yang salah atau benar'
		if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 'Yes') {
			selfPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIGenerateInvLink).getValue(
					GlobalVariable.NumofColm, rowExcel('selfPhoto')))) + '"')
		} else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 'No') {
			selfPhoto = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('selfPhoto'))
		}
		
		'check if id photo mau menggunakan base64 yang salah atau benar'
		if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 idPhoto')) == 'Yes') {
			idPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathAPIGenerateInvLink).getValue(
					GlobalVariable.NumofColm, rowExcel('idPhoto')))) + '"')
		} else if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 idPhoto')) == 'No') {
			idPhoto = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('idPhoto'))
		}
		
		'HIT API'
		respon = WS.sendRequest(findTestObject('APIFullService/Postman/Generate Invitation Link', [('nama') : findTestData(
						excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('nama')), ('email') : findTestData(excelPathAPIGenerateInvLink).getValue(
						GlobalVariable.NumofColm, rowExcel('email')), ('tmpLahir') : findTestData(excelPathAPIGenerateInvLink).getValue(
						GlobalVariable.NumofColm, rowExcel('tmpLahir')), ('tglLahir') : findTestData(excelPathAPIGenerateInvLink).getValue(
						GlobalVariable.NumofColm, rowExcel('tglLahir')), ('jenisKelamin') : findTestData(excelPathAPIGenerateInvLink).getValue(
						GlobalVariable.NumofColm, rowExcel('jenisKelamin')), ('tlp') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm,
						rowExcel('tlp')), ('idKtp') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('idKtp'))
					, ('alamat') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('alamat')), ('kecamatan') : findTestData(
						excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('kecamatan')), ('kelurahan') : findTestData(
						excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('kelurahan')), ('kota') : findTestData(excelPathAPIGenerateInvLink).getValue(
						GlobalVariable.NumofColm, rowExcel('kota')), ('provinsi') : findTestData(excelPathAPIGenerateInvLink).getValue(
						GlobalVariable.NumofColm, rowExcel('provinsi')), ('kodePos') : findTestData(excelPathAPIGenerateInvLink).getValue(
						GlobalVariable.NumofColm, rowExcel('kodePos')), ('selfPhoto') : selfPhoto, ('idPhoto') : idPhoto, ('region') : findTestData(
						excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('region')), ('type') : findTestData(excelPathAPIGenerateInvLink).getValue(
						GlobalVariable.NumofColm, rowExcel('type')), ('office') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm,
						rowExcel('office')), ('businessLine') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm,
						rowExcel('businessLine')), ('taskNo') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('taskNo'))
					, ('callerId') : findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))]))
		
        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            if (code == 0) {
                'mengambil response'
                GlobalVariable.Link = WS.getElementPropertyValue(respon, 'link', FailureHandling.OPTIONAL)

                'write to excel generated link'
	            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Generate Invitation Link', 
	                5, GlobalVariable.NumofColm - 1, GlobalVariable.Link)

				'write to excel Success'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Generate Invitation Link',
					0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
				
                if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
                    'call test case ResponseAPIStoreDB'
                    WebUI.callTestCase(findTestCase('APIFullService/Generate Invitation Link/APIGenInvLinkStoreDB'), [('excelPathGenInvLink') : 'APIFullService/API_GenInvLink'], 
						FailureHandling.CONTINUE_ON_FAILURE)
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, '<' + message + '>')
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Generate Invitation Link', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, '<' + message + '>')
        }
    }
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}