package input;

public class Input {
    // sample class fields
    private int x = 1;
    private int y = 2;
    private int z = 3;

    // sample constructor
    public Input() {
        int a = 1;
        int b = 2;
        int c = 3;
        this.x = 100;
    }

    // sample methods which will contain a bunch of varialbes for xpath to map
    public void method1new(Integer x, int y) {
        String new_a = " a ";
        new_a = "b";
        int new_b = 2;
        int new_c = 3;
    }

    public void method2() {
        int d = 4;
        int e = 5;
        int f = 6;
    }

    public void method3() {
        int g = 7;
        int h = 8;
        int i = 9;
    }

    // sample methods invoking other methods
    public void method4() {
        method1new(1, 2);
        method2();
        method3();
    }

    class new_b {}
    interface new_xyz {}
}
