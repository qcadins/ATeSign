package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class Meterai {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getTotalMeterai(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT COUNT(*) FROM tr_stamp_duty tsd JOIN ms_tenant mt ON mt.id_ms_tenant = tsd.id_ms_tenant where tsd.dtm_crt >= date_trunc('month', now()) and tsd.dtm_crt <= now() AND tenant_code = '"+ GlobalVariable.Tenant +"'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
}
