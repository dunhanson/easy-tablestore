package site.dunhanson.aliyun.tablestore.test;

import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.query.*;
import lombok.extern.log4j.Log4j;
import org.junit.Test;
import site.dunhanson.aliyun.tablestore.entity.Document;
import site.dunhanson.aliyun.tablestore.entity.Page;
import site.dunhanson.aliyun.tablestore.utils.TableStoreMultipleIndexUtils;

import java.util.Arrays;

/**
 * 常用 {@link Query} 用法（即：二级索引的使用demo）
 */
@Log4j
public class TestMultipleIndex {


    /**
     * 精准查询（用于 字符串类型）
     */
    @Test
    public void testTermQuery() {
        TermQuery query = new TermQuery();
        query.setFieldName("docchannel");
        query.setTerm(ColumnValue.fromLong(51));

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(query);
        Page<Document> page = TableStoreMultipleIndexUtils.search(searchQuery, Document.class);
        print(page);
    }
    /**
     * 精准查询（用于 字符串类型）
     */
    @Test
    public void testTermsQuery() {
        TermsQuery query = new TermsQuery();
        query.setFieldName("docchannel");
        query.addTerm(ColumnValue.fromLong(51));
        query.addTerm(ColumnValue.fromLong(52));

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(query);
        Page<Document> page = TableStoreMultipleIndexUtils.search(searchQuery, Document.class);
        print(page);
    }


    /**
     * 匹配查询（用于分词字符串-最大数量语义分词）
     * 例子：我来自广东 会自动根据最长语义分词为 我/来自/广东 三个词语
     */
    @Test
    public void testMatchQuery() {
        MatchQuery query = new MatchQuery();
        query.setFieldName("dochtmlcon");
        query.setText("我来自广东");


        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(query);
        Page<Document> page = TableStoreMultipleIndexUtils.search(searchQuery, Document.class);
        print(page);
    }

    /**
     * 短语匹配查询（用于分词字符串-最大数量语义分词）
     * 例子：我来自广东 就等于 我来自广东
     */
    @Test
    public void testMatchPhraseQuery () {
        MatchPhraseQuery query = new MatchPhraseQuery();
        query.setFieldName("dochtmlcon");
        query.setText("我来自广东");

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(query);
        Page<Document> page = TableStoreMultipleIndexUtils.search(searchQuery, Document.class);
        print(page);
    }

    /**
     * 前缀查询（用于分词字符串-最大数量语义分词 或者 字符串）
     */
    @Test
    public void testPrefixQuery() {
        PrefixQuery query = new PrefixQuery();
        query.setFieldName("dochtmlcon");
        query.setPrefix("广东");

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(query);
        Page<Document> page = TableStoreMultipleIndexUtils.search(searchQuery, Document.class);
        print(page);
    }

    /**
     * 范围条件查询
     * 例子： 2020-01-01 00:00:00 <= publishtime <= 2020-01-03 00:00:00
     */
    @Test
    public void testRangeQuery() {
        RangeQuery query = new RangeQuery();
        query.setFieldName("publishtime");


        query.setFrom(ColumnValue.fromString("2020-01-01 00:00:00"), true);
        query.setTo(ColumnValue.fromString("2020-01-03 00:00:00"), true);

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(query);
        Page<Document> page = TableStoreMultipleIndexUtils.search(searchQuery, Document.class);
        print(page);
    }

    /**
     * 组合查询
     * 例子：docid < 4  or (docchannel = 52 and (province = 广东 or province = 广西))
     */
    @Test
    public void testBoolQuery() {
        // 条件1 col2 < 4
        RangeQuery rangeQuery1 = new RangeQuery();
        rangeQuery1.setFieldName("docid");
        rangeQuery1.lessThan(ColumnValue.fromLong(4));


        // 条件2 (docchannel = 52 and (province = 广东 or province = 广西))
        TermQuery termQuery1 = new TermQuery();
        termQuery1.setFieldName("docchannel");
        termQuery1.setTerm(ColumnValue.fromLong(52));

        TermsQuery termsQuery = new TermsQuery();
        termsQuery.setFieldName("province");
        termsQuery.addTerm(ColumnValue.fromString("广东"));
        termsQuery.addTerm(ColumnValue.fromString("广西"));
        BoolQuery boolQuery1 = new BoolQuery();
        boolQuery1.setMustQueries(Arrays.asList(termQuery1, termsQuery));


        // 组合：条件1/条件2
        BoolQuery query = new BoolQuery();
        query.setShouldQueries(Arrays.asList(rangeQuery1, boolQuery1));

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(query);
        Page<Document> page = TableStoreMultipleIndexUtils.search(searchQuery, Document.class);
        print(page);
    }


    private void print(Page page) {
        log.warn("offset:" + page.getOffset());
        log.warn("limit:" + page.getLimit());
        log.warn("totalPage:" + page.getTotalPage());
        log.warn("totalCount:" + page.getTotalCount());
        log.warn("list:" + page.getList().size());
        page.getList().forEach(obj->{
            log.warn(obj);
        });
    }
    
}
