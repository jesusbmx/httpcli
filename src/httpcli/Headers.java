package httpcli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Headers {

  public static final String CONTENT_DISPOSITION = "Content-Disposition";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";

  private final List<String> namesAndValues;

  public Headers() {
    this(10);
  }
  
  public Headers(int size) {
    namesAndValues = new ArrayList<String>(size * 2);
  }
  
  public int size() {
    return namesAndValues.size() / 2;
  }
  
  public Headers add(String name, String value) {
    // Check for malformed headers.
    if (name == null) throw new NullPointerException("name == null");
    if (name.isEmpty()) throw new IllegalArgumentException("name is empty");
    if (value == null) throw new NullPointerException("value for name " + name + " == null");
    if (name.indexOf('\0') != -1 || value.indexOf('\0') != -1) {
      throw new IllegalArgumentException("Unexpected header: " + name + ": " + value);
    }
    
    namesAndValues.add(name);
    namesAndValues.add(value);
    return this;
  }
  
  public String name(int index) {
    return namesAndValues.get(index * 2);
  }
  
  public String value(int index) {
    return namesAndValues.get(index * 2 + 1);
  }
  
  public String value(String key) {
    for (int i = namesAndValues.size() - 2; i >= 0; i -= 2) {
      if (key.equalsIgnoreCase(namesAndValues.get(i))) {
        return namesAndValues.get(i + 1);
      }
    }
    return null;
  }

  public static Headers of(Map<String, List<String>> map) {
    Headers headers = new Headers(map.size());
    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
      String key = entry.getKey();
      if (key != null) {
        List<String> value = entry.getValue();
        for (int i = 0; i < value.size(); i++) {
          headers.add(key, value.get(i));
        } 
      }
    }
    return headers;
  }

  /**
   * Returns headers for the alternating header names and values. There must be
   * an even number of arguments, and they must alternate between header names
   * and values.
   */
  public static Headers of(String... namesAndValues) {
    if (namesAndValues == null)  throw new NullPointerException("namesAndValues == null");
    if (namesAndValues.length % 2 != 0) {
      throw new IllegalArgumentException("Expected alternating header names and values");
    }

    Headers headers = new Headers(namesAndValues.length / 2);
    
    for (int i = 0; i < namesAndValues.length; i += 2) {
      String name = namesAndValues[i];
      String value = namesAndValues[i + 1];
      headers.add(name, value);
    }

    return headers;
  }

  @Override public String toString() {
    StringBuilder result = new StringBuilder();
    for (int i = 0, size = size(); i < size; i++) {
      result.append(name(i)).append(": ").append(value(i)).append("\n");
    }
    return result.toString();
  }
}
