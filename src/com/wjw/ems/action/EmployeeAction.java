package com.wjw.ems.action;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.WebUtils;

import com.opensymphony.xwork2.ActionContext;
import com.wjw.ems.entities.Authority;
import com.wjw.ems.entities.Employee;
import com.wjw.ems.entities.Role;
import com.wjw.ems.exception.LoginNameNotFoundException;
import com.wjw.ems.orm.Page;
import com.wjw.ems.security.EmsUserDetailsService.SecurityUser;
import com.wjw.ems.service.DepartmentService;
import com.wjw.ems.service.EmployeeService;
import com.wjw.ems.service.RoleService;
import com.wjw.ems.utils.EmployeeCrieriaFormBean;
import com.wjw.ems.utils.Navigation;

@Scope("prototype")
@Controller
public class EmployeeAction extends BaseAction<Employee> {
	// @Autowired
	// private EmployeeService employeeService;
	//
	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private RoleService roleService;

	private EmployeeService getEmployeeService() {
		return (EmployeeService) baseService;
	}

	private List<Navigation> navigations = null;

	public List<Navigation> getNavigations() {
		return navigations;
	}

	public String navigate() {
		String contextPath = ServletActionContext.getServletContext().getContextPath();

		navigations = new ArrayList<>();
		Navigation top = new Navigation(Integer.MAX_VALUE, "尚硅谷智能网络办公", null, null);
		navigations.add(top);

		Map<Integer, Navigation> parentNavigations = new HashMap<Integer, Navigation>();
		// 根据用户的权限来构建菜单导航
		Employee employee = (Employee) sessionMap.get("employee");
		for (Role role : employee.getRoles()) {
			for (Authority authority : role.getAuthorities()) {
				if (authority.getMainResource() != null) {
					Integer id = authority.getId();
					String text = authority.getDisplayName();
					String state = null;
					String url = contextPath + "/" + authority.getMainResource().getUrl();

					Navigation n = new Navigation(id, text, state, url);
					Navigation parentNavigation = parentNavigations.get(authority.getParentAuthority().getId());
					if (parentNavigation == null) {
						Authority parentAuthority = authority.getParentAuthority();
						Integer id2 = parentAuthority.getId();
						String text2 = parentAuthority.getDisplayName();

						parentNavigation = new Navigation(id2, text2, null, null);
						parentNavigations.put(id2, parentNavigation);

						top.getChildren().add(parentNavigation);
					}

					parentNavigation.getChildren().add(n);
				}
			}
		}

		top.getChildren().add(new Navigation(Integer.MAX_VALUE - 1, "登出", null, contextPath + "/security-logout"));

		return "navigation";
	}

	public String list3() {
		if (page == null) {
			page = new Page<>();
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> params = WebUtils.getParametersStartingWith(request, "filter_");

		page = baseService.getPae(page, params);
		// 吧请求参数序列化为一个查询字符串
		String queryString = parseParamsMapToQueryString(params);
		request.setAttribute("queryString", queryString);

		return "list3";
	}

	protected String parseParamsMapToQueryString(Map<String, Object> params) {
		StringBuilder result = new StringBuilder();

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			Object val = entry.getValue();
			result.append("filter_").append(key).append("=").append(val).append("&");
		}
		if (result.length() > 0) {
			result.replace(result.length() - 1, result.length(), "");
		}

		return result.toString();
	}

	EmployeeCrieriaFormBean employeeCrieriaFormBean = new EmployeeCrieriaFormBean();

	public void prepareCriteriaInput() {
		ActionContext.getContext().getValueStack().push(employeeCrieriaFormBean);
	}

	public String criteriaInput() {
		requsetMap.put("departments", departmentService.getAll());
		return "criteria-input";
	}

	@Override
	public void prepare() throws Exception {
		model = new Employee();
	}

	public void prepareLogin() {
		model = new Employee();
	}

