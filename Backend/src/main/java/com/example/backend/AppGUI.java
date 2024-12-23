package com.example.backend;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.example.backend.Appointment.AppointmentManager;
import com.example.backend.Calendar.ReminderDetailView;
import com.example.backend.Calendar.RemindersDialog;
import com.example.backend.Customer.Address;
import com.example.backend.Customer.CustomerManager;
import com.example.backend.Customer.Person;
import com.example.backend.Customer.Pet;

import com.example.backend.ReminderEmail.Reminder;
import com.example.backend.ReminderEmail.ReminderManager;
import com.example.backend.toDo.ToDoList;
import com.example.backend.toDo.ToDoListDialog;



public class AppGUI {
    // 主框架和其他GUI组件
    private JFrame frame;
    private MainPage mainPage;
    private AppointmentView appointmentView;
    private CustomerinfoView customerInfoView;

    private ToDoListDialog toDoListDialog;
    private RemindersDialog reminderDialog;
    private ReminderDetailView reminderDetailsDialog;

 

    
    // 管理类实例
    private AppointmentManager appointmentManager;
    private ReminderManager reminderManager;
    private ToDoList toDoList;
    private CustomerManager customerManager;

    public AppGUI() {
        // 初始化管理器
        this.appointmentManager = new AppointmentManager();
        this.reminderManager = new ReminderManager();
        this.toDoList = new ToDoList();
       
        this.customerManager = new CustomerManager();
        

        // 初始化视图
        initializeViews();
    }

