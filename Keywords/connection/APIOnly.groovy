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

		resultSet = stm.executeQuery("select usr_crt, lov_group, code, description, is_active, is_deleted from ms_lov where code = '" + code + "'")
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
			resultSet = stm.executeQuery("SELECT subdistrict_name AS subDistrictName, CAST(id_mssubdistrict AS VARCHAR) AS idMsSubDistrict FROM am_mssubdistrict WHERE subdistrict_name LIKE '%" + subDistrictName + "%' AND id_msdistrict = '" + districtId + "'")

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
			resultSet = stm.executeQuery("SELECT district_name AS subDistrictName, CAST(id_msdistrict AS VARCHAR) AS idMsSubDistrict FROM am_msdistrict WHERE district_name LIKE '%" + districtName + "%' AND id_msprovince = '" + provinceId + "'")

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

		resultSet = stm.executeQuery("SELECT province_name AS subDistrictName, CAST(id_msprovince AS VARCHAR) AS idMsSubDistrict FROM am_msprovince WHERE province_name LIKE '%" + provinceName + "%'")

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
	getCheckDocumentSendStatus(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(tdd.*) from tr_document_d tdd left join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '" + refNumber + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getRefNumberAndSortDescending(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number, tdd.request_date from tr_document_h tdh join tr_document_d tdd on tdh.id_document_h = tdd.id_document_h where tdd.document_id = '" + documentId + "'")

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
	listAvailableOptionSendingPointGetAvailSendingPointInv(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT distinct balType.code as balanceTypeCode FROM ms_balancevendoroftenant bvot JOIN ms_vendor mv ON bvot.id_ms_vendor = mv.id_ms_vendor JOIN ms_tenant mt ON bvot.id_Ms_tenant = mt.id_Ms_tenant JOIN ms_lov balType ON bvot.lov_balance_type = balType.id_lov WHERE mt.tenant_code = '" + tenantCode + "' AND mv.vendor_Code = 'ESG' AND balType.is_Active = '1' AND mv.is_Active = '1' AND mt.is_Active = '1' AND (bvot.is_Hidden IS NULL OR bvot.is_Hidden = '0')")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)

				if (data == 'SMS' || data == 'WA') {
					listdata.add(data)
				}
			}
		}
		listdata
	}
	
	@Keyword
	getIdMsUser(Connection conn, String NIK) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select id_ms_user from am_msuser where hashed_id_no = encode(sha256('" + NIK + "'), 'hex')")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
}
