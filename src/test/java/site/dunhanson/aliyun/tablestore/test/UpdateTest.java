package site.dunhanson.aliyun.tablestore.test;

import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import site.dunhanson.aliyun.tablestore.entity.Document;
import site.dunhanson.aliyun.tablestore.entity.DocumentExtract;
import site.dunhanson.aliyun.tablestore.entity.Page;
import site.dunhanson.aliyun.tablestore.utils.TableStoreUtils;

import java.util.Date;
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





}
