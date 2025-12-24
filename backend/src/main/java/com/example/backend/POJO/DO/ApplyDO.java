package com.example.backend.POJO.DO;

public class ApplyDO {
    private String id;
    private String user_id;
    private String to_apply;
    private String reason;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTo_apply() {
        return to_apply;
    }

    public void setTo_apply(String to_apply) {
        this.to_apply = to_apply;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
