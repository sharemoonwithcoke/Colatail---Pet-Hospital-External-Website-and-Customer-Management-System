package com.example.backend;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.example.backend.Customer.Address;
import com.example.backend.Customer.CustomerManager;
import com.example.backend.Customer.Person;
import com.example.backend.Customer.Pet;

public class CustomerinfoView extends JDialog {

    private CustomerManager customerManager;
    private JFrame parent;
    private DefaultTableModel tableModel;

    public CustomerinfoView(JFrame parent, CustomerManager customerManager) {
        super(parent, "Customer Information", true);
        this.parent = parent;
        this.customerManager = customerManager;

        setSize(600, 400);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Customer");
        JButton updateButton = new JButton("Update Customer");
        JButton removeButton = new JButton("Remove Customer");
        JButton findButton = new JButton("Find Customer");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addCustomer());
        updateButton.addActionListener(e -> updateCustomerInfo());
        removeButton.addActionListener(e -> removeCustomer());
        findButton.addActionListener(e -> findCustomer());
        refreshButton.addActionListener(e -> refreshCustomerTable());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(findButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        initializeTable();
    }

    private void initializeTable() {
        String[] columnNames = {"First Name", "Last Name", "Phone Number", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable customerTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);

        refreshCustomerTable();
    }

    private void addCustomer() {
        String firstName = JOptionPane.showInputDialog(this, "Enter customer's first name:");
        String lastName = JOptionPane.showInputDialog(this, "Enter customer's last name:");
        String phoneNumber = JOptionPane.showInputDialog(this, "Enter customer's phone number:");
        String email = JOptionPane.showInputDialog(this, "Enter customer's email:");

        Address address = createAddressDialog();
        List<Pet> pets = new ArrayList<>();
        boolean addingPets = true;
        while (addingPets) {
            int addMore = JOptionPane.showConfirmDialog(this, "Do you want to add a pet?", "Add Pet", JOptionPane.YES_NO_OPTION);
            if (addMore == JOptionPane.YES_OPTION) {
                Pet pet = createPetDialog();
                if (pet != null) {
                    pets.add(pet);
                }
            } else {
                addingPets = false;
            }
        }

        Person newCustomer = new Person(firstName, lastName, phoneNumber, address, email, pets);
        customerManager.addCustomer(newCustomer);
        JOptionPane.showMessageDialog(this, "Customer added successfully.");
        refreshCustomerTable();
    }
    private Pet createPetDialog() {
        JTextField nameField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField speciesField = new JTextField();
        JTextField colorField = new JTextField();
        JComboBox<String> genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});
        JTextField birthdayField = new JTextField("YYYY-MM-DD");
    
        final JComponent[] inputs = new JComponent[]{
            new JLabel("Name:"),
            nameField,
            new JLabel("Type:"),
            typeField,
            new JLabel("Species:"),
            speciesField,
            new JLabel("Color:"),
            colorField,
            new JLabel("Gender:"),
            genderComboBox,
            new JLabel("Birthday (YYYY-MM-DD):"),
            birthdayField
        };
    
        int result = JOptionPane.showConfirmDialog(this, inputs, "Enter Pet Details", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                return new Pet(
                    nameField.getText(),
                    typeField.getText(),
                    speciesField.getText(),
                    colorField.getText(),
                    genderComboBox.getSelectedItem().toString(),
                    LocalDate.parse(birthdayField.getText()),
                    null // Owner will be set separately
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please check the values and try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    

    private Address createAddressDialog() {
        JTextField streetField = new JTextField();
        JTextField cityField = new JTextField();
        JTextField stateField = new JTextField();
        JTextField zipCodeField = new JTextField();

        final JComponent[] inputs = new JComponent[]{
            new JLabel("Street:"),
            streetField,
            new JLabel("City:"),
            cityField,
            new JLabel("State:"),
            stateField,
            new JLabel("Zip Code:"),
            zipCodeField
        };

        int result = JOptionPane.showConfirmDialog(this, inputs, "Enter Address Details", JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            return new Address(
                streetField.getText(),
                cityField.getText(),
                stateField.getText(),
                zipCodeField.getText()
            );
        }
        return null;
    }

    private void updateCustomerInfo() {
        String identifier = JOptionPane.showInputDialog(this, "Enter customer's email, phone number, or last name to update:");
        Person customer = customerManager.findCustomerByCriteria(identifier).orElse(null);
        if (customer != null) {
            String newPhone = JOptionPane.showInputDialog(this, "Enter new phone number:", customer.getPhoneNumber());
            String newEmail = JOptionPane.showInputDialog(this, "Enter new email:", customer.getEmail());
            customer.setPhoneNumber(newPhone);
            customer.setEmail(newEmail);
            JOptionPane.showMessageDialog(this, "Customer updated successfully.");
            refreshCustomerTable();
        } else {
            JOptionPane.showMessageDialog(this, "Customer not found.");
        }
    }

    private void findCustomer() {
        String identifier = JOptionPane.showInputDialog(this, "Enter search criteria (email, phone, last name, or pet name):");
        Person customer = customerManager.findCustomerByCriteria(identifier).orElse(null);
        if (customer != null) {
            Address address = customer.getAddress();
            String addressInfo = String.format("Address: %s, %s, %s, %s",
                address.getStreet(), address.getCity(), address.getState(), address.getZipCode());

            List<Pet> pets = customer.getPets();
            StringBuilder petInfo = new StringBuilder("Pets:\n");
            for (Pet pet : pets) {
                petInfo.append(String.format("- %s (%s, %s)\n", pet.getName(), pet.getType(), pet.getColor()));
            }

            JOptionPane.showMessageDialog(this,
                "Customer found: " + customer.getFirstName() + " " + customer.getLastName() + "\n" +
                "Phone: " + customer.getPhoneNumber() + "\n" +
                "Email: " + customer.getEmail() + "\n" +
                addressInfo + "\n" +
                petInfo.toString(),
                "Customer Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Customer not found.");
        }
    }

    private void removeCustomer() {
        String identifier = JOptionPane.showInputDialog(this, "Enter customer's email, phone number, or last name to remove:");
        boolean removed = customerManager.removeCustomerByCriteria(identifier);
        if (removed) {
            JOptionPane.showMessageDialog(this, "Customer removed successfully.");
            refreshCustomerTable();
        } else {
            JOptionPane.showMessageDialog(this, "Customer not found.");
        }
    }

    private void refreshCustomerTable() {
        tableModel.setRowCount(0);
        for (Person customer : customerManager.getAllCustomers()) {
            tableModel.addRow(new Object[]{
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhoneNumber(),
                customer.getEmail()
            });
        }
    }
}


