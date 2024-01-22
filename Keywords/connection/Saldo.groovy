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

		resultSet = stm.executeQuery("SELECT tenant_name FROM ms_tenant WHERE tenant_code = '" + GlobalVariable.Tenant + "'")

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
	getTrxSaldo(Connection conn, String startDate, String endDate, String refNo, String saldoType, String documentName, String officeName, String vendor, String trxType, String docType) {
		String commandRefNo = '', commandTrxType = '', commandDocName = '', commandOfficeName = '', commandDocType = '', queryCustName = ''

		stm = conn.createStatement()

		if (refNo == '') {
			commandRefNo = '--'
		}
		if (trxType == 'All' || trxType == '') {
			commandTrxType = '--'
		}
		if (documentName == '') {
			commandDocName = '--'
		}
		if (officeName == 'All' || officeName == '') {
			commandOfficeName = '--'
		}
		if (docType == 'All' || docType == '') {
			commandDocType = '--'
		}

		if (saldoType == 'Stamp Duty' || saldoType == 'Stamp Duty Postpaid') {
			queryCustName = 'doch.id_msuser_customer'
		} else {
			queryCustName = 'tbm.id_ms_user'
		}

		resultSet = stm.executeQuery("with bdc AS ( SELECT recap_date, recap_total_balance FROM tr_balance_daily_recap bdc WHERE 1 = 1 AND bdc.id_ms_tenant = (SELECT id_ms_tenant FROM ms_tenant WHERE tenant_code = '" + GlobalVariable.Tenant + "') AND bdc.lov_balance_type = (SELECT id_lov FROM ms_lov WHERE description = '" + saldoType + "' AND lov_group = 'BALANCE_TYPE') AND bdc.recap_date <= ( CAST('" + startDate + "' AS DATE) - INTERVAL '1 DAY' ) ORDER BY bdc.recap_date DESC limit 1 ) select * from ( SELECT tbm.trx_no as trxNo, TO_CHAR( tbm.trx_date, 'YYYY-MM-DD HH24:MI:SS' ) as trxDate, COALESCE(CASE WHEN office_name is null THEN '' ELSE office_name END, '') as officename, ml.description, COALESCE( am.full_name, COALESCE(am.full_name, tbm.usr_crt) ) as customerName, COALESCE(CASE WHEN tbm.ref_no is null then '' ELSE tbm.ref_no END, '') as refNo, COALESCE(mlovDocType.code, '') as documentType, COALESCE(case when docd.id_ms_doc_template is null then docd.document_name else dt.doc_template_name end, '') as documentName, COALESCE(tbm.notes, '') as notes, COALESCE(CAST(tbm.qty * -1 AS VARCHAR), '') FROM tr_balance_mutation tbm join bdc ON 1 = 1 JOIN ms_vendor mv ON mv.id_ms_vendor = tbm.id_ms_vendor left join tr_document_d docd ON docd.id_document_d = tbm.id_document_d LEFT JOIN ms_office mo ON mo.id_ms_office = tbm.id_ms_office LEFT JOIN ms_region mr ON mr.id_ms_region = mo.id_ms_region left join ms_doc_template AS dt ON dt.id_doc_template = docd.id_ms_doc_template LEFT JOIN ms_business_line mbl ON mbl.id_ms_business_line = tbm.id_ms_business_line left join tr_document_h doch ON doch.id_document_h = tbm.id_document_h LEFT JOIN am_msuser am ON am.id_ms_user = " + queryCustName + " left join ms_lov AS mlovDocType ON mlovDocType.id_lov = doch.lov_doc_type join ms_lov balancetype ON balancetype.id_lov = tbm.lov_balance_type JOIN ms_lov ml ON tbm.lov_trx_type = ml.id_lov where DATE(tbm.trx_date) > bdc.recap_date AND tbm.id_ms_tenant = (SELECT id_ms_tenant FROM ms_tenant WHERE tenant_code = '" + GlobalVariable.Tenant + "') AND tbm.lov_balance_type = (SELECT id_lov FROM ms_lov WHERE description = '" + saldoType + "' AND lov_group = 'BALANCE_TYPE') and DATE(tbm.trx_date) >= '" + startDate + "' and DATE(tbm.trx_date) <= '" + endDate + "' AND vendor_name = '" + vendor + "'" + '\n' +
				commandTrxType + " AND ml.description = '" + trxType + "'" + '\n' +
				commandDocType + " AND mlovDocType.description = '" + docType + "'" + '\n' +
				commandRefNo + " AND tbm.ref_No = '" + refNo + "'" + '\n' +
				commandDocName + " AND (dt.doc_template_name = '" + documentName + "' OR docd.document_name = '" + documentName + "')" + '\n' +
				commandOfficeName + " AND office_name = '" + officeName + "'" + '\n' +
				" order by tbm.trx_date, tbm.trx_no) as result LIMIT 1")

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
