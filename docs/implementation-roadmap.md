# 引擎实现计划（自驱执行版）

## 目标

在不改动或微改现有 Mule app XML 的前提下，迭代实现可替换运行时。

## 阶段计划

### 阶段 1：主链路打通（已完成）

- [x] 建立 Java 21 + Maven 工程。
- [x] 完成 XML -> IR -> Runtime 主链路。
- [x] 提供 Processor SPI 与基础算子。

### 阶段 2：流程语义增强（本次已推进）

- [x] 表达式求值器（payload / vars / 插值）。
- [x] flow-ref 调用。
- [x] choice 分支控制。
- [x] for-each 迭代执行（基础版）。
- [x] 解析器支持嵌套节点。

### 阶段 3：连接器能力接入（下一步）

- [ ] HTTP Listener / Request 可执行实现。
- [ ] JDBC 执行与事务边界。
- [ ] 标准错误模型映射（timeout/deadlock 等）。

### 阶段 4：工程化与上线准备（后续）

- [ ] 调度器接入与运行参数化。
- [ ] 可观测（metrics/tracing/logging correlation）。
- [ ] 双跑回归平台与压测基线。
