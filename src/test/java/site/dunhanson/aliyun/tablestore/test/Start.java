package site.dunhanson.aliyun.tablestore.test;

import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.query.MatchQuery;
import org.junit.Test;
import site.dunhanson.aliyun.tablestore.entity.Document;
import site.dunhanson.aliyun.tablestore.entity.Page;
import site.dunhanson.aliyun.tablestore.utils.TableStoreUtils;

public class Start {

    @Test
    public void test() {
        SearchQuery searchQuery = new SearchQuery();
        //字段
        MatchQuery query = new MatchQuery();
        query.setFieldName("docchannel");
        query.setText("52");
        searchQuery.setQuery(query);
        //查询
        Page<Document> page = TableStoreUtils.search(searchQuery, Document.class, true);
        System.out.println("offset:" + page.getOffset());
        System.out.println("limit:" + page.getLimit());
        System.out.println("totalPage:" + page.getTotalPage());
        System.out.println("totalCount:" + page.getTotalCount());
        System.out.println("list:" + page.getList().size());
        page.getList().forEach(obj->{
            System.out.println(obj);
        });
    }

}
