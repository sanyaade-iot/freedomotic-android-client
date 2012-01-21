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
package it.freedom.reactions;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
*
* @author Enrico
*/
public class Payload implements Serializable {

   ArrayList<Statement> payload = new ArrayList<Statement>();

   public Payload() {
   }

   public void addStatement(String logical,
           String attribute,
           String operand,
           String value) {
       enqueueStatement(new Statement().create(logical, attribute, operand, value));
   }

   public void addStatement(String attribute, String value) {
       enqueueStatement(new Statement().create(Statement.AND, attribute, Statement.EQUALS, value));
   }

   public void addStatement(String attribute, int value) {
       enqueueStatement(new Statement().create(Statement.AND, attribute, Statement.EQUALS, Integer.toString(value)));
   }

   public void enqueueStatement(Statement s) {
       if (s != null) {
           payload.add(s);
       } else {
          // Freedom.logger.warning("Attempt to add a null or empty statement in payload. Statement discarded");
       }
   }

   public ArrayList<Statement> getStatements() {
       return payload;
   }

   @Override
   public boolean equals(Object obj) {
       boolean consistent = true;
       if (obj instanceof Payload) {
           Payload eventPayload = (Payload) obj;
           Iterator triggerIterator = this.getStatements().iterator();
           ArrayList<Boolean> results = new ArrayList<Boolean>();
           //check all statement for consistency
           while (triggerIterator.hasNext()) {
               Statement trigger = (Statement) triggerIterator.next();
               Statement event = eventPayload.findAttribute(trigger.attribute);
               /*TODO: waring, supports only operand equal in event
                *compared to equal, morethen, lessthen in triggers.
                *Refacor with a strategy pattern.*/
               if (event != null) {
                   //is setting a value must be not used to filter
                   if (trigger.logical.equalsIgnoreCase("SET")) {
                       return true;
                   } else {
                       if (isStatementConsistent(trigger.operand, trigger.value, event.value)) {
//                           Freedom.logger.info("[NOT CONSISTENT] The trigger has fail the check because the event statement '" + event.toString() +
//                                   "' is not contistent with trigger statement '" + trigger.attribute + " " + trigger.operand + " " + trigger.value + "'");
                           results.add(Boolean.TRUE);
                       } else {
                           results.add(Boolean.FALSE);
                       }
                   }
               }
           }
           for (int i = 0; i < results.size(); i++) {
               if (getStatements().get(i).getLogical().equalsIgnoreCase("AND")) {
                   consistent = consistent && results.get(i);
                   //Freedom.logger.severe("AND: " + consistent + " " + results.get(i) + " consistent is: " + consistent);
               } else {
                   if (getStatements().get(i).getLogical().equalsIgnoreCase("OR")) {
                      // Freedom.logger.warning("This payload uses the experimental feature 'OR'. Use it carefully because is still not tested properly");
                       consistent = consistent || results.get(i);
                       //Freedom.logger.severe("OR: " + consistent + " " + results.get(i) + " consistent is: " + consistent);
                   }
               }
           }
       }
       return consistent;
   }

   @Override
   public int hashCode() {
       int hash = 7;
       hash = 67 * hash + (this.payload != null ? this.payload.hashCode() : 0);
       return hash;
   }

   private static boolean isStatementConsistent(String triggerOperand, String triggerValue, String eventValue) {
       if (triggerOperand.equalsIgnoreCase(Statement.EQUALS)) { //event operand="EQUALS", trigger operand="EQUALS"
           if (triggerValue.equalsIgnoreCase(eventValue)
                   || (triggerValue.equals(Statement.ANY))) {
               return true;
           }
       }

       if (triggerOperand.equals(Statement.REGEX)) { //event operand="EQUALS", trigger operand="REGEX"
           Pattern pattern = Pattern.compile(triggerValue);
           Matcher matcher = pattern.matcher(eventValue);
           if (matcher.matches()) {
               return true;
           } else {
               return false;
           }
       }

       //applies only to integer values
       if (triggerOperand.equals(Statement.GREATER_THEN)) { //event operand="EQUALS", trigger operand="GREATER_THEN"
           try {
               Integer intReactionValue = new Integer(triggerValue);
               Integer intEventValue = new Integer(eventValue);
               if (intEventValue > intReactionValue) {
                   return true;
               } else {
                   return false;
               }
           } catch (NumberFormatException numberFormatException) {
           //    Freedom.logger.warning(Statement.GREATER_THEN.toString() + " operator can be applied only to integer values");
               return false;
           }

       }
       if (triggerOperand.equals(Statement.LESS_THEN)) { //event operand="EQUALS", trigger operand="LESS_THEN"
           try {
               Integer intReactionValue = new Integer(triggerValue);
               Integer intEventValue = new Integer(eventValue);
               if (intEventValue < intReactionValue) {
                   return true;
               } else {
                   return false;
               }
           } catch (NumberFormatException numberFormatException) {
               //is not a number
             //  Freedom.logger.warning(Statement.LESS_THEN.toString() + " operator can be applied only to integer values");
               return false;
           }
       }
               //applies only to integer values
       if (triggerOperand.equals(Statement.GREATER_EQUAL_THEN)) { //event operand="EQUALS", trigger operand="GREATER_THEN"
           try {
               Integer intReactionValue = new Integer(triggerValue);
               Integer intEventValue = new Integer(eventValue);
               if (intEventValue >= intReactionValue) {
                   return true;
               } else {
                   return false;
               }
           } catch (NumberFormatException numberFormatException) {
            //   Freedom.logger.warning(Statement.GREATER_EQUAL_THEN.toString() + " operator can be applied only to integer values");
               return false;
           }

       }
       if (triggerOperand.equals(Statement.LESS_EQUAL_THEN)) { //event operand="EQUALS", trigger operand="LESS_THEN"
           try {
               Integer intReactionValue = new Integer(triggerValue);
               Integer intEventValue = new Integer(eventValue);
               if (intEventValue <= intReactionValue) {
                   return true;
               } else {
                   return false;
               }
           } catch (NumberFormatException numberFormatException) {
               //is not a number
            //   Freedom.logger.warning(Statement.LESS_EQUAL_THEN.toString() + " operator can be applied only to integer values");
               return false;
           }
       }
       return false;
   }

   public Statement findAttribute(String key) {
       Iterator it = payload.iterator();
       while (it.hasNext()) {
           Statement s = (Statement) it.next();
           //Freedom.logger.info("DEBUG: contains attribute " + s.getAttribute());
           if (s.getAttribute().equalsIgnoreCase(key)) {
               return s;
           }
       }
       return null;
   }

   @Override
   public String toString() {
       StringBuilder buffer = new StringBuilder();
       Iterator it = payload.iterator();
       while (it.hasNext()) {
           Statement s = (Statement) it.next();
           buffer.append("; " + s.toString());
       }
       return buffer.toString();
   }

}

