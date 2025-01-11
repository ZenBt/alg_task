package com.heroes_task.programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        // Создаем новый объект армии
        Army computerArmy = new Army();
        List<Unit> selectedUnits = new ArrayList<>();
        int currentPoints = 0;

        // Сортируем юнитов по двум ключам: эффективность атаки и здоровья к стоимости
        unitList.sort(Comparator.comparingDouble(
            unit -> -(unit.getBaseAttack() / (double) unit.getCost() + unit.getHealth() / (double) unit.getCost())
        ));

        // Идем по каждому типу юнита
        for (Unit unit : unitList) {
            int unitCount = 0;

            // Добавляем юнитов, пока это возможно
            while (unitCount < 11 && currentPoints + unit.getCost() <= maxPoints) {
                // Клонируем юнита, чтобы добавлять уникальные экземпляры
                Unit newUnit = new Unit(
                    unit.getName(),
                    unit.getUnitType(),
                    unit.getHealth(),
                    unit.getBaseAttack(),
                    unit.getCost(),
                    unit.getAttackType(),
                    new HashMap<>(unit.getAttackBonuses()),
                    new HashMap<>(unit.getDefenceBonuses()),
                    unit.getxCoordinate(),
                    unit.getyCoordinate()
                );
                selectedUnits.add(newUnit);
                currentPoints += newUnit.getCost();
                unitCount++;
            }
        }

        // Устанавливаем список юнитов и очки в армию
        computerArmy.setUnits(selectedUnits);
        computerArmy.setPoints(currentPoints);

        return computerArmy;
    }
}