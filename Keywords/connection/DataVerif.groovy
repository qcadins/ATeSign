package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class DataVerif {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getOTP(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT otp_code FROM tr_invitation_link til JOIN ms_vendor mv ON til.id_ms_vendor = mv.id_ms_vendor WHERE receiver_detail = '"+ email +"' AND vendor_code = '"+ GlobalVariable.Psre +"'")

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
	getSaldoTrx(Connection conn, String email, String notelp, String desc) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tbm.trx_no, to_char(trx_date, 'yyyy-MM-dd HH24:mi:SS'), description, CASE WHEN amu.full_name IS NULL THEN tbm.usr_crt ELSE amu.full_name END, notes, qty FROM tr_balance_mutation tbm JOIN ms_lov ml ON ml.id_lov = tbm.lov_trx_type LEFT JOIN am_msuser amu ON amu.id_ms_user = tbm.id_ms_user WHERE description = '"+ desc +"' AND (tbm.usr_crt = '"+ email +"' OR tbm.usr_crt = '"+ notelp +"') ORDER BY id_balance_mutation DESC LIMIT 1")

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
	getTenantCode(Connection conn, String loginId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mst.tenant_code from ms_tenant mst join ms_useroftenant muot on mst.id_ms_tenant = muot.id_ms_tenant join am_msuser amm on amm.id_ms_user = muot.id_ms_user where amm.login_id = '" + loginId + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getVendorNameForSaldo(Connection conn, String refNumber) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msv.vendor_name from ms_vendor msv left join tr_document_d tdd on tdd.id_ms_vendor = msv.id_ms_vendor left join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '" + refNumber + "' limit 1")

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

		resultSet = stm.executeQuery("select tdd.document_id from tr_document_h tdh join tr_document_d tdd on tdh.id_document_h = tdd.id_document_h join ms_tenant mst on tdh.id_ms_tenant = mst.id_ms_tenant join ms_vendor msv on tdd.id_ms_vendor = msv.id_ms_vendor where tdh.ref_number = '"+refNumber+"' and mst.tenant_code = '"+tenantCode+"' order by tdh.dtm_crt desc")
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
	getOfficeCode(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mso.office_code from ms_office mso left join tr_document_h tdh on tdh.id_ms_office = mso.id_ms_office left join tr_document_d tdd on tdd.id_document_h = tdh.id_document_h where tdd.document_id = '"+documentId+"'")
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
}