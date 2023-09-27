import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

sheet = 'API Send Document'

'variable untuk keperluan split excel'
semicolon = ';'

delimiter = '\\|'

enter = '\\n'

int splitnum = -1

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(API_Excel_Path, GlobalVariable.NumofColm, 57)

        'Inisialisasi ref No berdasarkan delimiter ;'
        refNo = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11)

        'Inisialisasi document template code berdasarkan delimiter ;'
        documentTemplateCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12).split(semicolon, splitnum)

        'Inisialisasi office Code berdasarkan delimiter ;'
        officeCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13).split(semicolon, splitnum)

        'Inisialisasi office name berdasarkan delimiter ;'
        officeName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 14).split(semicolon, splitnum)

        'Inisialisasi region code berdasarkan delimiter ;'
        regionCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 15).split(semicolon, splitnum)

        'Inisialisasi region name berdasarkan delimiter ;'
        regionName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 16).split(semicolon, splitnum)

        'Inisialisasi business line code berdasarkan delimiter ;'
        businessLineCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 17).split(semicolon, splitnum)

        'Inisialisasi business line name berdasarkan delimiter ;'
        businessLineName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 18).split(semicolon, splitnum)

        'Inisialisasi is sequence berdasarkan delimiter ;'
        isSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 19).split(semicolon, splitnum)

        'Inisialisasi document file berdasarkan delimiter ;'
        documentFile = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 20).split(enter, splitnum)

        'Inisialisasi psre Code berdasarkan delimiter ;'
        psreCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 21).split(semicolon, splitnum)

        'Inisialisasi successUrl berdasarkan delimiter ;'
        successURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 22).split(semicolon, splitnum)

        'Inisialisasi psre Code berdasarkan delimiter ;'
        uploadURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 23).split(semicolon, splitnum)

        'split signer untuk doc1 dan signer untuk doc2'
        signAction = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 25).split(enter, splitnum)

        signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).split(enter, splitnum)

        signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 27).split(enter, splitnum)

        alamat = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).split(enter, splitnum)

        jenisKelamin = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 29).split(enter, splitnum)

        kecamatan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 30).split(enter, splitnum)

        kelurahan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 31).split(enter, splitnum)

        kodePos = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 32).split(enter, splitnum)

        kota = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 33).split(enter, splitnum)

        nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 34).split(enter, splitnum)

        tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 35).split(enter, splitnum)

        tglLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 36).split(enter, splitnum)

        provinsi = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 37).split(enter, splitnum)

        idKtp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 38).split(enter, splitnum)

        tmpLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 39).split(enter, splitnum)

        email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).split(enter, splitnum)

        npwp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 41).split(enter, splitnum)

        idPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 42)

        signerSelfPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 43)

        String stringRefno = new String()

        String bodyAPI = new String()

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

            String listSigner

            listSigner = ''

            for (int i = 0; i < signActions.size(); i++) {
                'Pembuatan pengisian variable di sendRequest per jumlah signer.'
                ArrayList list = []

                if ((i == 0) && (i == (signActions.size() - 1))) {
                    list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + (signActions[i])) + ',"signerType": ') + 
                        (signerTypes[i])) + ',"signSequence":') + (signSequences[i])) + ',"alamat": ') + (alamats[(i - 1)])) + 
                        ',"jenisKelamin": ') + (jenisKelamins[i])) + ',"kecamatan": ') + (kecamatans[i])) + ',"kelurahan": ') + 
                        (kelurahans[i])) + ',"kodePos": ') + (kodePoss[i])) + ',"kota": ') + (kotas[i])) + ',"nama": ') + 
                        (namas[i])) + ',"tlp": ') + (tlps[i])) + ',"tglLahir": ') + (tglLahirs[i])) + ',"provinsi": ') + 
                        (provinsis[i])) + ',"idKtp": ') + (idKtps[i])) + ',"tmpLahir": ') + (tmpLahirs[i])) + ',"email": ') + 
                        (emails[i])) + ',"npwp": ') + (npwps[i])) + ',"idPhoto": ') + idPhoto) + ',"signerSelfPhoto": ') + 
                        signerSelfPhoto) + '}')
                } else if (i == (signActions.size() - 1)) {
                    list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + (signActions[i])) + ',"signerType": ') + 
                        (signerTypes[i])) + ',"signSequence":') + (signSequences[i])) + ',"alamat": ') + (alamats[(i - 1)])) + 
                        ',"jenisKelamin": ') + (jenisKelamins[i])) + ',"kecamatan": ') + (kecamatans[i])) + ',"kelurahan": ') + 
                        (kelurahans[i])) + ',"kodePos": ') + (kodePoss[i])) + ',"kota": ') + (kotas[i])) + ',"nama": ') + 
                        (namas[i])) + ',"tlp": ') + (tlps[i])) + ',"tglLahir": ') + (tglLahirs[i])) + ',"provinsi": ') + 
                        (provinsis[i])) + ',"idKtp": ') + (idKtps[i])) + ',"tmpLahir": ') + (tmpLahirs[i])) + ',"email": ') + 
                        (emails[i])) + ',"npwp": ') + (npwps[i])) + ',"idPhoto": ') + idPhoto) + ',"signerSelfPhoto": ') + 
                        signerSelfPhoto) + '}')
                } else {
                    list.add(((((((((((((((((((((((((((((((((((((('{"signAction": ' + (signActions[i])) + ',"signerType": ') + 
                        (signerTypes[i])) + ',"signSequence":') + (signSequences[i])) + ',"alamat": ') + (alamats[i])) + 
                        ',"jenisKelamin": ') + (jenisKelamins[i])) + ',"kecamatan": ') + (kecamatans[i])) + ',"kelurahan": ') + 
                        (kelurahans[i])) + ',"kodePos": ') + (kodePoss[i])) + ',"kota": ') + (kotas[i])) + ',"nama": ') + 
                        (namas[i])) + ',"tlp": ') + (tlps[i])) + ',"tglLahir": ') + (tglLahirs[i])) + ',"provinsi": ') + 
                        (provinsis[i])) + ',"idKtp": ') + (idKtps[i])) + ',"tmpLahir": ') + (tmpLahirs[i])) + ',"email": ') + 
                        (emails[i])) + ',"npwp": ') + (npwps[i])) + ',"idPhoto": ') + idPhoto) + ',"signerSelfPhoto": ') + 
                        signerSelfPhoto) + '},')
                }
                
                'Memasukkan seluruh BodyAPI ke listSigner'
                listSigner = (listSigner + (list[(i - 1)]))

                'check ada value maka setting email service tenant'
                if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 52).length() > 0) {
                    'setting email service tenant'
                    CustomKeywords.'connection.SendSign.settingEmailServiceVendorRegisteredUser'(conneSign, findTestData(
                            API_Excel_Path).getValue(GlobalVariable.NumofColm, 52), (email[(i - 1)]).replace('"', ''))
                }
            }
            
            if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 51) == 'Yes') {
                bodyAPI = (('"documentFile": "' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(documentFile[
                    o])) + '"')
            } else {
                bodyAPI = (('"documentFile": "' + (documentFile[o])) + '"')
            }
            
            if (o == (documentFile.size() - 1)) {
                stringRefno = (((((((((((((((((((((((((((((stringRefno + '{"referenceNo" : ') + refNo) + ', "documentTemplateCode": ') + 
                (documentTemplateCode[o])) + ', "officeCode": ') + (officeCode[o])) + ', "officeName": ') + (officeName[
                o])) + ', "regionCode": ') + (regionCode[o])) + ', "regionName": ') + (regionName[o])) + ', "businessLineCode": ') + 
                (businessLineCode[o])) + ', "businessLineName": ') + (businessLineName[o])) + ', "isSequence": ') + (isSequence[
                o])) + ', "signer":[') + listSigner) + '],') + bodyAPI) + ', "psreCode" : ') + (psreCode[o])) + ', "successURL": ') + 
                (successURL[o])) + ', "uploadURL": ') + (uploadURL[o])) + '}')
            } else {
                stringRefno = (((((((((((((((((((((((((((((stringRefno + '{"referenceNo" : ') + refNo) + ', "documentTemplateCode": ') + 
                (documentTemplateCode[o])) + ', "officeCode": ') + (officeCode[o])) + ', "officeName": ') + (officeName[
                o])) + ', "regionCode": ') + (regionCode[o])) + ', "regionName": ') + (regionName[o])) + ', "businessLineCode": ') + 
                (businessLineCode[o])) + ', "businessLineName": ') + (businessLineName[o])) + ', "isSequence": ') + (isSequence[
                o])) + ', "signer":[') + listSigner) + '],') + bodyAPI) + ', "psreCode" : ') + (psreCode[o])) + ', "successURL": ') + 
                (successURL[o])) + ', "uploadURL": ') + (uploadURL[o])) + '},')
            }
        }
        
        String isDownloadDocument = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 54)

        String isDeleteDownloadedDocument = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 55)

        String isViewDocument = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 56)

        'Jika flag tenant no'
        if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 49) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 50)
        } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 49) == 'Yes') {
            'Input tenant'
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 47) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 47) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 48)
        }
        
        'Hit API'
        respon = WS.sendRequest(findTestObject('Postman/Send Document', [('tenantCode') : findTestData(API_Excel_Path).getValue(
                        GlobalVariable.NumofColm, 9), ('request') : stringRefno, ('callerId') : findTestData(API_Excel_Path).getValue(
                        GlobalVariable.NumofColm, 45)]))

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
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 5, GlobalVariable.NumofColm - 
                    1, GlobalVariable.Response.toString().replace('[', '').replace(']', ''))

                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)

                if (GlobalVariable.checkStoreDB == 'Yes') {
                    'call test case ResponseAPIStoreDB'
                    WebUI.callTestCase(findTestCase('InactiveTC/Send Document/ResponseAPIStoreDB'), [('API_Excel_Path') : API_Excel_Path], 
                        FailureHandling.CONTINUE_ON_FAILURE)
                }
                
                'jika status codenya bukan 0, yang berarti antara salah verifikasi data dan error'
            } else {
                messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

                'write to excel status failed dan reason'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                    (((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + semicolon) + 
                    '<') + messageFailed) + '>')
            }
        } else {
            messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

            'write to excel status failed dan reason'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2).replace('-', '') + semicolon) + '<') + 
                messageFailed) + '>')
        }
        
        'close browser'
        WebUI.closeBrowser()
    }
}

