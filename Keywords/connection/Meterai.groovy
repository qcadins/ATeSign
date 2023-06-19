package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class Meterai {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getTotalMeterai(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT COUNT(*) FROM tr_stamp_duty tsd JOIN ms_tenant mt ON mt.id_ms_tenant = tsd.id_ms_tenant where tsd.dtm_crt >= date_trunc('month', now()) and tsd.dtm_crt <= now() AND tenant_code = '"+ GlobalVariable.Tenant +"'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getStampdutyData(Connection conn, String stampdutyno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.notes, tbm.ref_no, to_char(tbm.trx_date, 'dd-Mon-yyyy'), tsd.stamp_duty_fee, mo.office_name, mr.region_name, mbl.business_line_name, ml.description from tr_balance_mutation tbm join tr_document_d tdd on tdd.id_document_d = tbm.id_document_d join tr_document_h tdh on tdh.id_document_h = tdd.id_document_h join tr_stamp_duty tsd on tsd.id_stamp_duty = tbm.id_stamp_duty join ms_business_line mbl on mbl.id_ms_business_line = tdh.id_ms_business_line join ms_office mo on mo.id_ms_office = tdh.id_ms_office join ms_region mr on mr.id_ms_region = mo.id_ms_region join ms_lov ml on ml.id_lov = tsd.lov_stamp_duty_status where tbm.notes = '" + stampdutyno + "'")

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
	getStampdutyTrxData(Connection conn, String stampdutyno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.trx_no, tbm.ref_no, CASE WHEN tdd.id_ms_doc_template IS NULL THEN tdd.document_name ELSE mdt.doc_template_name END, amu.full_name, ml.description, to_char(tbm.trx_date, 'dd-Mon-yyyy HH24:MI') from tr_balance_mutation tbm join tr_document_d tdd on tdd.id_document_d = tbm.id_document_d left join am_msuser amu on amu.id_ms_user = tbm.id_ms_user join ms_lov ml on ml.id_lov = tbm.lov_trx_type join tr_stamp_duty tsd on tsd.id_stamp_duty = tbm.id_stamp_duty left join ms_doc_template mdt on mdt.id_doc_template = tdd.id_ms_doc_template where tbm.notes = '" + stampdutyno + "'")

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
