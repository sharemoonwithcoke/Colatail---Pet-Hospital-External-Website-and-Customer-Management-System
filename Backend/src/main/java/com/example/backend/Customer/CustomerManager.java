package com.example.backend.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomerManager {

    private List<Person> customers;

    public CustomerManager() {
        this.customers = new ArrayList<>();
    }

    // 添加客户
    public void addCustomer(Person customer) {
        this.customers.add(customer);
    }

    // 根据多个条件查找客户
    public Optional<Person> findCustomerByCriteria(String criteria) {
        return customers.stream()
                .filter(customer -> customer.getEmail().equalsIgnoreCase(criteria) ||
                                    customer.getPhoneNumber().equalsIgnoreCase(criteria) ||
                                    customer.getLastName().equalsIgnoreCase(criteria) ||
                                    customer.getPets().stream()
                                            .anyMatch(pet -> pet.getName().equalsIgnoreCase(criteria)))
                .findFirst();
    }

    // 更新客户信息
    public boolean updateCustomer(String criteria, Person updatedCustomer) {
        Optional<Person> optionalCustomer = findCustomerByCriteria(criteria);
        if (optionalCustomer.isPresent()) {
            Person customer = optionalCustomer.get();
            int index = customers.indexOf(customer);
            customers.set(index, updatedCustomer);
            return true;
        }
        return false;
    }

    // 删除客户
    public boolean removeCustomer(String criteria) {
        return customers.removeIf(customer -> customer.getEmail().equalsIgnoreCase(criteria) ||
                                              customer.getPhoneNumber().equalsIgnoreCase(criteria) ||
                                              customer.getLastName().equalsIgnoreCase(criteria) ||
                                              customer.getPets().stream()
                                                      .anyMatch(pet -> pet.getName().equalsIgnoreCase(criteria)));
    }

    // 获取所有客户
    public List<Person> getAllCustomers() {
        return new ArrayList<>(this.customers);
    }

    // 获取所有宠物
    public List<Pet> getAllPets() {
        return customers.stream()
                .flatMap(customer -> customer.getPets().stream())
                .collect(Collectors.toList());
    }

    // 获取所有人
    public List<Person> getAllPersons() {
        return new ArrayList<>(this.customers);
    }

    public boolean removeCustomerByCriteria(String criteria) {
        Optional<Person> customerToRemove = customers.stream()
            .filter(customer -> customer.getEmail().equalsIgnoreCase(criteria) ||
                                customer.getPhoneNumber().equalsIgnoreCase(criteria) ||
                                customer.getLastName().equalsIgnoreCase(criteria))
            .findFirst();
    
        if (customerToRemove.isPresent()) {
            customers.remove(customerToRemove.get());
            return true; // 表示删除成功
        } else {
            return false; // 未找到匹配客户
        }
    }
    

    // 统计某一宠物种类的数量
    public long countPetsBySpecies(String species) {
        return customers.stream()
                .flatMap(customer -> customer.getPets().stream())
                .filter(pet -> pet.getSpecies().equalsIgnoreCase(species))
                .count();
    }
}


