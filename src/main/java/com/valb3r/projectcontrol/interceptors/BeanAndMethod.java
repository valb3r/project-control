package com.valb3r.projectcontrol.interceptors;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Data
@AllArgsConstructor
public class BeanAndMethod {

    private Object declaringObject;
    private Method method;

    public void doInvoke(Object... args) throws IllegalAccessException, InvocationTargetException {
        method.invoke(declaringObject, args);
    }
}
