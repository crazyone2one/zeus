package cn.master.zeus.service;

import cn.master.zeus.dto.request.QueryMemberRequest;
import cn.master.zeus.entity.SystemUser;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 用户信息表 服务层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
public interface ISystemUserService extends IService<SystemUser> {

    List<SystemUser> getMemberList(QueryMemberRequest queryMemberRequest);
}
