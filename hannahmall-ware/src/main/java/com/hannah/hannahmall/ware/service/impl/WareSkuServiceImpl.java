package com.hannah.hannahmall.ware.service.impl;


import com.hannah.hannahmall.common.exception.HannahmallExceptinCodeEnum;
import com.hannah.hannahmall.common.feign.MqServiceAPI;
import com.hannah.hannahmall.common.feign.OrderServiceAPI;
import com.hannah.hannahmall.common.feign.ProductServiceAPI;
import com.hannah.hannahmall.common.feign.vo.*;
import com.hannah.hannahmall.common.model.enume.OrderStatusEnum;
import com.hannah.hannahmall.common.model.mq.to.OrderEntityPayedTO;
import com.hannah.hannahmall.common.model.mq.to.OrderEntityReleaseTO;
import com.hannah.hannahmall.common.model.mq.to.WareStockDelayTO;

import com.hannah.hannahmall.common.utils.R;
import com.hannah.hannahmall.common.utils.ResultBody;

import com.hannah.hannahmall.ware.dao.WareOrderTaskDao;
import com.hannah.hannahmall.ware.dao.WareOrderTaskDetailDao;
import com.hannah.hannahmall.ware.entity.WareOrderTaskDetailEntity;
import com.hannah.hannahmall.ware.entity.WareOrderTaskEntity;

import com.hannah.hannahmall.ware.service.WareOrderTaskDetailService;
import com.hannah.hannahmall.ware.vo.WareSkuVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hannah.hannahmall.common.utils.PageUtils;
import com.hannah.hannahmall.common.utils.Query;

import com.hannah.hannahmall.ware.dao.WareSkuDao;
import com.hannah.hannahmall.ware.entity.WareSkuEntity;
import com.hannah.hannahmall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;

import static com.hannah.hannahmall.common.constant.MqConstant.MQ_STOCK_LOCKED_ROUTINGKEY;
import static com.hannah.hannahmall.common.constant.MqConstant.MQ_WARE_EXCHANGE;



