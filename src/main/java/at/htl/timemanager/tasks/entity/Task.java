package at.htl.timemanager.tasks.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {

    private String number;
    private Integer status;
    private String assignee;
    private Date date;
    private String description;

    public Task(String number, String status, String assignee, String date, String description) {
        setNumber(number);
        try {
            this.status = Integer.valueOf(status);
        } catch (Exception e) {
            this.status = 0;
        }
        this.assignee = assignee;
        if (date != null && !date.isEmpty()) {
            try {
                this.date = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        for (int i = 0; i < 4 - number.length(); ) {
            number = "0" + number;
        }
        this.number = number;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}