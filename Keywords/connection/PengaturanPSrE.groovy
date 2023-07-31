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

		resultSet = stm.executeQuery("SELECT count(*) FROM ms_vendor WHERE is_active = '1' AND is_Operating IS NOT NULL")

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
}