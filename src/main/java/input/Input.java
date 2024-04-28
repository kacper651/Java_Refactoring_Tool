package input;

class a{

    class b {
        private int c;
    }
    private int d;
    private String e;
    public a(){
        this.d = 5;
        this.e = "XD";
    }
    public a(int d){
        this.d = d;
        e = "???";
    }
    public a(String e){
        this.e = e;
        d = 0;
    }
    public a(int d, String e){
        this.d = d;
        this.e = e;
    }

    public int method1(int b){
        int c = 5;
        d+=1;

        for (int x = 1; x < d; x++){
            c+=x;
            c+=d;
        }

        while  (d < 10){
            e = "xd";
            do {
              String z = e + "xd";
            } while (d != 3);
        }

        switch (d){
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }
        return d+1;
    }

    public int method2(){
        return method1(d);
    }
}
