package com.kaige.distribution.transaction.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaige.distribution.transaction.pay.dao.PayInfoDao;
import com.kaige.distribution.transaction.pay.entity.PayInfo;
import com.kaige.distribution.transaction.pay.service.PayInfoService;
import org.springframework.stereotype.Service;

/**
 * 支付信息表(PayInfo)表服务实现类
 *
 * @author kaige
 * @since 2021-12-12 16:53:20
 */
@Service("payInfoService")
public class PayInfoServiceImpl extends ServiceImpl<PayInfoDao, PayInfo>
    implements PayInfoService {}
