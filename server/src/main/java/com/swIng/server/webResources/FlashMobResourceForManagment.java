package com.swIng.server.webResources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.security.MemoryRealm;
import org.restlet.security.User;

import com.swIng.server.commons.*;
import com.swIng.server.MyWebApp;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class FlashMobResourceForManagment extends ServerResource{

	private TreeMap<String, FlashMob> flashMobList;
	private MyUser[] users;
	@Get
	public String getList() {
		File fmDir = new File(System.getProperty("user.dir")+"/flashmobs/"+getAttribute("flashMobTitle"));
		return new Gson().toJson(fmDir.list(),String[].class);
	}
	
	
	
	@Post
	public String registerUser(String userName) {
		if(!isInRole(MyWebApp.ROLE_USER)) throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
		
		
		
	    try {
	    		users= new Gson().fromJson(new FileReader("users.json"), MyUser[].class);
			FileInputStream fileIn = new FileInputStream("flashMobs.db");
			ObjectInputStream in = new ObjectInputStream(fileIn);
	    		
	    		
	    		flashMobList = (TreeMap<String, FlashMob>) in.readObject();
	    		
	    		in.close();
	    		fileIn.close();
			if(flashMobList != null && users!=null)	{
				System.err.println(flashMobList.toString());
				System.err.println(users.toString());
				System.err.println(users.toString());
				
		    		MyUser user = users[getUserIndex(userName)];
		    			
		    		
				if(!getFlashMob(getAttribute("flashMobTitle"))) {
					
					throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
					
				}
				else {
					System.err.println("Se sono qui allora ho trovato il falshmob");
					if(this.getRequest().getDate().after(flashMobList.get(getAttribute("flashMobTitle")).getEnd())){
						Status status = new Status(ErrorCodes.EXPIRED_FLASHMOB);
						setStatus(status);
						
						ExpiredFlashMobException e = new ExpiredFlashMobException("You can'tregister for expired flash mobs");
						return new Gson().toJson(e,ExpiredFlashMobException.class);
					}
					else {
					
					flashMobList.get(getAttribute("flashMobTitle")).addUser(user.getIdentifier());;
					user.addFlashMob(getAttribute("flashMobTitle"));
					
					System.out.println("PRIMA DELLA SCRITTURA SU FILE");
					//users[getUserIndex(userName)]=user;
					FileOutputStream  writerFM= new FileOutputStream("flashMobs.db");
				    ObjectOutputStream printerFM = new ObjectOutputStream(writerFM);
				    
				    
				    printerFM.writeObject(flashMobList);
				    
				    printerFM.close();
				    writerFM.close();
				    
				    
				    
				    FileOutputStream  writerUS= new FileOutputStream("users.json");
				    PrintStream printerUS = new PrintStream(writerUS);
				    String us_json= new Gson().toJson(users,MyUser[].class);
				    
				    printerUS.print(us_json);
				    
				    
				    printerUS.close();
				    writerUS.close();
					}
				}
			}
			else {
				//non ci sono flashmob o non ci sono utenti
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			}
		
		    return "OK";
			
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} 
	    catch (JsonIOException e) {
			
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} 
	    catch (IOException e) {
			
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} 
	    catch (ClassNotFoundException  e) {
			
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} 
	    
	}
	
	
	
	
	
	
	
	private int getUserIndex(String username) {
		int res = -1;
		for(int i=0; i< this.users.length;i++) {
			if(users[i].getIdentifier().equals(username)) res=i;
		}
		
		return res;
		
		
	}
	
	
	
	private boolean getFlashMob(String name) {
		
		for(String fm: this.flashMobList.keySet()) {
			if(fm.equals(name)) return true;
		}
		
		return false;
	}
	
	
	
}
