package main;

import com.beta.domain.dao.generated.entity.BankFinancialProductEntity;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.FastMath;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by SoftStar on 2017/9/27.
 */

/**
 * 数学之美
 * 信息熵、增益
 * TF-IDF
 * 布尔运算
 * 分词中的最长匹配
 * 隐马尔科夫和朴素贝叶斯和贝叶斯信念网络
 * 状态机
 * 向量集合运算，余弦定理
 * 矩阵运算、奇异值分解
 * 最大熵模型（金融）
 * 布隆过滤器
 * 香农定理
 * 期望最大化
 * 分类和回归
 * 分治法MapReduce
 * 动态规划
 * Huffman编码
 */
public class StatisticsUtils {
    //随机数生成器
    private final static RandomDataGenerator random = new RandomDataGenerator();
    //标准正态分布
    private final static NormalDistribution normalDistribution = new NormalDistribution(0, 1);

    /**
     * map集合合并
     * 传递 (K,List<>)
     *
     * @param target
     * @param source
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, List<V>> merge(Map<K, List<V>> target, final Map<K, List<V>> source) {
        source.forEach((k, v) -> {
            target.getOrDefault(k, new ArrayList<>()).addAll(v);
        });
        return target;
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

    /**
     * 条件概率 又名贝叶斯公式
     * 贝叶斯定理：P(A|B) = P(B|A)*P(A)/P(B)
     *
     * @param
     * @return
     */
    public static double bayes(final double PA, final double PB, final double PBorA) {
        return PBorA * PA / PB;
    }

    /**
     * 贝叶斯信念网络
     *
     * @return
     */
    public static double bayesNet() {

        return 0;
    }


    /**
     * map集合合并
     * 传递 (K,List<>)
     *
     * @param target
     * @param source
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> void merge2(Map<K, List<V>> target, final Map<K, List<V>> source) {
        Map<K, V> result = new LinkedHashMap<>();
        source.forEach((k, v) -> {
            target.getOrDefault(k, new ArrayList<>()).addAll(v);
        });
    }

    /**
     * TF 词频 (term frequency, TF)
     * TF(w)=在某一类中词条w出现的次数/该类中所有的词条数目
     * 某个词在 本篇文章 中出现的次数
     *
     * @param wordCount
     * @param docWordCount
     * @return
     */
    public static double TF(final int wordCount, final int docWordCount) {
        return 1.0 * wordCount / docWordCount;
    }

    /**
     * IDF 逆向文件频率 (inverse document frequency, IDF)
     * 某个词在 所有文档 中逆向文件频率
     * IDF=log(语料库的文档总数/包含词条w的文档数+1) #(分母之所以要加1，是为了避免分母为0,这里的log也是以2为底数，参考信息熵)
     *
     * @param wordCount
     * @param allWordCount
     * @return
     */
    public static double IDF(final int wordCount, final int allWordCount) {
        return StatisticsUtils.log(allWordCount / (1.0 + wordCount), 2);
    }

    /**
     * 计算TF-IDF得分
     * TF-IDF = TF*IDF
     * library->docs->words
     * keyWord->docs排名
     *
     * @return
     */
    public static Map<Docs, Double> TF_IDF(final String keyWord, final List<Docs> library) {
        Map<Docs, Double> result = new HashMap<>();
        library.forEach(doc -> {
            Map<String, Integer> wordMap = doc.getWordMap();
            Integer wordCount = wordMap.getOrDefault(keyWord, 0);
            double tf = TF(wordCount, doc.getWordCount());
            double idf = IDF(wordCount, library.stream().mapToInt(t -> t.getWordCount()).sum());
            result.put(doc, tf * idf);
        });
        Map<Docs, Double> docsDoubleMap = sortByValue(result);
        return docsDoubleMap;
    }

    /**
     * map对value进行排序
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(final Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();
        Stream<Map.Entry<K, V>> st = map.entrySet().stream();
        st.sorted(Comparator.comparing(e -> e.getValue())).forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    class Docs {
        private Integer docId;
        private Integer wordCount;
        private Map<String, Integer> wordMap;

        public Integer getWordCount() {
            return wordCount;
        }

        public Integer getDocId() {
            return docId;
        }

        public Map<String, Integer> getWordMap() {
            return wordMap;
        }
    }


    /**
     * 产生等差数列
     *
     * @return
     */
    public static List<Double> arange(final int start, final int end, final int step) {
        List<Double> list = new ArrayList<>((end - start) / step);
        for (int i = start; i < end; i += step) {
            list.add(i * 1.0);
        }
        return list;
    }

