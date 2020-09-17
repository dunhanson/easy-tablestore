package site.dunhanson.aliyun.tablestore.tunnel;

import com.alicloud.openservices.tablestore.model.StreamRecord;
import com.alicloud.openservices.tablestore.tunnel.worker.IChannelProcessor;
import com.alicloud.openservices.tablestore.tunnel.worker.ProcessRecordsInput;

import java.util.List;

public class SimpleProcessor implements IChannelProcessor {

    private static int num = 0;
    @Override
    public void process(ProcessRecordsInput input) {
        List<StreamRecord> records = input.getRecords();
        // 需要把 records 插入 document_extract 新表，感觉这种方式和我自己查询（根据docid分组多线程查询）再插入，应该差不多的速度
    }

    @Override
    public void shutdown() {
        System.out.println("Mock shutdown");
    }

}
