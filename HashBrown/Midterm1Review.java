/**
 * Created by bakafish on 2/13/17.
 */
public class Midterm1Review {

    public static void switchy(Box a, Box b) {
        Box temp = a;
        a = b;
        b = temp;
    }

    public static void swappy(Box a, Box b) {
        a.x ^= b.x;
        b.x ^= a.x;
        a.x ^= b.x;
        a.y ^= b.y;
        b.y ^= a.y;
        a.y ^= b.y;
    }

    public static int jumble(int a) {
        int b = Integer.MAX_VALUE;
        for(int i = 0; i < 32; i++) {
            b = b << 1;
            b = b | (a & 1);
            a = a >>> 1;
        }
        return b;
    }

    public static boolean findSumFaster(int[] A, int x) {
        int i, j, iprev, jprev, sum;
        iprev = jprev = -1;
        i = j = 0;
        while(i != iprev || j != jprev) {
            sum = A[i] + A[j];
            if(sum == x)
                return true;
            else {
                iprev = i;
                jprev = j;
                if(sum < x) {
                    if(i < A.length - 1) {
                        i++;
                    }
                    if(j < A.length - 1) {
                        j++;
                    }
                }
                else if(sum > x) {
                    if(j > 0) {
                        j--;
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Box a = new Box(1, 2);
        Box b = new Box(3, 4);

        System.out.println(a);
        System.out.println(b);
        System.out.println();

        switchy(a, b);
        System.out.println(a);
        System.out.println(b);
        System.out.println();

        swappy(a, b);
        System.out.println(a);
        System.out.println(b);
        System.out.println();

        int c = 5;
        int d = jumble(c);
        System.out.println(c == jumble(d));
        System.out.println("============");
        int[] test = new int[]{1, 4, 6, 8, 11, 14, 17, 21, 25};
        System.out.println(findSumFaster(test, 27));
        System.out.println(findSumFaster(test, 50));
        System.out.println(findSumFaster(test, 3));
    }

}
