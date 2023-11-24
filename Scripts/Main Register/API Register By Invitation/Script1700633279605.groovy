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
			'get invitationcode dari DB > encrypt invitation code > encode invitation code yang sudah di encrypt'
			value = encodeValue(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"','') , conneSign)
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
				'declare arraylist arraymatch'
				ArrayList<String> arrayMatch = []
				
//                if ((GlobalVariable.checkStoreDB == 'Yes') && (GlobalVariable.FlagFailed == 0)) {
//                	'declare arrayindex'
//					arrayindex = 0
//					
//					'get data buat undangan dari DB'
//					ArrayList<String> resultDataDiri = CustomKeywords.'connection.Registrasi.buatUndanganStoreDB'(conneSign, findTestData(excelPathRegister).getValue(
//							GlobalVariable.NumofColm, rowExcel('$Email')).replace('"','').toUpperCase())
//					
//					'verify nama'
//					arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Nama')).replace('"','').toUpperCase(),
//							(resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
//					
//					'verify tempat lahir'
//					arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Tempat Lahir')).replace('"','').toUpperCase(),
//							(resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
//					
//					'parse Date from MM/dd/yyyy > yyyy-MM-dd'
//					sDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(resultDataDiri[arrayindex++],
//						'MM/dd/yyyy', 'yyyy-MM-dd')
//					
//					'verify tanggal lahir'
//					arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Tanggal Lahir')).replace('"',''),
//							sDate, false, FailureHandling.CONTINUE_ON_FAILURE))
//					
//					'verify jenis kelamin'
//					arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('jenisKelamin')).replace('"','').toUpperCase(),
//							(resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
//					
//					'verify email'
//					arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"','').toUpperCase(),
//							(resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
//					
//					'verify provinsi'
//					arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('provinsi')).replace('"','').toUpperCase(),
//							(resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
//					
//					'verify kota'
//					arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('kota')).replace('"','').toUpperCase(),
//							(resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
//					
//					'verify kecamatan'
//					arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('kecamatan')).replace('"','').toUpperCase(),
//							(resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
//					
//					'verify kelurahan'
//					arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('kelurahan')).replace('"','').toUpperCase(),
//							(resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
//					
//					'verify kode pos'
//					arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('kodePos')).replace('"','').toUpperCase(),
//							(resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
//                }
//				
//				'jika data db tidak sesuai dengan excel'
//				if (arrayMatch.contains(false)) {
//					'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
//					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(SheetName, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
//						(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedStoredDB)
//				} else {
//					'write to excel success'
//					CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm -
//						1, GlobalVariable.StatusSuccess)
//				}
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