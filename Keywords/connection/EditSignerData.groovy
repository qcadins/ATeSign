package connection

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class EditSignerData {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []
	String emailWhere

	@Keyword
	countEditSignerDataBasedOnEmail(Connection conn, String email, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(amm.login_id) from ms_useroftenant muot join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_user_personal_data aupd on amm.id_ms_user = aupd.id_ms_user where amm.login_id = '"+email+"' and msv.is_active = '1' and msv.is_operating = '1' and mst.tenant_code = '"+tenantCode+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getBeforeEditDataEditSignerData(Connection conn, String email, String tenantCode, String vendorName) {
		if (email.length() == 64) {
			emailWhere = "amm.hashed_id_no = '" + email + "'"
		} else {
			emailWhere = "amm.login_id = '" + email + "'"
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.full_name, amm.login_id, amm.hashed_phone, amm.hashed_id_no, aupd.date_of_birth from ms_useroftenant muot join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_user_personal_data aupd on amm.id_ms_user = aupd.id_ms_user where "+emailWhere+" and msv.is_active = '1' and msv.is_operating = '1' and mst.tenant_code = '"+tenantCode+"' and msv.vendor_name = '"+vendorName+"'")
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
	getEditSignerData(Connection conn, String email, String tenantCode) {
		if (email.length() == 64) {
			emailWhere = "amm.hashed_id_no = '" + email + "'"
		} else {
			emailWhere = "amm.login_id = '" + email + "'"
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msv.vendor_name, amm.login_id, amm.full_name, case when msvr.is_active = '1' then 'Sudah Aktivasi' else 'Belum Aktivasi' end from ms_useroftenant muot join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_user_personal_data aupd on amm.id_ms_user = aupd.id_ms_user where "+emailWhere+" and msv.is_active = '1' and msv.is_operating = '1' and mst.tenant_code = '"+tenantCode+"'")
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
	getEditDataAndEditActivationEditSignerData(Connection conn, String email, String tenantCode) {
		if (email.length() == 64) {
			emailWhere = "amm.hashed_id_no = '" + email + "'"
		} else {
			emailWhere = "amm.login_id = '" + email + "'"
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.login_id, msv.vendor_name from ms_useroftenant muot join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_user_personal_data aupd on amm.id_ms_user = aupd.id_ms_user where "+emailWhere+" and msv.is_active = '1' and msv.is_operating = '1' and mst.tenant_code = '"+tenantCode+"'")
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
	getStatusActivationEditSignerData(Connection conn, String email, String tenantCode, String vendorName) {
		if (email.length() == 64) {
			emailWhere = "amm.hashed_id_no = '" + email + "'"
		} else {
			emailWhere = "amm.login_id = '" + email + "'"
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select case when msvr.is_active = '1' then 'Sudah Aktivasi' else 'Belum Aktivasi' end from ms_useroftenant muot join am_msuser amm on muot.id_ms_user = amm.id_ms_user join ms_vendor_registered_user msvr on amm.id_ms_user = msvr.id_ms_user join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_user_personal_data aupd on amm.id_ms_user = aupd.id_ms_user where "+emailWhere+" and msv.is_active = '1' and msv.is_operating = '1' and mst.tenant_code = '"+tenantCode+"' and msv.vendor_name = '"+vendorName+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
}
