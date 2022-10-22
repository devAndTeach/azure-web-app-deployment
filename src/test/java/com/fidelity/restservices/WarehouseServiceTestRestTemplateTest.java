package com.fidelity.restservices;

import java.net.URI;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;

import com.fidelity.business.Gadget;
import com.fidelity.business.Widget;
import com.fidelity.restservices.dto.DatabaseRequestResultDTO;
import com.fidelity.warehouseservice.WarehouseServiceApplication;

/**
 * These test cases assert the behavior of our deployed service. To do that we will
 * start the application up and listen for a connection like it would do in
 * production, and then send an HTTP request and assert the response.
 * 
 * Note that Spring Boot has provided a TestRestTemplate for you automatically.
 * All you have to do is @Autowired it.
 * 
 * Instead of using a specific port number when constructing the server URL, simply use a 
 * relative URL. For example:
 * 	   String request = "/warehouse/widgets";
 * 
 * By using webEnvironment = WebEnvironment.RANDOM_PORT in SpringBootTest, a random port 
 * will be selected and added to the URL automatically.
 * The port that is being used appears in the Console window when the application is run.
 *
 * Also note the use of @Sql on the class to execute the database setup scripts before
 * each test case. Because @SpringBootTest runs Tomcat in a different thread than the
 * test cases themselves, @Transactional has no effect here. So we need to re-initialize 
 * the database before each test case.
 * Just another reason not to use the production database in testing :)
 * 
 * The database scripts referenced in @Sql are in the folder src/test/resources
 * 
 * For some test cases we'll need to query or modify the database directly, so we'll
 * use Spring's JdbcTestUtils class, which has methods like countRowsInTable() and
 * deleteFromTables().
 *  
 * Note that Spring Boot needs to find an application class in order to scan
 * for components. The trivial class com.fidelity.TestApplication in src/test/java 
 * contains the @SpringBootApplication annotation, which triggers the component scan.
 * 
 * @author ROI Instructor
 */

@SpringBootTest(classes=WarehouseServiceApplication.class, 
                webEnvironment=WebEnvironment.RANDOM_PORT)
@Sql(scripts={"classpath:schema-dev.sql", "classpath:data-dev.sql"},
     executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD) 
public class WarehouseServiceTestRestTemplateTest {
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;  // for executing SQL queries
	
	// **** Widget Tests ****
	/**
	 * This test verifies the WarehouseController can query successfully for all the
	 * Widgets in the Warehouse.
	 */
	@Test
	public void testQueryForAllWidgets() {
		// get the row count from the Widgets table
		int widgetCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "widgets");
		
		String request = "/warehouse/widgets";

		ResponseEntity<Widget[]> response = restTemplate.getForEntity(request, Widget[].class);
		
		// verify the response HTTP status is OK
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
		
		// verify that the service returned all Widgets in the database
		Widget[] responseWidgets = response.getBody();
		assertThat(responseWidgets.length, is(equalTo(widgetCount))); 
		
		// spot-check a few Widgets
		assertThat(responseWidgets[0], is(equalTo(
				   new Widget(1, "Low Impact Widget", 12.99, 2, 3))));
		assertThat(responseWidgets[2], is(equalTo(
				   new Widget(3, "High Impact Widget", 89.99, 10, 8))));
	}

	/**
	 * This test verifies the WarehouseController successfully handles the case
	 * where there are no Widgets in the Warehouse.
	 */
	@Test
	public void testQueryForAllWidgets_NoWidgetsInDb() {
		// delete all rows from the Widgets table
		JdbcTestUtils.deleteFromTables(jdbcTemplate, "widgets");
		
		String request = "/warehouse/widgets";

		ResponseEntity<String> response = restTemplate.getForEntity(request, String.class);
		
		// verify the response HTTP status is 204 (NO_CONTENT)
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));
		
		// verify that the response body is empty
		assertThat(response.getBody(), is(emptyOrNullString()));
	}

	/**
	 * This test verifies the WarehouseController successfully handles a database exception.
	 */
	@Test
	public void testQueryForAllWidgets_DbException() {
		// drop the Widgets table to force a database exception
		JdbcTestUtils.dropTables(jdbcTemplate, "widgets");
		
		String request = "/warehouse/widgets";

		ResponseEntity<String> response = restTemplate.getForEntity(request, String.class);
		
		// verify the response HTTP status is 500
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR)));
		
		// We won't test the message in the response because the message
		// might change in the future, and that would break this test. But we 
		// could make the test less brittle by matching a regular expression pattern:
