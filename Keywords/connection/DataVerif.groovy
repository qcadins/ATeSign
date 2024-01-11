package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class DataVerif {

	String data, helperQuery
	int columnCount, i, updateVariable
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getOTP(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT otp_code FROM tr_invitation_link til JOIN ms_vendor mv ON til.id_ms_vendor = mv.id_ms_vendor WHERE receiver_detail = '"+ value +"' AND vendor_code = '"+ GlobalVariable.Psre +"'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getSaldo(Connection conn, String user) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT qty FROM tr_balance_mutation where usr_crt = '" +  user  + "' ORDER BY trx_date DESC LIMIT 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getResetOTP(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT reset_code_request_num FROM am_msuser where login_id = '" +  value  + "' or hashed_phone = '" + value + " ' or hashed_id_no = '" + value + " '")

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
	getAESKey(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT gs_value FROM am_generalsetting WHERE gs_code = 'AES_KEY'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getOTPAktivasi(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT otp_code FROM am_msuser WHERE login_id = '" +  value  + "' OR hashed_phone = encode(sha256('"+ value +"'), 'hex')")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getSaldoTrx(Connection conn, String email, String notelp, String desc) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tbm.trx_no, to_char(trx_date, 'yyyy-MM-dd HH24:mi:SS'), office_name, description, CASE WHEN amu.full_name IS NULL THEN tbm.usr_crt ELSE amu.full_name END, ref_no, notes, qty, office_code, office_name, business_line_code, business_line_name FROM tr_balance_mutation tbm JOIN ms_lov ml ON ml.id_lov = tbm.lov_trx_type LEFT JOIN am_msuser amu ON amu.id_ms_user = tbm.id_ms_user left join ms_office mo on mo.id_ms_office = tbm.id_ms_office left join ms_business_line mbl on mbl.id_ms_business_line = tbm.id_ms_business_line WHERE description = '"+ desc +"' ORDER BY id_balance_mutation DESC LIMIT 1")
		//		resultSet = stm.executeQuery("SELECT tbm.trx_no, to_char(trx_date, 'yyyy-MM-dd HH24:mi:SS'), description, CASE WHEN amu.full_name IS NULL THEN tbm.usr_crt ELSE amu.full_name END, notes, qty FROM tr_balance_mutation tbm JOIN ms_lov ml ON ml.id_lov = tbm.lov_trx_type LEFT JOIN am_msuser amu ON amu.id_ms_user = tbm.id_ms_user WHERE description = '"+ desc +"' AND (tbm.usr_crt = '"+ email.toUpperCase() +"' OR tbm.usr_crt = '"+ notelp +"') ORDER BY id_balance_mutation DESC LIMIT 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				if (data == null) {
					listdata.add('')
				} else {
					listdata.add(data)
				}
			}
		}
		listdata
	}

	@Keyword
	getCountTrx(Connection conn, String email, String notelp, String desc) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT COUNT(*) FROM tr_balance_mutation tbm JOIN ms_lov ml ON ml.id_lov = tbm.lov_trx_type JOIN am_msuser amu ON amu.id_ms_user = tbm.id_ms_user WHERE description = '" +  desc  + "' AND (tbm.usr_crt = '" +  email  + "' OR tbm.usr_crt = '" +  notelp  + "')")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	gettrxSaldo(Connection conn, String refnumber, String limit) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.trx_no, TO_CHAR(tbm.dtm_crt,'YYYY-MM-DD HH24:MI:SS'), ml.description ,amm.full_name, case when amm_two.full_name != null or amm_two.full_name != '' then tdh.ref_number||'('||amm_two.full_name||')' else tdh.ref_number end ,ml_doc_h.code,case when mdt.doc_template_name != null then mdt.doc_template_name else tdd.document_name end, tbm.notes, tbm.qty from tr_balance_mutation as tbm join ms_lov as ml on tbm.lov_trx_type = ml.id_lov join am_msuser as amm on tbm.id_ms_user = amm.id_ms_user join tr_document_h as tdh on tbm.id_document_h = tdh.id_document_h join ms_lov as ml_doc_h on tdh.lov_doc_type = ml_doc_h.id_lov join tr_document_d as tdd on tbm.id_document_d = tdd.id_document_d left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template  left join am_msuser as amm_two on tdh.id_msuser_customer = amm_two.id_ms_user where tdh.ref_number = '"+refnumber+"' order by tbm.dtm_crt asc limit " + limit + " ")
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
	getAesKeyBasedOnTenant(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select aes_encrypt_key from ms_tenant where tenant_code = '" + tenantCode + "' ")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
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
	getDocumentName(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select case when mdt.doc_template_name != '' or mdt.doc_template_name != null then mdt.doc_template_name else tdd.document_name end from tr_document_d tdd left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '" + refNumber + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getTenantCode(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mst.tenant_code from ms_tenant mst join ms_useroftenant muot on mst.id_ms_tenant = muot.id_ms_tenant join am_msuser amm on amm.id_ms_user = muot.id_ms_user where amm.login_id = '" + value.toUpperCase() + "' OR hashed_phone = '" + value + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getVendorNameForSaldo(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msv.vendor_name from ms_vendor msv left join tr_document_d tdd on tdd.id_ms_vendor = msv.id_ms_vendor left join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '" + value + "' OR tdd.document_id = '"+value+"' limit 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getDocId(Connection conn, String refNumber, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select STRING_AGG(tdd.document_id,', ') from tr_document_h tdh join tr_document_d tdd on tdh.id_document_h = tdd.id_document_h join ms_tenant mst on tdh.id_ms_tenant = mst.id_ms_tenant join ms_vendor msv on tdd.id_ms_vendor = msv.id_ms_vendor where tdh.ref_number = '"+refNumber+"' and mst.tenant_code = '"+tenantCode+"' and tdh.is_active = '1'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getDocIdBasedOnLoginSigner(Connection conn, String refNumber, String tenantCode, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdd.document_id from tr_document_h tdh LEFT join tr_document_d tdd on tdh.id_document_h = tdd.id_document_h LEFT join ms_tenant mst on tdh.id_ms_tenant = mst.id_ms_tenant LEFT join tr_document_d_sign tdds on tdd.id_Document_d = tdds.id_document_d left join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdh.ref_number = '"+refNumber+"' and mst.tenant_code = '"+tenantCode+"' AND amm.login_id = '"+emailSigner+"' GROUP BY tdds.id_document_d, tdd.document_id order by tdd.document_id desc")
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
	getParameterFlagPassOTP(Connection conn, String docID) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT mv.must_user_vendor_otp, mt.need_otp_for_signing, mt.need_password_for_signing FROM tr_document_d tdd LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = tdd.id_ms_tenant LEFT JOIN ms_vendor mv ON mv.id_ms_vendor = tdd.id_ms_vendor where document_id = '" + docID + "'")

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
	getTenantandVendorCode(Connection conn, String docID) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT mt.tenant_code, mv.vendor_code FROM tr_document_d tdd LEFT JOIN ms_tenant mt ON mt.id_ms_tenant = tdd.id_ms_tenant LEFT JOIN ms_vendor mv ON mv.id_ms_vendor = tdd.id_ms_vendor where document_id = '" + docID + "'")

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
	getOfficeCode(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mso.office_code from ms_office mso left join tr_document_h tdh on tdh.id_ms_office = mso.id_ms_office left join tr_document_d tdd on tdd.id_document_h = tdh.id_document_h where tdd.document_id = '"+ value +"' OR tdh.ref_number = '"+ value +"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getMustLivenessFaceCompare(Connection conn, String tenant) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT use_liveness_facecompare_first FROM ms_tenant WHERE tenant_code = '" + tenant + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getCountFaceCompDaily(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT liveness_facecompare_request_num FROM am_msuser WHERE login_id = '" + email + "'")

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
	getLimitLivenessDaily(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select gs_value from am_generalsetting WHERE gs_code = 'LIVENESS_FACECOMPARE_USER_DAILY_LIMIT'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getEmailService(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select email_service from ms_tenant where tenant_code = '"+tenantCode+"'")
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
	settingResetOTPNol(Connection conn, String value) {
		stm = conn.createStatement()

		updateVariable = stm.executeUpdate("UPDATE am_msuser SET reset_code_request_num = 0 WHERE login_id = '"+ value +"' OR hashed_phone = encode(sha256('"+ value +"'), 'hex')")
	}

	@Keyword
	getEmailHosting(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select email_hosting_domain from ms_tenant mt join ms_email_hosting meh on mt.id_email_hosting = meh.id_email_hosting where tenant_code = '"+ GlobalVariable.Tenant +"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		'@'+data
	}

	@Keyword
	getEmailFromNIK(Connection conn, String hashIdNo) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select login_id from am_msuser where hashed_id_no = '"+hashIdNo+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getEmailServiceFromTenant(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select sent_otp_by_email from ms_tenant where tenant_code = '"+tenantCode+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data == null) {
			data = '0'
		}

		data
	}

	@Keyword
	getEmailFromPhone(Connection conn, String hashPhone) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select login_id from am_msuser where hashed_phone = '"+hashPhone+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getSentOtpByEmail(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select sent_otp_by_email from ms_tenant where tenant_code = '"+tenantCode+"'")
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
	getMustUseWAFirst(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select must_use_wa_first from ms_tenant where tenant_code = '"+tenantCode+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data == null) {
			data = '0'
		}
		data
	}

	@Keyword
	getUseWAMessage(Connection conn, String tenantCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select use_wa_message from ms_tenant where tenant_code = '" + tenantCode + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data == null) {
			data = '0'
		}
		data
	}

	@Keyword
	getEmailServiceAsVendorUser(Connection conn, String emailSigner) {
		stm = conn.createStatement()

		if (!emailSigner.contains('@')) {
			helperQuery = 'amm.hashed_phone'
		} else {
			helperQuery = 'amm.login_id'
		}

		resultSet = stm.executeQuery("select msvr.email_service from ms_vendor_registered_user msvr left join am_msuser amm on amm.id_ms_user = msvr.id_ms_user left join ms_vendor msv on msvr.id_ms_vendor = msv.id_ms_vendor where "+helperQuery+" = '"+emailSigner+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data == null) {
			data = '0'
		}
		data
	}

	@Keyword
	getSMSSetting(Connection conn, String decisionCode) {
		stm = conn.createStatement()

		if (decisionCode == 'Forgot Password') {
			helperQuery = 'SEND_SMS_FORPASS'
		} else if (decisionCode == 'Send Document') {
			helperQuery = 'SEND_SMS_SENDDOC'
		} else if (decisionCode == 'OTP') {
			helperQuery = 'SEND_SMS_OTP_USER'
		}
		resultSet = stm.executeQuery("select gs_value from am_generalsetting left join ms_tenant mst on am_generalsetting.id_ms_tenant = mst.id_ms_tenant where gs_code = '"+helperQuery+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		if (data == null) {
			data = '0'
		}
		data
	}

	@Keyword
	getFullNameOfUser(Connection conn, String valueUser) {

		if (!valueUser.contains('@')) {
			helperQuery = 'hashed_phone'
		} else {
			helperQuery = 'login_id'
		}

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select full_name from am_msuser where " + helperQuery + " = '"+ valueUser +"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		data
	}

	@Keyword
	getTrxSaldoWASMS(Connection conn, String usage, String fullName) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tbm.trx_no, mso.office_name, TO_CHAR(tbm.trx_date, 'YYYY-MM-DD HH24:MI:SS'), 'Use ' || msl.description, amm.full_name, '', '', '', tbm.notes, tbm.qty FROM tr_balance_mutation tbm LEFT JOIN ms_office mso on mso.id_ms_office = tbm.id_ms_office LEFT JOIN ms_lov msl ON tbm.lov_balance_type = msl.id_lov LEFT JOIN am_msuser amm ON tbm.id_ms_user = amm.id_ms_user LEFT JOIN tr_document_d tdd ON tbm.id_document_d = tdd.id_document_d LEFT JOIN tr_document_h tdh ON tbm.id_document_h = tdh.id_document_h WHERE msl.description = '"+usage+"' AND amm.full_name = '"+fullName+"' AND tbm.trx_date BETWEEN current_timestamp - interval '"+GlobalVariable.batasWaktu+" minutes' AND current_timestamp + interval '"+GlobalVariable.batasWaktu+" minutes' ORDER BY tbm.dtm_crt DESC;")
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
	getDetailTrx(Connection conn, String trxNo) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tbm.trx_no, mso.office_name, TO_CHAR(tbm.trx_date, 'YYYY-MM-DD HH24:MI:SS'), 'Use ' || msl.description, amm.full_name, tdh.ref_number||'('||amm1.full_name||')', msl1.code, '', tbm.notes, tbm.qty FROM tr_balance_mutation tbm LEFT JOIN ms_office mso on tbm.id_ms_office = mso.id_ms_office LEFT JOIN ms_business_line mbl on tbm.id_ms_business_line = mbl.id_ms_business_line LEFT JOIN ms_lov msl ON tbm.lov_balance_type = msl.id_lov LEFT JOIN am_msuser amm ON tbm.id_ms_user = amm.id_ms_user LEFT JOIN tr_document_d tdd ON tbm.id_document_d = tdd.id_document_d LEFT JOIN tr_document_h tdh ON tbm.id_document_h = tdh.id_document_h  left join am_msuser amm1 on tdh.id_msuser_customer = amm1.id_ms_user left join ms_lov msl1 on tdh.lov_doc_type = msl1.id_lov WHERE tbm.trx_no = '"+trxNo+"' ORDER BY tbm.dtm_crt DESC")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				if (data == null) {
					listdata.add('')
				} else {
					listdata.add(data)
				}
			}
		}
		listdata
	}

	@Keyword
	getLimitValidationLivenessDaily(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select gs_value from am_generalsetting WHERE gs_code = 'LIVENESS_FACECOMPARE_VALIDATION_USER_DAILY_LIMIT'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getCountValidationFaceCompDaily(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT liveness_facecompare_validation_num FROM am_msuser WHERE login_id = '" + email + "'")

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
	getOfficeRegionBlineCodeUsingRefNum(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mso.office_code, msr.region_code, mbl.business_line_code from tr_document_h tdh left join ms_office mso on tdh.id_ms_office = mso.id_ms_office left join ms_region msr on mso.id_ms_region = msr.id_ms_region left join ms_business_line mbl on tdh.id_ms_business_line = mbl.id_ms_business_line where tdh.ref_number = '"+refNumber+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				if (data == null) {
					listdata.add('')
				} else {
					listdata.add(data)
				}
			}
		}

		resultSet = stm.executeQuery("select mso.office_code, msr.region_code, mbl.business_line_code from tr_balance_mutation tbm left join ms_office mso on tbm.id_ms_office = mso.id_ms_office left join ms_region msr on mso.id_ms_region = msr.id_ms_region left join ms_business_line mbl on tbm.id_ms_business_line = mbl.id_ms_business_line left join tr_document_h tdh on tbm.id_document_h = tdh.id_document_h where tdh.ref_number = '"+refNumber+"' ORDER BY tbm.trx_date desc limit 1")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				if (data == null) {
					listdata.add('')
				} else {
					listdata.add(data)
				}
			}
		}
		listdata
	}

	@Keyword
	getOfficeName(Connection conn, String value) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mso.office_name from ms_office mso left join tr_document_h tdh on tdh.id_ms_office = mso.id_ms_office left join tr_document_d tdd on tdd.id_document_h = tdh.id_document_h where tdd.document_id = '"+ value +"' OR tdh.ref_number = '"+ value +"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getBusinessLineOfficeCode(Connection conn, String value, String type) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT qty, id_ms_business_line, tbm.id_ms_office FROM tr_balance_mutation tbm JOIN ms_tenant mt ON mt.id_ms_tenant = tbm.id_ms_tenant JOIN ms_useroftenant mot ON mot.id_ms_tenant = mt.id_ms_tenant JOIN am_msuser amu ON amu.id_ms_user = mot.id_ms_user WHERE(notes ILIKE '%SEND OTP%' OR notes ILIKE '%Sending WhatsApp%' OR notes ILIKE '%Resend sign notification%') AND tbm.id_ms_user is not null AND login_id = '" + value.toUpperCase() + "' OR hashed_phone = encode(sha256('" + value.toUpperCase() + "'), 'hex') ORDER BY trx_date DESC LIMIT 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				if (data == null) {
					listdata.add('')
				} else {
					listdata.add(data)
				}
			}
		}

		if (type.equalsIgnoreCase('Register')) {
			resultSet = stm.executeQuery("select '-1', id_ms_business_line, id_ms_office from tr_invitation_link til join ms_vendor mv on til.id_ms_vendor = mv.id_ms_vendor where (receiver_detail = '" + value.toUpperCase() + "' or phone = '" + value.toUpperCase() + "') order by id_ms_user desc limit 1")
		} else if (type.equalsIgnoreCase('Document')) {
			resultSet = stm.executeQuery("SELECT '-1', COALESCE(tdh.id_ms_business_line, tlink.id_ms_business_line) AS id_ms_business_line, COALESCE(tdh.id_ms_office, tlink.id_ms_office) AS id_ms_office FROM tr_document_d td LEFT JOIN tr_document_h tdh ON td.id_document_h = tdh.id_document_h LEFT JOIN tr_document_d_sign tds ON tds.id_document_d = td.id_document_d LEFT JOIN am_msuser amu ON amu.id_ms_user = tds.id_ms_user LEFT JOIN tr_invitation_link tlink ON tlink.email = '" + value.toUpperCase() + "' WHERE amu.login_id = '" + value.toUpperCase() + "' ORDER BY td.id_document_d DESC LIMIT 1")
		}

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				if (data == null) {
					listdata.add('')
				} else {
					listdata.add(data)
				}
			}
		}
		listdata
	}

}