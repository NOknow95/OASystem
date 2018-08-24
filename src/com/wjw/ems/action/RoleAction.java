package com.wjw.ems.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.wjw.ems.entities.Role;
import com.wjw.ems.service.AuthoritySerivce;

@Scope("prototype")
@Controller
public class RoleAction extends BaseAction<Role> {

	@Autowired
	private AuthoritySerivce authoritySerivce;

	public String input() {
		requsetMap.put("subAuthorities", authoritySerivce.getByIsNotNull("parentAuthority"));
		requsetMap.put("parentAuthority", authoritySerivce.getByIsNull("parentAuthority"));
		return "role-input";
	}

}
