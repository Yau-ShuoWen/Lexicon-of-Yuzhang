//package com.shuowen.yuzong.Linguistics.Mandarin;
//
//import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
//
//public class RussiaScheme
//{
//    private enum Scheme
//    {
//        Russia,
//        RussiaNew,
//        Ukraine,
//        Belarus,
//        Serbia,
//        Mavrfonis,
//        Bulgaria
//    }
//
//    public String format(Zhuyin zy, Scheme scheme)
//    {
//        var pair = initial(zy, scheme);
//        String initial = pair.getLeft();
//        boolean zero = pair.getRight();
//
//        // 空韵的处理流程：只要有，就拼好返回
//        String emptyYun = zicisi(zy, scheme);
//        if (!emptyYun.isEmpty()) return initial + emptyYun;
//
//        String yun = last(zy, scheme, zero);
//
//        return initial + yun;
//    }
//
//    private Pair<String, Boolean> initial(Zhuyin zy, Scheme scheme)
//    {
//        boolean zero = false;
//        String s = switch (zy.getInitial())
//        {
//            case "ㄅ" -> "б";
//            case "ㄆ" -> "п";
//            case "ㄇ" -> "м";
//            case "ㄈ" -> "ф";
//            case "ㄉ" -> "д";
//            case "ㄊ" -> "т";
//            case "ㄋ" -> "н";
//            case "ㄌ" -> "л";
//            case "ㄍ" -> "г";
//            case "ㄎ" -> "к";
//            case "ㄏ" -> "х";
//            case "ㄐ" -> switch (scheme)
//            {
//                case Russia, RussiaNew, Ukraine, Belarus -> "цз";
//                case Serbia -> "ђ";
//                case Mavrfonis -> "џј";
//                case Bulgaria -> "дз";
//            };
//            case "ㄑ" -> switch (scheme)
//            {
//                case Russia, RussiaNew, Ukraine, Belarus, Bulgaria -> "ц";
//                case Serbia -> "ћ";
//                case Mavrfonis -> "чј";
//            };
//            case "ㄒ" -> switch (scheme)
//            {
//                case Russia, RussiaNew, Ukraine, Belarus, Bulgaria, Serbia -> "с";
//                case Mavrfonis -> "шј";
//            };
//            case "ㄓ" -> switch (scheme)
//            {
//                case Russia, Ukraine, Belarus -> "чж";
//                case RussiaNew -> "ж";
//                case Serbia, Mavrfonis -> "џ";
//                case Bulgaria -> "дж";
//            };
//            case "ㄔ" -> "ч";
//            case "ㄕ" -> "ш";
//            case "ㄖ" -> switch (scheme)
//            {
//                case Russia, Ukraine, Belarus, Bulgaria, Serbia, Mavrfonis -> "ж";
//                case RussiaNew -> "р";
//            };
//            case "ㄗ" -> switch (scheme)
//            {
//                case Russia, Ukraine, Belarus -> "цз";
//                case RussiaNew -> "з";
//                case Serbia -> "ц";
//                case Mavrfonis -> "???";
//                case Bulgaria -> "дз";
//            };
//            case "ㄘ" -> "ц";
//            case "ㄙ" -> "с";
//            default ->
//            {
//                zero = true;
//                yield "";
//            }
//        };
//        return Pair.of(s, zero);
//    }
//
//    private String zicisi(Zhuyin zy, Scheme scheme)
//    {
//
//        return switch (zy.toStringWithoutTone())
//        {
//            case "ㄓ", "ㄔ", "ㄕ", "ㄖ" -> switch (scheme)
//            {
//                case Russia, RussiaNew, Ukraine, Serbia, Mavrfonis -> "и";
//                case Belarus -> "ы";
//                case Bulgaria -> "ъ";
//            };
//            case "ㄗ", "ㄘ", "ㄙ" -> switch (scheme)
//            {
//                case Russia, RussiaNew, Belarus -> "";
//                case Ukraine, Serbia, Mavrfonis -> "и";
//                case Bulgaria -> "ъ";
//            };
//            default -> "";
//        };
//    }
//а о э э ай эй ао оу ань энь ан эн эр и я - е яо ю	янь	инь	ян ин у	уа/ва о/во уай/вай уй/вэй уань/вань	унь/вэнь уан/ван ун/вэн юй юэ юань юнь юн
//    private String last(Zhuyin zy, Scheme scheme, boolean zero)
//    {
//        return switch (zy.getMiddle() + zy.getLast())
//        {
//            case "ㄚ" -> "а";
//            case "ㄛ" -> "о";
//            case "ㄜ" -> switch (scheme)
//            {
//                case Russia, RussiaNew, Belarus -> "э";
//                case Ukraine, Serbia, Mavrfonis -> "е";
//                case Bulgaria -> "ъ";
//            };
//            case "ㄞ" -> switch (scheme)
//            {
//                // 偏差：塞尔维亚j韵尾 马其顿反n韵尾
//                case Russia, RussiaNew, Ukraine, Belarus, Bulgaria -> "ай";
//                case Serbia -> "ај";
//                case Mavrfonis -> "аи";
//            };
//            case "ㄟ" -> switch (scheme)
//            {
//                // 偏差：
//                // 1. 塞尔维亚j韵尾 马其顿反n韵尾
//                // 2. 乌克兰 白俄罗斯 塞尔维亚 马其顿 正e
//                case Russia, RussiaNew, Belarus -> "эй";
//                case Ukraine, Bulgaria -> "ей";
//                case Serbia -> "еј";
//                case Mavrfonis -> "еи";
//            };
//            case "ㄠ" -> switch (scheme)
//            {
//                // 偏差：新版本追本溯源y韵尾 白俄罗斯A音化（wiki）
//                case Russia, Ukraine, Serbia, Mavrfonis, Bulgaria -> "ао";
//                case RussiaNew -> "aу";
//                case Belarus -> "aa";
//            };
//            case "ㄡ" -> "оу";
//            case "ㄢ" -> switch (scheme)
//            {
//                // 偏差：塞尔维亚 马其顿：后鼻音自有办法 保加利亚：前后鼻音不分
//                case Russia, RussiaNew, Ukraine, Belarus -> "ань";
//                case Serbia, Mavrfonis, Bulgaria -> "ан";
//            };
//            case "ㄣ" -> switch (scheme)
//            {
//                // 偏差：
//                // 1. 乌克兰 塞尔维亚 马其顿 正e  保加利亚：前后鼻音特殊b
//                // 2. 塞尔维亚 马其顿：后鼻音自有办法 保加利亚：前后鼻音不分
//                case Russia, RussiaNew, Belarus -> "энь";
//                case Ukraine -> "ень";
//                case Serbia, Mavrfonis -> "ен";
//                case Bulgaria -> "ън";
//            };
//            case "ㄤ" -> switch (scheme)
//            {
//                // 偏差：塞尔维亚 马其顿：后鼻音自有办法
//                case Russia, RussiaNew, Ukraine, Belarus, Bulgaria -> "ан";
//                case Serbia, Mavrfonis -> "анг";
//            };
//            case "ㄥ" -> switch (scheme)
//            {
//                // 偏差：
//                // 1. 乌克兰 塞尔维亚 马其顿 正e  保加利亚：前后鼻音特殊b
//                // 2. 塞尔维亚 马其顿：后鼻音自有办法
//                case Russia, RussiaNew, Belarus -> "эн";
//                case Ukraine -> "ен";
//                case Serbia, Mavrfonis -> "енг";
//                case Bulgaria -> "ън";
//            };
//            case "ㄦ" -> switch (scheme)
//            {
//                // 偏差： 乌克兰 塞尔维亚 马其顿 保加利亚：正e
//                case Russia, RussiaNew, Belarus -> "эр";
//                case Ukraine, Serbia, Mavrfonis, Bulgaria -> "ер";
//            };
//
//            case "ㄧ" -> switch (scheme)
//            {
//                case Russia -> "";
//                case RussiaNew -> "";
//                case Ukraine -> "";
//                case Belarus -> "";
//                case Serbia -> "";
//                case Mavrfonis -> "";
//                case Bulgaria -> "";
//            };
//            case "ㄧㄚ" -> switch (scheme)
//            {
//                case Russia -> "";
//                case RussiaNew -> "";
//                case Ukraine -> "";
//                case Belarus -> "";
//                case Serbia -> "";
//                case Mavrfonis -> "";
//                case Bulgaria -> "";
//            };
//            case "ㄧㄛ" -> switch (scheme)
//            {
//                case Russia -> "";
//                case RussiaNew -> "";
//                case Ukraine -> "";
//                case Belarus -> "";
//                case Serbia -> "";
//                case Mavrfonis -> "";
//                case Bulgaria -> "";
//            };
//            case "ㄧㄝ" -> switch (scheme)
//            {
//                case Russia -> "";
//                case RussiaNew -> "";
//                case Ukraine -> "";
//                case Belarus -> "";
//                case Serbia -> "";
//                case Mavrfonis -> "";
//                case Bulgaria -> "";
//            };
//            case "ㄧㄠ" -> switch (scheme)
//            {
//                case Russia -> "";
//                case RussiaNew -> "";
//                case Ukraine -> "";
//                case Belarus -> "";
//                case Serbia -> "";
//                case Mavrfonis -> "";
//                case Bulgaria -> "";
//            };
//            case "ㄧㄡ" -> switch (scheme)
//            {
//                case Russia -> "";
//                case RussiaNew -> "";
//                case Ukraine -> "";
//                case Belarus -> "";
//                case Serbia -> "";
//                case Mavrfonis -> "";
//                case Bulgaria -> "";
//            };
//            case "ㄧㄢ" -> switch (scheme)
//            {
//                case Russia -> "";
//                case RussiaNew -> "";
//                case Ukraine -> "";
//                case Belarus -> "";
//                case Serbia -> "";
//                case Mavrfonis -> "";
//                case Bulgaria -> "";
//            };
//            case "ㄧㄣ" -> switch (scheme)
//            {
//                case Russia -> "";
//                case RussiaNew -> "";
//                case Ukraine -> "";
//                case Belarus -> "";
//                case Serbia -> "";
//                case Mavrfonis -> "";
//                case Bulgaria -> "";
//            };
//            case "ㄧㄤ" -> switch (scheme)
//            {
//                case Russia -> "";
//                case RussiaNew -> "";
//                case Ukraine -> "";
//                case Belarus -> "";
//                case Serbia -> "";
//                case Mavrfonis -> "";
//                case Bulgaria -> "";
//            };
//            case "ㄧㄥ" -> switch (scheme)
//            {
//                case Russia -> "";
//                case RussiaNew -> "";
//                case Ukraine -> "";
//                case Belarus -> "";
//                case Serbia -> "";
//                case Mavrfonis -> "";
//                case Bulgaria -> "";
//            };
//
//            case "ㄨ" -> "";
//            case "ㄨㄚ" -> "";
//            case "ㄨㄛ" -> "";
//            case "ㄨㄞ" -> "";
//            case "ㄨㄟ" -> "";
//            case "ㄨㄢ" -> "";
//            case "ㄨㄣ" -> "";
//            case "ㄨㄤ" -> "";
//            case "ㄨㄥ" -> "";
//
//            case "ㄩ" -> "";
//            case "ㄩㄝ" -> "";
//            case "ㄩㄢ" -> "";
//            case "ㄩㄣ" -> "";
//            case "ㄩㄥ" -> "";
//            default -> throw new RuntimeException("");
//        }
//    }
//}
