package com.fidelity.restservices;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;

import com.fidelity.business.Gadget;
import com.fidelity.business.Product;
import com.fidelity.business.Widget;
import com.fidelity.business.service.WarehouseBusinessService;
import com.fidelity.restservices.dto.DatabaseRequestResultDTO;

/**
 * WarehouseController is a RESTful web service.
 * It provides web methods to manage Widgets and Gadgets 
 * in the Warehouse database.
 * 
 * @author ROI Instructor
 *
 */
@RestController
@RequestMapping("/warehouse")
public class WarehouseController {
	private static final String DB_ERROR_MSG = 
			"Error communicating with the warehouse database";
	
	@Autowired
	private WarehouseBusinessService service;

	@GetMapping(value="/ping",
				produces=MediaType.ALL_VALUE)
	public String ping() {
		return "Wareshouse web service is alive and awaits your command";
	}
	
	// **** Widget methods ****
	
	@GetMapping(value="/widgets",
				produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Widget>> queryForAllWidgets() {
		ResponseEntity<List<Widget>> result;
		List<Widget> products;
		try {
			products = service.findAllWidgets();
		} 
		catch (Exception e) {
			throw new ServerErrorException(DB_ERROR_MSG, e);
		}
		
		if (products.size() > 0) {
			result = ResponseEntity.ok(products);
		}
		else {
			result = ResponseEntity.noContent().build();
		}
		return result;
	}

	@GetMapping(value="/widgets/{id}",
				produces=MediaType.APPLICATION_JSON_VALUE)
	public Widget queryForWidgetById(@PathVariable int id) {
		Widget widget = null;
		try {
			widget = service.findWidgetById(id);
		} 
		catch (Exception e) {
			throw new ServerErrorException(DB_ERROR_MSG, e);
		}
		if (widget == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
					"No widget in the warehouse with id = " + id);
		}
		return widget;
	}

	@DeleteMapping(value="/widgets/{id}",
				   produces=MediaType.APPLICATION_JSON_VALUE)
	public DatabaseRequestResultDTO removeWidget(@PathVariable("id") int id) {
		int rows = 0;
		try {
			rows = service.removeWidget(id);
		} 
		catch (Exception e) {
			throw new ServerErrorException(DB_ERROR_MSG, e);
		}
		if (rows == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
					"No widget in the warehouse with id = " + id);
		}
		return new DatabaseRequestResultDTO(rows);
	}

	@PostMapping(value="/widgets",
				 produces=MediaType.APPLICATION_JSON_VALUE,
				 consumes=MediaType.APPLICATION_JSON_VALUE)
	public DatabaseRequestResultDTO insertWidget(@RequestBody Widget w) {
		int count = 0;
		try {
			count = service.addWidget(w);
		} 
		catch (Exception e) {
			throw new ServerErrorException(DB_ERROR_MSG, e);
		}
		if (count == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		return new DatabaseRequestResultDTO(count);
	}

	@PutMapping(value="/widgets",
					produces=MediaType.APPLICATION_JSON_VALUE,
					consumes=MediaType.APPLICATION_JSON_VALUE)
	public DatabaseRequestResultDTO updateWidget(@RequestBody Widget w) {
		int count = 0;
		try {
			count = service.modifyWidget(w);
		} 
		catch (Exception e) {
			throw new ServerErrorException(DB_ERROR_MSG, e);
		}
		return new DatabaseRequestResultDTO(count);
	}

	// Gadget methods
	
	@GetMapping(value="/gadgets",
				produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Gadget>> queryForAllGadgets() {
		ResponseEntity<List<Gadget>> result;
		List<Gadget> products;
		try {
			products = service.findAllGadgets();
		} 
		catch (Exception e) {
			throw new ServerErrorException(DB_ERROR_MSG, e);
		}
		if (products.size() > 0) {
			result = ResponseEntity.ok(products);
		}
		else {
			result = ResponseEntity.noContent().build();
		}
		return result;
	}

	@GetMapping(value="/gadgets/{id}",
				produces=MediaType.APPLICATION_JSON_VALUE)
	public Gadget queryForGadgetById(@PathVariable("id") int id) {
		Gadget gadget = null;
		try {
			gadget = service.findGadgetById(id);
		} 
		catch (Exception e) {
			throw new ServerErrorException(DB_ERROR_MSG, e);
		}
		if (gadget == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
					"No gadgets in the warehouse with id = " + id);
		}
		return gadget;
	}

	@DeleteMapping(value="/gadgets/{id}",
				   produces=MediaType.APPLICATION_JSON_VALUE)
	public DatabaseRequestResultDTO removeGadget(@PathVariable("id") int id) {
		int rows = 0;
		try {
			rows = service.removeGadget(id);
		} 
		catch (Exception e) {
			throw new ServerErrorException(DB_ERROR_MSG, e);
		}
		if (rows == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
					"No gadgets in the warehouse with id = " + id);
		}
		return new DatabaseRequestResultDTO(rows);
	}

	@PostMapping(value="/gadgets",
				 produces=MediaType.APPLICATION_JSON_VALUE,
				 consumes=MediaType.APPLICATION_JSON_VALUE)
	public DatabaseRequestResultDTO insertGadget(@RequestBody Gadget g) {
		int count = 0;
		try {
			count = service.addGadget(g);
		} 
		catch (Exception e) {
			throw new ServerErrorException(DB_ERROR_MSG, e);
		}
		if (count == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		return new DatabaseRequestResultDTO(count);
	}

	@PutMapping(value="/gadgets",
				produces=MediaType.APPLICATION_JSON_VALUE,
				consumes=MediaType.APPLICATION_JSON_VALUE)
	public DatabaseRequestResultDTO updateGadget(@RequestBody Gadget g) {
		int count = 0;
		try {
			count = service.modifyGadget(g);
		} 
		catch (Exception e) {
			throw new ServerErrorException(DB_ERROR_MSG, e);
		}
		if (count == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		return new DatabaseRequestResultDTO(count);
	}
	
	// Query for all products
	@GetMapping(value="/products",
				produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Product>> queryForAllProducts() {
		ResponseEntity<List<Product>> result;
		List<Product> products = new ArrayList<>();
		try {
			List<Widget> widgets = service.findAllWidgets();
			List<Gadget> gadgets = service.findAllGadgets();

			products.addAll(widgets);
			products.addAll(gadgets);
		} 
		catch (Exception e) {
			throw new ServerErrorException(DB_ERROR_MSG, e);
		}
		if (products.size() > 0) {
			result = ResponseEntity.ok(products);
		}
		else {
			result = ResponseEntity.noContent().build();
		}
		return result;
	}
}

