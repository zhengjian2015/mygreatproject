package com.org.seckill.service;

import com.org.seckill.dto.Exposer;
import com.org.seckill.dto.SeckillExecution;
import com.org.seckill.entity.Seckill;
import com.org.seckill.exception.RepeatKillException;
import com.org.seckill.exception.SeckillCloseException;
import com.org.seckill.exception.SeckillException;

import java.util.List;

/*
 *
 * 业务接口 站在使用者角度使用接口
 * 三个方面 方法定义粒度 参数 ,返回类型，（return 类型/异常）
 *
 */
public interface SeckillService {
    //查询所有秒杀记录
    List<Seckill> getSeckillList();

    Seckill getById(long seckillId);

    //秒杀开启是输出秒杀接口地址 否则输出系统时间和秒杀时间
    Exposer exportSeckillUrl(long seckillId);

    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws RepeatKillException,SeckillCloseException,SeckillException;

    SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);
}