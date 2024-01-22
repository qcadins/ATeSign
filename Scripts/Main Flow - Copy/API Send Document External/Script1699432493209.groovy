import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'variable untuk keperluan split excel'
semicolon = ';'

delimiter = '\\|'

enter = '\\n'

int splitnum = -1

'setting menggunakan base url yang benar atau salah'
CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPISendDoc, GlobalVariable.NumofColm, rowExcel('Use Correct base Url'))

'Deklarasi variable mengenai signLoc untuk store db'
String signlocStoreDB

'get data inisialisasi dari excel'
getDataExcel(semicolon, splitnum, delimiter, enter)

'jika sign action terdapat at'
if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$signAction')).contains('at')) {
    'flag use autosign'
    useAutoSign = true
}

'Deklarasi variable string Ref no untuk full body API.'
String stringRefno = new String()

'inisialisasi split'
ArrayList<String> split = []

'set body api untuk send document external kepada split'
split = setBodyAPI(stringRefno, signlocStoreDB, conneSign)

'body untuk send document'
stringRefno = (split[0])

'body untuk sign location store db'
signlocStoreDB = (split[1])

'Jika flag tenant no'
if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 'No') {
    'set tenant kosong'
    GlobalVariable.Tenant = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
} else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 
'Yes') {
    'Input tenant'
    GlobalVariable.Tenant = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))
}

'check if mau menggunakan api_key yang salah atau benar'
if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'Yes') {
    'get api key dari db'
    GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
} else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'No') {
    'get api key salah dari excel'
    GlobalVariable.api_key = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
}

'Hit API'
respon = WS.sendRequest(findTestObject('APIFullService/Postman/Send Document Signing', [('tenantCode') : findTestData(excelPathAPISendDoc).getValue(
                GlobalVariable.NumofColm, rowExcel('Tenant')), ('request') : stringRefno, ('callerId') : findTestData(excelPathAPISendDoc).getValue(
                GlobalVariable.NumofColm, rowExcel('callerId'))]))

'jika response 200 / hit api berhasil'
if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
    'ambil respon text dalam bentuk code.'
    statusCode = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

    'jika status codenya 0, verifikasi datanya benar'
    if (statusCode == 0) {
        'mengambil documentid, trxno dan response doc template code dari hasil response API'
        documentId = WS.getElementPropertyValue(respon, 'documents.documentId', FailureHandling.OPTIONAL)

        'value trxno akan ada jika melakukan sign action autosign(at)'
        trxno = WS.getElementPropertyValue(respon, 'documents.trxNos', FailureHandling.OPTIONAL)

        'get respons doc template code'
        responseDocTemplateCode = WS.getElementPropertyValue(respon, 'documents.docTemplateCode', FailureHandling.OPTIONAL)

        'get respons psre Code'
        responsePsreCode = WS.getElementPropertyValue(respon, 'documents.psreCode', FailureHandling.OPTIONAL)

        'Jika doc template code di excel tidak sesuai dengan response doc template code'
        if (documentTemplateCode.toString().replace('"', '') != responseDocTemplateCode.toString()) {
            'write to excel status failed dan reason failed h'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                ((findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace(
                    '-', '') + semicolon) + GlobalVariable.ReasonFailedSaveGagal) + ' pada perbedaan document template code ')
        }
        
        'Memasukkan documentid dan trxno ke dalam excel'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('documentid') - 
            1, GlobalVariable.NumofColm - 1, documentId.toString().replace('[', '').replace(']', ''))

        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('trxNo') - 
            1, GlobalVariable.NumofColm - 1, trxno.toString().replace('[', '').replace(']', ''))

        'write to excel success'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status') - 
            1, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

        'Jika check storedb'
        if (GlobalVariable.checkStoreDB == 'Yes') {
            'call test case storedb'
            WebUI.callTestCase(findTestCase('Main Flow - Copy/API Send Document External StoreDB'), [('excelPathAPISendDoc') : excelPathAPISendDoc
                    , ('sheet') : sheet, ('signlocStoreDB') : signlocStoreDB, ('responsePsreCode') : responsePsreCode], 
                FailureHandling.CONTINUE_ON_FAILURE)
        }
    } else {
        getErrorMessageAPI(respon)
    }
} else {
    getErrorMessageAPI(respon)
}

