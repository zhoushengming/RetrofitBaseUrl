package com.ericcode;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by xiaoming on 2017/7/27.
 */

public class ProxyInfo {
    private String packageName;
    private String proxyClassName;
    private TypeElement typeElement;

    public Map<String, ExecutableElement> injectVariables = new HashMap<>();

    public static final String PROXY = "BaseUrl";

    public ProxyInfo(Elements elementUtils, TypeElement classElement) {
        this.typeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        String packageName = packageElement.getQualifiedName().toString();
        //classname
        String className = ClassValidator.getClassName(classElement, packageName);
        this.packageName = packageName;
        this.proxyClassName = className + "$$" + PROXY;
    }


    public String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(packageName).append(";\n\n");
        builder.append("// Generated code. Do not modify!\n");
        builder.append('\n');

        builder.append("public final class ").append(proxyClassName);
        builder.append(" {\n");

        generateMethods(builder);
        builder.append('\n');

        builder.append("}\n");
        return builder.toString();

    }


    private void generateMethods(StringBuilder builder) {
        for (String url : injectVariables.keySet()) {
            ExecutableElement element = injectVariables.get(url);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            builder.append("\n\tString data[]");
            builder.append(" = ");
            builder.append("new String[]{");
            builder.append("\"" + url + "\",");
            builder.append("\"" + name + "\",");
            builder.append("\"" + type + "\"");
            builder.append("};\n");
        }
    }

    public String getProxyClassFullName() {
        return packageName + "." + proxyClassName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }
}
