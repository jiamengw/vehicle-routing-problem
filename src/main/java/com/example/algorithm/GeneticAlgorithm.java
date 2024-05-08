package com.example.algorithm;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
    private final int populationSize;
    private final double mutationRate;
    private List<Chromosome> population;

    public static void main(String[] args) {
        int[][] initialGenes = {
                {1, 0, 1},
                {0, 1, 0},
                {1, 1, 1}
        };

        GeneticAlgorithm ga = new GeneticAlgorithm(100, 0.01, initialGenes);
        ga.evolve(100); // 进化100代
        Chromosome bestSolution = ga.getBestSolution();

        System.out.println("Best Solution:");
        for (int[] row : bestSolution.getGenes()) {
            for (int gene : row) {
                System.out.print(gene + " ");
            }
            System.out.println();
        }
        System.out.println("Fitness: " + bestSolution.getFitness());
    }


    public GeneticAlgorithm(int populationSize, double mutationRate, int[][] initialGenes) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        initializePopulation(initialGenes);
    }

    private void initializePopulation(int[][] initialGenes) {
        population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(new Chromosome(initialGenes));
        }
    }

    public void evolve(int generations) {
        for (int i = 0; i < generations; i++) {
            List<Chromosome> newPopulation = new ArrayList<>();
            // 选择父代
            Chromosome parent1 = selectParent();
            Chromosome parent2 = selectParent();
            // 交叉并产生子代
            Chromosome child = parent1.crossover(parent2);
            // 变异
            child.mutate(mutationRate);
            newPopulation.add(child);
            // 更新种群
            population = newPopulation;
        }
    }

    private Chromosome selectParent() {
        Random random = new Random();
        int totalFitness = 0;
        for (Chromosome chromosome : population) {
            totalFitness += chromosome.getFitness();
        }
        int rand = random.nextInt(totalFitness);
        int runningSum = 0;
        for (Chromosome chromosome : population) {
            runningSum += chromosome.getFitness();
            if (runningSum > rand) {
                return chromosome;
            }
        }
        return null; // This should never happen
    }

    public Chromosome getBestSolution() {
        Chromosome best = population.get(0);
        for (Chromosome chromosome : population) {
            if (chromosome.getFitness() > best.getFitness()) {
                best = chromosome;
            }
        }
        return best;
    }

    static class Chromosome {
        private final int[][] genes;
        private int fitness;

        public Chromosome(int[][] genes) {
            this.genes = genes;
            calculateFitness();
        }

        public int[][] getGenes() {
            return genes;
        }

        public int getFitness() {
            return fitness;
        }

        private void calculateFitness() {
            // 计算适应度，这里以数组中所有元素的和作为适应度
            int sum = 0;
            for (int[] row : genes) {
                for (int gene : row) {
                    sum += gene;
                }
            }
            fitness = sum;
        }

        // 交叉操作
        public Chromosome crossover(Chromosome partner) {
            int[][] childGenes = new int[genes.length][genes[0].length];
            Random random = new Random();
            for (int i = 0; i < genes.length; i++) {
                for (int j = 0; j < genes[0].length; j++) {
                    if (random.nextBoolean()) {
                        childGenes[i][j] = genes[i][j];
                    } else {
                        childGenes[i][j] = partner.getGenes()[i][j];
                    }
                }
            }
            return new Chromosome(childGenes);
        }

        // 变异操作
        public void mutate(double mutationRate) {
            Random random = new Random();
            for (int i = 0; i < genes.length; i++) {
                for (int j = 0; j < genes[0].length; j++) {
                    if (random.nextDouble() < mutationRate) {
                        genes[i][j] = 1 - genes[i][j]; // 翻转基因
                    }
                }
            }
            calculateFitness(); // 变异后重新计算适应度
        }
    }
}
