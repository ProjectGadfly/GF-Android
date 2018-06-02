
package com.forvm.gadfly.projectgadfly.data;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class getScriptResponse {

    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Script")
    @Expose
    @Nullable
    private Script script = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }
}