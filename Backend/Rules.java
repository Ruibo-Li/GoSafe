import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DBManager.ImportRulesRequest;
import DBManager.QueryRulesRequest;
import DBManager.UserRegistrationRequest;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;

/**
 * Servlet implementation class Rules
 */
public class Rules extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Rules() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String id = request.getParameter("id");
		System.out.println(id);
		UserRegistrationRequest getZipRequest = new UserRegistrationRequest();
		try {
			String zipcode = getZipRequest.getZipcode(id);
			System.out.println(zipcode);
			QueryRulesRequest queryRulesRequest = new QueryRulesRequest();
			response.setContentType("application/json");
			response.getOutputStream().print(queryRulesRequest.getRules(zipcode).toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ImportRulesRequest importRulesRequest = new ImportRulesRequest();
		try {
			JSONArray rules = getRequestBody(request);
			String status = importRulesRequest.importRules(rules);
			response.getOutputStream().print(status);
		} catch (Exception e) {
			response.getOutputStream().print("Error: " + e.getMessage());
		}
		importRulesRequest.shutdown();
	}

	public static JSONArray getRequestBody(HttpServletRequest request) throws IOException, JSONException {
		InputStream inputStream = request.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null)
			builder.append(line);
		JSONArray requestBody = new JSONArray(builder.toString());
		reader.close();
		return requestBody;
	}
	
}
