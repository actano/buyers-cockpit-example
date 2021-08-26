package com.example.buyerscockpitexample;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@WebServlet(name = "useCase4", value = "/use-case-4")
public class UseCase4 extends HttpServlet {

  public void init() {
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    res.setContentType("text/html");
    try {
      var client = AllexAPI.getHttpClient();
      var taskId = req.getParameter("taskId");
      var planningObject = AllexAPI.fetchPlanningObject(client, taskId);

      String[] taskIds = {taskId};
      JSONObject customValuesById = AllexAPI.fetchCustomValuesOfPlanningObjects(client, Arrays.asList(taskIds));
      var customValues = customValuesById.optJSONArray(taskId);
      var supplierIdValue = Utils.findSupplierIdCustomValue(customValues);
      String supplierFieldId = null;
      String supplierId = null;
      if (supplierIdValue != null) {
        supplierFieldId = supplierIdValue.optString("id");
        supplierId = supplierIdValue.optString("textValue");
      }

      req.setAttribute("task", planningObject);
      req.setAttribute("supplierId", supplierId);
      req.setAttribute("supplierFieldId", supplierFieldId);
      req.getRequestDispatcher("/WEB-INF/use-case-4.jsp").forward(req, res);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    res.setContentType("text/html");
    try {
      var name = req.getParameter("name");
      var supplierId = req.getParameter("supplierId");
      var supplierIdFieldId = req.getParameter("supplierFieldId");
      var client = AllexAPI.getHttpClient();
      var taskId = req.getParameter("taskId");

      AllexAPI.updateStandaloneTaskName(client, taskId, name);
      if (supplierIdFieldId == null) {
        AllexAPI.addSupplierIdToPlanningObject(client, taskId, supplierId);
      } else {
        AllexAPI.updateSupplierIdOfPlanningObject(client, supplierIdFieldId, supplierId);
      }

      res.sendRedirect("use-case-1");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void destroy() {
  }
}
