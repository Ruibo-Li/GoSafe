import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DBManager.UserRegistrationRequest;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

/**
 * Servlet implementation class Registration
 */
public class Registration extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Registration() {
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
		UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
		try {
			JSONObject user = ReportCrime.getRequestBody(request);
			String status = registrationRequest.register(user);
			response.getOutputStream().print(status);
		} catch (Exception e) {
			e.printStackTrace();
			response.getOutputStream().print("Error: " + e.getMessage());
		}
		registrationRequest.shutdown();
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
		try {
			JSONObject updateInfo = ReportCrime.getRequestBody(request);
			String status = registrationRequest.updateAddress(updateInfo.getString("id"), updateInfo.getString("address"));
			response.getOutputStream().print(status);
		} catch (JSONException e) {
			e.printStackTrace();
			response.getOutputStream().print("Error: " + e.getMessage());
		}
		registrationRequest.shutdown();
	}

}
