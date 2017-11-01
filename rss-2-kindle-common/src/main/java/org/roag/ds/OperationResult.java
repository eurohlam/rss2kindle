package org.roag.ds;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by eurohlam on 09.12.16.
 */
public enum OperationResult
{
    SUCCESS("success"),
    FAILURE("failure"),
    NOT_EXIST("not_exist"),
    DUPLICATED("duplicated");

    private String status;
    private Map<String, String> map;
    private Gson gson=new Gson();

    private OperationResult(String status)
    {
        this.status = status;
        map = new LinkedHashMap<>(1);
        map.put("status", status);
    }

    public String toJSON()
    {
        return gson.toJson(map);
    }
}
