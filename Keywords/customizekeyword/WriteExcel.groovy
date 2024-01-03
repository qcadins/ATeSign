package customizekeyword

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable
import org.apache.poi.xssf.usermodel.XSSFRow

public class WriteExcel {
	
	@Keyword
	writeToExcel(String filePath, String sheetName, int rowNo, int collNo, String cellValue) {
		FileInputStream file = new FileInputStream(new File(filePath))

		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheet(sheetName)

		row = sheet.getRow(rowNo) ?: sheet.createRow(rowNo)
		cell = row.getCell(collNo) ?: row.createCell(collNo)

		cell.setCellValue(cellValue)

		file.close()
		file.withCloseable {
			workbook.write(new FileOutputStream(new File(filePath)))
		}
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
		outFile.flush()
		outFile.close()
	}

	@Keyword
	writeToExcelDecimal(String filePath, String sheetName, int rowNo, int collNo, Double cellValue) {
		FileInputStream file = new FileInputStream(new File(filePath)) //initiate excel repository

		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheet(sheetName)

		'Write data to excel'
		sheet.getRow(rowNo).createCell(collNo).setCellValue(cellValue)

		file.close()
		FileOutputStream outFile = new FileOutputStream(new File(filePath))
		workbook.write(outFile)
		outFile.flush()
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
		FileInputStream file = new FileInputStream(new File(filePath))
		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheet(sheetName)

		sheet.getRow(rowNo).createCell(collNo).cellFormula = cellValue

		file.close()
		FileOutputStream outFile = new FileOutputStream(new File(filePath))
		workbook.write(outFile)
		outFile.flush()
		outFile.close()
	}

	//keyword getExcelPath
	@Keyword
	getExcelPath(String path) {
		String userDir = System.getProperty('user.dir')

		String filePath = userDir + path

		filePath
	}

	//keyword getExcelRow
	@Keyword
	public int getExcelRow(String filePath, String sheetName, String cellValue) {
		FileInputStream file = new FileInputStream(new File(filePath)) //initiate excel repository
		XSSFWorkbook workbook = new XSSFWorkbook(file)
		XSSFSheet sheet = workbook.getSheet(sheetName) //getSheet -> sheet num n (start from index 0)
		XSSFRow row = null
		int rowNum = -1
		for (int i = 0; i <= sheet.lastRowNum; i++) {
			row = sheet.getRow(i)
			try {
				if (row.getCell(0).stringCellValue.equalsIgnoreCase(cellValue)) {
					rowNum = i
					break
				}
			}
			catch(Exception e) {
			}
		}
		file.close()
		rowNum + 1
	}
	
}
