package com.jetbrains.isaev.dao;

/**
 * Created by Ilya.Isaev on 25.08.2014.
 */
public class DatabaseMigrationScripts {
    /**
     * index == (old database version - 1)
     */
    public static final String[][] scripts = new String[][]{
            {//v1 -> v2
                    "ALTER TABLE Issues ALTER COLUMN title CLOB"
            },
            {//v2 -> v3 and so on
                    "ALTER TABLE Accounts ADD COLUMN asGuest BOOLEAN AFTER password"
            },
            {
                    "ALTER TABLE Issues ADD COLUMN mustBeShown BOOLEAN DEFAULT TRUE AFTER number",
                    "ALTER TABLE STElements ADD COLUMN dndInfo CLOB AFTER anOrder"
            },
            {
                    "ALTER TABLE STElements ADD COLUMN onPlace BOOLEAN DEFAULT FALSE AFTER dndInfo"
            }
    };
}
