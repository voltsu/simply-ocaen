package cn.simplyocean.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidation {
	public static boolean isMobileNO(String mobiles) {  
	    /* 
	    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188 
	    联通：130、131、132、152、155、156、185、186 
	    电信：133、153、180、189、（1349卫通） 
	    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9 
	    */ 		
		String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。  
		if (mobiles.isEmpty()){ 
		    return false;
		}else{
		    return mobiles.matches(telRegex);
		}  
	}  
	
	public static boolean isPasswordStandard(String password) {
		//不能包含中文
		if(hasChinese(password)){
			return false;
		}

		/**
		* 正则匹配
		* \\w{6,18}匹配所有字母、数字、下划线 字符串长度6到18（不含空格）
		*/
		String format = "\\w{6,18}+";
		if(password.matches(format)){
			return true;
		} else {
			return false;
		}
	}

	public static boolean hasChinese(String source) {
		String reg_charset = "([\\u4E00-\\u9FA5]*+)";
		Pattern p = Pattern.compile(reg_charset);
		Matcher m = p.matcher(source);
		boolean hasChinese=false;
		while (m.find())
		{
			if(!"".equals(m.group(1))){
				hasChinese=true;
			}
		}
		return hasChinese;
	}
}
