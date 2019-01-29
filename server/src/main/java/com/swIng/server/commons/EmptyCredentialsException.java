package com.swIng.server.commons;

public class EmptyCredentialsException extends Exception{
	private static final long serialVersionUID = -2161808073357292178L;
	
	
	public EmptyCredentialsException(String msg) {
		super(msg);
	}
}
