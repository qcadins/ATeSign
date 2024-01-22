package connection

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class APIFullService {

	String data, helperQuery
	int columnCount, i, countLengthforSHA256 = 64, updateVariable
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet, helperResult
	ArrayList<String> listdata = []
	String emailWhere, selectData

	@Keyword
	getGenInvLink(Connection conn, String tenant, String phone, String idno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tril.usr_crt, tril.gender, tril.kelurahan, tril.kecamatan, tril.kota, tril.zip_code, tril.date_of_birth, tril.place_of_birth, tril.provinsi, tril.email, tril.id_no, tril.phone, tril.address, tril.full_name, mst.tenant_code from tr_invitation_link as tril join ms_tenant as mst on tril.id_ms_tenant = mst.id_ms_tenant where tril.is_active = '1' and mst.tenant_code = '" + tenant + "' and tril.phone = '" + phone + "' and tril.id_no = '" + idno + "'")

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
	getOTP(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT otp_code FROM tr_invitation_link WHERE receiver_detail = '" + email + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getAPIGenInvLinkVerifTrx(Connection conn, String name, String phone) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select qty from tr_balance_mutation tbm left join am_msuser am on am.id_ms_user = tbm.id_ms_user join ms_lov ml on ml.id_lov = tbm.lov_trx_type where ml.description = 'Use Verification' AND (am.full_name = '" + name + "' OR tbm.usr_crt = '" + phone + "')  order by trx_date desc")
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
	getAPIGenInvLinkOTPTrx(Connection conn, String name, String usedSaldo) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.qty from tr_balance_mutation tbm join am_msuser am on am.id_ms_user = tbm.id_ms_user join ms_lov ml on ml.id_lov = tbm.lov_trx_type where am.full_name = '" + name + "' and ml.description = 'Use " + usedSaldo + "'")

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
	getOTPAktivasi(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT otp_code FROM am_msuser WHERE login_id = '" + email + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getResetOTP(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT reset_code_request_num FROM am_msuser where login_id = '" + email + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getTenantAPIKey(Connection conn, String tenantcode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT api_key FROM ms_tenant WHERE tenant_code = '" + tenantcode + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data.toString() == 'null') {
			data = ''
		}

		data
	}

	@Keyword
	settingEmailServiceTenant(Connection conn, String value) {
		stm = conn.createStatement()
		updateVariable = stm.executeUpdate("UPDATE ms_tenant SET email_service = " + value + " WHERE tenant_code = '" + GlobalVariable.Tenant + "'")
	}

	@Keyword
	getTotalUnsignedDocuments(Connection conn, String tenantCode, String email) {
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(distinct (tdds.id_document_d)) from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d join am_msuser amm on amm.id_ms_user = tdds.id_ms_user join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_tenant mst on tdd.id_ms_tenant = mst.id_ms_tenant where (amm.login_id = '" + email + "' OR amm.hashed_phone = encode(sha256('" + email + "'), 'hex')) and tdds.sign_date is null and mst.tenant_code = '" + tenantCode + "' AND tdh.is_active = '1' group by mst.tenant_code, mst.api_key")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data != null) {
			Integer.parseInt(data)
		} else {
			data = 0
		}
	}

	@Keyword
	checkAPIRegisterActive(Connection conn, String email, String notelp) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mvru.is_active, mvru.is_registered from ms_vendor_registered_user mvru JOIN ms_vendor mv ON mv.id_ms_vendor = mvru.id_ms_vendor where vendor_code = '" + GlobalVariable.Psre + "' AND (signer_registered_email = '" + email + "' OR mvru.hashed_signer_registered_phone = encode(sha256('" + notelp + "'), 'hex'))")

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
	getAPIRegisterTrx(Connection conn, String trxNo, String trxNo1) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select qty from tr_balance_mutation WHERE trx_no IN('" + trxNo + "', '" + trxNo1 + "')")

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
	getTotalMaterai(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select SUM(total_materai) from tr_document_d tdd JOIN tr_document_h tdh ON tdh.id_document_h = tdd.id_document_h where tdh.ref_number = '" + value + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getAPIRequestStampingTrx(Connection conn, String value, String totalMaterai) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select SUM(qty) from tr_balance_mutation tbm JOIN ms_lov ml ON ml.id_lov = tbm.lov_trx_type WHERE ref_no = '" + value + "' AND ml.description = 'Use Stamp Duty' LIMIT " + totalMaterai)

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	settingEmailServiceVendorRegisteredUser(Connection conn, String value, String email) {
		stm = conn.createStatement()

		updateVariable = stm.executeUpdate("UPDATE ms_vendor_registered_user SET email_service = '" + value + "' WHERE signer_registered_email = '" + email + "' ")
	}

	@Keyword
	getSendDocSigning(Connection conn, String documentid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT msv.vendor_code, mst.tenant_code, tdh.ref_number,tdd.document_id,CASE WHEN mdt.doc_template_code IS NULL THEN '' ELSE mdt.doc_template_code END, case when mso.office_code is null then '' else mso.office_code end,mso.office_name, case when msr.region_code is null then '' else msr.region_code end, case when msr.region_name is null then '' else msr.region_name end, case when mbl.business_line_code is null then '' else mbl.business_line_code end, case when mbl.business_line_name is null then '' else mbl.business_line_name end,tdh.total_document, tdd.is_sequence from tr_document_d as tdd join tr_document_h as tdh on tdd.id_document_h = tdh.id_document_h join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join am_msuser as amm on tdds.id_ms_user = amm.id_ms_user join ms_tenant as mst on tdd.id_ms_tenant = mst.id_ms_tenant join ms_lov as lov on tdds.lov_signer_type = lov.id_lov left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join ms_office as mso on tdh.id_ms_office = mso.id_ms_office left join ms_region as msr on mso.id_ms_region = msr.id_ms_region left join ms_business_line as mbl on tdh.id_ms_business_line = mbl.id_ms_business_line left join ms_vendor as msv on tdd.id_ms_vendor = msv.id_ms_vendor  where tdd.document_id = '" + documentid + "' GROUP BY mst.tenant_code, tdh.ref_number, tdd.document_id,mdt.doc_template_code, mso.office_code,mso.office_name, msr.region_code, msr.region_name,mbl.business_line_code, mbl.business_line_name, mso.office_code, tdd.is_sequence,msv.vendor_code,tdh.total_document")
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
	getTrxSendDocSigning(Connection conn, String trxno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.trx_no,tbm.ref_no, TO_CHAR(tbm.trx_date, 'yyyy-MM-DD'), tbm.qty, tbm.notes, mso.office_code, msr.region_code, mbl.business_line_code from tr_balance_mutation tbm left join ms_business_line mbl on tbm.id_ms_business_line = mbl.id_ms_business_line left join ms_office mso on tbm.id_ms_office = mso.id_ms_office left join ms_region msr on mso.id_ms_region = msr.id_ms_region where trx_no = '" + trxno + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				if (data == null) {
					data = ''
				}
				listdata.add(data)
			}
		}
		listdata
	}

	@Keyword
	getSignLocation(Connection conn, String docid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT STRING_AGG(sign_locations, ';') AS combined_sign_locations FROM (SELECT STRING_AGG(tdds.sign_location, ';') AS sign_locations FROM tr_document_d tdd JOIN tr_document_d_sign tdds ON tdd.id_document_d = tdds.id_document_d WHERE tdd.document_id = '" + docid + "' GROUP BY tdds.id_document_d_sign) AS subquery;")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getPrivyStampLocation(Connection conn, String docid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select STRING_AGG(sdt.privy_sign_location,';') from tr_document_d_stampduty sdt join tr_document_d d ON d.id_document_d = sdt.id_document_d join tr_document_h h ON h.id_document_h = d.id_document_h where d.document_id = '" + docid + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getTemplateDocPrivyStampLoc(Connection conn, String docid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT STRING_AGG(mds.privy_sign_location,';') FROM ms_doc_template_sign_loc mds LEFT JOIN ms_doc_template mdt ON mdt.id_doc_template = mds.id_doc_template LEFT JOIN tr_document_d tdd ON tdd.id_ms_doc_template = mds.id_doc_template LEFT JOIN ms_lov mlo ON mlo.id_lov = mds.lov_sign_type WHERE document_id = '" + docid + "' AND description = 'Stamp Duty (materai)'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
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
	getResetCodeRequestNum(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select reset_code_request_num from am_msuser where login_id = '" + email + "' limit 1 ")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	checkAPISentOTPSigning(Connection conn, String trxno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.trx_no, mvru.signer_registered_email, tbm.qty, CASE WHEN tbm.ref_no is null then '' else tbm.ref_no END, tbm.notes,amm.otp_code,amm.reset_code_request_num, mst.api_key, mst.tenant_code from tr_balance_mutation tbm join ms_vendor_registered_user mvru on tbm.id_ms_user = mvru.id_ms_user join ms_tenant mst on tbm.id_ms_tenant = mst.id_ms_tenant join am_msuser amm on tbm.id_ms_user = amm.id_ms_user join ms_lov msltrxtype on tbm.lov_trx_type = msltrxtype.id_lov join ms_lov mslbalancetype on tbm.lov_balance_type = mslbalancetype.id_lov where tbm.trx_no = '" + trxno + "' and msltrxtype.description = 'Use OTP' and mslbalancetype.description = 'OTP'")

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
	getRefNumber(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number from tr_document_h tdh join tr_document_d tdd on tdh.id_document_h = tdd.id_document_h where tdd.document_id = '" + documentId + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data == null) {
			data = ''
		}

		data
	}

	@Keyword
	getSign(Connection conn, String documentid, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.qty, tdsr.request_status, tdh.ref_number, CASE WHEN tdsr.user_request_ip is null then '' else tdsr.user_request_ip end,  CASE WHEN tdsr.user_request_browser_information is null then '' else tdsr.user_request_browser_information end, tdsr.usr_crt, tdd.signing_process, TO_CHAR(tdds.sign_date, 'yyyy-MM-dd'), mst.api_key, mst.tenant_code from tr_document_signing_request tdsr  join tr_document_h tdh on tdh.id_document_h = tdsr.id_document_h left join tr_document_d tdd on tdd.id_document_h = tdh.id_document_h left join tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d left join tr_balance_mutation tbm on tdd.id_document_d = tbm.id_document_d left join am_msuser amm on tdds.id_ms_user = amm.id_ms_user left join ms_tenant mst on tbm.id_ms_tenant = mst.id_ms_tenant left join tr_document_signing_request_detail tdsrd on tdd.id_document_d = tdsrd.id_document_d where tdd.document_id = '" + documentid + "' and amm.login_id = '" + emailSigner + "' order by tbm.dtm_crt asc, tdds.sign_date desc, tdsr.id_document_signing_request desc")
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
	getTotalSigned(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select total_signed from tr_document_d where document_id = '" + documentId + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data != null) {
			Integer.parseInt(data)
		} else {
			data = 0
		}

		Integer.parseInt(data)
	}

	@Keyword
	getTotalSigner(Connection conn, String documentId, String emailsigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(tdds.id_document_d_sign) from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d left join ms_lov msl on tdds.lov_signer_type = msl.id_lov join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdd.document_id = '" + documentId + "' and amm.login_id = '" + emailsigner + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getAPICheckRegisterStoreDB(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mv.vendor_name, CASE WHEN mvru.is_registered = '0' AND mvru.is_active = '0' THEN '0' WHEN mvru.is_registered = '1' AND mvru.is_active = '1' THEN '2' WHEN mvru.is_registered = '1' AND mvru.is_active = '0' THEN '1' END from ms_vendor_registered_user mvru JOIN am_msuser amu ON amu.id_ms_user = mvru.id_ms_user JOIN ms_vendor mv ON mv.id_ms_vendor = mvru.id_ms_vendor where login_id = '" + value + "' OR amu.hashed_id_no = encode(sha256('" + value + "'), 'hex') OR amu.hashed_phone = encode(sha256('" + value + "'), 'hex')")

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
	getAPICheckStampingStoreDB(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT document_id, CASE WHEN proses_materai = 0 OR proses_materai IS NULL THEN 0 WHEN proses_materai = 53 THEN 1 WHEN proses_materai = 51 AND error_message IS NOT NULL THEN 2 WHEN proses_materai = 51 AND sdt_process = 'NOT_STR' THEN 0 WHEN proses_materai = 51 AND sdt_process = 'STM_SDT' THEN 4 WHEN proses_materai = 51 AND sdt_process = 'UPL_DOC' OR sdt_process = 'GEN_SDT' THEN 3 WHEN proses_materai = 51 AND sdt_process = 'UPL_OSS' OR sdt_process = 'UPL_CON' THEN 5 WHEN proses_materai = 51 AND sdt_process = 'SDT_FIN' THEN 1 WHEN proses_materai = 55 OR proses_materai = 52 AND sdt_process = 'NOT_STR' THEN 0 WHEN proses_materai = 55 OR proses_materai = 52 AND sdt_process = 'STM_SDT' THEN 4 WHEN proses_materai = 55 OR proses_materai = 52 AND sdt_process = 'UPL_DOC' OR sdt_process = 'GEN_SDT' THEN 3 WHEN proses_materai = 55 OR proses_materai = 52 AND sdt_process = 'UPL_OSS' OR sdt_process = 'UPL_CON' THEN 5 WHEN proses_materai = 55 OR proses_materai = 52 AND sdt_process = 'SDT_FIN' THEN 1 END, CASE WHEN error_message IS NULL THEN '' ELSE error_message END FROM tr_document_h tdh JOIN tr_document_d tdd ON tdd.id_document_h = tdh.id_document_h LEFT JOIN tr_document_h_stampduty_error tdhse ON tdhse.id_document_d = tdd.id_document_d WHERE tdh.ref_number = '" + value + "'")

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
	getAPICheckSigningStoreDB(Connection conn, String value, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select document_id, login_id,CASE WHEN ml.description = 'Employee' THEN '1' WHEN ml.description = 'Customer' THEN '2' WHEN ml.description = 'Spouse' THEN '3' WHEN ml.description = 'Guarantor' THEN '4' ELSE '' END, CASE WHEN LENGTH(TO_CHAR(MAX(tdds.sign_date), 'yyyy-MM-dd HH24:MI:SS')) > 0 THEN '1' ELSE '0' END,CASE WHEN TO_CHAR(MAX(tdds.sign_date), 'yyyy-MM-dd HH24:MI:SS') IS NULL THEN '' ELSE TO_CHAR(MAX(tdds.sign_date), 'yyyy-MM-dd HH24:MI:SS') END , amu.full_name from tr_document_h tdh LEFT JOIN tr_document_d tdd ON tdd.id_document_h = tdh.id_document_h LEFT JOIN tr_document_d_sign tdds ON tdds.id_document_d = tdd.id_document_d LEFT JOIN am_msuser amu ON amu.id_ms_user = tdds.id_ms_user LEFT JOIN ms_lov ml ON ml.id_lov = tdds.lov_signer_type LEFT JOIN ms_tenant mst on mst.id_ms_tenant = tdh.id_ms_tenant where ref_number = '" + value + "' AND mst.tenant_code = '" + tenantCode + "' GROUP BY document_id, login_id, ml.description, amu.full_name ")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				if (data == null) {
					data = ''
				}
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
	getPaymentTypeMULTIDOC(Connection conn, String docId) {
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from tr_document_d tdd join ms_lov msl on tdd.lov_payment_sign_type = msl.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdd.document_id = '" + docId + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getSaldoUsedBasedonPaymentType(Connection conn, String refNumber, String emailSigner) {
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select CASE WHEN msl_tdd.description = 'Per Document' then '1' WHEN msl_tdd.description = 'Per Sign' then count(tdds.sign_location) end from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d left join ms_lov msl on tdds.lov_signer_type = msl.id_lov join ms_lov msl_tdd on tdd.lov_payment_sign_type = msl_tdd.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdh.ref_number = '" + refNumber + "' and amm.login_id = '" + emailSigner + "' GROUP BY msl.description, msl_tdd.description")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getSaldoUsedBasedonPaymentTypeMULTIDOC(Connection conn, String documentId, String emailSigner) {
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select CASE WHEN msl_tdd.description = 'Per Document' then '1' WHEN msl_tdd.description = 'Per Sign' then count(tdds.sign_location) end from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d left join ms_lov msl on tdds.lov_signer_type = msl.id_lov join ms_lov msl_tdd on tdd.lov_payment_sign_type = msl_tdd.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdd.document_id = '" + documentId + "' and amm.login_id = '" + emailSigner + "' GROUP BY msl.description, msl_tdd.description")
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
	getAesKeyBasedOnTenant(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select aes_encrypt_key from ms_tenant where tenant_code = '" + tenantCode + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	gettrxSaldo(Connection conn, String refnumber, String tipeTransaksi) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.trx_no, TO_CHAR(tbm.trx_date,'YYYY-MM-DD HH24:MI:SS'), mso.office_name, ml.description ,amm.full_name, case when amm_two.full_name != '' or amm_two.full_name != null then tdh.ref_number||'('||amm_two.full_name||')' else tdh.ref_number end ,ml_doc_h.code,case when mdt.doc_template_name != null or mdt.doc_template_name != '' then mdt.doc_template_name else tdd.document_name end, tbm.notes, tbm.qty from tr_balance_mutation as tbm join ms_lov as ml on tbm.lov_trx_type = ml.id_lov join am_msuser as amm on tbm.id_ms_user = amm.id_ms_user join tr_document_h as tdh on tbm.id_document_h = tdh.id_document_h join ms_lov as ml_doc_h on tdh.lov_doc_type = ml_doc_h.id_lov join tr_document_d as tdd on tbm.id_document_d = tdd.id_document_d left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join am_msuser as amm_two on tdh.id_msuser_customer = amm_two.id_ms_user left join ms_office mso on tbm.id_ms_office = mso.id_ms_office where tdh.ref_number = '" + refnumber + "' AND ml.description = '" + tipeTransaksi + "' order by tbm.trx_no asc")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				if (data == null) {
					data = ''
				}
				listdata.add(data)
			}
		}
		listdata
	}

	@Keyword
	getFeedbackStoreDB(Connection conn, String emailsigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT trf.feedback_value, trf.comment FROM tr_feedback trf join am_msuser amm on trf.id_ms_user = amm.id_ms_user where amm.login_id = '" + emailsigner + "' ORDER BY trf.dtm_crt DESC LIMIT 1")

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
	getKotakMasukSendDoc(Connection conn, String documentid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number, msl.description as doctype,case when mdt.doc_template_name is null then tdd.document_name else mdt.doc_template_name end, TO_CHAR(tdd.dtm_crt, 'DD-Mon-YYYY HH24:MI') as timee, CASE WHEN msl_sign.description = 'Need Sign' THEN 'Belum TTD' END as description , case when tdd.completed_date is null then '-' else TO_CHAR(tdd.completed_date, 'DD-Mon-YYYY HH24:MI') end from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_lov msl on tdh.lov_doc_type = msl.id_lov left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join ms_lov ms on tdds.lov_signer_type = ms.id_lov join am_msuser amm on tdds.id_ms_user = amm.id_ms_user join ms_lov msl_sign on tdd.lov_sign_status = msl_sign.id_lov where document_id = '" + documentid + "' ORDER BY tdds.id_document_d_sign asc limit 1")

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
	getProsesTtdProgress(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT COUNT(tdsr.id_ms_user) FROM tr_document_h tdh JOIN tr_document_signing_request tdsr ON tdh.id_document_h = tdsr.id_document_h WHERE tdh.ref_number = '" + refNumber + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getSignerKotakMasukSendDoc(Connection conn, String documentid, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select ms.description as signertype,amm.full_name as name, amm.login_id as email,CASE WHEN amm.is_active = '1' THEN 'Sudah Aktivasi' END as aktivasi, CASE WHEN tdds.sign_date is not null THEN 'Signed' ELSE msl.description END as status, CASE WHEN tdds.sign_date IS null THEN '-' else to_char(tdds.sign_date, 'DD-Mon-YYYY HH24:MI') END sign_date from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_lov msl on tdd.lov_sign_status = msl.id_lov left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join ms_lov ms on tdds.lov_signer_type = ms.id_lov join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where document_id = '" + documentid + "' and amm.login_id = '" + emailSigner + "' ORDER BY tdds.id_document_d_sign asc limit 1")
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
	getTypeUsedSaldo(Connection conn, String trxno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mslo.description from tr_balance_mutation tbm join ms_lov mslo on tbm.lov_trx_type = mslo.id_lov where tbm.trx_no = '" + trxno + "' ")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getSplitLivenessFaceCompareBill(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select split_liveness_face_compare_bill from ms_tenant where tenant_code = '" + GlobalVariable.Tenant.toString().toUpperCase() + "' ")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	gettrxSaldoForMeterai(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.trx_no, mso.office_name, TO_CHAR(tbm.dtm_crt,'YYYY-MM-DD HH24:MI:SS'), ml.description , case when amm.full_name != '' or amm.full_name != null then amm.full_name else tbm.usr_crt end, case when amm_two.full_name != '' or amm_two.full_name != null then tdh.ref_number||'('||amm_two.full_name||')' else tdh.ref_number end, ml_doc_h.code,case when mdt.doc_template_name IS NOT NULL OR mdt.doc_template_name != null then mdt.doc_template_name else tdd.document_name end , tbm.notes, tbm.qty from tr_document_d_stampduty tddstamp join tr_document_d tdd on tddstamp.id_document_d = tdd.id_document_d join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join tr_stamp_duty tsd on tddstamp.id_stamp_duty = tsd.id_stamp_duty left join tr_balance_mutation tbm on tsd.id_stamp_duty = tbm.id_stamp_duty left join ms_business_line mbl on tdh.id_ms_business_line = mbl.id_ms_business_line left join ms_office mso on mso.id_ms_office = tdh.id_ms_office left join ms_lov as ml on tbm.lov_trx_type = ml.id_lov left join am_msuser as amm on tdh.id_msuser_customer = amm.id_ms_user left join ms_region msr on mso.id_ms_region = msr.id_ms_region join ms_lov as ml_doc_h on tdh.lov_doc_type = ml_doc_h.id_lov left join ms_lov msl on tsd.lov_stamp_duty_status = msl.id_lov left join am_msuser as amm_two on tdh.id_msuser_customer = amm_two.id_ms_user where tdh.ref_number = '" + refNumber + "' ORDER BY tbm.dtm_crt ")
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
	getVendorAccessToken(Connection conn, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msvr.vendor_access_token from ms_vendor_registered_user msvr join am_msuser amm on amm.id_ms_user = msvr.id_ms_user where amm.login_id = '" + emailSigner + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getPrivyId(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT VENDOR_REGISTRATION_ID FROM ms_vendor_registered_user WHERE SIGNER_REGISTERED_EMAIL = '" + email + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getVendorCodeUsingDocId(Connection conn, String docId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select case when msv.vendor_code is not null then msv.vendor_code else msv1.vendor_code end from tr_document_d tdd left join ms_doc_template mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join ms_vendor msv on mdt.id_ms_vendor = msv.id_ms_vendor left join ms_vendoroftenant mvot on tdd.id_ms_tenant = mvot.id_ms_tenant left join ms_vendor msv1 on mvot.id_ms_vendor = msv1.id_ms_vendor where tdd.document_id = '" + docId + "' order by mvot.default_vendor asc limit 1")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getSeqNoBasedOnDocTemplate(Connection conn, String docTemplate, String signerType) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select seq_no from ms_doc_template_sign_loc mdtsl join ms_doc_template mdt on mdtsl.id_doc_template = mdt.id_doc_template left join ms_lov msl on mdtsl.lov_sign_type = msl.id_lov left join ms_lov msl1 on mdtsl.lov_signer_type = msl1.id_lov left join ms_tenant mst on mdt.id_ms_tenant = mst.id_ms_tenant where mst.tenant_code = '" + GlobalVariable.Tenant + "' and mdt.doc_template_code = '" + docTemplate + "' and msl.description = 'Tanda tangan' and msl1.code = '" + signerType + "' limit 1")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data != null) {
			Integer.parseInt(data)
		} else {
			data = 0
		}
	}

	@Keyword
	getDocSignSequence(Connection conn, String docId, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT login_id, seq_no, is_sequence, document_id, sign_date FROM tr_document_d tdd JOIN tr_document_d_sign tdds ON tdds.id_document_d = tdd.id_document_d JOIN am_msuser amu ON amu.id_ms_user = tdds.id_ms_user WHERE document_id = '" + docId + "' ORDER BY seq_no ASC")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}

		ArrayList<String> resultList = []

		'hardcode untuk direct langsung ke colm is_sequence'
		if (listdata[2] == '0') {
			'hardcode untuk direct langsung ke colm document_id'
			resultList.add(listdata[3])
			resultList.add('0')
		} else if (listdata[2] == '1') {
			'hardcode untuk direct langsung ke colm document_id'
			resultList.add(listdata[3])

			int index = listdata.indexOf(email) + 1

			if (index - 5 >= 0) {
				if (listdata[index - 2] != null) {
					resultList.add('0')
				} else {
					resultList.add('2')
				}
			} else if (index - 5 <= 0) {
				if (listdata[index + 3] != null) {
					resultList.add('2')
				} else {
					resultList.add('0')
				}
			}
		}

		resultList
	}

	@Keyword
	getEmailBasedOnSequence(Connection conn, String value) {
		stm = conn.createStatement()
		helperResult = stm.executeQuery("select tdd.is_sequence from tr_Document_d tdd left join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h WHERE tdd.document_id = '" + value + "' OR tdh.ref_number = '" + value + "'")
		metadata = helperResult.metaData

		columnCount = metadata.getColumnCount()

		while (helperResult.next()) {
			data = helperResult.getObject(1)
		}

		if (data == '1') {
			helperQuery = 'tdds.seq_no'
		} else if (data == '0') {
			helperQuery = 'tdds.id_document_d_sign'
		}

		helperResult = stm.executeQuery("select id_ms_doc_template from tr_Document_d tdd left join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h WHERE tdd.document_id = '" + value + "' OR tdh.ref_number = '" + value + "'")
		metadata = helperResult.metaData

		columnCount = metadata.getColumnCount()

		while (helperResult.next()) {
			data = helperResult.getObject(1)
		}

		if (data != 'null') {
			if (helperQuery != 'tdds.id_document_d_sign') {
				helperQuery = 'tdds.seq_no'
			}
		} else if (data == 'null') {
			if (helperQuery != 'tdds.seq_no') {
				helperQuery = 'tdds.id_document_d_sign'
			}
		}

		resultSet = stm.executeQuery("SELECT STRING_AGG(login_id, ';' ORDER BY seq_no) AS aa FROM (SELECT DISTINCT tdds.id_ms_user,au.login_id, FIRST_VALUE(" + helperQuery + ") OVER (PARTITION BY tdds.id_ms_user ORDER BY tdds.seq_no) AS seq_no FROM tr_document_h AS tdh JOIN tr_document_d AS tdd ON tdh.id_document_h = tdd.id_document_h JOIN tr_document_d_sign AS tdds ON tdd.id_document_d = tdds.id_document_d JOIN am_msuser AS au ON au.id_ms_user = tdds.id_ms_user WHERE tdd.document_id = '" + value + "' OR tdh.ref_number = '" + value + "') AS alls;")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	settingBaseUrl(String excelPath, int colm, int row) {
		if (findTestData(excelPath).getValue(colm, row) == 'No') {
			GlobalVariable.base_url = findTestData('Login/Setting').getValue(7, 2) + 'BASEURLSALAH'
		} else {
			GlobalVariable.base_url = findTestData('Login/Setting').getValue(7, 2)
		}
	}

	@Keyword
	getHashedNo(Connection conn, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select hashed_phone from am_msuser where login_id = '" + emailSigner + "' limit 1")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getAPIRegisterPrivyStoreDB(Connection conn, String trxNo) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT request_status, is_external FROM tr_balance_mutation tbm JOIN tr_job_check_register_status tjc ON tbm.id_balance_mutation = tjc.id_balance_mutation WHERE trx_no = '" + trxNo + "'")

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
	getInvitationCode(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select invitation_code from tr_invitation_link inv JOIN ms_vendor mv ON inv.id_ms_vendor = mv.id_ms_vendor where  receiver_detail = '" + value + "' or phone = '" + value + "' and vendor_code = '" + GlobalVariable.Psre + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	settingFlagNeedPassword(Connection conn, String value) {
		stm = conn.createStatement()

		updateVariable = stm.executeUpdate("UPDATE ms_tenant SET need_password_for_signing = '" + value + "' WHERE tenant_code = '" + GlobalVariable.Tenant + "' ")
	}

	@Keyword
	getAesKeyEncryptUrl(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select gs_value from am_generalsetting where gs_code = 'AES_KEY'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getCheckInvRegisStoreDB(Connection conn, String value) {
		stm = conn.createStatement()

		if (GlobalVariable.Psre == 'PRIVY') {
			//			resultSet = stm.executeQuery("SELECT CASE WHEN login_id is null then '0' else '1' END, CASE WHEN login_id is null then '0' else '1' END, case when request_status = '0' or '1' then '1' else '2' end, notes FROM tr_balance_mutation tbm JOIN tr_job_check_register_status tjc ON tbm.id_balance_mutation = tjc.id_balance_mutation LEFT JOIN am_msuser amu ON tbm.id_ms_user = amu.id_ms_user WHERE amu.login_id = '" + value + "' or tbm.usr_crt = '" + value + "'")
			//
			resultSet = stm.executeQuery(" SELECT CASE WHEN mvru.is_active is null then '0' else mvru.is_active END, CASE WHEN request_status = 3 then '1' else '0' END, request_status, notes, is_external FROM tr_job_check_register_status tjc JOIN tr_balance_mutation tbm ON tbm.id_balance_mutation = tjc.id_balance_mutation LEFT JOIN am_msuser amu ON tjc.hashed_id_no = amu.hashed_id_no LEFT JOIN ms_vendor_registered_user mvru ON mvru.id_ms_user = amu.id_ms_user WHERE amu.login_id = '" + value + "'")
		} else {
			resultSet = stm.executeQuery("select mvru.is_active, is_registered, '0', '', CASE WHEN is_external_activation is null THEN '0' ELSE is_external_activation END from ms_vendor_registered_user mvru join ms_vendor mv on mvru.id_ms_vendor = mv.id_ms_vendor where signer_registered_email = '" + value + "' or hashed_signer_registered_phone = encode(sha256('" + value + "'), 'hex') and mv.vendor_code = '" + GlobalVariable.Psre + "'")
		}

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				if (data == null) {
					listdata.add('0')
				} else {
					listdata.add(data)
				}
			}
		}
		listdata
	}

	@Keyword
	settingAllowRegenerateLink(Connection conn, String value) {
		stm = conn.createStatement()

		updateVariable = stm.executeUpdate("WITH rows_to_update AS ( SELECT mvt.id_ms_vendoroftenant FROM ms_vendoroftenant mvt JOIN ms_tenant mt ON mvt.id_ms_tenant = mt.id_ms_tenant JOIN ms_vendor mv ON mv.id_ms_vendor = mvt.id_ms_vendor WHERE mt.tenant_code = '" + GlobalVariable.Tenant + "' AND mv.vendor_code = '" + GlobalVariable.Psre + "' ) UPDATE ms_vendoroftenant mvt SET allow_regenerate_inv_link = " + value + " FROM rows_to_update rtu WHERE mvt.id_ms_vendoroftenant = rtu.id_ms_vendoroftenant")
	}

	@Keyword
	settingLinkIsActive(Connection conn, String value, String email) {
		stm = conn.createStatement()

		updateVariable = stm.executeUpdate("UPDATE tr_invitation_link SET is_active = '" + value + "' WHERE receiver_detail = '" + email + "'")
	}

	@Keyword
	settingRegisterasDukcapilCheck(Connection conn, String value) {
		stm = conn.createStatement()

		updateVariable = stm.executeUpdate("UPDATE ms_tenant SET register_as_dukcapil_check = " + value + " WHERE tenant_code = '" + GlobalVariable.Tenant + "'")
	}

	@Keyword
	settingOTPActiveDuration(Connection conn, String value) {
		stm = conn.createStatement()

		updateVariable = stm.executeUpdate("UPDATE ms_tenant SET otp_active_duration = " + value + " WHERE tenant_code = '" + GlobalVariable.Tenant + "'")
	}

	@Keyword
	settingEmailServiceUser(Connection conn, String value, String idKtp) {
		stm = conn.createStatement()

		updateVariable = stm.executeUpdate("UPDATE ms_vendor_registered_user SET email_service = '" + value + "' WHERE id_ms_user IN (SELECT mvr.id_ms_user FROM ms_vendor_registered_user mvr JOIN am_msuser am ON mvr.id_ms_user = am.id_ms_user WHERE am.hashed_id_no = encode(sha256('" + idKtp + "'), 'hex'))")
	}

	@Keyword
	checkAPIGetActLinkStoreDB(Connection conn, String idno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mvru.is_active, mvru.is_registered from ms_vendor_registered_user mvru JOIN ms_vendor mv ON mv.id_ms_vendor = mvru.id_ms_vendor JOIN am_msuser am ON am.id_ms_user = mvru.id_ms_user where vendor_code = '" + GlobalVariable.Psre + "' AND am.hashed_id_no = encode(sha256('" + idno + "'), 'hex')")

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
	getCountTtdLocation(Connection conn, String refnumber, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(tdds.id_Document_d_sign) from tr_document_d_sign tdds LEFT JOIN am_msuser amm on tdds.id_ms_user = amm.id_ms_user LEFT JOIN tr_document_d tdd on tdds.id_Document_d = tdd.id_Document_d LEFT JOIN tr_Document_h tdh on tdh.id_Document_h = tdd.id_document_h WHERE tdh.ref_number = '" + refnumber + "' AND amm.login_id = '" + emailSigner + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getUserAlreadySigned(Connection conn, String refnumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(distinct(tdds.id_ms_user)) from tr_document_d_sign tdds LEFT JOIN am_msuser amm on tdds.id_ms_user = amm.id_ms_user LEFT JOIN tr_document_d tdd on tdds.id_Document_d = tdd.id_Document_d LEFT JOIN tr_Document_h tdh on tdh.id_Document_h = tdd.id_document_h WHERE tdh.ref_number = '" + refnumber + "' AND tdds.sign_date IS NOT NULL")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getTrxNoAPISign(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select STRING_AGG(tbm.trx_no, ';') from tr_balance_mutation tbm LEFT JOIN tr_document_d tdd on tbm.id_document_d = tdd.id_document_d LEFT JOIN ms_lov msl on tbm.lov_trx_type = msl.id_lov LEFT JOIN am_msuser amm on tbm.id_ms_user = amm.id_ms_user where tdd.document_id = '" + documentId + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getTotalSign(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select total_sign from tr_document_d where document_id = '" + documentId + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data != null) {
			Integer.parseInt(data)
		} else {
			data = 0
		}
		Integer.parseInt(data)
	}

	@Keyword
	getDefaultVendor(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select vendor_code from ms_vendoroftenant mvo join ms_tenant mt on mvo.id_ms_tenant = mt.id_ms_tenant join ms_vendor mv on mvo.id_ms_vendor = mv.id_ms_vendor where tenant_code = '" + tenantCode + "' order by default_vendor ASC limit 1")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getRegisteredVendor(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select vendor_code from ms_vendor_registered_user mvr join ms_vendor mv on mv.id_ms_vendor = mvr.id_ms_vendor where signer_registered_email = '" + email.toUpperCase() + "' order by id_ms_vendor_registered_user desc limit 1")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getTotalAutosignOnDocument(Connection conn, String documentId) {
		String data

		stm = conn.createStatement()
		resultSet = stm.executeQuery("SELECT count(DISTINCT(amm.login_id)) FROM tr_document_d tdd LEFT JOIN tr_document_d_sign tdds on tdds.id_document_d = tdd.id_document_d LEFT JOIN ms_lov msl on tdds.lov_autosign = msl.id_lov LEFT JOIN am_msuser amm on tdds.id_ms_user = amm.id_ms_user WHERE tdd.document_id = '" + documentId + "' AND msl.description = 'Autosign'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data != null) {
			Integer.parseInt(data)
		} else {
			data = 0
		}
		Integer.parseInt(data)
	}

	@Keyword
	getIfSignerAutosign(Connection conn, String documentId, String emailSigner) {
		stm = conn.createStatement()
		resultSet = stm.executeQuery("select msl.description from tr_Document_d tdd left join tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d left join am_msuser amm on amm.id_ms_user = tdds.id_ms_user left join ms_lov msl on  tdds.lov_autosign = msl.id_lov where tdd.document_id = '" + documentId + "' AND amm.login_id = '" + emailSigner + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	gettrxSaldoForMeteraiPrivy(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tbm.trx_no, mso.office_name, TO_CHAR(tbm.dtm_crt,'YYYY-MM-DD HH24:MI:SS'), ml.description , case when amm.full_name != '' or amm.full_name != null then amm.full_name else tbm.usr_crt end, case when amm_two.full_name != '' or amm_two.full_name != null then tdh.ref_number||'('||amm_two.full_name||')' else tdh.ref_number end, ml_doc_h.code, case when mdt.doc_template_name != null OR mdt.doc_template_name IS NOT null then mdt.doc_template_name else tdd.document_name end, tbm.notes, tbm.qty FROM tr_balance_mutation tbm left join ms_office mso on tbm.id_ms_office = mso.id_ms_office join tr_document_h tdh on tbm.id_document_h = tdh.id_document_h left join ms_lov as ml on tbm.lov_trx_type = ml.id_lov join tr_document_d tdd on tdd.id_document_h = tdh.id_document_h left join am_msuser as amm on tdh.id_msuser_customer = amm.id_ms_user join ms_lov as ml_doc_h on tdh.lov_doc_type = ml_doc_h.id_lov left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join am_msuser as amm_two on tdh.id_msuser_customer = amm_two.id_ms_user WHERE ref_number = '" + refNumber + "' AND ml.description = 'Use Stamp Duty' ORDER BY tbm.dtm_crt DESC")
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
	getSignersAutosignOnDocument(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT DISTINCT(amm.login_id) FROM tr_document_d tdd LEFT JOIN tr_document_d_sign tdds on tdds.id_document_d = tdd.id_document_d LEFT JOIN ms_lov msl on tdds.lov_autosign = msl.id_lov LEFT JOIN am_msuser amm on tdds.id_ms_user = amm.id_ms_user WHERE tdd.document_id = '" + documentId + "' AND msl.description = 'Autosign'")
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
	settingMustUseWAFirst(Connection conn, String value) {
		stm = conn.createStatement()
		if (value != '') {
			updateVariable = stm.executeUpdate("UPDATE ms_tenant SET must_use_wa_first = " + value + " WHERE tenant_code = '" + GlobalVariable.Tenant + "'")
		}
	}

	@Keyword
	settingUseWAMessage(Connection conn, String value) {
		stm = conn.createStatement()

		if (value != '') {
			updateVariable = stm.executeUpdate("UPDATE ms_tenant SET use_wa_message = " + value + " WHERE tenant_code = '" + GlobalVariable.Tenant + "'")
		}
	}

	@Keyword
	getDataInvRegist(Connection conn, String tenant, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tril.provinsi, tril.kota, tril.kecamatan, tril.email, tril.is_active, tril.invitation_by, tril.receiver_detail, TO_CHAR(tril.dtm_upd, 'DD-Mon-YYYY HH24:MI:SS') AS formatted_timestamp, mv.resend_activation_link, mv.vendor_name, mv.vendor_code, mv.edit_after_register, mvot.allow_regenerate_inv_link, tril.kelurahan, tril.zip_code, tril.full_name, tril.address, tril.gender, tril.phone, tril.place_of_birth, TO_CHAR(tril.date_of_birth, 'YYYY-Mon-DD') AS formatted_date, tril.id_no from tr_invitation_link as tril join ms_tenant as mst on tril.id_ms_tenant = mst.id_ms_tenant left join ms_vendor mv ON mv.id_ms_vendor = tril.id_ms_vendor left join ms_vendoroftenant mvot ON mvot.id_ms_vendor = tril.id_ms_vendor AND mvot.id_ms_tenant = tril.id_ms_tenant where tril.is_active = '1' and mst.tenant_code = '" + tenant + "' and tril.email = '" + email + "'")

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
	getInvRegisterData(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tril.provinsi, tril.kota, tril.kecamatan, tril.email, tril.kelurahan, tril.zip_code, tril.full_name, tril.address, tril.gender, tril.phone, tril.place_of_birth, tril.date_of_birth, tril.id_no, mst.tenant_code, mv.vendor_code, mv.vendor_name, mv.verif_phone from tr_invitation_link as tril join ms_tenant as mst on tril.id_ms_tenant = mst.id_ms_tenant left join ms_vendor mv ON mv.id_ms_vendor = tril.id_ms_vendor where tril.is_active = '1' and tril.email = '" + email + "'")

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
	getCountInvCodeonDB(Connection conn, String decrypted) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT COUNT(invitation_code) FROM tr_invitation_link WHERE invitation_code = '" + decrypted + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data != null) {
			Integer.parseInt(data)
		} else {
			data = 0
		}
		Integer.parseInt(data)
	}
	@Keyword
	getTemplateSignloc(Connection conn, String doctemplatecode, String tenant, String signertypeCode) {
		String commandCode = '', queryActive = ''

		if (signertypeCode == '') {
			commandCode = '--'
		} else if (signertypeCode == 'SDT') {
			queryActive = ' AND mlo.code'
		} else {
			queryActive = ' AND ml.code'
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT CASE WHEN mlo.description = 'Stamp Duty (materai)' THEN 'Stamp Duty' ELSE ml.description END as signer_type, mlo.description, sign_page, jsonb_build_object( 'llx', round((sign_location::jsonb->>'llx')::numeric), 'lly', round((sign_location::jsonb->>'lly')::numeric), 'urx', round((sign_location::jsonb->>'urx')::numeric), 'ury', round((sign_location::jsonb->>'ury')::numeric)) AS digiSignLoc, jsonb_build_object( 'llx', round((sign_location::jsonb->>'llx')::numeric), 'lly', round((sign_location::jsonb->>'lly')::numeric), 'urx', round((sign_location::jsonb->>'urx')::numeric), 'ury', round((sign_location::jsonb->>'ury')::numeric) ) AS tknajasignLoc FROM ms_doc_template_sign_loc mdts JOIN ms_doc_template mdt ON mdt.id_doc_template = mdts.id_doc_template LEFT JOIN ms_lov ml ON mdts.lov_signer_type = ml.id_lov JOIN ms_lov mlo ON mdts.lov_sign_type = mlo.id_lov LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mdt.id_ms_tenant WHERE doc_template_code = '" + doctemplatecode + "' AND tenant_code = '" + tenant + "'" + '\n' +
				commandCode + queryActive + "= '" + signertypeCode + "'")

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
	getTemplateSignlocDetail(Connection conn, String doctemplatecode, String tenant, String signertypeCode) {
		String commandCode = '', queryActive = ''

		if (signertypeCode == '') {
			commandCode = '--'
		} else if (signertypeCode == 'SDT') {
			queryActive = ' AND mlo.code'
		} else {
			queryActive = ' AND ml.code'
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT jsonb_build_object( 'x', round((vida_sign_location::jsonb->>'x')::numeric), 'y', round((vida_sign_location::jsonb->>'y')::numeric), 'h', round((vida_sign_location::jsonb->>'h')::numeric), 'w', round((vida_sign_location::jsonb->>'w')::numeric))::text::jsonb AS vidasignLoc, jsonb_build_object( 'x', round((privy_sign_location::jsonb->>'x')::numeric), 'y', round((privy_sign_location::jsonb->>'y')::numeric), 'h', round((privy_sign_location::jsonb->>'h')::numeric), 'w', round((privy_sign_location::jsonb->>'w')::numeric) )::text::jsonb AS privysignLoc FROM ms_doc_template_sign_loc mdts JOIN ms_doc_template mdt ON mdt.id_doc_template = mdts.id_doc_template LEFT JOIN ms_lov ml ON mdts.lov_signer_type = ml.id_lov LEFT JOIN ms_lov mlo ON mdts.lov_sign_type = mlo.id_lov  LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mdt.id_ms_tenant WHERE doc_template_code = '" + doctemplatecode + "' AND tenant_code = '" + tenant + "'" + '\n' +
				commandCode + queryActive + " = '" + signertypeCode + "'")

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
	getCountSignLoc(Connection conn, String doctemplatecode, String tenant, String signertypeCode) {
		String commandCode = '', queryActive = ''

		if (signertypeCode == '') {
			commandCode = '--'
		} else if (signertypeCode == 'SDT') {
			queryActive = ' AND mlo.code'
		} else {
			queryActive = ' AND ml.code'
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT count(id_ms_doc_template_sign_loc) FROM ms_doc_template_sign_loc mdts JOIN ms_doc_template mdt ON mdt.id_doc_template = mdts.id_doc_template LEFT JOIN ms_lov ml ON mdts.lov_signer_type = ml.id_lov LEFT JOIN ms_lov mlo ON mdts.lov_sign_type = mlo.id_lov LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mdt.id_ms_tenant WHERE doc_template_code = '" + doctemplatecode + "' AND tenant_code = '" + tenant + "'" + '\n' +
				commandCode + queryActive + " = '" + signertypeCode + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data != null) {
			Integer.parseInt(data)
		} else {
			data = 0
		}
		Integer.parseInt(data)
	}
	@Keyword
	getTenantSettingAPIOnly(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tenant_code, tenant_name, mst.ref_number_label, mst.activation_callback_url, mst.use_wa_message, mst.threshold_balance, mst.email_reminder_dest FROM ms_tenant mst WHERE tenant_code = '" + GlobalVariable.Tenant + "'")

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
	settingSendSMSGenInv(Connection conn, String value) {
		stm = conn.createStatement()
		if (value != '') {
			updateVariable = stm.executeUpdate("UPDATE am_generalsetting SET gs_value = " + value + " WHERE gs_code = 'SEND_SMS_GENINV'")
		}
	}

	@Keyword
	getUpdateActivationUserAPIOnly(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT amu.full_name, amu.hashed_id_no, mvru.hashed_signer_registered_phone, signer_registered_email, TO_CHAR(mvru.dtm_crt::timestamp, 'DD FMMonth YYYY HH24:MI:SS') AS formatted_date FROM am_msuser amu LEFT JOIN ms_vendor_registered_user mvru ON mvru.id_ms_user = amu.id_ms_user WHERE login_id = '" + value + "' OR hashed_phone = encode(sha256('" + value + "'), 'hex')")

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
	getSaldoTrx(Connection conn, String value, String desc) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT qty FROM tr_balance_mutation tbm JOIN ms_lov ml ON ml.id_lov = tbm.lov_trx_type LEFT JOIN am_msuser amu ON amu.id_ms_user = tbm.id_ms_user WHERE description = 'Use " + desc + "' AND tbm.usr_crt = '" + value.toUpperCase() + "' ORDER BY id_balance_mutation DESC LIMIT 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getVendorofTenant(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msv.vendor_code, msv.vendor_name from ms_vendoroftenant mvot left join ms_tenant mst on mvot.id_ms_tenant = mst.id_ms_tenant left join ms_vendor msv on mvot.id_ms_vendor = msv.id_ms_vendor where mst.tenant_code = '" + GlobalVariable.Tenant + "' and msv.is_active = '1' and msv.is_operating = '1' and mvot.default_vendor > '0' order by mvot.id_ms_vendoroftenant desc")
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
	getLovGroup(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select code, description, sequence from ms_lov where lov_group = '" + value.toUpperCase() + "' and is_active = '1'")
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
	getRegionList(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select region_code, region_name from ms_region mr join ms_tenant mt on mr.id_ms_tenant = mt.id_ms_tenant where tenant_code = '" + GlobalVariable.Tenant + "'")
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
	getOfficeList(Connection conn, String region) {
		String commandRegion = ''

		if (region == '') {
			commandRegion = '--'
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select office_code, office_name from ms_office mo LEFT join ms_tenant mt on mo.id_ms_tenant = mt.id_ms_tenant LEFT JOIN ms_region mr ON mr.id_ms_region = mo.id_ms_region where tenant_code = '" + GlobalVariable.Tenant + "'" + '\n' +
				commandRegion + " AND region_code = '" + region + "'")

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
	getViewSigner(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select CASE WHEN ml1.description is null then 'Signer' else ml1.description END, am.full_name, am.hashed_phone, am.login_id, case when to_char(tdds.sign_date, 'yyyy-mm-dd hh24:mi:ss') is not null then 'Signed' else 'Need Sign' end, case when to_char(tdds.sign_date, 'yyyy-mm-dd hh24:mi:ss') is not null then to_char(tdds.sign_date, 'yyyy-mm-dd hh24:mi:ss') else '' end , case when am.is_active = '1' then 'Sudah Aktivasi' else 'Belum Aktivasi' end from tr_document_d_sign tdds join tr_document_d tdd on tdd.id_document_d = tdds.id_document_d left join ms_lov ml1 on tdds.lov_signer_type = ml1.id_lov join am_msuser am on am.id_ms_user = tdds.id_ms_user where document_id = '" + documentId + "' group by id_ms_tenant, document_id, ml1.description, am.full_name, am.hashed_phone, am.login_id, to_char(tdds.sign_date, 'yyyy-mm-dd hh24:mi:ss'), am.is_active, tdds.seq_no order by tdds.seq_no asc")

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
	getSignerDetailAPIOnly(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT mvru.hashed_signer_registered_phone, signer_registered_email, CASE WHEN amu.liveness_facecompare_request_num is null THEN 0 ELSE amu.liveness_facecompare_request_num END as maxFacecomprequestNum FROM am_msuser amu LEFT JOIN ms_vendor_registered_user mvru ON mvru.id_ms_user = amu.id_ms_user WHERE login_id = '" + value + "' OR hashed_phone = encode(sha256('" + value + "'), 'hex')")

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
	getLivenessFCTenantStatAPIOnly(Connection conn, String tenantcode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT liveness_facecompare_services, use_liveness_facecompare_first FROM ms_tenant WHERE tenant_code = '" + tenantcode + "'")

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
	getTenantCodeFromUser(Connection conn, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mst.tenant_code from ms_useroftenant muot left join ms_tenant mst on muot.id_ms_tenant = mst.id_ms_tenant left join am_msuser amm on amm.id_ms_user = muot.id_ms_user where amm.login_id = '" + emailSigner + "' order by muot.dtm_upd desc limit 1")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	convertRegionOfficetoCode(Connection conn, String officeName, String regionName, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT COALESCE(office_code,'') FROM ms_office mso LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mso.id_ms_tenant WHERE office_name = '" + officeName + "' AND tenant_code = '" + tenantCode + "' LIMIT 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}

		resultSet = stm.executeQuery("SELECT COALESCE(region_code,'') FROM ms_region mro LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mro.id_ms_tenant WHERE region_name = '" + regionName + "' AND tenant_code = '" + tenantCode + "' LIMIT 1")

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
	getProfileAPIOnly(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT mt.tenant_name, mro.role_code, mro.role_name, mt.tenant_code FROM am_msuser amu LEFT JOIN ms_useroftenant mot ON mot.id_ms_user = amu.id_ms_user LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mot.id_ms_tenant LEFT JOIN am_memberofrole mor ON mor.id_ms_user = amu.id_ms_user LEFT JOIN am_msrole mro ON mro.id_ms_role = mor.id_ms_role WHERE amu.login_id = '" + email + "' ORDER BY mro.id_ms_role DESC")

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
	getAutoStampAfterSignAPIOnly(Connection conn, String tenant) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT CASE WHEN automatic_stamping_after_sign is null OR automatic_stamping_after_sign = '0' THEN 'false' WHEN automatic_stamping_after_sign = '1' THEN 'true' END FROM ms_tenant WHERE tenant_code = '" + tenant + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
	@Keyword
	getAutoStampTenantAPIOnly(Connection conn, String tenant) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT automatic_stamping_after_sign FROM ms_tenant WHERE tenant_code = '" + tenant + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
	@Keyword
	downloadReportCountAPIOnly(Connection conn, String tenant) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT count(*) FROM tr_manual_report mr LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mr.id_ms_tenant WHERE mt.tenant_code = '" + tenant + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
	@Keyword
	getListPaymentTypeAPIOnly(Connection conn, String tenant, String vendor) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT mlov.code, mlov.description FROM ms_paymentsigntypeoftenant mspot LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mspot.id_ms_tenant LEFT JOIN ms_vendor mv ON mv.id_ms_vendor = mspot.id_ms_vendor LEFT JOIN ms_lov mlov ON mlov.id_lov = mspot.lov_payment_sign_type WHERE vendor_code = '" + vendor + "' AND tenant_code = '" + tenant + "'")

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
	getStatusEmailServiceAPIOnly(Connection conn, String tenant) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tenant_code, tenant_name, email_service FROM ms_tenant WHERE tenant_code = '" + tenant + "'")

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
	getCheckRegistAutoFillAPIOnly(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT full_name, login_id, hashed_phone FROM am_msuser WHERE login_id = '" + email + "'")

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
	getPsrePriorityAPIOnly(Connection conn, String tenant) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT mv.vendor_code, mv.vendor_name, default_vendor FROM ms_vendoroftenant vot LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = vot.id_ms_tenant LEFT JOIN ms_vendor mv ON mv.id_ms_vendor = vot.id_ms_vendor WHERE tenant_code = '" + tenant + "' AND lov_vendor_type != 160 ORDER BY default_vendor ASC")

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
	getIDManualReportAPIOnly(Connection conn, String filename) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT id_manual_report FROM tr_manual_report WHERE filename = '" + filename + "' ORDER BY report_date LIMIT 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getPaymentSignTypeAPI(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select DISTINCT msl.code, msl.description from ms_paymentsigntypeoftenant mpst left join ms_tenant mst on mpst.id_ms_tenant = mst.id_ms_tenant left join ms_lov msl on mpst.lov_payment_sign_type = msl.id_lov where mst.tenant_code = '" + tenantCode + "'")
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
	getListMessageDeliveryAPIOnly(Connection conn, String tenant, String vendor, String messageMedia, String dateStart, String dateEnd, String deliveryStatus, String recipient) {
		String commandRec = '', commandReport = '', commandMedia = '', commandVendor = '', commandDelivStat = ''

		stm = conn.createStatement()

		if (recipient == '') {
			commandRec = '--'
		}
		if (dateStart == '' || dateEnd == '') {
			commandReport = '--'
		}
		if (messageMedia == '') {
			commandMedia = '--'
		}
		if (vendor == '') {
			commandVendor = '--'
		}
		if (deliveryStatus == '') {
			commandDelivStat = '--'
		}
		resultSet = stm.executeQuery("SELECT vendor_name, mdr.report_time, recipient_detail, trx_no, description, delivery_status, CASE WHEN delivery_status = '0' THEN 'Not Started' WHEN delivery_status = '1' THEN 'Waiting' WHEN delivery_status = '2' THEN 'Failed' WHEN delivery_status = '3' THEN 'Delivered' WHEN delivery_status = '4' THEN 'Read' END as delivery_status_information FROM tr_message_delivery_report mdr LEFT JOIN ms_tenant mt ON mdr.id_ms_tenant = mt.id_ms_tenant LEFT JOIN ms_vendor mv ON mv.id_ms_vendor = mdr.id_ms_vendor LEFT JOin ms_lov mlov ON mlov.id_lov = mdr.lov_message_media WHERE tenant_code = '" + tenant + "'" + '\n' +
				commandRec + " AND(recipient_detail = '" + recipient + "') " + '\n' +
				commandReport + " AND(mdr.report_time BETWEEN '" + dateStart + " 00:00:00.000' AND '" + dateEnd + " 23:59:59.999') " + '\n' +
				commandMedia + " AND (mlov.description = '" + messageMedia + "') " + '\n' +
				commandVendor + " AND (vendor_code = '" + vendor + "') " + '\n' +
				commandDelivStat + " AND (mdr.delivery_status = '" + deliveryStatus + "') " + '\n' +
				"ORDER BY report_time DESC ")

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
	listDocTemplateAPIOnly(Connection conn, String tenant, String docTempCode, String docTempName, String isActive) {
		String commandCode = '', commandName = '', commandisActive = ''

		if (docTempCode == '') {
			commandCode = '--'
		}
		if (docTempName == '') {
			commandName = '--'
		}
		if (isActive == '') {
			commandisActive = '--'
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT count(*) FROM ms_doc_template mdt LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mdt.id_ms_tenant WHERE tenant_code = '" + tenant + "'" + '\n' +
				commandCode + " and mdt.doc_template_code = '" + docTempCode + "'" + '\n' +
				commandName + " AND mdt.doc_template_name = '" + docTempName + "'" + '\n' +
				commandisActive + " AND mdt.is_active = '" + isActive + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
	@Keyword
	getResetOtpCodeAPIOnly(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT reset_code_request_num FROM am_msuser where login_id = '" + email + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
	@Keyword
	businessLineAPIOnly(Connection conn, String tenant) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT count(*) FROM ms_business_line mbl LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = mbl.id_ms_tenant WHERE tenant_code = '" + tenant + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data != null) {
			Integer.parseInt(data)
		} else {
			data = 0
		}

		Integer.parseInt(data)
	}
	@Keyword
	listTenantAPIOnly(Connection conn, String tenantName, String isActive) {
		String commandName = '', commandisActive = '', commandWhere = 'AND'

		if (tenantName == '') {
			commandName = '--'
			commandWhere = 'WHERE'
		}
		if (isActive == '') {
			commandisActive = '--'
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT count(*) from ms_tenant " + '\n' +
				commandName + " WHERE tenant_name LIKE '" + tenantName + "%'" + '\n' +
				commandisActive + commandWhere + " is_active = '" + isActive + "'")

		metadata = resultSet.metaData

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data != null) {
			Integer.parseInt(data)
		} else {
			data = 0
		}

		Integer.parseInt(data)
	}

	@Keyword
	listPsreSettingAPIOnly(Connection conn, String vendorName, String vendorCode, String isActive, String isOperating) {
		String commandName = '', commandisActive = '', commandCode = '', commandisOperating = ''

		if (vendorName == '') {
			commandName = '--'
		}
		if (vendorCode == '') {
			commandCode = '--'
		}
		if (isActive == '') {
			commandisActive = '--'
		}
		if (isOperating == '') {
			commandisOperating = '--'
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT count(*) FROM ms_vendor mv LEFT JOIN ms_lov mlo ON mlo.id_lov = mv.lov_vendor_type WHERE mlo.code = 'PSRE'" + '\n' +
				commandName + " AND vendor_name LIKE '%" + vendorName + "%'" + '\n' +
				commandCode + " AND vendor_code LIKE '%" + vendorCode + "%'" + '\n' +
				commandisActive + " AND mv.is_active = '" + isActive + "'" + '\n' +
				commandisOperating + " AND mv.is_operating = '" + isOperating + "'")

		metadata = resultSet.metaData

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data != null) {
			Integer.parseInt(data)
		} else {
			data = 0
		}
		Integer.parseInt(data)
	}

	@Keyword
	getPsrePriorityAPIONLY(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT VENDOR_CODE FROM ms_vendoroftenant mvt JOIN ms_tenant mt ON mvt.id_ms_tenant = mt.id_ms_tenant JOIN ms_vendor mv ON mvt.id_ms_vendor = mv.id_ms_vendor join ms_lov ml on ml.id_lov = mv.lov_vendor_type WHERE mt.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.description = 'PSRE' AND default_vendor is not null ORDER BY mvt.default_vendor ASC")

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
	getTopUpAPIONLY(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tenant_code, vendor_code, ml.description, notes, qty, ref_no, To_char(trx_date, 'yyyy-MM-dd') from tr_balance_mutation tbm join ms_tenant mt on mt.id_ms_tenant = tbm.id_ms_tenant join ms_vendor mv on mv.id_ms_vendor = tbm.id_ms_vendor join ms_lov ml on ml.id_lov = tbm.lov_balance_type where ref_no = '" + value + "'")

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
	getUpdateVendorPsreAPIONLY(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select vendor_code, vendor_name, mv.is_active, is_operating, ml.code from ms_vendor mv join ms_lov ml on ml.id_lov = mv.lov_vendor_sign_payment_type where vendor_code = '" + GlobalVariable.Psre + "'")

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
	getProfileUserAPIONLY(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT full_name, login_id, mv.vendor_code FROM am_msuser amu LEFT JOIN ms_vendor_registered_user mvt ON mvt.id_ms_user = amu.id_ms_user LEFT JOIN ms_vendor mv ON mv.id_ms_vendor = mvt.id_ms_vendor WHERE amu.login_id = '" + email + "'")

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
	getBalanceTypeList(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT code, description FROM ms_lov WHERE lov_group = 'BALANCE_TYPE' AND is_active = '1'")

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
	getBalanceMutation(Connection conn, String balType, String startDate, String endDate, String officeCode, String blCode, String regCode) {
		stm = conn.createStatement()

		if (balType == 'SDT') {
			resultSet = stm.executeQuery("with bdc AS ( SELECT recap_date, recap_total_balance FROM tr_balance_daily_recap bdc WHERE 1 = 1 AND bdc.id_ms_tenant = (SELECT id_ms_tenant FROM ms_tenant WHERE tenant_code = '" + GlobalVariable.Tenant + "') AND bdc.lov_balance_type = (SELECT id_lov FROM ms_lov WHERE code = 'SDT' AND lov_group = 'BALANCE_TYPE') AND bdc.recap_date <= ( CAST('" + startDate + "' AS DATE) - INTERVAL '1 DAY' ) ORDER BY bdc.recap_date DESC limit 1 ) select * from ( select 'no:' || row_number() over ( order by bm.trx_date asc ) as no, CASE WHEN region.region_name is null THEN '' ELSE 'region:' || region.region_name END as regionName, CASE WHEN office.office_name is null THEN '' ELSE 'office:' || office.office_name END as officename, 'customerName:'|| COALESCE( userCust2.full_name, COALESCE(userCust.full_name, bm.usr_crt) ) as customerName, CASE WHEN bm.ref_no is null then '' ELSE 'refNo:' || bm.ref_no END as refNo, CASE WHEN businessLine.business_line_name is null then '' ELSE 'businessLine:' || businessLine.business_line_name END as businessLine, 'trxDate:' || TO_CHAR( bm.trx_date, 'YYYY-MM-DD HH24:MI:SS' ) as trxDate, 'balanceType:' || balancetype.description as balanceType, 'notes:' || bm.notes as notes, 'qty:' || CAST(bm.qty AS VARCHAR), 'trxNo:' || bm.trx_no as trxNo, CASE WHEN bm.vendor_trx_no is null then '' ELSE 'vendorTrxNo:' || bm.vendor_trx_no END as vendorTrxNo, 'documentType:' || mlovDocType.description as documentType, 'documentName:' || case when docd.id_ms_doc_template is null then docd.document_name else dt.doc_template_name end as documentName, 'requestDate:' || TO_CHAR( docd.request_date, 'YYYY-MM-DD HH24:MI:SS' ) as requestDate, 'signCompleteDate:' || TO_CHAR( docd.completed_date, 'YYYY-MM-DD HH24:MI:SS' ) as signCompleteDate, 'signProcess:' || case when docd.total_sign is not null then CONCAT( docd.total_signed, '/', docd.total_sign ) else null end as signProcess, 'documentSignStatus:' || mlovSignStatus.description as documentSignStatus, 'stampingProcess:' || CASE WHEN doch.proses_materai is null then null WHEN doch.proses_materai IN (1, 51, 61, 521) THEN 'Failed' WHEN doch.proses_materai IN (3, 53, 523, 63) THEN 'Success' WHEN doch.proses_materai IN (2, 5, 55, 52, 62, 65, 525, 522) THEN 'In Progress' ELSE 'Not Started' END as stampingProcess from tr_balance_mutation bm join bdc ON 1 = 1 join ms_lov balancetype ON balancetype.id_lov = bm.lov_balance_type left join am_msuser u ON u.login_id = bm.usr_crt left join tr_document_d docd ON docd.id_document_d = bm.id_document_d AND docd.id_ms_tenant = (SELECT id_ms_tenant FROM ms_tenant WHERE tenant_code = '" + GlobalVariable.Tenant + "') and 'SDT' in ('SDT', 'SDT_POSTPAID') left join tr_document_h doch ON doch.id_document_h = bm.id_document_h AND doch.id_ms_tenant = (SELECT id_ms_tenant FROM ms_tenant WHERE tenant_code = '" + GlobalVariable.Tenant + "') and 'SDT' in ('SDT', 'SDT_POSTPAID') left join ms_doc_template AS dt ON dt.id_doc_template = docd.id_ms_doc_template and 'SDT' in ('SDT', 'SDT_POSTPAID') left join ms_lov AS mlovDocType ON mlovDocType.id_lov = doch.lov_doc_type and 'SDT' in ('SDT', 'SDT_POSTPAID') left join ms_lov AS mlovSignStatus ON mlovSignStatus.id_lov = docd.lov_sign_status and 'SDT' in ('SDT', 'SDT_POSTPAID') left join am_msuser AS userCust ON userCust.id_ms_user = doch.id_msuser_customer left join am_msuser AS userCust2 ON userCust2.id_ms_user = bm.id_ms_user left join ms_office office on bm.id_ms_office = office.id_ms_office left join ms_region AS region on region.id_ms_region = office.id_ms_region left join ms_business_line businessLine ON businessLine.id_ms_business_line = bm.id_ms_business_line where DATE(bm.trx_date) > bdc.recap_date AND bm.id_ms_tenant = (SELECT id_ms_tenant FROM ms_tenant WHERE tenant_code = '" + GlobalVariable.Tenant + "') AND bm.lov_balance_type = (SELECT id_lov FROM ms_lov WHERE code = 'SDT' AND lov_group = 'BALANCE_TYPE') and DATE(bm.trx_date) >= '" + startDate + "' and DATE(bm.trx_date) <= '" + endDate + "' order by bm.trx_date, bm.trx_no ) as result")
		} else {
			resultSet = stm.executeQuery("with bdc AS ( SELECT recap_date, recap_total_balance FROM tr_balance_daily_recap bdc WHERE 1 = 1 AND bdc.id_ms_tenant = (SELECT id_ms_tenant FROM ms_tenant WHERE tenant_code = '" + GlobalVariable.Tenant + "') AND bdc.lov_balance_type = (SELECT id_lov FROM ms_lov WHERE code = '" + balType + "' AND lov_group = 'BALANCE_TYPE') AND bdc.recap_date <= ( CAST('" + startDate + "' AS DATE) - INTERVAL '1 DAY' ) ORDER BY bdc.recap_date DESC limit 1 ) select * from ( SELECT 'no:' || row_number() over ( order by tbm.trx_date asc ) as no, CASE WHEN region_name is null THEN '' ELSE 'region:' || region_name END as regionName, CASE WHEN office_name is null THEN '' ELSE 'office:' || office_name END as officename, 'customerName:' || COALESCE( am.full_name, COALESCE(am.full_name, tbm.usr_crt) ) as customerName, CASE WHEN mbl.business_line_name is null THEN '' ELSE 'businessLine:' || COALESCE(mbl.business_line_name, '')END as businessLine, 'trxDate:' || TO_CHAR( tbm.trx_date, 'YYYY-MM-DD HH24:MI:SS' ) as trxDate, 'balanceType:' || balancetype.description as balanceType, 'notes:' || tbm.notes as notes, 'qty:' || CAST(tbm.qty AS VARCHAR), 'trxNo:' || tbm.trx_no as trxNo, CASE WHEN tbm.vendor_trx_no is null then '' ELSE 'vendorTrxNo:' || tbm.vendor_trx_no END as vendorTrxNo FROM tr_balance_mutation tbm LEFT JOIN ms_office mo ON mo.id_ms_office = tbm.id_ms_office LEFT JOIN ms_region mr ON mr.id_ms_region = mo.id_ms_region LEFT JOIN am_msuser am ON am.id_ms_user = tbm.id_ms_user LEFT JOIN ms_business_line mbl ON mbl.id_ms_business_line = tbm.id_ms_business_line join ms_lov balancetype ON balancetype.id_lov = tbm.lov_balance_type where tbm.id_ms_tenant = (SELECT id_ms_tenant FROM ms_tenant WHERE tenant_code = '" + GlobalVariable.Tenant + "') AND tbm.lov_balance_type = (SELECT id_lov FROM ms_lov WHERE code = '" + balType + "' AND lov_group = 'BALANCE_TYPE') and DATE(tbm.trx_date) >= '" + startDate + "' and DATE(tbm.trx_date) <= '" + endDate + "' AND office_code = '" + officeCode + "' AND business_line_code = '" + blCode + "' AND region_code = '" + regCode + "' order by tbm.trx_date, tbm.trx_no ) as result")
		}

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
	getUseSignQR(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select use_sign_qr from tr_Document_d where document_id = '" + documentId + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		if (data == null || data == 'null') {
			data = 0
		}
		data
	}

	@Keyword
	getUseSignQRFromDocTemplate(Connection conn, String documentTemplateCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select use_sign_qr from ms_doc_template where doc_template_code = '" + documentTemplateCode + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		if (data == null || data == 'null') {
			data = 0
		}
		data
	}

	@Keyword
	getWASMSFromNotificationType(Connection conn, String userEmail, String code, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT CASE WHEN mntot.must_use_wa_first = '1' THEN 'WhatsApp Message' ELSE CASE WHEN (SELECT msvr.email_service FROM ms_vendor_registered_user msvr LEFT JOIN am_msuser amm ON msvr.id_ms_user = amm.id_ms_user WHERE amm.login_id = '"+userEmail+" OR amm.hashed_phone = '"+userEmail+"' OR amm.hashed_id_phone = '"+userEmail+"'') = '1' THEN 'SMS Notif' ELSE CASE WHEN mntot.use_wa_message = '1' THEN 'WA' ELSE 'SMS Notif' END END END AS result FROM ms_notificationtypeoftenant mntot LEFT JOIN ms_lov msl ON mntot.lov_sending_point = msl.id_lov LEFT JOIN ms_tenant mst ON mntot.id_ms_tenant = mst.id_ms_tenant WHERE msl.code = '" + code + "' and mst.tenant_code = '" + tenantCode + "';")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data == null || data == 'null') {
			data = 0
		}

		data
	}
}