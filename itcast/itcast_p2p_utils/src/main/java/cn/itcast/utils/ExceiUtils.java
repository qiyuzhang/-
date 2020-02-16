package cn.itcast.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExceiUtils<T> {

	// 参数File就是excel文件
	// 返回值就是最终的要的结果
	public List<T> getEntity(File file, ExcelRowResultHandler<T> err) throws InvalidFormatException, IOException {

		// 1.得到一个book
		XSSFWorkbook book = new XSSFWorkbook(file);

		// 2.得到一个sheet
		XSSFSheet sheet = book.getSheetAt(0);

		// 3.调用一个方法，将sheet中的内容封装成List<List<Object>>
		List<List<Object>> ls = sheetToList(sheet);

		List<T> list = new ArrayList<T>();
		for (List<Object> lo : ls) {
			// 将lo转换成T对象
			T t = err.invoke(lo); // 接口回调，最终是通过具体的接口实现类来完成对象封装
			list.add(t);
		}
		return list;
	}

	private List<List<Object>> sheetToList(XSSFSheet sheet) {
		List<List<Object>> list = new ArrayList<List<Object>>();
		// 遍历每一行
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {

			List<Object> lo = new ArrayList<Object>();

			XSSFRow row = sheet.getRow(i);
			// 遍历每一列
			for (int j = 0; j < row.getLastCellNum(); j++) {
				XSSFCell cell = row.getCell(j);
				// 将每一列的值装入到lo中
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_NUMERIC:
					lo.add(cell.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_STRING:
					lo.add(cell.getStringCellValue());
					break;
				}
			}
			// 将每一行的值装入到list中
			list.add(lo);
		}

		return list;
	}
}
