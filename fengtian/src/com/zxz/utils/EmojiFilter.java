package com.zxz.utils;

public class EmojiFilter {

	public static void main(String[] args) {
		String filterEmoji = filterEmoji("asdfa闅惧緱绯婃秱馃毈顚勯檲鍐� 顢�"+1);
		System.out.println(filterEmoji);
	}
	
    /**
     * 妫�娴嬫槸鍚︽湁emoji瀛楃
     * @param source
     * @return 涓�鏃﹀惈鏈夊氨鎶涘嚭
     */
    public static boolean containsEmoji(String source) {
        if (source == null || "".equals(source)) {
            return false;
        }
        
        int len = source.length();
        
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            
            if (isEmojiCharacter(codePoint)) {
                //do nothing锛屽垽鏂埌浜嗚繖閲岃〃鏄庯紝纭鏈夎〃鎯呭瓧绗�
                return true;
            }
        }
        
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || 
                (codePoint == 0x9) ||                            
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }
    
    /**
     * 杩囨护emoji 鎴栬�� 鍏朵粬闈炴枃瀛楃被鍨嬬殑瀛楃
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
        
        if (!containsEmoji(source)) {
            return source;//濡傛灉涓嶅寘鍚紝鐩存帴杩斿洖
        }
        //鍒拌繖閲岄搧瀹氬寘鍚�
        StringBuilder buf = null;
        
        int len = source.length();
        
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            
            if (isEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }
                
                buf.append(codePoint);
            } else {
            }
        }
        
        if (buf == null) {
            return source;//濡傛灉娌℃湁鎵惧埌 emoji琛ㄦ儏锛屽垯杩斿洖婧愬瓧绗︿覆
        } else {
            if (buf.length() == len) {//杩欓噷鐨勬剰涔夊湪浜庡敖鍙兘灏戠殑toString锛屽洜涓轰細閲嶆柊鐢熸垚瀛楃涓�
                buf = null;
                return source;
            } else {
                return buf.toString();
            }
        }
        
    }
}