'jika tidak ada document id'
if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).length() > 0) {
    'get check saldo wa or sms di send document'
    checkSaldoWAOrSMS(conneSign, findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$idKtp')).replace(
            '"', ''))
}

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        (';<' + message) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def getDataExcel(String semicolon, int splitnum, String delimiter, String enter) {
    'Inisialisasi ref No'
    refNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo'))

    'Inisialisasi document template code berdasarkan delimiter ;'
    documentTemplateCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$documentTemplateCode')).split(
        semicolon, splitnum)

    'Inisialisasi document name berdasarkan delimiter ;'
    documentName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('documentName')).split(
        semicolon, splitnum)

    'Inisialisasi office Code berdasarkan delimiter ;'
    officeCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('officeCode')).split(semicolon, 
        splitnum)

    'Inisialisasi office name berdasarkan delimiter ;'
    officeName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('officeName')).split(semicolon, 
        splitnum)

    'Inisialisasi region code berdasarkan delimiter ;'
    regionCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('regionCode')).split(semicolon, 
        splitnum)

    'Inisialisasi region name berdasarkan delimiter ;'
    regionName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('regionName')).split(semicolon, 
        splitnum)

    'Inisialisasi business line code berdasarkan delimiter ;'
    businessLineCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('businessLineCode')).split(
        semicolon, splitnum)

    'Inisialisasi business line name berdasarkan delimiter ;'
    businessLineName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('businessLineName')).split(
        semicolon, splitnum)

    'Inisialisasi is sequence berdasarkan delimiter ;'
    isSequence = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('isSequence')).split(semicolon, 
        splitnum)

    'Inisialisasi psre Code berdasarkan delimiter ;'
    psreCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('psreCode')).split(semicolon, 
        splitnum)

    'Inisialisasi document file berdasarkan delimiter ;'
    documentFile = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('documentFile')).split(
        enter, splitnum)

    'Inisialisasi qrEnable berdasarkan delimiter ;'
    qrEnable = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('QR')).replace('Ya', 'true').replace(
        'Tidak', 'false').split(semicolon, splitnum)

    'split signer untuk doc1 dan signer untuk doc2'
    signAction = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$signAction')).split(enter, 
        splitnum)

    signerType = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$signerType')).split(enter, 
        splitnum)

    tlp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$tlp')).split(enter, splitnum)

    idKtp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$idKtp')).split(enter, splitnum)

    email = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$email')).split(enter, splitnum)

    pageStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('page (Send stampExternal)')).split(
        delimiter, splitnum)

    llxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('llx (Send stampExternal)')).split(
        delimiter, splitnum)

    llyStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('lly (Send stampExternal)')).split(
        delimiter, splitnum)

    urxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('urx (Send stampExternal)')).split(
        delimiter, splitnum)

    uryStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('ury (Send stampExternal)')).split(
        delimiter, splitnum)
}

