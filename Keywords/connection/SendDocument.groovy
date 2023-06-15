package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class SendDocument {
	
	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []
	
	@Keyword
	settingEmailServiceVendorRegisteredUser(Connection conn, String value, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("UPDATE ms_vendor_registered_user SET email_service = "+ value +" WHERE signer_registered_email = '"+ email +"'")
	}
}
