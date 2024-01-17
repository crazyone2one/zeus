package cn.master.zeus.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.ApiScenario;
import cn.master.zeus.mapper.ApiScenarioMapper;
import cn.master.zeus.service.IApiScenarioService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class ApiScenarioServiceImpl extends ServiceImpl<ApiScenarioMapper, ApiScenario> implements IApiScenarioService {

}
