package com.anniweiya.fastdfs;

import com.anniweiya.fastdfs.exception.FastDFSException;
import com.anniweiya.fastdfs.pool.ConnectionPoolFactory;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ProtoCommon;
import org.csource.fastdfs.StorageClient;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * FastDFS 模板类
 */
public class FastDFSTemplate {

    private ConnectionPoolFactory connPoolFactory;
    private FastDFSTemplateFactory factory;

    public FastDFSTemplate(FastDFSTemplateFactory factory) {
        this.connPoolFactory = new ConnectionPoolFactory(factory);
        this.factory = factory;
    }

    /**
     * 上传文件
     *
     * @param data
     * @param ext 后缀，如：jpg、bmp（注意不带.）
     *
     * @return
     *
     * @throws FastDFSException
     */
    public FastDfsInfo upload(byte[] data, String ext) throws FastDFSException {
        return this.upload(data, ext, null);
    }

    /**
     * 上传文件
     *
     * @param data
     * @param ext 后缀，如：jpg、bmp（注意不带.）
     * @param values
     *
     * @return
     *
     * @throws FastDFSException
     */
    public FastDfsInfo upload(byte[] data, String ext, Map<String, String> values) throws FastDFSException {
        NameValuePair[] valuePairs = null;
        if (values != null && !values.isEmpty()) {
            valuePairs = new NameValuePair[values.size()];
            int index = 0;
            for (Map.Entry<String, String> entry : values.entrySet()) {
                valuePairs[index] = new NameValuePair(entry.getKey(), entry.getValue());
                index++;
            }
        }
        StorageClient client = getClient();

        try {
            String[] uploadResults = client.upload_file(data, ext, valuePairs);
            String groupName = uploadResults[0];
            String remoteFileName = uploadResults[1];
            FastDfsInfo fastDfsInfo = new FastDfsInfo(groupName, remoteFileName);
            if (factory != null) {
                this.setFileAbsolutePath(fastDfsInfo);
            }
            return fastDfsInfo;
        } catch (Exception e) {
            throw new FastDFSException(e.getMessage(), e, 0);
        } finally {
            releaseClient(client);
        }
    }

    /**
     * 下载文件
     *
     * @param dfs
     *
     * @return
     *
     * @throws FastDFSException
     */
    public byte[] loadFile(FastDfsInfo dfs) throws FastDFSException {
        return this.loadFile(dfs.getGroup(), dfs.getPath());
    }

    /**
     * 下载文件
     *
     * @param groupName
     * @param remoteFileName
     *
     * @return
     *
     * @throws FastDFSException
     */
    public byte[] loadFile(String groupName, String remoteFileName) throws FastDFSException {
        StorageClient client = getClient();
        try {
            return client.download_file(groupName, remoteFileName);
        } catch (Exception e) {
            throw new FastDFSException(e.getMessage(), e, 0);
        } finally {
            releaseClient(client);
        }
    }

    /**
     * 删除文件
     *
     * @param dfs
     *
     * @throws FastDFSException
     */
    public void deleteFile(FastDfsInfo dfs) throws FastDFSException {
        this.deleteFile(dfs.getGroup(), dfs.getPath());
    }

    /**
     * 删除文件
     *
     * @param groupName
     * @param remoteFileName
     *
     * @throws FastDFSException
     */
    public void deleteFile(String groupName, String remoteFileName) throws FastDFSException {
        int code;
        StorageClient client = getClient();
        try {
            code = client.delete_file(groupName, remoteFileName);
        } catch (Exception e) {
            throw new FastDFSException(e.getMessage(), e, 0);
        } finally {
            releaseClient(client);
        }
        if (code != 0) {
            throw new FastDFSException(code);
        }
    }

    /**
     * 设置远程可访问路径
     *
     * @param path
     * @param group
     *
     * @return
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws MyException
     */
    public String setFileAbsolutePath(String group, String path)
            throws IOException, NoSuchAlgorithmException, MyException {
        int ts = (int) (System.currentTimeMillis() / 1000), port;
        String token = "";
        if (factory.isG_anti_steal_token()) {
            token = ProtoCommon.getToken(path, ts, factory.getG_secret_key());
            token = "?token=" + token + "&ts=" + ts;
        }
        List<String> addressList;
        if (factory.getNginx_address() != null) {
            addressList = factory.getNginx_address();
        } else {
            addressList = factory.getTracker_servers();
        }

        Random random = new Random();
        int i = random.nextInt(addressList.size());
        String[] split = addressList.get(i).split(":", 2);

        if (split.length > 1) {
            port = Integer.parseInt(split[1].trim());
        } else {
            port = factory.getG_tracker_http_port();
        }
        String address = split[0].trim();
        return factory.getProtocol() +
               address + ":" +
               port +
               factory.getSepapator() +
               group +
               factory.getSepapator() +
               path + token;

    }

    public void setFileAbsolutePath(FastDfsInfo fastDfsInfo) throws IOException, NoSuchAlgorithmException, MyException {
        fastDfsInfo.setFileAbsolutePath(this.setFileAbsolutePath(fastDfsInfo.getGroup(), fastDfsInfo.getPath()));
    }

    protected StorageClient getClient() {
        StorageClient client = null;
        while (client == null) {
            try {
                client = connPoolFactory.getClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    protected void releaseClient(StorageClient client) {
        connPoolFactory.releaseConnection(client);
    }

}
