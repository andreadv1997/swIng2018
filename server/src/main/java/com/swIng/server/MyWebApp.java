package com.swIng.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import org.restlet.Request;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.resource.Directory;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MemoryRealm;
import org.restlet.security.Role;
import org.restlet.security.User;

import com.swIng.server.commons.FlashMob;
import com.swIng.server.commons.MyUser;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import com.swIng.server.webResources.*;








//add a comment to trigger build on travis
public class MyWebApp extends Application{
	public final static String ROLE_USER="user";
	public final static String ROLE_ADMIN="admin";
	public static final String FLASH_MOB_ROUT=System.getProperty("user.dir")+"/"+"flashmobs";
	private MemoryRealm myRealm;
	
	
	@Override
	public Restlet createInboundRoot() {
		 
		
		
		myRealm = new MemoryRealm();
		try {
			User[] admins = new Gson().fromJson(new FileReader("admins.json"), User[].class);
			if(admins!=null) {
				
				myRealm.getUsers().addAll(Arrays.asList(admins));
				for(User user:  admins) {
					
					myRealm.map(user, Role.get(this, ROLE_ADMIN));
					
				 
				}
			
			}
		}catch (JsonSyntaxException e){
				System.err.println(e.getMessage());		
		}
		catch(JsonIOException e) {
			System.err.println(e.getMessage());
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
		}
		finally {
			User default_admin= new User("admin", "admin");
			
			myRealm.getUsers().add(default_admin);
			myRealm.map(default_admin, Role.get(this, ROLE_ADMIN));
		}
		
		
		try {
			MyUser[] users = new Gson().fromJson(new FileReader("users.json"), MyUser[].class);
		    	
			if(users!=null) {
				
				for (MyUser new_user_id : users) {
					myRealm.getUsers().add(new_user_id);
					myRealm.map(new_user_id, Role.get(this, ROLE_USER));
				}
			      
			}	
		} 
		catch (JsonSyntaxException e){
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
		catch(JsonIOException e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
		catch(IOException e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
			
		
		ChallengeAuthenticator guardRegAdmin = createAuthenticator();
		guardRegAdmin.setNext(FlashMobAdministrator.class);
		
		
		ChallengeAuthenticator guardAutUser = createAuthenticator();
		guardAutUser.setNext(UserAuthentication.class);
		
		
		ChallengeAuthenticator guardAuthAdmin = createAuthenticator();
		guardAuthAdmin.setNext(AdminAuthentication.class);
		
		//ChallengeAuthenticator guardGenericUser = createAuthenticator();
		
		ChallengeAuthenticator guardGenericUser = new ChallengeAuthenticator(getContext(),ChallengeScheme.HTTP_BASIC,"MyRealm"){

			@Override
			protected int beforeHandle(Request request, Response response) {
				if (request.getMethod() == Method.GET) {
					return CONTINUE;
				} else {
					return super.beforeHandle(request, response);
				}
			}

		};
		
		guardGenericUser.setNext(FlashMobPictureStorageResource.class);
		guardGenericUser.setVerifier(myRealm.getVerifier());
		guardGenericUser.setEnroler(myRealm.getEnroler());
		
		ChallengeAuthenticator guardUserForFlashMob = new ChallengeAuthenticator(getContext(),ChallengeScheme.HTTP_BASIC,"MyRealm"){

			@Override
			protected int beforeHandle(Request request, Response response) {
				if (request.getMethod() == Method.GET) {
					return CONTINUE;
				} else {
					return super.beforeHandle(request, response);
				}
			}

		};
		
		guardUserForFlashMob.setNext(FlashMobResourceForManagment.class);
		guardUserForFlashMob.setVerifier(myRealm.getVerifier());
		guardUserForFlashMob.setEnroler(myRealm.getEnroler());
		
		
		ChallengeAuthenticator guardUserAuthForFlashMob = new ChallengeAuthenticator(getContext(),ChallengeScheme.HTTP_BASIC,"MyRealm");
		
		guardUserAuthForFlashMob.setNext(FlashMobAuthorizationManagment.class);
		guardUserAuthForFlashMob.setVerifier(myRealm.getVerifier());
		guardUserAuthForFlashMob.setEnroler(myRealm.getEnroler());
		
		
		
		
		Router router = new Router(getContext());
		router.attach("/content/admin/authentication", guardAuthAdmin);
        router.attach("/content/admin/flashmob", guardRegAdmin);
        router.attach("/content/user/authentication", guardAutUser);
        router.attach("/content/user/registration", UserRegistration.class);
        router.attach("/content/user/flashmob/list", FlashMobList.class);
        router.attach("/content/user/flashmob/{flashMobTitle}", guardUserForFlashMob);
        router.attach("/content/user/flashmob/{flashMobTitle}/authorization", guardUserAuthForFlashMob);
        router.attach("/content/user/flashmob/{flashMobTitle}/{fname}", guardGenericUser); 
 
        return router;
		
		
		
		
	}
	
                
       

 
	
	private ChallengeAuthenticator createAuthenticator() {
        ChallengeAuthenticator guard = new ChallengeAuthenticator(
                getContext(), ChallengeScheme.HTTP_BASIC, "MyRealm") ;
			
			guard.setVerifier(myRealm.getVerifier());
			guard.setEnroler(myRealm.getEnroler());
			
			
			System.err.println("Users: "+myRealm.getUsers());
//			System.err.println("Enroler :" +myRealm.getEnroler());
//			System.err.println("Verifier :"+ myRealm.getVerifier());
			return guard;
		
			
	}
	
	public MemoryRealm getRealm() {return myRealm;}
	
	
public static void main(String[] args) {
		File flashDir = new File(FLASH_MOB_ROUT);
		if(!flashDir.exists()) flashDir.mkdir();
		
		
		File flash_file = new File("flashMobs.db");
		
		if(flash_file.length() == 0) {
		try {
			FileOutputStream fileOut = new FileOutputStream("flashMobs.db");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			
			
			out.writeObject(new TreeMap<String,FlashMob>());
			
			out.close();
			fileOut.close();
			
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		
		
		
		try {
	        
	        Component component = new Component();
	        
	        component.getServers().add(Protocol.HTTP, 8182);
	        
	        component.getClients().add(Protocol.FILE);
	        
	        component.getDefaultHost().attach(new MyWebApp());
	        
	        component.start();
	    } catch (Exception e) {	  // Something is wrong.
	        e.printStackTrace();
	    }
	}
}
