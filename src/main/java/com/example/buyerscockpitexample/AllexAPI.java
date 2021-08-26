package com.example.buyerscockpitexample;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AllexAPI {
  public static HttpClient getHttpClient() {
    return HttpClient
      .newBuilder()
      .version(HttpClient.Version.HTTP_2)
      .build();
  }

  public static JSONObject fetchPlanningObject(HttpClient client, String entityId) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest
      .newBuilder(URI.create(
        Config.BASE_URL
          + "/planning-objects/v1/"
          + entityId
      ))
      .header("Accept", "application/json, text/plain, */*")
      .header("Content-Type", "application/json;charset=UTF-8")
      .header("Authorization", String.format("Bearer %s", Config.API_KEY))
      .header("x-as-email", Config.USER_EMAIL)
      .GET()
      .build();

    var response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    return new JSONObject(response.body());
  }

  public static JSONObject fetchPlanningObjects(HttpClient client, List<String> entityIds) throws IOException, InterruptedException {
    JSONObject requestBody = new JSONObject();
    requestBody.put("entityIds", new JSONArray(entityIds));

    HttpRequest request = HttpRequest
      .newBuilder(URI.create(
        Config.BASE_URL + "/planning-objects/v1/queries/entities"
      ))
      .header("Accept", "application/json, text/plain, */*")
      .header("Content-Type", "application/json;charset=UTF-8")
      .header("Authorization", String.format("Bearer %s", Config.API_KEY))
      .header("x-as-email", Config.USER_EMAIL)
      .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
      .build();

    var response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    return new JSONObject(response.body()).getJSONObject("entities");
  }

  public static JSONObject fetchCalculatedTime(HttpClient client, String entityId) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest
      .newBuilder(URI.create(
        Config.BASE_URL + "/planning-objects-dates/v1/entities/" + entityId + "/calculated-dates"
      ))
      .header("Accept", "application/json, text/plain, */*")
      .header("Content-Type", "application/json;charset=UTF-8")
      .header("Authorization", String.format("Bearer %s", Config.API_KEY))
      .header("x-as-email", Config.USER_EMAIL)
      .GET()
      .build();

    var response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    var node = new JSONObject(response.body()).optJSONObject("node");

    if (node != null) {
      return node.optJSONObject("time");
    }

    return null;
  }

  public static JSONArray fetchMyPlanningObjects(HttpClient client) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest
      .newBuilder(URI.create(Config.BASE_URL + "/planning-objects/v1/my-tasks"))
      .header("Accept", "application/json, text/plain, */*")
      .header("Content-Type", "application/json;charset=UTF-8")
      .header("Authorization", String.format("Bearer %s", Config.API_KEY))
      .header("x-as-email", Config.USER_EMAIL)
      .GET()
      .build();

    var response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    var jsonResponseBody = new JSONObject(response.body());

    return jsonResponseBody.getJSONArray("myTasks");
  }

  public static String getUserIdForEmail(HttpClient client, String email) throws IOException, InterruptedException {
    JSONArray emails = new JSONArray();
    emails.put(email);

    JSONObject requestBody = new JSONObject();
    requestBody.put("emails", emails);

    HttpRequest request = HttpRequest
      .newBuilder(URI.create(
        Config.BASE_URL + "/accounts/v0/users"
      ))
      .header("Accept", "application/json, text/plain, */*")
      .header("Content-Type", "application/json;charset=UTF-8")
      .header("Authorization", String.format("Bearer %s", Config.API_KEY))
      .header("x-as-email", Config.USER_EMAIL)
      .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
      .build();

    var response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    var list = new JSONObject(response.body()).getJSONArray("results");
    var user = list.getJSONObject(0);

    var userId = user.getString("userId");

    return userId;
  }

  public static String createStandaloneTask(HttpClient client, String name, String supplierId) throws IOException, InterruptedException {
    var userId = getUserIdForEmail(client, Config.USER_EMAIL);

    JSONObject requestBody = new JSONObject();

    var parent = new JSONObject();
    parent.put("principalId", userId);
    requestBody.put("parent", parent);

    var base = new JSONObject();
    base.put("name", name);
    requestBody.put("base", base);

    var assignments = new JSONObject();
    assignments.put("responsible", userId);
    requestBody.put("assignments", assignments);

    HttpRequest request = HttpRequest
      .newBuilder(URI.create(
        Config.BASE_URL + "/planning-objects/v1/standalone-tasks"
      ))
      .header("Accept", "application/json, text/plain, */*")
      .header("Content-Type", "application/json;charset=UTF-8")
      .header("Authorization", String.format("Bearer %s", Config.API_KEY))
      .header("x-as-email", Config.USER_EMAIL)
      .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
      .build();

    var response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    var entityId = new JSONObject(response.body()).getString("entityId");

    addSupplierIdToPlanningObject(client, entityId, supplierId);

    return entityId;
  }

  public static void updateStandaloneTaskName(HttpClient client, String entityId, String name) throws IOException, InterruptedException {
    JSONObject requestBody = new JSONObject();
    var base = new JSONObject();
    base.put("name", name);
    requestBody.put("base", base);

    HttpRequest request = HttpRequest
      .newBuilder(URI.create(
        Config.BASE_URL + "/planning-objects/v1/standalone-tasks/" + entityId
      ))
      .header("Accept", "application/json, text/plain, */*")
      .header("Content-Type", "application/json;charset=UTF-8")
      .header("Authorization", String.format("Bearer %s", Config.API_KEY))
      .header("x-as-email", Config.USER_EMAIL)
      .method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody.toString()))
      .build();

    client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );
  }

  public static JSONObject fetchCustomValuesOfPlanningObjects(HttpClient client, List<String> taskIds) throws IOException, InterruptedException {
    JSONObject requestBody = new JSONObject();
    requestBody.put("entityIds", new JSONArray(taskIds));
    requestBody.put("entityType", "task");
    requestBody.put("organizationId", Config.ORG_ID);

    HttpRequest request = HttpRequest
      .newBuilder(URI.create(Config.BASE_URL + "/custom-fields/v0/entities/resolved-values"))
      .header("Accept", "application/json, text/plain, */*")
      .header("Content-Type", "application/json;charset=UTF-8")
      .header("Authorization", String.format("Bearer %s", Config.API_KEY))
      .header("x-as-email", Config.USER_EMAIL)
      .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
      .build();

    var response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    var body = new JSONObject(response.body());
    return body.getJSONObject("values");
  }

  public static JSONArray fetchPlanningObjectIdsBySupplierId(HttpClient client, String supplierId) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest
      .newBuilder(URI.create(
        Config.BASE_URL
          + "/custom-fields/v0/fields/"
          + Config.SUPPLIER_ID_FIELD_ID
          + "/entityIds?value="
          + URLEncoder.encode(supplierId, StandardCharsets.UTF_8.toString())
      ))
      .header("Accept", "application/json, text/plain, */*")
      .header("Content-Type", "application/json;charset=UTF-8")
      .header("Authorization", String.format("Bearer %s", Config.API_KEY))
      .header("x-as-email", Config.USER_EMAIL)
      .GET()
      .build();

    var response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    return new JSONObject(response.body()).getJSONArray("entityIds");
  }

  public static String addSupplierIdToPlanningObject(HttpClient client, String entityId, String supplierId) throws IOException, InterruptedException {
    JSONObject requestBody = new JSONObject();
    requestBody.put("entityId", entityId);
    requestBody.put("fieldId", Config.SUPPLIER_ID_FIELD_ID);
    requestBody.put("value", supplierId);

    HttpRequest request = HttpRequest
      .newBuilder(URI.create(
        Config.BASE_URL + "/custom-fields/v0/values"
      ))
      .header("Accept", "application/json, text/plain, */*")
      .header("Content-Type", "application/json;charset=UTF-8")
      .header("Authorization", String.format("Bearer %s", Config.API_KEY))
      .header("x-as-email", Config.USER_EMAIL)
      .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
      .build();

    var response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    var valueId = new JSONObject(response.body()).getString("id");

    return valueId;
  }

  public static void updateSupplierIdOfPlanningObject(HttpClient client, String valueId, String supplierId) throws IOException, InterruptedException {
    JSONObject requestBody = new JSONObject();
    requestBody.put("value", supplierId);

    HttpRequest request = HttpRequest
      .newBuilder(URI.create(
        Config.BASE_URL + "/custom-fields/v0/values/" + valueId
      ))
      .header("Accept", "application/json, text/plain, */*")
      .header("Content-Type", "application/json;charset=UTF-8")
      .header("Authorization", String.format("Bearer %s", Config.API_KEY))
      .header("x-as-email", Config.USER_EMAIL)
      .method("PATCH", HttpRequest.BodyPublishers.ofString(requestBody.toString()))
      .build();

    var response = client.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );
  }
}
