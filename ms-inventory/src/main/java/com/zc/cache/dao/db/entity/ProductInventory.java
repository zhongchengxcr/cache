package com.zc.cache.dao.db.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhongc
 * @since 2017-12-24
 */
public class ProductInventory extends Model<ProductInventory> {



    private static final long serialVersionUID = 1L;

	private Long productId;
	private Long inventoryCnt;


	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getInventoryCnt() {
		return inventoryCnt;
	}

	public void setInventoryCnt(Long inventoryCnt) {
		this.inventoryCnt = inventoryCnt;
	}

	@Override
	protected Serializable pkVal() {
		return this.productId;
	}

	@Override
	public String toString() {
		return "ProductInventory{" +
			"productId=" + productId +
			", inventoryCnt=" + inventoryCnt +
			"}";
	}
}
