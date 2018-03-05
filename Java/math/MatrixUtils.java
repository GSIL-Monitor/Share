package main;

import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.util.FastMath;

import java.util.Arrays;


/**
 * Created by SoftStar on 2017/10/4.
 * ********************************************************************
 * *****************************矩阵运算
 * *****************************1、传入double[][]返回matrix
 * *****************************2、传入两个double[] 按照某种方式合并返回matrix
 * *****************************3、矩阵转置、求逆、行列式
 * *****************************4、两个矩阵运算
 * *****************************5、矩阵转向量
 * *****************************6、几何运算、计算两个向量的余弦cos
 * *****************************7、todo 矩阵奇异值分解、特征值分解、主成分分析
 * *****************************7、todo 矩阵卷积和池化
 * *********************************************************************
 */
public class MatrixUtils {
    /**
     * 数组转换为矩阵
     * 得到一个列向量（仍然是matrix对象）
     *
     * @param datas
     * @return
     */
    public static RealMatrix convertToMatrix(final double[]... datas) {
        return new Array2DRowRealMatrix(datas);
    }

    /**
     * 数组创建向量
     *
     * @param data
     * @return
     */
    public static RealVector convertToVector(final double[] data) {
        return new ArrayRealVector(data);
    }

    /**
     * 矩阵转换为向量
     *
     * @param matrix
     * @return
     */
    public static RealVector convertToVector(final RealMatrix matrix) {
        RealVector vector = new ArrayRealVector();
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            RealVector rowVector = matrix.getRowVector(i);
            vector = vector.append(rowVector);
        }
        return vector;
    }

    /**
     * 矩阵转换为向量
     *
     * @param data
     * @return
     */
    public static RealVector convertToVector(final double[][] data) {
        double[] result = new double[data.length * data[0].length];
        int index = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                result[index++] = data[i][j];
            }
        }
        return new ArrayRealVector(result);
    }

    /**
     * 矩阵奇异值分解
     *
     * @param matrix
     * @return
     */
    public static RealMatrix svd(final RealMatrix matrix) {
        return null;
    }

    /**
     * 矩阵特征值分解
     *
     * @param matrix
     * @return
     */
    public static RealMatrix evd(final RealMatrix matrix) {
        return null;
    }

    /**
     * 主成分分析
     *
     * @param matrix
     * @return
     */
    public static RealMatrix pca(final RealMatrix matrix) {
        return null;
    }

    /**
     * 矩阵转成数组
     *
     * @param matrix
     * @return
     */
    public static double[][] convertMatrixToArray(final RealMatrix matrix) {
        return matrix.getData();
    }

    /**
     * 向量转为数组
     *
     * @param vector
     * @return
     */
    public static double[] convertVectorToArray(final RealVector vector) {
        return vector.toArray();
    }

    /**
     * 矩阵转置
     *
     * @param matrix
     */
    public static RealMatrix transpose(final RealMatrix matrix) {
        return matrix.transpose();
    }

    /**
     * 矩阵相乘
     *
     * @param matrix1
     * @param matrix2
     * @return
     */
    public static RealMatrix multiply(final RealMatrix matrix1, final RealMatrix matrix2) {
        return matrix1.multiply(matrix2);
    }

    public static RealVector multiply(final RealVector v1, final RealVector v2) {
        return v1.ebeMultiply(v2);
    }

    public static RealMatrix multiply(final double[][] data1, final double[][] data2) {
        return convertToMatrix(data1).multiply(convertToMatrix(data2));
    }

    public static RealMatrix multiply(final double[] data1, final double[] data2) {
        return convertToMatrix(data1).multiply(convertToMatrix(data2));
    }

    public static RealVector multiplyToVector(final double[] data1, final double[] data2) {
        return convertToVector(data1).ebeMultiply(convertToVector(data2));
    }

    /**
     * 矩阵求逆
     *
     * @param matrix
     * @return
     */
    public static RealMatrix inverse(final RealMatrix matrix) {
        //LUDecomposition:矩阵LU分解
        /**
         * 矩阵分解主要有三种方式：LU分解，QR分解和奇异值分解。当然在Math的linear包中提供了对应的接口有
         * CholeskyDecomposition、EigenDecomposition、LUDecomposition、QRDecomposition和SingularValueDecomposition这5种分解方式。
         * Math库中的LU分解主要是LUP分解，即针对n*n方阵A，找出三个n*n的矩阵L、U和P，满足PA=LU。其中L是一个单位下三角矩阵，U是一个上三角矩阵，P是一个置换矩阵。
         * 非奇异的矩阵（可逆）都有这样一种分解（可证明）。LUP分解的计算方法就是高斯消元。
         */
        return new LUDecomposition(matrix).getSolver().getInverse();
    }

    /**
     * 计算向量卷积 参考MATLAB conv
     * TODO 降维卷积
     * TODO 对data添加首位元素从而削弱边界条件的验证，从而不用定义window直接移动
     * 输出将是过去产生的所有信号经过系统的「处理／响应」后得到的结果的叠加，这也就是卷积的物理意义了。
     *
     * @param data
     * @param convKernel
     * @return
     */
    public static double[] conv(final double[] data, final double[] convKernel) {
        //得到length长度的结果集
        int length = data.length + convKernel.length - 1;
        double[] result = new double[length];
        //对convKernel反褶
        double[] deConvKernel = deconvolution(convKernel);
        //创建窗口
        double[] window = new double[deConvKernel.length];
        //保存每个窗口计算结果
        double windowConvSum = 0;
        int windowIndex = 0;
        for (int i = 0; i < result.length; i++) {
            //滑动窗口计算
            //1、填充窗口数据
            windowIndex = i;
            for (int j = window.length - 1; j >= 0; j--) {
                window[j] = (windowIndex >= 0 && windowIndex < data.length) ? data[windowIndex] : 0;
                windowIndex--;
            }
            //2、计算卷积
            windowConvSum = 0;
            for (int j = 0; j < deConvKernel.length; j++) {
                windowConvSum += window[j] * deConvKernel[j];
            }
            result[i] = windowConvSum;
        }
        return result;
    }

    /**
     * 计算矩阵卷积
     *
     * @param data
     * @param convKernel
     * @return
     */
    public static double[][] conv2(final double[][] data, final double[][] convKernel) {
        return null;
    }

    /**
     * 向量反褶
     *
     * @param data
     * @return
     */
    public static double[] deconvolution(final double[] data) {
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[data.length - i - 1];
        }
        return result;
    }

    /**
     * 计算矩阵的行列式
     *
     * @param matrix
     * @return
     */
    public static RealMatrix det(final RealMatrix matrix) {
        return null;
    }

    /**
     * 计算矩阵的迹
     *
     * @param matrix
     * @return
     */
    public static double trace(final RealMatrix matrix) {
        return matrix.getTrace();
    }

    /**
     * 计算两个向量的距离
     * 欧氏距离：=sqrt((x1-x2)^2+(y1-y2)^2+……)
     * todo 曼哈顿距离、切比雪夫距离、闵可夫斯基距离、杰卡德距离& 杰卡德相似系数
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double distance(final RealVector v1, final RealVector v2) {
        return v1.getDistance(v2);
    }

    public static double distance(final double[] v1, final double[] v2) {
        return convertToVector(v1).getDistance(convertToVector(v2));
    }

    /**
     * 计算两个向量的余弦值
     * cos<a,b> = (a·b)/(|a|*|b|) = (x1*x2+y1*y2)/(sqrt(x1^2+y1^2)*sqrt(x2^2+y2^2))
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double cos(final RealVector v1, final RealVector v2) {
        return v1.cosine(v2);
    }

    /**
     * pearson相关系数
     * 皮尔逊先关系数 = 标准化后的 cos<a,b>
     *
     * @param v1
     * @param v2
     * @return
     * @link https://pic2.zhimg.com/50/v2-71de3ac89fb7e62a24eae9bdbb56aa8d_hd.png
     */
    public static double pearson(final double[] v1, final double[] v2) {
        double avg_v1 = StatisticsUtils.mean(v1);
        double avg_v2 = StatisticsUtils.mean(v2);
        double sum_1 = 0;
        double sum_2 = 0;
        double sum_3 = 0;
        //标准化
        for (int i = 0; i < v1.length; i++) {
            sum_1 += (v1[i] - avg_v1) * (v2[i] - avg_v2);
            sum_2 += FastMath.pow(v1[i] - avg_v1, 2);
            sum_3 += FastMath.pow(v2[i] - avg_v2, 2);
        }
        return sum_1 / FastMath.pow(sum_2, 0.5) / FastMath.pow(sum_3, 0.5);
    }

    public static double cos(final double[] v1, final double[] v2) {
        return convertToVector(v1).cosine(convertToVector(v2));
    }

    /**
     * 计算信息熵
     * entropy = -p1*log(p1)-p2*log(p2)-…… (log底数为2)
     * 1=sum(p1)
     *
     * @param p1
     * @return
     */
    public static double entropy(final double[] p1) {
        double sum = Arrays.stream(p1).map(t -> t * StatisticsUtils.log(t, 2)).sum();
        return -1.0 * sum;
    }

    public static void main(String[] args) {
        double[] a = new double[]{2, 4};
        double[] b = new double[]{2, 8};
        double distance = distance(a, b);
        double cos = cos(a, b);
        double pearson = pearson(a, b);
        System.out.println("distinct=" + distance);
        System.out.println("cos=" + cos);
        System.out.println("pearson=" + pearson);

        a = new double[]{-1, 1};
        b = new double[]{-3, 3};
        cos = cos(a, b);
        System.out.println("cos=" + cos);
        double[] c = new double[]{0.2, 0.4, 0.1, 0.3};
        double entropy = entropy(c);
        System.out.println("entropy=" + entropy);
        System.out.println("log(4)=" + StatisticsUtils.log(4, 2));
        double[] d = new double[]{1, 2, 3, 4, 7, 4};
        double[] e = new double[]{1, 2, 1};
        double[] conv = conv(d, e);
        Arrays.stream(conv).forEach(t -> System.out.print(t + "\t"));
        double[][] f = new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        RealVector vector = convertToVector(convertToMatrix(f));
        System.out.println(vector);
        vector = convertToVector(f);
        System.out.println(vector);

    }
}
