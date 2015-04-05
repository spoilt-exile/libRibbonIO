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

package Import;

import Utils.IOControl;
import java.io.File;

/**
 * Import quene class.
 * @author Stanislav Nepochatov <spoilt.exile@gmail.com>
 */
public class Quene {
    
    /**
     * List of current running instances of <code>Importer</code> class.
     */
    private java.util.ArrayList<Importer> importList = new java.util.ArrayList();
    
    /**
     * List of current registred modules.
     */
    private java.util.ArrayList<Utils.ModuleContainer> moduleList;
    
    /**
     * Path to import schemas files.
     */
    public String importDirPath;
    
    /**
     * Default constructor.
     * @param pluginPath path to search plugins.
     * @param givenImportDirPath path to search import configuration files.
     */
    public Quene (String pluginPath, String givenImportDirPath) {
        importDirPath = givenImportDirPath;
        this.moduleList = IOControl.loadModules(pluginPath);
        java.io.File importPropsDir = new java.io.File(givenImportDirPath);
        if (!importPropsDir.exists()) {
            importPropsDir.mkdirs();
            IOControl.serverWrapper.log(IOControl.IMPORT_LOGID, 2, "Створюю теку імпорту...");
        }
        java.io.File[] importProps = importPropsDir.listFiles(new java.io.FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".import")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        for (java.io.File importPropertyFile : importProps) {
            java.util.Properties importConfig = new java.util.Properties();
            try {
                importConfig.load(new java.io.FileReader(importPropertyFile));
            } catch (java.io.FileNotFoundException ex) {
                IOControl.serverWrapper.log(IOControl.IMPORT_LOGID, 1, "неможливо знайти файл " + importPropertyFile.getName());
            } catch (java.io.IOException ex) {
                IOControl.serverWrapper.log(IOControl.IMPORT_LOGID, 1, "помилка при зчитуванні файлу " + importPropertyFile.getName());
            }
            Importer newImport = this.getNewInstanceForType(importConfig);
            if (newImport != null) {
                this.importList.add(newImport);
            } else {
                IOControl.serverWrapper.log(IOControl.IMPORT_LOGID, 2, "заванатження модулю для " + importPropertyFile.getName() + " завершилось з помилкою.");
            }
        }
        if (this.importList.isEmpty()) {
            IOControl.serverWrapper.log(IOControl.IMPORT_LOGID, 2, "система не знайшла жодної схеми імпорту!");
        }
    }
    
    /**
     * Get new instance of importer by guvin type.
     * @param givenConfig config for importer construction;
     * @return new reference of Importer class or null if type uknown.
     */
    public Importer getNewInstanceForType(java.util.Properties givenConfig) {
        java.util.ListIterator<Utils.ModuleContainer> modIter = this.moduleList.listIterator();
        Utils.ModuleContainer findedMod = null;
        while (modIter.hasNext()) {
            Utils.ModuleContainer currMod = modIter.next();
            if (currMod.moduleType.equals(givenConfig.getProperty("import_type"))) {
                findedMod = currMod;
                break;
            }
        }
        if (findedMod != null) {
            try {
                return (Importer) findedMod.moduleClass.getConstructor(java.util.Properties.class).newInstance(givenConfig);
            } catch (java.lang.reflect.InvocationTargetException ex) {
                ex.getTargetException().printStackTrace();
            } catch (Exception ex) {
                IOControl.serverWrapper.log(IOControl.IMPORT_LOGID, 1, "неможливо опрацювати класс " + findedMod.moduleClass.getName());
                ex.printStackTrace();
            }
        } else {
            IOControl.serverWrapper.log(IOControl.IMPORT_LOGID, 2, "неможливо знайти модуль для типу " + givenConfig.getProperty("import_type"));
        }
        return null;
    }

    /**
     * Run all import schemas.
     */
    public void importRun() {
        java.util.ListIterator<Importer> importIter = this.importList.listIterator();
        while (importIter.hasNext()) {
            importIter.next().start();
        }
        IOControl.serverWrapper.log(IOControl.IMPORT_LOGID, 3, "запуск усіх схем імпорту");
    }
}
