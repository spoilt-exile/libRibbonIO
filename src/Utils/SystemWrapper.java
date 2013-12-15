/**
 * This file is part of libRibbonIO library (check README).
 * Copyright (C) 2012-2013 Stanislav Nepochatov
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
**/

package Utils;

/**
 * Server communication wrapper class.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public abstract class SystemWrapper {
    
    /**
     * Log message to the system;
     * @param logSource source of the message;
     * @param logLevel level of the message (according to server specs);
     * @param logMessage message for logging;
     */
    public abstract void log(String logSource, Integer logLevel, String logMessage);
    
    /**
     * Add message to the system.
     * @param schemeName name of import scheme;
     * @param typeName name of import type;
     * @param givenMessage message to add;
     */
    public abstract void addMessage(String schemeName, String typeName, MessageClasses.Message givenMessage);
    
    /**
     * Call message index updating.
     */
    public abstract void updateIndex(String givenIndex);
    
    /**
     * Register property name for import/export module.
     * @param givenName name of the module.
     */
    public abstract void registerPropertyName(String givenName);
    
    /**
     * Get date from server.
     * @return formated date;
     */
    public abstract String getDate();
    
    /**
     * Return property value.
     * @return value of property;
     */
    public abstract String getProperty(String key);
    
    /**
     * Enable <code>DIRTY</code> state of the RibbonServer.
     * <br><br><b>WARNING!</b><br>
     * <p>This method switch system to the <code>DIRTY</code> state. 
     * Such state should be present only if libRibbonIO 
     * imports/exports has been recieved an error while 
     * processing. You have to think twice befor use 
     * this method!</p>
     * 
     * <p>This method and <code>disableDirtyState</code> 
     * will deliver to the system type and hash print of IO module. 
     * System will place print in list. <code>DIRTY</code> 
     * state will be enabled until there are records 
     * in list.
     * </p>
     * @param moduleType type of the module;
     * @param moduleScheme name of IO scheme;
     * @param modulePrint module unique ID;
     * @see #disableDirtyState(java.lang.String, java.lang.String, java.lang.String) 
     */
    public abstract void enableDirtyState(String moduleType, String moduleScheme, String modulePrint);
    
    /**
     * Disable <code>DIRTY</code> state of the RibbonServer.
     * <br><br><b>WARNING!</b><br>
     * <p>This method try to switch system back to normal state. 
     * System will be turned to normal state only if there is no 
     * no more records in dirty module list.</p>
     * @param moduleType type of the module;
     * @param moduleScheme name of IO scheme;
     * @param modulePrint module unique ID;
     * @see #enableDirtyState(java.lang.String, java.lang.String, java.lang.String) 
     */
    public abstract void disableDirtyState(String moduleType, String moduleScheme, String modulePrint);
    
    /**
     * Post exception as message to the debug directory.
     * @param desc short description of exceptional situation;
     * @param ex exception object;
     */
    public abstract void postException(String desc, Throwable ex);
}
