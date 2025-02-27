///*
// * ========================================================================
// *
// * Codehaus Cargo, copyright 2004-2011 Vincent Massol, 2012-2023 Ali Tokmen.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// * ========================================================================
// */
//package org.codehaus.cargo.module;
//
//import org.jdom2.Element;
//
///**
// * Extension of JDOM element that represents a descriptor element.
// */
//public class DescriptorElement extends Element
//{
//    /**
//     * The tag that this element represents.
//     */
//    private DescriptorTag tag;
//
//    /**
//     * Constructor.
//     *
//     * @param tag the tag type
//     */
//    public DescriptorElement(DescriptorTag tag)
//    {
//        super(tag.getTagName(), tag.getTagNamespace());
//        this.tag = tag;
//    }
//
//    /**
//     * Constructor.
//     *
//     * @param tag the tag type
//     * @param element element to clone
//     */
//    public DescriptorElement(DescriptorTag tag, Element element)
//    {
//        this.tag = tag;
//        this.addContent(element.detach());
//    }
//
//    /**
//     * @return the tag
//     */
//    public DescriptorTag getTag()
//    {
//        return this.tag;
//    }
//
//    /**
//     * @param tag the tag to set
//     */
//    public void setTag(DescriptorTag tag)
//    {
//        this.tag = tag;
//    }
//}

package org.codehaus.cargo.module;

import org.jdom2.Element;

public class DescriptorElement extends Element {
    private DescriptorTag tag;

    public DescriptorElement(DescriptorTag tag) {
        super(tag.getTagName(), tag.getTagNamespace());
        this.tag = tag;
    }

    public DescriptorElement(DescriptorTag tag, Element element) {
        this.tag = tag;
        this.addContent(element.detach());
    }

    public DescriptorTag getTag() {
        return this.tag;
    }

    public void setTag(DescriptorTag tag) {
        this.tag = tag;
    }

    // Extracted class for handling tag-specific operations
    private static class TagOperationsHandler {
        protected void performTagOperation1(DescriptorTag tag) {
            // Operations specific to tag 1
        }

        protected void performTagOperation2(DescriptorTag tag) {
            // Operations specific to tag 2
        }

        // Other tag-specific operations
    }

    // Remaining methods and members of DescriptorElement class

    // Public methods remain unchanged
}