	public String upload() throws InvalidFormatException, IOException {
		System.out.println("--------->file:" + file);
		List<String[]> errors = getEmployeeService().upload(file);

		if (errors != null && errors.size() > 0) {
			StringBuilder s = new StringBuilder();
			for (String[] error : errors) {
				String errorMessage = getText("errors.ems.emp.upload", error);
				s.append(errorMessage);
			}

			addActionError(s.toString());
			return "input";
		}
		if (file != null && file.exists() && file.delete()) {
			System.out.println("--------->file exists:" + file.exists());
			file = null;
		}
		return "success";
	}

	public String downloadUploadExcelTemplate() throws IOException {
		String fileDir = ServletActionContext.getServletContext().getRealPath("/files/employeesTemplate.xls");
		inputStream = new FileInputStream(fileDir);
		contentLength = inputStream.available();
		contentType = "application/vnd.ms-excel";
		filename = "employeesTemplate.xls";
		return "down-success";
	}

	public String downToExcel() throws IOException {
		String fileDir = ServletActionContext.getServletContext()
				.getRealPath("/files/" + System.currentTimeMillis() + ".xls");

		requsetMap.put("fileDir", fileDir);

		getEmployeeService().buildExcel(fileDir);

		inputStream = new FileInputStream(fileDir);
		contentLength = inputStream.available();
		contentType = "application/vnd.ms-excel";
		filename = "employee.xls";
		return "down-success";
	}

	public void validateLoginName() throws IOException {
		System.out.println("..................validateLoginName");
		String[] vals = parameterMap.get("loginName");
		if (vals != null && vals.length == 1) {
			String loginName = vals[0];
			Employee employee = getEmployeeService().getBy("loginName", loginName);
			String result = "1";
			if (employee != null) {
				result = "2";
			}
			ServletActionContext.getResponse().getWriter().print(result);
		}
	}

	public void prepareSave() {
		if (id == null) {
			model = new Employee();
		} else {
			model = getEmployeeService().get(id);
			model.getRoles().clear();
			// 修改部门报错的修改方法
			model.setDepartment(null);
		}
	}

	public String save() {
		boolean requredValidate = true;

		String oldLoginNameParam[] = parameterMap.get("oldLoginName");
		if (oldLoginNameParam != null && oldLoginNameParam.length == 1) {
			String oldLoginName = oldLoginNameParam[0];
			if (model.getLoginName().equals(oldLoginName)) {
				requredValidate = false;
			}
		}

		if (requredValidate && getEmployeeService().getBy("loginName", model.getLoginName()) != null) {
			addFieldError("loginName", getText("errors.ems.emp.loginName"));
			return "input";
		}
		getEmployeeService().save(model);
		return "success";
	}

	public void prepareInput() {
		if (id != null) {
			model = getEmployeeService().get(id);
		}
	}

	public String input() {
		System.out.println("input");
		requsetMap.put("departments", departmentService.getAll());
		requsetMap.put("roles", roleService.getAll());
		return "emp-input";
	}

	public void delete() throws IOException {
		String result = getEmployeeService().delete(id);
		// 原生api
		ServletActionContext.getResponse().getWriter().print(result);

	}

	public String list2() {
		this.page = this.getEmployeeService().getPae(page);
		return "json-page";
	}

	public String list() {
		if (this.page == null) {
			this.page = new Page<>();
		}
		this.page = this.getEmployeeService().getPae(page);
		System.out.println("page=" + page);
		return "list";
	}

	public String login() {
		System.out.println("EmployeeAction--login");

		// String loginName = model.getLoginName();
		// String password = model.getPassword();
		// Employee employee = getEmployeeService().login(loginName, password);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new LoginNameNotFoundException();
		}

		Object principle = authentication.getPrincipal();
		System.out.println("principle====" + principle);
		SecurityUser su = (SecurityUser) principle;

		Employee employee = su.getEmployee();

		getEmployeeService().updateVisitedTimes(employee);

		this.sessionMap.put("employee", employee);
		return SUCCESS;
	}
}
