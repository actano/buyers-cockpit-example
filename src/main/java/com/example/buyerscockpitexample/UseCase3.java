package com.example.buyerscockpitexample;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "useCase3", value = "/use-case-3")
public class UseCase3 extends HttpServlet {

  public void init() {
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    res.setContentType("text/html");
    req.getRequestDispatcher("/WEB-INF/use-case-3.jsp").forward(req, res);
  }

  public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    res.setContentType("text/html");
    try {
      var name = req.getParameter("name");
      var supplierId = req.getParameter("supplierId");
      var client = AllexAPI.getHttpClient();

      var taskId = AllexAPI.createStandaloneTask(client, name, supplierId);

      PrintWriter out = res.getWriter();
      out.println("<html><body>");

      var planningObject = AllexAPI.fetchPlanningObject(client, taskId);
      out.println("<h3>Planning Object: " + planningObject.getJSONObject("base").getString("name") + "</h3>");
      out.println("Type: " + planningObject.getString("type"));
      out.println("<br />");

      out.println("</body></html>");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void destroy() {
  }
}
