
package httpcli.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ObjectAdapter<T> {

    /** Cache de adaptadores para los objectos. */
    private static final Map<Class, ObjectAdapter> objectAdapters = 
            new HashMap<Class, ObjectAdapter>();
    
    public final Class<T> classOf;
    public final FieldAdapter[] fields;

    public ObjectAdapter(Class<T> classOf) throws Exception {
        this.classOf = classOf;
        this.fields = FieldAdapter.fields(classOf);
    }
        
    public static <V> ObjectAdapter<V> get(Class<V> classOf) throws Exception {
        ObjectAdapter<V> adapter = objectAdapters.get(classOf);
        if (adapter == null) {
            adapter = new ObjectAdapter<V>(classOf);
            set(classOf, adapter);
        }
        return adapter;
    }
    
    public static <V> ObjectAdapter<V> set(Class<V> classOf, ObjectAdapter<V> adapter) {
        objectAdapters.put(classOf, adapter);
        return adapter;
    }
    
    public Map<String, Object> toMap(T src) throws Exception {
        if (src == null) return null;

        final Map<String, Object> result = new HashMap<String, Object>(fields.length);
        fill(src, result);
                    
        return result;
    }
    
    public <V> void fill(V src, Map<String, Object> dest) throws Exception {        
        for (int i = 0; i < fields.length; i++) {
            FieldAdapter field = fields[i];
            
            String key = field.getName();
            Object value = field.get(src);
            
            dest.put(key, value);
        }
    }

    public static class FieldAdapter<T> {
        Field field;
        Method getMethod;

        public FieldAdapter(Field field, Method getMethod) {
            this.field = field;
            this.getMethod = getMethod;
        }
        
        public String getName() {
            return field.getName();
        }
        
        public T get(Object object) throws Exception {
            if (getMethod != null) 
                return (T) getMethod.invoke(object);
            else
                return (T) field.get(object);
        } 
        
        static FieldAdapter[] fields(Class classOf) throws Exception {
            final Field[] fs = classOf.getDeclaredFields();
            final Method[] m = classOf.getDeclaredMethods();
            final FieldAdapter[] result = new FieldAdapter[fs.length];

            Method get;
            //Method set;
            int y = 0;

            for (Field f : fs) {
              get = findGetMethod(f, m);
              //set = OrmUtils.setMethod(f, m);
              result[y++] = new FieldAdapter(f, get);
            }

            final FieldAdapter[] copy = new FieldAdapter[y];
            System.arraycopy(result, 0, copy, 0, y);
            return copy;
        }
        
        public static String capitalize(String str) {
            char[] data = str.toCharArray();
            data[0] = Character.toUpperCase(data[0]);
            return new String(data);
        }
    
        public static Method findGetMethod(Field f, Method[] m) throws Exception {
            String name = capitalize(f.getName());
            String methodnameA = "get" + name;
            String methodnameB = "is" + name;

            Class[] parameterTypes;

            for (Method method : m) {
                parameterTypes = method.getParameterTypes();

                if (parameterTypes.length > 0) {
                    continue;
                }

                if (method.getName().equals(methodnameA)) {
                    return method;
                } else if (method.getName().equals(methodnameB)) {
                    return method;
                }
            }

            return null;
        }
        
    }
}