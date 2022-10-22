package com.fidelity.business.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fidelity.business.Gadget;
import com.fidelity.business.Widget;
import com.fidelity.integration.WarehouseDao;
import com.fidelity.integration.WarehouseDaoMyBatisImpl;

/**
 * JUnit tests for WarehouseBusinessService using Mockito for managing mock objects.
 * 
 * Note that this is a pure unit test, not an integration test: Spring is not 
 * involved in this test at all. We are testing the business service class as a POJO
 * in complete isolation, so we don't want Spring configuration issues to 
 * cause these tests to fail. The other test classes in this package will do 
 * integration testing to verify the Spring configuration. 
 * 
 * The business service has a dependency  on the data access object.
 * Mockito is used to mock the data access object.
 * That way we can focus entirely on the business service functionality.
 * 
 * Spring Boot includes Mockito, but if you are running unit tests
 * without Spring Boot, you need to add Mockito dependencies to pom.xml:
 * 		<dependency>
 *			<groupId>org.mockito</groupId>
 *			<artifactId>mockito-core</artifactId>
 *			<version>3.3.3</version>
 *			<scope>test</scope>
 *		</dependency>
 *		<dependency>
 *			<groupId>org.mockito</groupId>
 *			<artifactId>mockito-junit-jupiter</artifactId>
 *			<version>3.3.3</version>
 *			<scope>test</scope>
 *		</dependency>
 *
 * You also may need to change the version of the junit-platform-surefire-provider dependency:
 *		<dependency>
 *			<groupId>org.junit.platform</groupId>
 *			<artifactId>junit-platform-surefire-provider</artifactId>
 *			<version>1.3.2</version>
 *		</dependency>
 * 
 * 
 * @author ROI Instructor
 *
 */
class WarehouseBusinessServiceUnitTest {
	@Mock
	static WarehouseDao mockDao;
	
	@InjectMocks
	static WarehouseBusinessService service;
	
	@BeforeAll
	static void setUp() throws Exception {
		service = new WarehouseBusinessServiceImpl();
	}
	
	@BeforeEach
	void init() {
		MockitoAnnotations.initMocks(this);		
	}

	// **** Widget Tests ****
	@Test
	void testFindAllWidgets() {
		// Initialize the list of Widgets that will be returned by the mock DAO
		List<Widget> widgets = Arrays.asList(
			    new Widget(1, "Low Impact Widget", 12.99, 2, 3),
			    new Widget(2, "High Impact Widget", 15.99, 4, 5));

		when(mockDao.getAllWidgets())
        	.thenReturn(widgets);
		
		List<Widget> allWidgets = service.findAllWidgets();

		// verify the collection of Widgets
		assertThat(allWidgets, equalTo(widgets));
	}

	@Test
	void testFindWidgetById() {
		int id = 1;
		Widget firstWidget = new Widget(id, "Low Impact Widget", 12.99, 2, 3);
		
		when(mockDao.getWidget(id))
			.thenReturn(firstWidget);
		
		Widget w = service.findWidgetById(id);
		
		// verify the Widget 
		assertThat(w, equalTo(firstWidget));
	}
	
	@Test
	void testDeleteWidget() {
		int id = 1;
		int expected = 1;
		
		when(mockDao.deleteWidget(id))
			.thenReturn(1);
		
		int count = service.removeWidget(id);
		
		// verify the count = 1
		assertThat(count, equalTo(expected));
	}
	
	@Test
	void testInsertWidget() {
		int id = 1;
		Widget w = new Widget(id, "Test widget", 4.52, 20, 10);
		int expected = 1;
		
		when(mockDao.insertWidget(w))
			.thenReturn(1);

		int count = service.addWidget(w);
		
		assertThat(count, equalTo(expected));		
	}
	
	@Test
	void testUpdateWidget() {
		int id = 1;
		int expected = 1;
		
		Widget originalWidget = new Widget(id, "Test widget", 4.52, 20, 10);
		
		when(mockDao.updateWidget(originalWidget))
			.thenReturn(1);
		
		int count = service.modifyWidget(originalWidget);
		
		// verify the count = 1
		assertThat(count, equalTo(expected));						
	}

	// **** Gadget Tests ****
	@Test
	void testFindAllGadgets() {
		// Initialize the list of Widgets that will be returned by the mock DAO
		List<Gadget> allGadgets = Arrays.asList(
				new Gadget(1, "Two Cylinder Gadget", 19.99, 2), 
				new Gadget(2, "Four Cylinder Gadget", 29.99, 4), 
				new Gadget(3, "Eight Cylinder Gadget", 49.99, 8) 
			);

		when(mockDao.getAllGadgets())
        	.thenReturn(allGadgets);
		
		List<Gadget> gadgets = service.findAllGadgets();

		// verify the collection of Gadgets
		assertThat(gadgets, equalTo(allGadgets));
	}

