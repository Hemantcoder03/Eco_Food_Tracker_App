package com.hemant.ecofoodtrackerapp.models;

public class ChatsDonor {

    String chatDonorName, chatDonorLastMessage, chatDonorImage;
    public ChatsDonor() {
    }

    public ChatsDonor(String chatDonorName, String chatDonorLastMessage, String chatDonorImage) {
        this.chatDonorName = chatDonorName;
        this.chatDonorLastMessage = chatDonorLastMessage;
        this.chatDonorImage = chatDonorImage;
    }

    public String getChatDonorName() {
        return chatDonorName;
    }

    public void setChatDonorName(String chatDonorName) {
        this.chatDonorName = chatDonorName;
    }

    public String getChatDonorLastMessage() {
        return chatDonorLastMessage;
    }

    public void setChatDonorLastMessage(String chatDonorLastMessage) {
        this.chatDonorLastMessage = chatDonorLastMessage;
    }

    public String getChatDonorImage() {
        return chatDonorImage;
    }

    public void setChatDonorImage(String chatDonorImage) {
        this.chatDonorImage = chatDonorImage;
    }
}
