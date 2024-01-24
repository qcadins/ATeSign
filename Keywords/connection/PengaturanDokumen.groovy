package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

class PengaturanDokumen {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getDataDocTemplate(Connection conn, String docTempCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT doc_template_code, doc_template_name, doc_template_description, CASE WHEN is_active = '1' THEN 'Active' ELSE 'Inactive' END FROM esign.ms_doc_template where doc_template_code = '" + docTempCode + "'")

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
	dataDocTemplateStoreDB(Connection conn, String docTempCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT mdt.doc_template_code, mdt.doc_template_name, mdt.doc_template_description, (SELECT description FROM ms_lov WHERE id_lov = mdt.lov_payment_sign_type) AS description1, CASE WHEN mdt.is_active = '1' THEN 'Active' ELSE 'Inactive' END, mv.vendor_name, CASE WHEN mdt.is_sequence = '1' THEN 'Iya' ELSE 'Tidak' END FROM esign.ms_doc_template mdt JOIN ms_doc_template_sign_loc mdtsl ON mdt.id_doc_template = mdtsl.id_doc_template JOIN ms_lov ml2 ON ml2.id_lov = mdtsl.lov_signer_type LEFT JOIN ms_vendor mv ON mv.id_ms_vendor = mdt.id_ms_vendor WHERE mdt.doc_template_code = '" + docTempCode + "' GROUP BY mdt.doc_template_code, mdt.doc_template_name, mdt.doc_template_description, description1, mdt.is_active, mv.vendor_name, mdt.is_sequence")

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
	getLovTipePembayaran(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select DISTINCT description from ms_lov ml JOIN ms_paymentsigntypeoftenant pt ON ml.id_lov = pt.lov_payment_sign_type JOIN ms_tenant mt ON mt.id_ms_tenant = pt.id_ms_tenant where lov_group = 'PAYMENT_SIGN_TYPE' AND mt.tenant_code = '" + GlobalVariable.Tenant + "' order by description asc")

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
	getTotalDokumenTemplatKode(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(*) from ms_doc_template where is_active = '1' and id_ms_tenant = 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getDDLVendor(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT vendor_name FROM ms_vendor mv JOIN ms_vendoroftenant mvt ON mv.id_ms_vendor = mvt.id_ms_vendor JOIN ms_tenant mt ON mt.id_ms_tenant = mvt.id_ms_tenant join ms_lov ml on ml.id_lov = mv.lov_vendor_type where mv.is_active = '1' and ml.description = 'PSRE' and tenant_code = '" + GlobalVariable.Tenant + "'  ORDER BY mv.vendor_name ASC")
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
	getDefaultVendor(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT vendor_name FROM ms_vendoroftenant mvt JOIN ms_vendor mv on mvt.id_ms_vendor = mv.id_ms_vendor JOIN ms_tenant mt ON mt.id_ms_tenant = mvt.id_ms_tenant WHERE mt.tenant_code = '" + GlobalVariable.Tenant + "' AND is_operating = '1' AND mv.is_active = '1' AND default_vendor = '1'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getUrutanSigning(Connection conn, String docTempCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT STRING_AGG(subquery.description, ';' ORDER BY subquery.seq_no) AS concatenated_descriptions FROM (SELECT DISTINCT ml.description, mdtsl.seq_no FROM ms_doc_template mdt JOIN ms_doc_template_sign_loc mdtsl ON mdt.id_doc_template = mdtsl.id_doc_template JOIN ms_lov ml ON ml.id_lov = mdtsl.lov_signer_type WHERE mdt.doc_template_code = '" + docTempCode + "') AS subquery;")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getDocumentTemplateAPI(Connection conn, String documentTemplateCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mdtsl.sign_location, msl.code, msl1.code, mdtsl.sign_page, mdtsl.transform, mdtsl.tekenaj_sign_location, mdtsl.vida_sign_location, mdtsl.privy_sign_location, mdtsl.seq_no from ms_doc_template mdt left join ms_doc_template_sign_loc mdtsl on mdt.id_doc_template = mdtsl.id_doc_template left join ms_lov msl on mdtsl.lov_signer_type = msl.id_lov left join ms_lov msl1 on mdtsl.lov_sign_type = msl1.id_lov where mdt.doc_template_code = '" + documentTemplateCode + "'")

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
	getDocumentTemplateAPI2(Connection conn, String documentTemplateCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mdt.doc_template_code, mdt.doc_template_name, mdt.doc_template_description, mdt.is_active, msl.code, mdt.is_sequence, msv.vendor_code  from ms_doc_template mdt left join ms_lov msl on mdt.lov_payment_sign_type = msl.id_lov left join ms_vendor msv on mdt.id_ms_vendor = msv.id_ms_vendor where mdt.doc_template_code = '" + documentTemplateCode + "'")
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
