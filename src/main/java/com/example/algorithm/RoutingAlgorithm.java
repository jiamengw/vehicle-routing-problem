package com.example.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Customer {
    int id;
    int demand;
    int startTime;
    int endTime;

    public Customer(int id, int demand, int startTime, int endTime) {
        this.id = id;
        this.demand = demand;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

class Truck {
    int capacity;

    public Truck(int capacity) {
        this.capacity = capacity;
    }
}

public class RoutingAlgorithm {
    public static List<Integer> customerRouting(List<Customer> customers, Truck truck) {
        List<Integer> route = new ArrayList<>();
        int currentCapacity = truck.capacity;
        int currentTime = 0;

        PriorityQueue<Customer> customerQueue = new PriorityQueue<>(Comparator.comparingInt(c -> c.startTime));

        customerQueue.addAll(customers);

        while (!customerQueue.isEmpty()) {
            Customer currentCustomer = customerQueue.poll();

            if (currentCustomer.startTime > currentTime) {
                currentTime = currentCustomer.startTime;
            }

            if (currentCustomer.endTime < currentTime) {
                continue;
            }

            if (currentCustomer.demand <= currentCapacity) {
                route.add(currentCustomer.id);
                currentCapacity -= currentCustomer.demand;
                currentTime += 1; // Assuming 1 unit of time to deliver
            }

            if (currentCapacity == 0) {
                break;
            }
        }

        return route;
    }

    public static void main(String[] args) {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer(1, 5, 0, 10));
        customers.add(new Customer(2, 3, 2, 8));
        customers.add(new Customer(3, 4, 1, 7));
        customers.add(new Customer(4, 2, 5, 12));

        Truck truck = new Truck(10);

        List<Integer> route = customerRouting(customers, truck);

        System.out.println("Optimal route: " + route);
    }
}
