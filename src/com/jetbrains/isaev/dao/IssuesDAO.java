package com.jetbrains.isaev.dao;

import com.intellij.openapi.diagnostic.Logger;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.issues.StackTraceElement;
import com.jetbrains.isaev.state.BTAccount;
import com.jetbrains.isaev.state.BTAccountType;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.state.BTProject;
import com.jetbrains.isaev.ui.ParsedException;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.rowset.serial.SerialClob;
import java.sql.*;
import java.util.*;

/**
 * Created by Ilya.Isaev on 12.08.2014.
 */
public class IssuesDAO {

    private static final String PATH_SEPARATOR = System.getProperty("file.separator");
    private static final String DB_NAME = "BTIssuesDB";
    private static final int CURRENT_DATABASE_VERSION = 7;
    private static final String STORAGE_FOLDER_PATH = GlobalVariables.getInstance().project.getBasePath() + PATH_SEPARATOR + ".idea" + PATH_SEPARATOR + "BTIssuesDB";
    public static final String ACCOUNTS_CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS Accounts (" +
            "accountID INT  PRIMARY KEY  AUTO_INCREMENT, " +
            "domainName VARCHAR(255) NOT NULL, " +
            "login VARCHAR(255) NOT NULL, " +
            "password VARCHAR(255) NOT NULL, " +
            "asGuest BOOLEAN NOT NULL, " +
            "type TINYINT NOT NULL, " +
            "UNIQUE(domainName, login, password))";
    public static final String PROJECTS_CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS Projects (" +
            "projectID INT  PRIMARY KEY  AUTO_INCREMENT, " +
            "accountID INT, shortName VARCHAR(255), " +
            "longName VARCHAR(255), " +
            "customFieldName VARCHAR(255), " +
            "mustBeUpdated BOOL, " +
            "lastUpdated TIMESTAMP, " +
            "FOREIGN KEY(accountID) REFERENCES Accounts(accountID) ON DELETE CASCADE,UNIQUE(accountID,shortName,longName))";
    public static final String ISSUES_CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS Issues (" +
            "issueID INT  PRIMARY KEY  AUTO_INCREMENT, " +
            "projectID INT, " +
            "title CLOB, " +
            "description CLOB, " +
            "number VARCHAR(63), " +
            "mustBeShown BOOLEAN DEFAULT TRUE, " +
            "lastUpdated TIMESTAMP, " +
            "FOREIGN KEY(projectID) REFERENCES Projects(projectID) ON DELETE CASCADE)";
    public static final String EXCEPTIONS_CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS Exceptions (" +
            "exceptionID IDENTITY, " +
            "issueID INT, " +
            "name VARCHAR(255), " +
            "message CLOB, " +
            "FOREIGN KEY(issueID) REFERENCES Issues(issueID) ON DELETE CASCADE)";
    public static final String ST_ELEMENTS_CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS STElements (" +
            "stElementID IDENTITY, " +
            "exceptionID BIGINT, " +
            "issueID INT, " +
            "declaringClass VARCHAR(255), " +
            "methodName VARCHAR(255), " +
            "fileName VARCHAR(255), " +
            "lineNumber INT, " +
            "anOrder TINYINT, " +
            "dndInfo CLOB, " +
            "onPlace BOOLEAN DEFAULT FALSE, " +
            "FOREIGN KEY(exceptionID) REFERENCES Exceptions(exceptionID) ON DELETE CASCADE, " +
            "FOREIGN KEY(issueID) REFERENCES Issues(issueID) ON DELETE CASCADE)";
    public static final String ACCOUNTS_DELETE_STATEMENT = "DROP TABLE IF EXISTS Accounts";
    public static final String PROJECTS_DELETE_STATEMENT = "DROP TABLE IF EXISTS Projects";
    public static final String ISSUES_DELETE_STATEMENT = "DROP TABLE IF EXISTS Issues";
    public static final String EXCEPTIONS_DELETE_STATEMENT = "DROP TABLE IF EXISTS Exceptions";
    public static final String ST_ELEMENTS_DELETE_STATEMENT = "DROP TABLE IF EXISTS STElements";

    protected static final Logger logger = Logger.getInstance(IssuesDAO.class);
    private static IssuesDAO instance;
    private static boolean dbChanged = false;
    JdbcTemplate db;

    private static HashMap<Integer, BTIssue> cachedIssues;
    HashSet<BTAccount> accounts;
    private static HashMap<Long, StackTraceElement> stElementsCache;
    private static HashMap<Integer, BTAccount> accountsCache;
    private static RowMapper<StackTraceElement> stackTraceElementRowMapper = new RowMapper<StackTraceElement>() {
        @Override
        public StackTraceElement mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("stElementID");
            if (!stElementsCache.containsKey(id)) {
                stElementsCache.put(id, new StackTraceElement(id, rs.getInt("issueID"), rs.getString("declaringClass"), rs.getString("methodName"), rs.getString("fileName"), rs.getInt("lineNumber"), rs.getLong("exceptionID"), rs.getByte("anOrder"), rs.getBoolean("onPlace"), getStringFromClob(rs.getClob("dndInfo"))));
            }
            return stElementsCache.get(id);
        }
    };

    public HashMap<Long, StackTraceElement> getAllSTElements(int issueID) {
        HashMap<Long, StackTraceElement> result = new HashMap<Long, StackTraceElement>();
        for (StackTraceElement el : stElementsCache.values()) {
            if (el.getIssueID() == issueID) result.put(el.getID(), el);
        }
        return result;
    }


    private class CachedArrayList<T> extends ArrayList<T> {
        public boolean stateChanged = true;
    }

    private class CachedHashSet<T> extends HashSet<T> {
        public boolean stateChanged = true;
    }

    public static IssuesDAO getInstance() {
        if (instance == null) {

            instance = new IssuesDAO();
        }
        return instance;
    }

    protected IssuesDAO() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(org.h2.Driver.class);
        dataSource.setUsername("user");
        dataSource.setUrl("jdbc:h2:file:" + STORAGE_FOLDER_PATH);
        dataSource.setPassword("pass");
        db = new JdbcTemplate(dataSource);
        int knownVersion = ProjectData.getDbVersion();
        if (knownVersion == 0) {//first open
            ProjectData.setDbVersion(CURRENT_DATABASE_VERSION);
            //some clean-up
            db.execute(ST_ELEMENTS_DELETE_STATEMENT);
            db.execute(EXCEPTIONS_DELETE_STATEMENT);
            db.execute(ISSUES_DELETE_STATEMENT);
            db.execute(PROJECTS_DELETE_STATEMENT);
            db.execute(ACCOUNTS_DELETE_STATEMENT);
            //lets create some tables
            db.execute(ACCOUNTS_CREATE_STATEMENT);
            db.execute(PROJECTS_CREATE_STATEMENT);
            db.execute(ISSUES_CREATE_STATEMENT);
            db.execute(EXCEPTIONS_CREATE_STATEMENT);
            db.execute(ST_ELEMENTS_CREATE_STATEMENT);
            db.execute("SET COMPRESS_LOB DEFLATE");
        } else {
            if (knownVersion != CURRENT_DATABASE_VERSION) {
                DatabaseMigrationScripts.db = db;
                startMigrationToAnotherVersion(knownVersion);
            }
        }
        cachedIssues = new HashMap<Integer, BTIssue>();
        accounts = new CachedHashSet<BTAccount>();
        stElementsCache = new HashMap<Long, StackTraceElement>();
        accountsCache = new HashMap<Integer, BTAccount>();
    }

    private void startMigrationToAnotherVersion(int oldVersion) {
        int index = oldVersion - 1;
        for (int i = index; i < CURRENT_DATABASE_VERSION - 1; i++) {
            DatabaseMigrationScripts.scripts[i].run();
            ProjectData.setDbVersion(i + 2);
        }
    }


    public void updateProject(BTProject btProject) {
        db.update("UPDATE Projects SET shortName = ? , longName = ? , lastUpdated = ? , mustBeUpdated = ?, customFieldName = ? WHERE projectID = ?",
                new SqlParameterValue(Types.VARCHAR, btProject.getShortName()),
                new SqlParameterValue(Types.VARCHAR, btProject.getFullName()),
                new SqlParameterValue(Types.TIMESTAMP, btProject.getLastUpdated()),
                new SqlParameterValue(Types.BOOLEAN, btProject.isMustBeUpdated()),
                new SqlParameterValue(Types.VARCHAR, btProject.getCustomFieldName()),
                new SqlParameterValue(Types.INTEGER, btProject.getProjectID()));
    }

    public ParsedException getException(long exceptionID) {
        return db.query("SELECT * FROM Exceptions WHERE exceptionID = ?", new Object[]{exceptionID}, new ResultSetExtractor<ParsedException>() {
            @Override
            public ParsedException extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return new ParsedException(rs.getInt("issueID"), rs.getString("name"), rs.getLong("exceptionID"), getStringFromClob(rs.getClob("message")));
                }
                return null;
            }
        });
    }

    public BTIssue getIssue(int issueID) {
        BTIssue issue = db.query("SELECT * FROM Issues WHERE issueID = ?", new Object[]{issueID}, new ResultSetExtractor<BTIssue>() {
            @Override
            public BTIssue extractData(ResultSet rs) throws SQLException, DataAccessException {
                boolean f = true;
                if (rs.next()) {
                    int id = rs.getInt("issueID");
                    if (!cachedIssues.containsKey(id)) {
                        cachedIssues.put(id, new BTIssue(id, getStringFromClob(rs.getClob("title")), getStringFromClob(rs.getClob("description")), rs.getTimestamp("lastUpdated"), rs.getString("number"), rs.getInt("projectID"), rs.getBoolean("mustBeShown")));
                    }
                    return cachedIssues.get(id);
                } else {
                    return null;
                }
            }
        });
        return issue;
    }

    public void deleteBtAccount(BTAccount acc) {
        db.update("DELETE FROM Accounts WHERE accountID = ?", acc.getAccountID());
        accountsCache.remove(acc.getAccountID());
    }

    public List<BTIssue> getAllIssuesFullState() {
        final Map<Integer, BTIssue> btIssues = new HashMap<Integer, BTIssue>();//new TreeSet<>((i1, i2) -> i1.getIssueID() < i2.getIssueID() ? -1 : 1);
        List<BTIssue> result = db.query("SELECT * FROM STElements JOIN Exceptions ON STElements.exceptionID = Exceptions.exceptionID JOIN Issues ON Exceptions.issueID = Issues.issueID", (Object[]) null, new RowMapper<BTIssue>() {


            @Override
            public BTIssue mapRow(ResultSet rs, int i) throws SQLException {
                int id = rs.getInt("issueID");
                if (!cachedIssues.containsKey(id)) {
                    cachedIssues.put(id, new BTIssue(id, getStringFromClob(rs.getClob("title")), getStringFromClob(rs.getClob("description")), rs.getTimestamp("lastUpdated"), rs.getString("number"), rs.getInt("projectID"), rs.getBoolean("mustBeShown")));
                }
                BTIssue issue = cachedIssues.get(id);
                if (!btIssues.containsKey(issue.hashCode())) btIssues.put(issue.hashCode(), issue);
                issue = btIssues.get(issue.hashCode());
                ParsedException exception = new ParsedException(rs.getInt("issueID"), rs.getString("name"), rs.getLong("exceptionID"), getStringFromClob(rs.getClob("message")));
                if (!issue.getExceptions().containsKey(exception.hashCode()))
                    issue.getExceptions().put(exception.hashCode(), exception);
                long stID = rs.getLong("stElementID");
                if (!stElementsCache.containsKey(stID)) {
                    stElementsCache.put(stID, new StackTraceElement(stID, rs.getInt("issueID"), rs.getString("declaringClass"), rs.getString("methodName"), rs.getString("fileName"), rs.getInt("lineNumber"), rs.getLong("exceptionID"), rs.getByte("anOrder"), rs.getBoolean("onPlace"), getStringFromClob(rs.getClob("dndInfo"))));
                }
                StackTraceElement element = stElementsCache.get(stID);
                if (!exception.getStacktrace().containsKey(element.hashCode()))
                    exception.getStacktrace().put(element.hashCode(), element);
                return null;
            }
        });

        for (BTIssue issue : btIssues.values())
            for (ParsedException ex : issue.getExceptions().values())
                ex.orderStacktrace();
        return new ArrayList<BTIssue>(btIssues.values());
    }

    //todo remove copy-paste
    public List<BTIssue> getAllProjectIssues(int projectID) {
        final Map<Integer, BTIssue> btIssues = new HashMap<Integer, BTIssue>();//new TreeSet<>((i1, i2) -> i1.getIssueID() < i2.getIssueID() ? -1 : 1);
        List<BTIssue> result = db.query("SELECT * FROM STElements JOIN Exceptions ON STElements.exceptionID = Exceptions.exceptionID JOIN Issues ON Exceptions.issueID = Issues.issueID WHERE Issues.projectID = ?", new Object[]{projectID}, new RowMapper<BTIssue>() {


            @Override
            public BTIssue mapRow(ResultSet rs, int i) throws SQLException {
                int id = rs.getInt("issueID");
                if (!cachedIssues.containsKey(id)) {
                    cachedIssues.put(id, new BTIssue(id, getStringFromClob(rs.getClob("title")), getStringFromClob(rs.getClob("description")), rs.getTimestamp("lastUpdated"), rs.getString("number"), rs.getInt("projectID"), rs.getBoolean("mustBeShown")));
                }
                BTIssue issue = cachedIssues.get(id);
                if (!btIssues.containsKey(issue.hashCode())) btIssues.put(issue.hashCode(), issue);
                issue = btIssues.get(issue.hashCode());
                ParsedException exception = new ParsedException(rs.getInt("issueID"), rs.getString("name"), rs.getLong("exceptionID"), getStringFromClob(rs.getClob("message")));
                if (!issue.getExceptions().containsKey(exception.hashCode()))
                    issue.getExceptions().put(exception.hashCode(), exception);
                long stID = rs.getLong("stElementID");
                if (!stElementsCache.containsKey(stID)) {
                    stElementsCache.put(stID, new StackTraceElement(stID, rs.getInt("issueID"), rs.getString("declaringClass"), rs.getString("methodName"), rs.getString("fileName"), rs.getInt("lineNumber"), rs.getLong("exceptionID"), rs.getByte("anOrder"), rs.getBoolean("onPlace"), getStringFromClob(rs.getClob("dndInfo"))));
                }
                StackTraceElement element = stElementsCache.get(stID);
                if (!exception.getStacktrace().containsKey(element.hashCode()))
                    exception.getStacktrace().put(element.hashCode(), element);
                return null;
            }
        });

        for (BTIssue issue : btIssues.values())
            for (ParsedException ex : issue.getExceptions().values())
                ex.orderStacktrace();
        return new ArrayList<BTIssue>(btIssues.values());
    }

    public List<BTIssue> getAllIssues() {
        List<BTIssue> result = db.query("SELECT * FROM Issues", (Object[]) null, new RowMapper<BTIssue>() {
            @Override
            public BTIssue mapRow(ResultSet rs, int i) throws SQLException {
                int id = rs.getInt("issueID");
                if (!cachedIssues.containsKey(id)) {
                    cachedIssues.put(id, new BTIssue(id, getStringFromClob(rs.getClob("title")), getStringFromClob(rs.getClob("description")), rs.getTimestamp("lastUpdated"), rs.getString("number"), rs.getInt("projectID"), rs.getBoolean("mustBeShown")));
                }
                return cachedIssues.get(id);

            }
        });
        return result;
    }

    public List<BTProject> getProjects() {
        return db.query("SELECT * FROM Projects", (Object[]) null, new RowMapper<BTProject>() {
            @Override
            public BTProject mapRow(ResultSet rs, int i) throws SQLException {
                return new BTProject(rs.getInt("projectID"), rs.getInt("accountID"), rs.getString("shortName"), rs.getString("longName"), rs.getTimestamp("lastUpdated"), rs.getBoolean("mustBeUpdated"), rs.getString("customFieldName"));
            }
        });
    }

    private static
    @NotNull
    String getStringFromClob(Clob clob) throws SQLException {
        if (clob == null) return "";
        java.util.Scanner s = new java.util.Scanner(clob.getCharacterStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public List<BTAccount> getAccountsWithProjects() {
        accounts.clear();
        final Map<Integer, BTAccount> tmp = new HashMap<Integer, BTAccount>();
        final Map<Integer, List<BTProject>> tmp2 = new HashMap<Integer, List<BTProject>>();
        db.query("SELECT * FROM Accounts LEFT OUTER JOIN Projects ON (Projects.accountID = Accounts.accountID)", (Object[]) null, new RowMapper<Object>() {
            @Override
            public Object mapRow(ResultSet rs, int i) throws SQLException {
                BTAccount acc = new BTAccount(rs.getInt("accountID"), rs.getString("domainName"), rs.getString("login"), rs.getString("password"), BTAccountType.valueOf(rs.getByte("type")), rs.getBoolean("asGuest"));
                if (!tmp.containsKey(acc.getAccountID())) {
                    tmp.put(acc.getAccountID(), acc);
                    tmp2.put(acc.getAccountID(), new ArrayList<BTProject>());
                }
                if (rs.getInt("projectID") != 0) {
                    BTProject project = new BTProject(rs.getInt("projectID"), rs.getInt("accountID"), rs.getString("shortName"), rs.getString("longName"), rs.getTimestamp("lastUpdated"), rs.getBoolean("mustBeUpdated"), rs.getString("customFieldName"));
                    project.setBtAccount(tmp.get(acc.getAccountID()));
                    tmp2.get(acc.getAccountID()).add(project);
                }
                return acc;

            }
        });
        if (tmp.size() == 0) return getOnlyAccounts();
        for (BTAccount acc : tmp.values()) {
            acc.setProjects(tmp2.get(acc.getAccountID()));
        }
        return new ArrayList<BTAccount>(tmp.values());
    }

    private List<BTAccount> getOnlyAccounts() {
        accounts.clear();
        db.query("SELECT * FROM Accounts ", (Object[]) null, new RowMapper<Object>() {
            @Override
            public Object mapRow(ResultSet rs, int i) throws SQLException {
                BTAccount acc = new BTAccount(rs.getInt("accountID"), rs.getString("domainName"), rs.getString("login"), rs.getString("password"), BTAccountType.valueOf(rs.getByte("type")), rs.getBoolean("asGuest"));
                accounts.add(acc);
                return acc;

            }
        });
        return new ArrayList<BTAccount>(accounts);
    }

    public List<StackTraceElement> getMethodNameToSTElement(String className, String methodName) {
        List<StackTraceElement> result = db.query("SELECT * FROM STElements WHERE declaringClass = ? AND methodName = ?", (new Object[]{className, methodName}), stackTraceElementRowMapper);
        return result;
    }

    public List<StackTraceElement> getClassNameToSTElement(String className) {
        return db.query("SELECT * FROM STElements WHERE declaringClass = ?", (new Object[]{className}), stackTraceElementRowMapper);
    }


    public List<StackTraceElement> getFileNameToSTElement(String fileName) {
        return db.query("SELECT * FROM STElements WHERE fileName = ?", (new Object[]{fileName}), stackTraceElementRowMapper);
    }

    public void updateAccounts(List<BTAccount> accountsFromUI) {
        for (BTAccount e : accountsFromUI) {
            if (e.getAccountID() != 0) {
                db.update("UPDATE Accounts SET domainName = ? , login = ?, password = ?, type = ? , asGuest = ? WHERE accountID = ?", e.getDomainName(), e.getLogin(), e.getPassword(), e.getType().getType(), e.isAsGuest(), e.getAccountID());
            } else {
                List<BTAccount> acc = db.query("SELECT * FROM Accounts WHERE domainName= ? AND login = ? AND password = ? ", new Object[]{e.getDomainName(), e.getLogin(), e.getPassword()}, new RowMapper<BTAccount>() {
                    @Override
                    public BTAccount mapRow(ResultSet rs, int i) throws SQLException {
                        return new BTAccount(rs.getInt("accountID"), rs.getString("domainName"), rs.getString("login"), rs.getString("password"), BTAccountType.valueOf(rs.getByte("type")), rs.getBoolean("asGuest"));
                    }
                });
                if (acc.size() == 0) {
                    db.update("INSERT INTO Accounts ( domainName, login, password, type, asGuest) values (?,?,?,?,?)", e.getDomainName(), e.getLogin(), e.getPassword(), e.getType().getType(), e.isAsGuest());
                    int tmp = db.query("SELECT accountID FROM Accounts WHERE (domainName = ? AND login = ? AND password = ? )", new Object[]{e.getDomainName(), e.getLogin(), e.getPassword()}, new ResultSetExtractor<Integer>() {
                        @Override
                        public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                            rs.next();
                            return rs.getInt(1);
                        }
                    });
                    e.setAccountID(tmp);
                }
            }
            updateProjects(e.getAccountID(), e.getProjects());
        }
    }

    private void updateProjects(int accountID, List<BTProject> projects) {
        for (BTProject p : projects) {
            // if (p.isMustBeUpdated()) {
            List<BTProject> prs = db.query("SELECT * FROM Projects WHERE accountID= ? AND shortName = ? AND longName = ? ", new Object[]{accountID, p.getShortName(), p.getFullName()}, new RowMapper<BTProject>() {
                @Override
                public BTProject mapRow(ResultSet rs, int i) throws SQLException {
                    int projectID = rs.getInt("projectID");
                    String shortName = rs.getString("shortName");
                    String longName = rs.getString("longName");
                    return new BTProject(shortName, longName);
                }
            });
            if (prs.size() == 0) {
                db.update("INSERT INTO Projects ( accountID, shortName, longName, lastUpdated, mustBeUpdated) values (?,?,?,?, ?)", accountID, p.getShortName(), p.getFullName(), p.getLastUpdated(), p.isMustBeUpdated());
                p.setProjectID(db.query("SELECT projectID FROM Projects WHERE (accountID = ? AND shortName = ? AND longName = ? )", new Object[]{accountID, p.getShortName(), p.getFullName()}, new ResultSetExtractor<Integer>() {
                    @Override
                    public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                        rs.next();
                        return rs.getInt(1);

                    }
                }));
            } else {
                db.update("UPDATE Projects SET mustBeUpdated = ? WHERE projectID = ?", p.isMustBeUpdated(), p.getProjectID());
            }
            //}
        }
    }

    public void saveState() {
        for (StackTraceElement element : stElementsCache.values()) {
            if (element.mustBeUpdatedOnClose) {
                Clob json = getClobFromString(element.getWritablePlacementInfo());
                db.update("UPDATE STElements SET dndInfo = ?, onPlace = ? WHERE stElementID = ?", json, true, element.getID());
                element.mustBeUpdatedOnClose = false;
            }
        }
    }

    public void storeIssues(List<BTIssue> issues) {
        for (BTIssue e : issues) {
            List<BTIssue> btIssues = db.query("SELECT * FROM Issues WHERE number =  ?", new Object[]{e.getNumber()}, new RowMapper<BTIssue>() {
                @Override
                public BTIssue mapRow(ResultSet resultSet, int i) throws SQLException {
                    return new BTIssue();
                }
            });
            for (BTIssue issue : btIssues) {
                db.update("DELETE FROM Issues WHERE number = ?", issue.getNumber());
            }

            db.update("INSERT INTO Issues (projectID,title,description,number,lastUpdated,mustBeShown) values (?,?,?,?,?,?)", e.getProjectID(), getClobFromString(e.getTitle()), getClobFromString(e.getDescription()), e.getNumber(), e.getLastUpdated(), e.isMustBeShown());
            e.setIssueID(db.query("SELECT issueID FROM Issues WHERE (projectID = ? AND number = ?)", new Object[]{e.getProjectID(), e.getNumber()}, new ResultSetExtractor<Integer>() {
                @Override
                public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                    rs.next();
                    return rs.getInt("issueID");
                }
            }));
            storeExceptions(e.getIssueID(), e.getExceptions());
        }
        // dbChanged = true;
    }

