package com.swIng.server.commons;

public class UnregisteredUserException extends Exception{
	private static final long serialVersionUID = -2161808073357292176L;
	public UnregisteredUserException(String msg) {
		super(msg);
	}

}
