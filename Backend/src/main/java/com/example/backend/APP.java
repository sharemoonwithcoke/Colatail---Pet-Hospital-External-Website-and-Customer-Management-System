package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import javax.swing.SwingUtilities;
import com.example.backend.Customer.CustomerManager;
import com.example.backend.Appointment.AppointmentManager;
import com.example.backend.toDo.ToDoList;
import com.example.backend.ReminderEmail.ReminderManager;
@SpringBootApplication(scanBasePackages = {"com.example.backend"})
public class APP {
    public static void main(String[] args) {
        SpringApplication.run(APP.class, args);

        // 在单独的线程中运行 GUI
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                // 初始化 GUI 管理器
                AppointmentManager appointmentManager = new AppointmentManager();
                ToDoList toDoList = new ToDoList();
                ReminderManager reminderManager = new ReminderManager();
                CustomerManager customerManager = new CustomerManager();

                MainPage mainPage = new MainPage(appointmentManager, toDoList, reminderManager, customerManager);
                mainPage.setVisible(true);
            });
        }).start();
    }
}




