import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/index.html")
public class Welcome extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		sendTextFile(response, "form.html", null);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String userName = request.getParameter("userName");
		String president = request.getParameter("president");

		Map<String,String> context = new HashMap<String,String>();
		context.put("$ipAddress$", request.getRemoteAddr());
		context.put("$myName$", request.getServerName());
		context.put("$name$", userName);
		context.put("$pres$", president);
		
		if (president == null) {
			context.put("$feeling$", "fooling around");
		} else if (president.length() == 0) {
			context.put("$feeling$", "stupid");
		} else if (president.length() < 4) {
			context.put("$feeling$", "expedient");
		} else if (president.length() > 10) {
			context.put("$feeling$", "enlightened");
		} else  {
			context.put("$feeling$", "common");
		}
		
		sendTextFile(response, "result.html", context);
	}
	
	private String insertContext(String html, Map<String,String> context) {
		StringBuilder b = new StringBuilder(html);
		for (String key : context.keySet()) {
			int startPos = b.indexOf(key);
			int endPos = startPos + key.length();
			String value = context.get(key);
			b.replace(startPos, endPos, value);
		}
		return b.toString();
	}
	

    private void sendTextFile(HttpServletResponse response, String fileName, Map<String,String> context)
            throws IOException {
	
	    	InputStream is = getServletContext().getResourceAsStream("/content/" + fileName);
	    if (is == null) {
	        response.sendError(HttpServletResponse.SC_NOT_FOUND);
	        return;
	    }
	

        String originalHtml = streamToString(is);

        	if (context == null) {
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.println(originalHtml);
            return;
        	}
        	
	    String updatedHtml = insertContext(originalHtml, context);
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.println(updatedHtml);
    }

    private String streamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        try {
            StringBuilder b = new StringBuilder();

            String line = br.readLine();
            while (line != null) {
                b.append(line);
                b.append('\n');
                line = br.readLine();
            }

            return b.toString();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
