import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DBManager.ImportCrimeRequest;
import DBManager.QueryUsersRequest;
import Publisher.PostToTwitterRequest;
import Publisher.SendEmailRequest;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

/**
 * Servlet implementation class ReportCrime
 */
public class ReportCrime extends HttpServlet {
	
	private int count = 1;
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportCrime() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ImportCrimeRequest importCrimeRequest = new ImportCrimeRequest();
		try {
			JSONObject crime = getRequestBody(request);
			crime.put("id", String.format("u%d", count));
			String status = importCrimeRequest.importCrime(crime);
			
			if (status.equalsIgnoreCase("Success")) {
				String message = String.format("%s %s: A/An %s happened at %s.", crime.getString("crime_date"),
						crime.getString("crime_time"), crime.getString("crime_type"), importCrimeRequest.getFormattedAddress());
				PostToTwitterRequest postToTwitterRequest = new PostToTwitterRequest(message);
				FutureTask<String> postToTwitterTask = new FutureTask<String>(postToTwitterRequest);
				ExecutorService executor = Executors.newFixedThreadPool(2);
				executor.execute(postToTwitterTask);
				QueryUsersRequest queryUsersRequest = new QueryUsersRequest();
				JSONArray users = queryUsersRequest.getUsers(crime.getString("zipcode"));
				for (int i = 0; i < users.length(); i++) {
					SendEmailRequest sendEmailRequest = new SendEmailRequest(users.getJSONObject(i).getString("email"), users.getJSONObject(i).getString("first_name"), "Crime Alert", message);
					FutureTask<String> sendEmailTask = new FutureTask<String>(sendEmailRequest);
					executor.execute(sendEmailTask);
				}
				queryUsersRequest.shutdown();
				response.getOutputStream().print(status);
				count++;
			}
		} catch (JSONException | ParseException e) {
			response.getOutputStream().print("Error: Please check your input and try again!");
		}
		importCrimeRequest.shutdown();
	}

	public static JSONObject getRequestBody(HttpServletRequest request) throws IOException, JSONException {
		InputStream inputStream = request.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null)
			builder.append(line);
		JSONObject requestBody = new JSONObject(builder.toString());
		reader.close();
		return requestBody;
	}
	
}
