package connection
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import groovy.sql.ResultSetMetaDataWrapper
import groovy.sql.Sql
import internal.GlobalVariable

public class dataVerif {

	int columnCount

	@Keyword
	public getOTP (Connection conn, String email){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT otp_code FROM tr_invitation_link WHERE receiver_detail = '"+ email +"'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

	@Keyword
	public BuatUndanganStoreDB (Connection conn, String email){
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT full_name, place_of_birth, To_char(date_of_birth, 'MM/dd/yyyy'), gender, email, provinsi, kota, kecamatan, kelurahan, zip_code FROM am_user_personal_data aupd JOIN am_msuser amu ON aupd.id_ms_user = amu.id_ms_user WHERE EMAIL = '"+ email +"'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public InquiryInvitationViewDataVerif (Connection conn, String email){
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT invitation_by, receiver_detail, id_no, full_name, place_of_birth, date_of_birth, phone, email, address, provinsi, kota, kecamatan, kelurahan, zip_code  FROM tr_invitation_link WHERE email = '"+ email +"'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public InquiryInvitationStoreDB (Connection conn, String idno){
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT invitation_by, receiver_detail, id_no, full_name, place_of_birth, date_of_birth, gender, phone, email, address, provinsi, kota, kecamatan, kelurahan, zip_code  FROM tr_invitation_link WHERE id_no = '"+ idno +"'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getSaldo (Connection conn, String user){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT qty FROM tr_balance_mutation where usr_crt = '"+ user +"' ORDER BY trx_date DESC LIMIT 1")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

	@Keyword
	public getSendDoc(Connection conn, String documentid){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select STRING_AGG(amm.login_id,';') as email, STRING_AGG(lov.code,';') as code, mst.tenant_code, tdh.ref_number, tdd.document_id,mdt.doc_template_code, mso.office_code,mso.office_name, msr.region_code, msr.region_name,mbl.business_line_code, mbl.business_line_name, tdd.is_sequence,msv.vendor_code, tdh.result_url,tdh.url_upload from tr_document_d as tdd join tr_document_h as tdh on tdd.id_document_h = tdh.id_document_h join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join am_msuser as amm on tdds.id_ms_user = amm.id_ms_user join ms_tenant as mst on tdd.id_ms_tenant = mst.id_ms_tenant join ms_lov as lov on tdds.lov_signer_type = lov.id_lov join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join ms_office as mso on tdh.id_ms_office = mso.id_ms_office join ms_region as msr on mso.id_ms_region = msr.id_ms_region join ms_business_line as mbl on tdh.id_ms_business_line = mbl.id_ms_business_line join ms_vendor as msv on tdd.id_ms_vendor = msv.id_ms_vendor where tdd.document_id = '"+documentid+"' GROUP BY mst.tenant_code, tdh.ref_number, tdd.document_id,mdt.doc_template_code, mso.office_code,mso.office_name, msr.region_code, msr.region_name,mbl.business_line_code, mbl.business_line_name, mso.office_code, tdd.is_sequence,msv.vendor_code,tdh.result_url,tdh.url_upload")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}


	@Keyword
	public getTotalDataError (Connection conn, String date){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select COUNT(*) from tr_error_history where error_date >= '"+ date +"' AND id_ms_tenant = 1")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

	@Keyword
	public getErrorReportDetail (Connection conn, String name){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select ERROR_MESSAGE from tr_error_history where cust_name = '"+ name +"' AND id_ms_tenant = 1 ORDER BY id_error_history DESC LIMIT 1")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

	@Keyword
	public getStatusActivation (Connection conn){
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT cust_name, cust_idno, is_registered, is_active FROM tr_error_history teh JOIN ms_vendor_registered_user mvru ON teh.id_ms_vendor = mvru.id_ms_vendor ORDER BY teh.error_date DESC LIMIT 1")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getTenant (Connection conn, String user){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("Select mt.tenant_code from am_msuser amu JOIN ms_office mo ON mo.id_ms_office = amu.id_ms_office JOIN ms_tenant mt ON mt.id_ms_tenant = mo.id_ms_tenant where login_id = '"+ user +"' AND mt.is_active = '1'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

	@Keyword
	public getDataPencarianPengguna (Connection conn, String email){
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT full_name, place_of_birth, date_of_birth, email, provinsi, kota, kecamatan, kelurahan, zip_code FROM am_user_personal_data aupd JOIN am_msuser amu ON aupd.id_ms_user = amu.id_ms_user WHERE EMAIL = '"+ email +"'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getResetOTP (Connection conn, String email){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT reset_code_request_num FROM am_msuser where login_id = '"+ email +"'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return Integer.parseInt(data)
	}

	@Keyword
	public getPencarianPengguna (Connection conn, String email){
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT amu.full_name, email, aupd.date_of_birth, CASE WHEN LENGTH(mvru.vendor_user_autosign_key) > 0 THEN 'Active' ELSE 'Inactive' END , CASE WHEN mvru.is_active = '1' THEN 'Active' WHEN mvru.is_active = '0' THEN 'Inactive' ELSE mvru.is_active END FROM ms_vendor_registered_user mvru JOIN am_msuser amu ON amu.id_ms_user = mvru.id_ms_user JOIN am_user_personal_data aupd ON aupd.id_ms_user = amu.id_ms_user WHERE signer_registered_email = '"+ email +"'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getAESKey (Connection conn){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT gs_value FROM am_generalsetting WHERE gs_code = 'AES_KEY'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

	@Keyword
	public getAgreementCanceled (Connection conn, String documentId){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select tdh.is_active from tr_document_d as tdd join tr_document_h as tdh on tdd.id_document_h = tdh.id_document_h where tdd.document_id = '"+ documentId +"'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

	@Keyword
	public getbulkSign(Connection conn, String documentids){
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select STRING_AGG(amm.login_id,';'),msv.vendor_code, tdd.total_signed from tr_document_d_sign as tdds join tr_document_d as tdd on tdds.id_document_d = tdd.id_document_d join am_msuser as amm on tdds.id_ms_user = amm.id_ms_user join ms_vendor as msv on tdd.id_ms_vendor = msv.id_ms_vendor join tr_document_h as tdh on tdh.id_document_h = tdd.id_document_h where tdd.document_id = '"+documentids+"' GROUP BY msv.vendor_code, tdd.total_signed")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getOTPAktivasi (Connection conn, String email){

		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT otp_code FROM am_msuser WHERE login_id = '"+ email +"'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

	@Keyword
	public getGenInvLink(Connection conn, String tenant,String phone, String idno, String email){
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select tril.usr_crt, tril.gender, tril.kelurahan, tril.kecamatan, tril.kota, tril.zip_code, tril.date_of_birth, tril.place_of_birth, tril.provinsi, tril.email, tril.id_no, tril.phone, tril.address, tril.full_name, mst.tenant_code from tr_invitation_link as tril join ms_tenant as mst on tril.id_ms_tenant = mst.id_ms_tenant where tril.is_active = '1' and mst.tenant_code = '"+tenant+"' and tril.phone = '"+phone+"' and tril.id_no = '"+idno+"' and tril.email = '"+email+"'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getSetEditAfterRegister (Connection conn){

		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select edit_after_register from ms_vendor Where vendor_code = 'VIDA'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		return Integer.parseInt(data)
	}

	@Keyword
	public getSetResendLink (Connection conn){

		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select resend_activation_link from ms_vendor Where vendor_code = 'VIDA'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return Integer.parseInt(data)
	}

	@Keyword
	public getSetInvLinkAct (Connection conn, String email){

		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select is_active from tr_invitation_link where receiver_detail = '"+ email +"'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return Integer.parseInt(data)
	}

	@Keyword
	public getKotakMasukSendDoc(Connection conn, String documentid){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select tdh.ref_number,  msl.description as doctype,mdt.doc_template_name, TO_CHAR(tdd.dtm_crt, 'DD-Mon-YYYY HH24:MI') as timee from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_lov msl on tdh.lov_doc_type = msl.id_lov join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join ms_lov ms on tdds.lov_signer_type = ms.id_lov join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where document_id = '"+documentid+"' ORDER BY tdds.id_document_d_sign asc limit 1")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getBuatUndanganDataPerusahaanStoreDB (Connection conn, String email){
		String data

		ArrayList<String> listdata = new ArrayList<>()

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select region, office, business_line, task_no from tr_invitation_link where receiver_detail = '"+ email +"'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getSignerKotakMasukSendDoc(Connection conn, String documentid){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select ms.description as signertype,amm.full_name as name, amm.login_id as email,CASE WHEN amm.is_active = '1' THEN 'Sudah Aktivasi' END as aktivasi, msl.description as status,CASE WHEN tdds.sign_date IS null THEN '-' END sign_date from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join ms_lov msl on tdd.lov_sign_status = msl.id_lov join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join tr_document_d_sign as tdds on tdd.id_document_d = tdds.id_document_d join ms_lov ms on tdds.lov_signer_type = ms.id_lov join am_msuser amm on tdds.id_ms_user = amm.id_ms_user where document_id = '"+documentid+"' ORDER BY tdds.id_document_d_sign asc")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getFeedbackStoreDB(Connection conn){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT feedback_value, comment FROM tr_feedback ORDER BY dtm_crt DESC LIMIT 1")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getLovTipePembayaran(Connection conn){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select description from ms_lov where lov_group = 'PAYMENT_SIGN_TYPE' order by description asc")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getTotalDokumenTemplatKode(Connection conn){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select count(*) from ms_doc_template where is_active = '1' and id_ms_tenant = 1")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

	@Keyword
	public getEmailLogin(Connection conn, String documentid){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT STRING_AGG(au.login_id, ';') AS login FROM tr_document_h AS tdh JOIN tr_document_d AS tdd ON tdh.id_document_h = tdd.id_document_h JOIN tr_document_d_sign AS tdds ON tdd.id_document_d = tdds.id_document_d JOIN am_msuser AS au ON au.id_ms_user = tdds.id_ms_user WHERE tdd.document_id = '"+documentid+"' ")
		
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

	public getSignerInfo(Connection conn, String refnum, String psre){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select lov.description, amm.full_name, amm.login_id, CASE WHEN mvru.is_registered = '1' AND mvru.is_active = '1' THEN 'Sudah Aktivasi' WHEN mvru.is_registered = '0' AND mvru.is_active = '0' THEN 'Belum Registrasi' ELSE 'Belum Aktivasi' END, CASE WHEN tdds.sign_date IS NOT null THEN 'Signed' ELSE 'Need Sign' END, CASE WHEN TO_char(tdds.sign_date, 'DD-Mon-YYYY HH24:MI') IS NOT null THEN TO_char(tdds.sign_date, 'DD-Mon-YYYY HH24:MI') ELSE '-' END from tr_document_d tdd join tr_document_h tdh on tdd.id_document_h = tdh.id_document_h join tr_document_d_sign tdds on tdd.id_document_d = tdds.id_document_d join am_msuser amm on tdds.id_ms_user = amm.id_ms_user join ms_lov lov on tdds.lov_signer_type = lov.id_lov join ms_vendor_registered_user mvru ON mvru.id_ms_user = amm.id_ms_user JOIN tr_document_signing_request tdsr ON tdsr.id_document_h = tdh.id_document_h JOIN ms_vendor mv ON mv.id_ms_vendor = mvru.id_ms_vendor where tdh.ref_number = '"+ refnum +"' and mv.vendor_code = '"+ psre +"'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getTotalPencarianDokumen (Connection conn, String loginid, String tenant){

		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT count(*) FROM tr_document_d dd JOIN LATERAL( select dds.* from tr_document_d_sign dds JOIN am_msuser amu ON amu.id_ms_user = dds.id_ms_user where dds.id_document_d = dd.id_document_d and login_id = '"+ loginid +"' limit 1) dds on true JOIN tr_document_h dh ON dh.id_document_h = dd.id_document_h AND dh.is_active = '1' JOIN ms_tenant mt ON dh.id_ms_tenant = mt.id_ms_tenant LEFT JOIN am_msuser usercust ON dh.id_msuser_customer = usercust.id_ms_user LEFT JOIN ms_doc_template dt ON dd.id_ms_doc_template = dt.id_doc_template JOIN ms_lov lovDocType ON dh.lov_doc_type = lovDocType.id_lov JOIN ms_lov lovSignStat ON dd.lov_sign_status = lovSignStat.id_lov JOIN ms_office office ON office.id_ms_office = dh.id_ms_office LEFT JOIN ms_region region ON region.id_ms_region = office.id_ms_region WHERE 1 = 1 and mt.tenant_code = '"+ tenant +"' and dd.request_date >= date_trunc('month', now()) and dd.request_date <= now()")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return Integer.parseInt(data)
	}

	@Keyword
	public getDDLTenant(Connection conn){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT Trim(tenant_name) FROM ms_tenant where is_active = '1'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getDDLVendor(Connection conn, String tenant){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT vendor_name FROM ms_vendor mv JOIN ms_vendoroftenant mvt ON mv.id_ms_vendor = mvt.id_ms_vendor JOIN ms_tenant mt ON mt.id_ms_tenant = mvt.id_ms_tenant where mv.is_active = '1' and tenant_name = '"+ tenant +"'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getDDLTipeSaldo(Connection conn, String tenant, String vendor){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select description from ms_balancevendoroftenant mbv JOIN ms_vendor mv ON mv.id_ms_vendor = mbv.id_ms_vendor JOIN ms_tenant mt ON mt.id_ms_tenant = mbv.id_ms_tenant JOIN ms_lov ml ON ml.id_lov = mbv.lov_balance_type where tenant_name = '"+ tenant +"' AND vendor_name = '"+ vendor +"'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getIsiSaldoStoreDB(Connection conn, String refno){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT tenant_name, vendor_name, description, qty, ref_no, notes, to_char(trx_date, 'yyyy-mm-dd') FROM tr_balance_mutation tbm JOIN ms_tenant mt ON mt.id_ms_tenant = tbm.id_ms_tenant JOIN ms_vendor mv ON mv.id_ms_vendor = tbm.id_ms_vendor JOIN ms_lov ml ON ml.id_lov = tbm.lov_balance_type WHERE ref_no = '"+ refno +"'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getOTPLoginId(Connection conn, String email){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select otp_code from am_msuser where login_id = '"+email+"'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

	@Keyword
	public gettrxSaldo(Connection conn){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select tbm.trx_no, TO_CHAR(tbm.dtm_crt,'YYYY-MM-DD HH24:MI:SS'), ml.description , amm.full_name, tdh.ref_number,ml_doc_h.code,mdt.doc_template_name, tbm.notes, tbm.qty from tr_balance_mutation as tbm join ms_lov as ml on tbm.lov_trx_type = ml.id_lov join am_msuser as amm on tbm.id_ms_user = amm.id_ms_user join tr_document_h as tdh on tbm.id_document_h = tdh.id_document_h join ms_lov as ml_doc_h on tdh.lov_doc_type = ml_doc_h.id_lov join tr_document_d as tdd on tbm.id_document_d = tdd.id_document_d join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template order by tbm.dtm_crt desc limit 1")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getRequestDateSendSign(Connection conn, String documentid){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select TO_char(request_date, 'DD-Mon-YYYY HH24:MI') from tr_document_d where document_id = '"+documentid+"'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

	@Keyword
	public getTotalTenant (Connection conn){

		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select count(*) from ms_tenant")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return Integer.parseInt(data)
	}

	@Keyword
	public getTenantStoreDB(Connection conn, String refnum){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select tenant_name, tenant_code, ref_number_label, api_key, threshold_balance, email_reminder_dest from ms_tenant where ref_number_label = '"+ refnum +"'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}

	@Keyword
	public getTenantServices(Connection conn, String tenantname){
		String data

		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select threshold_balance from ms_tenant where tenant_name = '"+ tenantname +"'")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

	@Keyword
	public getEmailsSign(Connection conn, String documentid){

		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("SELECT au.login_id FROM tr_document_h AS tdh JOIN tr_document_d AS tdd ON tdh.id_document_h = tdd.id_document_h JOIN tr_document_d_sign AS tdds ON tdd.id_document_d = tdds.id_document_d JOIN am_msuser AS au ON au.id_ms_user = tdds.id_ms_user WHERE tdd.document_id = '"+documentid+"' ")
		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for(int i = 1 ; i <= columnCount ; i++){
				data = resultSet.getObject(i)
				listdata.add(data)
			}
		}
		return listdata
	}
}
