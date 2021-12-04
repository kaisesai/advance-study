package com.kaige.distribution.transaction.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaige.distribution.transaction.dao.EventDataDao;
import com.kaige.distribution.transaction.entity.EventData;
import com.kaige.distribution.transaction.service.EventDataService;
import org.springframework.stereotype.Service;

/**
 * 本地事件表(EventData)表服务实现类
 *
 * @author kaige
 * @since 2021-11-28 00:07:10
 */
@Service("eventDataService")
public class EventDataServiceImpl extends ServiceImpl<EventDataDao, EventData>
    implements EventDataService {}
