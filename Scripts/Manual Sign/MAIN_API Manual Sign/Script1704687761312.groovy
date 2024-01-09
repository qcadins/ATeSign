import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import internal.GlobalVariable as GlobalVariable

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - API Only.xlsx')

'variable untuk keperluan split excel'
semicolon = ';'

delimiter = '\\|'

enter = '\\n'

for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= findTestData(API_Excel_Path).columnNumbers; (GlobalVariable.NumofColm)++) {
    if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'Inisialisasi callerId'
        callerId = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('callerId'))

        'Inisialisasi ref No'
        refNo = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$referenceNo'))

        'Inisialisasi psreCode'
        psreCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$psreCode'))

        'Inisialisasi documentName'
        documentName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$documentName'))

        'Inisialisasi documentDate'
        documentDate = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$documentDate'))

        'Inisialisasi peruriDocType'
        peruriDocType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('peruriDocType'))

        'Inisialisasi isAutomaticStamp'
        isAutomaticStamp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('isAutomaticStamp'))

        'Inisialisasi paymentType'
        paymentType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$paymentType'))

        'Inisialisasi isSequence'
        isSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('isSequence'))

        String documentFile

        'Inisialisasi documentFile'
        if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 Document')) == 
        'Yes') {
            documentFile = (('"' + CustomKeywords.'customizekeyword.ConvertFile.base64File'(findTestData(API_Excel_Path).getValue(
                    GlobalVariable.NumofColm, rowExcel('documentFile')))) + '"')
        } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('enter Correct base64 Document')) == 
        'No') {
            documentFile = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('documentFile'))
        }
        
        'Inisialisasi tenantCode'
        tenantCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Tenant Login'))

        'join all main parameter'
        String bodyAPI = (((((((((((((((((((('{"audit": {"callerId": "' + callerId) + '"},"psreCode": "') + psreCode) + 
        '","tenantCode": "') + tenantCode) + '","referenceNo": "') + refNo) + '","documentName": "') + documentName) + '","documentDate": "') + 
        documentDate) + '","peruriDocType": "') + peruriDocType) + '","isAutomaticStamp": "') + isAutomaticStamp) + '","paymentType": "') + 
        paymentType) + '","isSequence": "') + isSequence) + '","documentFile": ') + documentFile

        'pindahkan hasil bodiAPI ke bodyAPIFinal'
        String bodyAPIFinal = bodyAPI

        println(bodyAPIFinal)

        'clear variable body API untuk menampung stamp location'
        bodyAPI = ''

        'Inisialisasi stampPage'
        stampPage = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('stampPage')).split(semicolon, 
            -1)

        'Inisialisasi transformStamping'
        transformStamping = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('transform - Stamping')).split(
            enter, -1)

        'Inisialisasi notes'
        notes = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('notes')).split(semicolon, -1)

        'Inisialisasi stampLocation'
        stampLocation = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('stampLocation')).split(
            enter, -1)

        'looping untuk menggabungkan stamping location'
        for (index = 0; index < stampPage.size(); index++) {
            if ((stampPage.size() - 1) == index) {
                bodyAPI = (((((((((bodyAPI + '{"stampPage": ') + (stampPage[index])) + ',"transform": "') + (transformStamping[
                index])) + '","notes": "') + (notes[index])) + '","stampLocation": ') + (stampLocation[index])) + '}')

                bodyAPI = ((',"stampingLocations": [' + bodyAPI) + ']')
            } else if ((stampPage.size() - 1) != index) {
                bodyAPI = (((((((((bodyAPI + '{"stampPage": "') + (stampPage[index])) + '","transform": "') + (transformStamping[
                index])) + '","notes": "') + (notes[index])) + '","stampLocation": ') + (stampLocation[index])) + '},')
            }
        }
        
        bodyAPIFinal = (bodyAPIFinal + bodyAPI)

        bodyAPI = ''

        'Inisialisasi nama'
        nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$nama')).split(semicolon, -1)

        'Inisialisasi tlp'
        tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$tlp')).split(semicolon, -1)

        'Inisialisasi email'
        email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$email')).split(semicolon, -1)

        'Inisialisasi signerType'
        signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('$signerType')).split(semicolon, 
            -1)

        'Inisialisasi signSequence'
        signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('signSequence')).split(semicolon, 
            -1)

        'Inisialisasi id'
        id = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('id')).split(enter, -1)

        'Inisialisasi signPage'
        signPage = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('signPage')).split(enter, -1)

        'Inisialisasi transformSign'
        transformSign = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('transform - Signing')).split(
            enter, -1)

        'Inisialisasi position'
        position = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('position')).split(enter, -1)

        'Inisialisasi positionVida'
        positionVida = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('positionVida')).split(enter, 
            -1)

        'Inisialisasi positionPrivy'
        positionPrivy = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('positionPrivy')).split(
            enter, -1)

        'Inisialisasi signLocation'
        signLocation = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('signLocation')).split(enter, 
            -1)

        'looping untuk menggabungkan Signer'
        for (signerIndex = 0; signerIndex < nama.size(); signerIndex++) {
            String signLoc = ''

            'Inisialisasi id'
            ids = (id[signerIndex]).split(semicolon, -1)

            'Inisialisasi signPage'
            signPages = (signPage[signerIndex]).split(semicolon, -1)

            'Inisialisasi transformSign'
            transformSigns = (transformSign[signerIndex]).split(semicolon, -1)

            'Inisialisasi position'
            positions = (position[signerIndex]).split(semicolon, -1)

            'Inisialisasi positionVida'
            positionVidas = (positionVida[signerIndex]).split(semicolon, -1)

            'Inisialisasi positionPrivy'
            positionPrivys = (positionPrivy[signerIndex]).split(semicolon, -1)

            'Inisialisasi signLocation'
            signLocations = (signLocation[signerIndex]).split(semicolon, -1)

            'looping untuk menggabungkan Sign location'
            for (index = 0; index < signPages.size(); index++) {
                if ((signPages.size() - 1) == index) {
                    signLoc = (((((((((((((((signLoc + '{"id": ') + (ids[index])) + ',"signPage": ') + (signPages[index])) + 
                    ',"transform": "') + (transformSigns[index])) + '","position": ') + (positions[index])) + ',"positionVida": ') + 
                    (positionVidas[index])) + ',"positionPrivy": ') + (positionPrivys[index])) + ',"signLocation": ') + 
                    (signLocations[index])) + '}')

                    signLoc = ((',"signLocations": [' + signLoc) + ']')
                } else if ((signPages.size() - 1) != index) {
                    signLoc = (((((((((((((((signLoc + '{"id": ') + (ids[index])) + ',"signPage": ') + (signPages[index])) + 
                    ',"transform": "') + (transformSigns[index])) + '","position": ') + (positions[index])) + ',"positionVida": ') + 
                    (positionVidas[index])) + ',"positionPrivy": ') + (positionPrivys[index])) + ',"signLocation": ') + 
                    (signLocations[index])) + '},')
                }
            }
            
            if ((nama.size() - 1) == signerIndex) {
                bodyAPI = (((((((((((((bodyAPI + '{"name":"') + (nama[signerIndex])) + '","phone": "') + (tlp[signerIndex])) + 
                '","email": "') + (email[signerIndex])) + '","signerTypeCode": "') + (signerType[signerIndex])) + '","seqNo": "') + 
                (signSequence[signerIndex])) + '"') + signLoc) + '}')

                bodyAPI = ((',"signers": [' + bodyAPI) + ']')
            } else if ((nama.size() - 1) != signerIndex) {
                bodyAPI = (((((((((((((bodyAPI + '{"name":"') + (nama[signerIndex])) + '","phone": "') + (tlp[signerIndex])) + 
                ',""email": "') + (email[signerIndex])) + '","signerTypeCode": "') + (signerType[signerIndex])) + '","seqNo": "') + 
                (signSequence[signerIndex])) + '"') + signLoc) + '},')
            }
        }
        
        'menggabungkan body request kedalam 1 variable'
        bodyAPIFinal = ((bodyAPIFinal + bodyAPI) + '}')

        'HIT API'
        responLogin = WS.sendRequest(findTestObject('APIFullService - Privy/Postman/Login', [
						('email') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Email Login')), 
						('password') : findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Password Login'))]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(responLogin, 200, FailureHandling.OPTIONAL) == true) {
            if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Use True Token')).equalsIgnoreCase(
                'Yes')) {
                'Parsing token menjadi GlobalVariable'
                GlobalVariable.token = WS.getElementPropertyValue(responLogin, 'access_token')
            } else if (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Use True Token')).equalsIgnoreCase(
                'No')) {
                GlobalVariable.token = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Wrong Token'))
            }
            
            println(GlobalVariable.token)

            'HIT API Manual Sign'
            responManualSign = WS.sendRequest(findTestObject('Postman/Manual Sign', [('request') : bodyAPIFinal]))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(responManualSign, 200, FailureHandling.OPTIONAL) == true) {
                'get Status Code'
                statusCode = WS.getElementPropertyValue(responManualSign, 'status.code')

				'ambil lama waktu yang diperlukan hingga request menerima balikan'
				elapsedTime = (responManualSign.elapsedTime / 1000) + ' second'
	
				'ambil body dari hasil respons'
				responseBody = responManualSign.responseBodyContent
	
				'panggil keyword untuk proses beautify dari respon json yang didapat'
				CustomKeywords.'customizekeyword.BeautifyJson.process'(responseBody, sheet, rowExcel('Respons') - 1, findTestData(
						API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Scenario')))
	
				'write to excel response elapsed time'
				CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Process Time') -
					1, GlobalVariable.NumofColm - 1, elapsedTime.toString())
				
                'Jika status codenya 0'
                if (statusCode == 0) {
                    if (GlobalVariable.FlagFailed == 0) {
                        'write to excel success'
                        CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, 0, 
                            GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
                    }
                } else {
                    getErrorMessageAPI(responManualSign)
                }
            } else {
                getErrorMessageAPI(responManualSign)
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(responLogin, 'error_description', FailureHandling.OPTIONAL).toString()

            'Write To Excel GlobalVariable.StatusFailed and errormessage'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
                (((findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', 
                    '') + ';') + '<') + message) + '>')

            GlobalVariable.FlagFailed = 1
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

