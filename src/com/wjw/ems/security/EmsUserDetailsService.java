package com.wjw.ems.security;

import java.util.Collection;
import java.util.HashSet;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wjw.ems.entities.Authority;
import com.wjw.ems.entities.Employee;
import com.wjw.ems.entities.Role;
import com.wjw.ems.service.EmployeeService;

@Service
public class EmsUserDetailsService implements UserDetailsService {

	@Autowired
	private EmployeeService employeeService;

	@Override
	public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
		Employee employee = employeeService.getBy("loginName", loginName);
		if (employee == null) {
			throw new UsernameNotFoundException(loginName);
		}

		String username = loginName;
		String password = employee.getPassword();
		boolean enabled = employee.getEnabled() == 1;

		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		Collection<GrantedAuthority> authorities = new HashSet<>();
		for (Role role : employee.getRoles()) {
			for (Authority authority : role.getAuthorities()) {
				// 把权限的父权限初始化
				Hibernate.initialize(authority.getParentAuthority());
				// 初始化权限关联的Resource
				Hibernate.initialize(authority.getMainResource());

				String name = authority.getName();
				GrantedAuthorityImpl impl = new GrantedAuthorityImpl(name);
				authorities.add(impl);
			}
		}

		SecurityUser user = new SecurityUser(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities, employee);
		return user;
	}

	public class SecurityUser extends User {

		private Employee employee;

		public SecurityUser(String username, String password, boolean enabled, boolean accountNonExpired,
				boolean credentialsNonExpired, boolean accountNonLocked,
				Collection<? extends GrantedAuthority> authorities, Employee employee) {
			super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
			this.employee = employee;
		}

		public Employee getEmployee() {
			return employee;
		}
	}

}
