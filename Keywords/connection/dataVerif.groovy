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

		ResultSet resultSet = stm.executeQuery("SELECT otp_code FROM tr_invitation_link WHERE email = '"+ email +"'")

		ResultSetMetaData metadata = resultSet.getMetaData()

		columnCount = metadata.getColumnCount()

		while (resultSet.next()) {
			data = resultSet.getObject(1)
		}
		return data
	}

//		@Keyword
//		public getOTP (Connection conn, String email){
//			String data
//	
//			ArrayList<String> listdata = new ArrayList<>()
//	
//			Statement stm = conn.createStatement()
//	
//			ResultSet resultSet = stm.executeQuery("SELECT otp_code FROM tr_invitation_link WHERE email = '"+ email +"'")
//	
//			ResultSetMetaData metadata = resultSet.getMetaData()
//	
//			int columnCount = metadata.getColumnCount()
//	
//			while (resultSet.next()) {
//				for(int i = 1 ; i < columnCount ; i++){
//					data = resultSet.getObject(i)
//					listdata.add(data)
//				}
//			}
//			return listdata
//		}
}
