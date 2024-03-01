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

'variable untuk keperluan split excel'
semicolon = ';'

delimiter = '\\|'

enter = '\\n'

int splitnum = -1

'setting menggunakan base url yang benar atau salah'
CustomKeywords.'connection.APIFullService.settingBaseUrl'(API_Excel_Path, GlobalVariable.NumofColm, rowExcel('Use Correct base Url'))

'Inisialisasi ref No berdasarkan delimiter ;'
refNo = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo'))

'Inisialisasi document template code berdasarkan delimiter ;'
documentTemplateCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$documentTemplateCode')).split(
    semicolon, splitnum)

'Inisialisasi office Code berdasarkan delimiter ;'
officeCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('officeCode')).split(semicolon, splitnum)

'Inisialisasi office name berdasarkan delimiter ;'
officeName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('officeName')).split(semicolon, splitnum)

'Inisialisasi region code berdasarkan delimiter ;'
regionCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('regionCode')).split(semicolon, splitnum)

'Inisialisasi region name berdasarkan delimiter ;'
regionName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('regionName')).split(semicolon, splitnum)

'Inisialisasi business line code berdasarkan delimiter ;'
businessLineCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('businessLineCode')).split(semicolon, 
    splitnum)

'Inisialisasi business line name berdasarkan delimiter ;'
businessLineName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('businessLineName')).split(semicolon, 
    splitnum)

'Inisialisasi is sequence berdasarkan delimiter ;'
isSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('isSequence')).split(semicolon, splitnum)

'Inisialisasi qr enable berdasarkan delimiter ;'
useSignQr = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('QR')).split(semicolon, splitnum)

'Inisialisasi document file berdasarkan delimiter ;'
documentFile = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('documentFile')).split(enter, splitnum)

'Inisialisasi psre Code berdasarkan delimiter ;'
psreCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('psreCode')).split(semicolon, splitnum)

'Inisialisasi successUrl berdasarkan delimiter ;'
successURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('successURL (Send Normal)')).split(
    semicolon, splitnum)

'Inisialisasi psre Code berdasarkan delimiter ;'
uploadURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('uploadURL (Send Normal)')).split(semicolon, 
    splitnum)

'split signer untuk doc1 dan signer untuk doc2'
signAction = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$signAction')).split(enter, splitnum)

signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$signerType')).split(enter, splitnum)

signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('signSequence (Send Normal)')).split(
    enter, splitnum)

alamat = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('alamat (Send Normal)')).split(enter, 
    splitnum)

jenisKelamin = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('jenisKelamin (Send Normal)')).split(
    enter, splitnum)

kecamatan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('kecamatan (Send Normal)')).split(enter, 
    splitnum)

kelurahan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('kelurahan (Send Normal)')).split(enter, 
    splitnum)

kodePos = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('kodePos (Send Normal)')).split(enter, 
    splitnum)

kota = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('kota (Send Normal)')).split(enter, splitnum)

nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$nama (Send Normal)')).split(enter, splitnum)

tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$tlp')).split(enter, splitnum)

tglLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('tglLahir (Send Normal)')).split(enter, 
    splitnum)

provinsi = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('provinsi (Send Normal)')).split(enter, 
    splitnum)

idKtp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$idKtp')).split(enter, splitnum)

tmpLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('tmpLahir (Send Normal)')).split(enter, 
    splitnum)

email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$email')).split(enter, splitnum)

npwp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('npwp (Send Normal)')).split(enter, splitnum)

idPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('idPhoto (Send Normal)')).split(enter, 
    splitnum)

signerSelfPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('signerSelfPhoto (Send Normal)')).split(
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
                (signerTypes[i])) + '","signSequence":"') + (signSequences[i])) + '","alamat": "') + (alamats[i])) + '","jenisKelamin": "') + 
                (jenisKelamins[i])) + '","kecamatan": "') + (kecamatans[i])) + '","kelurahan": "') + (kelurahans[i])) + 
                '","kodePos": "') + (kodePoss[i])) + '","kota": "') + (kotas[i])) + '","nama": "') + (namas[i])) + '","tlp": "') + 
                (tlps[i])) + '","tglLahir": "') + (tglLahirs[i])) + '","provinsi": "') + (provinsis[i])) + '","idKtp": "') + 
                (idKtps[i])) + '","tmpLahir": "') + (tmpLahirs[i])) + '","email": "') + (emails[i])) + '","npwp": "') + 
                (npwps[i])) + '","idPhoto": "') + (idPhotos[i])) + '","signerSelfPhoto": "') + (signerSelfPhotos[i])) + 
                '"}')
        } else if (i == (signActions.size() - 1)) {
            list.add(((((((((((((((((((((((((((((((((((((('{"signAction": "' + (signActions[i])) + '","signerType": "') + 
                (signerTypes[i])) + '","signSequence":"') + (signSequences[i])) + '","alamat": "') + (alamats[i])) + '","jenisKelamin": "') + 
                (jenisKelamins[i])) + '","kecamatan": "') + (kecamatans[i])) + '","kelurahan": "') + (kelurahans[i])) + 
                '","kodePos": "') + (kodePoss[i])) + '","kota": "') + (kotas[i])) + '","nama": "') + (namas[i])) + '","tlp": "') + 
                (tlps[i])) + '","tglLahir": "') + (tglLahirs[i])) + '","provinsi": "') + (provinsis[i])) + '","idKtp": "') + 
                (idKtps[i])) + '","tmpLahir": "') + (tmpLahirs[i])) + '","email": "') + (emails[i])) + '","npwp": "') + 
                (npwps[i])) + '","idPhoto": "') + (idPhotos[i])) + '","signerSelfPhoto": "') + (signerSelfPhotos[i])) + 
                '"}')
        } else {
            list.add(((((((((((((((((((((((((((((((((((((('{"signAction": "' + (signActions[i])) + '","signerType": "') + 
                (signerTypes[i])) + '","signSequence":"') + (signSequences[i])) + '","alamat": "') + (alamats[i])) + '","jenisKelamin": "') + 
                (jenisKelamins[i])) + '","kecamatan": "') + (kecamatans[i])) + '","kelurahan": "') + (kelurahans[i])) + 
                '","kodePos": "') + (kodePoss[i])) + '","kota": "') + (kotas[i])) + '","nama": "') + (namas[i])) + '","tlp": "') + 
                (tlps[i])) + '","tglLahir": "') + (tglLahirs[i])) + '","provinsi": "') + (provinsis[i])) + '","idKtp": "') + 
                (idKtps[i])) + '","tmpLahir": "') + (tmpLahirs[i])) + '","email": "') + (emails[i])) + '","npwp": "') + 
                (npwps[i])) + '","idPhoto": "') + (idPhotos[i])) + '","signerSelfPhoto": "') + (signerSelfPhotos[i])) + 
                '"},')
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
                CustomKeywords.'connection.SendSign.settingEmailServiceVendorRegisteredUser'(conneSign, findTestData(API_Excel_Path).getValue(
                        GlobalVariable.NumofColm, rowExcel('Setting Email Service')), SHA256IdNo)
            }
        }
    }
    
    if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 Document')) == 'Yes') {
        bodyAPI = (('"documentFile": "' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(documentFile[o])) + '"')
    } else {
        bodyAPI = (('"documentFile": "' + (documentFile[o])) + '"')
    }
    
    if (o == (documentFile.size() - 1)) {
        stringRefno = stringRefno + '{"referenceNo" : "' + refNo + '", "documentTemplateCode": "' + 
        documentTemplateCode[o] + '", "officeCode": "' + officeCode[o] + '", "officeName": "' + officeName[o] + 
        '", "regionCode": "' + regionCode[o] + '", "regionName": "' + regionName[o] + '", "businessLineCode": "' + 
        businessLineCode[o] + '", "businessLineName": "' + businessLineName[o] + '", "isSequence": "' + isSequence[
        o] + '" ,'
		
		if (useSignQr[o] != '') {
			 stringRefno = stringRefno + '"useSignQr" : "' + useSignQr[o] + '" ,'
		}
		
		stringRefno = stringRefno + '"signer":[' + listSigner + '],' + bodyAPI + ', "psreCode" : "' + 
        psreCode[o] + '", "successURL": "' + successURL[o] + '", "uploadURL": "' + uploadURL[o] + '"}'
    } else {
        stringRefno = stringRefno + '{"referenceNo" : "' + refNo + '", "documentTemplateCode": "' + 
        documentTemplateCode[o] + '", "officeCode": "' + officeCode[o] + '", "officeName": "' + officeName[o] + 
        '", "regionCode": "' + regionCode[o] + '", "regionName": "' + regionName[o] + '", "businessLineCode": "' + 
        businessLineCode[o] + '", "businessLineName": "' + businessLineName[o] + '", "isSequence": "' + isSequence[
        o] + '" ,'
		
		if (useSignQr[o] != '') {
			 stringRefno = stringRefno + '"useSignQr" : "' + useSignQr[o] + '" ,'
		}
		
		stringRefno = stringRefno + '"signer":[' + listSigner + '],' + bodyAPI + ', "psreCode" : "' + 
        psreCode[o] + '", "successURL": "' + successURL[o] + '", "uploadURL": "' + uploadURL[o] + '"},'
    }
}

