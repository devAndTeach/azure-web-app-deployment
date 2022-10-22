package com.fidelity.restservices.dto;

/**
 * DatabaseRequestResultDTO is a Data Transfer Object (DTO) that wraps an integer
 * row count. Without this DTO, if a service method returns a row count as a plain int, 
 * the response body is not valid JSON because there is no element name:
 *    { 14 }
 * But if the service method returns an instance of this DTO instead, the response is
 * valid JSON: 
 *    { "rowCount": 14 }
 *    
 * @author ROI Instructor
 * 
 */
public class DatabaseRequestResultDTO {
	private int rowCount;
	
	public DatabaseRequestResultDTO () {}
	
	public DatabaseRequestResultDTO(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getRowCount() {
		return rowCount;
	}
	
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
}
