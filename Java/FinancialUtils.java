package main;

import com.beta.domain.dao.generated.entity.FofsCalculateEntity;
import com.beta.domain.model.FofsCalcModel;
import com.beta.domain.model.FofsTotalRateModel;
import org.HdrHistogram.DoubleLinearIterator;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

import java.util.*;

/**
 * Created by SoftStar on 2017/10/7.
 */
public class FinancialUtils {

    //标准正态分布
    private final static NormalDistribution normalDistribution = new NormalDistribution(0, 1);

    /**
     * 对数年化收益率
     *
     * @param data
     * @param cycle
     * @return
     */
    public static double rtn_ln(final double[] data, final int cycle) {

        //1、根据周期分割数据
        //2、计算每个周期内的对数收益率 ln(EPi／BPi)
        //3、求均值
        //4、做年化-> 均值 * （365/cycle）
        // 如周期为周 -> *52
        // 周期为季度 -> *2
        return 0;
    }

    /**
     * 对数年化收益率
     *
     * @param data
     * @param cycle
     * @return
     */
    public static double rtn_arg(final double[] data, final int cycle) {

        //1、根据周期分割数据
        //2、计算每个周期内的平均收益率：(EPi／BPi)-1
        //3、求均值
        //4、做年化-> 均值 * （365/cycle）
        // 如周期为周 -> *52
        // 周期为季度 -> *2
        return 0;
    }




    /**
     * 最大回撤率
     * 最大右下滑区间内（最高点净值-最低点净值）/最高点时的净值
     *
     * @param data
     */
    public static double maxDrawDownRate(final double[] data) {
        double maxValue = data[0];
        double minDraw = Double.MIN_VALUE;
        double drawDownRate = 0;
        for (int i = 1; i < data.length; i++) {
            drawDownRate = (maxValue - data[i]) / maxValue;
            if (drawDownRate < minDraw) {
                minDraw = drawDownRate;
            }
            if (data[i] > maxValue) {
                maxValue = data[i];
            }
        }
        return minDraw;
    }

    /**
     * 计算每日收益
     * python pct_change()
     *
     * @param list
     * @return
     */

    public static List<Double> pct_chg(final List<Double> list) {
        List<Double> resut = new ArrayList<>(list.size());
        //(B-A)/A ,第一个位置填充0
        resut.add(0.0);
        for (int i = 0; i < list.size() - 1; i++) {
            resut.add((list.get(i + 1) - list.get(i)) / list.get(i));
        }
        return resut;
    }

    public static double[] pct_chg(final double[] array) {
        double[] resut = new double[array.length];
        //(B-A)/A ,第一个位置填充0
        resut[0] = 0.0;
        for (int i = 0; i < array.length - 1; i++) {
            resut[i] = (array[i + 1] - array[i]) / array[i];
        }
        return resut;
    }

    /**
     * 计算累计收益率
     * python sumsum()
     *
     * @param list
     * @return
     */
    public static List<Double> cum_sum(final List<Double> list) {
        List<Double> resut = new ArrayList<>(list.size());
        // 0,A,A+B
        Double sum = 0.0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
            resut.add(sum);
        }
        return resut;
    }

    /**
     * 金融计算数据年化
     *
     * @param data
     */
    public static double annualised(final double data, final int size) {
        return FastMath.pow(data, 252.0 / size) - 1;
    }

    /**
     * 金融计算去年化
     * 公式不确定
     *
     * @param data
     * @param size
     * @return
     */
    public static double remove_annualised(final double data, final int size) {
        return FastMath.pow(data + 1, size / 252.0);
    }

    /**
     * 计算样本标准差
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

    public static Double std(final List<Double> list) {
        double average = list.stream().mapToDouble(t -> t).average().getAsDouble();
        double reduce = list.stream().mapToDouble(t -> FastMath.pow(t - average, 2)).reduce(0, (a, b) -> a + b);
        double std = FastMath.pow(reduce / (list.size() - 1), 0.5);
        return std;
    }


    /**
     * 计算样本协方差 类比python
     * 反映两组数据的变化趋势，从数值来看，协方差的数值越大，两个变量同向程度也就越大。反之亦然
     * COV(X,Y)= E((X-mean(X))*(Y-mean(Y)))  E():数学期望
     *
     * @param dataA length相同
     * @param dataB
     * @return
     */
    public static double cov(final double[] dataA, final double[] dataB) {
        double meanA = mean(dataA);
        double meanB = mean(dataB);
        double sum = 0;
        for (int i = 0; i < dataA.length; i++) {
            sum += (dataA[i] - meanA) * (dataB[i] - meanB);
        }
        return sum / (dataA.length - 1);
    }

    /**
     * 总体标准差
     *
     * @param dataA
     * @param dataB
     * @return
     */
    public static double cov_all(final double[] dataA, final double[] dataB) {
        double meanA = mean(dataA);
        double meanB = mean(dataB);
        double sum = 0;
        for (int i = 0; i < dataA.length; i++) {
            sum += (dataA[i] - meanA) * (dataB[i] - meanB);
        }
        return sum / dataA.length;
    }

    /**
     * 计算相关系数
     * corr(X,Y)= COV(X,Y)/(std(X)*Std(Y))
     *
     * @param dataA
     * @param dataB
     * @return
     */
    public static double corr(final double[] dataA, final double[] dataB) {
        return cov(dataA, dataB) / std(dataA) / std(dataB);
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
     * @param std  标准差
     * @return
     */
    public static double gaussianDistribution(final double mean, final double std, final double x) {
        NormalDistribution normalDistribution = new NormalDistribution(mean, std);
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

    /**
     * 参考python descript
     *
     * @param data
     * @return
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
     * 数据归一化 0-1分布
     * Z-Score归一化
     * (x-mean)/std
     *
     * @param data
     * @return
     */
    public static double[] normalize(final double[] data) {
        double[] normalize = StatUtils.normalize(data);
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
     * R-square
     *
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    public static double R_square(final double[] data) {
        //todo 1- SSR/SST;
        return 0;
    }
}
