import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testobject.ResponseObject as ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'variable untuk keperluan split excel'
semicolon = ';'

delimiter = '\\|'

enter = '\\n'

int splitnum = -1

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'looping berdasarkan total kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathAPISendDoc).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
        'setting menggunakan base url yang benar atau salah'
        CustomKeywords.'connection.APIFullService.settingBaseUrl'(excelPathAPISendDoc, GlobalVariable.NumofColm, rowExcel(
                'Use Correct Base Url'))

        'Deklarasi variable mengenai signLoc untuk store db'
        String signlocStoreDB = new String()

        'Inisialisasi ref No'
        refNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo'))

        'Inisialisasi document template code berdasarkan delimiter ;'
        documentTemplateCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$documentTemplateCode')).split(
            semicolon, splitnum)

        'Inisialisasi document name berdasarkan delimiter ;'
        documentName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('documentName')).split(
            semicolon, splitnum)

        'Inisialisasi office Code berdasarkan delimiter ;'
        officeCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('officeCode')).split(
            semicolon, splitnum)

        'Inisialisasi office name berdasarkan delimiter ;'
        officeName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('officeName')).split(
            semicolon, splitnum)

        'Inisialisasi region code berdasarkan delimiter ;'
        regionCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('regionCode')).split(
            semicolon, splitnum)

        'Inisialisasi region name berdasarkan delimiter ;'
        regionName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('regionName')).split(
            semicolon, splitnum)

        'Inisialisasi business line code berdasarkan delimiter ;'
        businessLineCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('businessLineCode')).split(
            semicolon, splitnum)

        'Inisialisasi business line name berdasarkan delimiter ;'
        businessLineName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('businessLineName')).split(
            semicolon, splitnum)

        'Inisialisasi is sequence berdasarkan delimiter ;'
        isSequence = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('isSequence')).split(
            semicolon, splitnum)
		
		'Inisialisasi QREnable berdasarkan delimiter ;'
		qrEnable = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('QR')).split(
			semicolon, splitnum)

        'Inisialisasi psre Code berdasarkan delimiter ;'
        psreCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('psreCode')).split(semicolon, 
            splitnum)

        'Inisialisasi document file berdasarkan delimiter ;'
        documentFile = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('documentFile')).split(
            enter, splitnum)

        'split signer untuk doc1 dan signer untuk doc2'
        signAction = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$signAction')).split(
            enter, splitnum)

        signerType = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$signerType')).split(
            enter, splitnum)

        tlp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$tlp')).split(enter, splitnum)

        idKtp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$idKtp')).split(enter, splitnum)

        email = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$email')).split(enter, splitnum)

        pageStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('page (stamp)')).split(
            delimiter, splitnum)

        llxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('llx (stamp)')).split(delimiter, 
            splitnum)

        llyStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('lly (stamp)')).split(delimiter, 
            splitnum)

        urxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('urx (stamp)')).split(delimiter, 
            splitnum)

        uryStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('ury (stamp)')).split(delimiter, 
            splitnum)

        'Deklarasi variable string Ref no untuk full body API.'
        String stringRefno = new String()

        'Looping berdasarkan total dari dokumen file ukuran'
        for (int i = 0; i < documentFile.size(); i++) {
            'signloc store db harus dikosongkan untuk loop dokumen selanjutnya.'
            signlocStoreDB = ''

            'Splitting kembali dari dokumen pertama per signer'
            signActions = (signAction[i]).split(semicolon, splitnum)

            signerTypes = (signerType[i]).split(semicolon, splitnum)

            tlps = (tlp[i]).split(semicolon, splitnum)

            idKtps = (idKtp[i]).split(semicolon, splitnum)

            emails = (email[i]).split(semicolon, splitnum)

            'inisialisasi bodyAPI untuk menyusun body'
            String bodyAPI = new String()

            'Pengisian body'
            bodyAPI = (((((((((((((((((((((((bodyAPI + '{"referenceNo" : ') + refNo) + ', "documentTemplateCode": ') + (documentTemplateCode[
            i])) + ', "documentName": ') + (documentName[i])) + ', "officeCode": ') + (officeCode[i])) + ', "officeName": ') + 
            (officeName[i])) + ', "regionCode": ') + (regionCode[i])) + ', "regionName": ') + (regionName[i])) + ', "businessLineCode": ') + 
            (businessLineCode[i])) + ', "businessLineName": ') + (businessLineName[i])) + ', "isSequence": ') + (isSequence[
            i])) + ',  "psreCode": ') + (psreCode[i])) + ',')

			bodyAPI = (((bodyAPI + ' "qrEnable" : ') + (qrEnable[i])) + ', ')
			
            'Memasukkan bodyAPI ke stringRefno'
            stringRefno = (stringRefno + bodyAPI)

            'inisialisasi bodyAPI untuk menyusun body'
            bodyAPI = new String()

            'inisialisasi body untuk seq no sebagai array'
            ArrayList seqNoBodyAPI = []

            'looping berdasarkan jumlah dari signAction di dokumen pertama'
            for (int t = 0; t < signActions.size(); t++) {
                'Jika semua data mengenai Sign Location seperti page, llx, lly, urx, ury tidak kosong'
                if (!(((((findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('page (sign)')).length() == 0) && (findTestData(
                    excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('llx (sign)')).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(
                    GlobalVariable.NumofColm, rowExcel('lly (sign)')).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 
                    rowExcel('urx (sign)')).length() == 0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('ury (sign)')).length() == 
                0))) {
                    'Split mengenai signLocation dimana berdasarkan dokumen'
                    pageSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('page (sign)')).split(
                        enter, splitnum)

                    llxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('llx (sign)')).split(
                        enter, splitnum)

                    llySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('lly (sign)')).split(
                        enter, splitnum)

                    urxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('urx (sign)')).split(
                        enter, splitnum)

                    urySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('ury (sign)')).split(
                        enter, splitnum)

                    'split mengenai sequence Number'
                    seqNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('SeqNo')).split(
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
                        if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('SeqNo')).length() == 
                        0) {
                            seqNoBodyAPI.add('')
                        } else {
                            'split seq number per documentnya'
                            seqNos = (seqNo[i]).split(semicolon, splitnum)

                            'looping mengenai total sequence number'
                            for (int p = 0; p < seqNos.size(); p++) {
                                'jika seq numbernya tidak kosong'
                                if ((seqNos[p]) != '') {
                                    'Memasukkan value seqNo dan body API kepada array'
                                    seqNoBodyAPI.add(',"seqNo": ' + (seqNos[p]))
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
                                        bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxSigns[l])) + ', "lly" : ') + (llySigns[
                                        l])) + ', "urx" : ') + (urxSigns[l])) + ', "ury" : ') + (urySigns[l])) + '}]')
                                    } else if ((llxSigns[l]) == '""') {
                                        'Input body mengenai page'
                                        bodyAPI = (((bodyAPI + '{"page" : ') + (pageSigns[l])) + '}]')
                                    } else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
                                        'Input body mengenai page dan llx,lly,urx, dan ury'
                                        bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageSigns[l])) + ', "llx" : ') + 
                                        (llxSigns[l])) + ', "lly" : ') + (llySigns[l])) + ', "urx" : ') + (urxSigns[l])) + 
                                        ', "ury" : ') + (urySigns[l])) + '}]')
                                    }
                                    
                                    'Jika loopingan sudah di akhir'
                                    if (t == (signActions.size() - 1)) {
                                        'isi signlocStoreDB'
                                        signlocStoreDB = (((((((((signlocStoreDB + '{"llx":') + (llxSigns[l])) + ',"lly":') + 
                                        (llySigns[l])) + ',"urx":') + (urxSigns[l])) + ',"ury":') + (urySigns[l])) + '}')
                                    } else {
                                        'isi signlocStoreDB'
                                        signlocStoreDB = (((((((((signlocStoreDB + '{"llx":') + (llxSigns[l])) + ',"lly":') + 
                                        (llySigns[l])) + ',"urx":') + (urxSigns[l])) + ',"ury":') + (urySigns[l])) + '};')
                                    }
                                }
                            } else {
                                if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
                                    'Isi bodyAPI'
                                    bodyAPI = ((bodyAPI + (seqNoBodyAPI[t])) + ',"signLocations": [')

                                    'Jika pageSign yang pertama di signer pertama kosong'
                                    if ((pageSigns[l]) == '') {
                                        'Input body mengenai x dan y'
                                        bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxSigns[l])) + ', "lly" : ') + (llySigns[
                                        l])) + ', "urx" : ') + (urxSigns[l])) + ', "ury" : ') + (urySigns[l])) + '},')
                                    } else if ((llxSigns[l]) == '""') {
                                        'Input body mengenai page'
                                        bodyAPI = (((bodyAPI + '{"page" : ') + (pageSigns[l])) + '},')
                                    } else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
                                        'Input body mengenai page dan koordinat'
                                        bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageSigns[l])) + ', "llx" : ') + 
                                        (llxSigns[l])) + ', "lly" : ') + (llySigns[l])) + ', "urx" : ') + (urxSigns[l])) + 
                                        ', "ury" : ') + (urySigns[l])) + '},')
                                    }
                                    
                                    'isi signlocStoreDB'
                                    signlocStoreDB = (((((((((signlocStoreDB + '{"llx":') + (llxSigns[l])) + ',"lly":') + 
                                    (llySigns[l])) + ',"urx":') + (urxSigns[l])) + ',"ury":') + (urySigns[l])) + '};')
                                }
                            }
                        } else if (l == (pageSigns.size() - 1)) {
                            if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
                                'Jika pageSign yang pertama di signer pertama kosong'
                                if ((pageSigns[l]) == '') {
                                    'Input body mengenai koordinat'
                                    bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxSigns[l])) + ', "lly" : ') + (llySigns[
                                    l])) + ', "urx" : ') + (urxSigns[l])) + ', "ury" : ') + (urySigns[l])) + '}]')
                                } else if ((llxSigns[l]) == '""') {
                                    'Input body mengenai page'
                                    bodyAPI = (((bodyAPI + '{"page" : ') + (pageSigns[l])) + '}]')
                                } else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
                                    'Input body mengenai page dan koordinat'
                                    bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageSigns[l])) + ', "llx" : ') + (llxSigns[
                                    l])) + ', "lly" : ') + (llySigns[l])) + ', "urx" : ') + (urxSigns[l])) + ', "ury" : ') + 
                                    (urySigns[l])) + '}]')
                                }
                            }
                            
                            'Jika loopingan sudah di akhir'
                            if (t == (signActions.size() - 1)) {
                                'isi signlocStoreDB'
                                signlocStoreDB = (((((((((signlocStoreDB + '{"llx":') + (llxSigns[l])) + ',"lly":') + (llySigns[
                                l])) + ',"urx":') + (urxSigns[l])) + ',"ury":') + (urySigns[l])) + '}')
                            } else {
                                'isi signlocStoreDB'
                                signlocStoreDB = (((((((((signlocStoreDB + '{"llx":') + (llxSigns[l])) + ',"lly":') + (llySigns[
                                l])) + ',"urx":') + (urxSigns[l])) + ',"ury":') + (urySigns[l])) + '};')
                            }
                        } else {
                            if (((pageSigns[l]) != '') || ((llxSigns[l]) != '""')) {
                                'Jika pageSign yang pertama di signer pertama kosong'
                                if ((pageSigns[l]) == '') {
                                    'Input body mengenai koordinat'
                                    bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxSigns[l])) + ', "lly" : ') + (llySigns[
                                    l])) + ', "urx" : ') + (urxSigns[l])) + ', "ury" : ') + (urySigns[l])) + '},')
                                } else if ((llxSigns[l]) == '""') {
                                    'Input body mengenai page'
                                    bodyAPI = (((bodyAPI + '{"page" : ') + (pageSigns[l])) + '},')
                                } else if (((pageSigns[l]) != '') && ((llxSigns[l]) != '""')) {
                                    'Input body mengenai page dan koordinat'
                                    bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageSigns[l])) + ', "llx" : ') + (llxSigns[
                                    l])) + ', "lly" : ') + (llySigns[l])) + ', "urx" : ') + (urxSigns[l])) + ', "ury" : ') + 
                                    (urySigns[l])) + '},')
                                }
                            }
                            
                            'isi signlocStoreDB'
                            signlocStoreDB = (((((((((signlocStoreDB + '{"llx":') + (llxSigns[l])) + ',"lly":') + (llySigns[
                            l])) + ',"urx":') + (urxSigns[l])) + ',"ury":') + (urySigns[l])) + '};')
                        }
                    }
                }
                
                'Jika signAction yang pertama untuk dokumen pertama'
                if (t == 0) {
                    if (t == (signActions.size() - 1)) {
                        'isi bodyAPI dengan bodyAPI yang atas'
                        bodyAPI = ((((((((((('"signers" : [{"signAction": ' + (signActions[t])) + ',"signerType": ') + (signerTypes[
                        t])) + ',"tlp": ') + (tlps[t])) + ',"idKtp": ') + (idKtps[t])) + ',"email": ') + (emails[t])) + 
                        bodyAPI) + '}],')
                    } else {
                        'isi bodyAPI dengan bodyAPI yang atas'
                        bodyAPI = ((((((((((('"signers" : [{"signAction": ' + (signActions[t])) + ',"signerType": ') + (signerTypes[
                        t])) + ',"tlp": ') + (tlps[t])) + ',"idKtp": ') + (idKtps[t])) + ',"email": ') + (emails[t])) + 
                        bodyAPI) + '},')
                    }
                }
                
                if (t == (signActions.size() - 1)) {
                    'isi bodyAPI dengan bodyAPI yang atas'
                    bodyAPI = ((((((((((('{"signAction": ' + (signActions[t])) + ',"signerType": ') + (signerTypes[t])) + 
                    ',"tlp": ') + (tlps[t])) + ',"idKtp": ') + (idKtps[t])) + ',"email": ') + (emails[t])) + bodyAPI) + 
                    '}],')
                } else {
                    'isi bodyAPI dengan bodyAPI yang atas'
                    bodyAPI = ((((((((((('{"signAction": ' + (signActions[t])) + ',"signerType": ') + (signerTypes[t])) + 
                    ',"tlp": ') + (tlps[t])) + ',"idKtp": ') + (idKtps[t])) + ',"email": ') + (emails[t])) + bodyAPI) + 
                    '},')
                }
                
                'check ada value maka setting email service tenant'
                if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')).length() > 
                0) {
                    'setting email service tenant'
                    CustomKeywords.'connection.APIFullService.settingEmailServiceVendorRegisteredUser'(conneSign, findTestData(
                            excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')), 
                        (emails[t]).replace('"', ''))
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

            'Jika informasi di excel mengenai stampLocation seperti page dan koordinat ada, maka'
            if (!(((((findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('page (stamp)')).length() == 
            0) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('llx (stamp)')).length() == 
            0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('lly (stamp)')).length() == 
            0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('urx (stamp)')).length() == 
            0)) && (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('ury (stamp)')).length() == 
            0))) {
                'Splitting dari dokumen pertama per signer mengenai stamping'
                pageStamps = (pageStamp[i]).split(semicolon, splitnum)

                llxStamps = (llxStamp[i]).split(semicolon, splitnum)

                llyStamps = (llyStamp[i]).split(semicolon, splitnum)

                urxStamps = (urxStamp[i]).split(semicolon, splitnum)

                uryStamps = (uryStamp[i]).split(semicolon, splitnum)

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
                                    bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxStamps[b])) + ', "lly" : ') + (llyStamps[
                                    b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + (uryStamps[b])) + '}]')
                                } else if ((llxStamps[b]) == '""') {
                                    'Input body mengenai page'
                                    bodyAPI = (((bodyAPI + '{"page" : ') + (pageStamps[b])) + '}]')
                                } else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
                                    'Input body mengenai page dan koordinat'
                                    bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageStamps[b])) + ', "llx" : ') + (llxStamps[
                                    b])) + ', "lly" : ') + (llyStamps[b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + 
                                    (uryStamps[b])) + '}]')
                                }
                            }
                        } else {
                            if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {
                                'Isi bodyAPI'
                                bodyAPI = (bodyAPI + ',"stampLocations": [')

                                'Jika pageStampnya kosong'
                                if ((pageStamps[b]) == '') {
                                    'Input body mengenai koordinat'
                                    bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxStamps[b])) + ', "lly" : ') + (llyStamps[
                                    b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + (uryStamps[b])) + '},')
                                } else if ((llxStamps[b]) == '""') {
                                    'Input body mengenai page'
                                    bodyAPI = (((bodyAPI + '{"page" : ') + (pageStamps[b])) + '},')
                                } else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
                                    'Input body mengenai page dan koordinat'
                                    bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageStamps[b])) + ', "llx" : ') + (llxStamps[
                                    b])) + ', "lly" : ') + (llyStamps[b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + 
                                    (uryStamps[b])) + '},')
                                }
                            }
                        }
                    } else if (b == (pageStamps.size() - 1)) {
                        if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {
                            'Jika pageStampnya kosong'
                            if ((pageStamps[b]) == '') {
                                'Input body mengenai koordinat'
                                bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxStamps[b])) + ', "lly" : ') + (llyStamps[
                                b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + (uryStamps[b])) + '}]')
                            } else if ((llxStamps[b]) == '""') {
                                'Input body mengenai page'
                                bodyAPI = (((bodyAPI + '{"page" : ') + (pageStamps[b])) + '}]')
                            } else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
                                'Input body mengenai page dan koordinat'
                                bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageStamps[b])) + ', "llx" : ') + (llxStamps[
                                b])) + ', "lly" : ') + (llyStamps[b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + 
                                (uryStamps[b])) + '}]')
                            }
                        }
                    } else {
                        if (((pageStamps[b]) != '') || ((llxStamps[b]) != '""')) {
                            'Jika pageStampnya kosong'
                            if ((pageStamps[b]) == '') {
                                'Input body mengenai koordinat'
                                bodyAPI = (((((((((bodyAPI + '{"llx" : ') + (llxStamps[b])) + ', "lly" : ') + (llyStamps[
                                b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + (uryStamps[b])) + '},')
                            } else if ((llxStamps[b]) == '""') {
                                'Input body mengenai page'
                                bodyAPI = (((bodyAPI + '{"page" : ') + (pageStamps[b])) + '},')
                            } else if (((pageStamps[b]) != '') && ((llxStamps[b]) != '""')) {
                                'Input body mengenai page dan koordinat'
                                bodyAPI = (((((((((((bodyAPI + '{"page" : ') + (pageStamps[b])) + ', "llx" : ') + (llxStamps[
                                b])) + ', "lly" : ') + (llyStamps[b])) + ', "urx" : ') + (urxStamps[b])) + ', "ury" : ') + 
                                (uryStamps[b])) + '},')
                            }
                        }
                    }
                }
            }
            
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
        
        'Jika flag tenant no'
        if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 
        'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Wrong tenant Code'))
        } else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('use Correct Tenant Code')) == 
        'Yes') {
            'Input tenant'
            GlobalVariable.Tenant = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('$tenantCode'))
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.APIFullService.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('use Correct API Key')) == 
        'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, rowExcel('Wrong API Key'))
        }
        
        'Hit API'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Send Document Signing', [('tenantCode') : GlobalVariable.Tenant.replace(
                        '"', ''), ('request') : stringRefno, ('callerId') : findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 
                        rowExcel('callerId')).replace('"', '')]))

        'jika response 200 / hit api berhasil'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'ambil respon text dalam bentuk code.'
            status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika status codenya 0, verifikasi datanya benar'
            if (status_Code == 0) {
                'mengambil documentid, trxno dan response doc template code dari hasil response API'
                documentId = WS.getElementPropertyValue(respon, 'documents.documentId', FailureHandling.OPTIONAL)

                trxno = WS.getElementPropertyValue(respon, 'documents.trxNos', FailureHandling.OPTIONAL)

                responseDocTemplateCode = WS.getElementPropertyValue(respon, 'documents.docTemplateCode', FailureHandling.OPTIONAL)

                'Jika doc template code di excel tidak sesuai dengan response doc template code'
                if (documentTemplateCode.toString().replace('"', '') != responseDocTemplateCode.toString()) {
                    'write to excel status failed dan reason failed h'
                    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, ((findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 
                            2).replace('-', '') + semicolon) + GlobalVariable.ReasonFailedSaveGagal) + ' pada perbedaan document template code ')
                }
                
                'Memasukkan documentid dan trxno ke dalam excel'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 5, GlobalVariable.NumofColm - 
                    1, documentId.toString().replace('[', '').replace(']', ''))

                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 6, GlobalVariable.NumofColm - 
                    1, trxno.toString().replace('[', '').replace(']', ''))

                'write to excel success'
                CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, GlobalVariable.NumofColm - 
                    1, GlobalVariable.StatusSuccess)

                'Jika check storedb'
                if (GlobalVariable.checkStoreDB == 'Yes') {
                    'call test case storedb'
                    WebUI.callTestCase(findTestCase('APIFullService/Send Document/API Send Document Store DB'), [('excelPathAPISendDoc') : excelPathAPISendDoc
                            , ('sheet') : sheet, ('signlocStoreDB') : signlocStoreDB], FailureHandling.CONTINUE_ON_FAILURE)
                }
            } else {
                getErrorMessageAPI(respon)
            }
        } else {
            getErrorMessageAPI(respon)
        }
    }
}

def getErrorMessageAPI(ResponseObject respon) {
    'mengambil status code berdasarkan response HIT API'
    message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

    'Write To Excel GlobalVariable.StatusFailed and errormessage'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
        ('<' + message) + '>')

    GlobalVariable.FlagFailed = 1
}

def rowExcel(String cellValue) {
    CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

