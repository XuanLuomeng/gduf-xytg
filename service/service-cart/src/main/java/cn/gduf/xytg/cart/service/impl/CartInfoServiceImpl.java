package cn.gduf.xytg.cart.service.impl;

import cn.gduf.xytg.cart.service.CartInfoService;
import cn.gduf.xytg.client.product.ProductFeignClient;
import cn.gduf.xytg.common.constant.RedisConst;
import cn.gduf.xytg.common.exception.XytgException;
import cn.gduf.xytg.common.result.ResultCodeEnum;
import cn.gduf.xytg.enums.SkuType;
import cn.gduf.xytg.model.order.CartInfo;
import cn.gduf.xytg.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 购物车信息服务实现类
 * @date 2025/10/27 21:58
 */
@Service
public class CartInfoServiceImpl implements CartInfoService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * 获取购物车在Redis中的存储键名
     *
     * @param userId 用户ID
     * @return 拼接后的购物车键名字符串
     */
    private String getCartKey(Long userId) {
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }

    /**
     * 设置购物车键的过期时间
     *
     * @param key 购物车键名
     */
    private void setCartKeyExpire(String key) {
        redisTemplate.expire(key, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 向用户的购物车中添加商品或更新已有商品的数量
     *
     * @param skuId  商品SKU ID
     * @param userId 用户ID
     * @param skuNum 商品数量（正数表示增加，负数表示减少）
     * @return 添加是否成功。若操作后数量小于1则返回false
     * @throws XytgException 当商品限购超限、获取商品信息失败时抛出异常
     */
    @Override
    public boolean addToCart(Long skuId, Long userId, Integer skuNum) {
        // 构造用户购物车的Redis Key，并绑定Hash操作对象
        String cartKey = getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOperations =
                redisTemplate.boundHashOps(cartKey);

        CartInfo cartInfo = null;

        // 判断该商品是否已在购物车中存在
        if (hashOperations.hasKey(skuId.toString())) {
            // 存在：取出原购物项并更新数量与状态
            cartInfo = hashOperations.get(skuId.toString());
            Integer currentSkuNum = cartInfo.getSkuNum() + skuNum;
            if (currentSkuNum < 1) {
                return false; // 若更新后数量小于1，则不执行任何操作
            }

            cartInfo.setSkuNum(currentSkuNum);
            cartInfo.setCurrentBuyNum(currentSkuNum);

            // 校验是否超过限购数量
            Integer perLimit = cartInfo.getPerLimit();
            if (currentSkuNum > perLimit) {
                throw new XytgException(ResultCodeEnum.SKU_LIMIT_ERROR);
            }

            cartInfo.setIsChecked(1); // 默认选中
            cartInfo.setUpdateTime(new Date()); // 更新时间戳
        } else {
            // 不存在：新建购物项
            skuNum = 1; // 新增默认数量为1

            // 远程调用获取商品详细信息
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if (skuInfo == null) {
                throw new XytgException(ResultCodeEnum.DATA_ERROR);
            }

            // 填充购物项属性
            cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setCategoryId(skuInfo.getCategoryId());
            cartInfo.setSkuType(skuInfo.getSkuType());
            cartInfo.setIsNewPerson(skuInfo.getIsNewPerson());
            cartInfo.setUserId(userId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCurrentBuyNum(skuNum);
            cartInfo.setSkuType(SkuType.COMMON.getCode());
            cartInfo.setPerLimit(skuInfo.getPerLimit());
            cartInfo.setImgUrl(skuInfo.getImgUrl());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setWareId(skuInfo.getWareId());
            cartInfo.setIsChecked(1);
            cartInfo.setStatus(1);
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());
        }

        // 将购物项存入Redis Hash结构
        hashOperations.put(skuId.toString(), cartInfo);

        // 设置购物车键的过期时间
        this.setCartKeyExpire(cartKey);

        return true;
    }

    /**
     * 删除购物车中的商品
     *
     * @param skuId  商品SKU ID
     * @param userId 用户ID
     * @return 删除成功返回true，失败返回false
     */
    @Override
    public boolean deleteCart(Long skuId, Long userId) {
        BoundHashOperations<String, String, CartInfo> hashOperations =
                redisTemplate.boundHashOps(this.getCartKey(userId));

        // 检查要删除的商品是否存在
        if (hashOperations.hasKey(skuId.toString())) {
            Long delete = hashOperations.delete(skuId.toString());
            return delete > 0;
        }

        return false;
    }

    /**
     * 删除用户购物车中的所有商品
     *
     * @param userId 用户ID
     * @return 删除成功返回true，失败返回false
     */
    @Override
    public boolean deleteAllCart(Long userId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);

        // 获取购物车中所有商品信息
        List<CartInfo> values = boundHashOperations.values();

        // 遍历删除每个商品
        for (CartInfo cartInfo : values) {
            Long delete = boundHashOperations.delete(cartInfo.getSkuId().toString());
            if (delete <= 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * 批量删除购物车中的商品
     *
     * @param skuIdList 商品SKU ID列表
     * @param userId    用户ID
     * @return 删除成功返回true，失败返回false
     */
    @Override
    public boolean batchDeleteCart(List<Long> skuIdList, Long userId) {
        // 获取用户购物车的Redis key
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);

        // 遍历SKU ID列表，从购物车中删除对应商品
        skuIdList.forEach(skuId -> {
            Long delete = boundHashOperations.delete(skuId.toString());
        });

        return true;
    }

    /**
     * 获取用户购物车中的商品列表
     *
     * @param userId 用户ID
     * @return 购物车中的商品列表
     */
    @Override
    public List<CartInfo> cartList(Long userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        if (StringUtils.isEmpty(userId)) {
            return cartInfoList;
        }

        // 构造购物车在Redis中的key
        String cartKey = this.getCartKey(userId);

        BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);

        // 从Redis中获取购物车所有商品信息
        cartInfoList = boundHashOperations.values();

        // 按更新时间倒序排列购物车商品
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            cartInfoList.sort((o1, o2) ->
                    o1.getUpdateTime().compareTo(o2.getUpdateTime())
            );
        }

        return cartInfoList;
    }

    /**
     * 获取用户购物车中的商品列表
     *
     * @param userId 用户ID
     * @return 购物车中的商品列表
     */
    @Override
    public List<CartInfo> getCartList(Long userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        if (StringUtils.isEmpty(userId)) {
            return cartInfoList;
        }
        // 根据用户ID获取购物车在Redis中的key
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);
        // 从Redis中获取购物车所有商品信息
        cartInfoList = boundHashOperations.values();
        // 按创建时间升序排序购物车商品列表
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o1.getCreateTime().compareTo(o2.getCreateTime());
                }
            });
        }
        return cartInfoList;
    }

    /**
     * 选中购物车中的商品
     *
     * @param userId    用户ID
     * @param skuId     商品SKU ID
     * @param isChecked 是否选中
     */
    @Override
    public void checkCart(Long userId, Long skuId, Integer isChecked) {
        // 获取用户购物车的Redis key
        String cartKey = this.getCartKey(userId);

        // 获取购物车hash操作对象
        BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);

        // 查询购物车中指定商品的信息
        CartInfo cartInfo = boundHashOperations.get(skuId.toString());
        if (cartInfo != null) {
            // 更新商品选中状态
            cartInfo.setIsChecked(isChecked);

            // 将更新后的商品信息保存回购物车
            boundHashOperations.put(skuId.toString(), cartInfo);

            // 重置购物车过期时间
            this.setCartKeyExpire(cartKey);
        }
    }

    /**
     * 全选购物车中的商品
     *
     * @param userId    用户ID
     * @param isChecked 是否选中
     */
    @Override
    public void checkAllCart(Long userId, Integer isChecked) {
        // 获取用户购物车的Redis key
        String cartKey = this.getCartKey(userId);

        // 获取购物车的hash操作对象
        BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);

        // 遍历购物车中所有商品，更新选中状态并保存回Redis
        boundHashOperations.values().forEach(cartInfo -> {
            cartInfo.setIsChecked(isChecked);
            boundHashOperations.put(cartInfo.getSkuId().toString(), cartInfo);
        });

        // 设置购物车key的过期时间
        this.setCartKeyExpire(cartKey);
    }

    /**
     * 批量选中购物车中的商品
     *
     * @param userId    用户ID
     * @param skuIdList 商品SKU ID列表
     * @param isChecked 是否选中
     */
    @Override
    public void batchCheckCart(Long userId, List<Long> skuIdList, Integer isChecked) {
        // 获取购物车key
        String cartKey = this.getCartKey(userId);

        // 获取购物车hash操作对象
        BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);

        // 遍历SKU ID列表，批量更新选中状态
        skuIdList.forEach(skuId -> {
            CartInfo cartInfo = boundHashOperations.get(skuId.toString());
            if (cartInfo != null) {
                cartInfo.setIsChecked(isChecked);
                boundHashOperations.put(skuId.toString(), cartInfo);
            }
        });

        // 设置购物车key过期时间
        this.setCartKeyExpire(cartKey);
    }

    /**
     * 获取用户选中的购物车列表
     *
     * @param userId 用户ID
     * @return 用户选中的购物车列表
     */
    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        // 获取用户的购物车key
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);

        // 获取购物车中所有商品信息
        List<CartInfo> cartInfoList = boundHashOperations.values();

        // 筛选出用户选中的购物车商品
        List<CartInfo> cartInfoListNew = cartInfoList.stream()
                .filter(cartInfo ->
                        cartInfo.getIsChecked().intValue() == 1
                ).collect(Collectors.toList());

        return cartInfoListNew;
    }

    /**
     * 删除用户选中的购物车商品
     *
     * @param userId 用户ID
     */
    @Override
    public void deleteCartChecked(Long userId) {
        // 获取用户选中的购物车商品列表
        List<CartInfo> cartCheckedList = this.getCartCheckedList(userId);

        // 提取选中商品的SKU ID列表
        List<Long> skuIdList = cartCheckedList.stream().map(
                cartInfo -> cartInfo.getSkuId()
        ).collect(Collectors.toList());

        // 构造用户购物车在Redis中的key
        String cartKey = this.getCartKey(userId);

        // 获取Redis中对应用户的购物车哈希操作对象
        BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);

        // 遍历选中的SKU ID列表，从Redis购物车中删除对应商品
        skuIdList.forEach(skuId -> {
            boundHashOperations.delete(skuId.toString());
        });
    }
}
