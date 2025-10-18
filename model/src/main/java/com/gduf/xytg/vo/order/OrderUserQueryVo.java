package com.gduf.xytg.vo.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.gduf.xytg.enums.OrderStatus;

@Data
public class OrderUserQueryVo {

	private Long userId;

	@ApiModelProperty(value = "订单状态")
	private OrderStatus orderStatus;

}

