package connection

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword

import internal.GlobalVariable
import customizekeyword.WriteExcel

class UpdateData {

	String data = '', sheet = 'Main'
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
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND (ml.code = 'SEND_DOC' OR ml.code = 'MANUAL_SIGN_REQ') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifSendDocAndManualSign(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND (ml.code = 'SEND_DOC' OR ml.code = 'MANUAL_SIGN_REQ') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifSendDocAndManualSign(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND (ml.code = 'SEND_DOC' OR ml.code = 'MANUAL_SIGN_REQ') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifOTPSignNormal(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'OTP_SIGN_NORMAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifOTPSignNormal(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'OTP_SIGN_NORMAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifOTPSignNormal(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'OTP_SIGN_NORMAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifOTPSignEmbedV2(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'OTP_SIGN_EMBED_V2' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifOTPSignEmbedV2(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'OTP_SIGN_EMBED_V2' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifOTPSignEmbedV2(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'OTP_SIGN_EMBED_V2' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifOTPSignExternal(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'OTP_SIGN_EXTERNAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifOTPSignExternal(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'OTP_SIGN_EXTERNAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifOTPSignExternal(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'OTP_SIGN_EXTERNAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
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
		APIFullService apiFullService = new APIFullService()

		'get connection SendSign'
		SendSign sendSign = new SendSign()

		'setting menggunakan Must WA'
		apiFullService.settingMustUseWAFirst(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must WA Level Tenant')))

		'setting menggunakan Use Wa Message'
		apiFullService.settingUseWAMessage(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message Level Tenant')))

		'update setting sent otp by email'
		sendSign.settingSentOTPbyEmail(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Send Otp By Email Level Tenant')))
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
		'get connection apifullservice'
		APIFullService apiFullService = new APIFullService()

		'get connection SendSign'
		SendSign sendSign = new SendSign()

		'get connection ManualStamp'
		ManualStamp manualStamp = new ManualStamp()

		if (checkNotifTypeExistforTenant(conneSign) > 0) {
			'Setting must wa level notif send doc'
			updateMustWALevelNotifSendDocAndManualSign(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Send Document & Manual Sign Request)')))

			'Setting use wa level notif send doc'
			updateUseWAMsgLevelNotifSendDocAndManualSign(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Send Document & Manual Sign Request)')))

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

			'Setting must wa level notif otp signing normal'
			updateMustWALevelNotifResendSignNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Resend Sign Notification)')))

			'Setting use wa level notif otp signing normal'
			updateUseWAMsgLevelNotifResendSignNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Resend Sign Notification)')))

			'Setting must wa level notif otp signing normal'
			updateMustWALevelNotifResendSignNotifEmbedV1(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Resend Sign Notification Embed V1)')))

			'Setting use wa level notif otp signing normal'
			updateUseWAMsgLevelNotifResendSignNotifEmbedV1(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Resend Sign Notification Embed V1)')))

			'Setting must wa level notif otp signing normal'
			updateMustWALevelNotifResendSignNotifEmbedV2(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Resend Sign Notification Embed V2)')))

			'Setting use wa level notif otp signing normal'
			updateUseWAMsgLevelNotifResendSignNotifEmbedV2(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Resend Sign Notification Embed V2)')))
		} else {
			'setting menggunakan Must WA'
			apiFullService.settingMustUseWAFirst(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Level Tenant)')))

			'setting menggunakan Use Wa Message'
			apiFullService.settingUseWAMessage(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Level Tenant)')))

			'update setting sent otp by email'
			sendSign.settingSentOTPbyEmail(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email (Level Tenant)')))
		}
		'ambil idLov untuk diupdate secara otomatis ke DB'
		int idLov = manualStamp.getIdLovVendorStamping(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Vendor for Stamping')))

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

		'update setting otp ke table di DB'
		updateVendorOTP(conneSign, GlobalVariable.Psre, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting must_user_vendor_otp?')))

		'Setting OTP Active Duration'
		apiFullService.settingOTPActiveDuration(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting OTP Active Duration')))
	}

	@Keyword
	rowExcel(String cellValue) {
		WriteExcel needto = new WriteExcel()
		needto.getExcelRow(GlobalVariable.DataFilePath, sheet, cellValue)
	}

	@Keyword
	updateMustWALevelNotifGenInvLink(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'GEN_INV' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifGenInvLink(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'GEN_INV' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifGenInvLink(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'GEN_INV' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifGenInvMenu(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'GEN_INV_MENU' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifGenInvMenu(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'GEN_INV_MENU' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifGenInvMenu(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'GEN_INV_MENU' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifRegenLink(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'REGEN_INV' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifRegenLink(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'REGEN_INV' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifRegenLink(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'REGEN_INV' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifResendInv(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'RESEND_INV' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifResendInv(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'RESEND_INV' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifResendInv(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'RESEND_INV' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifOTPAct(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'OTP_ACT' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifOTPAct(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'OTP_ACT' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifOTPAct(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'OTP_ACT' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifCert(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'CERT_NOTIF' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifCert(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'CERT_NOTIF' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifCert(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'CERT_NOTIF' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateDBNotifTypeMultiPointOTPAct(Connection conneSign, String excelPathMain, String sheets) {
		sheet = sheets

		'Setting must wa level notif'
		updateMustWALevelNotifGenInvLink(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First - Gen Inv')))

		'Setting use wa level notif'
		updateUseWAMsgLevelNotifGenInvLink(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message - Gen Inv')))

		'Setting otp by email level'
		updateOtpByEmailLevelNotifGenInvLink(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email - Gen Inv')))

		'Setting must wa level notif'
		updateMustWALevelNotifGenInvMenu(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First - Gen Inv Menu')))

		'Setting use wa level notif'
		updateUseWAMsgLevelNotifGenInvMenu(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message - Gen Inv Menu')))

		'Setting otp by email level'
		updateOtpByEmailLevelNotifGenInvMenu(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email - Gen Inv Menu')))

		'Setting must wa level notif'
		updateMustWALevelNotifRegenLink(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First - Regen Link')))

		'Setting use wa level notif'
		updateUseWAMsgLevelNotifRegenLink(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message - Regen Link')))

		'Setting otp by email level'
		updateOtpByEmailLevelNotifRegenLink(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email - Regen Link')))

		'Setting must wa level notif'
		updateMustWALevelNotifResendInv(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First - Resend Inv')))

		'Setting use wa level notif'
		updateUseWAMsgLevelNotifResendInv(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message - Resend Inv')))

		'Setting otp by email level'
		updateOtpByEmailLevelNotifResendInv(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email - Resend Inv')))

		'Setting must wa level notif'
		updateMustWALevelNotifOTPAct(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First - OTP Act')))

		'Setting use wa level notif'
		updateUseWAMsgLevelNotifOTPAct(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message - OTP Act')))

		'Setting otp by email level'
		updateOtpByEmailLevelNotifOTPAct(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email - OTP Act')))

		'Setting must wa level notif'
		updateMustWALevelNotifCert(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First - Cert Notif')))

		'Setting use wa level notif'
		updateUseWAMsgLevelNotifCert(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message - Cert Notif')))

		'Setting otp by email level'
		updateOtpByEmailLevelNotifCert(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email - Cert Notif')))
	}

	@Keyword
	checkNotifTypeExistforTenant(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(*) from ms_notificationtypeoftenant mn join ms_tenant mt on mn.id_ms_tenant = mt.id_ms_tenant where tenant_code = '" + GlobalVariable.Tenant + "'")

		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		Integer.parseInt(data)
	}

	@Keyword
	updateDBMainRegister(Connection conneSign, String excelPathMain, String sheets) {
		sheet = sheets

		println(sheet)
		'get connection apifullservice'
		APIFullService apiFullService = new APIFullService()
		Registrasi registrasi = new Registrasi()

		'setting email services'
		apiFullService.settingEmailServiceTenant(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Email Service')))

		'setting sms notif'
		registrasi.settingSendCertNotifbySMS(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Certif Notif')))

		'setting allow regenerate link'
		apiFullService.settingAllowRegenerateLink(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Allow Regenarate Link')))

		'setting sned sms gen invitation'
		apiFullService.settingSendSMSGenInv(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS GenInv')))

		'setting sned WA gen invitation'
		apiFullService.settingSendWAGenInv(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send WA GenInv')))

		if (checkNotifTypeExistforTenant(conneSign) > 0) {
			'update notif pada table ms_notificationtypeoftenant'
			updateDBNotifTypeMultiPointOTPAct(conneSign, excelPathMain, sheets)
		} else {
			'setting must use wa first ms_tenant'
			apiFullService.settingMustUseWAFirst(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Must Use WA First')))

			'setting use wa message ms_tenant'
			apiFullService.settingUseWAMessage(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Use WA Message')))

			'setting sent otp by email ms_tenant'
			apiFullService.settingSentOTPByEmail(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Sent OTP by Email')))
		}
	}

	@Keyword
	updateMustWALevelNotifResendSignNotif(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'RESEND_SIGN_NOTIF' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifResendSignNotif(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'RESEND_SIGN_NOTIF' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifResendSignNotif(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'RESEND_SIGN_NOTIF' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifResendSignNotifEmbedV1(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'RESEND_SIGN_NOTIF_EMBED_V1' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifResendSignNotifEmbedV1(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'RESEND_SIGN_NOTIF_EMBED_V1' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifResendSignNotifEmbedV1(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'RESEND_SIGN_NOTIF_EMBED_V1' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifResendSignNotifEmbedV2(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'RESEND_SIGN_NOTIF_EMBED_V2' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifResendSignNotifEmbedV2(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'RESEND_SIGN_NOTIF_EMBED_V2' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifResendSignNotifEmbedV2(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.code = 'RESEND_SIGN_NOTIF_EMBED_V2' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateDBDocumentMonitoring(Connection conneSign, String excelPathMain, String sheets) {
		sheet = sheets
		if (checkNotifTypeExistforTenant(conneSign) > 0) {
			'Setting must wa level notif otp signing normal'
			updateMustWALevelNotifResendSignNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Resend Sign Notification)')))

			'Setting use wa level notif otp signing normal'
			updateUseWAMsgLevelNotifResendSignNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Resend Sign Notification)')))

			'Setting must wa level notif otp signing normal'
			updateMustWALevelNotifResendSignNotifEmbedV1(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Resend Sign Notification Embed V1)')))

			'Setting use wa level notif otp signing normal'
			updateUseWAMsgLevelNotifResendSignNotifEmbedV1(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Resend Sign Notification Embed V1)')))

			'Setting must wa level notif otp signing normal'
			updateMustWALevelNotifResendSignNotifEmbedV2(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Resend Sign Notification Embed V2)')))

			'Setting use wa level notif otp signing normal'
			updateUseWAMsgLevelNotifResendSignNotifEmbedV2(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Resend Sign Notification Embed V2)')))
		} else {
			'get connection apifullservice'
			APIFullService apiFullService = new APIFullService()

			'get connection SendSign'
			SendSign sendSign = new SendSign()

			'get connection ManualStamp'
			ManualStamp manualStamp = new ManualStamp()

			'setting menggunakan Must WA'
			apiFullService.settingMustUseWAFirst(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Level Tenant)')))

			'setting menggunakan Use Wa Message'
			apiFullService.settingUseWAMessage(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Level Tenant)')))
		}
	}

	@Keyword
	updateMustWALevelNotifSendDoc(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND (ml.code = 'SEND_DOC') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifSendDoc(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND (ml.code = 'SEND_DOC') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifSendDoc(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND (ml.code = 'SEND_DOC') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifManualSign(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND (ml.code = 'MANUAL_SIGN_REQ') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifManualSign(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND (ml.code = 'MANUAL_SIGN_REQ') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifManualSign(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND (ml.code = 'MANUAL_SIGN_REQ') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateDBSendDocLevelNotification(Connection conneSign, String excelPathMain, String sheets) {
		sheet = sheets
		if (checkNotifTypeExistforTenant(conneSign) > 0) {
			'Setting must wa level notif otp signing normal'
			updateMustWALevelNotifSendDoc(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must WA Level Notification')))

			'Setting use wa level notif otp signing normal'
			updateUseWAMsgLevelNotifSendDoc(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message Level Notification')))
		} else {
			'get connection apifullservice'
			APIFullService apiFullService = new APIFullService()

			'setting menggunakan Must WA'
			apiFullService.settingMustUseWAFirst(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Level Tenant)')))

			'setting menggunakan Use Wa Message'
			apiFullService.settingUseWAMessage(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Level Tenant)')))
		}
	}
}
