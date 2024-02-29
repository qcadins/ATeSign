package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword

class EditSignerData {

	String data, helperQuery
	int columnCount = 0, i = 0, countLengthforSHA256 = 64
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []
	String valueWhere

	@Keyword
	countEditSignerDataBasedOnEmail(Connection conn, String email, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(amm.login_id) from ms_useroftenant muot join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_user_personal_data aupd on amm.id_ms_user = aupd.id_ms_user where (amm.login_id = '" + email + "' OR msvr.signer_registered_email = '" + email + "') and mst.tenant_code = '" + tenantCode + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getBeforeEditDataEditSignerData(Connection conn, String value, String tenantCode, String vendorName) {
		if (value.length() == countLengthforSHA256) {
			valueWhere = "amm.hashed_id_no = '" + value + "'"
		} else {
			valueWhere = "amm.login_id = '" + value + "'"
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.full_name, amm.login_id, amm.hashed_phone, amm.hashed_id_no, aupd.date_of_birth from ms_useroftenant muot join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_user_personal_data aupd on amm.id_ms_user = aupd.id_ms_user where " + valueWhere + " and msv.is_active = '1' and msv.is_operating = '1' and mst.tenant_code = '" + tenantCode + "' and msv.vendor_name = '" + vendorName + "'")
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
	getEditSignerData(Connection conn, String value, String tenantCode) {
		if (value.length() == countLengthforSHA256) {
			valueWhere = "amm.hashed_id_no = '" + value + "'"
		} else {
			valueWhere = "amm.login_id = '" + value + "'"
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msv.vendor_name, amm.login_id, amm.full_name, case when msvr.is_active = '1' then 'Sudah Aktivasi' else 'Belum Aktivasi' end from ms_useroftenant muot join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_user_personal_data aupd on amm.id_ms_user = aupd.id_ms_user where " + valueWhere + " and msv.is_active = '1' and msv.is_operating = '1' and mst.tenant_code = '" + tenantCode + "'")
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
	getEditDataAndEditActivationEditSignerData(Connection conn, String value, String tenantCode) {
		if (value.length() == countLengthforSHA256) {
			valueWhere = "amm.hashed_id_no = '" + value + "'"
		} else {
			valueWhere = "amm.login_id = '" + value + "'"
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.login_id, msv.vendor_name from ms_useroftenant muot join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_user_personal_data aupd on amm.id_ms_user = aupd.id_ms_user where " + valueWhere + " and msv.is_active = '1' and msv.is_operating = '1' and mst.tenant_code = '" + tenantCode + "'")
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
	getStatusActivationEditSignerData(Connection conn, String value, String tenantCode, String vendorName) {
		if (value.length() == countLengthforSHA256) {
			valueWhere = "amm.hashed_id_no = '" + value + "'"
		} else {
			valueWhere = "amm.login_id = '" + value + "'"
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select case when msvr.is_active = '1' then 'Sudah Aktivasi' else 'Belum Aktivasi' end from ms_useroftenant muot join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_user_personal_data aupd on amm.id_ms_user = aupd.id_ms_user where " + valueWhere + " and msv.is_active = '1' and msv.is_operating = '1' and mst.tenant_code = '" + tenantCode + "' and msv.vendor_name = '" + vendorName + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getGetListDataPenggunaAPI(Connection conn, String value, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.login_id, amm.full_name, CASE WHEN msvr.is_registered = '0' THEN 'belum registrasi' ELSE CASE WHEN msvr.is_active = '0' THEN 'belum aktivasi' ELSE 'sudah aktivasi' END END, msv.vendor_code, msvr.is_registered, msvr.is_active, msv.vendor_name from am_msuser amm  left join ms_useroftenant muot on amm.id_ms_user = muot.id_ms_user left join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user left join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor left join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant where (amm.login_id = '" + value + "' OR amm.hashed_phone = encode(sha256('" + value + "'), 'hex') OR amm.hashed_id_no = encode(sha256('" + value + "'), 'hex')) AND mst.tenant_code = '" + tenantCode + "'")
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
	getUpdateDataSignerAPI(Connection conn, String value, String tenantCode, String vendorCode) {
		if (vendorCode != '') {
			helperQuery =  "and msv.vendor_code = '" + vendorCode + "'"
		} else {
			helperQuery = ''
		}
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.full_name, amm.login_id, amm.hashed_phone, amm.hashed_id_no, aupd.date_of_birth from ms_useroftenant muot join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_user_personal_data aupd on amm.id_ms_user = aupd.id_ms_user where (amm.login_id = '" + value + "' OR amm.hashed_phone = encode(sha256('" + value + "'), 'hex') OR amm.hashed_id_no = encode(sha256('" + value + "'), 'hex')) and msv.is_active = '1' and msv.is_operating = '1' and mst.tenant_code = '" + tenantCode + "'" + helperQuery + "")
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
	getStatusActivationAPI(Connection conn, String value, String tenantCode, String vendorCode) {
		stm = conn.createStatement()

		helperQuery = ''
		if (tenantCode != '') {
			resultSet = stm.executeQuery("select count(*) from ms_tenant where tenant_code = '" + tenantCode + "'")
			metadata = resultSet.metaData

			columnCount = metadata.getColumnCount()

			while (resultSet.next()) {
				data = resultSet.getObject(1)
			}

			if (Integer.parseInt(data) > 0) {
				helperQuery = "and mst.tenant_code = '" + tenantCode + "'"
			}
		}

		resultSet = stm.executeQuery("select msvr.is_active from ms_useroftenant muot join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user left join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor left join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_user_personal_data aupd on amm.id_ms_user = aupd.id_ms_user where (amm.login_id = '" + value + "' OR amm.hashed_phone = encode(sha256('" + value + "'), 'hex') OR amm.hashed_id_no = encode(sha256('" + value + "'), 'hex')) and msv.is_active = '1' and msv.is_operating = '1' " + helperQuery + " and msv.vendor_code = '" + vendorCode + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
}
