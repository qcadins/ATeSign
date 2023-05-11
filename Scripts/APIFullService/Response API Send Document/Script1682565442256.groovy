import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'looping berdasarkan total kolom'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(excelPathAPISendDoc).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'Deklarasi variable mengenai signLoc untuk store db'
        String signlocStoreDB = new String()

        'Inisialisasi ref No berdasarkan delimiter ;'
        refNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 11).split(';', -1)

        'Inisialisasi document template code berdasarkan delimiter ;'
        documentTemplateCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 12).split(';', -1)

        'Inisialisasi document name berdasarkan delimiter ;'
        documentName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 13).split(';', -1)

        'Inisialisasi office Code berdasarkan delimiter ;'
        officeCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 14).split(';', -1)

        'Inisialisasi office name berdasarkan delimiter ;'
        officeName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 15).split(';', -1)

        'Inisialisasi region code berdasarkan delimiter ;'
        regionCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 16).split(';', -1)

        'Inisialisasi region name berdasarkan delimiter ;'
        regionName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 17).split(';', -1)

        'Inisialisasi business line code berdasarkan delimiter ;'
        businessLineCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 18).split(';', -1)

        'Inisialisasi business line name berdasarkan delimiter ;'
        businessLineName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 19).split(';', -1)

        'Inisialisasi document file berdasarkan delimiter ;'
        documentFile = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 20).split(';', -1)

        'split signer untuk doc1 dan signer untuk doc2'
        signAction = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 28).split('\\|', -1)

        signerType = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 29).split('\\|', -1)

        tlp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 30).split('\\|', -1)

        idKtp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 31).split('\\|', -1)

        email = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 32).split('\\|', -1)

        pageStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 22).split('\\|', -1)

        llxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 23).split('\\|', -1)

        llyStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 24).split('\\|', -1)

        urxStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 25).split('\\|', -1)

        uryStamp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 26).split('\\|', -1)

        'Deklarasi variable string Ref no untuk full body API.'
        String stringRefno = new String()

        'Looping berdasarkan total dari dokumen file ukuran'
        for (int i = 0; i < documentFile.size(); i++) {
			
            'signloc store db harus dikosongkan untuk loop dokumen selanjutnya.'
            signlocStoreDB = ''

            'Splitting kembali dari dokumen pertama per signer'
            signActions = signAction[i].split(';', -1)

            signerTypes = signerType[i].split(';', -1)

            tlps = tlp[i].split(';', -1)

            idKtps = idKtp[i].split(';', -1)

            emails = email[i].split(';', -1)

            'Splitting dari dokumen pertama per signer mengenai stamping'
            pageStamps = pageStamp[i].split(';', -1)

            llxStamps = llxStamp[i].split(';', -1)

            llyStamps = llyStamp[i].split(';', -1)

            urxStamps = urxStamp[i].split(';', -1)

            uryStamps = uryStamp[i].split(';', -1)

            'inisialisasi bodyAPI untuk menyusun body'
            String bodyAPI = new String()

            'Pengisian body'
            bodyAPI = bodyAPI + '{"referenceNo" : ' + refNo[i] + ', "documentTemplateCode": ' + 
            documentTemplateCode[i] + ', "documentName": ' + documentName[i] + ', "officeCode": ' + officeCode[
            i] + ', "officeName": ' + officeName[i] + ', "regionCode": ' + regionCode[i] + ', "regionName": ' + 
            regionName[i] + ', "businessLineCode": ' + businessLineCode[i] + ', "businessLineName": ' + businessLineName[
            i] + ','

            'Memasukkan bodyAPI ke stringRefno'
            stringRefno = stringRefno + bodyAPI

            'inisialisasi bodyAPI untuk menyusun body'
            bodyAPI = new String()

            'looping berdasarkan jumlah dari signAction di dokumen pertama'
            for (int t = 0; t < signActions.size(); t++) {
				'Jika semua data mengenai Sign Location seperti page, llx, lly, urx, ury tidak kosong'
				if(!(findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm,34).length() == 0 && findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 35).length() == 0 && findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 36).length() == 0 && findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 37).length() == 0 && findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 38).length() == 0)) {
					
                'Split mengenai signLocation dimana berdasarkan dokumen'
                pageSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 34).split('\\|\\|', -1)

                llxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 35).split('\\|\\|', -1)

                llySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 36).split('\\|\\|', -1)

                urxSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 37).split('\\|\\|', -1)

                urySign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 38).split('\\|\\|', -1)
				
                'Split mengenai signLocation dimana berdasarkan dokumen dan berdasarkan signer'
                pageSigns = pageSign[i].split('\\|', -1)

                llxSigns = llxSign[i].split('\\|', -1)

                llySigns = llySign[i].split('\\|', -1)

                urxSigns = urxSign[i].split('\\|', -1)

                urySigns = urySign[i].split('\\|', -1)
				
                'Split mengenai signLocation dimana berdasarkan dokumen dan berdasarkan signer. Didapatlah semua lokasi signLocation di satu signer.'
                pageSigns = pageSigns[t].split(';', -1)

                llxSigns = llxSigns[t].split(';', -1)

                llySigns = llySigns[t].split(';', -1)

                urxSigns = urxSigns[t].split(';', -1)

                urySigns = urySigns[t].split(';', -1)
				
                'looping menuju jumlah lokasi pageSign di 1 signer'
                for (int l = 0; l < pageSigns.size(); l++) {
					'Jika loopingan pertama'
                    if (l == 0) {
						'Jika dari loopingan pertama, pageSignnya hanya ada 1 dan yang terakhir'
                        if (l == pageSigns.size() - 1) {
                            'Isi bodyAPI'
                            bodyAPI = bodyAPI + ',"signLocations": ['
							
							'Jika pageSign untuk yang pertama di signer pertama kosong'
                            if (pageSigns[l] == '') {
								'Input body mengenai llx dan lly'
                                bodyAPI = bodyAPI + '{"llx" : ' + llxSigns[l] + ', "lly" : ' + llySigns[l] + 
								', "urx" : ' + urxSigns[l] + ', "ury" : ' + urySigns[l] + '}]'
                            } 
							//Jika koordinat nya untuk yang pertama di signer pertama kosong
							else if(llxSigns[l] == '""'){
								'Input body mengenai page'
                                bodyAPI = bodyAPI + '{"page" : ' + pageSigns[l] + '}]'
                            }
							//Jika page dan koordinatnya tidak kosong, maka 
							else if(pageSigns[l] != '' && llxSigns[l] != '""'){
								'Input body mengenai page dan llx,lly,urx, dan ury'
								bodyAPI = bodyAPI + '{"page" : ' + pageSigns[l] + ', "llx" : ' + llxSigns[l] + 
								', "lly" : ' + llySigns[l] + ', "urx" : ' + urxSigns[l] + ', "ury" : ' +
								urySigns[l] + '}]'
							}
                            
                            'isi signlocStoreDB'
                            signlocStoreDB = signlocStoreDB + '{"llx":' + llxSigns[l] + ',"lly":' +
								 llySigns[l] + ',"urx":' + urxSigns[l] + ',"ury":' + urySigns[l] + '}'
                        } 
						//Jika tidak yang terakhir, maka
						else {
                            'Isi bodyAPI'
                            bodyAPI = bodyAPI + ',"signLocations": ['
							
							'Jika pageSign yang pertama di signer pertama kosong'
                            if (pageSigns[l] == '') {
								'Input body mengenai x dan y'
                                bodyAPI = bodyAPI + '{"llx" : ' + llxSigns[l] + ', "lly" : ' + llySigns[l] +
								 ', "urx" : ' + urxSigns[l] + ', "ury" : ' + urySigns[l] + '},'
                            } 
							//Jika koordinat pada signLoc yang pertama di signer pertama kosong, maka
							else if(llxSigns[l] == '""'){
								'Input body mengenai page'
                                bodyAPI = bodyAPI + '{"page" : ' + pageSigns[l] + '},'
                            }
							//Jika pageSign yang pertama dan koordinat yang pertama di signer pertama tidak kosong
							else if(pageSigns[l] != '' && llxSigns[l] != '""'){
								'Input body mengenai page dan koordinat'
								bodyAPI = bodyAPI + '{"page" : ' + pageSigns[l] + ', "llx" : ' + llxSigns[l] +
								 ', "lly" : ' + llySigns[l] + ', "urx" : ' + urxSigns[l] + ', "ury" : ' +
									urySigns[l] + '},'
							}
							'isi signlocStoreDB'
                            signlocStoreDB = signlocStoreDB + '{"llx":' + llxSigns[l] + ',"lly":' + llySigns[l] +
								 ',"urx":' + urxSigns[l] + ',"ury":' + urySigns[l] + '};'
                        }
                    }
					//Jika loopingan sudah berada di akhir
					 else if (l == pageSigns.size() - 1) {
						 	'Jika pageSign yang pertama di signer pertama kosong'
                            if (pageSigns[l] == '') {
								'Input body mengenai koordinat'
                                bodyAPI = bodyAPI + '{"llx" : ' + llxSigns[l] + ', "lly" : ' + llySigns[l] +
								 ', "urx" : ' + urxSigns[l] + ', "ury" : ' + urySigns[l] + '}]'
                            } 
							//Jika koordinat yang pertama di signer pertama kosong 
							else if(llxSigns[l] == '""'){
								'Input body mengenai page'
                                bodyAPI = bodyAPI + '{"page" : ' + pageSigns[l] + '}]'
                            }
							//Jika page dan koordinat yang pertama di signer pertama tidak kosong 
							else if(pageSigns[l] != '' && llxSigns[l] != '""'){
								'Input body mengenai page dan koordinat'
								bodyAPI = bodyAPI + '{"page" : ' + pageSigns[l] + ', "llx" : ' + llxSigns[l] +
								 ', "lly" : ' + llySigns[l] + ', "urx" : ' + urxSigns[l] + ', "ury" : ' +
									urySigns[l] + '}]'
							}
                        
							'Jika loopingan sudah di akhir'
                        if (t == signActions.size() - 1) {
                            'isi signlocStoreDB'
                            signlocStoreDB = signlocStoreDB + '{"llx":' + llxSigns[l] + ',"lly":' +
								 llySigns[l] + ',"urx":' + urxSigns[l] + ',"ury":' + urySigns[l] + '}'
                        } else {
                            'isi signlocStoreDB'
                            signlocStoreDB = signlocStoreDB + '{"llx":' + llxSigns[l] + ',"lly":' +
								 llySigns[l] + ',"urx":' + urxSigns[l] + ',"ury":' + urySigns[l] + '};'
                        }
                    } 
					//Jika loopingan tidak diawal dan diakhir
					else {
							'Jika pageSign yang pertama di signer pertama kosong'
                            if (pageSigns[l] == '') {
								'Input body mengenai koordinat'
                                bodyAPI = bodyAPI + '{"llx" : ' + llxSigns[l] + ', "lly" : ' + llySigns[l] +
								 ', "urx" : ' + urxSigns[l] + ', "ury" : ' + urySigns[l] + '},'
                            } 
							//Jika koordinat yang pertama di signer pertama kosong
							else if(llxSigns[l] == '""'){
								'Input body mengenai page'
                                bodyAPI = bodyAPI + '{"page" : ' + pageSigns[l] + '},'
                            }
							//Jika page dan koordinat tidak kosong
							else if(pageSigns[l] != '' && llxSigns[l] != '""'){
								'Input body mengenai page dan koordinat'
								bodyAPI = bodyAPI + '{"page" : ' + pageSigns[l] + ', "llx" : ' + llxSigns[l] +
								 ', "lly" : ' + llySigns[l] + ', "urx" : ' + urxSigns[l] + ', "ury" : ' +
									urySigns[l] + '},'
							}
                        
                        'isi signlocStoreDB'
                        signlocStoreDB = signlocStoreDB + '{"llx":' + llxSigns[l] + ',"lly":' +
							 llySigns[l] + ',"urx":' + urxSigns[l] + ',"ury":' + urySigns[l] + '};'
                    }
                }
                }
                'Jika signAction yang pertama untuk dokumen pertama'
                if (t == 0) {
                    if (t == signActions.size() - 1) {
                        'isi bodyAPI dengan bodyAPI yang atas'
                        bodyAPI = '"signers" : [{"signAction": ' + signActions[t] + ',"signerType": ' + 
                        signerTypes[t] + ',"tlp": ' + tlps[t] + ',"idKtp": ' + idKtps[t] + ',"email": ' + 
                        emails[t] + bodyAPI + '}],'
                    } else {
                        'isi bodyAPI dengan bodyAPI yang atas'
                        bodyAPI = '"signers" : [{"signAction": ' + signActions[t] + ',"signerType": ' + 
                        signerTypes[t] + ',"tlp": ' + tlps[t] + ',"idKtp": ' + idKtps[t] + ',"email": ' + 
                        emails[t] + bodyAPI + '},'
                    }
                } else if (t == signActions.size() - 1) {
                    'isi bodyAPI dengan bodyAPI yang atas'
                    bodyAPI = '{"signAction": ' + signActions[t] + ',"signerType": ' + 
					signerTypes[t] + ',"tlp": ' + tlps[t] + ',"idKtp": ' + (idKtps[t]) + ',"email": ' + 
					emails[t] + bodyAPI + '}],'
                } else {
                    'isi bodyAPI dengan bodyAPI yang atas'
                    bodyAPI = '{"signAction": ' + signActions[t] + ',"signerType": ' +
					signerTypes[t] + ',"tlp": ' + tlps[t] + ',"idKtp": ' + idKtps[t] + ',"email": ' +
					emails[t] + bodyAPI + '},'
                }
                
                'Memasukkan bodyAPI ke stringRefno'
                stringRefno = stringRefno + bodyAPI

                'Mengkosongkan bodyAPI untuk digunakan selanjutnya'
                bodyAPI = ''
            }
            
            'Deklarasi bodyAPI kembali'
            bodyAPI = new String()

            'Jika dokumennya menggunakan base64'
            if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 46) == 'Yes') {
                'input bodyAPI dengan Base64'
                bodyAPI = bodyAPI + '"documentFile": "' + PDFtoBase64(documentFile[i]) + '"'
            } else {
                'input bodyAPI tidak dengan Base64'
                bodyAPI = bodyAPI + '"documentFile": "' + (documentFile[i]) + '"'
            }
            
            'Input bodyAPI ke stringRefno'
            stringRefno = stringRefno + bodyAPI

            'Mengkosongkan bodyAPI'
            bodyAPI = ''
			'Jika informasi di excel mengenai stampLocation seperti page dan koordinat ada, maka'
			if(!(findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm,22).length() == 0 && findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 23).length() == 0 && findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 24).length() == 0 && findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 25).length() == 0 && findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 26).length() == 0)) {
				
                'looping berdasarkan pagestamp per dokumen'
                for (int b = 0; b < pageStamps.size(); b++) {
                    'Jika dia loopingan yang pertama'
                    if (b == 0) {
						if (b == (pageStamps.size() - 1)) {
							'Isi bodyAPI'
							bodyAPI = (bodyAPI + ',"stampLocations": [')
							
							'Jika pageStampnya kosong'
							if (pageStamps[b] == '') {
								'Input body mengenai koordinat'
								bodyAPI = bodyAPI + '{"llx" : ' + llxStamps[b] + ', "lly" : ' + llyStamps[b] +
								 ', "urx" : ' + urxStamps[b] + ', "ury" : ' + uryStamps[b] + '}]'
							} 
							//Jika koordinatnya kosong
							else if(llxStamps[b] == '""'){
								'Input body mengenai page'
								bodyAPI = bodyAPI + '{"page" : ' + pageStamps[b] + '}]'
							}
							//Jika page dan koordinat tidak kosong
							else if(pageStamps[b] != '' && llxStamps[b] != '""'){
								'Input body mengenai page dan koordinat'
								bodyAPI = bodyAPI + '{"page" : ' + pageStamps[b] + ', "llx" : ' + llxStamps[b] +
								 ', "lly" : ' + llyStamps[b] + ', "urx" : ' + urxStamps[b] + ', "ury" : ' +
									uryStamps[b] + '}]'
							}
							
						} 
						//Jika loopingan yang pertama namun masih ada kelanjutan dalam data
						else {
							'Isi bodyAPI'
							bodyAPI = (bodyAPI + ',"stampLocations": [')
							'Jika pageStampnya kosong'
							if (pageStamps[b] == '') {
								'Input body mengenai koordinat'
								bodyAPI = bodyAPI + '{"llx" : ' + llxStamps[b] + ', "lly" : ' + llyStamps[b] +
								 ', "urx" : ' + urxStamps[b] + ', "ury" : ' + uryStamps[b] + '},'
							} 
							//Jika koordinatnya kosong
							else if(llxStamps[b] == '""'){
								'Input body mengenai page'
								bodyAPI = bodyAPI + '{"page" : ' + pageStamps[b] + '},'
							}
							//Jika page dan koordinat tidak kosong
							else if(pageStamps[b] != '' && llxStamps[b] != '""'){
								'Input body mengenai page dan koordinat'
								bodyAPI = bodyAPI + '{"page" : ' + pageStamps[b] + ', "llx" : ' + llxStamps[b] +
								 ', "lly" : ' + llyStamps[b] + ', "urx" : ' + urxStamps[b] + ', "ury" : ' +
									uryStamps[b] + '},'
							}
							
						}

                    } 
					//Jika loopingan telah di akhir
					else if (b == (pageStamps.size() - 1)) {
						'Jika pageStampnya kosong'
                            if (pageStamps[b] == '') {
								'Input body mengenai koordinat'
                                bodyAPI = bodyAPI + '{"llx" : ' + llxStamps[b] + ', "lly" : ' + llyStamps[b] +
								 ', "urx" : ' + urxStamps[b] + ', "ury" : ' + uryStamps[b] + '}]'
                            } 
							//Jika koordinatnya kosong
							else if(llxStamps[b] == '""'){
								'Input body mengenai page'
                                bodyAPI = bodyAPI + '{"page" : ' + pageStamps[b] + '}]'
                            }
							//Jika page dan koordinat tidak kosong
							else if(pageStamps[b] != '' && llxStamps[b] != '""'){
								'Input body mengenai page dan koordinat'
								bodyAPI = bodyAPI + '{"page" : ' + pageStamps[b] + ', "llx" : ' + llxStamps[b] +
								 ', "lly" : ' + llyStamps[b] + ', "urx" : ' + urxStamps[b] + ', "ury" : ' +
									uryStamps[b] + '}]'
							}
                    } 
					//Jika loopingan masih berlanjut
					else {
						'Jika pageStampnya kosong'
                            if (pageStamps[b] == '') {
								'Input body mengenai koordinat'
                                bodyAPI = bodyAPI + '{"llx" : ' + llxStamps[b] + ', "lly" : ' + llyStamps[b] +
								 ', "urx" : ' + urxStamps[b] + ', "ury" : ' + uryStamps[b] + '},'
                            }
							//Jika koordinatnya kosong
							 else if(llxStamps[b] == '""'){
								 'Input body mengenai page'
                                bodyAPI = bodyAPI + '{"page" : ' + pageStamps[b] + '},'
                            }
							//Jika page dan koordinat tidak kosong
							else if(pageStamps[b] != '' && llxStamps[b] != '""'){
								'Input body mengenai page dan koordinat'
								bodyAPI = bodyAPI + '{"page" : ' + pageStamps[b] + ', "llx" : ' + llxStamps[b] +
								 ', "lly" : ' + llyStamps[b] + ', "urx" : ' + urxStamps[b] + ', "ury" : ' +
									uryStamps[b] + '},'
							}
                    }
                }
			}
            
            'jika dokumennya di akhir'
            if (i == documentFile.size() - 1) {
                'input body API berdasarkan bodyAPI diatasnya'
                bodyAPI = bodyAPI + '}'
            } else {
                'input body API berdasarkan bodyAPI diatasnya'
                bodyAPI = bodyAPI + '},'
            }
            
            'input body API kedalam stringRefno'
            stringRefno = stringRefno + bodyAPI
        }
		
        'Jika flag tenant no'
        if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 44) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 45)
        } else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 44) == 'Yes') {
            'Input tenant'
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 42) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.DataVerif.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 42) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 43)
        }

        'Hit API'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Send Document Signing', [('tenantCode') : findTestData(
                        excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 9), ('request') : stringRefno, ('callerId') : findTestData(
                        excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 40)]))

        'jika response 200 / hit api berhasil'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'ambil respon text dalam bentuk code.'
            status_Code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'jika status codenya 0, verifikasi datanya benar'
            if (status_Code == 0) {
                'mengambil documentid dan trxno dari hasil response API'
                documentId = WS.getElementPropertyValue(respon, 'documents.documentId', FailureHandling.OPTIONAL)

                trxno = WS.getElementPropertyValue(respon, 'documents.trxNos', FailureHandling.OPTIONAL)

                'Memasukkan documentid dan trxno ke dalam excel'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
                    4, GlobalVariable.NumofColm - 1, documentId.toString().replace('[', '').replace(']', ''))

                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
                    5, GlobalVariable.NumofColm - 1, trxno.toString().replace('[', '').replace(']', ''))

                'write to excel success'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Send Document', 
                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                'Jika check storedb'
                if (GlobalVariable.checkStoreDB == 'Yes') {
                    'Fungsi storedb'
                    ResponseAPIStoreDB(signlocStoreDB)
                }
            } else {
                'Mengambil message Failed'
                messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

                'write to excel status failed dan reason'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 2).replace(
                        '-', '') + ';') + messageFailed)
            }
        } else {
            'mengambil message Failed'
            messageFailed = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL).toString()

            'write to excel status failed dan reason'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 2).replace(
                    '-', '') + ';') + messageFailed.toString())
        }
    }
}

