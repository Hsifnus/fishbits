/**
 * Created by bakafish on 3/14/17.
 */

import java.util.HashMap;

public class Fibonacci {
    private HashMap<Integer, Integer> map = new HashMap<>();

    public int fib(int n) {
        if (n < 0) {
            return n;
        } else if (map.containsKey(n)) {
            return map.get(n);
        } else {
            int val = (n < 2) ? Math.min(n, 1) : 0;
            if (n >= 2) {
                val = fib(n - 1) + fib(n - 2);
            }
            map.put(n, val);
            return val;
        }
    }

    public static void main(String[] args) {
        Fibonacci fib = new Fibonacci();

        for (int i = 0; i < 20; i++) {
            System.out.println(fib.fib(i));
        }
    }
}
