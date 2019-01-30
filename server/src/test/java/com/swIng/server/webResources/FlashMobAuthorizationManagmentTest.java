package com.swIng.server.webResources;

import static org.junit.Assert.assertEquals;

import java.io.File;
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

import com.google.gson.Gson;
import com.swIng.server.MyWebApp;
import com.swIng.server.commons.FlashMob;

public class FlashMobAuthorizationManagmentTest {

	private static Gson gson = new Gson();
	static FlashMob flashMob;
	static FlashMob futureFlashMob;
	static Component component;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		Date date = sdf.parse("15/01/2020");
		flashMob = new FlashMob("Test2", "Test2", new Date(), date);

		Date startDate = sdf.parse("15/01/2020");
		Date endDate = sdf.parse("15/01/2021");
		futureFlashMob = new FlashMob("Test3", "Test3", startDate, endDate);
		
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
		
		String url = "http://localhost:8182/content/admin/flashmob";
		Client client = new Client(Protocol.HTTP);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin", "admin");
		Request request = new Request(Method.POST, url);
		request.setEntity(gson.toJson(flashMob, FlashMob.class), MediaType.APPLICATION_ALL);
		request.setChallengeResponse(challengeResponse);
		client.handle(request);
		
		url = "http://localhost:8182/content/admin/flashmob";
		client = new Client(Protocol.HTTP);
		request.setEntity(gson.toJson(futureFlashMob, FlashMob.class), MediaType.APPLICATION_ALL);
		request.setChallengeResponse(challengeResponse);
		client.handle(request);
		
		
		ClientResource cr;
        cr = new ClientResource("http://localhost:8182/content/user/flashmob/" + flashMob.getName());
        ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
        ChallengeResponse authentication = new ChallengeResponse(scheme, "a", "a");
        cr.setChallengeResponse(authentication);
        cr.post("a").getText();
        
        cr = new ClientResource("http://localhost:8182/content/user/flashmob/" + futureFlashMob.getName());
        scheme = ChallengeScheme.HTTP_BASIC;
        authentication = new ChallengeResponse(scheme, "a", "a");
        cr.setChallengeResponse(authentication);
        cr.post("a").getText();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		component.stop();
		
		File dir = new File(MyWebApp.FLASH_MOB_ROUT + "/" + flashMob.getName());
		dir.delete();
		
		dir = new File(MyWebApp.FLASH_MOB_ROUT + "/" + futureFlashMob.getName());
		dir.delete();
		
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
		String url = "http://localhost:8182/content/user/flashmob/FlashMob1/authorization";
		Client client = new Client(Protocol.HTTP);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "admin", "admin");
		Request request = new Request(Method.GET, url);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);

		assertEquals(403, response.getStatus().getCode());
	}

	@Test
	public void testGet2() {
		String url = "http://localhost:8182/content/user/flashmob/FlashMob1/authorization";
		Client client = new Client(Protocol.HTTP);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "andrea", "pass1");
		Request request = new Request(Method.GET, url);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);

		assertEquals(8001, response.getStatus().getCode());
	}

	@Test
	public void testGet3() {
		String url = "http://localhost:8182/content/user/flashmob/FlashMob1/authorization";
		Client client = new Client(Protocol.HTTP);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "a", "a");
		Request request = new Request(Method.GET, url);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);

		assertEquals(8003, response.getStatus().getCode());
	}

	@Test
	public void testGet4() {
		String url = "http://localhost:8182/content/user/flashmob/" + flashMob.getName() + "/authorization";
		Client client = new Client(Protocol.HTTP);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "a", "a");
		Request request = new Request(Method.GET, url);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);

		assertEquals(200, response.getStatus().getCode());
	}
	
	@Test
	public void testGet5() {
		String url = "http://localhost:8182/content/user/flashmob/" + futureFlashMob.getName() + "/authorization";
		Client client = new Client(Protocol.HTTP);
		ChallengeResponse challengeResponse = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, "a", "a");
		Request request = new Request(Method.GET, url);
		request.setChallengeResponse(challengeResponse);
		Response response = client.handle(request);

		assertEquals(8002, response.getStatus().getCode());
	}

}
