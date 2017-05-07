package com.mobile.newFramework.database;

/**
 * Base table class.
 * @author sergiopereira
 */
public abstract class BaseTable {
    
    /**
     * Returns the table type used on upgrade database.<br>
     * - {@link DarwinDatabaseHelper#CACHE}: Drop old and create a new table.<br>
     * - {@link DarwinDatabaseHelper#FREEZE}: Retain table.<br>
     * - {@link DarwinDatabaseHelper#PERSIST}: Upgrade the table trying copy the old content for the new table schema.
     * WARNING: Not support table rename and some update types.<br>
     * @return TableType for upgrade
     * @author sergiopereira
     */
    @DarwinDatabaseHelper.UpgradeType
    public abstract int getUpgradeType();
    
    /**
     * Returns the table name.
     * @return String
     * @author sergiopereira
     */
    public abstract String getName();
    
    /**
     * Returns the SQL instruction to create the table.
     * @return String.
     * @author sergiopereira
     */
    public abstract String create();
    
}