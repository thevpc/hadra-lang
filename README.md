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
  
    def void main(String[] args){
       var a=1î+2;
       var b=1î+2;
       println(a+b)
       (a,b)=(b,a); // using Tuples to swap a and b
    }

    Complex î=Complex(0,1);

    class Complex(double real,double imag){
      contructor(double)->this(value,0);
      def abs -> sqrt(real²+imag²);
      def Complex operator this+Complex -> Complex(this.real+value.real,this.imag+value.imag);
      def Complex operator this*double -> Complex(this.real*value,this.imag*value);
      def Complex operator double*this -> Complex(this.real*value,this.imag*value);
    }
```

more documentation under doc folder...