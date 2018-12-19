package com.swIng.server.webResources;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import com.swIng.server.MyWebApp;

public class UserAuthentication extends ServerResource{
	@Get
	public String chekUser() {
		if (!isInRole(MyWebApp.ROLE_USER)) {
            throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
        }
		
    	return "OK";
	}
}
