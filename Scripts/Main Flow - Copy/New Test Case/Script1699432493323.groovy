import groovy.json.JsonSlurper
import internal.GlobalVariable
import java.sql.Connection as Connection

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

//'connect DB eSign'
//Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()
//
//GlobalVariable.eSignData['VerifikasiOTP'] = 0
//
//GlobalVariable.eSignData['VerifikasiOTP'] = 1
//
//'menggunakan saldo wa'
//balmut = CustomKeywords.'connection.DataVerif.getTrxSaldoWASMS'(conneSign, 'OTP', 'USERCIIE', (GlobalVariable.eSignData.getAt('VerifikasiOTP')))
//
//println balmut
//WebUI.Delay(100000000)
otp = CustomKeywords.'customizekeyword.GetSMS.getOTP'('eSignHub')
println otp

otp1 = CustomKeywords.'customizekeyword.GetSMS.getOTP'('PrivyID')
println otp1

otp2 = CustomKeywords.'customizekeyword.GetSMS.getOTP'('DIGISIGN')
println otp2

//'get data file path'
//GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2.1 Esign - Full API Services.xlsx')
//
//
//ArrayList officeRegionBline = CustomKeywords.'connection.DataVerif.getOfficeRegionBlineCodeUsingRefNum'(conneSign, 'TEST DOCUMENT 3')
//
//println officeRegionBline
//println officeRegionBline.size()
