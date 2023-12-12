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
import org.apache.commons.lang3.time.StopWatch
import org.apache.commons.lang3.time.DurationFormatUtils
'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

GlobalVariable.NumofColm = 2

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign Main.xlsx')

HashMap aa = []
aa.put('USERCJIA@GMAIL.COM', '1')
aa.put('USERCIIE@AD-INS.COM', '1')

GlobalVariable.storeVar.putAt('00155D0B-7502-8FE8-11EE-98A96BAF4E51', 'USERCJIA@GMAIL.COM')

println aa
println GlobalVariable.storeVar

for (looping = 0; looping < aa.keySet().size(); looping++) {
if (GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[0]) == aa.keySet()[looping]) {
	flagBreak = 1
} else {
	println GlobalVariable.storeVar.getAt(GlobalVariable.storeVar.keySet()[0])
	println aa.keySet()[looping]
}
}