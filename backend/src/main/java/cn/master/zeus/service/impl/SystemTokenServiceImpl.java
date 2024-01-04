package cn.master.zeus.service.impl;

import cn.master.zeus.entity.SystemToken;
import cn.master.zeus.mapper.SystemTokenMapper;
import cn.master.zeus.service.ISystemTokenService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * token信息表 服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class SystemTokenServiceImpl extends ServiceImpl<SystemTokenMapper, SystemToken> implements ISystemTokenService {

}
