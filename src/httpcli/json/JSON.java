package httpcli.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSON {

  public static JSON of(String json) throws JSONException {
    if (json == null) 
      throw new JSONException("json == null");

    if (json.isEmpty()) 
      throw new JSONException("json is empty");

    int c = json.charAt(0);
    switch (c) {
      case '{':
        return new JSON(new JSONObject(json), null);

      case '[':
        return new JSON(null, new JSONArray(json));

      default:
        throw new JSONException("invalidate: " + json);
    }
  }
  
  private final JSONObject object;
  private final JSONArray array;
  private final boolean isObject;
  private final boolean isArray;

  JSON(JSONObject object, JSONArray array) {
    this.object = object;
    this.array = array;
    this.isObject = object != null;
    this.isArray = array != null;
  }

  public JSONObject getObject() {
    return object;
  }

  public JSONObject optObject(JSONObject defaultVal) {
    return isObject() ? getObject() : defaultVal;
  }
  
  public JSONObject optObject() {
    return isObject() ? getObject() : new JSONObject();
  }

  public JSONArray getArray() {
    return array;
  }

  public JSONArray optArray(JSONArray defaultVal) {
    return isArray() ? getArray() : defaultVal;
  }
  
  public JSONArray optArray() {
    return isArray() ? getArray() : new JSONArray();
  }

  public boolean isObject() {
    return isObject;
  }

  public boolean isArray() {
    return isArray;
  }

  public String toString(int indentFactor) {
    try {
        if (isArray()) 
            return array.toString(indentFactor);
        if (isObject()) 
            return object.toString(indentFactor);
        
        return null;
        
    } catch(Exception e) {
        return toString();
    }
  }

  @Override public String toString() {
    if (isArray()) 
        return array.toString();
    if (isObject()) 
        return object.toString();
    
    return null;
  }
}