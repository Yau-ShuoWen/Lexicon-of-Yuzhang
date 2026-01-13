package com.shuowen.yuzong.Tool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author github.com/rocicorp
 */

public class FractionalIndexing
{

    private static final String BASE_62_DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private FractionalIndexing()
    {
        // Utility class - prevent instantiation
    }

    private static String midpoint(String a, String b, String digits)
    {
        char zero = digits.charAt(0);
        if (b != null && a.compareTo(b) >= 0)
        {
            throw new RuntimeException(a + " >= " + b);
        }
        if (!a.isEmpty() && a.charAt(a.length() - 1) == zero ||
                (b != null && !b.isEmpty() && b.charAt(b.length() - 1) == zero))
        {
            throw new RuntimeException("trailing zero");
        }
        if (b != null)
        {
            int n = 0;
            while (true)
            {
                char aChar = (n >= a.length()) ? zero : a.charAt(n);
                char bChar = (n >= b.length()) ? zero : b.charAt(n);
                if (aChar == bChar)
                {
                    n++;
                } else
                {
                    break;
                }
            }
            if (n > 0)
            {
                return b.substring(0, n) + midpoint(a.substring(n), b.substring(n), digits);
            }
        }

        int digitA = a.isEmpty() ? 0 : digits.indexOf(a.charAt(0));
        int digitB = (b == null || b.isEmpty()) ? digits.length() : digits.indexOf(b.charAt(0));

        if (digitB - digitA > 1)
        {
            int midDigit = Math.round(0.5f * (digitA + digitB));
            return String.valueOf(digits.charAt(midDigit));
        } else
        {
            if (b != null && b.length() > 1)
            {
                return b.substring(0, 1);
            } else
            {
                return digits.charAt(digitA) + midpoint(a.length() > 1 ? a.substring(1) : "", null, digits);
            }
        }
    }

    private static void validateInteger(String integer)
    {
        if (integer.length() != getIntegerLength(String.valueOf(integer.charAt(0))))
        {
            throw new RuntimeException("invalid integer part of order key: " + integer);
        }
    }

    private static int getIntegerLength(String head)
    {
        if (head.compareTo("a") >= 0 && head.compareTo("z") <= 0)
        {
            return head.charAt(0) - 'a' + 2;
        } else if (head.compareTo("A") >= 0 && head.compareTo("Z") <= 0)
        {
            return 'Z' - head.charAt(0) + 2;
        } else
        {
            throw new RuntimeException("invalid order key head: " + head);
        }
    }

    private static String getIntegerPart(String key)
    {
        int integerPartLength = getIntegerLength(String.valueOf(key.charAt(0)));
        if (integerPartLength > key.length())
        {
            throw new RuntimeException("invalid order key: " + key);
        }
        return key.substring(0, integerPartLength);
    }

    private static void validateOrderKey(String key, String digits)
    {
        if (key.equals("A" + String.valueOf(digits.charAt(0)).repeat(26)))
        {
            throw new RuntimeException("invalid order key: " + key);
        }

        String integerPart = getIntegerPart(key);
        String fractionalPart = key.substring(integerPart.length());
        if (!fractionalPart.isEmpty() && fractionalPart.charAt(fractionalPart.length() - 1) == digits.charAt(0))
        {
            throw new RuntimeException("invalid order key: " + key);
        }
    }

