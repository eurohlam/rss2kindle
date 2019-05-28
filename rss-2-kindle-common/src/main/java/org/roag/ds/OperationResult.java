package org.roag.ds;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by eurohlam on 09.12.16.
 */
public enum OperationResult {
    SUCCESS("success"),
    FAILURE("failure"),
    NOT_EXIST("not_exist"),
    DUPLICATED("duplicated");

    private final String status;
    private final Map<String, String> map;

    OperationResult(String status) {
        this.status = status;
        map = new LinkedHashMap<>(1);
        map.put("status", status);
    }

    public String toJson() {
        return new Gson().toJson(map);
    }

    @Override
    public String toString() {
        return status;
    }
}
