/*
 * Copyright (c) 2013, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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

package jdk.javadoc.internal.doclets.formats.html;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import jdk.javadoc.internal.doclets.formats.html.markup.ContentBuilder;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlTree;
import jdk.javadoc.internal.doclets.formats.html.markup.Table;
import jdk.javadoc.internal.doclets.formats.html.markup.TableHeader;
import jdk.javadoc.internal.doclets.toolkit.AnnotationTypeFieldWriter;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.MemberSummaryWriter;

/**
 * Writes annotation type field documentation in HTML format.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 *
 * @author Bhavesh Patel
 */
public class AnnotationTypeFieldWriterImpl extends AbstractMemberWriter
    implements AnnotationTypeFieldWriter, MemberSummaryWriter {

    /**
     * Construct a new AnnotationTypeFieldWriterImpl.
     *
     * @param writer         the writer that will write the output.
     * @param annotationType the AnnotationType that holds this member.
     */
    public AnnotationTypeFieldWriterImpl(SubWriterHolderWriter writer,
            TypeElement annotationType) {
        super(writer, annotationType);
    }

    /**
     * {@inheritDoc}
     */
    public Content getMemberSummaryHeader(TypeElement typeElement,
            Content memberSummaryTree) {
        memberSummaryTree.add(
                MarkerComments.START_OF_ANNOTATION_TYPE_FIELD_SUMMARY);
        Content memberTree = new ContentBuilder();
        writer.addSummaryHeader(this, typeElement, memberTree);
        return memberTree;
    }

    /**
     * {@inheritDoc}
     */
    public Content getMemberTreeHeader() {
        return writer.getMemberTreeHeader();
    }

    /**
     * {@inheritDoc}
     */
    public void addMemberTree(Content memberSummaryTree, Content memberTree) {
        writer.addMemberTree(HtmlStyle.fieldSummary, memberSummaryTree, memberTree);
    }

    /**
     * {@inheritDoc}
     */
    public void addAnnotationFieldDetailsMarker(Content memberDetails) {
        memberDetails.add(MarkerComments.START_OF_ANNOTATION_TYPE_FIELD_DETAILS);
    }

    /**
     * {@inheritDoc}
     */
    public Content getAnnotationDetailsTreeHeader(TypeElement typeElement) {
        Content memberDetailsTree = new ContentBuilder();
        if (!writer.printedAnnotationFieldHeading) {
            Content heading = HtmlTree.HEADING(Headings.TypeDeclaration.DETAILS_HEADING,
                    contents.fieldDetailsLabel);
            memberDetailsTree.add(heading);
            memberDetailsTree.add(links.createAnchor(
                    SectionName.ANNOTATION_TYPE_FIELD_DETAIL));
            writer.printedAnnotationFieldHeading = true;
        }
        return memberDetailsTree;
    }

    /**
     * {@inheritDoc}
     */
    public Content getAnnotationDocTreeHeader(Element member,
            Content annotationDetailsTree) {
        Content annotationDocTree = new ContentBuilder();
        Content heading = new HtmlTree(Headings.TypeDeclaration.MEMBER_HEADING);
        heading.add(name(member));
        annotationDocTree.add(heading);
        annotationDocTree.add(links.createAnchor(name(member)));
        return HtmlTree.SECTION(HtmlStyle.detail, annotationDocTree);
    }

    /**
     * {@inheritDoc}
     */
    public Content getSignature(Element member) {
        return new MemberSignature(member)
                .addType(getType(member))
                .toContent();
    }

    /**
     * {@inheritDoc}
     */
    public void addDeprecated(Element member, Content annotationDocTree) {
        addDeprecatedInfo(member, annotationDocTree);
    }

    /**
     * {@inheritDoc}
     */
    public void addComments(Element member, Content annotationDocTree) {
        addComment(member, annotationDocTree);
    }

    /**
     * {@inheritDoc}
     */
    public void addTags(Element member, Content annotationDocTree) {
        writer.addTagsInfo(member, annotationDocTree);
    }

    /**
     * {@inheritDoc}
     */
    public Content getAnnotationDetails(Content annotationDetailsTreeHeader, Content annotationDetailsTree) {
        Content annotationDetails = new ContentBuilder();
        annotationDetails.add(annotationDetailsTreeHeader);
        annotationDetails.add(annotationDetailsTree);
        return getMemberTree(HtmlTree.SECTION(HtmlStyle.fieldDetails, annotationDetails));
    }

    /**
     * {@inheritDoc}
     */
    public Content getAnnotationDoc(Content annotationDocTree) {
        return getMemberTree(annotationDocTree);
    }

    /**
     * {@inheritDoc}
     */
    public void addSummaryLabel(Content memberTree) {
        Content label = HtmlTree.HEADING(Headings.TypeDeclaration.SUMMARY_HEADING,
                contents.fieldSummaryLabel);
        memberTree.add(label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableHeader getSummaryTableHeader(Element member) {
        return new TableHeader(contents.modifierAndTypeLabel, contents.fields,
                contents.descriptionLabel);
    }

    @Override
    protected Table createSummaryTable() {
        Content caption = contents.getContent("doclet.Fields");

        TableHeader header = new TableHeader(contents.modifierAndTypeLabel, contents.fields,
            contents.descriptionLabel);

        return new Table(HtmlStyle.memberSummary)
                .setCaption(caption)
                .setHeader(header)
                .setRowScopeColumn(1)
                .setColumnStyles(HtmlStyle.colFirst, HtmlStyle.colSecond, HtmlStyle.colLast);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSummaryAnchor(TypeElement typeElement, Content memberTree) {
        memberTree.add(links.createAnchor(
                SectionName.ANNOTATION_TYPE_FIELD_SUMMARY));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addInheritedSummaryAnchor(TypeElement typeElement, Content inheritedTree) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addInheritedSummaryLabel(TypeElement typeElement, Content inheritedTree) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addSummaryLink(LinkInfoImpl.Kind context, TypeElement typeElement, Element member,
            Content tdSummary) {
        Content memberLink = HtmlTree.SPAN(HtmlStyle.memberNameLink,
                writer.getDocLink(context, member, name(member), false));
        Content code = HtmlTree.CODE(memberLink);
        tdSummary.add(code);
    }

    /**
     * {@inheritDoc}
     */
    protected void addInheritedSummaryLink(TypeElement typeElement,
            Element member, Content linksTree) {
        //Not applicable.
    }

    /**
     * {@inheritDoc}
     */
    protected void addSummaryType(Element member, Content tdSummaryType) {
        addModifierAndType(member, getType(member), tdSummaryType);
    }

    /**
     * {@inheritDoc}
     */
    protected Content getDeprecatedLink(Element member) {
        return writer.getDocLink(LinkInfoImpl.Kind.MEMBER,
                member, utils.getFullyQualifiedName(member));
    }

    private TypeMirror getType(Element member) {
        if (utils.isConstructor(member))
            return null;
        if (utils.isExecutableElement(member))
            return utils.getReturnType((ExecutableElement)member);
        return member.asType();
    }
}
