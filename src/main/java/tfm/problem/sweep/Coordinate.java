package tfm.problem.sweep;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coordinate {
    private float x;
    private float y;

    public float euclideanDistanceTo(Coordinate coordinate) {
        return (float) Math.sqrt(Math.pow(coordinate.getX() - x, 2) + Math.pow(coordinate.getY() - y, 2));
    }
}
