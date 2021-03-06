package edu.csupomona.cs480.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;

import org.apache.commons.io.LineIterator;
import org.apache.commons.io.FileUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import edu.csupomona.cs480.App;
import edu.csupomona.cs480.data.GpsProduct;
import edu.csupomona.cs480.data.User;
import edu.csupomona.cs480.data.provider.GpsProductManager;
import edu.csupomona.cs480.data.provider.UserManager;


/**
 * This is the controller used by Spring framework.
 * <p>
 * The basic function of this controller is to map
 * each HTTP API Path to the correspondent method.
 *
 */

@RestController
public class WebController {

	/**
	 * When the class instance is annotated with
	 * {@link Autowired}, it will be looking for the actual
	 * instance from the defined beans.
	 * <p>
	 * In our project, all the beans are defined in
	 * the {@link App} class.
	 */
	@Autowired
	private UserManager userManager;
	@Autowired
	private GpsProductManager gpsProductManager;

	/**
	 * This is a simple example of how the HTTP API works.
	 * It returns a String "OK" in the HTTP response.
	 * To try it, run the web application locally,
	 * in your web browser, type the link:
	 * 	http://localhost:8080/cs480/ping
	 */
	@RequestMapping(value = "/cs580/ping", method = RequestMethod.GET)
	String healthCheck() {
		// You can replace this with other string,
		// and run the application locally to check your changes
		// with the URL: http://localhost:8080/
		return "OK-CS480-Demo";
	}

	/**
	 * This is a simple example of how to use a data manager
	 * to retrieve the data and return it as an HTTP response.
	 * <p>
	 * Note, when it returns from the Spring, it will be
	 * automatically converted to JSON format.
	 * <p>
	 * Try it in your web browser:
	 * 	http://localhost:8080/cs480/user/user101
	 */
	@RequestMapping(value = "/cs480/user/{userId}", method = RequestMethod.GET)
	User getUser(@PathVariable("userId") String userId) {
		User user = userManager.getUser(userId);
		return user;
	}

	/**
	 * This is an example of sending an HTTP POST request to
	 * update a user's information (or create the user if not
	 * exists before).
	 *
	 * You can test this with a HTTP client by sending
	 *  http://localhost:8080/cs480/user/user101
	 *  	name=John major=CS
	 *
	 * Note, the URL will not work directly in browser, because
	 * it is not a GET request. You need to use a tool such as
	 * curl.
	 *
	 * @param id
	 * @param name
	 * @param major
	 * @return
	 */
	@RequestMapping(value = "/cs480/user/{userId}", method = RequestMethod.POST)
	User updateUser(
			@PathVariable("userId") String id,
			@RequestParam("name") String name,
			@RequestParam(value = "major", required = false) String major) {
		User user = new User();
		user.setId(id);
		user.setMajor(major);
		user.setName(name);
		userManager.updateUser(user);
		return user;
	}

	/**
	 * This API deletes the user. It uses HTTP DELETE method.
	 *
	 * @param userId
	 */
	@RequestMapping(value = "/cs480/user/{userId}", method = RequestMethod.DELETE)
	void deleteUser(
			@PathVariable("userId") String userId) {
		userManager.deleteUser(userId);
	}

	/**
	 * This API lists all the users in the current database.
	 *
	 * @return
	 */
	@RequestMapping(value = "/cs480/users/list", method = RequestMethod.GET)
	List<User> listAllUsers() {
		return userManager.listAllUsers();
	}
	
	@RequestMapping(value = "/cs480/gps/list", method = RequestMethod.GET)
	List<GpsProduct> listGpsProducts() {
		return gpsProductManager.listAllGpsProducts();
	}

	/*********** Web UI Test Utility **********/
	/**
	 * This method provide a simple web UI for you to test the different
	 * functionalities used in this web service.
	 */
	@RequestMapping(value = "/cs480/home", method = RequestMethod.GET)
	ModelAndView getUserHomepage() {
		ModelAndView modelAndView = new ModelAndView("home");
		modelAndView.addObject("users", listAllUsers());
		return modelAndView;
	}

	@RequestMapping(value = "/cs480/TeamNULL/KyleHubbard", method = RequestMethod.GET)
	String kyleHubbard() {

		return "New HTTP Get Request: Kyle Hubbard from Team NULL";
	}

	// Returns the Top 15 games being broadcast on Twitch as of the current time
	@RequestMapping(value = "/cs480/TeamNULL/KyleHubbard/Unirest", method = RequestMethod.GET)
	String kyleHubbardHttpClient() throws UnirestException {
		String clientID = "yxkhooad1bbvkjb13ibi0hcv4hhlcu";

		HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.twitch.tv/helix/games/top")
  			.header("Client-ID", clientID)
			.asJson();

		String result = "";
		for(int i = 0; i < 15; i++) {
			result += (i + 1) + ": " + jsonResponse.getBody().getObject().getJSONArray("data").getJSONObject(i).get("name").toString() + "\n";
		}

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println("Twitch Top 15 Games as of " + timestamp + "\n" + result);

		return "See Console for output.";
	}
	
	@RequestMapping(value = "/cs480/TeamNULL/CarlosHernandez", method = RequestMethod.GET)
	String showName() {

		return "Carlos Hernandez - CS4800 - 01";
	}
	@RequestMapping(value = "/cs480/TeamNULL/CarlosHernandez/CommonsMath", method = RequestMethod.GET) 
	RealVector matrixLU(RealMatrix A, RealVector B)	{
		DecompositionSolver solution = new LUDecomposition(A).getSolver();
		return solution.solve(B);
	}
	void stats(int[] array) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for(double i : array)
			stats.addValue(i);
		System.out.println(stats.getMean());
		System.out.println(stats.getPercentile(50));
		System.out.println(stats.getStandardDeviation());
	}

	@RequestMapping(value = "/cs480/TeamNULL/AronHubbard", method = RequestMethod.GET)
	String aronHubbard() {
		return "Aron Hubbard - Team NULL";
	}

	@RequestMapping(value = "/cs480/TeamNULL/AronHubbard/JSoup", method = RequestMethod.GET)
	String getImages() throws IOException {
			Document doc;
			//get all images
			doc = Jsoup.connect("https://www.reddit.com/r/PixelArt/").get();
			Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
			for (Element image : images) {
				System.out.println("\nsrc : " + image.attr("src"));
			}
		return "See Console for Image links and click any of the links that DON'T have 'renderTimingPixel.png' at the end to see an actual picture because I don't know how to filter the garbage out yet ¯\\_(ツ)_/¯";

	}
	@RequestMapping(value = "/cs480/TeamNULL/YujinKwon", method = RequestMethod.GET)
	String myStringYK() {
		return "Yujin Kwon ";
	}
	@RequestMapping(value = "/cs480/TeamNULL/YujinKwon/CommonsIO", method= RequestMethod.GET)
	void findText(File file, String search) throws IOException {
		 LineIterator it = FileUtils.lineIterator(file, "UTF-8");
		 try {
		   while (it.hasNext()) {
		     String line = it.nextLine();
		     if(line.contains("search")) {
		    	 System.out.println("Name found!");
		     }
		   }
		 } finally {
		   LineIterator.closeQuietly(it);
		 }
	}
}
