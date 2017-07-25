package com.sample.chatbotlib;

import java.util.ArrayList;

/**
 * Created by Priyanka on 30/09/2014.
 */
public class ChatMessage {
    public boolean left, show;
    public String text, selectedDate;
    public String type, option1, option2, subtype;
    public int radiocheck = -1;
    public String selectedSpinner = null;
    public ArrayList<String> options = new ArrayList<>();
    public ArrayList<String> optionids = new ArrayList<>();
    public ArrayList<String> selectedOptions = new ArrayList<>();
    public String browsePath = null;
    public int input = -1;
    public int catradiocheck = -1;
    public int catpos;
    public String subcatpos;

    public ChatMessage(boolean left, String text, String type, String option1, String option2) {
        super();
        this.left = left;
        this.text = text;
        this.type = type;
        this.option1 = option1;
        this.option2 = option2;
    }

    public ChatMessage(boolean left, String text, boolean show) {
        super();
        this.left = left;
        this.text = text;
        this.show = show;

    }

    public ChatMessage(boolean left, String text, boolean show, int input) {
        super();
        this.left = left;
        this.text = text;
        this.show = show;
        this.input = input;

    }

    public ChatMessage(boolean left, String text, String type) {
        super();
        this.left = left;
        this.text = text;
        this.type = type;
    }

    public ChatMessage(boolean left, String text, String type, String subtype, String option1, String option2, ArrayList<String> options, ArrayList<String> optionids, int radiocheck, String selectedSpinner, ArrayList<String> selectedOptions, String selectedDate, String browsePath) {
        super();
        this.left = left;
        this.text = text;
        this.type = type;
        this.subtype = subtype;
        this.option1 = option1;
        this.option2 = option2;
        this.options = options;
        this.optionids = optionids;
        this.radiocheck = radiocheck;
        this.selectedSpinner = selectedSpinner;
        this.selectedOptions = selectedOptions;
        this.selectedDate = selectedDate;
        this.browsePath = browsePath;
    }

    public ChatMessage(boolean left, String text, String type, String subtype, String option1, String option2, ArrayList<String> options, ArrayList<String> optionids, int radiocheck, String selectedSpinner, ArrayList<String> selectedOptions, String selectedDate, String browsePath, int catpos, String subcatpos, int catradiocheck) {
        super();
        this.left = left;
        this.text = text;
        this.type = type;
        this.subtype = subtype;
        this.option1 = option1;
        this.option2 = option2;
        this.options = options;
        this.optionids = optionids;
        this.radiocheck = radiocheck;
        this.selectedSpinner = selectedSpinner;
        this.selectedOptions = selectedOptions;
        this.selectedDate = selectedDate;
        this.browsePath = browsePath;
        this.catpos = catpos;
        this.subcatpos = subcatpos;
        this.catradiocheck = catradiocheck;
    }

    public ChatMessage(boolean left, String text, String type, String subtype, String option1, String option2, ArrayList<String> options, ArrayList<String> optionids) {
        super();
        this.left = left;
        this.text = text;
        this.type = type;
        this.subtype = subtype;
        this.option1 = option1;
        this.option2 = option2;
        this.options = options;
        this.optionids = optionids;
    }
}
