package testSrc;

import java.util.List;
import java.util.Set;

/**
 * Created by Ilya.Isaev on 26.08.2014.
 */
public class NestedExceptionTest {
    static class A {
        class B {
            class C {
                void test() throws Exception {
                    new Runnable() {
                        @Override
                        public void run() {

                        }
                    }.run();
                    new Runnable() {
                        void run2() {
                        }

                        @Override
                        public void run() {
                            class D {
                                void test3() {
                                    A aa = null;
                                    aa.b.c.test2();
                                }
                            }
                            class E {
                                void test3() {
                                    A aa = null;
                                    aa.b.c.test2();
                                }
                            }
                            new E().test3();
                        }
                    }.run();
                }

                void test2() {

                }
            }

            C c = new C();
        }

        B b = new B();
    }

    class AA{
        List bb;
    }
    class BB extends  AA{
        Set bb;
    }

    public static void main(String[] args) throws Exception {
        new A().b.c.test();
    }
}
