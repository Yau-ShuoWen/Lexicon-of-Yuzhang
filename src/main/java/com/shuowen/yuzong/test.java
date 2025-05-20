package com.shuowen.yuzong;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Linguistics.IPA.IPAtransfer;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;

import java.util.Scanner;

public class test
{

    public static void main(String[] args)
    {
        while (true)
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println(NamPinyin.parseAndReplace(scanner.nextLine()));
        }
    }
//    public static void main(String[] args) throws Exception {
//        String jsonStr = "{\"name\":\"Alice\", \"age\":25}";
//
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode rootNode = mapper.readTree(jsonStr);
//
//        String name = rootNode.get("name").asText();
//        int age = rootNode.get("age").asInt();
//
//        System.out.println("Name: " + name + ", Age: " + age);
//    }
}
