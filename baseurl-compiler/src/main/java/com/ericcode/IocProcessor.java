package com.ericcode;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class IocProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Map<String, ProxyInfo> mProxyMap = new HashMap<String, ProxyInfo>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        elementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BaseUrl.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedBaseUrl = roundEnv.getElementsAnnotatedWith(BaseUrl.class);
        for (Element element : elementsAnnotatedBaseUrl) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "element getKind:" + element.getKind(), element);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "element getSimpleName:" + element.getSimpleName(), element);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "element:" + element, element);
            checkAnnotationValid(element, BaseUrl.class);
            if (element.getKind() == ElementKind.METHOD) {

                if (element instanceof ExecutableElement) { //方法注解
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "element instanceof ExecutableElement", element);
                    ExecutableElement executableElement = (ExecutableElement) element;
                    //class type
                    TypeElement classElement = (TypeElement) executableElement.getEnclosingElement();
                    //full class name
                    String fqClassName = classElement.getQualifiedName().toString();
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "fqClassName:" + fqClassName, element);

                    ProxyInfo proxyInfo = mProxyMap.get(fqClassName);
                    if (proxyInfo == null) {
                        proxyInfo = new ProxyInfo(elementUtils, classElement);
                        mProxyMap.put(fqClassName, proxyInfo);
                    }

                    BaseUrl baseUrl = executableElement.getAnnotation(BaseUrl.class);
                    String url = baseUrl.value();

                    proxyInfo.injectVariables.put(url, executableElement);
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "url:" + url, element);

                } else if (element instanceof VariableElement) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "element instanceof ExecutableElement", element);
                } else if (element instanceof TypeElement) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "element instanceof TypeElement", element);
                }
            } else if (element.getKind() == ElementKind.ANNOTATION_TYPE) {

            }
        }
        for (String key : mProxyMap.keySet()) {
            ProxyInfo proxyInfo = mProxyMap.get(key);
            try {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        proxyInfo.getProxyClassFullName(),
                        proxyInfo.getTypeElement());
                Writer writer = jfo.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                error(proxyInfo.getTypeElement(),
                        "Unable to write injector for type %s: %s",
                        proxyInfo.getTypeElement(), e.getMessage());
            }

        }
        return true;
    }

    private boolean checkAnnotationValid(Element annotatedElement, Class clazz) {
        if (annotatedElement.getKind() != ElementKind.METHOD && annotatedElement.getKind() != ElementKind.ANNOTATION_TYPE) {
            error(annotatedElement, "%s must be declared on method or class.", clazz.getSimpleName());
            return false;
        }
//        if (ClassValidator.isPrivate(annotatedElement)) {
//            error(annotatedElement, "%s() must can not be private.", annotatedElement.getSimpleName());
//            return false;
//        }

        return true;
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }
}
