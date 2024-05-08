package com.example;

import java.util.*;

public class VehicleRoutingProblem {
    private static final int POPULATION_SIZE = 50; // 种群大小
    private static final double MUTATION_RATE = 0.01; // 变异率
    private static final int TOURNAMENT_SIZE = 5; // 锦标赛选择的个体数量
    private static final int MAX_GENERATIONS = 10000; // 最大迭代次数

    private static Random random = new Random(); // 随机数生成器

    public static void main(String[] args) {
        int numCustomers = 50;
        int[][] locations = generateLocations(numCustomers); // 生成50个客户的随机位置
        int[] demands = generateDemands(numCustomers); // 生成50个客户的随机需求
        for (int i = 0; i < locations.length; i++) {
            System.out.println("Location " + i + ": " + Arrays.toString(locations[i]) + ",Demand: " + demands[i]);
        }
        Route bestRoute = findBestRoute(locations, demands);
        System.out.println("Best Route: ");
        System.out.println(bestRoute);
    }

    public static Route findBestRoute(int[][] locations, int[] demands) {
        Population population = new Population(POPULATION_SIZE, locations.length);
        population.initialize(locations, demands);

        int generationCount = 0;
        while (generationCount < MAX_GENERATIONS) {
            population.evolve(locations, demands);
            generationCount++;
        }

        return population.getFittestRoute();
    }

    static class Patch {
        private final List<Integer> paths;
        private double demands;

        Patch(List<Integer> paths, double demands) {
            this.paths = paths;
            this.demands = demands;
        }

        public List<Integer> getPaths() {
            return paths;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (Integer path : paths) {
                sb.append(path).append(",");
            }
            sb.append("], Demands: ").append(demands);
            return sb.toString();
        }
    }

    static class Route {
        private final List<Patch> paths; // 路径
        private double fitness; // 适应度
        // 载重

        public Route(List<Patch> paths) {
            this.paths = paths;
        }

        public List<Patch> getPaths() {
            return paths;
        }

        public double getFitness() {
            return fitness;
        }

