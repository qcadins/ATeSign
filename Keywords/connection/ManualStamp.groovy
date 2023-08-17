package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class ManualStamp {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getDocType(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select description from ms_lov where lov_group = 'DOC_TYPE'")
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
	getDocTypePeruri(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select doc_name from ms_peruri_doc_type where is_active = '1'")
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
	getManualStamp(Connection conn, String refNumber, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_Number, tdd.document_name, TO_CHAR(tdd.request_date, 'yyyy-mm-dd'), msl.description, case when mpdt.doc_name != '' or mpdt.doc_name != null then mpdt.doc_name else '' end, tdh.total_document, tdh.is_manual_upload, tdd.total_stamping, tdd.total_materai from tr_document_h tdh left join tr_document_d tdd on tdd.id_Document_h = tdh.id_document_h left join ms_lov msl on tdh.lov_doc_type = msl.id_lov left join ms_peruri_doc_type mpdt on tdd.id_peruri_doc_type = mpdt.id_peruri_doc_type left join ms_vendor msv on tdd.id_ms_vendor = msv.id_ms_vendor left join ms_tenant mst on tdh.id_ms_tenant = mst.id_ms_tenant where tdh.ref_number = '"+refNumber+"' and mst.tenant_code = '"+tenantCode+"'")
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
	getInputeMeteraiMonitoring(Connection conn, String refNumber, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number,case when mpdt.doc_name != '' or mpdt.doc_name != null then mpdt.doc_name else '' end, msl1.description,CASE WHEN mdt.doc_template_description IS NULL THEN 'MANUAL' ELSE mdt.doc_template_description END,TO_CHAR(tdd.request_date, 'yyyy-mm-dd'), case when tdd.completed_date != null then TO_CHAR(tdd.completed_date, 'yyyy-mm-dd') else '' END, CASE WHEN tdh.proses_materai = 0 THEN 'Not Started' WHEN tdh.proses_materai IN (1, 51, 321, 521) THEN 'Failed' WHEN tdh.proses_materai IN (2, 52, 322, 522, 5, 55, 325, 525) THEN 'In Progress' WHEN tdh.proses_materai IN (3, 53, 323, 523) THEN 'Success' END  ,case when tsd.stamp_duty_no is not null then tsd.stamp_duty_no else '' end,mso.office_name , case when tdh.is_postpaid_stampduty = '1' then 'Pemungut' else 'Non Pemungut' end from tr_document_h tdh left join tr_document_d tdd on tdd.id_Document_h = tdh.id_document_h left join ms_lov msl on tdd.lov_payment_sign_type = msl.id_lov left join ms_lov msl1 on tdh.lov_doc_type = msl1.id_lov left join ms_peruri_doc_type mpdt on tdd.id_peruri_doc_type = mpdt.id_peruri_doc_type left join ms_doc_template mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join ms_vendor msv on tdd.id_ms_vendor = msv.id_ms_vendor left join ms_tenant mst on tdh.id_ms_tenant = mst.id_ms_tenant left join ms_office mso on tdh.id_ms_office = mso.id_ms_office left join tr_document_d_stampduty tddstamp on tdd.id_document_d = tddstamp.id_document_d LEFT join tr_stamp_duty tsd on tddstamp.id_stamp_duty = tsd.id_stamp_duty where tdh.ref_number = '"+refNumber+"' and mst.tenant_code = '"+tenantCode+"'")
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
