package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword

class APIOnly {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []
	HashMap<String, String> commandSql = [:]

	@Keyword
	getAddBalanceType(Connection conn, String code) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select usr_crt, lov_group, code, description, is_active, is_deleted from ms_lov where code = '"+code+"'")
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
	getSubDistrict(Connection conn, String subDistrictName, String districtId) {
		stm = conn.createStatement()

		if (districtId == '') {
			listdata
		} else {
			resultSet = stm.executeQuery("SELECT subdistrict_name AS subDistrictName, CAST(id_mssubdistrict AS VARCHAR) AS idMsSubDistrict FROM am_mssubdistrict WHERE subdistrict_name LIKE '%" + subDistrictName + "%' AND id_msdistrict = '" + districtId + "'");

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

	@Keyword
	getDistrict(Connection conn, String districtName, String provinceId) {
		stm = conn.createStatement()

		if (provinceId == '') {
			listdata
		} else {
			resultSet = stm.executeQuery("SELECT district_name AS subDistrictName, CAST(id_msdistrict AS VARCHAR) AS idMsSubDistrict FROM am_msdistrict WHERE district_name LIKE '%" + districtName + "%' AND id_msprovince = '" + provinceId + "'");

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

	@Keyword
	getProvince(Connection conn, String provinceName) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT province_name AS subDistrictName, CAST(id_msprovince AS VARCHAR) AS idMsSubDistrict FROM am_msprovince WHERE province_name LIKE '%" + provinceName + "%'");

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
	getOtpCode(Connection conn, String user) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.otp_code from ms_vendor_registered_user msvr left join am_msuser amm on amm.id_ms_user = msvr.id_ms_user left join ms_vendor msv on msv.id_ms_vendor = msvr.id_ms_vendor where (msvr.signer_registered_email = '" + user + "' OR msvr.hashed_signer_registered_phone = encode(sha256('" + user + "'), 'hex') OR hashed_id_no = encode(sha256('" + user + "'), 'hex'))")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getResetCode(Connection conn, String user) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.reset_code from ms_vendor_registered_user msvr left join am_msuser amm on amm.id_ms_user = msvr.id_ms_user left join ms_vendor msv on msv.id_ms_vendor = msvr.id_ms_vendor where (msvr.signer_registered_email = '" + user + "' OR msvr.hashed_signer_registered_phone = encode(sha256('" + user + "'), 'hex') OR hashed_id_no = encode(sha256('" + user + "'), 'hex'))")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
}
