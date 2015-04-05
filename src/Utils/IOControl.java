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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * IO subsystem general control class.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public final class IOControl {
    
    /**
     * Log id for import subsystem.
     */
    public static String IMPORT_LOGID = "ІМПОРТ";
    
    /**
     * Log id for export subsystem.
     */
    public static String EXPORT_LOGID = "ЕКСПОРТ";
    
    /**
     * Import configs dir path.
     */
    public static String IMPORT_DIR;
    
    /**
     * Export configs dir path.
     */
    public static String EXPORT_DIR;
    
    /**
     * Log id for this class.
     */
    public static String LOG_ID = "ВВІД/ВИВІД";
    
    /**
     * Numeric id of current library API.
     */
    public static final int IO_API = 2;
    
    /**
     * System wrapper object.
     */
    public static Utils.SystemWrapper serverWrapper;
    
    /**
     * Current import quene object.
     */
    public static Import.Quene quene;
    
    /**
     * Current export dispathcher object.
     */
    public static Export.Dispatcher dispathcer;
    
    /**
     * Load wrapper to the IO subsystem.
     * @param givenWrapper wrapper to load.
     */
    public static void initWrapper(Utils.SystemWrapper givenWrapper) {
        IOControl.serverWrapper = givenWrapper;
    }
    
    /**
     * Register path dir variables (uses for modules initiation).
     * @param importPath path of import config folder;
     * @param exportPath path of export config folder;
     */
    public static void registerPathes(String importPath, String exportPath) {
        IMPORT_DIR = importPath;
        EXPORT_DIR = exportPath;
    }
    
    /**
     * Register quene 
     * @param givenQuene quene reference to register;
     */
    public static void registerImport(Import.Quene givenQuene) {
        IOControl.quene = givenQuene;
    }
    
    /**
     * Register dispatcher.
     * @param givenDispatcher dispather reference to register 
     */
    public static void registerExport(Export.Dispatcher givenDispatcher) {
        IOControl.dispathcer = givenDispatcher;
    }
    
    /**
     * Load import modules.
     * @param modulePath path to the module folder.
     * @return list with module containers.
     */
    public static java.util.ArrayList<Utils.ModuleContainer> loadModules(String modulePath) {
        ArrayList<Utils.ModuleContainer> classes = new ArrayList();
        File[] modulesRaw = new File(modulePath).listFiles();
        for (File moduleFile : modulesRaw) {
            IOControl.processModule(moduleFile.getName(), modulePath, classes);
        }
        return classes;
    }
    
    /**
     * Process module file and add it to module store.
     * @param filename name of module file;
     * @param restOfPath full path to module directory;
     * @param classes modules store list;
     */
    private static void processModule(String fileName, String restOfPath, ArrayList<Utils.ModuleContainer> classes) {
        try {
            JarFile jarFile;
            java.net.URLClassLoader loader;
            loader = java.net.URLClassLoader.newInstance(new URL[] {new URL("jar:file:" + restOfPath + fileName + "!/")});
            jarFile = new JarFile(restOfPath + "/" + fileName);
            Enumeration<JarEntry> entries = jarFile.entries();
            while(entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                String className = null;
                if(entryName.endsWith(".class")) {
                    className = entry.getName().substring(0,entry.getName().length()-6).replace('/', '.');
                }
                if (className != null) {
                    try {
                        Class loadedClass = loader.loadClass(className);
                        Utils.ModuleContainer module = tryModule(loadedClass);
                        if (module != null) {
                            IOControl.serverWrapper.log(LOG_ID, 2, "завантажено модуль '" + module.moduleClass.getName() + "'");
                            classes.add(module);
                        }
                    } catch (ClassNotFoundException ex) {
                        IOControl.serverWrapper.log(LOG_ID, 1, "неможливо завантажити клас " + className + "!");
                    }
                }
            }
        } catch (java.net.MalformedURLException ex) {
            IOControl.serverWrapper.log(LOG_ID, 1, "некоректний URL для файлу " + fileName + "!");
        } catch (IOException ex) {
            IOControl.serverWrapper.log(LOG_ID, 1, "неможливо прочитати файл " + fileName + "!");
        }
    }
    
    /**
     * Try to define module type and build <code>ModuleContainer</code>.
     * @param givenClass class to try;
     * @return builded <code>ModuleContainer</code> instance.
     */
    private static Utils.ModuleContainer tryModule(Class givenClass) {
        try {
            RibbonIOModule moduleNote = (RibbonIOModule) givenClass.getAnnotation(RibbonIOModule.class);
            if (moduleNote != null && (moduleNote.api_version() <= IOControl.IO_API)) {
                IOControl.serverWrapper.registerPropertyName(moduleNote.property());
                return new Utils.ModuleContainer(moduleNote.type(), moduleNote.property(), givenClass);
            } else {
                return null;
            }
        } catch (Exception ex) {
            IOControl.serverWrapper.log(LOG_ID, 1, "помилка опрацювання класу " + givenClass.getName());
            return null;
        }
    }
}
