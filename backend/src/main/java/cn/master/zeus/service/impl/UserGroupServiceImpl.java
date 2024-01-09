package cn.master.zeus.service.impl;

import cn.master.zeus.dto.UserGroupDTO;
import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.service.IUserGroupService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.zeus.entity.table.SystemGroupTableDef.SYSTEM_GROUP;
import static cn.master.zeus.entity.table.UserGroupTableDef.USER_GROUP;

/**
 * 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class UserGroupServiceImpl extends ServiceImpl<UserGroupMapper, UserGroup> implements IUserGroupService {

    @Override
    public List<UserGroupDTO> getUserGroup(String userId, String projectId) {
        QueryChain<UserGroup> select = queryChain().select(USER_GROUP.USER_ID, USER_GROUP.GROUP_ID, USER_GROUP.SOURCE_ID, SYSTEM_GROUP.NAME, SYSTEM_GROUP.TYPE)
                .join(SYSTEM_GROUP).on(SYSTEM_GROUP.ID.eq(USER_GROUP.GROUP_ID))
                .where(USER_GROUP.USER_ID.eq(userId).and(USER_GROUP.SOURCE_ID.eq(projectId)));
        return mapper.selectListByQueryAs(select, UserGroupDTO.class);
    }
}
