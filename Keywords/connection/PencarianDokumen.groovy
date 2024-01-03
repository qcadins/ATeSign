package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword

public class PencarianDokumen {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getSignerInfo(Connection conn, String refnum, String psre) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select lov.description, amm.full_name, amm.login_id, CASE WHEN mvru.is_registered = '1' AND mvru.is_active = '1' THEN 'Sudah Aktivasi' WHEN mvru.is_registered = '0' AND mvru.is_active = '0' THEN 'Belum Registrasi' ELSE 'Belum Aktivasi' END, CASE WHEN tdds.sign_date IS NOT null THEN 'Signed' ELSE 'Need Sign' END, CASE WHEN TO_char(tdds.sign_date, 'DD-Mon-YYYY HH24:MI') IS NOT null THEN TO_char(tdds.sign_date, 'DD-Mon-YYYY HH24:MI') ELSE '-' END from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d join am_msuser amm on tdds.id_ms_user = amm.id_ms_user join ms_lov lov on tdds.lov_signer_type = lov.id_lov join ms_vendor_registered_user mvru ON mvru.id_ms_user = amm.id_ms_user JOIN tr_document_signing_request tdsr ON tdsr.id_document_h = tdh.id_document_h JOIN ms_vendor mv ON mv.id_ms_vendor = mvru.id_ms_vendor where tdh.ref_number = '" +  refnum  + "' and mv.vendor_code = '" +  psre  + "'")
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
	getTotalPencarianDokumen(Connection conn, String loginid, String tenant) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT count(*) FROM tr_document_d dd JOIN LATERAL( select dds.* from tr_document_d_sign dds JOIN am_msuser amu ON amu.id_ms_user = dds.id_ms_user where dds.id_document_d = dd.id_document_d and login_id = '" +  loginid  + "' limit 1) dds on true JOIN tr_document_h dh ON dh.id_document_h = dd.id_document_h AND dh.is_active = '1' JOIN ms_tenant mt ON dh.id_ms_tenant = mt.id_ms_tenant LEFT JOIN am_msuser usercust ON dh.id_msuser_customer = usercust.id_ms_user LEFT JOIN ms_doc_template dt ON dd.id_ms_doc_template = dt.id_doc_template JOIN ms_lov lovDocType ON dh.lov_doc_type = lovDocType.id_lov JOIN ms_lov lovSignStat ON dd.lov_sign_status = lovSignStat.id_lov JOIN ms_office office ON office.id_ms_office = dh.id_ms_office LEFT JOIN ms_region region ON region.id_ms_region = office.id_ms_region WHERE 1 = 1 and mt.tenant_code = '" +  tenant  + "' and dd.request_date >= date_trunc('month', now()) and dd.request_date <= now()")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}
	
}
