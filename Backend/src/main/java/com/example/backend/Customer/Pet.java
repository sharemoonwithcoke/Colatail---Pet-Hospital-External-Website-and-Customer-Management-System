package com.example.backend.Customer;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private String species;
    private String color;
    private String gender;
    private LocalDate birthday;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties({"pets", "hibernateLazyInitializer", "handler"})
    private Person owner;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"pet", "hibernateLazyInitializer", "handler"})
    private List<CaseRecord> caseRecords = new ArrayList<>();

    public Pet() {}

    public Pet(String name, String type, String species, String color, String gender, LocalDate birthday, Person owner) {
        this.name = name;
        this.type = type;
        this.species = species;
        this.color = color;
        this.gender = gender;
        this.birthday = birthday;
        this.owner = owner;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }
    public Person getOwner() { return owner; }
    public void setOwner(Person owner) { this.owner = owner; }
    public List<CaseRecord> getCaseRecords() { return caseRecords; }
    public void setCaseRecords(List<CaseRecord> caseRecords) { this.caseRecords = caseRecords; }

    @Transient
    private List<com.example.backend.ReminderEmail.Reminder> reminders = new java.util.ArrayList<>();

    public List<com.example.backend.ReminderEmail.Reminder> getReminders() { return reminders; }
    public void setReminders(List<com.example.backend.ReminderEmail.Reminder> reminders) { this.reminders = reminders; }
    public void addReminder(com.example.backend.ReminderEmail.Reminder reminder) { reminders.add(reminder); }
    public void removeReminder(com.example.backend.ReminderEmail.Reminder reminder) { reminders.remove(reminder); }
}
