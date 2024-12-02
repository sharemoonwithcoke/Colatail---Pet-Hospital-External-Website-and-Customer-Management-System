package com.example.backend.Customer;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.example.backend.Customer.CaseRecord;
import com.example.backend.ReminderEmail.Reminder;


@Entity
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // 基于前端的分类
   
    private String species;
    private String color;
    private String gender;
    private LocalDate birthday;

    @ManyToOne
    private Person owner;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CaseRecord> caseRecords = new ArrayList<>();

    @Transient
    private List<Reminder> reminders = new ArrayList<>();

    // 默认构造函数
    public Pet() {
    }

   // 带参数的构造函数
   public Pet(String name, String type, String species, String color, String gender, LocalDate birthday, Person owner) {
    this.name = name;
    this.type = type;
    this.species = species;
    this.color = color;
    this.gender = gender;
    this.birthday = birthday;
    this.owner = owner;
}


    // Getter 和 Setter 方法

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public List<CaseRecord> getCaseRecords() {
        return caseRecords;
    }

    public void setCaseRecords(List<CaseRecord> caseRecords) {
        this.caseRecords = caseRecords;
    }

    public void addCaseRecord(CaseRecord caseRecord) {
        caseRecords.add(caseRecord);
        caseRecord.setPet(this);
    }

    public void removeCaseRecord(CaseRecord caseRecord) {
        caseRecords.remove(caseRecord);
        caseRecord.setPet(null);
    }

    public List<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    public void addReminder(Reminder reminder) {
        reminders.add(reminder);
    }

    public void removeReminder(Reminder reminder) {
        reminders.remove(reminder);
    }
}



    

    

