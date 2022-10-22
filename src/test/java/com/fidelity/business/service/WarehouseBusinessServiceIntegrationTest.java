package com.fidelity.business.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.fidelity.business.Gadget;
import com.fidelity.business.Widget;

/**
 * WarehouseBusinessServiceIntegrationTest is an integration test for WarehouseBusinessServiceImpl.
 * 
 * Notice that the database schema and data scripts are run
 * after setting the DataSource. 
 * The database scripts are in the folder: src/test/resources/
 * This guarantees that the database is in a known state before the tests are run.
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
class WarehouseBusinessServiceIntegrationTest {
	@Autowired
	WarehouseBusinessService service;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;  // for executing SQL queries
	
	// Because the test database is tiny, we can check all products.
	// If the database was larger, we could just spot-check a few products.
	
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
	
	@Test
	void basicSanityTest() {
		assertNotNull(service);
	}

	// ***** Widget Test *****
	@Test
	void testGetAllWidgets() {
		List<Widget> widgets = service.findAllWidgets();
		
		// verify the collection of Widgets
		assertThat(widgets, is(equalTo(allWidgets)));
	}

	@Test
	void testFindWidgetById() {
		int id = 1;
		Widget firstWidget = new Widget(id, "Low Impact Widget", 12.99, 2, 3);
		
		Widget w = service.findWidgetById(id);
		
		// verify the Widget
		assertThat(w, equalTo(firstWidget));
	}

	@Test
	void testDeleteWidget() {
		int id = 1;
		
		// verify that Widget 1 IS in the database
		assertThat(1, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "widgets", "id = " + id))));

		int rows = service.removeWidget(id);
		
		// verify that 1 row was deleted
		assertThat(rows, is(equalTo(1)));
		
		// verify that Widget 1 is NOT in the database
		assertThat(0, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "widgets", "id = " + id))));
	}
	
	@Test
	void testInsertWidget() {
		int id = 42;
		
		// verify Widget 42 is NOT in the database
		assertThat(0, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "widgets", "id = " + id))));

		Widget w = new Widget(id, "Test widget", 4.52, 20, 10);

		int rows = service.addWidget(w);
		
		// verify that 1 row was inserted		
		assertThat(rows, is(equalTo(1)));
		
		// verify that Widget 42 iIS in the database
		assertThat(1, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "widgets", "id = " + id))));
	}
	
	@Test
	void testUpdateWidget() {
		int id = 1;
		
		// load Widget 1 from teh database
		Widget localWidget = loadWidgetFromDb(id);
		
		// modify the local Widget 1
		localWidget.setPrice(localWidget.getPrice() + 1.0);

		int rows = service.modifyWidget(localWidget);
		
		// verify that 1 row was updated
		assertThat(rows, is(equalTo(1)));
		
		// reload widget from database 
		Widget updatedWidget = loadWidgetFromDb(id);
		
		// verify that only the price was updated
		assertThat(localWidget, is(equalTo(updatedWidget)));

	}
	// ***** Gadget Test *****
	
	@Test
	void testGetAllGadgets() {
		List<Gadget> gadgets = service.findAllGadgets();
		
		// verify teh collection of Gadgets
		assertThat(gadgets, is(equalTo(allGadgets)));
	}

	@Test
	void testFindGadgetById() {
		int id = 1;
		Gadget firstGadget = new Gadget(1, "Two Cylinder Gadget", 19.99, 2);
		
		Gadget w = service.findGadgetById(id);
		
		// verify Gadget 1
		assertThat(w, equalTo(firstGadget));
	}

	@Test
	void testDeleteGadget() {
		int id = 1;
		// verify that Gadget 1 IS in the database
		assertThat(1, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "gadgets", "id = " + id))));

		int rows = service.removeGadget(id);
		
		// verify that 1 row was deleted
		assertThat(rows, is(equalTo(1)));
		
		// verify that Gadget 1 is NOT in the database
		assertThat(0, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "gadgets", "id = " + id))));
	}
	
	@Test
	void testInsertGadget() {
		int id = 42;
		
		// verify that Gadget 42 is NOT in the database
		assertThat(0, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "gadgets", "id = " + id))));

		Gadget g = new Gadget(id, "Two Cylinder Gadget", 19.99, 2);

		int rows = service.addGadget(g);
		
		// verify that 1 row was inserted
		assertThat(rows, is(equalTo(1)));
		
		// verify that Gadget 42 IS in the database
		assertThat(1, is(equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "gadgets", "id = " + id))));
	}
	
	@Test
	void testUpdateGadget() {
		int id = 1;
		// load Gadget 1 from database
		Gadget g = loadGadgetFromDb(id);
		
		// modify the local Gadget
		g.setPrice(g.getPrice() + 1.0);

		int rows = service.modifyGadget(g);
		
		// verify that 1 row was updated
		assertThat(rows, is(equalTo(1)));
		
		// reload widget from database 
		Gadget updatedGadget = loadGadgetFromDb(id);
		
		// verify that only the price was updated
		assertThat(g, is(equalTo(updatedGadget)));

	}

	// ***** Utility Methods to Load a Widget or Gadget from the Database *****
	private Widget loadWidgetFromDb(int id) {
		String sql = "select * from widgets where id = " + id;
		
		return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> 
			new Widget(rs.getInt("id"), rs.getString("description"), rs.getDouble("price"), 
					   rs.getInt("gears"), rs.getInt("sprockets")));
	}
	
	private Gadget loadGadgetFromDb(int id) {
		String sql = "select * from gadgets where id = " + id;
		
		return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> 
			new Gadget(rs.getInt("id"), rs.getString("description"), 
					   rs.getDouble("price"), rs.getInt("cylinders")));
	}
	

}
