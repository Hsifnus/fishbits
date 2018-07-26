import java.util.HashMap;

/**
 *
 * Created by bakafish on 2/9/17.
 */
public class Squirrel{

    private class Table {
        private int rows, cols;
        private String id;
        private HashMap<String, Object> tableMap = new HashMap<>();

        public Table(String name) {
            id = name;
            rows = cols = 0;
        }

        private String numToKey(int r, int c) {
            return String.format("(%1$d, %2$d)", r, c);
        }

        public void insertItem(int r, int c, Object item) {
            tableMap.put(numToKey(r, c), item);
        }

        public Object retrieveItem(int r, int c) {
            return tableMap.get(numToKey(r, c));
        }
    }

}
