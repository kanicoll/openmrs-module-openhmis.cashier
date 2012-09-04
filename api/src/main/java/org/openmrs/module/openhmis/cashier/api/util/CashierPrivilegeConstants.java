/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.openhmis.cashier.api.util;

import org.openmrs.annotation.AddOnStartup;

public class CashierPrivilegeConstants {
	@AddOnStartup(description = "Able to add/edit/delete billing items", core = false)
	public static final String MANAGE_ITEMS = "Manage Cashier Items";

	@AddOnStartup(description = "Able to view billing items", core = false)
	public static final String VIEW_ITEMS = "View Cashier Items";

	public static final String PURGE_ITEMS = "Purge Cashier Items";

	@AddOnStartup(description = "Able to add/edit/delete bills", core = false)
	public static final String MANAGE_BILLS = "Manage Cashier Bills";

	@AddOnStartup(description = "Able to view bills", core = false)
	public static final String VIEW_BILLS = "View Cashier Bills";

	public static final String PURGE_BILLS = "Purge Cashier Bills";

	@AddOnStartup(description = "Able to add/edit/delete cashier module metadata", core = false)
	public static final String MANAGE_METADATA = "Manage Cashier Metadata";

	@AddOnStartup(description = "Able to view cashier module metadata", core = false)
	public static final String VIEW_METADATA = "View Cashier Metadata";

	public static final String PURGE_METADATA = "Purge Cashier Metadata";

}