package com.fidelity.business;


/**
 * 
 * A Gadget is a Product with cylinders.
 * 
 * @author ROI Instructor
 *
 */

public class Gadget extends Product {
	private int cylinders;
	
	// ***** Eclipse-generated from here *****
	
	public Gadget( int id, String description, double unitPrice, int cylinders) {
		super(description, id, unitPrice);
		this.cylinders = cylinders;
	}

	public Gadget() {}


	public int getCylinders() {
		return cylinders;
	}

	public void setCylinders(int cylinders) {
		this.cylinders = cylinders;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + cylinders;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Gadget other = (Gadget) obj;
		if (cylinders != other.cylinders)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Gadget [cylinders=" + cylinders + ", superclass=" + super.toString() + "]";
	}

}
