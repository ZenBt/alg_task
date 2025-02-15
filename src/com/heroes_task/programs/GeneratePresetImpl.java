package com.heroes_task.programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

// Сортировка списка `unitList` (линия `unitList.sort`) имеет временную сложность O(n * log(n)), где `n`количество юнитов в списке.
// Вложенный цикл:
//    Внешний цикл итерирует по `unitList`максимум n итераций.
//    Внутренний цикл добавляет юнитов, пока не достигнут лимит очков `maxPoints` (в худшем случае до 11 итераций для одного юнита, так как это ограничено `unitCount < 11`)

// Лучший случай (если простые критерии): O(n * log(n) + n).
// Худший случай: O(n * log(n) + n * 11 * (WIDTH * HEIGHT)) ≈ O(n * WIDTH * HEIGHT) в случае большого количества юнитов и сильно ограниченного пространства.
public class GeneratePresetImpl implements GeneratePreset {

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        System.out.println("start generating");
        // Создаем новый объект армии
        Army computerArmy = new Army();
        List<Unit> selectedUnits = new ArrayList<>();
        int currentPoints = 0;

        // Размер игрового поля
        final int WIDTH = 3;
        final int HEIGHT = 21;

        // Множество занятых координат
        Set<String> occupiedCoordinates = new HashSet<>();

        // Сортируем юнитов по двум ключам: эффективность атаки и здоровья к стоимости
        unitList.sort(Comparator.comparingDouble(
                unit -> -(unit.getBaseAttack() / (double) unit.getCost()
                        + unit.getHealth() / (double) unit.getCost())));

        Random random = new Random();

        // Идем по каждому типу юнита
        for (Unit unit : unitList) {
            int unitCount = 0;

            // Добавляем юнитов, пока это возможно
            while (unitCount < 11 && currentPoints + unit.getCost() <= maxPoints) {
                // Генерация уникальной случайной координаты
                int x, y;
                String coordinateKey;
                do {
                    x = random.nextInt(WIDTH);
                    y = random.nextInt(HEIGHT);
                    coordinateKey = x + "_" + y; // Ключ для проверки уникальности
                } while (occupiedCoordinates.contains(coordinateKey));

                // Помечаем координату как занятую
                occupiedCoordinates.add(coordinateKey);

                // Клонируем юнита с новой уникальной координатой
                Unit newUnit = new Unit(
                        unit.getUnitType() + " " + unitCount,
                        unit.getUnitType(),
                        unit.getHealth(),
                        unit.getBaseAttack(),
                        unit.getCost(),
                        unit.getAttackType(),
                        new HashMap<>(unit.getAttackBonuses()),
                        new HashMap<>(unit.getDefenceBonuses()),
                        x,
                        y);

                System.out.println(newUnit.getName() + " x:" + newUnit.getxCoordinate() + " y:" +
                        newUnit.getyCoordinate());
                selectedUnits.add(newUnit);
                currentPoints += newUnit.getCost();
                unitCount++;
            }
        }

        // Устанавливаем список юнитов и очки в армию
        computerArmy.setUnits(selectedUnits);
        computerArmy.setPoints(currentPoints);
        System.out.println("finish generating");
        return computerArmy;
    }
}