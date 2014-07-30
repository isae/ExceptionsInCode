package com.jetbrains.isaev.ui;

import com.intellij.CommonBundle;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.components.JBList;
import com.jetbrains.isaev.common.CommonBTProject;
import com.jetbrains.isaev.common.CommonIssue;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackClient;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackClientFactory;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackIssue;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackProject;
import com.jetbrains.isaev.issues.StacktraceProvider;
import com.jetbrains.isaev.state.BTAccountStorageProvider;
import com.jetbrains.isaev.state.CommonBTAccount;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import test.YouTrackTest;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Xottab
 * Date: 25.07.2014
 */
public class AddNewReportsSourcesDialog extends DialogWrapper {
    private static YouTrackClientFactory clientFactory;
    private final MyDialog dialog;
    int lastSelectedPos = -1;
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
    private DefaultListModel<CommonBTAccount> model = new DefaultListModel<>();
    private DefaultListModel<SelectableItem<CommonBTProject>> projectsModel = new DefaultListModel<>();
    private Project project;
    private BTAccountStorageProvider provider;
    private ApplyAction applyAction = new ApplyAction();

    public AddNewReportsSourcesDialog(Project project) {
        super(project, false);
        this.project = project;
        this.dialog = new MyDialog(project);
        init();
        clientFactory = new YouTrackClientFactory();
        setTitle("Sources of Reports");
    }

    @NotNull
    protected Action[] createActions() {
        if (getHelpId() == null) {
            if (SystemInfo.isMac) {
                return new Action[]{getCancelAction(), applyAction, getOKAction()};
            }

            return new Action[]{getOKAction(), applyAction, getCancelAction()};
        } else {
            if (SystemInfo.isMac) {
                return new Action[]{getHelpAction(), getCancelAction(), applyAction, getOKAction()};
            }
            return new Action[]{getOKAction(), applyAction, getCancelAction(), getHelpAction()};
        }
    }

    private List<CommonBTAccount> getAccountsFromUI() {
        List<CommonBTAccount> accounts = new ArrayList<>(model.size());
        for (int i = 0; i < model.size(); i++) {
            accounts.add(model.get(i));
        }
        return accounts;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return dialog.getContentPane();
    }

    private class ApplyAction extends AbstractAction {
        public ApplyAction() {
            super(CommonBundle.getApplyButtonText());
            setEnabled(false);
        }

        public void actionPerformed(final ActionEvent e) {
            Messages.showInfoMessage(project, String.valueOf(lastSelectedPos), "Title");
            if (lastSelectedPos != -1 && lastSelectedPos < model.size()) {
                CommonBTAccount account = model.get(lastSelectedPos);
                account.setDomainName(dialog.textField1.getText());
                account.setLogin(dialog.textField2.getText());
                account.setPassword(new String(dialog.passwordField1.getPassword()));
            }
            dialog.accountsUIList.repaint();
            provider.getState().setBtAccounts(getAccountsFromUI());
            setEnabled(false);
        }
    }

    public class MyDialog extends JDialog {
        private JPanel contentPane;
        private JButton addButton;
        private JTextField textField1;
        private JPasswordField passwordField1;
        private JTextField textField2;
        private JButton testButton;
        private JBList accountsUIList;
        private JBList projectsList;
        private JButton processIssuesButton;

