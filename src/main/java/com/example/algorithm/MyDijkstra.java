package com.example.algorithm;

import cn.hutool.core.util.ArrayUtil;

import java.util.Arrays;
import java.util.Random;

public class MyDijkstra {
    //问题：有n个任务，需分配给m个服务器节点，每个任务有任务长度、每台服务器有各自的处理速度
    //建模：用task = [task(1), task(2), ...,] 表示任务长度，nodes = [server(1）, server(2), ...,] 表示处理速度
    //每个任务和每个节点之间对应的处理时间矩阵：timeMatrix[i][j] = task[i] / nodes[j]
    //初始化染色体：chromosome = [a(1),a(2),a(3)，...] a(n)表示将第n个任务分配个第a(n)个节点
    //设计适应的函数：timeNeed(k) = max(节点1处理时间，节点2处理时间，...,节点m处理时间)
    // timeNeed = sum(timeNeed(k)) k = 1,2,3,...,n
    // 适应度函数：adaptability = 1 / timeNeed
    // 每条染色体被挑选的概率 selectionProbability = adaptability / sum(adaptability)
    //算法：初始化种群，选择，交叉，变异，适应度评估，选择，交叉，变异，适应度评估，直到满足条件
    public static void main(String[] args) {
        // 任务
        int[] task = {2, 3, 4, 5, 6};
        //服务器节点
        int[] nodes = {4, 5};
        //每个任务和每个节点之间对应的处理时间矩阵
        double[][] timeMatrix = new double[task.length][nodes.length];
        double adaptabilityTotal = getTimeMatrix(task, nodes, timeMatrix);
        //初始化种群 种群数为5
        int[][] population = initializePopulation(task, nodes, 5);
        for (int i = 0; i < 100; i++) {
            // 交叉
            population = cross(population, timeMatrix, adaptabilityTotal);
            // 变异
            mutation(population);
            // 适应度评估
            adaptabilityTotal = getTimeMatrix(task, nodes, timeMatrix);
        }
        for (int i = 0; i < population.length; i++) {
            int[] chromosome = population[i];
            if (chromosome == null) {
                continue;
            }
            double abli = 0;
            StringBuilder sb = new StringBuilder();
            sb.append("第").append(i).append("个染色体的分配方案为：{");
            for (int j = 0; j < chromosome.length; j++) {
                abli += 1 / timeMatrix[j][chromosome[j]];
                sb.append(chromosome[j]).append(",");
            }
            sb.append("},适应度为：").append(abli / adaptabilityTotal);
            System.out.println(sb);
        }
        System.out.println("去重后-----------------");
        //删除重复种群
        for (int i = 0; i < population.length; i++) {
            for (int j = i + 1; j < population.length; j++) {
                if (Arrays.equals(population[i], population[j])) {
                    population[j] = null;
                }
            }
        }
        for (int i = 0; i < population.length; i++) {
            int[] chromosome = population[i];
            if (chromosome == null) {
                continue;
            }
            double abli = 0;
            StringBuilder sb = new StringBuilder();
            sb.append("第").append(i).append("个染色体的分配方案为：{");
            for (int j = 0; j < chromosome.length; j++) {
                abli += 1 / timeMatrix[j][chromosome[j]];
                sb.append(chromosome[j]).append(",");
            }
            sb.append("},适应度为：").append(abli / adaptabilityTotal);
            System.out.println(sb);
        }
    }

    /**
     * 变异
     *
     * @param population
     */
    private static void mutation(int[][] population) {
        for (int i = 0; i < population.length; i++) {
            Random random = new Random();
            if (random.nextDouble() < 0.01) {
                int[] chromosome = population[i];
                ArrayUtil.swap(chromosome, random.nextInt(chromosome.length), random.nextInt(chromosome.length));
            }
        }
    }

    /**
     * 交叉
     *
     * @param population
     * @param timeMatrix
     * @param adaptabilityTotal
     * @return
     */
    private static int[][] cross(int[][] population, double[][] timeMatrix, double adaptabilityTotal) {
        int[][] newPopulation = new int[population.length][population[0].length];
        for (int i = 0; i < population.length; i++) {
            int[] parent1 = selectParent(population, timeMatrix, adaptabilityTotal);
            int[] parent2 = selectParent(population, timeMatrix, adaptabilityTotal);
            int[] child = new int[parent1.length];
            Random random = new Random();
            int crossPoint = random.nextInt(parent1.length);
            for (int j = 0; j < parent1.length; j++) {
                if (j < crossPoint) {
                    child[j] = parent1[j];
                } else {
                    child[j] = parent2[j];
                }
            }
            newPopulation[i] = child;
        }
        return newPopulation;
    }

    /**
     * 选择父代
     *
     * @param population
     * @param timeMatrix
     * @param adaptabilityTotal
     * @return
     */
    private static int[] selectParent(int[][] population, double[][] timeMatrix, double adaptabilityTotal) {
        //生成一个随机数
        Random random = new Random();
        double randomNUm = random.nextDouble(1);
        for (int i = 0; i < population.length; i++) {
            int[] chromosome = population[i];
            double abli = 0;
            for (int j = 0; j < chromosome.length; j++) {
                abli += 1 / timeMatrix[j][chromosome[j]];
            }
            if ((abli / adaptabilityTotal) > randomNUm) {
                return chromosome;
            }
        }
        return population[random.nextInt(population.length)];
    }

    /**
     * 初始化种群
     *
     * @param task
     * @param nodes
     * @param k
     * @return
     */
    private static int[][] initializePopulation(int[] task, int[] nodes, int k) {
        int[][] population = new int[k][task.length];
        Random random = new Random();
        for (var i = 0; i < k; i++) {
            for (var j = 0; j < task.length; j++) {
                population[i][j] = random.nextInt(nodes.length);
            }
        }
        return population;
    }


    /**
     * 获取每个任务和每个节点之间的处理时间矩阵
     *
     * @param task
     * @param nodes
     * @param timeMatrix
     * @return
     */
    private static double getTimeMatrix(int[] task, int[] nodes, double[][] timeMatrix) {
        double adaptabilityTotal = 0;
        for (var i = 0; i < task.length; i++) {
            for (var j = 0; j < nodes.length; j++) {
                // 计算任务i在节点j上的处理时间
                double timeNeed = (double) task[i] / nodes[j];
                timeMatrix[i][j] = timeNeed;
                adaptabilityTotal += 1 / timeNeed;
            }
        }
        return adaptabilityTotal;
    }
}