@Service("wareSkuService")
@Slf4j
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private WareOrderTaskDao wareOrderTaskDao;
    @Autowired
    private WareOrderTaskDetailDao wareOrderTaskDetailDao;
    @Autowired
    private OrderServiceAPI orderServiceAPI;
    @Autowired
    MqServiceAPI mqServiceAPI;
    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;
    @Autowired
    ProductServiceAPI productServiceAPI;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId) && !"0".equalsIgnoreCase(skuId)) {
            queryWrapper.eq("sku_id",skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId) && !"0".equalsIgnoreCase(wareId)) {
            queryWrapper.eq("ware_id",wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据skuid判断是否有库存
     *
     * @param skuIds
     * @return
     */
    @Override
    public List<WareHasStockVO> hasStock(List<Long> skuIds) {
        List<WareHasStockVO> list = skuIds.stream().map(skuid -> {
            WareHasStockVO wareHasStockVo = new WareHasStockVO();
            Long sumStock = wareSkuDao.selectStockBySkuId(skuid);
            wareHasStockVo.setSkuId(skuid);
            wareHasStockVo.setHasStock(sumStock > 0 ? 1 : 0);
            return wareHasStockVo;
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public ResultBody<Integer> hasStock(Long skuId) {
        Long count = wareSkuDao.selectStockBySkuId(skuId);
        if (count > 0) {
            return new ResultBody<>(1);
        } else {
            return new ResultBody<>(0);
        }
    }

    @Override
    @Transactional
    public ResultBody wareSkuLock(WareSkuLockVO wareSkuLockVO) {

        List<Long> wareOrderTaskDetailIds = new ArrayList<>();
        for (SkuOrderItem skuOrderItem : wareSkuLockVO.getSkuOrderItems()) {
            Boolean flag = false;
            List<WareSkuVO> wareSkuVOS = wareSkuDao.selectWareIdsBySkuId(skuOrderItem.getSkuId(), skuOrderItem.getNum());
            for (WareSkuVO wareSkuVO : wareSkuVOS) {
                if (wareSkuVO.getSkuRemainingQuantity() >= 0) {
                    //修改仓库数量
                    wareSkuDao.updateWareSkuStockLock(wareSkuVO.getId(), skuOrderItem.getNum());
                    //先判断再增加WareOrderTaskEntity
                    WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskDao.selectOne(new QueryWrapper<WareOrderTaskEntity>().
                            eq("order_sn", wareSkuLockVO.getOrderSn()).last("limit 1"));
                    if (wareOrderTaskEntity == null) {
                        //创建WareOrderTaskEntity
                        wareOrderTaskEntity = new WareOrderTaskEntity();
                        wareOrderTaskEntity.setOrderSn(wareSkuLockVO.getOrderSn());
                        wareOrderTaskEntity.setOrderId(wareSkuLockVO.getOrderId());
                        wareOrderTaskEntity.setConsignee(wareSkuLockVO.getReceiverName());
                        wareOrderTaskEntity.setConsigneeTel(wareSkuLockVO.getReceiverPhone());
                        wareOrderTaskEntity.setOrderComment(wareSkuLockVO.getNote());
                        wareOrderTaskEntity.setDeliveryAddress(wareSkuLockVO.getReceiverAddress());
                        wareOrderTaskEntity.setTaskStatus(1);//任务状态为已锁定
                        wareOrderTaskEntity.setCreateTime(new Date());
                        wareOrderTaskEntity.setWareId(wareSkuVO.getWareId());
                        wareOrderTaskDao.insert(wareOrderTaskEntity);
                    }
                    //创建WareOrderTaskDetailEntity
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
                    wareOrderTaskDetailEntity.setSkuId(skuOrderItem.getSkuId());
                    wareOrderTaskDetailEntity.setSkuName(skuOrderItem.getSkuName());
                    wareOrderTaskDetailEntity.setSkuNum(skuOrderItem.getNum());
                    wareOrderTaskDetailEntity.setWareSkuId(wareSkuVO.getId());
                    wareOrderTaskDetailEntity.setLockStatus(1);//锁定状态为已锁定
                    wareOrderTaskDetailEntity.setTaskId(wareOrderTaskEntity.getId());
                    wareOrderTaskDetailDao.insert(wareOrderTaskDetailEntity);
                    wareOrderTaskDetailIds.add(wareOrderTaskDetailEntity.getId());
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return new ResultBody(HannahmallExceptinCodeEnum.WARE_STOCK_ERROR);
            }
        }
        /**
         * 库存自动解锁功能:当用户超时未支付,或者订单服务出现异常,库存需要自动解锁,即将锁定的库存数量重新减去
         * 发送延时队列,对象需要包含wms_ware_order_task_detail主键的id集合,orderSn,
         * 当消费者拿到这个消息时可以根据orderSn查询订单状态来修改库存状态
         */
        WareStockDelayTO wareStockDelayTO = new WareStockDelayTO();
        wareStockDelayTO.setWareOrderTaskDetailIds(wareOrderTaskDetailIds);
        wareStockDelayTO.setOrderSn(wareSkuLockVO.getOrderSn());
        SendMessageRequest messageRequest = SendMessageRequest.builder().className(wareStockDelayTO.getClass().getName()).
                routingKey(MQ_STOCK_LOCKED_ROUTINGKEY).object(wareStockDelayTO).exchange(MQ_WARE_EXCHANGE).build();
        mqServiceAPI.sendMessage(messageRequest);
        return new ResultBody();
    }

    @Override
    @Transactional
    public void stockRelease(WareStockDelayTO wareStockDelayTO) {
        //根据订单查询库存状态
        String orderSn = wareStockDelayTO.getOrderSn();
        ResultBody<OrderEntityVO> resultBody = orderServiceAPI.getOrderEntityByOrderSn(orderSn);  //如果远程调用失败,则重新归还队列
        if (resultBody.getCode() == 0) {
            OrderEntityVO orderEntityVO = resultBody.getData();
            if (orderEntityVO == null || orderEntityVO.getStatus().equals(OrderStatusEnum.CANCLED.getCode())) {
                //订单创建失败,或者用户取消需要回滚,需要将库存加上去
                List<Long> wareOrderTaskDetailIds = wareStockDelayTO.getWareOrderTaskDetailIds();
                wareOrderTaskDetailIds.stream().forEach(id -> {
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = wareOrderTaskDetailDao.selectOne(new QueryWrapper<WareOrderTaskDetailEntity>().eq("id", id).eq("lock_status", 1));
                    if (wareOrderTaskDetailEntity != null) {
                        log.info("*********库存服务的自动解锁库存消息,订单号:{}***********", wareStockDelayTO.getOrderSn());
                        //修改库存工作单
                        WareOrderTaskDetailEntity wareOrderTaskDetail = new WareOrderTaskDetailEntity();
                        wareOrderTaskDetail.setId(wareOrderTaskDetailEntity.getId());
                        wareOrderTaskDetail.setLockStatus(2);
                        wareOrderTaskDetailDao.updateById(wareOrderTaskDetail);
                        //修改商品库存
                        Long wareSkuId = wareOrderTaskDetailEntity.getWareSkuId();
                        Integer skuNum = wareOrderTaskDetailEntity.getSkuNum();
                        WareSkuEntity wareSkuEntity = wareSkuDao.selectById(wareSkuId);
                        WareSkuEntity wareSku = new WareSkuEntity();
                        wareSku.setId(wareSkuEntity.getId());
                        wareSku.setStockLocked(wareSkuEntity.getStockLocked() - skuNum);
                        wareSkuDao.updateById(wareSku);
                        //修改库存工作单
                        WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskDao.
                                selectOne(new QueryWrapper<WareOrderTaskEntity>().
                                        eq("id", wareOrderTaskDetailEntity.getTaskId()).
                                        eq("task_status", 1));
                        if (wareOrderTaskEntity != null) {
                            WareOrderTaskEntity updateWareOrderTaskEntity = new WareOrderTaskEntity();
                            updateWareOrderTaskEntity.setId(wareOrderTaskDetailEntity.getTaskId());
                            wareOrderTaskEntity.setTaskStatus(2);
                            wareOrderTaskDao.updateById(wareOrderTaskEntity);
                        }
                    }
                });
            }
        }
    }

    @Override
    @Transactional
    public void stockRelease(OrderEntityReleaseTO orderEntityReleaseTO) {
        if (OrderStatusEnum.CREATE_NEW.getCode().equals(orderEntityReleaseTO.getStatus())) {
            String orderSn = orderEntityReleaseTO.getOrderSn();
            //从订单服务再查一次订单的状态
            ResultBody<OrderEntityVO> resultBody = orderServiceAPI.getOrderEntityByOrderSn(orderSn);
            if (resultBody.getCode() == 0) {
                OrderEntityVO data = resultBody.getData();
                if (OrderStatusEnum.CANCLED.getCode().equals(data.getStatus())) {
                    WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskDao.selectOne(new QueryWrapper<WareOrderTaskEntity>().
                            eq("order_sn", orderSn).eq("task_status", 1).last("limit 1"));
                    if (wareOrderTaskEntity != null) {
                        WareOrderTaskEntity updateWareOrderTaskEntity = new WareOrderTaskEntity();
                        Long taskId = wareOrderTaskEntity.getId();
                        updateWareOrderTaskEntity.setId(taskId);
                        updateWareOrderTaskEntity.setTaskStatus(2);
                        wareOrderTaskDao.updateById(updateWareOrderTaskEntity);
                        //查询工作单详情列表
                        List<WareOrderTaskDetailEntity> wareOrderTaskDetailEntities = wareOrderTaskDetailDao.
                                selectList(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", taskId).
                                        eq("lock_status", 1));
                        if (wareOrderTaskDetailEntities != null && wareOrderTaskDetailEntities.size() > 0) {
                            log.info("*********订单超时未支付,自动解锁库存,订单号:{}************", orderEntityReleaseTO.getOrderSn());
                            wareOrderTaskDetailEntities.stream().forEach(wareOrderTaskDetailEntity -> {
                                WareOrderTaskDetailEntity updateWareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
                                updateWareOrderTaskDetailEntity.setId(wareOrderTaskDetailEntity.getId());
                                updateWareOrderTaskDetailEntity.setLockStatus(2);
                                wareOrderTaskDetailDao.updateById(updateWareOrderTaskDetailEntity);
                                //恢复库存
                                Long wareSkuId = wareOrderTaskDetailEntity.getWareSkuId();
                                Integer skuNum = wareOrderTaskDetailEntity.getSkuNum();
                                WareSkuEntity wareSkuEntity = wareSkuDao.selectById(wareSkuId);
                                WareSkuEntity updateWareSkuEntity = new WareSkuEntity();
                                updateWareSkuEntity.setId(wareSkuId);
                                updateWareSkuEntity.setStockLocked(wareSkuEntity.getStockLocked() - skuNum);
                                wareSkuDao.updateById(updateWareSkuEntity);
                            });
                        }
                    }
                }
            }
        }
    }

    /**
     * 减去库存
     *
     * @param orderEntityPayedTO
     */
    @Override
    @Transactional
    public void stockReduce(OrderEntityPayedTO orderEntityPayedTO) {
        if (OrderStatusEnum.PAYED.getCode().equals(orderEntityPayedTO.getStatus())) {
            String orderSn = orderEntityPayedTO.getOrderSn();
            ResultBody<OrderEntityVO> resultBody = orderServiceAPI.getOrderEntityByOrderSn(orderSn);
            if (resultBody.getCode() == 0) {
                OrderEntityVO entityVO = resultBody.getData();
                if (OrderStatusEnum.PAYED.getCode().equals(entityVO.getStatus())) {
                    WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskDao.selectOne(new QueryWrapper<WareOrderTaskEntity>().
                            eq("order_sn", orderSn).eq("task_status", 1).last("limit 1"));
                    if (wareOrderTaskEntity != null) {
                        WareOrderTaskEntity updateWareOrderTaskEntity = new WareOrderTaskEntity();
                        Long taskId = wareOrderTaskEntity.getId();
                        updateWareOrderTaskEntity.setId(taskId);
                        updateWareOrderTaskEntity.setTaskStatus(3);
                        wareOrderTaskDao.updateById(updateWareOrderTaskEntity);
                        //查询工作单详情列表
                        List<WareOrderTaskDetailEntity> wareOrderTaskDetailEntities = wareOrderTaskDetailDao.
                                selectList(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", taskId).
                                        eq("lock_status", 1));
                        if (wareOrderTaskDetailEntities != null && wareOrderTaskDetailEntities.size() > 0) {
                            log.info("*********订单已支付,减去库存,订单号:{}************", orderEntityPayedTO.getOrderSn());
                            List<WareOrderTaskDetailEntity> taskDetailUpdateList = wareOrderTaskDetailEntities.stream().map(wareOrderTaskDetailEntity -> {
                                WareOrderTaskDetailEntity updateWareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
                                updateWareOrderTaskDetailEntity.setId(wareOrderTaskDetailEntity.getId());
                                updateWareOrderTaskDetailEntity.setLockStatus(3);
                                return updateWareOrderTaskDetailEntity;
                            }).collect(Collectors.toList());
                            wareOrderTaskDetailService.updateBatchById(taskDetailUpdateList);
                            List<WareSkuEntity> wareSkuList = wareOrderTaskDetailEntities.stream().map(wareOrderTaskDetailEntity -> {
                                Long wareSkuId = wareOrderTaskDetailEntity.getWareSkuId();
                                Integer skuNum = wareOrderTaskDetailEntity.getSkuNum();
                                WareSkuEntity wareSkuEntity = wareSkuDao.selectById(wareSkuId);
                                //锁定的库存需要减去,库存数也要减去
                                WareSkuEntity updateWareSkuEntity = new WareSkuEntity();
                                updateWareSkuEntity.setId(wareSkuId);
                                updateWareSkuEntity.setStockLocked(wareSkuEntity.getStockLocked() - skuNum);
                                updateWareSkuEntity.setStock(wareSkuEntity.getStock() - skuNum);
                                return updateWareSkuEntity;
                            }).collect(Collectors.toList());
                            //真正减库存
                            this.updateBatchById(wareSkuList);
                        }
                    }
                }
            }
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {

        //1、判读如果没有这个库存记录新增
        List<WareSkuEntity> wareSkuEntities = wareSkuDao.selectList(
                new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));

        if (wareSkuEntities == null || wareSkuEntities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字，如果失败整个事务无需回滚
            //1、自己catch异常
            try{
                ResultBody<SkuInfoVO> info = productServiceAPI.info(skuId);
                if (info.getCode() == 0) {
                    SkuInfoVO data = info.getData();
                    wareSkuEntity.setSkuName(data.getSkuName());
                }
            } catch (Exception e) {

            }
            //添加库存信息
            wareSkuDao.insert(wareSkuEntity);
        } else {
            //修改库存信息
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }

    }
}
