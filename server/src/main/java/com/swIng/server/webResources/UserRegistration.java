package com.swIng.server.webResources;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import org.restlet.data.Status;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.security.MemoryRealm;
import org.restlet.security.Role;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.swIng.server.MyWebApp;
import com.swIng.server.commons.AlreadyExistingException;
import com.swIng.server.commons.ErrorCodes;
import com.swIng.server.commons.MyUser;

public class UserRegistration extends ServerResource {
	private MyUser[] users;

	@Put
	public String registerUser(String payload) {

		MyUser user_registation = new Gson().fromJson(payload, MyUser.class);
		MemoryRealm realm = ((MyWebApp) getApplication()).getRealm();

		System.err.println("L'utente si chiama :" + user_registation);

		try {
			FileReader fr =new FileReader("users.json");
			users = new Gson().fromJson(fr, MyUser[].class);
			fr.close();

			if (users != null) {
				if (!getMyUser(user_registation.getIdentifier())) {
					realm.getUsers().add(user_registation);
					realm.map(user_registation, Role.get(this.getApplication(), MyWebApp.ROLE_USER));

					users = Arrays.copyOf(users, users.length + 1);
					users[users.length - 1] = user_registation;
				} else {
					Status status = new Status(ErrorCodes.USERNAME_ALREADY_EXISTING);
					setStatus(status);
					AlreadyExistingException e = new AlreadyExistingException("User Name already taken");
					return new Gson().toJson(e, AlreadyExistingException.class);
				}

			} else {
				System.err.println(user_registation.toString() + " non registrato.Procedo con la registrazione.");
				users = new MyUser[1];
				users[0] = user_registation;
				realm.getUsers().add(user_registation);
				realm.map(user_registation, Role.get(this.getApplication(), MyWebApp.ROLE_USER));
			}

			System.err.println(users.toString());
			FileOutputStream fileOut = new FileOutputStream("users.json");
			PrintStream out = new PrintStream(fileOut);
			out.print(new Gson().toJson(users, MyUser[].class));

			out.close();
			fileOut.close();

			return "OK";

		} catch (JsonSyntaxException e) {
			System.err.println(e.getMessage());
			System.err.println(e.getClass());
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (JsonIOException e) {
			System.err.println(e.getMessage());
			System.err.println(e.getClass());
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.err.println(e.getClass());
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	private boolean getMyUser(String identifier) {

		for (int i = 0; i < users.length; i++) {
			if (users[i].getIdentifier().equals(identifier))
				return true;
		}

		return false;
	}
}