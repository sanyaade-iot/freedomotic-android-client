/*******************************************************************************
 * Copyright (c) 2011 Gabriel Pulido.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Gabriel Pulido - initial API and implementation
 ******************************************************************************/
package it.freedomotic.reactions;


import java.io.Serializable;


/**
 *
 * @author Enrico
 */
public class Statement implements Serializable {

    public static final String EQUALS = "EQUALS";
    public static final String GREATER_THEN = "GREATER_THEN";
    public static final String LESS_THEN = "LESS_THEN";
    public static final String GREATER_EQUAL_THEN = "GREATER_EQUAL_THEN";
    public static final String LESS_EQUAL_THEN = "LESS_EQUAL_THEN";
    public static final String REGEX = "REGEX";
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String NOT = "NOT";
    public static final String ANY = "ANY";
    public String logical;
    public String attribute;
    public String operand;
    public String value;

    public Statement create(String logical, String attribute, String operand, String value) {
        if ((attribute != null) && (value != null)) {
            if ((!attribute.trim().equals("")) && (!value.trim().equals(""))) {
                this.logical = logical;
                this.attribute = attribute;
                this.operand = operand;
                this.value = value;
                return this;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }

    public String getOperand() {
        return operand;
    }

    public String getLogical() {
        return logical;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void setLogical(String logical) {
        this.logical = logical;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return attribute + " " + operand + " " + value;
    }
}
