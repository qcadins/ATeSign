package connection
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import java.sql.Connection
import java.sql.DriverManager
import com.kms.katalon.core.annotation.Keyword

public class ConnectDB {

	@Keyword
	def connectDBeSign() {

		String servername = findTestData('Login/Login').getValue(1, 8)

		String port = findTestData('Login/Login').getValue(2, 8)

		String database = findTestData('Login/Login').getValue(3, 8)

		String username = findTestData('Login/Login').getValue(4, 8)

		String password = findTestData('Login/Login').getValue(5, 8)

		String url = servername + ':' + port + '/' + database

		Connection conn = DriverManager.getConnection(url, username, password)

		return conn
	}
}
