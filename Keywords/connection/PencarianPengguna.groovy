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

		resultSet = stm.executeQuery("SELECT amu.full_name, email, aupd.date_of_birth, CASE  WHEN LENGTH(mvru.vendor_user_autosign_key) > 0 THEN 'Active - ' || msv.vendor_code ELSE 'Inactive - ' || msv.vendor_name END AS vendor_status, CASE WHEN mvru.is_active = '1' THEN 'Active' WHEN mvru.is_active = '0' THEN CASE WHEN mvru.is_registered = '0' THEN 'Not Registered' ELSE 'Not Active' END END AS user_status FROM ms_vendor_registered_user mvru JOIN am_msuser amu ON amu.id_ms_user = mvru.id_ms_user JOIN am_user_personal_data aupd ON aupd.id_ms_user = amu.id_ms_user LEFT JOIN ms_vendor msv ON mvru.id_ms_vendor = msv.id_ms_vendor WHERE signer_registered_email = '" + email +  "'")

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
	getUserDataAPI(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT ampd.provinsi, ampd.kota, ampd.kecamatan, login_id, ampd.email, CASE WHEN mvru.is_registered = '0' THEN 'Belum Aktivasi - ' || msv.vendor_code ELSE 'Sudah Aktivasi - ' || msv.vendor_code END AS user_status, CASE WHEN LENGTH(mvru.vendor_user_autosign_key) > 0 THEN 'Active - ' || msv.vendor_name ELSE 'Inactive - ' || msv.vendor_name END AS autosign_status, TO_CHAR(mvru.cert_expired_date, 'DD-Mon-YYYY'), ampd.kelurahan, ampd.zip_code, amu.full_name, til.address, ampd.gender, til.phone, ampd.place_of_birth, ampd.date_of_birth, til.id_no, msv.vendor_code FROM am_user_personal_data ampd LEFT JOIN am_msuser amu ON amu.id_ms_user = ampd.id_ms_user LEFT JOIN ms_vendor_registered_user mvru ON mvru.id_ms_user = ampd.id_ms_user LEFT JOIN ms_vendor msv ON msv.id_ms_vendor = mvru.id_ms_vendor LEFT JOIN tr_invitation_link til ON til.receiver_detail = ampd.email WHERE ampd.email = '" + email + "'")

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
