import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = []

'declare arrayindex'
arrayindex = 0

if(GlobalVariable.Psre == 'VIDA') {	
	'get data buat undangan dari DB'
	ArrayList<String> resultDataDiri = CustomKeywords.'connection.Registrasi.buatUndanganStoreDB'(conneSign, findTestData(excelPathBuatUndangan).getValue(
	        GlobalVariable.NumofColm, 15).toUpperCase())
	
	'get data perusahaan buat undangan dari DB'
	ArrayList<String> resultDataPerusahaan = CustomKeywords.'connection.Registrasi.getBuatUndanganDataPerusahaanStoreDB'(conneSign, 
	    findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 15).toUpperCase())
	
	'verify nama'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 10).toUpperCase(), 
	        (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify tempat lahir'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 11).toUpperCase(), 
	        (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify tanggal lahir'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 12).toUpperCase(), 
	        (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify jenis kelamin'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 13).toUpperCase(), 
	        (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify email'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 15).toUpperCase(), 
	        (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify provinsi'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 18).toUpperCase(), 
	        (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify kota'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 19).toUpperCase(), 
	        (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify kecamatan'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 20).toUpperCase(), 
	        (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify kelurahan'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 21).toUpperCase(), 
	        (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify kode pos'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 22).toUpperCase(), 
	        (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'declare arrayindex'
	arrayindex = 0
	
	'verify wilayah'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 23).toUpperCase(), 
	        (resultDataPerusahaan[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify office'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 25).toUpperCase(), 
	        (resultDataPerusahaan[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify lini bisnis'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 26).toUpperCase(), 
	        (resultDataPerusahaan[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify Taskno'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 27).toUpperCase(), 
	        (resultDataPerusahaan[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
} else if (GlobalVariable.Psre == 'PRIVY') {
	'looping untuk delay 100detik menunggu proses request status'
	for(delay = 1; delay <= 5; delay++) {
		'get data status request dari tr_job_check_register_status DB'
		ArrayList<String> result = CustomKeywords.'connection.Registrasi.getRegisterPrivyStoreDB'(conneSign, findTestData(excelPathBuatUndangan).getValue(
				GlobalVariable.NumofColm, 15).toUpperCase())
		
		'verify request status'
		arrayMatch.add(WebUI.verifyMatch('1', (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'verify is external'
		arrayMatch.add(WebUI.verifyMatch('1', (result[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))
		
		
		if (arrayMatch.contains(false)) {
			'jika sudah delay ke 5 maka dianggap failed'
			if(delay == 5) {
				'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Registrasi', GlobalVariable.NumofColm,
					GlobalVariable.StatusFailed, (findTestData(excelPathAPIRegistrasi).getValue(GlobalVariable.NumofColm,
						2) + ';') + GlobalVariable.ReasonFailedStoredDB + ' Karena Job Tidak Jalan')
			}
			'delay 20detik'
			WebUI.delay(20)
		} else {
			break
		}
	}
}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('BuatUndangan', GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (findTestData(excelPathBuatUndangan).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}

