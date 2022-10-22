package com.fidelity.business.service;

public class WarehouseBusinessServiceException extends RuntimeException {

	public WarehouseBusinessServiceException() {
		super();
	}

	public WarehouseBusinessServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WarehouseBusinessServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public WarehouseBusinessServiceException(String message) {
		super(message);
	}

	public WarehouseBusinessServiceException(Throwable cause) {
		super(cause);
	}

}
