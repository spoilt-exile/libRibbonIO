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

import java.lang.annotation.*;

/**
 * Ribbon module mark annotation.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
@Target(value=ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface RibbonIOModule {
    
    /**
     * Get type of the module.
     * @return type string;
     */
    String type();
    
    /**
     * Get property id.
     * @return id for property creation;
     */
    String property();
    
    /**
     * Version of required libRibbonIO api level.
     * @return version of api;
     */
    int api_version();
}