//		   assertThat(response.getBody(), matchesPattern(".*[Ee]rror.*database.*"));
	}

	/**
	 * This test verifies the WarehouseController can query successfully for one Widget.
	 */
	@Test
	public void testQueryForWidgetById() {
		String request = "/warehouse/widgets/3";

		ResponseEntity<Widget> response = restTemplate.getForEntity(request, Widget.class);
		
		// verify the response HTTP status
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
		
		// verify that the service returned the correct Widget
		assertThat(response.getBody(), is(equalTo(
				   new Widget(3, "High Impact Widget", 89.99, 10, 8))));
	}

	/**
	 * This test verifies the WarehouseController handles a query for a non-existent Widget.
	 */
	@Test
	public void testQueryForWidgetById_NotPresent() {
		String request = "/warehouse/widgets/99";

		ResponseEntity<Widget> response = restTemplate.getForEntity(request, Widget.class);
		
		// verify the response HTTP status
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
	}

	/**
	 * This test verifies the WarehouseController correctly handles a database exception.
	 */
	@Test
	public void testQueryForWidgetById_DbException() {
		// drop the Widgets table to force a database exception
		JdbcTestUtils.dropTables(jdbcTemplate, "widgets");

		String request = "/warehouse/widgets/99";

		ResponseEntity<Widget> response = restTemplate.getForEntity(request, Widget.class);
		
		// verify the response HTTP status
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR)));
	}

	/**
	 * This test verifies the WarehouseController can successfully add a Widget to the
	 * Warehouse.
	 */
	@Test
	public void testAddWidgetToWarehouse() throws Exception {
		int widgetCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "widgets");
		
		int id = 42;
		Widget w = new Widget(id, "Test widget", 4.52, 20, 10);

		String request = "/warehouse/widgets";
		
		ResponseEntity<DatabaseRequestResultDTO> response = 
				restTemplate.postForEntity(request, w, DatabaseRequestResultDTO.class);
		
		// verify the response HTTP status and response body
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(response.getBody().getRowCount(), is(equalTo(1))); // {"rowCount": 1}
		
		// verify that one row was added to the Widgets table
		int newWidgetCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "widgets");
		assertThat(newWidgetCount, is(equalTo(widgetCount + 1)));
		
		// verify that the new widget is in the Widgets table
		assertThat(1, equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "widgets", "id = " + id)));
	}

	/**
	 * This test verifies the WarehouseController can successfully remove a Widget from
	 * the Warehouse.
	 */
	@Test
	public void testRemoveWidgetFromWarehouse() throws Exception{
		int widgetCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "widgets");

		int id = 1;
		String request = "/warehouse/widgets/" + id;
		
		RequestEntity<Void> requestEntity = 
				RequestEntity.delete(new URI(request)).build();
		
		ResponseEntity<DatabaseRequestResultDTO> response = 
				restTemplate.exchange(requestEntity, DatabaseRequestResultDTO.class);
		
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(response.getBody().getRowCount(), is(equalTo(1))); // {"rowCount": 1}

		// verify that one row was deleted from the Widgets table
		int newWidgetCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "widgets");
		assertThat(newWidgetCount, is(equalTo(widgetCount - 1)));
		
		// verify that the widget is no longer in the Widgets table
		assertThat(0, equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "widgets", "id = " + id)));
	}
	
	/**
	 * This test verifies the WarehouseController will return the HTTP status
	 * NotFound when attempting to remove a Widget that is not in the Warehouse.
	 */
	@Test
	public void testRemoveWidgetFromWarehouse_NotPresent() throws Exception{
		int widgetCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "widgets");

		int id = 99;
		String request = "/warehouse/widgets/" + id;
		
		RequestEntity<Void> requestEntity = 
				RequestEntity.delete(new URI(request)).build();
		
		ResponseEntity<DatabaseRequestResultDTO> response = 
				restTemplate.exchange(requestEntity, DatabaseRequestResultDTO.class);
		
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
		assertThat(response.getBody().getRowCount(), is(equalTo(0))); // {"rowCount": 0}

		// verify that no rows were deleted from the Widgets table
		assertThat(widgetCount, is(equalTo(
				JdbcTestUtils.countRowsInTable(jdbcTemplate, "widgets"))));
	}

	// **** Gadget Tests ****
	
	/**
	 * This test verifies the WarehouseController can query successfully for all the
	 * Gadgets in the Warehouse.
	 */
	@Test
	public void testQueryForAllGadgets() {
		// get the row count from the Gadgets table
		int gadgetCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "gadgets");
		
		String request = "/warehouse/gadgets";

		ResponseEntity<Gadget[]> response = restTemplate.getForEntity(request, Gadget[].class);
		
		// verify the response HTTP status
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
		
		// verify that the service returned all Gadgets in the database
		Gadget[] responseGadgets = response.getBody();
		assertThat(responseGadgets.length, is(equalTo(gadgetCount))); 
		
		// spot-check a few Gadgets
		assertThat(responseGadgets[0], is(equalTo(
				   new Gadget(1, "Two Cylinder Gadget", 19.99, 2))));
		assertThat(responseGadgets[2], is(equalTo(
				   new Gadget(3, "Eight Cylinder Gadget", 49.99, 8))));
	}

	/**
	 * This test verifies the WarehouseController successfully handles the case
	 * where there are no Gadgets in the Warehouse.
	 */
	@Test
	public void testQueryForAllGadgets_NoGadgetsInDb() {
		// delete all rows from the Gadgets table
		JdbcTestUtils.deleteFromTables(jdbcTemplate, "gadgets");
		
		String request = "/warehouse/gadgets";

		ResponseEntity<String> response = restTemplate.getForEntity(request, String.class);
		
		// verify the response HTTP status is 204
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));
		
		// verify that the response body is empty
		assertThat(response.getBody(), is(emptyOrNullString()));
	}

	/**
	 * This test verifies the WarehouseController successfully handles a database exception.
	 */
	@Test
	public void testQueryForAllGadgets_DbException() {
		// drop the Gadgets table to force a database exception
		JdbcTestUtils.dropTables(jdbcTemplate, "gadgets");
		
		String request = "/warehouse/gadgets";

		ResponseEntity<String> response = restTemplate.getForEntity(request, String.class);
		
		// verify the response HTTP status is 500
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR)));
		
		// We won't test the message in the response because the message
		// might change in the future, and that would break this test. But we 
		// could make the test less brittle by matching a regular expression pattern:
