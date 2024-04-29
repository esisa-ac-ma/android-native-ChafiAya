package esisa.ac.ma.projet_natif.entities;

import java.util.ArrayList;
import java.util.List;

public class Sms {
    private String address;// phone number
    private String body;
    private long date;
    private int type;
    private boolean expanded;
    private List<String> messageList;

    public Sms(String address, String body, long date, int type) {
        this.address = address;
        this.body = body;
        this.date = date;
        this.type = type;
        this.expanded = false;
        this.messageList = new ArrayList<>();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public List<String> getMessageList() {
        return messageList;
    }
    public void addMessage(String message) {
        this.messageList.add(message);
    }
    public void setMessageList(List<String> messageList) {
        this.messageList = messageList;
    }
}
