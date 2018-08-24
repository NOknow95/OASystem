package com.wjw.ems.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wjw.ems.dao.DepartmentDao;
import com.wjw.ems.dao.RoleDao;
import com.wjw.ems.entities.Department;
import com.wjw.ems.entities.Employee;
import com.wjw.ems.entities.Role;
import com.wjw.ems.exception.AccountUnabledException;
import com.wjw.ems.exception.LoginNameNotFoundException;
import com.wjw.ems.exception.PasswordNotMatchException;

@Service
public class EmployeeService extends BaseService<Employee> {

	@Autowired
	private DepartmentDao departmentDao;
	@Autowired
	private RoleDao roleDao;

	@Transactional
	public List<String[]> upload(File file) throws IOException, InvalidFormatException {
		List<String[]> result = new ArrayList<>();

		// 1. 由 File 对象, 得到 Workbook 对象
		InputStream inp = new FileInputStream(file);
		Workbook wb = WorkbookFactory.create(inp);

		// 2. 得到 Sheet
		Sheet sheet = wb.getSheet("employees");

		// 3. 解析 Sheet 为一个 Employee 的集合.
		// 其中每一行是一个 Employee 对象, 每一个单元格是一个 Employee 对象的属性
		// 在此期间需要对每个字段都进行校验
		List<Employee> employees = parseSheetToEmployees(sheet, result);

		// 4. 若验证时有错误, 则直接返回错误消息, 不再执行录入操作.
		if (result.size() > 0) {
			return result;
		}

		// 5. 调用 Dao 方法完成批量的录入
		dao.batch(employees);

		return result;
	}

	private List<Employee> parseSheetToEmployees(Sheet sheet, List<String[]> result) {
		List<Employee> employees = new ArrayList<>();

		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			Employee employee = parseRowToEmployee(row, i + 1, result);

			if (employee != null) {
				employees.add(employee);
			}
		}

