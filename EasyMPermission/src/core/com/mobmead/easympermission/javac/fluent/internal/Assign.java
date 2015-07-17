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

import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;

/**
 * Class to build assign statement
 */
public class Assign {

    public interface ILeft {
        IRight left(JCExpression expression);
    }

    public interface IRight {
        Builder right(JCExpression expression);
    }

    public interface IBuilder {
        JCAssign build();
    }

    public static class Builder implements ILeft, IRight, IBuilder {

        private final JavacTreeMaker treeMaker;
        private JCExpression leftExpression;
        private JCExpression rightExpression;

        public Builder(JavacNode typeNode) {
            this.treeMaker = typeNode.getTreeMaker();
        }

        @Override
        public IRight left(JCExpression expression) {
            this.leftExpression = expression;
            return this;
        }

        @Override
        public Builder right(JCExpression expression) {
            this.rightExpression = expression;
            return this;
        }

        @Override
        public JCAssign build() {
            return treeMaker.Assign(leftExpression, rightExpression);
        }
    }
}
