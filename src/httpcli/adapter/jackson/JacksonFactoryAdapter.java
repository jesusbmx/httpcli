package httpcli.adapter.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import httpcli.adapter.FactoryAdapter;
import httpcli.adapter.ReqBodyAdapter;
import httpcli.adapter.RespBodyAdapter;
import java.util.HashMap;
import java.util.Map;

public class JacksonFactoryAdapter extends FactoryAdapter {

    public final ObjectMapper mapper;

    public JacksonFactoryAdapter(ObjectMapper mapper) {
        this.mapper = mapper;
    }
    
    @Override
    public <V> RespBodyAdapter<V> newOtherRespBodyAdapter(final Class<V> classOf) {
        ObjectReader reader = mapper.readerFor(classOf);
        return new RespBodyJackson<V>(reader);
    }

    @Override
    public <V> ReqBodyAdapter<V> newOtherReqBodyAdapter(final Class<V> classOf) {
        ObjectWriter writer = mapper.writerFor(classOf);
        return new ReqBodyJackson<V>(writer);
    }

    @Override
    public <V> Map<String, Object> toMap(V src) {
        try {
            TypeReference<HashMap<String, Object>> typeRef 
                    = new TypeReference<HashMap<String, Object>>() {};
            
            return mapper.convertValue(src, typeRef);
            
        } catch(IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }   
}
