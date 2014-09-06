package com.jetbrains.isaev.dao;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by Ilya.Isaev on 25.08.2014.
 */
public class DatabaseMigrationScripts {
    /**
     * index == (old database version - 1)
     */
    public static JdbcTemplate db;

    public static final Runnable[] scripts = new Runnable[]{
            //v1 -> v2
            new Runnable() {
                @Override
                public void run() {
                    db.update("ALTER TABLE Issues ALTER COLUMN title CLOB");
                }
            },
            new Runnable() {//v2 -> v3 and so on
                @Override
                public void run() {
                    db.update("ALTER TABLE Accounts ADD COLUMN  IF NOT EXISTS asGuest BOOLEAN AFTER password");
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    db.update("ALTER TABLE Issues ADD COLUMN  IF NOT EXISTS  mustBeShown BOOLEAN DEFAULT TRUE AFTER number");
                    db.update("ALTER TABLE STElements ADD COLUMN  IF NOT EXISTS  dndInfo CLOB AFTER anOrder");
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    db.update("ALTER TABLE STElements ADD COLUMN IF NOT EXISTS onPlace BOOLEAN DEFAULT FALSE AFTER dndInfo");
                }
            },
            new Runnable() {
                @Override
                public void run() {
                    db.update("ALTER TABLE STElements ADD COLUMN  IF NOT EXISTS issueID INT AFTER exceptionID");
                    db.update("UPDATE STElements S SET issueID = (SELECT E.issueID FROM Exceptions E WHERE E.exceptionID = S.exceptionID)");
                    db.update("ALTER TABLE STElements ADD FOREIGN KEY (issueID) REFERENCES Issues(issueID) ON DELETE CASCADE");
                }
            },
            new Runnable() {
                @Override
                public void run() {//because of new version of db
                    db.execute(IssuesDAO.ST_ELEMENTS_DELETE_STATEMENT);
                    db.execute(IssuesDAO.EXCEPTIONS_DELETE_STATEMENT);
                    db.execute(IssuesDAO.ISSUES_DELETE_STATEMENT);
                    db.execute(IssuesDAO.PROJECTS_DELETE_STATEMENT);
                    db.execute(IssuesDAO.ACCOUNTS_DELETE_STATEMENT);
                    db.execute(IssuesDAO.ACCOUNTS_CREATE_STATEMENT);
                    db.execute(IssuesDAO.PROJECTS_CREATE_STATEMENT);
                    db.execute(IssuesDAO.ISSUES_CREATE_STATEMENT);
                    db.execute(IssuesDAO.EXCEPTIONS_CREATE_STATEMENT);
                    db.execute(IssuesDAO.ST_ELEMENTS_CREATE_STATEMENT);
                }
            }
    };
}
