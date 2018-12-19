package com.swIng.server.commons;

public class ExpiredFlashMobException extends Exception{
	private static final long serialVersionUID = -2161808073357292178L;
	
	
	public ExpiredFlashMobException(String msg) {
		super(msg);
	}
}
