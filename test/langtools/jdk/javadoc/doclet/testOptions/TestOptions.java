/*
 * Copyright (c) 2013, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug      4749567 8071982 8175200 8186332 8185371 8182765 8217034
 * @summary  Test the output for -header, -footer, -nooverview, -nodeprecatedlist, -nonavbar, -notree,
 *           -stylesheetfile, --main-stylesheet, --add-stylesheet options.
 * @author   Bhavesh Patel
 * @library  ../../lib
 * @modules jdk.javadoc/jdk.javadoc.internal.tool
 * @build    javadoc.tester.*
 * @run main TestOptions
 */

import java.io.File;

import javadoc.tester.JavadocTester;

public class TestOptions extends JavadocTester {

    public static void main(String... args) throws Exception {
        TestOptions tester = new TestOptions();
        tester.runTests();
    }

    @Test
    public void testHeaderFooter() {
        javadoc("-d", "out-1",
                "-header", "Test header",
                "-footer", "Test footer",
                "-sourcepath", testSrc,
                "pkg");
        checkExit(Exit.OK);

        checkOutput("pkg/package-summary.html", true,
                "<div class=\"aboutLanguage\">Test header</div>",
                "<div class=\"aboutLanguage\">Test footer</div>");
    }

    @Test
    public void testNoOverview() {
        javadoc("-d", "out-4",
                "-nooverview",
                "-sourcepath", testSrc,
                "pkg", "deprecated");

        checkExit(Exit.OK);

        checkFiles(false, "overview-summary.html");
    }

    @Test
    public void testNoDeprecatedList() {
        javadoc("-d", "out-5",
                "-nodeprecatedlist",
                "-sourcepath", testSrc,
                "deprecated");
        checkExit(Exit.OK);

        checkFiles(false, "deprecated-list.html");
    }

    @Test
    public void testNoNavbar() {
        javadoc("-d", "out-6",
                "-nonavbar",
                "-bottom", "Bottom text",
                "-sourcepath", testSrc,
                "pkg");
        checkExit(Exit.OK);

        checkOutput("pkg/Foo.html", false, "navbar");
        checkOutput("pkg/Foo.html", true, "Bottom text");
    }

    @Test
    public void testNoTree() {
        javadoc("-d", "out-7",
                "-notree",
                "-sourcepath", testSrc,
                "pkg");
        checkExit(Exit.OK);

        checkFiles(false, "overview-tree.html");
        checkFiles(false, "pkg/package-tree.html");
        checkOutput("pkg/Foo.html", false, "<li><a href=\"package-tree.html\">Tree</a></li>");
    }

    @Test
    public void testStylesheetFile() {
        javadoc("-d", "out-8",
                "-stylesheetfile", new File(testSrc, "custom-stylesheet.css").getAbsolutePath(),
                "-sourcepath", testSrc,
                "pkg");
        checkExit(Exit.OK);

        checkOutput("custom-stylesheet.css", true, "Custom javadoc style sheet");
        checkOutput("pkg/Foo.html", true, "<link rel=\"stylesheet\" type=\"text/css\" "
                + "href=\"../custom-stylesheet.css\" title=\"Style\">");
    }

    @Test
    public void testStylesheetFileAltOption() {
        javadoc("-d", "out-stylesheet-file",
                "--main-stylesheet", new File(testSrc, "custom-stylesheet.css").getAbsolutePath(),
                "-sourcepath", testSrc,
                "pkg");
        checkExit(Exit.OK);

        checkOutput("custom-stylesheet.css", true, "Custom javadoc style sheet");
        checkOutput("pkg/Foo.html", true, "<link rel=\"stylesheet\" type=\"text/css\" "
                + "href=\"../custom-stylesheet.css\" title=\"Style\">");
    }

