/*
 * Copyright (C) 2015 Jian Chen <jian@mobmead.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.mobmead.easympermission.javac.handlers;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import lombok.core.AnnotationValues;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;
import org.mangosdk.spi.ProviderFor;

import com.sun.tools.javac.tree.JCTree.*;

import com.mobmead.easympermission.Permission;
import com.mobmead.easympermission.RuntimePermission;

import static com.mobmead.easympermission.javac.fluent.Builder.*;

import static lombok.javac.Javac.*;
import static lombok.javac.handlers.JavacHandlerUtil.*;

/**
 * Handles the {@code easympermission.RuntimePermission} annotation for javac.
 */
@ProviderFor(JavacAnnotationHandler.class)
public class HandleRuntimePermission extends JavacAnnotationHandler<RuntimePermission> {

    private final String MESSAGE_PERMISSIONS_WERE_NOT_GRANTED = "Permissions were not granted.";
    private final String MESSAGE_PERMISSIONS_HAVE_BEEN_GRANTED = "Permissions have been granted.";
    private final int FIRST_PERMISSION_REQUEST_CODE = 1000;
    private final String IS_PERMISSION_GRANTED_METHOD = "isPermissionGranted$";

    @Override
    public void handle(AnnotationValues<RuntimePermission> annotation, JCAnnotation ast, JavacNode annotationNode) {

        deleteAnnotationIfNeccessary(annotationNode, RuntimePermission.class);
        JavacNode typeNode = annotationNode.up();
        boolean notAClass = !isClass(typeNode);

        if (notAClass) {
            annotationNode.addError("@RuntimePermission is only supported on a class.");
            return;
        }

        JCMethodDecl method;

        List<PermissionAnnotatedItem> permissionList = findAllAnnotatedMethods(typeNode);
        for (PermissionAnnotatedItem item : permissionList) {
            method = createPermissionCheckedMethod(typeNode, item);
            removeMethod(typeNode, item.getMethod());
            injectMethod(typeNode, recursiveSetGeneratedBy(method, annotationNode.get(), typeNode.getContext()));
        }

        method = recursiveSetGeneratedBy(createIsPermissionGrantedMethod(typeNode), annotationNode.get(), typeNode.getContext());
        injectMethod(typeNode, method);

        method = recursiveSetGeneratedBy(createOnRequestPermissionMethod(typeNode, permissionList), annotationNode.get(), typeNode.getContext());
        injectMethod(typeNode, method);
    }