        public MyDialog(final Project project) {
            setContentPane(contentPane);
            setModal(true);
            provider = project.getComponent(BTAccountStorageProvider.class);
            if (provider.getState().getBtAccounts() == null) {
                provider.getState().setBtAccounts(new ArrayList<CommonBTAccount>());
            }
            for (CommonBTAccount acc : provider.getState().getBtAccounts()) {
                model.addElement(acc);
            }
            accountsUIList.setModel(model);
            accountsUIList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            accountsUIList.setCellRenderer(new ExceptionsSourceCellRenderer());
            projectsList.setCellRenderer(new ProjectsChooseListRenderer());
            accountsUIList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    int tmp = lastSelectedPos;
                    if (e.getFirstIndex() != lastSelectedPos) {
                        lastSelectedPos = e.getFirstIndex();
                    } else {
                        lastSelectedPos = e.getLastIndex();
                    }
                    projectsModel.clear();
                    Messages.showInfoMessage(project, String.valueOf(tmp + " " + e.getFirstIndex()) + " " + e.getLastIndex() + " " + lastSelectedPos, "Title");
                    if (model.size() > 0) {
                        CommonBTAccount account = model.get(lastSelectedPos);
                        textField1.setText(account.getDomainName());
                        textField2.setText(account.getLogin());
                        passwordField1.setText(account.getPassword());
                        for (CommonBTProject project : account.getProjects()) {
                            projectsModel.addElement(new SelectableItem<>(project, SelectableItem.getCheckBox(project)));
                        }
                    }
                    projectsList.setModel(projectsModel);
                }
            });
            accountsUIList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int index = accountsUIList.locationToIndex(e.getPoint());
                    CommonBTAccount item = (CommonBTAccount) accountsUIList.getModel().getElementAt(index);
                    int k = 0;
                    for (CommonBTProject project1 : item.getProjects()) k += project1.getIssues().size();
                    Messages.showInfoMessage("Hello " + k, "Title");

                }
            });
            accountsUIList.registerKeyboardAction(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //   Messages.showInfoMessage(project, String.valueOf(lastSelectedPos), "Title");
                    if (lastSelectedPos != -1 && model.size() > lastSelectedPos) {
                        model.remove(lastSelectedPos);
                        lastSelectedPos = -1;
                    }
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_FOCUSED);
            textField1.getDocument().addDocumentListener(changeListener);
            textField2.getDocument().addDocumentListener(changeListener);
            passwordField1.getDocument().addDocumentListener(changeListener);


            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CommonBTAccount account = new CommonBTAccount(textField1.getText(), textField2.getText(), new String(passwordField1.getPassword()));
                    model.addElement(account);
                    //  ActionGroup actionGroup = new DefaultActionGroup(new DumbAction2(), new DumbAction2());
                    //  DataContext context = SimpleDataContext.getProjectContext(null);
                    //  JBPopupFactory.getInstance().createActionGroupPopup("Choose system", actionGroup, context, JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, true);
                }
            });

            testButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    YouTrackClient client = clientFactory.getClient(textField1.getText());
                    client.login(textField2.getText(), new String(passwordField1.getPassword()));
                    CommonBTAccount account = (CommonBTAccount) accountsUIList.getModel().getElementAt(lastSelectedPos);
                    projectsList.setModel(projectsModel);
                    List<YouTrackProject> projects = client.getProjects();
                    List<CommonBTProject> mustBeAdded = new ArrayList<>();
                    for (YouTrackProject project : projects) {
                        CommonBTProject wrapper = new CommonBTProject(project.getProjectFullName(), project.getProjectShortName());
                        if (!projectsModel.contains(wrapper)) {
                            projectsModel.addElement(new SelectableItem<>(wrapper));
                            mustBeAdded.add(wrapper);
                        }
                    }
                    account.getProjects().addAll(mustBeAdded);

                }
            });

            processIssuesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CommonBTAccount account = model.getElementAt(lastSelectedPos);
                    List<CommonBTProject> projects = account.getProjects();
                    for (CommonBTProject project : projects) {
                        if (project.isMustBeUpdated()) {
                            YouTrackClient client = clientFactory.getClient(textField1.getText());
                            client.login(textField2.getText(), new String(passwordField1.getPassword()));
                            List<YouTrackIssue> issues = YouTrackTest.getIssuesAvoidBugged(project.getShortName(), "", 0, 1000, project.getLastUpdated(), client, null);
                            StacktraceProvider provider = StacktraceProvider.getInstance();
                            int k = 0;
                            for (YouTrackIssue issue : issues) {
                                List<ParsedException> parsedExceptions = provider.parseAllExceptions(issue.getSummary() + " " + issue.getDescription());
                                if (parsedExceptions.size() != 0) {
                                    CommonIssue is = new CommonIssue();
                                    is.setDescription(issue.getDescription());
                                    is.setTitle(issue.getSummary());
                                    is.setExceptions(parsedExceptions);
                                    project.getIssues().add(is);
                                    ++k;
                                }
                            }
                            Messages.showInfoMessage("Hello: " + k, "");
                            applyAction.setEnabled(true);
                            project.setLastUpdated(System.currentTimeMillis());
                        }
                    }
                }
            });

            projectsList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int index = projectsList.locationToIndex(e.getPoint());
                    SelectableItem<CommonBTProject> item = (SelectableItem<CommonBTProject>) projectsList.getModel().getElementAt(index);
                    //  Messages.showInfoMessage(project, "Hello " + index + " " + item.value.getTarget().getFullName() + " " + item.checkbox.isSelected(), "Title");
                    item.checkbox.setSelected(!item.checkbox.isSelected());
                    item.value.setMustBeUpdated(item.checkbox.isSelected());
                    //Messages.showInfoMessage(project, String.valueOf(item.checkbox.isSelected()), "Title");
                    Rectangle rect = projectsList.getCellBounds(index, index);
                    projectsList.repaint();//repaint(rect);
                }
            });

// call onCancel() when cross is clicked
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    onCancel();
                }
            });

// call onCancel() on ESCAPE
            contentPane.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    onCancel();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        }

        public JPanel getContentPane() {
            return contentPane;
        }

        private void onOK() {
// add your code here
            provider.getState().setBtAccounts(getAccountsFromUI());
            dispose();
        }


        private void onCancel() {
// add your code here if necessary
            dispose();
        }


        private void createUIComponents() {
            // TODO: place custom component creation code here
        }
    }
}
