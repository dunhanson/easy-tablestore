package site.dunhanson.aliyun.tablestore.test;

import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.query.BoolQuery;
import com.alicloud.openservices.tablestore.model.search.query.MatchQuery;
import com.alicloud.openservices.tablestore.model.search.query.Query;
import org.apache.commons.lang3.RandomUtils;
import org.checkerframework.dataflow.qual.TerminatesExecution;
import org.junit.Test;
import site.dunhanson.aliyun.tablestore.entity.Document;
import site.dunhanson.aliyun.tablestore.entity.Page;
import site.dunhanson.aliyun.tablestore.entity.Str;
import site.dunhanson.aliyun.tablestore.utils.TableStoreUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Start {
    String[] keywordArr = Str.words.split("\n");

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

    public void testBasic() {

        //省份
        String[] provinceArr = new String[]{"广东", "湖南", "浙江", "上海", "北京", "广西", "安徽", "江苏", "新疆", "湖北"};
        MatchQuery query1 = new MatchQuery();
        query1.setFieldName("province");
        query1.setText(provinceArr[RandomUtils.nextInt(0, provinceArr.length)]);

        //类别
        String[] channelArr = new String[]{"102", "52", "51", "103", "105", "104", "101"};
        MatchQuery query2 = new MatchQuery();
        query2.setFieldName("docchannel");
        query2.setText(channelArr[RandomUtils.nextInt(0, channelArr.length)]);

        //关键词
        MatchQuery query3 = new MatchQuery();
        query3.setFieldName("dochtmlcon");
        query3.setText(keywordArr[RandomUtils.nextInt(0, keywordArr.length)]);

        List<Query> list = new ArrayList<>();
        list.add(query1);
        list.add(query2);
        list.add(query3);
        BoolQuery boolQuery = new BoolQuery();
        boolQuery.setMustQueries(list);
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(boolQuery);

        //查询
        TableStoreUtils.search(searchQuery, Document.class, true);
    }

    @Test
    public void testBasic2() {
        //省份
        String[] provinceArr = new String[]{"广东", "湖南", "浙江", "上海", "北京", "广西", "安徽", "江苏", "新疆", "湖北"};
        MatchQuery query1 = new MatchQuery();
        query1.setFieldName("province");
        query1.setText(provinceArr[RandomUtils.nextInt(0, provinceArr.length)]);
        System.out.println(query1.getText());

        //类别
        String[] channelArr = new String[]{"102", "52", "51", "103", "105", "104", "101"};
        MatchQuery query2 = new MatchQuery();
        query2.setFieldName("docchannel");
        query2.setText(channelArr[RandomUtils.nextInt(0, channelArr.length)]);
        System.out.println(query2.getText());

        //关键词
        MatchQuery query3 = new MatchQuery();
        query3.setFieldName("dochtmlcon");
        query3.setText(keywordArr[RandomUtils.nextInt(0, keywordArr.length)]);
        System.out.println(query3.getText());

        List<Query> list = new ArrayList<>();
        list.add(query1);
        list.add(query2);
        list.add(query3);
        BoolQuery boolQuery = new BoolQuery();
        boolQuery.setMustQueries(list);
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(boolQuery);

        //查询
        Page<Document> page = TableStoreUtils.search(searchQuery, Document.class, true);
        page.getList().forEach(obj->{
            System.out.println(obj);
        });
    }

    @Test
    public void test2() throws InterruptedException {
        //开始时间
        LocalDateTime startTime = LocalDateTime.now();

        Thread.sleep(100);
        //结束时间
        LocalDateTime endTime = LocalDateTime.now();

        System.out.println(ChronoUnit.MILLIS.between(startTime, endTime));
    }

    @Test
    public void test3() {
        System.out.println(RandomUtils.nextInt(0, 1000));
    }

    @Test
    public void test4() {
        Str.words.split("\n");
        System.out.println(Str.words.split("\n").length);
    }

    @Test
    public void test5() {
        //testBasic("档案");
    }

    @Test
    public void testThreadPool() throws InterruptedException {
        ArrayBlockingQueue<Runnable> arrayBlockingQueue = new ArrayBlockingQueue<Runnable>(50);
        int corePoolSize = 1;
        int maximumPoolSize = 1000;
        int keepAliveTime = 30;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, arrayBlockingQueue
        );
        //循环100次，每次50个线程
        for(int i = 1; i <= 100; i++) {
            for(int j = 1; j <= 50; j++) {
                int index = RandomUtils.nextInt(0, 1000);
                executor.execute(()->{
                    testBasic();
                });
            }
            //模拟每秒
            Thread.sleep(1000);
        }
        Thread.sleep(24*69*60);
    }

}
