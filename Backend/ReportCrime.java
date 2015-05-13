

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		ImportCrimeRequest importDataRequest = new ImportCrimeRequest();
		try {
			JSONObject crime = getRequestBody(request);
			crime.put("id", String.format("u%d", count));
			String status = importDataRequest.importCrime(crime);
			response.getOutputStream().print(status);
			if (status.equalsIgnoreCase("Success"))
				count++;
		} catch (Exception e) {
			response.getOutputStream().print("Error: " + e.getMessage());
		}
		importDataRequest.shutdown();
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
