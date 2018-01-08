---
--- Created by zhongcheng.
--- DateTime: 2018/1/8 下午11:05
---
local uri_args = ngx.req.get_uri_args()
local method = uri_args["method"]
local hot_product_id = uri_args["productId"]
local hot_product_key = "hot_point_product_" .. hot_product_id

local hot_cache = ngx.shared.hot_cache

if method == "add" then
    hot_cache:set(hot_product_key, "true", 60 * 60)
elseif method == "del" then
    hot_cache:set(hot_product_key, "false")
end
