package customizeKeyword

import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class WriteExcel {
	@Keyword
	def writeToExcel(String filePath, String sheetName, int rowNo, int collNo, String cellValue){
		FileInputStream file = new FileInputStream (new File(filePath))

		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheet(sheetName)

		'Write data to excel'
		sheet.getRow(rowNo).createCell(collNo).setCellValue(cellValue)

		file.close()
		FileOutputStream outFile = new FileOutputStream(new File(filePath))
		workbook.write(outFile)
		outFile.close()
	}

	@Keyword
	def writeToExcelNumber (String filePath, String sheetName, int rowNo, int collNo, Integer cellValue){
		FileInputStream file = new FileInputStream (new File(filePath))

		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheet(sheetName)

		'Write data to excel'
		sheet.getRow(rowNo).createCell(collNo).setCellValue(cellValue)

		file.close()
		FileOutputStream outFile = new FileOutputStream(new File(filePath))
		workbook.write(outFile)
		outFile.close()
	}

	@Keyword
	def writeToExcelDecimal(String filePath, String sheetName, int rowNo, int collNo, Double cellValue){
		FileInputStream file = new FileInputStream (new File(filePath)) //initiate excel repository

		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheet(sheetName)

		'Write data to excel'
		sheet.getRow(rowNo).createCell(collNo).setCellValue(cellValue)

		file.close()
		FileOutputStream outFile = new FileOutputStream(new File(filePath))
		workbook.write(outFile)
		outFile.close()
	}

	// write to excel status and reason
	@Keyword
	public writeToExcelStatusReason (String sheetname, int colm, String status, String reason){

		(new customizeKeyword.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
				0, colm - 1, status)
		(new customizeKeyword.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
				1, colm - 1, reason)
	}

	@Keyword
	public void writeToExcelFormula(String filePath, String sheetName, int rowNo, int collNo, String cellValue) throws IOException{
		FileInputStream file = new FileInputStream (new File(filePath))
		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheet(sheetName)

		sheet.getRow(rowNo).createCell(collNo).setCellFormula(cellValue)

		file.close()
		FileOutputStream outFile = new FileOutputStream(new File(filePath))
		workbook.write(outFile)
		outFile.close()

	}

	//keyword getExcelPath
	@Keyword
	public getExcelPath (String Path){
		String userDir = System.getProperty('user.dir')

		String filePath = userDir + Path

		return filePath
	}
}
