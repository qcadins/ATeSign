import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'Pembuatan pengisian variable di sendRequest per column berdasarkan data excel.'
ArrayList listInvitation = []

'Declare variable untuk sendRequest'
(listInvitation[0]) = (((((((((((((((((((((((((('{"email" : "' + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
    rowExcel('$Email'))) + '" ,"nama" : "') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
        '$Nama'))) + '" ,"tlp": "') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No telepon'))) + 
'" ,"jenisKelamin" : "') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Jenis Kelamin'))) + 
'" ,"tmpLahir" : "') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Tempat Lahir'))) + '" ,"tglLahir" : "') + 
findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Tanggal Lahir'))) + '" ,"idKtp" : "') + findTestData(
    excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$NIK'))) + '" , "provinsi" : "') + findTestData(excelPathRegister).getValue(
    GlobalVariable.NumofColm, rowExcel('Provinsi'))) + '" , "kota" : "') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
    rowExcel('Kota'))) + '" , "kecamatan" : "') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
        'Kecamatan'))) + '" ,"kelurahan": "') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
        'Kelurahan'))) + '" ,"kodePos" : "') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
        'Kode Pos'))) + '" ,"alamat" : "') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
        'Alamat'))) + '" }')

'HIT API'
respon = WS.sendRequest(findTestObject('Postman/Gen Invitation Link', [('callerId') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('callerId')), ('users') : listInvitation[0], ('businessLineCode') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('BusinessLineCode')), ('businessLineName') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('BusinessLineName')), ('officeCode') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('OfficeCode')), ('officeName') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('OfficeName')), ('regionCode') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('RegionCode')), ('regionName') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('RegionName')), ('referenceNo') : findTestData(excelPathRegister).getValue(
                GlobalVariable.NumofColm, rowExcel('Ref No'))]))

'Jika status HIT API 200 OK'
if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
    'mengambil status code berdasarkan response HIT API'
    statusCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

	'ambil lama waktu yang diperlukan hingga request menerima balikan'
	elapsedTime = (respon.elapsedTime / 1000) + ' second'
	
	'ambil body dari hasil respons'
	responseBody = respon.responseBodyContent
	
	'panggil keyword untuk proses beautify dari respon json yang didapat'
	CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(excelPathRegister).getValue(
			GlobalVariable.NumofColm, rowExcel('Scenario')))
	
	'write to excel response elapsed time'
	CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
		1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
	
    'jika status codenya 0'
    if (statusCode == 0) {
        'Mengambil links berdasarkan response HIT API'
        GlobalVariable.Link = WS.getElementPropertyValue(respon, 'links', FailureHandling.OPTIONAL).toString().replace('[', 
            '').replace(']', '')

        'write to excel generated link'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Link Invitation') - 
            1, GlobalVariable.NumofColm - 1, GlobalVariable.Link)

        if (GlobalVariable.Link == '') {
            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                'Generate Link Null / Kosong')

            GlobalVariable.FlagFailed = 1
        }
        
        if (WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL) == 'User sudah terdaftar') {
            'cek ke excel bahwa data user sudah diregist otomatis ke tenant lain'
            result = CustomKeywords.'connection.Registrasi.checkAddUserOtherTenant'(conneSign, findTestData(excelPathRegister).getValue(
                    GlobalVariable.NumofColm, rowExcel('No Telepon')).replace('"', ''))

            if (result == 0) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                    'Error User di Tenant baru tidak berhasil ditambahkan')

                GlobalVariable.FlagFailed = 1
            } else if (result > 1) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
                    'Terdapat 2 user yang sama di tenant ') + GlobalVariable.Tenant)

                GlobalVariable.FlagFailed = 1
            } else {
                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)
            }
        }
        
        if (GlobalVariable.FlagFailed == 0) {
            'write to excel success'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                1, GlobalVariable.StatusSuccess)

            if (GlobalVariable.checkStoreDB == 'Yes') {
                'call test case ResponseAPIStoreDB'
                WebUI.callTestCase(findTestCase('Main Register/APIGenInvLinkStoreDB'), [('excelPathRegister') : 'Registrasi/MainRegister'
                        , ('sheet') : 'Main Register'], FailureHandling.CONTINUE_ON_FAILURE)
            }
            
            if ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS GenInv')) ==
				'1' || findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send WA GenInv')) == 
				'1') && (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() <= 
				2)) {
                'check jika Must use WA message = 1'
                if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First')) == 
                '1') {
                    usedSaldo = 'WhatsApp Message'
                } else {
                    'check jika email service on'
                    if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')) == 
                    '1') {
                        'check jika use WA message = 1'
                        if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message')) == 
                        '1') {
                            usedSaldo = 'WhatsApp Message'
                        } else {
                            'jika use WA message bukan 1 maka use SMS Notif'
                            usedSaldo = 'SMS'
                        }
                    } else {
                        'jika use WA message bukan 1 maka use SMS Notif'
                        usedSaldo = 'SMS'
                    }
                }
                
                'check potong saldo sms notif / WA untuk kirim link undangan'
                if (!WebUI.verifyMatch(CustomKeywords.'connection.APIFullService.getSaldoTrx'(conneSign, 
                		findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('callerId')).replace(
                				'"', ''), usedSaldo), '-1', false, FailureHandling.CONTINUE_ON_FAILURE)) {
                	'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
                	CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
                			((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) +
                			' Saldo ' + usedSaldo + ' tidak terpotong untuk kirim link undangan')
                }
            }
        }
    } else {
        'call function get API error message'
        getAPIErrorMessage(respon)
    }
} else {
    'call function get API error message'
        getAPIErrorMessage(respon)
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

def getAPIErrorMessage(ResponseObject respon) {
    'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
    messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'write to excel status failed dan reason : '
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
        ';') + '<') + messageFailed) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

