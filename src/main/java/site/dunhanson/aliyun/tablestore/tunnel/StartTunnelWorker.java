package site.dunhanson.aliyun.tablestore.tunnel;

import com.alicloud.openservices.tablestore.TunnelClient;
import com.alicloud.openservices.tablestore.tunnel.worker.TunnelWorker;
import com.alicloud.openservices.tablestore.tunnel.worker.TunnelWorkerConfig;
import site.dunhanson.aliyun.tablestore.entity.ConnectInfo;
import site.dunhanson.aliyun.tablestore.utils.Store;

public class StartTunnelWorker {


    public static void main(String[] args) throws Exception {
        //1.初始化Tunnel Client。
        ConnectInfo connectInfo = Store.getInstance().getConnectInfo();
        TunnelClient tunnelClient = new TunnelClient(connectInfo.getEndPoint(), connectInfo.getAccessKeyId(), connectInfo.getAccessKeySecret(), connectInfo.getInstanceName());
        //3.用户自定义数据消费Callback，开始自动化的数据消费。
        //TunnelWorkerConfig中有更多的高级参数。
        TunnelWorkerConfig config = new TunnelWorkerConfig(new SimpleProcessor());
        TunnelWorker worker = new TunnelWorker("23df1990-ff49-4337-9107-168a317f05d6", tunnelClient, config);
        try {
            worker.connectAndWorking();
        } catch (Exception e) {
            e.printStackTrace();
            worker.shutdown();
            tunnelClient.shutdown();
        }
    }

}
