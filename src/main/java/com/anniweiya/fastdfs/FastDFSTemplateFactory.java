package com.anniweiya.fastdfs;

import com.anniweiya.fastdfs.exception.FastDFSException;
import com.anniweiya.fastdfs.pool.PoolConfig;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerGroup;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

/**
 * FastDFS 初始化
 *
 * @author KisChang
 * @version 1.0
 */
public class FastDFSTemplateFactory {

    //connect_timeout
    private int g_connect_timeout;
    //network_timeout
    private int g_network_timeout;
    //charset
    private String g_charset;
    //http.tracker_http_port
    private int g_tracker_http_port;
    //http.anti_steal_token
    private boolean g_anti_steal_token;
    //http.secret_key
    private String g_secret_key;

    private List<String> tracker_servers;
    private List<String> nginx_address;

    private TrackerGroup g_tracker_group;

    private PoolConfig poolConfig = new PoolConfig();


    private String protocol = "http://";
    private String sepapator = "/";


    public void init() throws Exception {

        if (g_connect_timeout <= 0) {
            g_connect_timeout = ClientGlobal.DEFAULT_CONNECT_TIMEOUT;
        }

        if (g_network_timeout <= 0) {
            g_network_timeout = ClientGlobal.DEFAULT_NETWORK_TIMEOUT;
        }
        g_connect_timeout *= 1000; //millisecond
        g_network_timeout *= 1000; //millisecond

        if (g_charset == null || g_charset.length() == 0) {
            g_charset = "UTF-8";
        }

        if (g_tracker_http_port <= 0) {
            g_tracker_http_port = 80;
        }

        if (tracker_servers == null || tracker_servers.isEmpty()) {
            throw new FastDFSException("item \"tracker_server\"  not found", -1);
        }

        InetSocketAddress[] tracker_servers_socket = new InetSocketAddress[tracker_servers.size()];
        for (int i = 0; i < tracker_servers.size(); i++) {
            String str = tracker_servers.get(i);
            String[] parts = str.split("\\:", 2);
            if (parts.length != 2) {
                throw new FastDFSException(
                        "the value of item \"tracker_server\" is invalid, the correct format is host:port", -2);
            }

            tracker_servers_socket[i] = new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
        }
        g_tracker_group = new TrackerGroup(tracker_servers_socket);

        if (g_anti_steal_token) {
            if (g_secret_key == null || "".equals(g_secret_key)) {
                throw new FastDFSException("item \"secret_key\"  not found", -2);
            }
        }
        setToGlobal();
    }

    private void setToGlobal() {
        ClientGlobal.setG_connect_timeout(this.g_connect_timeout);
        ClientGlobal.setG_network_timeout(this.g_network_timeout);
        ClientGlobal.setG_charset(this.g_charset);
        ClientGlobal.setG_tracker_http_port(this.g_tracker_http_port);
        ClientGlobal.setG_anti_steal_token(this.g_anti_steal_token);
        ClientGlobal.setG_secret_key(this.g_secret_key);
        ClientGlobal.setG_tracker_group(this.g_tracker_group);
    }

    public PoolConfig getPoolConfig() {
        if (poolConfig == null) {
            return new PoolConfig();
        }
        return poolConfig;
    }

    public void setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public int getG_connect_timeout() {
        return g_connect_timeout;
    }

    public void setG_connect_timeout(int g_connect_timeout) {
        this.g_connect_timeout = g_connect_timeout;
    }

    public int getG_network_timeout() {
        return g_network_timeout;
    }

    public void setG_network_timeout(int g_network_timeout) {
        this.g_network_timeout = g_network_timeout;
    }

    public String getG_charset() {
        return g_charset;
    }

    public void setG_charset(String g_charset) {
        this.g_charset = g_charset;
    }

    public int getG_tracker_http_port() {
        return g_tracker_http_port;
    }

    public void setG_tracker_http_port(int g_tracker_http_port) {
        this.g_tracker_http_port = g_tracker_http_port;
    }

    public boolean isG_anti_steal_token() {
        return g_anti_steal_token;
    }

    public void setG_anti_steal_token(boolean g_anti_steal_token) {
        this.g_anti_steal_token = g_anti_steal_token;
    }

    public String getG_secret_key() {
        return g_secret_key;
    }

    public void setG_secret_key(String g_secret_key) {
        this.g_secret_key = g_secret_key;
    }

    public List<String> getTracker_servers() {
        return tracker_servers;
    }

    public void setTracker_servers(String tracker_servers) {
        this.tracker_servers = Arrays.asList(tracker_servers.split(","));
    }

    public List<String> getNginx_address() {
        return nginx_address;
    }

    public void setNginx_address(String nginx_address) {
        this.nginx_address = Arrays.asList(nginx_address.split(","));
    }

    public TrackerGroup getG_tracker_group() {
        return g_tracker_group;
    }

    public void setG_tracker_group(TrackerGroup g_tracker_group) {
        this.g_tracker_group = g_tracker_group;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getSepapator() {
        return sepapator;
    }

    public void setSepapator(String sepapator) {
        this.sepapator = sepapator;
    }
}