//		assertThat(response.getBody(), matchesPattern(".*[Ee]rror.*database.*"));
	}

	/**
	 * This test verifies the WarehouseController can query successfully for one Gadget.
	 */
	@Test
	public void testQueryForGadgetById() {
		String request = "/warehouse/gadgets/3";

		ResponseEntity<Gadget> response = restTemplate.getForEntity(request, Gadget.class);
		
		// verify the response HTTP status
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
		
		// verify that the service returned the correct Gadget
		assertThat(response.getBody(), is(equalTo(
				   new Gadget(3, "Eight Cylinder Gadget", 49.99, 8))));
	}

	/**
	 * This test verifies the WarehouseController handles a query for a non-existent Gadget.
	 */
	@Test
	public void testQueryForGadgetById_NotPresent() {
		String request = "/warehouse/gadgets/99";

		ResponseEntity<Gadget> response = restTemplate.getForEntity(request, Gadget.class);
		
		// verify the response HTTP status
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
	}
	
	/**
	/**
	 * This test verifies the WarehouseController correctly handles a database exception.
	 */
	@Test
	public void testQueryForGadgetById_DbException() {
		// drop the Gadgets table to force a database exception
		JdbcTestUtils.dropTables(jdbcTemplate, "gadgets");

		String request = "/warehouse/gadgets/99";

		ResponseEntity<Gadget> response = restTemplate.getForEntity(request, Gadget.class);
		
		// verify the response HTTP status
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.INTERNAL_SERVER_ERROR)));
	}

	/**
	 * This test verifies the WarehouseController can successfully add a Gadget to the
	 * Warehouse.
	 */
	@Test
	public void testAddGadgetToWarehouse() throws Exception {
		String tableName = "gadgets";
		int gadgetCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, tableName);
		
		int id = 42;
		Gadget w = new Gadget(42, "Test Gadget", 19.99, 2);

		String request = "/warehouse/gadgets";
		
		ResponseEntity<DatabaseRequestResultDTO> response = 
				restTemplate.postForEntity(request, w, DatabaseRequestResultDTO.class);
		
		// verify the response HTTP status and response body
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(response.getBody().getRowCount(), is(equalTo(1))); // {"rowCount": 1}
		
		// verify that one row was added to the Gadgets table
		int newWidgetCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, tableName);
		assertThat(newWidgetCount, is(equalTo(gadgetCount + 1)));
		
		// verify that the new Gadget is in the Gadgets table
		assertThat(1, equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, tableName, "id = " + id)));
	}

	/**
	 * This test verifies the WarehouseController can successfully remove a Gadget from
	 * the Warehouse.
	 */
	@Test
	public void testRemoveGadgetFromWarehouse() throws Exception{
		int gadgetCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "gadgets");

		int id = 1;
		String request = "/warehouse/gadgets/" + id;
		
		RequestEntity<Void> requestEntity = 
				RequestEntity.delete(new URI(request)).build();
		
		ResponseEntity<DatabaseRequestResultDTO> response = 
				restTemplate.exchange(requestEntity, DatabaseRequestResultDTO.class);
		
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));
		assertThat(response.getBody().getRowCount(), is(equalTo(1))); // {"rowCount": 1}

		// verify that one row was deleted from the Gadgets table
		int newWidgetCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "gadgets");
		assertThat(newWidgetCount, is(equalTo(gadgetCount - 1)));
		
		// verify that the gadget is no longer in the Gadgets table
		assertThat(0, equalTo(
			JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "gadgets", "id = " + id)));
	}

	/**
	 * This test verifies the WarehouseController will return the HTTP status
	 * NotFound when attempting to remove a Gadget that is not in the Warehouse.
	 */
	@Test
	public void testRemoveGadgetFromWarehouse_NotPresent() throws Exception{
		int gadgetCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, "gadgets");

		int id = 99;
		String request = "/warehouse/gadgets/" + id;
		
		RequestEntity<Void> requestEntity = 
				RequestEntity.delete(new URI(request)).build();
		
		ResponseEntity<DatabaseRequestResultDTO> response = 
				restTemplate.exchange(requestEntity, DatabaseRequestResultDTO.class);
		
		assertThat(response.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
		assertThat(response.getBody().getRowCount(), is(equalTo(0))); // {"rowCount": 0}

		// verify that no rows were deleted from the Gadgets table
		assertThat(gadgetCount, is(equalTo(
				JdbcTestUtils.countRowsInTable(jdbcTemplate, "gadgets"))));
	}

}
