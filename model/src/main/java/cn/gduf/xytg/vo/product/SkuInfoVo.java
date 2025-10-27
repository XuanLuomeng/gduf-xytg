package cn.gduf.xytg.vo.product;

import cn.gduf.xytg.model.product.SkuImage;
import cn.gduf.xytg.model.product.SkuAttrValue;
import cn.gduf.xytg.model.product.SkuInfo;
import cn.gduf.xytg.model.product.SkuPoster;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SkuInfoVo extends SkuInfo {

	@ApiModelProperty(value = "海报列表")
	private List<SkuPoster> skuPosterList;

	@ApiModelProperty(value = "属性值")
	private List<SkuAttrValue> skuAttrValueList;

	@ApiModelProperty(value = "图片")
	private List<SkuImage> skuImagesList;

}

