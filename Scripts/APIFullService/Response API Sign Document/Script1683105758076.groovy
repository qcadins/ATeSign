import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import java.sql.Connection as Connection
import org.apache.commons.io.FileUtils as FileUtils
import java.net.InetAddress as InetAddress

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizeKeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathAPISignDocument).getColumnNumbers()

'looping API Sign Document'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 1).length() == 0) {
        break
    } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 1).equalsIgnoreCase('Unexecuted')) {
        'Inisialisasi otp, Photo, ipaddress, dan total signed sebelumnya yang dikosongkan'
        String otp, Photo, ipaddress, totalSigned_before

        'Split dokumen id agar mendapat dokumenid 1 per 1 dengan case bulk'
        documentId = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 10).replace('[', '').replace(
            ']', '').replace('"', '').split(',', -1)

        'check if tidak mau menggunakan tenant code yang benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 19) == 'No') {
            'set tenant kosong'
            GlobalVariable.Tenant = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 20)
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 19) == 'Yes') {
            'Mengambil tenant dari setting'
            GlobalVariable.Tenant = findTestData(excelPathSetting).getValue(6, 2)
        }
        
        'check if mau menggunakan api_key yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 17) == 'Yes') {
            'get api key dari db'
            GlobalVariable.api_key = CustomKeywords.'connection.DataVerif.getTenantAPIKey'(conneSign, GlobalVariable.Tenant)
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 17) == 'No') {
            'get api key salah dari excel'
            GlobalVariable.api_key = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 18)
        }
        
        'check if mau menggunakan OTP yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 21) == 'Yes') {
            'request OTP dengan HIT API'

            'Constaint : Dokumen yang dipasang selalu dengan referal number di dokumen pertama.'
            respon_OTP = WS.sendRequest(findTestObject('APIFullService/Postman/Sent Otp Signing', [('callerId') : findTestData(
                            excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 26), ('phoneNo') : findTestData(
                            excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 8), ('email') : findTestData(excelPathAPISignDocument).getValue(
                            GlobalVariable.NumofColm, 11), ('refnumber') : ('"' + CustomKeywords.'connection.DataVerif.getRefNumber'(
                            conneSign, documentId[0])) + '"']))

            'Jika status HIT API 200 OK'
            if (WS.verifyResponseStatusCode(respon_OTP, 200, FailureHandling.OPTIONAL) == true) {
                'get status code'
                code_otp = WS.getElementPropertyValue(respon_OTP, 'status.code', FailureHandling.OPTIONAL)

                'jika codenya 0'
                if (code_otp == 0) {
                    'Dikasih delay 1 detik dikarenakan loading untuk mendapatkan OTP.'
                    WebUI.delay(1)

                    'Mengambil otp dari database'
                    otp = (('"' + CustomKeywords.'connection.DataVerif.getOTPAktivasi'(conneSign, findTestData(excelPathAPISignDocument).getValue(
                            GlobalVariable.NumofColm, 11).replace('"', ''))) + '"')
                } else {
                    'mengambil status code berdasarkan response HIT API'
                    message = WS.getElementPropertyValue(respon_OTP, 'status.message', FailureHandling.OPTIONAL)

                    'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                    CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                        GlobalVariable.StatusFailed, message)
                }
            } else {
                'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.HITAPI Gagal'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        2) + ';') + GlobalVariable.ReasonFailedOTPError)
            }
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 21) == 'No') {
            'get otp dari excel'
            otp = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 22)
        }
        
        'check if mau menggunakan base64 untuk photo yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 23) == 'Yes') {
            'get base64 photo dari fungsi'
            Photo = (('"' + PhototoBase64(findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 15))) + 
            '"')
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 23) == 'No') {
            'get base64 photo salah dari excel'
            Photo = (('"' + findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 15)) + '"')
        }
        
        'check if mau menggunakan ip address yang salah atau benar'
        if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 24) == 'Yes') {
            'get ip address dari fungsi'
            ipaddress = (('"' + CorrectipAddress()) + '"')
        } else if (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 24) == 'No') {
            'get ip address salah dari excel'
            ipaddress = findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 13)
        }
        
        'looping berdasarkan ukuran dari dokumen id'
        for (int z = 0; z < documentId.size(); z++) {
            'Memasukkan input dari total signed dengan ,'
            totalSigned_before = (CustomKeywords.'connection.DataVerif.getTotalSigned'(conneSign, documentId[z]) + ',')

            'Jika di paling akhir loop'
            if (z == (documentId.size() - 1)) {
                'Memasukkan input dari total signed tanpa ,'
                totalSigned_before = CustomKeywords.'connection.DataVerif.getTotalSigned'(conneSign, documentId[z])
            }
        }
        
        'HIT API Sign'
        respon = WS.sendRequest(findTestObject('APIFullService/Postman/Sign Document', [('callerId') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, 26), ('documentId') : findTestData(excelPathAPISignDocument).getValue(
                        GlobalVariable.NumofColm, 10), ('email') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        11), ('password') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 12)
                    , ('ipAddress') : ipaddress, ('browserInfo') : findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                        14), ('otp') : otp, ('selfPhoto') : Photo]))

        'Jika status HIT API 200 OK'
        if (WS.verifyResponseStatusCode(respon, 200, FailureHandling.OPTIONAL) == true) {
            'get status code'
            code = WS.getElementPropertyValue(respon, 'status.code', FailureHandling.OPTIONAL)

            'get status code'
            trxNo = WS.getElementPropertyValue(respon, 'trxNo', FailureHandling.OPTIONAL)

            'Jika trxNonya tidak kosong dari response'
            if (trxNo != null) {
                'Input excel'
                CustomKeywords.'customizeKeyword.writeExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sign Document', 
                    4, GlobalVariable.NumofColm - 1, trxNo.toString().replace('[', '').replace(']', ''))
            }

            'jika codenya 0'
            if (code == 0) {
                'Loop berdasarkan jumlah documen id'
                for (int x = 0; x < documentId.size(); x++) {
                    'Loop untuk check db update sign. Maksimal 200 detik.'
                    for (int v = 1; v <= 20; v++) {
                        'Mengambil total Signed setelah sign'
                        totalSigned_after = CustomKeywords.'connection.DataVerif.getTotalSigned'(conneSign, documentId[x])

                        'Verify total signed sebelum dan sesudah. Jika sesuai maka break'
                        if (WebUI.verifyEqual(totalSigned_after, Integer.parseInt(totalSigned_before.split(',', -1)[x]) + 
                            Integer.parseInt(CustomKeywords.'connection.DataVerif.getTotalSigner'(conneSign, documentId[
                                    x], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 11).replace(
                                        '"', ''))), FailureHandling.CONTINUE_ON_FAILURE)) {
                            break
                        } else if (v == 20) {
                            'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
                            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                                GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                                    2) + ';') + GlobalVariable.ReasonFailedSignGagal)
                        } else {
                            'Delay 10 detik.'
                            WebUI.delay(10)
                        }
                    }
                }

                'write to excel success'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, 'API Sign Document', 
                    0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)

                'check Db'
                if (GlobalVariable.checkStoreDB == 'Yes') {
                    'Panggil function ResponseAPIStoreDB dengan parameter totalSigned, ipaddress, dan array dari documentId'
                    ResponseAPIStoreDB(conneSign, ipaddress, documentId)
                }
            } else {
                'mengambil status code berdasarkan response HIT API'
                message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

                'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
                CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                    GlobalVariable.StatusFailed, message)
            }
        } else {
            'mengambil status code berdasarkan response HIT API'
            message = WS.getElementPropertyValue(respon, 'status.message', FailureHandling.OPTIONAL)

            'Write To Excel GlobalVariable.StatusFailed and errormessage dari api'
            CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
                GlobalVariable.StatusFailed, message)
        }
    }
}

