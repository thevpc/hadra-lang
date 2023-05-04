//package net.hl.compiler.utils;
//
//import java.util.concurrent.Callable;
//
//public class TestExample {
//    public static void main(String[] args) throws Exception {
//        execute(() -> "done");  // Line-1
//    }
//
//    static void execute(Runnable runnable) {
//        System.out.println("Executing Runnable...");
//    }
//
//    static void execute(Callable<String> callable) throws Exception {
//        System.out.println("Executing Callable...");
//        callable.call();
//    }
//
///* static void execute(PrivilegedAction<String> action) {
//    System.out.println("Executing PrivilegedAction...");
//    action.run();
//} */
//}
