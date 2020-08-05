package site.dunhanson.aliyun.tablestore.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import site.dunhanson.aliyun.tablestore.entity.bidi.DocumentExtract;
import site.dunhanson.aliyun.tablestore.entity.bidi.DocumentTemp;
import site.dunhanson.aliyun.tablestore.entity.bidi.Enterprise;
import site.dunhanson.aliyun.tablestore.utils.TableStoreUtils;

import java.util.List;

@Slf4j
public class UpdateTest {


    /**
     * 测试获取一行记录
     */
    @Test
    public void testGet() {
        DocumentTemp temp = new DocumentTemp();
        temp.setPageTime("2018-10-10");
        temp.setDocid(9464174112L);
        DocumentTemp documentTemp = TableStoreUtils.get(temp, DocumentTemp.class);
        System.out.println(documentTemp);
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
        Enterprise enterprise = new Enterprise();
        enterprise.setBidiId(1314L);
        enterprise.setProvince("广东d2");


        // 更新招投标数量
        enterprise.setBidNumber(40);
        enterprise.setZhaoBiaoNumber(10);
        enterprise.setDaiLiNumber(10);
        enterprise.setZhongBiaoNumber(10);
        enterprise.setTouBiaoNumber(10);

        int num = TableStoreUtils.insert(enterprise);
        log.debug("影响行数={}", num);
    }

}
