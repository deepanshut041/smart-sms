package com.example.deepanshutyagi.smartsms.models;

/**
 * Created by Deepanshu Tyagi on 2/22/2018.
 */

public class SmsModel {
    private int id;
    private String address;
    private long date;
    private long dateSent;
    private int protocol;
    private int read;
    private int status;
    private String body;
    private String serviceCenter;
    private int seen;

    private String income_expense_data;
    private String purpose;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDateSent() {
        return dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getServiceCenter() {
        return serviceCenter;
    }

    public void setServiceCenter(String serviceCenter) {
        this.serviceCenter = serviceCenter;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public String getIncomeExpenseData(){
        return income_expense_data;
    }

    public void setIncomeExpenseData(String income_expense){
        this.income_expense_data = income_expense;
    }

    public String getPurpose(){
        return purpose;
    }

    public void setPurpose(String purpose){
        this.purpose = purpose;
    }

}
