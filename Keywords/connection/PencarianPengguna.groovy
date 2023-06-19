package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class PencarianPengguna {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getDataPencarianPengguna(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT full_name, place_of_birth, date_of_birth, email, provinsi, kota, kecamatan, kelurahan, zip_code FROM am_user_personal_data aupd JOIN am_msuser amu ON aupd.id_ms_user = amu.id_ms_user WHERE EMAIL = '" +  email  + "'")

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
	getPencarianPengguna(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT amu.full_name, email, aupd.date_of_birth, CASE WHEN LENGTH(mvru.vendor_user_autosign_key) > 0 THEN 'Active' ELSE 'Inactive' END , CASE WHEN mvru.is_active = '1' THEN 'Active' WHEN mvru.is_active = '0' THEN 'Inactive' ELSE mvru.is_active END FROM ms_vendor_registered_user mvru JOIN am_msuser amu ON amu.id_ms_user = mvru.id_ms_user JOIN am_user_personal_data aupd ON aupd.id_ms_user = amu.id_ms_user WHERE signer_registered_email = '" +  email  + "'")

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
