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

		resultSet = stm.executeQuery("select tbm.trx_no, tbm.ref_no, CASE WHEN tdd.id_ms_doc_template IS NULL THEN tdd.document_name ELSE mdt.doc_template_name END, case when amu.full_name is null then amu2nd.full_name else amu.full_name end, ml.description, to_char(tbm.trx_date, 'dd-Mon-yyyy HH24:MI') from tr_balance_mutation tbm join tr_document_d tdd on tdd.id_document_d = tbm.id_document_d left join tr_document_h tdh on tbm.id_document_h = tdh.id_document_h left join am_msuser amu on amu.id_ms_user = tbm.id_ms_user join ms_lov ml on ml.id_lov = tbm.lov_trx_type join tr_stamp_duty tsd on tsd.id_stamp_duty = tbm.id_stamp_duty left join ms_doc_template mdt on mdt.id_doc_template = tdd.id_ms_doc_template join am_msuser amu2nd on tdh.id_msuser_customer = amu2nd.id_ms_user where tbm.notes = '" + stampdutyno + "'")
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
	getMeterai(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tsd.stamp_duty_no, to_char(tddstamp.stamping_date,'dd-Mon-yyyy') ,tsd.stamp_duty_fee, mso.office_name, msr.region_name,  mbl.business_line_name from tr_document_d_stampduty tddstamp join tr_document_d tdd on tddstamp.id_document_d = tdd.id_document_d join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h left join tr_stamp_duty tsd on tddstamp.id_stamp_duty = tsd.id_stamp_duty left join tr_balance_mutation tbm on tsd.id_stamp_duty = tbm.id_stamp_duty left join ms_business_line mbl on tdh.id_ms_business_line = mbl.id_ms_business_line left join ms_office mso on mso.id_ms_office = tdh.id_ms_office left join ms_region msr on mso.id_ms_region = msr.id_ms_region where tdh.ref_number = '" + refNumber + "' order by tddstamp.dtm_crt desc")
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
	getTotalMateraiAndTotalStamping(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select SUM(total_materai), SUM(total_stamping) from tr_document_d tdd JOIN tr_document_h tdh ON tdh.id_document_h = tdd.id_document_h where tdh.ref_number = '" + value + "' ")
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
	getProsesMaterai(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select proses_materai from tr_document_h where ref_number = '" + value + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getInputMeterai(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number, msl.description, mbl.business_line_name , msr.region_name, mso.office_name, to_char(tddstamp.stamping_date,'dd-Mon-yyyy'), to_char(tsd.dtm_crt, 'dd-Mon-yyyy'), tsd.stamp_duty_no from tr_document_d_stampduty tddstamp join tr_document_d tdd on tddstamp.id_document_d = tdd.id_document_d join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h left join tr_stamp_duty tsd on tddstamp.id_stamp_duty = tsd.id_stamp_duty left join tr_balance_mutation tbm on tsd.id_stamp_duty = tbm.id_stamp_duty left join ms_business_line mbl on tdh.id_ms_business_line = mbl.id_ms_business_line left join ms_office mso on mso.id_ms_office = tdh.id_ms_office left join ms_region msr on mso.id_ms_region = msr.id_ms_region left join ms_lov msl on tsd.lov_stamp_duty_status = msl.id_lov where tdh.ref_number = '" + refNumber + "' order by tbm.id_balance_mutation asc")
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
	getValueMeterai(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tsd.stamp_duty_no, tdh.ref_number, to_char(tddstamp.stamping_date,'dd-Mon-yyyy') ,tsd.stamp_duty_fee, mso.office_name, msr.region_name,  mbl.business_line_name, msl.code from tr_document_d_stampduty tddstamp join tr_document_d tdd on tddstamp.id_document_d = tdd.id_document_d join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h left join tr_stamp_duty tsd on tddstamp.id_stamp_duty = tsd.id_stamp_duty left join tr_balance_mutation tbm on tsd.id_stamp_duty = tbm.id_stamp_duty left join ms_business_line mbl on tdh.id_ms_business_line = mbl.id_ms_business_line left join ms_office mso on mso.id_ms_office = tdh.id_ms_office join ms_lov msl on msl.id_lov = tsd.lov_stamp_duty_status left join ms_region msr on mso.id_ms_region = msr.id_ms_region where tdh.ref_number = '" + refNumber + "' order by tbm.id_balance_mutation asc ")
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
	getValueDetailMeterai(Connection conn, String stampdutyno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.trx_no, tbm.ref_no, CASE WHEN tdd.id_ms_doc_template IS NULL THEN tdd.document_name ELSE mdt.doc_template_name END, CASE WHEN amu.full_name != '' OR amu.full_name != null then amu.full_name ELSE 'SYSTEM' END, ml.description, to_char(tbm.trx_date, 'dd-Mon-yyyy HH24:MI') from tr_balance_mutation tbm join tr_document_d tdd on tdd.id_document_d = tbm.id_document_d join ms_lov ml on ml.id_lov = tbm.lov_trx_type join tr_stamp_duty tsd on tsd.id_stamp_duty = tbm.id_stamp_duty left join ms_doc_template mdt on mdt.id_doc_template = tdd.id_ms_doc_template join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h left join am_msuser amu on amu.id_ms_user = tdh.id_msuser_customer where tbm.notes = '" + stampdutyno + "'")

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

		resultSet = stm.executeQuery("select error_message from tr_document_h_stampduty_error tdhse left join tr_document_h tdh on tdhse.id_document_h = tdh.id_document_h where tdh.ref_number = '"+refNumber+"' order by tdhse.dtm_crt desc limit 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getAutomaticStamp(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select automatic_stamping_after_sign from tr_document_h where ref_number = '"+refNumber+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
}
