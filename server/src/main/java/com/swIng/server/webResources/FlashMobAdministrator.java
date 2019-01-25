package com.swIng.server.webResources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.TreeMap;

import org.restlet.data.Status;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.swIng.server.MyWebApp;
import com.swIng.server.commons.AlreadyExistingException;
import com.swIng.server.commons.ErrorCodes;
import com.swIng.server.commons.FlashMob;

public class FlashMobAdministrator extends ServerResource {
	private TreeMap<String, FlashMob> flashMobList;

	@Post
	public synchronized String registerFlashMob(String payload) {
		if (!isInRole(MyWebApp.ROLE_ADMIN))
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);

		FlashMob flashMob = new Gson().fromJson(payload, FlashMob.class);
		try {
			FileInputStream filein = new FileInputStream("flashMobs.db");
			ObjectInputStream in = new ObjectInputStream(filein);

			flashMobList = (TreeMap<String, FlashMob>) in.readObject();

			in.close();
			filein.close();
			if (flashMobList != null) {

				boolean check = getFlashMob(flashMob.getName());

				if (check) {
					Status status = new Status(ErrorCodes.USERNAME_ALREADY_EXISTING);
					setStatus(status);
					AlreadyExistingException e = new AlreadyExistingException("FlashMob Name already taken");
					return new Gson().toJson(e, AlreadyExistingException.class);
				} else {

					flashMobList.put(flashMob.getName(), flashMob);

				}
			} else {
				flashMobList = new TreeMap<String, FlashMob>();
				flashMobList.put(flashMob.getName(), flashMob);
			}

			FileOutputStream writer = new FileOutputStream("flashMobs.db");
			ObjectOutputStream printer = new ObjectOutputStream(writer);

			printer.writeObject(flashMobList);

			File new_flashmob = new File(MyWebApp.FLASH_MOB_ROUT + "/" + flashMob.getName());
			new_flashmob.mkdir();

			printer.close();
			writer.close();

			return "OK";

		} catch (JsonSyntaxException e) {

			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (JsonIOException e) {

			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (IOException e) {

			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

	}

	private boolean getFlashMob(String name) {

		for (String fm_name : this.flashMobList.keySet()) {
			if (fm_name.equals(name))
				return true;
		}

		return false;
	}

}
