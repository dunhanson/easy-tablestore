package site.dunhanson.aliyun.tablestore.test;

import com.alibaba.fastjson.JSON;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.query.*;
import com.alicloud.openservices.tablestore.model.search.sort.FieldSort;
import com.alicloud.openservices.tablestore.model.search.sort.Sort;
import com.alicloud.openservices.tablestore.model.search.sort.SortOrder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import site.dunhanson.aliyun.tablestore.entity.Page;
import site.dunhanson.aliyun.tablestore.entity.bidi.Document;
import site.dunhanson.aliyun.tablestore.entity.bidi.DocumentExtract;
import site.dunhanson.aliyun.tablestore.entity.bidi.DocumentTemp;
import site.dunhanson.aliyun.tablestore.entity.bidi.enterprise.*;
import site.dunhanson.aliyun.tablestore.utils.TableStoreMultipleIndexUtils;
import site.dunhanson.aliyun.tablestore.utils.TableStoreUtils;

import java.lang.reflect.Field;
import java.util.*;

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
        Enterprise enterprise = new Enterprise();
        enterprise.setName("深圳市国际招标有限公司");
        enterprise = TableStoreUtils.get(enterprise, Enterprise.class);
        System.out.println(enterprise);
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
    public void testInsert() throws Exception {
        Enterprise enterprise = buildTemp();
        enterprise.setName("testEnterprise");
        enterprise.setNicknames("testEnterprise");
        enterprise.setArea("华南12");
        enterprise.setProvince("广东");
        enterprise.setCity("深圳");
        enterprise.setBidiId(1314520L);

        List<EnterpriseProfileCreditChinaRedRecordItem> creditRedRecord = new ArrayList<>();
        EnterpriseProfileCreditChinaRedRecordItem item = new EnterpriseProfileCreditChinaRedRecordItem();
        item.setDocName("docName");
        creditRedRecord.add(item);
        enterprise.setCreditRedRecord(creditRedRecord);

        TableStoreUtils.insert(enterprise);
    }



    @Test
    public void te2() {


        StringBuffer stringBuffer = new StringBuffer();
        while (true){
            stringBuffer.append("中");
            if (stringBuffer.length() >= 1699050) { // 699050
                break;
            }
        }
        String str = stringBuffer.toString();
        System.out.println(str.length());

        Enterprise enterprise = new Enterprise();
        enterprise.setName("国家电网有限公司12");
        enterprise.setProvince("广东");

        List<EnterpriseProfilePatentItem> patents = new ArrayList<>();
        EnterpriseProfilePatentItem item = new EnterpriseProfilePatentItem();
        item.setAddress(str);
        patents.add(item);
        enterprise.setPatents(patents);
//
        TableStoreUtils.insert(enterprise);
//        TableStoreUtils.update(enterprise);
//        System.out.println(12);

//
//        List<Enterprise> list = new ArrayList<>();
////        list.add(enterprise);
////        int i = TableStoreUtils.batchUpdate(list);
//        System.out.println(12);

//        Enterprise enterprise = new Enterprise();
//        enterprise.setName("国家电网有限公司12");
//
//        Enterprise enterprise1 = TableStoreUtils.get(enterprise, Enterprise.class);
//        System.out.println(enterprise1.getName());

    }

    /*查bidi_io大于某个100的100条记录（最多一次只能获取 100 条）*/
    @Test
    public void search() {
        // 条件1 bidi_id > 100
        RangeQuery rangeQuery = new RangeQuery();
        rangeQuery.setFieldName("bidi_id");
        rangeQuery.greaterThan(ColumnValue.fromLong(100));

        // 条件2 name is not null
        ExistsQuery existQuery = new ExistsQuery();
        existQuery.setFieldName("name");

        // 合并 bidi_id > 100 and tyc_id is not null
        BoolQuery boolQuery = new BoolQuery();
        boolQuery.setMustQueries(new ArrayList<>(Arrays.asList(rangeQuery, existQuery)));

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(boolQuery);
        searchQuery.setLimit(100);              // 最多一次只能获取 100 条
        Page<Enterprise> page = TableStoreMultipleIndexUtils.search(searchQuery, Enterprise.class, Arrays.asList("bidi_id","tyc_update_time","tyc_id"));

//        log.warn("offset:" + page.getOffset());
//        log.warn("limit:" + page.getLimit());
//        log.warn("totalPage:" + page.getTotalPage());
//        log.warn("totalCount:" + page.getTotalCount());
//        log.warn("list:" + page.getList().size());
//        List<Enterprise> list = page.getList();     //  这个就是结果集
//
//        for (Enterprise enterprise : list) {
//            if (enterprise.getTycId() == null) {
//                System.out.println(12);
//            }
//        }

    }

    @Test
    public void searchIsNull() {
        // 条件1 bidi_id > 100
        RangeQuery rangeQuery = new RangeQuery();
        rangeQuery.setFieldName("bidi_id");
        rangeQuery.greaterThan(ColumnValue.fromLong(100));

        // 条件2 tyc_id tyc_id is not null
        ExistsQuery existQuery = new ExistsQuery();
        existQuery.setFieldName("tyc_id");

        // 合并 bidi_id > 100 and tyc_id is null
        BoolQuery boolQuery = new BoolQuery();
        boolQuery.setMustQueries(new ArrayList<>(Arrays.asList(rangeQuery)));
        boolQuery.setMustNotQueries(new ArrayList<>(Arrays.asList(existQuery)));

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(boolQuery);
        searchQuery.setLimit(100);              // 最多一次只能获取 100 条
        Page<Enterprise> page = TableStoreMultipleIndexUtils.search(searchQuery, Enterprise.class, Arrays.asList("bidi_id","tyc_update_time","tyc_id"));

//        log.warn("offset:" + page.getOffset());
//        log.warn("limit:" + page.getLimit());
//        log.warn("totalPage:" + page.getTotalPage());
//        log.warn("totalCount:" + page.getTotalCount());
//        log.warn("list:" + page.getList().size());
//        List<Enterprise> list = page.getList();     //  这个就是结果集
//
//        for (Enterprise enterprise : list) {
//            if (enterprise.getTycId() == null) {
//                System.out.println(12);
//            }
//        }

    }
    /*查*/
    @Test
    public void get() {
        TermsQuery query = new TermsQuery();
        query.setFieldName("bidi_id");
        query.addTerm(ColumnValue.fromLong(211663999468777472L));
        query.addTerm(ColumnValue.fromLong(218751833451212800L));
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(query);
        searchQuery.setLimit(10);
        searchQuery.setSort(new Sort(Arrays.asList(new FieldSort("bid_number", com.alicloud.openservices.tablestore.model.search.sort.SortOrder.DESC))));
        Page<Enterprise> search = TableStoreMultipleIndexUtils.search(searchQuery, Enterprise.class);

        System.out.println(search.getList());
    }
    /*新增*/
    @Test
    public void insert() {
        Enterprise enterprise = new Enterprise();
        enterprise.setName("test");
        // 设置普通字段
        enterprise.setProvince("广东");

        // 设置大字段
        List<EnterpriseProfilePatentItem> patents = new ArrayList<>();
        EnterpriseProfilePatentItem item = new EnterpriseProfilePatentItem();
        item.setAddress("广东天河");
        patents.add(item);
        enterprise.setPatents(patents);
        TableStoreUtils.insert(enterprise);
    }
    /*更新*/
    @Test
    public void update() {
        Enterprise enterprise = new Enterprise();
        enterprise.setName("test");
        // 设置普通字段
        enterprise.setProvince("广东");

        // 更新大字段
        List<EnterpriseProfilePatentItem> patents = new ArrayList<>();
        EnterpriseProfilePatentItem item = new EnterpriseProfilePatentItem();
        item.setAddress("广东天河");
        patents.add(item);
        enterprise.setPatents(patents);
        TableStoreUtils.update(enterprise);
    }
    /*删除列（使用于，需要清空列的场景）*/
    @Test
    public void deleteColumns() {
        Enterprise enterprise = new Enterprise();
        enterprise.setName("test");
        TableStoreUtils.deleteColumns(enterprise, Arrays.asList("patents", "patents","city"));
    }

    @Test
    public void te() {
        Long maxDocid = 0L;
        for (int i = 1; i < 1500; i++) {
            SearchQuery query = new SearchQuery();
            BoolQuery boolQuery = new BoolQuery();

            // 1、docid > maxDocid
            RangeQuery rangeQuery1 = new RangeQuery();
            rangeQuery1.setFieldName("docid");
            rangeQuery1.greaterThan(ColumnValue.fromLong(maxDocid));


            // 2、docchannel=100
            TermQuery termQuery = new TermQuery();
            termQuery.setFieldName("docchannel");
            termQuery.setTerm(ColumnValue.fromLong(100));
            boolQuery.setMustQueries(new ArrayList<>(Arrays.asList(rangeQuery1, termQuery)));

            query.setQuery(boolQuery);
            query.setLimit(0);
            query.setLimit(100);
            query.setSort(new Sort(Arrays.asList(new FieldSort("docid", SortOrder.ASC))));

            Page<Document> page = TableStoreMultipleIndexUtils.search(query, Document.class, new ArrayList<>(Arrays.asList("docchannel")));
            List<Document> docs = page.getList();
            if (docs != null && docs.size() > 0) {
                List<DocumentTemp> temps = new LinkedList<>();
                for (Document doc : docs) {
                    DocumentTemp temp = new DocumentTemp();
                    temp.setPageTime(doc.getPageTime());
                    temp.setDocid(doc.getDocid());
                    temps.add(temp);
                }
                maxDocid = docs.get(docs.size() - 1).getDocid();
                List<DocumentTemp> documentTemps = TableStoreUtils.batchGetRow(temps, DocumentTemp.class);
                Map<Long, DocumentTemp> map = new HashMap<>();
                for (DocumentTemp temp : documentTemps) {
                    map.put(temp.getDocid(), temp);
                }


                // 更新
                List<Document> updates = new LinkedList<>();
                for (Document doc : docs) {
                    DocumentTemp temp = map.get(doc.getDocid());
                    if (temp != null) {
                        Long docchannel = temp.getDocchannel();
                        if (docchannel != null && docchannel != 100L) {
                            doc.setDocchannel(docchannel);
                            updates.add(doc);
                        }
                    }
                }
                int updateSum = TableStoreUtils.batchUpdate(updates);
                System.out.println(updateSum);
            }
        }
    }


    /*新增*/
    @Test
    public void insertTest() {
        Document document = new Document();
        document.setArea("华南");
        document.setProvince("广东");
        TableStoreUtils.insert(document);
    }


    /**
     * 把 from 的同名的属性值覆盖到 to
     * @param from
     * @param to
     * @throws IllegalAccessException
     */
    public static void copy(Object from, Object to) throws IllegalAccessException {
        if (from != null && to != null) {
            Field[] inputFields = from.getClass().getDeclaredFields();
            Field[] outputFields = to.getClass().getDeclaredFields();

            Map<String, Field> outputMap = new HashMap<>();
            for (Field field : outputFields) {
                outputMap.put(field.getName(), field);
            }

            for (Field inputField : inputFields) {
                String name = inputField.getName();
                Field outputField = outputMap.get(name);
                if (outputField != null) {
                    inputField.setAccessible(true);
                    outputField.setAccessible(true);
                    outputField.set(to, inputField.get(from));
                }
            }
        }
    }

}
