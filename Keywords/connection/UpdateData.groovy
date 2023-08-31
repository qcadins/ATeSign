package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class UpdateData {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	updateVendorOTP(Connection conn, String vendor, String number) {

		Statement stm = conn.createStatement()

		int updateCount = stm.executeUpdate("UPDATE ms_vendor SET must_user_vendor_otp = '" + number + "' WHERE vendor_code = '" + vendor + "';")
	}

	@Keyword
	updateTenantPassReq(Connection conn, String tenant, String number) {

		Statement stm = conn.createStatement()

		int updateCount = stm.executeUpdate("UPDATE ms_tenant SET need_password_for_signing = '" + number + "' WHERE tenant_code = '" + tenant + "';")
	}

	@Keyword
	updateTenantOTPReq(Connection conn, String tenant, String number) {

		Statement stm = conn.createStatement()

		int updateCount = stm.executeUpdate("UPDATE ms_tenant SET need_otp_for_signing = '" + number + "' WHERE tenant_code = '" + tenant + "';")
	}
}
