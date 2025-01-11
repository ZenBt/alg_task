package com.heroes_task.programs;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;


// Проверка живых юнитов: O(P).
// Сортировка юнитов: O(NlogN+MlogM).
// Симуляция раунда: O(P)
// Удаление мертвых юнитов: O(P)

// Итого общая сложность O(P*P+P*logP)

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog; // Позволяет логировать. Использовать после каждой атаки юнита

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        // Получаем юниты обеих армий
        List<Unit> playerUnits = playerArmy.getUnits();
        List<Unit> computerUnits = computerArmy.getUnits();

        // Пока есть живые юниты в обеих армиях
        while (hasAliveUnits(playerUnits) && hasAliveUnits(computerUnits)) {
            // Сортируем юниты по убыванию атаки
            sortUnitsByAttack(playerUnits);
            sortUnitsByAttack(computerUnits);

            // Симулируем раунд
            simulateRound(playerUnits, computerUnits);

            // Удаляем мертвых юнитов
            removeDeadUnits(playerUnits);
            removeDeadUnits(computerUnits);
        }

        // Проверяем результат боя
        if (hasAliveUnits(playerUnits)) {
            System.out.println("Player wins!");
        } else if (hasAliveUnits(computerUnits)) {
            System.out.println("Computer wins!");
        } else {
            System.out.println("It's a draw!");
        }
    }

    private boolean hasAliveUnits(List<Unit> units) {
        return units.stream().anyMatch(Unit::isAlive);
    }

    private void sortUnitsByAttack(List<Unit> units) {
        units.sort((u1, u2) -> Integer.compare(u2.getBaseAttack(), u1.getBaseAttack()));
    }

    private void simulateRound(List<Unit> playerUnits, List<Unit> computerUnits) throws InterruptedException {
        Queue<Unit> allUnits = new LinkedList<>();

        // Добавляем всех живых юнитов в очередь ходов
        allUnits.addAll(playerUnits);
        allUnits.addAll(computerUnits);

        while (!allUnits.isEmpty()) {
            Unit currentUnit = allUnits.poll();

            // Пропускаем ход, если юнит мертв
            if (!currentUnit.isAlive()) continue;

            // Определяем цель атаки
            Unit target = currentUnit.getProgram().attack();

            // Если цель атаки существует и жива
            if (target != null && target.isAlive()) {
                // Выполняем атаку
                attack(currentUnit, target);

                // Логируем атаку
                printBattleLog.printBattleLog(currentUnit, target);
            }
        }
    }

    private void attack(Unit attacker, Unit target) {
        // Уменьшаем здоровье цели
        int damage = attacker.getBaseAttack();
        target.setHealth(target.getHealth() - damage);

        // Проверяем, умер ли юнит
        if (target.getHealth() <= 0) {
            target.setAlive(false);
        }
    }

    private void removeDeadUnits(List<Unit> units) {
        units.removeIf(unit -> !unit.isAlive());
    }
}