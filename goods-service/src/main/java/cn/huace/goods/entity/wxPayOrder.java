package cn.huace.goods.entity;


public class wxPayOrder {
	/**
	 * 公众账号ID
	 * @desc 微信支付分配的公众账号ID（企业号corpid即为此appId）
	 */
	private String appid;
	/**
	 * 商户号
	 * @desc 微信支付分配的商户号
	 */
	private String mch_id;
	/**
	 * 随机字符串
	 * @desc 随机字符串，长度要求在32位以内。推荐随机数生成算法
	 */
	private String nonce_str;
	/**
	 * 签名
	 * @desc 通过签名算法计算得出的签名值，详见签名生成算法
	 */
	private String sign;
	/**
	 * 商品描述
	 * @desc 商品简单描述，该字段请按照规范传递，具体请见参数规定
	 */
	private String body;
	/**
	 * 商户订单号
	 * @desc 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一。详见商户订单号
	 */
	private String out_trade_no;
	/**
	 * 标价金额
	 * @desc 订单总金额，单位为分，详见支付金额
	 */
	private Integer total_fee;
	/**
	 * 终端IP
	 * @desc APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
	 */
	private String spbill_create_ip;
	/**
	 * 通知地址
	 * @desc 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
	 */
	private String notify_url;
	/**
	 * 交易类型
	 * @desc JSAPI -JSAPI支付    NATIVE -Native支付    APP -APP支付
	 */
	private String trade_type;
	/**
	 * 商品ID
	 * @desc trade_type=NATIVE时，此参数必传。此参数为二维码中包含的商品ID，商户自行定义。
	 */
	private String  product_id;
}













