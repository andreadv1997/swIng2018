package andreadelvecchio.pervasivestudent.gmail.it.flashmobclient;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.restlet.security.User;

public class MyUser extends User implements Serializable{
private ArrayList<String> reg_flashMob = new ArrayList<String>();



public MyUser(String username, String password) {
	super(username,password);
}


public ArrayList<String> getFlashMob(){return reg_flashMob;}

public void addFlashMob(String fm) {
	for(String flmB : reg_flashMob) {
		if(flmB.equals(fm)) return;
	}
	reg_flashMob.add(fm);
	
}


}
