package cn.master.zeus.common.listener.project;

import cn.master.zeus.common.holder.SpringContextHolder;
import cn.master.zeus.entity.Project;
import cn.master.zeus.entity.SystemUser;
import cn.master.zeus.mapper.SystemUserMapper;
import com.mybatisflex.annotation.SetListener;

import java.io.Serializable;

/**
 * @author Created by 11's papa on 01/11/2024
 **/
public class ProjectListener implements SetListener {
    @Override
    public Object onSet(Object entity, String property, Object value) {
        Project obj = (Project) entity;
        if (property.equals("createUser")) {
            SystemUserMapper systemUserMapper = SpringContextHolder.getBean(SystemUserMapper.class);
            SystemUser user = systemUserMapper.selectOneById((Serializable) value);
            //obj.setCreateUser(user.getName());
            value = user.getName();
        }

        return value;
    }
}
