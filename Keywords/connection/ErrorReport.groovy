package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class ErrorReport {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getTotalDataError(Connection conn, String date) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select COUNT(*) from tr_error_history where error_date >= '" +  date  + "' AND id_ms_tenant = 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getErrorReportDetail(Connection conn, String name) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select ERROR_MESSAGE from tr_error_history where cust_name = '" +  name  + "' AND id_ms_tenant = 1 ORDER BY id_error_history DESC LIMIT 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
	
	@Keyword
	getStatusActivation(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT cust_name, cust_idno, is_registered, is_active FROM tr_error_history teh JOIN ms_vendor_registered_user mvru ON teh.id_ms_vendor = mvru.id_ms_vendor ORDER BY teh.error_date DESC LIMIT 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata
	}
}
