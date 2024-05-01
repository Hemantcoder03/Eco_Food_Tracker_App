package com.hemant.ecofoodtrackerapp.models;

public class FeedbackModel {

    String feedbackMsg;

    public FeedbackModel(String feedbackMsg) {
        this.feedbackMsg = feedbackMsg;
    }

    public FeedbackModel() {
    }

    public String getFeedbackMsg() {
        return feedbackMsg;
    }

    public void setFeedbackMsg(String feedbackMsg) {
        this.feedbackMsg = feedbackMsg;
    }
}
