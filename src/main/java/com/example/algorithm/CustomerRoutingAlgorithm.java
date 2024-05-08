package com.example.algorithm;

import java.util.*;

/**
 * CustomerRoutingAlgorithm类实现了遗传算法，用于在图中寻找满足所有客户需求的最短路径。
 */
public class CustomerRoutingAlgorithm {
    // 遗传算法的参数
    private static final int POPULATION_SIZE = 50;
    private static final double MUTATION_RATE = 0.01;
    private static final int TOURNAMENT_SIZE = 5;
    private static final int MAX_GENERATIONS = 1000;

    private static final Random random = new Random();

    /**
     * 主方法在一个样本图上运行遗传算法，并打印最短路径。
     *
     * @param args 命令行参数（未使用）。
     */
    public static void main(String[] args) {
        // 客户的位置和需求量
        int[][] locations = {
                {0, 0},  // 客户0的位置
                {1, 2},  // 客户1的位置
                {3, 1},  // 客户2的位置
                {2, 3},  // 客户3的位置
                {4, 4}   // 客户4的位置
        };
        int[] demands = {0, 10, 5, 8, 6}; // 客户的需求量

        // 调用findBestRoute方法，传入客户位置和需求量，找到最佳路线。
        // findBestRoute方法会使用遗传算法来寻找满足所有客户需求的最短路线。
        // 该方法返回一个Route对象，表示最佳路线。
        Route bestRoute = findBestRoute(locations, demands);
        System.out.println("Best Route: " + bestRoute);
    }

    /**
     * 使用遗传算法找到满足所有客户需求的最短路径。
     *
     * @param locations 客户的位置。
     * @param demands   客户的需求量。
     * @return 最佳路线。
     */
    public static Route findBestRoute(int[][] locations, int[] demands) {
        Population population = new Population(POPULATION_SIZE, locations.length);
        population.initialize(locations);

        int generationCount = 0;
        while (generationCount < MAX_GENERATIONS) {
            population.evolve(demands, locations);
            generationCount++;
        }

        return population.getFittestRoute();
    }

    /**
     * Route类表示一条路线。
     * 路线由一系列的位置组成，每个位置对应一个客户。
     * 路线的适应度表示该路线的优劣，适应度越高，路线越好。
     */
    static class Route {
        private final int[] path;
        /**
         * 适应度
         */
        private double fitness;

        public Route(int[] path) {
            this.path = path;
        }

        public int[] getPath() {
            return path;
        }

        public double getFitness() {
            return fitness;
        }

        public void setFitness(double fitness) {
            this.fitness = fitness;
        }

        @Override
        public String toString() {
            return Arrays.toString(path) + ", Fitness: " + fitness;
        }
    }

    /**
     * Population类表示一群路线。
     * 遗传算法通过对这个群体进行选择、交叉和变异操作，来生成新的群体。
     * 这个过程模拟了自然界的进化过程。
     */
    static class Population {
        private Route[] routes;

        /**
         * 构造函数，初始化一群路线。
         *
         * @param populationSize 人口数量。
         * @param locationsCount 位置数量。
         */
        public Population(int populationSize, int locationsCount) {
            routes = new Route[populationSize];
            for (int i = 0; i < populationSize; i++) {
                routes[i] = new Route(new int[locationsCount]);
            }
        }

        /**
         * 初始化路线。
         *
         * @param locations 客户的位置。
         */
        public void initialize(int[][] locations) {
            for (Route route : routes) {
                route.setFitness(0);
                ArrayList<Integer> indexes = new ArrayList<>();
                for (int i = 1; i < locations.length; i++) {
                    indexes.add(i);
                }
                Collections.shuffle(indexes);
                for (int i = 1; i < locations.length; i++) {
                    route.getPath()[i] = indexes.get(i - 1);
                }
            }
        }

