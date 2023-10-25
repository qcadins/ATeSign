import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import java.sql.Connection as Connection

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

sheet = 'Main'

'looping untuk menjalankan Main'
for (GlobalVariable.NumofColm = 61; GlobalVariable.NumofColm <= findTestData(excelPathMain).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
        'reset value di excel mengenai output previous run. Jika menggunakan opsi Sign Only, maka document id tidak akan didelete'
        resetValue()

        inisializeValue()

        'pasang gv tenant agar tidak berubah'
        GlobalVariable.Tenant = findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Tenant'))

        'inisialisasi signerInput dan email signer sebagai array list'
        ArrayList emailSignerPerDoc

        ArrayList signers = []

        ArrayList documentId = []

        ArrayList signersPerDoc = []

        LinkedHashMap emailSigner = new LinkedHashMap()

        LinkedHashMap signerInput = new LinkedHashMap()

        'Pemilihan opsi send document. Jika send document API Send External'
        if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) == 'API Send Document External') {
            'call test case send doc external'
            WebUI.callTestCase(findTestCase('Main Flow/API Send Document External'), [('excelPathAPISendDoc') : excelPathMain
                    , ('sheet') : sheet], FailureHandling.CONTINUE_ON_FAILURE)

            documentId = findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', 
                -1)

			signerInput = checkingDocAndEmailFromInput(documentId, '$email (Send External)', signerInput)

        } else if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) == 
        'API Send Document Normal') {
            'jika send document menggunakan api send normal, maka call test case send doc normal'
            WebUI.callTestCase(findTestCase('Main Flow/API Send Document Normal'), [('API_Excel_Path') : excelPathMain, ('sheet') : sheet], 
                FailureHandling.CONTINUE_ON_FAILURE)

            documentId = findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', 
                -1)

			signerInput = checkingDocAndEmailFromInput(documentId, '$email (Send Normal)', signerInput)

        } else if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) == 
        'Manual Sign') {
            'jika send document menggunakan manual sign, maka call test case manual sign'
            WebUI.callTestCase(findTestCase('Main Flow/Manual Sign'), [('excelPathManualSigntoSign') : excelPathMain, ('sheet') : sheet], 
                FailureHandling.CONTINUE_ON_FAILURE)

            documentId = findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).split(', ', 
                -1)

			signerInput = checkingDocAndEmailFromInput(documentId, 'jumlah signer lokasi per signer (Send Manual)',signerInput)
        }
        'jika documentid nya tidak kosong'
        if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('documentid')).length() > 0) {
            'jika opsi tanda tangannya bukan sign only'
            if ((findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) != 
            'Sign Only') && (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) != 
            'Stamp Only')) {
                'call test case kotak masuk dan verify document monitoring. Document monitoring terdapat didalam kotak masuk.'
                WebUI.callTestCase(findTestCase('Main Flow/KotakMasuk'), [('excelPathFESignDocument') : excelPathMain, ('sheet') : sheet
                        , ('checkBeforeSigning') : 'Yes', ('CancelDocsSend') : findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Cancel Docs after Send?'))], FailureHandling.CONTINUE_ON_FAILURE)

                'jika ada proses cancel doc'
                if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Cancel Docs after Send?')) == 
                'Yes') {
                    'lanjutkan loop'
                    continue
                }
            }
            
            'jika opsi tanda tangannya bukan stamp only'
            if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) != 
            'Stamp Only') {
                'jika memerlukan tanda tangan pada dokumen ini'
                if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Need Sign for this document?')) == 
                'Yes') {
					'setting must liveness dimatikan/diaktifkan'
					if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must LivenessFaceCompare')).length() >
					0) {
						'update setting vendor otp ke table di DB'
						CustomKeywords.'connection.UpdateData.settingLivenessFaceCompare'(conneSign, findTestData(excelPathMain).getValue(
								GlobalVariable.NumofColm, rowExcel('Setting Must LivenessFaceCompare')))
					}
				
                    'ambil opsi signing'
                    ArrayList opsiSigning = findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Option for Sign Document per Signer')).split(
                        ';', -1)

                    'jika opsi tanda tangannya bukan sign only'
                    if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) != 
                    'Sign Only') {
                        for (i = documentId.size() - 1; i >= 0; i--) {
                            emailSignerPerDoc = CustomKeywords.'connection.SendSign.getEmailLogin'(conneSign, documentId[
                                i]).split(';', -1)

                            emailSignerPerDoc = emailSignerPerDoc.collect({ 
                                    it.trim()
                                })

                            emailSigner.put(documentId[i], emailSignerPerDoc)
                        }

                        'pengecekan jika total signer dari inputan dan dari db tidak sama'
                        if (WebUI.verifyNotEqual(signerInput.size(), emailSigner.size(), FailureHandling.OPTIONAL)) {
                            'Write To Excel GlobalVariable.StatusFailed and total signernya tidak sesuai'
                            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, 
                                    rowExcel('Reason Failed')) + ';') + ' total signer pada Send Document dengan signer yang terdaftar tidak sesuai ')
                        }
                    } else {
						
						signerInput = checkingDocAndEmailFromInput(documentId, 'email Signer (Sign Only)', signerInput)
                        'jika menggunakan opsi sign only ,maka email signernya diinput'
                        emailSigner = findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('email Signer (Sign Only)')).split(
                            ';', -1)
                    }
                    
                    String cancelDocsValue = ''

                    'looping berdasarkan email yang akan menandatangani'
                    for (int i = 0; i < emailSigner.keySet().size(); i++) {
						for (y = 0; y < emailSigner.get(emailSigner.keySet()[i]).size(); y++) {
							
                        if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Cancel Docs after Sign?')) == 
                        'Yes') {
                            'integrasikan cancel docs jika signer sudah sesuai'
                            if ((Integer.parseInt(findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel(
                                        'Cancel Docs after how many Signer?'))) - 1) == y) {
                                'ubah value cancel docs menjadi yes'
                                cancelDocsValue = findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel(
                                        'Cancel Docs after Sign?'))
                            }
                        }
						
						ifSignerAuto = CustomKeywords.'connection.APIFullService.getIfSignerAutosign'(conneSign,emailSigner.keySet()[i],emailSigner.get(emailSigner.keySet()[i])[y])
						
						if (ifSignerAuto == 'Autosign') {
							continue
						}
						
						GlobalVariable.storeVar = [:]
						GlobalVariable.storeVar.putAt(emailSigner.keySet()[i], emailSigner.get(emailSigner.keySet()[i])[y])

                        'jika opsi signing untuk signer adalah api sign document external'
                        if ((opsiSigning[y]) == 'API Sign Document External') {
                            'setting index untuk penggunaan data. Cara bacanya adalah apakah opsi tersebut telah digunakan. Jika sudah digunakan, maka + 1, jika tidak, maka 0.'
                            indexReadDataExcelAPIExternal = inisializeArray(isUsedAPIExternal, indexReadDataExcelAPIExternal)

                            'diberikan kepada gv indexused'
                            GlobalVariable.indexUsed = indexReadDataExcelAPIExternal

                            'call test case api sign document external'
                            WebUI.callTestCase(findTestCase('Main Flow/API Sign Document External'), [('excelPathAPISignDocument') : excelPathMain
                                    , ('sheet') : sheet, ('CancelDocsSign') : cancelDocsValue], FailureHandling.CONTINUE_ON_FAILURE)

                            'set boolean is used api external menjadi true'
                            isUsedAPIExternal = true
                        } else if ((opsiSigning[y]) == 'API Sign Document Normal') {
                            'jika opsi signing untuk signer adalah api sign document normal setting index untuk penggunaan data. Cara bacanya adalah apakah opsi tersebut telah digunakan. Jika sudah digunakan, maka + 1, jika tidak, maka 0.'
                            indexReadDataExcelAPINormal = inisializeArray(isUsedAPINormal, indexReadDataExcelAPINormal)

                            'diberikan kepada gv indexused'
                            GlobalVariable.indexUsed = indexReadDataExcelAPINormal

                            'call test case api sign document normal'
                            WebUI.callTestCase(findTestCase('Main Flow/API Sign Document Normal'), [('API_Excel_Path') : excelPathMain
                                    , ('sheet') : sheet, ('CancelDocsSign') : cancelDocsValue], FailureHandling.CONTINUE_ON_FAILURE)

                            'set boolean menjadi true'
                            isUsedAPINormal = true
                        } else if ((opsiSigning[y]) == 'Webview Sign') {
                            'jika opsi signing untuk signer adalah webview sign setting index untuk penggunaan data. Cara bacanya adalah apakah opsi tersebut telah digunakan. Jika sudah digunakan, maka + 1, jika tidak, maka 0.'
                            indexReadDataExcelWebview = inisializeArray(isUsedUI, indexReadDataExcelUI)

                            'diberikan kepada gv indexused'
                            GlobalVariable.indexUsed = indexReadDataExcelWebview

                            'call test case webview embed sign'
                            WebUI.callTestCase(findTestCase('Main Flow/Webview Embed Sign'), [('excelPathFESignDocument') : excelPathMain
                                    , ('sheet') : sheet, ('indexUsed') : indexReadDataExcelWebview,('opsiSigning') : opsiSigning[i], ('CancelDocsSign') : cancelDocsValue], FailureHandling.CONTINUE_ON_FAILURE)

                            'set boolean true'
                            isUsedUI = true
                        } else if ((opsiSigning[y]) == 'Embed Sign') {
                            'jika opsi signing untuk signer adalah embed sign setting index untuk penggunaan data. Cara bacanya adalah apakah opsi tersebut telah digunakan. Jika sudah digunakan, maka + 1, jika tidak, maka 0.'
                            indexReadDataExcelEmbed = inisializeArray(isUsedUI, indexReadDataExcelUI)

                            'diberikan kepada gv indexused'
                            GlobalVariable.indexUsed = indexReadDataExcelEmbed

                            'call test case webview embed sign'
                            WebUI.callTestCase(findTestCase('Main Flow/Webview Embed Sign'), [('excelPathFESignDocument') : excelPathMain
                                    , ('sheet') : sheet, ('indexUsed') : indexReadDataExcelEmbed, ('opsiSigning') : opsiSigning[i], ('CancelDocsSign') : cancelDocsValue], FailureHandling.CONTINUE_ON_FAILURE)

                            'set boolean true'
                            isUsedUI = true
                        } else if ((opsiSigning[y]) == 'Sign Via Inbox') {
                            'jika opsi signing untuk signer adalah sign via inbox setting index untuk penggunaan data. Cara bacanya adalah apakah opsi tersebut telah digunakan. Jika sudah digunakan, maka + 1, jika tidak, maka 0.'
                            indexReadDataExcelInboxSigner = inisializeArray(isUsedUI, indexReadDataExcelUI)

                            'diberikan kepada gv indexused'
                            GlobalVariable.indexUsed = indexReadDataExcelInboxSigner

                            'call test case signer login sign'
                            WebUI.callTestCase(findTestCase('Main Flow/SignerLogin Sign'), [('excelPathFESignDocument') : excelPathMain
                                    , ('sheet') : sheet, ('opsiSigning') : opsiSigning[i], ('CancelDocsSign') : cancelDocsValue], FailureHandling.CONTINUE_ON_FAILURE)

                            'set boolean true'
                            isUsedUI = true
                        }
                        
                        'jika ada proses cancel doc'
                        if (cancelDocsValue != '') {
                            'lanjutkan loop'
                            continue
                        }
                    }
                }
                }
            }
            
            'jika set stamping'
            if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Do Stamp for this document?')) == 
            'Yes') {
                'call test case stamping'
                WebUI.callTestCase(findTestCase('Main Flow/Stamping'), [('excelPathStamping') : excelPathMain, ('sheet') : sheet
                        , ('linkDocumentMonitoring') : '', ('CancelDocsStamp') : findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, 
                            rowExcel('Cancel Docs after Stamp?'))], FailureHandling.CONTINUE_ON_FAILURE)

                'jika ada proses cancel doc'
                if (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Cancel Docs after Stamp?')) == 
                'Yes') {
                    'lanjutkan loop'
                    continue
                }
            }
        }
    }
}

