package com.example.backend.Controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    @PostMapping("/api/customers")
    public Customer saveCustomer(@RequestBody Customer customer) {
        // 示例：打印客户信息并返回
        System.out.println("Saved customer: " + customer);
        return customer;
    }

    static class Customer {
        public String name;
        public int age;
        public String contact;

        public Customer() {} // 必须有无参构造函数

        @Override
        public String toString() {
            return "Customer{name='" + name + "', age=" + age + ", contact='" + contact + "'}";
        }
    }
}