    @Test
    public void testAdditionalStylesheetFile() {
        javadoc("-d", "out-additional-css",
                "--add-stylesheet", new File(testSrc, "additional-stylesheet-1.css").getAbsolutePath(),
                "--add-stylesheet", new File(testSrc, "additional-stylesheet-2.css").getAbsolutePath(),
                "--add-stylesheet", new File(testSrc, "additional-stylesheet-3.css").getAbsolutePath(),
                "-sourcepath", testSrc,
                "pkg");
        checkExit(Exit.OK);

        checkOutput("additional-stylesheet-1.css", true, "Additional javadoc style sheet 1");
        checkOutput("additional-stylesheet-2.css", true, "Additional javadoc style sheet 2");
        checkOutput("additional-stylesheet-3.css", true, "Additional javadoc style sheet 3");
        checkOutput("pkg/Foo.html", true,
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"../additional-stylesheet-1.css\" title=\"Style\">\n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"../additional-stylesheet-2.css\" title=\"Style\">\n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"../additional-stylesheet-3.css\" title=\"Style\">");
    }

    @Test
    public void testInvalidStylesheetFile() {
        javadoc("-d", "out-invalid-css",
                "--main-stylesheet", new File(testSrc, "custom-stylesheet-1.css").getAbsolutePath(),
                "-sourcepath", testSrc,
                "pkg");
        checkExit(Exit.ERROR);

        checkOutput(Output.OUT, true,
                "javadoc: error - File not found:",
                "custom-stylesheet-1.css");
    }

    @Test
    public void testInvalidAdditionalStylesheetFiles() {
        javadoc("-d", "out-invalid-additional-css",
                "--add-stylesheet", new File(testSrc, "additional-stylesheet-4.css").getAbsolutePath(),
                "-sourcepath", testSrc,
                "pkg");
        checkExit(Exit.ERROR);

        checkOutput(Output.OUT, true,
                "javadoc: error - File not found:",
                "additional-stylesheet-4.css");
    }

