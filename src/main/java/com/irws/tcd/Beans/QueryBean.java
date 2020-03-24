package com.irws.tcd.Beans;

public class QueryBean {
    private String num,title,description,narrative;
    private int queryNo;

    public int getqueryNo() {
        return queryNo;
    }

    public void setqueryNo(int queryNo) {
        this.queryNo = queryNo;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }
}
