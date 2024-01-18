package connection

import com.kms.katalon.core.annotation.Keyword
import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.ResultSetMetaData

public class ForgotPassword {

	String data, helperQuery
	int columnCount = 0 , i = 0, countLengthforSHA256 = 64
	Statement stm
	ResultSet resultSet
	ResultSetMetaData metadata
	ArrayList<String> listdata = []

	@Keyword
	getResetCode(Connection conn, String email) {
		stm = conn.createStatement()

		if (email.length() == countLengthforSHA256) {
			helperQuery = 'hashed_phone'
		} else {
			helperQuery = 'login_id'
		}

		resultSet = stm.executeQuery("SELECT reset_code FROM am_msuser WHERE " + helperQuery + " = '" + email + "'")

		while (resultSet.next()){

			data = resultSet.getObject(1);
		}

		data
	}

	@Keyword
	getResetCodeLimit(Connection conn) {

		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT gs_value FROm am_generalsetting WHERE gs_code = 'OTP_RESET_PWD_DAILY'")

		while (resultSet.next()){

			data = resultSet.getObject(1);
		}

		Integer.parseInt(data)
	}

	//	@Keyword
	//	getOTPActiveDuration(Connection conn, String email) {
	//
	//		stm = conn.createStatement()
	//
	//		resultSet = stm.executeQuery("SELECT mt.otp_active_duration FROM am_msuser amu LEFT JOIN ms_useroftenant mot ON mot.id_ms_user = amu.id_ms_user LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mot.id_ms_tenant LEFT JOIN tr_document_h tdh ON tdh.id_ms_tenant = mt.id_ms_tenant WHERE amu.login_id = '" + email + "' ORDER BY id_document_h DESC LIMIT 1")
	//
	//		while (resultSet.next()) {
	//			data = resultSet.getObject(1)
	//		}
	//		if (data != null) {
	//			Integer.parseInt(data)
	//		} else {
	//			data = 0
	//		}
	//	}

	@Keyword
	getResetNum(Connection conn, String email) {
		int data

		if (email.length() == countLengthforSHA256) {
			helperQuery = 'hashed_phone'
		} else {
			helperQuery = 'login_id'
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT reset_code_request_num FROM am_msuser where " + helperQuery + " = '" +  email  + "'")

		while (resultSet.next()){

			data = resultSet.getObject(1);
		}

		data
	}

	@Keyword
	getTenantCode(Connection conn, String email) {
		data

		if (email.length() == countLengthforSHA256) {
			helperQuery = 'amu.hashed_phone'
		} else {
			helperQuery = 'amu.login_id'
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT mt.tenant_code FROM am_msuser amu LEFT JOIN ms_useroftenant mot ON mot.id_ms_user = amu.id_ms_user LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mot.id_ms_tenant LEFT JOIN tr_document_h tdh ON tdh.id_ms_tenant = mt.id_ms_tenant WHERE " + helperQuery + " = '" + email + "' ORDER BY id_document_h DESC LIMIT 1")

		while (resultSet.next()){

			data = resultSet.getObject(1);
		}

		data
	}

	@Keyword
	updateResetRequestNum(Connection conn, String email) {

		stm = conn.createStatement()

		if (email.length() == countLengthforSHA256) {
			helperQuery = 'hashed_phone'
		} else {
			helperQuery = 'login_id'
		}

		stm.executeUpdate("UPDATE am_msuser SET reset_code_request_num = 0 WHERE " + helperQuery + " = '" + email + "';")
	}

	@Keyword
	updateOTPActiveDuration(Connection conn, String tenantcode, int otpduration) {

		stm = conn.createStatement()

		stm.executeUpdate("UPDATE ms_tenant SET otp_active_duration = " + otpduration + " WHERE tenant_code = '" + tenantcode + "';")
	}

	@Keyword
	getTrxSaldoWASMS(Connection conn, String usage, String fullName) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tbm.trx_no, TO_CHAR(tbm.trx_date, 'YYYY-MM-DD HH24:MI:SS'), 'Use ' || msl.description, amm.full_name, '', '', '', tbm.notes, tbm.qty FROM tr_balance_mutation tbm LEFT JOIN ms_lov msl ON tbm.lov_balance_type = msl.id_lov LEFT JOIN am_msuser amm ON tbm.id_ms_user = amm.id_ms_user LEFT JOIN tr_document_d tdd ON tbm.id_document_d = tdd.id_document_d LEFT JOIN tr_document_h tdh ON tbm.id_document_h = tdh.id_document_h WHERE msl.description = '" + usage + "' AND amm.full_name = '" + fullName + "' AND tbm.trx_date >= CURRENT_DATE AND tbm.trx_date < CURRENT_DATE + 1 ORDER BY tbm.dtm_crt DESC LIMIT 1;")

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
