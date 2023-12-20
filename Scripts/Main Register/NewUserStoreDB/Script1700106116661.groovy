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
	'check if email kosong atau tidak'
	if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase('Edit') &&
		findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Invite By')).equalsIgnoreCase('Email')) {
		'get email dari row edit'
		email = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Email - Edit')).replace('"', '')
	} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).length() > 2 &&
		!findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase('Edit')) {
		'get email dari row input'
		email = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', '')
	} else {
		'get name + email hosting'
		email = findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Nama')).replace('"', '') + CustomKeywords.'connection.DataVerif.getEmailHosting'(conneSign)
	}
	
	ArrayList<String> resultDataPerusahaan, resultDataDiri
	
    'get data buat undangan dari DB'
    resultDataDiri = CustomKeywords.'connection.Registrasi.buatUndanganStoreDB'(conneSign, email.toUpperCase(), 
        findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No Telepon')).replace('"', ''))
	
	'check if email kosong atau tidak'
	if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase('Edit') &&
		findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Invite By')).equalsIgnoreCase('Email')) {
		'get data perusahaan buat undangan dari DB'
		resultDataPerusahaan = CustomKeywords.'connection.Registrasi.getBuatUndanganDataPerusahaanStoreDB'(
				conneSign, email.toUpperCase())		
	} else if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase('Edit') &&
		findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Invite By')).equalsIgnoreCase('SMS')) {
		'get data perusahaan buat undangan dari DB'
		resultDataPerusahaan = CustomKeywords.'connection.Registrasi.getBuatUndanganDataPerusahaanStoreDB'(
				conneSign, findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No Telepon - Edit')).replace(
						'"', '').toUpperCase())
	} else if (!findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase('Edit') &&
		findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', '').length() > 0) {
		'get data perusahaan buat undangan dari DB'
		resultDataPerusahaan = CustomKeywords.'connection.Registrasi.getBuatUndanganDataPerusahaanStoreDB'(
				conneSign, findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace(
						'"', '').toUpperCase())
	} else if (!findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')).equalsIgnoreCase('Edit') &&
		findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('$Email')).replace('"', '').length() == 0) {
		'get data perusahaan buat undangan dari DB'
		resultDataPerusahaan = CustomKeywords.'connection.Registrasi.getBuatUndanganDataPerusahaanStoreDB'(
				conneSign, findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('No Telepon')).replace(
						'"', '').toUpperCase())
	}

	if (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Inquiry Invitation Action')) == 'Edit') {
		'call function verify store db after edit'
		verifyStoreDBAfterEdit(arrayMatch, resultDataPerusahaan, resultDataDiri)
	} else {
		'call function verify store db'
		verifyStoreDB(arrayMatch, resultDataPerusahaan, resultDataDiri)		
	}

    'jika data db tidak sesuai dengan excel'
    if (arrayMatch.contains(false)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedStoredDB)
    }
} else if (GlobalVariable.Psre == 'PRIVY') {
    'looping untuk delay 100detik menunggu proses request status'
    for (delay = 1; delay <= 5; delay++) {
        'reset array index'
        arrayindex = 0

        'get data status request dari tr_job_check_register_status DB'
        ArrayList<String> result = CustomKeywords.'connection.Registrasi.getRegisterPrivyStoreDB'(conneSign, findTestData(
                excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('idKTP')).replace('"','').toUpperCase())

        'verify request status 0 karena belum terverifikasi'
        arrayMatch.add(WebUI.verifyMatch('2', result[arrayindex++], false, FailureHandling.OPTIONAL))

        'verify is external 0 karena melalui UI buatundangan'
        arrayMatch.add(WebUI.verifyMatch('0', result[arrayindex++], false, FailureHandling.OPTIONAL))

        if (arrayMatch.contains(false)) {
            'jika sudah delay ke 5 maka dianggap failed'
            if (delay == 5) {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, ((findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, 
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

def verifyStoreDB(ArrayList<String> arrayMatch, ArrayList<String> resultDataPerusahaan, ArrayList<String> resultDataDiri) {
	'verify nama'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'$Nama')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify tempat lahir'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Tempat Lahir')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	if(!findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel('Generate Link With')).equalsIgnoreCase(
            'Menu Buat Undangan')) {
		'parse Date from MM/dd/yyyy > yyyy-MM-dd'
		sDate = CustomKeywords.'customizekeyword.ParseDate.parseDateFormat'(resultDataDiri[arrayindex++], 'MM/dd/yyyy', 'yyyy-MM-dd')

		'verify tanggal lahir'
		arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
				'Tanggal Lahir')).replace('"', ''), sDate, false, FailureHandling.CONTINUE_ON_FAILURE))
	} else {
		'verify tanggal lahir'
		arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
				'Tanggal Lahir')).replace('"', ''), resultDataDiri[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))
	}
	

	'verify jenis kelamin'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Jenis Kelamin')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false,
			FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify email'
	arrayMatch.add(WebUI.verifyMatch(email.toUpperCase(), resultDataDiri[arrayindex++].toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify provinsi'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Provinsi')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify kota'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Kota')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify kecamatan'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Kecamatan')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify kelurahan'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Kelurahan')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify kode pos'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Kode Pos')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'declare arrayindex'
	arrayindex = 0

	'verify wilayah'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Wilayah')).replace('"', '').toUpperCase(), (resultDataPerusahaan[arrayindex++]).toUpperCase(), false,
			FailureHandling.CONTINUE_ON_FAILURE))

	'verify office'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Office')).replace('"', '').toUpperCase(), (resultDataPerusahaan[arrayindex++]).toUpperCase(), false,
			FailureHandling.CONTINUE_ON_FAILURE))

	'verify lini bisnis'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Lini Bisnis')).replace('"', '').toUpperCase(), (resultDataPerusahaan[arrayindex++]).toUpperCase(),
			false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify Taskno'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Task No')).replace('"', '').toUpperCase(), (resultDataPerusahaan[arrayindex++]).toUpperCase(), false,
			FailureHandling.CONTINUE_ON_FAILURE))
}

