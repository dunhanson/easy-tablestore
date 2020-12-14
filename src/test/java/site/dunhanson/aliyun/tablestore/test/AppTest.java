package site.dunhanson.aliyun.tablestore.test;

import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.query.*;
import org.junit.Test;
import site.dunhanson.aliyun.tablestore.entity.*;
import site.dunhanson.aliyun.tablestore.utils.TableStoreMultipleIndexUtils;
import site.dunhanson.aliyun.tablestore.utils.TableStoreUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppTest {




    @Test
    public void distinctCount() {


//        List<DocumentExtract> list = TableStoreUtils.getRangeByPrimaryKey(DocumentExtract.class, null, 1);
//        System.out.println(list);




        NestedQuery query = new NestedQuery();
        // 配置嵌套字段
        query.setPath("sub_docs_json");

        // 需要查询的查询
        RangeQuery rangeQuery = new RangeQuery();
        rangeQuery.setFieldName("sub_docs_json.win_bid_price");
        rangeQuery.setFrom(ColumnValue.fromDouble(10D), true);
        rangeQuery.setTo(ColumnValue.fromDouble(50D), true);
        query.setQuery(rangeQuery);
        query.setScoreMode(ScoreMode.None);


        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(new MatchAllQuery());
        Page<Document> page = TableStoreMultipleIndexUtils.search(searchQuery, Document.class);
        System.out.println(page);

    }

    @Test
    public void tee() {
        Document document = new Document();

        List<SubDocument> subDocuments = new ArrayList<>();
        SubDocument subDocument = new SubDocument();
        subDocument.setBiddingBudget(16.8D);
        subDocument.setWinBidPrice(16D);
        subDocuments.add(subDocument);
        document.setSubDocsJson(subDocuments);
        document.setPartitionkey(2L);
        document.setDocid(1L);
        document.setPageTime("2020-02-02");

//        int num1 = TableStoreUtils.insert(document);
//        int num2 = TableStoreUtils.batchInsert(Arrays.asList(document));
//        int num3 = TableStoreUtils.update(document);
        int num4 = TableStoreUtils.batchUpdate(Arrays.asList(document));

        System.out.println(123);
    }



}
