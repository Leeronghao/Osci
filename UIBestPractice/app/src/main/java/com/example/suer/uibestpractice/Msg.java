package com.example.suer.uibestpractice;

/**
 * Created by SUER on 2018/4/17.
 */

public class Msg {
    public  static final int TYPE_RECEIVED = 0;   //
    public  static final int TYPE_SEND = 1;
    public Msg(int type,String content){
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private  int type;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;
}
