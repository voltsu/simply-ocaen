package cn.simplyocean.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidation {
	public static boolean isMobileNO(String mobiles) {  
	    /* 
	    �ƶ���134��135��136��137��138��139��150��151��157(TD)��158��159��187��188 
	    ��ͨ��130��131��132��152��155��156��185��186 
	    ���ţ�133��153��180��189����1349��ͨ�� 
	    �ܽ��������ǵ�һλ�ض�Ϊ1���ڶ�λ�ض�Ϊ3��5��8������λ�õĿ���Ϊ0-9 
	    */ 		
		String telRegex = "[1][358]\\d{9}";//"[1]"�����1λΪ����1��"[358]"����ڶ�λ����Ϊ3��5��8�е�һ����"\\d{9}"��������ǿ�����0��9�����֣���9λ��  
		if (mobiles.isEmpty()){ 
		    return false;
		}else{
		    return mobiles.matches(telRegex);
		}  
	}  
	
	public static boolean isPasswordStandard(String password) {
		//���ܰ�������
		if(hasChinese(password)){
			return false;
		}

		/**
		* ����ƥ��
		* \\w{6,18}ƥ��������ĸ�����֡��»��� �ַ�������6��18�������ո�
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
