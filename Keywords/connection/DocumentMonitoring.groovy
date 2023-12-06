package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class DocumentMonitoring {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getDocumentMonitoringSigner(Connection conn, String refNo) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select ms.description as signertype, au.full_name as name, au.login_id as login , CASE WHEN au.is_active = '1' THEN 'Sudah Aktivasi' END as aktivasi, CASE WHEN tdds.sign_date is not null THEN 'Signed' ELSE msl.description END as status, CASE WHEN tdds.sign_date IS null THEN '-' else to_char(tdds.sign_date, 'DD-Mon-YYYY HH24:MI') END sign_date from tr_document_d_sign tdds left join tr_document_d as tdd on tdd.id_document_d = tdds.id_document_d left join tr_document_h as tdh on tdh.id_document_h = tdd.id_document_h join am_msuser as au on au.id_ms_user = tdds.id_ms_user left join ms_lov ms on tdds.lov_signer_type = ms.id_lov join ms_lov msl on tdd.lov_sign_status = msl.id_lov WHERE tdh.ref_number = '" + refNo + "' group by ms.description, au.full_name, au.login_id, au.is_active, tdds.sign_date, msl.description")
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
	getEmailSigneronRefNumber(Connection conn, String refNumber, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT STRING_AGG(distinct au.login_id, ';') AS login FROM tr_document_h AS tdh JOIN tr_document_d as tdd on tdh.id_document_h = tdd.id_document_h JOIN tr_document_d_sign AS tdds ON tdd.id_document_d = tdds.id_document_d JOIN am_msuser AS au ON au.id_ms_user = tdds.id_ms_user JOIN ms_tenant AS mst on tdh.id_ms_tenant = mst.id_ms_tenant WHERE tdh.ref_number = '" + refNumber + "' AND mst.tenant_code = '"+tenantCode+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getInputDocumentMonitoring(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select case when amm.full_name != '' or amm.full_name != null then amm.full_name else '' end, TO_CHAR(tdd.request_date, 'yyyy-mm-dd'), case when tdd.completed_date is not null then TO_CHAR(tdd.completed_date,'yyyy-mm-dd') else '' end, msl_doctype.description, msl.description, msr.region_name, mso.office_name , CASE WHEN tdh.proses_materai = 0 THEN 'Not Started' WHEN tdh.proses_materai IN (1, 51, 321, 521, 61) THEN 'Failed' WHEN tdh.proses_materai IN (2, 52, 322, 522, 5, 55, 325, 525, 65, 62) THEN 'In Progress' WHEN tdh.proses_materai IN (3, 53, 323, 523, 63) THEN 'Success' END from tr_document_h tdh left join ms_office mso on tdh.id_ms_office = mso.id_ms_office left join ms_region msr on mso.id_ms_region = msr.id_ms_region join tr_document_d tdd on tdd.id_document_h = tdh.id_document_h left join ms_lov msl on tdd.lov_sign_status = msl.id_lov left join am_msuser amm on tdh.id_msuser_customer = amm.id_ms_user left join ms_lov msl_doctype on tdh.lov_doc_type = msl_doctype.id_lov left join tr_document_d_stampduty tddstampduty on tdd.id_document_d = tddstampduty.id_document_d where tdh.ref_number = '"+refNumber+"'  order by tdh.dtm_crt desc limit 1")
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
	getInputDocumentMonitoringMULTIDOC(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select case when amm.full_name != '' or amm.full_name != null then amm.full_name else '' end, tdh.ref_number,  TO_CHAR(tdd.request_date, 'yyyy-mm-dd'), case when tdd.completed_date is not null then TO_CHAR(tdd.completed_date,'yyyy-mm-dd') else '' end, msl_doctype.description, msl.description, msr.region_name, mso.office_name from tr_document_h tdh left join ms_office mso on tdh.id_ms_office = mso.id_ms_office left join ms_region msr on mso.id_ms_region = msr.id_ms_region join tr_document_d tdd on tdd.id_document_h = tdh.id_document_h left join ms_lov msl on tdd.lov_sign_status = msl.id_lov left join am_msuser amm on tdh.id_msuser_customer = amm.id_ms_user left join ms_lov msl_doctype on tdh.lov_doc_type = msl_doctype.id_lov left join tr_document_d_stampduty tddstampduty on tdd.id_document_d = tddstampduty.id_document_d where tdd.document_id = '"+documentId+"'  order by tdh.dtm_crt desc limit 1")
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
	getDocumentMonitoring(Connection conn, String refNumber, String fullname) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number, msl_doctype.description, case when tdd.id_ms_doc_template is null then case when tdd.document_name is null then '' else tdd.document_name end else mdt.doc_template_name end, case when amm.full_name is null then '' else amm.full_name end, TO_CHAR(tdh.dtm_crt, 'DD-Mon-YYYY HH24:MI'), case when tdd.completed_date is null then '-' else TO_CHAR(tdd.completed_date, 'DD-Mon-YYYY HH24:MI') end ,mso.office_name, msr.region_name, msl_signstatus.description from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_lov msl_doctype on tdh.lov_doc_type = msl_doctype.id_lov left join ms_doc_template mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join am_msuser amm on tdh.id_msuser_customer = amm.id_ms_user join ms_lov msl_signstatus on tdd.lov_sign_status = msl_signstatus.id_lov join ms_office mso on tdh.id_ms_office = mso.id_ms_office join ms_region msr on mso.id_ms_region = msr.id_ms_region where tdh.ref_number = '" + refNumber + "' and amm.full_name = '" + fullname + "' order by tdd.document_name desc, mdt.doc_template_name desc")
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
	getTotalStampingandTotalMaterai(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdd.total_stamping, tdd.total_materai from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '" + refNumber + "' ORDER BY tdd.id_document_d asc")
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
	getTotalStampingandTotalMateraiMULTIDOC(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdd.total_stamping, tdd.total_materai from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdd.document_id = '" + documentId + "' ORDER BY tdd.id_document_d asc")
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
	getDocumentMonitoringBasedOnEmbed(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tdh.ref_number, msl_doctype.description, CASE WHEN tdd.id_ms_doc_template IS NULL THEN CASE WHEN tdd.document_name IS NULL THEN '' ELSE tdd.document_name END ELSE mdt.doc_template_name END, CASE WHEN amm.full_name IS NULL THEN '' ELSE amm.full_name END, TO_CHAR(tdd.request_date, 'DD-Mon-YYYY HH24:MI'), CASE WHEN tdd.completed_date IS NULL THEN '-' ELSE TO_CHAR(tdd.completed_date, 'DD-Mon-YYYY HH24:MI') END, msl_signstatus.description, mso.office_name, CASE WHEN msr.region_name != null or msr.region_name IS NOT NULL or msr.region_name != '' THEN msr.region_name else '' end, CASE WHEN tdh.proses_materai = 0 THEN 'Not Started' WHEN tdh.proses_materai IN (1, 51, 321, 521, 61) THEN 'Failed' WHEN tdh.proses_materai IN (2, 52, 322, 522, 5, 55, 325, 525, 65, 62) THEN 'In Progress' WHEN tdh.proses_materai IN (3, 53, 323, 523, 63) THEN 'Success' END FROM tr_document_d tdd JOIN tr_document_h tdh ON tdd.id_document_h = tdh.id_document_h LEFT JOIN ms_lov msl_doctype ON tdh.lov_doc_type = msl_doctype.id_lov LEFT JOIN ms_doc_template mdt ON tdd.id_ms_doc_template = mdt.id_doc_template LEFT JOIN am_msuser amm ON tdh.id_msuser_customer = amm.id_ms_user LEFT JOIN ms_lov msl_signstatus ON tdd.lov_sign_status = msl_signstatus.id_lov LEFT JOIN ms_office mso ON tdh.id_ms_office = mso.id_ms_office LEFT JOIN ms_region msr ON mso.id_ms_region = msr.id_ms_region WHERE tdh.ref_number = '"+refNumber+"' ORDER BY tdd.id_document_d asc")

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
	getManualUpload(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select case when is_manual_upload = '1' then 'Yes' else 'No' end from tr_document_h tdh left join tr_document_d tdd on tdh.id_document_h = tdd.id_document_h where tdh.ref_number = '"+refNumber+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getCancelDocStatus(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT is_active FROM tr_document_h WHERE ref_number = '" + refNumber + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
}
