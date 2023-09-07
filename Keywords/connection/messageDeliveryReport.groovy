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
	HashMap<String, String> commandSql
	
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
	getFilterMessageDeliveryReport(Connection conn, String tenantCode, HashMap<String, String> value) {
		stm = conn.createStatement()

		for (String key : value.keySet()) {
			String values = value.get(key)
			if (values == '') {
				commandSql.put(key,'--')
			}
		}
		if (commandSql.get("Report Time Start").length() <= 0 || commandSql.get("Report Time End").length() <= 0) {
		commandSql.put('defaultSetting', '--')
		}
		
		resultSet = stm.executeQuery("SELECT mv.vendor_name,mdr.report_time,mdr.recipient_detail,mdr.trx_no,ml.description,CASE WHEN mdr.delivery_status = '0' THEN 'Not Started' WHEN mdr.delivery_status = '1' THEN 'Waiting' WHEN mdr.delivery_status = '2' THEN 'Failed' WHEN mdr.delivery_status = '3' THEN 'Delivered' WHEN mdr.delivery_status = '4' THEN 'Read' ELSE '' END FROM tr_message_delivery_report mdr JOIN ms_vendor mv ON (mdr.id_ms_vendor = mv.id_ms_vendor) JOIN ms_lov ml ON (mdr.lov_message_media = ml.id_lov) JOIN ms_tenant mt ON (mdr.id_ms_tenant = mt.id_ms_tenant) WHERE mt.tenant_code = '"+tenantCode+"'" + 
    commandSql.get("defaultSetting") + "and mdr.report_time >= date_trunc('MONTH', now()) and mdr.report_time <= now()" +
     commandSql.get("Vendor") + "and mv.vendor_name = '"+value.get("Vendor")+"'" + 
    commandSql.get("Message Media") + "and ml.description = '"+value.get("Message Media")+"'" +
   commandSql.get("Report Time Start") + "and mdr.report_time >= '"+value.get("Report Time Start")+"' and mdr.report_time <= '"+value.get("Report Time End")+"'" +
    commandSql.get("Status Delivery") + "and mdr.delivery_status = '"+value.get("Status Delivery")+"'" + 
    commandSql.get("Recipient") + "and recipient_detail = '"+value.get("Recipient")+"'" + 
   " ORDER BY mdr.report_time DESC")

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
