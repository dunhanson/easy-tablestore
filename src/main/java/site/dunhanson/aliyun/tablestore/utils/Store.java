package site.dunhanson.aliyun.tablestore.utils;

import com.alicloud.openservices.tablestore.SyncClient;
import lombok.extern.slf4j.Slf4j;
import site.dunhanson.aliyun.tablestore.constants.Constants;
import site.dunhanson.aliyun.tablestore.entity.ConnectInfo;

/**
 * 双检锁/双重校验锁的单例模式
 */
@Slf4j
public class Store {


    private static volatile Store instance;
    private static SyncClient syncClient;
    private static ConnectInfo connectInfo = new ConnectInfo();

    // 不给外部实例化
    private Store(){

    }

    /**
     * 获取对象实例
     * @return
     */
    public static Store getInstance() {
        if (instance == null) {
            synchronized (Store.class) {
                if (instance == null) {
                    instance = new Store();

                    // 初始化gdb配置
                    connectInfo.setActive(YamlUtils.getValueToString(Constants.FILE_PATH, Constants.TABLE_STORE, Constants.ACTIVE));
                    connectInfo.setEndPoint(YamlUtils.getValueToString(Constants.FILE_PATH, connectInfo.getActive(), Constants.END_POINT));
                    connectInfo.setAccessKeyId(YamlUtils.getValueToString(Constants.FILE_PATH, connectInfo.getActive(), Constants.ACCESS_KEY_ID));
                    connectInfo.setAccessKeySecret(YamlUtils.getValueToString(Constants.FILE_PATH, connectInfo.getActive(), Constants.ACCESS_KEY_SECRET));
                    connectInfo.setInstanceName(YamlUtils.getValueToString(Constants.FILE_PATH, connectInfo.getActive(), Constants.INSTANCE_NAME));

                    syncClient = new SyncClient(connectInfo.getEndPoint(), connectInfo.getAccessKeyId(), connectInfo.getAccessKeySecret(), connectInfo.getInstanceName());
                    log.warn("ConnectInfo完成初始化={}", connectInfo);
                }
            }
        }
        return instance;
    }

    /**
     * 获取 {@link SyncClient} 全应用只需要一个即可（以后考虑做成连接池那种，不过现在这种也会失败自动连接的机制）
     * @return
     */
    public SyncClient getSyncClient() {
        return syncClient;
    }

}
