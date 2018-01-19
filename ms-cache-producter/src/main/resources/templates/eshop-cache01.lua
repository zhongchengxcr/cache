local producer = require("resty.kafka.producer")
local cjson = require("cjson")
local template = require("resty.template")

local broker_list = {
    { host = "192.168.0.105", port = 9092 },
    { host = "192.168.0.106", port = 9092 },
    { host = "192.168.0.107", port = 9092 }
}

local log_json = {}
log_json["headers"] = ngx.req.get_headers()
log_json["uri_args"] = ngx.req.get_uri_args()
log_json["body"] = ngx.req.read_body()
log_json["http_version"] = ngx.req.http_version()
log_json["method"] = ngx.req.get_method()
log_json["raw_reader"] = ngx.req.raw_header()
log_json["body_data"] = ngx.req.get_body_data()

local message = cjson.encode(log_json)
local productId = ngx.req.get_uri_args()["productId"]

local async_producer = producer:new(broker_list, { producer_type = "async" })
local ok, err = async_producer:send("access-log", productId, message)

if not ok then
    ngx.log(ngx.ERR, "kafka send err:", err)
    return
end


local uri_args = ngx.req.get_uri_args()
local product_id = uri_args["productId"]
local shop_id = uri_args["shopId"]

local cache_ngx = ngx.shared.my_cache

local product_cache_key = "product_info_" .. product_id
local shop_cache_key = "shop_info_" .. shop_id

local product_cache = cache_ngx:get(product_cache_key)
local shop_cache = cache_ngx:get(shop_cache_key)

if product_cache == "" or product_cache == nil then
    local http = require("resty.http")
    local httpc = http.new()

    local resp, _ = httpc:request_uri("http://192.168.0.101:8081", {
        method = "GET",
        path = "/product/" .. product_id
    })

    product_cache = resp.body
    -- 失效时间随机,避免同时失效
    local expire_time = math.random(600,1200)
    cache_ngx:set(product_cache_key, product_cache, expire_time)
end



if shop_cache == "" or shop_cache == nil then
    local http = require("resty.http")
    local httpc = http.new()

    local resp, err = httpc:request_uri("http://192.168.0.101:8081", {
        method = "GET",
        path = "/shop/" .. shop_id
    })

    shop_cache = resp.body
    cache_ngx:set(shop_cache_key, shop_cache, 10 * 60)
end



local product_cache_json = cjson.decode(product_cache)
local shop_cache_json = cjson.decode(shop_cache)

local context = {

    productId = product_cache_json.id,
    productName = product_cache_json.name,
    productPrice = product_cache_json.price,
    productPictureList = product_cache_json.pictureList,
    productSpecification = product_cache_json.specification,
    productService = product_cache_json.service,
    productColor = product_cache_json.color,
    shopId = shop_cache_json.id,
    shopName = shop_cache_json.name,
    shopLevel = shop_cache_json.level,
    shopGoodCommentRate = shop_cache_json.goodCommentRate

}


template.render("product.html", context)

