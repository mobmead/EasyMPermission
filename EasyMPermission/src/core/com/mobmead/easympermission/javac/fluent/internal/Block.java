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

import com.sun.tools.javac.tree.JCTree.JCStatement;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.util.List;

import com.sun.tools.javac.util.ListBuffer;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;

/**
 * Class to build block
 */
public class Block {

    public interface IAdd {
        IAdd add(JCStatement statement);
        IAdd add(List<JCStatement> statementList);
        IBuilder last(JCStatement statement);
        IBuilder last(List<JCStatement> statementList);
    }

    public interface IBuilder {
        JCBlock build();
    }

    public static class Builder implements IAdd, IBuilder {

        private final JavacTreeMaker treeMaker;

        private ListBuffer<JCStatement> statementList = new ListBuffer<JCStatement>();

        public Builder(JavacNode typeNode) {
            this.treeMaker = typeNode.getTreeMaker();
        }

        @Override
        public IAdd add(JCStatement statement) {
            return add(List.<JCStatement>of(statement));
        }

        @Override
        public IAdd add(List<JCStatement> statementList) {
            this.statementList.appendList(statementList);
            return this;
        }

        @Override
        public IBuilder last(JCStatement statement) {
            return last(List.<JCStatement>of(statement));
        }

        @Override
        public IBuilder last(List<JCStatement> statementList) {
            add(statementList);
            return this;
        }

        @Override
        public JCBlock build() {
            return treeMaker.Block(0, statementList.toList());

        }
    }
}
