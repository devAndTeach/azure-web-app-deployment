package com.fidelity.business.service;

import java.util.List;

import com.fidelity.business.Gadget;
import com.fidelity.business.Widget;

/**
 * The WarehouseBusinessService interface declares
 * the methods implemented by the business service.
 * 
 * @author Instructor
 *
 */
public interface WarehouseBusinessService {
	// ***** Widget Methods *****
	List<Widget> findAllWidgets();
	Widget findWidgetById(int id);
	int removeWidget(int id);
	int addWidget(Widget w);
	int modifyWidget(Widget originalWidget);
	
	// ***** Gadget Methods *****
	List<Gadget> findAllGadgets();
	Gadget findGadgetById(int id);
	int removeGadget(int id);
	int addGadget(Gadget g);
	int modifyGadget(Gadget g);

}