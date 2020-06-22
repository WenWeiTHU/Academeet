package com.example.academeet.Object;

import java.io.Serializable;

public class Note implements Serializable {
    private String title;
    private String noteAbstract;
    private String editDate;
    private String createDate = "";
    private String id;
    private String content;

    /**
     * @describe: 生成一个空白 Note
     */
    public Note() {

    }

    /**
     * @describe: 生成一个带有指定数据的 Note
     * @param title 标题
     * @param noteAbstract 摘要
     * @param editDate 修改日期
     * @param createDate 创建日期
     */
    public Note(String title, String noteAbstract, String editDate, String createDate) {
        this.title = title;
        this.noteAbstract = noteAbstract;
        this.editDate = editDate;
        this.createDate = createDate;
    }

    /**
     * @describe: 获取 Note 的 ID
     * @return Note 的 ID
     */
    public String getId() {
        return id;
    }

    /**
     * @describe: 设置 Note 的 ID
     * @param id Note的 新 ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @describe: 获取 Note 的内容
     * @return Note 的内容
     */
    public String getContent() {
        return content;
    }

    /**
     * @describe: 设置 Note 的修改日期
     * @param date 新的修改日期
     */
    public void setEditDate(String date) {editDate = date;}

    /**
     * @describe: 设置 Note 的内容
     * @param content 新的内容
     */
    public void setContent(String content) {
        this.content = content;
        int index;
        try {
            index = content.indexOf("\n");
            index = index == -1 ? 20 : index;
            index = index > 20 ? 20 : index;
            setTitle(content.substring(0, index));
        } catch (StringIndexOutOfBoundsException e) {
            setTitle(content.substring(0, content.length()));
            setNoteAbstract("");
            return;
        }
        try{
            setNoteAbstract(content.substring(index, 100));
        } catch (StringIndexOutOfBoundsException e) {
            setNoteAbstract(content.substring(index, content.length()));
        }
    }

    /**
     * @describe: 设置 Note 的摘要
     * @param noteAbstract 新的摘要
     */
    public void setNoteAbstract(String noteAbstract) {
        this.noteAbstract = noteAbstract;
    }

    /**
     * @describe: 获取 Note 的摘要
     * @return Note 的摘要
     */
    public String getNoteAbstract() {
        return noteAbstract;
    }

    /**
     * @describe: 获取 Note 的修改日期
     * @return Note 的修改日期
     */
    public String getEditDate() {
        return editDate;
    }

    /**
     * @describe: 获取 Note 的创建日期
     * @return Note 的创建日期
     */
    public String getCreateDate() { return createDate; }


    /**
     * @describe: 设置 Note 的创建日期
     * @param date 新的创建日期
     */
    public void setCreateDate(String date) {
        this.createDate = date;
    }

    /**
     * @describe: 设置 Note 的标题
     * @param title 新的标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @describe: 获取 Note 的标题
     * @return Note 的标题
     */
    public String getTitle() {
        return title;
    }
}
