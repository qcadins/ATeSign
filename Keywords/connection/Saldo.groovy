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
	getDDLTipeSaldoActive(Connection conn, String tenant, String vendor) {
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

		resultSet = stm.executeQuery("select count(*) from tr_balance_mutation tbm join ms_tenant mst on tbm.id_ms_tenant = mst.id_ms_tenant join ms_vendor msv on tbm.id_ms_vendor = msv.id_ms_vendor join ms_lov msl on tbm.lov_balance_type = msl.id_lov where tbm.trx_date >= date_trunc('month', now()) and tbm.trx_date <= now() and mst.tenant_code = '" + tenantCode + "' and msv.vendor_code = '" + vendorCode + "' and msl.description = '" + balanceType + "' ")
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

	@Keyword
	getTenantName(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tenant_name FROM ms_tenant WHERE tenant_code = '"+ GlobalVariable.Tenant +"'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getDDLTipeSaldo(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT description FROM ms_lov WHERE lov_group = 'BALANCE_TYPE' AND is_active = '1'")
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
	getDDLTipeTrx(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT description FROM ms_lov WHERE lov_group = 'TRX_TYPE' AND is_active = '1'")
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
	getDDLTipeDokumen(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT description FROM ms_lov WHERE lov_group = 'DOC_TYPE' AND is_active = '1'")
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
	getTrxSaldo(Connection conn, String startDate, String endDate, String refNo, String saldoType, String documentName, String officeName) {
		String commandRefNo = '', commandTrxType = '', commandDocName = '', commandOfficeName = ''

		stm = conn.createStatement()

		if (refNo == '') {
			commandRefNo = '--'
		}
		if (saldoType == '') {
			commandTrxType = '--'
		}
		if (documentName == '') {
			commandDocName = '--'
		}

		if (officeName == 'All' || officeName == '') {
			commandOfficeName = '--'
		}

		resultSet = stm.executeQuery("SELECT COALESCE(tbm.trx_no,''), COALESCE(TO_CHAR(trx_date, 'yyyy-MM-dd HH24:MI:SS'),''),COALESCE(mo.office_name ,''), COALESCE(ml.description,''), COALESCE(full_name,''), COALESCE(ref_no,''), COALESCE(ml2.code,''), COALESCE(case when mdt.doc_template_name != null or mdt.doc_template_name != '' then mdt.doc_template_name else tdd.document_name end,''), COALESCE(notes,''), COALESCE(qty * -1,0) FROM tr_balance_mutation tbm LEFT JOIN ms_lov ml ON tbm.lov_trx_type = ml.id_lov LEFT JOIN tr_document_h tdh ON tbm.id_document_h = tdh.id_document_h LEFT JOIN tr_document_d tdd ON tbm.id_document_d = tdd.id_document_d LEFT JOIN am_msuser amu ON tbm.id_ms_user = amu.id_ms_user LEFT JOIN( SELECT id_lov, code FROM ms_lov) ml2 ON tdh.lov_doc_type = ml2.id_lov LEFT JOIN ms_doc_template mdt ON tdd.id_ms_doc_template = mdt.id_doc_template LEFT JOIN ms_office mo ON mo.id_ms_office = tbm.id_ms_office WHERE (TO_CHAR(trx_date, 'yyyy-MM-dd') BETWEEN '" + startDate + "' AND '" + endDate + "')" + '\n' +
				commandTrxType + " AND ml.description ILIKE '%" + saldoType + "%'" + '\n' +
				commandRefNo + " AND ref_no = '" + refNo + "'" + '\n' +
				commandDocName + " AND (tdd.document_name = '" + documentName + "' OR mdt.doc_template_name = '" + documentName + "'" + '\n' +
				commandOfficeName + " AND mo.office_name = '" + officeName + "'" + '\n' +
				" LIMIT 1")

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
