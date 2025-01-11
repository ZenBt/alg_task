package com.heroes_task.programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

// На каждом узле мы храним расстояние. Теоретически, количество узлов равно числу клеток на поле: width*height
// Инициализация занимает O(width*height)
// Каждый узел добавляется в очередь с приоритетом максимум один раз
// Очередь с приоритетом (PriorityQueue) использует вставку и извлечение с логарифмической сложностью: O(log(число узлов в очереди)).
// Каждый узел имеет максимум 4 соседей (по направлению DIRECTIONS).
// Проверка соседей добавляет O(1) операций для каждого соседа.
// Общая сложность O(width*height * log(width*height))

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        Set<String> occupiedCells = getOccupiedCells(existingUnitList, attackUnit, targetUnit);

        // Карта расстояний
        Map<Node, Integer> distances = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        // Инициализируем стартовый узел
        Node startNode = new Node(attackUnit.getxCoordinate(), attackUnit.getyCoordinate());
        distances.put(startNode, 0);
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Если достигли целевой точки, прерываем цикл
            if (current.getX() == targetUnit.getxCoordinate() && current.getY() == targetUnit.getyCoordinate()) {
                break;
            }

            for (int[] dir : DIRECTIONS) {
                int neighborX = current.getX() + dir[0];
                int neighborY = current.getY() + dir[1];
                Node neighbor = new Node(neighborX, neighborY);

                if (isValid(neighborX, neighborY, occupiedCells)) {
                    int newDistance = distances.getOrDefault(current, Integer.MAX_VALUE) + 1;
                    if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                        distances.put(neighbor, newDistance);
                        previous.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }

        return constructPath(previous, attackUnit, targetUnit);
    }

    private Set<String> getOccupiedCells(List<Unit> existingUnitList, Unit attackUnit, Unit targetUnit) {
        Set<String> occupiedCells = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit.isAlive() && unit != attackUnit && unit != targetUnit) {
                occupiedCells.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }
        return occupiedCells;
    }

    private boolean isValid(int x, int y, Set<String> occupiedCells) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && !occupiedCells.contains(x + "," + y);
    }

    private List<Edge> constructPath(Map<Node, Node> previous, Unit attackUnit, Unit targetUnit) {
        List<Edge> path = new ArrayList<>();
        Node targetNode = new Node(targetUnit.getxCoordinate(), targetUnit.getyCoordinate());
        Node current = targetNode;

        while (current != null) {
            path.add(new Edge(current.getX(), current.getY()));
            current = previous.get(current);
        }

        Collections.reverse(path);
        return path;
    }

    private static class Node {
        private final int x;
        private final int y;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}