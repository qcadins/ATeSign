import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'variable untuk keperluan split excel'
semicolon = ';'

delimiter = '\\|'

enter = '\\n'

sheet = 'API Send Document'

int splitnum = -1

'looping berdasarkan total kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(API_Excel_Path, GlobalVariable.NumofColm, rowExcel('Use Correct base Url'))
        
        GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$tenantCode'))

        CustomKeywords.'connection.UpdateData.updateDBSendDocLevelNotification'(conneSign, API_Excel_Path, sheet)

        'Inisialisasi ref No berdasarkan delimiter ;'
        refNo = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo'))

        'Inisialisasi document template code berdasarkan delimiter ;'
        documentTemplateCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$documentTemplateCode')).split(
            semicolon, splitnum)

        'Inisialisasi office Code berdasarkan delimiter ;'
        officeCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('officeCode')).split(semicolon, 
            splitnum)

        'Inisialisasi office name berdasarkan delimiter ;'
        officeName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('officeName')).split(semicolon, 
            splitnum)

        'Inisialisasi region code berdasarkan delimiter ;'
        regionCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('regionCode')).split(semicolon, 
            splitnum)

        'Inisialisasi region name berdasarkan delimiter ;'
        regionName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('regionName')).split(semicolon, 
            splitnum)

        'Inisialisasi business line code berdasarkan delimiter ;'
        businessLineCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('businessLineCode')).split(
            semicolon, splitnum)

        'Inisialisasi business line name berdasarkan delimiter ;'
        businessLineName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('businessLineName')).split(
            semicolon, splitnum)

        'Inisialisasi is sequence berdasarkan delimiter ;'
        isSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('isSequence')).split(semicolon, 
            splitnum)

        'Inisialisasi document file berdasarkan delimiter ;'
        documentFile = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('documentFile')).split(enter, 
            splitnum)

        'Inisialisasi psre Code berdasarkan delimiter ;'
        psreCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$psreCode')).split(semicolon, 
            splitnum)

        'Inisialisasi successUrl berdasarkan delimiter ;'
        successURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('successURL')).split(semicolon, 
            splitnum)

        'Inisialisasi psre Code berdasarkan delimiter ;'
        uploadURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('uploadURL')).split(semicolon, 
            splitnum)

        'split signer untuk doc1 dan signer untuk doc2'
        signAction = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$signAction')).split(enter, 
            splitnum)

        signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$signerType')).split(enter, 
            splitnum)

        signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('signSequence')).split(enter, 
            splitnum)

        alamat = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('alamat')).split(enter, splitnum)

        jenisKelamin = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('jenisKelamin')).split(enter, 
            splitnum)

        kecamatan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('kecamatan')).split(enter, 
            splitnum)

        kelurahan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('kelurahan')).split(enter, 
            splitnum)

        kodePos = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('kodePos')).split(enter, splitnum)

        kota = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('kota')).split(enter, splitnum)

        nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$nama')).split(enter, splitnum)

        tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$tlp')).split(enter, splitnum)

        tglLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('tglLahir')).split(enter, splitnum)

        provinsi = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('provinsi')).split(enter, splitnum)

        idKtp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$idKtp')).split(enter, splitnum)

        tmpLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('tmpLahir')).split(enter, splitnum)

        email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$email')).split(enter, splitnum)

        npwp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('npwp')).split(enter, splitnum)

        idPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('idPhoto')).split(enter, splitnum)

        signerSelfPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('signerSelfPhoto')).split(
            enter, splitnum)

        String stringRefno = ''

        String bodyAPI = ''

        'Looping berdasarkan total dari dokumen file ukuran'
        for (int o = 0; o < documentFile.size(); o++) {
            'split signer untuk doc1 dan signer untuk doc2'
            signActions = (signAction[o]).split(semicolon, splitnum)

            signerTypes = (signerType[o]).split(semicolon, splitnum)

            signSequences = (signSequence[o]).split(semicolon, splitnum)

            alamats = (alamat[o]).split(semicolon, splitnum)

            jenisKelamins = (jenisKelamin[o]).split(semicolon, splitnum)

            kecamatans = (kecamatan[o]).split(semicolon, splitnum)

            kelurahans = (kelurahan[o]).split(semicolon, splitnum)

            kodePoss = (kodePos[o]).split(semicolon, splitnum)

            kotas = (kota[o]).split(semicolon, splitnum)

            namas = (nama[o]).split(semicolon, splitnum)

            tlps = (tlp[o]).split(semicolon, splitnum)

            tglLahirs = (tglLahir[o]).split(semicolon, splitnum)

            provinsis = (provinsi[o]).split(semicolon, splitnum)

            idKtps = (idKtp[o]).split(semicolon, splitnum)

            tmpLahirs = (tmpLahir[o]).split(semicolon, splitnum)

            emails = (email[o]).split(semicolon, splitnum)

            npwps = (npwp[o]).split(semicolon, splitnum)

            idPhotos = (idPhoto[o]).split(semicolon, splitnum)

            signerSelfPhotos = (signerSelfPhoto[o]).split(semicolon, splitnum)

            String listSigner

            listSigner = ''

            'Pembuatan pengisian variable di sendRequest per jumlah signer.'
            ArrayList<String> list = []

            for (int i = 0; i < signActions.size(); i++) {
                if ((i == 0) && (i == (signActions.size() - 1))) {
                    list.add(((((((((((((((((((((((((((((((((((((('{"signAction": "' + (signActions[i])) + '","signerType": "') + 
                        (signerTypes[i])) + '","signSequence":"') + (signSequences[i])) + '","alamat": "') + (alamats[i])) + 
                        '","jenisKelamin": "') + (jenisKelamins[i])) + '","kecamatan": "') + (kecamatans[i])) + '","kelurahan": "') + 
                        (kelurahans[i])) + '","kodePos": "') + (kodePoss[i])) + '","kota": "') + (kotas[i])) + '","nama": "') + 
                        (namas[i])) + '","tlp": "') + (tlps[i])) + '","tglLahir": "') + (tglLahirs[i])) + '","provinsi": "') + 
                        (provinsis[i])) + '","idKtp": "') + (idKtps[i])) + '","tmpLahir": "') + (tmpLahirs[i])) + '","email": "') + 
                        (emails[i])) + '","npwp": "') + (npwps[i])) + '","idPhoto": "') + (idPhotos[i])) + '","signerSelfPhoto": "') + 
                        (signerSelfPhotos[i])) + '"}')
                } else if (i == (signActions.size() - 1)) {
                    list.add(((((((((((((((((((((((((((((((((((((('{"signAction": "' + (signActions[i])) + '","signerType": "') + 
                        (signerTypes[i])) + '","signSequence":"') + (signSequences[i])) + '","alamat": "') + (alamats[i])) + 
                        '","jenisKelamin": "') + (jenisKelamins[i])) + '","kecamatan": "') + (kecamatans[i])) + '","kelurahan": "') + 
                        (kelurahans[i])) + '","kodePos": "') + (kodePoss[i])) + '","kota": "') + (kotas[i])) + '","nama": "') + 
                        (namas[i])) + '","tlp": "') + (tlps[i])) + '","tglLahir": "') + (tglLahirs[i])) + '","provinsi": "') + 
                        (provinsis[i])) + '","idKtp": "') + (idKtps[i])) + '","tmpLahir": "') + (tmpLahirs[i])) + '","email": "') + 
                        (emails[i])) + '","npwp": "') + (npwps[i])) + '","idPhoto": "') + (idPhotos[i])) + '","signerSelfPhoto": "') + 
                        (signerSelfPhotos[i])) + '"}')
                } else {
                    list.add(((((((((((((((((((((((((((((((((((((('{"signAction": "' + (signActions[i])) + '","signerType": "') + 
                        (signerTypes[i])) + '","signSequence":"') + (signSequences[i])) + '","alamat": "') + (alamats[i])) + 
                        '","jenisKelamin": "') + (jenisKelamins[i])) + '","kecamatan": "') + (kecamatans[i])) + '","kelurahan": "') + 
                        (kelurahans[i])) + '","kodePos": "') + (kodePoss[i])) + '","kota": "') + (kotas[i])) + '","nama": "') + 
                        (namas[i])) + '","tlp": "') + (tlps[i])) + '","tglLahir": "') + (tglLahirs[i])) + '","provinsi": "') + 
                        (provinsis[i])) + '","idKtp": "') + (idKtps[i])) + '","tmpLahir": "') + (tmpLahirs[i])) + '","email": "') + 
                        (emails[i])) + '","npwp": "') + (npwps[i])) + '","idPhoto": "') + (idPhotos[i])) + '","signerSelfPhoto": "') + 
                        (signerSelfPhotos[i])) + '"},')
                }
                
                'Memasukkan seluruh BodyAPI ke listSigner'
                listSigner = (listSigner + (list[i]))

                'check ada value maka setting email service tenant'
                if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')).length() > 
                0) {
                    for (loopingSignerEmailActive = 0; loopingSignerEmailActive < idKtps.size(); loopingSignerEmailActive++) {
                        SHA256IdNo = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'((idKtps[loopingSignerEmailActive]).replace(
                                '"', ''))

                        'setting email service tenant'
                        CustomKeywords.'connection.SendSign.settingEmailServiceVendorRegisteredUser'(conneSign, findTestData(
                                API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')), SHA256IdNo)
                    }
                }
            }
            
            if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 Document')) == 
            'Yes') {
                bodyAPI = (('"documentFile": "' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(documentFile[
                    o])) + '"')
            } else {
                bodyAPI = (('"documentFile": "' + (documentFile[o])) + '"')
            }
            
            if (o == (documentFile.size() - 1)) {
                stringRefno = (((((((((((((((((((((((((((((stringRefno + '{"referenceNo" : "') + refNo) + '", "documentTemplateCode": "') + 
                (documentTemplateCode[o])) + '", "officeCode": "') + (officeCode[o])) + '", "officeName": "') + (officeName[
                o])) + '", "regionCode": "') + (regionCode[o])) + '", "regionName": "') + (regionName[o])) + '", "businessLineCode": "') + 
                (businessLineCode[o])) + '", "businessLineName": "') + (businessLineName[o])) + '", "isSequence": "') + 
                (isSequence[o])) + '", "signer":[') + listSigner) + '],') + bodyAPI) + ', "psreCode" : "') + (psreCode[o])) + 
                '", "successURL": "') + (successURL[o])) + '", "uploadURL": "') + (uploadURL[o])) + '"}')
            } else {
                stringRefno = (((((((((((((((((((((((((((((stringRefno + '{"referenceNo" : "') + refNo) + '", "documentTemplateCode": "') + 
                (documentTemplateCode[o])) + '", "officeCode": "') + (officeCode[o])) + '", "officeName": "') + (officeName[
                o])) + '", "regionCode": "') + (regionCode[o])) + '", "regionName": "') + (regionName[o])) + '", "businessLineCode": "') + 
                (businessLineCode[o])) + '", "businessLineName": "') + (businessLineName[o])) + '", "isSequence": "') + 
                (isSequence[o])) + '", "signer":[') + listSigner) + '],') + bodyAPI) + ', "psreCode" : "') + (psreCode[o])) + 
                '", "successURL": "') + (successURL[o])) + '", "uploadURL": "') + (uploadURL[o])) + '"},')
            }
        }
        
        'Jika flag tenant no'
        if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
        } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 
        'Yes') {
            'Input tenant'
            GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$tenantCode'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        'Hit API'
        respon = WS.sendRequest(findTestObject('Postman/Send Document', [('tenantCode') : findTestData(API_Excel_Path).getValue(
                        GlobalVariable.NumofColm, rowExcel('$tenantCode')), ('request') : stringRefno, ('callerId') : findTestData(
                        API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))]))

        'jika response 200 / hit api berhasil'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'ambil respon text dalam bentuk code.'
            status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika status codenya 0, verifikasi datanya benar'
            if (status_Code == 0) {
                documentId = WS.getElementPropertyValue(respon, 'documentId', FailureHandling.OPTIONAL)

                'masih ada [ ] nya dalam documentid'
                GlobalVariable.Response = documentId

                'Write to excel mengenai Document ID'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('documentid') - 
                    1, GlobalVariable.NumofColm - 1, GlobalVariable.Response.toString().replace('[', '').replace(']', ''))

                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 
                    1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                if (GlobalVariable.checkStoreDB == 'Yes') {
                    'call test case ResponseAPIStoreDB'
                    WebUI.callTestCase(findTestCase('Send Document/ResponseAPIStoreDB'), [('API_Excel_Path') : API_Excel_Path
                            , ('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)
                }
                
                'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            getErrorMessageAPI(respon)
        }
		'jika tidak ada document id'
		if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).length() > 0) {
			'get check saldo wa or sms di send document'
			checkSaldoWAOrSMS(conneSign, findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$idKtp')))
		}
    }
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
        ';') + '<') + message) + '>')

    GlobalVariable.FlagFailed = 1
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
            reason)

        GlobalVariable.FlagFailed = 1
    }
}

