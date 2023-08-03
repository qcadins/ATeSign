package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class ManualSign {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getVendorofTenant(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msv.vendor_name from ms_vendoroftenant mvot left join ms_tenant mst on mvot.id_ms_tenant = mst.id_ms_tenant left join ms_vendor msv on mvot.id_ms_vendor = msv.id_ms_vendor where mst.tenant_code = '"+ tenantCode +"' and msv.is_operating = '1' and mvot.default_vendor > '0' order by mvot.id_ms_vendoroftenant asc")
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
	getPaymentTypeBasedOnTenantAndVendor(Connection conn, String tenantCode, String vendorName) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from ms_paymentsigntypeoftenant mpstot left join ms_tenant mst on mpstot.id_ms_tenant = mst.id_ms_tenant left join ms_vendor msv on mpstot.id_ms_vendor = msv.id_ms_vendor left join ms_lov msl on mpstot.lov_payment_sign_type = msl.id_lov where mst.tenant_code = '"+ tenantCode +"' and msv.vendor_name = '"+ vendorName +"'")
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
	getVerificationSigner(Connection conn, String emailSigner){
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select full_name from am_msuser where login_id = '"+ emailSigner +"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
	
	@Keyword
	getInformationUser(Connection conn, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select full_name, hashed_phone from am_msuser where login_id = '"+ emailSigner +"'")
		
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
