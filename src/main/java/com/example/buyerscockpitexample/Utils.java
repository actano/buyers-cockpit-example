package com.example.buyerscockpitexample;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Utils {
  public static <E> Stream<E> streamJsonArray(JSONArray array) {
    return (Stream<E>) StreamSupport.stream(
      Spliterators.spliteratorUnknownSize(array.iterator(), Spliterator.ORDERED),
      false
    );
  }

  public static String findSupplierId(JSONArray customValues) {
    var customValue = findSupplierIdCustomValue(customValues);

    if (customValue != null) {
      return customValue.optString("textValue");
    }

    return null;
  }

  public static JSONObject findSupplierIdCustomValue(JSONArray customValues) {
    for (Object o : customValues) {
      var value = (JSONObject) o;
      if (value.getString("fieldId").equals(Config.SUPPLIER_ID_FIELD_ID)) {
        return value;
      }
    }

    return null;
  }
}
