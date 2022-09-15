package com.example.rurbansoft;

public class AllUsers {
    private String Name;
    private String Design;
    private String Phone;
    private String Email;
    private int id;

    // creating getter and setter methods
    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getDesign() {
        return Design;
    }

    public void setDesign(String Design) {
        this.Design = Design;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // constructor
    public AllUsers(String Name, String Design, String Phone, String Email) {
        this.Name = Name;
        this.Design = Design;
        this.Phone = Phone;
        this.Email = Email;
    }
}

