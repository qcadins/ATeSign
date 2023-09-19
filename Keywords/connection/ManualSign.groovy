package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class ManualSign {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getVendorofTenant(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msv.vendor_name, mvot.id_ms_vendoroftenant from ms_vendoroftenant mvot left join ms_tenant mst on mvot.id_ms_tenant = mst.id_ms_tenant left join ms_vendor msv on mvot.id_ms_vendor = msv.id_ms_vendor where mst.tenant_code = '"+tenantCode+"' and msv.is_active = '1' and mvot.default_vendor > '0' order by mvot.default_vendor asc")		
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
	getPaymentTypeBasedOnTenantAndVendor(Connection conn, String tenantCode, String vendorName) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from ms_paymentsigntypeoftenant mpstot left join ms_tenant mst on mpstot.id_ms_tenant = mst.id_ms_tenant left join ms_vendor msv on mpstot.id_ms_vendor = msv.id_ms_vendor left join ms_lov msl on mpstot.lov_payment_sign_type = msl.id_lov where mst.tenant_code = '"+ tenantCode +"' and msv.vendor_name = '"+ vendorName +"'")
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
	getVerificationSigner(Connection conn, String value){
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select full_name from am_msuser where login_id = '"+value+"' or hashed_phone = '"+value+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getInformationUser(Connection conn, String value, String vendorName) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.full_name, amm.hashed_phone, amm.login_id from am_msuser amm left join ms_vendor_registered_user msvr on msvr.id_ms_user = amm.id_ms_user left join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor where (amm.login_id = '"+value+"' or amm.hashed_phone = '"+value+"') and msv.vendor_name = '"+vendorName+"'")
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
	getManualSign(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msv.vendor_name, tdh.ref_Number, tdd.document_name, TO_CHAR(tdd.request_date, 'yyyy-mm-dd'), msl.description ,tdh.automatic_stamping_after_sign, case when mpdt.doc_name != '' or mpdt.doc_name != null then mpdt.doc_name else '' end, tdh.total_document, tdh.is_manual_upload, CASE WHEN tdd.is_sequence != '0' or tdd.is_sequence != null THEN 'Ya' else 'Tidak' end from tr_document_h tdh left join tr_document_d tdd on tdd.id_Document_h = tdh.id_document_h left join ms_lov msl on tdd.lov_payment_sign_type = msl.id_lov left join ms_peruri_doc_type mpdt on tdd.id_peruri_doc_type = mpdt.id_peruri_doc_type left join ms_vendor msv on tdd.id_ms_vendor = msv.id_ms_vendor where tdh.ref_number = '"+refNumber+"'")

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
