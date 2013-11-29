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
 * Module class container.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class ModuleContainer<E> {
    
    /**
     * Type of import/export module.
     */
    public String moduleType;
    
    /**
     * Property of import/export module.
     */
    public String moduleProperty;
    
    /**
     * Class of import/export module.
     */
    public Class<E> moduleClass;
    
    /**
     * Default constructor.
     * @param givenType type of constructed container;
     * @param givenModule class of container's module;
     */
    public ModuleContainer(String givenType, Class givenModule) {
        moduleType = givenType;
        moduleClass = givenModule;
    }
    
    /**
     * Default constructor.
     * @param givenType type of constructed container;
     * @param givenModule class of container's module;
     */
    public ModuleContainer(String givenType, String givenProp, Class givenModule) {
        moduleType = givenType;
        moduleProperty = givenProp;
        moduleClass = givenModule;
    }
}
