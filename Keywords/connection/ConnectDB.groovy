package connection

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import java.sql.DriverManager
import com.kms.katalon.core.annotation.Keyword

class ConnectDB {

	@Keyword
	connectDBeSign() {
		String servername = findTestData('Login/Login').getValue(1, 13)
		String port = findTestData('Login/Login').getValue(2, 13)
		String database = findTestData('Login/Login').getValue(3, 13)
		String username = findTestData('Login/Login').getValue(4, 13)
		String password = findTestData('Login/Login').getValue(5, 13)

		String url = "${servername}:${port}/${database}"

		DriverManager.getConnection(url, username, password)
	}
}
