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
import java.util.ArrayList as arr
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.By as By
'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

GlobalVariable.NumofColm = 2

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign Main.xlsx')

aa = CustomKeywords.'connection.DataVerif.getDetailTrx'(conneSign, '834839')
println aa

  'get row lastest'
                        modifyObjectBtnLastest = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/Sign/modifyObject'), 
                            'class', 'equals', ('datatable-icon-skip'), true)

						'jika btn lastest dapat diclick'
                        if (WebUI.verifyElementPresent(modifyObjectBtnLastest, GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
                            WebUI.focus(modifyObjectBtnLastest)

                            'Klik button Lastest'
                            WebUI.click(modifyObjectBtnLastest, FailureHandling.CONTINUE_ON_FAILURE)
                        }