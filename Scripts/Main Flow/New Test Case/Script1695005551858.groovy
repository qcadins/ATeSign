import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import org.openqa.selenium.By as By
import java.sql.Connection as Connection


String aa = '"WIKY.HENDRA@AD-INS.COM";"FENDY.TIO@AD-INS.COM";"KEVIN.EDGAR@AD-INS.COM"'

signersPerDoc = (aa).split(';', -1)
println signersPerDoc
signersPerDoc = (aa).split(';', -1).collect({
		it.trim()
	})

println signersPerDoc

println signersPerDoc.size()
for (loopingperSigner = 0; loopingperSigner < 3; loopingperSigner++) {
	println loopingperSigner
	if (signersPerDoc[loopingperSigner] != '"WIKY.HENDRA@AD-INS.COM"') {
		signersPerDoc.remove(loopingperSigner)
		println signersPerDoc
		WebUI.delay(3)
	}
}

println signersPerDoc
WebUI.delay(50)