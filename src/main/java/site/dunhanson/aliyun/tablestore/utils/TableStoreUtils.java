package site.dunhanson.aliyun.tablestore.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.TableStoreException;
import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import com.alicloud.openservices.tablestore.model.search.query.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import site.dunhanson.aliyun.tablestore.entity.BasicInfo;
import site.dunhanson.aliyun.tablestore.entity.Page;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author dunhanson
 * @date 2020.03.20
 * @description TableStore工具类
 */
@Slf4j
public class TableStoreUtils {
    private static final String FILE_PATH = "tableStore.yaml";
    private static final String TABLE_STORE = "tableStore";
    private static final String DEFAULT = "default";
    private static final String END_POINT = "endPoint";
    private static final String ACCESS_KEY_ID = "accessKeyId";
    private static final String ACCESS_KEY_SECRET = "accessKeySecret";
    private static final String INSTANCE_NAME = "instanceName";
    private static final String TABLE_NAME = "tableName";
    private static final String PRIMARY_KEY = "primaryKey";
    private static final String SECONDARY_INDEX = "secondaryIndex";
    private static final String INDEX_NAME = "indexName";
    private static final String IGNORE_COLUMN = "ignoreColumn";
    private static final String LIMIT = "limit";
    private static final String UNDERLINE = "_";
    /**SyncClient Map集合**/
    private static Map<BasicInfo, SyncClient> syncClientMap = new HashMap<>();
    /**gson**/
    private static Gson gson = new Gson();

    /**
     * 获取基础信息对象
     * @param alias
     * @return
     */
    private static BasicInfo getBasicInfo(String alias) {
        BasicInfo basicInfo = new BasicInfo();
        String[] arr = new String[]{
                END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET, INSTANCE_NAME, TABLE_NAME, PRIMARY_KEY, SECONDARY_INDEX, INDEX_NAME,
                IGNORE_COLUMN, LIMIT
        };
        for(int i = 0; i < arr.length; i++) {
            String key = arr[i];
            Object value = YamlUtils.getValue(FILE_PATH, TABLE_STORE, alias, key);
            if(key.equals(END_POINT)) {
                basicInfo.setEndPoint((String)value);
            } else if(key.equals(ACCESS_KEY_ID)) {
                basicInfo.setAccessKeyId((String)value);
            } else if(key.equals(ACCESS_KEY_SECRET)) {
                basicInfo.setAccessKeySecret((String)value);
            } else if(key.equals(INSTANCE_NAME)) {
                basicInfo.setInstanceName((String)value);
            } else if(key.equals(TABLE_NAME)) {
                basicInfo.setTableName((String)value);
            } else if(key.equals(PRIMARY_KEY)) {
                basicInfo.setPrimaryKey((List<String>) value);
            } else if(key.equals(SECONDARY_INDEX)) {
                basicInfo.setSecondaryIndex((List<String>) value);
            } else if(key.equals(INDEX_NAME)) {
                basicInfo.setIndexName((List<String>) value);
            } else if(key.equals(IGNORE_COLUMN)) {
                basicInfo.setIgnoreColumn((List<String>) value);
            } else if(key.equals(LIMIT)) {
                basicInfo.setLimit(((Integer)value));
            }
        }
        return basicInfo;
    }

    /**
     * 获取SyncClient
     * @param basicInfo
     * @return
     */
    private static SyncClient getSyncClient(BasicInfo basicInfo) {
        SyncClient client = syncClientMap.get(basicInfo);;
        if(client == null) {
            String endPoint = basicInfo.getEndPoint();
            String accessKeyId = basicInfo.getAccessKeyId();
            String accessKeySecret = basicInfo.getAccessKeySecret();
            String instanceName = basicInfo.getInstanceName();
            client = new SyncClient(endPoint, accessKeyId, accessKeySecret, instanceName);
            syncClientMap.put(basicInfo, client);
        } else {
            client = syncClientMap.get(basicInfo);
        }
        return client;
    }