    private JCMethodDecl createOnRequestPermissionMethod(JavacNode typeNode, List<PermissionAnnotatedItem> permissionList) {
        JavacTreeMaker treeMaker = typeNode.getTreeMaker();

        ListBuffer<JCStatement> statements = new ListBuffer<JCStatement>();

        JCTree.JCExpression returnType = treeMaker.Type(Javac.createVoidType(treeMaker, CTC_VOID));

        List<JCVariableDecl> paramList = List.of(
            Variable(typeNode)
                    .modifiers(Flags.PARAMETER)
                    .name("requestCode")
                    .type(treeMaker.TypeIdent(CTC_INT))
                    .value(null)
                    .build(),
            Variable(typeNode)
                    .modifiers(Flags.PARAMETER)
                    .name("permissions")
                    .type(treeMaker.TypeArray(genJavaLangTypeRef(typeNode, "String")))
                    .value(null)
                    .build(),
            Variable(typeNode)
                    .modifiers(Flags.PARAMETER)
                    .name("grantResults")
                    .type(treeMaker.TypeArray(treeMaker.TypeIdent(CTC_INT)))
                    .value(null)
                    .build()
        );

        for (PermissionAnnotatedItem item : permissionList) {

            JCReturn jcReturn = treeMaker.Return(null);

            JCBinary cmpRequestCode = treeMaker.Binary(CTC_EQUAL,
                    treeMaker.Ident(typeNode.toName("requestCode")),
                    treeMaker.Literal(CTC_INT, item.getRequestCode()));

            JCBinary cmpGrantResult = treeMaker.Binary(CTC_NOT_EQUAL,
                    treeMaker.Indexed(treeMaker.Ident(typeNode.toName("grantResults")), treeMaker.Literal(CTC_INT, 0)),
                    JavacHandlerUtil.chainDots(typeNode, "android", "content", "pm", "PackageManager", "PERMISSION_GRANTED"));

            JCExpression toastMethod =
                    JavacHandlerUtil.chainDots(typeNode, "android", "widget", "Toast", "makeText");
            List<JCExpression> toastArgs = List.<JCTree.JCExpression>of(
                    treeMaker.Ident(typeNode.toName("this")),
                    treeMaker.Literal(MESSAGE_PERMISSIONS_WERE_NOT_GRANTED),
                    treeMaker.Literal(CTC_INT, 1));
            JCMethodInvocation printlnInvocation =
                    treeMaker.Apply(List.<JCTree.JCExpression>nil(), toastMethod, toastArgs);
            JCExpressionStatement thenPart = treeMaker.Exec(
                    treeMaker.Apply(List.<JCTree.JCExpression>nil(), treeMaker.Select(printlnInvocation, typeNode.toName("show")), List.<JCExpression>nil()));

            toastArgs = List.<JCTree.JCExpression>of(
                    treeMaker.Ident(typeNode.toName("this")),
                    treeMaker.Literal(MESSAGE_PERMISSIONS_HAVE_BEEN_GRANTED),
                    treeMaker.Literal(CTC_INT, 1));
            printlnInvocation =
                    treeMaker.Apply(List.<JCTree.JCExpression>nil(), toastMethod, toastArgs);
            JCExpressionStatement elsePart = treeMaker.Exec(
                    treeMaker.Apply(List.<JCTree.JCExpression>nil(), treeMaker.Select(printlnInvocation, typeNode.toName("show")), List.<JCExpression>nil()));

            JCIf jcIfGrantResult = If(typeNode)
                    .condition(cmpGrantResult)
                    .withThen(Block(typeNode).last(thenPart).build())
                    .withElse(Block(typeNode).last(elsePart).build())
                    .build();

            JCIf jcIfRequestCode = If(typeNode)
                    .condition(cmpRequestCode)
                    .onlyThen(Block(typeNode).add(jcIfGrantResult).last(jcReturn).build())
                    .build();

            statements.append(jcIfRequestCode);
        }

        JCBlock body = Block(typeNode)
                .last(statements.toList())
                .build();

        return Method(typeNode)
                .modifiers(Flags.PUBLIC)
                .returnType(returnType)
                .name("onRequestPermissionsResult")
                .paramType()
                .parameters(paramList)
                .thrown()
                .body(body)
                .build();
    }

    private List<PermissionAnnotatedItem> findAllAnnotatedMethods(JavacNode node) {
        int requestCode = FIRST_PERMISSION_REQUEST_CODE;
        ListBuffer<PermissionAnnotatedItem> permissionList = new ListBuffer<PermissionAnnotatedItem>();

        node = upToTypeNode(node);

        if (node != null && node.get() instanceof JCTree.JCClassDecl) {
           for (JCTree def : ((JCTree.JCClassDecl)node.get()).defs) {
                if (def instanceof JCMethodDecl) {
                    JCMethodDecl md = (JCMethodDecl) def;

                    List<JCAnnotation> annotations = md.getModifiers().getAnnotations();
                    if (annotations != null) {
                        for (JCAnnotation anno : annotations) {
                            if (anno.getAnnotationType() != null
                                    && anno.getAnnotationType().type.toString().equals(Permission.class.getName())) {
                                if (anno.getArguments().get(0) instanceof JCTree.JCAssign) {
                                    JCTree.JCAssign assign = (JCTree.JCAssign) anno.getArguments().get(0);
                                    PermissionAnnotatedItem item = new PermissionAnnotatedItem(md, requestCode,
                                            convertStringToList(assign.rhs.toString()));
                                    permissionList.append(item);
                                    requestCode++;
                                }
                            }
                        }
                    }
                }
            }
        }

        return permissionList.toList();
    }

