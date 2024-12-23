package com.example.backend.Calendar;


import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.example.backend.Customer.Pet;
import com.example.backend.ReminderEmail.Reminder;
import com.example.backend.ReminderEmail.ReminderManager;

public class RemindersDialog extends JDialog {
    private JFrame parent;
    private JList<String> reminderList;
    private DefaultListModel<String> reminderListModel;
    private ReminderManager reminderManager;
    private List<Reminder> reminders;
    private List<Pet> pets;

    public RemindersDialog(JFrame parent, ReminderManager reminderManager, List<Pet> pets) {
        super(parent, "Reminders", true);
        this.parent = parent;
        this.reminderManager = reminderManager;
        this.pets = pets;
        initializeUI();
        loadReminders();
    }

    private void initializeUI() {
        setSize(300, 400);
        setLayout(new BorderLayout());

        reminderListModel = new DefaultListModel<>();
        reminderList = new JList<>(reminderListModel);
        JScrollPane scrollPane = new JScrollPane(reminderList);
        add(scrollPane, BorderLayout.CENTER);

        JButton viewDetailButton = new JButton("View Detail");
        JButton dismissButton = new JButton("Dismiss");
        JButton addReminderButton = new JButton("Add Reminder");

        viewDetailButton.addActionListener(e -> viewReminderDetail());
        dismissButton.addActionListener(e -> removeReminder());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewDetailButton);
        buttonPanel.add(dismissButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadReminders() {
        // 假设ReminderManager有一个方法返回所有提醒
        reminders = reminderManager.getAllReminders(pets);
        reminderListModel.clear();
        reminders.forEach(reminder -> reminderListModel.addElement(reminder.getNote()));
    }

    private void viewReminderDetail() {
        int selectedIndex = reminderList.getSelectedIndex();
        if (selectedIndex != -1) {
            Reminder selectedReminder = reminders.get(selectedIndex);
            new ReminderDetailView((JFrame)this.getParent(), selectedReminder).setVisible(true);

        }
    }

    //、、、、、、、、、添加方法中需要选择已有的宠物

    private void addReminder() {
        // 假设ReminderManager有方法添加提醒
        reminderManager.addGeneralReminder(pets.get(0), LocalDate.now().plusDays(7), "Reminder", 7);
        loadReminders(); // 重新加载提醒列表
    }

private void removeReminder() {
        int selectedIndex = reminderList.getSelectedIndex();
        if (selectedIndex != -1) {
            // 假设ReminderManager有方法根据索引删除提醒
            reminderManager.removeReminder(reminders.get(selectedIndex));
            loadReminders(); // 重新加载提醒列表
        }
    }

   

    public void showDialog() {
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
}
