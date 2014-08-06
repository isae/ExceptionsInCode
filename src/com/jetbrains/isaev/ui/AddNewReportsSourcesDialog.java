package com.jetbrains.isaev.ui;

import com.intellij.CommonBundle;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.components.JBList;
import com.jetbrains.isaev.GlobalVariables;
import com.jetbrains.isaev.dao.IssuesDAO;
import com.jetbrains.isaev.dao.SerializableIssuesDAO;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackClient;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackClientFactory;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackIssue;
import com.jetbrains.isaev.integration.youtrack.client.YouTrackProject;
import com.jetbrains.isaev.issues.StacktraceProvider;
import com.jetbrains.isaev.state.BTAccount;
import com.jetbrains.isaev.state.BTIssue;
import com.jetbrains.isaev.state.BTProject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    static int lastSelectedPos = -1;
    private static YouTrackClientFactory clientFactory;
    private static DefaultListModel<BTAccount> model = new DefaultListModel<>();
    private static DefaultListModel<SelectableItem<BTProject>> projectsModel = new DefaultListModel<>();
    private static IssuesDAO issuesDAO = SerializableIssuesDAO.getInstance();
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
    private JBList projectsList;
    private JButton processIssuesButton;
    private ApplyAction applyAction = new ApplyAction();

    public AddNewReportsSourcesDialog() {
        super(GlobalVariables.project, false);
        init();
        clientFactory = new YouTrackClientFactory();
        setTitle("Sources of Reports");

        setModal(true);
        textField1.setText("http://youtrack.jetbrains.com");
        textField2.setText("Ilya.Isaev@jetbrains.com");
        passwordField1.setText(".Lu85Ga");
        for (BTAccount acc : issuesDAO.getAccounts()) {
            model.addElement(acc);
            for (BTProject proj : acc.getProjects())
                for (BTIssue issue : proj.getIssues())
                    System.out.println(issue.getNumber());
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
                // Messages.showInfoMessage(GlobalVariables.project, String.valueOf(tmp + " " + e.getFirstIndex()) + " " + e.getLastIndex() + " " + lastSelectedPos, "Title");
                if (model.size() > 0) {
                    BTAccount account = model.get(lastSelectedPos);
                    textField1.setText(account.getDomainName());
                    textField2.setText(account.getLogin());
                    passwordField1.setText(account.getPassword());
                    for (BTProject project : account.getProjects()) {
                        projectsModel.addElement(new SelectableItem<>(project, SelectableItem.getCheckBox(project)));
                    }
                }
                projectsList.setModel(projectsModel);
            }
        });
        accountsUIList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (accountsUIList.getModel().getSize() > 0) {
                    int index = accountsUIList.locationToIndex(e.getPoint());
                    BTAccount item = (BTAccount) accountsUIList.getModel().getElementAt(index);
                    int k = 0;
                    for (BTProject project1 : item.getProjects()) k += project1.getIssues().size();
                    //Messages.showInfoMessage("Hello " + k, "Title");
                }

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
                BTAccount account = new BTAccount(textField1.getText(), textField2.getText(), new String(passwordField1.getPassword()));
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
                BTAccount account = (BTAccount) accountsUIList.getModel().getElementAt(lastSelectedPos);
                projectsList.setModel(projectsModel);
                List<YouTrackProject> projects = client.getProjects();
                List<BTProject> mustBeAdded = new ArrayList<>();
                for (YouTrackProject project : projects) {
                    BTProject wrapper = new BTProject(project.getProjectFullName(), project.getProjectShortName());
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
                BTAccount account = model.getElementAt(lastSelectedPos);
                List<BTProject> projects = account.getProjects();
                for (BTProject project : projects) {
                    //   System.out.println(project.getShortName()+" "+project.getShortName().equals("IDEA"));
                    if (project.isMustBeUpdated() & project.getShortName().equals("IDEA")) {
                        YouTrackClient client = clientFactory.getClient(textField1.getText());
                        client.login(textField2.getText(), new String(passwordField1.getPassword()));
                        //   List<YouTrackIssue> issues = YouTrackTest.getIssuesAvoidBugged(project.getShortName(), "", 0, 1000, project.getLastUpdated(), client, null);
                        StacktraceProvider provider = StacktraceProvider.getInstance();
                        //  int k = 0;
                            /*for (YouTrackIssue issue : issues) {
                                List<ParsedException> parsedExceptions = provider.parseAllExceptions(issue.getSummary() + " " + issue.getDescription());
                                if (parsedExceptions.size() != 0) {
                                    BTIssue is = new BTIssue();
                                    is.setDescription(issue.getDescription());
                                    is.setTitle(issue.getSummary());
                                    is.setNumber(issue.getId());
                                    is.setExceptions(parsedExceptions);
                                    project.getIssues().add(is);
                                    ++k;
                                }
                            }*/
                        for (String s : ExceptionTest.issues) {
                            YouTrackIssue issue = client.getIssue(s, false);
                            YouTrackIssue issue2 = client.getIssue(s, true);
                            List<ParsedException> parsedExceptions = provider.parseAllExceptions(issue.getSummary() + " " + issue.getDescription());
                            if (parsedExceptions.size() != 0) {
                                BTIssue is = new BTIssue();
                                for (ParsedException ex : parsedExceptions) ex.setIssue(is);
                                is.setDescription(issue2.getDescription());
                                is.setTitle(issue2.getSummary());
                                is.setNumber(issue.getId());
                                is.setExceptions(parsedExceptions);
                                project.getIssues().add(is);
                                issuesDAO.getIssues().add(is);
                            }
                        }
                        //Messages.showInfoMessage("Hello: " + k, "");
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
                SelectableItem<BTProject> item = (SelectableItem<BTProject>) projectsList.getModel().getElementAt(index);
                //  Messages.showInfoMessage(project, "Hello " + index + " " + item.value.getTarget().getFullName() + " " + item.checkbox.isSelected(), "Title");
                item.checkbox.setSelected(!item.checkbox.isSelected());
                item.value.setMustBeUpdated(item.checkbox.isSelected());
                //Messages.showInfoMessage(project, String.valueOf(item.checkbox.isSelected()), "Title");
                Rectangle rect = projectsList.getCellBounds(index, index);
                projectsList.repaint();//repaint(rect);
            }
        });

// call onCancel() when cross is clicked
        //  setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
       /*addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });*/

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private static List<BTAccount> getAccountsFromUI() {
        List<BTAccount> accounts = new ArrayList<>(model.size());
        for (int i = 0; i < model.size(); i++) {
            accounts.add(model.get(i));
        }
        return accounts;
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

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }

    private void onOK() {
// add your code here
        issuesDAO.saveAccounts(getAccountsFromUI());
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private static class ExceptionTest {
        public static String[] issues = {"IDEA-104113", "IDEA-121168"};
    }

    private class ApplyAction extends AbstractAction {
        public ApplyAction() {
            super(CommonBundle.getApplyButtonText());
            setEnabled(false);
        }

        public void actionPerformed(final ActionEvent e) {
            //Messages.showInfoMessage(GlobalVariables.project, String.valueOf(lastSelectedPos), "Title");
            if (lastSelectedPos != -1 && lastSelectedPos < model.size()) {
                BTAccount account = model.get(lastSelectedPos);
                account.setDomainName(textField1.getText());
                account.setLogin(textField2.getText());
                account.setPassword(new String(passwordField1.getPassword()));
            }
            accountsUIList.repaint();
            issuesDAO.saveAccounts(getAccountsFromUI());
            setEnabled(false);
        }
    }
}
