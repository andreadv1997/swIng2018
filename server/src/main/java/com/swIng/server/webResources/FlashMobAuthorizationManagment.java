package com.swIng.server.webResources;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.TreeMap;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.swIng.server.MyWebApp;
import com.swIng.server.commons.ErrorCodes;
import com.swIng.server.commons.ExpiredFlashMobException;
import com.swIng.server.commons.FlashMob;
import com.swIng.server.commons.FutureFlashMobException;
import com.swIng.server.commons.MyUser;
import com.swIng.server.commons.UnregisteredUserException;

public class FlashMobAuthorizationManagment extends ServerResource {
	private TreeMap<String, FlashMob> flashMobList;
	private MyUser[] users;

	@Get
	public String getPostAuthorization() {
		if (!isInRole(MyWebApp.ROLE_USER))
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);

		String username = this.getClientInfo().getUser().getIdentifier();
		try {
			FileReader fr = new FileReader("users.json");
			users = new Gson().fromJson(fr, MyUser[].class);
			fr.close();
			ArrayList<String> fm_registered = users[getUserIndex(username)].getFlashMob();
			String fm_Name = getAttribute("flashMobTitle");
			if (getRegisteredFlashMob(fm_Name, fm_registered)) {
				FileInputStream fileIn = new FileInputStream("flashMobs.db");
				ObjectInputStream in;

				in = new ObjectInputStream(fileIn);

				flashMobList = (TreeMap<String, FlashMob>) in.readObject();

				in.close();
				fileIn.close();

				FlashMob flashMob = flashMobList.get(fm_Name);
				if (this.getRequest().getDate().after(flashMob.getEnd())) { // past flash mob
					Status status = new Status(ErrorCodes.EXPIRED_FLASHMOB);
					setStatus(status);

					ExpiredFlashMobException e = new ExpiredFlashMobException(
							"You can't post a picture for an expired flash mob");
					return new Gson().toJson(e, ExpiredFlashMobException.class);
				}
				if (this.getRequest().getDate().before(flashMob.getStart())) { // future flash mob
					Status status = new Status(ErrorCodes.FUTURE_FLASHMOB);
					setStatus(status);

					System.out.println("HO MANDATO UNA ECCEZIONE FUTURE FLASH MOB");

					FutureFlashMobException e = new FutureFlashMobException(
							"You can't post a picture for a future flash mob");
					return new Gson().toJson(e, FutureFlashMobException.class);
				}

				// present flash mob: check registration
				return "OK";

			} else {
				Status status = new Status(ErrorCodes.UNREGISTERED_USER);
				setStatus(status);

				UnregisteredUserException e = new UnregisteredUserException(
						"You are not registered for this flashmob. Register to post.");
				return new Gson().toJson(e, UnregisteredUserException.class);
			}

		} catch (JsonSyntaxException e1) {
			e1.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (JsonIOException e1) {
			e1.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		// return "OK";

	}

	private int getUserIndex(String username) {
		int res = -1;

		for (int i = 0; i < this.users.length; i++) {
			if (users[i].getIdentifier().equals(username))
				res = i;
		}

		return res;

	}

	private boolean getRegisteredFlashMob(String name, ArrayList<String> fm_reg) {
		if (fm_reg == null)
			return false;
		for (String fm : fm_reg) {
			if (fm.equals(name))
				return true;
		}

		return false;
	}

}
