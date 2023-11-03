import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import java.sql.Connection

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.driver.DriverFactory as DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.By as By
import org.openqa.selenium.Keys as Keys

'get data file path'
GlobalVariable.DataFilePath = CustomKeywords.'customizekeyword.WriteExcel.getExcelPath'('\\Excel\\2. Esign.xlsx')

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'get colm excel'
int countColmExcel = findTestData(excelPathDownloadReport).columnNumbers

int firstRun = 0

String totalPage

'looping Download Report'
for (GlobalVariable.NumofColm = 2; GlobalVariable.NumofColm <= countColmExcel; (GlobalVariable.NumofColm)++) {
    if (findTestData(excelPathDownloadReport).getValue(GlobalVariable.NumofColm, rowExcel('Status')).length() == 0) {
        break
    } else if (findTestData(excelPathDownloadReport).getValue(GlobalVariable.NumofColm, rowExcel('Status')).equalsIgnoreCase(
        'Unexecuted')) {
	
		GlobalVariable.FlagFailed = 0
		
		int page = 1
		
        'check if button menu visible atau tidak'
        if ((WebUI.verifyElementNotPresent(findTestObject('RegisterEsign/checkSaldo/menu_Saldo'), GlobalVariable.TimeOut, 
            FailureHandling.OPTIONAL) || (findTestData(excelPathDownloadReport).getValue(GlobalVariable.NumofColm - 1, rowExcel(
                'Email Login')) != findTestData(excelPathDownloadReport).getValue(GlobalVariable.NumofColm, rowExcel('Email Login')))) || 
        (firstRun == 0)) {
            WebUI.callTestCase(findTestCase('Login/Login_perCase'), [('SheetName') : sheet, ('Path') : excelPathDownloadReport
                    , ('Email') : 'Email Login', ('Password') : 'Password Login', ('Perusahaan') : 'Perusahaan Login', ('Peran') : 'Peran Login'], 
                FailureHandling.STOP_ON_FAILURE)

            totalPage = checkPaging()

            firstRun = 1
        }
        
		'click menu download report'
        WebUI.click(findTestObject('DownloadReport/menu_DownloadReport'))
		
		'delay menunggu menu loading'
		WebUI.delay(GlobalVariable.TimeOut)
		
		'get row'
		variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-download-report > div > div > div > div:nth-child(2) > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
		
		// Use a for loop to iterate through each row
		for (index = 1; index <= variable.size(); index++) {
			// Modify the object in the DownloadReport folder to match the current row
			modifyObjectPeriode = WebUI.modifyObjectProperty(findTestObject('DownloadReport/modifyObject'), 'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-download-report/div/div/div/div[2]/app-msx-datatable/section/ngx-datatable/div/datatable-body/datatable-selection/datatable-scroller/datatable-row-wrapper[' +
				index) + ']/datatable-body-row/div[2]/datatable-body-cell[1]/div/p', true)
		
			// If the text matches the data in the Excel sheet, click the "Download" button
			if (findTestData(excelPathDownloadReport).getValue(GlobalVariable.NumofColm, rowExcel('$Periode')).equalsIgnoreCase(WebUI.getText(modifyObjectPeriode))) {
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
							(findTestData(excelPathDownloadReport).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') +
									errorLog)
					
					GlobalVariable.FlagFailed = 1
					
				} else {
					'check is file downloaded dan apakah mau di delete'
					if (CustomKeywords.'customizekeyword.Download.isFileDownloaded'(findTestData(excelPathDownloadReport).getValue(
							GlobalVariable.NumofColm, rowExcel('Delete File ?'))) == true) {
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
				if (page < Integer.parseInt(totalPage)) {
					'click next page'
					WebUI.click(findTestObject('DownloadReport/button_Next'))
					
					'+1 page'
					page++
					
					'rest index menjadi 0'
					index = 0
					
					'reset row page baru'
					variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-download-report > div > div > div > div:nth-child(2) > app-msx-datatable > section > ngx-datatable > div > datatable-body > datatable-selection > datatable-scroller datatable-row-wrapper'))
				} else if(index == variable.size()){
					'write to excel failed download'
					CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed,
							(findTestData(excelPathDownloadReport).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') +
									' Report Tidak Ditemukan')
					
					GlobalVariable.FlagFailed = 1			}
				
				continue
			}
		}
    }
}



def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, sheet, cellValue)
}

def checkPaging() {
	
	'click menu download report'
	WebUI.click(findTestObject('DownloadReport/menu_DownloadReport'))
	
	'tunggu page loading'
	WebUI.delay(GlobalVariable.TimeOut)
	
	Double totalData = Double.parseDouble(WebUI.getText(findTestObject('DownloadReport/label_TotalData')).split(' ', -1)[0])
	
	String totalPage
	 
    'check if ada paging'
    if (totalData > 10) {
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

        'get total page'
        variable = DriverFactory.webDriver.findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-download-report > div > div > div > div:nth-child(2) > app-msx-datatable > section > ngx-datatable > div > datatable-footer > div > datatable-pager > ul li'))

        'click max page'
        WebUI.click(findTestObject('DownloadReport/button_Max'))

        'get total data'
        Double lastPage = totalData/10
		
        'jika hasil perhitungan last page memiliki desimal'
        if (lastPage.toString().endsWith('.0')) {
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
        checkVerifyPaging(WebUI.verifyMatch(WebUI.getAttribute(findTestObject('DownloadReport/button_page1'), 'class', FailureHandling.CONTINUE_ON_FAILURE), 
                'pages active ng-star-inserted', false, FailureHandling.CONTINUE_ON_FAILURE))
    }
	
	return totalPage
}

def checkVerifyEqualorMatch(Boolean isMatch, String reason) {
    if (isMatch == false) {
        'Write to excel status failed and ReasonFailedVerifyEqualorMatch'
        GlobalVariable.FlagFailed = 1

        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            ((findTestData(excelPathDownloadReport).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedVerifyEqualOrMatch) + reason)
    }
}

def checkVerifyPaging(Boolean isMatch) {
    if (isMatch == false) {
        'Write To Excel GlobalVariable.StatusFailed and GlobalVariable.ReasonFailedVerifyEqualOrMatch'
        CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusFailed, 
            (findTestData(excelPathDownloadReport).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')) + ';') + 
            GlobalVariable.ReasonFailedPaging)

        GlobalVariable.FlagFailed = 1
    }
}

