package com.mobile.framework.database;

import com.mobile.framework.database.DarwinDatabaseHelper.TableType;


/**
 * Base table class.
 * @author sergiopereira
 */
public abstract class BaseTable {
    
    /**
     * Returns the table type used on upgrade database.<br>
     * - {@link TableType#CACHE}: Drop old and create a new table.<br>
     * - {@link TableType#FREEZE}: Retain table.<br>
     * - {@link TableType#PERSIST}: Upgrade the table trying copy the old content for the new table schema. 
     * WARNING: Not support table rename and some update types.<br>
     * @return TableType for upgrade
     * @author sergiopereira
     */
    public abstract TableType getUpgradeType();
    
    /**
     * Returns the table name.
     * @return String
     * @author sergiopereira
     */
    public abstract String getName();
    
    /**
     * Returns the SQL instruction to create the table.
     * @param table name
     * @return String.
     * @author sergiopereira
     */
    public abstract String create(String table);
    
}