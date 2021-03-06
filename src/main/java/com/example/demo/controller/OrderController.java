package com.example.demo.controller;

import com.example.demo.VO.ResultVO;
import com.example.demo.entity.order.dao.OrderDetail;
import com.example.demo.entity.order.dto.OrderDTO;
import com.example.demo.service.DishService;
import com.example.demo.service.OrderService;
import com.example.demo.utils.KeyUtils;
import com.example.demo.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private DishService dishService;

    //创建订单
    @RequestMapping("/order")
    public ResultVO create(String openId,
                                                String[] dishid,
                                                String[] dishnum){



        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserOpenid(openId);
        List<OrderDetail> orderDetailList = new ArrayList<OrderDetail>();
        for(int i = 0 ; i < dishid.length ; i++){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setDetailId(KeyUtils.genUniqueKey());
            orderDetail.setDishId(dishid[i]);
            orderDetail.setDishQuantity(Integer.valueOf(dishnum[i]));
            orderDetail.setDishPrice(dishService.findOne(dishid[i]).getDishPrice());
            orderDetailList.add(orderDetail);
        }
        orderDTO.setOrderDetailList(orderDetailList);
        if(CollectionUtils.isEmpty(orderDTO.getOrderDetailList())){
            return ResultVOUtil.error(-1,"ERROR");//补写错误
        }



        OrderDTO createResult = orderService.create(orderDTO);
        createResult.setOrderDetailList(orderDetailList);

//        Map<String ,String> map = new HashMap<>();
//        map.put("orderId",createResult.getOrderId());

        return ResultVOUtil.success(createResult);

    }

    //订单列表
    @RequestMapping("/showList")
    public ResultVO<List<OrderDTO>> list(@RequestParam("openid") String openid,
                                         @RequestParam(value = "page",defaultValue = "0") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size){
        if(StringUtils.isEmpty(openid)){
            return null;//补写异常
        }

        PageRequest request = new PageRequest(page,size);
        Page<OrderDTO> orderDTOPage = orderService.findList(openid,request);

        return ResultVOUtil.success(orderDTOPage.getContent());
    }

    //查询单个订单
    @RequestMapping("/showList/detail")
    public ResultVO<OrderDTO> detail(@RequestParam("openId") String openid,
                                     @RequestParam("orderId") String orderId){
        //TODO 不安全做法
        OrderDTO orderDTO = orderService.findOne(orderId);
        return ResultVOUtil.success(orderDTO);
    }

    //取消订单
    @RequestMapping("/showList/detail/cancel")
    public ResultVO cancel(@RequestParam("openId") String openid,
                           @RequestParam("orderId") String orderId){

        //TODO 不安全
        OrderDTO orderDTO = orderService.findOne(orderId);

        orderService.cancel(orderDTO);
        return ResultVOUtil.success();
    }

    //接单
    @RequestMapping("/getOrder")
    public ResultVO pick(@RequestParam("orderId") String orderId,
                         @RequestParam("pickmanId") String pickmanId){
        OrderDTO orderDTO = orderService.findOne(orderId);

        OrderDTO result = orderService.pick(orderDTO,pickmanId);
        if(result == null)return ResultVOUtil.error(-1,"ERROR");//补写异常

        return ResultVOUtil.success(result);


    }

    //完成订单
    @RequestMapping("/finishOrder")
    public ResultVO finishOrder(@RequestParam("orderId") String orderId){
        OrderDTO orderDTO = orderService.findOne(orderId);
        if(orderDTO==null)return ResultVOUtil.error(-1,"ERROR");

        OrderDTO result  = orderService.finish(orderDTO);

        return ResultVOUtil.success(result);


    }

    //返回未接受的订单，参数openid
    @RequestMapping("/notAccepted")
    public ResultVO<List<OrderDTO>> notAccept(@RequestParam("openId") String openId,
                              @RequestParam(value = "page",defaultValue = "0") Integer page,
                              @RequestParam(value = "size", defaultValue = "10") Integer size){
        PageRequest pageRequest = new PageRequest(page,size);
        Page<OrderDTO> orderDTOPage = orderService.findNotDeliver(openId,pageRequest);
        if(orderDTOPage == null) return ResultVOUtil.error(-1,"ERROR");

        return ResultVOUtil.success(orderDTOPage);
    }

    //返回派送中的订单，参数openID
    @RequestMapping("/delivering")
    public ResultVO<List<OrderDTO>> delivering(@RequestParam("openId") String openId,
                                               @RequestParam(value = "page",defaultValue = "0") Integer page,
                                               @RequestParam(value = "size", defaultValue = "10") Integer size){
        PageRequest pageRequest = new PageRequest(page,size);
        Page<OrderDTO> orderDTOPage = orderService.findDelivering(openId,pageRequest);
        if(orderDTOPage==null)return ResultVOUtil.error(-1,"ERROR");

        return ResultVOUtil.success(orderDTOPage);
    }

    //已完成的订单，参数openID
    @RequestMapping("/finished")
    public ResultVO<List<OrderDTO>> finished(@RequestParam("openId") String openId,
                                             @RequestParam(value = "page",defaultValue = "0") Integer page,
                                             @RequestParam(value = "size", defaultValue = "10") Integer size){
        PageRequest pageRequest = new PageRequest(page,size);
        Page<OrderDTO> orderDTOPage = orderService.findFinished(openId,pageRequest);
        if(orderDTOPage==null)return ResultVOUtil.error(-1,"ERROR");

        return ResultVOUtil.success(orderDTOPage);
    }
}
