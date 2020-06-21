package com.example.academeet.Item;

public class PaperItem {
    private String title;
    private String abstracts;
    private String fileUrl;
    private String authors;
    private String id;

    /**
     * @describe: 获取论文的id
     * @return 论文的id
     */
    public String getId() {
        return id;
    }

    /**
     * @describe: 设置论文的id
     * @param id 论文的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @describe: 获取论文对应的pdf的链接
     * @return 文件对应链接
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * @describe: 设置会议论文对应的链接
     * @param fileUrl 文件的链接
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * @describe: 获取论文的标题
     * @return 论文的标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * @describe: 设置论文的标题
     * @param title 论文的标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @describe: 获取论文的摘要
     * @return 论文的摘要
     */
    public String getAbstracts() {
        return abstracts;
    }

    /**
     * @describe: 设置论文的摘要
     * @param abstracts 论文的摘要
     */
    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }


    /**
     * @describe: 获取论文的作者
     * @return 论文的作者
     */
    public String getAuthors() {
        return authors;
    }

    /**
     * @describe: 设置论文的作者
     * @param authors 论文的作者
     */
    public void setAuthors(String authors) {
        this.authors = authors;
    }

    /**
     * @describe: 生成一个 PaperItem 实例
     * @param id 论文的id
     * @param title 论文的标题
     * @param abstracts 论文的摘要
     * @param authors 论文的作者
     * @param fileUrl 论文的文件链接
     */
    public PaperItem(String id, String title, String abstracts, String authors, String fileUrl) {
        setId(id);
        setAbstracts(abstracts);
        setTitle(title);
        setAuthors(authors);
        setFileUrl(fileUrl);
    }

}
