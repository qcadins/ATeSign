package connection

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import java.sql.Connection
import java.sql.Statement

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.openqa.selenium.support.ui.Select
import groovy.sql.Sql as Sql

public class ForgotPassword {

	String data
	int columnCount
	Statement stm
	ResultSet resultSet
	ResultSetMetaData metadata

	@Keyword
	getResetCode(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT reset_code FROM am_msuser WHERE login_id = '" + email + "'")

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

	@Keyword
	getOTPActiveDuration(Connection conn, String email) {

		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT mt.otp_active_duration FROM am_msuser amu LEFT JOIN ms_useroftenant mot ON mot.id_ms_user = amu.id_ms_user LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mot.id_ms_tenant LEFT JOIN tr_document_h tdh ON tdh.id_ms_tenant = mt.id_ms_tenant WHERE amu.login_id = '" + email + "' ORDER BY id_document_h DESC LIMIT 1")

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		if (data != null) {
			Integer.parseInt(data)
		} else {
			data = 0
		}
	}

	@Keyword
	getResetNum(Connection conn, String email) {
		int data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT reset_code_request_num FROM am_msuser where login_id = '" +  email  + "'")

		while (resultSet.next()){

			data = resultSet.getObject(1);
		}

		data
	}

	@Keyword
	getTenantCode(Connection conn, String email) {
		data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT mt.tenant_code FROM am_msuser amu LEFT JOIN ms_useroftenant mot ON mot.id_ms_user = amu.id_ms_user LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mot.id_ms_tenant LEFT JOIN tr_document_h tdh ON tdh.id_ms_tenant = mt.id_ms_tenant WHERE amu.login_id = '" + email + "' ORDER BY id_document_h DESC LIMIT 1")

		while (resultSet.next()){

			data = resultSet.getObject(1);
		}

		data
	}

	@Keyword
	updateResetRequestNum(Connection conn, String email) {

		stm = conn.createStatement()

		int updateCount = stm.executeUpdate("UPDATE am_msuser SET reset_code_request_num = 0 WHERE login_id = '" + email + "';")
	}

	@Keyword
	updateOTPActiveDuration(Connection conn, String tenantcode, int otpduration) {

		stm = conn.createStatement()

		int updateCount = stm.executeUpdate("UPDATE ms_tenant SET otp_active_duration = " + otpduration + " WHERE tenant_code = '" + tenantcode + "';")
	}
}
