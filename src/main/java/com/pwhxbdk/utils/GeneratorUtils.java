package com.pwhxbdk.utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.pwhxbdk.utils.Constant.*;

/**
 * @Author: pwhxbdk
 * @Date: 2020/7/15 15:32
 */
public class GeneratorUtils {



    private final Project project;
    private final PsiFile psiFile;
    private final PsiElementFactory elementFactory;
    /**
     * 选中文本
     * 选中文本的作用：
     * 用来标识操作单个方法
     */
    private final String selectionText;

    /**
     * 生成注解工具类
     * @param project 项目名称
     * @param psiFile 类文件
     * @param elementFactory
     * @param selectionText 选中文本
     */
    public GeneratorUtils(Project project, PsiFile psiFile, PsiElementFactory elementFactory, String selectionText) {
        this.project = project;
        this.psiFile = psiFile;
        this.elementFactory = elementFactory;
        this.selectionText = selectionText;
    }

    public void doGenerate() {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            boolean selection = false;
            if (StringUtils.isNotEmpty(selectionText)) {
                selection = true;
            }
            // 遍历当前对象的所有属性
            for (PsiElement psiElement : psiFile.getChildren()) {
                // 判断是类
                if (psiElement instanceof PsiClass){
                    PsiClass psiClass = (PsiClass) psiElement;
                    boolean isController = this.isController(psiClass);
                    // 执行选中部分
                    if (selection) {
                        this.generateSelection(psiClass, selectionText, isController);
                        break;
                    }
                    // 获取注释
                    this.generateClassAnnotation(psiClass,isController);
                    // 是Controller的话需要给方法也生成对应的注解
                    if (isController) {
                        // 类方法列表
                        PsiMethod[] methods = psiClass.getMethods();
                        for (PsiMethod psiMethod : methods) {
                            this.generateMethodAnnotation(psiMethod);
                        }
                    } else {
                        // 类属性列表
                        PsiField[] field = psiClass.getAllFields();
                        for (PsiField psiField : field) {
                            this.generateFieldAnnotation(psiField);
                        }
                    }
                }
            }
        });
    }

    /**
     * 写入到文件
     * @param name 注解名
     * @param qualifiedName 注解全包名
     * @param annotationText 生成注解文本
     * @param psiModifierListOwner 当前写入对象
     */
    private void doWrite(String name, String qualifiedName, String annotationText, PsiModifierListOwner psiModifierListOwner) {
        PsiAnnotation psiAnnotationDeclare = elementFactory.createAnnotationFromText(annotationText, psiModifierListOwner);
        final PsiNameValuePair[] attributes = psiAnnotationDeclare.getParameterList().getAttributes();
        PsiAnnotation existAnnotation = psiModifierListOwner.getModifierList().findAnnotation(qualifiedName);
        if (existAnnotation != null) {
            existAnnotation.delete();
        }
        addImport(elementFactory, psiFile, name);
        PsiAnnotation psiAnnotation = psiModifierListOwner.getModifierList().addAnnotation(name);
        for (PsiNameValuePair pair : attributes) {
            psiAnnotation.setDeclaredAttributeValue(pair.getName(), pair.getValue());
        }
    }

    /**
     * 类是否为controller
     * 为选中的部分生成注解
     * @param psiClass 类元素
     * @param selectionText 选中元素
     * @param isController 是否是Controller类
     */
    private void generateSelection(PsiClass psiClass, String selectionText, boolean isController) {
        // 选中的部分是类名
        if (Objects.equals(selectionText, psiClass.getName())) {
            this.generateClassAnnotation(psiClass,isController);
        }
        // 生成方法对应的注解
        PsiMethod[] methods = psiClass.getMethods();
        for (PsiMethod psiMethod : methods) {
            if (Objects.equals(selectionText, psiMethod.getName())) {
                this.generateMethodAnnotation(psiMethod);
                return;
            }
        }
        // 生成属性对应的注解
        PsiField[] field = psiClass.getAllFields();
        for (PsiField psiField : field) {
            if (Objects.equals(selectionText, psiField.getNameIdentifier().getText())) {
                this.generateFieldAnnotation(psiField);
                return;
            }
        }
    }

    /**
     * 类是否为controller
     * @param psiClass 类元素
     * @return boolean
     */
    private boolean isController(PsiClass psiClass) {
        PsiAnnotation[] psiAnnotations = psiClass.getModifierList().getAnnotations();
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            if (CONTROLLER_ANNOTATION.equals(psiAnnotation.getQualifiedName())
                    || REST_CONTROLLER_ANNOTATION.equals(psiAnnotation.getQualifiedName())) {
                // controller
                return true;
            }
        }
        return false;
    }

    /**
     * 获取RequestMapping注解属性
     * @param psiAnnotations 注解元素数组
     * @param attributeName 属性名
     * @return String 属性值
     */
    private String getMappingAttribute(PsiAnnotation[] psiAnnotations, String attributeName) {
        for (PsiAnnotation psiAnnotation : psiAnnotations) {
            switch (Objects.requireNonNull(psiAnnotation.getQualifiedName())) {
                case REQUEST_MAPPING_ANNOTATION:
                    return getAttribute(psiAnnotation, attributeName);
                case POST_MAPPING_ANNOTATION:
                    return "POST";
                case GET_MAPPING_ANNOTATION:
                    return "GET";
                case DELETE_MAPPING_ANNOTATION:
                    return "DELETE";
                case PATCH_MAPPING_ANNOTATION:
                    return "PATCH";
                case PUT_MAPPING_ANNOTATION:
                    return "PUT";
                default:break;
            }
        }
        return "";
    }

    /**
     * 获取注解属性
     * @param psiAnnotation 注解全路径
     * @param attributeName 注解属性名
     * @return 属性值
     */
    private String getAttribute(PsiAnnotation psiAnnotation, String attributeName) {
        if (Objects.isNull(psiAnnotation)) {
            return "";
        }
        PsiAnnotationMemberValue psiAnnotationMemberValue = psiAnnotation.findDeclaredAttributeValue(attributeName);
        if (Objects.isNull(psiAnnotationMemberValue)) {
            return "";
        }
        return psiAnnotationMemberValue.getText();
    }

    /**
     * 生成类注解
     * @param psiClass 类元素
     * @param isController 是否为controller
     */
    private void generateClassAnnotation(PsiClass psiClass, boolean isController){
        // 获取注解
        PsiDocComment docComment = psiClass.getDocComment();
        String annotationFromText;
        String annotation;
        String qualifiedName;
        String fieldValue = "";
        // 处理普通类和MVC类的区别
        if (isController) {
            annotation = SWAGGER_ANNOTATIONS_NAME_API;
            qualifiedName = SWAGGER_ANNOTATIONS_API;
            fieldValue = this.getMappingAttribute(psiClass.getModifierList().getAnnotations(),MAPPING_VALUE);
        } else {
            annotation = SWAGGER_ANNOTATIONS_NAME_API_MODEL;
            qualifiedName = SWAGGER_ANNOTATIONS_API_MODEL;
        }
        // 处理有无注释的
        if (Objects.isNull(docComment)){
            if (isController) {
                annotationFromText = String.format("@%s(value = %s)",annotation,fieldValue);
            } else {
                annotationFromText = String.format("@%s", annotation);
            }
        }else {
            // 注释的内容
            String tmpText = docComment.getText();
            String commentDesc = CommentUtils.getCommentDesc(tmpText);
            if (isController) {
                annotationFromText = String.format("@%s(value = %s, tags = {\"%s\"})",annotation,fieldValue,commentDesc);
            } else {
                annotationFromText = String.format("@%s(value = \"%s\")", annotation, commentDesc);
            }
        }

        this.doWrite(annotation, qualifiedName, annotationFromText, psiClass);
    }

    /**
     * 生成方法注解
     * @param psiMethod 类方法元素
     */
    private void generateMethodAnnotation(PsiMethod psiMethod){
        PsiAnnotation[] psiAnnotations = psiMethod.getModifierList().getAnnotations();
        String methodValue = this.getMappingAttribute(psiAnnotations,MAPPING_METHOD);
        if (StringUtils.isNotEmpty(methodValue)) {
            methodValue = methodValue.substring(methodValue.indexOf(".")+1);
        }
        // 如果存在注解，获取注解原本的value和notes内容
        PsiAnnotation apiOperationExist = psiMethod.getModifierList().findAnnotation(SWAGGER_ANNOTATIONS_API_OPERATION);
        String apiOperationAttrValue = "\"\"";
        String apiOperationAttrNotes = "\"\"";
        if (!Objects.isNull(apiOperationExist)){
            apiOperationAttrValue = this.getAttribute(apiOperationExist,"value");
            apiOperationAttrNotes = this.getAttribute(apiOperationExist,"notes");
        }
        if(StringUtils.isBlank(apiOperationAttrValue)){
            apiOperationAttrValue = "\"\"";
        }
        if (StringUtils.isBlank(apiOperationAttrNotes)){
            apiOperationAttrNotes = "\"\"";
        }

        PsiDocComment docComment = psiMethod.getDocComment();
        if (null != docComment){
            String text = docComment.getText();
            if (StringUtils.isNotBlank(text)){
                String commentDesc = CommentUtils.getCommentDesc(text);
                if (StringUtils.equals(apiOperationAttrNotes,"\"\"")){
                    apiOperationAttrNotes = "\""+commentDesc+"\"";
                }
                if (StringUtils.equals(apiOperationAttrValue,"\"\"")){
                    apiOperationAttrValue = "\""+commentDesc+"\"";
                }
            }
        }

        String apiOperationAnnotationText = String.format("@%s(value = %s, notes = %s,httpMethod = \"%s\")", SWAGGER_ANNOTATIONS_NAME_API_OPERATION,apiOperationAttrValue, apiOperationAttrNotes, methodValue);
        geneerateMethodParamsAnnotation(psiMethod);
        this.doWrite(SWAGGER_ANNOTATIONS_NAME_API_OPERATION, SWAGGER_ANNOTATIONS_API_OPERATION, apiOperationAnnotationText, psiMethod);

    }

    /**
     * 生成方法参数对应的注解
     * @param psiMethod
     */
    private void geneerateMethodParamsAnnotation(PsiMethod psiMethod) {
        String apiImplicitParamsAnnotationText = null;
        // 获取参数列表
        PsiParameter[] psiParameters = psiMethod.getParameterList().getParameters();
        // 创建参数列表对应的集合
        List<String> apiImplicitParamList = new ArrayList<>(psiParameters.length);
        for (PsiParameter psiParameter : psiParameters) {
            // 获取数据类型
            PsiType psiType = psiParameter.getType();
            // 获取源数据类型
            String dataType = CommentUtils.getDataType(psiType.getCanonicalText(), psiType);
            if (StringUtils.isEmpty(dataType)) {
                continue;
            }
            String paramType = "query";
            if (Objects.equals(dataType,"file")) {
                paramType = "form";
            }

            for (PsiAnnotation psiAnnotation : psiParameter.getModifierList().getAnnotations()) {
                if (StringUtils.isEmpty(psiAnnotation.getQualifiedName())) {
                    break;
                }
                switch (psiAnnotation.getQualifiedName()) {
                    case REQUEST_HEADER_TEXT:
                        paramType = "header";
                        break;
                    case REQUEST_PARAM_TEXT:
                        paramType = "query";
                        break;
                    case PATH_VARIABLE_TEXT:
                        paramType = "path";
                        break;
                    case REQUEST_BODY_TEXT:
                        paramType = "body";
                        break;
                    default:
                        break;
                }
            }
            String apiImplicitParamText =
                    String.format("@%s(paramType = \"%s\", dataType = \"%s\", name = \"%s\", value = \"\")",SWAGGER_ANNOTATIONS_NAME_API_IMPLICIT_PARAM,paramType,dataType,psiParameter.getNameIdentifier().getText());
            apiImplicitParamList.add(apiImplicitParamText);
        }
        if (apiImplicitParamList.size() != 0) {
            apiImplicitParamsAnnotationText = apiImplicitParamList.stream().collect(Collectors.joining(",\n", "@ApiImplicitParams({\n", "\n})"));
        }
        if (StringUtils.isNotBlank(apiImplicitParamsAnnotationText)){
            this.doWrite(SWAGGER_ANNOTATIONS_NAME_API_IMPLICIT_PARAMS, SWAGGER_ANNOTATIONS_API_IMPLICIT_PARAMS, apiImplicitParamsAnnotationText, psiMethod);
        }
        WriteCommandAction.runWriteCommandAction(project, () -> addImport(elementFactory, psiFile, SWAGGER_ANNOTATIONS_NAME_API_IMPLICIT_PARAMS));
        WriteCommandAction.runWriteCommandAction(project, () -> addImport(elementFactory, psiFile, SWAGGER_ANNOTATIONS_NAME_API_IMPLICIT_PARAM));
    }

    /**
     * 生成属性注解
     * @param psiField 类属性元素
     */
    private void generateFieldAnnotation(PsiField psiField){
        PsiComment classComment = null;
        for (PsiElement tmpEle : psiField.getChildren()) {
            if (tmpEle instanceof PsiComment) {
                classComment = (PsiComment) tmpEle;
                // 注释的内容
                String tmpText = classComment.getText();
                String commentDesc = CommentUtils.getCommentDesc(tmpText);
                String apiModelPropertyText = String.format("@%s(value=\"%s\")",SWAGGER_ANNOTATIONS_NAME_API_MODEL_PROPERTY,commentDesc);
                this.doWrite(SWAGGER_ANNOTATIONS_NAME_API_MODEL_PROPERTY, SWAGGER_ANNOTATIONS_API_MODEL_PROPERTY, apiModelPropertyText, psiField);
            }
        }
        if (Objects.isNull(classComment)) {
            this.doWrite(SWAGGER_ANNOTATIONS_NAME_API_MODEL_PROPERTY, SWAGGER_ANNOTATIONS_API_MODEL_PROPERTY, "@ApiModelProperty(\"\")", psiField);
        }
    }

    /**
     * 导入类依赖
     * @param elementFactory 元素Factory
     * @param file 当前文件对象
     * @param className 类名
     */
    private void addImport(PsiElementFactory elementFactory, PsiFile file, String className) {
        if (!(file instanceof PsiJavaFile)) {
            return;
        }
        final PsiJavaFile javaFile = (PsiJavaFile) file;
        // 获取所有导入的包
        final PsiImportList importList = javaFile.getImportList();
        if (importList == null) {
            return;
        }
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(className, GlobalSearchScope.allScope(project));
        // 待导入类有多个同名类或没有时 让用户自行处理
        if (psiClasses.length != 1) {
            return;
        }
        PsiClass waiteImportClass = psiClasses[0];
        for (PsiImportStatementBase is : importList.getAllImportStatements()) {
            String impQualifiedName = is.getImportReference().getQualifiedName();
            if (waiteImportClass.getQualifiedName().equals(impQualifiedName)) {
                // 已经导入
                return;
            }
        }
        importList.add(elementFactory.createImportStatement(waiteImportClass));
    }
}
