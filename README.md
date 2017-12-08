# GoZhiHuGo
简单写了一下知乎的模拟登录，网络用OkHttp手写request和response，后面留着再慢慢完善。
## 关于验证码
`/captcha.gif?r=时间戳&type=login`

r需要传入时间戳

> 时间戳是指格林威治时间1970年01月01日00时00分00秒(北京时间1970年01月01日08时00分00秒)起至现在的总秒数。
 
然后有两种验证方式
1. `&type=login`
验证码图片，Fresco拉下来直接手动输入
2. `&type=login&lang=cn`
点击倒立文字，需要传入input_points[每个点的坐标值]
