package com.nd.hy.android.webview.library;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author Jackoder
 * @version 2015/7/14.
 */
public class BaseJSInterface {

    ObjectMapper mMapper = new ObjectMapper();

    protected <T> T convertJson2Data(Class<T> model, String jsonString) {
        try {
            return mMapper.readValue(jsonString, model);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String convertData2Json(Object data) {
        try {
            return mMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected boolean isNull(Object obj) {
        if (null != obj) {
            if (obj instanceof String) {
                return obj.equals("null");
            }
            return false;
        }
        return true;
    }
}
