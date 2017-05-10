package com.anniweiya.fastdfs.pool;

import com.anniweiya.fastdfs.FastDFSTemplateFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import java.io.Closeable;
import java.io.IOException;

/**
 * 链接创建
 */
class ConnectionFactory extends BasePooledObjectFactory<StorageClient> {
    private FastDFSTemplateFactory factory;

    public ConnectionFactory(FastDFSTemplateFactory templateFactory) {
        this.factory = templateFactory;
    }

    @Override
    public StorageClient create() throws Exception {
        TrackerClient trackerClient = new TrackerClient(factory.getG_tracker_group());
        TrackerServer trackerServer = trackerClient.getConnection();
        return new StorageClient(trackerServer, null);
    }

    @Override
    public PooledObject<StorageClient> wrap(StorageClient storageClient) {
        return new DefaultPooledObject<>(storageClient);
    }

    public PooledObject<StorageClient> makeObject() throws Exception {
        return wrap(create());
    }

    public void destroyObject(StorageClient obj) throws Exception {
        close(obj.getTrackerServer());
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }
}