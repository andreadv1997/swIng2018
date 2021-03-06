package com.swIng.server.webResources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.TreeMap;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
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
import com.swIng.server.commons.PayloadNullException;
import com.swIng.server.commons.UnregisteredUserException;

public class FlashMobPictureStorageResource extends ServerResource {
	MyUser[] users;

	@Get
	public Representation getPicture() {

		FileRepresentation file = new FileRepresentation(new File(System.getProperty("user.dir") + "/flashmobs/"
				+ getAttribute("flashMobTitle") + "/" + getAttribute("fname")), MediaType.IMAGE_ALL);

		return file;

	}

	@Put
	public String upload(Representation entity) throws ResourceException {
		if (!isInRole(MyWebApp.ROLE_USER))
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);

		if (entity == null) {
			Status status = new Status(ErrorCodes.NULL_PAYLOAD);
			setStatus(status);
			PayloadNullException e = new PayloadNullException("Null Payload");
			return new Gson().toJson(e, PayloadNullException.class);
		}

		String username = this.getClientInfo().getUser().getIdentifier();
		try {
			FileReader fr = new FileReader("users.json");
			users = new Gson().fromJson(fr, MyUser[].class);
			fr.close();
		} catch (JsonSyntaxException e1) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (JsonIOException e1) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (FileNotFoundException e1) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (IOException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
		MyUser user = getUser(username);
		ArrayList<String> fm_registered = user.getFlashMob();

		if (!getRegisteredFlashMob(getAttribute("flashMobTitle"), fm_registered)) { // fm non registrato
			Status status = new Status(ErrorCodes.UNREGISTERED_USER);
			setStatus(status);

			UnregisteredUserException e = new UnregisteredUserException(
					"You are not registered for this flashmob. Register to post.");
			return new Gson().toJson(e, UnregisteredUserException.class);
		}

		// fm registrato per l'utente. Quindi esiste e non devo fare il controllo
		// sull'esistenza della lista
		try {
			FileInputStream fileIN = new FileInputStream("flashMobs.db");
			ObjectInputStream in = new ObjectInputStream(fileIN);

			TreeMap<String, FlashMob> flashMobList = (TreeMap<String, FlashMob>) in.readObject();

			in.close();
			fileIN.close();

			FlashMob fm = flashMobList.get(getAttribute("flashMobTitle"));
			if (this.getRequest().getDate().before(fm.getStart())) {
				// future flashMob
				Status status = new Status(ErrorCodes.FUTURE_FLASHMOB);
				setStatus(status);

				FutureFlashMobException e = new FutureFlashMobException(
						"You can't post a picture for a Future flash mob");
				return new Gson().toJson(e, ExpiredFlashMobException.class);
			}

			if (this.getRequest().getDate().after(fm.getEnd())) {
				// past flash mob

				Status status = new Status(ErrorCodes.EXPIRED_FLASHMOB);
				setStatus(status);

				ExpiredFlashMobException e = new ExpiredFlashMobException(
						"You can't post a picture for a future flash mob");
				return new Gson().toJson(e, FutureFlashMobException.class);
			}
			// present flash Mob: posso postare le foto;
			// utente registrato per l'evento presente
			System.out.println(entity);
			FileOutputStream fr = new FileOutputStream(new File(System.getProperty("user.dir") + "/flashmobs/"
					+ getAttribute("flashMobTitle") + "/" + getAttribute("fname")));
			entity.write(fr);
			fr.close();
		} catch (NullPointerException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
		return "OK";
	}

	private MyUser getUser(String username) {
		for (int i = 0; i < users.length; i++) {
			if (users[i].getIdentifier().equals(username))
				return users[i];
		}

		return null;
	}

	private boolean getRegisteredFlashMob(String name, ArrayList<String> fm_reg) {
		FlashMob check = null;
		for (String fm : fm_reg) {
			if (fm.equals(name))
				return true;
		}

		return false;
	}
}
