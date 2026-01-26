package com.example.coligify.Model;

public class NotificationModel {

    public static final String TYPE_SUCCESS = "success";
    public static final String TYPE_ERROR = "error";
    public static final String TYPE_UPDATE = "update";
    public static final String TYPE_ALERT = "alert";

    private String title;
    private String message;
    private String time;
    private String type;

    public NotificationModel(String title, String message, String time, String type) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }
}
