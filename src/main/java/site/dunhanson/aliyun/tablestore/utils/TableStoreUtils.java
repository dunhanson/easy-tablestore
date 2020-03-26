package site.dunhanson.aliyun.tablestore.utils;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKeyColumn;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import site.dunhanson.aliyun.tablestore.entity.BasicInfo;
import site.dunhanson.aliyun.tablestore.entity.Page;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author dunhanson
 * @date 2020.03.20
 * @description TableStore工具类
 */
public class TableStoreUtils {
    private static final String FILE_PATH = "tableStore.yaml";
    private static final String TABLE_STORE = "tableStore";
    private static final String DEFAULT = "default";
    private static final String END_POINT = "endPoint";
    private static final String ACCESS_KEY_ID = "accessKeyId";
    private static final String ACCESS_KEY_SECRET = "accessKeySecret";
    private static final String INSTANCE_NAME = "instanceName";
    private static final String TABLE_NAME = "tableName";
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
    public static BasicInfo getBasicInfo(String alias) {
        BasicInfo basicInfo = new BasicInfo();
        String[] arr = new String[]{
                END_POINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET, INSTANCE_NAME, TABLE_NAME, INDEX_NAME,
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
    public static SyncClient getSyncClient(BasicInfo basicInfo) {
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
    public static <T> T rowToEntity(Row row, Class<T> clazz) {
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
    public static String underlineToHump(String text) {
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
    public static String humpToUnderline(String text){
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
    public static String getAlias(Class clazz) {
        String alias = clazz.getSimpleName();
        alias = alias.substring(0,1).toLowerCase() + alias.substring(1);
        return alias;
    }

    /**
     * 获取没有忽略字段的字段集合
     * @param clazz
     * @param ignoreColumns
     * @return
     */
    public static List<String> getNoIgnoreColumns(Class clazz, List<String> ignoreColumns) {
        List<String> allColumns = getAllFieldName(clazz);
        allColumns.removeAll(ignoreColumns);
        return allColumns;
    }

    /**
     * 获取没有忽略字段的字段集合
     * @param clazz
     * @param ignoreColumns
     * @return
     */
    public static List<String> getNoIgnoreColumns(Class clazz, String...ignoreColumns) {
        return getNoIgnoreColumns(clazz, Arrays.asList(ignoreColumns));
    }

    /**
     * 获取没有忽略字段的字段集合
     * @param clazz
     * @return
     */
    public static List<String> getNoIgnoreColumns(Class clazz) {
        List<String> ignoreColumns = getBasicInfo(getAlias(clazz)).getIgnoreColumn();
        return getNoIgnoreColumns(clazz, ignoreColumns);
    }

    /**
     * 获取类的所有字段名
     * @param clazz
     * @return
     */
    public static List<String> getAllFieldName(Class clazz) {
        List<String> list = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            list.add(field.getName());
        }
        return list;
    }

    /**
     * 设置默认参数
     * @param aliasBasicInfo
     */
    public static void setDefault(BasicInfo aliasBasicInfo) {
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
}
