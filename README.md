# hadra-lang
Do we really need a new programming language?

Here is a hello world in hadra-lang

```hl
    println("Hello world");  
```

Here is a snippet of the one file project in hadra-lang
```hl
    package my-appcom.com.mycompany:my-app#1.0{
       import package junit:junit#5.0 for test;
    }
  
    fun void main(String[] args){
       var a=1î+2;
       var b=1î+2;
       println(a+b)
       (a,b)=(b,a); // using Tuples to swap a and b
       String[] a=(for i:[1..10] String(i*2));
       int x=try int(a[0]); //convert first element and catch exeception
    }

    Complex î=Complex(0,1);

    class Complex(double real,double imag){
      contructor(double)->this(value,0);
      fun abs -> sqrt(real²+imag²);
      fun operator this+Complex -> Complex(this.real+value.real,this.imag+value.imag);
      fun operator this*double -> Complex(this.real*value,this.imag*value);
      fun operator double*this -> Complex(this.real*value,this.imag*value);
    }
```

more documentation under in the [open ebook](doc/ebook/hadra-lang-book.md)