package site.dunhanson.aliyun.tablestore.test;

import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.query.BoolQuery;
import com.alicloud.openservices.tablestore.model.search.query.RangeQuery;
import com.alicloud.openservices.tablestore.model.search.query.TermQuery;
import com.alicloud.openservices.tablestore.model.search.query.TermsQuery;
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
        DocumentExtract doc = new DocumentExtract();
        doc.setStatus(1L);
        List<DocumentExtract> docs = TableStoreUtils.getRangeBysecondaryIndex(doc, 1L, 10L, DocumentExtract.class, Arrays.asList("extract_json"), 100);
        System.out.println(docs);

//        int num = TableStoreUtils.countBysecondaryIndex(doc, 1L, 10L, Document.class);
//        System.out.println("[1,100]="+num);
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