    private void initializeViews() {
        // 创建主窗口
        frame = new JFrame("Calendar Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
    
        // 实例化 MainPage 并传递必要的管理器，如果 MainPage 需要
       mainPage = new MainPage(appointmentManager, toDoList, reminderManager, customerManager);

        frame.add(mainPage, BorderLayout.CENTER);
    
        // 初始化其他视图，但不立即显示它们
        appointmentView = new AppointmentView(frame, appointmentManager, customerManager, LocalDate.now());

        toDoListDialog = new ToDoListDialog(frame, toDoList);
        reminderDialog = new RemindersDialog(frame, reminderManager, null); // 假设这里需要额外参数
        // 注意：如果 CustomerInfoView 需要特定的 Person 对象，你可能需要在特定事件触发时实例化它
    
        // 设置并添加事件监听器
        addListeners();
    }
    
   
    
    // 显示预约视图的方法
   
    
    private void addListeners() {

        mainPage.getViewAppointmentsButton().addActionListener(e -> showAppointmentDialog());
        mainPage.getViewTodoListButton().addActionListener(e -> showToDoListDialog());
        mainPage.getViewRemindersButton().addActionListener(e -> showReminderDialog());




        //预约
        JButton openAppointmentViewButton = mainPage.getViewAppointmentsButton(); // 假设你的MainPage有这个按钮
        if (openAppointmentViewButton != null) {
            openAppointmentViewButton.addActionListener(e -> showAppointmentDialog());
        }

        //客户
        // 假设这是选择了一个Person对象的事件处理器里的代码
Person selectedPerson = getSelectedPerson(); 

JButton showCustomerInfoButton = new JButton("Show Customer Info");
showCustomerInfoButton.addActionListener(e -> showCustomerInfo(selectedPerson));

// 然后你可能需要把这个按钮添加到界面中



        // 待办事项
        JButton openTodoListButton = mainPage.getViewTodoListButton(); // 假设你的MainPage有这个按钮
        if (openTodoListButton != null) {
            openTodoListButton.addActionListener(e -> showToDoListDialog());
        }

        // 提醒

    JButton openRemindersButton = mainPage.getViewRemindersButton(); // 假设你的MainPage有这个按钮
    if (openRemindersButton != null) {
        openRemindersButton.addActionListener(e -> showReminderDialog());
    }

        //
       
    }

// 显示预约视


// 在显示预约视图
private void showAppointmentDialog() {
    if (appointmentView == null) {
        // 假设你已经有了appointmentManager实例
       // 修改 AppointmentView 的初始化
appointmentView = new AppointmentView(frame, appointmentManager, customerManager, LocalDate.now());

    }
    appointmentView.setVisible(true);
}



// 显示客户信息视图



private void showCustomerInfo(Person person) {
    CustomerinfoView customerViewDialog = new CustomerinfoView(frame, customerManager);
    customerViewDialog.setVisible(true);
}



// 显示待办列表对话框
private void showToDoListDialog() {
    if (toDoListDialog == null) {
        toDoListDialog = new ToDoListDialog(frame, toDoList);
    }
    toDoListDialog.setVisible(true);
}

// 显示提醒对话框
private void showReminderDialog() {
    if (reminderDialog == null) {
        reminderDialog = new RemindersDialog(frame, reminderManager, null);//更改！！
    }
    reminderDialog.setVisible(true);
}

// 显示提醒详情视图
private void showReminderDetails(Reminder reminder) {
    reminderDetailsDialog = new ReminderDetailView(frame, reminder);
    reminderDetailsDialog.setVisible(true);
}

   
     // Display the total number of appointments
     ///////////等下去玩
     private void showAppointmentCount() {
        int totalAppointments = appointmentManager.getAppointmentCount();
        JOptionPane.showMessageDialog(frame, "Total Appointments: " + totalAppointments);
    }
////////////to do list

   


    ///display to-do list
    private void showTodoListDialog() {
        ToDoListDialog dialog = new ToDoListDialog(frame, toDoList);
        dialog.setVisible(true);
    }
   


//添加选择客户







    // Add a new to-do item
    private void addTodoItem() {
        String description = JOptionPane.showInputDialog(frame, "Enter the description for the new to-do item:");
        if (description != null && !description.trim().isEmpty()) {
            toDoList.addTask(description.trim());
        }
    }

    

    // Remove a to-do item
    private void removeTodoItem(int index) {
        toDoList.removeTask(index);
    }

    // Display reminders
    private void showReminders() {
        // Assuming getReminders returns a List<Reminder>
        List<Reminder> reminders = reminderManager.getAllReminders();
        reminders.forEach(reminder -> {
            // Assuming Reminder has a toString that formats the reminder for display
            JOptionPane.showMessageDialog(frame, reminder.toString());
        });
    }
///add reminder
  private void addReminder() {
    // 提示用户输入提醒详情
    String note = JOptionPane.showInputDialog(frame, "Enter reminder note:");
    // 提示用户输入日期
    String dueDateString = JOptionPane.showInputDialog(frame, "Enter due date (YYYY-MM-DD):");
    LocalDate dueDate = null;
    try {
        dueDate = LocalDate.parse(dueDateString);
    } catch (DateTimeParseException e) {
        JOptionPane.showMessageDialog(frame, "Invalid date format.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
  

    // 提供选择宠物的方式
    Pet selectedPet = selectPetForReminder();
    
    if (note != null && dueDate != null && selectedPet != null) {
        reminderManager.addGeneralReminder(selectedPet, dueDate, note, 0); // 最后一个参数为提前天数，根据需要调整
        JOptionPane.showMessageDialog(frame, "Reminder added successfully.");
    }
}

private Pet selectPetForReminder() {
    List<Pet> allPets = customerManager.getAllPets(); // 获取所有宠物
    Pet[] petsArray = allPets.toArray(new Pet[0]); // 转换成Pet数组以供选择

    Pet selectedPet = (Pet) JOptionPane.showInputDialog(
            frame,
            "Choose a pet for the reminder:",
            "Select Pet",
            JOptionPane.QUESTION_MESSAGE,
            null,
            petsArray,
            petsArray[0]
    );

    return selectedPet;
}

    

//提供选择客户的方式
private Person getSelectedPerson() {
List<Person> allCustomers = customerManager.getAllCustomers(); // 获取所有客户
Person[] customersArray = allCustomers.toArray(new Person[0]); // 转换成Person数组以供选择
 
Person selectedPerson = (Person) JOptionPane.showInputDialog(
        frame,
        "Choose a customer for the reminder:",
        "Select Customer",
        JOptionPane.QUESTION_MESSAGE,
        null,
        customersArray,
        customersArray[0]
);

return selectedPerson;
}
   

    // Remove a reminder
    private void removeReminder() {
        // Assuming getReminders returns a List<Reminder>
        List<Reminder> reminders = reminderManager.getAllReminders();
        String[] reminderStrings = reminders.stream().map(Reminder::toString).toArray(String[]::new);
        String selectedReminder = (String) JOptionPane.showInputDialog(frame, "Select a reminder to remove:", "Remove Reminder",
                JOptionPane.QUESTION_MESSAGE, null, reminderStrings, reminderStrings[0]);
        if (selectedReminder != null) {
            Reminder reminder = reminders.stream().filter(r -> r.toString().equals(selectedReminder)).findFirst().orElse(null);
            if (reminder != null) {
                reminderManager.removeReminder(reminder);
                JOptionPane.showMessageDialog(frame, "Reminder removed successfully.");
            }
        }
    }


    ///////////////////////////////////////////////////////////////客户信息管理

   // 增加新客户信息
   private void addCustomer() {
    // 从用户获取客户信息
    String firstName = JOptionPane.showInputDialog(frame, "Enter customer's first name:");
    String lastName = JOptionPane.showInputDialog(frame, "Enter customer's last name:");
    String phoneNumber = JOptionPane.showInputDialog(frame, "Enter customer's phone number:");
    String email = JOptionPane.showInputDialog(frame, "Enter customer's email:");
    
    // 假设Address类有一个合适的构造器或者一个输入对话框来创建Address对象
    Address address = createAddressDialog();
    
    // 收集宠物信息
    List<Pet> pets = new ArrayList<>();
    int addMore = JOptionPane.showConfirmDialog(frame, "Do you want to add pet information?", "Add Pet", JOptionPane.YES_NO_OPTION);
    while (addMore == JOptionPane.YES_OPTION) {
        Pet pet = createPetDialog();
        if (pet != null) {
            pets.add(pet);
        }
        addMore = JOptionPane.showConfirmDialog(frame, "Do you want to add more pet information?", "Add More Pets", JOptionPane.YES_NO_OPTION);
    }
    
    // 创建并添加新的Person实例到CustomerManager
    if (email != null && !email.trim().isEmpty()) {
        Person newCustomer = new Person(firstName, lastName, phoneNumber, address, email, pets);
        customerManager.addCustomer(newCustomer);
        JOptionPane.showMessageDialog(frame, "Customer added successfully.");
    } else {
        JOptionPane.showMessageDialog(frame, "Customer addition cancelled or invalid input.");
    }
}

private Pet createPetDialog() {
    JTextField nameField = new JTextField();
    JTextField typeField = new JTextField(); // 新增类型输入字段
    JTextField speciesField = new JTextField();
    JTextField colorField = new JTextField();
    JTextField genderField = new JTextField();
    JTextField birthdayField = new JTextField("yyyy-MM-dd"); // 提示用户输入格式

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel("Name:"));
    panel.add(nameField);
    panel.add(new JLabel("Type:")); // 添加类型输入提示
    panel.add(typeField);
    panel.add(new JLabel("Species:"));
    panel.add(speciesField);
    panel.add(new JLabel("Color:"));
    panel.add(colorField);
    panel.add(new JLabel("Gender:"));
    panel.add(genderField);
    panel.add(new JLabel("Birthday (yyyy-MM-dd):"));
    panel.add(birthdayField);

    int result = JOptionPane.showConfirmDialog(frame, panel, "Enter Pet Details", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        try {
            // 解析生日
            LocalDate birthday = LocalDate.parse(birthdayField.getText());
            // 获取当前用户作为 owner
            Person owner = getCurrentCustomer(); // 假设有方法获取当前用户
            if (owner == null) {
                JOptionPane.showMessageDialog(frame, "No owner selected. Please add a customer first.");
                return null;
            }
            // 创建宠物实例
            return new Pet(
                nameField.getText(),
                typeField.getText(), // 添加类型字段
                speciesField.getText(),
                colorField.getText(),
                genderField.getText(),
                birthday,
                owner
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Invalid input: " + e.getMessage());
        }
    }
    return null;
}


private Person getCurrentCustomer() {
    // 获取用户输入，用于匹配现有客户
    String criteria = JOptionPane.showInputDialog(frame, "Enter customer's email, phone, or name to select:");

    if (criteria != null && !criteria.trim().isEmpty()) {
        // 使用 CustomerManager 查询客户
        Optional<Person> customerOptional = customerManager.findCustomerByCriteria(criteria);
        if (customerOptional.isPresent()) {
            return customerOptional.get();
        } else {
            JOptionPane.showMessageDialog(frame, "No customer found with the given criteria.");
        }
    } else {
        JOptionPane.showMessageDialog(frame, "Search criteria cannot be empty.");
    }
    return null; // 如果未找到客户，返回 null
}


private Address createAddressDialog() {
    JTextField streetField = new JTextField();
    JTextField cityField = new JTextField();
    JTextField stateField = new JTextField();
    JTextField zipCodeField = new JTextField();

    final JComponent[] inputs = new JComponent[] {
        new JLabel("Street"),
        streetField,
        new JLabel("City"),
        cityField,
        new JLabel("State"),
        stateField,
        new JLabel("Zip Code"),
        zipCodeField
    };
    
    int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Address Details", JOptionPane.PLAIN_MESSAGE);
    if (result == JOptionPane.OK_OPTION) {
        String street = streetField.getText();
        String city = cityField.getText();
        String state = stateField.getText();
        String zipCode = zipCodeField.getText();

        // 根据收集的信息创建Address对象
        return new Address(street, city, state, zipCode);
    } else {
        System.out.println("User canceled or closed the dialog.");
        return null;
    }
}

private void updateCustomerInfo() {
    String identifier = JOptionPane.showInputDialog(frame, "Enter customer's email to update:");
    
    Optional<Person> optionalCustomer = customerManager.findCustomerByCriteria(identifier);
    
    if (optionalCustomer.isPresent()) {
        // 提取出 Person 对象
        Person customer = optionalCustomer.get();

        // 更新基本信息
        String firstName = JOptionPane.showInputDialog(frame, "Enter the new first name:", customer.getFirstName());
        String lastName = JOptionPane.showInputDialog(frame, "Enter the new last name:", customer.getLastName());
        String phoneNumber = JOptionPane.showInputDialog(frame, "Enter the new phone number:", customer.getPhoneNumber());
        String email = JOptionPane.showInputDialog(frame, "Enter the new email:", customer.getEmail());

        // 更新地址信息
        Address updatedAddress = updateAddressDialog(customer.getAddress());

        // 更新宠物信息
        List<Pet> updatedPets = new ArrayList<>(customer.getPets());
        updatePetDialog(updatedPets);

        // 构造新的 Person 对象来反映更新
        Person updatedCustomer = new Person(
            firstName.isEmpty() ? customer.getFirstName() : firstName,
            lastName.isEmpty() ? customer.getLastName() : lastName,
            phoneNumber.isEmpty() ? customer.getPhoneNumber() : phoneNumber,
            updatedAddress,
            email.isEmpty() ? customer.getEmail() : email,
            updatedPets
        );

        // 替换旧客户信息
        boolean updateSuccess = customerManager.updateCustomer(identifier, updatedCustomer);
        if (updateSuccess) {
            JOptionPane.showMessageDialog(frame, "Customer updated successfully.");
        } else {
            JOptionPane.showMessageDialog(frame, "Failed to update customer.");
        }
    } else {
        JOptionPane.showMessageDialog(frame, "Customer not found.");
    }
}



//更新地址

private Address updateAddressDialog(Address currentAddress) {
    JTextField streetField = new JTextField(currentAddress.getStreet());
    JTextField cityField = new JTextField(currentAddress.getCity());
    JTextField stateField = new JTextField(currentAddress.getState());
    JTextField zipCodeField = new JTextField(currentAddress.getZipCode());

    final JComponent[] inputs = new JComponent[] {
        new JLabel("Street"),
        streetField,
        new JLabel("City"),
        cityField,
        new JLabel("State"),
        stateField,
        new JLabel("Zip Code"),
        zipCodeField
    };

    int result = JOptionPane.showConfirmDialog(null, inputs, "Update Address Details", JOptionPane.PLAIN_MESSAGE);
    if (result == JOptionPane.OK_OPTION) {
        return new Address(
            streetField.getText(),
            cityField.getText(),
            stateField.getText(),
            zipCodeField.getText()
        );
    } else {
        return currentAddress; // 如果用户取消，则返回原地址
    }
}

// 更新宠物信息
private void updatePetDialog(List<Pet> pets) {
    String[] petNames = pets.stream().map(Pet::getName).toArray(String[]::new);
    String selectedPet = (String) JOptionPane.showInputDialog(null, "Choose a pet to update", 
        "Update Pet", JOptionPane.QUESTION_MESSAGE, null, petNames, petNames[0]);

    if (selectedPet != null) {
        for (Pet pet : pets) {
            if (pet.getName().equals(selectedPet)) {
                Pet updatedPet = createPetDialog();
                if (updatedPet != null) {
                    pets.set(pets.indexOf(pet), updatedPet);
                    JOptionPane.showMessageDialog(null, "Pet updated successfully.");
                }
                return;
            }
        }
    }

    int addNew = JOptionPane.showConfirmDialog(null, "Would you like to add a new pet?", "Add Pet", JOptionPane.YES_NO_OPTION);
    if (addNew == JOptionPane.YES_OPTION) {
        Pet newPet = createPetDialog();
        if (newPet != null) pets.add(newPet);
    }

    int remove = JOptionPane.showConfirmDialog(null, "Would you like to remove a pet?", "Remove Pet", JOptionPane.YES_NO_OPTION);
    if (remove == JOptionPane.YES_OPTION) {
        String petToRemove = (String) JOptionPane.showInputDialog(null, "Choose a pet to remove", 
            "Remove Pet", JOptionPane.QUESTION_MESSAGE, null, petNames, petNames[0]);
        pets.removeIf(p -> p.getName().equals(petToRemove));
    }
}



// 查找客户
private void findCustomer() {
    String criteria = JOptionPane.showInputDialog(frame, "Enter search criteria (email, phone, last name, or pet name):");

    // 使用Optional来处理findCustomerByCriteria的返回值
    Optional<Person> optionalCustomer = customerManager.findCustomerByCriteria(criteria);

    if (optionalCustomer.isPresent()) {
        // 从Optional中获取实际的Person对象
        Person customer = optionalCustomer.get();

        // 将地址信息转换为字符串
        Address address = customer.getAddress();
        String addressInfo = String.format("Address: %s, %s, %s, %s", 
                                address.getStreet(), address.getCity(), address.getState(), address.getZipCode());

        // 将宠物名字转换为字符串数组
        String[] petNames = customer.getPets().stream().map(Pet::getName).toArray(String[]::new);

        // 显示客户信息和宠物名字列表
        JOptionPane.showMessageDialog(frame, 
            "Customer found: " + customer.getFirstName() + " " + customer.getLastName() + "\n" +
            customer.getEmail() + "\n" + customer.getPhoneNumber() + "\n" + addressInfo,
            "Customer Details", JOptionPane.INFORMATION_MESSAGE);

        // 提示用户选择一个宠物来查看详情
        if (petNames.length > 0) {
            String selectedPetName = (String) JOptionPane.showInputDialog(frame, 
                    "Select a pet to view details:", "Pet List",
                    JOptionPane.QUESTION_MESSAGE, null, 
                    petNames, petNames[0]);

            // 如果用户选择了一个宠物，展示宠物详情
            if (selectedPetName != null && !selectedPetName.isEmpty()) {
                showPetDetails(customer.getPets().stream()
                    .filter(p -> p.getName().equals(selectedPetName))
                    .findFirst()
                    .orElse(null));
            }
        }
    } else {
        // 如果Optional为空，表示未找到客户
        JOptionPane.showMessageDialog(frame, "Customer not found.");
    }
}

private void showPetDetails(Pet pet) {
    if (pet != null) {
        // 定义表格数据
        String[][] data = {
            {"Name", pet.getName()},
            {"Species", pet.getSpecies()},
            {"Color", pet.getColor()},
            {"Gender", pet.getGender()}
        };

        String[] columnNames = {"Attribute", "Value"};

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // 创建一个对话框来展示宠物详情表格
        JOptionPane.showMessageDialog(null, scrollPane, "Pet Details", JOptionPane.INFORMATION_MESSAGE);
    }
}



// 显示所有客户的数量
private void showAllCustomersCount() {
    List<Person> allCustomers = customerManager.getAllCustomers();
    JOptionPane.showMessageDialog(frame, "Total number of customers: " + allCustomers.size());
}
}