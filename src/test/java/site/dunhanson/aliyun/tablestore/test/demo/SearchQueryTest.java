package site.dunhanson.aliyun.tablestore.test.demo;

import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.query.*;
import org.junit.Test;
import site.dunhanson.aliyun.tablestore.entity.bidi.Document;
import site.dunhanson.aliyun.tablestore.utils.TableStoreMultipleIndexUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchQueryTest {

    @Test
    public void test1() {
        //条件1 col2 < 4
        RangeQuery rangeQuery1 = new RangeQuery();
        rangeQuery1.setFieldName("col2");
        rangeQuery1.lessThan(ColumnValue.fromLong(4));

        //条件二 col3<5
        RangeQuery rangeQuery2 = new RangeQuery();
        rangeQuery2.setFieldName("col3");
        rangeQuery2.lessThan(ColumnValue.fromLong(5));

        //条件 col2 =4
        TermQuery termQuery = new TermQuery();
        termQuery.setFieldName("col2");
        termQuery.setTerm(ColumnValue.fromLong(4));

        //条件四 col3 = 5 or col3 = 6
        TermsQuery termsQuery = new TermsQuery();
        termsQuery.setFieldName("col3");
        termsQuery.addTerm(ColumnValue.fromLong(5));
        termsQuery.addTerm(ColumnValue.fromLong(6));

        SearchQuery searchQuery = new SearchQuery();

        List<Query> queryList1 = new ArrayList<>();
        queryList1.add(rangeQuery1);
        queryList1.add(rangeQuery2);

        //组合1：col2 < 4 OR col3 < 5
        BoolQuery boolQuery1 = new BoolQuery();
        boolQuery1.setShouldQueries(queryList1);

        //组合2： col2 = 4  and (col3 = 5 or col3 = 6)
        List<Query> queryList2 = new ArrayList<>();
        queryList2.add(termQuery);
        queryList2.add(termsQuery);

        BoolQuery boolQuery2 = new BoolQuery();
        boolQuery2.setMustQueries(queryList2);

        //总组合  (col2 < 4 OR col3 < 5)  or (col2 = 4  and (col3 = 5 or col3 = 6))

        List<Query> queryList3 = new ArrayList<>();
        queryList3.add(boolQuery1);
        queryList3.add(boolQuery2);

        BoolQuery boolQuery = new BoolQuery();
        boolQuery.setShouldQueries(queryList3);

        System.out.println(TableStoreMultipleIndexUtils.getQueryString(boolQuery));
    }

    @Test
    public void test() {
        SearchQuery searchQuery = new SearchQuery();
        System.out.println(TableStoreMultipleIndexUtils.getQueryString(searchQuery.getQuery()));
    }

    @Test
    public void start() {
        System.out.println("匹配所有行:" + TableStoreMultipleIndexUtils.getQueryString(getMatchAllQuery()));
        System.out.println("匹配查询:" + TableStoreMultipleIndexUtils.getQueryString(getMatchQuery()));
        System.out.println("短语匹配查询:" + TableStoreMultipleIndexUtils.getQueryString(getMatchPhraseQuery()));
        System.out.println("精确查询:" + TableStoreMultipleIndexUtils.getQueryString(getTermQuery()));
        System.out.println("多词精确查询:" + TableStoreMultipleIndexUtils.getQueryString(getTermsQuery()));
        System.out.println("前缀查询:" + TableStoreMultipleIndexUtils.getQueryString(getPrefixQuery()));
        System.out.println("范围查询:" + TableStoreMultipleIndexUtils.getQueryString(getRangeQuery()));
        System.out.println("通配符查询:" + TableStoreMultipleIndexUtils.getQueryString(getWildcardQuery()));
        System.out.println("复合条件组合查询-MustQueries:" + TableStoreMultipleIndexUtils.getQueryString(getBoolQueryMustQueries()));
        System.out.println("复合条件组合查询-MustNotQueries:" + TableStoreMultipleIndexUtils.getQueryString(getBoolQueryMustNotQueries()));
        System.out.println("复合条件组合查询-ShouldQueries:" + TableStoreMultipleIndexUtils.getQueryString(getBoolQueryShouldQueries()));
        System.out.println("复合条件组合查询-FilterQueries:" + TableStoreMultipleIndexUtils.getQueryString(getBoolQueryFilterQueries()));
    }

    @Test
    public void query() {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(getWildcardQuery());
        TableStoreMultipleIndexUtils.search(searchQuery, Document.class).getList().forEach(obj->{
            System.out.println(obj.getDoctitle());
        });
    }

    public Query getBoolQueryFilterQueries() {
        BoolQuery boolQuery = new BoolQuery();
        boolQuery.setFilterQueries(Arrays.asList(getRangeQuery(), getMatchQuery()));
        return boolQuery;
    }

    public Query getBoolQueryShouldQueries() {
        BoolQuery boolQuery = new BoolQuery();
        boolQuery.setShouldQueries(Arrays.asList(getRangeQuery(), getMatchQuery()));
        return boolQuery;
    }

    public Query getBoolQueryMustNotQueries() {
        BoolQuery boolQuery = new BoolQuery();
        boolQuery.setMustNotQueries(Arrays.asList(getRangeQuery(), getMatchQuery()));
        return boolQuery;
    }

    public Query getBoolQueryMustQueries() {
        BoolQuery boolQuery = new BoolQuery();
        boolQuery.setMustQueries(Arrays.asList(getRangeQuery(), getMatchQuery()));
        return boolQuery;
    }

    public Query getWildcardQuery() {
        WildcardQuery query = new WildcardQuery();
        query.setFieldName("doctitle");
        query.setValue("先锋?");
        return query;
    }

    public Query getRangeQuery() {
        RangeQuery query = new RangeQuery();
        query.setFieldName("publishtime");
        query.setFrom(ColumnValue.fromString("2020-01-10 00:00:00"));
        query.setTo(ColumnValue.fromString("2020-01-11 00:00:00"));
        return query;
    }

    public Query getPrefixQuery() {
        PrefixQuery query = new PrefixQuery();
        query.setFieldName("doctitle");
        query.setPrefix("农产品");
        return query;
    }

    public Query getTermsQuery() {
        List<ColumnValue> list = new ArrayList<>();
        list.add(ColumnValue.fromString("南乐县"));
        list.add(ColumnValue.fromString("农产品"));
        TermsQuery query = new TermsQuery();
        query.setFieldName("doctitle");
        query.setTerms(list);
        return query;
    }

    public Query getTermQuery() {
        TermQuery query = new TermQuery();
        query.setFieldName("doctitle");
        query.setTerm(ColumnValue.fromString("南乐县农产品"));
        return query;
    }

    public Query getMatchPhraseQuery() {
        MatchPhraseQuery query = new MatchPhraseQuery();
        query.setFieldName("doctitle");
        query.setText("南乐县农产品");
        return query;
    }

    public Query getMatchQuery() {
        MatchQuery query = new MatchQuery();
        query.setFieldName("doctitle");
        query.setText("南乐县农产品");
        return query;
    }

    public Query getMatchAllQuery() {
        return new MatchAllQuery();
    }

}
