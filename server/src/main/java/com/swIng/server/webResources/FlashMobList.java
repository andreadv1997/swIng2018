package com.swIng.server.webResources;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.TreeMap;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.swIng.server.commons.FlashMob;

public class FlashMobList extends ServerResource {

	@Get
	public String getFlashMobList() {
		try {
			// useless, but smooth
			FileInputStream fileIn = new FileInputStream("flashMobs.db");
			ObjectInputStream in = new ObjectInputStream(fileIn);

			TreeMap<String, FlashMob> flashMobList = (TreeMap<String, FlashMob>) in.readObject();

			in.close();
			fileIn.close();

			FlashMob[] list = flashMobList.values().toArray(new FlashMob[flashMobList.size()]);
			return new Gson().toJson(list, FlashMob[].class);
		} catch (JsonSyntaxException e) {

			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (JsonIOException e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
