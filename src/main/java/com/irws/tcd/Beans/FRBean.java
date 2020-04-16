package com.irws.tcd.Beans;

public class FRBean {
    private String docNo,parent,Text,Title;

    public String getDocNo() {
        return docNo;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