    /**
     * 计算标准差
     * std = [((A-avg)^2+(B-avg)^……)/N]^(1/2)
     * 如是总体,标准差公式根号内除以n
     * 如是样本,标准差公式根号内除以（n-1)
     * 因为我们大量接触的是样本,所以普遍使用根号内除以（n-1)
     *
     * @return
     */
    public static Double std(final double[] array) {
        double averge = Arrays.stream(array).average().getAsDouble();
        double reduce = Arrays.stream(array).map(t -> FastMath.pow(t - averge, 2)).reduce(0, (a, b) -> a + b);
        return FastMath.pow(reduce / (array.length - 1), 0.5);
    }

    /**
     * math3 std
     * 样本分母 N-1
     *
     * @param list
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Double std(final List<Double> list) {
        double[] array = list.stream().mapToDouble(t -> t).toArray();
        StandardDeviation StandardDeviation = new StandardDeviation();//标准差
        double evaluate = StandardDeviation.evaluate(array);
        return evaluate;
    }

    /**
     * 总体标准差
     *
     * @param array
     * @return
     */
    public static Double std_all(final double[] array) {
        double averge = Arrays.stream(array).average().getAsDouble();
        double reduce = Arrays.stream(array).map(t -> FastMath.pow(t - averge, 2)).reduce(0, (a, b) -> a + b);
        return FastMath.pow(reduce / array.length, 0.5);
    }

    /**
     * Math.log()默认以e为底数,使用对数换底公式替换
     * log(A)/log(B) = logB(A)
     *
     * @param num
     * @param base
     * @return
     */
    public static double log(final double num, final double base) {
        return FastMath.log(num) / FastMath.log(base);
    }

    /**
     * 线性回归
     *
     * @param data
     */
    public static void line_regression(final double[]... data) {
        SimpleRegression regression = new SimpleRegression();
        regression.addData(data);
        double intercept = regression.getIntercept();
        double interceptStdErr = regression.getInterceptStdErr();
        double regressionSumSquares = regression.getRegressionSumSquares();
        double rSquare = regression.getRSquare();
    }

    /**
     * logistic回归
     *
     * @param list
     */
    public static void logistic_regression(final List<Double> list) {
//        LogisticDistribution distribution = new LogisticDistribution();
    }

    /********************************************************************
     * ******************************伪随机数
     * ******************************概率分布 正态分布、泊松分布、最大似然估计……
     * ******************************shuffle
     * ******************************10折交叉数据分割
     * ******************************简单线性回归
     * ******************************最小二乘
     * ******************************多项式回归
     * ******************************梯度下降
     * ******************************朴素贝叶斯
     * ******************************决策树
     * ******************************svm
     * ******************************huffman压缩
     * ******************************************************************
     */
    public static int nextInt(final int lower, final int upper) {
        return random.nextInt(lower, upper);
    }

    /**
     * shuffle
     *
     * @param data
     * @return
     */

    public static List<?> shuffle(List<?> data) {
        Collections.shuffle(data);
        return data;
    }

    /**
     * 交换元素
     *
     * @param list
     * @param i
     * @param j
     */
    public static void swap(List<?> list, int i, int j) {
        final List l = list;
        l.set(i, l.set(j, l.get(i)));
    }


    /**
     * 简单线性回归
     *
     * @param data
     */
    public static void lineRegression(double[][] data) {
        SimpleRegression regression = new SimpleRegression();
        regression.addData(data);
        double intercept = regression.getIntercept();
    }

    /**
     * 聚类算法
     * K-Means
     * todo 可用性 DBScan
     *
     * @param clusterSize
     * @param list
     * @return
     */
    public static <T> List<Cluster<? extends T>> cluster2(int clusterSize, List<T> list) {
        Clusterer clusterer = new KMeansPlusPlusClusterer(clusterSize);
        List<Cluster<? extends T>> cluster = clusterer.cluster(list);
        return cluster;
    }

