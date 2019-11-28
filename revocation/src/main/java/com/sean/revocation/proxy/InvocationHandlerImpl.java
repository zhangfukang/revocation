package com.sean.revocation.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InvocationHandlerImpl implements InvocationHandler {
	private Object target;
	
	public InvocationHandlerImpl(Object target) {
		this.target = target;
	}
	
    public Object invoke(Method method, Object[] args) {
    	try {
			return method.invoke(target, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
    	return null;
    }

	@Override
	public Object target() {
		return target;
	}
}
