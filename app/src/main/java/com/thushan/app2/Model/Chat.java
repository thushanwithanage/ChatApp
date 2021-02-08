package com.thushan.app2.Model;

public class Chat
{
    private String sender;
    private String receiver;
    private String message;
    private boolean isseen;

    private String msgId;
    private boolean delSender;
    private boolean delReceiver;
    private String msgType;
    private String msgDate;
    private String msgTime;



    public Chat() {
    }

    public Chat(String msgId, String sender, String receiver, String message, boolean isseen, boolean delSender, boolean delReceiver, String msgType, String msgDate, String msgTime) {
        this.msgId = msgId;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.delSender = delSender;
        this.delReceiver = delReceiver;
        this.msgType = msgType;
        this.msgDate = msgDate;
        this.msgTime = msgTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public boolean isDelSender() {
        return delSender;
    }

    public void setDelSender(boolean delSender) {
        this.delSender = delSender;
    }

    public boolean isDelReceiver() {
        return delReceiver;
    }

    public void setDelReceiver(boolean delReceiver) {
        this.delReceiver = delReceiver;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }
}
