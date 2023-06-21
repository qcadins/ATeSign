package connection

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.sql.Connection
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Statement
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class JobResult {

	String data
	int columnCount, i
	Statement stm
	ResultSetMetaData metadata
	ResultSet resultSet
	ArrayList<String> listdata = []


	@Keyword
	jobResultDB(Connection conn, String startDate, String endDate) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select tjr.usr_crt, msj.job_name, to_char(tjr.dtm_crt,'dd-Mon-yyyy hh24:mi:ss'), to_char(tjr.process_start_time,'dd-Mon-yyyy hh:mi:ss'), to_char(tjr.process_finish_time,'dd-Mon-yyyy hh:mi:ss'), cast(round(EXTRACT(EPOCH FROM (tjr.process_finish_time - tjr.process_start_time))) as varchar) AS duration_second, case when tjr.process_status = 0 then 'New' WHEN tjr.process_status = 1 then 'In Progress' when tjr.process_status = 2 then 'Completed' when tjr.process_status = 3 then 'Cancelled' when tjr.process_status = 4 then 'Failed' when tjr.process_status = 5 then 'Deleted' end, JSONB_EXTRACT_PATH_TEXT(tjr.request_params::JSONB, 'vendorCode') AS vendorCode, JSONB_EXTRACT_PATH_TEXT(tjr.request_params::JSONB, 'tenantCode'), JSONB_EXTRACT_PATH_TEXT(tjr.request_params::JSONB, 'transactionDateStart') AS tenantCodeAndDate, JSONB_EXTRACT_PATH_TEXT(tjr.request_params::JSONB, 'transactionDateEnd') AS transactionDateEnd, JSONB_EXTRACT_PATH_TEXT(tjr.request_params::JSONB, 'balanceType') AS balanceType from tr_job_result tjr left join ms_lov msl on msl.id_lov = tjr.lov_job_type left join ms_job msj on msj.id_ms_job = tjr.id_ms_job where tjr.process_start_time BETWEEN '"+startDate+"' AND '"+endDate+"'order by tjr.dtm_crt desc")
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
	countJobResult(Connection conn, String startDate, String endDate) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select count(tjr.usr_crt) from tr_job_result tjr where tjr.process_start_time BETWEEN '"+startDate+"' AND '"+endDate+"'")
		metadata = resultSet.metaData

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		data
	}

	@Keyword
	jobResultViewReqParamDB(Connection conn, String startDate, String endDate) {
		stm = conn.createStatement()

		resultSet = stm.executeQuery("select JSONB_EXTRACT_PATH_TEXT(tjr.request_params::JSONB, 'vendorCode') AS vendorCode, JSONB_EXTRACT_PATH_TEXT(tjr.request_params::JSONB, 'tenantCode'), JSONB_EXTRACT_PATH_TEXT(tjr.request_params::JSONB, 'transactionDateStart') AS tenantCodeAndDate, JSONB_EXTRACT_PATH_TEXT(tjr.request_params::JSONB, 'transactionDateEnd') AS transactionDateEnd, JSONB_EXTRACT_PATH_TEXT(tjr.request_params::JSONB, 'balanceType') AS balanceType from tr_job_result tjr left join ms_lov msl on msl.id_lov = tjr.lov_job_type left join ms_job msj on msj.id_ms_job = tjr.id_ms_job where tjr.process_start_time BETWEEN '"+startDate+"' AND '"+endDate+"'order by tjr.dtm_crt desc")
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
