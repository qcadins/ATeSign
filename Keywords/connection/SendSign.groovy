package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class SendSign {

	String data
	int columnCount, i, countLengthforSHA256 = 64, updateVariable
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []
	String emailWhere, selectData

	@Keyword
	settingEmailServiceVendorRegisteredUser(Connection conn, String value, String idNo) {
		stm = conn.createStatement()

		updateVariable = stm.executeUpdate("UPDATE ms_vendor_registered_user SET email_service = '"+value+"' WHERE EXISTS (SELECT * FROM AM_MSUSER AMM WHERE amm.hashed_id_no = '"+idNo+"')")
	}

	@Keyword
	getSigningStatusProcess(Connection conn, String documentid, String emailsigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdsp.request_status, CASE WHEN tdds.sign_date is not null THEN 'Sudah TTD' end , tdd.total_signed from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d join am_msuser amm on tdds.id_ms_user = amm.id_ms_user join tr_document_signing_request tdsp on amm.id_ms_user = tdsp.id_ms_user where tdd.document_id = '" + documentid + "' and amm.login_id = '" + emailsigner + "' ORDER BY tdsp.dtm_crt desc limit 1 ")

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
	getDataSendtoSign(Connection conn, String documentid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number, msl.description, TO_char(tdd.request_date, 'DD-Mon-YYYY HH24:MI') from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_lov msl on tdh.lov_doc_type = msl.id_lov where tdd.document_id = '" + documentid + "'")
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
	getEmailLogin(Connection conn, String documentid) {
		stm = conn.createStatement()
		resultSet = stm.executeQuery("SELECT STRING_AGG(login_id, ';' ORDER BY id_document_d_sign) AS aa FROM (SELECT DISTINCT tdds.id_ms_user,au.login_id, FIRST_VALUE(tdds.id_document_d_sign) OVER (PARTITION BY tdds.id_ms_user ORDER BY tdds.id_document_d_sign) AS id_document_d_sign FROM tr_document_h AS tdh JOIN tr_document_d AS tdd ON tdh.id_document_h = tdd.id_document_h JOIN tr_document_d_sign AS tdds ON tdd.id_document_d = tdds.id_document_d JOIN am_msuser AS au ON au.id_ms_user = tdds.id_ms_user WHERE tdd.document_id = '"+ documentid +"') AS alls;")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getProsesTtdProgress(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT COUNT(tdsr.id_ms_user) FROM tr_document_d tdd JOIN tr_document_signing_request tdsr ON tdd.id_document_d = tdsr.id_document_d WHERE tdd.document_id = '" + documentId + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getTotalSignerTtd(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT COUNT (DISTINCT amm.login_id) FROM tr_document_d_sign tdds LEFT JOIN tr_document_d tdd on tdd.id_document_d = tdds.id_document_d LEFT JOIN tr_Document_h tdh on tdd.id_document_h = tdh.id_document_h LEFT JOIN am_msuser amm on tdds.id_ms_user = amm.id_ms_user WHERE tdd.document_id = '"+documentId+"'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getTotalSigned(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select total_signed from tr_document_d where document_id = '" + documentId + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getPaymentType(Connection conn, String refnumber){
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from tr_document_d tdd join ms_lov msl on tdd.lov_payment_sign_type = msl.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '" + refnumber + "'")
		metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getSaldoUsedBasedonPaymentType(Connection conn, String refnumber, String emailSigner){
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select CASE WHEN msl_tdd.description = 'Per Document' then '1' WHEN msl_tdd.description = 'Per Sign' then count(tdds.sign_location) end from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d left join ms_lov msl on tdds.lov_signer_type = msl.id_lov join ms_lov msl_tdd on tdd.lov_payment_sign_type = msl_tdd.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdh.ref_number = '" + refnumber + "' and amm.login_id = '" + emailSigner + "' GROUP BY msl.description, msl_tdd.description")
		metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getTotalSignedUsingRefNumber(Connection conn, String refnumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdd.total_signed from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number=  '" + refnumber + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getDocumentType(Connection conn, String refnumber){
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from tr_document_h tdh join ms_lov msl on tdh.lov_doc_type = msl.id_lov where tdh.ref_number = '" + refnumber + "'")
		metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getTenant(Connection conn, String user) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("Select mt.tenant_code from am_msuser amu JOIN ms_office mo ON mo.id_ms_office = amu.id_ms_office JOIN ms_tenant mt ON mt.id_ms_tenant = mo.id_ms_tenant where login_id = '" +  user  + "' AND mt.is_active = '1'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getSendDoc(Connection conn, String documentid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mst.tenant_code, tdh.ref_number, tdd.document_id,mdt.doc_template_code, mso.office_code,mso.office_name, msr.region_code, msr.region_name,mbl.business_line_code, mbl.business_line_name, tdd.is_sequence,msv.vendor_code, tdh.result_url,tdh.url_upload, tdh.total_document from tr_document_d as tdd join tr_document_h as tdh on tdd.id_document_h = tdh.id_document_h join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join am_msuser as amm on tdds.id_ms_user = amm.id_ms_user join ms_tenant as mst on tdd.id_ms_tenant = mst.id_ms_tenant join ms_lov as lov on tdds.lov_signer_type = lov.id_lov join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join ms_office as mso on tdh.id_ms_office = mso.id_ms_office join ms_region as msr on mso.id_ms_region = msr.id_ms_region join ms_business_line as mbl on tdh.id_ms_business_line = mbl.id_ms_business_line join ms_vendor as msv on tdd.id_ms_vendor = msv.id_ms_vendor where tdd.document_id = '" + documentid + "' GROUP BY mst.tenant_code, tdh.ref_number, tdd.document_id,mdt.doc_template_code, mso.office_code,mso.office_name, msr.region_code, msr.region_name,mbl.business_line_code, mbl.business_line_name, mso.office_code, tdd.is_sequence,msv.vendor_code,tdh.result_url,tdh.url_upload,tdh.total_document")

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
	getSendDocForEmailAndSignerType(Connection conn, String documentid,String emailSigner) {
		if (emailSigner.length() == countLengthforSHA256) {
			emailWhere = "amm.hashed_id_no = '" + emailSigner + "'"
			selectData = "amm.hashed_id_no "
		} else {
			emailWhere = "amm.login_id = '" + emailSigner + "'"
			selectData = "amm.login_id "
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select DISTINCT " + selectData + " as email, lov.code as code, tdds.seq_no from tr_document_d as tdd join tr_document_h as tdh on tdd.id_document_h = tdh.id_document_h join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join am_msuser as amm on tdds.id_ms_user = amm.id_ms_user join ms_tenant as mst on tdd.id_ms_tenant = mst.id_ms_tenant join ms_lov as lov on tdds.lov_signer_type = lov.id_lov where tdd.document_id = '"+documentid+"' and " + emailWhere + "")
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
	getKotakMasukSendDoc(Connection conn, String refNumber, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number, msl.description as doctype,case when mdt.doc_template_name is null then tdd.document_name else mdt.doc_template_name end,  case when amm2.full_name != '' or amm2.full_name != null then amm2.full_name else '' end, TO_CHAR(tdd.request_date, 'DD-Mon-YYYY HH24:MI') as timee, case when tdd.completed_date is null then '-' else TO_CHAR(tdd.completed_date, 'DD-Mon-YYYY HH24:MI') end , msl_sign.description as description  from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h left join ms_lov msl on tdh.lov_doc_type = msl.id_lov left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d left join ms_lov ms on tdds.lov_signer_type = ms.id_lov join am_msuser amm on tdds.id_ms_user = amm.id_ms_user join ms_lov msl_sign on tdd.lov_sign_status = msl_sign.id_lov left join am_msuser amm2 on tdh.id_msuser_customer = amm2.id_ms_user   where tdh.ref_number = '"+refNumber+"' and amm.login_id = '"+emailSigner+"' GROUP BY tdds.id_document_d, tdh.ref_number, msl.description, mdt.doc_template_name, tdd.document_name, amm2.full_name, tdd.request_date , tdd.completed_date, msl_sign.description ORDER BY tdds.id_document_d desc")
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
	getSignStatus(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from tr_document_d tdd LEFT JOIN tr_document_h tdh on tdd.id_Document_h = tdh.id_document_h join ms_lov msl on tdd.lov_sign_status = msl.id_lov where tdh.ref_number = '" + refNumber + "' GROUP BY tdd.id_document_d, msl.description ORDER BY tdd.id_document_d desc")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getTotalStampingandTotalMaterai(Connection conn, String refNumber, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdd.total_stamping, tdd.total_materai from tr_document_d tdd LEFT join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h LEFT JOIN tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d LEFT JOIN am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdh.ref_number = '"+refNumber+"' AND amm.login_id = '"+emailSigner+"' GROUP BY tdds.id_document_d, tdd.total_stamping, tdd.total_materai ORDER BY tdds.id_document_d desc")

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
	getSignerKotakMasukSendDoc(Connection conn, String value, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select case when ms.description != '' or ms.description != null then ms.description else 'Signer' end as signertype,amm.full_name as name, amm.login_id as email,CASE WHEN amm.is_active = '1' THEN 'Sudah Aktivasi' END as aktivasi, CASE WHEN tdds.sign_date is not null THEN 'Signed' ELSE msl.description END as status, CASE WHEN tdds.sign_date IS null THEN '-' else to_char(tdds.sign_date, 'DD-Mon-YYYY HH24:MI') END sign_date from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_lov msl on tdd.lov_sign_status = msl.id_lov left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d left join ms_lov ms on tdds.lov_signer_type = ms.id_lov join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where (document_id = '" + value + "' OR tdh.ref_number = '"+ value +"') and amm.login_id = '" + emailSigner + "' ORDER BY tdds.id_document_d_sign asc limit 1")
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
	getEmailsSign(Connection conn, String documentid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT au.login_id FROM tr_document_h AS tdh JOIN tr_document_d AS tdd ON tdh.id_document_h = tdd.id_document_h JOIN tr_document_d_sign AS tdds ON tdd.id_document_d = tdds.id_document_d JOIN am_msuser AS au ON au.id_ms_user = tdds.id_ms_user WHERE tdd.document_id = '" + documentid + "' ")

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
	getbulkSign(Connection conn, String documentids) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select STRING_AGG(amm.login_id,';'),msv.vendor_code, tdd.total_signed from tr_document_d_sign as tdds join tr_document_d as tdd on tdds.id_document_d = tdd.id_document_d join am_msuser as amm on tdds.id_ms_user = amm.id_ms_user join ms_vendor as msv on tdd.id_ms_vendor = msv.id_ms_vendor join tr_document_h as tdh on tdh.id_document_h = tdd.id_document_h where tdd.document_id = '" + documentids + "' GROUP BY msv.vendor_code, tdd.total_signed")
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
	getVendorCodeUsingDocId(Connection conn, String docId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select case when msv.vendor_code is not null then msv.vendor_code else msv1.vendor_code end from tr_document_d tdd left join ms_doc_template mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join ms_vendor msv on tdd.id_ms_vendor = msv.id_ms_vendor left join ms_vendor msv1 on mdt.id_ms_vendor = msv1.id_ms_vendor where tdd.document_id = '"+ docId +"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getTrxSendDocSigning(Connection conn, String trxno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select trx_no,ref_no, TO_CHAR(trx_date, 'yyyy-MM-DD'), qty, notes from tr_balance_mutation where trx_no = '" + trxno + "'")

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
	getDataPencarianDokumen(Connection conn, String emailSigner, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select case when amm2.full_name is null then '' else amm2.full_name end, tdh.ref_number, msl.description from tr_document_d_sign tdds left join am_msuser amm on tdds.id_ms_user = amm.id_ms_user left join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d left join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h left join ms_lov msl_signertype on tdds.lov_signer_type = msl_signertype.id_lov left join ms_lov msl on tdh.lov_doc_type = msl.id_lov left join am_msuser amm2 on tdh.id_msuser_customer = amm2.id_ms_user  where amm.login_id = '"+emailSigner+"' and tdh.ref_number = '"+refNumber+"' GROUP BY tdds.id_document_d, amm2.full_name, tdh.ref_number, msl.description ORDER BY tdds.id_document_d desc")
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
	getRoleLogin(Connection conn, String emailSigner, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amr.role_name from ms_useroftenant muot join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_memberofrole amor on muot.id_ms_user = amor.id_ms_user join am_msuser amm on muot.id_ms_user = amm.id_ms_user join am_msrole amr on amor.id_ms_role = amr.id_ms_role where amm.login_id = '"+emailSigner+"' and mst.tenant_code = '"+tenantCode+"' limit 1")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getIsManualUpload(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select case when tdh.is_manual_upload is null or tdh.is_manual_upload = '0' then 'No' else 'Yes' end from tr_document_h tdh  left join tr_document_d tdd on tdd.id_document_h = tdh.id_document_h where tdd.document_id = '"+documentId+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getSignDocEmbedStoreDB(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select TO_CHAR(sign_date, 'yyyy-MM-dd') from tr_document_d_sign tdds JOIN tr_document_d tdd ON tdds.id_document_d = tdd.id_document_d where document_id = '"+documentId+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getTotalDocumentBasedOnSigner(Connection conn, String refNumber, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(distinct(tdd.id_document_d)) from tr_document_d tdd left join tr_document_h tdh on tdd.id_Document_h = tdh.id_document_h left join tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d left join am_msuser amm on amm.id_ms_user = tdds.id_ms_user where tdh.ref_number = '"+refNumber+"' AND amm.login_id = '"+emailSigner+"' AND tdds.sign_date IS NULL")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getDocumentIdBasedOnSigner(Connection conn, String refNumber, String tenantCode, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdd.document_id from tr_document_h tdh join tr_document_d tdd on tdh.id_document_h = tdd.id_document_h  join ms_tenant mst on tdh.id_ms_tenant = mst.id_ms_tenant join ms_vendor msv on tdd.id_ms_vendor = msv.id_ms_vendor  left join tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d left join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdh.ref_number = '"+refNumber+"' and mst.tenant_code = '"+tenantCode+"' AND amm.login_id = '"+emailSigner+"' AND tdds.sign_date IS NULL group by tdd.document_id, tdd.id_document_d")
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
	settingSentOTPbyEmail(Connection conn, String value) {
		stm = conn.createStatement()

		updateVariable = stm.executeUpdate("UPDATE ms_tenant SET sent_otp_by_email = "+ value +" WHERE tenant_code = '"+ GlobalVariable.Tenant +"'")
	}
}
