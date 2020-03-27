package org.zaproxy.zap.extension.brk;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FilterOptions {
    private JCheckBox javascriptCheckBox;
    private JCheckBox CSSFontsCheckBox;
    private JCheckBox imagesAndMediaCheckBox;
    private JCheckBox DELETECheckBox;
    private JCheckBox PUTCheckBox;
    private JCheckBox POSTCheckBox;
    private JCheckBox GETCheckBox;
    private JCheckBox OPTIONSCheckBox;
    private JCheckBox otherCheckBox;
    private JTextField javascriptTextField;
    private JTextField cssAndFontsTextField;
    private JTextField imagesMediaExtensionTextField;
    private JCheckBox onlyInScopeCheckBox;
    private JButton addButton;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JTable table1;


    public FilterOptions() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

            }
        });
    }

    public void setData(filterOptionsBean data) {
        javascriptCheckBox.setSelected(data.isCanBreakOnJs());
        CSSFontsCheckBox.setSelected(data.isCanBreakOnCssAndFonts());
        imagesAndMediaCheckBox.setSelected(data.isCanBreakOnMedia());
        javascriptTextField.setText(data.getJavascriptExtensions());
        cssAndFontsTextField.setText(data.getCssAndFontsExtensions());
        imagesMediaExtensionTextField.setText(data.getImagesAndMediaExtensions());
        onlyInScopeCheckBox.setSelected(data.isCanOnlyBreakOnScope());
        DELETECheckBox.setSelected(data.isCanBreakOnDeleteM());
        PUTCheckBox.setSelected(data.isCanBreakOnPutM());
        POSTCheckBox.setSelected(data.isCanBreakOnPostM());
        GETCheckBox.setSelected(data.isCanBreakOnGet());
        OPTIONSCheckBox.setSelected(data.isCanBreakOnOptions());
        otherCheckBox.setSelected(data.isCanBreakOnOtherHTTPMethods());
    }

    public void getData(filterOptionsBean data) {
        data.setCanBreakOnJs(javascriptCheckBox.isSelected());
        data.setCanBreakOnCssAndFonts(CSSFontsCheckBox.isSelected());
        data.setCanBreakOnMedia(imagesAndMediaCheckBox.isSelected());
        data.setJavascriptExtensions(javascriptTextField.getText());
        data.setCssAndFontsExtensions(cssAndFontsTextField.getText());
        data.setImagesAndMediaExtensions(imagesMediaExtensionTextField.getText());
        data.setCanOnlyBreakOnScope(onlyInScopeCheckBox.isSelected());
        data.setCanBreakOnDeleteM(DELETECheckBox.isSelected());
        data.setCanBreakOnPutM(PUTCheckBox.isSelected());
        data.setCanBreakOnPostM(POSTCheckBox.isSelected());
        data.setCanBreakOnGet(GETCheckBox.isSelected());
        data.setCanBreakOnOptions(OPTIONSCheckBox.isSelected());
        data.setCanBreakOnOtherHTTPMethods(otherCheckBox.isSelected());
    }

    public boolean isModified(filterOptionsBean data) {
        if (javascriptCheckBox.isSelected() != data.isCanBreakOnJs()) return true;
        if (CSSFontsCheckBox.isSelected() != data.isCanBreakOnCssAndFonts()) return true;
        if (imagesAndMediaCheckBox.isSelected() != data.isCanBreakOnMedia()) return true;
        if (javascriptTextField.getText() != null ? !javascriptTextField.getText().equals(data.getJavascriptExtensions()) : data.getJavascriptExtensions() != null)
            return true;
        if (cssAndFontsTextField.getText() != null ? !cssAndFontsTextField.getText().equals(data.getCssAndFontsExtensions()) : data.getCssAndFontsExtensions() != null)
            return true;
        if (imagesMediaExtensionTextField.getText() != null ? !imagesMediaExtensionTextField.getText().equals(data.getImagesAndMediaExtensions()) : data.getImagesAndMediaExtensions() != null)
            return true;
        if (onlyInScopeCheckBox.isSelected() != data.isCanOnlyBreakOnScope()) return true;
        if (DELETECheckBox.isSelected() != data.isCanBreakOnDeleteM()) return true;
        if (PUTCheckBox.isSelected() != data.isCanBreakOnPutM()) return true;
        if (POSTCheckBox.isSelected() != data.isCanBreakOnPostM()) return true;
        if (GETCheckBox.isSelected() != data.isCanBreakOnGet()) return true;
        if (OPTIONSCheckBox.isSelected() != data.isCanBreakOnOptions()) return true;
        if (otherCheckBox.isSelected() != data.isCanBreakOnOtherHTTPMethods()) return true;
        return false;
    }
}
