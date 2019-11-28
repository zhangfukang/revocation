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

public class CustomProxy {
	public static JobService getJobService(Class jobServiceImpl) {
		try {
			Object object = new Proxy(jobServiceImpl).newInstance();
			return (JobService) object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static class Proxy {
		private Class iface;
		
		private Class impl;
		
		private String className;
		
		private String interfaceName;
		
		private String implName;
		
		
		private final static String LINE = "\n";
		
		private final static String BLANK = "\t";
		
		public Proxy(Class impl) {
			this.iface = impl.getInterfaces()[0];
			this.impl = impl;
			this.interfaceName = iface.getSimpleName();
			this.implName = impl.getSimpleName();
			this.className = String.format("$Custom%sProxy",interfaceName);
			this.impl = impl;
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
            Constructor constructor = clazz.getConstructor(impl);
            return constructor.newInstance(new JobServiceImpl());
		}

		private void compile(String fileName) throws IOException {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			compiler.run(null, null, null, fileName);
//            StandardJavaFileManager fileMgr = compiler.getStandardFileManager(null, null, null);
//            Iterable units = fileMgr.getJavaFileObjects(fileName);
//            JavaCompiler.CompilationTask t = compiler.getTask(null, fileMgr, null, null, null, units);
//            t.call();
//            fileMgr.close();
		}

		private void createFile(String context, String fileName) throws IOException {
			FileUtils.writeStringToFile(new File(fileName), context,false);
		}

		public String createContext() {
			StringBuilder context = new StringBuilder();
			context.append(String.format("package com.sean.revocation.proxy;%s%s",LINE,LINE));
			context.append(String.format("import %s;",iface.getName()));
			context.append(LINE);
			context.append(String.format("import %s;",impl.getName()));
			context.append(LINE);
			context.append(LINE);
			context.append(String.format("public class %s implements %s {%s",className,interfaceName,LINE));
			context.append(String.format("%sprivate %s %s;%s%s",BLANK, implName,lowerFirstCapse(implName),LINE,LINE));
			context.append(String.format("%spublic %s (%s %s) {%s",BLANK, className,implName,lowerFirstCapse(implName),LINE));
			context.append(String.format("%s%sthis.%s = %s;%s",BLANK,BLANK,lowerFirstCapse(implName),lowerFirstCapse(implName),LINE));
			context.append(String.format("%s}%s",BLANK,LINE));
			for(Method method : impl.getDeclaredMethods()) {
				context.append(LINE + createMethod(method));
			}
			context.append("}");
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
			stringBuilder.append("{" + LINE + BLANK + BLANK);
			if(!"void".equals(method.getReturnType().getSimpleName())) {
				stringBuilder.append("return ");
			}
			stringBuilder.append(String.format("%s.%s",lowerFirstCapse(implName),method.getName()));
			stringBuilder.append("(");
			if(method.getParameterCount() > 0) {
				stringBuilder.append(method.getParameters()[0].getName());
			}
			for(int i = 1;i < method.getParameterCount();i++) {
				stringBuilder.append("," + method.getParameters()[i]);
			}
			stringBuilder.append(");");
			stringBuilder.append(LINE + BLANK + "}" + LINE);
			return stringBuilder;
		}
		

		private static String lowerFirstCapse(String str){
			char[]chars = str.toCharArray();
			chars[0] += 32;
			return String.valueOf(chars);
		}
	}
	
	public static void main(String[] args) throws IOException {
		JobService jobService = CustomProxy.getJobService(JobServiceImpl.class);
		System.out.println(jobService.findJob());
	}
}


