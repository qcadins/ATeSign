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

tenantCode = GlobalVariable.Tenant
email = '3511000101802884'
		if (email.length() == 16 && email.matches("\\d+")) {
			emailWhere = "amm.hashed_id_no = '" + email + "'"
		} else {
			emailWhere = "amm.login_id = '" + email + "'"
		}

println("select amm.login_id, msv.vendor_name from ms_useroftenant muot join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_user_personal_data aupd on amm.id_ms_user = aupd.id_ms_user where "+emailWhere+" and msv.is_active = '1' and msv.is_operating = '1' and mst.tenant_code = '"+tenantCode+"'")