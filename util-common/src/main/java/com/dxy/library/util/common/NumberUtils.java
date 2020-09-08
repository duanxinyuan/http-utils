package com.dxy.library.util.common;


import com.google.common.util.concurrent.AtomicDouble;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数字工具类
 * 提供精确的浮点数运算，包括加减乘除和四舍五入。
 * @author duanxinyuan
 * 2016/6/14 14:44
 */
public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {

    /**
     * 判断Integer是否不为空和0
     * @return true-不为空和0
     */
    public static boolean isNNZ(Number n) {
        if (null == n) {
            return false;
        }
        if (n instanceof Short) {
            return n.shortValue() != 0;
        } else if (n instanceof Integer || n instanceof AtomicInteger || n instanceof MutableInt) {
            return n.intValue() != 0;
        } else if (n instanceof Long || n instanceof AtomicLong || n instanceof MutableLong) {
            return n.longValue() != 0;
        } else if (n instanceof Float || n instanceof MutableFloat) {
            return n.floatValue() != 0;
        } else if (n instanceof Double || n instanceof AtomicDouble || n instanceof MutableDouble) {
            return n.doubleValue() != 0;
        } else if (n instanceof BigDecimal) {
            return n.doubleValue() != 0;
        } else {
            return n.doubleValue() != 0;
        }
    }

    /**
     * 判断Integer是否大于0
     * @return true-大于0
     */
    public static boolean isGTZ(Number n) {
        if (null == n) {
            return false;
        }
        if (n instanceof Short) {
            return n.shortValue() > 0;
        } else if (n instanceof Integer || n instanceof AtomicInteger || n instanceof MutableInt) {
            return n.intValue() > 0;
        } else if (n instanceof Long || n instanceof AtomicLong || n instanceof MutableLong) {
            return n.longValue() > 0;
        } else if (n instanceof Float || n instanceof MutableFloat) {
            return n.floatValue() > 0;
        } else if (n instanceof Double || n instanceof AtomicDouble || n instanceof MutableDouble) {
            return n.doubleValue() > 0;
        } else if (n instanceof BigDecimal) {
            return n.doubleValue() > 0;
        } else {
            return n.doubleValue() > 0;
        }
    }

    /**
     * 判断Integer是否小于0
     * @return true-大于0
     */
    public static boolean isLTZ(Number n) {
        if (null == n) {
            return false;
        }
        if (n instanceof Short) {
            return n.shortValue() < 0;
        } else if (n instanceof Integer || n instanceof AtomicInteger || n instanceof MutableInt) {
            return n.intValue() < 0;
        } else if (n instanceof Long || n instanceof AtomicLong || n instanceof MutableLong) {
            return n.longValue() < 0;
        } else if (n instanceof Float || n instanceof MutableFloat) {
            return n.floatValue() < 0;
        } else if (n instanceof Double || n instanceof AtomicDouble || n instanceof MutableDouble) {
            return n.doubleValue() < 0;
        } else if (n instanceof BigDecimal) {
            return n.doubleValue() < 0;
        } else {
            return n.doubleValue() < 0;
        }
    }

    /**
     * 加法运算
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的和
     */
    public static double add(Number v1, Number... v2) {
        return round(addNoRound(v1, v2));
    }

    /**
     * 加法运算
     * @param v1 被减数
     * @param v2 减数
     * @param scale 保留的小数点位数
     * @return 两个参数的和
     */
    public static double addForScale(Number v1, int scale, Number... v2) {
        return round(addNoRound(v1, v2), scale);
    }

    /**
     * 加法运算
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的和
     */
    public static double addNoRound(Number v1, Number... v2) {
        v1 = v1 == null ? 0 : v1;
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(v1));
        for (Number number : v2) {
            if (number != null) {
                bigDecimal = bigDecimal.add(new BigDecimal(String.valueOf(number)));
            }
        }
        return bigDecimal.doubleValue();
    }

    /**
     * 减法运算
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double subtract(Number v1, Number... v2) {
        return round(subtractNoRound(v1, v2));
    }

    /**
     * 减法运算
     * @param v1 被减数
     * @param v2 减数
     * @param scale 保留的小数点位数
     * @return 两个参数的差
     */
    public static double subtractForScale(Number v1, int scale, Number... v2) {
        return round(subtractNoRound(v1, v2), scale);
    }

    /**
     * 减法运算
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double subtractNoRound(Number v1, Number... v2) {
        v1 = v1 == null ? 0 : v1;
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(v1));
        for (Number number : v2) {
            if (number != null) {
                bigDecimal = bigDecimal.subtract(new BigDecimal(String.valueOf(number)));
            }
        }
        return bigDecimal.doubleValue();
    }

    /**
     * 乘法运算
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double multiply(Number v1, Number... v2) {
        return round(multiplyNoRound(v1, v2));
    }

    /**
     * 乘法运算
     * @param v1 被乘数
     * @param v2 乘数
     * @param scale 保留的小数点位数
     * @return 两个参数的积
     */
    public static double multiplyForScale(Number v1, int scale, Number... v2) {
        double multiply = multiplyNoRound(v1, v2);
        return round(multiply, scale);
    }

    /**
     * 乘法运算
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double multiplyNoRound(Number v1, Number... v2) {
        v1 = v1 == null ? 0 : v1;
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(v1));
        for (Number number : v2) {
            if (number != null) {
                bigDecimal = bigDecimal.multiply(new BigDecimal(String.valueOf(number)));
            }
        }
        return bigDecimal.doubleValue();
    }

    /**
     * 除法运算
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double divide(Number v1, Number... v2) {
        return divideForScale(v1, 2, v2);
    }

    /**
     * 除法运算
     * @param v1 被除数
     * @param v2 除数
     * @param scale 保留的小数点位数
     * @return 两个参数的商
     */
    public static double divideForScale(Number v1, int scale, Number... v2) {
        v1 = v1 == null ? 0 : v1;
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(v1));
        for (Number number : v2) {
            if (number != null) {
                bigDecimal = bigDecimal.setScale(scale + 1, RoundingMode.HALF_UP);
                bigDecimal = bigDecimal.divide(new BigDecimal(String.valueOf(number)), RoundingMode.HALF_UP);
            }
        }
        return round(bigDecimal.doubleValue(), scale);
    }

    /**
     * 保留两位小数，四舍五入
     */
    public static double round(String v) {
        return round(v, 2);
    }

    /**
     * 保留两位小数，四舍五入
     */
    public static double round(Number v) {
        return round(v, 2);
    }

    /**
     * 四舍五入
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(Number v, int scale) {
        if (null == v) {
            return 0;
        }
        return round(String.valueOf(v), scale);
    }

    /**
     * 四舍五入
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(String v, int scale) {
        if (StringUtils.isEmpty(v)) {
            return 0;
        }
        if (scale < 0) {
            throw new IllegalArgumentException("scale can't be less than 0");
        }
        BigDecimal b = new BigDecimal(v);
        return b.setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 四舍五入取整
     */
    public static int roundInt(String v) {
        if (StringUtils.isEmpty(v)) {
            return 0;
        }
        BigDecimal b = new BigDecimal(v);
        return b.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    /**
     * 四舍五入取整
     */
    public static int roundInt(Number v) {
        if (null == v) {
            return 0;
        }
        return roundInt(String.valueOf(v));
    }

    /**
     * 得到一个在指定范围内的随机数
     * @param start 开始数
     * @param end 结束数（范围内不包含本数）
     * @return 随机数
     */
    public static int getRandomInt(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("random number range error");
        }
        Random random = new Random();
        int fraction = random.nextInt(end - start);
        return start + fraction;
    }

}
