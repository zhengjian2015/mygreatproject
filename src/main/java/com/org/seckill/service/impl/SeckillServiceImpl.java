package com.org.seckill.service.impl;

import com.org.seckill.dao.SeckillDao;
import com.org.seckill.dao.SuccessKillDao;
import com.org.seckill.dto.Exposer;
import com.org.seckill.dto.SeckillExecution;
import com.org.seckill.entity.Seckill;
import com.org.seckill.entity.SuccessKilled;
import com.org.seckill.enums.SeckillStatEnum;
import com.org.seckill.exception.RepeatKillException;
import com.org.seckill.exception.SeckillCloseException;
import com.org.seckill.exception.SeckillException;
import com.org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(SeckillServiceImpl.class);

    private final String salt = "saaa2333(((9cxx";

    //注入service依赖
    @Resource
    private SeckillDao seckillDao;

    @Resource
    private SuccessKillDao successKillDao;

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        /**
         *  get from cache
         *  if null
         *     get from db
         *     cacnh.put(db)
         *  else
         *    return
         */
        Seckill seckill = seckillDao.queryById(seckillId);
        if(seckill == null) {
            return new Exposer(false,seckillId);
        }

        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统当前时间
        Date nowTime = new Date();
        if(nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }
        //转化特定字符串的过程，不可逆
        String md5 = getMd5(seckillId);
        return new Exposer(true,md5,seckillId);
    }


    private String getMd5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    /*使用注解控制事物方法的优先
     * 1：开发团队达成一致约定，明确标注事务方法的编程风格
     * 2：保证事务方法的执行时间尽可能短，不要穿插其他网络操作 Http请求或剥离 到事务方法外部
     * 3：不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制
     * @see org.seckill.service.SeckillService#executeSeckill(long, long, java.lang.String)
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws RepeatKillException, SeckillCloseException, SeckillException {
        // TODO Auto-generated method stub
        if(md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑
        Date nowTime = new Date();
        try {
            //记录购买行为
            int inserCount = successKillDao.insertSuccesskilled(seckillId, userPhone);
            if(inserCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeat");
            } else {
                int updateCoint = seckillDao.reduceNumber(seckillId, nowTime);
                if(updateCoint <= 0) {
                    //没有更新库存 rollback
                    throw new SeckillCloseException("Seckill close");
                } else {
                    //秒杀成功 commit
                    SuccessKilled successKilled = successKillDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }

            }

        }
        catch (SeckillCloseException e1){
            throw e1;
        }
        catch (RepeatKillException e2) {
            throw e2;
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
            //所有编译形异常 转化为 运行期异常
            throw new SeckillException("seckill inner error:"+e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        return null;
    }
}
