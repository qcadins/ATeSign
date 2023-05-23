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

		resultSet = stm.executeQuery("SELECT otp_code FROM tr_invitation_link WHERE receiver_detail = '" +  email  + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	buatUndanganStoreDB(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT full_name, place_of_birth, To_char(date_of_birth, 'MM/dd/yyyy'), gender, email, provinsi, kota, kecamatan, kelurahan, zip_code FROM am_user_personal_data aupd JOIN am_msuser amu ON aupd.id_ms_user = amu.id_ms_user WHERE EMAIL = '" +  email  + "'")

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
	inquiryInvitationViewDataVerif(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT invitation_by, receiver_detail, id_no, full_name, place_of_birth, date_of_birth, phone, email, address, provinsi, kota, kecamatan, kelurahan, zip_code  FROM tr_invitation_link WHERE email = '" +  email  + "'")

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
	inquiryInvitationStoreDB(Connection conn, String idno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT invitation_by, receiver_detail, id_no, full_name, place_of_birth, date_of_birth, gender, phone, email, address, provinsi, kota, kecamatan, kelurahan, zip_code  FROM tr_invitation_link WHERE id_no = '" +  idno  + "'")

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
	getTotalDataError(Connection conn, String date) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select COUNT(*) from tr_error_history where error_date >= '" +  date  + "' AND id_ms_tenant = 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getErrorReportDetail(Connection conn, String name) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select ERROR_MESSAGE from tr_error_history where cust_name = '" +  name  + "' AND id_ms_tenant = 1 ORDER BY id_error_history DESC LIMIT 1")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getStatusActivation(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT cust_name, cust_idno, is_registered, is_active FROM tr_error_history teh JOIN ms_vendor_registered_user mvru ON teh.id_ms_vendor = mvru.id_ms_vendor ORDER BY teh.error_date DESC LIMIT 1")

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
	getDataPencarianPengguna(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT full_name, place_of_birth, date_of_birth, email, provinsi, kota, kecamatan, kelurahan, zip_code FROM am_user_personal_data aupd JOIN am_msuser amu ON aupd.id_ms_user = amu.id_ms_user WHERE EMAIL = '" +  email  + "'")

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
	getPencarianPengguna(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT amu.full_name, email, aupd.date_of_birth, CASE WHEN LENGTH(mvru.vendor_user_autosign_key) > 0 THEN 'Active' ELSE 'Inactive' END , CASE WHEN mvru.is_active = '1' THEN 'Active' WHEN mvru.is_active = '0' THEN 'Inactive' ELSE mvru.is_active END FROM ms_vendor_registered_user mvru JOIN am_msuser amu ON amu.id_ms_user = mvru.id_ms_user JOIN am_user_personal_data aupd ON aupd.id_ms_user = amu.id_ms_user WHERE signer_registered_email = '" +  email  + "'")

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
	getAgreementCanceled(Connection conn, String documentId) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.is_active from tr_document_d as tdd join tr_document_h as tdh on tdd.id_document_h = tdh.id_document_h where tdd.document_id = '" +  documentId  + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
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
	getGenInvLink(Connection conn, String tenant,String phone, String idno, String email) {
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
	getSetEditAfterRegister(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select edit_after_register from ms_vendor Where vendor_code = 'VIDA'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		Integer.parseInt(data)
	}

	@Keyword
	getSetResendLink(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select resend_activation_link from ms_vendor Where vendor_code = 'VIDA'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getSetInvLinkAct(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select is_active from tr_invitation_link where receiver_detail = '" +  email  + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getKotakMasukSendDoc(Connection conn, String documentid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number, msl.description as doctype,mdt.doc_template_name, TO_CHAR(tdd.dtm_crt, 'DD-Mon-YYYY HH24:MI') as timee, CASE WHEN msl_sign.description = 'Need Sign' THEN 'Belum TTD' END as description from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_lov msl on tdh.lov_doc_type = msl.id_lov join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join ms_lov ms on tdds.lov_signer_type = ms.id_lov join am_msuser amm on tdds.id_ms_user = amm.id_ms_user join ms_lov msl_sign on tdd.lov_sign_status = msl_sign.id_lov where document_id = '" + documentid + "' ORDER BY tdds.id_document_d_sign asc limit 1")

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
	getBuatUndanganDataPerusahaanStoreDB(Connection conn, String email) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select region, office, business_line, task_no from tr_invitation_link where receiver_detail = '" +  email  + "'")

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
	getSignerKotakMasukSendDoc(Connection conn, String documentid, String emailSigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select ms.description as signertype,amm.full_name as name, amm.login_id as email,CASE WHEN amm.is_active = '1' THEN 'Sudah Aktivasi' END as aktivasi, CASE WHEN tdds.sign_date is not null THEN 'Signed' ELSE msl.description END as status, CASE WHEN tdds.sign_date IS null THEN '-' else to_char(tdds.sign_date, 'DD-Mon-YYYY HH24:MI') END sign_date from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_lov msl on tdd.lov_sign_status = msl.id_lov join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join ms_lov ms on tdds.lov_signer_type = ms.id_lov join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where document_id = '" + documentid + "' and amm.login_id = '"+emailSigner+"' ORDER BY tdds.id_document_d_sign asc limit 1")
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
	getFeedbackStoreDB(Connection conn,String emailsigner) {
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
	getLovTipePembayaran(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select description from ms_lov where lov_group = 'PAYMENT_SIGN_TYPE' order by description asc")

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
	getSignerInfo(Connection conn, String refnum, String psre) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select lov.description, amm.full_name, amm.login_id, CASE WHEN mvru.is_registered = '1' AND mvru.is_active = '1' THEN 'Sudah Aktivasi' WHEN mvru.is_registered = '0' AND mvru.is_active = '0' THEN 'Belum Registrasi' ELSE 'Belum Aktivasi' END, CASE WHEN tdds.sign_date IS NOT null THEN 'Signed' ELSE 'Need Sign' END, CASE WHEN TO_char(tdds.sign_date, 'DD-Mon-YYYY HH24:MI') IS NOT null THEN TO_char(tdds.sign_date, 'DD-Mon-YYYY HH24:MI') ELSE '-' END from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d join am_msuser amm on tdds.id_ms_user = amm.id_ms_user join ms_lov lov on tdds.lov_signer_type = lov.id_lov join ms_vendor_registered_user mvru ON mvru.id_ms_user = amm.id_ms_user JOIN tr_document_signing_request tdsr ON tdsr.id_document_h = tdh.id_document_h JOIN ms_vendor mv ON mv.id_ms_vendor = mvru.id_ms_vendor where tdh.ref_number = '" +  refnum  + "' and mv.vendor_code = '" +  psre  + "'")
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
	getTotalPencarianDokumen(Connection conn, String loginid, String tenant) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT count(*) FROM tr_document_d dd JOIN LATERAL( select dds.* from tr_document_d_sign dds JOIN am_msuser amu ON amu.id_ms_user = dds.id_ms_user where dds.id_document_d = dd.id_document_d and login_id = '" +  loginid  + "' limit 1) dds on true JOIN tr_document_h dh ON dh.id_document_h = dd.id_document_h AND dh.is_active = '1' JOIN ms_tenant mt ON dh.id_ms_tenant = mt.id_ms_tenant LEFT JOIN am_msuser usercust ON dh.id_msuser_customer = usercust.id_ms_user LEFT JOIN ms_doc_template dt ON dd.id_ms_doc_template = dt.id_doc_template JOIN ms_lov lovDocType ON dh.lov_doc_type = lovDocType.id_lov JOIN ms_lov lovSignStat ON dd.lov_sign_status = lovSignStat.id_lov JOIN ms_office office ON office.id_ms_office = dh.id_ms_office LEFT JOIN ms_region region ON region.id_ms_region = office.id_ms_region WHERE 1 = 1 and mt.tenant_code = '" +  tenant  + "' and dd.request_date >= date_trunc('month', now()) and dd.request_date <= now()")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

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
	getDDLTipeSaldo(Connection conn, String tenant, String vendor) {
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

		resultSet = stm.executeQuery("SELECT tenant_name, vendor_name, description, qty, ref_no, notes, to_char(trx_date, 'yyyy-mm-dd') FROM tr_balance_mutation tbm JOIN ms_tenant mt ON mt.id_ms_tenant = tbm.id_ms_tenant JOIN ms_vendor mv ON mv.id_ms_vendor = tbm.id_ms_vendor JOIN ms_lov ml ON ml.id_lov = tbm.lov_balance_type WHERE ref_no = '" +  refno  + "'")
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
	getTotalTenant(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(*) from ms_tenant")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getTenantStoreDB(Connection conn, String refnum) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tenant_name, tenant_code, ref_number_label, api_key, email_reminder_dest from ms_tenant where ref_number_label = '" +  refnum  + "'")

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
	getTenantServices(Connection conn, String tenantname) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select threshold_balance from ms_tenant where tenant_name = '" +  tenantname  + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
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
	getTenantServicesDescription(Connection conn, String tenantname) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT ms_lov.description, value FROM ms_tenant CROSS JOIN LATERAL json_each_text(threshold_balance::json) AS threshold_type JOIN ms_lov ON ms_lov.lov_group = 'BALANCE_TYPE' AND ms_lov.code = threshold_type.key WHERE tenant_name = '" +  tenantname  + "'")

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
	getIsiSaldoTrx(Connection conn, String refno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tbm.trx_no, trx_date, description, amu.full_name, ref_no, notes, qty FROM tr_balance_mutation tbm JOIN ms_lov ml ON ml.id_lov = tbm.lov_trx_type JOIN am_msuser amu ON amu.id_ms_user = tbm.id_ms_user WHERE ref_no = '" +  refno  + "'")

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
	getSignStatus(Connection conn, String documentid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from tr_document_d tdd join ms_lov msl on tdd.lov_sign_status = msl.id_lov where document_id = '" + documentid + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	getSigningStatusProcess(Connection conn, String documentid, String emailsigner) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdsp.request_status, CASE WHEN tdds.sign_date is not null THEN 'Sudah TTD' end , tdd.total_signed from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d join am_msuser amm on tdds.id_ms_user = amm.id_ms_user join tr_document_signing_request tdsp on amm.id_ms_user = tdsp.id_ms_user where tdd.document_id = '"+documentid+"' and amm.login_id = '"+emailsigner+"' ORDER BY tdsp.dtm_crt desc limit 1 ")

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
	getSaldoTrx(Connection conn, String email, String notelp, String desc) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT tbm.trx_no, to_char(trx_date, 'yyyy-MM-dd HH24:mi:SS'), description, amu.full_name, notes, qty FROM tr_balance_mutation tbm JOIN ms_lov ml ON ml.id_lov = tbm.lov_trx_type JOIN am_msuser amu ON amu.id_ms_user = tbm.id_ms_user WHERE description = '" +  desc  + "' AND (tbm.usr_crt = '" +  email  + "' OR tbm.usr_crt = '" +  notelp  + "') ORDER BY id_balance_mutation DESC")

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
	getDataDocTemplate(Connection conn, String docTempCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT doc_template_code, doc_template_name, doc_template_description, CASE WHEN is_active = '1' THEN 'Active' ELSE 'Inactive' END FROM esign.ms_doc_template where doc_template_code = '" +  docTempCode  + "'")

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

		resultSet = stm.executeQuery("SELECT doc_template_code, doc_template_name, doc_template_description, ml.description, CASE WHEN mdt.is_active = '1' THEN 'Active' ELSE 'Inactive' END FROM esign.ms_doc_template mdt JOIN ms_lov ml ON ml.id_lov = mdt.lov_payment_sign_type where doc_template_code = '" +  docTempCode  + "'")

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
	getSendDocSigning(Connection conn, String documentid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select STRING_AGG(amm.login_id,';') as email,STRING_AGG(lov.code,';') as code,mst.tenant_code, tdh.ref_number, tdd.document_id,CASE WHEN mdt.doc_template_code IS NULL THEN '' ELSE mdt.doc_template_code END,mso.office_code,mso.office_name, msr.region_code, msr.region_name,mbl.business_line_code, mbl.business_line_name,tdh.total_document from tr_document_d as tdd join tr_document_h as tdh on tdd.id_document_h = tdh.id_document_h join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join am_msuser as amm on tdds.id_ms_user = amm.id_ms_user join ms_tenant as mst on tdd.id_ms_tenant = mst.id_ms_tenant join ms_lov as lov on tdds.lov_signer_type = lov.id_lov left join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join ms_office as mso on tdh.id_ms_office = mso.id_ms_office join ms_region as msr on mso.id_ms_region = msr.id_ms_region join ms_business_line as mbl on tdh.id_ms_business_line = mbl.id_ms_business_line join ms_vendor as msv on tdd.id_ms_vendor = msv.id_ms_vendor where tdd.document_id = '" + documentid + "' GROUP BY mst.tenant_code, tdh.ref_number, tdd.document_id,mdt.doc_template_code, mso.office_code,mso.office_name, msr.region_code, msr.region_name,mbl.business_line_code, mbl.business_line_name, mso.office_code, tdd.is_sequence,msv.vendor_code,tdh.total_document")

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
	getAPIRequestStampingTrx(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select qty from tr_balance_mutation ORDER BY trx_date DESC")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
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
	getSignerTypeonDocTemplate(Connection conn, String documentTemplate) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.code from ms_doc_template_sign_loc mdtsl join ms_doc_template mdt on mdtsl.id_doc_template = mdt.id_doc_template join ms_lov msl on mdtsl.lov_signer_type = msl.id_lov where mdt.doc_template_code = '" + documentTemplate + "'")

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
	getuserCustomerondocument(Connection conn, String refnumber){
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select amm.full_name from tr_document_h tdh join tr_document_d tdd on tdh.id_document_h = tdd.id_document_h join am_msuser amm on tdh.id_msuser_customer = amm.id_ms_user where tdh.ref_number = '"+refnumber+"'")
		metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}


	@Keyword
	getSaldoUsedBasedonPaymentType(Connection conn, String refnumber, String userEmail){
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select CASE WHEN msl_tdd.description = 'Per Document' then '1' WHEN msl_tdd.description = 'Per Sign' then count(tdds.sign_location) end from tr_document_d_sign tdds join tr_document_d tdd on tdds.id_document_d = tdd.id_document_d join ms_lov msl on tdds.lov_signer_type = msl.id_lov join ms_lov msl_tdd on tdd.lov_payment_sign_type = msl_tdd.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where tdh.ref_number = '"+refnumber+"' and amm.login_id = '"+userEmail+"' GROUP BY msl.description, msl_tdd.description")
		metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		Integer.parseInt(data)
	}

	@Keyword
	getDocumentMonitoring(Connection conn, String refNumber, String fullname) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number, msl_doctype.description, case when tdd.id_ms_doc_template is null then case when tdd.document_name is null then '' else tdd.document_name end else mdt.doc_template_name end, case when amm.full_name is null then '' else amm.full_name end, TO_CHAR(tdh.dtm_crt, 'DD-Mon-YYYY HH24:MI'), case when tdd.completed_date is null then '-' else TO_CHAR(tdd.completed_date, 'DD-Mon-YYYY HH24:MI') end ,mso.office_name, msr.region_name, msl_signstatus.description from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_lov msl_doctype on tdh.lov_doc_type = msl_doctype.id_lov left join ms_doc_template mdt on tdd.id_ms_doc_template = mdt.id_doc_template left join am_msuser amm on tdh.id_msuser_customer = amm.id_ms_user join ms_lov msl_signstatus on tdd.lov_sign_status = msl_signstatus.id_lov join ms_office mso on tdh.id_ms_office = mso.id_ms_office join ms_region msr on mso.id_ms_region = msr.id_ms_region where tdh.ref_number = '"+refNumber+"' and amm.full_name = '"+fullname+"' order by tdd.document_name desc, mdt.doc_template_name desc")
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
	getStampdutyData(Connection conn, String stampdutyno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.notes, tbm.ref_no, to_char(tbm.trx_date, 'dd-Mon-yyyy'), tsd.stamp_duty_fee, mo.office_name, mr.region_name, mbl.business_line_name, ml.description from tr_balance_mutation tbm join tr_document_d tdd on tdd.id_document_d = tbm.id_document_d join tr_document_h tdh on tdh.id_document_h = tdd.id_document_h join tr_stamp_duty tsd on tsd.id_stamp_duty = tbm.id_stamp_duty join ms_business_line mbl on mbl.id_ms_business_line = tdh.id_ms_business_line join ms_office mo on mo.id_ms_office = tdh.id_ms_office join ms_region mr on mr.id_ms_region = mo.id_ms_region join ms_lov ml on ml.id_lov = tsd.lov_stamp_duty_status where tbm.notes = '" + stampdutyno + "'")

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
	getStampdutyTrxData(Connection conn, String stampdutyno) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.trx_no, tbm.ref_no, CASE WHEN tdd.id_ms_doc_template IS NULL THEN tdd.document_name ELSE mdt.doc_template_name END, amu.full_name, ml.description, to_char(tbm.trx_date, 'dd-Mon-yyyy HH24:MI') from tr_balance_mutation tbm join tr_document_d tdd on tdd.id_document_d = tbm.id_document_d left join am_msuser amu on amu.id_ms_user = tbm.id_ms_user join ms_lov ml on ml.id_lov = tbm.lov_trx_type join tr_stamp_duty tsd on tsd.id_stamp_duty = tbm.id_stamp_duty left join ms_doc_template mdt on mdt.id_doc_template = tdd.id_ms_doc_template where tbm.notes = '" + stampdutyno + "'")

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
	getDocumentStatus(Connection conn, String refnumber){
		String data

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select msl.description from tr_document_d tdd join ms_lov msl on tdd.lov_sign_status = msl.id_lov join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdh.ref_number = '"+refnumber+"'")
		metadata = resultSet.getMetaData()

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
	getInputDocumentMonitoring(Connection conn, String documentid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdh.ref_number, mso.office_name, msr.region_name from tr_document_h tdh left join ms_office mso on tdh.id_ms_office = mso.id_ms_office left join ms_region msr on mso.id_ms_region = msr.id_ms_region join tr_document_d tdd on tdd.id_document_h = tdh.id_document_h where tdd.document_id = '"+documentid+"'")
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
	getTotalStampingandTotalMaterai(Connection conn, String documentid) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tdd.total_stamping, tdd.total_materai from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h where tdd.document_id = '"+documentid+"'")
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
	getAPIGenInvLinkVerifTrx(Connection conn, String name) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tbm.qty from tr_balance_mutation tbm join am_msuser am on am.id_ms_user = tbm.id_ms_user join ms_lov ml on ml.id_lov = tbm.lov_trx_type where am.full_name = '"+ name +"' and ml.description = 'Use Verification'")
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
}