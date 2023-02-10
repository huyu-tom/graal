/*
 * Copyright (c) 2012, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oracle.truffle.api.dsl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.oracle.truffle.api.Assumption;
import com.oracle.truffle.api.ContextLocal;
import com.oracle.truffle.api.ContextThreadLocal;
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy;
import com.oracle.truffle.api.TruffleLanguage.LanguageReference;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.Node;

/**
 * Methods annotated with {@link Idempotent} must be methods that may cause side effects but will
 * always cause the same side effects for repeated invocations with the same parameters. From this
 * follows that an idempotent method must return the same result after it was executed once for each
 * consecutive execution given the same parameters.
 * <p>
 * Truffle DSL uses this property to execute {@link Specialization#guards() guards} where all bound
 * methods are idempotent once on the slow-path, during specialization, and never on the fast-path.
 * By default, all methods that do not bind dynamic parameters are interpreted as idempotent for
 * compatibility reasons. The DSL emits warnings whenever a method should be annotated either with
 * {@link Idempotent} or {@link NonIdempotent}. Note that guards that bind dynamic parameters will
 * never be idempotent in the fast-path, so the DSL will not require annotations for such guards.
 * <p>
 *
 * Examples for non-idempotent methods are:
 * <ul>
 * <li>{@link ContextThreadLocal#get()}
 * <li>{@link Assumption#isValid()}
 * <li>{@link Assumption#isValidAssumption(Assumption)}
 * <li>{@link Assumption#isValidAssumption(Assumption[])}
 * <li>{@link ContextLocal#get()} if the language uses a context policy {@link ContextPolicy#REUSE}
 * or {@link ContextPolicy#SHARED}
 * </ul>
 *
 * Examples for idempotent methods are:
 * <ul>
 * <li>{@link LanguageReference#get(Node)}
 * <li>{@link DirectCallNode#getCallTarget()
 * <li>{@link ContextLocal#get()} if the language uses context policy
 * {@link ContextPolicy#EXCLUSIVE}.
 * </ul>
 * <p>
 * Note that usages may assume that the current language instance never changes for a given Node.
 * Therefore it is safe to assume language reference accesses idempotent.
 *
 * @see NonIdempotent
 * @since 23.0
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface Idempotent {

}
