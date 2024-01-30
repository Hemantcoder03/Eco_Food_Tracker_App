package com.hemant.ecofoodtrackerapp.models;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatroomModel {

    String classroomId;
    List<String> userIds;
    Timestamp lastMessageTimestamp;
    String lastMessageSenderId;

    public ChatroomModel() {
    }

    public ChatroomModel(String classroomId, List<String> userIds, Timestamp lastMessageTimestamp, String lastMessageSenderId) {
        this.classroomId = classroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }
}
