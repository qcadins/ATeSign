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
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement

DB = ['MF','MF','CUST','CUST']

excel = ['MF','CUST']

signerpage = [1,2]

for (i = 0; i < excel.size(); i++){
countEmail = DB.count(excel[i])
WebUI.verifyEqual(countEmail, signerpage.size())
}

//'Pembuatan variable mengenai jumlah delete, jumlah lock, dan indexlock untuk loop kedepannya'
//RoleTandaTangan = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 15).split(';', -1)
//
//TipeTandaTangan = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 16).split(';', -1)
//
//SignBoxAction = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 17).split(';', -1)
//
//SignBoxLocation = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 18).split(';', -1)
//
//LockSignBox = findTestData(excelPathPengaturanDokumen).getValue(GlobalVariable.NumofColm, 19).split(';', -1)
//
//'count signbox'
//variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-setting-signer > div:nth-child(3) > div > app-document-anotate > section > section.box > div app-bbox'))
//
//def RoleTTD, tipeTTD
//
//'looping signbox sesuai jumlah yang ada di ui'
//for(index = 1; index <= variable.size() ; index++) {
//	'modify object Role sign box'
//	modifyObjectRoleSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals',
//		('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' +
//		index) + ']/div/div/small', true)
//	
//	'modify object Tipe sign box'
//	modifyObjectTipeSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals',
//		('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' +
//		index) + ']/div/div/span', true)
//	
//	'modify object sign box'
//	modifyObjectSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals',
//		('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' +
//		index) + ']/div', true)
//	
//	RoleTTD = WebUI.getText(modifyObjectRoleSignBox)
//	
//	if(!RoleTTD.equalsIgnoreCase('Meterai')) {
//		
//		tipeTTD = WebUI.getText(modifyObjectTipeSignBox).split(' ')[0]
//		
//	}else {
//		tipeTTD = RoleTTD
//	}
//	
//	if(tipeTTD == 'Prf') {
//		tipeTTD = 'Paraf'
//	}
//	
//	locationSignBox = WebUI.getAttribute(modifyObjectSignBox, 'style', FailureHandling.CONTINUE_ON_FAILURE)
//		
//	'looping signbox inputan excel'
//	for(indexExcel = 0; indexExcel < RoleTandaTangan.size(); indexExcel++) {
//		
//		if(!(RoleTTD.equalsIgnoreCase(RoleTandaTangan[indexExcel]) && tipeTTD.equalsIgnoreCase(TipeTandaTangan[indexExcel]))) {
//			if(indexExcel == RoleTandaTangan.size()-1) {
//				'modify object button delete sign box'
//				modifyObjectButtonDeleteSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals',
//					('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' +
//					index) + ']/div/button[2]/span', true)
//				
//				'click button delete'
//				WebUI.click(modifyObjectButtonDeleteSignBox)
//				
//				index--
//				
//				variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-setting-signer > div:nth-child(3) > div > app-document-anotate > section > section.box > div app-bbox'))
//			}
//		}else if(RoleTTD.equalsIgnoreCase(RoleTandaTangan[indexExcel]) && tipeTTD.equalsIgnoreCase(TipeTandaTangan[indexExcel])) {
//			
//			if(SignBoxAction[indexExcel].equalsIgnoreCase('Yes')){
//
//				if(!locationSignBox.contains(SignBoxLocation[indexExcel])) {
//					'memindahkan sign box'
//					'CUST'omKeywords.''CUST'omizeKeyword.JSExecutor.jsExecutionFunction'('arguments[0].style.transform = arguments[1]',
//						('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' +
//						index) + ']/div', SignBoxLocation[indexExcel])
//				}
//			}
//			
//			'modify object button lock sign box'
//			modifyObjectButtonLockSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals',
//				('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' +
//				index) + ']/div/button[1]', true)
//			
//			statusLock = WebUI.getAttribute(modifyObjectButtonLockSignBox, 'ng-reflect-ng-class', FailureHandling.CONTINUE_ON_FAILURE).toString()
//			
//			if(LockSignBox[indexExcel].equalsIgnoreCase('Yes')) {	
//				if(!statusLock.equalsIgnoreCase('fa-lock')) {
//					'click lock signbox'
//					WebUI.click(modifyObjectButtonLockSignBox)
//				}
//			}else if(LockSignBox[indexExcel].equalsIgnoreCase('Yes')) {
//				if(statusLock.equalsIgnoreCase('fa-lock')) {					
//					'click lock signbox'
//					WebUI.click(modifyObjectButtonLockSignBox)
//				}
//			}
//			
//			break
//		}
//	}
//}
//
//		
//	'looping signbox inputan excel'
//	for(indexExcel = 0; indexExcel < RoleTandaTangan.size(); indexExcel++) {
//		
//		
//		'looping signbox sesuai jumlah yang ada di ui'
//		for(index = 1; index <= variable.size() ; index++) {
//			'modify object Role sign box'
//			modifyObjectRoleSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals',
//				('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' +
//				index) + ']/div/div/small', true)
//			
//			'modify object Tipe sign box'
//			modifyObjectTipeSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals',
//				('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' +
//				index) + ']/div/div/span', true)
//			
//			'modify object sign box'
//			modifyObjectSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals',
//				('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' +
//				index) + ']/div', true)
//			
//			RoleTTD = WebUI.getText(modifyObjectRoleSignBox)
//			
//			if(!RoleTTD.equalsIgnoreCase('Meterai')) {
//				
//				tipeTTD = WebUI.getText(modifyObjectTipeSignBox).split(' ')[0]
//				
//			}else {
//				tipeTTD = RoleTTD
//			}
//			
//			if(tipeTTD == 'Prf') {
//				tipeTTD = 'Paraf'
//			}
//			
//			locationSignBox = WebUI.getAttribute(modifyObjectSignBox, 'style', FailureHandling.CONTINUE_ON_FAILURE)
//			
//		if(!(RoleTTD.equalsIgnoreCase(RoleTandaTangan[indexExcel]) && tipeTTD.equalsIgnoreCase(TipeTandaTangan[indexExcel]))) {
//			if(index == variable.size()) {
//                    if ((TipeTandaTangan[(indexExcel)]).equalsIgnoreCase('TTD')) {
//                        'Klik button tanda tangan'
//                        WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_ttd'))
//                    } else if ((TipeTandaTangan[(indexExcel)]).equalsIgnoreCase('Paraf')) {
//                        'Klik button tanda tangan'
//                        WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_paraf'))
//                    } else if ((TipeTandaTangan[(indexExcel)]).equalsIgnoreCase('Meterai')) {
//                        'Klik button tanda tangan'
//                        WebUI.click(findTestObject('Object Repository/TandaTanganDokumen/btn_materai'))
//                    }
//                    
//                    if ((TipeTandaTangan[(indexExcel)]).equalsIgnoreCase('TTD') || (TipeTandaTangan[(indexExcel)]).equalsIgnoreCase(
//                        'Paraf')) {
//                        'Verify label tanda tangannya muncul atau tidak'
//                        WebUI.verifyElementPresent(findTestObject('TandaTanganDokumen/lbl_TipeTandaTangan'), GlobalVariable.TimeOut, 
//                            FailureHandling.CONTINUE_ON_FAILURE)
//
//                        'Memilih tipe signer apa berdasarkan excel'
//                        WebUI.selectOptionByLabel(findTestObject('TandaTanganDokumen/ddl_TipeTandaTangan'), RoleTandaTangan[
//                            (indexExcel)], false)
//
//                        'Klik set tanda tangan'
//                        WebUI.click(findTestObject('TandaTanganDokumen/btn_setTandaTangan'))
//
//						'count signbox'
//						variable = DriverFactory.getWebDriver().findElements(By.cssSelector('body > app-root > app-full-layout > div > div.main-panel > div > div.content-wrapper > app-setting-signer > div:nth-child(3) > div > app-document-anotate > section > section.box > div app-bbox'))
//						
//                        'modify label tipe tanda tangan di kotak'
//                        modifyObjectRoleSignBox = WebUI.modifyObjectProperty(findTestObject('Object Repository/TandaTanganDokumen/lbl_TTDTipeTandaTangan'), 
//                            'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
//                            variable.size()) + ']/div/div/small', true)
//
//                        'Verifikasi antara excel dan UI, apakah tipenya sama'
//                        WebUI.verifyMatch(RoleTandaTangan[(indexExcel)], WebUI.getText(modifyObjectRoleSignBox), false)
//                    }
//                    
//                    'Verify apakah tanda tangannya ada'
//                    if (WebUI.verifyElementPresent(modifyObjectRoleSignBox, GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)) {
//                        'check if signbox mau dipindahkan'
//                        if ((SignBoxAction[(indexExcel)]).equalsIgnoreCase('Yes')) {
//                            'memindahkan sign box'
//                            'CUST'omKeywords.''CUST'omizeKeyword.JSExecutor.jsExecutionFunction'('arguments[0].style.transform = arguments[1]', 
//                                ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
//                                variable.size()) + ']/div', SignBoxLocation[(indexExcel)])
//                        }
//                        
//                        'check if signbox mau dilock posisinya'
//                        if ((LockSignBox[(indexExcel)]).equalsIgnoreCase('Yes')) {
//                            'modify obejct lock signbox'
//                            modifyobjectLockSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/btn_LockSignBox'), 
//                                'xpath', 'equals', ('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' + 
//                                variable.size()) + ']/div/button[1]/span', true)
//
//                            'click lock signbox'
//                            WebUI.click(modifyobjectLockSignBox)
//                        }
//                    }
//                }
//		}else if(RoleTTD.equalsIgnoreCase(RoleTandaTangan[indexExcel]) && tipeTTD.equalsIgnoreCase(TipeTandaTangan[indexExcel])) {
//			
//			if(SignBoxAction[indexExcel].equalsIgnoreCase('Yes')){
//
//				if(!locationSignBox.contains(SignBoxLocation[indexExcel])) {
//					'memindahkan sign box'
//					'CUST'omKeywords.''CUST'omizeKeyword.JSExecutor.jsExecutionFunction'('arguments[0].style.transform = arguments[1]',
//						('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' +
//						index) + ']/div', SignBoxLocation[indexExcel])
//				}
//			}
//			
//			'modify object button lock sign box'
//			modifyObjectButtonLockSignBox = WebUI.modifyObjectProperty(findTestObject('TandaTanganDokumen/modifyObject'), 'xpath', 'equals',
//				('/html/body/app-root/app-full-layout/div/div[2]/div/div[2]/app-setting-signer/div[2]/div/app-document-anotate/section/section[2]/div/app-bbox[' +
//				index) + ']/div/button[1]', true)
//			
//			statusLock = WebUI.getAttribute(modifyObjectButtonLockSignBox, 'ng-reflect-ng-class', FailureHandling.CONTINUE_ON_FAILURE).toString()
//			
//			if(LockSignBox[indexExcel].equalsIgnoreCase('Yes')) {
//				if(!statusLock.equalsIgnoreCase('fa-lock')) {
//					'click lock signbox'
//					WebUI.click(modifyObjectButtonLockSignBox)
//				}
//			}else if(LockSignBox[indexExcel].equalsIgnoreCase('Yes')) {
//				if(statusLock.equalsIgnoreCase('fa-lock')) {
//					'click lock signbox'
//					WebUI.click(modifyObjectButtonLockSignBox)
//				}
//			}
//			
//			break
//		}
//	}
//}
//
////arrayBESAR = []
////
////array1 = ['refnum1','refnum2', 'refnum3']
////
////array3 = ['namadokumen1','namadokumen2', 'namadokumen3']
////
////array4 = ['signer','signer','signer']
////
////array2 = [['wiky','kegar'],['wilis','fendy'],['hendra','edgar']]
////
////ArrayList<String> stringRefno = new ArrayList<String>()
////
////for(i = 0; i < array1.size(); i++) {
////	ArrayList<String> string = new ArrayList<String>()
////	string.add(array1[i])
////	
////	stringRefno.add(string)
//////	string = new ArrayList<String>()
////
////	for(x = 0; x < array2[i].size(); x++) {
////		if(x == 0) {
////			
////		array4[i]= array4[i] + ': [{' + array2[i][x]
////		}else {
////			array4[i]= array4[i] + ', '+ array2[i][x] + '}'
////		}
////	}
////	string.add(array4[i])
//////	stringRefno.add(string)
//////	string = new ArrayList<String>()
////	string.add(array3[i])
////	stringRefno.add(string)
//////	stringRefno = stringRefno+string
////	
////}
////
////println(stringRefno)
