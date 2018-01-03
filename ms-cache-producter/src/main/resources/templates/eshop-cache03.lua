local uri_args = ngx.req.get_uri_args()
local product_id = uri_args["productId"]
local shop_id = uri_args["shopId"]


local host = {"192.168.0.105","192.168.0.106"}
local hash = ngx.crc32_long(product_id)
local index = (hash % 2) +1
backend = "http://"..host[index]


local request_path = uri_args["requestPath"]
local request_body = "/"..request_path.."?productId="..product_id.."&shopId="..shop_id

local http = require("resty.http")
local httpc = http.new()

local resp, err = httpc:request_uri(backend, {
  method = "GET",
  path = request_body,

})

if not resp then 
  ngx.say("request err: ",err)
  return
end

ngx.say(resp.body)

httpc:close()


