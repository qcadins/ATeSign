package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class Registrasi {
	
	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	settingEmailServiceTenant(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("UPDATE ms_tenant SET email_service = "+ value +" WHERE tenant_code = '"+ GlobalVariable.Tenant +"'")
	}
	
}
