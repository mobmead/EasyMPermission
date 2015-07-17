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

import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.util.Name;
import lombok.javac.JavacNode;

/**
 * Class to build variable
 */
public class Variable {

    public interface IModifier {
        IName modifiers(long modifiers);
    }

    public interface IName {
        IType name(String name);
    }

    public interface IType {
        IValue type(JCExpression type);
    }

    public interface IValue {
        IBuilder value(JCExpression value);
    }

    public interface IBuilder {
        JCVariableDecl build();
    }

    public static class Builder implements IModifier, IName, IType, IValue, IBuilder {

        private final JavacNode typeNode;

        private long modifiers;
        private Name name;
        private JCExpression type;
        private JCExpression value;

        public Builder(JavacNode typeNode) {
            this.typeNode = typeNode;
        }

        @Override
        public IName modifiers(long modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        @Override
        public IType name(String name) {
            this.name = typeNode.toName(name);
            return this;
        }

        @Override
        public IValue type(JCExpression type) {
            this.type = type;
            return this;
        }

        @Override
        public IBuilder value(JCExpression value) {
            this.value = value;
            return this;
        }

        @Override
        public JCVariableDecl build() {
            return typeNode.getTreeMaker().VarDef(
                    typeNode.getTreeMaker().Modifiers(modifiers),
                    name,
                    type,
                    value);
        }
    }
}
