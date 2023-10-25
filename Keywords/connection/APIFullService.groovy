package connection

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class APIFullService {

	String data
	int columnCount, i, countLengthforSHA256 = 64, updateVariable
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []
	String emailWhere, selectData

	@Keyword
	getGenInvLink(Connection conn, String tenant, String phone, String idno, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tril.usr_crt, tril.gender, tril.kelurahan, tril.kecamatan, tril.kota, tril.zip_code, tril.date_of_birth, tril.place_of_birth, tril.provinsi, tril.email, tril.id_no, tril.phone, tril.address, tril.full_name, mst.tenant_code from tr_invitation_link as tril join ms_tenant as mst on tril.id_ms_tenant = mst.id_ms_tenant where tril.is_active = '1' and mst.tenant_code = '" + tenant + "' and tril.phone = '" + phone + "' and tril.id_no = '" + idno + "' and tril.email = '" + email + "'")

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

		resultSet = stm.executeQuery("SELECT otp_code FROM tr_invitation_link WHERE receiver_detail = '" +  email  + "'")

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

		resultSet = stm.executeQuery("select qty from tr_balance_mutation tbm left join am_msuser am on am.id_ms_user = tbm.id_ms_user join ms_lov ml on ml.id_lov = tbm.lov_trx_type where ml.description = 'Use Verification' AND (am.full_name = '" + name + "' OR tbm.usr_crt = '" + phone + "')")
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
	getAPIGenInvLinkOTPTrx(Connection conn, String name) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.qty from tr_balance_mutation tbm join am_msuser am on am.id_ms_user = tbm.id_ms_user join ms_lov ml on ml.id_lov = tbm.lov_trx_type where am.full_name = '"+ name +"' and ml.description = 'Use OTP'")
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

		resultSet = stm.executeQuery("SELECT otp_code FROM am_msuser WHERE login_id = '" +  email  + "'")

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

		resultSet = stm.executeQuery("SELECT reset_code_request_num FROM am_msuser where login_id = '" +  email  + "'")

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

		resultSet = stm.executeQuery("SELECT api_key FROM ms_tenant WHERE tenant_code = '" +  tenantcode  + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	settingEmailServiceTenant(Connection conn, String value) {
		stm = conn.createStatement()
		updateVariable = stm.executeUpdate("UPDATE ms_tenant SET email_service = " + value + " WHERE tenant_code = '" + GlobalVariable.Tenant + "'")
	}

	@Keyword
	getTotalUnsignedDocuments(Connection conn, String tenantCode, String email){
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(distinct (tdds.id_document_d)) from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d join am_msuser amm on amm.id_ms_user = tdds.id_ms_user join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_tenant mst on tdd.id_ms_tenant = mst.id_ms_tenant where amm.login_id = '"+email+"' and tdds.sign_date is null and mst.tenant_code = '"+tenantCode+"' group by mst.tenant_code, mst.api_key")
		metadata = resultSet.getMetaData()

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

		resultSet = stm.executeQuery("select mvru.is_active, mvru.is_registered from ms_vendor_registered_user mvru JOIN ms_vendor mv ON mv.id_ms_vendor = mvru.id_ms_vendor where vendor_code = '" +  GlobalVariable.Psre  + "' AND (signer_registered_email = '" +  email  + "' OR mvru.hashed_signer_registered_phone = encode(sha256('" +  notelp  + "'), 'hex'))")

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

		resultSet = stm.executeQuery("select SUM(total_materai) from tr_document_d tdd JOIN tr_document_h tdh ON tdh.id_document_h = tdd.id_document_h where tdh.ref_number = '"+ value +"'")

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

		resultSet = stm.executeQuery("select SUM(qty) from tr_balance_mutation tbm JOIN ms_lov ml ON ml.id_lov = tbm.lov_trx_type WHERE ref_no = '"+ value +"' AND ml.description = 'Use Stamp Duty' LIMIT "+ totalMaterai)

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

		resultSet = stm.executeQuery("SELECT msv.vendor_code, mst.tenant_code, tdh.ref_number,tdd.document_id,CASE WHEN mdt.doc_template_code IS NULL THEN '' ELSE mdt.doc_template_code END, case when mso.office_code is null then '' else mso.office_code end,mso.office_name, case when msr.region_code is null then '' else msr.region_code end, case when msr.region_name is null then '' else msr.region_name end, case when mbl.business_line_code is null then '' else mbl.business_line_code end, case when mbl.business_line_name is null then '' else mbl.business_line_name end,tdh.total_document, tdd.is_sequence from tr_document_d as tdd join tr_document_h as tdh on tdd.id_document_h = tdh.id_document_h join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join am_msuser as amm on tdds.id_ms_user = amm.id_ms_user join ms_tenant as mst on tdd.id_ms_tenant = mst.id_ms_tenant join ms_lov as lov on tdds.lov_signer_type = lov.id_lov left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join ms_office as mso on tdh.id_ms_office = mso.id_ms_office left join ms_region as msr on mso.id_ms_region = msr.id_ms_region left join ms_business_line as mbl on tdh.id_ms_business_line = mbl.id_ms_business_line left join ms_vendor as msv on tdd.id_ms_vendor = msv.id_ms_vendor  where tdd.document_id = '"+ documentid +"' GROUP BY mst.tenant_code, tdh.ref_number, tdd.document_id,mdt.doc_template_code, mso.office_code,mso.office_name, msr.region_code, msr.region_name,mbl.business_line_code, mbl.business_line_name, mso.office_code, tdd.is_sequence,msv.vendor_code,tdh.total_document")
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
	getSignLocation(Connection conn, String docid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select STRING_AGG(tdds.sign_location,';') from tr_document_d tdd join tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d where tdd.document_id = '" + docid + "'")

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
		data
	}

	@Keyword
	getSign(Connection conn, String documentid, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.qty, tdsr.request_status, tdh.ref_number, CASE WHEN tdsr.user_request_ip is null then '' else tdsr.user_request_ip end,  CASE WHEN tdsr.user_request_browser_information is null then '' else tdsr.user_request_browser_information end, tdsr.usr_crt, tdd.signing_process, TO_CHAR(tdds.sign_date, 'yyyy-MM-dd'), mst.api_key, mst.tenant_code from tr_document_signing_request tdsr  join tr_document_h tdh on tdh.id_document_h = tdsr.id_document_h left join tr_document_d tdd on tdd.id_document_h = tdh.id_document_h left join tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d left join tr_balance_mutation tbm on tdd.id_document_d = tbm.id_document_d left join am_msuser amm on tdds.id_ms_user = amm.id_ms_user left join ms_tenant mst on tbm.id_ms_tenant = mst.id_ms_tenant left join tr_document_signing_request_detail tdsrd on tdd.id_document_d = tdsrd.id_document_d where tdd.document_id = '"+documentid+"' and amm.login_id = '"+emailSigner+"' order by tbm.dtm_crt asc, tdds.sign_date desc, tdsr.id_document_signing_request desc limit 1")
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

		resultSet = stm.executeQuery("select mv.vendor_name, CASE WHEN mvru.is_registered = '0' AND mvru.is_active = '0' THEN '0' WHEN mvru.is_registered = '1' AND mvru.is_active = '1' THEN '2' WHEN mvru.is_registered = '1' AND mvru.is_active = '0' THEN '1' END from ms_vendor_registered_user mvru JOIN am_msuser amu ON amu.id_ms_user = mvru.id_ms_user JOIN ms_vendor mv ON mv.id_ms_vendor = mvru.id_ms_vendor where login_id = '" +  value  + "' OR amu.hashed_id_no = encode(sha256('" +  value  + "'), 'hex') OR amu.hashed_phone = encode(sha256('" +  value  + "'), 'hex')")

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

		resultSet = stm.executeQuery("SELECT document_id, CASE WHEN proses_materai = 0 OR proses_materai IS NULL THEN 0 WHEN proses_materai = 53 THEN 1 WHEN proses_materai = 51 AND error_message IS NOT NULL THEN 2 WHEN proses_materai = 51 AND sdt_process = 'NOT_STR' THEN 0 WHEN proses_materai = 51 AND sdt_process = 'STM_SDT' THEN 4 WHEN proses_materai = 51 AND sdt_process = 'UPL_DOC' OR sdt_process = 'GEN_SDT' THEN 3 WHEN proses_materai = 51 AND sdt_process = 'UPL_OSS' OR sdt_process = 'UPL_CON' THEN 5 WHEN proses_materai = 51 AND sdt_process = 'SDT_FIN' THEN 1 WHEN proses_materai = 55 OR proses_materai = 52 AND sdt_process = 'NOT_STR' THEN 0 WHEN proses_materai = 55 OR proses_materai = 52 AND sdt_process = 'STM_SDT' THEN 4 WHEN proses_materai = 55 OR proses_materai = 52 AND sdt_process = 'UPL_DOC' OR sdt_process = 'GEN_SDT' THEN 3 WHEN proses_materai = 55 OR proses_materai = 52 AND sdt_process = 'UPL_OSS' OR sdt_process = 'UPL_CON' THEN 5 WHEN proses_materai = 55 OR proses_materai = 52 AND sdt_process = 'SDT_FIN' THEN 1 END, CASE WHEN error_message IS NULL THEN '' ELSE error_message END FROM tr_document_h tdh JOIN tr_document_d tdd ON tdd.id_document_h = tdh.id_document_h LEFT JOIN tr_document_h_stampduty_error tdhse ON tdhse.id_document_d = tdd.id_document_d WHERE tdh.ref_number = '" +  value  + "'")

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
	getAPICheckSigningStoreDB(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select document_id, login_id, CASE WHEN ml.description = 'Employee' THEN '1' WHEN ml.description = 'Customer' THEN '2' WHEN ml.description = 'Spouse' THEN '3' WHEN ml.description = 'Guarantor' THEN '4' END, CASE WHEN LENGTH(TO_CHAR(MAX(tdds.sign_date), 'yyyy-MM-dd HH24:MI:SS')) > 0 THEN '1' ELSE '0' END, CASE WHEN TO_CHAR(MAX(tdds.sign_date), 'yyyy-MM-dd HH24:MI:SS') IS NULL THEN '' ELSE TO_CHAR(MAX(tdds.sign_date), 'yyyy-MM-dd HH24:MI:SS') END from tr_document_h tdh JOIN tr_document_d tdd ON tdd.id_document_h = tdh.id_document_h LEFT JOIN tr_document_d_sign tdds ON tdds.id_document_d = tdd.id_document_d JOIN am_msuser amu ON amu.id_ms_user = tdds.id_ms_user JOIN ms_lov ml ON ml.id_lov = tdds.lov_signer_type where ref_number = '"+ value +"' GROUP BY document_id, login_id, ml.description")

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
	getPaymentType(Connection conn, String refnumber){
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from tr_document_d tdd join ms_lov msl on tdd.lov_payment_sign_type = msl.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '"+refnumber+"'")
		metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}


	@Keyword
	getPaymentTypeMULTIDOC(Connection conn, String docId){
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from tr_document_d tdd join ms_lov msl on tdd.lov_payment_sign_type = msl.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdd.document_id = '"+docId+"'")
		metadata = resultSet.getMetaData()

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

		resultSet = stm.executeQuery("select CASE WHEN msl_tdd.description = 'Per Document' then '1' WHEN msl_tdd.description = 'Per Sign' then count(tdds.sign_location) end from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d left join ms_lov msl on tdds.lov_signer_type = msl.id_lov join ms_lov msl_tdd on tdd.lov_payment_sign_type = msl_tdd.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdh.ref_number = '"+refNumber+"' and amm.login_id = '"+emailSigner+"' GROUP BY msl.description, msl_tdd.description")
		metadata = resultSet.getMetaData()

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

		resultSet = stm.executeQuery("select CASE WHEN msl_tdd.description = 'Per Document' then '1' WHEN msl_tdd.description = 'Per Sign' then count(tdds.sign_location) end from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d left join ms_lov msl on tdds.lov_signer_type = msl.id_lov join ms_lov msl_tdd on tdd.lov_payment_sign_type = msl_tdd.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdd.document_id = '"+documentId+"' and amm.login_id = '"+emailSigner+"' GROUP BY msl.description, msl_tdd.description")
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

		resultSet = stm.executeQuery("select tdd.total_signed from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number=  '"+refnumber+"'")
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

		resultSet = stm.executeQuery("select aes_encrypt_key from ms_tenant where tenant_code = '"+tenantCode+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	gettrxSaldo(Connection conn, String refnumber, String limit, String tipeTransaksi) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.trx_no, TO_CHAR(tbm.dtm_crt,'YYYY-MM-DD HH24:MI:SS'), ml.description ,amm.full_name, case when amm_two.full_name != '' or amm_two.full_name != null then tdh.ref_number||'('||amm_two.full_name||')' else tdh.ref_number end ,ml_doc_h.code,case when mdt.doc_template_name != null or mdt.doc_template_name != '' then mdt.doc_template_name else tdd.document_name end, tbm.notes, tbm.qty from tr_balance_mutation as tbm join ms_lov as ml on tbm.lov_trx_type = ml.id_lov join am_msuser as amm on tbm.id_ms_user = amm.id_ms_user join tr_document_h as tdh on tbm.id_document_h = tdh.id_document_h join ms_lov as ml_doc_h on tdh.lov_doc_type = ml_doc_h.id_lov join tr_document_d as tdd on tbm.id_document_d = tdd.id_document_d left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join am_msuser as amm_two on tdh.id_msuser_customer = amm_two.id_ms_user where tdh.ref_number = '" + refnumber + "' AND ml.description = '"+tipeTransaksi+"' order by tbm.trx_no asc")

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

		resultSet = stm.executeQuery("select msl.description from tr_document_h tdh join ms_lov msl on tdh.lov_doc_type = msl.id_lov where tdh.ref_number = '"+refnumber+"'")
		metadata = resultSet.getMetaData()

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

		resultSet = stm.executeQuery("SELECT COUNT(tdsr.id_ms_user) FROM tr_document_h tdh JOIN tr_document_signing_request tdsr ON tdh.id_document_h = tdsr.id_document_h WHERE tdh.ref_number = '"+refNumber+"'")
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

		resultSet = stm.executeQuery("select ms.description as signertype,amm.full_name as name, amm.login_id as email,CASE WHEN amm.is_active = '1' THEN 'Sudah Aktivasi' END as aktivasi, CASE WHEN tdds.sign_date is not null THEN 'Signed' ELSE msl.description END as status, CASE WHEN tdds.sign_date IS null THEN '-' else to_char(tdds.sign_date, 'DD-Mon-YYYY HH24:MI') END sign_date from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_lov msl on tdd.lov_sign_status = msl.id_lov left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join ms_lov ms on tdds.lov_signer_type = ms.id_lov join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where document_id = '" + documentid + "' and amm.login_id = '"+emailSigner+"' ORDER BY tdds.id_document_d_sign asc limit 1")
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

		resultSet = stm.executeQuery("select tbm.trx_no,TO_CHAR(tbm.dtm_crt,'YYYY-MM-DD HH24:MI:SS'), ml.description , case when amm.full_name != '' or amm.full_name != null then amm.full_name else tbm.usr_crt end, case when amm_two.full_name != '' or amm_two.full_name != null then tdh.ref_number||'('||amm_two.full_name||')' else tdh.ref_number end, ml_doc_h.code,case when mdt.doc_template_name != null then mdt.doc_template_name else tdd.document_name end , tbm.notes, tbm.qty from tr_document_d_stampduty tddstamp join tr_document_d tdd on tddstamp.id_document_d = tdd.id_document_d join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join tr_stamp_duty tsd on tddstamp.id_stamp_duty = tsd.id_stamp_duty left join tr_balance_mutation tbm on tsd.id_stamp_duty = tbm.id_stamp_duty left join ms_business_line mbl on tdh.id_ms_business_line = mbl.id_ms_business_line left join ms_office mso on mso.id_ms_office = tdh.id_ms_office left join ms_lov as ml on tbm.lov_trx_type = ml.id_lov left join am_msuser as amm on tdh.id_msuser_customer = amm.id_ms_user left join ms_region msr on mso.id_ms_region = msr.id_ms_region join ms_lov as ml_doc_h on tdh.lov_doc_type = ml_doc_h.id_lov left join ms_lov msl on tsd.lov_stamp_duty_status = msl.id_lov left join am_msuser as amm_two on tdh.id_msuser_customer = amm_two.id_ms_user where tdh.ref_number = '"+refNumber+"' ORDER BY tbm.dtm_crt ")
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

		resultSet = stm.executeQuery("SELECT VENDOR_REGISTRATION_ID FROM ms_vendor_registered_user WHERE SIGNER_REGISTERED_EMAIL = '"+ email +"'")
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

		resultSet = stm.executeQuery("select case when msv.vendor_code is not null then msv.vendor_code else msv1.vendor_code end from tr_document_d tdd left join ms_doc_template mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join ms_vendor msv on mdt.id_ms_vendor = msv.id_ms_vendor left join ms_vendoroftenant mvot on tdd.id_ms_tenant = mvot.id_ms_tenant left join ms_vendor msv1 on mvot.id_ms_vendor = msv1.id_ms_vendor where tdd.document_id = '"+ docId  +"' order by mvot.default_vendor asc limit 1")
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

		resultSet = stm.executeQuery("select seq_no from ms_doc_template_sign_loc mdtsl join ms_doc_template mdt on mdtsl.id_doc_template = mdt.id_doc_template left join ms_lov msl on mdtsl.lov_sign_type = msl.id_lov left join ms_lov msl1 on mdtsl.lov_signer_type = msl1.id_lov where mdt.doc_template_code = '"+ docTemplate +"' and msl.description = 'Tanda tangan' and msl1.code = '"+ signerType +"' limit 1")
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

		resultSet = stm.executeQuery("SELECT login_id, seq_no, is_sequence, document_id, sign_date FROM tr_document_d tdd JOIN tr_document_d_sign tdds ON tdds.id_document_d = tdd.id_document_d JOIN am_msuser amu ON amu.id_ms_user = tdds.id_ms_user WHERE document_id = '"+docId+"' ORDER BY seq_no ASC")
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
		} else if(listdata[2] == '1') {
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

		resultSet = stm.executeQuery("SELECT STRING_AGG(login_id, ';' ORDER BY seq_no) AS aa FROM (SELECT DISTINCT tdds.id_ms_user,au.login_id, FIRST_VALUE(tdds.seq_no) OVER (PARTITION BY tdds.id_ms_user ORDER BY tdds.seq_no) AS seq_no FROM tr_document_h AS tdh JOIN tr_document_d AS tdd ON tdh.id_document_h = tdd.id_document_h JOIN tr_document_d_sign AS tdds ON tdd.id_document_d = tdds.id_document_d JOIN am_msuser AS au ON au.id_ms_user = tdds.id_ms_user WHERE tdd.document_id = '"+ value +"' OR tdh.ref_number = '"+ value +"') AS alls;")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	settingBaseUrl(String excelPath, int colm, int row) {
		if(findTestData(excelPath).getValue(colm, row) == 'No') {
			GlobalVariable.base_url = 'http://gdkwebsvr:7021/'
		} else {
			GlobalVariable.base_url = findTestData('Login/Setting').getValue(7, 2)
		}
	}

	@Keyword
	getHashedNo(Connection conn, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select hashed_phone from am_msuser where login_id = '"+emailSigner+"' limit 1")
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
	getInvitationCode(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select invitation_code from tr_invitation_link inv JOIN ms_vendor mv ON inv.id_ms_vendor = mv.id_ms_vendor where  receiver_detail = '"+ email +"' and vendor_code = '"+ GlobalVariable.Psre +"'")

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
	getCheckInvRegisStoreDB(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT CASE WHEN login_id is null then '0' else '1' END, CASE WHEN login_id is null then '0' else '1' END, case when request_status = '0' or '1' then '1' else '2' end, notes FROM tr_balance_mutation tbm JOIN tr_job_check_register_status tjc ON tbm.id_balance_mutation = tjc.id_balance_mutation LEFT JOIN am_msuser amu ON tbm.id_ms_user = amu.id_ms_user WHERE tjc.usr_crt = '"+ email +"'")

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
	settingAllowRegenerateLink(Connection conn, String value) {
		stm = conn.createStatement()

		updateVariable = stm.executeUpdate("WITH rows_to_update AS ( SELECT mvt.id_ms_vendoroftenant FROM ms_vendoroftenant mvt JOIN ms_tenant mt ON mvt.id_ms_tenant = mt.id_ms_tenant JOIN ms_vendor mv ON mv.id_ms_vendor = mvt.id_ms_vendor WHERE mt.tenant_code = '"+ GlobalVariable.Tenant +"' AND mv.vendor_code = '"+ GlobalVariable.Psre +"' ) UPDATE ms_vendoroftenant mvt SET allow_regenerate_inv_link = "+ value +" FROM rows_to_update rtu WHERE mvt.id_ms_vendoroftenant = rtu.id_ms_vendoroftenant")
	}

	@Keyword
	settingLinkIsActive(Connection conn, String value, String email) {
		stm = conn.createStatement()

		updateVariable = stm.executeUpdate("UPDATE tr_invitation_link SET is_active = '"+ value +"' WHERE receiver_detail = '"+ email +"'")
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

		updateVariable = stm.executeUpdate("UPDATE ms_vendor_registered_user SET email_service = '"+ value +"' WHERE id_ms_user IN (SELECT mvr.id_ms_user FROM ms_vendor_registered_user mvr JOIN am_msuser am ON mvr.id_ms_user = am.id_ms_user WHERE am.hashed_id_no = encode(sha256('"+ idKtp +"'), 'hex'))")
	}

	@Keyword
	checkAPIGetActLinkStoreDB(Connection conn, String idno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mvru.is_active, mvru.is_registered from ms_vendor_registered_user mvru JOIN ms_vendor mv ON mv.id_ms_vendor = mvru.id_ms_vendor JOIN am_msuser am ON am.id_ms_user = mvru.id_ms_user where vendor_code = '" +  GlobalVariable.Psre  + "' AND am.hashed_id_no = encode(sha256('" +  idno  + "'), 'hex')")

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

		resultSet = stm.executeQuery("select count(tdds.id_Document_d_sign) from tr_document_d_sign tdds LEFT JOIN am_msuser amm on tdds.id_ms_user = amm.id_ms_user LEFT JOIN tr_document_d tdd on tdds.id_Document_d = tdd.id_Document_d LEFT JOIN tr_Document_h tdh on tdh.id_Document_h = tdd.id_document_h WHERE tdh.ref_number = '"+refnumber+"' AND amm.login_id = '"+emailSigner+"'")
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

		resultSet = stm.executeQuery("select count(distinct(tdds.id_ms_user)) from tr_document_d_sign tdds LEFT JOIN am_msuser amm on tdds.id_ms_user = amm.id_ms_user LEFT JOIN tr_document_d tdd on tdds.id_Document_d = tdd.id_Document_d LEFT JOIN tr_Document_h tdh on tdh.id_Document_h = tdd.id_document_h WHERE tdh.ref_number = '"+refnumber+"' AND tdds.sign_date IS NOT NULL")
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

		resultSet = stm.executeQuery("select STRING_AGG(tbm.trx_no, ';') from tr_balance_mutation tbm LEFT JOIN tr_document_d tdd on tbm.id_document_d = tdd.id_document_d LEFT JOIN ms_lov msl on tbm.lov_trx_type = msl.id_lov LEFT JOIN am_msuser amm on tbm.id_ms_user = amm.id_ms_user where tdd.document_id = '"+documentId+"'")
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

		resultSet = stm.executeQuery("select vendor_code from ms_vendoroftenant mvo join ms_tenant mt on mvo.id_ms_tenant = mt.id_ms_tenant join ms_vendor mv on mvo.id_ms_vendor = mv.id_ms_vendor where tenant_code = '"+ tenantCode +"' order by default_vendor ASC limit 1")
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

		resultSet = stm.executeQuery("select vendor_code from ms_vendor_registered_user mvr join ms_vendor mv on mv.id_ms_vendor = mvr.id_ms_vendor where signer_registered_email = '"+ email.toUpperCase() +"' order by id_ms_vendor_registered_user desc limit 1")
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
		resultSet = stm.executeQuery("SELECT count(DISTINCT(amm.login_id)) FROM tr_document_d tdd LEFT JOIN tr_document_d_sign tdds on tdds.id_document_d = tdd.id_document_d LEFT JOIN ms_lov msl on tdds.lov_autosign = msl.id_lov LEFT JOIN am_msuser amm on tdds.id_ms_user = amm.id_ms_user WHERE tdd.document_id = '"+documentId+"' AND msl.description = 'Autosign'")
		metadata = resultSet.getMetaData()

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
		String dataaa

		stm = conn.createStatement()
		resultSet = stm.executeQuery("select msl.description from tr_Document_d tdd left join tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d left join am_msuser amm on amm.id_ms_user = tdds.id_ms_user left join ms_lov msl on  tdds.lov_autosign = msl.id_lov where tdd.document_id = '"+documentId+"' AND amm.login_id = '"+emailSigner+"'")
		metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}


	@Keyword
	gettrxSaldoForMeteraiPrivy(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tbm.trx_no, TO_CHAR(tbm.dtm_crt,'YYYY-MM-DD HH24:MI:SS'), ml.description , case when amm.full_name != '' or amm.full_name != null then amm.full_name else tbm.usr_crt end, case when amm_two.full_name != '' or amm_two.full_name != null then tdh.ref_number||'('||amm_two.full_name||')' else tdh.ref_number end, ml_doc_h.code, case when mdt.doc_template_name != null then mdt.doc_template_name else tdd.document_name end, tbm.notes, tbm.qty FROM tr_balance_mutation tbm join tr_document_h tdh on tbm.id_document_h = tdh.id_document_h left join ms_lov as ml on tbm.lov_trx_type = ml.id_lov join tr_document_d tdd on tdd.id_document_h = tdh.id_document_h left join am_msuser as amm on tdh.id_msuser_customer = amm.id_ms_user join ms_lov as ml_doc_h on tdh.lov_doc_type = ml_doc_h.id_lov left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join am_msuser as amm_two on tdh.id_msuser_customer = amm_two.id_ms_user WHERE ref_number = '" + refNumber + "' ORDER BY tbm.dtm_crt DESC LIMIT 1")
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

		resultSet = stm.executeQuery("SELECT DISTINCT(amm.login_id) FROM tr_document_d tdd LEFT JOIN tr_document_d_sign tdds on tdds.id_document_d = tdd.id_document_d LEFT JOIN ms_lov msl on tdds.lov_autosign = msl.id_lov LEFT JOIN am_msuser amm on tdds.id_ms_user = amm.id_ms_user WHERE tdd.document_id = '"+documentId+"' AND msl.description = 'Autosign'")
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
