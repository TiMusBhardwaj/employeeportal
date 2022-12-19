package com.workmotion.statemachine;

/**
 * 
 *
 * Runtime Exception when event is not accepted by stateMachine  
 * @author sumit.b
 */
public class UnAcceptedEventException extends RuntimeException {

	public UnAcceptedEventException() {
		super();
		
	}

	public UnAcceptedEventException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		                                             
	}

	public UnAcceptedEventException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public UnAcceptedEventException(String message) {
		super(message);
		
	}

	public UnAcceptedEventException(Throwable cause) {
		super(cause);
		
	}
	
	

}
