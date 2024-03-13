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
	updateLOVSMSGatewayLevelTenant(Connection conn, String smsGateway) {
		Statement stm = conn.createStatement()

		if (smsGateway != 'Value First' && smsGateway != 'Jatis') {
			data = '1'
		} else {
			resultSet = stm.executeQuery("select id_lov from ms_lov where description = '" + smsGateway + "'")

			metadata = resultSet.metaData

			columnCount = metadata.getColumnCount()

			while (resultSet.next()) {
				data = resultSet.getObject(1)
			}
		}

		stm.executeUpdate("UPDATE ms_tenant SET lov_sms_gateway = '" + data + "' WHERE tenant_code = '" + GlobalVariable.Tenant + "'")
	}

	@Keyword
	updateLOVSMSGatewayLevelNotif(Connection conn, String smsGateway, String sendingPoint) {
		stm = conn.createStatement()

		if (smsGateway != '') {
			if (smsGateway != 'Value First' && smsGateway != 'Jatis') {
				data = '1'
			} else {
				resultSet = stm.executeQuery("select id_lov from ms_lov where description = '" + smsGateway + "'")

				metadata = resultSet.metaData

				columnCount = metadata.getColumnCount()

				while (resultSet.next()) {
					data = resultSet.getObject(1)
				}
			}
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET lov_sms_gateway = '" + data + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND ml.description = '" + sendingPoint + "' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateLOVSendingPointLevelNotif(Connection conn, String value, String sendingPoint) {
		String hardCode

		switch (sendingPoint) {
			case 'Send Document':
				hardCode = '1'
			case 'Manual Sign Request':
				hardCode = '2'
			case 'OTP Sign Normal Flow':
				hardCode = '3'
			case 'OTP Sign Embed V2 Flow':
				hardCode = '4'
			case 'OTP Sign External Flow':
				hardCode = '5'
			case 'Resend Sign Notification':
				hardCode = '6'
			case 'Resend Sign Notification Embed V1':
				hardCode = '7'
			case 'Resend Sign Notification Embed V2':
				hardCode = '8'
		}

		'Nembak Sending Point Send Document. Wrong Sending Point hardcode = 1.'
		String dataComparison

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select id_lov from ms_lov where description = '" + sendingPoint + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		resultSet = stm.executeQuery("select mn.lov_sending_point from ms_notificationtypeoftenant mn left join ms_lov msl on msl.id_lov = mn.lov_sending_point where msl.description = '" + sendingPoint + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			dataComparison = resultSet.getObject(1)
		}

		if (value == 'Yes' || value == '1') {
			if (data != dataComparison) {
				println data
				println dataComparison
				stm.executeUpdate("UPDATE ms_notificationtypeoftenant set lov_sending_point = '" + data + "' WHERE lov_sending_point = '" + hardCode + "'")
			}
		} else if (value == 'No' || value == '0') {
			if (data == dataComparison) {
				println data
				println dataComparison
				stm.executeUpdate("UPDATE ms_notificationtypeoftenant set lov_sending_point = '" + hardCode + "' WHERE lov_sending_point = '" + data + "'")
			}
		}
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
		'LOV Sending Point Send Document'
		updateLOVSendingPointLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Sending Point to Send Document ?')), 'Send Document')

		'LOV Sending Point Manual Sign Request'
		updateLOVSendingPointLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Sending Point to Manual Sign Request ?')), 'Manual Sign Request')

		'LOV Sending Point OTP Sign Normal Flow'
		updateLOVSendingPointLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Sending Point to OTP Sign Normal Flow ?')), 'OTP Sign Normal Flow')

		'LOV Sending Point OTP Sign Embed V2 Flow'
		updateLOVSendingPointLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Sending Point to OTP Sign Embed V2 Flow ?')), 'OTP Sign Embed V2 Flow')

		'LOV Sending Point OTP Sign External Flow'
		updateLOVSendingPointLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Sending Point to OTP Sign External Flow ?')), 'OTP Sign External Flow')

		'LOV Sending Point Resend Sign Notification'
		updateLOVSendingPointLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Sending Point to Resend Sign Notification ?')), 'Resend Sign Notification')

		'LOV Sending Point Resend Sign Notification Embed V1'
		updateLOVSendingPointLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Sending Point to Resend Sign Notification Embed V1 ?')), 'Resend Sign Notification Embed V1')

		'LOV Sending Point Resend Sign Notification Embed V2'
		updateLOVSendingPointLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Sending Point to Resend Sign Notification Embed V2 ?')), 'Resend Sign Notification Embed V2')

		'LOV SMS Gateway Level Notif untuk Send Document'
		updateLOVSMSGatewayLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Gateway for Send Document ?')), 'Send Document')

		'LOV SMS Gateway Level Notif untuk Manual Sign Request'
		updateLOVSMSGatewayLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Gateway for Manual Sign Request ?')), 'Manual Sign Request')

		'LOV SMS Gateway Level Notif untuk OTP Sign Normal Flow'
		updateLOVSMSGatewayLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Gateway for OTP Sign Normal Flow ?')), 'OTP Sign Normal Flow')

		'LOV SMS Gateway Level Notif untuk OTP Sign Embed V2 Flow'
		updateLOVSMSGatewayLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Gateway for OTP Sign Embed V2 Flow ?')), 'OTP Sign Embed V2 Flow')

		'LOV SMS Gateway Level Notif untuk OTP Sign External Flow'
		updateLOVSMSGatewayLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Gateway for OTP Sign External Flow ?')), 'OTP Sign External Flow')

		'LOV SMS Gateway Level Notif untuk Resend Sign Notification'
		updateLOVSMSGatewayLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Gateway for Resend Sign Notification ?')), 'Resend Sign Notification')

		'LOV SMS Gateway Level Notif untuk Resend Sign Notification Embed V1'
		updateLOVSMSGatewayLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Gateway for Resend Sign Notification Embed V1 ?')), 'Resend Sign Notification Embed V1')

		'LOV SMS Gateway Level Notif untuk Resend Sign Notification Embed V2'
		updateLOVSMSGatewayLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Gateway for Resend Sign Notification Embed V2 ?')), 'Resend Sign Notification Embed V2')

		'LOV SMS Gateway Level Notif untuk OTP Sign Embed V2 Flow'
		updateLOVSMSGatewayLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Gateway for OTP Sign Embed V2 Flow ?')), 'OTP Sign Embed V2 Flow')

		'LOV SMS Gateway Level Tenant'
		updateLOVSMSGatewayLevelTenant(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting SMS Gateway for Level Tenant ?')))

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

		'setting send wa send doc general setting'
		apiFullService.settingSendWASendDoc(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send WA SendDoc')))

		'setting send sms send doc general setting'
		apiFullService.settingSendSMSSendDoc(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS SendDoc')))

		'setting send sms send doc general setting'
		apiFullService.settingSendSMSOtpUser(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS OTP For User')))
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
		'LOV Sending Point Send Document'
		updateLOVSendingPointLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Sending Point to Generate Invitation ?')), 'Generate Invitation')

		'LOV Sending Point Send Document'
		updateLOVSendingPointLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Sending Point to Generate Invitation Menu ?')), 'Generate Invitation Menu')

		'LOV Sending Point Send Document'
		updateLOVSendingPointLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Sending Point to Regenerate Invitation ?')), 'Regenerate Invitation')

		'LOV Sending Point Send Document'
		updateLOVSendingPointLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Sending Point to Resend Invitation ?')), 'Resend Invitation')

		'LOV Sending Point Send Document'
		updateLOVSendingPointLevelNotif(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Sending Point to Certificate Notification ?')), 'Certificate Notification')

		'LOV Balance Type SMS dan WA'
		updateLOVBalanceMutationBalanceVendorOfTenant(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Balance Type for Tenant and Vendor about SMS ?')), 'SMS Notif', GlobalVariable.Tenant)

		'LOV Balance Type SMS dan WA'
		updateLOVBalanceMutationBalanceVendorOfTenant(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Balance Type for Tenant and Vendor about WhatsApp ?')), 'WhatsApp Message', GlobalVariable.Tenant)
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

		'get connection apifullservice'
		APIFullService apiFullService = new APIFullService()

		if (checkNotifTypeExistforTenant(conneSign) > 0) {
			'Setting must wa level notif otp signing normal'
			updateMustWALevelNotifSendDoc(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must WA Level Notification')))

			'Setting use wa level notif otp signing normal'
			updateUseWAMsgLevelNotifSendDoc(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message Level Notification')))
		} else {
			'setting menggunakan Must WA'
			apiFullService.settingMustUseWAFirst(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Level Tenant)')))

			'setting menggunakan Use Wa Message'
			apiFullService.settingUseWAMessage(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Level Tenant)')))
		}
		'setting send wa send doc general setting'
		apiFullService.settingSendWASendDoc(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send WA SendDoc')))

		'setting send sms send doc general setting'
		apiFullService.settingSendSMSSendDoc(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS SendDoc')))
	}

	@Keyword
	updateDBManualSignLevelNotification(Connection conneSign, String excelPathMain, String sheets) {
		sheet = sheets

		'get connection apifullservice'
		APIFullService apiFullService = new APIFullService()

		if (checkNotifTypeExistforTenant(conneSign) > 0) {
			'Setting must wa level notif otp signing normal'
			updateMustWALevelNotifManualSign(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must WA Level Notification')))

			'Setting use wa level notif otp signing normal'
			updateUseWAMsgLevelNotifManualSign(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message Level Notification')))
		} else {
			'setting menggunakan Must WA'
			apiFullService.settingMustUseWAFirst(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Level Tenant)')))

			'setting menggunakan Use Wa Message'
			apiFullService.settingUseWAMessage(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Level Tenant)')))
		}
		'setting send wa send doc general setting'
		apiFullService.settingSendWASendDoc(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send WA SendDoc')))

		'setting send sms send doc general setting'
		apiFullService.settingSendSMSSendDoc(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS SendDoc')))
	}

	@Keyword
	updateMustWALevelNotifForgotPassword(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND (ml.code = 'RESET_PASSWORD') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifForgotPassword(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND (ml.code = 'RESET_PASSWORD') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifForgotPassword(Connection conn, String value) {
		Statement stm = conn.createStatement()
		if (value != '') {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '" + GlobalVariable.Tenant + "' AND (ml.code = 'RESET_PASSWORD') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateDBForgotPasswordLevelNotification(Connection conneSign, String excelPathMain, String sheets) {
		sheet = sheets

		'get connection apifullservice'
		APIFullService apiFullService = new APIFullService()

		if (checkNotifTypeExistforTenant(conneSign) > 0) {
			'Setting must wa level notif otp signing normal'
			updateMustWALevelNotifForgotPassword(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must WA Level Notification')))

			'Setting use wa level notif otp signing normal'
			updateUseWAMsgLevelNotifForgotPassword(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message Level Notification')))

			'Setting use wa level notif otp signing normal'
			updateOtpByEmailLevelNotifForgotPassword(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Send Otp By Email Level Notification')))
		} else {
			'setting menggunakan Must WA'
			apiFullService.settingMustUseWAFirst(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Must Wa (Level Tenant)')))

			'setting menggunakan Use Wa Message'
			apiFullService.settingUseWAMessage(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Use WA Message (Level Tenant)')))

			'setting menggunakan Use Wa Message'
			apiFullService.settingSentOTPByEmail(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Send Otp By Email (Level Tenant)')))
		}
		'setting send wa send doc general setting'
		apiFullService.settingSendWAForgotPassword(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send WA ForgotPassword')))

		'setting send sms send doc general setting'
		apiFullService.settingSendSMSForgotPassword(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting Send SMS ForgotPassword')))
	}

	@Keyword
	updateLOVBalanceMutationBalanceVendorOfTenant(Connection conn, String value, String balanceType, String tenantCode) {
		String hardCode

		if (balanceType == 'WhatsApp Message') {
			hardCode = '9'
		} else if (balanceType == 'SMS Notif') {
			hardCode = '10'
		}

		'Nembak Sending Point Send Document. Wrong Sending Point hardcode = 1.'
		String dataComparison, dataUpdated

		stm = conn.createStatement()

		resultSet = stm.executeQuery("select id_lov from ms_lov where description = '" + balanceType + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}

		resultSet = stm.executeQuery("select mbvot.lov_balance_type, mbvot.id_balancevendoroftenant from ms_balancevendoroftenant mbvot left join ms_tenant mst on mbvot.id_ms_tenant = mst.id_ms_tenant left join ms_lov msl on mbvot.lov_balance_type = msl.id_lov where msl.description = '" + balanceType + "' and mst.tenant_code = '" + tenantCode + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			dataComparison = resultSet.getObject(1)
			dataUpdated = resultSet.getObject(2)
		}

		if (value == 'Yes' || value == '1') {
			if (data != dataComparison) {
				println data
				println dataComparison
				stm.executeUpdate("UPDATE ms_balancevendoroftenant set lov_balance_type = '" + data + "' WHERE lov_balance_type = '" + hardCode + "'")
			}
		} else if (value == 'No' || value == '0') {
			if (data == dataComparison) {
				println data
				println dataComparison
				stm.executeUpdate("UPDATE ms_balancevendoroftenant set lov_balance_type = '" + hardCode + "' WHERE id_balancevendoroftenant = '" + dataUpdated + "'")
			}
		}
	}
	@Keyword
	updateLOVBalanceMutationBalanceVendorOfTenantPackage(Connection conneSign, String excelPathMain, String sheets) {
		sheet = sheets
		
		'LOV Balance Type SMS dan WA'
		updateLOVBalanceMutationBalanceVendorOfTenant(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Balance Type for Tenant and Vendor about SMS ?')), 'SMS Notif', GlobalVariable.Tenant)
	
		'LOV Balance Type SMS dan WA'
		updateLOVBalanceMutationBalanceVendorOfTenant(conneSign, findTestData(excelPathMain).getValue(GlobalVariable.NumofColm, rowExcel('Setting True LOV Balance Type for Tenant and Vendor about WhatsApp ?')), 'WhatsApp Message', GlobalVariable.Tenant)
	
	}
}
