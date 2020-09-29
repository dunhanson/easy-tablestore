package site.dunhanson.aliyun.tablestore.utils;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.SearchRequest;
import com.alicloud.openservices.tablestore.model.search.SearchResponse;
import com.alicloud.openservices.tablestore.model.search.query.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import site.dunhanson.aliyun.tablestore.entity.Page;
import site.dunhanson.aliyun.tablestore.entity.TableInfo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dunhanson
 * @date 2020.03.20
 * @description TableStore工具类（多元索引）
 */
@Slf4j
public class TableStoreMultipleIndexUtils {


    /**
     * 根据多元索引查询（默认查第一个多元索引，并且查全部字段）
     * @param query      {@link SearchQuery}
     * @param clazz       实体类
     * @param <T>
     * @return
     */
    public static <T> Page<T> search(SearchQuery query, Class<T> clazz) {
        return search(query, clazz, new ArrayList<>());
    }

    /**
     * 根据多元索引查询（默认查第一个多元索引）
     * @param query      {@link SearchQuery}
     * @param clazz             实体类
     * @param columns           需要获取的字段（为空集合时，查全部）
     * @param <T>
     * @return
     */
    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, List<String> columns) {
        return search(query, clazz, 0, columns);
    }

    /**
     * 根据多元索引查询（默认查第一个多元索引）
     * @param query             {@link SearchQuery}
     * @param clazz              实体类
     * @param ignoreColumns     需要忽略的字段集合
     * @param <T>
     * @return
     */
    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, String...ignoreColumns) {
        return search(query, clazz, CommonUtils.getNoIgnoreColumns(clazz, ignoreColumns));
    }

    /**
     * 根据多元索引查询（默认查第一个多元索引）
     * @param query            {@link SearchQuery}
     * @param clazz             实体类
     * @param ignoreColumn     是否需要根据 alias.ignoreColumn 配置的来忽略字段
     * @param <T>
     * @return
     */
    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, boolean ignoreColumn) {
        return ignoreColumn ? search(query, clazz, CommonUtils.getNoIgnoreColumns(clazz)) : search(query, clazz);
    }

    /**
     * 根据多元索引查询（默认查第一个多元索引，并且查全部字段）
     * @param query            {@link SearchQuery}
     * @param clazz             实体类
     * @param offset            分页起始下标（下标从0开始，比如[a,b,c,d,e] offset=2 limit=2 则结果为 [c,d]）
     * @param limit             分页大小，即返回的行数
     * @param <T>
     * @return
     */
    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, int offset, int limit) {
        return search(query, clazz, offset, limit, new ArrayList<>());
    }

    /**
     * 根据多元索引查询（默认查第一个多元索引）
     * @param query            {@link SearchQuery}
     * @param clazz             实体类
     * @param offset            分页起始下标（下标从0开始，比如[a,b,c,d,e] offset=2 limit=2 则结果为 [c,d]）
     * @param limit             分页大小，即返回的行数
     * @param columns           需要获取的字段（为空集合时，查全部）
     * @param <T>
     * @return
     */
    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, int offset, int limit, List<String> columns) {
        query.setOffset(offset);
        query.setLimit(limit);
        return search(query, clazz, 0, columns);
    }

    /**
     * 根据多元索引查询（默认查第一个多元索引）
     * @param query            {@link SearchQuery}
     * @param clazz             实体类
     * @param offset            分页起始下标（下标从0开始，比如[a,b,c,d,e] offset=2 limit=2 则结果为 [c,d]）
     * @param limit             分页大小，即返回的行数
     * @param ignoreColumns    需要忽略的字段集合
     * @param <T>
     * @return
     */
    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, int offset, int limit, String...ignoreColumns) {
        return search(query, clazz, offset, limit, CommonUtils.getNoIgnoreColumns(clazz, ignoreColumns));
    }

    /**
     * 根据多元索引查询（默认查第一个多元索引）
     * @param query            {@link SearchQuery}
     * @param clazz             实体类
     * @param offset            分页起始下标（下标从0开始，比如[a,b,c,d,e] offset=2 limit=2 则结果为 [c,d]）
     * @param limit             分页大小，即返回的行数
     * @param ignoreColumn     是否需要根据 alias.ignoreColumn 配置的来忽略字段
     * @param <T>
     * @return
     */
    public static <T> Page<T> search(SearchQuery query, Class<T> clazz, int offset, int limit, boolean ignoreColumn) {
        return ignoreColumn ? search(query, clazz, offset, limit, CommonUtils.getNoIgnoreColumns(clazz)) : search(query, clazz, offset, limit);
    }

    /**
     * 根据多元索引查询
     * @param searchQuery      {@link SearchQuery}
     * @param clazz             实体类
     * @param index             多元索引的下标（每个表建议配置一个多元索引即可）
     * @param columns           需要获取的字段（为空集合时，查全部）
     * @param <T>
     * @return
     */
    private static <T> Page<T> search(SearchQuery searchQuery, Class<T> clazz, int index, List<String> columns) {
        //开始时间
        LocalDateTime startTime = LocalDateTime.now();
        //基础信息
        TableInfo tableInfo = CommonUtils.getTableInfo(clazz);
        //获取总记录数
        searchQuery.setGetTotalCount(true);
        //分页数
        Integer limit = searchQuery.getLimit();
        if(limit == null) {
            limit = tableInfo.getLimit();
        }
        searchQuery.setLimit(limit);
        //偏移数
        Integer offset = searchQuery.getOffset();
        if(offset == null) {
            offset = 0;
        }
        searchQuery.setOffset(offset);
        //查询对象
        SearchRequest request = new SearchRequest(tableInfo.getTableName(), tableInfo.getIndexName().get(index), searchQuery);
        //返回字段
        SearchRequest.ColumnsToGet columnsToGet = new SearchRequest.ColumnsToGet();
        if(columns == null || columns.size() == 0) {
            columnsToGet.setReturnAll(true);
        } else {
            columnsToGet.setColumns(columns);
        }
        request.setColumnsToGet(columnsToGet);
        //查询并返回响应
        SyncClient client = Store.getInstance().getSyncClient();
        SearchResponse resp = client.search(request);
        //设置分页对象
        Page<T> page = getListFromSearchResponse(resp, clazz, limit);
        //设置偏移数
        page.setOffset(offset);
        //结束时间
        LocalDateTime endTime = LocalDateTime.now();
        //日志打印
        StringBuffer logStr = new StringBuffer();
        logStr.append("\n");
        logStr.append("---------------------- TableStore Search ----------------------");
        logStr.append("\n");
        logStr.append("Search：" + getQueryString(searchQuery.getQuery()));
        logStr.append("\n");
        logStr.append("ElapsedTime：" + ChronoUnit.MILLIS.between(startTime, endTime) + "ms");
        logStr.append("\n");
        logStr.append("---------------------- TableStore Search ----------------------");
        log.info(logStr.toString());
        return page;
    }

    /**
     * 获取结果集
     * @param response  {@link SearchResponse}
     * @param clazz      实体类
     * @param <T>
     * @return
     */
    private static <T> Page<T> getListFromSearchResponse(SearchResponse response, Class<T> clazz, int limit) {
        long totalCount = response.getTotalCount();
        List<T> list = new ArrayList();
        List<Row> rows = response.getRows();
        for(Row row : rows) {
            list.add(CommonUtils.rowToEntity(row, clazz));
        }
        return new Page<T>(limit, totalCount, list);
    }

    /**
     * 查询字符串
     * @param query
     * @return
     */
    public static String getQueryString(Query query) {
        String queryString = formatQueryString(query);
        if (queryString.split("[(]+").length == 2) { // 有且仅有一个TermsQuery/RangQuery在最里层，0或多个BoolQuery在外层包裹：匹配连续多个(
            queryString = queryString.replaceAll("[(]+", "").replaceAll("[)]+", "");
        } else { // BoolQuery在最外层
            if (queryString.startsWith("(") && queryString.endsWith(")")) {
                queryString = queryString.substring(1, queryString.length() - 1);
            }
        }

        return queryString;
    }

    /**
     * 查询字符串
     * @param query
     * @return
     */
    public static String formatQueryString(Query query) {
        StringBuffer stringBuffer = new StringBuffer();
        if (query instanceof MatchAllQuery) {
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
            if (terms != null && terms.size() > 0) {
                if (terms.size() > 1) {
                    stringBuffer.append("(");
                }
                for (ColumnValue columnValue : terms) {
                    stringBuffer.append(fieldName);
                    stringBuffer.append("=");
                    stringBuffer.append("\"");
                    stringBuffer.append(columnValue.getValue());
                    stringBuffer.append("\"");
                    stringBuffer.append(" OR ");
                }
                stringBuffer = new StringBuffer(stringBuffer.substring(0, stringBuffer.lastIndexOf(" OR ")));
                if (terms.size() > 1) {
                    stringBuffer.append(")");
                }
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
            if (from != null && to != null) {
                stringBuffer.append("(");
            }
            if (from != null) {
                stringBuffer.append(temp.getFieldName());
                stringBuffer.append(">");
                stringBuffer.append("\"");
                stringBuffer.append(from);
                stringBuffer.append("\"");
            }
            if (to != null) {
                if (from != null) {
                    stringBuffer.append(" AND ");
                }
                stringBuffer.append(temp.getFieldName());
                stringBuffer.append("<");
                stringBuffer.append("\"");
                stringBuffer.append(to);
                stringBuffer.append("\"");
            }
            if (from != null && to != null) {
                stringBuffer.append(")");
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
            List<Query> mustNotQueries = temp.getMustNotQueries();
            List<Query> filterQueries = temp.getFilterQueries();
            List<Query> mustQueries = temp.getMustQueries();
            List<Query> shouldQueries = temp.getShouldQueries();
            int queryNum = 0;
            if (mustNotQueries != null && mustNotQueries.size() > 0) {
                queryNum++;
            }
            if (mustQueries != null && mustQueries.size() > 0) {
                queryNum++;
            }
            if (filterQueries != null && filterQueries.size() > 0) {
                queryNum++;
            }
            if (shouldQueries != null && shouldQueries.size() > 0) {
                queryNum++;
            }
            if (queryNum > 1) {
                stringBuffer.append("(");
            }
            if (mustNotQueries != null && mustNotQueries.size() > 0) {
                if (StringUtils.isNotBlank(stringBuffer.toString()) && !"(".equals(stringBuffer.toString())) {
                    stringBuffer.append(" AND ");
                }
                stringBuffer.append("!");
                stringBuffer.append("(");
                for (Query getQuery : mustNotQueries) {
                    stringBuffer.append(formatQueryString(getQuery));
                    stringBuffer.append(" AND ");
                }
                stringBuffer = new StringBuffer(stringBuffer.substring(0, stringBuffer.lastIndexOf(" AND ")));
                stringBuffer.append(")");
            }
            if (mustQueries != null && mustQueries.size() > 0) {
                //多条件-且查询
                if (StringUtils.isNotBlank(stringBuffer.toString()) && !"(".equals(stringBuffer.toString())) {
                    stringBuffer.append(" AND ");
                }
                if (mustQueries.size() > 1) {
                    stringBuffer.append("(");
                }
                for (Query getQuery : mustQueries) {
                    stringBuffer.append(formatQueryString(getQuery));
                    stringBuffer.append(" AND ");
                }
                stringBuffer = new StringBuffer(stringBuffer.substring(0, stringBuffer.lastIndexOf(" AND ")));
                if (mustQueries.size() > 1) {
                    stringBuffer.append(")");
                }
            }
            if (filterQueries != null && filterQueries.size() > 0) {
                //多条件-或查询
                if (StringUtils.isNotBlank(stringBuffer.toString()) && !"(".equals(stringBuffer.toString())) {
                    stringBuffer.append(" AND ");
                }
                if (filterQueries.size() > 1) {
                    stringBuffer.append("(");
                }
                for (Query getQuery : filterQueries) {
                    stringBuffer.append(formatQueryString(getQuery));
                    stringBuffer.append(" OR ");
                }
                stringBuffer = new StringBuffer(stringBuffer.substring(0, stringBuffer.lastIndexOf(" OR ")));
                if (filterQueries.size() > 1) {
                    stringBuffer.append(")");
                }
            }
            if (shouldQueries != null && shouldQueries.size() > 0) {
                //多条件-或查询
                if (StringUtils.isNotBlank(stringBuffer.toString()) && !"(".equals(stringBuffer.toString())) {
                    stringBuffer.append(" AND ");
                }
                if (shouldQueries.size() > 1) {
                    stringBuffer.append("(");
                }
                for (Query getQuery : shouldQueries) {
                    stringBuffer.append(formatQueryString(getQuery));
                    stringBuffer.append(" OR ");
                }
                stringBuffer = new StringBuffer(stringBuffer.substring(0, stringBuffer.lastIndexOf(" OR ")));
                if (shouldQueries.size() > 1) {
                    stringBuffer.append(")");
                }
            }
            if (queryNum > 1) {
                stringBuffer.append(")");
            }
        }

        return stringBuffer.toString();
    }




}
