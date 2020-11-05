package com.hsbook.model;

public class TextDetailModel {

    private String text_title;
    private String text_desc;

    public TextDetailModel() {
    }

    public TextDetailModel(String text_title, String text_desc) {
        this.text_title = text_title;
        this.text_desc = text_desc;
    }

    public String getText_title() {
        return text_title;
    }

    public void setText_title(String text_title) {
        this.text_title = text_title;
    }

    public String getText_desc() {
        return text_desc;
    }

    public void setText_desc(String text_desc) {
        this.text_desc = text_desc;
    }
}