def inisializeArray(boolean isUsed, int indexReadDataExcel) {
    if (isUsed == false) {
        return indexReadDataExcel
    } else {
        return indexReadDataExcel + 1
    }
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def resetValue() {
    if ((findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) != 'Sign Only') && 
    (findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Option for Send Document :')) != 'Stamp Only')) {
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('documentid') - 
            1, GlobalVariable.NumofColm - 1, '')
    }
    
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('trxNo') - 1, 
        GlobalVariable.NumofColm - 1, '')

    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('PsRE Document') - 
        1, GlobalVariable.NumofColm - 1, '')

    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('trxNos') - 1, 
        GlobalVariable.NumofColm - 1, '')

    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Result Count Success') - 
        1, GlobalVariable.NumofColm - 1, '')

    CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Result Count Failed') - 
        1, GlobalVariable.NumofColm - 1, '')
}

def inisializeValue() {
    isUsedAPIExternal = false

    isUsedAPINormal = false

    isUsedUI = false

    indexReadDataExcelAPIExternal = 0

    indexReadDataExcelAPINormal = 0

    indexReadDataExcelInboxSigner = 0

    indexReadDataExcelEmbed = 0

    indexReadDataExcelWebview = 0

    indexReadDataExcelUI = 0
}

def checkingDocAndEmailFromInput(ArrayList documentId, String rowEmail, LinkedHashMap signerInput) {
for (loopingDocument = documentId.size() -1; loopingDocument >= 0; loopingDocument--) {
	signers = findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel(rowEmail)).split(
		'\n', -1)

	signersPerDoc = (signers[loopingDocument]).split(';', -1)
	
	signersPerDoc = (signers[loopingDocument]).split(';', -1).collect({
			it.trim()
		})

	signerInput.put(documentId[loopingDocument], signersPerDoc)
}
return signerInput
}