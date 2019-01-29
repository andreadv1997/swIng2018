package com.swIng.server.webResources;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.google.gson.Gson;
import com.swIng.server.MyWebApp;
import com.swIng.server.commons.FlashMob;

public class FlashMobResoucerForManagmentTest {

	static Component component;
	private static Gson gson = new Gson();
	static FlashMob flashMob;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		Date date = sdf.parse("15/01/2020");
		flashMob = new FlashMob("Test6", "Test6", new Date(), date);

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


		String url = "http://localhost:8182/content/admin/flashmob";
		Client client = new Client(Protocol.HTTP);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin", "admin");
		Request request = new Request(Method.POST, url);
		request.setEntity(gson.toJson(flashMob, FlashMob.class), MediaType.APPLICATION_ALL);
		request.setChallengeResponse(challengeResponse);
		client.handle(request);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		component.stop();

		File dir = new File(MyWebApp.FLASH_MOB_ROUT + "/" + flashMob.getName());
		dir.delete();
		
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
	public void testGet() {
		String url = "http://localhost:8182/content/user/flashmob/FlashMob1";
		Client client = new Client(Protocol.HTTP);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "a", "a");
		Request request = new Request(Method.GET, url);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);

		assertEquals(200, response.getStatus().getCode());
	}

	@Test
	public void testPost1() throws ResourceException, IOException {

		ClientResource cr;
		cr = new ClientResource("http://localhost:8182/content/user/flashmob/" + flashMob.getName());
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
		ChallengeResponse authentication = new ChallengeResponse(scheme, "a", "a");
		cr.setChallengeResponse(authentication);
		cr.post("a").getText();

		assertEquals(200, cr.getStatus().getCode());

	}

	@Test
	public void testPost2() throws ResourceException, IOException {

		ClientResource cr;
		cr = new ClientResource("http://localhost:8182/content/user/flashmob/FlashMob1");
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
		ChallengeResponse authentication = new ChallengeResponse(scheme, "a", "a");
		cr.setChallengeResponse(authentication);
		cr.post("a").getText();

		assertEquals(8001, cr.getStatus().getCode());

	}

	@Test
	public void testPost3() throws IOException {

		ClientResource cr;
		cr = new ClientResource("http://localhost:8182/content/user/flashmob/FlashMob2022");
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
		ChallengeResponse authentication = new ChallengeResponse(scheme, "a", "a");
		cr.setChallengeResponse(authentication);
		try {
			cr.post("a").getText();
		} catch (ResourceException e) {
			assertEquals(404, cr.getStatus().getCode());
		}
	}

	@Test
	public void testPost4() throws IOException {

		ClientResource cr;
		cr = new ClientResource("http://localhost:8182/content/user/flashmob/" + flashMob.getName());
		ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
		ChallengeResponse authentication = new ChallengeResponse(scheme, "admin", "admin");
		cr.setChallengeResponse(authentication);
		try {
			cr.post("a").getText();
		} catch (ResourceException e) {
			assertEquals(403, cr.getStatus().getCode());
		}
	}

}
