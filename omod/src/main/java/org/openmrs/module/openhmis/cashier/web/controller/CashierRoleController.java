package org.openmrs.module.openhmis.cashier.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.APIException;
import org.openmrs.api.UserService;
import org.openmrs.module.openhmis.cashier.model.CashierRole;
import org.openmrs.module.openhmis.cashier.web.CashierWebConstants;
import org.openmrs.util.RoleConstants;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(CashierWebConstants.CASHIER_ROLE_PAGE)
public class CashierRoleController {
	protected final Log log = LogFactory.getLog(getClass());

	private UserService userService;
	
	@Autowired
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
		} else if (param.equals("remove")) {
			removeCashierPrivileges(cashierRole.getPrivRemoved());

			session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "openhmis.cashier.roleCreation.page.feedback.remove");
		} else if (param.equals("new")) {
			// Validate that role name is defined and does not already exist
			if (cashierRole.getNewCashierRole() == "") {
				errors.rejectValue("role", "openhmis.cashier.roleCreation.page.feedback.error.blankRole");
			} else if (checkForDuplicateRole(cashierRole.getNewCashierRole())) {
				errors.rejectValue("role", "openhmis.cashier.roleCreation.page.feedback.error.existingRole");
			} else {
				// We're good, create the role
				createNewRole(cashierRole.getNewCashierRole());

				session.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "openhmis.cashier.roleCreation.page.feedback.new");
			}
		}

		render(model);
	}
	
	private Boolean checkForDuplicateRole(String role) {
		for (Role name : userService.getAllRoles()) {
			if (name.getRole().equals(role)) {
				return true;
			}
		}
		return false;
	}
	
	private void addCashierPrivileges(String selectedRole) throws Exception {
		Role role = userService.getRoleByUuid(selectedRole);
		if (role == null) {
			throw new APIException("The role '" + selectedRole + "' could not be found.");
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
			throw new APIException("The role '" + selectedRole + "' could not be found.");
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
		Set<Privilege> privileges = new HashSet<Privilege>();
		
		privileges.add(userService.getPrivilege("View Cashier Bills"));
		privileges.add(userService.getPrivilege("Manage Cashier Bills"));
		privileges.add(userService.getPrivilege("Adjust Cashier Bills"));
		privileges.add(userService.getPrivilege("Give Refund"));
		privileges.add(userService.getPrivilege("Reprint Receipt"));
		privileges.add(userService.getPrivilege("Purge Cashier Bills"));
		privileges.add(userService.getPrivilege("View Cashier Items"));
		privileges.add(userService.getPrivilege("Manage Cashier Items"));
		privileges.add(userService.getPrivilege("Purge Cashier Items"));
		privileges.add(userService.getPrivilege("View Cashier Metadata"));
		privileges.add(userService.getPrivilege("Manage Cashier Metadata"));
		privileges.add(userService.getPrivilege("Purge Cashier Metadata"));
		privileges.add(userService.getPrivilege("View Cashier Timesheets"));
		privileges.add(userService.getPrivilege("Manage Cashier Timesheets"));
		privileges.add(userService.getPrivilege("Purge Cashier Timesheets"));
		privileges.add(userService.getPrivilege("View Jasper Reports"));
		
		return privileges;
	}
	
	private void saveRole(Role role) throws Exception {
		try {
			userService.saveRole(role);
		} catch(Exception e) {
			log.error("The role '" + role.getName() + "' could not be saved.", e);

			throw e;
		}
	}
}
