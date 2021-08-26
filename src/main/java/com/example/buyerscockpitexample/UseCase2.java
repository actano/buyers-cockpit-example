package com.example.buyerscockpitexample;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "useCase2", value = "/use-case-2")
public class UseCase2 extends HttpServlet {

  public void init() {
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    res.setContentType("text/html");
    req.getRequestDispatcher("/WEB-INF/use-case-2.jsp").forward(req, res);
  }

  public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    res.setContentType("text/html");
    try {
      var supplierId = req.getParameter("supplierId");
      var client = AllexAPI.getHttpClient();
      var ids = AllexAPI.fetchPlanningObjectIdsBySupplierId(client, supplierId);
      var planningObjects = AllexAPI.fetchPlanningObjects(
        client,
        Utils.<String>streamJsonArray(ids).collect(Collectors.toList())
      );

      PrintWriter out = res.getWriter();
      out.println("<html><body>");

      out.println("Planning Objects found: " + ids.length());

      for (Object o: ids) {
        var entityId = (String) o;
        var planningObject = planningObjects.getJSONObject(entityId);
        out.println("<h3>Planning Object: " + planningObject.getJSONObject("base").getString("name") + "</h3>");
        out.println("Type: " + planningObject.getString("type"));
        out.println("<br />");
      }

      out.println("</body></html>");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void destroy() {
  }
}
