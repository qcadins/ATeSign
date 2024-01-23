package connection
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

import internal.GlobalVariable
import customizekeyword.*
import connection.ManualStamp.*

public class UpdateData {

	String data, sheet = 'Main'
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	updateVendorOTP(Connection conn, String vendor, String number) {
		Statement stm = conn.createStatement()

		stm.executeUpdate("UPDATE ms_vendor SET must_user_vendor_otp = '" + number + "' WHERE vendor_code = '" + vendor + "';")
	}

	@Keyword
	updateTenantPassReq(Connection conn, String tenant, String number) {
		Statement stm = conn.createStatement()

		if (number != '') {
			stm.executeUpdate("UPDATE ms_tenant SET need_password_for_signing = '" + number + "' WHERE tenant_code = '" + tenant + "';")
		}
	}

	@Keyword
	updateTenantOTPReq(Connection conn, String tenant, String number) {
		Statement stm = conn.createStatement()

		if (number != '') {
			stm.executeUpdate("UPDATE ms_tenant SET need_otp_for_signing = '" + number + "' WHERE tenant_code = '" + tenant + "';")
		}
	}
	@Keyword
	settingLivenessFaceCompare(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_tenant SET use_liveness_facecompare_first = '" + value + "' WHERE tenant_code = '" + GlobalVariable.Tenant + "'")
		}
	}

	@Keyword
	updateVendorStamping(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_tenant SET lov_vendor_stamping = " + value + " WHERE tenant_code = '" + GlobalVariable.Tenant + "'")
		}
	}

	@Keyword
	updateMustWA(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_tenant SET lov_vendor_stamping = " + value + " WHERE tenant_code = '" + GlobalVariable.Tenant + "'")
		}
	}

	@Keyword
	updateMustWALevelNotifSendDocAndManualSign(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND (ml.code = 'SEND_DOC' OR ml.code = 'MANUAL_SIGN_REQ') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifSendDocAndManualSign(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND (ml.code = 'SEND_DOC' OR ml.code = 'MANUAL_SIGN_REQ') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifSendDocAndManualSign(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND (ml.code = 'SEND_DOC' OR ml.code = 'MANUAL_SIGN_REQ') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifOTPSignNormal(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_NORMAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifOTPSignNormal(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_NORMAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifOTPSignNormal(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_NORMAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifOTPSignEmbedV2(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_EMBED_V2' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifOTPSignEmbedV2(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_EMBED_V2' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifOTPSignEmbedV2(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_EMBED_V2' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifOTPSignExternal(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_EXTERNAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifOTPSignExternal(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_EXTERNAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifOTPSignExternal(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_EXTERNAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateExternalActivationVendorUser(Connection conn, int value, String email) {
		Statement stm = conn.createStatement()

		stm.executeUpdate("UPDATE ms_vendor_registered_user SET is_external_activation = '" + value + "' WHERE signer_registered_email = '" + email + "'")
	}

	@Keyword
	updateDBLevelTenant(Connection conneSign, String excelPathMain, String sheets) {
		sheet = sheets
		'get connection apifullservice'
		APIFullService APIFullService = new APIFullService()

		'get connection SendSign'
		SendSign SendSign = new SendSign()

		'setting menggunakan Must WA'
		APIFullService.settingMustUseWAFirst(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must WA Level Tenant')))

		'setting menggunakan Use Wa Message'
		APIFullService.settingUseWAMessage(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message Level Tenant')))

		'update setting sent otp by email'
		SendSign.settingSentOTPbyEmail(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Send Otp By Email Level Tenant')))
	}

	@Keyword
	updateDBOTPNormalLevelNotification(Connection conneSign, String excelPathMain, String sheets) {
		sheet = sheets

		'Setting must wa level notif otp signing normal'
		updateMustWALevelNotifOTPSignNormal(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must WA Level Notification')))

		'Setting use wa level notif otp signing normal'
		updateUseWAMsgLevelNotifOTPSignNormal(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message Level Notification')))

		'Setting otp by email level notif otp signing normal'
		updateOtpByEmailLevelNotifOTPSignNormal(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Send Otp By Email Level Notification')))
	}

	@Keyword
	updateDBOTPExternalLevelNotification(Connection conneSign, String excelPathMain, String sheets) {
		sheet = sheets
		'Setting must wa level notif otp signing normal'
		updateMustWALevelNotifOTPSignExternal(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must WA Level Notification')))

		'Setting use wa level notif otp signing normal'
		updateUseWAMsgLevelNotifOTPSignExternal(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message Level Notification')))

		'Setting otp by email level notif otp signing normal'
		updateOtpByEmailLevelNotifOTPSignExternal(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Send Otp By Email Level Notification')))
	}

	@Keyword
	updateDBOTPSignEmbedV2LevelNotification(Connection conneSign, String excelPathMain, String sheets) {
		sheet = sheets
		'Setting must wa level notif otp signing normal'
		updateMustWALevelNotifOTPSignEmbedV2(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must WA Level Notification')))

		'Setting use wa level notif otp signing normal'
		updateUseWAMsgLevelNotifOTPSignEmbedV2(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message Level Notification')))

		'Setting otp by email level notif otp signing normal'
		updateOtpByEmailLevelNotifOTPSignEmbedV2(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Send Otp By Email Level Notification')))
	}

	@Keyword
	updateDBMainFlowBefore(Connection conneSign, String excelPathMain) {
		'Setting must wa level notif send doc'
		updateMustWALevelNotifSendDocAndManualSign(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Send Document & Manual Sign Request)')))

		'Setting use wa level notif send doc'
		updateUseWAMsgLevelNotifSendDocAndManualSign(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Send Document & Manual Sign Request)')))

		'Setting otp by email level notif send doc'
		updateOtpByEmailLevelNotifSendDocAndManualSign(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP By Email (Send Document & Manual Sign Request)')))

		'Setting must wa level notif otp sign embed v2'
		updateMustWALevelNotifOTPSignEmbedV2(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (OTP Sign Embed V2 Flow)')))

		'Setting use wa level notif otp sign embed v2'
		updateUseWAMsgLevelNotifOTPSignEmbedV2(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (OTP Sign Embed V2 Flow)')))

		'Setting otp by email level notif otp sign embed v2'
		updateOtpByEmailLevelNotifOTPSignEmbedV2(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP By Email (OTP Sign Embed V2 Flow)')))

		'Setting must wa level notif OTP Sign External'
		updateMustWALevelNotifOTPSignExternal(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (OTP Sign External Flow)')))

		'Setting use wa level notif OTP Sign External'
		updateUseWAMsgLevelNotifOTPSignExternal(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (OTP Sign External Flow)')))

		'Setting otp by email level notif OTP Sign External'
		updateOtpByEmailLevelNotifOTPSignExternal(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP By Email (OTP Sign External Flow)')))

		'get connection apifullservice'
		APIFullService APIFullService = new APIFullService()

		'get connection SendSign'
		SendSign SendSign = new SendSign()

		'get connection ManualStamp'
		ManualStamp ManualStamp = new ManualStamp()

		'setting menggunakan Must WA'
		APIFullService.settingMustUseWAFirst(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Level Tenant)')))

		'setting menggunakan Use Wa Message'
		APIFullService.settingUseWAMessage(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Level Tenant)')))

		'update setting sent otp by email'
		SendSign.settingSentOTPbyEmail(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email (Level Tenant)')))

		'ambil idLov untuk diupdate secara otomatis ke DB'
		int idLov = ManualStamp.getIdLovVendorStamping(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')))

		if (idLov != 0) {
			'lakukan update vendor stamping yang akan dipakai'
			updateVendorStamping(conneSign, idLov)
		}

		'update setting vendor otp ke table di DB'
		settingLivenessFaceCompare(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting use_liveness_facecompare_first')))

		'update setting otp ke table di DB'
		updateTenantOTPReq(conneSign, GlobalVariable.Tenant, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Enable Need OTP for signing?')))

		'update setting pass tenant ke table di DB'
		updateTenantPassReq(conneSign, GlobalVariable.Tenant, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Enable Need Password for signing?')))

		'Setting OTP Active Duration'
		APIFullService.settingOTPActiveDuration(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')))
	}

	@Keyword
	def rowExcel(String cellValue) {
		WriteExcel needto = new WriteExcel()
		return needto.getExcelRow(GlobalVariable.DataFilePath, sheet, cellValue)
	}

	@Keyword
	updateMustWALevelNotifGenInvLink(Connection conn, String value) {
		Statement stm = conn.createStatement()

		stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND (ml.code = 'GEN_INV' OR ml.code = 'GEN_INV_MENU') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
	}

	@Keyword
	updateUseWAMsgLevelNotifGenInvLink(Connection conn, String value) {
		Statement stm = conn.createStatement()

		stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND (ml.code = 'GEN_INV' OR ml.code = 'GEN_INV_MENU') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
	}

	@Keyword
	updateOtpByEmailLevelNotifGenInvLink(Connection conn, String value) {
		Statement stm = conn.createStatement()

		stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND (ml.code = 'GEN_INV' OR ml.code = 'GEN_INV_MENU') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
	}
}