    private List<String> convertStringToList(String input) {
        ListBuffer<String> list = new ListBuffer<String>();
        for (String a : input.replaceAll("^\\{+", "").replaceAll("\\}$", "").split(",")) {
            list.append(a.replaceAll("^\"+", "").replaceAll("\"$", ""));
        }
        return list.toList();
    }

    private JCMethodDecl createPermissionCheckedMethod(JavacNode typeNode, PermissionAnnotatedItem permissionAnnotatedItem) {
        JCBlock block = createPermissionCheckedBlock(typeNode, permissionAnnotatedItem);
        JCMethodDecl origMethod = permissionAnnotatedItem.getMethod();
        return Method(typeNode)
                .modifiers(origMethod.getModifiers())
                .returnType((JCExpression) origMethod.getReturnType())
                .name(origMethod.getName().toString())
                .paramType(origMethod.getTypeParameters())
                .parameters(origMethod.getParameters())
                .thrown(origMethod.getThrows())
                .body(block)
                .build();
    }

    private void removeMethod(JavacNode typeNode, JCMethodDecl method) {
        typeNode = upToTypeNode(typeNode);
        ListBuffer<JCTree> newList = new ListBuffer<JCTree>();
        for (JCTree def : ((JCTree.JCClassDecl)typeNode.get()).defs) {
            if (!(def instanceof JCMethodDecl && def == method)) {
                newList.append(def);
            }
        }
        ((JCTree.JCClassDecl)typeNode.get()).defs = newList.toList();
    }

    private JCMethodDecl createIsPermissionGrantedMethod(JavacNode typeNode) {
        JavacTreeMaker treeMaker = typeNode.getTreeMaker();

        ListBuffer<JCStatement> statements = new ListBuffer<JCStatement>();

        statements.append(Variable(typeNode)
                .modifiers(0)
                .name("ok")
                .type(treeMaker.TypeIdent(CTC_BOOLEAN))
                .value(treeMaker.Literal(CTC_BOOLEAN, 1))
                .build());

        statements.append(Variable(typeNode)
                .modifiers(0)
                .name("failed")
                .type(treeMaker.TypeIdent(CTC_BOOLEAN))
                .value(treeMaker.Literal(CTC_BOOLEAN, 0))
                .build());

        JCTree.JCExpression returnType = treeMaker.TypeIdent(CTC_BOOLEAN);

        List<JCTypeParameter> typleList = List.nil();

        List<JCVariableDecl> paramList = List.of(
                Variable(typeNode)
                        .modifiers(Flags.PARAMETER)
                        .name("permissions")
                        .type(treeMaker.TypeArray(genJavaLangTypeRef(typeNode, "String")))
                        .value(null)
                        .build()
        );

        JCVariableDecl v = Variable(typeNode)
                .modifiers(Flags.PARAMETER)
                .name("permission")
                .type(genJavaLangTypeRef(typeNode, "String"))
                .value(null)
                .build();

        JCExpression checkMethod = JavacHandlerUtil.chainDots(typeNode, "this", "checkSelfPermission");
        List<JCExpression> checkArgs = List.<JCExpression>of(treeMaker.Ident(typeNode.toName("permission")));
        JCMethodInvocation checkInvocation = treeMaker.Apply(List.<JCExpression>nil(), checkMethod, checkArgs);
        JCExpression cmp = treeMaker.Binary(CTC_NOT_EQUAL,
                checkInvocation,
                JavacHandlerUtil.chainDots(typeNode, "android", "content", "pm", "PackageManager", "PERMISSION_GRANTED"));

        JCBlock body = Block(typeNode)
                .last(treeMaker.Return(treeMaker.Ident(typeNode.toName("failed"))))
                .build();

        JCIf jcIf = If(typeNode)
                .condition(cmp)
                .onlyThen(body)
                .build();

        JCEnhancedForLoop block = treeMaker.ForeachLoop(v,
                treeMaker.Ident(typeNode.toName("permissions")),
                jcIf);
        statements.append(block);

        statements.append(treeMaker.Return(treeMaker.Ident(typeNode.toName("ok"))));

        body = Block(typeNode)
                .last(statements.toList())
                .build();

        return Method(typeNode)
                .modifiers(Flags.PRIVATE)
                .returnType(returnType)
                .name(IS_PERMISSION_GRANTED_METHOD)
                .paramType(typleList)
                .parameters(paramList)
                .thrown()
                .body(body)
                .build();
    }

