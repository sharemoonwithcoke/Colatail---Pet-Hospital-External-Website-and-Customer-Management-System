package com.example.backend.toDo;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ToDoListDialog extends JDialog {
    private ToDoList toDoList;

    public ToDoListDialog(JFrame parent, ToDoList toDoList) {
        super(parent, "To-Do List", true);
        this.toDoList = toDoList;

        setLayout(new BorderLayout());
        setSize(400, 300);

        JList<String> taskList = new JList<>(toDoList.getTasks().stream().map(ToDoList.Task::toString).toArray(String[]::new));
        add(new JScrollPane(taskList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Task");
        JButton removeButton = new JButton("Remove Task");
        JButton completeButton = new JButton("Complete Task");
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(completeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addTask(taskList));
        removeButton.addActionListener(e -> removeTask(taskList));
        completeButton.addActionListener(e -> completeTask(taskList));
    }

    private void addTask(JList<String> taskList) {
        String description = JOptionPane.showInputDialog(this, "Enter task description:");
        if (description != null && !description.trim().isEmpty()) {
            toDoList.addTask(description);
            refreshTaskList(taskList);
        }
    }

    private void removeTask(JList<String> taskList) {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex >= 0) {
            toDoList.removeTask(selectedIndex);
            refreshTaskList(taskList);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to remove.");
        }
    }

    private void completeTask(JList<String> taskList) {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex >= 0) {
            toDoList.completeTask(selectedIndex);
            refreshTaskList(taskList);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to complete.");
        }
    }

    private void refreshTaskList(JList<String> taskList) {
        taskList.setListData(toDoList.getTasks().stream().map(ToDoList.Task::toString).toArray(String[]::new));
    }
}
