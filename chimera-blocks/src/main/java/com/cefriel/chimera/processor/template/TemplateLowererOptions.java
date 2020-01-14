package com.cefriel.chimera.processor.template;

public class TemplateLowererOptions {

    private String templatePath;
    private String destFileName;
    private String keyValuePairsPath;
    private String keyValueCsvPath;
    private String format;
    private String utils;
    private String queryFile;
    private boolean attachmentToExchange;

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getDestFileName() {
        return destFileName;
    }

    public void setDestFileName(String destFileName) {
        this.destFileName = destFileName;
    }

    public String getKeyValuePairsPath() {
        return keyValuePairsPath;
    }

    public void setKeyValuePairsPath(String keyValuePairsPath) {
        this.keyValuePairsPath = keyValuePairsPath;
    }

    public String getKeyValueCsvPath() {
        return keyValueCsvPath;
    }

    public void setKeyValueCsvPath(String keyValueCsvPath) {
        this.keyValueCsvPath = keyValueCsvPath;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUtils() {
        return utils;
    }

    public void setUtils(String utils) {
        this.utils = utils;
    }

    public String getQueryFile() {
        return queryFile;
    }

    public void setQueryFile(String queryFile) {
        this.queryFile = queryFile;
    }

    public boolean isAttachmentToExchange() {
        return attachmentToExchange;
    }

    public void setAttachmentToExchange(boolean attachmentToExchange) {
        this.attachmentToExchange = attachmentToExchange;
    }

}
