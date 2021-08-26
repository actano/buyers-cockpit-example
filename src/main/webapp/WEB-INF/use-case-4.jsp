<%@ page import="org.json.JSONObject" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Example Access to Allex API</title>
</head>
<body>
    <form action="use-case-4?taskId=<%= request.getParameter("taskId") %><%= request.getAttribute("supplierFieldId") != null ? "&supplierFieldId="+request.getAttribute("supplierFieldId") : "" %>" method="post">
        <input
            type="text"
            id="name"
            name="name"
            placeholder="enter task name"
            value="<%= ((JSONObject) request.getAttribute("task")).getJSONObject("base").get("name") %>"
        />
        <input
            type="text"
            id="supplierId"
            name="supplierId"
            placeholder="enter supplier id"
            value="<%= request.getAttribute("supplierId") != null ? request.getAttribute("supplierId") : ""%>"
        />
        <input type="submit" value="update" />
    </form>
</body>
</html>
