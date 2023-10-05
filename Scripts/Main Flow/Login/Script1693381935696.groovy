import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.configuration.RunConfiguration as RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import java.sql.Connection as Connection

'connect DB eSign'
Connection conneSign = CustomKeywords.'connection.ConnectDB.connectDBeSign'()

'setting untuk membuat lokasi default folder download'
HashMap<String, String> chromePrefs = new HashMap<String, String>()

chromePrefs.put('download.default_directory', System.getProperty('user.dir') + '\\Download')

RunConfiguration.setWebDriverPreferencesProperty('prefs', chromePrefs)

if (GlobalVariable.RunWith == 'Mobile') {
	
	'ambil koordinat dari settings'
	ArrayList coordinates = findTestData('Login/Setting').getValue(13, 2).split(',')
	
	'open browser'
	WebUI.openBrowser(findTestData('Login/Login').getValue(1, 2))
	
	'klik titik tiga'
	Mobile.tapAtPosition(1000, 180, FailureHandling.OPTIONAL)
	
	'aktifkan view desktop sites'
	Mobile.tapAtPosition(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), FailureHandling.OPTIONAL)
	
} else {
	'open browser'
	WebUI.openBrowser('')
	
	'navigate to url esign'
	WebUI.navigateToUrl(findTestData('Login/Login').getValue(1, 2))
	
	'maximized window'
	WebUI.maximizeWindow()
}

