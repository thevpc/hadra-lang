# hadra-lang
``HadraLang`` is a new ``GPL`` for ``DSL``, multi paradigm, statically typed, transpiled, programming language that is very inspired from Java, C\#, C++, Scala, Kotlin and Haskell among other programming languages around. HadraLang (or **HL**) compiles mainly to java source code or directly to java bytecode. By ``GPL`` for ``DSL`` we mean a General Purpose Programming language that makes it easy to be used as a Domain Specific Language in a range of domains.


``HadraLang`` is intended to be

* easy to learn for DSL users
* more precise semantics
* with better (not more) checking : compiler should do more work
* focus conciseness : write less, do more
* promote readability : meaningful statements
* enable extensibility : always make room for extensions

HadraLang also brings features like

* Dependency Management native support: Versioning and dependencies included in the language itself
* Single file project : In a single file you can declare your classes and even your project dependencies.
* Embeddable scripting: you can include multiple languages in the the very same file
* Transpillable to other Languages: Mainly Hadra Lang is transpilled to Java and/or compiled to Java byte code but we can think of other languages too.
* Preprocessing : The build process executes HadraLang scripts and makes room for dynamic dependencies management for instance
* Operator overloading with unicode support : Besides traditional operator overloading support, we can redefine `√`, `∛` or even superscripts/subscripts to help writing things like `A⁴` or `B₂` 
* Extension of existing classes: This is equivalent to Kotlin Extension Functions or C# Extension Methods
* Extension of control statements (you can redefine the `if`, `for` and `while` constructs, as a matter of fact, you can extend any control statement)
* `null` safety operators
* Checked and unchecked calls: this is kind of using reflection (Runtime Resolution and Execution) while writing simply the code as if it was to be compiled
* Tuples and Range support: tuples and ranges are part of the language SDK to help manipulate bulk data.

=== Why do we need another Programming Language?
``HadraLang`` was first designed as a backing scripting language for "Hadrumaths", a java/scala scientific library which is mainly an algebraic calculus library, and was intended to be a replacement of the scripting facilities implemented earlier in scala. Java and Scala are very powerful programming languages however code can get very verbose or very complicated sooner or later.        
Initially, ``HadraLang`` was implemented as a simple imperative programming language with operator overloading, implicit conversions, and extension functions with support for unicode operators (used in algebraic calculus). There we have seen the potentials of such programming language as a ``GPL``. We tried re-designing the language to make of it a multi-paradigm programming language.

DISCLAIMER
-----------
HadraLang is not yet production ready, we are still trying things and features to make it helpful.

EXAMPLES
-----------

A Hello World application is simply a file you can name "hello.hl" containing the following:

```java
    println("Hello world");  
```

Of course, a single file can include all multiple classes, functions and even project declaration with required dependencies.
Here is a snippet of the one file project in Hadra-lang

```java
    package my-appcom.com.mycompany:my-app#1.0{
       import package junit:junit#5.0 for test;
    }
  
    fun void main(String[] args){
       var a=1î+2;
       var b=1î+2;
       println(a+b);
       (a,b)=(b,a); // using Tuples to swap a and b
       String[] t=(for i:[1..10] String(i*2));
       int x=try int(t[0]); //convert first element and catch exception
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

The following is an example of a simple function declaration:

```java
var h=m(3);
fun m(int x) -> x+1;
```

The following creates an array of 4 elements, each containing the value 3

```java
fun m(int x)-> x+1;
int[m(3)] h(3);
```

The following replaces 'abcd' by 'dcbd'

```java
StringBuilder s('hello');
s[0..2]=s[3..1];
```


The following shows constructing and destructing tuples

```java
package aa.bb:cc#1.0;

fun Tuple<int,int> m() -> (1,2);
fun Tuple<int,int> n() -> (3,4);
fun Tuple<int,Tuple<int,int>> p() -> (1,(2,3));

//global
int a=3;
int b,c=4;
int d,e;
(d,e)=m();
var (f,g)=n();
int h,i=a;
var (v1,(v2,v3))=p();

fun void methodExample(){
    int a,b;
    (a,b)=m();
    var (c,d)=n();
    var (a1,(a2,a3))=p();
}


class test.ClassExample{
    int a=3;
    int b,c=4;
    int d,e;
    (d,e)=m();
    var (f,g)=n();
    int h,i=a;
    var (v1,(v2,v3))=p();

    static int sa=3;
    static int sb,sc=4;
    static int sd,se;
    (sd,se)=m();
    static var (sf,sg)=n();
    static int sh,si=sa;
    static var (sv1,(sv2,sv3))=p();
}

```

The following example demonstrates null de-referencing. It shows how we can get the value of the 
expression 'a.b.c.s' if none is null and falls back to "Hello" if any is null.

 ```java
    int v=a?.b?.c?.five;
    String s=a?.b?.c?.s??"Hello";
    println("s="+s);

```

This example shows how powerful switch can be to replace multiple if statements!

 ```java
double x=3.0;
switch(y=x*2){
    case 2|3+1..6|10:{
        println("two or four to six or ten");
    }
    case 3.0:{
        println("three");
    }
    default:{
        println(y);
    }
}
int result=switch (x=Math.random) {
    if x <0.1 : 1;
    if x in 0.1..<0.2 : 2;
    if x !in 0.5<..<0.6 : 3;
    if 0.2<x<0.3 : 4;
    default: 4;
};

Class x=String.class;
int r=switch x{
    case Integer:1;
    case CharSequence:2;
    default:3;
};
```

more examples under [examples folder](build/hadra-build-tool/src/test/resources/net/hl/test/compiler/examples/README.md)
more documentation under in the [open ebook](doc/ebook/hadra-lang-book.md)


HOW TO TEST HADRA LANG
----------------------
The simplest way to test HadraLang is to run the unit tests of the 'build/hadra-build-tool' project.

Alternatively you, you can install `hl` using nuts package manager (https://github.com/thevpc/nuts) 

```shell
echo 'println("hello world");' > your-file.hl 
nuts install hl
nuts hl your-file.hl
```
