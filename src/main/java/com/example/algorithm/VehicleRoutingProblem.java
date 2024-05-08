package com.example.algorithm;

import com.example.model.DistanceResult;
import com.example.model.UserVo;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class VehicleRoutingProblem {
    private static final int POPULATION_SIZE = 50; // 种群大小
    private static final double MUTATION_RATE = 0.05; // 变异率
    private static final int TOURNAMENT_SIZE = 5; // 锦标赛选择的个体数量
    private static final int MAX_GENERATIONS = 100000; // 最大迭代次数

    private static final Random random = new Random();

    public static Map<String, Object> findBestRoute(List<UserVo> list, GeoregeoApi georegeoApi) {
        Population population = new Population(list.size());
        population.initialize(list);

        int generationCount = 0;
        while (generationCount < MAX_GENERATIONS) {
            population.evolve(georegeoApi);
            generationCount++;
        }
        System.out.println("routs:" + new GsonBuilder().create().toJson(population.getRoutes()));
        population.distinct();
        Route fittestRoute = population.getFittestRoute();
        Map<String, Object> result = new HashMap<>();
        result.put("bestRoute", fittestRoute);
        result.put("routs", population.getRoutes());
        return result;
    }

    public static class Path {
        @Getter
        private final List<UserVo> nodeList;
        @Getter
        private final BigDecimal demands;
        @Getter
        private final long duration;

        Path(List<UserVo> nodeList, BigDecimal demands, long duration) {
            this.nodeList = nodeList.stream().sorted(Comparator.comparing(UserVo::getDistance)).collect(Collectors.toList());
            this.demands = demands;
            this.duration = duration;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (UserVo path : nodeList) {
                sb.append(path.getName()).append(",");
            }
            sb.append("], Demands: ").append(demands).append(", Duration: ").append(duration);
            return sb.toString();
        }
    }

    static class Route {
        private final List<Path> paths; // 路径
        @Getter
        private String routName;
        @Setter
        private double fitness; // 适应度
        // 载重

        public Route(List<Path> paths, String routName) {
            this.paths = paths;
            this.routName = routName;
        }

        public List<Path> getPaths() {
            return paths;
        }

        public double getFitness() {
            return fitness;
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
        @Getter
        private List<Route> routes;
        @Getter
        private Double totalFitness;

        public Population(int locationsCount) {
            routes = new ArrayList<>(locationsCount);
            totalFitness = 0.0;
            for (int i = 0; i < locationsCount; i++) {
                routes.add(new Route(new ArrayList<>(), "方案" + i));
            }
        }

        public void initialize(List<UserVo> list) { // 修正初始化方法参数
            for (Route route : routes) {
                List<Path> paths = new ArrayList<>();
                List<UserVo> currentPath = new ArrayList<>();
                BigDecimal currentCapacity = BigDecimal.ZERO;
                long currentTime = 0;
                double fitness = 0;
                for (UserVo user : list) {
                    BigDecimal demand = user.getDemands();
                    if (currentCapacity.add(demand).compareTo(BigDecimal.valueOf(300)) <= 0 && currentTime + user.getDuration() <= 3 * 60 * 60 * 60) {
                        currentPath.add(user);
                        currentCapacity = currentCapacity.add(demand);
                        currentTime += user.getDuration();
                        fitness += user.getDistance().doubleValue();
                    } else {
                        paths.add(new Path(new ArrayList<>(currentPath), currentCapacity, currentTime));
                        currentPath.clear();
                        currentCapacity = BigDecimal.ZERO;
                        currentTime = 0;
                    }
                }
                if (!currentPath.isEmpty()) {
                    paths.add(new Path(new ArrayList<>(currentPath), currentCapacity, currentTime));
                }
                route.setFitness(1 / fitness);
                route.getPaths().addAll(paths);
            }
            totalFitness = routes.stream().map(Route::getFitness).reduce(Double::sum).orElse(0.0);
        }

        public void evolve(GeoregeoApi georegeoApi) {
            for (Route route : routes) {
                //父本
                Route maleParent = selectParent();
                //母本
                Route femaleParent = selectParent();
                Route child = crossover(maleParent, femaleParent);
                mutate(route);
                evaluateRoute(route, georegeoApi);
                route = child;
            }
            totalFitness = routes.stream().map(Route::getFitness).reduce(Double::sum).orElse(0.0);
        }

        private Route selectParent() {
            List<Route> tournament = new ArrayList<>();
            double r = random.nextDouble();
            for (int i = 0; i < TOURNAMENT_SIZE; i++) {
                if (r < routes.get(i).getFitness() / totalFitness) {
                    tournament.add(routes.get(i));
                }
            }
            if (tournament.isEmpty()) {
                tournament.add(routes.get(random.nextInt(routes.size())));
            }
            return tournament.stream().min(Comparator.comparingDouble(Route::getFitness)).get();
        }

        public Route crossover(Route maleParent, Route femaleParent) {
            Random random = new Random();
            List<Path> maleParentPaths = maleParent.getPaths();
            List<Path> femaleParentPaths = femaleParent.getPaths();
            int crossoverRow = random.nextInt(Math.min(maleParentPaths.size(), femaleParentPaths.size()));
            int crossoverColumn = random.nextInt(Math.min(maleParentPaths.get(0).getNodeList().size(), femaleParentPaths.get(0).getNodeList().size()));

            List<Path> childGenes = new ArrayList<>();
            for (int i = 0; i < maleParentPaths.size(); i++) {
                List<UserVo> nodeList = new ArrayList<>();
                BigDecimal demands = BigDecimal.ZERO;
                long duration = 0;
                if (i < crossoverRow) {
                    nodeList.addAll(maleParentPaths.get(i).getNodeList());
                    demands = maleParentPaths.get(i).getDemands();
                    duration = maleParentPaths.get(i).getDuration();
                } else if (i == crossoverRow) {
                    for (int j = 0; j < Math.max(maleParentPaths.get(i).getNodeList().size(), femaleParentPaths.get(i).getNodeList().size()); j++) {
                        if (j < crossoverColumn) {
                            if (j < maleParentPaths.get(i).getNodeList().size()) {
                                UserVo userVo = maleParentPaths.get(i).getNodeList().get(j);
                                nodeList.add(userVo);
                                demands = demands.add(userVo.getDemands());
                                duration += userVo.getDuration();
                            } else {
                                UserVo userVo = femaleParentPaths.get(i).getNodeList().get(j);
                                nodeList.add(userVo);
                                demands = demands.add(userVo.getDemands());
                                duration += userVo.getDuration();
                            }
                        } else {
                            if (j < femaleParentPaths.get(i).getNodeList().size()) {
                                UserVo userVo = femaleParentPaths.get(i).getNodeList().get(j);
                                nodeList.add(userVo);
                                demands = demands.add(userVo.getDemands());
                                duration += userVo.getDuration();
                            } else {
                                UserVo userVo = maleParentPaths.get(i).getNodeList().get(j);
                                nodeList.add(userVo);
                                demands = demands.add(userVo.getDemands());
                                duration += userVo.getDuration();
                            }
                        }
                    }
                } else {
                    nodeList.addAll(femaleParentPaths.get(i).getNodeList());
                    demands = femaleParentPaths.get(i).getDemands();
                    duration = femaleParentPaths.get(i).getDuration();
                }
                childGenes.add(new Path(nodeList, demands, duration));
            }
            return new Route(childGenes, "方案" + random.nextInt(100));
        }

        /**
         * 变异操作
         *
         * @param route 路线
         */
        private void mutate(Route route) {
            for (Path path : route.getPaths()) {
                List<UserVo> nodeList = path.getNodeList();
                if (Math.random() < MUTATION_RATE) {
                    int index1 = random.nextInt(nodeList.size());
                    int index2 = random.nextInt(nodeList.size());
                    Collections.swap(nodeList, index1, index2);
                }
            }
        }

        private void evaluateRoute(Route route, GeoregeoApi georegeoApi) {
            double fitness = 0;
            for (Path path : route.getPaths()) {
                List<UserVo> nodeList = path.getNodeList();
                fitness += nodeList.get(0).getDistance().doubleValue();
                String ori = nodeList.get(0).getLongitude() + "," + nodeList.get(0).getLatitude();
                for (UserVo userVo : nodeList) {
                    String destination = userVo.getLongitude() + "," + userVo.getLatitude();
                    DistanceResult distance = georegeoApi.distance(ori, destination);
                    fitness += Double.parseDouble(distance.getDistance());
                    ori = destination;
                }
            }
            route.setFitness(1 / fitness);
        }

        public Route getFittestRoute() {
            return routes.stream().min(Comparator.comparingDouble(Route::getFitness)).get();
        }

        public void distinct() {
            routes = routes.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Route::getFitness))), ArrayList::new));
        }
    }
}
