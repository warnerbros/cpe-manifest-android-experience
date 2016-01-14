package net.flixster.android.storage;


/**
 * Object abstraction of the external storage preference file
 */
public abstract class ExternalPreferenceFile {
    public static final String DELIMITER = "ZZZZ";
    public static final String NONE = ".";

    protected abstract String createFileName();

    protected abstract String createFileSubDir();

    protected abstract String createFileContent();

    protected abstract boolean parseFileContent(String content);

    /**
     * @return Whether read was successful
     */
    public boolean read() {
        String content = ExternalStorage.readFile(createFileSubDir(), createFileName());
        return parseFileContent(content);
    }

    public void save() {
        ExternalStorage.writeFile(createFileContent(), createFileSubDir(), createFileName());
    }

    public boolean delete() {
        return ExternalStorage.deleteFile(createFileSubDir(), createFileName());
    }

    public String[] split(String content) {
        String[] params = null;
        if (content != null) {
            params = content.split(DELIMITER);
        }
        return params;
    }
}
