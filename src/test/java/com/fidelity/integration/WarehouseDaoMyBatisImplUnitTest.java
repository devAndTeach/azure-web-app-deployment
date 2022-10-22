package com.fidelity.integration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.datasource.init.*;
import com.fidelity.business.Gadget;
import com.fidelity.business.Widget;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * This is an integration test for WarehouseDaoMyBatisImpl.
 * 
 * Notice that the database schema and data scripts are run after setting the DataSource. 
 * This insures the database is in a known state prior to running the tests.
 * The database scripts are in the folder: src/test/resources/
 *  
 * To verify that the DAO is actually working, we'll need to query the database 
 * directly, so we'll use Spring's JdbcTestUtils class, which has methods like 
 * countRowsInTable() and deleteFromTables().
 *
 * Notice the use of @Transactional to automatically rollback 
 * any changes to the database that may be made in a test.
 * 
 * Note that Spring Boot needs to find an application class in order to scan
 * for components. The trivial class com.fidelity.TestApplication in src/test/java 
 * contains the @SpringBootApplication annotation, which triggers the component scan.
 * 
 * @author ROI Instructor
 *
 */
@SpringBootTest
@Transactional
public class WarehouseDaoMyBatisImplUnitTest {

	@Autowired
	private WarehouseDao dao;

	@Autowired
	private JdbcTemplate jdbcTemplate;  // for executing SQL queries
	
	// Because the test database is tiny, we can check all products.
	// If the database was larger, we could just spot-check a few products.
	// These are the values defined in the data-dev.sql script.
	
	private static List<Widget> allWidgets = Arrays.asList(
		new Widget(1, "Low Impact Widget", 12.99, 2, 3),
		new Widget(2, "Medium Impact Widget", 42.99, 5, 5),
		new Widget(3, "High Impact Widget", 89.99, 10, 8)
	);

	private static List<Gadget> allGadgets = Arrays.asList(
		new Gadget(1, "Two Cylinder Gadget", 19.99, 2), 
		new Gadget(2, "Four Cylinder Gadget", 29.99, 4), 
		new Gadget(3, "Eight Cylinder Gadget", 49.99, 8) 
	);

	// ***** Widget Tests *****
	@Test
	void testGetAllWidgets() {
		List<Widget> widgets = dao.getAllWidgets();
		
		// verify the Widgets from the database 
		assertThat(widgets, is(equalTo(allWidgets)));
	}

	@Test
	void testGetWidget() {
		Widget widget = dao.getWidget(1);
		
		// verify the Widget from the database
		assertThat(widget, is(equalTo(allWidgets.get(0))));
	}

	@Test
	void testDeleteWidget() {
		int id = 1;
		// verify that Widget 1 is in the database
		assertThat(1, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "widgets", "id = " + id))));

		int rows = dao.deleteWidget(id);
		
		// verify that 1 row was deleted
		assertThat(rows, is(equalTo(1)));
		
		// verify that Widget 1 is no longer in the database
		assertThat(0, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "widgets", "id = " + id))));
	}

	@Test
	void testInsertWidget() {
		int id = 42;
		
		// verify that Widget with id = 42 is NOT in the database
		assertThat(0, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "widgets", "id = " + id))));

		Widget w = new Widget(id, "Test widget", 4.52, 20, 10);

		int rows = dao.insertWidget(w);
		
		// verify that 1 row was inserted
		assertThat(rows, is(equalTo(1)));
		
		// verify that Widget with id = 42 IS in the database
		assertThat(1, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "widgets", "id = " + id))));
	}

	@Test
	void testUpdateWidget() {
		int id = 1;
		
		// load the original Widget from the database
		Widget originalWidget = loadWidgetFromDb(id);
		
		// modify the local Widget
		originalWidget.setPrice(originalWidget.getPrice() + 1.0);

		int rows = dao.updateWidget(originalWidget);
		
		// verify that 1 row was updated
		assertThat(rows, is(equalTo(1)));
		
		// reload widget from database 
		Widget updatedWidget = loadWidgetFromDb(id);
		
		// verify that only the price was updated in the database
		assertThat(originalWidget, is(equalTo(updatedWidget)));

	}

	// ***** Gadget Tests *****
	@Test
	void testGetAllGadgets() {
		List<Gadget> gadgets = dao.getAllGadgets();
		
		// verify the Gadgets from the database
		assertThat(gadgets, is(equalTo(allGadgets)));
	}

	@Test
	void testGetGadget() {
		Gadget gadget = dao.getGadget(1);
		
		// verify the Gadget from the database
		assertThat(gadget, is(equalTo(allGadgets.get(0))));
	}

	@Test
	void testDeleteGadget() {
		int id = 1;
		
		// verify that Gadget with id = 1 is in the database
		assertThat(1, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "gadgets", "id = " + id))));

		int rows = dao.deleteGadget(id);
		
		// verify that 1 row was deleted
		assertThat(rows, is(equalTo(1)));
		
		// verify that Gadget 1 is no longer in the database
		assertThat(0, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "gadgets", "id = " + id))));
	}

	@Test
	void testInsertGadget() {
		int id = 42;
		
		// verify the Gadget with id = 42 is NOT in the database
		assertThat(0, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "gadgets", "id = " + id))));

		Gadget g = new Gadget(id, "Test Gadget", 99.99, 2);

		int rows = dao.insertGadget(g);
		
		// verify that 1 row was inserted
		assertThat(rows, is(equalTo(1)));
		
		// verify that the Gadget with id = 42 IS in the database
		assertThat(1, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "gadgets", "id = " + id))));
	}

	@Test
	void testUpdateGadget() {
		int id = 1;
		
		// load the Gadget from the database
		Gadget originalGadget = loadGadgetFromDb(id);
		
		// modify the local Gadget
		originalGadget.setCylinders(originalGadget.getCylinders() * 2);

		int rows = dao.updateGadget(originalGadget);
		
		// verify that 1 row was updated
		assertThat(rows, is(equalTo(1)));
				
		// reload gadget from database 
		Gadget updatedGadget = loadGadgetFromDb(id);
		
		// verify that only the cylinder count was updated in the database
		assertThat(originalGadget, is(equalTo(updatedGadget)));

	}

	// ***** Utility Methods Used in the Tests *****
	
	// Load the Widget with the specified id from the database
	private Widget loadWidgetFromDb(int id) {
		String sql = "select * from widgets where id = " + id;
		
		return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> 
			new Widget(rs.getInt("id"), rs.getString("description"), rs.getDouble("price"), 
					   rs.getInt("gears"), rs.getInt("sprockets")));
	}
	
	// Load the Gadget with the specified id from the database
	private Gadget loadGadgetFromDb(int id) {
		String sql = "select * from gadgets where id = " + id;
		
		return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> 
			new Gadget(rs.getInt("id"), rs.getString("description"), 
					   rs.getDouble("price"), rs.getInt("cylinders")));
	}
	
}
