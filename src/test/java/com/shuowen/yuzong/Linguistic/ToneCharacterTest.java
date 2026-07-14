package com.shuowen.yuzong.Linguistic;

public class ToneCharacterTest
{
    public static void main(String[] args)
    {
        String s = "̴̵̶̷̸̡̢̧̨̛̖̗̘̙̜̝̞̟̠̣̤̥̦̩̪̫̬̭̮̯̰̱̲̳̹̺̻̼͇͈͉͍͎̀́̂̃̄̅̆̇̈̉̊̋̌̍̎̏̐̑̒̓̔̽̾̿̀́͂̓̈́͆͊͋͌̕̚ͅ͏͓͔͕͖͙͚͐͑͒͗͛ͣͤͥͦͧͨͩͪͫͬͭͮͯ͘͜͟͢͝͞͠͡";
        for (var c : "aoeiuü".toCharArray())
        {
            for (var i : s.toCharArray())
                System.out.print(""+c + i + " ");
            System.out.println();
        }
    }
}
