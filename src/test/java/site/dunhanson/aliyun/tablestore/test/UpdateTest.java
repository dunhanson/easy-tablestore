package site.dunhanson.aliyun.tablestore.test;

import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import site.dunhanson.aliyun.tablestore.entity.*;
import site.dunhanson.aliyun.tablestore.utils.TableStoreUtils;

import java.util.Date;
import java.util.LinkedList;
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
    public void testGetRange() {
        DocumentExtract extract = new DocumentExtract();

        extract.setStatus(1L);
        log.warn("开始查询，status={}", extract.getStatus());

        List<String> columnsToGet = new LinkedList<>();
        columnsToGet.add("dochtmlcon");

        List<DocumentExtract> list = TableStoreUtils.searchBysecondaryIndex(extract, DocumentExtract.class, columnsToGet, 2);
        log.warn("查询结果={}", list.size());
    }


    /**
     * 测试新增
     */
    @Test
    public void testAdd() {
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

    /**
     * 测试更新
     */
    @Test
    public void testUpdate() {
        Enterprise enterprise = new Enterprise();
        enterprise.setBidiId(175984918635089920L);
        enterprise.setProvince("广东d2");


        // 更新招投标数量
        enterprise.setBidNumber(40);
        enterprise.setZhaoBiaoNumber(10);
        enterprise.setDaiLiNumber(10);
        enterprise.setZhongBiaoNumber(10);
        enterprise.setTouBiaoNumber(10);

        int num = TableStoreUtils.update(enterprise);
        log.debug("影响行数={}", num);
    }

    /**
     * 测试批量
     */
    @Test
    public void testBatchUpdate() {
        Page<Document> page = TableStoreUtils.search(new SearchQuery(), Document.class, false);

        List<Document> list = page.getList();
        for (Document doc : list) {
            doc.setArea("华南");
        }

        int num = TableStoreUtils.batchUpdate(list);
        log.debug("影响行数={}", num);
    }


    /**
     * 测试添加 DocumentExtract
     */
    @Test
    public void testAddDocumentExtract() {
        DocumentExtract doc = new DocumentExtract();
        // 设置主键
        doc.setPageTime("2020-01-01");
        doc.setDocid(1L);
        doc.setStatus(1L);

        // 设置其他属性
        doc.setDochtmlcon("dochtmlcon");
        int num = TableStoreUtils.insert(doc);

        DocumentExtract doc2 = new DocumentExtract();
        // 设置主键
        doc2.setPageTime("2020-01-02");
        doc2.setDocid(2L);
        doc2.setStatus(1L);

        // 设置其他属性
        doc.setDochtmlcon("dochtmlcon");
        num += TableStoreUtils.insert(doc2);

        DocumentExtract doc3 = new DocumentExtract();
        // 设置主键
        doc3.setPageTime("2020-01-03");
        doc3.setDocid(3L);
        doc3.setStatus(1L);

        // 设置其他属性
        doc.setDochtmlcon("dochtmlcon");
        num += TableStoreUtils.insert(doc3);

        DocumentExtract doc4 = new DocumentExtract();
        // 设置主键
        doc4.setPageTime("2020-01-04");
        doc4.setDocid(4L);
        doc4.setStatus(1L);

        // 设置其他属性
        doc.setDochtmlcon("dochtmlcon");
        num += TableStoreUtils.insert(doc4);


        log.debug("影响行数={}", num);
    }


    /**
     * 测试 DocumentExtract （单行和批量删除）
     */
    @Test
    public void testDeleteDocumentExtract() {
        DocumentExtract doc = new DocumentExtract();
        // 设置主键
        doc.setPageTime("2020-01-01");
        doc.setDocid(1L);


        int num = TableStoreUtils.delete(doc);
        log.warn("影响行数={}", num);


        DocumentExtract doc2 = new DocumentExtract();
        // 设置主键
        doc2.setPageTime("2020-01-02");
        doc2.setDocid(2L);

        DocumentExtract doc3 = new DocumentExtract();
        // 设置主键
        doc3.setPageTime("2020-01-03");
        doc3.setDocid(3L);

        DocumentExtract doc4 = new DocumentExtract();
        // 设置主键
        doc4.setPageTime("2020-01-04");
        doc4.setDocid(4L);

        List<DocumentExtract> list = new LinkedList<>();
        list.add(doc2);
        list.add(doc3);
        list.add(doc4);

        num = TableStoreUtils.batchDelete(list);
        log.warn("影响行数={}", num);

    }

    @Test
    public void testBatchGetRow() {
        Document d1 = new Document();
        d1.setPageTime("1403-02-08");
        d1.setDocid(46445155L);
        Document d2 = new Document();
        d2.setPageTime("1403-02-08");
        d2.setDocid(46446290L);


        List<Document> list = new LinkedList<>();
        list.add(d1);
        list.add(d2);
        List<Document> documents = TableStoreUtils.batchGetRow(list, Document.class, null);
        System.out.println(documents);
    }


}
