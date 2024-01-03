package connection

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword

public class PengaturanTenant {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []

	@Keyword
	getPengaturanTenant(Connection conn, String emailLogin) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select mst.usr_upd,TO_CHAR(mst.dtm_upd,'yyyy-MM-dd'), mst.email_reminder_dest,mst.ref_number_label, mst.upload_url, mst.threshold_balance, case when mst.automatic_stamping_after_sign = '0' then 'No' else 'Yes' end, mst.activation_callback_url, mst.use_wa_message,  COALESCE(mst.client_callback_url, ''), COALESCE(mst.client_activation_redirect_url, ''), COALESCE(mst.client_signing_redirect_url, '') from ms_tenant mst join ms_useroftenant muot on mst.id_ms_tenant = muot.id_ms_tenant join am_msuser amm on muot.id_ms_user = amm.id_ms_user where amm.login_id = '" + emailLogin + "'")

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
	getDescriptionBalanceType(Connection conn, String lovCode) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select description from ms_lov where lov_group = 'BALANCE_TYPE' and code = '" + lovCode + "'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}
	
}
