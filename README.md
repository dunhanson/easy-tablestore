# Easy TableStore

简化阿里云TableStore表格存储的操作

采取非过度封装策略，贴切原有TableStore的SDK，即方便上手即用



## 简单开始

```java
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

```



## 配置文件

```yaml
tableStore:
  #默认全局参数
  default:
    #endPoint
    endPoint: https://dunhanson.cn-hangzhou.ots.aliyuncs.com
    #accessKeyId
    accessKeyId: '******'
    #accessKeySecret
    accessKeySecret: '******'
    #instanceName
    instanceName: dunhanson
    #默认分页大小
    limit: 20
  #alias，别名，命名规范为类的首字母小写，例：Object->object
  document:
    #endPoint，覆盖default
    endPoint: https://dunhanson.cn-hangzhou.ots.aliyuncs.com
    #accessKeyId，覆盖default
    accessKeyId: '******'
    #accessKeySecret，覆盖default
    accessKeySecret: '******'
    #instanceName，覆盖default
    instanceName: dunhanson
    #表名
    tableName: sys_document
    #索引
    indexName:
      - sys_document_index
    #查询返回需要忽略的列
    ignoreColumn:
      - dochtmlcon
    #默认分页大小，覆盖default
    limit: 30
```



## Maven

`仅限公司内部项目配置生效`

```xml
<dependency>
    <groupId>site.dunhanson.aliyun</groupId>
    <artifactId>easy-tablestore</artifactId>
    <version>2020.0326.1548</version>
</dependency>
```



## 参考资料

表格存储

https://help.aliyun.com/document_detail/27280.html

多条件组合查询

https://help.aliyun.com/document_detail/100422.html