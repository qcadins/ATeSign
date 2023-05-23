import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'declare arraylist arraymatch'
ArrayList<String> arrayMatch = new ArrayList<String>()

docid = GlobalVariable.Response.toString().replace('[', '').replace(']', '').split(', ', -1)

ArrayList<String> refNo = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 11).replace('"', '').split(';', 
    -1)

ArrayList<String> documentTemplateCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 12).replace('"', 
    '').split(';', -1)

ArrayList<String> officeCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 13).replace('"', '').split(
    ';', -1)

ArrayList<String> officeName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 14).replace('"', '').split(
    ';', -1)

ArrayList<String> regionCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 15).replace('"', '').split(
    ';', -1)

ArrayList<String> regionName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 16).replace('"', '').split(
    ';', -1)

ArrayList<String> businessLineCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 17).replace('"', '').split(
    ';', -1)

ArrayList<String> businessLineName = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 18).replace('"', '').split(
    ';', -1)

ArrayList<String> isSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 19).replace('"', '').split(
    ';', -1)

ArrayList<String> psreCode = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 21).replace('"', '').split(
    ';', -1)

ArrayList<String> successURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 22).replace('"', '').split(
    ';', -1)

ArrayList<String> uploadURL = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 23).replace('"', '').split(
    ';', -1)

'split signer untuk doc1 dan signer untuk doc2'
ArrayList<String> signAction = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 25).replace('"', '').split(
    '\\|', -1)

ArrayList<String> signerType = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 26).replace('"', '').split(
    '\\|', -1)

ArrayList<String> signSequence = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 27).split('\\|', -1)

ArrayList<String> alamat = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 28).split('\\|', -1)

ArrayList<String> jenisKelamin = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 29).split('\\|', -1)

ArrayList<String> kecamatan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 30).split('\\|', -1)

ArrayList<String> kelurahan = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 31).split('\\|', -1)

ArrayList<String> kodePos = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 32).split('\\|', -1)

ArrayList<String> kota = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 33).split('\\|', -1)

ArrayList<String> nama = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 34).split('\\|', -1)

ArrayList<String> tlp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 35).split('\\|', -1)

ArrayList<String> tglLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 36).split('\\|', -1)

ArrayList<String> provinsi = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 37).split('\\|', -1)

ArrayList<String> idKtp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 38).split('\\|', -1)

ArrayList<String> tmpLahir = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 39).split('\\|', -1)

ArrayList<String> email = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 40).replace('"', '').split('\\|', 
    -1)

ArrayList<String> npwp = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 41).split('\\|', -1)

ArrayList<String> idPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 42).split('\\|', -1)

ArrayList<String> signerSelfPhoto = findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 43).split('\\|', -1)

