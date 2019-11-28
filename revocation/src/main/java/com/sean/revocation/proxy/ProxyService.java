package com.sean.revocation.proxy;

import java.lang.reflect.Proxy;

import com.sean.revocation.InvocationHandlerImpl;
import com.sean.revocation.service.JobService;
import com.sean.revocation.service.impl.JobServiceImpl;

public class ProxyService {
	public static JobService getJobService() {
		return (JobService)Proxy.newProxyInstance(ProxyService.class.getClassLoader(), new Class[]{JobService.class}, new InvocationHandlerImpl(new JobServiceImpl()));
	}
}
