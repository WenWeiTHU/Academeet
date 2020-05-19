package com.example.academeet.Item;

import java.util.Date;

public class PaperItem {
    private String title;
    private String abstracts;
    private String fileUrl;
    private String authors;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }


    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public PaperItem(String id, String title, String abstracts, String authors, String fileUrl) {
        setId(id);
        setAbstracts(abstracts);
        setTitle(title);
        setAuthors(authors);
        setFileUrl(fileUrl);
    }

}
