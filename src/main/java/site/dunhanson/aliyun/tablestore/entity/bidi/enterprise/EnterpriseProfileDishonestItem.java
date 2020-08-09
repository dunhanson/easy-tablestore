package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/28 11:15
 * @description TODO
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileDishonestItem {
    //失信人名称
    private String iname;
    //法人、负责人姓名
    private String businessentity;
    //执行依据文号
    private String gistid;
    //省份地区
    private String areaname;
    //身份证号码/组织机构代码
    private String cardnum;

    //官网是否存在【0:存在;1:不存在】
    private String status;
    //	毫秒数	发布时间
    private String publishdate;
    //失信人类型，0代表人，1代表公司
    private String type;
    //法院
    private String courtname;
    //tycId
    private String tycId;
    //失信被执行人行为具体情形
    private String disruptTypeName;
    //做出执行的依据单位
    private String gistunit;
    //生效法律文书确定的义务
    private String duty;
    //跳转到天眼查链接
    private List<String> connList;
    //履行情况
    private String performance;
    //立案时间
    private String regdate;
    //对应表id
    private String tycCompanyId;
    //案号
    private String casecode;
}
