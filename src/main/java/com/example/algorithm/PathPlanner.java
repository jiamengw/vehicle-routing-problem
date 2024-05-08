package com.example.algorithm;

import java.util.*;

class Node implements Comparable<Node> {
    /**
     * 位置
     */
    public String location;
    /**
     * 起点距离
     */
    public double distanceFromStart;
    /**
     * 估计距离
     */
    public double estimatedDistanceToEnd;
    /**
     * 父节点
     */
    public Node parent;
    /**
     * 载重
     */
    public double currentLoad;
    /**
     * 送货时间
     */
    public double deliveryTime;

    @Override
    public int compareTo(Node other) {
        return Double.compare(this.distanceFromStart + this.estimatedDistanceToEnd,
                other.distanceFromStart + other.estimatedDistanceToEnd);
    }
}

public class PathPlanner {
    /**
     * 路径
     */
    private final Map<String, List<Node>> graph;
    /**
     * 最大载重
     */
    private final double maxLoad;

    public PathPlanner(Map<String, List<Node>> graph, double maxLoad) {
        this.graph = graph;
        this.maxLoad = maxLoad;
    }

    public List<Node> findPath(String start, String end) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Node startNode = new Node();
        startNode.location = start;
        startNode.distanceFromStart = 0;
        startNode.estimatedDistanceToEnd = estimateDistance(start, end);
        startNode.currentLoad = 0;
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.location.equals(end)) {
                return reconstructPath(current);
            }

            for (Node neighbor : graph.get(current.location)) {
                double newDistance = current.distanceFromStart + calculateDistance(current, neighbor);
                double newLoad = current.currentLoad + calculateLoad(neighbor);

                if (newLoad > maxLoad || neighbor.deliveryTime < newDistance) {
                    continue;
                }

                if (newDistance < neighbor.distanceFromStart) {
                    neighbor.distanceFromStart = newDistance;
                    neighbor.parent = current;
                    queue.add(neighbor);
                }
            }
        }

        throw new RuntimeException("No path found");
    }

    private List<Node> reconstructPath(Node endNode) {
        List<Node> path = new ArrayList<>();
        Node current = endNode;
        while (current != null) {
            path.add(current);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * 估计距离
     *
     * @param start 起点
     * @param end   终点
     * @return 估计距离
     */
    private double estimateDistance(String start, String end) {
        // Implement your heuristic here
        return 0;
    }

    /**
     * 计算距离
     *
     * @param current  当前节点
     * @param neighbor 邻居节点
     * @return 距离
     */
    private double calculateDistance(Node current, Node neighbor) {
        // Implement your distance calculation here
        return 0;
    }

    /**
     * 计算载重
     *
     * @param neighbor 邻居节点
     * @return 载重
     */
    private double calculateLoad(Node neighbor) {
        // Implement your load calculation here
        return 0;
    }
}