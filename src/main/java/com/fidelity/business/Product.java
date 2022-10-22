package com.fidelity.business;

/**
 * Product is the base class for all products stored in the warehouse.
 * 
 * @author ROI Instructor
 *
 */
public abstract class Product {
	private String description;
	private int id;
	private double price;

	// ***** Eclipse-generated from here *****
	public Product(String description, int id, double unitPrice) {
		this.description = description;
		this.id = id;
		this.price = unitPrice;
	}
	
	public Product() {}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public double getPrice() {
		return price;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(int id) {
		this.id = id;
	}


	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + id;
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [description=" + description + ", id=" + id + ", unitPrice=" + price + "]";
	}
}
