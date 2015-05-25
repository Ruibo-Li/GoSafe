import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

import DBManager.QueryCrimeRequest;
import DBManager.QueryUsersRequest;

/**
 * Servlet implementation class Crimes
 */
public class Crimes extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Crimes() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		response.setContentType("application/json");
		try {
			JSONObject query = new JSONObject();
			QueryUsersRequest getZipCodeRequest = new QueryUsersRequest();
			String zipcode = getZipCodeRequest.getZipCode(id);
			
			if (zipcode == null) {
				response.getOutputStream().print("{\"message\": \"Submit your address and see recent crimes happened in your area.\"}");
			} else {
				query.put("zipcode", zipcode);

				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
				Date date = new Date();
				date.setTime(date.getTime() - 24L * 30L * 24L * 60L * 60L * 1000L);
				query.put("start_date", dateFormatter.format(date));
				
				QueryCrimeRequest queryCrimeRequest = new QueryCrimeRequest();
				JSONArray crimes = queryCrimeRequest.getCrimes(query);
				
				if (crimes.length() == 0)
					response.getOutputStream().print("{\"message\": \"Congratulations! You are living in a safe neighborhood.\"}");
				else
					response.getOutputStream().print(crimes.toString());
				
				queryCrimeRequest.shutdown();
			}
			getZipCodeRequest.shutdown();

		} catch (JSONException | ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
