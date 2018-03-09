# China-block
## P2P
### Kademlia算法
[Kademlia算法](https://app.yinxiang.com/shard/s48/nl/13169588/0ef61b48-a1bc-4d94-b96a-ce055efb2694)\
[Kademlia for python](https://github.com/bmuller/kademlia)
```python

```



# 动态规划
```java
// TODO 定义状态转义方程
import org.apache.commons.math3.util.FastMath;

/**
 * Created by mike on 2018/2/5.
 */
public class DynamicProgramming {
    public static void main(String[] args) {
        int[] prices = new int[]{2, 3, 5, 6, 4, 3, 5, 7, 6};
        DynamicProgramming dynamicProgramming = new DynamicProgramming();
        System.out.println(dynamicProgramming.maxProfit(prices));
        System.out.println(dynamicProgramming.maxProfit2(prices));
        System.out.println(dynamicProgramming.maxProfit(2, prices));
    }

    /**
     * Best Time to Buy and Sell Stock III
     * 用一个数组表示股票每天的价格，数组的第i个数表示股票在第i天的价格。最多交易两次，手上最多只能持有一支股票，求最大收益。
     * 双向动态规划
     * 复杂度：时间 O(N) 空间 O(N)
     *
     * @param prices
     * @return
     */
    public int maxProfit(int[] prices) {
        if (prices.length == 0) return 0;
        int[] left = new int[prices.length];
        int[] right = new int[prices.length];
        int leftMin = prices[0];
        int rightMax = prices[prices.length - 1];
        int sum = 0;
        //计算左半段最大收益
        for (int i = 1; i < prices.length; i++) {
            leftMin = Math.min(prices[i], leftMin);
            left[i] = Math.max(prices[i] - leftMin, left[i - 1]);
        }
        //计算右半段最大收益
        for (int i = prices.length - 2; i >= 0; i--) {
            rightMax = Math.max(prices[i], rightMax);
            right[i] = Math.max(rightMax - prices[i], right[i + 1]);
        }
        //找出两次交易最大收益组合
        for (int i = 0; i < prices.length; i++) {
            if ((left[i] + right[i]) > sum)
                sum = left[i] + right[i];
        }
        return sum;
    }

    /**
     * Best Time to Buy and Sell Stock III
     * 用一个数组表示股票每天的价格，数组的第i个数表示股票在第i天的价格。最多交易两次，手上最多只能持有一支股票，求最大收益。
     * 滚动扫描法
     * 复杂度：时间 O(N) 空间 O(1)
     *
     * @param prices
     * @return
     */
    public int maxProfit2(int[] prices) {
        int hold1 = Integer.MIN_VALUE, hold2 = Integer.MIN_VALUE;
        int release1 = 0, release2 = 0;
        for (int i = 0; i < prices.length; i++) {
            //在该价格点卖出第二笔股票后手里剩的钱，等于上一轮买入第二笔股票后手里剩的钱加上卖出当前股票价格的钱，或者上一轮卖出第二笔股票后手里剩的钱两者中较大的
            release2 = Math.max(release2, hold2 + prices[i]);
            //在该价格点买入第二笔股票后手里剩的钱，等于上一轮卖出第一笔股票后手里剩的钱减去买入当前股票价格的钱，或者上一轮买入第二笔股票后手里剩的钱两者中较大的
            hold2 = Math.max(hold2, release1 - prices[i]);
            //在该价格点卖出第一笔股票后手里剩的钱，等于上一轮买入第一笔股票后手里剩的钱加上卖出当前股票价格的钱，或者上一轮卖出第一笔股票后手里剩的钱两者中较大的
            release1 = Math.max(release1, hold1 + prices[i]);
            //在该价格点买入第一笔股票后手里剩的钱，等于初始资金减去买入当前股票价格的钱或者初始资金（不买）中较大的
            hold1 = Math.max(hold1, -prices[i]);
        }
        return release2;
    }

    /**
     * Best Time to Buy and Sell Stock IV
     * 题意：用一个数组表示股票每天的价格，数组的第i个数表示股票在第i天的价格。最多交易k次，手上最多只能持有一支股票，求最大收益。
     * 滚动扫描法
     * 复杂度：时间 O(N) 空间 O(k)
     *
     * @param k
     * @param prices
     * @return
     */
    public int maxProfit(int k, int[] prices) {
        //用II的解法优化k > prices.length / 2的情况
        if (k > prices.length / 2) {
            int sum = 0;
            for (int i = 1; i < prices.length; i++) {
                if (prices[i] > prices[i - 1]) sum += prices[i] - prices[i - 1];
            }
            return sum;
        }
        //初始化买卖股票后剩余金钱的数组
        int[] release = new int[k + 1];
        int[] hold = new int[k + 1];
        for (int i = 0; i < k + 1; i++) {
            hold[i] = Integer.MIN_VALUE;
        }
        for (int i = 0; i < prices.length; i++) {
            for (int j = 1; j < k + 1; j++) {
                //卖出第j笔交易，所剩余的钱
                release[j] = Math.max(release[j], hold[j] + prices[i]);
                //买入第j笔交易，所剩余的钱
                hold[j] = Math.max(hold[j], release[j - 1] - prices[i]);
            }
        }
        return release[k];
    }

    /**
     * dynamicProgramming:DP 动态规划算法
     * 如果区间和商品长度为小数的话，放大转化为整数传入即可
     * 状态转移方程：dp[i] = max{dp[i],dp[i-g[i]]+v[i]}
     *
     * @param size  区间长度
     * @param goods 商品数据，每件商品占据的长度
     * @param value 每件商品对应的价值
     * @return
     */
    public static double DP(final int size, final int[] goods, final double[] value) {
        double[] f = new double[size + 1];
        for (int i = 0; i < f.length; i++) {
            f[i] = 0;
        }
        for (int i = 0; i < goods.length; i++) {
            for (int j = goods[i]; j < f.length; j++) {
                f[j] = FastMath.max(f[j], f[j - goods[i]] + value[i]);
            }
        }
        //最佳收益率
        return f[size];
    }
}
```
## 背包问题

## 

# 分类、回归

# 相似度算法 tf*idf

# 贪心穷尽

# 最小化 凸优化问题 

# 遗传算法

# N* 等寻到算法

[各类算法](https://algorithm.yuanbin.me/zh-hans/dynamic_programming/distinct_subsequences.html)
[各类算法](https://github.com/billryan/algorithm-exercise)

# OAuth2.0/JWT
参考 Share-Java