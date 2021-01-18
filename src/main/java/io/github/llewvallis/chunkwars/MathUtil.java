package io.github.llewvallis.chunkwars;

import lombok.experimental.UtilityClass;
import org.bukkit.util.Vector;

@UtilityClass
public class MathUtil {

    public Vector roundVector(Vector vector) {
        return new Vector(
                Math.round(vector.getX()),
                Math.round(vector.getY()),
                Math.round(vector.getZ())
        );
    }
}
