package com.swIng.server.webResources;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import com.swIng.server.MyWebApp;

public class AdminAuthentication extends ServerResource{
	
	@Get
	public String chekAdmin() {
		if (!isInRole(MyWebApp.ROLE_ADMIN)) {
			
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
        }
    	
    	return "OK";
	}

}