for (int i = 0; i < docid.size(); i++) {
    (signAction[i]) = (signAction[i]).split(';', -1)

    (signerType[i]) = (signerType[i]).split(';', -1)

    (signSequence[i]) = (signSequence[i]).split(';', -1)

    (alamat[i]) = (alamat[i]).split(';', -1)

    (jenisKelamin[i]) = (jenisKelamin[i]).split(';', -1)

    (kecamatan[i]) = (kecamatan[i]).split(';', -1)

    (kelurahan[i]) = (kelurahan[i]).split(';', -1)

    (kodePos[i]) = (kodePos[i]).split(';', -1)

    (kota[i]) = (kota[i]).split(';', -1)

    (nama[i]) = (nama[i]).split(';', -1)

    (tlp[i]) = (tlp[i]).split(';', -1)

    (tglLahir[i]) = (tglLahir[i]).split(';', -1)

    (provinsi[i]) = (provinsi[i]).split(';', -1)

    (idKtp[i]) = (idKtp[i]).split(';', -1)

    (tmpLahir[i]) = (tmpLahir[i]).split(';', -1)

    (email[i]) = (email[i]).split(';', -1)

    (npwp[i]) = (npwp[i]).split(';', -1)

    (idPhoto[i]) = (idPhoto[i]).split(';', -1)

    (signerSelfPhoto[i]) = (signerSelfPhoto[i]).split(';', -1)

    'get data API Send Document dari DB (hanya 1 signer)'
    ArrayList<String> result = CustomKeywords.'connection.DataVerif.getSendDoc'(conneSign, docid[(i - 1)])

    'declare arrayindex'
    arrayindex = 0

    'verify email'
    arrayMatch.add(WebUI.verifyMatch((email[(i - 1)]).toString().replace('[', '').replace(']', '').replace(', ', ';'), result[
            arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify signerType'
    arrayMatch.add(WebUI.verifyMatch((signerType[(i - 1)]).toString().replace('[', '').replace(']', '').replace(', ', ';'), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify tenant code'
    arrayMatch.add(WebUI.verifyMatch(findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 9).replace('"', ''), 
            result[arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify ref_number'
    arrayMatch.add(WebUI.verifyMatch((refNo[(i - 1)]).toString().replace('[', '').replace(']', ''), result[arrayindex++], 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify document_id'
    arrayMatch.add(WebUI.verifyMatch((docid[(i - 1)]).toString().replace('[', '').replace(']', ''), result[arrayindex++], 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify document template code'
    arrayMatch.add(WebUI.verifyMatch((documentTemplateCode[(i - 1)]).toString().replace('[', '').replace(']', ''), result[
            arrayindex++], false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify office code'
    arrayMatch.add(WebUI.verifyMatch((officeCode[(i - 1)]).toString().replace('[', '').replace(']', ''), result[arrayindex++], 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify office name'
    arrayMatch.add(WebUI.verifyMatch((officeName[(i - 1)]).toString().replace('[', '').replace(']', ''), result[arrayindex++], 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify region code'
    arrayMatch.add(WebUI.verifyMatch((regionCode[(i - 1)]).toString().replace('[', '').replace(']', ''), result[arrayindex++], 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify region name'
    arrayMatch.add(WebUI.verifyMatch((regionName[(i - 1)]).toString().replace('[', '').replace(']', ''), result[arrayindex++], 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify business line code'
    arrayMatch.add(WebUI.verifyMatch((businessLineCode[(i - 1)]).toString().replace('[', '').replace(']', ''), result[arrayindex++], 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify business line name'
    arrayMatch.add(WebUI.verifyMatch((businessLineName[(i - 1)]).toString().replace('[', '').replace(']', ''), result[arrayindex++], 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify is sequence'
    arrayMatch.add(WebUI.verifyMatch((isSequence[(i - 1)]).toString().replace('[', '').replace(']', ''), result[arrayindex++], 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify psre/vendor code'
    arrayMatch.add(WebUI.verifyMatch((psreCode[(i - 1)]).toString().replace('[', '').replace(']', ''), result[arrayindex++], 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify success url'
    arrayMatch.add(WebUI.verifyMatch((successURL[(i - 1)]).toString().replace('[', '').replace(']', ''), result[arrayindex++], 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify upload url'
    arrayMatch.add(WebUI.verifyMatch((uploadURL[(i - 1)]).toString().replace('[', '').replace(']', ''), result[arrayindex++], 
            false, FailureHandling.CONTINUE_ON_FAILURE))

    'verify sign Action : hardcode. Yang penting tidak boleh kosong'
    arrayMatch.add(WebUI.verifyNotMatch((signAction[(i - 1)]).toString().replace('[', '').replace(']', ''), '', false, FailureHandling.CONTINUE_ON_FAILURE))
}

'jika data db tidak sesuai dengan excel'
if (arrayMatch.contains(false)) {
    'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
    CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'('API Send Document', GlobalVariable.NumofColm, 
        GlobalVariable.StatusFailed, (findTestData(API_Excel_Path).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedStoredDB)
}


def PDFtoBase64(String fileName) {
	String base64 = CustomKeywords.'customizekeyword.ConvertFile.base64File'(fileName)

	return base64
}

