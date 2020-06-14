##HadraLang v1.0.0
v1.0.0 focuses on the very first implementation of the language


Here is raw examples of supported features

## uplets
```hl
var myuplet=(1,2.0);
println(myuplet._1,myuplet._2);
int a;
double b;
(a,b)=myuplet;

int[10] arr1(2);
int[10] arr2(3);
var x=(a*2,arr1[0..3],b*2);
(a,arr2[2..5],b)=x;
```

## nonnull values

```hl
    int v=a?b?c?five;
    String s=a?b?c?s??"Hello";
    println("s=",s);
```
## array selectors

```
    int[10] arr((i)->2*i);
    println("arr=",arr);
    
    int[] arr2=arr[3..9];
    println("1::arr2=",arr2);

    arr2[3..5]=arr[1..3];
    println("2::arr2=",arr2);

    arr2[(i)->i%2==0]=arr[1..3];
    println("3::arr2=",arr2);
```



## general
* fixed string manipulation
* fixed support for {} blocks in module
* fixed support of _ in identifiers 
* fixed support of _ in numbers
* fixed array $ in index for arrays
* fixed array $ in index for non arrays 
* fixed decreasing ranges 3>..1 or 3<..1

* [java] switch
* [java] continue (for/while)
* [java] break (for/while)
* [java] if(a is String s) expression
* [unique] json like manipulation
* [unique] tson matrices manipulation
* [unique] istep  as : 1..9:2  equivalent to [1,3,5,7,9]
* [unique] itimes as : 1..9:/3 equivalent to [1,5,9]
* [java] Enums
* [java] extends
* [java] implements?
* [python] Arbitrary Integer Size
* [python] a, b, *rest = range(10) , or first, *_, last = f.readlines()
* [python] print("The %(foo)s is %(bar)i." % {"foo": "answer", "bar": 42}) in stdlib
* [c#] optional parameters in method call, create multiple methods... from c#
* [c#] Employee emp = new Employee {Name="John Smith", StartDate=DateTime.Now()}; from c#
* [c#] SomeType y = x as SomeType; from c#
* [c#] names.Add(myname ??= "James"); from c#
* [c#] properties/getters/setters
* {WONT DO} [scala] * for/while yield from scala and c#

