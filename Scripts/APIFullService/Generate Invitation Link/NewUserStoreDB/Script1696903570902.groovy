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

if (GlobalVariable.Psre == 'VIDA') {
	ArrayList<String> resultDataPerusahaan, resultDataDiri
	
    'get data buat undangan dari DB'
    resultDataDiri = CustomKeywords.'connection.Registrasi.buatUndanganStoreDB'(conneSign, findTestData(
            excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', '').toUpperCase(), 
        findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('tlp')).replace('"', ''))
	
	'check if email kosong atau tidak'
	if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('email')).length() > 2) {
		'get data perusahaan buat undangan dari DB'
		resultDataPerusahaan = CustomKeywords.'connection.Registrasi.getBuatUndanganDataPerusahaanStoreDB'(
				conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace(
						'"', '').toUpperCase())		
	} else {
		'get data perusahaan buat undangan dari DB'
		resultDataPerusahaan = CustomKeywords.'connection.Registrasi.getBuatUndanganDataPerusahaanStoreDB'(
				conneSign, findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('tlp')).replace(
						'"', '').toUpperCase())
	}

    'verify nama'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'nama')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify tempat lahir'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'tmpLahir')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'parse Date from MM/dd/yyyy > yyyy-MM-dd'
    sDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(resultDataDiri[arrayindex++], 'MM/dd/yyyy', 'yyyy-MM-dd')

    'verify tanggal lahir'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'tglLahir')).replace('"', ''), sDate, false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify jenis kelamin'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'jenisKelamin')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, 
            FailureHandling.CONTINUE_ON_FAILURE))
	
	'check if email kosong atau tidak'
	if (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('email')).length() > 2) {
		email = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('email')).replace('"', '')
	} else {
		'get name + email hosting'
		email = findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('nama')).replace('"', '') + CustomKeywords.'connection.DataVerif.getEmailHosting'(conneSign)
	}
	
	'verify email'
	arrayMatch.add(WebUI.verifyMatch(email.toUpperCase(), resultDataDiri[arrayindex++].toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))		

    'verify provinsi'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'provinsi')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify kota'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'kota')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify kecamatan'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'kecamatan')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify kelurahan'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'kelurahan')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify kode pos'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'kodePos')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

    'declare arrayindex'
    arrayindex = 0

    'verify wilayah'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'region')).replace('"', '').toUpperCase(), (resultDataPerusahaan[arrayindex++]).toUpperCase(), false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'verify office'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'office')).replace('"', '').toUpperCase(), (resultDataPerusahaan[arrayindex++]).toUpperCase(), false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'verify lini bisnis'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'businessLine')).replace('"', '').toUpperCase(), (resultDataPerusahaan[arrayindex++]).toUpperCase(), 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify Taskno'
    arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel(
                    'taskNo')).replace('"', '').toUpperCase(), (resultDataPerusahaan[arrayindex++]).toUpperCase(), false, 
            FailureHandling.CONTINUE_ON_FAILURE))

    'jika data db tidak sesuai dengan excel'
    if (arrayMatch.contains(false)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedStoredDB)
    }
} else if (GlobalVariable.Psre == 'PRIVY') {
    'looping untuk delay 100detik menunggu proses request status'
    for (delay = 1; delay <= 5; delay++) {
        'reset array index'
        arrayindex = 0

        'get data status request dari tr_job_check_register_status DB'
        ArrayList<String> result = CustomKeywords.'connection.Registrasi.getRegisterPrivyStoreDB'(conneSign, findTestData(
                excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, rowExcel('idKTP')).replace('"','').toUpperCase())

        'verify request status 0 karena belum terverifikasi'
        arrayMatch.add(WebUI.verifyMatch('0', result[arrayindex++], false, FailureHandling.OPTIONAL))

        'verify is external 0 karena melalui UI buatundangan'
        arrayMatch.add(WebUI.verifyMatch('1', result[arrayindex++], false, FailureHandling.OPTIONAL))

        if (arrayMatch.contains(false)) {
            'jika sudah delay ke 5 maka dianggap failed'
            if (delay == 5) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, ((findTestData(excelPathAPIGenerateInvLink).getValue(GlobalVariable.NumofColm, 
                        2) + ';') + GlobalVariable.ReasonFailedStoredDB) + ' Karena Job Tidak Jalan')
            }
            
            'delay 20detik'
            WebUI.delay(20)
        } else {
            break
        }
    }
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

