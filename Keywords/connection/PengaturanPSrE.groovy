package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class PengaturanPSrE {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getDataVendor(Connection conn, String vendorCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT vendor_code, vendor_name, CASE WHEN mv.is_active = '1' THEN 'Aktif' WHEN mv.is_active = '0' THEN 'Tidak Aktif' END, CASE WHEN mv.is_operating = '1' THEN 'Aktif' WHEN mv.is_operating = '0' THEN 'Tidak Aktif' END, ml.description FROM ms_vendor mv JOIN ms_lov ml ON ml.id_lov = mv.lov_vendor_sign_payment_type WHERE vendor_code = '" +  vendorCode  + "'")

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
	getTotalPSrE(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT Count(*) FROM ms_vendor mv JOIN ms_lov ml ON mv.lov_vendor_type = ml.id_lov WHERE is_Operating IS NOT NULL AND ml.id_lov = 48")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getDDLVendorPaymentType(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("Select description From ms_lov WHERE lov_group = 'VENDOR_SIGN_PAYMENT_TYPE' AND is_active = '1'")

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
	getPsrePriority(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT VENDOR_NAME FROM ms_vendoroftenant mvt JOIN ms_tenant mt ON mvt.id_ms_tenant = mt.id_ms_tenant JOIN ms_vendor mv ON mvt.id_ms_vendor = mv.id_ms_vendor WHERE mt.tenant_code = '"+ GlobalVariable.Tenant +"' AND is_operating = '1' AND mv.is_active = '1' ORDER BY mvt.default_vendor ASC")

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
