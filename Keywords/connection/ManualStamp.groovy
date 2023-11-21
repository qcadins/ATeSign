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

		resultSet = stm.executeQuery("select tdh.ref_number,case when mpdt.doc_name != '' or mpdt.doc_name != null then mpdt.doc_name else '' end, msl1.description,CASE WHEN mdt.doc_template_description IS NULL THEN 'MANUAL' ELSE mdt.doc_template_description END, TO_CHAR(tdd.request_date, 'yyyy-mm-dd'), case when tddstamp.stamping_date is not null or tddstamp.stamping_date != null then TO_CHAR(tddstamp.stamping_date, 'yyyy-mm-dd') else TO_CHAR(tdd.request_date, 'yyyy-mm-dd') END, CASE WHEN tdh.proses_materai = 0 THEN 'Not Started' WHEN tdh.proses_materai IN (1, 51, 61, 321, 521) THEN 'Failed' WHEN tdh.proses_materai IN (2, 52, 62, 322, 522, 5, 55, 65, 325, 525) THEN 'In Progress' WHEN tdh.proses_materai IN (3, 53, 63, 323, 523) THEN 'Success' END  ,case when tsd.stamp_duty_no is not null then tsd.stamp_duty_no else '' end,mso.office_name , case when tdh.is_postpaid_stampduty = '1' then 'Pemungut' else 'Non Pemungut' end  from tr_document_h tdh left join tr_document_d tdd on tdd.id_Document_h = tdh.id_document_h left join ms_lov msl on tdd.lov_payment_sign_type = msl.id_lov left join ms_lov msl1 on tdh.lov_doc_type = msl1.id_lov left join ms_peruri_doc_type mpdt on tdd.id_peruri_doc_type = mpdt.id_peruri_doc_type left join ms_doc_template mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join ms_vendor msv on tdd.id_ms_vendor = msv.id_ms_vendor left join ms_tenant mst on tdh.id_ms_tenant = mst.id_ms_tenant left join ms_office mso on tdh.id_ms_office = mso.id_ms_office left join tr_document_d_stampduty tddstamp on tdd.id_document_d = tddstamp.id_document_d LEFT join tr_stamp_duty tsd on tddstamp.id_stamp_duty = tsd.id_stamp_duty where tdh.ref_number = '"+refNumber+"' and mst.tenant_code = '"+tenantCode+"'")

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
	getProsesMeterai(Connection conn, String refNumber, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select CASE WHEN tdh.proses_materai = 0 THEN 'Not Started' WHEN tdh.proses_materai IN (1, 51, 321, 521) THEN 'Failed' WHEN tdh.proses_materai IN (2, 52, 322, 522, 5, 55, 325, 525) THEN 'In Progress' WHEN tdh.proses_materai IN (3, 53, 323, 523) THEN 'Success' END   from tr_document_h tdh left join tr_document_d tdd on tdd.id_Document_h = tdh.id_document_h left join ms_lov msl on tdd.lov_payment_sign_type = msl.id_lov left join ms_lov msl1 on tdh.lov_doc_type = msl1.id_lov left join ms_peruri_doc_type mpdt on tdd.id_peruri_doc_type = mpdt.id_peruri_doc_type left join ms_doc_template mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join ms_vendor msv on tdd.id_ms_vendor = msv.id_ms_vendor left join ms_tenant mst on tdh.id_ms_tenant = mst.id_ms_tenant left join ms_office mso on tdh.id_ms_office = mso.id_ms_office left join tr_document_d_stampduty tddstamp on tdd.id_document_d = tddstamp.id_document_d LEFT join tr_stamp_duty tsd on tddstamp.id_stamp_duty = tsd.id_stamp_duty where tdh.ref_number = '"+refNumber+"' and mst.tenant_code = '"+tenantCode+"' limit 1")
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
	gettrxSaldoForMeterai(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.trx_no, TO_CHAR(tbm.dtm_crt,'YYYY-MM-DD HH24:MI:SS'), ml.description,tbm.usr_crt,case when amm_two.full_name != null or amm_two.full_name != '' then tdh.ref_number||'('||amm_two.full_name||')' else tdh.ref_number end,ml_doc_h.code,case when mdt.doc_template_name != null then mdt.doc_template_name else tdd.document_name end ,tbm.notes, tbm.qty from tr_document_d_stampduty tddstamp join tr_document_d tdd on tddstamp.id_document_d = tdd.id_document_d join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join tr_stamp_duty tsd on tddstamp.id_stamp_duty = tsd.id_stamp_duty left join tr_balance_mutation tbm on tsd.id_stamp_duty = tbm.id_stamp_duty left join ms_lov as ml on tbm.lov_trx_type = ml.id_lov left join am_msuser as amm on tbm.id_ms_user = amm.id_ms_user left join ms_lov as ml_doc_h on tdh.lov_doc_type = ml_doc_h.id_lov join ms_lov msl on tsd.lov_stamp_duty_status = msl.id_lov left join am_msuser as amm_two on tdh.id_msuser_customer = amm_two.id_ms_user where tdh.ref_number = '"+refNumber+"' ORDER BY tbm.dtm_crt asc ")
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
	getIdLovVendorStamping(Connection conn, String vendorStamping) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT id_lov FROM ms_lov WHERE code = '" + vendorStamping + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getLovVendorStamping(Connection conn, String tenantCode) {
		stm = conn.createStatement()
		resultSet = stm.executeQuery("select msl.description from ms_tenant mst left join ms_lov msl on msl.id_lov = mst.lov_vendor_stamping where mst.tenant_code = '"+tenantCode+"'")
		metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		
		if (data == null) {
			data = 'null'
		}
		
		data
	}
}
