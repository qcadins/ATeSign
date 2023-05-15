package customizekeyword

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class WriteExcel {
	@Keyword
	writeToExcel(String filePath, String sheetName, int rowNo, int collNo, String cellValue) {
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
	writeToExcelNumber(String filePath, String sheetName, int rowNo, int collNo, Integer cellValue) {
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
	writeToExcelDecimal(String filePath, String sheetName, int rowNo, int collNo, Double cellValue) {
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
	writeToExcelStatusReason(String sheetname, int colm, String status, String reason) {
		(new customizekeyword.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
				0, colm - 1, status)
		(new customizekeyword.WriteExcel()).writeToExcel(GlobalVariable.DataFilePath, sheetname,
				1, colm - 1, reason)
	}

	@Keyword
	writeToExcelFormula(String filePath, String sheetName, int rowNo, int collNo, String cellValue) throws IOException {
		FileInputStream file = new FileInputStream (new File(filePath))
		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheet(sheetName)

		sheet.getRow(rowNo).createCell(collNo).cellFormula = cellValue

		file.close()
		FileOutputStream outFile = new FileOutputStream(new File(filePath))
		workbook.write(outFile)
		outFile.close()
	}

	//keyword getExcelPath
	@Keyword
	getExcelPath(String path) {
		String userDir = System.getProperty('user.dir')

		String filePath = userDir + path

		filePath
	}

}
