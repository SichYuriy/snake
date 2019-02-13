package com.codenjoy.dojo.snakebattle.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.snakebattle.model.Elements;

import java.util.*;

/**
 * User: your name
 * Это твой алгоритм AI для игры. Реализуй его на свое усмотрение.
 * Обрати внимание на {@see YourSolverTest} - там приготовлен тестовый
 * фреймворк для тебя.
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        this.board = board;
        if (board.isGameOver()) return "";

        Point[][] way = new Point[board.size()][board.size()];
        Optional<Point> nearestGood = findNearestEatableBfs(board.getMe(), way);
        if (nearestGood.isPresent()) {
            Point good = nearestGood.get();
            Point next = getNextPointOnTheWay(board.getMe(), good, way);
            return getDirection(board.getMe(), next).toString();
        }

        return Direction.RIGHT.toString();
    }

    private Point getNextPointOnTheWay(Point from, Point to, Point[][] way) {
        Point previous = to;
        Point current = way[to.getX()][to.getY()];
        while (current.getX() != from.getX() || current.getY() != from.getY()) {
            previous = current;
            current = way[previous.getX()][previous.getY()];
        }
        return previous;
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "https://game2.epam-bot-challenge.com.ua/codenjoy-contest/board/player/vasya.pupkin.ivanovych@gmail.com?code=9067561311814908329",
                new YourSolver(new RandomDice()),
                new Board());
    }

    private Optional<Point> findNearestEatableBfs(Point from, Point[][] way) {
        way[from.getX()][from.getY()] = from;
        ArrayDeque<Point> bfs = new ArrayDeque<>();
        bfs.addLast(from);
        while (!bfs.isEmpty()) {
            Point current = bfs.pollFirst();
            List<Point> siblings = getSiblings(current);
            for (Point next : siblings) {
                if (way[next.getX()][next.getY()] == null
                        && isNotBarrier(next)) {
                    way[next.getX()][next.getY()] = current;
                    bfs.addLast(next);
                    if (board.isAt(next, Elements.APPLE, Elements.GOLD)) {
                        return Optional.of(next);
                    }
                }
            }
        }

        return Optional.empty();
    }

    private ArrayList<Point> getSiblings(Point point) {
        ArrayList<Point> siblings = new ArrayList<>();
        siblings.add(new PointImpl(point.getX(), point.getY() + 1));
        siblings.add(new PointImpl(point.getX() + 1, point.getY()));
        siblings.add(new PointImpl(point.getX(), point.getY() - 1));
        siblings.add(new PointImpl(point.getX() - 1, point.getY()));
        return siblings;
    }

    private boolean isNotBarrier(Point point) {
        return board.isAt(point, Elements.NONE, Elements.APPLE, Elements.GOLD, Elements.FLYING_PILL, Elements.FURY_PILL);
    }

    private Direction getDirection(Point from, Point next) {
        int dx = next.getX() - from.getX();
        int dy = next.getY() - from.getY();
        if (dx < 0) {
            return Direction.LEFT;
        } else if (dx > 0) {
            return Direction.RIGHT;
        } else if (dy > 0) {
            return Direction.UP;
        } else {
            return Direction.DOWN;
        }
    }

}