    private static String incrementInteger(String x, String digits)
    {
        validateInteger(x);
        String head = String.valueOf(x.charAt(0));
        List<String> digs = new ArrayList<>();
        for (int i = 1; i < x.length(); i++)
        {
            digs.add(String.valueOf(x.charAt(i)));
        }

        boolean carry = true;
        for (int i = digs.size() - 1; i >= 0; i--)
        {
            if (!carry) break;
            int d = digits.indexOf(digs.get(i)) + 1;
            if (d == digits.length())
            {
                digs.set(i, String.valueOf(digits.charAt(0)));
            } else
            {
                digs.set(i, String.valueOf(digits.charAt(d)));
                carry = false;
            }
        }

        if (carry)
        {
            if (head.equals("Z"))
            {
                return "a" + digits.charAt(0);
            }
            if (head.equals("z"))
            {
                return null;
            }
            String h = String.valueOf((char) (head.charAt(0) + 1));
            if (h.compareTo("a") > 0)
            {
                digs.add(String.valueOf(digits.charAt(0)));
            } else
            {
                if (!digs.isEmpty())
                {
                    digs.remove(digs.size() - 1);
                }
            }
            return h + String.join("", digs);
        } else
        {
            return head + String.join("", digs);
        }
    }

    private static String decrementInteger(String x, String digits)
    {
        validateInteger(x);
        String head = String.valueOf(x.charAt(0));
        List<String> digs = new ArrayList<>();
        for (int i = 1; i < x.length(); i++)
        {
            digs.add(String.valueOf(x.charAt(i)));
        }

        boolean borrow = true;
        for (int i = digs.size() - 1; i >= 0; i--)
        {
            if (!borrow) break;
            int d = digits.indexOf(digs.get(i)) - 1;
            if (d == -1)
            {
                digs.set(i, String.valueOf(digits.charAt(digits.length() - 1)));
            } else
            {
                digs.set(i, String.valueOf(digits.charAt(d)));
                borrow = false;
            }
        }

        if (borrow)
        {
            if (head.equals("a"))
            {
                return "Z" + digits.charAt(digits.length() - 1);
            }
            if (head.equals("A"))
            {
                return null;
            }
            String h = String.valueOf((char) (head.charAt(0) - 1));
            if (h.compareTo("Z") < 0)
            {
                digs.add(String.valueOf(digits.charAt(digits.length() - 1)));
            } else
            {
                if (!digs.isEmpty())
                {
                    digs.remove(digs.size() - 1);
                }
            }
            return h + String.join("", digs);
        } else
        {
            return head + String.join("", digs);
        }
    }

    public static String generateFractionalIndexBetween(String a, String b)
    {
        return generateFractionalIndexBetween(a, b, BASE_62_DIGITS);
    }

    public static String generateFractionalIndexBetween(String a, String b, String digits)
    {
        if (a != null)
        {
            validateOrderKey(a, digits);
        }
        if (b != null)
        {
            validateOrderKey(b, digits);
        }
        if (a != null && b != null && a.compareTo(b) >= 0)
        {
            throw new RuntimeException(a + " >= " + b);
        }

        if (a == null)
        {
            if (b == null)
            {
                return "a" + digits.charAt(0);
            }

            String integerPartB = getIntegerPart(b);
            String fractionalPartB = b.substring(integerPartB.length());
            if (integerPartB.equals("A" + String.valueOf(digits.charAt(0)).repeat(26)))
            {
                return integerPartB + midpoint("", fractionalPartB, digits);
            }
            if (integerPartB.compareTo(b) < 0)
            {
                return integerPartB;
            }
            String decremented = decrementInteger(integerPartB, digits);
            if (decremented == null)
            {
                throw new RuntimeException("cannot decrement any more");
            }
            return decremented;
        }

        if (b == null)
        {
            String integerPartA = getIntegerPart(a);
            String fractionalPartA = a.substring(integerPartA.length());
            String incremented = incrementInteger(integerPartA, digits);
            if (incremented != null)
            {
                return incremented;
            } else
            {
                return integerPartA + midpoint(fractionalPartA, null, digits);
            }
        }

        String integerPartA = getIntegerPart(a);
        String fractionalPartA = a.substring(integerPartA.length());
        String integerPartB = getIntegerPart(b);
        String fractionalPartB = b.substring(integerPartB.length());

        if (integerPartA.equals(integerPartB))
        {
            return integerPartA + midpoint(fractionalPartA, fractionalPartB, digits);
        }

        String incremented = incrementInteger(integerPartA, digits);
        if (incremented == null)
        {
            throw new RuntimeException("cannot increment any more");
        }
        if (incremented.compareTo(b) < 0)
        {
            return incremented;
        }
        return integerPartA + midpoint(fractionalPartA, null, digits);
    }

