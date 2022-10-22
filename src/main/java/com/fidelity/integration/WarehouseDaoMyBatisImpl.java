package com.fidelity.integration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fidelity.business.Gadget;
import com.fidelity.business.Widget;
import com.fidelity.integration.mapper.WarehouseMapper;

@Repository("warehouseDao")
public class WarehouseDaoMyBatisImpl implements WarehouseDao {
	@Autowired
	private WarehouseMapper mapper;
		
	@Override
	public List<Widget> getAllWidgets() {
		List<Widget> products = mapper.getAllWidgets();
		return products;
	}
	
	@Override
	public Widget getWidget(int id) {
		Widget widget = mapper.getWidget(id);
		return widget;
	}

	@Override
	public int deleteWidget(int id) {
		int count = mapper.deleteWidget(id);
		return count;
	}

	@Override
	public int insertWidget(Widget w) {
		int count = mapper.insertWidget(w);
		return count;
	}

	@Override
	public int updateWidget(Widget w) {
		int count = mapper.updateWidget(w);
		return count;
	}

	// Gadget methods
	@Override
	public List<Gadget> getAllGadgets() {
		List<Gadget> products = mapper.getAllGadgets();
		return products;
	}

	@Override
	public Gadget getGadget(int id) {
		Gadget gadget = mapper.getGadget(id);
		return gadget;
	}
	
	@Override
	public int deleteGadget(int id) {
		int count = mapper.deleteGadget(id);
		return count;
	}

	@Override
	public int insertGadget(Gadget g) {
		int count = mapper.insertGadget(g);
		return count;
	}

	@Override
	public int updateGadget(Gadget g) {
		int count = mapper.updateGadget(g);
		return count;
	}

}
