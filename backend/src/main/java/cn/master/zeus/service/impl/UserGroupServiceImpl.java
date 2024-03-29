package cn.master.zeus.service.impl;

import cn.master.zeus.entity.UserGroup;
import cn.master.zeus.mapper.UserGroupMapper;
import cn.master.zeus.service.IUserGroupService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class UserGroupServiceImpl extends ServiceImpl<UserGroupMapper, UserGroup> implements IUserGroupService {

}
