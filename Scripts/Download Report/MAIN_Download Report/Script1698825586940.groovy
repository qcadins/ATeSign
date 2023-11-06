import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection as Connection
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys
import org.openqa.selenium.JavascriptExecutor as JavascriptExecutor
import org.openqa.selenium.WebDriver as WebDriver

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect dengan db'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathDownRep).columnNumbers

int firstRun = 0, totalData

'looping saldo'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
	if (findTestData(excelPathDownRep).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
		break
	} else if (findTestData(excelPathDownRep).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase('Unexecuted')) {
		
		GlobalVariable.FlagFailed = 0
		
		'get data tenant'
		totalData = CustomKeywords.'connection.DownloadReport.getTotalDataDownloadReport'(conneSign)
		
		'check if email login case selanjutnya masih sama dengan sebelumnya'
		if (findTestData(excelPathDownRep).getValue(GlobalVariable.NumofColm - 1, rowExcel('Email Login')) !=
			findTestData(excelPathDownRep).getValue(GlobalVariable.NumofColm, rowExcel('Email Login')) || firstRun == 0) {
			'call test case login per case'
			WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet, ('Path') : excelPathDownRep, ('Email') : 'Email Login', ('Password') : 'Password Login'
				, ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], FailureHandling.STOP_ON_FAILURE)
			
			'apakah cek paging diperlukan di awal run'
			if(GlobalVariable.checkPaging.equals('Yes')) {
				'call function check paging'
				checkPaging(conneSign, totalData)
			}
			firstRun = 1
		}
		
		'lakukan loop hingga mendapat data yang diinginkan'
		for (index = 1; index <= totalData; index++) {
			// Modify the object in the DownloadReport folder to match the current row
			modifyObjectPeriode = WebUI.modifyObjectProperty(findTestObject('DownloadReport/modifyObject'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-download-report/div/div/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
				index) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', true)
		
			// If the text matches the data in the Excel sheet, click the "Download" button
			if (findTestData(excelPathDownRep).getValue(GlobalVariable.NumofColm, rowExcel('$Report Month')).equalsIgnoreCase(WebUI.getText(modifyObjectPeriode))) {
				'modify button download'
				modifyObjectDownload = WebUI.modifyObjectProperty(findTestObject('DownloadReport/modifyObject'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-download-report/div/div/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
					index) + ']/datatable-body-row/div[2]/datatable-body-cell[3]/div/a/em', true)
				
				'click button download'
				WebUI.click(modifyObjectDownload)
				
				if (WebUI.verifyElementPresent(findTestObject('DownloadReport/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
					'get error log'
					errorLog = '<' +  WebUI.getText(findTestObject('DownloadReport/errorLog')) + '>'
					
					'write to excel failed download'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
							(findTestData(excelPathDownRep).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') +
									errorLog)
					
					GlobalVariable.FlagFailed = 1
					
				} else {
					'check is file downloaded dan apakah mau di delete'
					if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathDownRep).getValue(
							GlobalVariable.NumofColm, rowExcel('Delete Downloaded File ?'))) == true) {
						if(GlobalVariable.FlagFailed == 0) {
							'write to excel success'
							CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet,
									0, GlobalVariable.NumofColm - 1, GlobalVariable.StatusSuccess)
						}
					}
				}
				
				'click first page'
				WebUI.click(findTestObject('DownloadReport/button_Min'))
				
				break
			} else {
				'jika page masih lebih kecil dari total page'
				if (index%10 == 0) {
					'click next page'
					WebUI.click(findTestObject('DownloadReport/button_Next'))
					
					'rest index menjadi 0'
					index = 0
					
					'kurangi total data sebanyak 10'
					totalData = totalData - 10
					
				} else if (index == totalData) {
					'write to excel failed download'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
							(findTestData(excelPathDownRep).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') +
									' Report Tidak Ditemukan')
					
					GlobalVariable.FlagFailed = 1		
					
					'click first page'
					WebUI.click(findTestObject('DownloadReport/button_Min'))
				}
				
				continue
			}
		}
	}
}

