/**
 *
 * Created by bakafish on 2/11/17.
 */

public class InheritanceTest {

    private class A extends B {
        private int y = 5;
        private int x;
        public A(int n) {
            super(n);
            x = n;
        }
        public int m1() {
            return x;
        }
        public int m2() {
            return y;
        }
        public int m3() {
            return x + y;
        }
    }

    private class B  {
        private int y = 6;
        private int x;
        public B(int n) {
            x = n;
        }
        public int m1() {
            return x;
        }
        public int m2() {
            return y;
        }
    }

    private class C extends A {
        private int y = 7;
        private int z = 8;
        private int x;
        public C(int n) {
            super(n);
            x = n;
        }
        public int m1() {
            return x;
        }
        public int m2() {
            return y;
        }
        public int m4() {
            return x + 2*y;
        }
        public int m5() {
            return z;
        }
    }

    public InheritanceTest() {
        A x1 = new C(18);
        B x2 = x1;
        B x3 = (B) x1;
        B x4 = (C) x1;
        B x5h = new C(20);
        C x5 = (C) x5h;

        System.out.println(x1.m1());
        System.out.println(x2.m1());
        System.out.println(x3.m1());
        System.out.println(x4.m1());
        System.out.println(x5.m1());

        System.out.println();

        System.out.println(x1.m2());
        System.out.println(x2.m2());
        System.out.println(x3.m2());
        System.out.println(x4.m2());
        System.out.println(x5.m2());

        System.out.println();

        System.out.println(x1.m3());
//        System.out.println(x2.m3());
//        System.out.println(x3.m3());
//        System.out.println(x4.m3());
        System.out.println(x5.m3());

        System.out.println();

//        System.out.println(x1.m4());
//        System.out.println(x2.m4());
//        System.out.println(x3.m4());
//        System.out.println(x4.m4());
        System.out.println(x5.m4());

        System.out.println();

        System.out.println(x5.m5());
    }

    public static void main(String[] args) {
        InheritanceTest test = new InheritanceTest();
    }
}
