/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2010 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.alert;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.ScrollableSizeHint;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.core.scanner.Alert;
import org.parosproxy.paros.core.scanner.Plugin;
import org.parosproxy.paros.core.scanner.PluginFactory;
import org.parosproxy.paros.extension.AbstractPanel;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.pscan.ExtensionPassiveScan;
import org.zaproxy.zap.extension.pscan.PluginPassiveScanner;
import org.zaproxy.zap.model.Vulnerabilities;
import org.zaproxy.zap.model.Vulnerability;
import org.zaproxy.zap.utils.FontUtils;
import org.zaproxy.zap.utils.ZapLabel;
import org.zaproxy.zap.utils.ZapNumberSpinner;
import org.zaproxy.zap.utils.ZapTextArea;
import org.zaproxy.zap.utils.ZapTextField;
import org.zaproxy.zap.view.LayoutHelper;

public class AlertViewPanel extends AbstractPanel {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(AlertViewPanel.class);

    private static final int UNDEFINED_ID = -1;

    private static final Insets DEFAULT_INSETS = new Insets(1, 1, 1, 1);

    private JScrollPane defaultPane = null;
    private JScrollPane alertPane = null;
    private ZapTextArea defaultOutput = null;
    private JXPanel alertDisplay = null;
    private CardLayout cardLayout = null;

    private ZapLabel alertUrl = null;
    private ZapLabel alertName = null;
    private JLabel alertRisk = null;
    private JLabel alertConfidence = null;
    private ZapLabel alertParam = null;
    private ZapLabel alertAttack = null;
    private ZapLabel alertEvidence = null;
    private ZapLabel alertInjectionLocation = null;
    private ZapTextArea alertDescription = null;
    private ZapTextArea alertOtherInfo = null;
    private ZapTextArea alertSolution = null;
    private ZapTextArea alertReference = null;
    private ZapLabel alertCweId = null;
    private ZapLabel alertWascId = null;
    private ZapLabel alertSource;

    private JComboBox<String> alertEditName = null;
    private JComboBox<String> alertEditRisk = null;
    private JComboBox<String> alertEditConfidence = null;
    private JComboBox<String> alertEditParam = null;
    private ZapTextField alertEditAttack = null;
    private ZapTextField alertEditEvidence = null;
    private DefaultComboBoxModel<String> nameListModel = null;
    private DefaultComboBoxModel<String> paramListModel = null;
    private ZapNumberSpinner alertEditCweId = null;
    private ZapNumberSpinner alertEditWascId = null;

    private JLabel attackLabel;
    private JLabel cweidLabel;
    private JLabel evidenceLabel;
    private JLabel injectionLocationLabel;
    private JLabel otherLabel;
    private JLabel confidenceLabel;
    private JLabel riskLabel;
    private JLabel sourceLabel;
    private JLabel urlLabel;
    private JLabel wascidLabel;

    private boolean editable = false;
    private Alert originalAlert = null;
    private List<Vulnerability> vulnerabilities = null;

    private HistoryReference historyRef = null;

    /** Used to set the {@code HttpMessage} to the new alert when there is no {@code historyRef}. */
    private HttpMessage httpMessage;

    public AlertViewPanel() {
        this(false);
    }

    public AlertViewPanel(boolean editable) {
        super();
        this.editable = editable;
        initialize();
    }

    /** This method initializes this */
    private void initialize() {
        cardLayout = new CardLayout();
        this.setLayout(cardLayout);
        this.setName("AlertView");

        if (!editable) {
            this.add(getDefaultPane(), getDefaultPane().getName());
        }
        this.add(getAlertPane(), getAlertPane().getName());
    }

    private JScrollPane getAlertPane() {
        if (alertPane == null) {
            alertPane = new JScrollPane();
            alertPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            alertPane.setViewportView(getAlertDisplay());
            alertPane.setName("alertPane");
        }
        return alertPane;
    }

