/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 2.0 (the "License"); you may not use this file except in
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
package org.openmrs.module.openhmis.cashier.web.controller;

import org.openmrs.Patient;
import org.directwebremoting.util.Logger;
import org.openmrs.api.PatientService;
import org.openmrs.module.openhmis.cashier.api.IBillService;
import org.openmrs.module.openhmis.cashier.api.model.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import java.lang.Object;

@Controller
@RequestMapping(value = "/module/openhmis/cashier/portlets/Bills")
public class BillsController {
	
	private static final Logger LOG = Logger.getLogger(BillsController.class);
	private IBillService billService;
	
	@Autowired
	public BillsController(IBillService billServce, PatientService patientService) {
		this.billService = billServce;
	}

	@RequestMapping(method = RequestMethod.GET)
	public void bills(	ModelMap model, 
						@RequestParam(value = "patientId", required = false) String patientId,
						@RequestParam(value = "patient", required = false) Patient patient) {
		LOG.info("In bills controller");
		
		if (patient != null)
		{
			LOG.info("patient provided, retrieving bills");
			List<Bill> bills = billService.findPatientBills(patient, null);
			model.addAttribute("bills", bills);
		}
		else if (patientId != null)
		{
			LOG.info("patientId provided, retrieving bills");
			List<Bill> bills = billService.findPatientBills(Integer.parseInt(patientId), null);
			model.addAttribute("bills", bills);
		}		
	}
}