def setBodyAPI(String stringRefno, String signlocStoreDB, Connection conneSign) {
    'Looping berdasarkan total dari dokumen file ukuran'
    for (int i = 0; i < documentFile.size(); i++) {
        'signloc store db harus dikosongkan untuk loop dokumen selanjutnya.'
        if (((signlocStoreDB != '') && (signlocStoreDB != 'null')) && (signlocStoreDB != null)) {
            signlocStoreDB = (signlocStoreDB + '|')
        } else {
            signlocStoreDB = ''
        }
        
        'Splitting kembali dari dokumen pertama per signer'
        signActions = (signAction[i]).split(semicolon, splitnum)

        signerTypes = (signerType[i]).split(semicolon, splitnum)

        tlps = (tlp[i]).split(semicolon, splitnum)

        idKtps = (idKtp[i]).split(semicolon, splitnum)

        emails = (email[i]).split(semicolon, splitnum)

        'inisialisasi bodyAPI untuk menyusun body'
        String bodyAPI = new String()

        'Pengisian body'
        bodyAPI = (((((((((((((((((((((((bodyAPI + '{"referenceNo" : "') + refNo) + '", "documentTemplateCode": "') + (documentTemplateCode[
        i])) + '", "documentName": "') + (documentName[i])) + '", "officeCode": "') + (officeCode[i])) + '", "officeName": "') + 
        (officeName[i])) + '", "regionCode": "') + (regionCode[i])) + '", "regionName": "') + (regionName[i])) + '", "businessLineCode": "') + 
        (businessLineCode[i])) + '", "businessLineName": "') + (businessLineName[i])) + '", "isSequence": "') + (isSequence[
        i])) + '",  "psreCode": "') + (psreCode[i])) + '", ')

        if ((documentTemplateCode[i]) != '') {
            bodyAPI = (((bodyAPI + ' "qrEnable" : ') + (qrEnable[i])) + ', ')
        }
        
        'Memasukkan bodyAPI ke stringRefno'
        stringRefno = (stringRefno + bodyAPI)

        'inisialisasi bodyAPI untuk menyusun body'
        bodyAPI = new String()

        'inisialisasi body untuk seq no sebagai array'
        ArrayList<String> seqNoBodyAPI = []

        'looping berdasarkan jumlah dari signAction di dokumen pertama'
        for (int t = 0; t < signActions.size(); t++) {
            'Jika semua data mengenai Sign Location seperti page, llx, lly, urx, ury tidak kosong'
            if (!(((((findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('page (Send signExternal)')).length() == 
            0) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('llx (Send signExternal)')).length() == 
            0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('lly (Send signExternal)')).length() == 
            0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('urx (Send signExternal)')).length() == 
            0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('ury (Send signExternal)')).length() == 
            0))) {
                'Split mengenai signLocation dimana berdasarkan dokumen'
                pageSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('page (Send signExternal)')).split(
                    enter, splitnum)

                llxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('llx (Send signExternal)')).split(
                    enter, splitnum)

                llySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('lly (Send signExternal)')).split(
                    enter, splitnum)

                urxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('urx (Send signExternal)')).split(
                    enter, splitnum)

                urySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('ury (Send signExternal)')).split(
                    enter, splitnum)

                'split mengenai sequence Number per document'
                seqNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('SeqNo (Send External)')).split(
                    enter, splitnum)

                'Split mengenai signLocation dimana berdasarkan dokumen dan berdasarkan signer'
                pageSigns = (pageSign[i]).split(delimiter, splitnum)

                llxSigns = (llxSign[i]).split(delimiter, splitnum)

                llySigns = (llySign[i]).split(delimiter, splitnum)

                urxSigns = (urxSign[i]).split(delimiter, splitnum)

                urySigns = (urySign[i]).split(delimiter, splitnum)

                'Split mengenai signLocation dimana berdasarkan dokumen dan berdasarkan signer. Didapatlah semua lokasi signLocation di satu signer.'
                pageSigns = (pageSigns[t]).split(semicolon, splitnum)

                llxSigns = (llxSigns[t]).split(semicolon, splitnum)

                llySigns = (llySigns[t]).split(semicolon, splitnum)

                urxSigns = (urxSigns[t]).split(semicolon, splitnum)

                urySigns = (urySigns[t]).split(semicolon, splitnum)

                'looping menuju jumlah lokasi pageSign di 1 signer'
                for (int l = 0; l < pageSigns.size(); l++) {
                    if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('SeqNo (Send External)')).length() == 
                    0) {
                        seqNoBodyAPI.add('')
                    } else {
                        'split seq number per signer'
                        seqNos = (seqNo[i]).split(semicolon, splitnum)

                        'looping mengenai total sequence number'
                        for (int p = 0; p < seqNos.size(); p++) {
                            'jika seq numbernya tidak kosong'
                            if ((seqNos[p]) != '') {
                                'Memasukkan value seqNo dan body API kepada array'
                                seqNoBodyAPI.add((',"seqNo": "' + (seqNos[p])) + '"')
                            } else {
                                'Jika seq number kosong ,maka input kosong'
                                seqNoBodyAPI.add('')
                            }
                        }
                    }
                    
                    'Jika loopingan pertama'
                    if (l == 0) {
                        'Jika dari loopingan pertama, pageSignnya hanya ada 1 dan yang terakhir'
                        if (l == (pageSigns.size() - 1)) {
                            if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
                                'Isi bodyAPI'
                                bodyAPI = ((bodyAPI + (seqNoBodyAPI[t])) + ',"signLocations": [')

                                'Jika pageSign untuk yang pertama di signer pertama kosong'
                                if ((pageSigns[l]) == '') {
                                    'Input body mengenai llx dan lly'
                                    bodyAPI = ((bodyAPI + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], 
                                        urySigns[l])) + ']')
                                } else if ((llxSigns[l]) == '""') {
                                    'Input body mengenai page'
                                    bodyAPI = ((bodyAPI + bodyLocationPage(pageSigns[l])) + ']')
                                } else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
                                    'Input body mengenai page dan llx,lly,urx, dan ury'
                                    bodyAPI = ((bodyAPI + bodyLocationCoordinatePage(llxSigns[l], llySigns[l], urxSigns[
                                        l], urySigns[l], pageSigns[l])) + ']')
                                }
                                
                                'Jika loopingan sudah di akhir'
                                if (t == (signActions.size() - 1)) {
                                    'isi signlocStoreDB'
                                    signlocStoreDB = (signlocStoreDB + bodyLocationCoordinate(llxSigns[l], llySigns[l], 
                                        urxSigns[l], urySigns[l]))
                                } else {
                                    'isi signlocStoreDB'
                                    signlocStoreDB = ((signlocStoreDB + bodyLocationCoordinate(llxSigns[l], llySigns[l], 
                                        urxSigns[l], urySigns[l])) + ';')
                                }
                            }
                        } else {
                            if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
                                'Isi bodyAPI'
                                bodyAPI = ((bodyAPI + (seqNoBodyAPI[t])) + ',"signLocations": [')

                                'Jika pageSign yang pertama di signer pertama kosong'
                                if ((pageSigns[l]) == '') {
                                    'Input body mengenai x dan y'
                                    bodyAPI = ((bodyAPI + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], 
                                        urySigns[l])) + ',')
                                } else if ((llxSigns[l]) == '""') {
                                    'Input body mengenai page'
                                    bodyAPI = ((bodyAPI + bodyLocationPage(pageSigns[l])) + ',')
                                } else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
                                    'Input body mengenai page dan koordinat'
                                    bodyAPI = ((bodyAPI + bodyLocationCoordinatePage(llxSigns[l], llySigns[l], urxSigns[
                                        l], urySigns[l], pageSigns[l])) + ',')
                                }
                                
                                'isi signlocStoreDB'
                                signlocStoreDB = ((signlocStoreDB + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[
                                    l], urySigns[l])) + ';')
                            }
                        }
                    } else if (l == (pageSigns.size() - 1)) {
                        if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
                            'Jika pageSign yang pertama di signer pertama kosong'
                            if ((pageSigns[l]) == '') {
                                'Input body mengenai koordinat'
                                bodyAPI = ((bodyAPI + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], urySigns[
                                    l])) + ']')
                            } else if ((llxSigns[l]) == '""') {
                                'Input body mengenai page'
                                bodyAPI = ((bodyAPI + bodyLocationPage(pageSigns[l])) + ']')
                            } else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
                                'Input body mengenai page dan koordinat'
                                bodyAPI = ((bodyAPI + bodyLocationCoordinatePage(llxSigns[l], llySigns[l], urxSigns[l], 
                                    urySigns[l], pageSigns[l])) + ']')
                            }
                        }
                        
                        'Jika loopingan sudah di akhir'
                        if (t == (signActions.size() - 1)) {
                            'isi signlocStoreDB'
                            signlocStoreDB = (signlocStoreDB + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[
                                l], urySigns[l]))
                        } else {
                            'isi signlocStoreDB'
                            signlocStoreDB = ((signlocStoreDB + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[
                                l], urySigns[l])) + ';')
                        }
                    } else {
                        if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
                            'Jika pageSign yang pertama di signer pertama kosong'
                            if ((pageSigns[l]) == '') {
                                'Input body mengenai koordinat'
                                bodyAPI = ((bodyAPI + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], urySigns[
                                    l])) + ',')
                            } else if ((llxSigns[l]) == '""') {
                                'Input body mengenai page'
                                bodyAPI = ((bodyAPI + bodyLocationPage(pageSigns[l])) + ',')
                            } else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
                                'Input body mengenai page dan koordinat'
                                bodyAPI = ((bodyAPI + bodyLocationCoordinatePage(llxSigns[l], llySigns[l], urxSigns[l], 
                                    urySigns[l], pageSigns[l])) + ',')
                            }
                        }
                        
                        'isi signlocStoreDB'
                        signlocStoreDB = ((signlocStoreDB + bodyLocationCoordinate(llxSigns[l], llySigns[l], urxSigns[l], 
                            urySigns[l])) + ';')
                    }
                }
            }
            
            'Jika signAction yang pertama untuk dokumen pertama'
            if (t == 0) {
                if (t == (signActions.size() - 1)) {
                    'isi bodyAPI dengan bodyAPI yang atas'
                    bodyAPI = (('"signers" : [' + bodySigner(signActions[t], signerTypes[t], tlps[t], idKtps[t], emails[
                        t], bodyAPI)) + '],')
                } else {
                    'isi bodyAPI dengan bodyAPI yang atas'
                    bodyAPI = (('"signers" : [' + bodySigner(signActions[t], signerTypes[t], tlps[t], idKtps[t], emails[
                        t], bodyAPI)) + ',')
                }
            } else if (t == (signActions.size() - 1)) {
                'isi bodyAPI dengan bodyAPI yang atas'
                bodyAPI = (bodySigner(signActions[t], signerTypes[t], tlps[t], idKtps[t], emails[t], bodyAPI) + '],')
            } else {
                'isi bodyAPI dengan bodyAPI yang atas'
                bodyAPI = (bodySigner(signActions[t], signerTypes[t], tlps[t], idKtps[t], emails[t], bodyAPI) + ',')
            }
            
            'check ada value maka setting email service tenant'
            if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')).length() > 
            0) {
                for (loopingSignerEmailActive = 0; loopingSignerEmailActive < idKtps.size(); loopingSignerEmailActive++) {
                    SHA256IdNo = CustomKeywords.'customizekeyword.ParseText.convertToSHA256'((idKtps[loopingSignerEmailActive]).replace(
                            '"', ''))

                    'setting email service tenant'
                    CustomKeywords.'connection.SendSign.settingEmailServiceVendorRegisteredUser'(conneSign, findTestData(
                            excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')), 
                        SHA256IdNo)
                }
            }
            
            'Memasukkan bodyAPI ke stringRefno'
            stringRefno = (stringRefno + bodyAPI)

            'Mengkosongkan bodyAPI untuk digunakan selanjutnya'
            bodyAPI = ''
        }
        
        'Deklarasi bodyAPI kembali'
        bodyAPI = new String()

        'Jika dokumennya menggunakan base64'
        if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 Document')) == 
        'Yes') {
            'input bodyAPI dengan Base64'
            bodyAPI = (((bodyAPI + '"documentFile": "') + CustomKeywords.'customizekeyword.ConvertFile.base64File'(documentFile[
                i])) + '"')
        } else {
            'input bodyAPI tidak dengan Base64'
            bodyAPI = (((bodyAPI + '"documentFile": "') + (documentFile[i])) + '"')
        }
        
        'Input bodyAPI ke stringRefno'
        stringRefno = (stringRefno + bodyAPI)

        'Mengkosongkan bodyAPI'
        bodyAPI = ''

        bodyAPI = setBodyForStampingLocation(pageStamp[i], llxStamp[i], llyStamp[i], urxStamp[i], uryStamp[i], bodyAPI)

        'jika dokumennya di akhir'
        if (i == (documentFile.size() - 1)) {
            'input body API berdasarkan bodyAPI diatasnya'
            bodyAPI = (bodyAPI + '}')
        } else {
            'input body API berdasarkan bodyAPI diatasnya'
            bodyAPI = (bodyAPI + '},')
        }
        
        'input body API kedalam stringRefno'
        stringRefno = (stringRefno + bodyAPI)
    }
    
    ArrayList<String> returning = []

    returning.add(stringRefno)

    returning.add(signlocStoreDB)

    returning
}