def rowExcel(String cellValue) {
	return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def checkPaging(Connection conneSign, int resultTotalData) {
	settingzoom()

	'get text total data dari ui'
	Total = WebUI.getText(findTestObject('DownloadReport/label_TotalData')).split(' ')

	'verify total data tenant'
	checkVerifyPaging(WebUI.verifyEqual(resultTotalData, Total[0], FailureHandling.CONTINUE_ON_FAILURE))

	if (Integer.parseInt(Total[0]) > 10) {
		
		'click page 2'
		WebUI.click(findTestObject('DownloadReport/button_page2'))
	
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DownloadReport/button_page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
	
		'click page 1'
		WebUI.click(findTestObject('DownloadReport/button_page1'))
	
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DownloadReport/button_page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
	
		'get total page'
		def variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-download-report > div > div > div > div:nth-child(2) > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))
		
		'click next page'
		WebUI.click(findTestObject('DownloadReport/button_Next'))
	
		'verify paging di page 2'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DownloadReport/button_page2'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
	
		'click prev page'
		WebUI.click(findTestObject('DownloadReport/button_Previous'))
	
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DownloadReport/button_page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
	
		'click max page'
		WebUI.click(findTestObject('DownloadReport/button_Max'))
		
		'get total data'
		lastPage = Double.parseDouble(WebUI.getText(findTestObject('DownloadReport/label_TotalData')).split(' ',-1)[0])/10
		
		'jika hasil perhitungan last page memiliki desimal'
		if (lastPage.toString().contains('.0')) {
			'tidak ada round up'
			additionalRoundUp = 0
		} else {
			'round up dengan tambahan 0.5'
			additionalRoundUp = 0.5
		}
		
		'get total page'
		totalPage = Math.round(lastPage + additionalRoundUp).toString()
		
		'verify paging di page terakhir'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DownloadReport/activePage'), 'aria-label',
					FailureHandling.CONTINUE_ON_FAILURE), 'page ' + totalPage,
				false, FailureHandling.CONTINUE_ON_FAILURE))
		
		'click min page'
		WebUI.click(findTestObject('DownloadReport/button_Min'))
	
		'verify paging di page 1'
		checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DownloadReport/button_Page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE),
				'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
	}
}

def zoomSetting(int percentage) {
	Float percentageZoom = percentage / 100

	WebDriver driver = DriverFactory.webDriver

	'buka tab baru'
		((driver) as JavascriptExecutor).executeScript('window.open();')

	'ambil index tab yang sedang dibuka di chrome'
	int currentTab = WebUI.getWindowIndex()

	'ganti fokus robot ke tab baru'
	WebUI.switchToWindowIndex(currentTab + 1)

	driver.get('chrome://settings/')

		((driver) as JavascriptExecutor).executeScript(('chrome.settingsPrivate.setDefaultZoom(' + percentageZoom.toString()) +
		');')

	'close tab baru'
		((driver) as JavascriptExecutor).executeScript('window.close();')
}

def settingzoom() {
	'ambil index tab yang sedang dibuka di chrome'
	int currentTab = WebUI.getWindowIndex()

	'setting zoom menuju 70 persen'
	zoomSetting(70)

	'ganti fokus robot ke tab baru'
	WebUI.switchToWindowIndex(currentTab)

	'focus ke menu'
	WebUI.focus(findTestObject('DownloadReport/menu_DownloadReport'))
	
	'click menu download report'
	WebUI.click(findTestObject('DownloadReport/menu_DownloadReport'))

	'ambil index tab yang sedang dibuka di chrome'
	currentTab = WebUI.getWindowIndex()

	'setting zoom menuju 100 persen'
	zoomSetting(100)

	'ganti fokus robot ke tab baru'
	WebUI.switchToWindowIndex(currentTab)
	
	'delay menunggu menu loading'
	WebUI.delay(GlobalVariable.TimeOut)
}

def checkVerifyPaging(Boolean isMatch) {
	if (isMatch == false) {
		'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
		CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
			(findTestData(excelPathDownRep).getValue(GlobalVariable.NumofColm, 2) + ';') + GlobalVariable.ReasonFailedPaging)

		GlobalVariable.FlagFailed = 1
	}
}