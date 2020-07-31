package site.dunhanson.aliyun.tablestore.utils;

import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.PrimaryKeyColumn;
import com.alicloud.openservices.tablestore.model.Row;
import com.google.gson.Gson;
import site.dunhanson.aliyun.tablestore.constants.Constants;
import site.dunhanson.aliyun.tablestore.entity.TableInfo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * tablestore公用的工具
 */
public class CommonUtils {


    /**TableInfo Map集合**/
    private static Map<String, TableInfo> tableInfoMap = new HashMap<>();
    /**gson**/
    private static Gson gson = new Gson();

    /**
     * 获取表基础信息对象
     * @param alias     实体类的别名（例如： Document=document）
     * @return
     */
    public static TableInfo getTableInfo(String alias) {
        TableInfo tableInfo = tableInfoMap.get(alias);
        if (tableInfo == null) {
            tableInfo = new TableInfo();
            tableInfo.setTableName(YamlUtils.getValueToString(Constants.FILE_PATH, Constants.TABLE_STORE, Constants.TABLES, alias, Constants.TABLE_NAME));
            tableInfo.setPrimaryKey(YamlUtils.getValueToList(Constants.FILE_PATH, Constants.TABLE_STORE, Constants.TABLES, alias, Constants.PRIMARY_KEY));
            tableInfo.setSecondaryIndex(YamlUtils.getValueToList(Constants.FILE_PATH, Constants.TABLE_STORE, Constants.TABLES, alias, Constants.SECONDARY_INDEX));
            tableInfo.setIndexName(YamlUtils.getValueToList(Constants.FILE_PATH, Constants.TABLE_STORE, Constants.TABLES, alias, Constants.INDEX_NAME));
            tableInfo.setIgnoreColumn(YamlUtils.getValueToList(Constants.FILE_PATH, Constants.TABLE_STORE, Constants.TABLES, alias, Constants.IGNORE_COLUMN));

            // 设置全局配置参数
            tableInfo.setLimit(YamlUtils.getValueToInteger(Constants.FILE_PATH, Constants.TABLE_STORE, Constants.DEFAULT, Constants.LIMIT));
        }
        return tableInfo;
    }

    /**
     * 获取表基础信息对象
     * @param obj     实体类的对象
     * @return
     */
    public static TableInfo getTableInfo(Object obj) {
        return getTableInfo(getAlias(obj));
    }

    /**
     * 获取表基础信息对象
     * @param clazz     实体类
     * @return
     */
    public static TableInfo getTableInfo(Class clazz) {
        return getTableInfo(getAlias(clazz));
    }

    /**
     * 行转换成对象
     * @param row       {@link Row}
     * @param clazz     实体类
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
            if(name.contains(Constants.UNDERLINE)) {
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
            if(name.contains(Constants.UNDERLINE)) {
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
        String[] arr = text.split(Constants.UNDERLINE);
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
                stringBuilder.append(Constants.UNDERLINE + Character.toLowerCase(tempChar));
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
     * 根据对象获取别名（首字母小写）
     * @param obj
     * @return
     */
    public static String getAlias(Object obj) {
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
    public static List<String> getNoIgnoreColumns(Class clazz, List<String> ignoreColumns) {
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
    public static List<String> getNoIgnoreColumns(Class clazz, String...ignoreColumns) {
        return getNoIgnoreColumns(clazz, Arrays.asList(ignoreColumns));
    }

    /**
     * 获取没有忽略字段的字段集合
     * @param clazz
     * @return
     */
    public static List<String> getNoIgnoreColumns(Class clazz) {
        List<String> ignoreColumns = getTableInfo(getAlias(clazz)).getIgnoreColumn();
        return getNoIgnoreColumns(clazz, ignoreColumns);
    }


    /**
     * 获取类的所有字段名
     * @param clazz
     * @param humpToUnderline   是否需要 驼峰转下划线
     * @return
     */
    public static List<String> getAllFieldName(Class clazz, boolean humpToUnderline) {
        List<String> list = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields) {
            String name = field.getName();
            list.add(humpToUnderline ? humpToUnderline(name) : name);
        }
        return list;
    }





}
