package io.github.sufod.graphics;

import io.github.sufod.entities.Direction;

public class DirectionUtils {
    // Converts a direction to a diagonal using the last horizontal intent.
    public static Direction toClosestDiagonal(Direction dir, int lastHorizontalSign) {
        if (dir == Direction.UP_LEFT || dir == Direction.UP_RIGHT
            || dir == Direction.DOWN_LEFT || dir == Direction.DOWN_RIGHT) {
            return dir;
        }

        if (dir == Direction.UP) {
            return lastHorizontalSign < 0 ? Direction.UP_LEFT : Direction.UP_RIGHT;
        }

        if (dir == Direction.DOWN) {
            return lastHorizontalSign < 0 ? Direction.DOWN_LEFT : Direction.DOWN_RIGHT;
        }

        if (dir == Direction.LEFT) {
            return Direction.DOWN_LEFT;
        }

        if (dir == Direction.RIGHT) {
            return Direction.DOWN_RIGHT;
        }

        return Direction.DOWN_RIGHT;
    }
}
