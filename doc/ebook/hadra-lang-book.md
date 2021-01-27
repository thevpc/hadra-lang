---
author:
- Taha BEN SALAH
bibliography:
- 'hadra-lang-bib.bib'
title: 'Hadra-Lang v0.1 Book[^1]'
---

This is a draft document. it is very incomplete\... Hadra Language
Version 0.0.1.1 buit on 2020-06-14 Document Version 0.0.1 buit on
2020-06-14

\

\

\

\

\

  Copyright ©  

Licensed under the Apache License, Version 3.0 (the "License"); you may
not use this file except in compliance with the License. You may obtain
a copy of the License at <http://www.apache.org/licenses/LICENSE-3.0>.
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an , either express or
implied. See the License for the specific language governing permissions
and limitations under the License.

*First printing, JanuaryFebruaryMarchAprilMayJune
JulyAugustSeptemberOctoberNovember December*

 

*Dedicated to my fabulous family.*

Introduction {#introduction .unnumbered}
------------

*HadraLang* is a new general purpose, multi paradigm, statically typed,
transpiled, programming language that that is very inspired from Java,
C\#, C++, Scala, Kotlin,Haskell among other programming languages
around. HadraLang (or \*\*HL\*\*). It compiles mainly to java source
code or directly to java bytecode. 

*HadraLang* focuses on

-   conciseness : write less, do more

-   readability : meaningful statements

-   extensibility : always make room for extensions

-   scientific expressiveness : write scientific software better

-   fast learning curve : Java, C\#, C++ developers will be comfortable
    with the syntax

and brings features like

-   Single file project

-   Scripting

-   Preprocessing

-   Operator overloading with unicode support

-   Extension of existing classes

-   null safety operators

-   Checked and unchecked calls

-   Tuples and Range support

### Why do we need another Programming Language? {#why-do-we-need-another-programming-language .unnumbered}

*HadraLang* was first designed as a backing scripting language for
\"Hadrumaths\" java/scala library which is mainly an algebraic calculus
library as a replacement of the scripting facilities implemented in
scala. Java and Scala are very powerful programming languages however
code can get very verbose or very complicated sooner or later.
Initially, *HadraLang* was implemented as a simple imperative
programming language with operator overloading, implicit conversion, and
extension functions with support for unicode operators (used in algebra
calculus).

### Hello World {#hello-world .unnumbered}

writing a Hello world in *HadraLang* could not be simpler. there is no
need to encapsulate your code in a function/procedure/class or what so
ever. The following code is a fully compilable/runnable valid code :

    println("Hello World");

Assuming this line of code is saved to \"Main.hl\", we need to compile
it and run it using this command:

    $ hl Main.hl

This command will actually compile the code, generating the
corresponding byte code and executing in a new virtual machine.

If we need only to compile the file to a jar we issue this command:

    $ hl --jar Main.hl

### Getting Started {#getting-started .unnumbered}

To use HadraLang we need to download the HadraLang Compiler and runtime.
The easiest way to do so is via nut package manager. if you have not
nuts installed you can do so by

    $ wget https://github.com/thevpc/vpc-public-maven\
    /raw/master/net/thevpc/nuts/nuts/0.8.0/nuts-0.8.0.jar

installing nuts

    $ java -jar nuts-0.8.0.jar

make sure you close and re-open you console after installing nuts for
the environment to be configured correctly.

Once you have nuts installed on one's machine, we issue this command to
install *HadraLang* compiler and runtime.

    $ nuts -y install hl

running Hello world

    $ hl Main.hl

bla bla bla

Reference Guide
===============

Lexical Structure
-----------------

bla bla bla

### Lexical Program Structure

bla bla bla

### Comments

### Keywords

  ---------- ------------ ----------- ----------------- -------------- ----------------
   abstract     assert      boolean         break            byte            case
    catch        char        class        continue         default            do
    double       else        enum          extends          final          finally
    float        for          if       ~~implements~~       import      ~~instanceof~~
     int      interface      long          native            new             null
   package     private     protected       public           return          short
    static     strictfp      super         switch        synchronized        this
    throw     ~~throws~~   transient         try             void          volatile
    while       **is**      **def**    **constructor**   **operator**  
  ---------- ------------ ----------- ----------------- -------------- ----------------

bla bla bla

### Literals

bla bla bla

#### Numerical Literals

int type

    int twelveDecimal=12;
    int oneThousand=1_000;
    int sixteenHexa=0x10;
    int sixteenOctal=020;
    int sixteenBinary=0b10000;

long type

    long twelveDecimal=12L;
    long oneThousand=1_000L;
    long sixteenHexa=0x10L;
    int sixteenOctal=020L;
    long sixteenBinary=0b10000L;

byte type

    byte twelveDecimal=12b;
    byte oneHandred=1_00b;
    byte sixteenHexa=0x10b;
    byte sixteenOctal=020b;
    byte sixteenBinary=0b10000b;

short type

    short twelveDecimal=12S;
    short oneHandred=1_00S;
    short sixteenHexa=0x10S;
    short sixteenOctal=020S;
    short sixteenBinary=0b10000S;

big integer type

    BigInteger twelveDecimal=12B;
    BigInteger oneHandred=1_00B;
    BigInteger sixteenHexa=0x10B;
    BigInteger sixteenOctal=020B;
    BigInteger sixteenBinary=0b10000B;

float type

    float twelveDecimal=12.0f;
    float oneHandred=1_00f;

double type

    double twelveDecimal=12.0d;
    double oneHandred=1_00d;

big decimal type

    BigDecimal twelveDecimal=12.0D;
    BigDecimal oneHandred=1_00D;

#### Character and String Literals

bla bla bla

#### Temporal Literals

    LocalDate d=t"2020-02-01";
    LocalDateTime dt=t"2020-02-01T12:00";
    LocalTime t=t"12:00";

#### Pattern Literals

    if (p"[0-9]+".matches("12")){
        println("12 is a number");
    }
    if (p"[0-9]+" ~ "12"){
        println("12 is a number");
    }
    if ("12" ~ p"[0-9]+"){
        println("12 is a number");
    }

#### Interpolated String Literals

    int x=12; 
    String s1="Hello";
    String s2="World";
    println($"$s1 ${s2} for number $x");

### Identifiers

### Operators

#### == operator

    String s1="hello";
    String s2(s1);
    if(s1==s1){
        println("this is true.");
    }
    if(null==s1){
        println("this is false.");
    }

#### === operator

    String s1="hello";
    String s2(s1);
    if(s1==s1){
        println("this is always true. null safe 'equals' will be called");
    }
    if(s1===s1){
        println("this is false. ref equality will be used");
    }

#### '.' member operator

    Person p;
    println(p.address.street.name);

#### '?' null expression member operator

    Person p;
    println(p?address?street?name);

#### '??' null member operator

                Person p;
                println(p??"null");

#### '.?' Operator (unchecked member)

    Person p;
    Object o=p;
    println(o.?address.?street.?name);
    println(o??address??street??name);

#### '()' Operator (apply)

    TODO

#### '\[\]' Operator (indexed)

    TODO

### Tuples

    Tuple2<int,int> x=(1,2);
    Tuple<int,int> x=(1,2);
    <int,int> x=(1,2);

Tuple deconstructors

    int x,y;
    (x,y)=m();
    (x,y)=(y,x);
    def m()->(1,2);

### Arrays

Left Hand Side matching

    int[10] x(i->i);
    x[0..2]=[15,20,30];
    x[0..2]=x[5..7];

Right Hand Side matching

    int[10] x(i->i);
    int[] y=x[5..7];

Array Length

    int[10] a;
    int[10] a(3);
    int[10] a(x->Math.random());
    int[10] a(Math::random);

### Lambda Expressions

### Annotations

TODO

Control-flow
------------

### if/else syntax

#### if statement

#### if expression

### switch expression

#### switch/case statement

#### switch case statement

#### switch is statement

#### switch if statement

#### switch default statement

#### switch on Tuples

#### switch Expression

### while loop syntax

#### while/do statement

#### do/while statement

#### while/do expression

#### do/while expression

### for loop

#### for loop statement

#### for iterator statement

loops on Iterable,Enumeration,Iterator and Streams. multi iterator does
the cross loop filter the loop

#### for loop expression

#### for iterator expression

\<statement\> ::= \<ident\> '=' \<expr\> 'for' \<ident\> '=' \<expr\>
'to' \<expr\> 'do' \<statement\> '' \<stat-list\> '' \<empty\>

\<stat-list\> ::= \<statement\> ';' \<stat-list\> \| \<statement\>

    int[10] a;
    for(x=0;x<a.length;x++){
        println(x);
    }

    int[10] a;
    int[10] b;
    for(x:a){
        println(x);
    }
    for(x:a,y:b){
        println(x,y);
    }

Statement Filter

    int[10] a;
    int[10] b;
    for(x:a){
        println(x);
    }
    for(x:a,y:b;x<y){
        println(x,y);
    }

For Expression: for expression is an enumeration comprehension that
creates enumerated type that will be processed at will. The result is
mainly a stream that is mapped implicitly to array/List or Iterable
according to the given context.

    Iterable<int> x=for(i=0;i<10;i++)->i;
    IntStream<int> x=for(i=0;i<10;i++)->i;
    List<int> x=for(i=0;i<10;i++)->i;
    int[] x=for(i=0;i<10;i++)->i;

### try/catch

All exceptions in Hadra-Lang are uncheked. This means there is no need
at compile time to add try/catch blocks. Besides Exceptions are handled
both as control blocs and as expressions.

Catch Blocs

                int x;
                try {
                    x=int(myString);
                }catch(Exception){
                    //do some thing
                }

Multi catch Blocs

                int x;
                try {
                    x=int(myString);
                }catch(NumberFormatException
                    |NullPointerException ex){
                    //do some thing
                }catch(Exception e){
                    //do some thing
                }

Exception variable Name

                int x;
                try {
                    x=int(myString);
                }catch(NumberFormatException
                    |NullPointerException){
                    throw error;
                }catch(Exception){
                    throw error;
                }
                

Catch Expressions

                int x = int(myString) catch 0;

Declarations
------------

#### Classes

HadraLang supports Object Oriented programming by providing syntax for
creating and manipulating classes and methods. All features from Java
such as class initializers, instance initializers, class constructors,
default constructor inheritence and visibility apply. Howerver some
differences will be discussed here after. a class in HadraLang must
start with the class keyword. constructor and declared fields in on
declaration statement.

                class Complex{
                }

As you can see, no modifiers are required. The default modifier is
[public]{style="color: keyword1"} (by opposition to package protected in
java). As a matter of fact, there is NO package protected visibility in
HadraLang. For top level classes, the modifiers that apply are public
and final

#### Annotation Classes

                @MathType
                class Complex{
                }

#### Exception Classes

#### Enumerations

#### Fields and Properties

Main Constructor Arguments

    class my.Complex{
        constructor(double real,double imag){
            this.real=real;
            this.imag=imag;
        }
    }

Field without Getters and Setters

    class my.Complex{
        double real;
        double imag;
    }

Default Getters and setters

    class my.Complex{
        double real{ get;set;}
        double image{ get;set;}
    }

Custom Getters and setters

    class my.Complex{
        //create custom getter/setter
        double real{
            get {
                println("before get");
                return this.real;
            }
            set {
                //field access because we are in the setter
                this.real=value;
            }
        }
    }

Dynamic Properties

    class my.Complex{
        double real;
        double imag;
        double abs{
            get->sqrt(this.real$^2$+this.imag$^2$);
        }
        double absSquare->abs*abs;
    }

Multiple Setters

    class my.Complex{
        double real;
        double imag;
        double radius {
            set{
                real=value*sin(angle);
                imag=value*cos(angle);
            }
            set(int){
                real=value*sin(angle);
                imag=value*cos(angle);
            }
            set(String s){
                set(double(s));
            }
        }
    }

Init values

    class my.Complex{
        double real{
            init->1;
            get;set;
        }
    }

#### Constructors

    class Complex(double real,double imag){
        constructor(double real)->this(real,0);
    }

    class Complex{
        double real;
        double imag;
        constructor(double real,double imag){
            this.real=real;
            this.imag=imag;
        }
    }    

    class Complex{
        double real;
        double imag;
    }

#### Methods

Main constructors define a simple way to create a class in a very
consise way. It defines the class, a default constructor and declared
fields in on declaration statement.

    class Complex(double real,double imag){

    }

This example creates the class Complex with a couple of public fields.
Actually it is equivalent to this java code

    //Equivalent Java
    public class Complex(double real,double imag){
    public double real;
    public double imag;
    public Complex(double real,double imag){
        this.real=real;
        this.imag=imag;
    }

    }

While the constructor itself is always public (regardless of Class
visibility), fields can be secured with lower visibility modifiers :

    class Complex(double real,private double imag){

    }

and hence the equivalent java code will be :

    //Equivalent Java
    public class Complex(double real,double imag){
        public double real;
        private double imag;
        public Complex(double real,double imag){
            this.real=real;
            this.imag=imag;
        }

    }

#### Functions and global variables

Global functions //global function (outside any class)

    def void ageOf(Person p){
        return p.age;
    }
    class Person{
        int age;
    }

main function

    //global function (outside any class)
    def void main(String[] args){
    }
    //or
    def main(String[] args){
    }

local functions

    def void main(String[] args){
        def int max(int a,int b)->if a<b a else b
        int x=max(3,5);
    }
                //or

#### main function

    def main(String[] args){
    }

#### Extension functions

    import OtherClass.**
    class my.Complex(double real,double imag){
        constructor(double real)->this(real,0);
    }
    //Other Compilation Unit
    class OtherClass{
        def String Complex.toXml() {
            return "<xml>"+this+"</xml>";
        }
    }

#### Operators

    class Complex{
        //...
        static def Complex 
            operator Complex left + Complex right 
            -> left.add(other);
        def Complex operator this + Complex other -> add(other);
        def Complex operator this - Complex -> add(value);
        def Complex operator + this -> this;
        def Complex operator ++ this -> add(1);
        def Complex operator this ++ -> add(1);
    }
    class Matrix{
        //...
        def Complex operator this(int column,int row)
            ->get(column,row);
        def Complex operator this[int column,int row]
            ->get(column,row);
        def void operator this[int column,int row]
            =Object
            -> set(column,row,value);
    }

#### Extension constructors

    import OtherClass.**
    class my.Complex(double real,double imag){
        constructor(double real)->this(real,0);
    }
    //Other Compilation Unit
    class OtherClass{
            //newComplex
            constructor(String value) for my.Complex{
                return Complex(double(value));
            }
            //newIntArray2
            constructor (String value) for int[][]{
                return int[0][0];
            }
    }

#### Package Class

package declaration must appear once in the project (unlike java).

    package com.company;
    class example.MyClass{
        //effective java package is com.company.example.MyClass
    }

package group and version

    package com.company:myapp#1.0;
    class example.MyClass{
        //effective java package is com.company.example.MyClass
    }

modules and dependencies //package declaration must appear once in the
project (unlike java).

    package com.company:myapp#1.0{
        import com.lib:other#1.0 for compile;
        import com.lib:test#1.0  for test;
    }

Compilation and Execution
-------------------------

### PreProcessor

//package declaration must appear once in the project (unlike java).

    package com.company:myapp#1.0{
        //effective java package is com.company.example.MyClass
    }

### Maven Support

### Interpreter

    hl myfile.hl

### Java Transpiler/Compiler

    hl -c src/

    hl -j src/

Programming with Hadra-Lang
===========================

IDE Support
-----------

### Netbeans Support

blabla

### Intellij Support

blabla

### Eclipse Support

blabla

### Visualcode Support

blabla

### Other Text Tools support

blabla

Standard Library
----------------

Java Integration
----------------

Future Features
===============

### Javascript/Typescript Transpiler

using \[jsweet\](http://www.jsweet.org)

    hl --js src/
    hl --ts src/

### Javascript/Typescript Transpiler

using \[j2c\](https://bitbucket.org/arnetheduck/j2c/src/default/)
\[j2c(alt)\](https://github.com/arnetheduck/j2c)

    hl --js src/
    hl --ts src/

### Native Compiler

using \[https://www.graalvm.org\](https://www.graalvm.org)

    hl --native src/

### Language Server Protocol

using \[https://langserver.org\](https://langserver.org)

    hl --slp

[^1]: Thanks to Edward R. Tufte for his template.
