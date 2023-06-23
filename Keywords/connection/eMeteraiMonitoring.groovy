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
	geteMeteraiMonitoring(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number, TO_CHAR(tdh.dtm_crt, 'DD-Mon-YYYY'), mso.office_name, tdd.document_name, mpdt.doc_name, msl_doctype.description, tdd.document_nominal, tdh.proses_materai from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h left join ms_doc_template mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join am_msuser amm on tdh.id_msuser_customer = amm.id_ms_user join ms_lov msl_signstatus on tdd.lov_sign_status = msl_signstatus.id_lov left join ms_office mso on tdh.id_ms_office = mso.id_ms_office left join ms_region msr on mso.id_ms_region = msr.id_ms_region left join ms_peruri_doc_type mpdt on tdd.id_peruri_doc_type = mpdt.id_peruri_doc_type join ms_lov msl_doctype on tdh.lov_doc_type = msl_doctype.id_lov where tdh.ref_number = '" + refNumber + "' ")
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
