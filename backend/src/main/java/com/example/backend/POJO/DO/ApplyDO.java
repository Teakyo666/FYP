package com.example.backend.POJO.DO;

public class ApplyDO {
    private Long id;
    private String user_id;
    private String to_apply;
    private int status;
    private Boolean is_deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
}