def checkSaldoWAOrSMS(Connection conneSign, String emailSigner) {
    'inisialisasi arraylist balmut'
    ArrayList<String> balmut = []

    'inisialisasi penggunaan saldo, pemotongan saldo, dan increment untuk kebutuhan selanjutnya'
    int penggunaanSaldo = 0

    int pemotonganSaldo = 0

    int increment

    'inisialisasi tipesaldo'
    String tipeSaldo

    'get email per document menggunakan email signer yang diplit enter'
    ArrayList<String> emailPerDoc = emailSigner.split('\\n', -1)

    'looping per email per document'
    for (loopingEmailPerDoc = 0; loopingEmailPerDoc < emailPerDoc.size(); loopingEmailPerDoc++) {
        'split lagi per ;'
        ArrayList<String> email = (emailPerDoc[loopingEmailPerDoc]).split(';', -1)

        'looping email per '
        for (loopingEmail = 0; loopingEmail < email.size(); loopingEmail++) {
            'get email dari nik/notelp/email tersebut'
            (email[loopingEmail]) = CustomKeywords.'connection.DataVerif.getEmailFromNIK'(conneSign, CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(
                    email[loopingEmail]))

            'get full name user dari email tersebut'
            fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, email[loopingEmail])

            notifTypeDB = CustomKeywords.'connection.APIFullService.getWASMSFromNotificationType'(conneSign, email[loopingEmail], 
                'SEND_DOC', GlobalVariable.Tenant)

			if(notifTypeDB == 'Email') {
				continue	
			}
			
            if ((notifTypeDB == '0') || (notifTypeDB == 'Level Tenant')) {
                'get email service dari email tersebut'
                emailServiceOnVendor = CustomKeywords.'connection.DataVerif.getEmailServiceAsVendorUser'(conneSign, email[
                    loopingEmail])

                'get settinog must use wa first'
                mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

                'jika must use wa first'
                if (mustUseWAFirst == '1') {
                    'tipe saldonya menjadi wa'
                    tipeSaldo = 'WhatsApp Message'

                    'menggunakan saldo wa'
                    balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)
					
					checkingSaldo(balmut, tipeSaldo)
                } else {
                    'jika email servicenya 1'
                    if (emailServiceOnVendor == '1') {
                        'check use wa message'
                        useWAMessage = CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, GlobalVariable.Tenant)

                        'jika menggunakan wa'
                        if (useWAMessage == '1') {
                            tipeSaldo = 'WhatsApp Message'

                            'menggunakan saldo wa'
                            balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

							checkingSaldo(balmut, tipeSaldo)
                        } else if (useWAMessage == '0') {
                            'jika tidak menggunakan use wa message, maka mengarah ke sms'
                            'ke sms / wa'
                            SMSSetting = CustomKeywords.'connection.DataVerif.getSMSSetting'(conneSign, 'Send Document')

                            'jika setting sms = 1'
                            if (SMSSetting == '1') {
                                'ke sms'
                                tipeSaldo = 'SMS Notif'

                                'get balmut dari sms '
                                balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

								checkingSaldo(balmut, tipeSaldo)
                            }
                        }
                    }
                }
            } else {
                tipeSaldo = notifTypeDB

                'menggunakan saldo wa'
                balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

				checkingSaldo(balmut, tipeSaldo)
            }
        }
    }
}

def checkingSaldo(ArrayList balmut, String tipeSaldo) {
	'jika balmutnya tidak ada value'
	if (balmut.size() == 0) {
		'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, ((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel(
					'Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via ') +
			tipeSaldo)
	} else if ((balmut[9]) != '-1') {
		GlobalVariable.FlagFailed = 1

		'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
			GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel(
					'Reason Failed')) + ';') + 'Saldo ' + tipeSaldo + ' tidak terpotong')
	}
}
