# 引擎实现计划（自驱执行版）

## 目标

在不改动或微改现有 Mule app XML 的前提下，迭代实现可替换运行时。

## 当前状态

- ✅ 已完成：XML -> IR -> Runtime 主链路与核心流程语义。
- ✅ 已完成：flow-ref / choice / for-each / try-error 基础行为。
- ✅ 已完成：HTTP Request、JDBC Query 可执行能力，HTTP Listener 基础部署能力。
- ⏳ 未完成：调度、完整事务语义、DataWeave 兼容、全面连接器生态。

## 阶段计划

### 阶段 1：主链路打通（已完成）

- [x] 建立 Java 21 + Maven 工程。
- [x] 完成 XML -> IR -> Runtime 主链路。
- [x] 提供 Processor SPI 与基础算子。

### 阶段 2：流程语义增强（已完成）

- [x] 表达式求值器（payload / vars / attributes / 插值 / 等值比较）。
- [x] flow-ref 调用。
- [x] choice 分支控制。
- [x] for-each 迭代执行（基础版）。
- [x] try + on-error-continue/on-error-propagate。

### 阶段 3：连接器能力接入（进行中）

- [x] HTTP Request 处理器 + JDK HttpClient 连接器。
- [x] JDBC Query 处理器 + DataSource 连接器。
- [x] HTTP Listener 基础部署（JDK HttpServer）。
- [ ] HTTP Listener 全量语义（headers/queryParams/status）
- [ ] JDBC 事务边界与参数映射规范。
- [ ] 标准错误模型映射（timeout/deadlock 等）。

### 阶段 4：工程化与上线准备（后续）

- [ ] Scheduler 语义与调度管理。
- [ ] 可观测（metrics/tracing/logging correlation）。
- [ ] 双跑回归平台与压测基线。
- [ ] 连接器治理（限流、熔断、重试策略统一）。
