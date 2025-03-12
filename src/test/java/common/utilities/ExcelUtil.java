package common.utilities;

import common.launchsetup.BaseTest;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelUtil {
	private File file;
	public String path;
	public FileInputStream fis = null;
	public FileOutputStream fileOut = null;
	private XSSFWorkbook workbook = null;
	private XSSFSheet sheet = null;
	private XSSFRow row = null;
	private XSSFCell cell = null;

	public ExcelUtil(File file) {
		this.file = file;
		try {
			fis = new FileInputStream(file);
			path = file.getAbsolutePath();
			workbook = new XSSFWorkbook(fis);
			sheet = workbook.getSheetAt(0);
			fis.close();
		} catch (Exception e) {
			// do nothing
		}

	}

	public XSSFWorkbook getWorkbook() {
		return workbook;
	}

	public void setSheet(XSSFSheet sheet) {
		this.sheet = sheet;
	}

	public XSSFSheet getSheet() {
		return sheet;
	}

	public ArrayList<ArrayList<Object>> extractAsList(int columnNo) {

		ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
		int maxDataCount = 0;
		try {
			/*
			 * FileInputStream file = new FileInputStream(this.file);
			 * 
			 * // Create Workbook instance holding reference to .xlsx file XSSFWorkbook
			 * workbook = new XSSFWorkbook(file);
			 * 
			 * // Get first/desired sheet from the workbook XSSFSheet sheet =
			 * workbook.getSheetAt(0);
			 */

			// Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {

				Row row = rowIterator.next();

				// Skip the first row beacause it will be header
				if (row.getRowNum() == 0) {

					maxDataCount = row.getLastCellNum();
					continue;
				}

				/**
				 * if row is empty then break the loop,do not go further
				 */
				if (this.isRowEmpty(row, maxDataCount)) {
					// Exit the processing
					break;
				}

				ArrayList<Object> singleRows = new ArrayList<Object>();

				// For each row, iterate through all the columns
				for (int cn = columnNo; cn < maxDataCount; cn++) {

					Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);

					switch (cell.getCellType()) {

					case Cell.CELL_TYPE_NUMERIC:

						if (DateUtil.isCellDateFormatted(cell)) {
							singleRows.add(new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue()));
						} else
							singleRows.add(cell.getNumericCellValue());
						break;

					case Cell.CELL_TYPE_STRING:
						singleRows.add(cell.getStringCellValue());
						break;

					case Cell.CELL_TYPE_BLANK:
						singleRows.add(null);
						break;

					default:
						singleRows.add(cell.getStringCellValue());
					}

				}
				list.add(singleRows);
			}
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public boolean isRowEmpty(Row row, int lastcellno) {
		for (int c = row.getFirstCellNum(); c < lastcellno; c++) {
			Cell cell = row.getCell(c, Row.CREATE_NULL_AS_BLANK);
			if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
				return false;
		}
		return true;
	}

	/**
	 * Give file name w.r.t. to test data folder
	 * 
	 * @param fileName
	 * @param testCaseName
	 * @param testRowNo
	 * @return
	 */
	public static Map<String, String> getTestDataRow(String fileName, String testCaseName, int testRowNo) {
		if (BaseTest.platform.equals("Web") || BaseTest.platform.equals("WebApi"))
			fileName = "web/" + fileName;
		else if (BaseTest.platform.contains("app") || BaseTest.platform.contains("App"))
			fileName = "app/" + fileName;
		else if (BaseTest.platform.equals("WAP") || BaseTest.platform.equals("WAPApi"))
			fileName = "wap/" + fileName;
		String filePath = System.getProperty("user.dir") + "/src/main/resources/testdata/" + fileName + ".xlsx";
		File file = new File(filePath);
		Map<String, String> data = new LinkedHashMap<>();
		try {
			@SuppressWarnings("resource")
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sh = workbook.getSheetAt(BaseTest.excelTestDataSheet);
			Row row;
			int rc = sh.getLastRowNum();
			for (int i = 1; i <= rc; i++) {
				if (sh.getRow(i).getCell(0, Row.CREATE_NULL_AS_BLANK).toString().equals(testCaseName)) {
					int k = testRowNo == 1 ? i : i + testRowNo - 1;
					row = sh.getRow(k);
					out: {
						for (int j = 1; j < sh.getRow(k).getLastCellNum(); j++) {
							Cell cell = row.getCell(j, Row.CREATE_NULL_AS_BLANK);
							Object cellValue = null;
							switch (cell.getCellType()) {

							case Cell.CELL_TYPE_NUMERIC:

								if (DateUtil.isCellDateFormatted(cell)) {
									cellValue = new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
								} else
									cellValue = cell.getNumericCellValue();
								data.put(sh.getRow(i - 1).getCell(j, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),
										cellValue.toString());
								break;

							case Cell.CELL_TYPE_STRING:
								cellValue = cell.getStringCellValue();
								data.put(sh.getRow(i - 1).getCell(j, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),
										cellValue.toString());
								break;

							case Cell.CELL_TYPE_BLANK:
								break out;

							default:
								cellValue = cell.getStringCellValue();
								data.put(sh.getRow(i - 1).getCell(j, Row.CREATE_NULL_AS_BLANK).getStringCellValue(),
										cellValue.toString());
							}
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(data + "Test Data for the test case");
		return data;

	}

	public boolean setCellData(String sheetName, String colName, int rowNum, String data, String url) {
		// System.out.println("setCellData setCellData******************");
		try {
			if (fis == null)
				fis = new FileInputStream(path);
			if (workbook == null)
				workbook = new XSSFWorkbook(fis);

			if (rowNum <= 0)
				return false;

			int index = workbook.getSheetIndex(sheetName);
			int colNum = -1;
			if (index == -1)
				return false;

			sheet = workbook.getSheetAt(index);
			// System.out.println("A");
			row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				// System.out.println(row.getCell(i).getStringCellValue().trim());
				if (row.getCell(i).getStringCellValue().trim().equalsIgnoreCase(colName))
					colNum = i;
			}

			if (colNum == -1)
				return false;
			sheet.autoSizeColumn(colNum);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				row = sheet.createRow(rowNum - 1);

			cell = row.getCell(colNum);
			if (cell == null)
				cell = row.createCell(colNum);

			cell.setCellValue(data);
			XSSFCreationHelper createHelper = (XSSFCreationHelper) workbook.getCreationHelper();

			// cell style for hyperlinks
			// by default hypelrinks are blue and underlined
			CellStyle hlink_style = workbook.createCellStyle();
			XSSFFont hlink_font = workbook.createFont();
			hlink_font.setUnderline(XSSFFont.U_SINGLE);
			hlink_font.setColor(IndexedColors.BLUE.getIndex());
			hlink_style.setFont(hlink_font);
			// hlink_style.setWrapText(true);

			XSSFHyperlink link = createHelper.createHyperlink(XSSFHyperlink.LINK_FILE);
			link.setAddress(url);
			cell.setHyperlink(link);
			cell.setCellStyle(hlink_style);

			fileOut = new FileOutputStream(path);
			workbook.write(fileOut);

			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean setCellData(String sheetName, String colName, int rowNum, String data) {
		try {
			if (fis == null)
				fis = new FileInputStream(path);
			if (workbook == null)
				workbook = new XSSFWorkbook(fis);

			if (rowNum <= 0)
				return false;

			int index = workbook.getSheetIndex(sheetName);
			int colNum = -1;
			if (index == -1)
				return false;

			sheet = workbook.getSheetAt(index);

			row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				// System.out.println(row.getCell(i).getStringCellValue().trim());
				if (row.getCell(i).getStringCellValue().trim().equals(colName))
					colNum = i;
			}
			if (colNum == -1)
				return false;

			// sheet.autoSizeColumn(colNum);
			sheet.setColumnWidth(colNum, 8500);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				row = sheet.createRow(rowNum - 1);
			row.setHeight((short) -1);
			cell = row.getCell(colNum);
			if (cell == null)
				cell = row.createCell(colNum);

			CellStyle style = workbook.createCellStyle();
			style.setWrapText(true);
			cell.setCellStyle(style);
			if (data.length() > 150)
				sheet.setColumnWidth(colNum, 10500);

			cell.setCellValue(data);
			fileOut = new FileOutputStream(path);

			workbook.write(fileOut);

			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean setCellDataNColor(String sheetName, String colName, int rowNum, String data,
			IndexedColors indexedColour) {
		try {
			if (fis == null)
				fis = new FileInputStream(path);
			if (workbook == null)
				workbook = new XSSFWorkbook(fis);

			if (rowNum <= 0)
				return false;

			int index = workbook.getSheetIndex(sheetName);
			int colNum = -1;
			if (index == -1)
				return false;

			sheet = workbook.getSheetAt(index);

			row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				// System.out.println(row.getCell(i).getStringCellValue().trim());
				if (row.getCell(i).getStringCellValue().trim().equals(colName))
					colNum = i;
			}
			if (colNum == -1)
				return false;

			sheet.setColumnWidth(colNum, 8500);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				row = sheet.createRow(rowNum - 1);
			row.setHeight((short) -1);
			cell = row.getCell(colNum);
			if (cell == null)
				cell = row.createCell(colNum);

			CellStyle style = workbook.createCellStyle();
			style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			style.setFillForegroundColor(indexedColour.getIndex());
			cell.setCellStyle(style);
			cell.setCellValue(data);

			fileOut = new FileOutputStream(path);

			workbook.write(fileOut);

			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean setCellData(String sheetName, int colNum, int rowNum, String... data) {
		try {
			if (fis == null)
				fis = new FileInputStream(path);
			if (workbook == null)
				workbook = new XSSFWorkbook(fis);

			if (rowNum <= 0)
				return false;

			int index = workbook.getSheetIndex(sheetName);
			// int colNum=-1;
			if (index == -1)
				return false;

			sheet = workbook.getSheetAt(index);

			// row=sheet.getRow(0);
			// for(int i=0;i<row.getLastCellNum();i++){

			// if(row.getCell(i).getStringCellValue().trim().equals(colName))
			// colNum=i;
			// }

			if (colNum == -1)
				return false;

			sheet.autoSizeColumn(colNum);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				row = sheet.createRow(rowNum - 1);

			// cell style
			// CellStyle cs = workbook.createCellStyle();
			// cs.setWrapText(true);
			// cell.setCellStyle(cs);
			int len = data.length;
			// System.out.println(len);
			for (int i = 0; i < len; i++) {
				cell = row.getCell(colNum + i, Row.RETURN_BLANK_AS_NULL);
				// System.out.println(Cell);
				if (cell == null) {
					cell = row.createCell(colNum + i);
					cell.setCellValue(data[i]);
				} else {
					cell.setCellValue(data[i]);
				}

			}

			fileOut = new FileOutputStream(path);

			workbook.write(fileOut);

			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String getCellData(String sheetName, int colNum, int rowNum) {
		try {
			if (rowNum <= 0)
				return "";

			int index = workbook.getSheetIndex(sheetName);

			if (index == -1)
				return "";

			sheet = workbook.getSheetAt(index);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				return "";
			cell = row.getCell(colNum);
			if (cell == null)
				return "";

			if (cell.getCellType() == Cell.CELL_TYPE_STRING)
				return cell.getStringCellValue();
			else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC || cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

				String cellText = String.valueOf((long) cell.getNumericCellValue());
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// format in form of M/D/YY
					double d = cell.getNumericCellValue();

					Calendar cal = Calendar.getInstance();
					cal.setTime(HSSFDateUtil.getJavaDate(d));
					cellText = (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
					cellText = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cellText;

					// System.out.println(cellText);

				}

				return cellText;
			} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK)
				return "";
			else
				return String.valueOf(cell.getBooleanCellValue());
		} catch (Exception e) {

			e.printStackTrace();
			return "row " + rowNum + " or column " + colNum + " does not exist in xls";
		}
	}

	public String getCellData(String sheetName, String colName, int rowNum) {
		try {
			if (rowNum <= 0)
				return "";

			int index = workbook.getSheetIndex(sheetName);
			int col_Num = -1;
			if (index == -1)
				return "";

			sheet = workbook.getSheetAt(index);
			row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				// System.out.println(row.getCell(i).getStringCellValue().trim());
				if (row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
					col_Num = i;
			}
			if (col_Num == -1)
				return "";

			sheet = workbook.getSheetAt(index);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				return "";
			cell = row.getCell(col_Num);

			if (cell == null)
				return "";
			// System.out.println(cell.getCellType());
			if (cell.getCellType() == Cell.CELL_TYPE_STRING)
				return cell.getStringCellValue();
			else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC || cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

				String cellText = "0";
				//////////// String cellText =
				//////////// String.valueOf(cell.getNumericCellValue());
				try {
					cellText = String.valueOf((long) cell.getNumericCellValue());

					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						// format in form of M/D/YY
						double d = cell.getNumericCellValue();

						Calendar cal = Calendar.getInstance();
						cal.setTime(HSSFDateUtil.getJavaDate(d));

						(String.valueOf(cal.get(Calendar.YEAR))).substring(2);
						cellText = cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + 1 + "/" + cellText;

					}
				} catch (IllegalStateException e) {
					// do nothing
				}
				// System.out.println(cellText);

				return cellText;
			} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK)
				return "";
			else
				return String.valueOf(cell.getBooleanCellValue());

		} catch (Exception e) {

			e.printStackTrace();
			return "row " + rowNum + " or column " + colName + " does not exist in xls";
		}
	}

	public int getCellRowNum(String sheetName, String colName, String cellValue) {

		for (int i = 2; i <= getRowCount(sheetName); i++) {
			if (getCellData(sheetName, colName, i).equalsIgnoreCase(cellValue)) {
				return i;
			}
		}
		return -1;

	}

	public int getRowCount(String sheetName) {
		int index = workbook.getSheetIndex(sheetName);
		if (index == -1)
			return 0;
		else {
			sheet = workbook.getSheetAt(index);
//			int number =	sheet.getPhysicalNumberOfRows()+1;
			int number = sheet.getLastRowNum() + 1;
			return number;
		}
	}

	public Sheet createSheet(File workbookFile, String sheetName) {
		Sheet sheet = null;
		try {
			sheet = workbook.createSheet(sheetName);
			FileOutputStream out = new FileOutputStream(workbookFile.getAbsolutePath());
			workbook.write(out);
			out.close();
		} catch (EncryptedDocumentException | IOException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sheet;
	}

	public boolean setCellDataWithoutWritingInSheet(String sheetName, String colName, int rowNum, String data) {
		try {
			if (fis == null) {
				fis = new FileInputStream(path);
			}
			if (workbook == null) {
				workbook = new XSSFWorkbook(fis);
			}
			if (rowNum <= 0)
				return false;

			int index = workbook.getSheetIndex(sheetName);
			int colNum = -1;
			if (index == -1)
				return false;

			sheet = workbook.getSheetAt(index);

			row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				// System.out.println(row.getCell(i).getStringCellValue().trim());
				if (row.getCell(i).getStringCellValue().trim().equals(colName))
					colNum = i;
			}
			if (colNum == -1)
				return false;

			// sheet.autoSizeColumn(colNum);
			sheet.setColumnWidth(colNum, 8500);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				row = sheet.createRow(rowNum - 1);
			row.setHeight((short) -1);
			cell = row.getCell(colNum);
			if (cell == null)
				cell = row.createCell(colNum);

			CellStyle style = workbook.createCellStyle();
			style.setWrapText(true);
			cell.setCellStyle(style);
			if (data.length() > 150)
				sheet.setColumnWidth(colNum, 10500);

			cell.setCellValue(data);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean writeDatainExcelSheet() {
		try {
			if (fis == null) {
				fis = new FileInputStream(path);
			}
			if (workbook == null) {
				workbook = new XSSFWorkbook(fis);
			}
			fileOut = new FileOutputStream(path);

			workbook.write(fileOut);

			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	
	public ArrayList<ArrayList<Object>> extractAsListFromParticularSheet(int columnNo, String sheetName) {

		ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
		int maxDataCount = 0;
		try {
			/*
			 * FileInputStream file = new FileInputStream(this.file);
			 * 
			 * // Create Workbook instance holding reference to .xlsx file XSSFWorkbook
			 * workbook = new XSSFWorkbook(file);
			 * 
			 * // Get first/desired sheet from the workbook XSSFSheet sheet =
			 * workbook.getSheetAt(0);
			 */

			// Iterate through each rows one by one
			
			int index = workbook.getSheetIndex(sheetName);
			if (index == -1)
				return list;

			sheet = workbook.getSheetAt(index);
			
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {

				Row row = rowIterator.next();

				// Skip the first row beacause it will be header
				if (row.getRowNum() == 0) {

					maxDataCount = row.getLastCellNum();
					continue;
				}

				/**
				 * if row is empty then break the loop,do not go further
				 */
				if (this.isRowEmpty(row, maxDataCount)) {
					// Exit the processing
					break;
				}

				ArrayList<Object> singleRows = new ArrayList<Object>();

				// For each row, iterate through all the columns
				for (int cn = columnNo; cn < maxDataCount; cn++) {

					Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);

					switch (cell.getCellType()) {

					case Cell.CELL_TYPE_NUMERIC:

						if (DateUtil.isCellDateFormatted(cell)) {
							singleRows.add(new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue()));
						} else
							singleRows.add(cell.getNumericCellValue());
						break;

					case Cell.CELL_TYPE_STRING:
						singleRows.add(cell.getStringCellValue());
						break;

					case Cell.CELL_TYPE_BLANK:
						singleRows.add(null);
						break;

					default:
						singleRows.add(cell.getStringCellValue());
					}

				}
				list.add(singleRows);
			}
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}
