package org.fatmansoft.teach.util;

public class Method {
        public static boolean IsDouble(String a){//判断是否为双浮点
            if(a.charAt(0) == '-'){
                        return false;
            }else {
                for (int i = 0; i < a.length(); i++) {
                    if ((a.charAt(i) < '0' || a.charAt(i) > '9')&&a.charAt(i)!='.')
                        return false;
                }
            }
            return true;
        }
}