    private ZapTextArea createZapTextArea() {
        ZapTextArea ZapTextArea = new ZapTextArea(3, 30);
        ZapTextArea.setLineWrap(true);
        ZapTextArea.setWrapStyleWord(true);
        ZapTextArea.setEditable(editable);
        return ZapTextArea;
    }

    private JScrollPane createJScrollPane(String name) {
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        null,
                        name,
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        FontUtils.getFont(FontUtils.Size.standard)));
        return jScrollPane;
    }

    private JPanel getAlertDisplay() {
        if (alertDisplay == null) {
            alertDisplay = new JXPanel();
            alertDisplay.setLayout(new GridBagLayout());
            alertDisplay.setScrollableHeightHint(ScrollableSizeHint.NONE);
            alertDisplay.setName("alertDisplay");

            // Create the labels

            alertEditName = new JComboBox<>();
            alertEditName.setEditable(true);
            nameListModel = new DefaultComboBoxModel<>();

            List<String> allVulns = getAllVulnerabilityNames();
            nameListModel.addElement(""); // Default to blank
            for (String vuln : allVulns) {
                nameListModel.addElement(vuln);
            }

            alertEditName.setModel(nameListModel);
            alertEditName.addActionListener(
                    new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if ("comboBoxChanged".equals(e.getActionCommand())) {
                                Vulnerability v =
                                        getVulnerability((String) alertEditName.getSelectedItem());
                                if (v != null) {
                                    if (v.getDescription() != null
                                            && v.getDescription().length() > 0) {
                                        setAlertDescription(v.getDescription());
                                    }
                                    if (v.getSolution() != null && v.getSolution().length() > 0) {
                                        setAlertSolution(v.getSolution());
                                    }
                                    if (v.getReferences() != null) {
                                        StringBuilder sb = new StringBuilder();
                                        for (String ref : v.getReferences()) {
                                            sb.append(ref);
                                            sb.append('\n');
                                        }
                                        setAlertReference(sb.toString());
                                    }
                                    alertEditWascId.setValue(v.getWascId());
                                }
                            }
                        }
                    });

            alertEditRisk = new JComboBox<>(Alert.MSG_RISK);
            alertEditConfidence = new JComboBox<>(Alert.MSG_CONFIDENCE);
            alertEditConfidence.setSelectedItem(Alert.MSG_CONFIDENCE[Alert.CONFIDENCE_MEDIUM]);
            alertEditAttack = new ZapTextField();

            paramListModel = new DefaultComboBoxModel<>();
            paramListModel.addElement(""); // Default is empty so user can type anything in
            alertEditParam = new JComboBox<>();
            alertEditParam.setModel(paramListModel);
            alertEditParam.setEditable(true);

            alertEditEvidence = new ZapTextField();
            alertEditCweId = new ZapNumberSpinner();
            if (alertEditCweId.getEditor() instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor) alertEditCweId.getEditor())
                        .getTextField()
                        .setHorizontalAlignment(JTextField.LEFT);
            }
            alertEditWascId = new ZapNumberSpinner();
            if (alertEditWascId.getEditor() instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor) alertEditWascId.getEditor())
                        .getTextField()
                        .setHorizontalAlignment(JTextField.LEFT);
            }

            // Read only ones
            alertName = new ZapLabel();
            alertName.setFont(FontUtils.getFont(Font.BOLD));
            alertName.setLineWrap(true);

            alertRisk = new JLabel();
            alertConfidence = new JLabel();
            alertParam = new ZapLabel();
            alertParam.setLineWrap(true);
            alertAttack = new ZapLabel();
            alertAttack.setLineWrap(true);
            alertEvidence = new ZapLabel();
            alertEvidence.setLineWrap(true);
            alertInjectionLocation = new ZapLabel();
            alertInjectionLocation.setLineWrap(true);
            alertCweId = new ZapLabel();
            alertWascId = new ZapLabel();
            alertSource = new ZapLabel();
            alertSource.setLineWrap(true);

            alertUrl = new ZapLabel();
            alertUrl.setLineWrap(true);

            alertDescription = createZapTextArea();
            JScrollPane descSp = createJScrollPane(Constant.messages.getString("alert.label.desc"));
            descSp.setViewportView(alertDescription);
            alertDescription.addKeyListener(
                    new KeyAdapter() {
                        // Change tab key to transfer focus to the next element
                        @Override
                        public void keyPressed(java.awt.event.KeyEvent evt) {
                            if (evt.getKeyCode() == KeyEvent.VK_TAB) {
                                alertDescription.transferFocus();
                            }
                        }
                    });

            alertOtherInfo = createZapTextArea();
            JScrollPane otherSp =
                    createJScrollPane(Constant.messages.getString("alert.label.other"));
            otherSp.setViewportView(alertOtherInfo);
            alertOtherInfo.addKeyListener(
                    new KeyAdapter() {
                        // Change tab key to transfer focus to the next element
                        @Override
                        public void keyPressed(java.awt.event.KeyEvent evt) {
                            if (evt.getKeyCode() == KeyEvent.VK_TAB) {
                                alertOtherInfo.transferFocus();
                            }
                        }
                    });

            alertSolution = createZapTextArea();
            JScrollPane solutionSp =
                    createJScrollPane(Constant.messages.getString("alert.label.solution"));
            solutionSp.setViewportView(alertSolution);
            alertSolution.addKeyListener(
                    new KeyAdapter() {
                        // Change tab key to transfer focus to the next element
                        @Override
                        public void keyPressed(java.awt.event.KeyEvent evt) {
                            if (evt.getKeyCode() == KeyEvent.VK_TAB) {
                                alertSolution.transferFocus();
                            }
                        }
                    });

            alertReference = createZapTextArea();
            JScrollPane referenceSp =
                    createJScrollPane(Constant.messages.getString("alert.label.ref"));
            referenceSp.setViewportView(alertReference);
            alertReference.addKeyListener(
                    new KeyAdapter() {
                        // Change tab key to transfer focus to the next element
                        @Override
                        public void keyPressed(java.awt.event.KeyEvent evt) {
                            if (evt.getKeyCode() == KeyEvent.VK_TAB) {
                                alertReference.transferFocus();
                            }
                        }
                    });

            int gbcRow = 0;

            alertDisplay.add(
                    editable ? alertEditName : alertName,
                    LayoutHelper.getGBC(0, gbcRow, 2, 0, DEFAULT_INSETS));
            // Show a blank label instead of the edit button if already editing
            gbcRow++;
            alertDisplay.add(getUrlLabel(), LayoutHelper.getGBC(0, gbcRow, 1, 0, DEFAULT_INSETS));
            alertDisplay.add(alertUrl, LayoutHelper.getGBC(1, gbcRow, 1, 1, DEFAULT_INSETS));
            gbcRow++;
            alertDisplay.add(getRiskLabel(), LayoutHelper.getGBC(0, gbcRow, 1, 0, DEFAULT_INSETS));
            alertDisplay.add(
                    editable ? alertEditRisk : alertRisk,
                    LayoutHelper.getGBC(1, gbcRow, 1, 1, DEFAULT_INSETS));
            gbcRow++;
            alertDisplay.add(
                    getConfidenceLabel(), LayoutHelper.getGBC(0, gbcRow, 1, 0, DEFAULT_INSETS));
            alertDisplay.add(
                    editable ? alertEditConfidence : alertConfidence,
                    LayoutHelper.getGBC(1, gbcRow, 1, 1, DEFAULT_INSETS));
            gbcRow++;
            alertDisplay.add(
                    getParameterLabel(), LayoutHelper.getGBC(0, gbcRow, 1, 0, DEFAULT_INSETS));
            alertDisplay.add(
                    editable ? alertEditParam : alertParam,
                    LayoutHelper.getGBC(1, gbcRow, 1, 1, DEFAULT_INSETS));
            gbcRow++;
            alertDisplay.add(
                    getAttackLabel(), LayoutHelper.getGBC(0, gbcRow, 1, 0, DEFAULT_INSETS));
            alertDisplay.add(
                    editable ? alertEditAttack : alertAttack,
                    LayoutHelper.getGBC(1, gbcRow, 1, 1, DEFAULT_INSETS));
            gbcRow++;
            alertDisplay.add(
                    getEvidenceLabel(), LayoutHelper.getGBC(0, gbcRow, 1, 0, DEFAULT_INSETS));
            alertDisplay.add(
                    editable ? alertEditEvidence : alertEvidence,
                    LayoutHelper.getGBC(1, gbcRow, 1, 1, DEFAULT_INSETS));
            gbcRow++;
            alertDisplay.add(getCweidLabel(), LayoutHelper.getGBC(0, gbcRow, 1, 0, DEFAULT_INSETS));
            alertDisplay.add(
                    editable ? alertEditCweId : alertCweId,
                    LayoutHelper.getGBC(1, gbcRow, 1, 1, DEFAULT_INSETS));
            gbcRow++;
            alertDisplay.add(
                    getWascidLabel(), LayoutHelper.getGBC(0, gbcRow, 1, 0, DEFAULT_INSETS));
            alertDisplay.add(
                    editable ? alertEditWascId : alertWascId,
                    LayoutHelper.getGBC(1, gbcRow, 1, 1, DEFAULT_INSETS));
            gbcRow++;
            if (!editable) {
                alertDisplay.add(
                        getSourceLabel(), LayoutHelper.getGBC(0, gbcRow, 1, 0, DEFAULT_INSETS));
                alertDisplay.add(alertSource, LayoutHelper.getGBC(1, gbcRow, 1, 1, DEFAULT_INSETS));
                gbcRow++;
                alertDisplay.add(
                        getInjectionLocationLabel(),
                        LayoutHelper.getGBC(0, gbcRow, 1, 0, DEFAULT_INSETS));
                alertDisplay.add(
                        alertInjectionLocation,
                        LayoutHelper.getGBC(1, gbcRow, 1, 1, DEFAULT_INSETS));
                gbcRow++;
            }

            alertDisplay.add(
                    descSp,
                    LayoutHelper.getGBC(
                            0, gbcRow, 2, 1.0D, 1.0D, GridBagConstraints.BOTH, DEFAULT_INSETS));
            gbcRow++;

            alertDisplay.add(
                    otherSp,
                    LayoutHelper.getGBC(
                            0, gbcRow, 2, 1.0D, 1.0D, GridBagConstraints.BOTH, DEFAULT_INSETS));
            gbcRow++;

            alertDisplay.add(
                    solutionSp,
                    LayoutHelper.getGBC(
                            0, gbcRow, 2, 1.0D, 1.0D, GridBagConstraints.BOTH, DEFAULT_INSETS));
            gbcRow++;

            alertDisplay.add(
                    referenceSp,
                    LayoutHelper.getGBC(
                            0, gbcRow, 2, 1.0D, 1.0D, GridBagConstraints.BOTH, DEFAULT_INSETS));
        }
        return alertDisplay;
    }

    public void displayAlert(Alert alert) {
        this.originalAlert = alert;

        alertUrl.setText(alert.getUri());

        if (editable) {
            nameListModel.addElement(alert.getName());
            alertEditName.setSelectedItem(alert.getName());
            alertEditRisk.setSelectedItem(Alert.MSG_RISK[alert.getRisk()]);
            alertEditConfidence.setSelectedItem(Alert.MSG_CONFIDENCE[alert.getConfidence()]);
            alertEditParam.setSelectedItem(alert.getParam());
            alertEditAttack.setText(alert.getAttack());
            alertEditAttack.discardAllEdits();
            alertEditEvidence.setText(alert.getEvidence());
            alertEditEvidence.discardAllEdits();
            alertEditCweId.setValue(alert.getCweId());
            alertEditWascId.setValue(alert.getWascId());

        } else {
            alertName.setText(alert.getName());

            alertRisk.setText(Alert.MSG_RISK[alert.getRisk()]);
            alertRisk.setIcon(alert.getIcon());
            alertConfidence.setText(Alert.MSG_CONFIDENCE[alert.getConfidence()]);
            alertParam.setText(alert.getParam());
            alertAttack.setText(alert.getAttack());
            alertEvidence.setText(alert.getEvidence());
            alertInjectionLocation.setText(getInjectionLocationText(alert));
            alertCweId.setText(normalisedId(alert.getCweId()));
            alertWascId.setText(normalisedId(alert.getWascId()));
            alertSource.setText(getSourceData(alert));
        }

        setAlertDescription(alert.getDescription());
        setAlertOtherInfo(alert.getOtherInfo());
        setAlertSolution(alert.getSolution());
        setAlertReference(alert.getReference());

        cardLayout.show(this, getAlertPane().getName());
    }

    private static String normalisedId(int id) {
        return id != UNDEFINED_ID ? Integer.toString(id) : "";
    }

    private String getSourceData(Alert alert) {
        String source = Constant.messages.getString(alert.getSource().getI18nKey());
        if (alert.getPluginId() == UNDEFINED_ID) {
            return source;
        }

        StringBuilder strBuilder = new StringBuilder(source);
        strBuilder.append(" (").append(alert.getPluginId());
        if (alert.getSource() == Alert.Source.ACTIVE) {
            Plugin plugin = PluginFactory.getLoadedPlugin(alert.getPluginId());
            if (plugin != null) {
                strBuilder.append(" - ").append(plugin.getName());
            }
        } else if (alert.getSource() == Alert.Source.PASSIVE) {
            ExtensionPassiveScan ext =
                    Control.getSingleton()
                            .getExtensionLoader()
                            .getExtension(ExtensionPassiveScan.class);
            if (ext != null) {
                PluginPassiveScanner scanner = ext.getPluginPassiveScanner(alert.getPluginId());
                if (scanner != null) {
                    strBuilder.append(" - ").append(scanner.getName());
                }
            }
        }
        strBuilder.append(')');
        return strBuilder.toString();
    }

    /**
     * @param alert
     * @return a pretty string representing injection location or empty string if alert is injection
     *     location is null/empty
     */
    private String getInjectionLocationText(Alert alert) {
        if (alert.getInjectionLocation() == null || alert.getInjectionLocation().length() == 0) {
            return "";
        }
        return Constant.messages.getString(
                "variant.alert.location." + alert.getInjectionLocation());
    }

    public void clearAlert() {
        cardLayout.show(this, getDefaultPane().getName());

        originalAlert = null;
        historyRef = null;
        httpMessage = null;

        alertName.setText("");
        alertRisk.setText("");
        alertConfidence.setText("");
        alertParam.setText("");
        alertAttack.setText("");
        alertDescription.setText("");
        alertOtherInfo.setText("");
        alertSolution.setText("");
        alertReference.setText("");
        alertSource.setText("");
        alertInjectionLocation.setText("");

        if (editable) {
            alertEditAttack.setText("");
            alertEditAttack.discardAllEdits();
            alertEditEvidence.setText("");
            alertEditEvidence.discardAllEdits();
            alertDescription.discardAllEdits();
            alertOtherInfo.discardAllEdits();
            alertSolution.discardAllEdits();
            alertReference.discardAllEdits();
        }
    }

    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getDefaultPane() {
        if (defaultPane == null) {
            defaultPane = new JScrollPane();
            defaultPane.setViewportView(getDefaultOutput());
            defaultPane.setName("defaultPane");
            defaultPane.setHorizontalScrollBarPolicy(
                    javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        }
        return defaultPane;
    }

    private ZapTextArea getDefaultOutput() {
        if (defaultOutput == null) {
            defaultOutput = new ZapTextArea();
            defaultOutput.setEditable(false);
            defaultOutput.setLineWrap(true);
            defaultOutput.setName("");
            defaultOutput.append(Constant.messages.getString("alerts.label.defaultMessage"));
        }
        return defaultOutput;
    }

    public void append(final String msg) {
        if (EventQueue.isDispatchThread()) {
            getDefaultOutput().append(msg);
            return;
        }
        try {
            EventQueue.invokeAndWait(
                    new Runnable() {
                        @Override
                        public void run() {
                            getDefaultOutput().append(msg);
                        }
                    });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void clear() {
        getDefaultOutput().setText("");
    }

    public void setParamNames(String[] paramNames) {
        for (String param : paramNames) {
            paramListModel.addElement(param);
        }
    }

    public Alert getAlert() {
        if (!editable && originalAlert != null) {
            Alert alert = originalAlert.newInstance();
            alert.setAlertId(originalAlert.getAlertId());
            alert.setName((String) alertEditName.getSelectedItem());
            alert.setParam((String) alertEditParam.getSelectedItem());
            alert.setRiskConfidence(
                    alertEditRisk.getSelectedIndex(), alertEditConfidence.getSelectedIndex());
            alert.setDescription(alertDescription.getText());
            alert.setOtherInfo(alertOtherInfo.getText());
            alert.setSolution(alertSolution.getText());
            alert.setReference(alertReference.getText());
            alert.setEvidence(alertEvidence.getText());
            alert.setInjectionLocation(originalAlert.getInjectionLocation());
            alert.setCweId(alertEditCweId.getValue());
            alert.setWascId(alertEditWascId.getValue());
            alert.setHistoryRef(historyRef);

            return alert;
        }

        Alert alert =
                new Alert(
                        -1,
                        alertEditRisk.getSelectedIndex(),
                        alertEditConfidence.getSelectedIndex(),
                        (String) alertEditName.getSelectedItem());
        alert.setHistoryRef(historyRef);
        if (originalAlert != null) {
            alert.setAlertId(originalAlert.getAlertId());
            alert.setSource(originalAlert.getSource());
            alert.setInjectionLocation(originalAlert.getInjectionLocation());
        }

        String uri = null;
        HttpMessage msg = null;
        if (httpMessage != null) {
            uri = httpMessage.getRequestHeader().getURI().toString();
            msg = httpMessage;
        } else if (historyRef != null) {
            try {
                uri = historyRef.getURI().toString();
                msg = historyRef.getHttpMessage();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else if (originalAlert != null) {
            uri = originalAlert.getUri();
            msg = originalAlert.getMessage();
        }
        alert.setDetail(
                alertDescription.getText(),
                uri,
                (String) alertEditParam.getSelectedItem(),
                alertEditAttack.getText(),
                alertOtherInfo.getText(),
                alertSolution.getText(),
                alertReference.getText(),
                alertEditEvidence.getText(),
                alertEditCweId.getValue(),
                alertEditWascId.getValue(),
                msg);
        return alert;
    }

    public Alert getOriginalAlert() {
        return this.originalAlert;
    }

    public void setHistoryRef(HistoryReference historyRef) {
        this.historyRef = historyRef;
        this.httpMessage = null;
        try {
            if (historyRef != null) {
                HttpMessage msg = historyRef.getHttpMessage();
                setParamNames(msg.getParamNames());
                this.alertUrl.setText(msg.getRequestHeader().getURI().toString());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Sets the {@code HttpMessage} that will be set to the new alert.
     *
     * @param httpMessage the {@code HttpMessage} that will be set to the new alert
     */
    public void setHttpMessage(HttpMessage httpMessage) {
        this.httpMessage = httpMessage;
        setParamNames(httpMessage.getParamNames());
        this.alertUrl.setText(httpMessage.getRequestHeader().getURI().toString());
        this.historyRef = null;
    }

    public boolean isEditable() {
        return editable;
    }

    private List<Vulnerability> getAllVulnerabilities() {
        if (vulnerabilities == null) {
            vulnerabilities = Vulnerabilities.getAllVulnerabilities();
        }
        return vulnerabilities;
    }

    private Vulnerability getVulnerability(String alert) {
        if (alert == null) {
            return null;
        }
        List<Vulnerability> vulns = this.getAllVulnerabilities();
        for (Vulnerability v : vulns) {
            if (alert.equals(v.getAlert())) {
                return v;
            }
        }
        return null;
    }

    private List<String> getAllVulnerabilityNames() {
        List<Vulnerability> vulns = this.getAllVulnerabilities();
        List<String> names = new ArrayList<>(vulns.size());
        for (Vulnerability v : vulns) {
            names.add(v.getAlert());
        }
        Collections.sort(names);
        return names;
    }

    private void setAlertDescription(String description) {
        setTextDiscardEditsAndInitCaretPosition(alertDescription, description);
    }

    private void setAlertOtherInfo(String otherInfo) {
        setTextDiscardEditsAndInitCaretPosition(alertOtherInfo, otherInfo);
    }

    private void setAlertSolution(String solution) {
        setTextDiscardEditsAndInitCaretPosition(alertSolution, solution);
    }

    private void setAlertReference(String reference) {
        setTextDiscardEditsAndInitCaretPosition(alertReference, reference);
    }

    private static void setTextDiscardEditsAndInitCaretPosition(ZapTextArea textArea, String text) {
        textArea.setText(text);
        textArea.discardAllEdits();
        textArea.setCaretPosition(0);
    }

    private JLabel getAttackLabel() {
        if (attackLabel == null) {
            attackLabel = new JLabel(Constant.messages.getString("alert.label.attack"));
        }
        return attackLabel;
    }

    private JLabel getCweidLabel() {
        if (cweidLabel == null) {
            cweidLabel = new JLabel(Constant.messages.getString("alert.label.cweid"));
        }
        return cweidLabel;
    }

    private JLabel getEvidenceLabel() {
        if (evidenceLabel == null) {
            evidenceLabel = new JLabel(Constant.messages.getString("alert.label.evidence"));
        }
        return evidenceLabel;
    }

    private JLabel getInjectionLocationLabel() {
        if (injectionLocationLabel == null) {
            injectionLocationLabel =
                    new JLabel(Constant.messages.getString("alert.label.injectionlocation"));
        }
        return injectionLocationLabel;
    }

    private JLabel getParameterLabel() {
        if (otherLabel == null) {
            otherLabel = new JLabel(Constant.messages.getString("alert.label.parameter"));
        }
        return otherLabel;
    }

    private JLabel getConfidenceLabel() {
        if (confidenceLabel == null) {
            confidenceLabel = new JLabel(Constant.messages.getString("alert.label.confidence"));
        }
        return confidenceLabel;
    }

    private JLabel getRiskLabel() {
        if (riskLabel == null) {
            riskLabel = new JLabel(Constant.messages.getString("alert.label.risk"));
        }
        return riskLabel;
    }

    private JLabel getSourceLabel() {
        if (sourceLabel == null) {
            sourceLabel = new JLabel(Constant.messages.getString("alert.label.source"));
        }
        return sourceLabel;
    }

    private JLabel getUrlLabel() {
        if (urlLabel == null) {
            urlLabel = new JLabel(Constant.messages.getString("alert.label.url"));
        }
        return urlLabel;
    }

    private JLabel getWascidLabel() {
        if (wascidLabel == null) {
            wascidLabel = new JLabel(Constant.messages.getString("alert.label.wascid"));
        }
        return wascidLabel;
    }
}
