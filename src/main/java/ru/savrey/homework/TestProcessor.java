package ru.savrey.homework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TestProcessor {

    /**
     * Данный метод находит все void методы без аргументов в классе, и запускает их.
     * <p>
     * Для запуска создается тестовый объект с помощью конструктора без аргументов.
     */
    public static void runTest(Class<?> testClass) {
        final Constructor<?> declaredConstructor;
        try {
            declaredConstructor = testClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Для класса \"" + testClass.getName() +
                    "\" не найден конструктор без аргументов.");
        }

        final Object testObj;
        try {
            testObj = declaredConstructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Не удалось создать объект класса \"" + testClass.getName() + "\"");
        }

        List<Method> methods = new ArrayList<>();
        Method before = null;
        Method after = null;
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                checkTestMethod(method);
                methods.add(method);
            } else if (method.isAnnotationPresent(BeforeEach.class)) {
                checkTestMethod(method);
                before = method;
            } else if (method.isAnnotationPresent(AfterEach.class)) {
                checkTestMethod(method);
                after = method;
            }
        }

        List<Method> methodsSortedByOrder = methods.stream().sorted(Comparator.comparingInt(m ->
                m.getAnnotation(Test.class).order())).toList();

        // methods.forEach(it -> runTest(it, testObj));

        for (Method method : methodsSortedByOrder ) {
            if (!method.isAnnotationPresent(Skip.class)) {
                assert before != null;
                runBefore(before, testObj);

                runTest(method, testObj);

                assert after != null;
                runAfter(after, testObj);
                System.out.println();
            }
        }
    }

    private static void checkTestMethod(Method method) {
        if (!method.getReturnType().isAssignableFrom(void.class) || method.getParameterCount() != 0) {
            throw new IllegalArgumentException("Метод \"" + method.getName() + "\" " +
                    "должен быть void и не иметь аргументов.");
        }
    }

    private static void runTest(Method testMethod, Object testObj) {
        try {
            testMethod.invoke(testObj);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Не удалось запустить тестовый метод \"" +
                    testMethod.getName() + "\"");
        } catch (AssertionError e) {
            throw new RuntimeException("Ошибка \"" + e.getMessage() + "\"");
        }
    }

    private static void runBefore(Method testMethod, Object testObj) {
        try {
            testMethod.invoke(testObj);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Не удалось запустить метод BeforeEach \"" +
                    testMethod.getName() + "\"");
        } catch (AssertionError e) {
            throw new RuntimeException("Ошибка \"" + e.getMessage() + "\"");
        }
    }

    private static void runAfter(Method testMethod, Object testObj) {
        try {
            testMethod.invoke(testObj);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Не удалось запустить метод AfterEach \"" +
                    testMethod.getName() + "\"");
        } catch (AssertionError e) {
            throw new RuntimeException("Ошибка \"" + e.getMessage() + "\"");
        }
    }
}
