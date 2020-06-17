package com.example.academeet.Object;

import java.io.Serializable;

public class Note implements Serializable {
    private String title;
    private String noteAbstract;
    private String editDate;
    private String createDate = "";
    private String id;
    private String content;

    public Note() {

    }

    public Note(String title, String noteAbstract, String editDate, String createDate) {
        this.title = title;
        this.noteAbstract = noteAbstract;
        this.editDate = editDate;
        this.createDate = createDate;
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

    public void setEditDate(String date) {editDate = date;}

    public void setContent(String content) {
        this.content = content;
        try {
            int index = content.indexOf("\n");
            index = index > 20 ? 20 : index;
            setTitle(content.substring(0, index));
        } catch (StringIndexOutOfBoundsException e) {
            setTitle(content.substring(0, content.length()));
            setNoteAbstract("");
            return;
        }
        try{
            setNoteAbstract(content.substring(20, 100));
        } catch (StringIndexOutOfBoundsException e) {
            setNoteAbstract(content.substring(20, content.length()));
        }
    }

    public void setNoteAbstract(String noteAbstract) {
        this.noteAbstract = noteAbstract;
    }

    public String getNoteAbstract() {
        return noteAbstract;
    }

    public String getEditDate() {
        return editDate;
    }

    public String getCreateDate() { return createDate; }


    public void setCreateDate(String date) {
        this.createDate = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
