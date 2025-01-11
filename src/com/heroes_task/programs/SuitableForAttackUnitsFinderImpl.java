package com.heroes_task.programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();
        // Для каждой строки в unitsByRow
        for (List<Unit> row : unitsByRow) {
            // Найти минимальную или максимальную координату Y в зависимости от isLeftArmyTarget
            if (isLeftArmyTarget) {
                // Если атакуется левая армия (компьютер), ищем минимальную координату Y
                Unit targetUnit = row.stream()
                        .filter(Unit::isAlive)
                        .min(Comparator.comparingInt(Unit::getyCoordinate))
                        .orElse(null);
                if (targetUnit != null) {
                    suitableUnits.add(targetUnit);
                }
            } else {
                // Если атакуется правая армия (игрок), ищем максимальную координату Y
                Unit targetUnit = row.stream()
                        .filter(Unit::isAlive)
                        .max(Comparator.comparingInt(Unit::getyCoordinate))
                        .orElse(null);
                if (targetUnit != null) {
                    suitableUnits.add(targetUnit);
                }
            }
        }

        return suitableUnits;
    }
}

