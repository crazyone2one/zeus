package cn.master.zeus.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import cn.master.zeus.entity.Schedule;
import cn.master.zeus.mapper.ScheduleMapper;
import cn.master.zeus.service.IScheduleService;
import org.springframework.stereotype.Service;

/**
 *  服务层实现。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements IScheduleService {

}
