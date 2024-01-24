package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword

class DownloadReport {

	String data
	int columnCount
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet

	@Keyword
	getTotalDataDownloadReport(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery('SELECT COUNT(*) FROM tr_manual_report')

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}
}
