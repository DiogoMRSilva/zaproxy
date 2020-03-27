package org.zaproxy.zap.extension.brk;

public class filterOptionsBean {
    private boolean canBreakOnJs;
    private boolean canBreakOnCssAndFonts;
    private boolean canBreakOnMedia;
    private String javascriptExtensions;
    private String cssAndFontsExtensions;
    private String imagesAndMediaExtensions;
    private boolean canOnlyBreakOnScope;
    private boolean canBreakOnDeleteM;
    private boolean canBreakOnPutM;
    private boolean canBreakOnPostM;
    private boolean canBreakOnGet;
    private boolean canBreakOnOptions;
    private boolean canBreakOnOtherHTTPMethods;

    public filterOptionsBean() {
    }

    public boolean isCanBreakOnJs() {
        return canBreakOnJs;
    }

    public void setCanBreakOnJs(final boolean canBreakOnJs) {
        this.canBreakOnJs = canBreakOnJs;
    }

    public boolean isCanBreakOnCssAndFonts() {
        return canBreakOnCssAndFonts;
    }

    public void setCanBreakOnCssAndFonts(final boolean canBreakOnCssAndFonts) {
        this.canBreakOnCssAndFonts = canBreakOnCssAndFonts;
    }

    public boolean isCanBreakOnMedia() {
        return canBreakOnMedia;
    }

    public void setCanBreakOnMedia(final boolean canBreakOnMedia) {
        this.canBreakOnMedia = canBreakOnMedia;
    }

    public String getJavascriptExtensions() {
        return javascriptExtensions;
    }

    public void setJavascriptExtensions(final String javascriptExtensions) {
        this.javascriptExtensions = javascriptExtensions;
    }

    public String getCssAndFontsExtensions() {
        return cssAndFontsExtensions;
    }

    public void setCssAndFontsExtensions(final String cssAndFontsExtensions) {
        this.cssAndFontsExtensions = cssAndFontsExtensions;
    }

    public String getImagesAndMediaExtensions() {
        return imagesAndMediaExtensions;
    }

    public void setImagesAndMediaExtensions(final String imagesAndMediaExtensions) {
        this.imagesAndMediaExtensions = imagesAndMediaExtensions;
    }

    public boolean isCanOnlyBreakOnScope() {
        return canOnlyBreakOnScope;
    }

    public void setCanOnlyBreakOnScope(final boolean canOnlyBreakOnScope) {
        this.canOnlyBreakOnScope = canOnlyBreakOnScope;
    }

    public boolean isCanBreakOnDeleteM() {
        return canBreakOnDeleteM;
    }

    public void setCanBreakOnDeleteM(final boolean canBreakOnDeleteM) {
        this.canBreakOnDeleteM = canBreakOnDeleteM;
    }

    public boolean isCanBreakOnPutM() {
        return canBreakOnPutM;
    }

    public void setCanBreakOnPutM(final boolean canBreakOnPutM) {
        this.canBreakOnPutM = canBreakOnPutM;
    }

    public boolean isCanBreakOnPostM() {
        return canBreakOnPostM;
    }

    public void setCanBreakOnPostM(final boolean canBreakOnPostM) {
        this.canBreakOnPostM = canBreakOnPostM;
    }

    public boolean isCanBreakOnGet() {
        return canBreakOnGet;
    }

    public void setCanBreakOnGet(final boolean canBreakOnGet) {
        this.canBreakOnGet = canBreakOnGet;
    }

    public boolean isCanBreakOnOptions() {
        return canBreakOnOptions;
    }

    public void setCanBreakOnOptions(final boolean canBreakOnOptions) {
        this.canBreakOnOptions = canBreakOnOptions;
    }

    public boolean isCanBreakOnOtherHTTPMethods() {
        return canBreakOnOtherHTTPMethods;
    }

    public void setCanBreakOnOtherHTTPMethods(final boolean canBreakOnOtherHTTPMethods) {
        this.canBreakOnOtherHTTPMethods = canBreakOnOtherHTTPMethods;
    }
}