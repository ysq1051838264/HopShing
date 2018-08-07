package com.example.hopshing.ble;

import java.util.Date;

public class CmdBuilder {

    /**
     * 构建一个命令
     *
     * @param cmd
     * @param protocolType
     * @param others
     * @return
     */
    public static byte[] buildCmd(int cmd, int protocolType, int... others) {
        int cnt = others.length + 4;
        byte[] cmds = new byte[4 + others.length];
        cmds[0] = (byte) cmd;
        cmds[1] = (byte) cnt;
        cmds[2] = (byte) protocolType;
        for (int i = 0; i < others.length; i++) {
            cmds[i + 3] = (byte) others[i];
        }
        int checkIndex = cmds.length - 1;
        for (int i = 0; i < checkIndex; i++) {
            cmds[checkIndex] += cmds[i];
        }
        return cmds;
    }


    /**
     * 发送结束命令
     *
     * @param protocolType 协议类型
     * @param dataType
     * @return
     */
    public static byte[] buildOverCmd(int protocolType, int dataType) {
        return buildCmd(0x1f, protocolType, dataType);
    }


    /**
     * 把体重编码称两个字节到数据，便于发送数据到秤
     *
     * @param weight
     * @return
     */
    public static int[] codeWeight(float weight) {
        int[] bs = new int[2];
        int w = (int) (weight * 10);
        int wh = w >> 8;
        int wl = w % 256;
        bs[0] = wh;
        bs[1] = wl;
        return bs;
    }
}
