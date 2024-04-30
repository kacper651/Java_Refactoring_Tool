package input;

interface new_IInput {
    void method1(int x, int y);
    void method2();
    void method3();
}
interface new_ISecondInput {
    void method4();
}
interface IThirdInput { }

public class Input implements new_IInput, new_ISecondInput, IThirdInput {
    // sample class fields
    private int x = 1;
    private int y = 2;
    private int z = 3;

    // sample constructor
    public Input() {
        x = 1;
        y = 2;
        z = 3;
    }

    public Input(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // sample methods which will contain a bunch of varialbes for xpath to map
    public void method1(int o, int p) {
        String a = " a ";
        a = "b";
        int b = 2 + y - p;
        int c = 3*o;
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
        method1(1, 2);
        method2();
        method3();
    }

    class b {}
    interface xyz {}
}
