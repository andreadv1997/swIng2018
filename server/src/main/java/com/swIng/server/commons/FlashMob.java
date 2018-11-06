package com.swIng.server.commons;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.restlet.security.User;

public class FlashMob implements Serializable{

	
public final static SimpleDateFormat DATEFORMAT= new SimpleDateFormat("dd/MM/yyyy HH:mm");
private String name;
private String description;
private Date start;
private Date end;
private ArrayList<String> users = new ArrayList<String>();

public FlashMob(String name, String description, Date start, Date end) {
	this.name=name;
	this.description=description;
	this.start=start;
	this.end=end;
}


public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getDescription() {
	return description;
}
public void setDescription(String description) {
	this.description = description;
}
public Date getStart() {
	return start;
}
public void setStart(Date start) {
	this.start = start;
}
public Date getEnd() {
	return end;
}
public void setEnd(Date end) {
	this.end = end;
}
public ArrayList<String> getUsers() {
	return users;
}

public boolean equals(FlashMob fm) {
	return this.name.equals(fm.getName()) && this.description.equals(fm.getDescription()) && this.start.equals(fm.getStart()) && this.end.equals(fm.getEnd());
}

public void addUser(String user) {
	for(String u : users) {
		if(u.equals(user)) return;
	}
	
	users.add(user);
}



	
	
}
