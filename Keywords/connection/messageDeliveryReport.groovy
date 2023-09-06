package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class messageDeliveryReport {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getTotalMessageDeliveryReport(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT COUNT(*) FROM tr_message_delivery_report mdr JOIN ms_vendor mv ON (mdr.id_ms_vendor = mv.id_ms_vendor) JOIN ms_lov ml ON (mdr.lov_message_media = ml.id_lov) JOIN ms_tenant mt ON (mdr.id_ms_tenant = mt.id_ms_tenant) WHERE mt.tenant_code = '"+tenantCode+"' and mdr.report_time >= date_trunc('MONTH', now()) and mdr.report_time <= now()")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getDDLVendor(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msv.vendor_name FROM ms_vendoroftenant mvot JOIN ms_vendor msv on mvot.id_ms_vendor = msv.id_ms_vendor JOIN ms_lov ms_vendor ON msv.lov_vendor_type = ms_vendor.id_lov JOIN ms_tenant mt on mt.id_ms_tenant = mvot.id_ms_tenant where ms_vendor.description = 'Message Gateway' AND mt.tenant_code = '"+tenantCode+"'")
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
	getDDLMessageMedia(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msv.description from ms_lov msv WHERE msv.lov_group = 'MESSAGE_MEDIA'")
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
	getDDLMessageMediawa(Connection conn) {
		stm = conn.createStatement()
		String aa = '--'
		resultSet = stm.executeQuery(
				"select msv.description from ms_lov msv " +
				aa + "WHERE msv.lov_group = 'MESSAGE_MEDIA'"
				)

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
