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

package com.mobmead.easympermission.javac.fluent;

import com.mobmead.easympermission.javac.fluent.internal.*;
import lombok.javac.JavacNode;

/**
 * Fluent java statement builder
 */
public class Builder {

    public static If.ICondition If(JavacNode typeNode) {
        return new If.Builder(typeNode);
    }

    public static Block.Builder Block(JavacNode typeNode) {
        return new Block.Builder(typeNode);
    }

    public static Assign.ILeft Assign(JavacNode typeNode) {
        return new Assign.Builder(typeNode);
    }

    public static Variable.IModifier Variable(JavacNode typeNode) {
        return new Variable.Builder(typeNode);
    }

    public static Method.IModifier Method(JavacNode typeNode) {
        return new Method.Builder(typeNode);
    }
}
