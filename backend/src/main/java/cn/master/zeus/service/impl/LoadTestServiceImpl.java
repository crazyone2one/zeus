package cn.master.zeus.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.LoadTest;
import cn.master.zeus.mapper.LoadTestMapper;
import cn.master.zeus.service.ILoadTestService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class LoadTestServiceImpl extends ServiceImpl<LoadTestMapper, LoadTest> implements ILoadTestService {

}
