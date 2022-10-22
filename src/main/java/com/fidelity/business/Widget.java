package com.fidelity.business;


/**
 * A Widget is a Product with gears and sprockets.
 * 
 * @author ROI Instructor
 *
 */

public class Widget extends Product {
	private int gears;
	private int sprockets;
	
	// ***** Eclipse-generated from here *****
	
	public Widget(int id, String description, double unitPrice, int gears, int sprockets) {
		super(description, id, unitPrice);
		this.gears = gears;
		this.sprockets = sprockets;
	}

	public Widget() {}

	public int getGears() {
		return gears;
	}

	public void setGears(int gears) {
		this.gears = gears;
	}

	public int getSprockets() {
		return sprockets;
	}

	public void setSprockets(int sprockets) {
		this.sprockets = sprockets;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + gears;
		result = prime * result + sprockets;
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
		Widget other = (Widget) obj;
		if (gears != other.gears)
			return false;
		if (sprockets != other.sprockets)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Widget [gears=" + gears + ", sprockets=" + sprockets + ", superclass=" + super.toString() + "]";
	}
}
