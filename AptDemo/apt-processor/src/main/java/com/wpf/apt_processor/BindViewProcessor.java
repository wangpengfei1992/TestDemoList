package com.wpf.apt_processor;

import com.google.auto.service.AutoService;
import com.wpf.apt_annotation.BindView;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

@AutoService(BindView.class)
@SuppressWarnings("unused")
public class BindViewProcessor extends AbstractProcessor {
    public Elements mElementUtils;
    public Map<String,ClassCreatorFactory> mClassCreatorFactoryMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //这个注解处理器是给哪个注解用的
        HashSet<String> supportType = new LinkedHashSet<>();
        supportType.add(BindView.class.getCanonicalName());
        return supportType;
    }



    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mClassCreatorFactoryMap.clear();
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element:elementSet) {
            //转换为VariableElement，VariableElement为Element的子类
            VariableElement variableElement = (VariableElement) element;
            //可以获取类的信息的element，也是element的子类
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            //获取包名加类名
            String fullName = classElement.getQualifiedName().toString();
            //保存到集合中
            ClassCreatorFactory creatorFactory = mClassCreatorFactoryMap.get(fullName);
            if (creatorFactory == null){
                creatorFactory = new ClassCreatorFactory(mElementUtils,classElement);
                mClassCreatorFactoryMap.put(fullName,creatorFactory);
            }
            BindView bindView = variableElement.getAnnotation(BindView.class);
            int id = bindView.value();
            creatorFactory.putElement(id,variableElement);
        }
        //开始创建java类
        for (String key:mClassCreatorFactoryMap.keySet()) {
            ClassCreatorFactory factory = mClassCreatorFactoryMap.get(key);
            try {
                JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(
                        factory.getClassFullName(),factory.getTypeElement());
                Writer writer = javaFileObject.openWriter();
                writer.write(factory.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        return false;
    }
}