package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class eMeteraiMonitoring {
	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getTotaleMeteraiMonitoring(Connection conn, String tenantCode) {
		stm = conn.createStatement()
		resultSet = stm.executeQuery("select count(*) from tr_document_d dd join tr_document_h dh on dd.id_document_h = dh.id_document_h join ms_lov ml on dh.lov_doc_type = ml.id_lov join ms_tenant mt on mt.id_ms_tenant = dh.id_ms_tenant join ms_office mo on mo.id_ms_office = dh.id_ms_office join ms_peruri_doc_type pdt on dd.id_peruri_doc_type = pdt.id_peruri_doc_type left join lateral ( Select bm.notes, st.stamp_duty_no, bm.trx_date from tr_balance_mutation bm join tr_stamp_duty st on st.id_stamp_duty = bm.id_stamp_duty join ms_lov lovBalType on lovBalType.id_lov = bm.lov_balance_type where lovBalType.code IN ('SDT', 'SDT_POSTPAID') and bm.id_document_d = dd.id_document_d ) bm ON TRUE where mt.tenant_code = '" + tenantCode + "' and dh.is_manual_upload = '1' and dd.request_date >= date_trunc('month', now()) and dd.request_date <= now()")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
	@Keyword
	geteMeteraiMonitoring(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tdh.ref_number, TO_CHAR(tdd.request_date, 'DD-Mon-YYYY'), mso.office_name, tdd.document_name,mpdt.doc_name, msl_doctype.description, CASE WHEN tdd.document_nominal IS NOT NULL THEN tdd.document_nominal::text ELSE '' END, CASE WHEN mdt.doc_template_description IS NULL THEN 'MANUAL' ELSE mdt.doc_template_description END, CASE WHEN tdh.proses_materai = 0 THEN 'Not Started' WHEN tdh.proses_materai IN (1, 51, 321, 521) THEN 'Failed' WHEN tdh.proses_materai IN (2, 52, 322, 522, 5, 55, 325, 525) THEN 'In Progress' WHEN tdh.proses_materai IN (3, 53, 323, 523) THEN 'Success' END  , case when tsd.stamp_duty_no is not null then tsd.stamp_duty_no else '' end, CONCAT(tdd.total_stamping, '/',tdd.total_materai), case when tdh.is_postpaid_stampduty = '1' then 'Pemungut' else 'Non Pemungut' end FROM tr_document_d tdd JOIN tr_document_h tdh ON tdd.id_document_h = tdh.id_document_h LEFT JOIN ms_doc_template mdt ON tdd.id_ms_doc_template = mdt.id_doc_template LEFT JOIN am_msuser amm ON tdh.id_msuser_customer = amm.id_ms_user JOIN ms_lov msl_signstatus ON tdd.lov_sign_status = msl_signstatus.id_lov LEFT JOIN ms_office mso ON tdh.id_ms_office = mso.id_ms_office LEFT JOIN ms_region msr ON mso.id_ms_region = msr.id_ms_region LEFT JOIN ms_peruri_doc_type mpdt ON tdd.id_peruri_doc_type = mpdt.id_peruri_doc_type JOIN ms_lov msl_doctype ON tdh.lov_doc_type = msl_doctype.id_lov LEFT JOIN tr_document_d_stampduty tddstamp on tdd.id_document_d = tddstamp.id_document_d LEFT join tr_stamp_duty tsd on tddstamp.id_stamp_duty = tsd.id_stamp_duty WHERE tdh.ref_number = '" + refNumber + "' ORDER BY tdd.request_date DESC")

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
	getErrorMessage(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select error_message from tr_document_h_stampduty_error tdhse join tr_document_h tdh on tdhse.id_document_h = tdh.id_document_h where tdh.ref_number = '" + refNumber + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getProseseMeteraiMonitoring(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT CASE WHEN tdh.proses_materai = '0' THEN 'Not Started' WHEN tdh.proses_materai = '521' or tdh.proses_materai = '51' THEN 'Failed' WHEN tdh.proses_materai = '523' or tdh.proses_materai = '53' THEN 'Success' ELSE 'In Progress' END, tsd.stamp_duty_no, CONCAT(tdd.total_stamping, '/',tdd.total_materai) FROM tr_document_d tdd JOIN tr_document_h tdh ON tdd.id_document_h = tdh.id_document_h LEFT JOIN ms_doc_template mdt ON tdd.id_ms_doc_template = mdt.id_doc_template LEFT JOIN am_msuser amm ON tdh.id_msuser_customer = amm.id_ms_user JOIN ms_lov msl_signstatus ON tdd.lov_sign_status = msl_signstatus.id_lov LEFT JOIN ms_office mso ON tdh.id_ms_office = mso.id_ms_office LEFT JOIN ms_region msr ON mso.id_ms_region = msr.id_ms_region LEFT JOIN ms_peruri_doc_type mpdt ON tdd.id_peruri_doc_type = mpdt.id_peruri_doc_type JOIN ms_lov msl_doctype ON tdh.lov_doc_type = msl_doctype.id_lov LEFT JOIN tr_document_d_stampduty tddstamp on tdd.id_document_d = tddstamp.id_document_d LEFT join tr_stamp_duty tsd on tddstamp.id_stamp_duty = tsd.id_stamp_duty WHERE tdh.ref_number = '" + refNumber + "' ")
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
