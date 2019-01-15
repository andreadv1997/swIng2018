package com.swIng.server.webResources;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;

import com.google.gson.Gson;
import com.swIng.server.MyWebApp;
import com.swIng.server.commons.MyUser;

public class UserRegistrationTest {
	
	static Component component;
	private static Gson gson = new Gson();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		component = new Component();

		component.getServers().add(Protocol.HTTP, 8182);

		component.getClients().add(Protocol.FILE);

		component.getDefaultHost().attach(new MyWebApp());

		component.start();
		
		File dir = new File("temp");
		dir.mkdir();
		File admins = new File("admins.json");
		File flashMobs = new File("flashMobs.db");
		File users = new File("users.json");
		Files.copy(admins.toPath(), Paths.get("temp\\admins.json"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(flashMobs.toPath(), Paths.get("temp\\FlashMobs.db"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(users.toPath(), Paths.get("temp\\users.json"), StandardCopyOption.REPLACE_EXISTING);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		component.stop();
		
		Files.delete(Paths.get("admins.json"));
		Files.delete(Paths.get("users.json"));
		Files.delete(Paths.get("flashMobs.db"));

		Files.copy(Paths.get("temp\\admins.json"), Paths.get("admins.json"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(Paths.get("temp\\FlashMobs.db"), Paths.get("FlashMobs.db"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(Paths.get("temp\\users.json"), Paths.get("users.json"), StandardCopyOption.REPLACE_EXISTING);

		Files.delete(Paths.get("temp\\admins.json"));
		Files.delete(Paths.get("temp\\users.json"));
		Files.delete(Paths.get("temp\\flashMobs.db"));
		Files.delete(Paths.get("temp"));
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPut1() {
		String url = "http://localhost:8182/content/user/registration";
		Client client = new Client(Protocol.HTTP);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin", "admin");
		Request request = new Request(Method.PUT, url);
		MyUser user = new MyUser("test", "pass");
		request.setEntity(gson.toJson(user, MyUser.class), MediaType.APPLICATION_ALL);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);
		
		assertEquals("OK", gson.fromJson(response.getEntityAsText(), String.class));
	}
	
	@Test
	public void testPut2() {
		String url = "http://localhost:8182/content/user/registration";
		Client client = new Client(Protocol.HTTP);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin", "admin");
		Request request = new Request(Method.PUT, url);
		MyUser user = new MyUser("a", "a");
		request.setEntity(gson.toJson(user, MyUser.class), MediaType.APPLICATION_ALL);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);
		
		assertEquals(8000, response.getStatus().getCode());
	}

}
