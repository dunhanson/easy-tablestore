package site.dunhanson.aliyun.tablestore.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.TableStoreException;
import com.alicloud.openservices.tablestore.core.utils.Pair;
import com.alicloud.openservices.tablestore.model.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import site.dunhanson.aliyun.tablestore.entity.TableInfo;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author chenjiaru
 * @date 2020.08.01
 * @description TableStore工具类（主表和二级索引工具，多元索引请使用 {@link TableStoreMultipleIndexUtils} ）
 */
@Slf4j
public class TableStoreUtils {

    /*每列最大2M，换成长度为 699050*/
    private static int columnMaxLength = 699050;
    /*每次请求（新增/更新）最大4M，换成长度为 699050 * 2*/
    private static int requestMaxLength = 699050 * 2;
    /**
     * SerializeConfig全局一个即可
     */
    private static SerializeConfig serializeConfig = new SerializeConfig();
    static {
        serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    /**
     * 新增（如果该记录存在则完成覆盖更新）
     * @param obj   实体类对象实例
     * @return
     */
    public static int insert(Object obj) {
        int num = 0;
        if (obj != null) {
            // 获取表的配置信息
            TableInfo aliasBasicInfo = CommonUtils.getTableInfo(obj);
            SyncClient client = Store.getInstance().getSyncClient();


            Map<String, Field> fieldMap = CommonUtils.getFieldMap(obj.getClass());
            // 1、构造主键
            PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
            List<String> primaryKeyList = aliasBasicInfo.getPrimaryKey();
            for (String key : primaryKeyList) {
                Field field = fieldMap.get(CommonUtils.underlineToHump(key));
                Object value = CommonUtils.getFieldValue(field, obj);
                if (field.getType().getSimpleName().equals("Long")) {
                    primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value));
                } else {
                    primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value));
                }
            }
            PrimaryKey primaryKey = primaryKeyBuilder.build();
            RowPutChange rowPutChange = new RowPutChange(aliasBasicInfo.getTableName(), primaryKey);

            // 2、设置其他属性
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, Field> map : fieldMap.entrySet()) {
                String key = map.getKey();
                Field field = map.getValue();
                Object value = CommonUtils.getFieldValue(field, obj);
                key = CommonUtils.humpToUnderline(key);     // 驼峰转下划线
                if (!primaryKeyList.contains(key) && value != null) {       // 非主键 非空 判断
                    if (field.getType().getSimpleName().equals("String")) {      // 待完善 其他类型当成字符串处理，目前是够用的
                        if (exceedRequestMaxLength(client, rowPutChange, stringBuilder, value, obj)) {
                            return 1;
                        }
                        rowPutChange.addColumn(new Column(key, ColumnValue.fromString((String) value)));
                    } else if (field.getType().getSimpleName().equals("Integer")) {
                        if (exceedRequestMaxLength(client, rowPutChange, stringBuilder, value, obj)) {
                            return 1;
                        }
                        rowPutChange.addColumn(new Column(key, ColumnValue.fromLong((Integer) value)));
                    } else if (field.getType().getSimpleName().equals("Long")) {
                        if (exceedRequestMaxLength(client, rowPutChange, stringBuilder, value, obj)) {
                            return 1;
                        }
                        rowPutChange.addColumn(new Column(key, ColumnValue.fromLong((Long) value)));
                    } else if (field.getType().getSimpleName().equals("Double")) {
                        if (exceedRequestMaxLength(client, rowPutChange, stringBuilder, value, obj)) {
                            return 1;
                        }
                        rowPutChange.addColumn(new Column(key, ColumnValue.fromDouble(Double.valueOf(value.toString()))));
                    }  else if (field.getType().getSimpleName().equals("Boolean")) {
                        if (exceedRequestMaxLength(client, rowPutChange, stringBuilder, value, obj)) {
                            return 1;
                        }
                        rowPutChange.addColumn(new Column(key, ColumnValue.fromBoolean(Boolean.valueOf(value.toString()))));
                    } else if (field.getType().getSimpleName().equals("Date"))  {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String format = sdf.format(value);
                        if (exceedRequestMaxLength(client, rowPutChange, stringBuilder, format, obj)) {
                            return 1;
                        }
                        rowPutChange.addColumn(new Column(key, ColumnValue.fromString(format)));
                    } else { // json数组，ots只支持json数组的嵌套数据类型，需要把驼峰法转成下划线再入库
                        String text = JSON.toJSONString(value, serializeConfig);
                        if (text.matches("^\\[.*\\]$")) {
                            List<String> columnValues = getColumnValues(text);
                            for (int i = 0; i < columnValues.size(); i++) {
                                String tempValue = columnValues.get(i);
                                if (exceedRequestMaxLength(client, rowPutChange, stringBuilder, tempValue, obj)) {
                                    return 1;
                                }
                                String columnName = (i == 0) ? key : (key + i);
                                rowPutChange.addColumn(new Column(columnName, ColumnValue.fromString(tempValue)));
                            }
                        }
                    }
                }
            }

            PutRowResponse putRowResponse = client.putRow(new PutRowRequest(rowPutChange));
            num = 1;
        }
        return num;
    }

    /**
     * 是否超出最大每次请求（新增/更新）最大4M，换成长度为 699050 * 2
     * @param stringBuilder
     * @param value
     * @return
     */
    private static boolean exceedRequestMaxLength(SyncClient client, RowPutChange rowPutChange, StringBuilder stringBuilder, Object value, Object obj) {
        stringBuilder.append(value);
        if (stringBuilder.length() > requestMaxLength) {        // 超过了限制，则先新增，后面再分批更新
            rowPutChange.getColumnsToPut().clear();
            PutRowResponse putRowResponse = client.putRow(new PutRowRequest(rowPutChange));
            update(obj);
            return true;
        }
        return false;
    }

    /**
     * 更新不为空的字段
     * @param obj   实体类对象实例
     * @return
     */
    public static int update(Object obj) {
        // 获取表的配置信息
        TableInfo aliasBasicInfo = CommonUtils.getTableInfo(obj);
        SyncClient client = Store.getInstance().getSyncClient();


        Map<String, Field> fieldMap = CommonUtils.getFieldMap(obj.getClass());

        // 1、构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        List<String> primaryKeyList = aliasBasicInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            Field field = fieldMap.get(CommonUtils.underlineToHump(key));
            Object value = CommonUtils.getFieldValue(field, obj);
            if (value.getClass().getSimpleName().equals("Long")) {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value));
            } else {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value));
            }
        }
        PrimaryKey primaryKey = primaryKeyBuilder.build();
        RowUpdateChange rowUpdateChange = new RowUpdateChange(aliasBasicInfo.getTableName(), primaryKey);

        // 2、设置其他属性
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Field> map : fieldMap.entrySet()) {
            String key = map.getKey();
            Field field = map.getValue();
            Object value = CommonUtils.getFieldValue(field, obj);
            key = CommonUtils.humpToUnderline(key);     // 驼峰转下划线
            if (!primaryKeyList.contains(key) && value != null) {       // 非主键 非空 判断
                if (value.getClass().getSimpleName().equals("String")) {      // 待完善 其他类型当成字符串处理，目前是够用的
                    if (exceedRequestMaxLengthForUpdate(client, rowUpdateChange, stringBuilder, value)) {   // 如果达到了最高请求，则重置
                        rowUpdateChange = new RowUpdateChange(aliasBasicInfo.getTableName(), primaryKey);
                        stringBuilder = new StringBuilder();
                    }
                    rowUpdateChange.put(new Column(key, ColumnValue.fromString((String) value)));
                } else if (value.getClass().getSimpleName().equals("Integer")) {
                    if (exceedRequestMaxLengthForUpdate(client, rowUpdateChange, stringBuilder, value)) {   // 如果达到了最高请求，则重置
                        rowUpdateChange = new RowUpdateChange(aliasBasicInfo.getTableName(), primaryKey);
                        stringBuilder = new StringBuilder();
                    }
                    rowUpdateChange.put(new Column(key, ColumnValue.fromLong((Integer) value)));
                } else if (value.getClass().getSimpleName().equals("Long")) {      // 待完善 其他类型当成字符串处理，目前是够用的
                    if (exceedRequestMaxLengthForUpdate(client, rowUpdateChange, stringBuilder, value)) {   // 如果达到了最高请求，则重置
                        rowUpdateChange = new RowUpdateChange(aliasBasicInfo.getTableName(), primaryKey);
                        stringBuilder = new StringBuilder();
                    }
                    rowUpdateChange.put(new Column(key, ColumnValue.fromLong((Long) value)));
                } else if (value.getClass().getSimpleName().equals("Double")) {
                    if (exceedRequestMaxLengthForUpdate(client, rowUpdateChange, stringBuilder, value)) {   // 如果达到了最高请求，则重置
                        rowUpdateChange = new RowUpdateChange(aliasBasicInfo.getTableName(), primaryKey);
                        stringBuilder = new StringBuilder();
                    }
                    rowUpdateChange.put(new Column(key, ColumnValue.fromDouble(Double.valueOf(value.toString()))));
                } else if (value.getClass().getSimpleName().equals("Boolean")) {
                    if (exceedRequestMaxLengthForUpdate(client, rowUpdateChange, stringBuilder, value)) {   // 如果达到了最高请求，则重置
                        rowUpdateChange = new RowUpdateChange(aliasBasicInfo.getTableName(), primaryKey);
                        stringBuilder = new StringBuilder();
                    }
                    rowUpdateChange.put(new Column(key, ColumnValue.fromBoolean(Boolean.valueOf(value.toString()))));
                } else if (value.getClass().getSimpleName().equals("Date"))  {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String format = sdf.format(value);

                    if (exceedRequestMaxLengthForUpdate(client, rowUpdateChange, stringBuilder, format)) {   // 如果达到了最高请求，则重置
                        rowUpdateChange = new RowUpdateChange(aliasBasicInfo.getTableName(), primaryKey);
                        stringBuilder = new StringBuilder();
                    }
                    rowUpdateChange.put(new Column(key, ColumnValue.fromString(format)));
                } else { // json数组，ots只支持json数组的嵌套数据类型，需要把驼峰法转成下划线再入库
                    String text = JSON.toJSONString(value, serializeConfig);
                    if (text.matches("^\\[.*\\]$")) {
                        // 手动请处理存在的列
                        deleteColumn(rowUpdateChange, key);
                        // 再存入
                        List<String> columnValues = getColumnValues(text);
                        for (int i = 0; i < columnValues.size(); i++) {
                            String columnName = (i == 0) ? key : (key + i);
                            String tempValue = columnValues.get(i);
                            if (exceedRequestMaxLengthForUpdate(client, rowUpdateChange, stringBuilder, tempValue)) {   // 如果达到了最高请求，则重置
                                rowUpdateChange = new RowUpdateChange(aliasBasicInfo.getTableName(), primaryKey);
                                stringBuilder = new StringBuilder();
                            }
                            rowUpdateChange.put(new Column(columnName, ColumnValue.fromString(tempValue)));
                        }
                    }
                }
            }
        }

        // 2、更新（最后一次）
        int num = 0;
        List<Pair<Column, RowUpdateChange.Type>> columnsToUpdate = rowUpdateChange.getColumnsToUpdate();
        if (columnsToUpdate != null && columnsToUpdate.size() > 0) {
            Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);
            rowUpdateChange.setCondition(condition);

            try {
                UpdateRowResponse updateRowResponse = client.updateRow(new UpdateRowRequest(rowUpdateChange));
                num = 1;
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
     * 删除字段
     * @param obj           实体类对象实例
     * @param columnNames    需要被删除的字段集合
     * @return
     */
    public static int deleteColumns(Object obj, List<String> columnNames) {
        // 获取表的配置信息
        TableInfo aliasBasicInfo = CommonUtils.getTableInfo(obj);
        SyncClient client = Store.getInstance().getSyncClient();


        JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);

        // 1、构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        List<String> primaryKeyList = aliasBasicInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            Object value = jsonObject.get(CommonUtils.underlineToHump(key));
            if (value.getClass().getSimpleName().equals("Long")) {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value));
            } else {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value));
            }
        }
        PrimaryKey primaryKey = primaryKeyBuilder.build();
        RowUpdateChange rowUpdateChange = new RowUpdateChange(aliasBasicInfo.getTableName(), primaryKey);
        // 构建需要删除的字段
        for (String columnName : columnNames) {
            rowUpdateChange.deleteColumns(columnName);
        }

        // 2、更新
        int num = 0;
        List<Pair<Column, RowUpdateChange.Type>> columnsToUpdate = rowUpdateChange.getColumnsToUpdate();
        if (columnsToUpdate != null && columnsToUpdate.size() > 0) {
            Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);
            rowUpdateChange.setCondition(condition);

            try {
                UpdateRowResponse updateRowResponse = client.updateRow(new UpdateRowRequest(rowUpdateChange));
                num = 1;
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
     * 是否超出最大每次请求（更新）最大4M，换成长度为 699050 * 2
     * @param stringBuilder
     * @param value
     * @return
     */
    private static boolean exceedRequestMaxLengthForUpdate(SyncClient client, RowUpdateChange rowUpdateChange, StringBuilder stringBuilder, Object value) {
        stringBuilder.append(value);
        if (stringBuilder.length() > requestMaxLength) {        // 超过了限制，则请求一次
            Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);
            rowUpdateChange.setCondition(condition);
            try {
                UpdateRowResponse updateRowResponse = client.updateRow(new UpdateRowRequest(rowUpdateChange));
            }catch (TableStoreException e){
                if ("OTSConditionCheckFail".equals(e.getErrorCode())) {     // 期望不一致返回 num=0即可

                } else {
                    e.printStackTrace();
                    throw e;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 获取 {@link RowUpdateChange}  行的更新对象（只会更新不为空的字段）
     * @param obj            实体类对象实例
     * @param tableInfo    {@link TableInfo}i
     * @return
     */
    private static RowUpdateChange getRowUpdateChange(Object obj, TableInfo tableInfo) {
        Map<String, Field> fieldMap = CommonUtils.getFieldMap(obj.getClass());

        // 1、构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        List<String> primaryKeyList = tableInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            Field field = fieldMap.get(CommonUtils.underlineToHump(key));
            Object value = CommonUtils.getFieldValue(field, obj);
            if (value.getClass().getSimpleName().equals("Long")) {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value));
            } else {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value));
            }
        }
        PrimaryKey primaryKey = primaryKeyBuilder.build();

        // 2、设置其他属性
        RowUpdateChange rowUpdateChange = new RowUpdateChange(tableInfo.getTableName(), primaryKey);
        for (Map.Entry<String, Field> map : fieldMap.entrySet()) {
            String key = map.getKey();
            Field field = map.getValue();
            Object value = CommonUtils.getFieldValue(field, obj);
            key = CommonUtils.humpToUnderline(key);     // 驼峰转下划线
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
                } else { // json数组，ots只支持json数组的嵌套数据类型，需要把驼峰法转成下划线再入库
                    String text = JSON.toJSONString(value, serializeConfig);
                    if (text.matches("^\\[.*\\]$")) {
                        // 手动请处理存在的列
                        deleteColumn(rowUpdateChange, key);
                        // 再存入
                        List<String> columnValues = getColumnValues(text);
                        for (int i = 0; i < columnValues.size(); i++) {
                            String columnName = (i == 0) ? key : (key + i);
                            rowUpdateChange.put(new Column(columnName, ColumnValue.fromString(columnValues.get(i))));
                        }
                    }
                }
            }
        }
        Condition condition = new Condition(RowExistenceExpectation.EXPECT_EXIST);
        rowUpdateChange.setCondition(condition);
        return rowUpdateChange;
    }

    /**
     * 获取拆分后的值（因为ots每个字段最多存2M（以长度 699050 为拆分），所以做了拆分，拆分规则 columnName/columnName1/columnName2 ... columnName9 最多拆成10列）
     * @param value
     */
    private static List<String> getColumnValues(String value) {
        int maxLength = columnMaxLength * 10;
        int length = value.length();

        List<String> result = new ArrayList<>();
        if (length <= maxLength) {   // 必须小于等于10列才会存储，否则放弃
            int size = (length % columnMaxLength == 0) ? (length / columnMaxLength) : (length / columnMaxLength + 1);
            for (int i = 0; i < size; i++) {
                int start = i * columnMaxLength;
                int end = start + columnMaxLength;
                if (end > length) {
                    result.add(value.substring(start, length));
                } else {
                    result.add(value.substring(start, end));
                }
            }
        }
        return result;
    }

    /**
     * 删除拆分的列（因为ots每个字段最多存2M，所以做了拆分，拆分规则 columnName/columnName1/columnName2 ... columnName9 最多拆成10列）
     * @param rowUpdateChange
     * @param columnName
     */
    private static void deleteColumn(RowUpdateChange rowUpdateChange, String columnName) {
        rowUpdateChange.deleteColumns(columnName);
        for (int i = 1; i < 10; i++) {
            rowUpdateChange.deleteColumns(columnName + i);
        }
    }


    /**
     * 获取 {@link RowPutChange}  行的新增对象
     * @param obj            实体类对象实例
     * @param tableInfo    {@link TableInfo}
     * @return
     */
    private static RowPutChange getRowPutChange(Object obj, TableInfo tableInfo) {
        Map<String, Field> fieldMap = CommonUtils.getFieldMap(obj.getClass());

        // 1、构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        List<String> primaryKeyList = tableInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            Field field = fieldMap.get(CommonUtils.underlineToHump(key));
            Object value = CommonUtils.getFieldValue(field, obj);
            if (value.getClass().getSimpleName().equals("Long")) {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value));
            } else {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value));
            }
        }
        PrimaryKey primaryKey = primaryKeyBuilder.build();

        // 2、设置其他属性
        RowPutChange rowPutChange = new RowPutChange(tableInfo.getTableName(), primaryKey);
        for (Map.Entry<String, Field> map : fieldMap.entrySet()) {
            String key = map.getKey();
            Field field = map.getValue();
            Object value = CommonUtils.getFieldValue(field, obj);
            key = CommonUtils.humpToUnderline(key);     // 驼峰转下划线
            if (!primaryKeyList.contains(key) && value != null) {       // 非主键 非空 判断
                if (value.getClass().getSimpleName().equals("String")) {      // 待完善 其他类型当成字符串处理，目前是够用的
                    rowPutChange.addColumn(new Column(key, ColumnValue.fromString((String) value)));
                } else if (value.getClass().getSimpleName().equals("Integer")) {
                    rowPutChange.addColumn(new Column(key, ColumnValue.fromLong((Integer) value)));
                } else if (value.getClass().getSimpleName().equals("Long")) {      // 待完善 其他类型当成字符串处理，目前是够用的
                    rowPutChange.addColumn(new Column(key, ColumnValue.fromLong((Long) value)));
                } else if (value.getClass().getSimpleName().equals("Double")) {
                    rowPutChange.addColumn(new Column(key, ColumnValue.fromDouble(Double.valueOf(value.toString()))));
                } else if (value.getClass().getSimpleName().equals("Boolean")) {
                    rowPutChange.addColumn(new Column(key, ColumnValue.fromBoolean(Boolean.valueOf(value.toString()))));
                } else if (value.getClass().getSimpleName().equals("Date"))  {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String format = sdf.format(value);
                    rowPutChange.addColumn(new Column(key, ColumnValue.fromString(format)));
                } else { // json数组，ots只支持json数组的嵌套数据类型，需要把驼峰法转成下划线再入库
                    String text = JSON.toJSONString(value, serializeConfig);
                    if (text.matches("^\\[.*\\]$")) {
                        List<String> columnValues = getColumnValues(text);
                        for (int i = 0; i < columnValues.size(); i++) {
                            String columnName = (i == 0) ? key : (key + i);
                            rowPutChange.addColumn(new Column(columnName, ColumnValue.fromString(columnValues.get(i))));
                        }
                    }
                }
            }
        }
        return rowPutChange;
    }

    /**
     * 获取 {@link RowDeleteChange}  行的删除对象
     * @param obj           实体类对象实例
     * @param tableInfo    {@link TableInfo}
     * @return
     */
    private static RowDeleteChange getRowDeleteChange(Object obj, TableInfo tableInfo) {
        JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);

        // 1、构造主键
        PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        List<String> primaryKeyList = tableInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            Object value = jsonObject.get(CommonUtils.underlineToHump(key));
            if (value.getClass().getSimpleName().equals("Long")) {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value));
            } else {
                primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value));
            }
        }
        PrimaryKey primaryKey = primaryKeyBuilder.build();

        RowDeleteChange rowDeleteChange = new RowDeleteChange(tableInfo.getTableName(), primaryKey);
        return rowDeleteChange;
    }

    /**
     * 批量新增
     * @param list  实体类对象实例集合
     * @return
     */
    public static int batchInsert(List list) {
        int num = 0;
        if (list != null) {
            List<List> batches = Lists.partition(list, 200);
            for (List batch : batches) {
                num += batchWriteFor200(batch, false);
            }
        }
        return num;
    }

    /**
     * 根据主键批量更新
     * @param list  实体类对象实例集合
     * @return
     */
    public static int batchUpdate(List list) {
        int num = 0;
        if (list != null && list.size() > 0) {
            List<List> batches = Lists.partition(list, 200);
            for (List batch : batches) {
                num += batchWriteFor200(batch, true);
            }
        }
        return num;
    }

    /**
     * 批量更新不为空的字段（ps：一次不能超过200）
     * @param list    实体类对象实例集合
     * @param update  是否为更新操作，否则为新增操作
     * @return
     */
    private static int batchWriteFor200(List list, boolean update) {
        int num = 0;
        if (list != null && list.size() > 0) {
            // 获取表的配置信息
            TableInfo basicInfo = CommonUtils.getTableInfo(list.get(0));
            SyncClient client = Store.getInstance().getSyncClient();

            // 1、构建的对象
            BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest();
            for (Object obj : list) {
                RowChange rowChange = null;
                if (update) {
                    rowChange = getRowUpdateChange(obj, basicInfo);
                } else {
                    rowChange = getRowPutChange(obj, basicInfo);
                }
                batchWriteRowRequest.addRowChange(rowChange);
            }

            // 2、更新
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
     * @param list  实体类对象实例集合
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
     * @param list    实体类对象实例集合
     * @return
     */
    private static int batchDeleteFor200(List list) {
        int num = 0;
        if (list != null && list.size() > 0) {
            // 获取表的配置信息
            TableInfo tableInfo = CommonUtils.getTableInfo(list.get(0));
            SyncClient client = Store.getInstance().getSyncClient();

            // 1、构建删除对象
            BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest();
            for (Object obj : list) {
                RowDeleteChange rowDeleteChange = getRowDeleteChange(obj, tableInfo);
                batchWriteRowRequest.addRowChange(rowDeleteChange);
            }

            // 2、删除
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
     * @param obj   实体类对象实例
     */
    public static int delete(Object obj) {
        // 获取表的配置信息
        TableInfo tableInfo = CommonUtils.getTableInfo(obj);
        SyncClient client = Store.getInstance().getSyncClient();

        // 1、构建删除对象
        RowDeleteChange rowDeleteChange = getRowDeleteChange(obj, tableInfo);


        // 2、删除
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
     * @param list           实体类对象实例集合
     * @param clazz          实体类
     * @param columnsToGet  添加要读取的列集合（默认查全部）
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
     * 通过主键批量获取
     * @param list           实体类对象实例集合
     * @param clazz          实体类
     * @param <T>
     * @return
     */
    public static <T> List<T> batchGetRow(List<T> list , Class<T> clazz) {
        return batchGetRow(list, clazz, null);
    }

    /**
     * 通过主键批量获取（list最多100，否则报错）
     * @param list           实体类对象实例集合
     * @param clazz          实体类
     * @param columnsToGet  添加要读取的列集合（默认查全部）
     * @param <T>
     * @return
     */
    private static <T> List<T> batchGetRowFor100(List<T> list , Class<T> clazz, Collection<String> columnsToGet) {
        // 获取表的配置信息
        TableInfo tableInfo = CommonUtils.getTableInfo(list.get(0));
        SyncClient client = Store.getInstance().getSyncClient();

        // 1、构造主键
        MultiRowQueryCriteria multiRowQueryCriteria = new MultiRowQueryCriteria(tableInfo.getTableName());
        for (Object obj : list) {
            JSONObject jsonObject = (JSONObject) JSON.toJSON(obj);
            PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
            List<String> primaryKeyList = tableInfo.getPrimaryKey();
            for (String key : primaryKeyList) {
                Object value = jsonObject.get(CommonUtils.underlineToHump(key));
                if (value.getClass().getSimpleName().equals("Long")) {
                    primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) value));
                } else {
                    primaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) value));
                }
            }
            PrimaryKey primaryKey = primaryKeyBuilder.build();
            multiRowQueryCriteria.addRow(primaryKey);
        }

        // 2、去最大版本，还有需要获取的字段
        BatchGetRowRequest batchGetRowRequest = new BatchGetRowRequest();
        // batchGetRow支持读取多个表的数据， 一个multiRowQueryCriteria对应一个表的查询条件，可以添加多个multiRowQueryCriteria.
        multiRowQueryCriteria.setMaxVersions(1);
        if (columnsToGet != null && columnsToGet.size() > 0) {
            multiRowQueryCriteria.addColumnsToGet(columnsToGet);
        }
        batchGetRowRequest.addMultiRowQueryCriteria(multiRowQueryCriteria);

        // 3、获取
        BatchGetRowResponse batchGetRowResponse = client.batchGetRow(batchGetRowRequest);
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
                result.add(CommonUtils.rowToEntity(row, clazz));
            }
        }
        return result;
    }

    /**
     * 根据主键获取一行记录
     * @param entity    实体类对象实例
     * @param clazz     实体类
     * @param <T>
     * @return
     */
    public static <T> T get(T entity, Class<T> clazz) {
        return get(entity, clazz, null);
    }

    /**
     * 根据主键获取一行记录
     * @param entity    实体类对象实例
     * @param clazz     实体类
     * @param columnsToGet  添加要读取的列集合（默认查全部）
     * @param <T>
     * @return
     */
    public static <T> T get(T entity, Class<T> clazz, Collection<String> columnsToGet) {
        T t = null;
        if (entity != null) {
            List<T> list = new ArrayList<>();
            list.add(entity);
            List<T> result = batchGetRow(list, clazz, columnsToGet);
            if (result != null && result.size() > 0) {
                t = result.get(0);
            }
        }
        return t;
    }

    /**
     * 根据二级索引查找（ps：只支持一个，即第一个不为空的二级索引）
     * 因为其他字段没有，所以反查主表
     * @param entity         实体类对象实例
     * @param clazz          实体类
     * @param columnsToGet  添加要读取的列集合（默认查全部）
     * @param <T>
     * @return
     */
    public static <T> List<T> searchBysecondaryIndex(T entity,  Class<T> clazz, Collection<String> columnsToGet){
        return searchBysecondaryIndex(entity, clazz, columnsToGet, 0);
    }


    /**
     * 根据二级索引查找（ps：只支持一个，即第一个不为空的二级索引）
     * 因为其他字段没有，所以反查主表
     * @param entity            实体类对象实例
     * @param clazz             实体类
     * @param columnsToGet     添加要读取的列集合
     * @param limit             获取的条数（如果小1则全查）
     * @param <T>
     * @return
     */
    public static <T> List<T> searchBysecondaryIndex(T entity,  Class<T> clazz, Collection<String> columnsToGet, int limit) {
        List<T> result = new LinkedList<>();
        // 获取表的配置信息
        TableInfo tableInfo = CommonUtils.getTableInfo(clazz);
        SyncClient client = Store.getInstance().getSyncClient();
        JSONObject jsonObject = (JSONObject) JSON.toJSON(entity);


        // 1、设置起始主键/结束主键
        PrimaryKeyBuilder startPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        PrimaryKeyBuilder endPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();

        // 1.1、设置二级索引表PK（查询时 二级索引表的PK要放在主表之前，要不然会报错）
        RangeRowQueryCriteria rangeRowQueryCriteria = null;
        List<String> secondaryIndexList = tableInfo.getSecondaryIndex();
        String secondaryIndexName = "";     // 当前需要查询的二级索引
        for (String secondaryIndex : secondaryIndexList) {
            String key = secondaryIndex.replace(tableInfo.getTableName() + "_index2_", "");
            if (secondaryIndex.equals(key)) {
                key = secondaryIndex.replace(tableInfo.getTableName() + "_", "");
            }
            Object value = jsonObject.get(CommonUtils.underlineToHump(key));
            if (value != null) {
                String simpleName = value.getClass().getSimpleName();
                if (simpleName.equals("Long") || simpleName.equals("Integer")) {
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

        // 1.2、设置主表PK（任意范围）
        List<String> primaryKeyList = tableInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            if (!key.equals(secondaryIndexName)) {  // 排除当前正在查询的二级索引
                startPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.INF_MIN); // 主表PK最小值。
                endPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.INF_MAX); // 主表PK最大值。
            }
        }

        // 2、查询
        if (rangeRowQueryCriteria != null) {
            rangeRowQueryCriteria.setInclusiveStartPrimaryKey(startPrimaryKeyBuilder.build());  // 开始主键
            rangeRowQueryCriteria.setExclusiveEndPrimaryKey(endPrimaryKeyBuilder.build());      // 结束主键
            rangeRowQueryCriteria.setMaxVersions(1);                                            // 设置读取最新版本
            if (limit > 0) {                                                                    // 默认一次扫描5000，如果有指定limit，设置成1000
                rangeRowQueryCriteria.setLimit(1000);
            }

            // 2.1、循环扫描（因为数据是分区的，所以需要分区扫描）
            boolean completed = false;
            long index = 0;
            while (true) {
                log.debug("第{}获取，准备获取数量={}", ++index, rangeRowQueryCriteria.getLimit());
                GetRangeResponse getRangeResponse = client.getRange(new GetRangeRequest(rangeRowQueryCriteria));
                List<Row> rows = getRangeResponse.getRows();
                log.debug("第{}获取，成功获取的数量={}", index, rows.size());
                // 1、处理 limit
                if (limit > 0) {
                    for (Row row : rows) {
                        if (result.size() >= limit) {
                            completed = true;
                            break;
                        } else {
                            result.add(CommonUtils.rowToEntity(row, clazz));
                        }
                    }
                } else {
                    for (Row row : rows) {
                        result.add(CommonUtils.rowToEntity(row, clazz));
                    }
                }

                // 若nextStartPrimaryKey不为null,可能扫描到分表的末尾了，则继续读取。
                if (getRangeResponse.getNextStartPrimaryKey() != null && !completed) {
                    rangeRowQueryCriteria.setInclusiveStartPrimaryKey(getRangeResponse.getNextStartPrimaryKey());
                } else {
                    break;
                }
            }
            // 2.2、反查主表的其他字段
            result = batchGetRow(result, clazz, columnsToGet);
        }
        return result;
    }


    /**
     * （测试用的）根据二级索引合计（ps：只支持一个，即第一个不为空的二级索引）
     * @param entity            实体类对象实例
     * @param clazz             实体类
     * @param <T>
     * @return
     */
    public static <T> int countBysecondaryIndex(T entity, Object start, Object end,  Class<T> clazz) {
        int num = 0;    // 测试用的
        // 获取表的配置信息
        TableInfo tableInfo = CommonUtils.getTableInfo(clazz);
        SyncClient client = Store.getInstance().getSyncClient();
        JSONObject jsonObject = (JSONObject) JSON.toJSON(entity);


        // 1、设置起始主键/结束主键
        PrimaryKeyBuilder startPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        PrimaryKeyBuilder endPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();

        // 1.1、设置二级索引表PK（查询时 二级索引表的PK要放在主表之前，要不然会报错）
        RangeRowQueryCriteria rangeRowQueryCriteria = null;
        List<String> secondaryIndexList = tableInfo.getSecondaryIndex();
        String secondaryIndexName = "";     // 当前需要查询的二级索引
        for (String secondaryIndex : secondaryIndexList) {
            String key = secondaryIndex.replace(tableInfo.getTableName() + "_index2_", "");
            if (secondaryIndex.equals(key)) {
                key = secondaryIndex.replace(tableInfo.getTableName() + "_", "");
            }
            Object value = jsonObject.get(CommonUtils.underlineToHump(key));
            if (value != null) {
                String simpleName = value.getClass().getSimpleName();
                if (simpleName.equals("Long") || simpleName.equals("Integer")) {
                    startPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) start)); // 索引表PK最小值。
                    endPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) end)); // 索引表PK最大值。
                } else {
                    startPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) start)); // 索引表PK最小值。
                    endPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) end)); // 索引表PK最大值。
                }
                rangeRowQueryCriteria = new RangeRowQueryCriteria(secondaryIndex);
                secondaryIndexName = key;
                break;
            }
        }

        // 1.2、设置主表PK（任意范围）
        List<String> primaryKeyList = tableInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            if (!key.equals(secondaryIndexName)) {  // 排除当前正在查询的二级索引
                startPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.INF_MIN); // 主表PK最小值。
                endPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.INF_MAX); // 主表PK最大值。
            }
        }

        // 2、查询
        if (rangeRowQueryCriteria != null) {
            rangeRowQueryCriteria.setInclusiveStartPrimaryKey(startPrimaryKeyBuilder.build());  // 开始主键
            rangeRowQueryCriteria.setExclusiveEndPrimaryKey(endPrimaryKeyBuilder.build());      // 结束主键
            rangeRowQueryCriteria.setMaxVersions(1);                                            // 设置读取最新版本

            // 2.1、循环扫描（因为数据是分区的，所以需要分区扫描）
            boolean completed = false;
            long index = 0;
            while (true) {
                log.warn("第{}获取，准备{}->{},获取数量={}", ++index, start, end, rangeRowQueryCriteria.getLimit());
                GetRangeResponse getRangeResponse = client.getRange(new GetRangeRequest(rangeRowQueryCriteria));
                List<Row> rows = getRangeResponse.getRows();
                num += rows.size();
                log.warn("第{}获取，成功获取的数量={},num={}", index, rows.size(),num);

                // 若nextStartPrimaryKey不为null，则继续读取。
                if (getRangeResponse.getNextStartPrimaryKey() != null && !completed) {
                    rangeRowQueryCriteria.setInclusiveStartPrimaryKey(getRangeResponse.getNextStartPrimaryKey());
                } else {
                    break;
                }
            }
        }
        return num;
    }




    /**
     * 根据二级索引查找（ps：只支持一个，即第一个不为空的二级索引，此方法不会反查主表，如果需要反查请用 searchBysecondaryIndex ）
     * @param entity            实体类对象实例
     * @param start             二级索引表PK最小值
     * @param end               二级索引表PK最小值
     * @param clazz             实体类
     * @param columnsToGet     添加要读取的列集合
     * @param limit             获取的条数（如果小1则全查）
     * @param <T>
     * @return
     */
    public static <T> List<T> getRangeBysecondaryIndex(T entity, Object start, Object end,  Class<T> clazz, Collection<String> columnsToGet, int limit) {
        List<T> result = new LinkedList<>();
        // 获取表的配置信息
        TableInfo tableInfo = CommonUtils.getTableInfo(clazz);
        SyncClient client = Store.getInstance().getSyncClient();
        JSONObject jsonObject = (JSONObject) JSON.toJSON(entity);


        // 1、设置起始主键/结束主键
        PrimaryKeyBuilder startPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        PrimaryKeyBuilder endPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();

        // 1.1、设置二级索引表PK（查询时 二级索引表的PK要放在主表之前，要不然会报错）
        RangeRowQueryCriteria rangeRowQueryCriteria = null;
        List<String> secondaryIndexList = tableInfo.getSecondaryIndex();
        String secondaryIndexName = "";     // 当前需要查询的二级索引
        for (String secondaryIndex : secondaryIndexList) {
            String key = secondaryIndex.replace(tableInfo.getTableName() + "_index2_", "");
            if (secondaryIndex.equals(key)) {
                key = secondaryIndex.replace(tableInfo.getTableName() + "_", "");
            }
            Object value = jsonObject.get(CommonUtils.underlineToHump(key));
            if (value != null) {
                String simpleName = value.getClass().getSimpleName();
                if (simpleName.equals("Long") || simpleName.equals("Integer")) {
                    startPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) start)); // 索引表PK最小值。
                    endPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromLong((Long) end)); // 索引表PK最大值。
                } else {
                    startPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) start)); // 索引表PK最小值。
                    endPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.fromString((String) end)); // 索引表PK最大值。
                }
                rangeRowQueryCriteria = new RangeRowQueryCriteria(secondaryIndex);
                secondaryIndexName = key;
                break;
            }
        }

        // 1.2、设置主表PK（任意范围）
        List<String> primaryKeyList = tableInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            if (!key.equals(secondaryIndexName)) {  // 排除当前正在查询的二级索引
                startPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.INF_MIN); // 主表PK最小值。
                endPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.INF_MAX); // 主表PK最大值。
            }
        }

        // 2、查询
        if (rangeRowQueryCriteria != null) {
            rangeRowQueryCriteria.setInclusiveStartPrimaryKey(startPrimaryKeyBuilder.build());  // 开始主键
            rangeRowQueryCriteria.setExclusiveEndPrimaryKey(endPrimaryKeyBuilder.build());      // 结束主键
            rangeRowQueryCriteria.setMaxVersions(1);                                            // 设置读取最新版本
            if (columnsToGet != null && columnsToGet.size() > 0) {                              // 指定获取的列
                rangeRowQueryCriteria.addColumnsToGet(columnsToGet);
            }

            if (limit > 0) {                                                                    // 默认一次扫描5000，如果有指定limit，设置成1000
                rangeRowQueryCriteria.setLimit(1000);
            }

            // 2.1、循环扫描（因为数据是分区的，所以需要分区扫描）
            boolean completed = false;
            long index = 0;
            while (true) {
                log.debug("第{}获取,{}->{}，准备获取数量={}", ++index, start, end, rangeRowQueryCriteria.getLimit());
                GetRangeRequest getRangeRequest = new GetRangeRequest(rangeRowQueryCriteria);
                GetRangeResponse getRangeResponse = client.getRange(getRangeRequest);
                List<Row> rows = getRangeResponse.getRows();
                log.debug("第{}获取，成功获取的数量={}", index, rows.size());
                // 1、处理 limit
                if (limit > 0) {
                    for (Row row : rows) {
                        if (result.size() >= limit) {
                            completed = true;
                            break;
                        } else {
                            result.add(CommonUtils.rowToEntity(row, clazz));
                        }
                    }
                } else {
                    for (Row row : rows) {
                        result.add(CommonUtils.rowToEntity(row, clazz));
                    }
                }

                // 若nextStartPrimaryKey不为null,可能扫描到分表的末尾了，则继续读取。
                if (getRangeResponse.getNextStartPrimaryKey() != null && !completed) {
                    rangeRowQueryCriteria.setInclusiveStartPrimaryKey(getRangeResponse.getNextStartPrimaryKey());
                } else {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 根据一级索引查找所有
     * @param clazz             实体类
     * @param columnsToGet     添加要读取的列集合
     * @param limit             获取的条数（如果小1则全查）
     * @param <T>
     * @return
     */
    public static <T> List<T> getRangeByPrimaryKey(Class<T> clazz, Collection<String> columnsToGet, int limit) {
        // 获取表的配置信息
        TableInfo tableInfo = CommonUtils.getTableInfo(clazz);
        SyncClient client = Store.getInstance().getSyncClient();

        // 1、设置主表PK（任意范围）
        PrimaryKeyBuilder startPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        PrimaryKeyBuilder endPrimaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();

        List<String> primaryKeyList = tableInfo.getPrimaryKey();
        for (String key : primaryKeyList) {
            startPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.INF_MIN); // 主表PK最小值。
            endPrimaryKeyBuilder.addPrimaryKeyColumn(key, PrimaryKeyValue.INF_MAX); // 主表PK最大值。
        }

        // 2、查询
        RangeRowQueryCriteria rangeRowQueryCriteria = new RangeRowQueryCriteria(tableInfo.getTableName());
        rangeRowQueryCriteria.setInclusiveStartPrimaryKey(startPrimaryKeyBuilder.build());  // 开始主键
        rangeRowQueryCriteria.setExclusiveEndPrimaryKey(endPrimaryKeyBuilder.build());      // 结束主键
        rangeRowQueryCriteria.setMaxVersions(1);                                            // 设置读取最新版本
        if (columnsToGet != null && columnsToGet.size() > 0) {                              // 指定获取的列
            rangeRowQueryCriteria.addColumnsToGet(columnsToGet);
        }

        if (limit > 0) {                                                                    // 默认一次扫描5000，如果有指定limit，设置成1000
            rangeRowQueryCriteria.setLimit(1000);
        }

        // 2.1、循环扫描（因为数据是分区的，所以需要分区扫描）
        List<T> result = new LinkedList<>();
        boolean completed = false;
        long index = 0;
        while (true) {
            log.debug("第{}获取,{}->{}，准备获取数量={}", ++index, rangeRowQueryCriteria.getLimit());
            GetRangeRequest getRangeRequest = new GetRangeRequest(rangeRowQueryCriteria);
            GetRangeResponse getRangeResponse = client.getRange(getRangeRequest);
            List<Row> rows = getRangeResponse.getRows();
            log.debug("第{}获取，成功获取的数量={}", index, rows.size());
            // 1、处理 limit
            if (limit > 0) {
                for (Row row : rows) {
                    if (result.size() >= limit) {
                        completed = true;
                        break;
                    } else {
                        result.add(CommonUtils.rowToEntity(row, clazz));
                    }
                }
            } else {
                for (Row row : rows) {
                    result.add(CommonUtils.rowToEntity(row, clazz));
                }
            }

            // 若nextStartPrimaryKey不为null,可能扫描到分表的末尾了，则继续读取。
            if (getRangeResponse.getNextStartPrimaryKey() != null && !completed) {
                rangeRowQueryCriteria.setInclusiveStartPrimaryKey(getRangeResponse.getNextStartPrimaryKey());
            } else {
                break;
            }
        }
        return result;
    }



}
