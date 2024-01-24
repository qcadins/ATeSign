package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword

class Tenant {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getTotalTenant(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery('select count(*) from ms_tenant')

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getTenantStoreDB(Connection conn, String refnum) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tenant_name, tenant_code, ref_number_label, api_key, email_reminder_dest from ms_tenant where ref_number_label = '" +  refnum  + "'")

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

	@Keyword
	getTenantServices(Connection conn, String tenantname) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select threshold_balance from ms_tenant where tenant_name = '" +  tenantname  + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getTenantServicesDescription(Connection conn, String tenantname) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT ms_lov.description, value FROM ms_tenant CROSS JOIN LATERAL json_each_text(threshold_balance::json) AS threshold_type JOIN ms_lov ON ms_lov.lov_group = 'BALANCE_TYPE' AND ms_lov.code = threshold_type.key WHERE tenant_name = '" +  tenantname  + "'")

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