    public static List<String> generateNFractionalIndicesBetween(String a, String b, int n)
    {
        return generateNFractionalIndicesBetween(a, b, n, BASE_62_DIGITS);
    }

    public static List<String> generateNFractionalIndicesBetween(String a, String b, int n, String digits)
    {
        if (n == 0)
        {
            return new ArrayList<>();
        }
        if (n == 1)
        {
            return List.of(generateFractionalIndexBetween(a, b, digits));
        }

        if (b == null)
        {
            List<String> result = new ArrayList<>();
            String c = generateFractionalIndexBetween(a, b, digits);
            result.add(c);
            for (int i = 0; i < n - 1; i++)
            {
                c = generateFractionalIndexBetween(c, b, digits);
                result.add(c);
            }
            return result;
        }

        if (a == null)
        {
            List<String> result = new ArrayList<>();
            String c = generateFractionalIndexBetween(a, b, digits);
            result.add(c);
            for (int i = 0; i < n - 1; i++)
            {
                c = generateFractionalIndexBetween(a, c, digits);
                result.add(c);
            }
            java.util.Collections.reverse(result);
            return result;
        }

        int mid = (int) Math.floor(n / 2.0);
        String c = generateFractionalIndexBetween(a, b, digits);
        List<String> result = new ArrayList<>();
        result.addAll(generateNFractionalIndicesBetween(a, c, mid, digits));
        result.add(c);
        result.addAll(generateNFractionalIndicesBetween(c, b, n - mid - 1, digits));
        return result;
    }
}

// 第一版草稿

//    // 可以修改字符集，所以编码不要使用任何真正的字符，所有的描述都使用十进制描述，但是之后可以直接修改
//    private static final char[] NUMBERS = "0123456789".toCharArray();
//
//    private static int getIndex(char ch)
//    {
//        if (NumberTool.closeBetween(ch, '0', '9')) return ch - '0';
//        // if (NumberTool.closeBetween(ch, 'a', 'z')) return ch - 'a' + 10;
//        // if (NumberTool.closeBetween(ch, 'A', 'Z')) return ch - 'A' + 36;
//        throw new IllegalArgumentException("超出范围");
//    }
//
//    private static final char NIL = NUMBERS[0]; // 最小的数字，开区间的边界
//    private static final char MIN = NUMBERS[1]; // 可以往末尾加的最小的数字（0不可以）
//    private static final char MAX = NUMBERS[NUMBERS.length - 1]; // 可以往末尾加的最大的数字
//    private static final char MID = average(MIN, MAX).getValue();
//
//    /**
//     * 检查字符串是否有效，判断依据，末尾是否是零
//     */
//    public static boolean isValid(String... str)
//    {
//        try
//        {
//            for (String s : str) if (StringTool.back(s) == NIL) return false;
//            return true;
//        } catch (IllegalArgumentException e) //back函数抛出异常，也就是字符串为空
//        {
//            return false;
//        }
//    }
//
//    /**
//     * 返回一个能充分应用区间的端点值
//     */
//    public static Pair<String, String> endPoint()
//    {
//        return Pair.of(MIN + "", MAX + ""); // 返回 0.1 和 0.9
//    }
//
//    private static Maybe<Character> average(char left, char right)
//    {
//        int l = getIndex(left);
//        int r = getIndex(right);
//        int m = (l + r) / 2;
//
//        return (ObjectTool.existEqual(m, l, r)) ?
//                Maybe.nothing() : Maybe.exist(NUMBERS[m]);
//    }
//
//
//    /**
//     *
//     */
//    public static Maybe<String> midPoint(String left, String right)
//    {
//        // 不是有效的索引    或者   索引left 大于等于 right
//        if (!isValid(left, right) || left.compareTo(right) >= 0) return Maybe.nothing();
//
//        for (int idx = 0; idx < Math.min(left.length(), right.length()); idx++)
//        {
//            if (left.charAt(idx) != right.charAt(idx))
//            {
//                var mid = average(left.charAt(idx), right.charAt(idx));
//                if (mid.isValid()) //123 125 -> 124
//                {
//                    return Maybe.exist(left.substring(0, idx) + mid.getValue());
//                }
//                else // 123 124
//                {
//                    // idx ：第一位不匹配的，str1，所有匹配的加上这一位
//                    String prefix = left.substring(0, idx + 1);
//
//                    var ch = StringTool.tryCharAt(left, idx + 1);
//                    if (ch.isValid())
//                    {
//                        char nextchar = ch.getValue();
//
//                        if (nextchar == MAX) return Maybe.exist(prefix + MAX + MID); // 0.29 0.3 -> 0.295
//                        if (nextchar + 1 == MAX) return Maybe.exist(prefix + MAX);   // 0.28 0.3 -> 0.29
//
//                        return average(nextchar, MAX).handleIfExist(s -> prefix + s);
//                    }
//                    else // 0.23 0.24 0.235
//                    {
//                        return average(MIN, MAX).handleIfExist(s -> prefix + s);
//                    }
//                }
//            }
//        }
//        throw new IllegalArgumentException("未知文字");
//    }

