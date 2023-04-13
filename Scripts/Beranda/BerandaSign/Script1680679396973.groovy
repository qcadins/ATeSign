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

GlobalVariable.Response = '00155D0B-7502-8128-11ED-D2984D31BA80'
WebUI.callTestCase(findTestCase('Send_Document/KotakMasuk'), [:], FailureHandling.STOP_ON_FAILURE)

'modify object btn TTD Dokumen Beranda'
modifyObjectbtnTTDDokumen = WebUI.modifyObjectProperty(findTestObject('Object Repository/KotakMasuk/btn_TTDDokumen'),'xpath','equals',"/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-dashboard1/div[3]/div/div/div[2]/div/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[8]/datatable-body-row/div[2]/datatable-body-cell[10]/div/a[1]",true)

WebUI.click(modifyObjectbtnTTDDokumen)

//WebUI.verifymatch nanti dari excel