package com.wingrez.easycontrolpc.utils;

public class Utils {

    public static boolean checkAddressValid(String address) {
        if(address.isEmpty()) return false;
        int point_cnt=0;
        for(int i=0;i<address.length();i++){
            if(address.charAt(i)=='.') point_cnt++;
            else if(!(address.charAt(i)>='0' && address.charAt(i)<='9')) return false;
        }
        if(point_cnt!=3) return false;
        String[] seg=address.split("\\.");
        if(seg.length!=4) return false;
        for(int i=0;i<seg.length;i++){
            int value=Integer.valueOf(seg[i]);
            if(!(value>=0 && value<=255)) return false;
        }
        return true;
    }

    public static boolean checkPortValid(int port) {
        if(!(port>=0 && port<=65535)) return false;
        return true;
    }
}
