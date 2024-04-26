package input;

interface i{}
interface j{}
interface k{}
interface l extends i, j, k {}
interface m{}

class a{}

class b extends a implements i, j, k{

    class c extends a implements i{

        class d extends a implements j{}

    }

}

class e extends b implements l, m {}

class f implements i{
    class g extends a implements j{}
    interface xyz {}
}


