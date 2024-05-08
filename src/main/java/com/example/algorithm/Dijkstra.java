package com.example.algorithm;

import java.util.Arrays;

/**
 * Dijkstra类实现了Dijkstra算法，用于在图中寻找最短路径。
 */
public class Dijkstra {
    // 一个表示无穷大的常量，用于初始化距离。
    private static final int INF = Integer.MAX_VALUE;

    /**
     * 主方法在一个样本图上运行Dijkstra算法，并打印最短距离。
     * @param args 命令行参数（未使用）。
     */
    public static void main(String[] args) {
        // 一个2D数组，表示图的邻接矩阵。
        int[][] graph = {
                {0, 10, INF, 30, 100},
                {INF, 0, 50, INF, INF},
                {INF, INF, 0, INF, 10},
                {INF, INF, 20, 0, 60},
                {INF, INF, INF, INF, 0}
        };
        // 运行Dijkstra算法，获取从节点0开始的最短距离。
        int[] shortestDistances = dijkstra(graph, 0);
        System.out.println("从节点0开始的最短距离：");
        for (int i = 0; i < shortestDistances.length; i++) {
            System.out.println("节点 " + i + ": " + shortestDistances[i]);
        }
    }

    /**
     * 实现Dijkstra算法，用于在图中寻找最短路径。
     * @param graph 图的邻接矩阵。
     * @param source 源节点。
     * @return 从源到每个节点的最短距离的数组。
     */
    public static int[] dijkstra(int[][] graph, int source) {
        int n = graph.length;
        // 一个数组，用于存储从源到每个节点的最短距离。
        int[] dist = new int[n];
        // 一个布尔数组，用于标记已经访问和处理过的节点。
        boolean[] visited = new boolean[n];
        // 初始化所有距离为无穷大，源的距离为0。
        Arrays.fill(dist, INF);
        dist[source] = 0;

        // 找到所有节点的最短路径。
        for (int count = 0; count < n - 1; count++) {
            // 从尚未处理的节点集中选择最小距离节点。
            int u = minDistance(dist, visited);
            // 标记选定的节点为已处理。
            visited[u] = true;
            // 更新选定节点的相邻节点的距离值。
            for (int v = 0; v < n; v++) {
                // 如果节点尚未处理，且从u到v有边，且通过u从源到v的路径的总权重小于dist[v]的当前值，则更新dist[v]。
                if (!visited[v] && graph[u][v] != INF && dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                }
            }
        }

        return dist;
    }

    /**
     * 一个实用方法，用于从尚未处理的顶点集中找到具有最小距离值的顶点。
     * @param dist 距离数组。
     * @param visited 访问过的节点数组。
     * @return 具有最小距离的节点的索引。
     */
    private static int minDistance(int[] dist, boolean[] visited) {
        // 初始化最小值和索引，以表示具有最小距离的节点。
        int min = INF, minIndex = -1;
        for (int i = 0; i < dist.length; i++) {
            // 如果节点尚未访问，且其距离小于当前最小值，则更新最小值。
            if (!visited[i] && dist[i] <= min) {
                min = dist[i];
                minIndex = i;
            }
        }
        return minIndex;
    }
}