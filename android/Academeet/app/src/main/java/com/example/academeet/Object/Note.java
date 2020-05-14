package com.example.academeet.Object;

import java.io.Serializable;

public class Note implements Serializable {
    private String title;
    private String noteAbstract;
    private String date;
    private String id;
    private String content;

    public Note() {

    }

    public Note(String title, String noteAbstract, String date) {
        this.title = title;
        this.noteAbstract = noteAbstract;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setNoteAbstract(String noteAbstract) {
        this.noteAbstract = noteAbstract;
    }

    public String getNoteAbstract() {
        return noteAbstract;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}