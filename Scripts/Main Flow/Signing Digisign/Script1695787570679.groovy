import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import java.sql.Connection
import java.util.concurrent.ConcurrentHashMap.KeySetView

import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

WebUI.switchToFrame(findTestObject('Signing-DIGISIGN/iFrame'), GlobalVariable.TimeOut)

WebUI.focus(findTestObject('Signing-DIGISIGN/pdf_document'))

WebUI.sendKeys(findTestObject('Signing-DIGISIGN/pdf_document'), Keys.chord(Keys.PAGE_DOWN))

WebUI.delay(10)

WebUI.sendKeys(findTestObject('Signing-DIGISIGN/pdf_document'), Keys.chord(Keys.PAGE_DOWN))

WebUI.click(findTestObject('Signing-DIGISIGN/button_prosesTtd'))

println WebUI.getText(findTestObject('Signing-DIGISIGN/text_documentId'))
WebUI.delay(40)