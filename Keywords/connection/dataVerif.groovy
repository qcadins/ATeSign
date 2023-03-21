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
	public getSendDoc(Connection conn, String documentid, String email, String fullname){
		String data
		ArrayList<String> listdata = new ArrayList<>()
		Statement stm = conn.createStatement()

		ResultSet resultSet = stm.executeQuery("select amm.full_name, mst.tenant_code, tdh.ref_number, tdd.document_id,mdt.doc_template_code,mso.office_code,mso.office_name, msr.region_code,msr.region_name,mbl.business_line_code, mbl.business_line_name, mso.office_code as branch, tdd.is_sequence,msv.vendor_code, tdh.result_url,tdh.url_upload,msvr.signer_registered_email from tr_document_d as tdd join ms_tenant as mst on tdd.id_ms_tenant = mst.id_ms_tenant join tr_document_h as tdh on tdd.id_document_h = tdh.id_document_h join ms_lov as lov on tdd.lov_sign_status = lov.id_lov join ms_doc_template as mdt on tdd.id_ms_doc_template = mdt.id_doc_template join ms_office as mso on tdh.id_ms_office = mso.id_ms_office join ms_region as msr on mso.id_ms_region = msr.id_ms_region join ms_business_line as mbl on tdh.id_ms_business_line = mbl.id_ms_business_line join ms_vendor as msv on tdd.id_ms_vendor = msv.id_ms_vendor join am_msuser as amm on tdh.id_msuser_customer = amm.id_ms_user join ms_vendor_registered_user as msvr on amm.id_ms_user = msvr.id_ms_user where tdd.document_id = '" + documentid + "' and (amm.full_name = '"+ fullname.toUpperCase() +"' or msvr.signer_registered_email = '"+ email +"') ORDER BY tdd.dtm_crt DESC")

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
		return data
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
}
