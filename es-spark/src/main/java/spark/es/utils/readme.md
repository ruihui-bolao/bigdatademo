# 1，主要是用spark实现对es数据的保存，注意在使用spark 操作es 的时候有两种方式：
  1) spark es newhadoopApiFile （在esUtils中保存数据，以及按照一段的时间查询时通过 spark newHadoopApifile 实现的 ）
  2) spark sql es （在esutils中通过某个字段查询某条数据是通过 spark sql 实现的）