import groovy.json.JsonSlurper
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

otp = CustomKeywords.'customizekeyword.GetSMS.getOTP'('eSignHub')
println otp

otp1 = CustomKeywords.'customizekeyword.GetSMS.getOTP'('PrivyID')
println otp1

otp2 = CustomKeywords.'customizekeyword.GetSMS.getOTP'('DIGISIGN')
println otp2