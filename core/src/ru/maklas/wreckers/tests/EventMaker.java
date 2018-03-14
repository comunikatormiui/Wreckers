package ru.maklas.wreckers.tests;

import com.badlogic.gdx.utils.Array;
import ru.maklas.libs.SimpleProfiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by maklas on 11-Jan-18.
 */

public class EventMaker {

    private String className;
    private Array<FieldData> fields = new Array<FieldData>();
    private Writer writer;
    private boolean makeSetters = false;


    public EventMaker name(String name){
        this.className = name;
        return this;
    }

    public EventMaker field(Class clazz, String varName){
        FieldData data = new FieldData(clazz, varName);
        fields.add(data);
        return this;
    }

    public EventMaker float_(String varName){
        FieldData data = new FieldData(float.class, varName);
        fields.add(data);
        return this;
    }

    public EventMaker int_(String varName){
        FieldData data = new FieldData(int.class, varName);
        fields.add(data);
        return this;
    }

    public EventMaker bool(String varName){
        FieldData data = new FieldData(boolean.class, varName);
        fields.add(data);
        return this;
    }

    public EventMaker string(String varName){
        FieldData data = new FieldData(String.class, varName);
        fields.add(data);
        return this;
    }


    public void build() {
        SimpleProfiler.start();
        checkName();
        File file = checkFileNameIsNotTaken();
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Writer writer = null;
        try {
            writer = new Writer(new PrintWriter(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.writer = writer;

        doPackage();
        writer.println();
        doImports();
        writer.println();
        doTemplate();
        doClass();
        writer.flush();

        System.out.println(className + " was successfully built in " + SimpleProfiler.getTimeAsString(4) + " seconds.");
        String path;
        try {
            path = file.getCanonicalPath();
        } catch (IOException e) {
            path = file.getPath();
        }
        System.out.println("Location: " + path);

    }

    private File checkFileNameIsNotTaken() {
        File file = new File(".\\core\\src\\ru\\maklas\\wreckers\\network\\events\\" + className + ".java");
        if (file.exists()){
            className = className + "1";
            return checkFileNameIsNotTaken();
        }
        return file;
    }

    private void checkName() {
        if (className == null){
            throw new RuntimeException("Name is a must");
        }

        Character first = className.charAt(0);
        if (Character.isLowerCase(first)){
            char upper = Character.toUpperCase(first);
            className = upper + className.substring(1);
        }
        className = className.replace(" ", "");
    }

    private void doPackage(){
        writer.println("package ru.maklas.wreckers.network.events" + ';');
    }

    private void doImports() {
        writer.println("import ru.maklas.wreckers.libs.Copyable;");
        if (containsArrays()) writer.println("import java.util.Arrays;");

        Set<FieldData> fieldSet = new HashSet<FieldData>();
        for (FieldData field : fields) {
            fieldSet.add(field);
        }

        for (FieldData field : fieldSet) {
            String anImport = field.importName;
            if (anImport != null && !anImport.equals("")) {
                writer.println("import " + anImport + ';');
            }
        }
    }

    private boolean containsArrays(){
        for (FieldData field : fields) {
            if (field.clazz.isArray()){
                return true;
            }
        }
        return false;

    }

    private void doTemplate(){
        final String creatorName = "MaklasEventMaker";
        final Date date = new Date();
        final String pattern = "dd.MM.yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        writer.println("/**");
        writer.println(" * Created by " + creatorName + " on " + simpleDateFormat.format(date));
        writer.println(" */");
    }

    private void doClass(){

        StringBuilder interfaces = new StringBuilder();
        interfaces.append("Copyable");

        writer.println("public class " + className + " implements " + interfaces.toString() + " {");
        writer.levelUp();
        {
            writer.println();
            doFields();
            writer.println();
            doConstructor();
            writer.println();
            doEmptyConstructor();
            writer.println();
            doSetAndRet();
            writer.println();
            doGetters();
            writer.println();
            if (makeSetters) doSetters();
            writer.println();
            doToString();
            writer.println();
            doCopy();
        }
        writer.levelDown();
        writer.println("}");
    }

    private void doConstructor() {
        Array<String> body = getAssignments();
        method(writer, "public", className, "", fields, body, "");
    }

    private void doEmptyConstructor(){
        method(writer, "public", className, "", new Array<FieldData>(), new Array<String>(), "");
    }

    private void doSetAndRet(){
        method(writer, "public", className, "setAndRet", fields, getAssignments(), "this");
    }

    private void doGetters(){
        for (FieldData field : fields) {
            doGetter(field);
            writer.println();
        }

    }

    private void doGetter(FieldData field) {
        String methodName = (field.varName.startsWith("is") && (field.clazz == Boolean.class || field.clazz ==  boolean.class))? field.varName : "get" + Character.toUpperCase(field.varName.charAt(0)) + field.varName.substring(1);
        method(writer, "public", field.type, methodName, new Array<FieldData>(), new Array<String>(), "this." + field.varName);
    }

    private void doSetters() {
        for (FieldData field : fields) {
            doSetter(field);
            writer.println();
        }
    }

    private void doSetter(FieldData field){
        String methodName = (field.varName.startsWith("is") && (field.clazz == Boolean.class || field.clazz ==  boolean.class))?  "set" + field.varName.substring(2) : "set" + Character.toUpperCase(field.varName.charAt(0)) + field.varName.substring(1);
        method(writer, "public", "void", methodName, Array.with(field), Array.with("this." + field.varName + " = " + field.varName + ';'), "");
    }

    private void doToString(){
        writer.println(override());

        Array<String> body = new Array<String>();
        body.add("return " + "\"" + className + "{\" +");
        if (fields.size == 0){
            body.add("\'}\';");
        } else {
            FieldData fieldData = fields.get(0);
            body.add("\"" + fieldData.varName + "=\" + " + fieldValueToString(fieldData.clazz, fieldData.varName) + " +");

            for (int i = 1; i < fields.size; i++) {
                FieldData field = fields.get(i);
                body.add("\", " + field.varName + "=\" + " + fieldValueToString(field.clazz, field.varName) + " +");
            }
            body.add("\'}\';");
        }
        method(writer, "public", "String", "toString", new Array<FieldData>(), body, "");
    }

    private String fieldValueToString(Class clazz, String varName){
        if (clazz.isArray()){
            return "Arrays.toString(" + varName + ")";
        } else {
            return varName;
        }


    }

    private Array<String> getAssignments(){
        Array<String> body = new Array<String>();
        for (FieldData field : fields) {
            body.add("this." + field.varName + " = " + field.varName + ';');
        }
        return body;
    }

    private void doFields() {
        for (FieldData field : fields) {
            doField(field);
        }
    }

    private void doField(FieldData field) {
        writer.println(field.type + " " + field.varName + ';');
    }

    private void doCopy() {
        writer.println("@Override");

        StringBuilder retBuilder = new StringBuilder();
        retBuilder.append("new ");
        retBuilder.append(className);
        retBuilder.append("(");
        if (fields.size > 0){
            retBuilder.append(getParamForCopy(fields.get(0)));

            for (int i = 1; i < fields.size; i++) {
                FieldData field = fields.get(i);
                retBuilder.append(", ");
                retBuilder.append(getParamForCopy(field));
            }
        }
        retBuilder.append(')');
        String ret = retBuilder.toString();

        method(writer,"public", "Object", "copy", Array.of(FieldData.class), new Array<String>(), ret);
    }

    private String getParamForCopy(FieldData data){
        Class clazz = data.clazz;
        if (clazz.isArray()) {
            return "Arrays.copyOf(" + data.varName + ", " + data.varName + ".length)";
        }
        if (
                (clazz.isPrimitive()) ||
                        (clazz == Integer.class) ||
                        (clazz == Float.class) ||
                        (clazz == Long.class) ||
                        (clazz == Double.class) ||
                        (clazz == String.class) ||
                        (clazz == Byte.class) ||
                        (clazz == Character.class) ||
                        (clazz == Boolean.class) ||
                        (clazz.isEnum()))  {
            return data.varName;
        } else {
            return "(" + data.type + ") " + data.varName + ".copy()";
        }
    }

    private String override(){
        return "@Override";
    }

    private void method(Writer writer, String publicity, String returnType, String name, Array<FieldData> params, Array<String> body, String returns){
        if (!publicity.equals("")) {
            writer.print(publicity + ' ');
        }
        writer.print(returnType + ' ');

        writer.print(name + '(');
        if (params.size > 0){
            writer.print(params.get(0).type + ' ' + params.get(0).varName);
            for (int i = 1; i < params.size; i++) {
                FieldData field = params.get(i);
                writer.print(", " + field.type + ' ' + field.varName);
            }
        }
        writer.println(") {");

        writer.levelUp();
        {
            if (body.size > 0) {
                for (String s : body) {
                    writer.println(s);
                }
            } else {
                if (returns.equals(""))
                    writer.println();
            }
            if (!returns.equals("")){
                writer.println("return " + returns + ";");
            }
        }
        writer.levelDown();
        writer.println("}");
    }

    /**
     * Добавляет сеттеры ко всем полям
     * @return
     */
    public EventMaker includeSetters() {
        makeSetters = true;
        return this;
    }


    private class FieldData{

        final Class clazz;
        final String type;
        final String varName;
        final String fullName;
        final String importName;

        public FieldData(Class clazz, String varName) {
            this.clazz = clazz;
            this.varName = Character.isLowerCase(varName.charAt(0)) ? Character.toLowerCase(varName.charAt(0)) + varName.substring(1) : varName;
            this.type = determineType(clazz);
            this.fullName = clazz.getName();
            this.importName = getImportName(clazz);
        }

        private String determineType(Class clazz) {
            if (clazz == Float.class){
                 return "float";
            } else if (clazz == Integer.class){
                return "int";
            } else if (clazz == Long.class){
                return "long";
            } else if (clazz == Double.class){
                return "double";
            } else if (clazz == String.class){
                return "String";
            } else if (clazz == Byte.class){
                return "byte";
            } else if (clazz == Character.class){
                return "char";
            } else if (clazz == Boolean.class){
                return "boolean";
            } else {
                return clazz.getSimpleName();
            }
        }

        private String getImportName(Class clazz) {
            if (clazz.isArray()) {
                return getImportName(clazz.getComponentType());
            }
            if (
                    (clazz.isPrimitive()) ||
            (clazz == Integer.class) ||
            (clazz == Float.class) ||
            (clazz == Long.class) ||
            (clazz == Double.class) ||
            (clazz == String.class) ||
            (clazz == Byte.class) ||
            (clazz == Character.class) ||
            (clazz == Boolean.class)) {
                return "";
            } else {
                return clazz.getCanonicalName();
            }
        }

        @Override
        public int hashCode() {
            return clazz.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null){
                return false;
            }
            if (!(o instanceof FieldData)){
                return false;
            }
            return (clazz.equals(((FieldData) o).clazz));
        }
    }


    private class Writer{

        PrintWriter printWriter;
        private int tabLevel = 0;
        private boolean first = true;

        public Writer(PrintWriter printWriter) {
            this.printWriter = printWriter;
        }

        void levelUp(){
            tabLevel++;
        }

        void levelDown(){
            tabLevel--;
        }

        void checkFirst(){
            if (first){
                tab(tabLevel);
                first = false;
            }
        }

        void print(String s){
            checkFirst();
            printWriter.print(s);
        }

        void println(String s){
            checkFirst();
            printWriter.println(s);
            first = true;
        }

        void println(){
            checkFirst();
            printWriter.println();
            first = true;
        }

        void flush(){
            printWriter.flush();
        }

        private void tab(){
            printWriter.print("    ");
        }

        private void tab(int amount){
            for (int i = 0; i < amount; i++) {
                tab();
            }
        }

    }
}
