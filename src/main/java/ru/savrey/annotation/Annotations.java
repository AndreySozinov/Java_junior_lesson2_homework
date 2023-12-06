package ru.savrey.annotation;

public class Annotations {

    public static void main(String[] args) {
        MyClass myClass = new MyClass();
        System.out.println(myClass.getNegative());
        System.out.println(myClass.getPositive());

        RandomIntegerProcessor.processObject(myClass);

        System.out.println(myClass.getNegative());
        System.out.println(myClass.getPositive());
    }

    static class MyClass {

        @RandomInteger(minValue = -15, maxValue = -1)
        private int negative;

        @RandomInteger(minValue = 1, maxValue = 20)
        private int positive;

        public int getNegative() {
            return negative;
        }

        public int getPositive() {
            return positive;
        }
    }
}
