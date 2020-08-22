package site.dunhanson.aliyun.tablestore.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import site.dunhanson.aliyun.tablestore.entity.bidi.DocumentExtract;
import site.dunhanson.aliyun.tablestore.entity.bidi.DocumentTemp;
import site.dunhanson.aliyun.tablestore.entity.bidi.enterprise.*;
import site.dunhanson.aliyun.tablestore.utils.TableStoreUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class UpdateTest {


    /**
     * 创建 Enterprise 对象
     * @return
     * @throws Exception
     */
    private Enterprise buildTemp() throws Exception {
        Enterprise temp = new Enterprise();
        Field[] fields = temp.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
           if (field.getType().getSimpleName().equals("String")) {
                field.set(temp, field.getName());
            } else if (field.getType().getSimpleName().equals("Integer")) {
                field.set(temp, Integer.valueOf(i));
            } else if (field.getType().getSimpleName().equals("Long")) {
                field.set(temp, Long.valueOf(i));
            }
        }
        return temp;
    }


    /**
     * 测试获取一行记录
     */
    @Test
    public void testGet() throws Exception {
        Enterprise enterprise = buildTemp();
        enterprise.setName("testEnterprise");
        enterprise.setNicknames("testEnterprise");
        enterprise.setArea("华南");
        enterprise.setProvince("广东");
        enterprise.setCity("深圳");
        enterprise.setBidiId(1314520L);

        TableStoreUtils.insert(enterprise);
    }

    /**
     * 根据二级索引查找
     */
    @Test
    public void testSearchBysecondaryIndexForGetRange() {
        DocumentExtract extract = new DocumentExtract();
        extract.setStatus(1L);
        log.warn("开始查询，status={}", extract.getStatus());
        List<DocumentExtract> list = TableStoreUtils.searchBysecondaryIndex(extract, DocumentExtract.class, null, 20);
        log.warn("查询结果={}", list.size());
    }


    /**
     * 测试新增
     */
    @Test
    public void testInsert() {
        DocumentExtract extract = new DocumentExtract();
        extract.setStatus(-2L);
        int num = TableStoreUtils.countBysecondaryIndex(extract, DocumentExtract.class);
        log.warn("影响行数={}", num);
    }

}