	@Test
	void testFindGadgetById() {
		int id = 1;
		Gadget gadget = new Gadget(1, "Two Cylinder Gadget", 19.99, 2);
		
		when(mockDao.getGadget(id))
			.thenReturn(gadget);
	
	Gadget g = service.findGadgetById(id);
	
	// verify the Gadget
	assertThat(g, equalTo(gadget));

	}

	@Test
	void testDeleteGadget() {
		int id = 1;
		int expected = 1;
		
		when(mockDao.deleteGadget(id))
			.thenReturn(1);
		
		int count = service.removeGadget(id);
		
		// verify count = 1
		assertThat(count, equalTo(expected));
	}

	@Test
	void testInsertGadget() {
		int id = 1;
		Gadget g = new Gadget(id, "Two Cylinder Gadget", 19.99, 2);
		int expected = 1;
		
		when(mockDao.insertGadget(g))
			.thenReturn(expected);

		int count = service.addGadget(g);
		
		// verify count = 1
		assertThat(count, equalTo(expected));		
	}

	@Test
	void testUpdateGadget() {
		int id = 1;
		int expected = 1;
		
		Gadget g = new Gadget(id, "Two Cylinder Gadget", 19.99, 2);
		
		when(mockDao.updateGadget(g))
			.thenReturn(expected);
		
		int count = service.modifyGadget(g);
		
		// verify count = 1
		assertThat(count, equalTo(expected));						
	}

	// **** Negative Widget Tests ****
	@Test
	void testFindAllWidgetsThrowsException() {
		when(mockDao.getAllWidgets())
			.thenThrow(WarehouseBusinessServiceException.class);

		// verify WarehouseBusinessServiceException is thrown
		assertThrows(WarehouseBusinessServiceException.class, 
					() -> service.findAllWidgets());
	}

	@Test
	void testFindWidgetByIdThrowsException() {
		int id = 42;
		when(mockDao.getWidget(id))
			.thenThrow(WarehouseBusinessServiceException.class);

		// verify WarehouseBusinessServiceException is thrown
		assertThrows(WarehouseBusinessServiceException.class, 
					() -> service.findWidgetById(id));
	}

	@Test
	void testDeleteWidgetThrowsException() {
		int id = 42;
		when(mockDao.deleteWidget(id))
			.thenThrow(WarehouseBusinessServiceException.class);

		// verify WarehouseBusinessServiceException is thrown
		assertThrows(WarehouseBusinessServiceException.class, 
					() -> service.removeWidget(id));
	}

	@Test
	void testInsertWidgetThrowsException() {
		Widget w = new Widget(42, "Test widget", 4.52, 20, 10);
		when(mockDao.insertWidget(w))
			.thenThrow(WarehouseBusinessServiceException.class);

		// verify WarehouseBusinessServiceException is thrown
		assertThrows(WarehouseBusinessServiceException.class, 
					() -> service.addWidget(w));
	}

	@Test
	void testUpdateWidgetThrowsException() {
		Widget w = new Widget(42, "Test widget", 4.52, 20, 10);
		when(mockDao.updateWidget(w))
			.thenThrow(WarehouseBusinessServiceException.class);

		// verify WarehouseBusinessServiceException is thrown
		assertThrows(WarehouseBusinessServiceException.class, 
					() -> service.modifyWidget(w));
	}


	// **** Negative Gadget Tests ****
	@Test
	void testFindAllGadgetsThrowsException() {
		when(mockDao.getAllGadgets())
			.thenThrow(WarehouseBusinessServiceException.class);

		// verify WarehouseBusinessServiceException is thrown
		assertThrows(WarehouseBusinessServiceException.class, 
					() -> service.findAllGadgets());
	}

	@Test
	void testFindGadgetByIdThrowsException() {
		int id = 42;
		when(mockDao.getGadget(id))
			.thenThrow(WarehouseBusinessServiceException.class);

		// verify WarehouseBusinessServiceException is thrown
		assertThrows(WarehouseBusinessServiceException.class, 
					() -> service.findGadgetById(id));
	}

	@Test
	void testDeleteGadgetThrowsException() {
		int id = 42;
		when(mockDao.deleteGadget(id))
			.thenThrow(WarehouseBusinessServiceException.class);

		// verify WarehouseBusinessServiceException is thrown
		assertThrows(WarehouseBusinessServiceException.class, 
					() -> service.removeGadget(id));
	}

	@Test
	void testInsertGadgetThrowsException() {
		Gadget g = new Gadget(42, "Test Gadget", 99.99, 2);
		when(mockDao.insertGadget(g))
			.thenThrow(WarehouseBusinessServiceException.class);

		// verify WarehouseBusinessServiceException is thrown
		assertThrows(WarehouseBusinessServiceException.class, 
					() -> service.addGadget(g));
	}

	@Test
	void testUpdateGadgetThrowsException() {
		Gadget g = new Gadget(42, "Test Gadget", 99.99, 2);
		when(mockDao.updateGadget(g))
			.thenThrow(WarehouseBusinessServiceException.class);

		// verify WarehouseBusinessServiceException is thrown
		assertThrows(WarehouseBusinessServiceException.class, 
					() -> service.modifyGadget(g));
	}
}
