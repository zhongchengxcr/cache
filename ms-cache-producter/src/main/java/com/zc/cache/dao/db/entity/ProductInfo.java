package com.zc.cache.dao.db.entity;

import java.io.Serializable;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhongc
 * @since 2017-12-27
 */
public class ProductInfo extends Model<ProductInfo> {

    private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private BigDecimal price;
	private String pictureList;
	private String specification;
	private String service;
	private String color;
	private String size;
	private Long shopId;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getPictureList() {
		return pictureList;
	}

	public void setPictureList(String pictureList) {
		this.pictureList = pictureList;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "ProductInfo{" +
			"id=" + id +
			", name=" + name +
			", price=" + price +
			", pictureList=" + pictureList +
			", specification=" + specification +
			", service=" + service +
			", color=" + color +
			", size=" + size +
			", shopId=" + shopId +
			"}";
	}
}
