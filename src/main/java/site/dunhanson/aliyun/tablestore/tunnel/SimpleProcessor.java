package site.dunhanson.aliyun.tablestore.tunnel;

import com.alicloud.openservices.tablestore.model.StreamRecord;
import com.alicloud.openservices.tablestore.tunnel.worker.IChannelProcessor;
import com.alicloud.openservices.tablestore.tunnel.worker.ProcessRecordsInput;

import java.util.List;

public class SimpleProcessor implements IChannelProcessor {

    private static int num = 0;
    @Override
    public void process(ProcessRecordsInput input) {
        System.out.println(String.format("Process %d records, NextToken: %s", input.getRecords().size(), input.getNextToken()));   //NextToken用于Tunnel Client的翻页。

        List<StreamRecord> records = input.getRecords();




    }

    @Override
    public void shutdown() {
        System.out.println("Mock shutdown");
    }

}