'Fungsi mendapat correct ip address'
def CorrectipAddress() {
    String ipAddress = InetAddress.getLocalHost().getHostAddress()

    return ipAddress
}

'Fungsi photo to base64'
def PhototoBase64(String filePath) {
    return CustomKeywords.'customizeKeyword.ConvertFile.BASE64File'(filePath)
}
'Fungsi StoreDB'
def ResponseAPIStoreDB(Connection conneSign, String ipaddress, ArrayList<String> documentId) {
    'get current date'
    def currentDate = new Date().format('yyyy-MM-dd')

    'declare arraylist arraymatch'
    ArrayList<String> arrayMatch = new ArrayList<String>()

    'loop berdasarkan dokumen id'
    for (int i = 0; i < documentId.size(); i++) {
        'get data from db'
        arrayIndex = 0

        'Array result. Value dari db'
        ArrayList<String> result = CustomKeywords.'connection.DataVerif.getSign'(conneSign, (documentId[i]).replace('"', 
                ''), findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 11).replace('"', ''))

        'verify qty dalam transaksi. Jika done = 1'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '-1', false, FailureHandling.CONTINUE_ON_FAILURE))

        'Check liveness compare adalah 0 dikarenakan trxNo yang didapat adalah transaksi untuk liveness compare.'
        'Ini perlu dideklarasi dikarenakan jika 2 dokumen, trxNo tetap 1, sehingga perlu diflag apakah dia sudah check trxnya atau belum'
        checkLivenessCompare = 0

        'Jika trxNonya tidak kosong dan checkLivenessComparenya 0'
        if ((findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 5) != '') || (checkLivenessCompare == 
        0)) {
            'verify trx no. Jika sesuai, maka'
            if (WebUI.verifyEqual(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    5), FailureHandling.CONTINUE_ON_FAILURE)) {
                'Ditambah 1'
                checkLivenessCompare++

                'arrayMatchnya diinput true'
                arrayMatch.add(true)
            }
        } else {
            'Tambah dari arrayIndex'
            arrayIndex++
        }
        
        'verify request status. 3 = done'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], '3', false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify ref number yang tertandatangan'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], CustomKeywords.'connection.DataVerif.getRefNumber'(conneSign, 
                    (documentId[i]).replace('"', '')), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify ip address'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], ipaddress.replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify user browser'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    14).replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify callerId'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 
                    26).replace('"', ''), false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify signing proces. 0 berarti tidak ada proses tanda tangan lagi.'
        arrayMatch.add(WebUI.verifyEqual(result[arrayIndex++], 0, FailureHandling.CONTINUE_ON_FAILURE))

        'verify tanggal tanda tangan'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], currentDate, false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify api key'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.api_key, false, FailureHandling.CONTINUE_ON_FAILURE))

        'verify tenant'
        arrayMatch.add(WebUI.verifyMatch(result[arrayIndex++], GlobalVariable.Tenant, false, FailureHandling.CONTINUE_ON_FAILURE))
    }
    
    'jika data db tidak sesuai dengan excel'
    if (arrayMatch.contains(false)) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedStoredDB'
        CustomKeywords.'customizeKeyword.WriteExcel.writeToExcelStatusReason'('API Sign Document', GlobalVariable.NumofColm, 
            GlobalVariable.StatusFailed, (findTestData(excelPathAPISignDocument).getValue(GlobalVariable.NumofColm, 2) + 
            ';') + GlobalVariable.ReasonFailedStoredDB)
    }
}

