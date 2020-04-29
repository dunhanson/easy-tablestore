package site.dunhanson.aliyun.tablestore.test;

import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import site.dunhanson.aliyun.tablestore.entity.Document;
import site.dunhanson.aliyun.tablestore.entity.DocumentExtract;
import site.dunhanson.aliyun.tablestore.entity.Page;
import site.dunhanson.aliyun.tablestore.utils.TableStoreUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class UpdateTest {


    /**
     * 测试新增
     */
    @Test
    public void testAdd() {
        Document doc = new Document();
        // 设置主键
        doc.setPageTime("2020-01-01");
        doc.setDocid(1L);

        // 设置其他属性
        doc.setArea("华南");
        doc.setPublishtime(new Date());


        int num = TableStoreUtils.insert(doc);
        log.debug("影响行数={}", num);
    }

    /**
     * 测试更新
     */
    @Test
    public void testUpdate() {
        Document doc = new Document();
        // 设置主键
        doc.setPageTime("2020-01-01");
        doc.setDocid(13L);

        // 设置其他属性（相当于改）
        doc.setArea("华南");

        int num = TableStoreUtils.update(doc);
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
        doc.setArea("华南");
        int num = TableStoreUtils.insert(doc);

        DocumentExtract doc2 = new DocumentExtract();
        // 设置主键
        doc2.setPageTime("2020-01-02");
        doc2.setDocid(2L);
        doc2.setStatus(1L);

        // 设置其他属性
        doc2.setArea("华南");
        num += TableStoreUtils.insert(doc2);

        DocumentExtract doc3 = new DocumentExtract();
        // 设置主键
        doc3.setPageTime("2020-01-03");
        doc3.setDocid(3L);
        doc3.setStatus(1L);

        // 设置其他属性
        doc3.setArea("华南");
        num += TableStoreUtils.insert(doc3);

        DocumentExtract doc4 = new DocumentExtract();
        // 设置主键
        doc4.setPageTime("2020-01-04");
        doc4.setDocid(4L);
        doc4.setStatus(1L);

        // 设置其他属性
        doc4.setArea("华南");
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



}
