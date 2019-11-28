package com.sean.revocation.proxy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;

import com.sean.revocation.service.JobService;
import com.sean.revocation.service.impl.JobServiceImpl;

public class CustomInvocationProxy {
	public static JobService getJobService(InvocationHandler invocationHandler) {
		try {
			Object object = new Proxy(invocationHandler).newInstance();
			return (JobService) object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static class Proxy {
		private InvocationHandler invocationHandler;
		
		private Class iface;
		
		private String className;
		
		private String interfaceName;
		
		private final static String LINE = "\n";
		
		private final static String BLANK = "\t";
		
		public Proxy(InvocationHandler invocationHandler) {
			this.iface = invocationHandler.getClass().getInterfaces()[0];
			this.invocationHandler = invocationHandler;
			this.interfaceName = iface.getSimpleName();
			this.className = String.format("$CustomInvocation%sProxy",interfaceName);
		}
		
		public Object newInstance() throws Exception {
			String context = createContext();
			String fileName = getFullJavaFileName();
			createFile(context,fileName);
			compile(fileName);
			return load();
		}
		
		private String getFullJavaFileName() {
			return "H:\\com\\sean\\revocation\\proxy\\" + className + ".java";
		}
		
		private Object load() throws Exception {
			URL[] urls = new URL[] {new URL("file:H:\\\\")};
			URLClassLoader urlClassLoader = new URLClassLoader(urls);
			Class clazz = urlClassLoader.loadClass("com.sean.revocation.proxy." + className);
			Constructor[] constructors = clazz.getConstructors();
            Constructor constructor = clazz.getConstructor(invocationHandler.getClass().getInterfaces()[0]);
            return constructor.newInstance(invocationHandler);
		}

		private void compile(String fileName) throws IOException {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			compiler.run(null, null, null, fileName);
		}

		private void createFile(String context, String fileName) throws IOException {
			FileUtils.writeStringToFile(new File(fileName), context,false);
		}

		public String createContext() {
			StringBuilder context = new StringBuilder();
			context.append(String.format("package com.sean.revocation.proxy;%s%s",LINE,LINE));
			context.append(String.format("import %s;",iface.getName()) + LINE);
			context.append(String.format("import java.lang.reflect.Method;%s",LINE));
			context.append(String.format("import com.sean.revocation.service.JobService;"));
			context.append(LINE);
			context.append(LINE);
			context.append(String.format("public class %s implements %s {%s",className,invocationHandler.target().getClass().getInterfaces()[0].getSimpleName(),LINE));
			context.append(String.format("%sprivate %s %s;%s%s",BLANK, interfaceName,lowerFirstCapse(interfaceName),LINE,LINE));
			context.append(String.format("%spublic %s (%s %s) {%s",BLANK, className,interfaceName ,lowerFirstCapse(interfaceName),LINE));
			context.append(String.format("%s%sthis.%s = %s;%s",BLANK,BLANK,lowerFirstCapse(interfaceName),lowerFirstCapse(interfaceName),LINE));
			context.append(String.format("%s}%s",BLANK,LINE));
			for(Method method : invocationHandler.target().getClass().getDeclaredMethods()) {
				context.append(LINE + createMethod(method));
			}
			context.append(LINE + "}");
			return context.toString();
		}
		
		private StringBuilder createMethod(Method method) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(BLANK);
			stringBuilder.append(Modifier.toString(method.getModifiers()));
			stringBuilder.append(" ");
			stringBuilder.append(method.getReturnType().getSimpleName());
			stringBuilder.append(" ");
			stringBuilder.append(method.getName());
			stringBuilder.append(" ");
			stringBuilder.append("(");
			for(Parameter param : method.getParameters()) {
				stringBuilder.append(param.toString());
			}
			stringBuilder.append(")");
			stringBuilder.append("{" + LINE);
			stringBuilder.append(BLANK + BLANK + "Method method;" + LINE);
			stringBuilder.append(BLANK + BLANK + "try {" + LINE);
			stringBuilder.append(BLANK + BLANK + BLANK + "method = invocationHandler.target().getClass().getDeclaredMethod(\"" + method.getName() + "\"" + getParams(method.getParameters()) + ");" + LINE);
			stringBuilder.append(BLANK + BLANK + BLANK + "return (String) invocationHandler.invoke(method,new Object[] {" + getParamNames(method.getParameters()) + "});" + LINE);
			stringBuilder.append(BLANK + BLANK + "} catch (Exception e) {" + LINE);
			stringBuilder.append(BLANK + BLANK + BLANK + "e.printStackTrace();" + LINE);
			stringBuilder.append(BLANK + BLANK + "}" + LINE);
			stringBuilder.append(BLANK + BLANK + "return null;");
			stringBuilder.append(LINE + BLANK + "}" + LINE);
			return stringBuilder;
		}
		

		private String getParams(Parameter[] parameters) {
			String p = "";
			for(Parameter param : parameters) {
				p += "," + param.getName() + ".getClass()";
			}
			return p;
		}
		
		private String getParamNames(Parameter[] parameters) {
			String p = "";
			if(parameters.length > 0) {
				p = parameters[0].getName();
				for(int i = 1;i < parameters.length;i++) {
					p += "," + parameters[i].getName();
				}
			}
			return p;
		}

		private static String lowerFirstCapse(String str){
			char[]chars = str.toCharArray();
			chars[0] += 32;
			return String.valueOf(chars);
		}
	}
	
	public static void main(String[] args) {
		JobService jobService = CustomInvocationProxy.getJobService(new InvocationHandlerImpl(new JobServiceImpl()));
		System.out.println(jobService.findJob());
	}
}


