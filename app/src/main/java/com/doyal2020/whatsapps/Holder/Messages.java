package com.doyal2020.whatsapps.Holder;

public class Messages {

    String from, message, type, to, time,date,messageID,name;

    public Messages() {

    }

    public Messages(String from, String message, String type, String to, String time, String date, String messageID, String name) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.to = to;
        this.time = time;
        this.date = date;
        this.messageID = messageID;
        this.name = name;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}