    public void updateIssue(BTIssue issue) {
        if (issue.getIssueID() != 0)
            db.update("UPDATE Issues SET title = ? , description = ?, mustBeShown = ? WHERE issueID = ?", getClobFromString(issue.getTitle()), getClobFromString(issue.getDescription()), issue.isMustBeShown(), issue.getIssueID());
    }

    private Clob getClobFromString(String description) {
        Clob tmp = null;
        try {
            tmp = new SerialClob(description.toCharArray());
        } catch (SQLException ignored) {
        }
        return tmp;
    }


    public BTProject getProject(int projectID) {
        List<BTProject> prs = db.query("SELECT * FROM Projects WHERE projectID= ?", new Object[]{projectID}, new RowMapper<BTProject>() {
            @Override
            public BTProject mapRow(ResultSet rs, int i) throws SQLException {
                return new BTProject(rs.getInt("projectID"), rs.getInt("accountID"), rs.getString("shortName"), rs.getString("longName"), rs.getTimestamp("lastUpdated"), rs.getBoolean("mustBeUpdated"), rs.getString("customFieldName"));
            }
        });
        if (prs.size() == 0)
            return null;
        return prs.get(0);
    }


    public BTAccount getAccount(int accountID) {
        if (accountsCache.containsKey(accountID)) return accountsCache.get(accountID);
        List<BTAccount> prs = db.query("SELECT * FROM Accounts WHERE accountID= ?", new Object[]{accountID}, new RowMapper<BTAccount>() {
            @Override
            public BTAccount mapRow(ResultSet rs, int i) throws SQLException {
                return new BTAccount(rs.getInt("accountID"), rs.getString("domainName"), rs.getString("login"), rs.getString("password"), BTAccountType.valueOf(rs.getByte("type")), rs.getBoolean("asGuest"));
            }
        });
        if (prs.size() == 0)
            return null;
        BTAccount acc = prs.get(0);
        accountsCache.put(acc.getAccountID(), acc);
        return accountsCache.get(accountID);
    }


