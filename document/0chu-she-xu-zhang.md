# 序

Spring的“重”是一个老生常谈的话题，由于开发[Vert.x Zero Up Framework](http://www.vertxup.cn)，深受函数式编程、响应式编程以及一切从简的影响，根据实际的业务需求，一直在思考如何做好微服务，如何设计一个基于微服务的框架，以及如何真正从业务层面驱动去完成一个优良的架构以及设计，于是有了Zero。这几个月一直在思索，于是开了另一个新的脚手架项目，和Zero Up一样，命名为Spring Up。

## 1. 脚手架的目标

* 和Zero一样，扩展JSR303完成基于Json的Object/Array级别的验证，别问我这个东西适用不适用，至少我们项目里铺天盖地的业务需求可能都和它相关，非侵入式的验证，会让一切变得更加简单。
* Spring Cloud中引用了超过100个第三方依赖库，那“蜘蛛网”一般的Maven依赖图，稍不注意可能引起的就是雪崩一样的“jar冲突”，抽丝剥茧一般，将Maven重新整理，并提供Jar的统一版本管理，让Spring Clould的应用变得更加流畅（对不起，我是强迫症）。
* 和Zero一样，同样需要完成gRpc的服务通信，根据对gRpc的深入分析和学习，实际上，我们可以将gRpc做得更加完善，而无缝实现让开发人员不关注细节的服务通信，真正将精力集中于业务。
* 实现统一的容错管理，让容错变得更加具有一致性，在系统内部提供完整的错误代码表，让脚手架中的异常流、正常流可以更加规范。
* 和Zero一样具有Utility X类似的超级工具包，帮助开发人员完成各种业务开发过程中的代码片段，以避免重复代码的泛滥。
* 在1.x的Cloud中实现纯函数、异步流（虽然我知道2.x已自带）、响应式编程，毕竟目前很多面临的业务项目，还没有办法直接迁移到2.x中，所以只能选择这种不完美。
* 最后，依旧以Service Mesh为目标，虽然我还不完全懂得什么是Service Mesh，什么是Serverless？

## 2. 灵感起源

我们每天面对着变化，日新月异的技术，以及各种创新，而往往在奔往新途径的过程中，似乎忘记了了解最基础的一切。在你使用`@Inject`或`@Autowired`的时候，Spring已经帮我们解决了“循环依赖”注入的检查问题，可是是否自己去实现过一个基于“有向图”的SCC强连通分量算法？更何况Java语言中没有Graphic。而业务本身的复杂度缘起于代码，而我们编程的过程是在“增加复杂度”还是“降低复杂度”？规范的代码如同捆绑一样，可能让开发人员无法施展，但真正的项目所需要的是团队一般的战斗力，如同当年的秦国，军纪严格，才有了整体战斗力的提升，而不是让开发人员在整个环境中随之发挥，自由肆掠，到最后各自为阵，代码走向病态，整个团队的成员都遇到了“改别人的代码改不动”的尴尬。

最初开发的框架名称叫vie，法语中的“生活”，以纯规范和数据驱动为主的框架，里面所有的代码内容都是数据和配置，灵感源于它，不过也深受其害，正如软件是可形式化的，如果要计算机本身懂得编程，第一件事就是想办法提炼“范式”，而这些“范式”的灵感都来源于摔了无数次坑坑出来的结果。性能和功能在系统面前究竟谁先行，正如分布式CAP理论一般，无法完美，只能因地制宜、因时制宜，而没有先后优劣之分。

## 3. Zero带来的福利

从上个月开始，已经有很多朋友在开始尝试着zero（包括我自己也在用zero开发一些试验性项目），因为在vert.x中摸爬滚打了两年多，虽谈不上精通，但对vert.x还是可以驾驭，它和Spring相比，S是大家闺秀，V是小家碧玉，应该说V更加小清新，所以团队选择了V而不是S——至少在目前的项目开发过程中，可以最大限度让所有团队开发人员在统一的代码中行走，保证每个人写出来的代码差异不大，到最后统一风格，生成器和界面都可以用“刷”的方式开发。

## 4. Summary

在驾驭了小家碧玉的同时，发现大家闺秀依旧是一枝独秀，于是考虑到是否可以按照V的思路，将S稍稍改变成基于业务系统的脚手架，规范、异步、快速，于是才有了Spring Up。我不保证它是最好用的脚手架，但至少对我们团队而言，我们需要的是一个礼仪规范的大家闺秀，而不是女王一样藐视天下的王者，这样才能去重就轻，真正让Spring在业务场景中发挥所长，而不是剪不断、理还乱的项目乱麻。

——2018年3月5日
