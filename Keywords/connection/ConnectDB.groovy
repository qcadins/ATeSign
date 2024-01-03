package connection

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import java.sql.Connection
import java.sql.DriverManager
import com.kms.katalon.core.annotation.Keyword

public class ConnectDB {

    @Keyword
    connectDBeSign() {
        def testData = findTestData('Login/Login')
        
        String servername = testData.getValue(1, 13)
        String port = testData.getValue(2, 13)
        String database = testData.getValue(3, 13)
        String username = testData.getValue(4, 13)
        String password = testData.getValue(5, 13)

        String url = "${servername}:${port}/${database}"

        DriverManager.getConnection(url, username, password)
    }
	
}
