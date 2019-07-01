package com.mjm.chapter6;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultChannelPipeline;

import java.util.*;

/**
 * 一句话功能简述 </br>
 *
 * @author majunmin
 * @description
 * @datetime 2019-06-27 19:31
 * @since
 */
public class ChannelPiplineTest {

    public static void main(String[] args) {
        test1();
    }

    private static void test1() {

        List<Map<String, Integer>> list = new ArrayList<>();
        Map<String, Integer> map1 = new HashMap<>();
        map1.put("level", 8);
        list.add(map1);

        Map<String, Integer> map5 = new HashMap<>();
        map5.put("level", 8);
        list.add(map5);

        Map<String, Integer> map2 = new HashMap<>();
        map2.put("level", 6);
        list.add(map2);

        Map<String, Integer> map3 = new HashMap<>();
        map3.put("level", 11);
        list.add(map3);

        Map<String, Integer> map4 = new HashMap<>();
        map4.put("level", 4);
        list.add(map4);


        Collections.sort(list, (o1, o2) -> {
            int o1Val = Objects.isNull(o1.get("level")) ? -1 :  o1.get("level");
            int o2Val = Objects.isNull(o2.get("level")) ? -1 :  o2.get("level");
            return o2Val - o1Val;
        });

        list.stream().forEach(i -> {
            System.out.println(i);
        });

        System.out.println(list.get(0).get("level"));
    }
}
