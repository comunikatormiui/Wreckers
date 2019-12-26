package ru.maklas.wreckers.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Predicate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public class ClassUtils {

	public static List<Class> getAllClasses() {
		File file = new File(".\\core\\src");

		List<Class> classes = new ArrayList<Class>();
		for (File node : file.listFiles()) {
			add(classes, node);
		}

		return classes;
	}

	public static <T> List<Class<T>> getAllClasses(Class<T> typeOf, boolean includeInterfaces){

		List<Class> allClasses = getAllClasses();

		List<Class<T>> ret = new ArrayList<Class<T>>();
		for(Class c:allClasses){
			int modifiers = c.getModifiers();
			if (!includeInterfaces && (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers))){
				continue;
			}
			Set<Class> interfaces = new HashSet<Class>();
			addAllParentInterfaces(interfaces, c);

			if (c.getSuperclass() == typeOf){
				ret.add(c);
			} else {
				for (Type clazz : interfaces) {
					if (clazz == typeOf) {
						ret.add(c);
					}
				}
			}
		}

		return ret;
	}

	public static int countStrings(Class clazz, Predicate<String> checker){
		Array<String> sourceCode = getSourceCode(clazz, checker);
		return sourceCode.size;
	}

	public static Array<String> getSourceCode(Class clazz, Predicate<String> checker){
		Array<String> ret = new Array<>();
		String name = clazz.getName();
		String replace = name.replace('.', '\\');
		File file = new File(".\\core\\src\\" + replace + ".java");
		if (!file.exists()){
			return ret;
		}

		Scanner scanner;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return ret;
		}

		while (scanner.hasNext()){
			String s = scanner.nextLine();
			if (checker.evaluate(s)) {
				ret.add(s);
			}
		}

		scanner.close();
		return ret;
	}

	public static StringCountResult countStrings(String packkage, Predicate<String> lineChecker){
		List<Class> allClasses = ClassUtils.getAllClasses();
		Array<Class> chosenClasses = new Array<>();
		for (Class allClass : allClasses) {
			if (isInPackage(allClass, packkage)){
				chosenClasses.add(allClass);
			}
		}

		Array<ClassStrings> classes = new Array<>();
		int overAll = 0;
		for (Class aClass : chosenClasses) {
			int i = ClassUtils.countStrings(aClass, lineChecker);
			overAll += i;
			classes.add(new ClassStrings(aClass, i));
		}
		Comparator<ClassStrings> comparator = new Comparator<ClassStrings>() {
			@Override
			public int compare(ClassStrings o1, ClassStrings o2) {
				int s1 = o1.getStrings();
				int s2 = o2.getStrings();
				if (s1 > s2) {
					return -1;
				}
				return s1 == s2 ? 0 : 1;
			}
		};
		classes.sort(comparator);
		return new StringCountResult(classes, overAll);
	}

	public static StringCountResult countStrings(String packkage, final boolean countImports, final boolean countComments, final boolean countEmptyLines){

		Predicate<String> lineChecker = new Predicate<String>() {
			@Override
			public boolean evaluate(String line) {

				line = line.replaceAll("\\t", "");
				line = line.replaceAll(" ", "");

				boolean anImport = line.startsWith("import");
				boolean aPackage = line.startsWith("package");
				boolean isEmpty = line.equals("");
				boolean commentaryStart = line.startsWith("/");
				boolean commentaryMiddle = line.startsWith("*");


				boolean shouldNotBeCounted = !countImports ? anImport || aPackage : false;
				if (!countEmptyLines){
					shouldNotBeCounted = shouldNotBeCounted || isEmpty;
				}
				if (!countComments){
					shouldNotBeCounted = shouldNotBeCounted || commentaryStart || commentaryMiddle;
				}

				return !shouldNotBeCounted;
			}
		};

		return countStrings(packkage, lineChecker);
	}




	public static boolean isInPackage(Class clazz, String s) {
		return clazz.getName().contains(s);
	}


	/**
	 * Starts main() method in new Process
	 */
	public static int newProcess(Class klass, String... args) throws IOException,
			InterruptedException {
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome +
				File.separator + "bin" +
				File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String className = klass.getCanonicalName();

		String[] command = new String[4 + args.length];
		command[0] = javaBin;
		command[1] = "-cp";
		command[2] = classpath;
		command[3] = className;
		System.arraycopy(args, 0, command, 4, args.length);
		ProcessBuilder builder = new ProcessBuilder(command);

		Process process = builder.start();
		process.waitFor();
		return process.exitValue();
	}


	/* PRIVATES */

	private static void addAllParentInterfaces(Set<Class> set, Class c){

		Class[] interfaces = c.getInterfaces();
		for (Class i : interfaces) {
			set.add(i);
			addAllParentInterfaces(set, i);
		}
	}



	private static void add(List<Class> classes, File file){
		File[] nodes = file.listFiles();

		for (File node:nodes) {
			if (node.isDirectory()){
				add(classes, node);
			} else if (node.getName().endsWith(".java")){
				String dottedPackage = node.getPath()
						.replaceAll("\\\\", ".")
						.replaceAll(".java", "")
						.substring(11);
				Class<?> x;
				try {
					x = Class.forName(dottedPackage);
					classes.add(x);
				} catch (ClassNotFoundException e) {
					System.err.println("Not found for: " + dottedPackage);
				}
			}
		}
	}



	public static class StringCountResult{

		private final Array<ClassStrings> classes;
		private final int total;

		public StringCountResult(Array<ClassStrings> classes, int total) {
			this.classes = classes;
			this.total = total;
		}

		public Array<ClassStrings> getClasses() {
			return classes;
		}

		public int getTotal() {
			return total;
		}

		@Override
		public String toString() {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Total strings: ").append(total).append("\n\n");
			int counter = 0;
			for (ClassStrings aClass : classes) {
				stringBuilder.append(StringUtils.addSpacesRight(counter++ + ":", 5));
				aClass.toString(stringBuilder);
				stringBuilder.append("\n");
			}

			return stringBuilder.toString();
		}
	}

	public static class ClassStrings{
		Class clazz;
		int strings;

		public ClassStrings(Class clazz, int strings) {
			this.clazz = clazz;
			this.strings = strings;
		}

		public Class getClazz() {
			return clazz;
		}

		public int getStrings() {
			return strings;
		}

		public void toString(StringBuilder stringBuilder) {
			stringBuilder.append(clazz.getSimpleName()).append(": ").append(strings);
		}

		public String toString(){
			return clazz.getSimpleName() + ": " + strings;
		}
	}

}
