package com.example.trackeme.Model;

import com.google.gson.annotations.Expose;

public class POSTRESPONSE {
    @Expose
    int POST_ID;
    @Expose
    String Status;

    public POSTRESPONSE() {
    }

    public POSTRESPONSE(int POST_ID, String status) {
        this.POST_ID = POST_ID;
        Status = status;
    }

    public int getPOST_ID() {
        return POST_ID;
    }

    public void setPOST_ID(int POST_ID) {
        this.POST_ID = POST_ID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
