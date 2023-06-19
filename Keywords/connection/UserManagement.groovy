package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class UserManagement {
	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getTotalUserManagement(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(amm.login_id) from am_memberofrole amor join am_msrole amr on amor.id_ms_role = amr.id_ms_role join ms_useroftenant muot on amor.id_ms_user = muot.id_ms_user join am_msuser amm on muot.id_ms_user = amm.id_ms_user where muot.id_ms_tenant = '1' and amr.id_ms_tenant = '1' and amr.is_usermanagement = '1' and amr.is_active = '1' and amr.is_deleted = '0'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getUserManagement(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.full_name, amm.login_id, amr.role_code, case when amr.is_active = '1' then 'Aktif' else 'Tidak Aktif' end from am_memberofrole amor join am_msrole amr on amor.id_ms_role = amr.id_ms_role join ms_useroftenant muot on amor.id_ms_user = muot.id_ms_user join am_msuser amm on muot.id_ms_user = amm.id_ms_user where muot.id_ms_tenant = '1' and amr.id_ms_tenant = '1' and amr.is_usermanagement = '1' and amr.is_active = '1' and amr.is_deleted = '0' and amm.login_id = '"+email+"'")
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
	getUserManagementonEdit(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.full_name, amm.login_id, to_char(amor.dtm_upd,'dd-Mon-yyyy') ,amr.role_name, mso.office_name, case when amr.is_active = '1' then 'on' else 'off' end  from am_memberofrole amor join am_msrole amr on amor.id_ms_role = amr.id_ms_role join ms_useroftenant muot on amor.id_ms_user = muot.id_ms_user join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_office mso on amm.id_ms_office = mso.id_ms_office where muot.id_ms_tenant = '1' and amr.id_ms_tenant = '1' and amr.is_usermanagement = '1' and amr.is_active = '1' and amr.is_deleted = '0' and amm.login_id = '"+email+"'")
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
