# SP0004 - QueryDSL查询引擎语法

使用QueryDSL的目的是泛化所有的查询流程，由于VertxUI中设计了完整的查询参数数据，而且该参数和Zero中的参数一致，所以spring-up中提供查询引擎直接生成QueryDSL的查询语句读取相关数据，您可以使用spring-data，但在不使用该依赖包的时候，则使用spring-up中的QueryDSL执行各种动态查询是比较方便的一种做法。

## 0.请求格式

查询部分的请求格式参考下边的Json

```json
{
    "criteria": {
        "enterprise.id,=": "49640202-f767-4e46-b892-34b511d9f50f"
    },
    "pager": {
        "page": 1,
        "size": 10
    },
    "projection": [],
    "sorter": [
        "code,DESC"
    ]
}
```

* pager：该参数为分页参数，page代表页码（非索引），size则是每一页的条数；
* sorter：该参数为排序规则，为一个JsonArray，排序字段优先级按照数组中的索引来处理；
* projection：该参数为列过滤（目前未启用，如果使用了列过滤，包含在projection中的数据才会被返回）；
* criteria：专用查询条件基础语法，本文主要讲解该参数的语法；

## 1.字段和操作符

criteria参数的查询条件为`key = value`中的格式，其中`key`中包含了基本操作符，字段名和操作符之间使用逗号分开，如果不写则视为`=`操作符，如果一个字段包含多个操作符则直接可写成`field,<`和`field,>`这种格式，作为Json的键值也不会有冲突。所有的操作符如下：

|  |  |
| :--- | :--- |
|  |  |