		return employees;
	}

	private Employee parseRowToEmployee(Row row, int rowNum, List<String[]> result) {
		// 序号 登录名 姓名 性别 登录许可 部门 E-mail 角色
		Cell cell = null;
		String cellVal = null;
		boolean flag = true;

		// loginName
		cell = row.getCell(1);
		cellVal = getCellValue(cell);
		flag = valiateLoginName(cellVal, result, rowNum, 2);
		String loginName = null;
		if (flag) {
			loginName = cellVal.trim();
		}

		// employeeName
		cell = row.getCell(2);
		cellVal = getCellValue(cell);
		String employeeName = cellVal;

		// gender
		cell = row.getCell(3);
		cellVal = getCellValue(cell);
		Integer gender = validateGenderOrEnabled(cellVal, result, rowNum, 4);
		flag = (gender != null) && flag;

		// enaled
		cell = row.getCell(4);
		cellVal = getCellValue(cell);
		Integer enabled = validateGenderOrEnabled(cellVal, result, rowNum, 5);
		flag = (enabled != null) && flag;

		// department
		cell = row.getCell(5);
		cellVal = getCellValue(cell);
		Department department = validateDepartment(cellVal, result, rowNum, 6);
		flag = (department != null) && flag;

		// email
		cell = row.getCell(6);
		cellVal = getCellValue(cell);
		flag = validateEmail(cellVal, result, rowNum, 7) && flag;
		String email = null;
		if (flag) {
			email = cellVal.trim();
		}

		// roles
		cell = row.getCell(7);
		cellVal = getCellValue(cell);
		Set<Role> roles = validateRoles(cellVal, result, rowNum, 8);
		flag = (roles != null) && flag;

		if (flag) {
			Employee employee = new Employee(loginName, employeeName, "123456", roles, enabled, department, gender + "",
					email, 0);
			return employee;
		}

		return null;
	}

	private Set<Role> validateRoles(String cellVal, List<String[]> result, int rowNum, int columnNum) {
		// Set<Role> roles = new HashSet<>();

		Object[] roleNames = cellVal.split(",");
		List<Role> roles2 = roleDao.getListByIn("roleName", Arrays.asList(roleNames));

		if (roleNames.length != roles2.size()) {
			result.add(new String[] { "" + rowNum, "" + columnNum });
			return null;
		}

		return new HashSet<>(roles2);
	}

	private boolean validateEmail(String cellVal, List<String[]> result, int rowNum, int columnNum) {
		if (cellVal == null || cellVal.trim().length() == 0) {
			return false;
		}

		Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
		Matcher matcher = pattern.matcher(cellVal);
		boolean b = matcher.matches();
		if (!b) {
			result.add(new String[] { "" + rowNum, "" + columnNum });
			return false;
		}

		return b;
	}

	private Department validateDepartment(String cellVal, List<String[]> result, int rowNum, int columnNum) {
		Department department = departmentDao.getBy("departmentName", cellVal);
		if (department != null) {
			return department;
		}

		result.add(new String[] { "" + rowNum, "" + columnNum });
		return null;
	}

	private Integer validateGenderOrEnabled(String cellVal, List<String[]> result, int rowNum, int columnNum) {
		try {
			double d = Double.parseDouble(cellVal);
			int i = (int) d;

			if (i == 1 || i == 0) {
				return i;
			}
		} catch (NumberFormatException e) {
		}

		result.add(new String[] { "" + rowNum, "" + columnNum });
		return null;
	}

	private boolean valiateLoginName(String loginName, List<String[]> result, int rowNum, int columnNum) {

		if (loginName != null && loginName.trim().length() >= 6) {
			Pattern p = Pattern.compile("^[a-zA-Z]\\w+\\w$");
			Matcher m = p.matcher(loginName.trim());
			boolean b = m.matches();

			if (b) {
				Employee employee = dao.getBy("loginName", loginName.trim());
				if (employee == null) {
					return true;
				}
			}
		}
		result.add(new String[] { "" + rowNum, "" + columnNum });
		return false;
	}

	private String getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.getRichStringCellValue().getString();
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue() + "";
			} else {
				return cell.getNumericCellValue() + "";
			}
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue() + "";
		case Cell.CELL_TYPE_FORMULA:
			return cell.getCellFormula() + "";
		}

		return null;
	}

	@Transactional(readOnly = true)
	public void buildExcel(String fileDir) throws IOException {
		//
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("employees");
		// 1. 创建标题行
		setTitles(sheet);

		// 2. 填充数据
		setEmployees(sheet);

		// 3. 设置行高、列宽、边框
		setCellStyle(wb, sheet);

		FileOutputStream fileOut = new FileOutputStream(fileDir);
		wb.write(fileOut);
		fileOut.close();
	}

	private void setCellStyle(Workbook wb, Sheet sheet) {
		CellStyle style = wb.createCellStyle();

		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());

		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			row.setHeightInPoints(30);

			for (int j = 0; j < row.getLastCellNum(); j++) {
				Cell cell = row.getCell(j);
				cell.setCellStyle(style);

				sheet.autoSizeColumn(j);
				sheet.setColumnWidth(j, (int) (sheet.getColumnWidth(j) * 1.3));
			}
		}
	}

	private void setEmployees(Sheet sheet) {
		List<Employee> emps = dao.getAll();

		Cell cell = null;
		for (int i = 0; i < emps.size(); i++) {
			Row row = sheet.createRow(i + 1);
			Employee emp = emps.get(i);

			cell = row.createCell(0);
			cell.setCellValue("" + (i + 1));

			cell = row.createCell(1);
			cell.setCellValue(emp.getEmployeeId());

			cell = row.createCell(2);
			cell.setCellValue(emp.getLoginName());

			cell = row.createCell(3);
			cell.setCellValue(emp.getEmployeeName());

			cell = row.createCell(4);
			cell.setCellValue("" + emp.getGender());

			cell = row.createCell(5);
			cell.setCellValue("" + emp.getEnabled());

			cell = row.createCell(6);
			cell.setCellValue("" + emp.getDepartmentName());

			cell = row.createCell(7);
			cell.setCellValue("" + emp.getEmail());

			cell = row.createCell(8);
			cell.setCellValue("" + emp.getRoleNames());
		}
	}

	private static final String[] TITLES = new String[] { "序号", "ID", "登录名", "姓名", "性别", "登录许可", "部门", "E-mail", "角色" };

	// 序号 登录名 姓名 性别 登录许可 部门 E-mail 角色
	private void setTitles(Sheet sheet) {
		Row row = sheet.createRow(0);
		for (int i = 0; i < TITLES.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(TITLES[i]);
		}
	}

	@Transactional
	public void save(Employee employee) {
		if (employee.getEmployeeId() == null) {
			employee.setPassword("123456");
			employee.setEnabled(1);
			employee.setVisitedTimes(0);
		}
		dao.save(employee);
	}

	@Transactional
	public Employee login(String loginName, String password) {

		Employee employee = dao.getBy("loginName", loginName);

		if (employee == null) {
			throw new LoginNameNotFoundException();
		}

		if (employee.getEnabled() != 1) {
			throw new AccountUnabledException();
		}

		if (!password.equals(employee.getPassword())) {
			throw new PasswordNotMatchException();
		}

		employee.setVisitedTimes(employee.getVisitedTimes() + 1);

		return employee;
	}

	@Transactional
	public String delete(Integer id) {
		Employee manager = new Employee();
		manager.setEmployeeId(id);
		Department department = departmentDao.getBy("manager", manager);
		if (department != null) {
			return "2";
		}

		Employee employee = dao.get(id);
		employee.setIsDeleted(1);
		return "1";
	}

	@Transactional
	public void updateVisitedTimes(Employee employee) {
		Employee emp = dao.get(employee.getEmployeeId());
		emp.setVisitedTimes(emp.getVisitedTimes() + 1);
	}
}
