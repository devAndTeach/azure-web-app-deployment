package com.fidelity.restservices;

import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fidelity.business.Gadget;
import com.fidelity.business.Widget;
import com.fidelity.business.service.WarehouseBusinessService;

/**
 * The WarehouseController has a dependency on the WarehouseBusinessService. 
 * The WarehouseBusinessService will be Autowired by Spring 
 * into the WarehouseController.
 * 
 * We will test this controller usiing @WebMvcTest.
 * We use @MockBean to create and inject a mock for the WarehouseBusinessService.
 * If you don’t do this the application context cannot start.
 * We set the expectations using MockMvc.
 *
 * Note that Spring Boot needs to find an application class in order to scan
 * for components. The trivial class com.fidelity.TestApplication in src/test/java 
 * contains the @SpringBootApplication annotation, which triggers the component scan.
 * 
 * @author ROI Instructor
 */

@WebMvcTest(controllers=WarehouseController.class)
public class WarehouseServiceWebLayerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	WarehouseBusinessService service;
	
	static List<Widget> widgets;
	static List<Gadget> gadgets;
	
	@BeforeAll
	public static void init() {
		widgets = Arrays.asList(
			new Widget(1, "Test Widget 1", 1.99, 2, 10),
			new Widget(2, "Test Widget 2", 2.99, 4, 20)
		);
		
		gadgets = Arrays.asList(
				new Gadget(1, "Two Cylinder Gadget", 19.99, 2), 
				new Gadget(2, "Four Cylinder Gadget", 29.99, 4), 
				new Gadget(3, "Eight Cylinder Gadget", 49.99, 8) 
			);
	}

	// **** Widget Tests ****
	
	/**
	 * This test verifies the WarehouseController can query successfully for all the
	 * Widgets in the Warehouse.
	 */
	@Test
	public void testQueryForAllWidgets() throws Exception {
		when(service.findAllWidgets()).thenReturn(widgets);
		
		mockMvc.perform(get("/warehouse/widgets"))
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.length()").value(2))
			   .andExpect(jsonPath("$[0].description").value("Test Widget 1"))
			   .andExpect(jsonPath("$[1].description").value("Test Widget 2"));
	}

	/**
	 * This test verifies the WarehouseController can query successfully for the
	 * Widget in the warehouse with the specified id.
	 */
	@Test
	public void testQueryForWidgetById() throws Exception {
		int id = 1;
		Widget firstWidget = new Widget(id, "Low Impact Widget", 12.99, 2, 3);
		
		when(service.findWidgetById(id)).thenReturn(firstWidget);
		
		mockMvc.perform(get("/warehouse/widgets/1"))
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.description").value("Low Impact Widget"));		
	}
	
	/**
	 * This test verifies that the WarehouseController returns an HTTP No_Content
	 * status when the widget list is empty.
	 * 
	 */
	@Test
	public void testQueryForAllWidgets_DaoReturnsEmptyList() throws Exception {
		when(service.findAllWidgets()).thenReturn(new ArrayList<Widget>());
		
		mockMvc.perform(get("/warehouse/widgets"))
			   .andDo(print())
			   .andExpect(status().isNoContent())
			   .andExpect(content().string(is(emptyOrNullString())));
	}

	/**
	 * This test verifies that the WarehouseController returns a 500 level 
	 * server HTTP status when and exception is thrown
	 */
	@Test
	public void testQueryForAllWidgets_DaoThrowsException() throws Exception {
		when(service.findAllWidgets()).thenThrow(new RuntimeException());
		
		mockMvc.perform(get("/warehouse/widgets"))
			   .andDo(print())
			   .andExpect(status().is5xxServerError())
			   .andExpect(content().string(is(emptyOrNullString())));
	}

	/**
	 * This test verifies the WarehouseController can successfully add a Widget 
	 * to the Warehouse.
	 */
	@Test
	public void testAddWidgetToWarehouse() throws Exception {
		Widget w = new Widget(42, "Test widget", 4.52, 20, 10);
		
		when(service.addWidget(w)).thenReturn(1);
		
		// Creating the ObjectMapper object
		ObjectMapper mapper = new ObjectMapper();
		// Converting the Object to JSONString
		String jsonString = mapper.writeValueAsString(w);
		
		mockMvc.perform(post("/warehouse/widgets")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(jsonString))
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.rowCount").value(1));
	}

	/**
	 * This test verifies the WarehouseController can successfully remove a Widget from
	 * the Warehouse.
	 */
	@Test
	public void testRemoveWidgetFromWarehouse() throws Exception{
		int id = 1;
		when(service.removeWidget(id)).thenReturn(1);
		
		mockMvc.perform(delete("/warehouse/widgets/" + id))
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.rowCount").value(1));
	}

	/**
	 * This test verifies the WarehouseController can successfully update a Widget in
	 * the Warehouse.
	 */
	@Test
	public void testUpdateWidgetInWarehouse() throws Exception{
		Widget w = new Widget(42, "Test widget", 4.52, 20, 10);
		
		when(service.modifyWidget(w)).thenReturn(1);
		
		// Creating the ObjectMapper object
		ObjectMapper mapper = new ObjectMapper();
		
		// Converting the Object to JSONString
		String jsonString = mapper.writeValueAsString(w);
		
		mockMvc.perform(put("/warehouse/widgets")
							.contentType(MediaType.APPLICATION_JSON)
							.content(jsonString))
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.rowCount").value(1));
	}


	// **** Gadget Tests ****
	
	/**
	 * This test verifies the WarehouseController can query successfully for all the
	 * Gadgets in the Warehouse.
	 */
	@Test
	public void testQueryForAllGadgets() throws Exception {
		when(service.findAllGadgets()).thenReturn(gadgets);
		
		mockMvc.perform(get("/warehouse/gadgets"))
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.length()").value(3))
			   .andExpect(jsonPath("$[0].description").value("Two Cylinder Gadget"))
			   .andExpect(jsonPath("$[1].description").value("Four Cylinder Gadget"))
			   .andExpect(jsonPath("$[2].description").value("Eight Cylinder Gadget"));
	}

	/**
	 * This test verifies the WarehouseController can query successfully for the
	 * Gadget in the warehouse with the specified id.
	 */
	@Test
	public void testQueryForGadgetById() throws Exception {
		int id = 1;
		Gadget firstGadget = new Gadget(1, "Two Cylinder Gadget", 19.99, 2);
		
		when(service.findGadgetById(id)).thenReturn(firstGadget);
		
		mockMvc.perform(get("/warehouse/gadgets/1"))
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.description").value("Two Cylinder Gadget"));		
	}
	
	/**
	 * This test verifies that the WarehouseController returns an HTTP No_Content
	 * status when the gadget list is empty.
	 * 
	 */
	@Test
	public void testQueryForAllGadgets_DaoReturnsEmptyList() throws Exception {
		when(service.findAllGadgets()).thenReturn(new ArrayList<Gadget>());
		
		mockMvc.perform(get("/warehouse/gadgets"))
			   .andDo(print())
			   .andExpect(status().isNoContent())
			   .andExpect(content().string(is(emptyOrNullString())));
	}

	/**
	 * This test verifies that the WarehouseController returns a 500 level 
	 * server HTTP status when and exception is thrown
	 */
	@Test
	public void testQueryForAllGadgets_DaoThrowsException() throws Exception {
		when(service.findAllGadgets()).thenThrow(new RuntimeException());
		
		mockMvc.perform(get("/warehouse/gadgets"))
			   .andDo(print())
			   .andExpect(status().is5xxServerError())
			   .andExpect(content().string(is(emptyOrNullString())));
	}

	/**
	 * This test verifies the WarehouseController can successfully add a Gadget 
	 * to the Warehouse.
	 */
	@Test
	public void testAddGadgetToWarehouse() throws Exception {
		Gadget w = new Gadget(42, "Two Cylinder Gadget", 19.99, 2);
		
		when(service.addGadget(w)).thenReturn(1);
		
		// Creating the ObjectMapper object
		ObjectMapper mapper = new ObjectMapper();
		// Converting the Object to JSONString
		String jsonString = mapper.writeValueAsString(w);
		
		mockMvc.perform(post("/warehouse/gadgets")
			   .contentType(MediaType.APPLICATION_JSON)
			   .content(jsonString))
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.rowCount").value(1));
	}

	/**
	 * This test verifies the WarehouseController can successfully remove a Gadget from
	 * the Warehouse.
	 */
	@Test
	public void testRemoveGadgetFromWarehouse() throws Exception{
		int id = 1;
		when(service.removeGadget(id)).thenReturn(1);
		
		mockMvc.perform(delete("/warehouse/gadgets/" + id))
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.rowCount").value(1));
	}

	/**
	 * This test verifies the WarehouseController can successfully update a Gadget in
	 * the Warehouse.
	 */
	@Test
	public void testUpdateGadgetInWarehouse() throws Exception{
		Gadget w = new Gadget(1, "Two Cylinder Gadget", 19.99, 2);
		
		when(service.modifyGadget(w)).thenReturn(1);
		
		// Creating the ObjectMapper object
		ObjectMapper mapper = new ObjectMapper();
		
		// Converting the Object to JSONString
		String jsonString = mapper.writeValueAsString(w);
		
		mockMvc.perform(put("/warehouse/gadgets")
							.contentType(MediaType.APPLICATION_JSON)
							.content(jsonString))
			   .andDo(print())
			   .andExpect(status().isOk())
			   .andExpect(jsonPath("$.rowCount").value(1));
	}
}