    private JCBlock createPermissionCheckedBlock(JavacNode typeNode, PermissionAnnotatedItem permissionAnnotatedItem) {
        JavacTreeMaker treeMaker = typeNode.getTreeMaker();

        // Create permission list
        ListBuffer<JCExpression> permissionArgs = new ListBuffer<JCExpression>();
        for (String permission : permissionAnnotatedItem.getPermissions()) {
            permissionArgs.append(treeMaker.Literal(permission));
        }

        String permissionVarName = "_permissions_" + permissionAnnotatedItem.getMethod().getName();
        JCExpression v = treeMaker.NewArray(null,
                List.<JCExpression>nil(),
                permissionArgs.toList());
        JCVariableDecl permissions = Variable(typeNode)
                .modifiers(Flags.PRIVATE)
                .name(permissionVarName)
                .type(treeMaker.TypeArray(genJavaLangTypeRef(typeNode, "String")))
                .value(v)
                .build();
        injectField(typeNode, permissions);

        JCExpression checkMethod = JavacHandlerUtil.chainDots(typeNode, "this", IS_PERMISSION_GRANTED_METHOD);
        List<JCExpression> checkArgs = List.<JCExpression>of(treeMaker.Ident(typeNode.toName(permissionVarName)));
        JCMethodInvocation checkInvocation = treeMaker.Apply(List.<JCExpression>nil(), checkMethod, checkArgs);
        JCExpression cmp = treeMaker.Binary(CTC_EQUAL,
                checkInvocation,
                treeMaker.Literal(CTC_BOOLEAN, 1));

        JCExpression requestMethod = JavacHandlerUtil.chainDots(typeNode, "this", "requestPermissions");
        List<JCExpression> requestArgs = List.<JCExpression>of(
                treeMaker.Ident(typeNode.toName(permissionVarName)),
                treeMaker.Literal(CTC_INT, permissionAnnotatedItem.getRequestCode()));
        JCMethodInvocation requestInvocation = treeMaker.Apply(List.<JCExpression>nil(), requestMethod, requestArgs);
        JCBlock body = Block(typeNode)
                .last(treeMaker.Exec(requestInvocation))
                .build();

        JCIf jcIf = If(typeNode)
                .condition(cmp)
                .withThen(permissionAnnotatedItem.getMethod().getBody())
                .withElse(body)
                .build();

        return Block(typeNode)
                .last(jcIf)
                .build();
    }

    static boolean isClass(JavacNode typeNode) {
        return isClassAndDoesNotHaveFlags(typeNode, Flags.INTERFACE | Flags.ENUM | Flags.ANNOTATION);
    }

    static boolean isClassOrEnum(JavacNode typeNode) {
        return isClassAndDoesNotHaveFlags(typeNode, Flags.INTERFACE | Flags.ANNOTATION);
    }

    private class PermissionAnnotatedItem {
        private final int requestCode;
        private final List<String> permissions;
        private final JCMethodDecl method;

        public PermissionAnnotatedItem(JCMethodDecl method, int requestCode, List<String> permissions) {
            this.method = method;
            this.requestCode = requestCode;
            this.permissions = permissions;
        }

        public JCMethodDecl getMethod() {
            return method;
        }

        public List<String> getPermissions() {
            return permissions;
        }

        public int getRequestCode() {
            return requestCode;
        }
    }
}
