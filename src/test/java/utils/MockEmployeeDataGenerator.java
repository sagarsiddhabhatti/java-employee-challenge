package utils;


import com.example.rqchallenge.model.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MockEmployeeDataGenerator {

    private static final String[] NAMES = {
            "Tiger Nixon", "John Doe", "Jane Doe", "Alice Johnson", "Bob Brown",
            "Charlie Davis", "David Evans", "Eve Foster", "Frank Green", "Grace Hall",
            "Hannah Ives", "Isaac Jones", "Jack King", "Kathy Lewis", "Liam Miller",
            "Mia Nelson", "Noah O'Brien", "Olivia Parker", "Peter Quinn", "Quinn Reyes",
            "Rose Smith", "Steve Taylor", "Tom Underwood", "Uma Van", "Vera Wang",
            "Will Young", "Xena Zane", "Yara Adams", "Zoe Baker", "Aaron Clark",
            "Bella Davis", "Cathy Evans", "Derek Fox", "Ella Gray", "Fiona Hill",
            "George Ivy", "Holly Johnson", "Ivy King", "James Lee", "Kelly Martinez",
            "Louis Nelson", "Monica O'Neill", "Nathan Perez", "Olivia Roberts", "Paul Sanchez",
            "Quincy Turner", "Ray Underwood", "Sophia Voss", "Tina Wong", "Uma Yang"
    };

    private static final Random RANDOM = new Random();

    public static List<Employee> generateEmployees(int count) {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String id = String.valueOf(i + 1);
            String name = NAMES[RANDOM.nextInt(NAMES.length)];
            String salary = String.valueOf(RANDOM.nextInt(1000000)); // Random salary up to 1 million
            String age = String.valueOf(RANDOM.nextInt(60) + 20); // Random age between 20 and 80
            employees.add(new Employee(id, name, salary, age, ""));
        }
        return employees;
    }
}
