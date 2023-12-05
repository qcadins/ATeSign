import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import java.nio.charset.StandardCharsets
import java.sql.Connection as Connection
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

String selfPhoto, idPhoto

		if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 'Yes') {
			selfPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathRegister).getValue(
					GlobalVariable.NumofColm, rowExcel('selfPhoto')))) + '"')
		} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 SelfPhoto')) == 'No') {
			selfPhoto = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('selfPhoto'))
		}
		
		if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 'Yes') {
			idPhoto = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(excelPathRegister).getValue(
					GlobalVariable.NumofColm, rowExcel('idPhoto')))) + '"')
		} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 IdPhoto')) == 'No') {
			idPhoto = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('idPhoto'))
		}
        
		String value
		
		if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Input correct Message')).equalsIgnoreCase('Yes')) {
			if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() > 2) {
				receiverDetail = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', '')
			} else {
				receiverDetail = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No Telepon')).replace('"','')
			}
			
			'get invitationcode dari DB > encrypt invitation code > encode invitation code yang sudah di encrypt'
			value = encodeValue(receiverDetail , conneSign)
		} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Input correct Message')).equalsIgnoreCase('No')) {
			value = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Message'))
		}
		
		println(value)
		
        'Pembuatan pengisian variable di sendRequest per column berdasarkan data excel.'
        ArrayList listInvitation = []

        'Declare variable untuk sendRequest'
        (listInvitation[0]) = ((((((((((((((((((((((((((('{"email" :') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Email'))) + ',"nama" :') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('$Nama'))) + ',"tlp": ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
                'No Telepon'))) + ',"jenisKelamin" : ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Jenis Kelamin'))) + ',"tmpLahir" : ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Tempat Lahir'))) + ',"tglLahir" : ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Tanggal Lahir'))) + ',"idKtp" : ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('$NIK'))) + ', "provinsi" : ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Provinsi'))) + ', "kota" : ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Kota'))) + ', "kecamatan" : ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Kecamatan'))) + ',"kelurahan": ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Kelurahan'))) + ',"kodePos" : ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Kode Pos'))) + ',"alamat" : ') + findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
            rowExcel('Alamat'))) + ',"selfPhoto" : ' + selfPhoto + ',"idPhoto" : ' + idPhoto + '}  ')

        'HIT API'
        respon = WS.sendRequest(findTestObject('Postman/Register Invitation', [('msg') : '"' + value + '"', ('userData') : listInvitation[0], 
			('callerId') : findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'mengambil status code berdasarkan response HIT API'
            status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika status codenya 0'
            if (status_Code == 0) {
				
				'call testcase form aktivasi vida'
				WebUI.callTestCase(findTestCase('Main Register/FormAktivasiEsign'), [('excelPathRegister') : excelPathRegister],
					FailureHandling.CONTINUE_ON_FAILURE)

				'looping untuk mengeck apakah case selanjutnya ingin melanjutkan input pada form aktivasi'
				while (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Continue Register & Activation')).equalsIgnoreCase(
					'Continue') && GlobalVariable.FlagFailed > 0) {
					(GlobalVariable.NumofColm)++

					GlobalVariable.FlagFailed = 0

					'call testcase form aktivasi vida'
					WebUI.callTestCase(findTestCase('Main Register/FormAktivasiEsign'), [('excelPathRegister') : excelPathRegister],
						FailureHandling.CONTINUE_ON_FAILURE)
				}
            } else {
                'call function get API error message'
                getAPIErrorMessage(respon)
            }
        } else {
            'write to excel status failed dan reason : '
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + ';') + GlobalVariable.ReasonFailedHitAPI)
			
			GlobalVariable.FlagFailed = 1
        }


def getAPIErrorMessage(def respon) {
    'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
    messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'write to excel status failed dan reason : '
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
            '') + ';') + '<') + messageFailed) + '>')
	
	GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def encodeValue(String value, Connection conneSign) {
	'get invitation code dari db'
	String invCode = CustomKeywords.'connection.APIFullService.getInvitationCode'(conneSign, value)

	'Mengambil aes key based on tenant tersebut'
	String aesKey = CustomKeywords.'connection.APIFullService.getAesKeyEncryptUrl'(conneSign)
	
	'encrypt invitation code'
	String encryptCode = CustomKeywords.'customizekeyword.ParseText.parseEncrypt'(invCode, aesKey)

	try {
		return URLEncoder.encode(encryptCode, StandardCharsets.UTF_8.toString());
	} catch (UnsupportedEncodingException ex) {
		throw new RuntimeException(ex.getCause());
	}
}