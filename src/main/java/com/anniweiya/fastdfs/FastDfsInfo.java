package com.anniweiya.fastdfs;

/**
 * FastDFS 文件描述
 */
public class FastDfsInfo implements java.io.Serializable {

    private String group;
    private String path;
    private String fileAbsolutePath;

    public FastDfsInfo(String group, String path) {
        this.group = group;
        this.path = path;
    }

    @Override
    public String toString() {
        return "FastDfsInfo{" + "group='" + group + '\'' +
               ", path='" + path + '\'' +
               '}';
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileAbsolutePath() {
        return fileAbsolutePath;
    }

    public void setFileAbsolutePath(String fileAbsolutePath) {
        this.fileAbsolutePath = fileAbsolutePath;
    }

}
