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

import com.sun.tools.javac.tree.JCTree.JCIf;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;

/**
 * Class to build if statement
 */
public class If {

    public interface ICondition {
        IThen condition(JCExpression expression);
    }

    public interface IThen {
        IElse withThen(JCBlock block);
        IBuilder onlyThen(JCBlock block);
    }

    public interface IElse {
        IBuilder withElse(JCBlock block);
    }

    public interface IBuilder {
        JCIf build();
    }

    public static class Builder implements ICondition, IThen, IElse, IBuilder {

        private final JavacTreeMaker treeMaker;

        private JCExpression condition;
        private JCBlock thenBlock;
        private JCBlock elseBlock;

        public Builder(JavacNode typeNode) {
            this.treeMaker = typeNode.getTreeMaker();
        }

        @Override
        public IThen condition(JCExpression expression) {
            this.condition = expression;
            return this;
        }

        @Override
        public IBuilder withElse(JCBlock block) {
            this.elseBlock = block;
            return this;
        }

        @Override
        public IElse withThen(JCBlock block) {
            this.thenBlock = block;
            return this;
        }

        @Override
        public IBuilder onlyThen(JCBlock block) {
            this.thenBlock = block;
            return this;
        }

        public JCIf build() {
            return treeMaker.If(condition, thenBlock, elseBlock);
        }
    }
}
