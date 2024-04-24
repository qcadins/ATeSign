package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

class SendSign {

	String data, commandSql
	int columnCount, i, countLengthforSHA256 = 64, updateVariable
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []
	String emailWhere, selectData

	@Keyword
	settingEmailServiceVendorRegisteredUser(Connection conn, String value, String idNo) {
		stm = conn.createStatement()

		updateVariable = stm.executeUpdate("UPDATE ms_vendor_registered_user SET email_service = '" + value + "' WHERE EXISTS (SELECT * FROM AM_MSUSER AMM WHERE amm.hashed_id_no = '" + idNo + "' OR amm.hashed_phone = '" + idNo + "' OR amm.login_id = '" + idNo + "')")
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
		resultSet = stm.executeQuery("SELECT STRING_AGG(login_id, ';' ORDER BY id_document_d_sign) AS aa FROM (SELECT DISTINCT tdds.id_ms_user,au.login_id, FIRST_VALUE(tdds.id_document_d_sign) OVER (PARTITION BY tdds.id_ms_user ORDER BY tdds.id_document_d_sign) AS id_document_d_sign FROM tr_document_h AS tdh JOIN tr_document_d AS tdd ON tdh.id_document_h = tdd.id_document_h JOIN tr_document_d_sign AS tdds ON tdd.id_document_d = tdds.id_document_d JOIN am_msuser AS au ON au.id_ms_user = tdds.id_ms_user WHERE tdd.document_id = '" + documentid + "') AS alls;")
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

		resultSet = stm.executeQuery("SELECT COUNT(DISTINCT(tdds.id_ms_user)) FROM tr_document_d tdd JOIN tr_document_d_sign tdds ON tdd.id_document_d = tdds.id_document_d LEFT JOIN tr_document_h tdh on tdd.id_document_h = tdh.id_Document_h WHERE (tdd.document_id = '" + documentId + "' OR tdh.ref_number = '" + documentId + "') AND sign_date IS NOT NULL")
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

		resultSet = stm.executeQuery("SELECT COUNT (DISTINCT amm.login_id) FROM tr_document_d_sign tdds LEFT JOIN tr_document_d tdd on tdd.id_document_d = tdds.id_document_d LEFT JOIN tr_Document_h tdh on tdd.id_document_h = tdh.id_document_h LEFT JOIN am_msuser amm on tdds.id_ms_user = amm.id_ms_user WHERE tdd.document_id = '" + documentId + "'")

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
	getPaymentType(Connection conn, String refnumber) {
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from tr_document_d tdd join ms_lov msl on tdd.lov_payment_sign_type = msl.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '" + refnumber + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getSaldoUsedBasedonPaymentType(Connection conn, String refnumber, String emailSigner) {
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select CASE WHEN msl_tdd.description = 'Per Document' then '1' WHEN msl_tdd.description = 'Per Sign' then count(tdds.sign_location) end from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d left join ms_lov msl on tdds.lov_signer_type = msl.id_lov join ms_lov msl_tdd on tdd.lov_payment_sign_type = msl_tdd.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdh.ref_number = '" + refnumber + "' and amm.login_id = '" + emailSigner + "' GROUP BY msl.description, msl_tdd.description")
		metadata = resultSet.metaData

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
	getDocumentType(Connection conn, String refnumber) {
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from tr_document_h tdh join ms_lov msl on tdh.lov_doc_type = msl.id_lov where tdh.ref_number = '" + refnumber + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getTenant(Connection conn, String user) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("Select mt.tenant_code from am_msuser amu JOIN ms_office mo ON mo.id_ms_office = amu.id_ms_office JOIN ms_tenant mt ON mt.id_ms_tenant = mo.id_ms_tenant where login_id = '" + user + "' AND mt.is_active = '1'")

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

		resultSet = stm.executeQuery("select mst.tenant_code, tdh.ref_number, tdd.document_id,mdt.doc_template_code, mso.office_code,mso.office_name, msr.region_code, msr.region_name,mbl.business_line_code, mbl.business_line_name, tdd.is_sequence,msv.vendor_code, tdh.result_url,tdh.url_upload, tdh.total_document from tr_document_d as tdd join tr_document_h as tdh on tdd.id_document_h = tdh.id_document_h join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join am_msuser as amm on tdds.id_ms_user = amm.id_ms_user join ms_tenant as mst on tdd.id_ms_tenant = mst.id_ms_tenant join ms_lov as lov on tdds.lov_signer_type = lov.id_lov join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join ms_office as mso on tdh.id_ms_office = mso.id_ms_office left join ms_region as msr on mso.id_ms_region = msr.id_ms_region left join ms_business_line as mbl on tdh.id_ms_business_line = mbl.id_ms_business_line join ms_vendor as msv on tdd.id_ms_vendor = msv.id_ms_vendor where tdd.document_id = '" + documentid + "' GROUP BY mst.tenant_code, tdh.ref_number, tdd.document_id,mdt.doc_template_code, mso.office_code,mso.office_name, msr.region_code, msr.region_name,mbl.business_line_code, mbl.business_line_name, mso.office_code, tdd.is_sequence,msv.vendor_code,tdh.result_url,tdh.url_upload,tdh.total_document")

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
	getSendDocForEmailAndSignerType(Connection conn, String documentid, String emailSigner) {
		if (emailSigner.length() == countLengthforSHA256) {
			emailWhere = "amm.hashed_id_no = '" + emailSigner + "'"
			selectData = "amm.hashed_id_no "
		} else {
			emailWhere = "amm.login_id = '" + emailSigner + "'"
			selectData = "amm.login_id "
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select DISTINCT " + selectData + " as email, lov.code as code, tdds.seq_no from tr_document_d as tdd join tr_document_h as tdh on tdd.id_document_h = tdh.id_document_h join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join am_msuser as amm on tdds.id_ms_user = amm.id_ms_user join ms_tenant as mst on tdd.id_ms_tenant = mst.id_ms_tenant join ms_lov as lov on tdds.lov_signer_type = lov.id_lov where tdd.document_id = '" + documentid + "' and " + emailWhere + "")
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
	getKotakMasukSendDoc(Connection conn, String documentId, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number, msl.description as doctype,case when mdt.doc_template_name is null then tdd.document_name else mdt.doc_template_name end,  case when amm2.full_name != '' or amm2.full_name != null then amm2.full_name else '' end, TO_CHAR(tdd.request_date, 'DD-Mon-YYYY HH24:MI') as timee, case when tdd.completed_date is null then '-' else TO_CHAR(tdd.completed_date, 'DD-Mon-YYYY HH24:MI') end , msl_sign.description as description  from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h left join ms_lov msl on tdh.lov_doc_type = msl.id_lov left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d left join ms_lov ms on tdds.lov_signer_type = ms.id_lov join am_msuser amm on tdds.id_ms_user = amm.id_ms_user join ms_lov msl_sign on tdd.lov_sign_status = msl_sign.id_lov left join am_msuser amm2 on tdh.id_msuser_customer = amm2.id_ms_user where tdd.document_id = '" + documentId + "' and amm.login_id = '" + emailSigner + "' GROUP BY tdds.id_document_d, tdh.ref_number, msl.description, mdt.doc_template_name, tdd.document_name, amm2.full_name, tdd.request_date , tdd.completed_date, msl_sign.description ORDER BY tdds.id_document_d desc")
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

		resultSet = stm.executeQuery("select tdd.total_stamping, tdd.total_materai from tr_document_d tdd LEFT join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h LEFT JOIN tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d LEFT JOIN am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdh.ref_number = '" + refNumber + "' AND amm.login_id = '" + emailSigner + "' GROUP BY tdds.id_document_d, tdd.total_stamping, tdd.total_materai ORDER BY tdds.id_document_d desc")

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
	getSignerKotakMasukSendDoc(Connection conn, String value, String documentName) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT description, full_name, signer_registered_email, is_active, sign_status, case when sign_date is null then '-' else sign_date end FROM (select distinct on (vuser.signer_registered_email) signer_registered_email, case when mu.is_active = '1' THEN 'Sudah Aktivasi' end as is_active, COALESCE(lovds.description, 'Signer') AS description, mu.full_name, case when sign_date is null then (select lov.description from ms_lov lov where lov.lov_group = 'LOV_SIGN_STATUS' and lov.code = 'NS') else 'Signed' end as sign_status, to_char(dds.sign_date, 'YYYY-MM-DD HH24:MI:SS') as sign_date , dds.seq_no, vuser.email_service from tr_document_d dd join tr_document_d_sign dds on dd.id_document_d = dds.id_document_d join tr_document_h dh on dh.id_document_h = dd.id_document_h left join ms_doc_template mdt on dd.id_ms_doc_template = mdt.id_doc_template left join lateral (select lovds.description from ms_lov lovds where dds.lov_signer_type = lovds.id_lov) lovds ON TRUE join am_msuser mu on dds.id_ms_user = mu.id_ms_user join ms_vendor_registered_user vuser on vuser.id_ms_user = mu.id_ms_user and vuser.id_ms_vendor = dd.id_ms_vendor join am_user_personal_data upd on mu.id_ms_user = upd.id_ms_user where (dd.document_name = '" + documentName + "' OR mdt.doc_template_name = '" + documentName + "') AND dh.ref_number = '" + value + "') sub order by description ASC, full_name ASC, signer_registered_email ASC, seq_no ASC;")

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

		resultSet = stm.executeQuery("select case when msv.vendor_code is not null then msv.vendor_code else msv1.vendor_code end from tr_document_d tdd left join ms_doc_template mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join ms_vendor msv on tdd.id_ms_vendor = msv.id_ms_vendor left join ms_vendor msv1 on mdt.id_ms_vendor = msv1.id_ms_vendor where tdd.document_id = '" + docId + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getDataPencarianDokumen(Connection conn, String emailSigner, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select case when amm2.full_name is null then '' else amm2.full_name end, tdh.ref_number, msl.description from tr_document_d_sign tdds left join am_msuser amm on tdds.id_ms_user = amm.id_ms_user left join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d left join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h left join ms_lov msl_signertype on tdds.lov_signer_type = msl_signertype.id_lov left join ms_lov msl on tdh.lov_doc_type = msl.id_lov left join am_msuser amm2 on tdh.id_msuser_customer = amm2.id_ms_user  where amm.login_id = '" + emailSigner + "' and tdh.ref_number = '" + refNumber + "' GROUP BY tdds.id_document_d, amm2.full_name, tdh.ref_number, msl.description ORDER BY tdds.id_document_d desc")
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

		resultSet = stm.executeQuery("select amr.role_name from ms_useroftenant muot join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant join am_memberofrole amor on muot.id_ms_user = amor.id_ms_user join am_msuser amm on muot.id_ms_user = amm.id_ms_user join am_msrole amr on amor.id_ms_role = amr.id_ms_role where amm.login_id = '" + emailSigner + "' and mst.tenant_code = '" + tenantCode + "' limit 1")
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

		resultSet = stm.executeQuery("select case when tdh.is_manual_upload is null or tdh.is_manual_upload = '0' then 'No' else 'Yes' end from tr_document_h tdh  left join tr_document_d tdd on tdd.id_document_h = tdh.id_document_h where tdd.document_id = '" + documentId + "'")
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

		resultSet = stm.executeQuery("select TO_CHAR(sign_date, 'yyyy-MM-dd') from tr_document_d_sign tdds JOIN tr_document_d tdd ON tdds.id_document_d = tdd.id_document_d where document_id = '" + documentId + "'")
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

		resultSet = stm.executeQuery("select count(distinct(tdd.id_document_d)) from tr_document_d tdd left join tr_document_h tdh on tdd.id_Document_h = tdh.id_document_h left join tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d left join am_msuser amm on amm.id_ms_user = tdds.id_ms_user where tdh.ref_number = '" + refNumber + "' AND amm.login_id = '" + emailSigner + "' AND tdd.document_name = '' AND tdds.sign_date IS NULL")
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

		resultSet = stm.executeQuery("select tdd.document_id from tr_document_h tdh join tr_document_d tdd on tdh.id_document_h = tdd.id_document_h  join ms_tenant mst on tdh.id_ms_tenant = mst.id_ms_tenant join ms_vendor msv on tdd.id_ms_vendor = msv.id_ms_vendor  left join tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d left join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdh.ref_number = '" + refNumber + "' and mst.tenant_code = '" + tenantCode + "' AND amm.login_id = '" + emailSigner + "' AND tdds.sign_date IS NULL group by tdd.document_id, tdd.id_document_d")
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
		if (value != '') {
			updateVariable = stm.executeUpdate("UPDATE ms_tenant SET sent_otp_by_email = " + value + " WHERE tenant_code = '" + GlobalVariable.Tenant + "'")
		}
	}

	@Keyword
	getProyectionOfVendorForSend(Connection conn, String documentTemplateCode, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msv.vendor_name from ms_doc_template mdt left join ms_tenant mst on mdt.id_ms_tenant = mst.id_ms_tenant join ms_vendor msv on mdt.id_ms_vendor = msv.id_ms_vendor WHERE mst.tenant_code = '" + tenantCode + "' AND mdt.doc_template_code = '" + documentTemplateCode + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data == null || data.toString() == 'null') {
			resultSet = stm.executeQuery("select msv.vendor_name from ms_vendoroftenant mvot left join ms_tenant mst on mvot.id_ms_tenant = mst.id_ms_tenant join ms_vendor msv on mvot.id_ms_vendor = msv.id_ms_vendor WHERE mst.tenant_code = '" + tenantCode + "' ORDER BY mvot.default_vendor limit 1")
			metadata = resultSet.metaData

			columnCount = metadata.getColumnCount()

			while (resultSet.next()) {
				data = resultSet.getObject(1)
			}
			data
		} else {
			data
		}
	}

	@Keyword
	getProsesTtdProgressPrivy(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT count(tdsr.id_ms_user) FROM tr_document_d tdd left join tr_document_h tdh on tdh.id_document_h = tdd.id_document_h JOIN tr_document_signing_request tdsr ON tdh.id_document_h = tdsr.id_document_h WHERE tdd.document_id = '" + documentId + "' AND tdsr.request_status != '2'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getTrxNo(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.trx_no from tr_Document_d tdd LEFT JOIN tr_balance_mutation tbm on tbm.id_document_d = tdd.id_Document_d LEFT JOIN tr_document_h tdh on tbm.id_Document_h = tdh.id_document_h WHERE tdd.document_id = '" + documentId + "' Order by tbm.dtm_crt asc ")

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
	getVendorNameUsingDocId(Connection conn, String docId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select case when msv.vendor_name is not null then msv.vendor_code else msv1.vendor_code end from tr_document_d tdd left join ms_doc_template mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join ms_vendor msv on tdd.id_ms_vendor = msv.id_ms_vendor left join ms_vendor msv1 on mdt.id_ms_vendor = msv1.id_ms_vendor where tdd.document_id = '" + docId + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getCheckingActiveDocument(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT is_active, ref_number FROM tr_document_h WHERE ref_number LIKE '" + refNumber + "${"_"}%' ESCAPE '\$' ORDER BY dtm_crt DESC")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		listdata

		String check = ''

		for (i = 0; i < listdata.size(); i++) {
			if (listdata[i] == '1') {
				check = check + listdata[i + 1] + ', '
			} else if (i == listdata.size() - 1) {
				check = check + ''
			}
		}

		if (check.size() == 0) {
			check
		} else {
			check[0..-3]
		}
	}

	@Keyword
	getEmaiLFromNIK(Connection conn, String nik) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT login_id from am_msuser where hashed_id_no =  encode(sha256('" + nik + "'), 'hex')")
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
	signCallBack(Connection conn, String tenantCode, String documentId, String loginId, String code) {
		stm = conn.createStatement()

		String helperQuery = ''

		if (code == 'SIGNING_COMPLETE') {
			helperQuery = "mst.tenant_code = '" + tenantCode + "' AND tdd.document_id = '" + documentId + "' AND amm.login_id = '" + loginId + "' AND msl.code = '" + code + "'"
		} else if (code == 'DOCUMENT_SIGN_COMPLETE' || code == 'ALL_DOCUMENT_SIGN_COMPLETE') {
			helperQuery = "mst.tenant_code = '" + tenantCode + "' AND tdd.document_id = '" + documentId + "' AND msl.code = '" + code + "'"
		}

		resultSet = stm.executeQuery("""select substring(callback_request, '"callbackType":"([^"]+)"') AS callbackType, substring(callback_request, '"message":"([^"]+)"') AS message from tr_client_callback_request tccr left join tr_document_d tdd on tccr.id_document_d = tdd.id_document_d left join am_msuser amm on amm.id_ms_user = tccr.id_ms_user left join ms_tenant mst on tccr.id_ms_tenant = mst.id_ms_tenant left join ms_lov msl on msl.id_lov = tccr.lov_callback_type where """ + helperQuery)
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
	checkDocumentIsSigned(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(tdds.id_ms_user) from tr_document_d_sign tdds left join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d where tdd.document_id = '" + documentId + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data == '1') {
			data
		} else {
			resultSet = stm.executeQuery("select count(tdds.id_ms_user) from tr_document_d_sign tdds left join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d where tdd.document_id = '" + documentId + "' AND tdds.sign_date IS NULL")
			metadata = resultSet.metaData

			columnCount = metadata.getColumnCount()

			while (resultSet.next()) {
				data = resultSet.getObject(1)
			}
			data
		}
	}

	@Keyword
	checkAllDocumentIsSigned(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(tdd.document_id) from tr_document_d tdd left join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '" + refNumber + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data == '1') {
			data
		} else {
			resultSet = stm.executeQuery("select count(tdd.document_id) from tr_document_d tdd left join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '" + refNumber + "' AND tdd.completed_date IS NULL")
			metadata = resultSet.metaData

			columnCount = metadata.getColumnCount()

			while (resultSet.next()) {
				data = resultSet.getObject(1)
			}
			data
		}
	}
}
