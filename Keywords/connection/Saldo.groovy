package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class Saldo {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getDDLTenant(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT Trim(tenant_name) FROM ms_tenant where is_active = '1'")
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
	getDDLVendor(Connection conn, String tenant) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT vendor_name FROM ms_vendor mv JOIN ms_vendoroftenant mvt ON mv.id_ms_vendor = mvt.id_ms_vendor JOIN ms_tenant mt ON mt.id_ms_tenant = mvt.id_ms_tenant where mv.is_active = '1' and tenant_name = '" +  tenant  + "'")
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
	getDDLTipeSaldo(Connection conn, String tenant, String vendor) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select description from ms_balancevendoroftenant mbv JOIN ms_vendor mv ON mv.id_ms_vendor = mbv.id_ms_vendor JOIN ms_tenant mt ON mt.id_ms_tenant = mbv.id_ms_tenant JOIN ms_lov ml ON ml.id_lov = mbv.lov_balance_type where tenant_name = '" +  tenant  + "' AND vendor_name = '" +  vendor  + "'")
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
	getIsiSaldoStoreDB(Connection conn, String refno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tenant_name, vendor_name, description, qty, ref_no, notes, to_char(trx_date, 'yyyy-mm-dd') FROM tr_balance_mutation tbm JOIN ms_tenant mt ON mt.id_ms_tenant = tbm.id_ms_tenant JOIN ms_vendor mv ON mv.id_ms_vendor = tbm.id_ms_vendor JOIN ms_lov ml ON ml.id_lov = tbm.lov_balance_type WHERE ref_no = '" +  refno  + "' order by tbm.dtm_crt desc limit 1")
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
	getIsiSaldoTrx(Connection conn, String refno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tbm.trx_no, trx_date, description, amu.full_name, ref_no, notes, qty FROM tr_balance_mutation tbm JOIN ms_lov ml ON ml.id_lov = tbm.lov_trx_type JOIN am_msuser amu ON amu.id_ms_user = tbm.id_ms_user WHERE ref_no = '" +  refno  + "' order by tbm.dtm_crt desc limit 1")

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
	getTenantTidakIsiSaldo(Connection conn, String tenantname, String refno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tenant_name FROM tr_balance_mutation tbm JOIN ms_tenant mt ON mt.id_ms_tenant = tbm.id_ms_tenant JOIN ms_vendor mv ON mv.id_ms_vendor = tbm.id_ms_vendor JOIN ms_lov ml ON ml.id_lov = tbm.lov_balance_type WHERE tenant_name != '" +  tenantname  + "' and Ref_no = '" +  refno  + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getTotalTrxBasedOnVendorAndBalanceType(Connection conn, String tenantCode, String vendorCode, String balanceType) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(*) from tr_balance_mutation tbm join ms_tenant mst on tbm.id_ms_tenant = mst.id_ms_tenant join ms_vendor msv on tbm.id_ms_vendor = msv.id_ms_vendor join ms_lov msl on tbm.lov_balance_type = msl.id_lov where tbm.dtm_crt >= date_trunc('month', now()) and tbm.dtm_crt <= now() and mst.tenant_code = '" + tenantCode + "' and msv.vendor_code = '" + vendorCode + "' and msl.description = '" + balanceType + "' ")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getAllBalanceType(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select description from ms_lov where lov_group = 'BALANCE_TYPE' and is_active = '1'")
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
