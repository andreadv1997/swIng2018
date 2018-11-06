package com.swIng.server.commons;

public class AlreadyExistingException extends Exception{
	private static final long serialVersionUID = -2161808073357292179L;

	public AlreadyExistingException(String msg){
		super(msg);
	}
}
