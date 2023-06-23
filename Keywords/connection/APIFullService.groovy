package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class APIFullService {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

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

		resultSet = stm.executeQuery("select qty from tr_balance_mutation tbm left join am_msuser am on am.id_ms_user = tbm.id_ms_user join ms_lov ml on ml.id_lov = tbm.lov_trx_type where ml.description = 'Use Verification' AND (am.full_name = '"+ name +"' OR tbm.usr_crt = '"+ phone +"')")
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

		resultSet = stm.executeQuery("UPDATE ms_tenant SET email_service = "+ value +" WHERE tenant_code = '"+ GlobalVariable.Tenant +"'")
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
	getAPIRegisterTrx(Connection conn, String trxno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select qty from tr_balance_mutation WHERE trx_no = '" +  trxno  + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
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

		resultSet = stm.executeQuery("UPDATE ms_vendor_registered_user SET email_service = "+ value +" WHERE signer_registered_email = '"+ email +"'")
	}

	@Keyword
	getSendDocSigning(Connection conn, String documentid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT mst.tenant_code, tdh.ref_number, tdd.document_id,CASE WHEN mdt.doc_template_code IS NULL THEN '' ELSE mdt.doc_template_code END,mso.office_code,mso.office_name, msr.region_code, msr.region_name,mbl.business_line_code, mbl.business_line_name,tdh.total_document from tr_document_d as tdd join tr_document_h as tdh on tdd.id_document_h = tdh.id_document_h join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join am_msuser as amm on tdds.id_ms_user = amm.id_ms_user join ms_tenant as mst on tdd.id_ms_tenant = mst.id_ms_tenant join ms_lov as lov on tdds.lov_signer_type = lov.id_lov left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join ms_office as mso on tdh.id_ms_office = mso.id_ms_office join ms_region as msr on mso.id_ms_region = msr.id_ms_region join ms_business_line as mbl on tdh.id_ms_business_line = mbl.id_ms_business_line join ms_vendor as msv on tdd.id_ms_vendor = msv.id_ms_vendor where tdd.document_id = '" + documentid + "' GROUP BY mst.tenant_code, tdh.ref_number, tdd.document_id,mdt.doc_template_code, mso.office_code,mso.office_name, msr.region_code, msr.region_name,mbl.business_line_code, mbl.business_line_name, mso.office_code, tdd.is_sequence,msv.vendor_code,tdh.total_document")

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
	getSendDocForEmailAndSignerType(Connection conn, String documentid,String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select DISTINCT amm.login_id as email, lov.code as code from tr_document_d as tdd join tr_document_h as tdh on tdd.id_document_h = tdh.id_document_h join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join am_msuser as amm on tdds.id_ms_user = amm.id_ms_user join ms_tenant as mst on tdd.id_ms_tenant = mst.id_ms_tenant join ms_lov as lov on tdds.lov_signer_type = lov.id_lov where tdd.document_id = '"+documentid+"' and amm.login_id = '"+emailSigner+"'")
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

		resultSet = stm.executeQuery("SELECT STRING_AGG(distinct au.login_id, ';') AS login FROM tr_document_h AS tdh JOIN tr_document_d AS tdd ON tdh.id_document_h = tdd.id_document_h JOIN tr_document_d_sign AS tdds ON tdd.id_document_d = tdds.id_document_d JOIN am_msuser AS au ON au.id_ms_user = tdds.id_ms_user WHERE tdd.document_id = '"+documentid+"'")
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

		resultSet = stm.executeQuery("select reset_code_request_num from am_msuser where login_id = '" + email + "'")

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
	getSign(Connection conn, String documentid, String emailsigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.qty, tbm.trx_no, tdsr.request_status, tdh.ref_number, CASE WHEN tdsr.user_request_ip is null then '' else tdsr.user_request_ip end, CASE WHEN tdsr.user_request_browser_information is null then '' else tdsr.user_request_browser_information end, tdsr.usr_crt, tdd.signing_process, TO_CHAR(tdds.sign_date, 'yyyy-MM-dd'), mst.api_key, mst.tenant_code from tr_document_signing_request tdsr join tr_document_d tdd on tdd.id_document_d = tdsr.id_document_d join tr_document_d_sign tdds on tdsr.id_document_d = tdds.id_document_d join am_msuser amm on tdds.id_ms_user = amm.id_ms_user join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join tr_balance_mutation tbm on tdd.id_document_d = tbm.id_document_d join ms_tenant mst on tbm.id_ms_tenant = mst.id_ms_tenant where tdd.document_id = '" + documentid + "' and amm.login_id = '" + emailsigner + "' order by tbm.dtm_crt asc, tdds.sign_date desc")

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
		Integer.parseInt(data)
	}

	@Keyword
	getTotalSigner(Connection conn, String documentId, String emailsigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(msl.code) from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d join ms_lov msl on tdds.lov_signer_type = msl.id_lov join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdd.document_id = '" + documentId + "' and amm.login_id = '" + emailsigner + "'")

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

		resultSet = stm.executeQuery("select mv.vendor_name, CASE WHEN mvru.is_registered = '1' AND mvru.is_active = '1' THEN '2' WHEN mvru.is_registered = '1' AND mvru.is_active = '0' THEN '1' END from ms_vendor_registered_user mvru JOIN am_msuser amu ON amu.id_ms_user = mvru.id_ms_user JOIN ms_vendor mv ON mv.id_ms_vendor = mvru.id_ms_vendor where login_id = '" +  value  + "' OR amu.hashed_id_no = encode(sha256('" +  value  + "'), 'hex') OR amu.hashed_phone = encode(sha256('" +  value  + "'), 'hex')")

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

		resultSet = stm.executeQuery("select document_id, login_id, CASE WHEN ml.description = 'Employee' THEN '1' WHEN ml.description = 'Customer' THEN '2' WHEN ml.description = 'Spouse' THEN '3' WHEN ml.description = 'Guarantor' THEN '4' END, CASE WHEN LENGTH(TO_CHAR(MAX(tdds.sign_date), 'yyyy-MM-dd HH24:MI:SS')) > 0 THEN '1' ELSE '0' END, TO_CHAR(MAX(tdds.sign_date), 'yyyy-MM-dd HH24:MI:SS') from tr_document_h tdh JOIN tr_document_d tdd ON tdd.id_document_h = tdh.id_document_h LEFT JOIN tr_document_d_sign tdds ON tdds.id_document_d = tdd.id_document_d JOIN am_msuser amu ON amu.id_ms_user = tdds.id_ms_user JOIN ms_lov ml ON ml.id_lov = tdds.lov_signer_type where ref_number = '"+ value +"' GROUP BY document_id, login_id, ml.description")

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
	getSaldoUsedBasedonPaymentType(Connection conn, String refnumber, String emailSigner){
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select CASE WHEN msl_tdd.description = 'Per Document' then '1' WHEN msl_tdd.description = 'Per Sign' then count(tdds.sign_location) end from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d join ms_lov msl on tdds.lov_signer_type = msl.id_lov join ms_lov msl_tdd on tdd.lov_payment_sign_type = msl_tdd.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdh.ref_number = '"+refnumber+"' and amm.login_id = '"+emailSigner+"' GROUP BY msl.description, msl_tdd.description")
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
	gettrxSaldo(Connection conn, String refnumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.trx_no, TO_CHAR(tbm.dtm_crt,'YYYY-MM-DD HH24:MI:SS'), ml.description , amm.full_name, tdh.ref_number||'('||amm_two.full_name||')',ml_doc_h.code,mdt.doc_template_name, tbm.notes, tbm.qty from tr_balance_mutation as tbm join ms_lov as ml on tbm.lov_trx_type = ml.id_lov join am_msuser as amm on tbm.id_ms_user = amm.id_ms_user join tr_document_h as tdh on tbm.id_document_h = tdh.id_document_h join ms_lov as ml_doc_h on tdh.lov_doc_type = ml_doc_h.id_lov join tr_document_d as tdd on tbm.id_document_d = tdd.id_document_d join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join am_msuser as amm_two on tdh.id_msuser_customer = amm_two.id_ms_user where tdh.ref_number = '"+refnumber+"' order by tbm.dtm_crt desc limit 1")

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
	getDocumentType(Connection conn, String refnumber){
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
}