        /**
         * 进化一代。
         *
         * @param demands   客户的需求量。
         * @param locations 客户的位置。
         */
        public void evolve(int[] demands, int[][] locations) {
            Route[] newRoutes = new Route[routes.length];
            for (int i = 0; i < routes.length; i++) {
                Route parent1 = selectParent();
                Route parent2 = selectParent();
                Route child = crossover(parent1, parent2);
                mutate(child);
                evaluateRoute(child, demands, locations);
                newRoutes[i] = child;
            }
            routes = newRoutes;
        }

        /**
         * 选择一个父代。
         *
         * @return 选择的父代。
         */
        private Route selectParent() {
            Route[] tournament = new Route[TOURNAMENT_SIZE];
            for (int i = 0; i < TOURNAMENT_SIZE; i++) {
                tournament[i] = routes[random.nextInt(routes.length)];
            }
            Arrays.sort(tournament, Comparator.comparingDouble(Route::getFitness).reversed());
            return tournament[0];
        }

        /**
         * 交叉两个父代，生成一个子代。
         *
         * @param parent1 父代1。
         * @param parent2 父代2。
         * @return 生成的子代。
         */
        private Route crossover(Route parent1, Route parent2) {
            int[] childPath = new int[parent1.getPath().length];
            int startPos = random.nextInt(parent1.getPath().length);
            int endPos = random.nextInt(parent1.getPath().length);

            for (int i = 0; i < childPath.length; i++) {
                if (i > startPos && i < endPos) {
                    childPath[i] = parent1.getPath()[i];
                } else if (startPos > endPos && !(i < startPos && i > endPos)) {
                    childPath[i] = parent1.getPath()[i];
                }
            }

            for (int i = 0; i < parent2.getPath().length; i++) {
                if (!containsLocation(childPath, parent2.getPath()[i])) {
                    for (int j = 0; j < childPath.length; j++) {
                        if (childPath[j] == 0) {
                            childPath[j] = parent2.getPath()[i];
                            break;
                        }
                    }
                }
            }

            return new Route(childPath);
        }

        /**
         * 检查路径中是否包含某个位置。
         *
         * @param path     路径。
         * @param location 位置。
         * @return 路径中是否包含该位置。
         */
        private boolean containsLocation(int[] path, int location) {
            for (int value : path) {
                if (value == location) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 变异路线。
         *
         * @param route 路线。
         */
        private void mutate(Route route) {
            for (int i = 1; i < route.getPath().length; i++) {
                if (Math.random() < MUTATION_RATE) {
                    int index1 = random.nextInt(route.getPath().length - 1) + 1;
                    int index2 = random.nextInt(route.getPath().length - 1) + 1;
                    int temp = route.getPath()[index1];
                    route.getPath()[index1] = route.getPath()[index2];
                    route.getPath()[index2] = temp;
                }
            }
        }

        /**
         * 评估路线的适应度。
         *
         * @param route     路线。
         * @param demands   客户的需求量。
         * @param locations 客户的位置。
         */
        private void evaluateRoute(Route route, int[] demands, int[][] locations) {
            double fitness = 0;
            for (int i = 0; i < route.getPath().length - 1; i++) {
                fitness += distance(locations[route.getPath()[i]], locations[route.getPath()[i + 1]]);
            }
            route.setFitness(1 / fitness);
            // 检查货车容量
            int capacity = 0;
            for (int i = 0; i < route.getPath().length; i++) {
                capacity += demands[route.getPath()[i]];
            }
            if (capacity > 100) { // 假设货车最大容量为100
                route.setFitness(0);
            }
        }


        /**
         * 计算两个位置之间的距离。
         *
         * @param loc1 位置1。
         * @param loc2 位置2。
         * @return 位置1和位置2之间的距离。
         */
        private double distance(int[] loc1, int[] loc2) {
            return Math.sqrt(Math.pow(loc2[0] - loc1[0], 2) + Math.pow(loc2[1] - loc1[1], 2));
        }

        /**
         * 获取适应度最高的路线。
         *
         * @return 适应度最高的路线。
         */
        public Route getFittestRoute() {
            Route fittest = routes[0];
            for (Route route : routes) {
                if (route.getFitness() > fittest.getFitness()) {
                    fittest = route;
                }
            }
            return fittest;
        }
    }
}