if (email == '') {
    if (linkUrl != '') {
        runWithEmbed(linkUrl)
    } else {
        'input email'
        WebUI.setText(findTestObject('Login/input_Email'), findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Username')))

        'store GV user login'
        GlobalVariable.userLogin = findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Username'))

        'input password'
        WebUI.setText(findTestObject('Login/input_Password'), findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Password')))

        'click button login'
        WebUI.click(findTestObject('Login/button_Login'), FailureHandling.STOP_ON_FAILURE)

        'input perusahaan'
        WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Perusahaan')))

        'enter untuk input perusahaan'
        WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))

        'input peran'
        WebUI.setText(findTestObject('Login/input_Peran'), findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Role')))

        'enter untuk input peran'
        WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))

        'fokus pada tombol pilih peran'
        WebUI.focus(findTestObject('Login/button_pilihPeran'))

        'click button pilih peran'
        WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
    }
} else {
    if ((checkBeforeSigning == 'Yes') || (findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Option for Sign Document per Signer')).split(';', -1)[GlobalVariable.indexUsed] == 
    'Sign Via Inbox')) {
        'input email'
        WebUI.setText(findTestObject('Login/input_Email'), email)

        'input password asumsi password = P@ssw0rd'
        WebUI.setText(findTestObject('Login/input_Password'), findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel(
                    'Password Signer')))

        'click button login'
        WebUI.click(findTestObject('Login/button_Login'), FailureHandling.STOP_ON_FAILURE)

        if (WebUI.verifyElementPresent(findTestObject('Login/input_Perusahaan'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL)) {
            'input perusahaan'
            WebUI.setText(findTestObject('Login/input_Perusahaan'), findTestData(excel).getValue(GlobalVariable.NumofColm, 
                    rowExcel('Perusahaan')))

            'enter untuk input perusahaan'
            WebUI.sendKeys(findTestObject('Login/input_Perusahaan'), Keys.chord(Keys.ENTER))

            'input peran'
            WebUI.click(findTestObject('Login/input_Peran'))

            WebUI.delay(2)
			
			GlobalVariable.roleLogin = WebUI.getText(findTestObject('Login/peranTerpilih'))
			
            'enter untuk input peran'
            WebUI.sendKeys(findTestObject('Login/input_Peran'), Keys.chord(Keys.ENTER))

            'click button pilih peran'
            WebUI.click(findTestObject('Login/button_pilihPeran'), FailureHandling.STOP_ON_FAILURE)
        } else {
			GlobalVariable.roleLogin = CustomKeywords.'connection.SendSign.getRoleLogin'(conneSign, email, GlobalVariable.Tenant)

		}
    } else if ((findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Option for Sign Document per Signer')) == 
    'Webview Sign') || (findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Option for Sign Document per Signer')) == 
    'Embed Sign')) {
        runWithEmbed(linkUrl)
    }
    
    'Jika error lognya muncul'
    if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && 
    (GlobalVariable.FlagFailed == 0)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.CONTINUE_ON_FAILURE)

        if (errormessage != null) {
            'Tulis di excel itu adalah error'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcelStatusReason'(sheet, GlobalVariable.NumofColm, GlobalVariable.StatusWarning, 
                (((findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + errormessage) + 
                '>')

            GlobalVariable.FlagFailed = 1
        }
    } else if (WebUI.verifyElementPresent(findTestObject('KotakMasuk/Sign/errorLog'), GlobalVariable.TimeOut, FailureHandling.OPTIONAL) && 
    (GlobalVariable.FlagFailed > 0)) {
        'ambil teks errormessage'
        errormessage = WebUI.getAttribute(findTestObject('KotakMasuk/Sign/errorLog'), 'aria-label', FailureHandling.OPTIONAL)

        if (errormessage != null) {
            'write to excel reason warning'
            CustomKeywords.'customizekeyword.WriteExcel.writeToExcel'(GlobalVariable.DataFilePath, sheet, rowExcel('Status'), GlobalVariable.NumofColm - 
                1, (((findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Reason Failed')).replace('-', '') + ';') + '<') + errormessage) + 
                '>')
        }
    }
}

def rowExcel(String cellValue) {
    return CustomKeywords.'customizekeyword.WriteExcel.getExcelRow'(GlobalVariable.DataFilePath, 'Main', cellValue)
}

def runWithEmbed(String linkUrl) {
    if ((findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Option for Sign Document per Signer')).split(';', 
        -1)[GlobalVariable.indexUsed]) == 'Webview Sign') {
        GlobalVariable.RunWithEmbed = 'No'
    } else if ((findTestData(excel).getValue(GlobalVariable.NumofColm, rowExcel('Option for Sign Document per Signer')).split(
        ';', -1)[GlobalVariable.indexUsed]) == 'Embed Sign') {
        GlobalVariable.RunWithEmbed = 'Yes'
    }
    
    'check if ingin menggunakan embed atau tidak'
    if (GlobalVariable.RunWithEmbed == 'Yes') {
		
		if (GlobalVariable.RunWith == 'Mobile') {
			//belum work
			'ambil koordinat dari settings'
			ArrayList coordinates = findTestData('Login/Setting').getValue(13, 2).split(',')
			
			'open browser'
			WebUI.openBrowser(GlobalVariable.embedUrl)
			
			'Diberikan delay 3 sec'
			WebUI.delay(3)
			
			'klik titik tiga'
			Mobile.tapAtPosition(1000, 180, FailureHandling.OPTIONAL)
			
			'aktifkan view desktop sites'
			Mobile.tapAtPosition(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), FailureHandling.OPTIONAL)
			
		} else {
			'navigate url ke daftar akun'
			WebUI.openBrowser(GlobalVariable.embedUrl)
	
			'Diberikan delay 3 sec'
			WebUI.delay(3)
	
			'Maximize windows'
			WebUI.maximizeWindow()
		}

        'Set text link Url'
        WebUI.setText(findTestObject('EmbedView/inputLinkEmbed'), linkUrl)

        'click button embed'
        WebUI.click(findTestObject('EmbedView/button_Embed'))

        'swith to iframe'
        WebUI.switchToFrame(findTestObject('EmbedView/iFrameEsign'), GlobalVariable.TimeOut, FailureHandling.CONTINUE_ON_FAILURE)
    } else if (GlobalVariable.RunWithEmbed == 'No') {
		
		if (GlobalVariable.RunWith == 'Mobile') {
			//belum work
			'ambil koordinat dari settings'
			ArrayList coordinates = findTestData('Login/Setting').getValue(13, 2).split(',')
			
			'buka web browser'
			WebUI.openBrowser(findTestData('Login/Login').getValue(1, 2))
			
			'Diberikan delay 3 sec'
			WebUI.delay(15)
			
			'klik titik tiga'
			Mobile.tapAtPosition(1000, 180, FailureHandling.OPTIONAL)
			
			'aktifkan view desktop sites'
			Mobile.tapAtPosition(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), FailureHandling.OPTIONAL)
			
			'open browser'
			WebUI.navigateToUrl(linkUrl)
			
		} else {
			
			'navigate url ke daftar akun'
			WebUI.openBrowser(linkUrl)
	
			'Maximize Windows'
			WebUI.maximizeWindow()
			
		}
    }
}