//    private Maybe<String> handleDifferent(Maybe<Character> left, Maybe<Character> right)
//    {
//        if (Maybe.allValidAndEqual(left, right)) throw new RuntimeException("不一样的才能处理");
//        if (!left.isValid() && !right.isValid()) throw new RuntimeException("这一位两个都没有了");
//
//        if (left.isValid()) // 小的数字更长一些 0.39 0.4
//        {
//            char ch = left.getValue();
//
//
//        }
//
//    }


//     if (right.length() > idx + 1) return Maybe.exist(right.substring(0, idx + 1));
//                    else return Maybe.exist(left.substring(0, idx + 1) + MID);
//    return Maybe.exist(left + midPoint("", right.substring(left.length())));

// 处理流程
// 0.123  0.123  异常，两个字符串相等
// 0.124  0.123  异常，前者大于后者
// 0.1237 0.124
// 1227 124  123


// 第二版草稿


//    private static BigDecimal fromString(String s)
//    {
//        if (!s.matches("\\d+")) throw new NumberFormatException("字符串不是纯数字");
//
//        BigDecimal d = new BigDecimal("0." + s);
//        if (d.compareTo(BigDecimal.ZERO) == 0) throw new IllegalArgumentException("zero");
//        else return d;
//    }
//
//    private static String toString(BigDecimal d)
//    {
//        return d.stripTrailingZeros().toPlainString().replace("0.", "");  // 去除"0."
//    }
//
//
//
//    /**
//     *
//     */
//    public static Pair<String, String> endPoint()
//    {
//        return Pair.of(
//                toString(BigDecimal.valueOf(0.1)),
//                toString(BigDecimal.valueOf(0.9))
//        );
//    }
//
//    @SuppressWarnings ("BigDecimalMethodWithoutRoundingCalled")
//    public static String midPoint(String left, String right)
//    {
//        BigDecimal l = fromString(left);
//        BigDecimal r = fromString(right);
//
//        if (l.compareTo(r) >= 0) throw new IllegalArgumentException();
//
//        var ans = l.add(r).divide(new BigDecimal(2));
//
//        return toString(ans);
//    }

// 第三版废弃部分

//        @SuppressWarnings ("BigDecimalMethodWithoutRoundingCalled")
//        var ans = l.add(r).divide(new BigDecimal(2)); // 这里除以二能保证除尽