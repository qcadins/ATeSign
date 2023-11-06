package connection

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class DownloadReport {

	String data, helperQuery
	int columnCount, i, countLengthforSHA256 = 64, updateVariable
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet, helperResult
	ArrayList<String> listdata = []
	String emailWhere, selectData

	@Keyword
	getTotalDataDownloadReport(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT COUNT(*) FROM tr_manual_report")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}
}