    public static List<Cluster<ClusterPoint>> cluster(int clusterSize, List<ClusterPoint> list) {
        Clusterer clusterer = new KMeansPlusPlusClusterer(clusterSize);
        List<Cluster<ClusterPoint>> cluster = clusterer.cluster(list);
        return cluster;
    }

    public static double[] split(double[] data) {
        return null;
    }

    /**
     * 数据分割
     *
     * @param data
     * @param proportion
     * @param shuffle
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<List<T>> split(List<T> data, final float proportion, boolean shuffle) {
        int sizeA = (int) (data.size() * proportion);
        List<T> listA = new ArrayList<T>(sizeA);
        for (int i = 0; i < sizeA; i++) {
            listA.add(data.remove(nextInt(0, data.size())));
        }
        List<List<T>> result = new ArrayList<>();
        result.add(listA);
        result.add(data);
        return result;
    }

    /**
     * Beta Distribution
     * 概率分布
     *
     * @param alpha
     * @param beta
     * @return
     */
    public static double nextBeta(double alpha, double beta) {
        return random.nextBeta(alpha, beta);
    }


    /**
     * ********************************************************************
     * ****************************简单统计
     * ****************************python pandas describe
     * ********************************************************************
     */
    public static Describe describe(final double[] data) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double sum = 0.0;
        double mean = 0.0;
        int count = data.length;
        double std = std(data); //  多一层循环
        for (double value : data) {
            if (value > max) {
                max = value;
            }
            if (value < min) {
                min = value;
            }
            sum += value;
        }
        mean = sum / count;
        Describe build = new Describe.Builder(count).mean(mean).std(std).values(max, min).sum(sum).build();
        return build;
    }

    /**
     * 峰度
     * 峰度（Kurtosis）是描述某变量所有取值分布形态陡缓程度的统计量。
     * 它是和正态分布相比较的。
     * Kurtosis=0 与正态分布的陡缓程度相同。
     * Kurtosis>0 比正态分布的高峰更加陡峭——尖顶峰
     * Kurtosis<0 比正态分布的高峰来得平台——平顶峰计算公式：β= M_4/σ^4
     *
     * @param data
     * @return
     */
    public static double kurtosis(double[] data) {
        Kurtosis kurtosis = new Kurtosis(); //峰度
        return kurtosis.evaluate(data);
    }

    /**
     * 偏度
     * 偏度（Skewness）是描述某变量取值分布对称性的统计量。
     * Skewness=0 分布形态与正态分布偏度相同
     * Skewness>0 正偏差数值较大，为正偏或右偏。长尾巴拖在右边。
     * Skewness<0 负偏差数值较大，为负偏或左偏。长尾巴拖在左边。
     * 计算公式：S= (X拔-M_0)/δ Skewness 越大，分布形态偏移程度越大。
     *
     * @param data
     * @return
     */
    public static double skewness(double[] data) {
        Skewness skewness = new Skewness();
        return skewness.evaluate(data);
    }

    /**
     * 标准正态分布
     * 若随机变量X服从一个数学期望为μ、方差为σ^2的正态分布，记为：N（μ，σ^2），
     * 其概率密度函数为正态分布的期望值μ决定了其位置，其标准差σ决定了分布的幅度。
     * 当μ=0，σ=1时的正态分布是标准正态分布即：N(0,1)
     *
     * @return
     */
    public static double normalDistribution(final double x) {
        return normalDistribution.cumulativeProbability(x);
    }

    /**
     * 正态分布 又名高斯分布
     *
     * @param mean 均值
     * @param sd   标准差
     * @return
     */
    public static double gaussianDistribution(final double mean, final double sd, final double x) {
        NormalDistribution normalDistribution = new NormalDistribution(mean, sd);
        return normalDistribution.cumulativeProbability(x);
    }

    /**
     * 获取均值
     *
     * @param dat
     * @return
     */
    public static double mean(final double[] dat) {
        return StatUtils.mean(dat);
    }

    public static double max(final double[] data) {
        return StatUtils.max(data);
    }

    public static double min(final double[] data) {
        return StatUtils.min(data);
    }

    public static double sum(final double[] data) {
        return StatUtils.sum(data);
    }

    /**
     * 加权平均数
     * (a*A+b*B+c*C)/(a+b+c)
     *
     * @param data
     * @return
     */
    public static double weightedAverage(final double[] data, final double[] weight) {
        double sum = 0;
        double sumWeight = 0;
        if (data.length == weight.length) {
            for (int i = 0; i < data.length; i++) {
                sumWeight += weight[i];
                sum += (data[i] * weight[i]);
            }
            return sum / sumWeight;
        }
        return 0;
    }

    /**
     * 移动平均 （滑动窗口平均）
     *
     * @param data
     * @param timePeriod 窗口大小
     * @return
     */
    public static double[] rollingMean(final double[] data, final int timePeriod) {
        double[] result = new double[data.length];
        for (int i = 0; i < timePeriod - 1; i++) {
//            result[i] = Double.NaN; todo 返回NAN还是0
            result[i] = 0;
        }
        for (int i = 0; i <= data.length - timePeriod; i++) {
            double windowSum = 0;
            for (int j = 0; j < timePeriod; j++) {
                windowSum += data[i + j];
            }
            result[i + timePeriod - 1] = windowSum / timePeriod;
        }
        return result;
    }


    /**
     * 计算几何平均数
     * (A*B*C……)^(1/N)
     * todo 计算复利
     * 不能出现负数和0 ！！！
     *
     * @param data
     * @return
     */
    public static double geometricMean(final double[] data) {
        return StatUtils.geometricMean(data);
    }

    public static double geometricMean2(final double[] data) {
        double reduce = Arrays.stream(data).reduce(1, (a, b) -> a * b);
        return FastMath.pow(reduce, 1.0 / data.length);
    }


    /**
     * 数据归一化 0-1分布
     * Z-Score归一化
     * (x-mean)/std
     *
     * @param dat
     * @return
     */
    public static double[] normalize(final double[] dat) {
        double[] normalize = StatUtils.normalize(dat);
        return normalize;
    }

    /**
     * min-max标准化（Min-Max Normalization）
     * (x-min)/(max-min)
     *
     * @param data
     * @return
     */
    public static double[] normalize_Max_Min(final double[] data) {
        Describe describe = describe(data);
        double[] result = new double[data.length];
        double max = describe.getMax();
        double min = describe.getMin();
        double dividend = max - min;
        for (int i = 0; i < data.length; i++) {
            result[i] = (data[i] - min) / dividend;
        }
        return result;
    }

    /**
     * Z-score标准化方法
     * (x-mean)/std
     *
     * @param data
     * @return
     */
    public static double[] normalize_ZScore(final double[] data) {
        Describe describe = describe(data);
        double[] result = new double[data.length];
        double mean = describe.getMean();
        double std = describe.getStd();
        for (int i = 0; i < data.length; i++) {
            result[i] = (data[i] - mean) / std;
        }
        return result;
    }

    /**
     * R-square
     *
     * @param data
     * @return
     */
    public static double R_square(final double[] data) {
        //todo 1- SSR/SST;
        return 0;
    }

    /**
     * ****************************************************************
     * ********************************二进制、十六进制计算
     * ********************************bool运算
     * ****************************************************************
     *
     * @param data
     */


    /**
     * test
     *
     * @param args
     */
    public static void main(String[] args) {
        List<Double> list = arange(1, 10, 1);
        double[] array = list.stream().mapToDouble(t -> t).toArray();
        List<ClusterPoint> ClusterPoints = new ArrayList<>();
        double[] a = new double[]{1, 3, 4, 6, 87, 3, 2};
        List<Double> list1 = new ArrayList<>();
        Arrays.stream(a).forEach(t -> list1.add(t));
        System.out.println(std(a));
        System.out.println(std(list1));

        double[] doubles = rollingMean(array, 3);
        Arrays.stream(doubles).forEach(t -> System.out.print(t + "\t"));

    }

    static class ClusterPoint implements Clusterable {
        private double[] value;

        @Override
        public double[] getPoint() {
            return value;
        }

        public ClusterPoint(double[] value) {
            this.value = value;
        }

        public double[] getValue() {
            return value;
        }

        public void setValue(double[] value) {
            this.value = value;
        }
    }

    public static void syso(double[] data) {
        for (double value : data) {
            System.out.print(value + "\t");
        }
        System.out.println();
    }
}
