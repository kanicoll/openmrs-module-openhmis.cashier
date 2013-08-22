package org.openmrs.module.openhmis.cashier.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.cashier.model.CashierRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openmrs.util.RoleConstants;
import org.openmrs.web.WebConstants;

@Controller
@RequestMapping("/module/openhmis/cashier/cashierRole")
public class CashierRoleController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	private UserService userService;
	
	@Autowired()
	public CashierRoleController(UserService userService) {
		this.userService = userService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public void render(ModelMap model) {
		List<Role> roles = userService.getAllRoles();
		model.addAttribute("roles", roles);
	}

	@RequestMapping(method = RequestMethod.POST)
	public void submit(HttpServletRequest request, CashierRole cashierRole, Errors errors, ModelMap model) throws Exception{
		HttpSession session = request.getSession();
		String param = request.getParameter("role");
		
		if (param.equals("add")) {
			addCashierPrivileges(cashierRole.getPrivAdded());
			session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "openhmis.cashier.roleCreation.page.feedback.add");
			log.info("Cashier privileges added to " + cashierRole.getPrivAdded());
		}
		else if (param.equals("remove")) {
			removeCashierPrivileges(cashierRole.getPrivRemoved());
			session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "openhmis.cashier.roleCreation.page.feedback.remove");
			log.info("Cashier privileges withdrawn from " + cashierRole.getPrivRemoved());
		}
		else if (param.equals("new")) {		
			
			if (cashierRole.getNewCashierRole() == "") {
				errors.rejectValue("role", "openhmis.cashier.roleCreation.page.feedback.error.blankRole");
				render(model);
				return;
			}
			if(checkForDuplicateRole(cashierRole.getNewCashierRole())) {
				errors.rejectValue("role", "openhmis.cashier.roleCreation.page.feedback.error.existingRole");
				render(model);
				return;
			}
			createNewRole(cashierRole.getNewCashierRole());
			session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "openhmis.cashier.roleCreation.page.feedback.new");
		}
		else {}
		render(model);
	}
	
	private Boolean checkForDuplicateRole(String role) {
		for (Role name : userService.getAllRoles()) {
			if (name.getRole().equals(role)) {
				return Boolean.valueOf(true);
			}
		}
		return false;
	}
	
	private void addCashierPrivileges(String selectedRole) throws Exception {
		Role role = userService.getRoleByUuid(selectedRole);
		if (role == null) {
			//log a message
			throw new NullPointerException("Selected role does not exist");
		}

		for (Privilege priv : getCashierPrivileges()) {
			if (!role.hasPrivilege(priv.getName())) {
				role.addPrivilege(priv);
			}
		}
		saveRole(role);
	}
	
	private void removeCashierPrivileges(String selectedRole) throws Exception {	
		Role role = userService.getRoleByUuid(selectedRole);
		
		if (role == null) {
			//log a message
			throw new NullPointerException("Selected role does not exist");
		}

		for (Privilege priv : getCashierPrivileges()) {
			if (role.hasPrivilege(priv.getName())) {
				role.removePrivilege(priv);
			}
		}
		saveRole(role);
	}
	
	private void createNewRole(String newCashierRole) throws Exception {
		
		Role inheritedRole = userService.getRole(RoleConstants.PROVIDER);
		Set<Role> inheritedRoles = new HashSet<Role>();
		inheritedRoles.add(inheritedRole);
		
		Role newRole = new Role();
		newRole.setRole(newCashierRole);
		newRole.setDescription("Creates/manages patient bills");
		newRole.setInheritedRoles(inheritedRoles);
		newRole.setPrivileges(getCashierPrivileges());
		
		saveRole(newRole);
	}

	private Set<Privilege> getCashierPrivileges() {
		
		Set<Privilege> priv_list = new HashSet<Privilege>();
		
		priv_list.add(userService.getPrivilege("View Cashier Bills"));
		priv_list.add(userService.getPrivilege("Manage Cashier Bills"));
		priv_list.add(userService.getPrivilege("Adjust Cashier Bills"));
		priv_list.add(userService.getPrivilege("Give Refund"));
		priv_list.add(userService.getPrivilege("Reprint Receipt"));
		priv_list.add(userService.getPrivilege("Purge Cashier Bills"));
		priv_list.add(userService.getPrivilege("View Cashier Items"));
		priv_list.add(userService.getPrivilege("Manage Cashier Items"));
		priv_list.add(userService.getPrivilege("Purge Cashier Items"));
		priv_list.add(userService.getPrivilege("View Cashier Metadata"));
		priv_list.add(userService.getPrivilege("Manage Cashier Metadata"));
		priv_list.add(userService.getPrivilege("Purge Cashier Metadata"));
		priv_list.add(userService.getPrivilege("View Cashier Timesheets"));
		priv_list.add(userService.getPrivilege("Manage Cashier Timesheets"));
		priv_list.add(userService.getPrivilege("Purge Cashier Timesheets"));
		priv_list.add(userService.getPrivilege("View Jasper Reports"));
		
		return priv_list;
	}
	
	private void saveRole(Role role) throws Exception {
		
		try {
			userService.saveRole(role);
		}
		catch(Exception e) {
			log.error("Error saving role", e);
			throw new Exception(e);
		}
	}
}