    private void storeExceptions(final int issueID, Map<Integer, ParsedException> exceptions) {
        for (final ParsedException e : exceptions.values()) {
            KeyHolder holder = new GeneratedKeyHolder();
            db.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement("INSERT INTO Exceptions (issueID,name,message) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    statement.setInt(1, issueID);
                    statement.setString(2, e.getName());
                    statement.setClob(3, getClobFromString(e.getOptionalMessage()));
                    return statement;
                }
            }, holder);
            e.setExceptionID(holder.getKey().longValue());
            Collection<StackTraceElement> values = e.getStacktrace().values();
            StackTraceElement[] elements = values.toArray(new StackTraceElement[values.size()]);
            for (final StackTraceElement el : elements) {
                el.setExceptionID(e.getExceptionID());
                KeyHolder hold = new GeneratedKeyHolder();
                db.update(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement statement = con.prepareStatement("INSERT INTO STElements (exceptionID,declaringClass,methodName,fileName, lineNumber, anOrder, onPlace, dndInfo, issueID) values (?,?,?,?,?,?, ?, ?,?)", Statement.RETURN_GENERATED_KEYS);
                        statement.setLong(1, el.getExceptionID());
                        statement.setString(2, el.getDeclaringClass());
                        statement.setString(3, el.getMethodName());
                        statement.setString(4, el.getFileName());
                        statement.setInt(5, el.getLineNumber());
                        statement.setByte(6, el.getOrder());
                        statement.setBoolean(7, el.isOnPlace());
                        statement.setClob(8, getClobFromString(el.getWritablePlacementInfo()));
                        statement.setInt(9, issueID);
                        return statement;
                    }
                }, hold);
                el.setID(hold.getKey().longValue());
            }
        }
    }
}
