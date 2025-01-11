package com.heroes_task.programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;


    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        // Проверяем, находятся ли юниты в пределах игрового поля
        if (!isWithinBounds(attackUnit.getxCoordinate(), attackUnit.getyCoordinate()) ||
                !isWithinBounds(targetUnit.getxCoordinate(), targetUnit.getyCoordinate())) {
            return Collections.emptyList();
        }

        // Множество занятых клеток
        Set<String> occupiedCells = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit.isAlive()) {
                occupiedCells.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }

        // Очередь с приоритетом для алгоритма Дейкстры
        PriorityQueue<EdgeNode> queue = new PriorityQueue<>(Comparator.comparingInt(EdgeNode::getDistance));
        Map<String, EdgeNode> nodes = new HashMap<>();

        // Инициализация начальной точки
        Edge startEdge = new Edge(attackUnit.getxCoordinate(), attackUnit.getyCoordinate());
        Edge targetEdge = new Edge(targetUnit.getxCoordinate(), targetUnit.getyCoordinate());

        EdgeNode startNode = new EdgeNode(startEdge, 0, null);
        nodes.put(edgeKey(startEdge), startNode);
        queue.add(startNode);

        // Алгоритм Дейкстры
        while (!queue.isEmpty()) {
            EdgeNode current = queue.poll();

            // Если достигли цели, строим путь
            if (current.getEdge().getX() == targetEdge.getX() && current.getEdge().getY() == targetEdge.getY()) {
                return constructPath(current);
            }

            // Обрабатываем соседние клетки
            for (int[] direction : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
                int newX = current.getEdge().getX() + direction[0];
                int newY = current.getEdge().getY() + direction[1];
                Edge neighborEdge = new Edge(newX, newY);
                String neighborKey = edgeKey(neighborEdge);

                // Проверяем границы, препятствия и уже посещенные клетки
                if (!isWithinBounds(newX, newY) || occupiedCells.contains(neighborKey)) {
                    continue;
                }

                int newDistance = current.getDistance() + 1;
                EdgeNode neighborNode = nodes.getOrDefault(neighborKey, new EdgeNode(neighborEdge, Integer.MAX_VALUE, null));

                if (newDistance < neighborNode.getDistance()) {
                    neighborNode.setDistance(newDistance);
                    neighborNode.setPrevious(current);
                    nodes.put(neighborKey, neighborNode);
                    queue.add(neighborNode);
                }
            }
        }

        // Если путь не найден
        return Collections.emptyList();
    }

    // Проверяем, находится ли точка в пределах игрового поля
    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    // Построение пути из цепочки узлов
    private List<Edge> constructPath(EdgeNode targetNode) {
        List<Edge> path = new ArrayList<>();
        EdgeNode current = targetNode;

        while (current != null) {
            path.add(current.getEdge());
            current = current.getPrevious();
        }

        Collections.reverse(path);
        return path;
    }

    // Генерация ключа для карты узлов
    private String edgeKey(Edge edge) {
        return edge.getX() + "," + edge.getY();
    }

    // Внутренний класс для хранения информации о вершинах графа
    private static class EdgeNode {
        private final Edge edge;
        private int distance;
        private EdgeNode previous;

        public EdgeNode(Edge edge, int distance, EdgeNode previous) {
            this.edge = edge;
            this.distance = distance;
            this.previous = previous;
        }

        public Edge getEdge() {
            return edge;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public EdgeNode getPrevious() {
            return previous;
        }

        public void setPrevious(EdgeNode previous) {
            this.previous = previous;
        }
    }
}
