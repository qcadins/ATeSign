package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class Stamping {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getMeterai(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tsd.stamp_duty_no, to_char(tddstamp.stamping_date,'dd-Mon-yyyy') ,tsd.stamp_duty_fee, mso.office_name, msr.region_name,  mbl.business_line_name from tr_document_d_stampduty tddstamp join tr_document_d tdd on tddstamp.id_document_d = tdd.id_document_d join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h left join tr_stamp_duty tsd on tddstamp.id_stamp_duty = tsd.id_stamp_duty left join tr_balance_mutation tbm on tsd.id_stamp_duty = tbm.id_stamp_duty left join ms_business_line mbl on tdh.id_ms_business_line = mbl.id_ms_business_line left join ms_office mso on mso.id_ms_office = tdh.id_ms_office left join ms_region msr on mso.id_ms_region = msr.id_ms_region where tdh.ref_number = '"+refNumber+"' order by tddstamp.dtm_crt desc")
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
	getTotalMaterai(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select SUM(total_materai) from tr_document_d tdd JOIN tr_document_h tdh ON tdh.id_document_h = tdd.id_document_h where tdh.ref_number = '"+ value +"'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
	
	@Keyword
	getProsesMaterai(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select proses_materai from tr_document_h where ref_number = '"+value+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}
	
}
