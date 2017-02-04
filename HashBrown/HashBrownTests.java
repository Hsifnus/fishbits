import static org.junit.Assert.*;
import org.junit.Test;

/**
 *  Some tests for HashBrown class.
 */
public class HashBrownTests {

    @Test
    public void FunctionMapTest() {
        System.out.println("===== RUNNING FUNCTION MAP TEST =====");

        HashBrown.Function<Double> pyDist = (x, y) ->
                Math.sqrt(HashBrown.valOf(x).pow(2).add(HashBrown.valOf(y).pow(2)).doubleValue());
        HashBrown<Double> test = new HashBrown<>(pyDist, -1, 1, -1, 1, 0.2, 0.2);

        System.out.println("Created 121-item HashBrown of Doubles generated from Pythagorean distance function.");
        assertEquals("(11.0 x 11.0)", test.getDimensionString());
        assertEquals(1, test.getCont(0, 0).traverse("up", 3).traverse("right", 4).getItem(), 0);
        assertEquals(null, test.getCont(0, 0).traverse("down", 5).getNeighbor("down"));
        System.out.println("All tests passed!\n");
    }

    @Test
    public void ExpandTest() {
        System.out.println("===== RUNNING EXPANSION TEST =====");

        HashBrown<Integer> test = new HashBrown<>(5);
        test.expand(4, 0, 0, "left");
        test.expand(6, 0, 0, "right");
        test.expand(2, 0, 0, "up");
        test.expand(8, 0, 0, "down");
        test.expand(9, 0, -1, "right");
        test.expand(7, 0, -1, "left");
        test.expand(3, 1, 0, "up");
        test.expand(1, -1, 0, "up");
        test.expand(0, 0, -1, "down");
        System.out.println("Created 10-item HashBrown of Integers.");
        assertEquals("(3.0 x 4.0)", test.getDimensionString());
        assertEquals(5, test.getCont(0, 0).getItem(), 0);
        assertEquals(3, test.getCont(0, 0).traverse("up", 1).traverse("right", 1).getItem(), 0);
        System.out.println("All tests passed!\n");
    }

    @Test
    public void ReplacementTest() {
        System.out.println("===== RUNNING CONTAINER REPLACEMENT TEST =====");

        System.out.println("Created 2-item HashBrown of Integers.");
        HashBrown<Integer> test = new HashBrown<>(0, true);
        test.expand(1, 0, 0, "right");

        assertEquals("(2.0 x 1.0)", test.getDimensionString());
        assertEquals(0, test.getCont(0, 0).getItem(), 0);
        assertEquals(1, test.getCont(0, 0).getNeighbor("right").getItem(), 0);
        assertEquals(0, test.getCont(0, 0).getNeighbor("right").getNeighbor("left").getItem(), 0);

        System.out.println("Now replacing item at (0, 0)...");
        test.expand(2, 1, 0, "left");

        assertEquals("(2.0 x 1.0)", test.getDimensionString());
        assertEquals(2, test.getCont(0, 0).getItem(), 0);
        assertEquals(1, test.getCont(0, 0).getNeighbor("right").getItem(), 0);
        assertEquals(2, test.getCont(0, 0).getNeighbor("right").getNeighbor("left").getItem(), 0);

        System.out.println("Created another 2-item HashBrown of Integers without replacement.");
        HashBrown<Integer> test2 = new HashBrown<>(0, false);
        test2.expand(1, 0, 0, "right");

        assertEquals("(2.0 x 1.0)", test2.getDimensionString());
        assertEquals(0, test2.getCont(0, 0).getItem(), 0);
        assertEquals(1, test2.getCont(0, 0).getNeighbor("right").getItem(), 0);
        assertEquals(0, test2.getCont(0, 0).getNeighbor("right").getNeighbor("left").getItem(), 0);

        System.out.println("Now replacing item at (0, 0)...");
        test2.expand(2, 1, 0, "left");

        assertEquals("(2.0 x 1.0)", test2.getDimensionString());
        assertEquals(0, test2.getCont(0, 0).getItem(), 0);
        assertEquals(1, test2.getCont(0, 0).getNeighbor("right").getItem(), 0);
        assertEquals(0, test2.getCont(0, 0).getNeighbor("right").getNeighbor("left").getItem(), 0);

        System.out.println("All tests passed!\n");
    }

    @Test
    public void DataMapTest() {
        System.out.println("===== RUNNING DATA MAPPING TEST =====");

        Integer[][] data = new Integer[][]{{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}, {11, 12, 13, 14, 15}};
        HashBrown<Integer> test = new HashBrown<>(data, 0, 0, true);
        System.out.println("Generated HashBrown of Integers from two-dimensional array of Integers.");

        assertEquals("(5.0 x 3.0)", test.getDimensionString());
        for(int i = 0; i < 15; i++) {
            assertEquals(i+1, test.getCont(i % 5, Math.floorDiv(i, 5)).getItem(), 0);
        }
        System.out.println("All tests passed!\n");
    }

    public static void main(String[] args) {
        System.out.println("Run these tests through JUnit!");
    }

}
