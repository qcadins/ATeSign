package customizekeyword

import java.text.SimpleDateFormat
import com.kms.katalon.core.annotation.Keyword
import internal.GlobalVariable

public class ParseDate {
	@Keyword
	parseDateFormat(String date, String format1, String format2) {
		Date parsedDate
		String sentDate, sDate
		//parse Date from MM/dd/yyyy > yyyy-MM-dd
		SimpleDateFormat sdf = new SimpleDateFormat(format1)

		parsedDate = null

		sentDate = date

		parsedDate = sdf.parse(sentDate)

		sdf = new SimpleDateFormat(format2)

		sDate = sdf.format(parsedDate)

		sDate
	}
}