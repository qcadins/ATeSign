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

		resultSet = stm.executeQuery("select ms.description as signertype, au.full_name as name, au.login_id as login , CASE WHEN au.is_active = '1' THEN 'Sudah Aktivasi' END as aktivasi, CASE WHEN tdds.sign_date is not null THEN 'Signed' ELSE msl.description END as status, CASE WHEN tdds.sign_date IS null THEN '-' else to_char(tdds.sign_date, 'DD-Mon-YYYY HH24:MI') END sign_date from tr_document_d_sign tdds left join tr_document_d as tdd on tdd.id_document_d = tdds.id_document_d left join tr_document_h as tdh on tdh.id_document_h = tdd.id_document_h join am_msuser as au on au.id_ms_user = tdds.id_ms_user join ms_lov ms on tdds.lov_signer_type = ms.id_lov join ms_lov msl on tdd.lov_sign_status = msl.id_lov WHERE tdh.ref_number = '"+ refNo +"' group by ms.description, au.full_name, au.login_id, au.is_active, tdds.sign_date, msl.description")
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
	getEmailSigneronRefNumber(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT STRING_AGG(distinct au.login_id, ';') AS login FROM tr_document_h AS tdh JOIN tr_document_d as tdd on tdh.id_document_h = tdd.id_document_h JOIN tr_document_d_sign AS tdds ON tdd.id_document_d = tdds.id_document_d JOIN am_msuser AS au ON au.id_ms_user = tdds.id_ms_user WHERE tdh.ref_number = '"+refNumber+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getDocumentStatus(Connection conn, String refnumber){
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from tr_document_d tdd join ms_lov msl on tdd.lov_sign_status = msl.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '"+refnumber+"'")
		metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getInputDocumentMonitoring(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mso.office_name, msr.region_name from tr_document_h tdh left join ms_office mso on tdh.id_ms_office = mso.id_ms_office left join ms_region msr on mso.id_ms_region = msr.id_ms_region join tr_document_d tdd on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '"+refNumber+"'")
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
	getuserCustomerondocument(Connection conn, String refnumber){
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.full_name from tr_document_h tdh join tr_document_d tdd on tdh.id_document_h = tdd.id_document_h join am_msuser amm on tdh.id_msuser_customer = amm.id_ms_user where tdh.ref_number = '"+refnumber+"'")
		metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getDocumentMonitoring(Connection conn, String refNumber, String fullname) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number, msl_doctype.description, case when tdd.id_ms_doc_template is null then case when tdd.document_name is null then '' else tdd.document_name end else mdt.doc_template_name end, case when amm.full_name is null then '' else amm.full_name end, TO_CHAR(tdh.dtm_crt, 'DD-Mon-YYYY HH24:MI'), case when tdd.completed_date is null then '-' else TO_CHAR(tdd.completed_date, 'DD-Mon-YYYY HH24:MI') end ,mso.office_name, msr.region_name, msl_signstatus.description from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_lov msl_doctype on tdh.lov_doc_type = msl_doctype.id_lov left join ms_doc_template mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join am_msuser amm on tdh.id_msuser_customer = amm.id_ms_user join ms_lov msl_signstatus on tdd.lov_sign_status = msl_signstatus.id_lov join ms_office mso on tdh.id_ms_office = mso.id_ms_office join ms_region msr on mso.id_ms_region = msr.id_ms_region where tdh.ref_number = '"+refNumber+"' and amm.full_name = '"+fullname+"' order by tdd.document_name desc, mdt.doc_template_name desc")
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
	getDocumentType(Connection conn, String refnumber){
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from tr_document_h tdh join ms_lov msl on tdh.lov_doc_type = msl.id_lov where tdh.ref_number = '"+refnumber+"'")
		metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getTotalStampingandTotalMaterai(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdd.total_stamping, tdd.total_materai from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '"+refNumber+"'")
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
	getDocumentMonitoringBasedOnEmbed(Connection conn, String refNumber, String fullname) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number, msl_doctype.description, case when tdd.id_ms_doc_template is null then case when tdd.document_name is null then '' else tdd.document_name end else mdt.doc_template_name end, case when amm.full_name is null then '' else amm.full_name end,TO_CHAR(tdh.dtm_crt, 'DD-Mon-YYYY HH24:MI'), case when tdd.completed_date is null then '-' else TO_CHAR(tdd.completed_date, 'DD-Mon-YYYY HH24:MI') end,msl_signstatus.description, mso.office_name, msr.region_name, case when tdd.total_stamping != tdd.total_materai then 'Not Started' else 'Success' end from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_lov msl_doctype on tdh.lov_doc_type = msl_doctype.id_lov left join ms_doc_template mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join am_msuser amm on tdh.id_msuser_customer = amm.id_ms_user join ms_lov msl_signstatus on tdd.lov_sign_status = msl_signstatus.id_lov join ms_office mso on tdh.id_ms_office = mso.id_ms_office join ms_region msr on mso.id_ms_region = msr.id_ms_region where tdh.ref_number = '"+refNumber+"' and amm.full_name = '"+fullname+"' order by tdd.document_name desc, mdt.doc_template_name desc")
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
