package com.mobilecourse.backend.model;

public class Chatroom {
    int chatroom_id;
    int participant_num;
    int record_num;

    public Chatroom() {
        participant_num = 0;
        record_num = 0;
    }

    public int getChatroom_id() {
        return chatroom_id;
    }

    public void setChatroom_id(int chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public int getParticipant_num() {
        return participant_num;
    }

    public void setParticipant_num(int participant_num) {
        this.participant_num = participant_num;
    }

    public int getRecord_num() {
        return record_num;
    }

    public void setRecord_num(int record_num) {
        this.record_num = record_num;
    }
}