def verifyStoreDBAfterEdit(ArrayList<String> arrayMatch, ArrayList<String> resultDataPerusahaan, ArrayList<String> resultDataDiri) {
	'verify nama'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Nama - Edit')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify tempat lahir'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Tempat Lahir - Edit')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify tanggal lahir'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Tanggal Lahir - Edit')).replace('"', ''), resultDataDiri[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify jenis kelamin'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Jenis Kelamin - Edit')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false,
			FailureHandling.CONTINUE_ON_FAILURE))
	
	'verify email'
	arrayMatch.add(WebUI.verifyMatch(email.toUpperCase(), resultDataDiri[arrayindex++].toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify provinsi'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Provinsi - Edit')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify kota'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Kota - Edit')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify kecamatan'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Kecamatan - Edit')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify kelurahan'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Kelurahan - Edit')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify kode pos'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Kode Pos - Edit')).replace('"', '').toUpperCase(), (resultDataDiri[arrayindex++]).toUpperCase(), false, FailureHandling.CONTINUE_ON_FAILURE))

	'declare arrayindex'
	arrayindex = 0

	'verify wilayah'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Wilayah - Edit')).replace('"', '').toUpperCase(), (resultDataPerusahaan[arrayindex++]).toUpperCase(), false,
			FailureHandling.CONTINUE_ON_FAILURE))

	'verify office'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Office - Edit')).replace('"', '').toUpperCase(), (resultDataPerusahaan[arrayindex++]).toUpperCase(), false,
			FailureHandling.CONTINUE_ON_FAILURE))

	'verify lini bisnis'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Lini Bisnis - Edit')).replace('"', '').toUpperCase(), (resultDataPerusahaan[arrayindex++]).toUpperCase(),
			false, FailureHandling.CONTINUE_ON_FAILURE))

	'verify Taskno'
	arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathRegister).getValue(GlobalVariable.NumofColm, rowExcel(
					'Task No - Edit')).replace('"', '').toUpperCase(), (resultDataPerusahaan[arrayindex++]).toUpperCase(), false,
			FailureHandling.CONTINUE_ON_FAILURE))
}