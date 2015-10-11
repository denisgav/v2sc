/*******************************************************************************
 * Copyright (c) 2004, 2006 KOBAYASHI Tadashi and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    KOBAYASHI Tadashi - initial API and implementation
 *******************************************************************************/
package parser;

import java.io.Reader;

public interface IParser
{
    static public final String EXT_VHDL = "vhd";
    static public final String EXT_VERILOG = "v";

    /**
     * parse file
     */
    public IASTNode parse(String path) throws ParserException;
    
    /**
     * parse buffer
     */
    public IASTNode parse(Reader reader) throws ParserException;
    
    /**
     * get comment blocks in parsed file
     */
    public CommentBlock[] getComment();
    
    /** 
     * get root node of ast
     */
    public IASTNode getRoot();
    
    /**
     * get symbol in symbol table or library 
     */
    public ISymbol getSymbol(IASTNode node, String name);
    
    /**
     * get symbol in symbol table or library(including child table) 
     */
    public ISymbol getSymbol(IASTNode node, String[] names);
    
    /**
     * get symbol table which actually contains the symbol of specified name
     */
    public ISymbolTable getTableOfSymbol(IASTNode node, String name);
}

