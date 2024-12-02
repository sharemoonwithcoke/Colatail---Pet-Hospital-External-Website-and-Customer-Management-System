
package com.example.backend.ReminderEmail;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.example.backend.Customer.CustomerManager;
import com.example.backend.Customer.Pet;

public class ReminderManager {
    

    // 获取接下来一周内的提醒
    public List<Reminder> getUpcomingReminders(List<Pet> pets) {
        LocalDate nextWeek = LocalDate.now().plusWeeks(1);
        return pets.stream()
                .flatMap(pet -> pet.getReminders().stream())
                .filter(reminder -> reminder.getDueDate().isBefore(nextWeek))
                .collect(Collectors.toList());
    }

    // 为宠物的生日创建提醒
    public void addBirthdayReminders(List<Pet> pets) {
        pets.forEach(pet -> {
            String birthdayNote = "请祝" + pet.getName() + "生日快乐！";
            Reminder birthdayReminder = new Reminder(pet.getBirthday(), birthdayNote);
            pet.addReminder(birthdayReminder);
        });
    }

    // 创建和添加一个通用提醒
    public void addGeneralReminder(Pet pet, LocalDate dueDate, String note, int daysBefore) {
        LocalDate reminderDate = dueDate.minusDays(daysBefore);
        Reminder newReminder = new Reminder(reminderDate, note);
        pet.addReminder(newReminder);
    }


    
    
    //更改地方！！！
    public List<Reminder> getAllReminders(List<Pet> pets) {
        return pets.stream()
                   .flatMap(pet -> pet.getReminders().stream())
                   .collect(Collectors.toList());
    }
    
    public List<Reminder> getAllReminders() {
        return getAllReminders(customerManager.getAllPets());
    }
    
    

    CustomerManager customerManager = new CustomerManager();
    // Assume customers and their pets are added to customerManager here

   
    

    public void removeReminder(Reminder reminder) {
        List<Pet> allPets = customerManager.getAllPets(); // 获取所有宠物
        for (Pet pet : allPets) {
            if (pet.getReminders().contains(reminder)) {
                pet.removeReminder(reminder); // 移除找到的提醒
                break; // 假设每个提醒只属于一个宠物，找到后即可停止搜索
            }
        }
    }

  
    

}
