package com.sample.chatbotlib;

import java.util.ArrayList;

/**
 * Created by marco.granatiero on 30/09/2014.
 */
public class ChatMessage {
    public boolean left;
    public String text,selectedDate;
    public String type,option1,option2;
    public int radiocheck=-1;
    public String selectedCntry=null;
    public ArrayList<String> options=new ArrayList<>();
    public ArrayList<String> selectedOptions=new ArrayList<>();
    public ChatMessage(boolean left, String text,String type,String option1,String option2) {
        super();
        this.left = left;
        this.text = text;
        this.type = type;
        this.option1 = option1;
        this.option2 = option2;
    }
    public ChatMessage(boolean left, String text) {
        super();
        this.left = left;
        this.text = text;

    }
    public ChatMessage(boolean left, String text,String type) {
        super();
        this.left = left;
        this.text = text;
        this.type = type;
    }
    public ChatMessage(boolean left, String text,String type,String option1,String option2,ArrayList<String> options) {
        super();
        this.left = left;
        this.text = text;
        this.type = type;
        this.option1 = option1;
        this.option2 = option2;
        this.options = options;
    }
}
