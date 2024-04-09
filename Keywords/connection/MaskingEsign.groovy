package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import org.apache.commons.lang.StringUtils

class MaskingEsign {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []
	HashMap<String, String> commandSql = [:]

	@Keyword
	protected static String maskData(String data) {
		Character asterisk = '*'
		if (StringUtils.isBlank(data)) {
			return data
		}

		// Mask phone number
		if (StringUtils.isNumeric(data)) {
			return maskString(data, 4, 3, asterisk)
		}

		// Mask email
		if (data.contains("@")) {
			String beforeAt = data.split("@")[0]
			int prefix = beforeAt.length() > 7 ? 5 : beforeAt.length() / 2
			return maskEmailAddress(data, prefix)
		}

		// Else, mask name
		return maskName(data)
	}

	private static String maskString(String input, int unmaskedPrefix, int unmaskedSuffix, char maskChar) {
		if (StringUtils.isBlank(input) || input.length() <= unmaskedPrefix + unmaskedSuffix) {
			return input
		}

		String prefixString = input[0..<unmaskedPrefix]
		String suffixString = input[input.length() - unmaskedSuffix..-1]

		def charsLengthToMask = input.length() - unmaskedPrefix - unmaskedSuffix
		def mask = ''
		(1..charsLengthToMask).each { mask += maskChar }

		return "${prefixString}${mask}${suffixString}"
	}

	private static String maskEmailAddress(String email, int prefixLength) {
		Character asterisk = '*'
		if (StringUtils.isBlank(email)) {
			return email
		}
		// Regex example: (?<=.{3}).(?=.*@)
		def regex = "(?<=.{$prefixLength}).(?=.*@)"
		return email.replaceAll(regex, "*")
	}

	private static String maskName(String name) {
		Character asterisk = '*'
		def separated = name.split(" ")
		def masked = new StringBuilder()
		separated.each { n ->
			def nMasked
			if (n.length() > 6) {
				nMasked = maskString(n, 2, 2, asterisk)
			} else {
				nMasked = maskString(n, 1, 1, asterisk)
			}

			if (masked.toString().length() == 0) {
				masked.append(nMasked)
			} else {
				masked.append(" ").append(nMasked)
			}
		}

		return masked.toString()
	}

	@Keyword
	getTableName(Connection conn) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'esign' order by table_name asc")
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
	verifyUserCrtUserDpd(Connection conn, String tableName) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT DISTINCT column_name FROM information_schema.columns WHERE table_name = '" + tableName + "' AND (column_name = 'usr_crt' OR column_name = 'usr_upd') ;")
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
	getLastDataTable(Connection conn, String tableName) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("SELECT usr_crt, usr_upd FROM " + tableName + " order by dtm_crt desc limit 1")
		metadata = resultSet.metaData
		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			for (i = 1 ; i <= columnCount ; i++) {
				data = resultSet.getObject(i)
				if (data == null) {
					data = 'null'
				}
				listdata.add(data)
			}
		}
		listdata
	}
}
