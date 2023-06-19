package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class InquiryInvitation {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

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
}