def setBodyForStampingLocation(String pageStamp, String llxStamp, String llyStamp, String urxStamp, String uryStamp, String bodyAPI) {
    'Jika informasi di excel mengenai stampLocation seperti page dan koordinat ada, maka'
    if (!(((((pageStamp.length() == 0) && (llxStamp.length() == 0)) && (llyStamp.length() == 0)) && (urxStamp.length() == 
    0)) && (uryStamp.length() == 0))) {
        'Splitting dari dokumen pertama per signer mengenai stamping'
        pageStamps = pageStamp.split(semicolon, splitnum)

        llxStamps = llxStamp.split(semicolon, splitnum)

        llyStamps = llyStamp.split(semicolon, splitnum)

        urxStamps = urxStamp.split(semicolon, splitnum)

        uryStamps = uryStamp.split(semicolon, splitnum)

        'looping berdasarkan pagestamp per dokumen'
        for (int b = 0; b < pageStamps.size(); b++) {
            'Jika dia loopingan yang pertama'
            if (b == 0) {
                if (b == (pageStamps.size() - 1)) {
                    if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {
                        'Isi bodyAPI'
                        bodyAPI = (bodyAPI + ',"stampLocations": [')

                        if ((pageStamps[b]) == '') {
                            'Input body mengenai koordinat'
                            bodyAPI = ((bodyAPI + bodyLocationCoordinate(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[
                                b])) + ']')
                        } else if ((llxStamps[b]) == '""') {
                            'Input body mengenai page'
                            bodyAPI = ((bodyAPI + bodyLocationPage(pageStamps[b])) + ']')
                        } else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
                            'Input body mengenai page dan koordinat'
                            bodyAPI = ((bodyAPI + bodyLocationCoordinatePage(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[
                                b], pageStamps[b])) + ']')
                        }
                    }
                } else {
                    if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {
                        'Isi bodyAPI'
                        bodyAPI = (bodyAPI + ',"stampLocations": [')

                        'Jika pageStampnya kosong'
                        if ((pageStamps[b]) == '') {
                            'Input body mengenai koordinat'
                            bodyAPI = ((bodyAPI + bodyLocationCoordinate(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[
                                b])) + ',')
                        } else if ((llxStamps[b]) == '""') {
                            'Input body mengenai page'
                            bodyAPI = ((bodyAPI + bodyLocationPage(pageStamps[b])) + ',')
                        } else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
                            'Input body mengenai page dan koordinat'
                            bodyAPI = ((bodyAPI + bodyLocationCoordinatePage(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[
                                b], pageStamps[b])) + ',')
                        }
                    }
                }
            } else if (b == (pageStamps.size() - 1)) {
                if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {
                    'Jika pageStampnya kosong'
                    if ((pageStamps[b]) == '') {
                        'Input body mengenai koordinat'
                        bodyAPI = ((bodyAPI + bodyLocationCoordinate(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[
                            b])) + ']')
                    } else if ((llxStamps[b]) == '""') {
                        'Input body mengenai page'
                        bodyAPI = ((bodyAPI + bodyLocationPage(pageStamps[b])) + ']')
                    } else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
                        'Input body mengenai page dan koordinat'
                        bodyAPI = ((bodyAPI + bodyLocationCoordinatePage(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[
                            b], pageStamps[b])) + ']')
                    }
                }
            } else {
                if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {
                    'Jika pageStampnya kosong'
                    if ((pageStamps[b]) == '') {
                        'Input body mengenai koordinat'
                        bodyAPI = ((bodyAPI + bodyLocationCoordinate(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[
                            b])) + ',')
                    } else if ((llxStamps[b]) == '""') {
                        'Input body mengenai page'
                        bodyAPI = ((bodyAPI + bodyLocationPage(pageStamps[b])) + ',')
                    } else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
                        'Input body mengenai page dan koordinat'
                        bodyAPI = ((bodyAPI + bodyLocationCoordinatePage(llxStamps[b], llyStamps[b], urxStamps[b], uryStamps[
                            b], pageStamps[b])) + ',')
                    }
                }
            }
        }
    }
    
    return bodyAPI
}

