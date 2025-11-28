# 查询

## 有筛选条件

`find返回值By关键词`

## 无筛选条件

`getAll返回值`

----------------

名词：
返回值的dao名称



关键词：
如果是可变字段就使用info

# 插入

`insert参数值`

| 模型层（model层名称）       | 参数值    | 例子                        |
|---------------------|--------|---------------------------|
| `UserEntity`        | `User` | `UserMapper.insertUser()` |
| `CharEntity`        | `User` | `UserMapper.insertUser()` |
| `CharMdr`           | `User` | `UserMapper.insertUser()` |
| `CharPinyin`        | `User` | `UserMapper.insertUser()` |
| `CharSimilar`       | `User` | `UserMapper.insertUser()` |
| `DialectChar`       | `User` | `UserMapper.insertUser()` |
| `IPASyllableEntity` | `User` | `UserMapper.insertUser()` |
| `IPAToneEntity`     | `User` | `UserMapper.insertUser()` |
| `DictEntity`        | `User` | `UserMapper.insertUser()` |
| `ReferEntity`       | `User` | `UserMapper.insertUser()` |

# 更新

# 删除

## 有对应主键

`delete表格名ById`

## 根据条件筛选

`clear表格名By关键词`

## ~~删库跑路~~
