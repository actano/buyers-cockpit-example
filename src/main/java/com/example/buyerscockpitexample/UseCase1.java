package com.example.buyerscockpitexample;

import org.json.JSONObject;

import java.io.*;
import java.net.http.*;
import java.util.stream.Collectors;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "useCase1", value = "/use-case-1")
public class UseCase1 extends HttpServlet {

  public void init() {
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.setContentType("text/html");

    HttpClient client = AllexAPI.getHttpClient();

    try {
      var myPOs = AllexAPI.fetchMyPlanningObjects(client);

      var ids = Utils.<JSONObject>streamJsonArray(myPOs)
        .map((task) -> task.getString("entityId"))
        .collect(Collectors.toList());

      JSONObject customValuesById = AllexAPI.fetchCustomValuesOfPlanningObjects(client, ids);

      PrintWriter out = res.getWriter();
      out.println("<html><body>");

      for (Object o : myPOs) {
        var planningObject = (JSONObject) o;
        var entityId = planningObject.getString("entityId");
        var customValues = customValuesById.optJSONArray(entityId);
        var supplierId = Utils.findSupplierId(customValues);
        var calculatedTime = AllexAPI.fetchCalculatedTime(client, entityId);

        out.println("<h3>Planning Object: " + planningObject.getJSONObject("base").getString("name") + "</h3>");
        if (planningObject.getString("type").equals("standalone-task")) {
          out.println("<a href=\"use-case-4?taskId=" + entityId + "\">Edit</a>");
        }

        if (supplierId != null) {
          out.println("Supplier Id: " + supplierId);
          out.println("<br />");
        }
        out.println("Type: " + planningObject.getString("type"));
        out.println("<br />");
        if (calculatedTime != null) {
          out.println("Calculated Time Data: " + calculatedTime.toString());
          out.println("<br />");
        }
      }

      out.println("</body></html>");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void destroy() {
  }
}
