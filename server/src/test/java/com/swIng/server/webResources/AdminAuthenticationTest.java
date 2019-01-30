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
import org.restlet.data.Method;
import org.restlet.data.Protocol;

import com.swIng.server.MyWebApp;

public class AdminAuthenticationTest {

	static Component component;

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
		Files.copy(flashMobs.toPath(), Paths.get("temp\\flashMobs.db"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(users.toPath(), Paths.get("temp\\users.json"), StandardCopyOption.REPLACE_EXISTING);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		component.stop();
		
		Files.delete(Paths.get("admins.json"));
		Files.delete(Paths.get("users.json"));
		Files.delete(Paths.get("flashMobs.db"));

		Files.copy(Paths.get("temp\\admins.json"), Paths.get("admins.json"), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(Paths.get("temp\\flashMobs.db"), Paths.get("flashMobs.db"), StandardCopyOption.REPLACE_EXISTING);
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
	public void testGet1() {
		String url = "http://localhost:8182/content/admin/authentication";
		Client client = new Client(Protocol.HTTP);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "a", "a");
		Request request = new Request(Method.GET, url);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);

		assertEquals(401, response.getStatus().getCode());
	}

	@Test
	public void testGet2() {
		String url = "http://localhost:8182/content/admin/authentication";
		Client client = new Client(Protocol.HTTP);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin", "admin");
		Request request = new Request(Method.GET, url);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);

		assertEquals(200, response.getStatus().getCode());
	}

}