        public void setFitness(double fitness) {
            this.fitness = fitness;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < paths.size(); i++) {
                sb.append("Truck ").append(i + 1).append(": ").append(paths.get(i).toString()).append("\n");
            }
            sb.append("Fitness: ").append(fitness);
            return sb.toString();
        }
    }

    static class Population {
        private Route[] routes;

        public Population(int populationSize, int locationsCount) {
            routes = new Route[populationSize];
            for (int i = 0; i < populationSize; i++) {
                routes[i] = new Route(new ArrayList<>());
            }
        }

        public void initialize(int[][] locations, int[] demands) { // 修正初始化方法参数
            for (Route route : routes) {
                route.setFitness(0);
                List<Patch> paths = new ArrayList<>();
                List<Integer> currentPath = new ArrayList<>();
                int currentCapacity = 0;
                int currentTime = 0;
                for (int i = 1; i < locations.length; i++) {
                    int customer = i;
                    int demand = demands[customer];
                    if (currentCapacity + demand <= 100 && currentTime + distance(locations[0], locations[customer]) <= 8 * 60) {
                        currentPath.add(customer);
                        currentCapacity += demand;
                        currentTime += (int) distance(locations[0], locations[customer]);
                    } else {
                        paths.add(new Patch(new ArrayList<>(currentPath), currentCapacity));
                        currentPath.clear();
                        currentCapacity = 0;
                        currentTime = 0;
                    }
                }
                if (!currentPath.isEmpty()) {
                    paths.add(new Patch(new ArrayList<>(currentPath), currentCapacity));
                }
                route.getPaths().addAll(paths);
            }
        }

        public void evolve(int[][] locations, int[] demands) {
            Route[] newRoutes = new Route[routes.length];
            for (int i = 0; i < routes.length; i++) {
                Route parent1 = selectParent();
                Route parent2 = selectParent();
                Route child = crossover(parent1, parent2, demands);
                mutate(child);
                evaluateRoute(child, locations);
                newRoutes[i] = child;
            }
            routes = newRoutes;
        }

        private Route selectParent() {
            Route[] tournament = new Route[TOURNAMENT_SIZE];
            for (int i = 0; i < TOURNAMENT_SIZE; i++) {
                tournament[i] = routes[random.nextInt(routes.length)];
            }
            Arrays.sort(tournament, Comparator.comparingDouble(Route::getFitness).reversed());
            return tournament[0];
        }

        private Route crossover(Route parent1, Route parent2, int[] demands) {
            List<Patch> childPaths = new ArrayList<>();
            for (int i = 0; i < parent1.getPaths().size(); i++) {//确定几条线路
                List<Integer> path = new ArrayList<>();
                Set<Integer> visited = new HashSet<>();
                Patch patch1 = parent1.getPaths().get(i);
                double demand = 0;
                for (int customer : patch1.getPaths()) {
                    path.add(customer);
                    visited.add(customer);
                    demand += demands[customer];
                }
                Patch patch2 = parent2.getPaths().get(i);
                for (int customer : patch2.getPaths()) {
                    if (!visited.contains(customer)) {
                        path.add(customer);
                        visited.add(customer);
                        demand += demands[customer];
                    }
                }
                childPaths.add(new Patch(path, demand));
            }
            return new Route(childPaths);
        }

        /**
         * 变异操作
         *
         * @param route 路线
         */
        private void mutate(Route route) {
            for (Patch path : route.getPaths()) {
                List<Integer> paths = path.getPaths();
                if (Math.random() < MUTATION_RATE) {
                    int index1 = random.nextInt(paths.size());
                    int index2 = random.nextInt(paths.size());
                    Collections.swap(paths, index1, index2);
                }
            }
        }

        private void evaluateRoute(Route route, int[][] locations) {
            double fitness = 0;
            for (Patch path : route.getPaths()) {
                List<Integer> paths = path.getPaths();
                for (int i = 0; i < paths.size() - 1; i++) {
                    fitness += distance(locations[paths.get(i)], locations[paths.get(i + 1)]);
                }
            }
            route.setFitness(fitness);
        }

        /**
         * 计算两个客户之间的距离
         *
         * @param loc1 客户1的位置
         * @param loc2 客户2的位置
         * @return 两个客户之间的距离
         */
        private double distance(int[] loc1, int[] loc2) {
            return Math.sqrt(Math.pow(loc2[0] - loc1[0], 2) + Math.pow(loc2[1] - loc1[1], 2));
        }

        public Route getFittestRoute() {
            Route fittest = routes[0];
            for (Route route : routes) {
                if (route.getFitness() < fittest.getFitness()) {
                    fittest = route;
                }
            }
            return fittest;
        }
    }

    // 生成随机客户位置的方法
    private static int[][] generateLocations(int numCustomers) {
        int[][] locations = new int[numCustomers + 1][2]; // 包括仓库
        Random random = new Random();
        for (int i = 1; i <= numCustomers; i++) {
//            locations[i][0] = random.nextInt(100); // X坐标随机生成在0到99之间
//            locations[i][1] = random.nextInt(100); // Y坐标随机生成在0到99之间
            locations[i][0] = i % 100; // X坐标随机生成在0到99之间
            locations[i][1] = i % 100; // Y坐标随机生成在0到99之间
        }
        return locations;
    }

    // 生成随机客户需求的方法
    private static int[] generateDemands(int numCustomers) {
        int[] demands = new int[numCustomers + 1]; // 包括仓库
        Random random = new Random();
        for (int i = 1; i <= numCustomers; i++) {
//            demands[i] = random.nextInt(20) + 1; // 随机生成需求量在1到20之间
            demands[i] = i % 50; // 随机生成需求量在1到20之间
        }
        return demands;
    }
}
