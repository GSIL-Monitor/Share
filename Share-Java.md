# Stream
```java
// groupBy
Map<Integer, List<UserOrderFofsEntity>> userFofsOrderMap = userOrderFofsEntities.stream()
                .sorted(Comparator.comparing(t -> t.getCreateTime().getTime()))
                .collect(Collectors.groupingBy(t -> t.getUserId()));
// toMap
Map<Date, FofsTotalRateModel> modelMap = models.stream().collect(Collectors.toMap(k -> DateUtil.formatDate(k.getDateTime()), v -> v));
//筛选数据
List<FundSupermarketModel> result = fundSupermarketModelList.stream()
        //基本属性
        .filter(c -> (!checkInput(param.getCategory()) || param.getCategory().equals(c.getCategory())))
        .filter(c -> (!checkInput(param.getCompany()) || param.getCompany().equals(c.getCompany())))
        .filter(c -> (!checkInput(param.getInvestmentType1()) || Objects.equals(param.getInvestmentType1(), c.getInvestmentType1())))
        .filter(c -> (!checkInput(param.getCompanySize()) || Objects.equals(param.getCompanySize(), c.getCompanySize())))
        .filter(c -> (!checkInput(param.getStar()) || Objects.equals(param.getStar(), c.getStar())))
        //区间范围 returnRateLow-returnRateHigh / scaleLow - scaleHigh
        .filter(c -> (!checkInput(param.getReturnRate3m())
                || (c.getReturnRate3m() >= FundSuperMarketEnum.ReturnRate.getEnum(param.getReturnRate3m()).getLow()
                && c.getReturnRate3m() < FundSuperMarketEnum.ReturnRate.getEnum(param.getReturnRate3m()).getHigh())))
        .filter(c -> (!checkInput(param.getScale())
                || (c.getScale() >= FundSuperMarketEnum.ReturnRate.getEnum(param.getScale()).getLow()
                && c.getScale() < FundSuperMarketEnum.ReturnRate.getEnum(param.getScale()).getHigh())))
        //狩猎属性
        .filter(c -> (!checkInput(param.getHunter()) || Objects.equals(param.getHunter(), c.getHunter())))
        .filter(c -> (!checkInput(param.getStyle()) || Objects.equals(param.getStyle(), c.getStyle())))
        .filter(c -> (!checkInput(param.getCompanySize()) || Objects.equals(param.getCompanySize(), c.getCompanySize())))
        .filter(c -> (!checkInput(param.getRisk()) || Objects.equals(param.getRisk(), c.getRisk())))
        //关键词筛选
        .filter(c -> (!checkInput(param.getKeyWord()) || c.getCategory().contains(param.getKeyWord())
                || c.getFundName().contains(param.getKeyWord()))).sorted((c1, c2) -> {
            if (checkInput(param.getSortByReturnrate3m())) {
                if (param.getSortByReturnrate3m() == SortedEnum.DESC.getValue()) {
                    return c1.getReturnRate3m().compareTo(c2.getReturnRate3m());
                } else {
                    return c2.getReturnRate3m().compareTo(c1.getReturnRate3m());
                }
            } else if (checkInput(param.getSortByUnitasset())) {
                if (param.getSortByUnitasset() == SortedEnum.DESC.getValue()) {
                    return c1.getUnitasset().compareTo(c2.getUnitasset());
                } else {
                    return c2.getUnitasset().compareTo(c1.getUnitasset());
                }
            }
            return 1;
        })
        //自定义排序
        .sorted((c1, c2) -> {
                    if (!checkInput(param.getInvestmentType1())) {
                        if (param.getInvestmentType1() == 0) {
                            if (c1.getInvestmentType1() < 6 && c2.getInvestmentType1() == 6) {
                                return -1;
                            } else if (c2.getInvestmentType1() < 6 && c1.getInvestmentType1() == 6) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    }
                    return 0;
                })
        //分页
        .skip(param.getPage() * param.getPageSize())
        .limit(param.getPageSize())
        .collect(Collectors.toList());
// 连接
String tagMsg = String.join(",", listByUserId.stream().map(t -> t.getUserManagerTag()).collect(Collectors.toList()));
// 统计
LongSummaryStatistics dateLineSummary = products.stream().filter(t -> !Objects.equals(t.getProductDateline(), 0)).collect(Collectors.summarizingLong(t -> t.getProductDateline()));
// BigDecimal求和
BigDecimal bigDecimal = allUserFofs.stream().map(UserFofsEntity::getTotalAsset).reduce(BigDecimal.ZERO, BigDecimal::add);
//求差集 collect1 - collect2
List<Integer> collect3 = collect1.stream().filter(t -> !collect2.contains(t)).collect(Collectors.toList());
//求交集
List<Integer> collect3 = collect1.stream().filter(t -> collect2.contains(t)).collect(Collectors.toList());
```

