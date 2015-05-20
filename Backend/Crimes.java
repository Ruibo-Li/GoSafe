

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

import DBManager.QueryCrimeRequest;
import DBManager.UserRegistrationRequest;

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
		UserRegistrationRequest getZipRequest = new UserRegistrationRequest();
		try {
			JSONObject query = new JSONObject();
			query.put("zipcode", getZipRequest.getZipcode(id));
						
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			date.setTime(date.getTime() - 24L * 30L * 24L * 60L * 60L * 1000L);
			query.put("start_date", dateFormatter.format(date));
			
			QueryCrimeRequest queryCrimeRequest = new QueryCrimeRequest();
			
			response.setContentType("application/json");
			response.getOutputStream().print(queryCrimeRequest.getCrimes(query).toString());
		} catch (JSONException | ParseException e) {
			// TODO Auto-generated catch block
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
