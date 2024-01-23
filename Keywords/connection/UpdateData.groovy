package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class UpdateData {

	String data
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
	updateMustWALevelNotifSendDocAndManualSign(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND (ml.code = 'SEND_DOC' OR ml.code = 'MANUAL_SIGN_REQ') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifSendDocAndManualSign(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND (ml.code = 'SEND_DOC' OR ml.code = 'MANUAL_SIGN_REQ') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifSendDocAndManualSign(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND (ml.code = 'SEND_DOC' OR ml.code = 'MANUAL_SIGN_REQ') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifOTPSignNormal(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_NORMAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifOTPSignNormal(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_NORMAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifOTPSignNormal(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_NORMAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifOTPSignEmbedV2(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_EMBED_V2' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifOTPSignEmbedV2(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_EMBED_V2' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifOTPSignEmbedV2(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_EMBED_V2' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateMustWALevelNotifOTPSignExternal(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_EXTERNAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifOTPSignExternal(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_EXTERNAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifOTPSignExternal(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '"+value+"' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND ml.code = 'OTP_SIGN_EXTERNAL' AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}
	@Keyword
	updateExternalActivationVendorUser(Connection conn, int value, String email) {
		Statement stm = conn.createStatement()

		stm.executeUpdate("UPDATE ms_vendor_registered_user SET is_external_activation = '" + value + "' WHERE signer_registered_email = '" + email + "'")
	}
	@Keyword
	updateMustWALevelNotifGenInvLink(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET must_use_wa_first = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND (ml.code = 'GEN_INV' OR ml.code = 'GEN_INV_MENU') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateUseWAMsgLevelNotifGenInvLink(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET use_wa_message = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND (ml.code = 'GEN_INV' OR ml.code = 'GEN_INV_MENU') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}

	@Keyword
	updateOtpByEmailLevelNotifGenInvLink(Connection conn, int value) {
		Statement stm = conn.createStatement()
		if (value != 0) {
			stm.executeUpdate("UPDATE ms_notificationtypeoftenant nt0 SET send_otp_by_email = '" + value + "' FROM ms_notificationtypeoftenant nt JOIN ms_tenant ot on nt.id_ms_tenant = ot.id_ms_tenant JOIN ms_lov ml ON nt.lov_sending_point = ml.id_lov WHERE ot.tenant_code = '"+GlobalVariable.Tenant+"' AND (ml.code = 'GEN_INV' OR ml.code = 'GEN_INV_MENU') AND nt0.id_ms_notificationtypeoftenant = nt.id_ms_notificationtypeoftenant;")
		}
	}
}
