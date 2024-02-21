package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword

class ViewUserOTP {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []
	HashMap<String, String> commandSql = [:]

	@Keyword
	getTotalViewUserOTP(Connection conn, String user) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(msvr.id_ms_vendor_registered_user) from ms_vendor_registered_user msvr left join am_msuser amm on amm.id_ms_user = msvr.id_ms_user left join ms_vendor msv on msv.id_ms_vendor = msvr.id_ms_vendor where (msvr.signer_registered_email = '"+user+"' OR hashed_signer_registered_phone = encode(sha256('" + user + "'), 'hex') OR hashed_id_no = encode(sha256('" + user + "'), 'hex'))")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getFilterViewUserOTP(Connection conn, String user) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.full_name, msvr.signer_registered_email, CASE WHEN msvr.is_registered = '0' THEN 'Belum Registrasi' WHEN msvr.is_active = '0' THEN 'Belum Aktivasi' WHEN msvr.is_active = '1' THEN 'Sudah Aktivasi' END, msv.vendor_name from ms_vendor_registered_user msvr left join am_msuser amm on amm.id_ms_user = msvr.id_ms_user left join ms_vendor msv on msv.id_ms_vendor = msvr.id_ms_vendor where (msvr.signer_registered_email = '" + user + "' OR msvr.hashed_signer_registered_phone = encode(sha256('" + user + "'), 'hex') OR hashed_id_no = encode(sha256('" + user + "'), 'hex')) ORDER BY msvr.dtm_crt asc")
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
	getOtpCode(Connection conn, String user) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.otp_code from ms_vendor_registered_user msvr left join am_msuser amm on amm.id_ms_user = msvr.id_ms_user left join ms_vendor msv on msv.id_ms_vendor = msvr.id_ms_vendor where (msvr.signer_registered_email = '" + user + "' OR msvr.hashed_signer_registered_phone = encode(sha256('" + user + "'), 'hex') OR hashed_id_no = encode(sha256('" + user + "'), 'hex'))")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getResetCode(Connection conn, String user) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.reset_code from ms_vendor_registered_user msvr left join am_msuser amm on amm.id_ms_user = msvr.id_ms_user left join ms_vendor msv on msv.id_ms_vendor = msvr.id_ms_vendor where (msvr.signer_registered_email = '" + user + "' OR msvr.hashed_signer_registered_phone = encode(sha256('" + user + "'), 'hex') OR hashed_id_no = encode(sha256('" + user + "'), 'hex'))")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
}
