package com.mjm.chapter12;

import io.netty.buffer.ByteBufUtil;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-07-02 20:51
 * @since
 */
public class Tests {

    public static void main(String[] args) {
        String code = "474554202f20485454502f312e310d0a486f73743a206c6f63616c686f73743a383938390d0a436f6e6e656374696f6e" +
                "3a206b6565702d616c6976650d0a43616368652d436f6e74726f6c3a206d61782d6167653d300d0a557067726164652d496e736" +
                "5637572652d52657175657374733a20310d0a557365722d4167656e743a204d6f7a696c6c612f352e3020284d6163696e746f73" +
                "683b20496e74656c204d6163204f5320582031305f31345f3029204170706c655765624b69742f3533372e333620284b48544d4" +
                "c2c206c696b65204765636b6f29204368726f6d652f37352e302e333737302e313030205361666172692f3533372e33360d0a41" +
                "63636570743a20746578742f68746d6c2c6170706c69636174696f6e2f7868746d6c2b786d6c2c6170706c69636174696f6e2f7" +
                "86d6c3b713d302e392c696d6167652f776562702c696d6167652f61706e672c2a2f2a3b713d302e382c6170706c69636174696f" +
                "6e2f7369676e65642d65786368616e67653b763d62330d0a4163636570742d456e636f64696e673a20677a69702c206465666c6" +
                "174652c2062720d0a4163636570742d4c616e67756167653a207a682d434e2c7a683b713d302e392c656e3b713d302e380d0a43" +
                "6f6f6b69653a205079636861726d2d65336132633164363d62323139353035312d363264362d343961612d393463622d6564373" +
                "166343763353830313b2063737266746f6b656e3d494f32307877754e6b7057644b6a6246674a7054364979737a4e5875367a54" +
                "47505350315352333965507950635746466a4a3063516153624254395a4245776e3b205f67613d4741312e312e3230373031373" +
                "13331312e313535343935313737340d0a0d0a";

        byte[] bytes = ByteBufUtil.decodeHexDump(code);
        System.out.println(new String(bytes));
    }
}
