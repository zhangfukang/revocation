package com.sean.revocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvocationHandlerImpl implements InvocationHandler {
	private Object target;
	
	public InvocationHandlerImpl(Object target) {
		this.target = target;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("JDK Proxy");
		return method.invoke(target, args);
	}
}