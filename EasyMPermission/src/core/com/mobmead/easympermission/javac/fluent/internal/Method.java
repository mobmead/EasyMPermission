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

package com.mobmead.easympermission.javac.fluent.internal;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.JCTypeParameter;
import com.sun.tools.javac.tree.JCTree.JCModifiers;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

import lombok.javac.JavacNode;

/**
 * Class to build method
 */
public class Method {
    public interface IModifier {
        IReturnType modifiers(long modifiers);
        IReturnType modifiers(JCModifiers modifiers);
    }

    public interface IReturnType {
        IName returnType(JCExpression type);
    }

    public interface IName {
        IParamType name(String name);
    }

    public interface IParamType {
        IParameter paramType(List<JCTypeParameter> typeList);
        IParameter paramType();
    }

    public interface IParameter {
        IThrown parameters(List<JCVariableDecl> paramList);
        IThrown parameters();
    }

    public interface IThrown {
        IBody thrown(List<JCExpression> thrownList);
        IBody thrown();
    }

    public interface IBody {
        IBuilder body(JCBlock body);
    }

    public interface IBuilder {
        JCMethodDecl build();
    }

    public static class Builder implements
            IModifier, IReturnType, IName, IParamType, IParameter, IThrown, IBody, IBuilder {

        private final JavacNode typeNode;

        private JCModifiers modifiers;
        private Name name;
        private List<JCTypeParameter> typeList = List.<JCTypeParameter>nil();
        private List<JCVariableDecl> paramList = List.<JCVariableDecl>nil();
        private JCExpression returnType;
        private JCBlock body;
        private List<JCExpression> thrownList = List.<JCExpression>nil();

        public Builder(JavacNode typeNode) {
            this.typeNode = typeNode;
        }

        @Override
        public IReturnType modifiers(long modifiers) {
            this.modifiers = typeNode.getTreeMaker().Modifiers(modifiers, List.<JCTree.JCAnnotation>nil());
            return this;
        }

        @Override
        public IReturnType modifiers(JCModifiers modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        @Override
        public IParamType name(String name) {
            this.name = typeNode.toName(name);
            return this;
        }

        @Override
        public IParameter paramType(List<JCTypeParameter> typeList) {
            this.typeList = typeList;
            return this;
        }

        @Override
        public IParameter paramType() {
            this.typeList = List.<JCTypeParameter>nil();
            return this;
        }

        @Override
        public IThrown parameters(List<JCVariableDecl> paramList) {
            this.paramList = paramList;
            return this;
        }

        @Override
        public IThrown parameters() {
            this.paramList = List.nil();
            return this;
        }

        @Override
        public IName returnType(JCExpression type) {
            this.returnType = type;
            return this;
        }

        @Override
        public IBuilder body(JCBlock body) {
            this.body = body;
            return this;
        }

        @Override
        public IBody thrown(List<JCExpression> thrownList) {
            this.thrownList.appendList(thrownList);
            return this;
        }

        @Override
        public IBody thrown() {
            return thrown(List.<JCExpression>nil());
        }

        @Override
        public JCMethodDecl build() {

            return typeNode.getTreeMaker().MethodDef(
                    modifiers,
                    name,
                    returnType,
                    typeList,
                    paramList,
                    thrownList,
                    body,
                    null);
        }

    }

}