    @Test
    public void testLinkSource() {
        javadoc("-d", "out-9",
                "-linksource",
                "-javafx",
                "--disable-javafx-strict-checks",
                "-sourcepath", testSrc,
                "-package",
                "linksource");
        checkExit(Exit.OK);
        checkLinks();
        checkOutput("linksource/AnnotationTypeField.html", true,
                "<pre>@Documented\npublic @interface <a href="
                + "\"../src-html/linksource/AnnotationTypeField.html#line.31\">"
                + "AnnotationTypeField</a></pre>",
                "<h3>DEFAULT_NAME</h3>\n"
                + "<a id=\"DEFAULT_NAME\">\n"
                + "<!--   -->\n"
                + "</a>\n"
                + "<div class=\"memberSignature\"><span class=\"modifiers\">static final</span>&nbsp;"
                + "<span class=\"returnType\">java.lang.String</span>&nbsp;<span class=\"memberName\">"
                + "<a href=\"../src-html/linksource/AnnotationTypeField.html#line.32\">DEFAULT_NAME</a></span></div>",
                "<h3>name</h3>\n"
                + "<a id=\"name()\">\n"
                + "<!--   -->\n"
                + "</a>\n"
                + "<div class=\"memberSignature\"><span class=\"returnType\">java.lang.String</span>&nbsp;"
                + "<span class=\"memberName\"><a href=\"../src-html/linksource/AnnotationTypeField.html#line.34\">"
                + "name</a></span></div>");

        checkOutput("src-html/linksource/AnnotationTypeField.html", true,
                "<title>Source code</title>",
                "<span class=\"sourceLineNo\">031</span><a id=\"line.31\">"
                + "@Documented public @interface AnnotationTypeField {</a>");

        checkOutput("linksource/Properties.html", true,
                "<pre>public class <a href=\"../src-html/linksource/Properties.html#line.29\">"
                + "Properties</a>",
                "<div class=\"memberSignature\"><span class=\"modifiers\">public</span>&nbsp;"
                + "<span class=\"returnType\">java.lang.Object</span>&nbsp;<span class=\"memberName\">"
                + "<a href=\"../src-html/linksource/Properties.html#line.31\">someProperty</a></span></div>");

        checkOutput("src-html/linksource/Properties.html", true,
                "<title>Source code</title>",
                "<span class=\"sourceLineNo\">031</span><a id=\"line.31\">    "
                + "public Object someProperty() {</a>");

        checkOutput("linksource/SomeClass.html", true,
                "<pre>public class <a href=\"../src-html/linksource/SomeClass.html#line.29\">"
                + "SomeClass</a>\nextends java.lang.Object</pre>",
                "<div class=\"memberSignature\"><span class=\"modifiers\">public</span>&nbsp;"
                + "<span class=\"returnType\">int</span>&nbsp;<span class=\"memberName\">"
                + "<a href=\"../src-html/linksource/SomeClass.html#line.31\">field</a></span></div>",
                "<div class=\"memberSignature\"><span class=\"modifiers\">public</span>&nbsp;"
                + "<span class=\"memberName\"><a href=\"../src-html/linksource/SomeClass.html#line.33\">"
                + "SomeClass</a></span>()</div>",
                "<div class=\"memberSignature\"><span class=\"modifiers\">public</span>&nbsp;"
                + "<span class=\"returnType\">int</span>&nbsp;<span class=\"memberName\">"
                + "<a href=\"../src-html/linksource/SomeClass.html#line.36\">method</a></span>()</div>");

        checkOutput("src-html/linksource/SomeClass.html", true,
                "<title>Source code</title>",
                "<span class=\"sourceLineNo\">029</span><a id=\"line.29\">"
                + "public class SomeClass {</a>",
                "<span class=\"sourceLineNo\">031</span><a id=\"line.31\">    "
                + "public int field;</a>",
                "<span class=\"sourceLineNo\">033</span><a id=\"line.33\">    "
                + "public SomeClass() {</a>",
                "<span class=\"sourceLineNo\">036</span><a id=\"line.36\">    "
                + "public int method() {</a>");

        checkOutput("linksource/SomeEnum.html", true,
                "<div class=\"memberSignature\"><span class=\"modifiers\">public static final</span>&nbsp;"
                + "<span class=\"returnType\"><a href=\"SomeEnum.html\" title=\"enum in linksource\">"
                + "SomeEnum</a></span>&nbsp;<span class=\"memberName\">"
                + "<a href=\"../src-html/linksource/SomeEnum.html#line.29\">VALUE1</a></span></div>",
                "<div class=\"memberSignature\"><span class=\"modifiers\">public static final</span>&nbsp;"
                + "<span class=\"returnType\"><a href=\"SomeEnum.html\" title=\"enum in linksource\">"
                + "SomeEnum</a></span>&nbsp;<span class=\"memberName\">"
                + "<a href=\"../src-html/linksource/SomeEnum.html#line.30\">VALUE2</a></span></div>");

        checkOutput("src-html/linksource/SomeEnum.html", true,
                "<span class=\"sourceLineNo\">029</span><a id=\"line.29\">    VALUE1,</a>",
                "<span class=\"sourceLineNo\">030</span><a id=\"line.30\">    VALUE2</a>");
    }

    @Test
    public void testNoQualifier() {
        javadoc("-d", "out-10",
                "-noqualifier", "pkg",
                "-sourcepath", testSrc,
                "pkg", "deprecated");
        checkExit(Exit.OK);

        checkOutput("pkg/Foo.html", true,
                "<div class=\"inheritance\">Foo</div>");
        checkOutput("deprecated/Foo.html", true,
                "<div class=\"inheritance\">deprecated.Foo</div>");

        javadoc("-d", "out-10a",
                "-noqualifier", "all",
                "-sourcepath", testSrc,
                "pkg", "deprecated");
        checkExit(Exit.OK);

        checkOutput("pkg/Foo.html", true,
                "<div class=\"inheritance\">Foo</div>");
        checkOutput("deprecated/Foo.html", true,
                "<div class=\"inheritance\">Foo</div>");
    }
}
