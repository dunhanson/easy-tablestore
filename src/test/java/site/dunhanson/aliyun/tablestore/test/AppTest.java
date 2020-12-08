package site.dunhanson.aliyun.tablestore.test;

import org.junit.Test;
import site.dunhanson.aliyun.tablestore.entity.DupDocument;
import site.dunhanson.aliyun.tablestore.utils.TableStoreUtils;

import java.util.List;

public class AppTest {




    @Test
    public void distinctCount() {


        List<DupDocument> list = TableStoreUtils.getRangeByPrimaryKey(DupDocument.class, null, 100);
        System.out.println(list);

    }



}