    public static <T> Page<T> search(SearchQuery query, Class<T> clazz) {
        return search(query, clazz, new ArrayList<>());
    }

    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, List<String> columns) {
        return search(getAlias(clazz), query, clazz, 0, columns);
    }

    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, String...ignoreColumns) {
        return search(query, clazz, getNoIgnoreColumns(clazz, ignoreColumns));
    }

    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, boolean ignoreColumn) {
        return ignoreColumn ? search(query, clazz, getNoIgnoreColumns(clazz)) : search(query, clazz);
    }

    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, int offset, int limit) {
        return search(query, clazz, offset, limit, new ArrayList<>());
    }

    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, int offset, int limit, List<String> columns) {
        query.setOffset(offset);
        query.setLimit(limit);
        query.setGetTotalCount(true);
        return search(getAlias(clazz), query, clazz, 0, columns);
    }

    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, int offset, int limit, String...ignoreColumns) {
        return search(query, clazz, offset, limit, getNoIgnoreColumns(clazz, ignoreColumns));
    }

    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, int offset, int limit, boolean ignoreColumn) {
        return ignoreColumn ? search(query, clazz, offset, limit, getNoIgnoreColumns(clazz)) : search(query, clazz, offset, limit);
    }

    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, int index) {
        return search(query, clazz, index, new ArrayList<>());
    }

    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, int index, List<String> columns) {
        return search(getAlias(clazz), query, clazz, index, columns);
    }

    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, int index, String...ignoreColumns) {
        return search(query, clazz, index, getNoIgnoreColumns(clazz, ignoreColumns));
    }

    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, int index, boolean ignoreColumn) {
        return ignoreColumn ? search(query, clazz, index, getNoIgnoreColumns(clazz)) : search(query, clazz, index);
    }

    public static <T> Page<T> search(String alias, SearchQuery searchQuery, Class<T> clazz, int index, List<String> columns) {
        //开始时间
        LocalDateTime startTime = LocalDateTime.now();
        //基础信息
        BasicInfo aliasBasicInfo = getBasicInfo(alias);
        //设置默认参数
        setDefault(aliasBasicInfo);
        //表名
        String tableName = aliasBasicInfo.getTableName();
        //实例名
        String indexName = aliasBasicInfo.getIndexName().get(index);
        //获取总记录数
        searchQuery.setGetTotalCount(true);
        //分页数
        Integer limit = searchQuery.getLimit();
        if(limit == null) {
            limit = aliasBasicInfo.getLimit();
        }
        searchQuery.setLimit(limit);
        //偏移数
        Integer offset = searchQuery.getOffset();
        if(offset == null) {
            offset = 0;
        }
        searchQuery.setOffset(offset);
        //查询对象
        SearchRequest request = new SearchRequest(tableName, indexName, searchQuery);
        //返回字段
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        if(columns == null || columns.size() == 0) {
            columnsToGet.setReturnAll(true);
        } else {
            columnsToGet.setColumns(columns);
        }
        request.setColumnsToGet(columnsToGet);
        //查询并返回响应
        SyncClient client = getSyncClient(aliasBasicInfo);
        SearchResponse resp = client.search(request);
        //设置分页对象
        Page<T> page = getListFromSearchResponse(resp, clazz, limit);
        //设置偏移数
        page.setOffset(offset);
        //结束时间
        LocalDateTime endTime = LocalDateTime.now();
        //日志打印
        StringBuffer logStr = new StringBuffer();
        logStr.append("---------> Easy TableStore Search --------->");
        logStr.append("\n");
        logStr.append("Search:" + getQueryString(searchQuery.getQuery()));
        logStr.append("\n");
        logStr.append("ElapsedTime:" + ChronoUnit.MILLIS.between(startTime, endTime));
        logStr.append("\n");
        logStr.append("<--------- Easy TableStore Search <---------");
        log.info(logStr.toString());
        return page;
    }

    /**
     * 获取结果集
     * @param response
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Page<T> getListFromSearchResponse(SearchResponse response, Class<T> clazz, int limit) {
        long totalCount = response.getTotalCount();
        List<T> list = new ArrayList();
        List<Row> rows = response.getRows();
        for(Row row : rows) {
            list.add(rowToEntity(row, clazz));
        }
        return new Page<T>(limit, totalCount, list);
    }

    /**
     * 行转换成对象
     * @param row
     * @param clazz
     * @param <T>
     * @return
     */
    private static <T> T rowToEntity(Row row, Class<T> clazz) {
        T entity = null;
        Map<String, Object> map = new HashMap<>();
        //遍历主键
        PrimaryKeyColumn[] primaryKeyColumns = row.getPrimaryKey().getPrimaryKeyColumns();
        for(PrimaryKeyColumn column : primaryKeyColumns) {
            String name = column.getName();
            Object value = null;
            try {
                value = column.getValue().toColumnValue().getValue();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //包含下划线，转换成驼峰
            if(name.contains(UNDERLINE)) {
                name = underlineToHump(name);
            }
            map.put(name, value);
        }
        //遍历列
        Column[] columns = row.getColumns();
        for(int i = 0; i < columns.length; i++) {
            Column column = columns[i];
            String name = column.getName();
            Object value = column.getValue().getValue();
            //包含下划线，转换成驼峰
            if(name.contains(UNDERLINE)) {
                name = underlineToHump(name);
            }
            map.put(name, value);
        }
        //map转换成对象
        return gson.fromJson(gson.toJson(map), clazz);
    }

    /**
     * 下划线变驼峰
     * @param text
     * @return
     */
    private static String underlineToHump(String text) {
        StringBuffer stringBuffer = new StringBuffer();
        String[] arr = text.split(UNDERLINE);
        for(int i = 0; i < arr.length; i++) {
            String str = arr[i];
            if(i != 0) {
                str = str.substring(0, 1).toUpperCase() + str.substring(1);
            }
            stringBuffer.append(str);
        }
        return stringBuffer.toString();
    }

    /**
     * 驼峰转下划线
     * @param text
     * @return
     */
    private static String humpToUnderline(String text){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < text.length(); i++) {
            char tempChar = text.charAt(i);
            if(Character.isUpperCase(tempChar)) {
                stringBuilder.append(UNDERLINE + Character.toLowerCase(tempChar));
            } else {
                stringBuilder.append(tempChar);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 获取alias
     * @param clazz
     * @return
     */
    private static String getAlias(Class clazz) {
        String alias = clazz.getSimpleName();
        alias = alias.substring(0,1).toLowerCase() + alias.substring(1);
        return alias;
    }

    /**
     * 根据对象获取别名（首字母小写）
     * @param obj
     * @return
     */
    private static String getAlias(Object obj) {
        String alias = obj.getClass().getSimpleName();
        alias = alias.substring(0,1).toLowerCase() + alias.substring(1);
        return alias;
    }

    /**
     * 获取没有忽略字段的字段集合
     * @param clazz
     * @param ignoreColumns
     * @return
     */
    private static List<String> getNoIgnoreColumns(Class clazz, List<String> ignoreColumns) {
        List<String> allColumns = getAllFieldName(clazz, true);
        allColumns.removeAll(ignoreColumns);
        return allColumns;
    }

    /**
     * 获取没有忽略字段的字段集合
     * @param clazz
     * @param ignoreColumns
     * @return
     */
    private static List<String> getNoIgnoreColumns(Class clazz, String...ignoreColumns) {
        return getNoIgnoreColumns(clazz, Arrays.asList(ignoreColumns));
    }

    /**
     * 获取没有忽略字段的字段集合
     * @param clazz
     * @return
     */
    private static List<String> getNoIgnoreColumns(Class clazz) {
        List<String> ignoreColumns = getBasicInfo(getAlias(clazz)).getIgnoreColumn();
        return getNoIgnoreColumns(clazz, ignoreColumns);
    }

    /**
     * 获取类的所有字段名
     * @param clazz
     * @param humpToUnderline   是否需要 驼峰转下划线
     * @return
     */
    private static List<String> getAllFieldName(Class clazz, boolean humpToUnderline) {
        List<String> list = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            String name = field.getName();
            list.add(humpToUnderline ? humpToUnderline(name) : name);
        }
        return list;
    }

    /**
     * 设置默认参数
     * @param aliasBasicInfo
     */
    private static void setDefault(BasicInfo aliasBasicInfo) {
        BasicInfo defaultBasicInfo = getBasicInfo(DEFAULT);
        // endPoint
        if(StringUtils.isBlank(aliasBasicInfo.getEndPoint())) {
            aliasBasicInfo.setEndPoint(defaultBasicInfo.getEndPoint());
        }
        // accessKeyId
        if(StringUtils.isBlank(aliasBasicInfo.getAccessKeyId())) {
            aliasBasicInfo.setAccessKeyId(defaultBasicInfo.getAccessKeyId());
        }
        // accessKeySecret
        if(StringUtils.isBlank(aliasBasicInfo.getAccessKeySecret())) {
            aliasBasicInfo.setAccessKeySecret(defaultBasicInfo.getAccessKeySecret());
        }
        // instanceName
        if(StringUtils.isBlank(aliasBasicInfo.getInstanceName())) {
            aliasBasicInfo.setInstanceName(defaultBasicInfo.getInstanceName());
        }
        // limit
        if(aliasBasicInfo.getLimit() == null) {
            if(defaultBasicInfo.getLimit() == null) {
                aliasBasicInfo.setLimit(10);
            } else {
                aliasBasicInfo.setLimit(defaultBasicInfo.getLimit());
            }
        }
    }


    /**
     * 查询字符串
     * @param query
     * @return
     */
    public static String getQueryString(Query query) {
        return getQueryString(query, true);
    }

    /**
     * 查询字符串
     * @param query
     * @return
     */
    public static String getQueryString(Query query, boolean top) {
        StringBuffer stringBuffer = new StringBuffer();
        if(query instanceof MatchAllQuery) {
            stringBuffer.append("*" + "=" + "*");
        } else if (query instanceof MatchQuery) {
            MatchQuery temp = (MatchQuery)query;
            stringBuffer.append(temp.getFieldName());
            stringBuffer.append("=");
            stringBuffer.append("\"");
            stringBuffer.append(temp.getText());
            stringBuffer.append("\"");
        } else if (query instanceof MatchPhraseQuery) {
            MatchPhraseQuery temp = (MatchPhraseQuery)query;
            stringBuffer.append(temp.getFieldName());
            stringBuffer.append("=");
            stringBuffer.append("\"");
            stringBuffer.append(temp.getText());
            stringBuffer.append("\"");
        } else if (query instanceof TermQuery) {
            TermQuery temp = (TermQuery)query;
            stringBuffer.append(temp.getFieldName());
            stringBuffer.append("=");
            stringBuffer.append("\"");
            stringBuffer.append(temp.getTerm().getValue());
            stringBuffer.append("\"");
        } else if (query instanceof TermsQuery) {
            TermsQuery temp = (TermsQuery)query;
            String fieldName = temp.getFieldName();
            List<ColumnValue> terms = temp.getTerms();
            if(terms != null && terms.size() > 0) {
                stringBuffer.append("(");
                for(ColumnValue columnValue : terms) {
                    stringBuffer.append(fieldName);
                    stringBuffer.append("=");
                    stringBuffer.append("\"");
                    stringBuffer.append(columnValue.getValue());
                    stringBuffer.append("\"");
                    stringBuffer.append(" OR ");
                }
                stringBuffer = new StringBuffer(stringBuffer.substring(0, stringBuffer.lastIndexOf(" OR ")));
                stringBuffer.append(")");
            }
        } else if (query instanceof PrefixQuery) {
            PrefixQuery temp = (PrefixQuery)query;
            stringBuffer.append(temp.getFieldName());
            stringBuffer.append("=");
            stringBuffer.append("\"");
            stringBuffer.append(temp.getPrefix());
            stringBuffer.append("\"");
        } else if (query instanceof RangeQuery) {
            RangeQuery temp = (RangeQuery)query;
            Object from = temp.getFrom();
            Object to = temp.getTo();
            if(from != null) {
                stringBuffer.append(temp.getFieldName());
                stringBuffer.append(">");
                stringBuffer.append("\"");
                stringBuffer.append(from);
                stringBuffer.append("\"");
            }
            if(to != null) {
                if(from != null) {
                    stringBuffer.append(" AND ");
                }
                stringBuffer.append(temp.getFieldName());
                stringBuffer.append("<");
                stringBuffer.append("\"");
                stringBuffer.append(to);
                stringBuffer.append("\"");
            }
        } else if (query instanceof WildcardQuery) {
            WildcardQuery temp = (WildcardQuery)query;
            stringBuffer.append(temp.getFieldName());
            stringBuffer.append("=");
            stringBuffer.append("\"");
            stringBuffer.append(temp.getValue());
            stringBuffer.append("\"");
        } else if (query instanceof BoolQuery) {
            BoolQuery temp = (BoolQuery)query;
            List<Query> mustQueries = temp.getMustQueries();
            List<Query> mustNotQueries = temp.getMustNotQueries();
            List<Query> shouldQueries = temp.getShouldQueries();
            List<Query> filterQueries = temp.getFilterQueries();
            if(mustQueries != null && mustQueries.size() > 0) {
                //多条件-
                if(!top) {
                    stringBuffer.append("(");
                }
                for(Query getQuery : mustQueries) {
                    stringBuffer.append(getQueryString(getQuery, false));
                    stringBuffer.append(" AND ");
                }
                stringBuffer = new StringBuffer(stringBuffer.substring(0, stringBuffer.lastIndexOf(" AND ")));
                if(!top) {
                    stringBuffer.append(")");
                }
            } else if(mustNotQueries != null && mustNotQueries.size() > 0) {
                //多条件-并查询
                if(!top) {
                    stringBuffer.append("(");
                }
                for(Query getQuery : mustNotQueries) {
                    stringBuffer.append("!");
                    stringBuffer.append("(");
                    stringBuffer.append(getQueryString(getQuery, false));
                    stringBuffer.append(")");
                    stringBuffer.append(" AND ");
                }
                stringBuffer = new StringBuffer(stringBuffer.substring(0, stringBuffer.lastIndexOf(" AND ")));
                if(!top) {
                    stringBuffer.append(")");
                }
            } else if(shouldQueries != null && shouldQueries.size() > 0) {
                //多条件-或查询
                if(!top) {
                    stringBuffer.append("(");
                }
                for(Query getQuery : shouldQueries) {
                    stringBuffer.append(getQueryString(getQuery, false));
                    stringBuffer.append(" OR ");
                }
                stringBuffer = new StringBuffer(stringBuffer.substring(0, stringBuffer.lastIndexOf(" OR ")));
                if(!top) {
                    stringBuffer.append(")");
                }
            } else if(filterQueries != null && filterQueries.size() > 0) {
                //多条件-或查询
                if(!top) {
                    stringBuffer.append("(");
                }
                for(Query getQuery : filterQueries) {
                    stringBuffer.append(getQueryString(getQuery, false));
                    stringBuffer.append(" OR ");
                }
                stringBuffer = new StringBuffer(stringBuffer.substring(0, stringBuffer.lastIndexOf(" OR ")));
                if(!top) {
                    stringBuffer.append(")");
                }
            }
        }
        return stringBuffer.toString();
    }




    // 增/删/改 操作

    /**
     * 新增（如果该记录存在则完成覆盖更新）
     * @param obj
     */
    public static int insert(Object obj) {
        // 获取表的配置信息
        BasicInfo aliasBasicInfo = buildBasicInfo(getAlias(obj));

        // 创建 SyncClient
        SyncClient client = getSyncClient(aliasBasicInfo);

        JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);

        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        List<String> primaryKeyList = aliasBasicInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            Object value = jsonObject.get(underlineToHump(key));
            if (value.getClass().getSimpleName().equals("Long")) {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value));
            } else {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value));
            }
        }
        PrimaryKey primaryKey = primaryKeyBuilder.build();
        RowPutChange rowPutChange = new RowPutChange(aliasBasicInfo.getTableName(), primaryKey);

        // 设置其他属性
        for (Map.Entry<String, Object> map : jsonObject.entrySet()) {
            String key = map.getKey();
            Object value = map.getValue();
            key = humpToUnderline(key);     // 驼峰转下划线
            if (!primaryKeyList.contains(key) && value != null) {       // 非主键 非空 判断
                if (value.getClass().getSimpleName().equals("String")) {      // 待完善 其他类型当成字符串处理，目前是够用的
                    rowPutChange.addColumn(new Column(key, ColumnValue.fromString((String) value)));
                } else if (value.getClass().getSimpleName().equals("Integer")) {
                    rowPutChange.addColumn(new Column(key, ColumnValue.fromLong((Integer) value)));
                } else if (value.getClass().getSimpleName().equals("Long")) {
                    rowPutChange.addColumn(new Column(key, ColumnValue.fromLong((Long) value)));
                } else if (value.getClass().getSimpleName().equals("Double")) {
                    rowPutChange.addColumn(new Column(key, ColumnValue.fromDouble(Double.valueOf(value.toString()))));
                }  else if (value.getClass().getSimpleName().equals("Boolean")) {
                    rowPutChange.addColumn(new Column(key, ColumnValue.fromBoolean(Boolean.valueOf(value.toString()))));
                } else if (value.getClass().getSimpleName().equals("Date"))  {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String format = sdf.format(value);
                    rowPutChange.addColumn(new Column(key, ColumnValue.fromString(format)));
                }
            }
        }
        // 写入
        PutRowResponse putRowResponse = client.putRow(new PutRowRequest(rowPutChange));
        int num = putRowResponse.getConsumedCapacity().getCapacityUnit().getWriteCapacityUnit();
        return num;
    }

    /**
     * 更新不为空的字段
     * @param obj
     */
    public static int update(Object obj) {
        // 获取表的配置信息
        BasicInfo aliasBasicInfo = buildBasicInfo(getAlias(obj));

        RowUpdateChange rowUpdateChange = getRowUpdateChange(obj, aliasBasicInfo);

        // 创建 SyncClient
        SyncClient client = getSyncClient(aliasBasicInfo);

        // 更新
        int num = 0;
        try {
            UpdateRowResponse updateRowResponse = client.updateRow(new UpdateRowRequest(rowUpdateChange));
            num = updateRowResponse.getConsumedCapacity().getCapacityUnit().getWriteCapacityUnit();
        }catch (TableStoreException e){
            if ("OTSConditionCheckFail".equals(e.getErrorCode())) {     // 期望不一致返回 num=0即可
                return 0;
            } else {
                e.printStackTrace();
                throw e;
            }
        }
        return num;
    }

    /**
     * 获取 {@link RowUpdateChange}  行的更新对象（只会更新不为空的字段）
     * @param obj
     * @param aliasBasicInfo    {@link BasicInfo}
     * @return
     */
    private static RowUpdateChange getRowUpdateChange(Object obj, BasicInfo aliasBasicInfo) {
        JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);

        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        List<String> primaryKeyList = aliasBasicInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            Object value = jsonObject.get(underlineToHump(key));
            if (value.getClass().getSimpleName().equals("Long")) {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value));
            } else {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value));
            }
        }
        PrimaryKey primaryKey = primaryKeyBuilder.build();

        RowUpdateChange rowUpdateChange = new RowUpdateChange(aliasBasicInfo.getTableName(), primaryKey);
        // 设置其他属性
        for (Map.Entry<String, Object> map : jsonObject.entrySet()) {
            String key = map.getKey();
            Object value = map.getValue();
            key = humpToUnderline(key);     // 驼峰转下划线
            if (!primaryKeyList.contains(key) && value != null) {       // 非主键 非空 判断
                if (value.getClass().getSimpleName().equals("String")) {      // 待完善 其他类型当成字符串处理，目前是够用的
                    rowUpdateChange.put(new Column(key, ColumnValue.fromString((String) value)));
                } else if (value.getClass().getSimpleName().equals("Integer")) {
                    rowUpdateChange.put(new Column(key, ColumnValue.fromLong((Integer) value)));
                } else if (value.getClass().getSimpleName().equals("Long")) {      // 待完善 其他类型当成字符串处理，目前是够用的
                    rowUpdateChange.put(new Column(key, ColumnValue.fromLong((Long) value)));
                } else if (value.getClass().getSimpleName().equals("Double")) {
                    rowUpdateChange.put(new Column(key, ColumnValue.fromDouble(Double.valueOf(value.toString()))));
                } else if (value.getClass().getSimpleName().equals("Boolean")) {
                    rowUpdateChange.put(new Column(key, ColumnValue.fromBoolean(Boolean.valueOf(value.toString()))));
                } else if (value.getClass().getSimpleName().equals("Date"))  {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String format = sdf.format(value);
                    rowUpdateChange.put(new Column(key, ColumnValue.fromString(format)));
                }
            }
        }
        Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);
        rowUpdateChange.setCondition(condition);
        return rowUpdateChange;
    }

    /**
     * 获取 {@link RowDeleteChange}  行的删除对象
     * @param obj
     * @param aliasBasicInfo    {@link BasicInfo}
     * @return
     */
    private static RowDeleteChange getRowDeleteChange(Object obj, BasicInfo aliasBasicInfo) {
        JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);

        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        List<String> primaryKeyList = aliasBasicInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            Object value = jsonObject.get(underlineToHump(key));
            if (value.getClass().getSimpleName().equals("Long")) {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value));
            } else {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value));
            }
        }
        PrimaryKey primaryKey = primaryKeyBuilder.build();

        RowDeleteChange rowDeleteChange = new RowDeleteChange(aliasBasicInfo.getTableName(), primaryKey);
        return rowDeleteChange;
    }

    /**
     * 构建 {@link BasicInfo}
     * @param alias 实体的别名
     * @return
     */
    private static BasicInfo buildBasicInfo(String alias) {
        //基础信息
        BasicInfo basicInfo = getBasicInfo(alias);
        //设置默认参数
        setDefault(basicInfo);
        return basicInfo;
    }


    /**
     * 根据主键批量删除
     * @param list
     * @return
     */
    public static int batchUpdate(List list) {
        int num = 0;
        if (list != null) {
            List<List> batches = Lists.partition(list, 200);
            for (List batch : batches) {
                num += batchUpdateFor200(batch);
            }
        }
        return num;
    }

    /**
     * 批量更新（更新不为空的字段，不能超过200）
     */
    private static int batchUpdateFor200(List list) {
        int num = 0;
        if (list != null && list.size() > 0) {
            BasicInfo aliasBasicInfo = buildBasicInfo(getAlias(list.get(0)));
            BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest();

            for (Object obj : list) {
                RowUpdateChange rowUpdateChange = getRowUpdateChange(obj, aliasBasicInfo);
                batchWriteRowRequest.addRowChange(rowUpdateChange);
            }

            // 创建 SyncClient
            SyncClient client = getSyncClient(aliasBasicInfo);

            try {
                BatchWriteRowResponse response = client.batchWriteRow(batchWriteRowRequest);
                num = response.getSucceedRows().size();
            }catch (TableStoreException e){
                if ("OTSConditionCheckFail".equals(e.getErrorCode())) {     // 期望不一致返回 num=0即可
                    return 0;
                } else {
                    e.printStackTrace();
                    throw e;
                }
            }

        }
        return num;
    }

    /**
     * 根据主键批量删除
     * @param list
     * @return
     */
    public static int batchDelete(List list) {
        int num = 0;
        if (list != null) {
            List<List> batches = Lists.partition(list, 200);
            for (List batch : batches) {
                num += batchDeleteFor200(batch);
            }
        }
        return num;
    }

    /**
     * 根据主键批量删除（一次最多删除200）
     * @param list
     * @return
     */
    private static int batchDeleteFor200(List list) {
        int num = 0;
        if (list != null && list.size() > 0) {
            BasicInfo aliasBasicInfo = buildBasicInfo(getAlias(list.get(0)));
            BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest();

            for (Object obj : list) {
                RowDeleteChange rowDeleteChange = getRowDeleteChange(obj, aliasBasicInfo);
                batchWriteRowRequest.addRowChange(rowDeleteChange);
            }

            // 创建 SyncClient
            SyncClient client = getSyncClient(aliasBasicInfo);
            try {
                BatchWriteRowResponse response = client.batchWriteRow(batchWriteRowRequest);
                num = response.getSucceedRows().size();
            }catch (TableStoreException e){
                if ("OTSConditionCheckFail".equals(e.getErrorCode())) {     // 期望不一致返回 num=0即可
                    return 0;
                } else {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        return num;
    }

    /**
     * 根据主键删除
     * @param obj
     */
    public static int delete(Object obj) {
        // 获取表的配置信息
        BasicInfo aliasBasicInfo = buildBasicInfo(getAlias(obj));

        RowDeleteChange rowDeleteChange = getRowDeleteChange(obj, aliasBasicInfo);

        // 创建 SyncClient
        SyncClient client = getSyncClient(aliasBasicInfo);

        // 更新
        int num = 0;
        try {
            DeleteRowResponse deleteRowResponse = client.deleteRow(new DeleteRowRequest(rowDeleteChange));
            num = deleteRowResponse.getConsumedCapacity().getCapacityUnit().getWriteCapacityUnit();
        }catch (TableStoreException e){
            if ("OTSConditionCheckFail".equals(e.getErrorCode())) {     // 期望不一致返回 num=0即可
                return 0;
            } else {
                e.printStackTrace();
                throw e;
            }
        }
        return num;
    }

    /**
     * 通过主键批量获取
     * @param list
     * @param clazz
     * @param columnsToGet  添加要读取的列集合
     * @param <T>
     * @return
     */
    public static <T> List<T> batchGetRow(List<T> list , Class<T> clazz, Collection<String> columnsToGet) {
        List<T> result = new LinkedList<>();
        if (list != null) {
            List<List<T>> batches = Lists.partition(list, 100);     // 因为ots最大能支持100所以分批处理了
            for (List<T> batch : batches) {
                result.addAll(batchGetRowFor100(batch, clazz, columnsToGet));
            }
        }
        return result;
    }

    /**
     * 通过主键批量获取（list最多100，否则报错）
     * @param list
     * @param clazz
     * @param columnsToGet      添加要读取的列集合（默认查全部）
     * @param <T>
     * @return
     */
    private static <T> List<T> batchGetRowFor100(List<T> list , Class<T> clazz, Collection<String> columnsToGet) {
        BasicInfo aliasBasicInfo = buildBasicInfo(getAlias(list.get(0)));

        MultiRowQueryCriteria multiRowQueryCriteria = new MultiRowQueryCriteria(aliasBasicInfo.getTableName());
        for (Object obj : list) {
            JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);

            // 构造主键
            PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
            List<String> primaryKeyList = aliasBasicInfo.getPrimaryKey();
            for (String key : primaryKeyList) {
                Object value = jsonObject.get(underlineToHump(key));
                if (value.getClass().getSimpleName().equals("Long")) {
                    primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value));
                } else {
                    primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value));
                }
            }
            PrimaryKey primaryKey = primaryKeyBuilder.build();
            multiRowQueryCriteria.addRow(primaryKey);
        }



        BatchGetRowRequest batchGetRowRequest = new BatchGetRowRequest();
        // batchGetRow支持读取多个表的数据， 一个multiRowQueryCriteria对应一个表的查询条件，可以添加多个multiRowQueryCriteria.
        multiRowQueryCriteria.setMaxVersions(1);
        if (columnsToGet != null && columnsToGet.size() > 0) {
            multiRowQueryCriteria.addColumnsToGet(columnsToGet);
        }
        batchGetRowRequest.addMultiRowQueryCriteria(multiRowQueryCriteria);

        BatchGetRowResponse batchGetRowResponse = getSyncClient(aliasBasicInfo).batchGetRow(batchGetRowRequest);
        if (!batchGetRowResponse.isAllSucceed()) {
            for (BatchGetRowResponse.RowResult rowResult : batchGetRowResponse.getFailedRows()) {
                log.warn("失败的行：" + batchGetRowRequest.getPrimaryKey(rowResult.getTableName(), rowResult.getIndex()));
                log.warn("失败原因：" + rowResult.getError());
            }

//            /**
//             * 可以通过createRequestForRetry方法再构造一个请求对失败的行进行重试。这里只给出构造重试请求的部分。
//             * 推荐的重试方法是使用SDK的自定义重试策略功能，支持对batch操作的部分行错误进行重试。设定重试策略后， 调用接口处即不需要增加重试代码.
//             */
//            BatchGetRowRequest retryRequest = batchGetRowRequest.createRequestForRetry(batchGetRowResponse.getFailedRows());
        }

        List<T> result = new LinkedList<>();
        List<BatchGetRowResponse.RowResult> succeedRows = batchGetRowResponse.getSucceedRows();
        for (BatchGetRowResponse.RowResult rowResult : succeedRows) {
            Row row = rowResult.getRow();
            if (row != null) {
                result.add(rowToEntity(row, clazz));
            }
        }
        return result;
    }


    /**
     * 根据主键获取一行记录
     * @param entity
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T get(T entity, Class<T> clazz) {
        // 获取表的配置信息
        BasicInfo aliasBasicInfo = buildBasicInfo(getAlias(clazz));

        JSONObject jsonObject = (JSONObject) JSON.toJSON(entity);

        // 构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        List<String> primaryKeyList = aliasBasicInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            Object value = jsonObject.get(underlineToHump(key));
            if (value.getClass().getSimpleName().equals("Long")) {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value));
            } else {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value));
            }
        }
        PrimaryKey primaryKey = primaryKeyBuilder.build();

        // 读一行
        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(aliasBasicInfo.getTableName(), primaryKey);
        // 设置读取最新版本
        criteria.setMaxVersions(1);
        GetRowResponse getRowResponse = getSyncClient(aliasBasicInfo).getRow(new GetRowRequest(criteria));
        Row row = getRowResponse.getRow();

        T t = null;
        if (row != null) {
            t = rowToEntity(row, clazz);
        }
        return t;
    }

    /**
     * 根据二级索引查找（ps：只支持一个，即第一个不为空的二级索引）
     * 因为其他字段没有，所以反查主表
     * @param entity
     * @param clazz
     * @param columnsToGet
     * @param <T>
     * @return
     */
    public static <T> List<T> searchBysecondaryIndex(T entity,  Class<T> clazz, Collection<String> columnsToGet){
        return searchBysecondaryIndex(entity, clazz, columnsToGet, 0);
    }


    /**
     * 根据二级索引查找（ps：只支持一个，即第一个不为空的二级索引）
     * 因为其他字段没有，所以反查主表
     * @param entity
     * @param clazz
     * @param columnsToGet      添加要读取的列集合
     * @param limit             获取的条数（如果小1则全查）
     * @param <T>
     * @return
     */
    public static <T> List<T> searchBysecondaryIndex(T entity,  Class<T> clazz, Collection<String> columnsToGet, int limit) {
        List<T> result = new LinkedList<>();
        // 1、获取表的配置信息
        BasicInfo aliasBasicInfo = buildBasicInfo(getAlias(clazz));
        JSONObject jsonObject = (JSONObject) JSON.toJSON(entity);


        // 2、设置起始主键/结束主键
        PrimaryKeyBuilder startPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        PrimaryKeyBuilder endPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();

        // 2.1、设置索引表PK（即：二级索引，只支持一个）（查询时 二级索引表的PK要放在主表之前，要不然会报错）
        RangeRowQueryCriteria rangeRowQueryCriteria = null;
        List<String> secondaryIndexList = aliasBasicInfo.getSecondaryIndex();
        String secondaryIndexName = "";     // 当前需要查询的二级索引
        for (String secondaryIndex : secondaryIndexList) {
            String key = secondaryIndex.replace(aliasBasicInfo.getTableName() + "_index2_", "");
            Object value = jsonObject.get(underlineToHump(key));
            if (value != null) {
                if (value.getClass().getSimpleName().equals("Long")) {
                    startPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value)); // 索引表PK最小值。
                    endPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value)); // 索引表PK最大值。
                } else {
                    startPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value)); // 索引表PK最小值。
                    endPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value)); // 索引表PK最大值。
                }
                rangeRowQueryCriteria = new RangeRowQueryCriteria(secondaryIndex);
                secondaryIndexName = key;
                break;
            }
        }

        // 2.2、设置主表PK（任意范围）
        List<String> primaryKeyList = aliasBasicInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            if (!key.equals(secondaryIndexName)) {  // 排除当前正在查询的二级索引
                startPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.INF_MIN); // 主表PK最小值。
                endPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.INF_MAX); // 主表PK最大值。
            }
        }

        // 3、查询
        if (rangeRowQueryCriteria != null) {
            rangeRowQueryCriteria.setInclusiveStartPrimaryKey(startPrimaryKeyBuilder.build());  // 开始主键
            rangeRowQueryCriteria.setExclusiveEndPrimaryKey(endPrimaryKeyBuilder.build());      // 结束主键
            rangeRowQueryCriteria.setMaxVersions(1);                                            // 设置读取最新版本
            if (limit > 0) {                                                                    // 默认一次扫描5000，如果有指定limit，设置成1000
                rangeRowQueryCriteria.setLimit(1000);
            }

            // 4、循环扫描（因为数据是分区的，所以需要分区扫描）
            SyncClient syncClient = getSyncClient(aliasBasicInfo);
            boolean completed = false;
            long index = 0;
            while (true) {
                log.debug("第{}获取，准备获取数量={}", ++index, rangeRowQueryCriteria.getLimit());
                GetRangeResponse getRangeResponse = syncClient.getRange(new GetRangeRequest(rangeRowQueryCriteria));
                List<Row> rows = getRangeResponse.getRows();
                log.debug("第{}获取，成功获取的数量={}", index, rows.size());
                // 1、处理 limit
                if (limit > 0) {
                    for (Row row : rows) {
                        if (result.size() >= limit) {
                            completed = true;
                            break;
                        } else {
                            result.add(rowToEntity(row, clazz));
                        }
                    }
                } else {
                    for (Row row : rows) {
                        result.add(rowToEntity(row, clazz));
                    }
                }

                // 若nextStartPrimaryKey不为null，则继续读取。
                if (getRangeResponse.getNextStartPrimaryKey() != null && !completed) {
                    rangeRowQueryCriteria.setInclusiveStartPrimaryKey(getRangeResponse.getNextStartPrimaryKey());
                } else {
                    break;
                }
            }
            // 如果需要其他字段需要反查主表
            result = batchGetRow(result, clazz, columnsToGet);
        }
        return result;
    }

}
