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
package org.openmrs.module.openhmis.cashier.api.model;

import org.apache.commons.lang.StringUtils;
import org.openmrs.BaseCustomizableMetadata;
import org.openmrs.customdatatype.Customizable;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Model class that represents an item that can be billed by an institution.
 */
public class Item extends BaseCustomizableMetadata<ItemAttribute> implements Customizable<ItemAttribute> {
	private Integer itemId;
	private Set<ItemCode> codes;
	private Set<ItemPrice> prices;
	private Department department;
	private ItemPrice defaultPrice;

	public Item() {
	}

	public Item(Integer itemId) {
		this.itemId = itemId;
	}

	@Override
	public Integer getId() {
		return this.itemId;
	}

	@Override
	public void setId(Integer id) {
		this.itemId = id;
	}

	public Set<ItemCode> getCodes() {
		return codes;
	}

	public void setCodes(Set<ItemCode> codes) {
		this.codes = codes;
	}

	public ItemCode addCode(String codeName, String code) {
		if (StringUtils.isEmpty(code)) {
			throw new IllegalArgumentException("The item code must be defined.");
		}

		ItemCode itemCode = new ItemCode(code, codeName);
		itemCode.setItem(this);

		addCode(itemCode);

		return itemCode;
	}

	public void addCode(ItemCode code) {
		if (code != null) {
			if (codes == null) {
				codes = new HashSet<ItemCode>();
			}

			codes.add(code);
		}
	}

	public void removeCode(ItemCode code) {
		if (code != null) {
			if (codes == null) {
				codes = new HashSet<ItemCode>();
			}

			codes.remove(code);
		}
	}

	public Set<ItemPrice> getPrices() {
		return prices;
	}

	public void setPrices(Set<ItemPrice> prices) {
		this.prices = prices;
	}

	public ItemPrice addPrice(String priceName, BigDecimal price) {
		if (StringUtils.isEmpty(priceName)) {
			throw new IllegalArgumentException("The price name must be defined.");
		}
		if (price == null) {
			throw new NullPointerException("The item price must be defined.");
		}

		ItemPrice itemPrice = new ItemPrice(price, priceName);

		addPrice(itemPrice);

		return itemPrice;
	}

	public void addPrice(ItemPrice price) {
		if (price != null) {
			if (prices == null) {
				prices = new HashSet<ItemPrice>();
			}

			price.setItem(this);
			prices.add(price);
		}
	}

	public void removePrice(ItemPrice price) {
		if (price != null) {
			if (prices == null) {
				prices = new HashSet<ItemPrice>();
			}

			prices.remove(price);
		}
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public ItemPrice getDefaultPrice() {
		return defaultPrice;
	}

	public void setDefaultPrice(ItemPrice defaultPrice) {
		this.defaultPrice = defaultPrice;
	}
}
