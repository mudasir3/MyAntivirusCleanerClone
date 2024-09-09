package com.ln.antivirus.mobilesecurity.iface;

import org.json.JSONException;
import org.json.JSONObject;

public interface IJSONSerializer {
    JSONObject buildJSONObject() throws JSONException;

    void loadFromJSON(JSONObject jSONObject);

    void writeToJSON(String str);
}
