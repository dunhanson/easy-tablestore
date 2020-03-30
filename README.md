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

tableStore.yaml

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

log4j.properties

```properties
### 设置###
log4j.rootLogger = info, stdout

### 输出信息到控制抬 ###
log4j.appender.stdout = org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target = System.out
log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern = [%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} method:%l%n%m%n
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



## 查询效率

数据量：1468213条

并发数：50个线程

循环数：100次

查条件：随机关键词（2000个）、随机公告类型、随机省份进行组合查询

首查询，平均访问延迟20毫秒

再查询，平均访问延迟10毫秒



## 参考资料

表格存储

https://help.aliyun.com/document_detail/27280.html

多条件组合查询

https://help.aliyun.com/document_detail/100422.html