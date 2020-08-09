package site.dunhanson.aliyun.tablestore.entity.bidi.enterprise;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author jiangzf
 * @version 1.0
 * @date 2019/5/28 11:16
 * @description TODO
 */
@Getter
@Setter
@ToString
public class EnterpriseProfileMortgageInfoItem{
    private BaseInfo baseInfo;
    private List<ChangeInfoList> changeInfoList;
    private List<PawnInfoList> pawnInfoList;
    private List<PeopleInfo> peopleInfo;
}
