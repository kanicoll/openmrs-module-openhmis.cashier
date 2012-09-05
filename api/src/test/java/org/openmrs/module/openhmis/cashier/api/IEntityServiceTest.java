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

package org.openmrs.module.openhmis.cashier.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

public abstract class IEntityServiceTest<S extends IEntityService<E>, E extends BaseOpenmrsObject> extends BaseModuleContextSensitiveTest {
	protected S service;

	protected abstract S createService();
	protected abstract E createEntity(boolean valid);
	protected abstract int getTestEntityCount();
	protected abstract void updateEntityFields(E entity);

	protected void assertEntity(E expected, E actual) {
		Assert.assertNotNull(expected);
		Assert.assertNotNull(actual);

		Assert.assertEquals(expected.getId(), actual.getId());
		Assert.assertEquals(expected.getUuid(), actual.getUuid());
	}

	@Before
	public void before() throws Exception{
		service = createService();
	}

	/**
	 * @verifies throw IllegalArgumentException if the entity is null
	 * @see IEntityService#save(org.openmrs.OpenmrsObject)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void save_shouldThrowIllegalArgumentExceptionIfTheEntityIsNull() throws Exception {
		service.save(null);
	}

	/**
	 * @verifies validate the entity before saving
	 * @see IEntityService#save(org.openmrs.OpenmrsObject)
	 */
	@Test(expected = APIException.class)
	public void save_shouldValidateTheEntityBeforeSaving() throws Exception {
		E entity = createEntity(false);

		service.save(entity);
	}

	/**
	 * @verifies return saved entity
	 * @see IEntityService#save(org.openmrs.OpenmrsObject)
	 */
	@Test
	public void save_shouldReturnSavedEntity() throws Exception {
		E entity = createEntity(true);

		E result = service.save(entity);
		Context.flushSession();

		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getId());
	}

	/**
	 * @verifies update the entity successfully
	 * @see IEntityService#save(org.openmrs.OpenmrsObject)
	 */
	@Test
	public void save_shouldUpdateTheEntitySuccessfully() throws Exception {
		E entity = service.getById(0);
		Assert.assertNotNull(entity);

		updateEntityFields(entity);

		service.save(entity);
		Context.flushSession();

		E updatedEntity = service.getById(entity.getId());
		assertEntity(entity, updatedEntity);
	}

	/**
	 * @verifies create the entity successfully
	 * @see IEntityService#save(org.openmrs.OpenmrsObject)
	 */
	@Test
	public void save_shouldCreateTheEntitySuccessfully() throws Exception {
		E entity = createEntity(true);

		entity = service.save(entity);
		Context.flushSession();

		E result = service.getById(entity.getId());
		assertEntity(entity, result);
	}

	/**
	 * @verifies throw IllegalArgumentException if the entity is null
	 * @see IEntityService#purge(org.openmrs.OpenmrsObject)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void purge_shouldThrowIllegalArgumentExceptionIfTheEntityIsNull() throws Exception {
		service.purge(null);
	}

	/**
	 * @verifies delete the specified entity
	 * @see IEntityService#purge(org.openmrs.OpenmrsObject)
	 */
	@Test
	public void purge_shouldDeleteTheSpecifiedEntity() throws Exception {
		E entity = service.getById(0);
		Assert.assertNotNull(entity);

		service.purge(entity);
		Context.flushSession();

		entity = service.getById(0);
		Assert.assertNull(entity);
	}

	/**
	 * @verifies return all entity records
	 * @see IEntityService#getAll()
	 */
	@Test
	public void getAll_shouldReturnAllEntityRecords() throws Exception {
		List<E> entities = service.getAll();
		Assert.assertNotNull(entities);

		Assert.assertEquals(entities.size(), getTestEntityCount());
	}

	/**
	 * @verifies return an empty list if there are no entities
	 * @see IEntityService#getAll()
	 */
	@Test
	public void getAll_shouldReturnAnEmptyListIfThereAreNoEntities() throws Exception {
		List<E> entities = service.getAll();
		for (E entity : entities) {
			service.purge(entity);
		}

		Context.flushSession();

		entities = service.getAll();
		Assert.assertNotNull(entities);
		Assert.assertEquals(0, entities.size());
	}

	/**
	 * @verifies return the entity with the specified id
	 * @see IEntityService#getById(int)
	 */
	@Test
	public void getById_shouldReturnTheEntityWithTheSpecifiedId() throws Exception {
		E entity = service.getById(0);

		Assert.assertEquals((Integer)0, entity.getId());
	}

	/**
	 * @verifies return null if no entity can be found.
	 * @see IEntityService#getById(int)
	 */
	@Test
	public void getById_shouldReturnNullIfNoEntityCanBeFound() throws Exception {
		E entity = service.getById(-100);

		Assert.assertNull(entity);
	}

	/**
	 * @verifies find the entity with the specified uuid
	 * @see IEntityService#getByUuid(String)
	 */
	@Test
	public void getByUuid_shouldFindTheEntityWithTheSpecifiedUuid() throws Exception {
		E entity = service.getById(0);
		E uuidEntity = service.getByUuid(entity.getUuid());

		assertEntity(entity, uuidEntity);
	}

	/**
	 * @verifies return null if no entity is found
	 * @see IEntityService#getByUuid(String)
	 */
	@Test
	public void getByUuid_shouldReturnNullIfNoEntityIsFound() throws Exception {
		E entity = service.getByUuid("Invalid");

		Assert.assertNull(entity);
	}

	/**
	 * @verifies throw IllegalArgumentException if uuid is null
	 * @see IEntityService#getByUuid(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getByUuid_shouldThrowIllegalArgumentExceptionIfUuidIsNull() throws Exception {
		service.getByUuid(null);
	}

	/**
	 * @verifies throw IllegalArgumentException if uuid is empty
	 * @see IEntityService#getByUuid(String)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void getByUuid_shouldThrowIllegalArgumentExceptionIfUuidIsEmpty() throws Exception {
		service.getByUuid("");
	}
}
