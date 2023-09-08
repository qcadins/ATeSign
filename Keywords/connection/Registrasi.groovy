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

	@Keyword
	getGenInvLink(Connection conn, String tenant, String phone, String idno, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tril.usr_crt, tril.gender, tril.kelurahan, tril.kecamatan, tril.kota, tril.zip_code, tril.date_of_birth, tril.place_of_birth, tril.provinsi, tril.email, tril.id_no, tril.phone, tril.address, tril.full_name, mst.tenant_code from tr_invitation_link as tril join ms_tenant as mst on tril.id_ms_tenant = mst.id_ms_tenant where tril.is_active = '1' and mst.tenant_code = '" + tenant + "' and tril.phone = '" + phone + "' and tril.id_no = '" + idno + "' and tril.email = '" + email + "'")

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
	buatUndanganStoreDB(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT full_name, place_of_birth, To_char(date_of_birth, 'MM/dd/yyyy'), gender, email, provinsi, kota, kecamatan, kelurahan, zip_code FROM am_user_personal_data aupd JOIN am_msuser amu ON aupd.id_ms_user = amu.id_ms_user WHERE EMAIL = '" +  email  + "'")

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
	getBuatUndanganDataPerusahaanStoreDB(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select region, office, business_line, task_no from tr_invitation_link where receiver_detail = '" +  email  + "'")

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
	getRegisterPrivyStoreDB(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT request_status, is_external FROM tr_balance_mutation tbm JOIN tr_job_check_register_status tjc ON tbm.id_balance_mutation = tjc.id_balance_mutation WHERE tjc.usr_crt = '" + email + "'")

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
