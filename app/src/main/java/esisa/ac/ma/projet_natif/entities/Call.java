package esisa.ac.ma.projet_natif.entities;

public class Call {
    private String photo;
    private String title;
    private boolean isExpanded;
    private String type;
    private String operator;

    private long date;
    private long duration;
    private String callTime;

    public long getDuration() {
        return duration;
    }


    public void setDuration(long duration) {
        this.duration = duration;
    }
    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
    public Call(String title, String type, String operator, long date, long duration, String callTime, String photo) {
        this.title = title;
        this.type = type;
        this.operator = operator;
        this.date = date;
        this.duration = duration;
        this.callTime = callTime;
        this.photo = (photo == null) ? "" : photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }
}
