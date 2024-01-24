package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

class ErrorReport {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getTotalDataError(Connection conn, String date) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select COUNT(*) from tr_error_history trh JOIN ms_tenant mt ON trh.id_ms_tenant = mt.id_ms_tenant where error_date >= '" + date + "' AND mt.tenant_code = '" + GlobalVariable.Tenant + "'")

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

		resultSet = stm.executeQuery("select ERROR_MESSAGE from tr_error_history where cust_name = '" + name + "' AND id_ms_tenant = 1 ORDER BY id_error_history DESC LIMIT 1")

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

	@Keyword
	getErrorHistoryActStatus(Connection conn, String idErrorHistory) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT treh.user_name, treh.user_idno, msl.description FROM tr_error_history_user_detail treh LEFT JOIN tr_error_history teh on treh.id_error_history = teh.id_error_history LEFT JOIN ms_lov msl ON treh.lov_signer_type = msl.id_lov WHERE treh.id_error_history = '" + idErrorHistory + "' ")

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

	//	@Keyword
	//	getErrorHistoryAPI(Connection conn) {
	//		stm = conn.createStatement()
	//
	//		resultSet = stm.executeQuery("select teh.id_error_history, msl.description, teh.ref_number, teh.cust_name, teh.office, teh.region, teh.business_line, teh.error_type, teh.error_date, teh.error_message, msv.vendor_code from tr_error_history teh left join ms_lov msl on teh.lov_modul = msl.id_lov left join ms_vendor msv on teh.id_ms_vendor = msv.id_ms_vendor where teh.error_date >= date_trunc('month', now()) and teh.error_date <= now() LIMIT 10 OFFSET 11")
	//
	//		metadata = resultSet.metaData
	//
	//		columnCount = metadata.getColumnCount()
	//
	//		while (resultSet.next()) {
	//			for (i = 1 ; i <= columnCount ; i++) {
	//				data = resultSet.getObject(i)
	//				listdata.add(data)
	//			}
	//		}
	//		listdata
	//	}

	@Keyword
	getStatusActivationAPI(Connection conn, String email, String idErrorReport) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT COALESCE( CASE WHEN msvr.is_active = '1' THEN 'Sudah Aktivasi' WHEN msvr.is_active = '0' AND msvr.is_registered = '1' THEN 'Belum Aktivasi' ELSE 'Belum Registrasi' END ) AS registration_status FROM tr_error_history teh LEFT JOIN ms_vendor_registered_user msvr ON teh.id_ms_vendor = msvr.id_ms_vendor AND msvr.signer_registered_email = '" + email + "' WHERE teh.id_error_history = '" + idErrorReport + "';")
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