'Fungsi PDF to Base64'
def PDFtoBase64(String fileName) {
    return CustomKeywords.'customizeKeyword.ConvertFile.base64File'(fileName)
}

'Fungsi storedb'
def ResponseAPIStoreDB(String signlocStoreDB) {
    'connect DB eSign'
    Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

    'declare arraylist arraymatch'
    arrayMatch = []

    'Mengambil documentid di excel dan displit'
    docid = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 5).split(', ', -1)

    'split signer untuk doc1 dan signer untuk doc2'
    signAction = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 28).split('\\|', 
        -1)

    signerType = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 29).split('\\|', 
        -1)

    tlp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 30).split('\\|', -1)

    idKtp = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 31).split('\\|', -1)

    email = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 32).split('\\|', -1)

    refNo = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 11).split(';', -1)

    'looping berdasarkan jumlah dari document id '
    for (int i = 0; i < docid.size(); i++) {
        'Inisialisasi document template code berdasarkan delimiter ;'
        documentTemplateCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 12).split(';', -1)

        'Inisialisasi document template code berdasarkan delimiter ;'
        documentName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 13).split(';', -1)

        'Inisialisasi office Code berdasarkan delimiter ;'
        officeCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 14).split(';', -1)

        'Inisialisasi office name berdasarkan delimiter ;'
        officeName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 15).split(';', -1)

        'Inisialisasi region code berdasarkan delimiter ;'
        regionCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 16).split(';', -1)

        'Inisialisasi region name berdasarkan delimiter ;'
        regionName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 17).split(';', -1)

        'Inisialisasi business line code berdasarkan delimiter ;'
        businessLineCode = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 18).split(';', -1)

        'Inisialisasi business line name berdasarkan delimiter ;'
        businessLineName = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 19).split(';', -1)

        'Inisialisasi pageSign berdasarkan delimiter ||'
        pageSign = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 34).split('\\|\\|', -1)

        'get data API Send Document dari DB (hanya 1 signer)'
        result = CustomKeywords.'connection.DataVerif.getSendDocSigning'(conneSign, docid[i])

        'declare arrayindex'
        arrayindex = 0

        'Jika documentTemplateCode di dokumen pertama adalah kosong'
        if ((documentTemplateCode[i]).replace('"', '') == '') {
            'Maka pengecekan signlocation yang diinput'
            arrayMatch.add(WebUI.verifyMatch(CustomKeywords.'connection.DataVerif.getSignLocation'(conneSign, docid[i]), 
                    signlocStoreDB, false, FailureHandling.CONTINUE_ON_FAILURE))
        }
        
        'get current date'
        def currentDate = new Date().format('yyyy-MM-dd')

        'Split result dari email berdasarkan db'
        EmailDB = result[arrayindex++].split(';', -1)

        'Split result dari signerType berdasarkan db'
        SignerTypeDB = result[arrayindex++].split(';', -1)

        'Split result dari signerType per signer berdasarkan excel yang telah displit per dokumen. '
        SignerTypeExcel = signerType[i].replace('"', '').split(';', -1)

        'Deklarasi index Email dan index Signer Type'
        indexEmail = 0

        indexSignerType = 0

        'Splitting email berdasarkan excel per dokumen'
        EmailExcel = email[i].split(';', -1)

        'Jika document Template pada excel tidak kosong'
        if (documentTemplateCode[i].replace('"', '') != '') {
            'Mengambil value tipe signer berdasarkan tipe dokumen template'
            resultDocTemplate = CustomKeywords.'connection.DataVerif.getSignerTypeonDocTemplate'(conneSign, (documentTemplateCode[
                i]).replace('"', ''))

            'Looping berdasarkan jumlah email per dokumen di excel'
            for (int c = 0; c < EmailExcel.size(); c++) {
                'Jika hasil dari db ada pada excel mengenai signer type per signer'
                if (resultDocTemplate.contains(SignerTypeExcel[c].replace('"', ''))) {
                    'Jika email pertama di dokumen pertama tidak kosong'
                    if (EmailExcel[c] != '""') {
                        'Verify email'
                        arrayMatch.add(WebUI.verifyMatch(EmailExcel[c].replace('"', ''), EmailDB[indexEmail++], false, 
                                FailureHandling.CONTINUE_ON_FAILURE))
                    }
                    
                    'verify signerType'
                    arrayMatch.add(WebUI.verifyMatch(SignerTypeExcel[c].replace('"', ''), SignerTypeDB[indexSignerType++], 
                            false, FailureHandling.CONTINUE_ON_FAILURE))
                } else {
                    continue
                }
            }
        } else if (documentTemplateCode[i].replace('"', '') == '') {
            'PageSign displit per signer.'
            pageSignSigner = pageSign[i].split('\\|', -1)

            'looping berdasarkan total email di excel'
            for (y = 0; y < EmailExcel.size(); y++) {
                'looping berdasarkan total pagesign per signer. Dalam per signer, displit lagi berdasarkan 1 lokasi'
                for (x = 0; x < pageSignSigner[y].split(';', -1).size(); x++) {
                    'Verifikasi email DB dengan email excel'
                    arrayMatch.add(WebUI.verifyMatch(EmailDB[indexEmail++], EmailExcel[y].replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

                    'Verifikasi signer Type DB dengan signer Type Excel'
                    arrayMatch.add(WebUI.verifyMatch(SignerTypeDB[indexSignerType++], SignerTypeExcel[y], false, FailureHandling.CONTINUE_ON_FAILURE))
                }
            }
        }
        
        'verify tenant code'
        arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 9).replace(
                    '"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify ref_number'
        arrayMatch.add(WebUI.verifyMatch(refNo[i].replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify document_id'
        arrayMatch.add(WebUI.verifyMatch(docid[i], result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify document template code'
        arrayMatch.add(WebUI.verifyMatch((documentTemplateCode[i]).replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify office code'
        arrayMatch.add(WebUI.verifyMatch(officeCode[i].replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify office name'
        arrayMatch.add(WebUI.verifyMatch(officeName[i].replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify region code'
        arrayMatch.add(WebUI.verifyMatch(regionCode[i].replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify region name'
        arrayMatch.add(WebUI.verifyMatch(regionName[i].replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify business line code'
        arrayMatch.add(WebUI.verifyMatch(businessLineCode[i].replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify business line name'
        arrayMatch.add(WebUI.verifyMatch(businessLineName[i].replace('"', ''), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify total document'
        arrayMatch.add(WebUI.verifyMatch(docid.size().toString(), result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

        'Looping berdasarkan jumlah dari signAction'
        for (int z = 0; z < signAction.size(); z++) {
            'Jika signAction tersebut adalah AT'
            if (signAction[z].replace('"', '') == 'at') {
                'Mengambil emailSign dari excel dan displit kembali'
                emailSign = (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 32).replace('"', '').split(
                    ';', -1)[z])

                'Mengambil trxno dari column tersebut'
                trxno = findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 6)

                'get data result trx untuk signing'
                resulttrxsigning = CustomKeywords.'connection.DataVerif.getTrxSendDocSigning'(conneSign, 
                    trxno)

                'declare arrayindex'
                arrayindex = 0

                'verify trx no'
                arrayMatch.add(WebUI.verifyMatch(trxno, resulttrxsigning[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                'verify ref no di trx'
                arrayMatch.add(WebUI.verifyMatch(findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 11).replace(
                            '"', ''), resulttrxsigning[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                'verify date req di trx'
                arrayMatch.add(WebUI.verifyMatch(currentDate, resulttrxsigning[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

                'verify trx qty = -1'
                arrayMatch.add(WebUI.verifyMatch(resulttrxsigning[arrayindex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

                'verify trx autosign'
                arrayMatch.add(WebUI.verifyMatch(resulttrxsigning[arrayindex++], ('Auto Sign (' + emailSign) + ')', false, 
                        FailureHandling.CONTINUE_ON_FAILURE)) 
            }
        }
        
        'jika data db tidak sesuai dengan excel'
        if (arrayMatch.contains(false)) {
            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, (findTestData(excelPathAPISendDoc).getValue(GlobalVariable.NumofColm, 2) + 
                ';') + GlobalVariable.ReasonFailedStoredDB)
        }
    }
}

