package com.jetbrains.isaev.ui;

import com.intellij.CommonBundle;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.labels.ActionLink;
import com.intellij.util.IconUtil;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.integration.youtrack.YouTrackIssuesDownloadStrategy;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackClient;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackClientFactory;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackCustomField;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackProject;
import com.jetbrains.isaev.state.BTAccount;
import com.jetbrains.isaev.state.BTAccountType;
import com.jetbrains.isaev.state.BTProject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: Xottab
 * Date: 25.07.2014
 */
public class AddNewReportsSourcesDialog extends DialogWrapper {
    //static int lastSelectedPos = -1;
    private static YouTrackClientFactory clientFactory;
    private static DefaultListModel model = new DefaultListModel();

    static class MyDefaultTableModel extends DefaultTableModel {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return SelectableItem.class;
        }
    }

    private static DefaultTableModel projectsModel = new MyDefaultTableModel();

    static {
        projectsModel.addColumn("");
        projectsModel.addColumn("Project name");
        projectsModel.addColumn("Custom field");
    }

    private static DefaultComboBoxModel accountTypeModel = new DefaultComboBoxModel();

    static {
        for (BTAccountType type : BTAccountType.values()) accountTypeModel.addElement(type);
    }

    private static IssuesDAO issuesDAO = GlobalVariables.getInstance().dao;
    DocumentListener changeListener = new DocumentListener() {
        public void changedUpdate(DocumentEvent e) {
            warn();
        }

        public void removeUpdate(DocumentEvent e) {
            warn();
        }

        public void insertUpdate(DocumentEvent e) {
            warn();
        }

        public void warn() {
            if (!applyAction.isEnabled()) {
                applyAction.setEnabled(true);
            }
        }
    };
    private JPanel contentPane;
    private JButton addButton;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JTextField textField2;
    private JButton testButton;
    private JBList accountsUIList;
    private JTable projectsTable;
    private JButton processIssuesButton;
    private JLabel projectsListLabel;
    private ActionLink actionLink1;
    private JComboBox comboBox1;
    private ActionLink actionLink2;
    private JPanel workingPane;
    private JBCheckBox checkBox1;
    private JButton commitIssuesPlacementInfoButton;
    private ApplyAction applyAction = new ApplyAction();
    private List<BTAccount> mustBeDeleted = new ArrayList<BTAccount>();
    private int prevSelectedIndex = -1;

    public AddNewReportsSourcesDialog() {
        super(GlobalVariables.getInstance().project, false);
        init();
        workingPane.setVisible(false);
        comboBox1.setModel(accountTypeModel);
        comboBox1.setRenderer(new BTAccountIconListRenderer());
        clientFactory = new YouTrackClientFactory();
        setTitle("Sources of Reports");
        setModal(true);
        //textField1.setText("http://youtrack.jetbrains.com");
        //textField2.setText("Ilya.Isaev@jetbrains.com");
        //passwordField1.setText(".Lu85Ga");
        model.clear();
        List<BTAccount> accsFromDB = issuesDAO.getAccountsWithProjects();
        if (!accsFromDB.isEmpty()) {
            workingPane.setVisible(true);
            for (BTAccount acc : accsFromDB) {
                model.addElement(acc);
            }
            accountsUIList.setSelectedIndex(0);
        }
        accountsUIList.setModel(model);
        accountsUIList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountsUIList.setCellRenderer(new ExceptionsSourceCellRenderer());
        ProjectListChooser chooser = new ProjectListChooser();
        chooser.setHorizontalTextPosition(SwingConstants.CENTER);
        projectsTable.setDefaultRenderer(SelectableItem.class, chooser);
        projectsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final int row = projectsTable.rowAtPoint(e.getPoint());
                final int col = projectsTable.columnAtPoint(e.getPoint());
                final SelectableItem item = (SelectableItem) projectsModel.getValueAt(row, 1);
                if (col == 0) {
                    item.checkbox.setSelected(!item.checkbox.isSelected());
                    item.project.setMustBeUpdated(item.checkbox.isSelected());
                } else if (col == 2) {
                    final JBList tmp = new JBList(getStringCustomFieldNames(item.project));
                    tmp.setEmptyText("No string fields found (check your credentials)");
                    final PopupChooserBuilder builder = JBPopupFactory.getInstance().createListPopupBuilder(tmp);
                    builder.setItemChoosenCallback(new Runnable() {
                        @Override
                        public void run() {
                            String name = (String) tmp.getSelectedValue();
                            item.project.setCustomFieldName(name);
                            IssuesDAO.getInstance().updateProject(item.project);
                            JLabel actionLink = (JLabel) projectsModel.getValueAt(row, col);
                            actionLink.setText(item.project.getCustomFieldName());
                            actionLink.repaint();
                        }
                    });
                    final JBPopup popup = builder.createPopup();
                    popup.show(new RelativePoint(e.getLocationOnScreen()));
                }
                projectsTable.repaint();
            }
        });
        // projectsTable.getColumnModel().getColumn(0).setMinWidth(-1);
        accountsUIList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (prevSelectedIndex >= 0 && prevSelectedIndex < accountsUIList.getModel().getSize()) {
                    BTAccount account = (BTAccount) accountsUIList.getModel().getElementAt(prevSelectedIndex);
                    updateFieldsFromBTAccount(account);
                    accountsUIList.repaint();
                }

                int tmp = accountsUIList.locationToIndex(e.getPoint());
                //Messages.showInfoMessage("LALA ", prevSelectedIndex+"     "+tmp);
                prevSelectedIndex = tmp;
                if (tmp != -1) {
                    projectsModel.setRowCount(0);
                    if (model.size() > 0) {
                        BTAccount account = (BTAccount) model.get(accountsUIList.getSelectedIndex());
                        textField1.setText(account.getDomainName());
                        textField2.setText(account.getLogin());
                        passwordField1.setText(account.getPassword());
                        checkBox1.setSelected(account.isAsGuest());
                        textField2.setEnabled(!checkBox1.isSelected());
                        passwordField1.setEnabled(!checkBox1.isSelected());
                        for (BTProject project : account.getProjects()) {
                            SelectableItem item = new SelectableItem(project);
                            projectsModel.addRow(new Object[]{item.checkbox, item, new JLabel(item.getCustomFieldName())});
                        }
                    }
                    projectsTable.setModel(projectsModel);
                }
                super.mouseClicked(e);
            }
        });
        accountsUIList.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeCurrentListElement();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);
        textField1.getDocument().addDocumentListener(changeListener);
        textField2.getDocument().addDocumentListener(changeListener);
        passwordField1.getDocument().addDocumentListener(changeListener);
        checkBox1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textField2.setEnabled(!checkBox1.isSelected());
                passwordField1.setEnabled(!checkBox1.isSelected());
                if (!applyAction.isEnabled()) {
                    applyAction.setEnabled(true);
                }
            }
        });

        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCurrentAccount();
                int pos = accountsUIList.getSelectedIndex();
                if (pos != -1) {
                    YouTrackClient client = clientFactory.getClient(textField1.getText());
                    String username = textField2.getText() == null ? "" : textField2.getText();
                    String pass = passwordField1.getPassword() == null ? "" : new String(passwordField1.getPassword());
                    if (!checkBox1.isSelected()) {
                        client.login(username, pass);
                    }
                    BTAccount account = (BTAccount) accountsUIList.getModel().getElementAt(pos);
                    projectsTable.setModel(projectsModel);
                    List<YouTrackProject> projects = client.getProjects();
                    for (YouTrackProject project : projects) {
                        BTProject wrapper = new BTProject(account, project.getProjectFullName(), project.getProjectShortName());
                        if (!account.getProjects().contains(wrapper)) {
                            SelectableItem item = new SelectableItem(wrapper);
                            projectsModel.addRow(new Object[]{item.checkbox, item, new JLabel(item.getCustomFieldName())});
                            account.getProjects().add(wrapper);
                        }
                    }
                }
            }
        });

        processIssuesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int pos = accountsUIList.getSelectedIndex();
                if (pos != -1) {
                    applyAction.actionPerformed(e);
                    BTAccount account = (BTAccount) model.getElementAt(pos);
                    List<BTProject> projects = account.getProjects();
                    List<BTProject> mustBeUpdated = new ArrayList<BTProject>();
                    for (BTProject project : projects) {
                        if (project.isMustBeUpdated()) {
                            mustBeUpdated.add(project);
                        }
                    }
                    if (!mustBeUpdated.isEmpty()) {
                        if (YouTrackIssuesDownloadStrategy.alreadyRunning) {
                            Notifications.Bus.notify(new com.intellij.notification.Notification("", "Cannot load issues", "Process is already runnning", NotificationType.ERROR));
                        } else {
                            ProgressManager.getInstance().run(new YouTrackIssuesDownloadStrategy(mustBeUpdated.toArray(new BTProject[mustBeUpdated.size()])));
                            Notifications.Bus.notify(new com.intellij.notification.Notification("", "Process started in background", "Issues are loading now", NotificationType.INFORMATION));
                        }
                    }
                }
            }
        });
        commitIssuesPlacementInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int pos = accountsUIList.getSelectedIndex();
                if (pos != -1) {
                    applyAction.actionPerformed(e);
                    BTAccount account = (BTAccount) model.getElementAt(pos);
                    List<BTProject> projects = account.getProjects();
                    List<BTProject> mustBeUpdated = new ArrayList<BTProject>();
                    for (BTProject project : projects) {
                        if (project.isMustBeUpdated()) {
                            mustBeUpdated.add(project);
                        }
                    }
                    if (!mustBeUpdated.isEmpty()) {
                        if(!CommitCustomFieldInfoTask.alreadyRunning)
                            ProgressManager.getInstance().run(new CommitCustomFieldInfoTask(mustBeUpdated));
                        }
                }

            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private List<String> getStringCustomFieldNames(BTProject project) {
        List<String> tmp = new ArrayList<String>();
        BTAccount acc = project.getBtAccount();
        YouTrackClient client = clientFactory.getClient(acc.getDomainName());
        if (!acc.isAsGuest())
            client.login(acc.getLogin(), acc.getPassword());
        Set<String> fields = client.getProjectCustomFieldNames(project.getShortName());
        for (String name : fields) {
            YouTrackCustomField field = client.getProjectCustomField(project.getShortName(), name);
            if (field.getType().equals("string")) {
                tmp.add(field.getName());
            }
        }
        return tmp;
    }

    private static List<BTAccount> getAccountsFromUI() {
        List<BTAccount> accounts = new ArrayList<BTAccount>(model.size());
        for (int i = 0; i < model.size(); i++) {
            accounts.add((BTAccount) model.get(i));
        }

        return accounts;
    }

    private void updateFieldsFromBTAccount(BTAccount acc) {
        acc.setDomainName(textField1.getText());
        acc.setLogin(textField2.getText());
        acc.setPassword(new String(passwordField1.getPassword()));
        acc.setType((BTAccountType) comboBox1.getModel().getSelectedItem());
        acc.setAsGuest(checkBox1.isSelected());
    }

    private void removeCurrentListElement() {
        prevSelectedIndex = -1;
        int pos = accountsUIList.getSelectedIndex();
        if (pos != -1) {
            BTAccount acc = (BTAccount) model.get(pos);
            if (acc.getAccountID() != 0) mustBeDeleted.add(acc);
            model.remove(pos);
        }
    }

    private void saveCurrentAccount() {
        int pos = accountsUIList.getSelectedIndex();
        if (pos != -1) {
            BTAccount account = (BTAccount) model.get(pos);
            updateFieldsFromBTAccount(account);
        }
    }

    @NotNull
    protected Action[] createActions() {
        if (getHelpId() == null) {
            if (SystemInfo.isMac)
                return new Action[]{getCancelAction(), applyAction, getOKAction()};
            return new Action[]{getOKAction(), applyAction, getCancelAction()};
        } else {
            if (SystemInfo.isMac)
                return new Action[]{getHelpAction(), getCancelAction(), applyAction, getOKAction()};
            return new Action[]{getOKAction(), applyAction, getCancelAction(), getHelpAction()};
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    @Override
    protected void doOKAction() {
        persistState();
        super.doOKAction();
    }

    private void createUIComponents() {
        actionLink1 = new ActionLink("", IconUtil.getAddIcon(), new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                workingPane.setVisible(true);
                BTAccount account = new BTAccount("", "", "", BTAccountType.YOUTRACK, false);
                model.addElement(account);
            }
        });
        actionLink2 = new ActionLink("", IconUtil.getRemoveIcon(), new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                removeCurrentListElement();
            }
        });
        actionLink1.setBorder(new EmptyBorder(0, 0, 0, 5));
    }

    private void persistState() {
        saveCurrentAccount();
        accountsUIList.repaint();
        for (BTAccount acc : mustBeDeleted) issuesDAO.deleteBtAccount(acc);
        issuesDAO.updateAccounts(getAccountsFromUI());
    }

    private class ApplyAction extends AbstractAction {
        public ApplyAction() {
            super(CommonBundle.getApplyButtonText());
            setEnabled(false);
        }

        public void actionPerformed(final ActionEvent e) {
            persistState();
            setEnabled(false);
        }
    }
}