def bodyLocationCoordinate(String llxSigns, String llySigns, String urxSigns, String urySigns) {
    ((((((('{"llx":"' + llxSigns) + '","lly":"') + llySigns) + '","urx":"') + urxSigns) + '","ury":"') + urySigns) + '"}'
}

def bodyLocationPage(String pageSigns) {
    ('{"page":' + pageSigns) + '}'
}

def bodyLocationCoordinatePage(String llxSigns, String llySigns, String urxSigns, String urySigns, String pageSigns) {
    ((((((((('{"page":' + pageSigns) + ',"llx":"') + llxSigns) + '","lly":"') + llySigns) + '","urx":"') + urxSigns) + '", "ury":"') + 
    urySigns) + '"}'
}

def bodySigner(String signActions, String signerTypes, String tlps, String idKtps, String emails, String bodyAPI) {
    ((((((((((('{"signAction": "' + signActions) + '","signerType": "') + signerTypes) + '","tlp": "') + tlps) + '","idKtp": "') + 
    idKtps) + '","email": "') + emails) + '"') + bodyAPI) + '}'
}

def checkVerifyEqualOrMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + GlobalVariable.ReasonFailedVerifyEqualOrMatch) + 
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

            if (notifTypeDB == '0') {
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

                    'jika arraylist tidak ada isinya'
                    if (balmut.size() == 0) {
                        'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                            GlobalVariable.StatusFailed, (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 
                                rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via WhatsApp')
                    } else {
                        'penggunaan saldo get dari kuantitas per array list balance mutation'
                        penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
                    }
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

                            'jika balmutnya tidak ada value'
                            if (balmut.size() == 0) {
                                'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                    GlobalVariable.StatusFailed, (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 
                                        rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via WhatsApp')
                            } else {
                                'penggunaan saldo didapat dari ikuantitaas query balmut'
                                penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
                            }
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

                                'jika tidak ada balmut'
                                if (balmut.size() == 0) {
                                    'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
                                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                        GlobalVariable.StatusFailed, (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 
                                            rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via SMS')
                                } else {
                                    'get kuantitas dari balmut'
                                    penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
                                }
                            }
                        }
                    }
                }
            } else {
				tipeSaldo = notifTypeDB
				
				'menggunakan saldo wa'
				balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, tipeSaldo, fullNameUser)

				'jika balmutnya tidak ada value'
				if (balmut.size() == 0) {
					'Jika equalnya salah maka langsung berikan reason bahwa reasonnya failed'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm,
						GlobalVariable.StatusFailed, (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm,
							rowExcel('Reason Failed')).replace('-', '') + ';') + 'Tidak ada transaksi yang terbentuk ketika melakukan pengiriman Informasi Signing Via ' + tipeSaldo.replace(' Message', '').replace(' Notif', ''))
				} else {
					'penggunaan saldo didapat dari ikuantitaas query balmut'
					penggunaanSaldo = (penggunaanSaldo + (balmut.size() / 10))
				}
			}
        }
        
        'jika ada penggunaan saldo'
        if (penggunaanSaldo > 0) {
            'looping mengenai penggunaan saldo'
            for (looping = 0; looping < penggunaanSaldo; looping++) {
                'jika loopingan pertama, incrementnya 0'
                if (looping == 0) {
                    increment = 0
                } else {
                    'increment meningkat 9'
                    increment = (increment + 10)
                }
                
                'pemotongan saldo akan di get berdasarkan kuantitas yang awalnya hanya get dari total 11 row query'
                pemotonganSaldo = (pemotonganSaldo + Integer.parseInt((balmut[(increment + 9)]).replace('-', '')))

                'input value trx number ke hashmap'
                (GlobalVariable.eSignData['allTrxNo']) = (((GlobalVariable.eSignData['allTrxNo']) + (balmut[(increment + 
                0)])) + ';')

                'input value sign type ke hashmap'
                (GlobalVariable.eSignData['allSignType']) = (((GlobalVariable.eSignData['allSignType']) + (balmut[(increment + 
                3)]).replace('Use ', '')) + ';')

                'input email usage sign (nama/email) ke hashmap'
                (GlobalVariable.eSignData['emailUsageSign']) = (((GlobalVariable.eSignData['emailUsageSign']) + fullNameUser) + 
                ';')
            }
        }
        
        'jika tipenya wa, maka tambah pemotongan saldo ke countverifikasiwa, jika sms, maka ke sms'
        if (tipeSaldo == 'WhatsApp Message') {
            (GlobalVariable.eSignData['CountVerifikasiWA']) = pemotonganSaldo
        } else if (tipeSaldo == 'SMS Notif') {
            (GlobalVariable.eSignData['CountVerifikasiSMS']) = pemotonganSaldo
        }
    }
}

