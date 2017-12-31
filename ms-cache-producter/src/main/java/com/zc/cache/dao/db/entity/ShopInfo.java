package com.zc.cache.dao.db.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
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
public class ShopInfo extends Model<ShopInfo> {

    private static final long serialVersionUID = 1L;

	@TableId(value="id", type= IdType.AUTO)
	private Long id;
	private String name;
	private Integer level;
	private Double goodCommentRate;


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

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Double getGoodCommentRate() {
		return goodCommentRate;
	}

	public void setGoodCommentRate(Double goodCommentRate) {
		this.goodCommentRate = goodCommentRate;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "ShopInfo{" +
			"id=" + id +
			", name=" + name +
			", level=" + level +
			", goodCommentRate=" + goodCommentRate +
			"}";
	}
}
