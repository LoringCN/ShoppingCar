package cn.huace.goods.enums;


/**
 * 生鲜商品规则
 * @author Lin Huan
 * @date  2019年1月3日
 * @desc   描述
 * @version 1.0.0
 */
public enum TempGoodsEnum {
	
	/**
	 * 28+6位条码+4位金额+1位校验码  共13位
	 * 27+5位条码+5位金额+1位校验码  共13位
	 * 26+4位条码+6位金额+1位校验码  共13位
	 * 24+6位条码+4位金额+1  共13位
	 * 23+5位条码+5位金额+1位校验码  共13位
     * 22+4位条码+6位金额+1  共13位
	 */
	TWO_2("22",4,6,1),
	TWO_3("23",5,5,1),
	TWO_4("24",6,4,1),
	TWO_6("26",4,6,1),
	TWO_7("27",5,5,1),
	TWO_8("28",6,4,1),
	;
	private String str;
	
	private Integer codeLen;
	
	private Integer priceLen;
	
	private Integer crc;
	
	TempGoodsEnum(String str,Integer codeLen,Integer priceLen,Integer crc){
		this.str = str;
		this.codeLen = codeLen;
		this.priceLen = priceLen;
		this.crc = crc;
	}
	
	public static boolean isTempGoods(String barCode){
		if(barCode.startsWith(TWO_2.str)|| 
		   barCode.startsWith(TWO_3.str)||
		   barCode.startsWith(TWO_4.str)||
		   barCode.startsWith(TWO_6.str)||
		   barCode.startsWith(TWO_7.str)||
		   barCode.startsWith(TWO_8.str)
		   ){
			return true;
		}
		return false;
	}
	
	public static  TempGoods  getTempGoods(String barCode){

		TempGoods tempGoods = null;
		
		TempGoodsEnum[] enums = TempGoodsEnum.values();
		for(TempGoodsEnum e:enums){
			if(barCode.startsWith(e.str)){
				tempGoods = new TempGoods();
				String goodsCode =  barCode.substring(2,2+e.codeLen);
				String goodsPrice =  barCode.substring(2+e.codeLen,2+e.codeLen+e.priceLen);
				tempGoods.setCode(goodsCode);
				tempGoods.setPrice(Double.valueOf(goodsPrice));
			}
		}
		return tempGoods;
	}
	
	
	public static class TempGoods{
		
		private String code;
		
		private Double price;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}
		
		
	}
}
