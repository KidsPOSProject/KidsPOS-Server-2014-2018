package info.nukoneko.kidspos.util.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.nukoneko.kidspos.model.BaseKPModel;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by TEJNEK on 2016/01/31.
 */
public class JSON {
    private final static ObjectMapper mapper = new ObjectMapper();
    public static String covertJSON(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String covertJSON(ArrayList<Object> obj){
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static <T extends BaseKPModel> T convertClass(String json, Class<T> clazz){
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
