package com.sakec.chembur.sakecvotes.CustomClass;

public class Poll {

    private String descr;
    private String title;
    private String optionO;
    private String optionT;
    private String user;
    private int countO;
    private int countT;

    public Poll(){}

    public Poll(String descr, String title, String optionO, String optionT, String user, int countO, int countT) {
        this.descr = descr;
        this.title = title;
        this.optionO = optionO;
        this.optionT = optionT;
        this.user = user;
        this.countO = countO;
        this.countT = countT;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOptionO() {
        return optionO;
    }

    public void setOptionO(String optionO) {
        this.optionO = optionO;
    }

    public String getOptionT() {
        return optionT;
    }

    public void setOptionT(String optionT) {
        this.optionT = optionT;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getCountO() {
        return countO;
    }

    public void setCountO(int countO) {
        this.countO = countO;
    }

    public int getCountT() {
        return countT;
    }

    public void setCountT(int countT) {
        this.countT = countT;
    }
}
