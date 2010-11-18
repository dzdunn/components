/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.richfaces.renderkit;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;

import org.ajax4jsf.javascript.JSFunction;
import org.richfaces.component.AbstractTreeNode;

/**
 * @author Nick Belaevski
 * 
 */
class TreeEncoderPartial extends TreeEncoderBase {

    protected final AbstractTreeNode treeNode;
    
    private Object rowKey;

    public TreeEncoderPartial(FacesContext context, AbstractTreeNode treeNode) {
        super(context, treeNode.findTreeComponent());
        
        this.treeNode = treeNode;
        
        this.rowKey = tree.getRowKey();

        if (this.rowKey == null) {
            throw new NullPointerException("rowKey");
        }
    }
    
    @Override
    public void encode() throws IOException {
        String elementId = treeNode.getClientId(context);
        
        PartialResponseWriter prw = context.getPartialViewContext().getPartialResponseWriter();
        prw.startUpdate(elementId);

        Object initialRowKey = tree.getRowKey();
        try {
            
            encodeTreeNode(rowKey, true);
            
            prw.endUpdate();
            
        } finally {
            try {
                tree.setRowKey(context, initialRowKey);
            } catch (Exception e) {
                TreeRendererBase.LOGGER.error(e.getMessage(), e);
            }
        }

        prw.startEval();
        JSFunction function = new JSFunction("RichFaces.ui.TreeNode.initNodeByAjax", elementId);
        prw.write(function.toScript());
        prw.endEval();
    }
}