# BigDecimal
```java
BigDecimal b1 = new BigDecimal(Double.toString(v1));  
BigDecimal b2 = BigDecimal.ZERO;
BigDecimal b3= b1.divide(BigDecimal.valueOf(2), 2);
# 四舍五入保留两位小数
double result = b3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
# 格式化为文本
//方式一  
DecimalFormat df1 = new DecimalFormat("0.00");  
String str = df1.format(b3);  
//方式二  
// #.00 表示两位小数 #.00%百分化后保留两位小数  
DecimalFormat df2 =new DecimalFormat("#.00");  
String str2 =df2.format(b3);  
//方式三  
//%.2f %. 表示 小数点前任意位数   2 表示两位小数 格式后的结果为f 表示浮点型  
String result = String.format("%.2f", b3);  
# 基本操作
public BigDecimal add(BigDecimal divisor)
public BigDecimal subtract(BigDecimal divisor)
public BigDecimal multiply(BigDecimal divisor)
public BigDecimal divide(BigDecimal divisor)
public BigDecimal divide(BigDecimal divisor, int roundingMode)
public BigDecimal round(MathContext.DECIMAL32)
```
#　RPC通信

# 安全问题
## 防重放限制
参考：[API的防重放机制](https://app.yinxiang.com/shard/s48/nl/13169588/179fb353-1df2-4de5-b596-9b2a7c347da7)
## CSRF 安全过滤/跨站攻击/异常请求/后端数据校验
参考：[使用spring validation完成数据后端校验](https://app.yinxiang.com/shard/s48/nl/13169588/1bbcca2c-4583-436b-90f1-a61c412ad44c)
## 防刷
参考：[基于Redis的防刷票、防刷短信、及所有防刷系统的设计](https://app.yinxiang.com/shard/s48/nl/13169588/14c94114-a2c0-4747-a04f-1c3e2a81de3d)
## XSS攻击和HttpOnly防止js读取cookie
```java
public SimpleCookie rememberMeCookie() {
    SimpleCookie cookie = new SimpleCookie("rememberMe");
    // 设置HttpOnly为true
    cookie.setHttpOnly(true);
    cookie.setMaxAge(604800);
    return cookie;
}
```
# Spring-Data
## Redis
## Elastic-Search
## neo4j

# JWT / OAuth 2.0
## JWT
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
</dependency>
```
```java

static final Logger LOGGER = LoggerFactory.getLogger(JwtHelper.class);
/**
  * 生成签名
  * @param secret
  * @param payloads
  * @return
  */
public static String encode(String secret, Map<String, Object> payloads) {
    String base64Secret = Base64.getEncoder().encodeToString(secret.getBytes());
    return Jwts.builder().signWith(SignatureAlgorithm.HS256, base64Secret).setClaims(payloads)
            .compressWith(CompressionCodecs.DEFLATE).compact();
}

/**
 * 
 * @param secret
 * @param claimsJws
 * @return {@link ApiConfig#ACCESS_USER_ID}, {@link ApiConfig#CHANNEL_ID}
 * @throws SignatureException
 *             签名失败（1. secret 错误；2. claimsJws 篡改；）
 */
public static Map<String, Object> decode(String secret, String claimsJws) {
    try {
        String base64Secret = Base64.getEncoder().encodeToString(secret.getBytes());
        Map<String, Object> loginUserMap = Jwts.parser().setSigningKey(base64Secret).parseClaimsJws(claimsJws)
                .getBody();
        return loginUserMap;
    } catch (Exception e) {
        throw new BetaException(String.format("验签失败: secret=%s, claims=%s", secret, claimsJws), e);
    }
}

/**
 * 验证签名
 * 
 * @param secret
 * @param claimsJws
 * @return
 */
public static boolean signed(String secret, String claimsJws) {
    try {
        decode(secret, claimsJws);
        return true;
    } catch (SignatureException e) {
        LOGGER.warn("验签失败 {} {}", secret, claimsJws);
    } catch (Exception e) {
        LOGGER.error("验签失败", e);
    }
    return false;
}

/**
 * 在cookie中添加jwt回写
 * 
 * @param request
 * @param response
 * @param loginUser
 * @return
 */
public void saveLoginCookie(HttpServletRequest request, HttpServletResponse response, LoginUser loginUser)
            throws Exception {

    int expire = SECOND_7_DAYS;
    //第一次生成jwtLoginToken
    String jwtLoginToken = JwtHelper.encode(betaProperties.getSecurity().getLoginSecret(),
            JacksonUtil.toMap(loginUser));
    CookieHelper.addCookieByDomain(request, response, ApiConfig.ACCESS_TOKEN, jwtLoginToken, expire);

    String accessUserId = DesHelper.encrypt(loginUser.getUserId().toString(), ApiConfig.ENCRYPT_USER_ID_KEY);
    CookieHelper.addCookieByDomain(request, response, ApiConfig.ACCESS_USER_ID, accessUserId, expire);

    //扩展记录来自那个系统
    String userFrom = loginUser.getFrom().toString();
    CookieHelper.addCookieByDomain(request, response, ApiConfig.USER_FROM_SYS, userFrom, expire);
}

 /**
 * jwt获取当前登陆用户
 * 
 * @return
 */
public LoginUser getCurrentUser() {
    HttpServletRequest request = this.getCurrentRequest();
    String jwt = CookieHelper.getCookieValueByName(request, ApiConfig.ACCESS_TOKEN);
    if(null!=jwt&&""!=jwt){
    	Map<String, Object> currentUserMap = JwtHelper.decode(betaProperties.getSecurity().getLoginSecret(), jwt);
    	LoginUser loginUser = JacksonUtil.fromMapToObj(currentUserMap, LoginUser.class);
    	return loginUser;
    }else{
    	return null;
    }
}
```

# MyCat