'Jika flag tenant no'
if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
    'set tenant kosong'
    GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
} else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'Yes') {
    'Input tenant'
    GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))
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
                GlobalVariable.NumofColm, rowExcel('Tenant')), ('request') : stringRefno, ('callerId') : findTestData(API_Excel_Path).getValue(
                GlobalVariable.NumofColm, rowExcel('callerId'))]))

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
            WebUI.callTestCase(findTestCase('Main Flow - Copy/API Send Document Normal StoreDB'), [('API_Excel_Path') : API_Excel_Path
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
    'get checking saldo wa or sms'
    checkSaldoWAOrSMS(conneSign, findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$idKtp')).replace(
            '"', '')) /*
def inputFilterTrx(String documentType, String signType) {
		'get current date'
		currentDate = new Date().format('yyyy-MM-dd')
		
		'input filter dari saldo'
		WebUI.setText(findTestObject('Saldo/input_tipesaldo'), signType)
	
		'Input enter'
		WebUI.sendKeys(findTestObject('Saldo/input_tipesaldo'), Keys.chord(Keys.ENTER))
	
		'Input tipe transaksi'
		WebUI.setText(findTestObject('Saldo/input_tipetransaksi'), 'Use ' + signType)
	
		'Input enter'
		WebUI.sendKeys(findTestObject('Saldo/input_tipetransaksi'), Keys.chord(Keys.ENTER))
	
		'Input date sekarang'
		WebUI.setText(findTestObject('Saldo/input_fromdate'), currentDate)
	
	   // 'Input tipe dokumen'
		//WebUI.setText(findTestObject('Saldo/input_tipedokumen'), documentType)
	
	  //  'Input enter'
		//WebUI.sendKeys(findTestObject('Saldo/input_tipedokumen'), Keys.chord(Keys.ENTER))
	
		'Input referal number'
		WebUI.setText(findTestObject('Saldo/input_refnumber'), findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo')).replace('"',''))
	
	  //  'Input documentTemplateName'
	 //   WebUI.setText(findTestObject('Saldo/input_namadokumen'), documentTemplateName)
	
		'Input date sekarang'
		WebUI.setText(findTestObject('Saldo/input_todate'), currentDate)
	
		'Klik cari'
		WebUI.click(findTestObject('Saldo/btn_cari'))
}
def inputFilterSaldo(String tipeSaldo, Connection conneSign, int saldoDocAutosign) {
	documentType = CustomKeywords.'connection.APIFullService.getDocumentType'(conneSign, findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo')))
	
	inputFilterTrx(documentType, tipeSaldo)

	'get row'
	variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

	'modify object button last page'
	modifyObjectButtonLastPage = WebUI.modifyObjectProperty(findTestObject('RegisterEsign/checkSaldo/modifyObject'), 'xpath',
		'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-footer/div/datatable-pager/ul/li[' +
		variable.size()) + ']', true)

	if (WebUI.getAttribute(modifyObjectButtonLastPage, 'class', FailureHandling.OPTIONAL) != 'disabled') {
		'click button last page'
		WebUI.click(findTestObject('RegisterEsign/checkSaldo/button_LastPage'))
	}
	
	'get column di saldo'
	variableSaldoColumn = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-header > div > div.datatable-row-center.ng-star-inserted datatable-header-cell'))

	'get row di saldo'
	variableSaldoRow = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-balance > app-msx-paging > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))

	'get trx dari db'
	ArrayList result = CustomKeywords.'connection.APIFullService.gettrxSaldo'(conneSign, findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo')).replace('"',''), saldoDocAutosign.toString(), 'Use ' + tipeSaldo)

	index = 0
	
	'looping mengenai rownya'
	for (int j = 1; j <= variableSaldoRow.size(); j++) {
		'looping mengenai columnnya'
		for (int u = 1; u <= variableSaldoColumn.size(); u++) {
			'modify per row dan column. column menggunakan u dan row menggunakan documenttemplatename'
			modifyperrowpercolumn = WebUI.modifyObjectProperty(findTestObject('KotakMasuk/Sign/lbl_notrxsaldo'),
				'xpath', 'equals', ((('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-balance/app-msx-paging/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
				j) + ']/datatable-body-row/div[2]/datatable-body-cell[') + u) + ']/div', true)

			WebUI.scrollToElement(modifyperrowpercolumn, GlobalVariable.TimeOut)

			'Jika u di lokasi qty atau kolom ke 9'
			if (u == 9) {
				'Jika yang qtynya 1 dan databasenya juga, berhasil'
				if ((WebUI.getText(modifyperrowpercolumn) == '1') || ((result[(u - 1)]) == '-1')) {
					'Jika bukan untuk 2 kolom itu, maka check ke db'
					checkVerifyEqualOrMatch(WebUI.verifyMatch('-' + WebUI.getText(modifyperrowpercolumn), result[
							index++], false, FailureHandling.CONTINUE_ON_FAILURE), 'pada Kuantitas di Mutasi Saldo dengan nomor kontrak ' +
						(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo')).replace('"','')))
				} else {
					'Jika bukan -1, atau masih 0. Maka ttdnya dibilang error'
					GlobalVariable.FlagFailed = 1

					'Jika saldonya belum masuk dengan flag, maka signnya gagal.'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (((((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedSignGagal) + ' terlihat pada Kuantitas di Mutasi Saldo dengan nomor kontrak ') +
						'<') + (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo')).replace('"',''))) + '>')
				}
			} else if (u == variableSaldoColumn.size()) {
				'Jika di kolom ke 10, atau di FE table saldo, check saldo dari table dengan saldo yang sekarang. Takeout dari dev karena no issue dan sudah sepakat'
			} else {
				'Jika bukan untuk 2 kolom itu, maka check ke db'
				checkVerifyEqualOrMatch(WebUI.verifyMatch(WebUI.getText(modifyperrowpercolumn), result[index++],
						false, FailureHandling.CONTINUE_ON_FAILURE), ' pada Mutasi Saldo dengan nomor kontrak ' +
					(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo')).replace('"','')))
			}
		}
	}

}
*/
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
    'inisialisasi penggunaan saldo, pemotongan saldo, dan increment untuk ekbutuhan selanjutnya'
    int penggunaanSaldo = 0

    int pemotonganSaldo = 0

    int increment

    'inisialisasi balmut'
    ArrayList<String> balmut = []

    'inisialisasi tipe saldo'
    String tipeSaldo

    'get email per doc splitting by enter'
    ArrayList<String> emailPerDoc = emailSigner.split('\\n', -1)

    'looping email per enter'
    for (loopingEmailPerDoc = 0; loopingEmailPerDoc < emailPerDoc.size(); loopingEmailPerDoc++) {
        'split per '

        'email'
        ArrayList<String> email = (emailPerDoc[loopingEmailPerDoc]).split(';', -1)

        'looping'
        for (loopingEmail = 0; loopingEmail < email.size(); loopingEmail++) {
            'get email from nik'
            (email[loopingEmail]) = CustomKeywords.'connection.DataVerif.getEmailFromNIK'(conneSign, CustomKeywords.'customizekeyword.ParseText.convertToSHA256'(
                    email[loopingEmail]))

            fullNameUser = CustomKeywords.'connection.DataVerif.getFullNameOfUser'(conneSign, email[loopingEmail])

            notifTypeDB = CustomKeywords.'connection.APIFullService.getWASMSFromNotificationType'(conneSign, email[loopingEmail], 
                'SEND_DOC', GlobalVariable.Tenant)

            if (notifTypeDB == 'Email') {
                continue
            }
            
            if ((notifTypeDB == '0') || (notifTypeDB == 'Level Tenant')) {
                mustUseWAFirst = CustomKeywords.'connection.DataVerif.getMustUseWAFirst'(conneSign, GlobalVariable.Tenant)

                'get email service, full name, dan setting must use wa first'
                emailServiceOnVendor = CustomKeywords.'connection.DataVerif.getEmailServiceAsVendorUser'(conneSign, email[
                    loopingEmail])

                'jika must use wa first'
                if (mustUseWAFirst == '1') {
                    tipeSaldo = 'WhatsApp Message'

                    'menggunakan saldo wa'
                    balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

					penggunaanSaldo = checkingSaldo(balmut, tipeSaldo, penggunaanSaldo)
                } else {
                    'jika email servicenya hidup'
                    if (emailServiceOnVendor == '1') {
                        useWAMessage = CustomKeywords.'connection.DataVerif.getUseWAMessage'(conneSign, GlobalVariable.Tenant)

                        'jiak use wa meesagenya 1'
                        if (useWAMessage == '1') {
                            'penggunaan kepada wa'
                            tipeSaldo = 'WhatsApp Message'

                            'menggunakan saldo wa'
                            balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

							penggunaanSaldo = checkingSaldo(balmut, tipeSaldo, penggunaanSaldo)
                        } else if (useWAMessage == '0') {
                            'jika use wa message 0, maka mengarah ke SMS'

                            'ke sms / wa'
                            SMSSetting = CustomKeywords.'connection.DataVerif.getSMSSetting'(conneSign, 'Send Document')

                            'jika setting SMS hidup'
                            if (SMSSetting == '1') {
                                'ke sms'
                                tipeSaldo = 'SMS Notif'

                                'get balmut sms'
                                balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

								penggunaanSaldo = checkingSaldo(balmut, tipeSaldo, penggunaanSaldo)
                            }
                        }
                    }
                }
            } else {
                tipeSaldo = notifTypeDB

                'menggunakan saldo wa'
                balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)
				
				penggunaanSaldo = checkingSaldo(balmut, tipeSaldo, penggunaanSaldo)
            }
        }
        
        'jika ada penggunaan saldo'
        if (penggunaanSaldo > 0) {
            'looping berdasarkan penggunaan saldo'
            for (looping = 1; looping <= penggunaanSaldo; looping++) {
                'jika looping pertama, maka increment menjadi 0, jika sudah kedua, maka increment naik 9'
                if (looping == 1) {
                    increment = 0
                } else {
                    increment = (increment + 10)
                }
                
                'pemotongan saldo di get dari balmut kuantitas'
                pemotonganSaldo = (pemotonganSaldo + Integer.parseInt((balmut[(increment + 9)]).replace('-', '')))

                'trx number akan masuk kepada hashmap'
                (GlobalVariable.eSignData['allTrxNo']) = (((GlobalVariable.eSignData['allTrxNo']) + (balmut[increment])) + 
                ';')

                'tipe sign akan masuk kepada hashmap'
                (GlobalVariable.eSignData['allSignType']) = (((GlobalVariable.eSignData['allSignType']) + (balmut[(increment + 
                3)]).replace('Use ', '')) + ';')

                'email usage akan masuk kepada hashmap'
                (GlobalVariable.eSignData['emailUsageSign']) = (((GlobalVariable.eSignData['emailUsageSign']) + fullNameUser) + 
                ';')
            }
            
            'jika tipe saldo wa, maka akan terpotong saldo wa dari pemotongan saldo, jika sms, maka mengarah ke sms'
            if (tipeSaldo == 'WhatsApp Message') {
                (GlobalVariable.eSignData['CountVerifikasiWA']) = pemotonganSaldo
            } else if (tipeSaldo == 'SMS Notif') {
                (GlobalVariable.eSignData['CountVerifikasiSMS']) = pemotonganSaldo
            }
        }
    }
}

def checkingSaldo(ArrayList<String> balmut, String tipeSaldo, int penggunaanSaldo) {
    'jika balmutnya tidak ada value'
    if (balmut.size() == 0) {
        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + 
            ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via ') + tipeSaldo)
    } else {
        'penggunaan saldo didapat dari ikuantitaas query balmut'
        penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
    }
    penggunaanSaldo
}

