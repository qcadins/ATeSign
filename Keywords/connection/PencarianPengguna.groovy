package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword

class PencarianPengguna {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	//Perubahan V.46 Buat Query Baru
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
	//Perubahan V.46 Tambahkan String User untuk ngegate data NIK dan No Telepon
	getPencarianPengguna(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT amu.full_name, email, TO_CHAR(aupd.date_of_birth, 'DD-Mon-YYYY'), case when mvru.is_active = '1' then 'Active' when mvru.is_registered = '1' then 'Not Active' else 'Not Registered' end, CASE WHEN LENGTH(mvru.vendor_user_autosign_key) > 0 THEN 'Active - ' || msv.vendor_name ELSE 'Inactive - ' || msv.vendor_name END AS vendor_status,  case when mvru.cert_expired_date IS NULL then 'Not Active' when mvru.cert_expired_date <= CURRENT_DATE THEN 'Expired' else 'Active' END FROM ms_vendor_registered_user mvru JOIN am_msuser amu ON amu.id_ms_user = mvru.id_ms_user JOIN am_user_personal_data aupd ON aupd.id_ms_user = amu.id_ms_user LEFT JOIN ms_vendor msv ON mvru.id_ms_vendor = msv.id_ms_vendor WHERE signer_registered_email = '" + email +  "' OR mvru.hashed_signer_registered_phone = encode(sha256('" + email +  "'), 'hex') OR amu.hashed_id_no = encode(sha256('" + email + "'), 'hex')  order by mvru.dtm_crt asc")
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

	@Keyword
	getListPencarianPenggunaPelanggan(Connection conn, String User) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msv.vendor_name, msvr.signer_registered_email, amm.full_name, case when msvr.is_active = '1' then 'Active' when msvr.is_registered = '1' then 'Not Active' else 'Not Registered' end, case when msvr.activated_date IS NULL then '-' else TO_CHAR(msvr.activated_date, 'DD-Mon-YYYY') end,  case when msvr.cert_expired_date IS NULL then 'Not Active' when msvr.cert_expired_date <= CURRENT_DATE THEN 'Expired' else 'Active' END from ms_vendor_registered_user msvr left join am_msuser amm on msvr.id_ms_user = amm.id_ms_user left join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor where msvr.signer_registered_email = '" + User + "' or msvr.hashed_signer_registered_phone = encode(sha256('" + User + "'), 'hex') or amm.hashed_id_no = encode(sha256('" + User + "'), 'hex') order by msvr.dtm_crt asc")

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
	getListPencarianPenggunaKaryawan(Connection conn, String User) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msv.vendor_name, msvr.signer_registered_email, amm.full_name, case when msvr.is_active = '1' then 'Active' when msvr.is_registered = '1' then 'Not Active' else 'Not Registered' end, case when msvr.activated_date IS NULL then '-' else TO_CHAR(msvr.activated_date, 'DD-Mon-YYYY') end,  case when msvr.cert_expired_date IS NULL then 'Not Active' when msvr.cert_expired_date <= CURRENT_DATE THEN 'Expired' else 'Active' END from ms_vendor_registered_user msvr left join am_msuser amm on msvr.id_ms_user = amm.id_ms_user left join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor where msvr.signer_registered_email = '" + User + "' or msvr.hashed_signer_registered_phone = encode(sha256('" + User + "'), 'hex') or amm.hashed_id_no = encode(sha256('" + User + "'), 'hex') order by msvr.dtm_crt asc")

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
